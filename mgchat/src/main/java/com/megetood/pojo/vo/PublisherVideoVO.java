package com.megetood.pojo.vo;

public class PublisherVideoVO {
	
	public UsersVO publisher;
	
	public boolean userLikeVideo;

	public UsersVO getPublisher() {
		return publisher;
	}

	public void setPublisher(UsersVO publisher) {
		this.publisher = publisher;
	}

	public boolean getUserLikeVideo() {
		return userLikeVideo;
	}

	public void setUserLikeVideo(boolean userLikeVideo) {
		this.userLikeVideo = userLikeVideo;
	}
	
	
	
}