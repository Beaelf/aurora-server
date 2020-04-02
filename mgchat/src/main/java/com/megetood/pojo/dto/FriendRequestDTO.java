package com.megetood.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Date 2020/3/24 17:53
 */
@Data
@Accessors(chain = true)
public class FriendRequestDTO {
    private String userId;
    private String friendUsername;
}
