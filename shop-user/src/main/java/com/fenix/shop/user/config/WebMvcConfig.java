package com.fenix.shop.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Web MVC配置类
 * 功能描述：配置静态资源访问路径和CORS跨域访问，支持文件上传后的访问
 * 采用技术：Spring Boot Web MVC配置
 * 技术优势：简单的静态资源配置，支持文件服务和跨域访问
 * 
 * @author fenix
 * @date 2025-01-27
 * @version 1.0
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 上传基础目录
     * 功能描述：从配置项读取上传根目录，用于静态资源映射
     * 采用技术：Spring @Value 注入配置
     * 优势：环境可配置、与工作目录解耦
     */
    @Value("${app.upload.base-dir:${user.dir}/shop-user/uploads}")
    private String uploadBaseDir;

    /**
     * 配置静态资源处理器
     * 功能描述：配置上传文件的访问路径映射
     * 采用技术：Spring MVC资源处理器
     * 技术优势：提供文件访问能力，支持头像等静态资源
     * 
     * @param registry 资源处理器注册表
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保上传目录存在（若不存在则创建）
        try {
            Path basePath = Paths.get(uploadBaseDir);
            if (!Files.exists(basePath)) {
                Files.createDirectories(basePath);
            }
        } catch (Exception ignored) { }

        // 配置上传文件访问路径，映射到可配置物理目录
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(
                        "classpath:/uploads/",
                        "file:" + (uploadBaseDir.endsWith("/") ? uploadBaseDir : uploadBaseDir + "/")
                )
                .setCachePeriod(3600); // 缓存1小时
    }

    /**
     * 配置CORS跨域访问
     * 功能描述：为MVC层添加额外的CORS配置，确保文件上传等功能的跨域支持
     * 采用技术：Spring MVC CORS配置
     * 技术优势：双重CORS保障，确保所有请求都能正确处理跨域
     * 
     * @param registry CORS注册表
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:3000", "http://127.0.0.1:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}