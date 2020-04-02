package com.megetood.service.v2;

import com.megetood.pojo.Bgm;

import java.util.List;

public interface BgmServiceV2 {

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
