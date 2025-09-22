package com.fenix.shop.user.security;

import com.fenix.shop.user.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * JWT认证过滤器
 * 
 * 功能描述：
 * 从HTTP请求中提取JWT令牌，验证令牌有效性，并将认证信息设置到Spring Security上下文中。
 * 该过滤器在每个请求处理前执行，确保只有携带有效JWT令牌的请求才能访问受保护的资源。
 * 
 * 采用的技术及原因：
 * 1. OncePerRequestFilter - Spring框架提供的过滤器基类，确保每个请求只执行一次过滤逻辑
 * 2. SecurityContextHolder - Spring Security的核心组件，用于存储当前线程的安全上下文
 * 3. UsernamePasswordAuthenticationToken - Spring Security标准认证令牌，用于表示已认证的用户
 * 4. JwtUtil - 自定义JWT工具类，封装JWT的解析、验证等操作
 * 5. Lombok注解 - 简化代码，自动生成构造函数和日志对象
 * 
 * 优势：
 * 1. 无状态认证 - JWT令牌包含所有必要信息，服务器无需存储会话状态
 * 2. 跨域友好 - 适合前后端分离架构和微服务架构
 * 3. 性能优良 - 避免频繁的数据库查询来验证用户身份
 * 4. 安全可靠 - 通过数字签名确保令牌完整性和真实性
 * 5. 灵活配置 - 可通过shouldNotFilter方法灵活配置哪些路径需要跳过认证
 * 6. 异常处理 - 完善的异常处理机制，确保认证失败时不影响系统稳定性
 * 
 * @author AI Assistant
 * @since 2025-01-27
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    /**
     * 执行JWT认证过滤逻辑
     * 
     * 功能描述：
     * 从HTTP请求中提取JWT令牌，验证令牌有效性，解析用户信息，
     * 创建Spring Security认证对象并设置到安全上下文中。
     * 
     * 采用的技术及原因：
     * 1. JWT令牌提取 - 从请求头或参数中获取Bearer令牌
     * 2. 令牌验证 - 验证令牌签名、过期时间等
     * 3. 用户信息解析 - 从令牌payload中提取用户ID和用户名
     * 4. 权限设置 - 为用户分配默认的ROLE_USER权限
     * 5. 安全上下文设置 - 将认证信息存储到当前线程的安全上下文
     * 
     * 优势：
     * 1. 自动认证 - 无需手动调用认证逻辑
     * 2. 线程安全 - 每个请求线程独立的安全上下文
     * 3. 异常隔离 - 认证失败不影响其他请求处理
     * 
     * @param request HTTP请求对象，包含客户端发送的所有信息
     * @param response HTTP响应对象，用于向客户端发送响应
     * @param filterChain 过滤器链，用于继续执行后续过滤器
     * @throws ServletException 当过滤器处理过程中发生Servlet相关异常
     * @throws IOException 当过滤器处理过程中发生IO异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // 从请求中提取JWT令牌
            String jwt = jwtUtil.getTokenFromRequest(request);
            
            if (StringUtils.hasText(jwt)) {
                boolean isValid = jwtUtil.validateToken(jwt);
                
                if (isValid) {
                    // 从令牌中获取用户信息
                    String userId = jwtUtil.getUserIdFromToken(jwt);
                    String username = jwtUtil.getUsernameFromToken(jwt);
                    
                    // 检查是否需要设置认证信息（当前为null或匿名用户时）
                    Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
                    boolean needsAuthentication = currentAuth == null || 
                        "anonymousUser".equals(currentAuth.getPrincipal());
                    
                    if (StringUtils.hasText(userId) && needsAuthentication) {
                        // 创建认证对象
                        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                            new SimpleGrantedAuthority("ROLE_USER")
                        );
                        
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(userId, null, authorities);
                        
                        // 设置认证详情
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        // 将认证信息存储到安全上下文中
                         SecurityContextHolder.getContext().setAuthentication(authentication);
                         
                         log.debug("JWT Filter - User {} authenticated successfully", username);
                    }
                }
            }
        } catch (Exception e) {
            log.error("JWT认证过程中发生错误: {}", e.getMessage());
            // 清除安全上下文
            SecurityContextHolder.clearContext();
        }
        
        // 继续过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 判断是否应该跳过此过滤器
     * 
     * 功能描述：
     * 根据请求路径判断是否需要跳过JWT认证过滤器。
     * 对于公开接口（如注册、登录）和系统接口（如健康检查、API文档）不需要进行JWT认证。
     * 
     * 采用的技术及原因：
     * 1. 路径匹配 - 使用字符串前缀匹配来识别公开接口
     * 2. 白名单机制 - 预定义不需要认证的路径列表
     * 3. OncePerRequestFilter内置方法 - Spring框架提供的标准过滤器跳过机制
     * 
     * 优势：
     * 1. 性能优化 - 避免对公开接口进行不必要的JWT解析和验证
     * 2. 用户体验 - 用户可以正常访问注册和登录接口
     * 3. 系统监控 - 健康检查和监控接口可以正常工作
     * 4. 开发便利 - API文档和调试接口可以直接访问
     * 5. 灵活配置 - 可以轻松添加或移除需要跳过的路径
     * 
     * @param request HTTP请求对象，包含请求路径等信息
     * @return boolean 返回true表示跳过此过滤器，false表示执行过滤器逻辑
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        
        log.info("JWT Filter - Request path: {}", path);
        
        // 跳过公开接口
        // 注意：由于设置了context-path为/api/user，实际请求路径会包含context-path
        boolean shouldSkip = path.startsWith("/api/user/register") ||
               path.startsWith("/api/user/login") ||
               path.startsWith("/api/user/check-") ||
               path.startsWith("/api/user/refresh") ||
               path.startsWith("/actuator/") ||
               path.startsWith("/swagger-ui/") ||
               path.startsWith("/v3/api-docs/") ||
               path.startsWith("/swagger-resources/") ||
               path.startsWith("/webjars/");
        
        log.info("JWT Filter - Should skip filter: {}", shouldSkip);
        return shouldSkip;
    }
}