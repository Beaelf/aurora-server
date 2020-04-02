package com.megetood.service.v2.impl;/**
 * @author Megetood
 * @date 2019/11/21 0:32
 */

import com.megetood.enums.MsgActionEnum;
import com.megetood.enums.MsgSignFlagEnum;
import com.megetood.enums.OperatorFriendRequestTypeEnum;
import com.megetood.enums.SearchFriendsStatusEnum;
import com.megetood.exception.http.CommonException;
import com.megetood.mapper.*;
import com.megetood.netty.entity.ChatMsg;
import com.megetood.netty.entity.DataContent;
import com.megetood.netty.entity.UserChannelRel;
import com.megetood.pojo.FriendsRequest;
import com.megetood.pojo.MyFriends;
import com.megetood.pojo.Users;
import com.megetood.pojo.bo.UsersBO;
import com.megetood.pojo.dto.*;
import com.megetood.pojo.vo.FriendRequestVO;
import com.megetood.pojo.vo.MyFriendsVO;
import com.megetood.pojo.vo.UsersVO;
import com.megetood.service.v2.UserServiceV2;
import com.megetood.utils.*;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.beans.Beans;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.megetood.interceptor.MiniInterceptor.USER_REDIS_SESSION;

/**
 * @Date 2019/11/21 0:32
 */
@Service
public class UserServiceImplV2 implements UserServiceV2 {

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private UsersMapperCustom usersMapperCustom;

    @Autowired
    private MyFriendsMapper myFriendsMapper;

    @Autowired
    private FriendsRequestMapper friendsRequestMapper;

    @Autowired
    private RedisOperator redis;

