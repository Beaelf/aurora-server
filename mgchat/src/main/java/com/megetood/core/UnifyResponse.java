package com.megetood.core;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Date 2020/3/17 15:42
 */
@Data
@Accessors(chain = true)
public class UnifyResponse {
    private int code;
    private String message;
    private String request;

    public UnifyResponse(int code, String message, String request) {
        this.code = code;
        this.message = message;
        this.request = request;
    }
}
