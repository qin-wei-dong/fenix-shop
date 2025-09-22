package com.fenix.shop.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户个人信息DTO
 * 
 * @author AI Assistant
 * @since 2025-01-27
 */
@Data
@Accessors(chain = true)
public class UserProfileDTO {

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
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;

    /**
     * 头像URL
     */
    @Size(max = 255, message = "头像URL长度不能超过255个字符")
    private String avatarUrl;

    /**
     * 用户等级
     */
    private Integer userLevel;

    /**
     * 积分
     */
    private Integer points;

    /**
     * 注册时间
     */
    private LocalDateTime registerTime;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 是否激活
     */
    private Boolean isActive;

    /**
     * 通知设置
     */
    private String notificationSettings;

    /**
     * 用户偏好设置
     */
    private String preferences;

    /**
     * 用户角色列表
     * 功能描述：用户拥有的角色编码列表，用于前端权限控制
     * 采用技术：角色编码字符串列表，便于前端权限判断
     * 技术优势：支持多角色用户，灵活的权限控制
     */
    private List<String> roles;
}