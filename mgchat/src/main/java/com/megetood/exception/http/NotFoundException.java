package com.megetood.exception.http;

/**
 * 请求资源不存在
 * @Date 2020/3/17 15:33
 */
public class NotFoundException extends HttpException {
    public NotFoundException(int code) {
        this.httpStatusCode = 404;
        this.code = code;
    }
}
