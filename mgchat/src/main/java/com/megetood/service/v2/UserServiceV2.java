package com.megetood.service.v2;

import com.megetood.pojo.dto.*;
import com.megetood.pojo.vo.FriendRequestVO;
import com.megetood.pojo.vo.MyFriendsVO;
import com.megetood.pojo.vo.UsersVO;

import java.util.List;

/**
 * @Date 2019/11/21 0:28
 */
public interface UserServiceV2 {

    /**
     * 登录注册
     *
     * @param userLoginDTO
     * @return
     */
    UsersVO loginOrRegister(UserLoginDTO userLoginDTO);

    /**
     * 修改用户头像
     *
     * @param userFaceDTO
     * @return
     */
    UsersVO updateUserFace(UserFaceDTO userFaceDTO);

    /**
     * 修改用户昵称
     *
     * @param userNicknameDTO
     * @return
     */
    UsersVO updateUserNickname(UserNicknameDTO userNicknameDTO);

    /**
     * 通过用户名搜索好友
     *
     * @param userId
     * @param friendUsername
     * @return
     */
    UsersVO searchFriendByUsername(String userId, String friendUsername);

    /**
     * 操作好友请求
     *
     * @param friendRequestOperDTO
     * @return
     */
    List<MyFriendsVO> operFriendRequest(FriendRequestOperDTO friendRequestOperDTO);

    /**
     * 发送好友请求
     *
     * @param friendRequestDTO
     * @return
     */
    String sendFriendRequest(FriendRequestDTO friendRequestDTO);

    /**
     * 查询好友请求列表
     *
     * @param acceptUserId
     * @return
     */
    List<FriendRequestVO> queryFriendRequestList(String acceptUserId);

    /**
     * 查询通讯录记录
     *
     * @param userId
     * @return
     */
    List<MyFriendsVO> queryFriends(String userId);

}
