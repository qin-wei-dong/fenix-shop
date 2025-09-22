package com.fenix.shop.user.dto;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 用户注册请求DTO
 * 
 * @author AI Assistant
 * @since 2025-01-27
 */
@Data
public class RegisterRequest {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度必须在6-50个字符之间")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{6,}$", 
             message = "密码必须包含至少一个大写字母、一个小写字母和一个数字")
    private String password;

    /**
     * 确认密码
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String mobile;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    /**
     * 昵称
     * 功能描述：用户显示名称，用于个人资料展示
     * 采用技术：Bean Validation注解进行参数校验
     * 技术优势：统一的校验规则，减少重复代码，提高数据质量
     */
    @NotBlank(message = "昵称不能为空")
    @Size(min = 2, max = 50, message = "昵称长度必须在2-50个字符之间")
    private String nickname;

    /**
     * 推荐码（可选）
     */
    @Size(max = 20, message = "推荐码长度不能超过20个字符")
    private String referralCode;

    /**
     * 是否同意用户协议
     */
    private Boolean agreeTerms = false;

    /**
     * 验证密码是否一致
     * @return true-一致，false-不一致
     */
    public boolean isPasswordMatch() {
        return password != null && password.equals(confirmPassword);
    }
}