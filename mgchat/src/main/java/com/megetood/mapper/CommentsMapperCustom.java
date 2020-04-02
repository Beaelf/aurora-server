package com.megetood.mapper;

import java.util.List;

import com.megetood.pojo.Comments;
import com.megetood.pojo.vo.CommentsVO;
import com.megetood.utils.MyMapper;

public interface CommentsMapperCustom extends MyMapper<Comments> {
	public List<CommentsVO> queryComments(String videoId);
}