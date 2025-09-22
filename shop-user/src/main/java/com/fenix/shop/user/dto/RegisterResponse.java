package com.fenix.shop.user.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 用户注册响应DTO
 * 
 * @author AI Assistant
 * @since 2025-01-27
 */
@Data
@Accessors(chain = true)
public class RegisterResponse {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号（脱敏）
     */
    private String mobile;

    /**
     * 注册时间
     */
    private LocalDateTime registerTime;

    /**
     * 是否需要邮箱验证
     */
    private Boolean needEmailVerification;

    /**
     * 是否需要手机验证
     */
    private Boolean needMobileVerification;

    /**
     * 注册成功提示信息
     */
    private String message;
}