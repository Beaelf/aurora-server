package com.megetood.service.v1.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.megetood.mapper.CommentsMapper;
import com.megetood.mapper.CommentsMapperCustom;
import com.megetood.mapper.SearchRecordsMapper;
import com.megetood.mapper.UsersLikeVideosMapper;
import com.megetood.mapper.UsersMapper;
import com.megetood.mapper.VideosMapper;
import com.megetood.mapper.VideosMapperCustom;
import com.megetood.pojo.Comments;
import com.megetood.pojo.SearchRecords;
import com.megetood.pojo.UsersLikeVideos;
import com.megetood.pojo.Videos;
import com.megetood.pojo.vo.CommentsVO;
import com.megetood.pojo.vo.VideosVO;
import com.megetood.service.v1.VideoService;
import com.megetood.utils.PagedResult;
import com.megetood.utils.TimeAgoUtils;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class VideoServiceImpl implements VideoService {

	@Autowired
	private VideosMapper videosMapper;
	
	@Autowired
	private VideosMapperCustom videosMapperCustom;
	
	@Autowired
	private SearchRecordsMapper searchRecordsMapper;
	
	@Autowired
	private UsersMapper usersMapper;
	
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

	@Override
	public void updateVideo(String videoId, String coverPath) {
		Videos video = new Videos();
		video.setId(videoId);
		video.setCoverPath(coverPath);
		videosMapper.updateByPrimaryKeySelective(video);
	}

	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public PagedResult getAllVideos(Videos video,Integer isSaveRecord,Integer page, Integer pageSize) {
		
		/*
		 * 保存热搜词
		 */
		// 视频详情
		String desc = video.getVideoDesc();
		// 获取用户id
		String userId = video.getUserId();
//		System.out.println("videoimpl:"+userId);
		
		// 判断是否为空
		if(isSaveRecord != null && isSaveRecord ==1) {
			SearchRecords record = new SearchRecords();
			String recordId = sid.nextShort();
			record.setId(recordId);
			record.setContent(desc);
			searchRecordsMapper.insert(record);
		}
		
		/*
		 * 分页查询
		 */
		PageHelper.startPage(page, pageSize);
		// 查询所有
		List<VideosVO> list = videosMapperCustom.queryAllVideos(desc,userId);
		
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

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public PagedResult getVideoList(Integer page, Integer pageSize) {
		/*
		 * 分页查询
		 */
		PageHelper.startPage(page, pageSize);
		// 查询所有
		List<VideosVO> list = videosMapperCustom.queryVideoList();
		for(VideosVO v : list){
				String timeAgo = TimeAgoUtils.format(v.getCreateTime());
				v.setTimeAgoStr(timeAgo);
			List<CommentsVO> cList = getAllComments_f(v.getId());
			v.setComments(cList);

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

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public PagedResult getMyLikeVideos(String userId,Integer page, Integer pageSize) {
		/*
		 * 分页查询
		 */
		PageHelper.startPage(page, pageSize);
		// 查询所有
		List<VideosVO> list = videosMapperCustom.queryMyLikeVideos(userId);
		
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

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public PagedResult getMyFollowVideos(String userId,Integer page, Integer pageSize) {
		/*
		 * 分页查询
		 */
		PageHelper.startPage(page, pageSize);
		// 查询所有
		List<VideosVO> list = videosMapperCustom.queryMyFollowVideos(userId);
		
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


	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public List<String> getHotRecords() {
		return searchRecordsMapper.getHotwords();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public int userLikeVideo(String userId, String videoId, String videoCreaterId) {
		// 1.保存用户和视频的喜欢关联关系表
		String likeId = sid.nextShort();
		
		UsersLikeVideos ulv = new UsersLikeVideos();
		ulv.setId(likeId);
		ulv.setUserId(userId);
		ulv.setVideoId(videoId);
		
		usersLikeVideosMapper.insert(ulv);
		
		// 2.视频喜欢数量累加
		videosMapperCustom.addVideoLikeCount(videoId);
		
		// 3.用户受喜欢数量累加
//		usersMapper.addReceiveLikeCount(userId);

		// 4.查询出点赞数
		int result = videosMapperCustom.queryVideoLikeCounts(videoId);
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public int userUnlikeVideo(String userId, String videoId, String videoCreaterId) {
		
		// 1.删除用户和视频的喜欢关联关系表
		Example example = new Example(UsersLikeVideos.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("userId", userId);
		criteria.andEqualTo("videoId", videoId);
		
		usersLikeVideosMapper.deleteByExample(example);
		
		// 2.视频喜欢数量累加
		videosMapperCustom.reduceVideoLikeCount(videoId);
		
		// 3.用户受喜欢数量累加
//		usersMapper.reduceReceiveLikeCount(userId);

		// 4.查询出点赞数
		int result = videosMapperCustom.queryVideoLikeCounts(videoId);
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public List<CommentsVO> saveComment(Comments comment) {

		// 生成一个主键
		String id = sid.nextShort();
		
		// 设置保存的值
		comment.setId(id);
		comment.setCreateTime(new Date());
		
		// 保存至数据库
		commentsMapper.insert(comment);

		// 查询出当前视频的所有评论
		List<CommentsVO> commentsVOList = getAllComments_f(comment.getVideoId());

		return commentsVOList;
	}

	@Override
	public PagedResult getAllComments(String videoId, Integer page, Integer pageSize) {
		// 开启分页
		PageHelper.startPage(page,pageSize);
		
		// 获取查询结果
		List<CommentsVO> list = commentsMapperCustom.queryComments(videoId);
		// 对结果中的日期进行处理
		for (CommentsVO c : list) {
			String timeAgo = TimeAgoUtils.format(c.getCreateTime());
			c.setTimeAgoStr(timeAgo);
		}
		
		PageInfo<CommentsVO> pageList = new PageInfo<>(list);
		
		PagedResult grid = new PagedResult();
		grid.setTotal(pageList.getPages());
		grid.setRows(list);
		grid.setPage(page);
		grid.setRecords(pageList.getTotal());
		
		return grid;
	}
	@Override
	public List<CommentsVO> getAllComments(String videoId) {
		List<CommentsVO> list = getAllComments_f(videoId);
		return list;
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
}
