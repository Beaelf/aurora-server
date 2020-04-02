package com.megetood.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import javax.persistence.*;

@Data
@Accessors(chain = true)
public class Comments {
    @Id
    private String id;

    @Column(name = "father_comment_id")
    private String fatherCommentId;

    @Column(name = "to_user_id")
    private String toUserId;

    /**
     * 视频id
     */
    @Column(name = "video_id")
    private String videoId;

    /**
     * 留言者，评论的用户id
     */
    @Column(name = "from_user_id")
    private String fromUserId;

    @Column(name = "create_time")
    private Date createTime;

    /**
     * 评论内容
     */
    private String comment;


    @Override
    public String toString() {
        return "Comments{" +
                "id='" + id + '\'' +
                ", fatherCommentId='" + fatherCommentId + '\'' +
                ", toUserId='" + toUserId + '\'' +
                ", videoId='" + videoId + '\'' +
                ", fromUserId='" + fromUserId + '\'' +
                ", createTime=" + createTime +
                ", comment='" + comment + '\'' +
                '}';
    }
}