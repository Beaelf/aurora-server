package com.megetood.mapper;

import com.megetood.pojo.Users;
import com.megetood.pojo.vo.FriendRequestVO;
import com.megetood.pojo.vo.MyFriendsVO;
import com.megetood.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UsersMapperCustom extends MyMapper<Users> {

    public List<FriendRequestVO> queryFriendRequestList(@Param("acceptUserId") String acceptUserId);
    public List<MyFriendsVO> queryMyFriends(@Param("userId") String userId);
    public void batchUpdateMsgSigned( List<String> msgIdList);
}