    @Autowired
    private QRCodeUtils qrCodeUtils;

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private FastDFSClient fastDFSClient;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public UsersVO loginOrRegister(UserLoginDTO userLoginDTO){

        // 1.判断用户名是否存在，存在则登录，不存在则注册
        boolean usernameIsExist = queryUsernameIsExist(userLoginDTO.getUsername());
        Users result = null;
        if (usernameIsExist) {
            // 1.1 登录
            try {
                result = login(userLoginDTO.getUsername(), MD5Utils.getMD5Str(userLoginDTO.getPassword()));
            } catch (Exception e) {
                e.printStackTrace();
                // TODO
            }
            if (result == null) {// 登录失败
                throw new CommonException(1006);
            }
        } else {
            // 1.2 注册
            try {
                result = register(userLoginDTO);
            } catch (Exception e) {
                e.printStackTrace();
                // TODO
            }
            if (result == null) {// 注册失败
                throw new CommonException(1007);
            }
        }

        UsersVO userVO = setUserRedisSessionToken(result);

        return userVO;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public UsersVO updateUserFace(UserFaceDTO userFaceDTO){
        // 获取前端传入的base64字符串，然后转换成文件对象
        String base64Data = userFaceDTO.getFaceData();
        String userFacePath = "C:\\" + userFaceDTO.getUserId() + "userface64.png";
        try {
            FileUtils.base64ToFile(userFacePath, base64Data);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO
        }

        // 上传头像到fastdfs
        MultipartFile faceFile = FileUtils.fileToMultipart(userFacePath);
        String url = null;
        try {
            url = fastDFSClient.uploadBase64(faceFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CommonException(1009);
        }
        System.out.println("face file url:"+url);

        // ajdfa.png
        // ajdfa_80x80.png
        // 获取略缩图url
        String thump = "_80x80.";
        String arr[] = url.split("\\.");
        String thumpImgUrl = arr[0] + thump + arr[1];

        // 更新用户头像
        Users user = new Users();
        user.setId(userFaceDTO.getUserId())
                .setFaceImage(thumpImgUrl)
                .setFaceImageBig(url);

        Users userResult = updateUserInfo(user);

        UsersVO userVO = makeUsersToUsersVO(userResult);

        return userVO;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public UsersVO updateUserNickname(UserNicknameDTO userNicknameDTO) {
        // 设置更新参数
        Users user = new Users();
        user.setId(userNicknameDTO.getUserId())
                .setNickname(userNicknameDTO.getNickname());
        Users userResult = updateUserInfo(user);

        UsersVO userVO = makeUsersToUsersVO(userResult);

        return userVO;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public UsersVO searchFriendByUsername(String userId, String friendUsername) {

        // status:
        // 0.ok
        // 1.搜索好友不存在
        // 2.搜索好友是用户自己
        // 3.搜索好友已经是用户自己的好友
        Integer status = preconditionSearchFriends(userId, friendUsername);
        if(status != SearchFriendsStatusEnum.SUCCESS.status){
            throw new CommonException(status);
        }

        // 匹配查询
        Users user = queryUserByUsername(friendUsername);
        UsersVO userVO = makeUsersToUsersVO(user);
        return userVO;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String sendFriendRequest(FriendRequestDTO friendRequestDTO) {
        String userId = friendRequestDTO.getUserId();
        String friendUsername = friendRequestDTO.getFriendUsername();

        Integer status = preconditionSearchFriends(userId, friendUsername);
        if(status != SearchFriendsStatusEnum.SUCCESS.status){
            throw new CommonException(status);
        }

        sendFriendRequest(userId, friendUsername);
        return "ok";
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public List<MyFriendsVO> operFriendRequest(FriendRequestOperDTO friendRequestOperDTO) {
        String acceptUserId = friendRequestOperDTO.getAcceptUserId();
        String sendUserId = friendRequestOperDTO.getSendUserId();
        Integer operType = friendRequestOperDTO.getOperType();

        // 操作类型未定义
        if(StringUtils.isBlank(OperatorFriendRequestTypeEnum.getMsgByType(operType))){
            throw new CommonException(1001);
        }
        // 拒绝添加好友
        if(operType == OperatorFriendRequestTypeEnum.IGNORE.type){
            // 删除好友请求记录
            deleteFriendRequest(sendUserId, acceptUserId);
        }
        // 通过好友请求
        if(operType == OperatorFriendRequestTypeEnum.PASS.type){
            // 添加好友请求记录
            passFriendRequest(sendUserId, acceptUserId);
        }


        List<MyFriendsVO> myFriendList = queryFriends(acceptUserId);

        return myFriendList;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<FriendRequestVO> queryFriendRequestList(String acceptUserId) {
        return usersMapperCustom.queryFriendRequestList(acceptUserId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<MyFriendsVO> queryFriends(String userId) {
        return usersMapperCustom.queryMyFriends(userId);
    }

    // 查询用户名是否存在
    private boolean queryUsernameIsExist(String username) {
        // 设置查询条件
        Users user = new Users();
        user.setUsername(username);

        // 查询
        Users userResult = usersMapper.selectOne(user);

        // 用户名存在
        if (userResult != null) {
            return true;
        }
        // 不存在
        return false;
    }

    // 保存redis token
    private UsersVO setUserRedisSessionToken(Users userModel) {
        String uniqueToken = UUID.randomUUID().toString();
        System.out.println(redis);
        redis.set(USER_REDIS_SESSION + ":" + userModel.getId(), uniqueToken, 1000 * 60 * 30);
        System.out.println("[redis]"+redis.get(USER_REDIS_SESSION + ":" + userModel.getId()));
        UsersVO userVO = makeUsersToUsersVO(userModel);
        userVO.setUserToken(uniqueToken);
        return userVO;
    }

    // 登录
    private Users login(String username, String password) {
        // 设置查询条件
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username", username);
        criteria.andEqualTo("password", password);

        // 查询
        Users userResult = usersMapper.selectOneByExample(example);
        return userResult;
    }

    // 注册
    private Users register(UserLoginDTO userLoginDTO){
        Users user = new Users();
                BeanUtils.copyProperties(userLoginDTO,user);
        // 判断用户名是否存在
        boolean usernameIsExist = queryUsernameIsExist(user.getUsername());
        if (usernameIsExist) {
            throw new CommonException(2001);
        }

        // 生成主键id
        String userId = sid.nextShort();
        // 生成二维码 mgchat_qrcode:[username]
        String qrCodePath = "C://user" + userId + "qrcode.png";
        qrCodeUtils.createQRCode(qrCodePath, "mgchat_qrcode:" + user.getUsername());
        MultipartFile qrCodeFile = fileUtils.fileToMultipart(qrCodePath);

        // 上传至文件服务器
        String qrCodeUrl = "";
        try {
            qrCodeUrl = fastDFSClient.uploadQRCode(qrCodeFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new CommonException(1008);
        }

        // 设置注册信息
        try {
            user.setQrcode(qrCodeUrl)
                    .setPassword(MD5Utils.getMD5Str(user.getPassword()))
                    .setNickname(user.getUsername())
                    .setId(userId)
                    .setFaceImageBig("")
                    .setFaceImage("")
                    .setUsername(user.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
        }
        usersMapper.insert(user);
        return user;
    }

    // 更新用户信息
    private Users updateUserInfo(Users user) {
        usersMapper.updateByPrimaryKeySelective(user);
        Users userResult = queryUserById(user.getId());
        return userResult;
    }

    // 搜索好友的前置条件
    private Integer preconditionSearchFriends(String userId, String friendUsername) {
        Users user = queryUserByUsername(friendUsername);
        // 1.搜索好友不存在
        if (user == null) {
            return SearchFriendsStatusEnum.USER_NOT_EXIST.status;
        }
        // 2.搜索好友是用户自己
        if (userId.equals(user.getId())) {
            return SearchFriendsStatusEnum.NOT_YOURSELF.status;
        }
        // 3.搜索好友已经是用户自己的好友
        Example example = new Example(MyFriends.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("myUserId", userId);
        criteria.andEqualTo("myFriendUserId", user.getId());

        MyFriends myFriends = myFriendsMapper.selectOneByExample(example);
        if (myFriends != null) {
            return SearchFriendsStatusEnum.ALREADY_FRIENDS.status;
        }

        return SearchFriendsStatusEnum.SUCCESS.status;
    }

    // 通过用户名查询用户
    private Users queryUserByUsername(String username) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username", username);

        return usersMapper.selectOneByExample(example);
    }

    // 发送好友请求
    private void sendFriendRequest(String userId, String friendUsername) {

        // 1.搜索好友不存在
        // 2.搜索好友是用户自己
        // 3.搜索好友已经是用户自己的好友
        // 查询出请求添加的好友信息
        Users newContact = queryUserByUsername(friendUsername);

        // 查询是否存在该好友申请记录
        Example example = new Example(FriendsRequest.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sendUserId", userId);
        criteria.andEqualTo("acceptUserId", newContact.getId());

        FriendsRequest friendsRequest = friendsRequestMapper.selectOneByExample(example);

        if (friendsRequest == null) {
            // 如果不存在申请记录，新增
            String requestId = sid.nextShort();
            FriendsRequest request = new FriendsRequest();
            request.setId(requestId)
                    .setSendUserId(userId)
                    .setAcceptUserId(newContact.getId())
                    .setRequestDataTime(new Date());
            System.out.println(request.toString());

            friendsRequestMapper.insert(request);
        }
    }

    // 删除好友请求
    private void deleteFriendRequest(String sendUserId, String acceptUserId) {
        Example example = new Example(FriendsRequest.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sendUserId", sendUserId);
        criteria.andEqualTo("acceptUserId", acceptUserId);
        friendsRequestMapper.deleteByExample(example);
    }

    // 通过好友请求
    private void passFriendRequest(String sendUserId, String acceptUserId) {
        saveMyFriends(acceptUserId, sendUserId);
        saveMyFriends(sendUserId, acceptUserId);
        deleteFriendRequest(sendUserId, acceptUserId);

        // 使用websocket推送消息到请求发起者，更新他的通讯录列表为最新
        Channel senderChannel = UserChannelRel.get(sendUserId);
        if (senderChannel != null) {
            DataContent dataContent = new DataContent();
            dataContent.setAction(MsgActionEnum.PULL_FRIEND.type);
            senderChannel.writeAndFlush(
                    new TextWebSocketFrame(
                            JsonUtils.objectToJson(dataContent)));
        }
    }

    // 添加好友
    private void saveMyFriends(String sendUserId, String acceptUserId) {
        MyFriends myFriends = new MyFriends();
        myFriends.setMyUserId(acceptUserId)
                .setMyFriendUserId(sendUserId)
                .setId(sid.nextShort());
        myFriendsMapper.insert(myFriends);
    }

    // 通过用户id查询用户
    private Users queryUserById(String userId) {
        return usersMapper.selectByPrimaryKey(userId);
    }

    // 组织返回对象
    private UsersVO makeUsersToUsersVO(Users userResult) {
        UsersVO userVO = new UsersVO();
        BeanUtils.copyProperties(userResult, userVO);
        return userVO;
    }
}
