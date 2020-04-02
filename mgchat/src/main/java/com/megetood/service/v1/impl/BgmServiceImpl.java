package com.megetood.service.v1.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.megetood.mapper.BgmMapper;
import com.megetood.pojo.Bgm;
import com.megetood.service.v1.BgmService;

@Service
public class BgmServiceImpl implements BgmService {
	
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
