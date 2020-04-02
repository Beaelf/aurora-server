package com.megetood.pojo.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FriendRequestVO {
    private String sendUserId;
    private String sendUsername;
    private String sendFaceImage;
    private String sendNickname;


}