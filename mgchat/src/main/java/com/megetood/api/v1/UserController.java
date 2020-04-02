package com.megetood.api.v1;

import com.megetood.enums.OperatorFriendRequestTypeEnum;
import com.megetood.enums.SearchFriendsStatusEnum;
import com.megetood.pojo.ChatMsg;
import com.megetood.pojo.Users;
import com.megetood.pojo.bo.UsersBO;
import com.megetood.pojo.dto.FriendRequestDTO;
import com.megetood.pojo.dto.FriendRequestOperDTO;
import com.megetood.pojo.vo.FriendRequestVO;
import com.megetood.pojo.vo.MyFriendsVO;
import com.megetood.pojo.vo.UsersVO;
import com.megetood.service.v1.UserService;
import com.megetood.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static com.megetood.interceptor.MiniInterceptor.USER_REDIS_SESSION;

/**
 * @author Megetood
 * @date 2019/11/20 23:58
 */
@Slf4j
@RestController
@RequestMapping("/user/")
public class UserController {
    @Autowired
    private RedisOperator redis;

    @Autowired
    private UserService userService;

    @Autowired
    private FastDFSClient fastDFSClient;

    /**
     * @param user
     * @return com.megetood.utils.JSONResult
     * @Description 注册登录接口
     */
    @PostMapping("registerOrLogin")
    public JSONResult RegisterOrLogin(@RequestBody Users user) throws Exception {
        // 0.用户名密码不能为空
        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
//            log.info("username:", user.getUsername(), "password:", user.getPassword());
            return JSONResult.errorMsg("用户名密码不能为空");
        }

        // 1.判断用户名是否存在，存在则登录，不存在则注册
        boolean usernameIsExist = userService.queryUsernameIsExist(user.getUsername());
        Users result = null;
        if (usernameIsExist) {
            // 1.1 登录
            result = userService.login(user.getUsername(), MD5Utils.getMD5Str(user.getPassword()));
            if (result == null) {
                return JSONResult.errorMsg("登录失败，用户名或密码不正确");
            }
        } else {
            // 1.2 注册
            result = userService.register(user);
            if (result == null) {
                return JSONResult.errorMsg("注册失败，用户名或密码不正确");
            }

        }

        UsersVO userVO = setUserRedisSessionToken(result);

