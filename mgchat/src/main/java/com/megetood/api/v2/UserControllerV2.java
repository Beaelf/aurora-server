package com.megetood.api.v2;

import com.megetood.enums.OperatorFriendRequestTypeEnum;
import com.megetood.pojo.ChatMsg;
import com.megetood.pojo.dto.*;
import com.megetood.pojo.vo.FriendRequestVO;
import com.megetood.pojo.vo.MyFriendsVO;
import com.megetood.pojo.vo.OkVO;
import com.megetood.pojo.vo.UsersVO;
import com.megetood.service.v2.UserServiceV2;
import com.megetood.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Megetood
 * @date 2019/11/20 23:58
 */
@Slf4j
@RestController("userControllerV2")
@RequestMapping("/user/")
public class UserControllerV2 {

    @Autowired
    private UserServiceV2 userServiceV2;

    @Autowired
    private FastDFSClient fastDFSClient;

    /**
     * 注册/登录
     * @param userLoginDTO 接收用户名，密码，客户端id
     * @return
     * @throws Exception
     */
    @PostMapping("registerOrLogin")
    public UsersVO RegisterOrLogin(@RequestBody @Validated UserLoginDTO userLoginDTO) throws Exception {

        UsersVO userVO = userServiceV2.loginOrRegister(userLoginDTO);

        return userVO;
    }

    /**
     * 上传头像接口
     * @param userFaceDTO
     * @return
     * @throws Exception
     */
    @PostMapping("update/face")
    public UsersVO uploadFaceBase64(@RequestBody @Validated UserFaceDTO userFaceDTO){

        UsersVO userVO = userServiceV2.updateUserFace(userFaceDTO);

        return userVO;

    }

    /**
     * 更新用户昵称
     * @param userNicknameDTO
     * @return
     */
    @PostMapping("update/nickname")
    public UsersVO setNickname(@RequestBody @Validated UserNicknameDTO userNicknameDTO){

        UsersVO userVO = userServiceV2.updateUserNickname(userNicknameDTO);

        return userVO;
    }

    /**
     * 搜索好友
     * @param userId
     * @param friendUsername
     * @return
     */
    @GetMapping("{userId}/search/friend/{name}")
    public UsersVO searchFriend(@PathVariable("userId") @NotNull String userId,
                                 @PathVariable("name") @NotNull String friendUsername){

        UsersVO userVO = userServiceV2.searchFriendByUsername(userId, friendUsername);

        return userVO;
    }

    /**
     * 发送好友申请
     * @Param friendRequestDTO
     * @return
     */
    @PostMapping("send/friend/reqeust")
    public OkVO sendFriendRequest(@RequestBody FriendRequestDTO friendRequestDTO){

        String result = userServiceV2.sendFriendRequest(friendRequestDTO);

        return new OkVO();
    }

    /**
     * 获取好友请求列表
     * @param userId
     * @return
     */
    @GetMapping("{userId}/get/friendrequest/list")
    public List<FriendRequestVO> queryFriendRequestList(@PathVariable @NotNull String userId){
        // 查询
        List<FriendRequestVO> friendRequestList = userServiceV2.queryFriendRequestList(userId);

        return  friendRequestList;
    }

    /**
     * 操作好友请求
     * @param friendRequestOperDTO
     * @return
     */
    @PostMapping("oper/friend/request")
    public List<MyFriendsVO> operFriendRequest(@RequestBody @Validated FriendRequestOperDTO friendRequestOperDTO) {

        List<MyFriendsVO> myFreinds = userServiceV2.operFriendRequest(friendRequestOperDTO);

        return myFreinds;
    }

    /**
     * 获取好友列表
     * @param userId
     * @return
     */
    @GetMapping("{userId}/get/friends")
    public List<MyFriendsVO> getContacts(@PathVariable @NotNull String userId){

        List<MyFriendsVO> myFriendList = userServiceV2.queryFriends(userId);

        return myFriendList;
    }


}
