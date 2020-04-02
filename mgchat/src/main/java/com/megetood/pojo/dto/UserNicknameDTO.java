package com.megetood.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class UserNicknameDTO {
    @NotNull
    private String userId;
    @NotNull
    private String nickname;

    @Override
    public String toString() {
        return "UsersBO{" +
                "userId='" + userId + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}