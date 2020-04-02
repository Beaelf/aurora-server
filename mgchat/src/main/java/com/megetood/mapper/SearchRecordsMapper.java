package com.megetood.mapper;

import java.util.List;

import com.megetood.pojo.SearchRecords;
import com.megetood.utils.MyMapper;

public interface SearchRecordsMapper extends MyMapper<SearchRecords> {
	public List<String> getHotwords();
}