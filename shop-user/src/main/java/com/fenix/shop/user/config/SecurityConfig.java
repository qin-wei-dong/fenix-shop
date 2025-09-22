package com.fenix.shop.user.config;

import com.fenix.shop.user.security.JwtAuthenticationEntryPoint;
import com.fenix.shop.user.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security安全配置类
 * 功能描述：配置Spring Security的安全策略，包括JWT认证、CORS跨域、会话管理等
 * 采用技术：Spring Security + JWT + BCrypt密码加密
 * 技术优势：无状态认证提高系统扩展性，JWT令牌减少服务器存储压力，BCrypt加密保证密码安全性
 * 
 * @author fenix
 * @date 2025-01-27
 * @version 1.0
 */
@Configuration // Spring配置类注解，标识这是一个配置类，用于定义Bean和配置信息
@EnableWebSecurity // 启用Spring Security Web安全功能，激活安全过滤器链
@EnableGlobalMethodSecurity(prePostEnabled = true) // 启用方法级安全控制，支持@PreAuthorize和@PostAuthorize注解
public class SecurityConfig {
    
    /**
     * JWT认证入口点
     * 功能描述：处理未认证请求的异常处理器
     * 采用技术：自定义AuthenticationEntryPoint实现
     * 技术优势：统一处理认证失败响应，提供友好的错误信息
     */
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    /**
     * JWT认证过滤器
     * 功能描述：拦截请求并验证JWT令牌的有效性
     * 采用技术：自定义OncePerRequestFilter实现
     * 技术优势：确保每个请求只执行一次过滤，提高性能和安全性
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    // 构造函数，用于验证SecurityConfig是否被正确加载
    public SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint, 
                         JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * 密码编码器Bean配置
     * 功能描述：提供密码加密和验证功能
     * 采用技术：BCrypt哈希算法，强度设置为12
     * 技术优势：BCrypt是自适应哈希函数，具有盐值和可调强度，有效防止彩虹表攻击
     * 
     * @return PasswordEncoder 密码编码器实例
     */
    @Bean // Spring Bean注解，将方法返回值注册为Spring容器中的Bean
    public PasswordEncoder passwordEncoder() {
        // 创建BCrypt密码编码器，强度为12（2^12次迭代）
        return new BCryptPasswordEncoder(12);
    }

    /**
     * 安全过滤器链配置Bean
     * 功能描述：配置Spring Security的核心安全策略，包括认证、授权、CORS等
     * 采用技术：Spring Security 6.x的函数式配置方式
     * 技术优势：链式配置清晰易读，函数式编程提高代码可维护性
     * 
     * @param http HttpSecurity配置对象
     * @return SecurityFilterChain 安全过滤器链
     * @throws Exception 配置异常
     */
    @Bean("securityFilterChain") // 指定Bean名称，避免多个SecurityFilterChain时的冲突
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            // 禁用CSRF保护，因为使用JWT无状态认证，不需要CSRF防护
            .csrf(AbstractHttpConfigurer::disable)
            
            // 配置CORS跨域资源共享，允许前端跨域访问
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 配置会话管理策略为无状态，不创建HttpSession
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 配置认证异常处理，使用自定义的JWT认证入口点
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            
            // 配置HTTP请求的授权规则
            .authorizeHttpRequests(authz -> {
                authz
                    // 配置公开接口，无需认证即可访问
                    // 注意：由于设置了context-path为/api/user，这里的路径是相对于context-path的
                    .requestMatchers(
                        "/register",      // 用户注册接口
                        "/login",         // 用户登录接口
                        "/check-username", // 用户名检查接口
                        "/check-email",    // 邮箱检查接口
                        "/check-mobile",   // 手机号检查接口
                        "/refresh",        // 令牌刷新接口
                        "/actuator/**",            // Spring Boot监控端点
                        "/swagger-ui/**",          // Swagger UI资源
                        "/v3/api-docs/**",         // OpenAPI 3.0文档
                        "/swagger-resources/**",   // Swagger资源文件
                        "/webjars/**"              // WebJars静态资源
                    ).permitAll() // 允许所有用户访问上述路径
                    
                    // 其他所有请求都需要通过认证才能访问
                    .anyRequest().authenticated();
            }
            )
            
            // 在用户名密码认证过滤器之前添加JWT认证过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            // 构建并返回安全过滤器链
            .build();
    }

    /**
     * CORS跨域配置源Bean
     * 功能描述：配置跨域资源共享策略，允许前端应用跨域访问后端API
     * 采用技术：Spring Web CORS配置，支持预检请求和凭证传递
     * 技术优势：灵活的跨域控制，支持复杂请求和认证信息传递，提高前后端分离架构的兼容性
     * 
     * @return CorsConfigurationSource CORS配置源
     */
    @Bean // 注册为Spring Bean，供Security配置使用
    public CorsConfigurationSource corsConfigurationSource() {
        // 创建CORS配置对象
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 设置允许的请求源，具体指定前端地址
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:3000",
            "http://127.0.0.1:3000",
            "http://localhost:8080",
            "http://127.0.0.1:8080",
            "http://localhost:5173",
            "http://127.0.0.1:5173"
        ));
        
        // 设置允许的HTTP请求方法，覆盖常用的RESTful操作
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // 设置允许的请求头，使用通配符允许所有请求头
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // 允许携带认证信息（如Cookie、Authorization头），支持JWT认证
        configuration.setAllowCredentials(true);
        
        // 设置预检请求的缓存时间为1小时，减少OPTIONS请求频率
        configuration.setMaxAge(3600L);
        
        // 设置暴露给前端的响应头，包含认证和内容类型信息
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // 创建基于URL的CORS配置源
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // 为所有路径注册CORS配置
        source.registerCorsConfiguration("/**", configuration);
        
        // 返回配置源供Spring Security使用
        return source;
    }
}