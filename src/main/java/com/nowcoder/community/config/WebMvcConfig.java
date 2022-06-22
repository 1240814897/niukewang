package com.nowcoder.community.config;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.controller.interceptor.LoginRequiredInterceptor;
import com.nowcoder.community.controller.interceptor.LoginTicketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    //登录口令拦截器
    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    //登录注册拦截器
    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;

    //拦截器的路径
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //不拦截静态资源
        registry.addInterceptor(loginTicketInterceptor)
        .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");

        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");
    }


}
