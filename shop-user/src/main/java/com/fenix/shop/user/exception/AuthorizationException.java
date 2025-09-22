package com.fenix.shop.user.exception;

/**
 * 授权异常类
 * 用于处理用户权限相关的异常
 * 
 * @author AI Assistant
 * @since 2025-01-27
 */
public class AuthorizationException extends RuntimeException {

    private String errorCode;

    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthorizationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AuthorizationException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    // 常用授权异常静态方法
    public static AuthorizationException accessDenied() {
        return new AuthorizationException("ACCESS_DENIED", "访问被拒绝");
    }

    public static AuthorizationException insufficientPermissions() {
        return new AuthorizationException("INSUFFICIENT_PERMISSIONS", "权限不足");
    }

    public static AuthorizationException resourceNotFound() {
        return new AuthorizationException("RESOURCE_NOT_FOUND", "资源不存在");
    }

    public static AuthorizationException operationNotAllowed() {
        return new AuthorizationException("OPERATION_NOT_ALLOWED", "操作不被允许");
    }

    public static AuthorizationException roleRequired(String role) {
        return new AuthorizationException("ROLE_REQUIRED", "需要" + role + "角色权限");
    }

    public static AuthorizationException ownershipRequired() {
        return new AuthorizationException("OWNERSHIP_REQUIRED", "只能操作自己的资源");
    }
}