        return JSONResult.ok(userVO);


    }

    public UsersVO setUserRedisSessionToken(Users userModel) {
        String uniqueToken = UUID.randomUUID().toString();
        redis.set(USER_REDIS_SESSION + ":" + userModel.getId(), uniqueToken, 1000 * 60 * 30);
        System.out.println("[redis]"+redis.get(USER_REDIS_SESSION + ":" + userModel.getId()));
        UsersVO userVO = new UsersVO();
        BeanUtils.copyProperties(userModel, userVO);
        userVO.setUserToken(uniqueToken);
        return userVO;
    }

    /**
     * @param userBO
     * @return com.megetood.utils.JSONResult
     * @Description 头像上传接口
     */
    @PostMapping("update/face")
    public JSONResult uploadFaceBase64(@RequestBody UsersBO userBO) throws Exception {
        System.out.println("[uploadFaceBase64]userBO:"+userBO.toString());
        // 获取前端传入的base64字符串，然后转换成文件对象
        String base64Data = userBO.getFaceData();
        String userFacePath = "C:\\" + userBO.getUserId() + "userface64.png";
        FileUtils.base64ToFile(userFacePath, base64Data);

        // 上传头像到fastdfs
        MultipartFile faceFile = FileUtils.fileToMultipart(userFacePath);
        String url = fastDFSClient.uploadBase64(faceFile);
        System.out.println("face file url:"+url);

        // ajdfa.png
        // ajdfa_80x80.png
        // 获取略缩图url
        String thump = "_80x80.";
        String arr[] = url.split("\\.");
        String thumpImgUrl = arr[0] + thump + arr[1];

        // 更新用户头像
        Users user = new Users();
        user.setId(userBO.getUserId())
                .setFaceImage(thumpImgUrl)
                .setFaceImageBig(url);

        Users userResult = userService.updateUserInfo(user);

        return JSONResult.ok(userResult);

    }

    /**
     * 更新用户昵称
     * @param userBO
     * @return
     * @throws Exception
     */
    @PostMapping("update/nickname")
    public JSONResult setNickname(@RequestBody UsersBO userBO) throws Exception {
//        log.info("user info:", userBO);
        // 设置更新参数
        Users user = new Users();
        user.setId(userBO.getUserId())
                .setNickname(userBO.getNickname());
        Users userResult = userService.updateUserInfo(user);

        return JSONResult.ok(userResult);
    }

    /**
     * 搜索好友
     * @param userId
     * @param friendUsername
     * @return
     */
    @GetMapping("{userId}/search/friend/{name}")
    public JSONResult searchUser(@PathVariable("userId") String userId,
                                 @PathVariable("name") String friendUsername){
        log.info("userId:"+userId+",txt:"+friendUsername);
        // 数据校验
        if(StringUtils.isBlank(friendUsername) || StringUtils.isBlank(userId)){
            return JSONResult.errorMsg("参数错误");
        }

        // 搜索
        // 1.搜索好友不存在
        // 2.搜索好友是用户自己
        // 3.搜索好友已经是用户自己的好友
        Integer status = userService.preconditionSearchFriends(userId, friendUsername);
        if(status == SearchFriendsStatusEnum.SUCCESS.status){
            // 匹配查询
            Users user = userService.queryUserByUsername(friendUsername);
            UsersVO userVO = new UsersVO();
            BeanUtils.copyProperties(user, userVO);
            return JSONResult.ok(userVO);
        }else{
            String errorMsg = SearchFriendsStatusEnum.getMsgByKey(status);
            return JSONResult.errorMsg(errorMsg);
        }
    }

    /**
     * 发送好友申请
     * @Param friendRequestDTO
     * @return
     */
    @PostMapping("send/friend/reqeust")
    public JSONResult sendFriendRequest(@RequestBody FriendRequestDTO friendRequestDTO){
        String userId = friendRequestDTO.getUserId();
        String friendUsername = friendRequestDTO.getFriendUsername();
        log.info("sendfriendrequest userId:"+userId+",txt:"+friendUsername);
        // 数据校验
        if(StringUtils.isBlank(friendUsername) || StringUtils.isBlank(userId)){
            return JSONResult.errorMsg("参数错误");
        }

        // 搜索
        // 1.搜索好友不存在
        // 2.搜索好友是用户自己
        // 3.搜索好友已经是用户自己的好友
        Integer status = userService.preconditionSearchFriends(userId, friendUsername);
        if(status == SearchFriendsStatusEnum.SUCCESS.status){
            userService.sendFriendRequest(userId, friendUsername);
            return JSONResult.ok();
        }else{
            String errorMsg = SearchFriendsStatusEnum.getMsgByKey(status);
            return JSONResult.errorMsg(errorMsg);
        }
    }

    /**
     * 获取好友请求列表
     * @param userId
     * @return
     */
    @GetMapping("{userId}/get/friendrequest/list")
    public JSONResult queryFriendRequestList(@PathVariable String userId){
        // 参数校验
        if(StringUtils.isBlank(userId)){
            return JSONResult.errorMsg("");
        }
        // 查询
        List<FriendRequestVO> friendRequestList = userService.queryFriendRequestList(userId);

        return  JSONResult.ok(friendRequestList);
    }

    /**
     * 操作好友请求
     * @param friendRequestOperDTO
     * @return
     */
    @PostMapping("oper/friend/request")
    public JSONResult operContactRequest(@RequestBody FriendRequestOperDTO friendRequestOperDTO){
        String acceptUserId = friendRequestOperDTO.getAcceptUserId();
        String sendUserId = friendRequestOperDTO.getSendUserId();
        Integer operType = friendRequestOperDTO.getOperType();
        // 参数校验
        if(StringUtils.isBlank(acceptUserId) || StringUtils.isBlank(sendUserId)
                    || operType == null){
            return JSONResult.errorMsg("参数错误");
        }
        // 操作类型未定义
        if(StringUtils.isBlank(OperatorFriendRequestTypeEnum.getMsgByType(operType))){
            return JSONResult.errorMsg("");
        }

        // 拒绝添加好友
        if(operType == OperatorFriendRequestTypeEnum.IGNORE.type){
            // 删除好友请求记录
            userService.deleteFriendRequest(sendUserId, acceptUserId);
        }
        // 通过好友请求
        if(operType == OperatorFriendRequestTypeEnum.PASS.type){
            // 添加好友请求记录
            userService.passFriendRequest(sendUserId, acceptUserId);
        }


        List<MyFriendsVO> myFriendList = userService.queryFriends(acceptUserId);
        return JSONResult.ok(myFriendList);
    }

    /**
     * 获取好友列表
     * @param userId
     * @return
     */
    @GetMapping("{userId}/get/friends")
    public JSONResult getContacts(@PathVariable String userId){
//        System.out.println("contactuserid:"+userId);
        if(StringUtils.isBlank(userId)){
            return JSONResult.errorMsg("");
        }

        List<MyFriendsVO> myFriendList = userService.queryFriends(userId);
//        log.info("contacts-userId:"+userId);
//        log.info("contacts-list:"+myFriendList);

        return JSONResult.ok(myFriendList);
    }


    /**
     * 用户手机端接收未读消息列表
     * @param acceptUserId
     * @return
     */
    @GetMapping("{userId}/get/unread/msgs")
    public JSONResult getUnReadMsgList(@PathVariable("userId") String acceptUserId){
        // 参数校验
        if(StringUtils.isBlank(acceptUserId)){
            return JSONResult.errorMsg("");
        }

        // 查询
        List<ChatMsg> unReadMsgList = userService.getUnReadMsgList(acceptUserId);

        return JSONResult.ok(unReadMsgList);
    }
}
