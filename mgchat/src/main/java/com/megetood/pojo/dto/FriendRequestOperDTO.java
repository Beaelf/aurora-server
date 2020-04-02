package com.megetood.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @Date 2020/3/24 17:55
 */
@Data
@Accessors(chain = true)
public class FriendRequestOperDTO {
    @NotNull
    private String acceptUserId;
    @NotNull
    private String sendUserId;
    @NotNull
    private Integer operType;
}
