package com.megetood.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="com.megetood")
@PropertySource("classpath:resource.properties")
public class ResourceConfig {
//	@Value("${fileSpace}")
	private String fileSpace;

//	@Value("${ffmpegEXEPath}")
	private String ffmpegEXEPath;

	public String getFileSpace() {
		return fileSpace;
	}

	public void setFileSpace(String fileSpace) {
		this.fileSpace = fileSpace;
	}

	public String getFfmpegEXEPath() {
		return ffmpegEXEPath;
	}

	public void setFfmpegEXEPath(String ffmpegEXEPath) {
		this.ffmpegEXEPath = ffmpegEXEPath;
	}
}
