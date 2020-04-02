package com.megetood.core;

import com.megetood.core.configuration.ExceptionCodeConfiguration;
import com.megetood.exception.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintViolationException;
import java.util.List;

/**
 * @Date 2020/3/17 15:27
 */
@ControllerAdvice
public class GlobalExceptionAdvice {

    @Autowired
    private ExceptionCodeConfiguration codeConfiguration;

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public UnifyResponse handelException(HttpServletRequest req, Exception e){
        System.out.println("exception-----");
        e.printStackTrace();
        String requestUrl = req.getRequestURI();
        String method = req.getMethod();
        UnifyResponse message = new UnifyResponse(999,codeConfiguration.getMessage(999),method+" "+requestUrl);
        return message;
    }

    @ExceptionHandler(value = HttpException.class)
    public ResponseEntity handelHttpException(HttpServletRequest req, HttpException e){

        String requestUrl = req.getRequestURI();
        String method = req.getMethod();

        System.out.println("httpexception----message"+codeConfiguration.getMessage(e.getCode()));
        UnifyResponse message = new UnifyResponse(e.getCode(),codeConfiguration.getMessage(e.getCode()),method+" "+requestUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpStatus httpStatus = HttpStatus.resolve(e.getHttpStatusCode());

        ResponseEntity<UnifyResponse> r = new ResponseEntity<>(message,headers,httpStatus);

        return r;
    }


    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public UnifyResponse handleBeanValidation(HttpServletRequest req, MethodArgumentNotValidException e){
        System.out.println("MethodArgumentNotValidException");
        String requestUrl = req.getRequestURI();
        String method = req.getMethod();

        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        String message = formatAllErrorMessages(errors);

        return new UnifyResponse(1001,message,method+" "+requestUrl);

    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public UnifyResponse handleConstraintViolationException(HttpServletRequest req, ConstraintViolationException e){
        System.out.println("ConstraintViolationException");
        String requestUrl = req.getRequestURI();
        String method = req.getMethod();

        String message = e.getMessage();

        return new UnifyResponse(1001,message,method+" "+requestUrl);

    }

    private String formatAllErrorMessages(List<ObjectError> errors) {
        StringBuffer errorMsg = new StringBuffer();
        errors.forEach(error->
                errorMsg.append(error.getDefaultMessage()).append(";")
        );

        return errorMsg.toString();
    }
}
