package com.megetood.utils;

import com.megetood.pojo.Users;
import com.megetood.pojo.vo.UsersVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;


import java.util.UUID;

import static com.megetood.interceptor.MiniInterceptor.USER_REDIS_SESSION;

/**
 * @Date 2020/3/20 19:37
 */
public class CommonUtil {

    private static RedisOperator redis =new RedisOperator();


    // 保存redis token
    public static UsersVO setUserRedisSessionToken(Users userModel) {
        String uniqueToken = UUID.randomUUID().toString();
        System.out.println(redis);
        redis.set(USER_REDIS_SESSION + ":" + userModel.getId(), uniqueToken, 1000 * 60 * 30);
        System.out.println("[redis]"+redis.get(USER_REDIS_SESSION + ":" + userModel.getId()));
        UsersVO userVO = new UsersVO();
        BeanUtils.copyProperties(userModel, userVO);
        userVO.setUserToken(uniqueToken);
        return userVO;
    }
}
