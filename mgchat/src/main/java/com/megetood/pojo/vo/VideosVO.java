package com.megetood.pojo.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;
import javax.persistence.*;

@Data
@Accessors(chain = true)
public class VideosVO {
    private List<CommentsVO> comments;

    private String id;

    /**
     * 发布者id
     */
    private String userId;

    /**
     * 用户使用音频的信息
     */
    private String audioId;

    /**
     * 视频描述
     */
    private String videoDesc;

    /**
     * 视频存放的路径
     */
    private String videoPath;

    /**
     * 视频秒数
     */
    private Float videoSeconds;

    /**
     * 视频宽度
     */
    private Integer videoWidth;

    /**
     * 视频高度
     */
    private Integer videoHeight;

    /**
     * 视频封面图
     */
    private String coverPath;

    /**
     * 喜欢/赞美的数量
     */
    private Integer likeCounts;

    /**
     * 视频状态：
1、发布成功
2、禁止播放，管理员操作
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;
    private String timeAgoStr;

    private String faceImage;
    private String nickname;

    private String like_status;
}