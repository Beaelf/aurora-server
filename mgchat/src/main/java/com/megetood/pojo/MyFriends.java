package com.megetood.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Data
@Accessors(chain = true)
@Table(name = "my_friends")
public class MyFriends {
    /**
     * 主键编号
     */
    @Id
    private String id;

    /**
     * 用户id
     */
    @Column(name = "my_user_id")
    private String myUserId;

    /**
     * 好友id
     */
    @Column(name = "my_friend_user_id")
    private String myFriendUserId;


}