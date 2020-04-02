package com.megetood.service.v1.impl;/**
 * @author Megetood
 * @date 2019/11/21 0:32
 */

import com.megetood.enums.MsgActionEnum;
import com.megetood.enums.MsgSignFlagEnum;
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
import com.megetood.pojo.vo.FriendRequestVO;
import com.megetood.pojo.vo.MyFriendsVO;
import com.megetood.pojo.vo.UsersVO;
import com.megetood.service.v1.UserService;
import com.megetood.utils.*;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.megetood.interceptor.MiniInterceptor.USER_REDIS_SESSION;

/**
 * @Date 2019/11/21 0:32
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private RedisOperator redis;

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private UsersMapperCustom usersMapperCustom;

    @Autowired
    private MyFriendsMapper myFriendsMapper;

    @Autowired
    private FriendsRequestMapper friendsRequestMapper;

    @Autowired
    private ChatMsgMapper chatMsgMapper;


    @Autowired
    private QRCodeUtils qrCodeUtils;

    @Autowired
    private FileUtils fileUtils;

    @Autowired
    private FastDFSClient fastDFSClient;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean queryUsernameIsExist(String username) {
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


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public UsersVO loginOrRegister(Users user) throws Exception {
        // 1.判断用户名是否存在，存在则登录，不存在则注册
        boolean usernameIsExist = queryUsernameIsExist(user.getUsername());

        Users userResult = null;
        // 注册
        if (!usernameIsExist) {
            userResult = register(user);
            // 注册失败
            if (userResult == null) {
                throw new CommonException(1007);
            }

        }
        // 登录
        userResult = login(user.getUsername(), MD5Utils.getMD5Str(user.getPassword()));
        if (userResult == null) {
            throw new CommonException(1006);
        }

        // 保存redis token
        UsersVO userVO = setUserRedisSessionToken(userResult);

        return userVO;
    }

    // 保存redis token
    private UsersVO setUserRedisSessionToken(Users userModel) {
        String uniqueToken = UUID.randomUUID().toString();
        System.out.println(redis);
        redis.set(USER_REDIS_SESSION + ":" + userModel.getId(), uniqueToken, 1000 * 60 * 30);
        System.out.println("[redis]"+redis.get(USER_REDIS_SESSION + ":" + userModel.getId()));
        UsersVO userVO = new UsersVO();
        BeanUtils.copyProperties(userModel, userVO);
        userVO.setUserToken(uniqueToken);
        return userVO;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users login(String username, String password) {
        // 设置查询条件
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username", username);
        criteria.andEqualTo("password", password);

        // 查询
        Users userResult = usersMapper.selectOneByExample(example);
        return userResult;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users register(Users user) throws Exception {
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
        qrCodeUrl = fastDFSClient.uploadQRCode(qrCodeFile);

        // 设置注册信息
        user.setQrcode(qrCodeUrl)
                .setPassword(MD5Utils.getMD5Str(user.getPassword()))
                .setNickname(user.getUsername())
                .setId(userId)
                .setFaceImageBig("")
                .setFaceImage("")
                .setUsername(user.getUsername());
        usersMapper.insert(user);
        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public UsersVO updateUserInfo(String userId, String nickname) {
        Users user = new Users();
        user.setId(userId).setNickname(nickname);

        Users userResult = updateUserInfo(user);

        UsersVO userVO = new UsersVO();
        BeanUtils.copyProperties(userResult,userVO);

        return userVO;
    }
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Users updateUserInfo(Users user) {
        usersMapper.updateByPrimaryKeySelective(user);
        Users userResult = queryUserById(user.getId());
        return userResult;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public UsersVO updateUserFace(UsersBO userBO) throws Exception {
        // 获取前端传入的base64字符串，然后转换成文件对象
        String base64Data = userBO.getFaceData();
        String userFacePath = "C:\\" + userBO.getUserId() + "userface64.png";
        FileUtils.base64ToFile(userFacePath, base64Data);

        // 上传头像到fastdfs
        MultipartFile faceFile = FileUtils.fileToMultipart(userFacePath);
        String url = fastDFSClient.uploadBase64(faceFile);
//        System.out.println("face file url:"+url);

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

        Users userResult = updateUserInfo(user);

        UsersVO userVO = new UsersVO();
        BeanUtils.copyProperties(userResult,userVO);

        return userVO;
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Integer preconditionSearchFriends(String userId, String friendUsername) {
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

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserByUsername(String username) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username", username);

        return usersMapper.selectOneByExample(example);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void sendFriendRequest(String userId, String friendUsername) {

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

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<FriendRequestVO> queryFriendRequestList(String acceptUserId) {
        return usersMapperCustom.queryFriendRequestList(acceptUserId);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteFriendRequest(String sendUserId, String acceptUserId) {
        Example example = new Example(FriendsRequest.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("sendUserId", sendUserId);
        criteria.andEqualTo("acceptUserId", acceptUserId);
        friendsRequestMapper.deleteByExample(example);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void passFriendRequest(String sendUserId, String acceptUserId) {
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

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<MyFriendsVO> queryFriends(String userId) {
        return usersMapperCustom.queryMyFriends(userId);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String saveMsg(ChatMsg chatmsg) {
        com.megetood.pojo.ChatMsg msgDB = new com.megetood.pojo.ChatMsg();
        String msgId = sid.nextShort();
        msgDB.setSignFlag(MsgSignFlagEnum.unsign.type)
                .setSendUserId(chatmsg.getSenderId())
                .setMsg(chatmsg.getMsg())
                .setId(msgId)
                .setCreateTime(new Date())
                .setAcceptUserId(chatmsg.getReceiverId());

        chatMsgMapper.insert(msgDB);
        return msgId;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateMsgSigned(List<String> msgIdList) {
        usersMapperCustom.batchUpdateMsgSigned(msgIdList);
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<com.megetood.pojo.ChatMsg> getUnReadMsgList(String acceptUserId) {
        Example example = new Example(com.megetood.pojo.ChatMsg.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("signFlag", 0);
        criteria.andEqualTo("acceptUserId", acceptUserId);

        List<com.megetood.pojo.ChatMsg> chatMsgList = chatMsgMapper.selectByExample(example);
        return chatMsgList;
    }

    private void saveMyFriends(String sendUserId, String acceptUserId) {
        MyFriends myFriends = new MyFriends();
        myFriends.setMyUserId(acceptUserId)
                .setMyFriendUserId(sendUserId)
                .setId(sid.nextShort());
        myFriendsMapper.insert(myFriends);
    }

    private Users queryUserById(String userId) {
        return usersMapper.selectByPrimaryKey(userId);
    }


}
