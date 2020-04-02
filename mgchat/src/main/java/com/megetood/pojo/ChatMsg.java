package com.megetood.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import javax.persistence.*;

@Data
@Accessors(chain = true )
@Table(name = "chat_msg")
public class ChatMsg {
    /**
     * 主键编号
     */
    @Id
    private String id;

    /**
     * 发送者id
     */
    @Column(name = "send_user_id")
    private String sendUserId;

    /**
     * 接受者id
     */
    @Column(name = "accept_user_id")
    private String acceptUserId;

    /**
     * 消息
     */
    private String msg;

    /**
     * 签收状态
     */
    @Column(name = "sign_flag")
    private Integer signFlag;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

}