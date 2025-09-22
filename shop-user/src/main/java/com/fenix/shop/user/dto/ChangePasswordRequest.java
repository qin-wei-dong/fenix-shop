package com.fenix.shop.user.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 修改密码请求DTO
 *
 * @author AI Assistant
 * @since 2025-01-27
 */
@Data
public class ChangePasswordRequest {

    /**
     * 当前密码
     */
    @NotBlank(message = "当前密码不能为空")
    private String currentPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 50, message = "新密码长度必须在6-50个字符之间")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@$!%*?&]{6,}$",
            message = "新密码必须包含至少一个大写字母、一个小写字母和一个数字")
    private String newPassword;

    /**
     * 确认新密码
     */
    @NotBlank(message = "确认新密码不能为空")
    private String confirmNewPassword;

    /**
     * 验证新密码是否一致
     *
     * @return true-一致，false-不一致
     */
    public boolean isNewPasswordMatch() {
        return newPassword != null && newPassword.equals(confirmNewPassword);
    }
}