package com.megetood.api.v2;

import com.megetood.pojo.Bgm;
import com.megetood.service.v1.BgmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController("bgmControllerV2")
@RequestMapping("/bgm")
public class BgmControllerV2 {
	
	@Autowired
	private BgmService bgmService;

	@GetMapping("/list")
	public List<Bgm> getBgmList() {

		List<Bgm> bgms = bgmService.queryBgmList();

		return 	bgms;
	}
	
	
	
}
