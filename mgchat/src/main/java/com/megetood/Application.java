package com.megetood;

import com.megetood.utils.SpringUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author Megetood
 * @date 2019/11/20 23:23
 */

@SpringBootApplication(scanBasePackages = {"com.megetood","org.n3r.idworker"})
@MapperScan(basePackages = "com.megetood.mapper")
public class Application {
    @Bean
    public SpringUtil getSpringUtil(){
        return new SpringUtil();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class,args);
    }
}
