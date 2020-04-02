package com.megetood.exception.http;

/**
 * 项目已知异常
 * @Date 2020/3/20 19:15
 */
public class CommonException extends HttpException {
    public CommonException(int code){
        this.code = code;
        this.httpStatusCode=500;
    }
}
