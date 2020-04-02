package com.megetood.core.configuration;

import com.megetood.interceptor.MiniInterceptor;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.MultipartConfigElement;

/**
 * @Author Megetood
 * @Date 2019/12/16 22:25
 */
@Configuration
public class MgChatCongfig implements WebMvcConfigurer {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        System.out.println("文件配置");
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //  单个数据大小
        factory.setMaxFileSize("102400KB"); // KB,MB
        /// 总上传数据大小
        factory.setMaxRequestSize("1024000KB");
        return factory.createMultipartConfig();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations("file:D:/mgchat/");
    }

    // 配置拦截器
    @Bean
    public MiniInterceptor miniInterceptor() {
        return new MiniInterceptor();
    }

    // 对拦截器进行注册
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(miniInterceptor())
//                .addPathPatterns("/user/**")
//                .addPathPatterns("/video/upload",
//                        "/video/likeOperate",
//                        "/video/saveComment")
//                .excludePathPatterns("/user/registerOrLogin");
//        WebMvcConfigurer.super.addInterceptors(registry);
//    }

}
