package com.megetood.service.v1;

import java.util.List;

import com.megetood.pojo.Bgm;

public interface BgmService {

	/**
	 * 查询背景音乐列表
	 * @return List
	 */
	public List<Bgm> queryBgmList();
	
	/**
	 * 根据bgmId查询bgm信息
	 * @param bgmId
	 * @return
	 */
	public Bgm queryBgmById(String bgmId);
}
