package com.megetood.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @Date 2020/3/30 15:51
 */
@Data
@Accessors(chain = true)
public class CommentAddDTO {
    private String fatherCommentId; // 回复的评论id
    private String toUserId;        // 回复的评论的用户id
    @NotNull
    private String videoId;         // 评论的视频id
    @NotNull
    private String fromUserId;      // 评论发布者id
    @NotNull
    private String comment;         // 评论内容
}
