package com.megetood.exception.http;

import lombok.Data;

/**
 * @Date 2020/3/17 15:32
 */
@Data
public class HttpException extends RuntimeException {
    protected Integer code;
    protected Integer httpStatusCode = 500;
}
