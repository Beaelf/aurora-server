package com.megetood.api.v1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.megetood.service.v1.BgmService;
import com.megetood.utils.JSONResult;


@RestController
@RequestMapping("/bgm")
public class BgmController {
	
	@Autowired
	private BgmService bgmService;

	@PostMapping("/list")
	public JSONResult list() {
		return 	JSONResult.ok(bgmService.queryBgmList());
	}
	
	
	
}
