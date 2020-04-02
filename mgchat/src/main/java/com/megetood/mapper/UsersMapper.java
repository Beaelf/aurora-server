package com.megetood.mapper;

import com.megetood.pojo.Users;
import com.megetood.utils.MyMapper;

public interface UsersMapper extends MyMapper<Users> {
    /**
     * 用户受喜欢数累加
     * @param userId
     */
    public void addReceiveLikeCount(String userId);

    /**
     * 用户受喜欢数累减
     * @param userId
     */
    public void reduceReceiveLikeCount(String userId);
}