package com.megetood.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class UserFaceDTO {
    @NotNull
    private String userId;
    @NotNull
    private String faceData;

    @Override
    public String toString() {
        return "UsersBO{" +
                "userId='" + userId + '\'' +
                ", faceData='" + faceData + '\'' +
                '}';
    }
}