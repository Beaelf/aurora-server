package com.megetood.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import javax.persistence.*;

@Data
@Accessors(chain = true)
@Table(name = "friends_request")
public class FriendsRequest {
    /**
     * 主键编号
     */
    @Id
    private String id;

    /**
     * 发送用户id
     */
    @Column(name = "send_user_id")
    private String sendUserId;

    /**
     * 接受用户id
     */
    @Column(name = "accept_user_id")
    private String acceptUserId;

    /**
     * 请求时间
     */
    @Column(name = "request_data_time")
    private Date requestDataTime;

}