package com.megetood.service.v2.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.megetood.config.ResourceConfig;
import com.megetood.enums.VideoStatusEnum;
import com.megetood.exception.http.CommonException;
import com.megetood.mapper.*;
import com.megetood.pojo.*;
import com.megetood.pojo.dto.CommentAddDTO;
import com.megetood.pojo.dto.VideoUploadDTO;
import com.megetood.pojo.vo.CommentsVO;
import com.megetood.pojo.vo.VideosVO;
import com.megetood.service.v1.BgmService;
import com.megetood.service.v2.VideoServiceV2;
import com.megetood.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service("videoServiceImplV2")
public class VideoServiceImplV2 implements VideoServiceV2 {

	@Autowired
	private ResourceConfig resourceConfig;

	@Autowired
	private BgmService bgmService;

	@Autowired
	private VideosMapper videosMapper;
	
	@Autowired
	private VideosMapperCustom videosMapperCustom;
	
	@Autowired
	private UsersLikeVideosMapper usersLikeVideosMapper;
	
	@Autowired
	private CommentsMapper commentsMapper;
	
	@Autowired
	private CommentsMapperCustom commentsMapperCustom;
	
	@Autowired
	private Sid sid;
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public String saveVideo(Videos video) {
		String id = sid.nextShort();
		video.setId(id);
		videosMapper.insertSelective(video);
		return id;
	}




	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public PagedResult getVideoList(String userId, Integer page, Integer pageSize) {
		/*
		 * 分页查询
		 */
		PageHelper.startPage(page, pageSize);
		// 查询所有
		List<VideosVO> list = videosMapperCustom.selectVideoWithFavstatus(userId);
		for(VideosVO v : list){
				String timeAgo = TimeAgoUtils.format(v.getCreateTime());
				v.setTimeAgoStr(timeAgo);

			int result = videosMapperCustom.queryVideoLikeCounts(v.getId());
			v.setLikeCounts(result);
		}

		PageInfo<VideosVO> pageList = new PageInfo<VideosVO>(list);

		// 设置分页信息
		PagedResult pagedResult = new PagedResult();
		// 当前页
		pagedResult.setPage(page);
		// 总页数
		pagedResult.setTotal(pageList.getPages());
		// 展示内容
		pagedResult.setRows(list);
		// 总记录数
		pagedResult.setRecords(pageList.getTotal());

		return pagedResult;
	}

