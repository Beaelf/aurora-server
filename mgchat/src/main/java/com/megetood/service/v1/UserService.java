package com.megetood.service.v1;/**
 * @author Megetood
 * @date 2019/11/21 0:28
 */

import com.megetood.netty.entity.ChatMsg;
import com.megetood.pojo.Users;
import com.megetood.pojo.bo.UsersBO;
import com.megetood.pojo.vo.FriendRequestVO;
import com.megetood.pojo.vo.MyFriendsVO;
import com.megetood.pojo.vo.UsersVO;

import java.util.List;

/**
 * @Date 2019/11/21 0:28
 */
public interface UserService {

    /**
     * @Description 判断用户名是否存在
     * @param username
     * @return boolean
     */
    public boolean queryUsernameIsExist(String username);

    /**
     * 登录注册
     * @param user
     * @return
     */
    UsersVO loginOrRegister(Users user) throws Exception;

    /**
     * @Description 登录
     * @param username
     * @param password
     * @return com.megetood.pojo.Users
     */
    public Users login(String username,String password);

    /**
     * @Description 注册
     * @param user
     * @return com.megetood.pojo.Users
     */
    public Users register(Users user) throws Exception;

    /**
     * @Description 修改用户信息
     * @param user
     * @return com.megetood.pojo.Users
     */
    public Users updateUserInfo(Users user);

    public UsersVO updateUserInfo(String userId, String nickname);
    /**
     * 修改用户头像
     * @param usersBO
     * @return
     */
    public UsersVO updateUserFace(UsersBO usersBO) throws Exception;

    /**
     * 搜索好友的前置条件
     * @param userId
     * @param friendUsername
     * @return
     */
    public Integer preconditionSearchFriends(String userId, String friendUsername);

    /**
     * 通过用户名查询用户信息
     * @param username
     * @return
     */
    public Users queryUserByUsername(String username);

    /**
     * 发送添加好友请求
     * @param userId
     * @param friendUsername
     */
    public void sendFriendRequest(String userId, String friendUsername);


    /**
     * 查询好友请求列表
     * @param acceptUserId
     * @return
     */
    public List<FriendRequestVO> queryFriendRequestList(String acceptUserId);

    /**
     * 删除好友请求记录
     * @param sendUserId
     * @param acceptUserId
     */
    public void deleteFriendRequest(String sendUserId, String acceptUserId);


    /**
     * 通过好友请求：
     *  1.新增好友记录
     *  2.逆向新增好友记录
     *  3.删除好友请求记录
     * @param sendUserId
     * @param acceptUserId
     */
    public void passFriendRequest(String sendUserId, String acceptUserId);

    /**
     * 查询通讯录记录
     * @param userId
     * @return
     */
    public List<MyFriendsVO> queryFriends(String userId);

    /**
     * 保存聊天消息到数据库
     * @param chatmsg
     * @return
     */
    public String saveMsg(ChatMsg chatmsg);

    /**
     * 批量签收消息
     * @param msgIdList
     */
    public void updateMsgSigned(List<String> msgIdList);

    /**
     * 获取未签收消息列表
     * @param acceptUserId
     * @return
     */
    public List<com.megetood.pojo.ChatMsg> getUnReadMsgList(String acceptUserId);
}
