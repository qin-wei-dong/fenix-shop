package com.fenix.shop.user.exception;

/**
 * 认证异常类
 * 用于处理用户认证相关的异常
 * 
 * @author AI Assistant
 * @since 2025-01-27
 */
public class AuthenticationException extends RuntimeException {

    private String errorCode;

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AuthenticationException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    // 常用认证异常静态方法
    public static AuthenticationException invalidToken() {
        return new AuthenticationException("INVALID_TOKEN", "无效的访问令牌");
    }

    public static AuthenticationException tokenExpired() {
        return new AuthenticationException("TOKEN_EXPIRED", "访问令牌已过期");
    }

    public static AuthenticationException tokenMissing() {
        return new AuthenticationException("TOKEN_MISSING", "缺少访问令牌");
    }

    public static AuthenticationException invalidCredentials() {
        return new AuthenticationException("INVALID_CREDENTIALS", "用户名或密码错误");
    }

    public static AuthenticationException userNotFound() {
        return new AuthenticationException("USER_NOT_FOUND", "用户不存在");
    }

    public static AuthenticationException accountLocked() {
        return new AuthenticationException("ACCOUNT_LOCKED", "账户已被锁定");
    }

    public static AuthenticationException accountDisabled() {
        return new AuthenticationException("ACCOUNT_DISABLED", "账户已被禁用");
    }

    public static AuthenticationException loginRequired() {
        return new AuthenticationException("LOGIN_REQUIRED", "请先登录");
    }
}