	@Override
	public VideosVO getVideoDetail(String userId, String videoId) {
		VideosVO videoVO = videosMapperCustom.selectVideoDetailById(userId,videoId);
		// 查询出对应的评论
		List<CommentsVO> comments = getAllComments_f(videoId);

		videoVO.setComments(comments);

		return videoVO;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public int userLikeVideo(String userId, String videoId, String videoCreaterId) {
		// 判断用户是点赞过
		boolean flag = this.isUserLikeVideo(userId, videoId);
		if(flag){
			// 点过赞了
			throw new CommonException(3001);
		}
		// 保存用户和视频的喜欢关联关系表
		String likeId = sid.nextShort();
		
		UsersLikeVideos ulv = new UsersLikeVideos();
		ulv.setId(likeId);
		ulv.setUserId(userId);
		ulv.setVideoId(videoId);
		
		usersLikeVideosMapper.insert(ulv);
		
		// 视频喜欢数量累加
		videosMapperCustom.addVideoLikeCount(videoId);

		// 查询出点赞数
		int result = videosMapperCustom.queryVideoLikeCounts(videoId);

		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public int userCancelLikeVideo(String userId, String videoId, String videoCreaterId) {
		// 判断用户是点赞过
		boolean flag = this.isUserLikeVideo(userId, videoId);
		if(!flag){
			// 未点过赞
			throw new CommonException(3001);
		}
		// 删除用户和视频的喜欢关联关系表
		Example example = new Example(UsersLikeVideos.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("userId", userId);
		criteria.andEqualTo("videoId", videoId);
		
		usersLikeVideosMapper.deleteByExample(example);
		
		// 视频喜欢数量-1
		videosMapperCustom.reduceVideoLikeCount(videoId);

		// 查询出点赞数
		int result = videosMapperCustom.queryVideoLikeCounts(videoId);
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public List<CommentsVO> saveComment(CommentAddDTO commentAddDTO) {
		Comments comment = new Comments();
		BeanUtils.copyProperties(commentAddDTO,comment);

		// 生成一个主键
		String id = sid.nextShort();
		// 设置保存的值
		comment.setId(id).setCreateTime(new Date());
		
		// 保存至数据库
		commentsMapper.insert(comment);

		// 查询出当前视频的所有评论
		List<CommentsVO> commentsVOList = getAllComments_f(comment.getVideoId());

		return commentsVOList;
	}

	private  List<CommentsVO> getAllComments_f(String videoId) {
		// 获取查询结果
		List<CommentsVO> list = commentsMapperCustom.queryComments(videoId);
		// 对结果中的日期进行处理
		for (CommentsVO c : list) {
			String timeAgo = TimeAgoUtils.format(c.getCreateTime());
			c.setTimeAgoStr(timeAgo);
		}

		return list;
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public boolean isUserLikeVideo(String userId, String videoId) {
		if (StringUtils.isBlank(userId) || StringUtils.isBlank(videoId)) {
			return false;
		}

		Example example = new Example(UsersLikeVideos.class);

		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("userId", userId);
		criteria.andEqualTo("videoId", videoId);

		List<UsersLikeVideos> list = usersLikeVideosMapper.selectByExample(example);

		if (list != null && list.size() > 0) {
			return true;
		}
		return false;
	}


	@Override
	public String uploadVideo(String userId,String bgmId,String des,HttpServletRequest request) throws Exception {
		// 获取参数
//		String userId = videoDTO.getUserId();
//		String bgmId = videoDTO.getBgmId();
//		String des = videoDTO.getDes();
		log.info("params:"+userId+"-"+bgmId+"-"+des);

		String fileSpace = resourceConfig.getFileSpace();
		String ffmpegEXE = resourceConfig.getFfmpegEXEPath();
		// 视频最终保存路径
		String finalVideoPath = "";
		// 保存到数据库中的相对路径
		String uploadPathDB = "/" + userId + "/video";
		String coverPathDB = "/" + userId + "/video";

		System.out.println("upload method start");

		// 获取文件
		MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
		Iterator<String> files = mRequest.getFileNames();
		if (files.hasNext()) {
			MultipartFile mFile = mRequest.getFile(files.next());
			System.out.println(mFile.getOriginalFilename());
			byte[] bytes = null;
			BufferedOutputStream stream = null;
			try {
				// 获取文件流
				bytes = mFile.getBytes();
				// 获取文件名
				String fileName = mFile.getOriginalFilename();
				if (StringUtils.isNotBlank(fileName)) {
					// 文件上传的最终保存路径
					finalVideoPath = fileSpace + uploadPathDB + "/" + fileName;
					// 数据库保存路径
					uploadPathDB = uploadPathDB + "/" + fileName;
					// 视频封面数据库路径
					String fileNamePrefix = fileName.substring(0, fileName.indexOf('.'));
					System.out.println(fileNamePrefix);
					coverPathDB = coverPathDB + "/" + fileNamePrefix + ".jpg";

					File outFile = new File(finalVideoPath);
					if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
						// 创建父文件夹
						outFile.getParentFile().mkdirs();
					}

					stream = new BufferedOutputStream(new FileOutputStream(outFile));
					stream.write(bytes);

				} else {
					throw new CommonException(3002);
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw new CommonException(3003);
			} finally {
				if (stream != null) {
					stream.flush();
					stream.close();
				}
			}

			// bgmId存在，合成背景音乐
			if (StringUtils.isNotBlank(bgmId)) {
				// 获取背景音乐路径
				Bgm bgm = bgmService.queryBgmById(bgmId);
				String bgmInputPath = fileSpace + bgm.getPath();

				// 定义FFmpeg对象
				MergeVideoMp3 tool = new MergeVideoMp3(ffmpegEXE);

				// 输入视频路径
				String videoInputPath = finalVideoPath;

				// 视频输出路径，及数据库保存路径
				String videoOutputName = UUID.randomUUID().toString() + ".mp4";
				uploadPathDB = "/" + userId + "/video" + "/" + videoOutputName;
				finalVideoPath = fileSpace + uploadPathDB;

				// 合成音频
				tool.convertor(videoInputPath, bgmInputPath, finalVideoPath);
			}

			System.out.println("uploadPathDB=" + uploadPathDB);
			System.out.println("finalVideoPath=" + finalVideoPath);

			// 截取视频封面
			FetchVideoCover fetchVideoCover = new FetchVideoCover(ffmpegEXE);
			fetchVideoCover.getCover(finalVideoPath, fileSpace + coverPathDB);
		}
		Videos video = new Videos();
		video.setVideoPath(uploadPathDB)
				.setVideoDesc(des)
				.setUserId(userId)
				.setCreateTime(new Date())
				.setAudioId(bgmId)
				.setCoverPath(coverPathDB)
				.setStatus(VideoStatusEnum.SUCCESS.value);

		String videoId = saveVideo(video);
		System.out.println("videoId:"+videoId);

		return videoId;
	}
}
