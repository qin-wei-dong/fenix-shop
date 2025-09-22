package com.fenix.shop.user.exception;

/**
 * 业务异常类
 * 用于处理业务逻辑中的异常情况
 * 
 * @author AI Assistant
 * @since 2025-01-27
 */
public class BusinessException extends RuntimeException {

    private String errorCode;
    private Object data;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(String errorCode, String message, Object data) {
        super(message);
        this.errorCode = errorCode;
        this.data = data;
    }

    public BusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Object getData() {
        return data;
    }

    // 常用业务异常静态方法
    public static BusinessException userNotFound() {
        return new BusinessException("USER_NOT_FOUND", "用户不存在");
    }

    public static BusinessException userAlreadyExists() {
        return new BusinessException("USER_ALREADY_EXISTS", "用户已存在");
    }

    public static BusinessException invalidCredentials() {
        return new BusinessException("INVALID_CREDENTIALS", "用户名或密码错误");
    }

    public static BusinessException accountLocked() {
        return new BusinessException("ACCOUNT_LOCKED", "账户已被锁定");
    }

    public static BusinessException accountDisabled() {
        return new BusinessException("ACCOUNT_DISABLED", "账户已被禁用");
    }

    public static BusinessException passwordTooWeak() {
        return new BusinessException("PASSWORD_TOO_WEAK", "密码强度不足");
    }

    public static BusinessException emailAlreadyExists() {
        return new BusinessException("EMAIL_ALREADY_EXISTS", "邮箱已被使用");
    }

    public static BusinessException mobileAlreadyExists() {
        return new BusinessException("MOBILE_ALREADY_EXISTS", "手机号已被使用");
    }

    public static BusinessException usernameAlreadyExists() {
        return new BusinessException("USERNAME_ALREADY_EXISTS", "用户名已被使用");
    }

    public static BusinessException invalidToken() {
        return new BusinessException("INVALID_TOKEN", "无效的令牌");
    }

    public static BusinessException tokenExpired() {
        return new BusinessException("TOKEN_EXPIRED", "令牌已过期");
    }

    public static BusinessException currentPasswordIncorrect() {
        return new BusinessException("CURRENT_PASSWORD_INCORRECT", "当前密码不正确");
    }

    public static BusinessException newPasswordSameAsCurrent() {
        return new BusinessException("NEW_PASSWORD_SAME_AS_CURRENT", "新密码不能与当前密码相同");
    }
}