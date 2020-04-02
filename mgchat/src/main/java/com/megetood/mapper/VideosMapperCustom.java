package com.megetood.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.megetood.pojo.Videos;
import com.megetood.pojo.vo.VideosVO;
import com.megetood.utils.MyMapper;

public interface VideosMapperCustom extends MyMapper<Videos> {
	
	/**
	 * 根据视频描述模糊查询喜欢的视频
	 * @param videoDesc
	 * @return
	 */
	public List<VideosVO> queryAllVideos(@Param("videoDesc") String videoDesc, @Param("userId") String userId);
	public List<VideosVO> queryVideoList();
	public List<VideosVO> queryMyLikeVideos(@Param("userId") String userId);
	public List<VideosVO> queryMyFollowVideos(@Param("userId") String userId);


	public Integer queryVideoLikeCounts(String videoId);
	/**
	 * 对喜欢的视频进行累加
	 * @param videoId
	 */
	public void addVideoLikeCount(String videoId);
	
	/**
	 * 对喜欢的视频进行累减
	 * @param videoId
	 */
	public void reduceVideoLikeCount(String videoId);


	/**
	 * 根据当前用户查询所有的视频
	 * @param userId
	 * @return
	 */
	public List<VideosVO> selectVideoWithFavstatus(@Param("userId") String userId );

	public VideosVO selectVideoDetailById(@Param("userId") String userId,
										  @Param("videoId") String videoId);

}