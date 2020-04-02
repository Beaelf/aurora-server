package com.megetood.service.v2.impl;

import com.megetood.mapper.BgmMapper;
import com.megetood.pojo.Bgm;
import com.megetood.service.v1.BgmService;
import com.megetood.service.v2.BgmServiceV2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BgmServiceImplV2 implements BgmServiceV2 {
	
	@Autowired
	private BgmMapper bgmMapper;

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public List<Bgm> queryBgmList() {
		return bgmMapper.selectAll();
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public Bgm queryBgmById(String bgmId) {
		return bgmMapper.selectByPrimaryKey(bgmId);
	}
	
}
