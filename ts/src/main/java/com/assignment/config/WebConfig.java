package com.assignment.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(@SuppressWarnings("null") InterceptorRegistry registry) {
        // Interceptor cho các request /order/**
        //registry.addInterceptor(new AuthInterceptor())
        //        .addPathPatterns("/order/**");

        // Interceptor mới cho các request /admin/**
        //registry.addInterceptor(new AdminAuthInterceptor())
        //        .addPathPatterns("/admin/**");
    }
}
