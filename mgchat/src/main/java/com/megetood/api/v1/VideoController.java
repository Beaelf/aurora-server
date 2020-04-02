package com.megetood.api.v1;

import com.megetood.config.ResourceConfig;
import com.megetood.constant.CommonConst;
import com.megetood.enums.VideoStatusEnum;
import com.megetood.pojo.Bgm;
import com.megetood.pojo.Comments;
import com.megetood.pojo.Videos;
import com.megetood.pojo.vo.CommentsVO;
import com.megetood.service.v1.BgmService;
import com.megetood.service.v1.VideoService;
import com.megetood.utils.FetchVideoCover;
import com.megetood.utils.JSONResult;
import com.megetood.utils.MergeVideoMp3;
import com.megetood.utils.PagedResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * @Author Megetood
 * @Date 2020/1/15 17:29
 */

@Slf4j
//@RestController
//@RequestMapping("/video/")
public class VideoController {

    @Autowired
    private ResourceConfig resourceConfig;

    @Autowired
    private BgmService bgmService;

    @Autowired
    private VideoService videoService;

    /**
     * 上传视频
     * @param userId
     * @param bgmId
     * @param des   视频描述
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/upload")
    public JSONResult upload(String userId, String bgmId, String des, HttpServletRequest request) throws Exception {

        System.out.println("[upload]接收值：" + userId);
        // 参数校验
        if(StringUtils.isBlank(userId)){
            return JSONResult.errorMsg("用户id不能为空");
        }


        String fileSpace = resourceConfig.getFileSpace();
        String ffmpegEXE = resourceConfig.getFfmpegEXEPath();
        // 视频最终保存路径
        String finalVideoPath = "";
        // 保存到数据库中的相对路径
        String uploadPathDB = "/" + userId + "/video";
        String coverPathDB = "/" + userId + "/video";

        System.out.println("upload method start");

        // 获取文件
        MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;
        Iterator<String> files = mRequest.getFileNames();
        if (files.hasNext()) {
            MultipartFile mFile = mRequest.getFile(files.next());
            System.out.println(mFile.getOriginalFilename());
            byte[] bytes = null;
            BufferedOutputStream stream = null;
            try {
                // 获取文件流
                bytes = mFile.getBytes();
                // 获取文件名
                String fileName = mFile.getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {
                    // 文件上传的最终保存路径
                    finalVideoPath = fileSpace + uploadPathDB + "/" + fileName;
                    // 数据库保存路径
                    uploadPathDB = uploadPathDB + "/" + fileName;
                    // 视频封面数据库路径
                    String fileNamePrefix = fileName.substring(0, fileName.indexOf('.'));
                    System.out.println(fileNamePrefix);
                    coverPathDB = coverPathDB + "/" + fileNamePrefix + ".jpg";

                    File outFile = new File(finalVideoPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    stream = new BufferedOutputStream(new FileOutputStream(outFile));
                    stream.write(bytes);

                } else {
                    return JSONResult.errorMsg("文件不存在...");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return JSONResult.errorMsg("异常，上传出错...");
            } finally {
                if (stream != null) {
                    stream.flush();
                    stream.close();
                }
            }

            if (StringUtils.isNotBlank(bgmId)) {
                // 获取背景音乐路径
                Bgm bgm = bgmService.queryBgmById(bgmId);
                String bgmInputPath = fileSpace + bgm.getPath();

                // 定义FFmpeg对象
                MergeVideoMp3 tool = new MergeVideoMp3(ffmpegEXE);

                // 输入视频路径
                String videoInputPath = finalVideoPath;

                // 视频输出路径，及数据库保存路径
                String videoOutputName = UUID.randomUUID().toString() + ".mp4";
                uploadPathDB = "/" + userId + "/video" + "/" + videoOutputName;
                finalVideoPath = fileSpace + uploadPathDB;

                // 合成音频
                tool.convertor(videoInputPath, bgmInputPath, finalVideoPath);
            }

            System.out.println("uploadPathDB=" + uploadPathDB);
            System.out.println("finalVideoPath=" + finalVideoPath);

            // 截取视频封面
            FetchVideoCover videoInfo = new FetchVideoCover(ffmpegEXE);
            videoInfo.getCover(finalVideoPath, fileSpace + coverPathDB);
        }
        Videos video = new Videos();
        video.setVideoPath(uploadPathDB)
                .setVideoDesc(des)
                .setUserId(userId)
                .setCreateTime(new Date())
                .setAudioId(bgmId)
                .setCoverPath(coverPathDB)
                .setStatus(VideoStatusEnum.SUCCESS.value);

        String videoId = videoService.saveVideo(video);
        System.out.println("videoId:"+videoId);

        return JSONResult.ok(videoId);
    }

    @PostMapping("/list")
    public JSONResult getVideoList(Integer page, Integer pageSize){
        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = CommonConst.PAGE_SIZE;
        }

        PagedResult result = videoService.getVideoList(page, pageSize);
        return JSONResult.ok(result);
    }

    /**
     * 是否喜欢视频
     * @param userId
     * @param videoId
     * @return
     */
    @PostMapping("likeOperate")
    public JSONResult likeOperate(String userId, String videoId, String videoCreaterId){

        System.out.println("userId:"+userId+"videoId:"+videoId+"createrId:"+videoCreaterId);
        // 判断用户是点赞过
        boolean flag = videoService.isUserLikeVideo(userId, videoId);
        int result;
        if(flag){
            // 点赞过，取消点赞操作
            result = videoService.userUnlikeVideo(userId, videoId, videoCreaterId);
        }else{
            // 没有点赞过，点赞操作
            result = videoService.userLikeVideo(userId, videoId, videoCreaterId);
        }
        return JSONResult.ok(result);
    }

