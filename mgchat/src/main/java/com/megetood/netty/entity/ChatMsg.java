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
public class ChatMsg implements Serializable {

    private static final long serialVersionUID = 4821609847812912672L;

    private String senderId;        // 发送者id
    private String receiverId;      // 接收者id
    private String msg;             // 聊天内容
    private String msgId;           // 用户消息的签收
}
