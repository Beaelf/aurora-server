package com.megetood.pojo.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class UsersBO {
    private String userId;
    private String faceData;

    private String nickname;

    @Override
    public String toString() {
        return "UsersBO{" +
                "userId='" + userId + '\'' +
                ", faceData='" + faceData + '\'' +
                ", nickname='" + nickname + '\'' +
                '}';
    }
}