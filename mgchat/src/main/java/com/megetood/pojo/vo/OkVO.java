package com.megetood.pojo.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Date 2020/3/31 17:19
 */
@Data
@Accessors(chain = true)
public class OkVO {
    private String msg;

    public OkVO(){
        this.msg = "ok";
    }
}
