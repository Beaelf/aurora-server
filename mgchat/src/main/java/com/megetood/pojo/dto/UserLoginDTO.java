package com.megetood.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Date 2020/3/31 13:45
 */
@Data
@Accessors(chain = true)
public class UserLoginDTO {
    private String username;    // 用户名
    private String password;    // 密码
    private String cid;         // 设备ID
}
