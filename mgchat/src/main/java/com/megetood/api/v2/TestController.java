package com.megetood.api.v2;

import com.megetood.exception.http.CommonException;
import com.megetood.exception.http.ForbiddenException;
import com.megetood.exception.http.NotFoundException;
import com.megetood.pojo.dto.PersonDTO;
import netscape.security.ForbiddenTargetException;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import java.util.Map;

/**
 * @Date 2020/3/19 11:54
 */
@RestController
@Validated
public class TestController {
    @RequestMapping("/hello")
    public String hello(){
        throw new ForbiddenException(1001);
    }

    @RequestMapping("/test2")
    public String test2(){
        throw new CommonException(1000);
    }

    @PostMapping("/test/{id}")
    public String test(@PathVariable @Max(value = 10,message = "不能超过10...") Integer id,
                       @RequestParam @Length(min=4) String name,
                       @RequestBody @Validated PersonDTO person){
        System.out.println(id+name);
//        throw new CommonException(1000);
        return "test demo";
    }
}
