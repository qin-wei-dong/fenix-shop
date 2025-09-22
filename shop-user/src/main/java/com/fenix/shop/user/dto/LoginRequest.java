package com.fenix.shop.user.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 用户登录请求DTO
 * 
 * @author AI Assistant
 * @since 2025-01-27
 */
@Data
public class LoginRequest {

    /**
     * 用户名（用户名/邮箱/手机号）
     */
    @NotBlank(message = "用户名不能为空")
    @Size(max = 100, message = "用户名长度不能超过100个字符")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度必须在6-50个字符之间")
    private String password;

    /**
     * 是否记住登录状态
     */
    private Boolean rememberMe = false;
}