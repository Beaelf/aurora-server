package com.megetood.service.v2;

import com.megetood.pojo.Comments;
import com.megetood.pojo.Videos;
import com.megetood.pojo.dto.CommentAddDTO;
import com.megetood.pojo.dto.VideoUploadDTO;
import com.megetood.pojo.vo.CommentsVO;
import com.megetood.pojo.vo.VideosVO;
import com.megetood.utils.PagedResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface VideoServiceV2 {


	/**
	 * 查询视频列表
	 * @param userId
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public PagedResult getVideoList(String userId, Integer page, Integer pageSize);

	/**
	 * 查询视频详情
	 * @param userId
	 * @param videoId
	 * @return
	 */
	public VideosVO getVideoDetail(String userId, String videoId);

	/**
	 * 保存视频
	 *
	 * @param video
	 */
	public String saveVideo(Videos video);

	/**
	 * 上传视频
	 * @param userId
	 * @param bgmId
	 * @param des
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public String uploadVideo(String userId,String bgmId,String des,HttpServletRequest request) throws Exception;

	/**
	 * 用户喜欢视频
	 *
	 * @param userId
	 * @param videoId
	 * @param videoCreaterId
	 * @return 返回点赞数
	 */
	public int userLikeVideo(String userId, String videoId, String videoCreaterId);

	/**
	 * 用户取消喜欢
	 *
	 * @param userId
	 * @param videoId
	 * @param videoCreaterId
	 * @return 返回点赞数
	 */
	public int userCancelLikeVideo(String userId, String videoId, String videoCreaterId);

	/**
	 * 保存评论信息
	 * @param commentAddDTO
	 */
	public List<CommentsVO> saveComment(CommentAddDTO commentAddDTO);


	/**
	 * 判断当前用户是否点赞过当前视频
	 *
	 * @param userId
	 * @param videoId
	 * @return
	 */
	public boolean isUserLikeVideo(String userId, String videoId);
}
