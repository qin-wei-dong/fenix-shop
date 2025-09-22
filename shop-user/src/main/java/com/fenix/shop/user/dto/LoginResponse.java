package com.fenix.shop.user.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户登录响应DTO
 * 功能描述：符合前端期望格式的登录响应数据传输对象
 * 采用技术：按照前端AuthResponse接口设计的响应格式
 * 技术优势：前后端数据格式统一，减少数据转换错误
 * 
 * @author fenix
 * @since 2025-01-27
 */
@Data
@Accessors(chain = true)
public class LoginResponse {

    /**
     * JWT访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 令牌类型
     */
    private String tokenType = "Bearer";

    /**
     * 令牌过期时间（秒）
     */
    private Long expiresIn;

    /**
     * 用户基本信息
     */
    private UserInfo user;

    /**
     * 用户信息内部类
     * 功能描述：符合前端UserInfo接口的用户信息格式
     */
    @Data
    @Accessors(chain = true)
    public static class UserInfo {
        /**
         * 用户ID（前端期望id字段）
         */
        private String id;

        /**
         * 用户名
         */
        private String username;

        /**
         * 邮箱
         */
        private String email;

        /**
         * 用户角色列表
         */
        private List<String> roles;

        /**
         * 头像URL
         */
        private String avatar;

        /**
         * 昵称
         */
        private String nickname;

        /**
         * 手机号（前端期望phone字段）
         */
        private String phone;

        /**
         * 账户状态
         */
        private String status;

        /**
         * 创建时间
         */
        private String createdAt;

        /**
         * 最后登录时间
         */
        private String lastLoginAt;
    }
}