package com.megetood.pojo.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Id;

@Data
@Accessors(chain = true)
public class UsersVO {
    private String id;
    private String username;
    private String nickname;
    private String faceImage;
    private String faceImageBig;
    private String qrcode;
    private String userToken;


}