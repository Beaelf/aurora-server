package com.megetood.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.coyote.http11.filters.SavedRequestInputFilter;

import javax.validation.constraints.NotNull;

/**
 * @Date 2020/3/30 17:19
 */
@Data
@Accessors(chain = true)
public class VideoUploadDTO {
    @NotNull
    private String userId;
    private String bgmId;
    private String des;

    @Override
    public String toString() {
        return "VideoUploadDTO{" +
                "userId='" + userId + '\'' +
                ", bgmId='" + bgmId + '\'' +
                ", des='" + des + '\'' +
                '}';
    }
}
