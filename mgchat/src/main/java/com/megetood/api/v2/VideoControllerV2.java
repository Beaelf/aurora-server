package com.megetood.api.v2;

import com.megetood.constant.CommonConst;
import com.megetood.pojo.Comments;
import com.megetood.pojo.dto.CommentAddDTO;
import com.megetood.pojo.dto.VideoUploadDTO;
import com.megetood.pojo.vo.CommentsVO;
import com.megetood.pojo.vo.VideosVO;
import com.megetood.service.v1.BgmService;
import com.megetood.service.v2.VideoServiceV2;
import com.megetood.utils.JSONResult;
import com.megetood.utils.PagedResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author Megetood
 * @Date 2020/1/15 17:29
 */

@Slf4j
@RestController("videoController2")
@Validated
public class VideoControllerV2 {
    @Autowired
    private VideoServiceV2 videoService2;

    /**
     * 获取视频列表
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/video/list")
    public PagedResult getVideoList(
            @RequestParam @NotNull String userId,
            @RequestParam(required = false,defaultValue = ""+CommonConst.Page ) Integer page,
            @RequestParam(required = false,defaultValue = ""+CommonConst.PAGE_SIZE ) Integer pageSize){

        PagedResult result = videoService2.getVideoList(userId,page, pageSize);

        return result;
    }

    /**
     * 获取某个视频的详情
     * @param userId
     * @param videoId
     * @return
     */
    @GetMapping("/video/{videoId}/detail")
    public VideosVO getVideoDetail(@RequestParam("userId") @NotNull String userId,
                                   @PathVariable("videoId") @NotNull String videoId){

        VideosVO videoDetail = videoService2.getVideoDetail(userId,videoId);

        return videoDetail;
    }

    /**
     * 上传视频
     * @param userId
     * @param bgmId
     * @param des
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/upload/video")
    public JSONResult upload(@NotNull String userId,
                             String bgmId,
                             String des,
                             HttpServletRequest request) throws Exception {

        String videoId = videoService2.uploadVideo(userId,bgmId,des,request);

        return JSONResult.ok(videoId);
    }

    /**
     * 点赞
     * @param userId
     * @param videoId
     * @param videoCreaterId
     * @return
     */
    @PostMapping(value = "/{userId}/like/video")
    public Integer userLike(@PathVariable("userId") @NotNull String userId,
                            @RequestParam @NotNull String videoId,
                            @RequestParam("createrId") @NotNull String videoCreaterId) {

        int result = videoService2.userLikeVideo(userId, videoId, videoCreaterId);

        return result;
    }

    /**
     * 取消点赞
     * @param userId
     * @param videoId
     * @param videoCreaterId
     * @return
     */
    @PostMapping(value = "/{userId}/cancel/like/video")
    public Integer userUnlike(@PathVariable("userId") @NotNull String userId,
                              @RequestParam @NotNull String videoId,
                              @RequestParam("createrId") @NotNull String videoCreaterId) {

        int result = videoService2.userCancelLikeVideo(userId, videoId, videoCreaterId);

        return result;
    }

    /**
     * 保存视频评论
     * @param commentDTO 用于接收评论相关参数
     * @return
     * @throws Exception
     */
    @PostMapping("/video/add/comment")
    public List<CommentsVO> saveComment(@RequestBody CommentAddDTO commentDTO){

        List<CommentsVO> commentsVOList = videoService2.saveComment(commentDTO);

        return commentsVOList;
    }

}
