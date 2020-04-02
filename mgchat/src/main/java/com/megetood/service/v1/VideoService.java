package com.megetood.service.v1;

import java.util.List;

import com.megetood.pojo.Comments;
import com.megetood.pojo.Videos;
import com.megetood.pojo.vo.CommentsVO;
import com.megetood.utils.PagedResult;

public interface VideoService {
	/**
	 * 保存视频
	 *
	 * @param video
	 */
	public String saveVideo(Videos video);

	/**
	 * 修改视频封面
	 *
	 * @param videoId
	 * @param coverPath
	 */
	public void updateVideo(String videoId, String coverPath);

	/**
	 * 分页查询并保存热搜
	 *
	 * @param video
	 * @param isSaveRecord 1：保存
	 *                     0：不保存
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public PagedResult getAllVideos(Videos video, Integer isSaveRecord, Integer page, Integer pageSize);


	/**
	 * 查询视频列表
	 *
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public PagedResult getVideoList(Integer page, Integer pageSize);


	/**
	 * 查询收藏的作品
	 *
	 * @param userId
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public PagedResult getMyLikeVideos(String userId, Integer page, Integer pageSize);

	/**
	 * 查询关注的作品
	 *
	 * @param userId
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public PagedResult getMyFollowVideos(String userId, Integer page, Integer pageSize);


	/**
	 * 获取热搜次列表
	 *
	 * @return
	 */
	public List<String> getHotRecords();

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
	public int userUnlikeVideo(String userId, String videoId, String videoCreaterId);

	/**
	 * 保存评论信息
	 *
	 * @param comment
	 */
//	public void saveComment(Comments comment);
	public List<CommentsVO> saveComment(Comments comment);

	/**
	 * 分页查询所有评论
	 *
	 * @param videoId
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public PagedResult getAllComments(String videoId, Integer page, Integer pageSize);
	public List<CommentsVO> getAllComments(String videoId);

	/**
	 * 判断当前用户是否点赞过当前视频
	 *
	 * @param userId
	 * @param videoId
	 * @return
	 */
	public boolean isUserLikeVideo(String userId, String videoId);
}
