package com.megetood.netty.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author Megetood
 * @Date 2019/12/11 13:10
 */
@Data
@Accessors(chain = true)
public class DataContent implements Serializable {

    private static final long serialVersionUID = 8295957379680382494L;

    private Integer action;     // 动作类型
    private ChatMsg chatMsg;    // 用户聊天内容entity
    private String extand;      // 扩展


}
