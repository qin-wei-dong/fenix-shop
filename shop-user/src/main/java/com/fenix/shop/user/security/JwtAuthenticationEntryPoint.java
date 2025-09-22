package com.fenix.shop.user.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT认证入口点
 * 
 * 功能描述：
 * 该类实现了Spring Security的AuthenticationEntryPoint接口，专门处理未认证用户访问受保护资源时的情况。
 * 当用户在没有有效JWT令牌的情况下尝试访问需要认证的API端点时，该类会被触发，
 * 返回统一格式的401未授权错误响应，而不是Spring Security默认的登录页面重定向。
 * 
 * 采用了什么技术以及为什么采用：
 * 1. Spring Security AuthenticationEntryPoint接口 - 提供了标准的认证失败处理机制
 * 2. Jackson ObjectMapper - 用于将错误响应对象序列化为JSON格式，确保API响应的一致性
 * 3. Jakarta Servlet API - 符合Jakarta EE规范，用于处理HTTP请求和响应
 * 4. SLF4J日志框架 - 提供结构化的日志记录，便于问题排查和安全审计
 * 5. Lombok @Slf4j注解 - 简化日志记录器的创建，减少样板代码
 * 
 * 有什么优势：
 * 1. 统一错误处理：为所有未认证访问提供一致的JSON格式错误响应，符合RESTful API设计规范
 * 2. 安全性增强：避免暴露系统内部信息，同时记录安全相关的访问尝试
 * 3. 前后端分离友好：返回JSON而非HTML页面，适合SPA应用和移动端集成
 * 4. 可扩展性：错误响应格式标准化，便于前端统一处理和国际化
 * 5. 调试友好：包含详细的错误信息和时间戳，便于开发和运维人员排查问题
 * 6. 符合HTTP标准：正确设置HTTP状态码和Content-Type，确保客户端能正确解析响应
 * 
 * @author AI Assistant
 * @since 2025-01-27
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, 
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException, ServletException {
        
        log.warn("未认证用户尝试访问受保护资源: {} {}", request.getMethod(), request.getRequestURI());
        
        // 设置响应状态码和内容类型
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        
        // 构建错误响应
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message", "访问被拒绝：请先登录");
        errorResponse.put("path", request.getRequestURI());
        
        // 添加详细错误信息（开发环境）
        if (authException != null) {
            errorResponse.put("details", authException.getMessage());
        }
        
        // 写入响应
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.getWriter().flush();
    }
}