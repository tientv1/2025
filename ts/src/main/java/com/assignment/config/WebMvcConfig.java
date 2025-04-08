package com.assignment.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @SuppressWarnings("null")
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Cấu hình cho images
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:uploads/");

        // Cấu hình cho các static resources khác
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}