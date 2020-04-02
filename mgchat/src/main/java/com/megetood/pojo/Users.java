package com.megetood.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class Users {
    /**
     * 主键编号
     */
    @Id
    private String id;

    /**
     * 用户名
     */
    @NotNull
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 显示名称
     */

    @NotNull
    private String password;

    /**
     * 头像
     */
    @Column(name = "face_image")
    private String faceImage;

    /**
     * 大头像
     */
    @Column(name = "face_image_big")
    private String faceImageBig;

    /**
     * 二维码
     */
    private String qrcode;

    /**
     * 客户设备id
     */
    private String cid;


}