    /**
     * 点赞
     * @param userId
     * @param videoId
     * @param videoCreaterId
     * @return
     */
    @PostMapping(value = "/userLike")
    public JSONResult userLike(String userId, String videoId, String videoCreaterId) {
        int result = videoService.userLikeVideo(userId, videoId, videoCreaterId);
        return JSONResult.ok(result);

    }

    /**
     * 取消点赞
     * @param userId
     * @param videoId
     * @param videoCreaterId
     * @return
     */
    @PostMapping(value = "/userUnlike")
    public JSONResult userUnlike(String userId, String videoId, String videoCreaterId) {
        int result = videoService.userUnlikeVideo(userId, videoId, videoCreaterId);
        return JSONResult.ok(result);

    }

    /**
     * 保存视频评论
     * @param comment
     * @param fatherCommentId
     * @param toUserId
     * @return
     * @throws Exception
     */
    @PostMapping("/saveComment")
    public JSONResult saveComment(@RequestBody Comments comment,
                                  String fatherCommentId, String toUserId) throws Exception {
        log.info("comment:"+comment);
        if(StringUtils.isNotBlank(fatherCommentId) && StringUtils.isNotBlank(toUserId)) {

            log.info("fatherCommentId:"+fatherCommentId+"toUserId:"+toUserId);
            comment.setFatherCommentId(fatherCommentId);
            comment.setToUserId(toUserId);
        }

        List<CommentsVO> commentsVOList = videoService.saveComment(comment);
        return JSONResult.ok();
    }

    /**
     * 获取视频评论
     * @param videoId
     * @param page
     * @param pageSize
     * @return
     * @throws Exception
     */
    @PostMapping("/getVideoComments")
    public JSONResult getVideoComments(String videoId, Integer page, Integer pageSize) throws Exception {
        log.info("videoId:"+videoId+"page:"+page+"pageSize:"+pageSize);
        // 判断videoId是否为空
        if(StringUtils.isBlank(videoId)) {
            return JSONResult.ok();
        }
        // 分页查询视频列表，事件顺序倒序排序
        if(page == null) {
            page = 1;
        }
        if(pageSize == null) {
            pageSize = 10;
        }

//        List<CommentsVO> list = videoService.getAllComments(videoId);
        PagedResult list = videoService.getAllComments(videoId, page, pageSize);
        List<CommentsVO> vos = (List<CommentsVO>) list.getRows();
        for (CommentsVO v: vos) {
            System.out.println(v);
        }
        return JSONResult.ok(list);
    }
}
