package com.megetood.exception.http;

/**
 * 禁止访问
 * @Date 2020/3/17 15:34
 */
public class ForbiddenException extends HttpException {
    public ForbiddenException(int code){
        this.httpStatusCode = 403;
        this.code = code;
    }
}
