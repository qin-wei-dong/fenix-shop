package com.fenix.shop.user.converter;

import com.fenix.shop.user.dto.UserProfileDTO;
import com.fenix.shop.user.entity.User;

import java.util.Collections;
import java.util.List;
import com.fenix.shop.user.dto.LoginResponse;

/**
 * 用户资料转换器
 * 功能描述：在实体 `User` 与 DTO `UserProfileDTO` 之间进行字段映射与数据转换，统一头像字段命名（实体 avatar ↔ DTO avatarUrl），避免控制层重复拼装
 * 采用了什么技术以及为什么采用：采用显式转换器（Assembler/Converter）模式集中管理映射逻辑，避免在 Controller 中分散处理字段转换，降低重复与出错概率
 * 有什么优势：
 * - 统一口径：集中管理 `avatar` 和 `avatarUrl` 的互转，避免命名不一致导致的错误
 * - 易于维护：新增字段或规则时只需改此处，减少散点修改
 * - 可测试性：转换逻辑可单元测试，提升可靠性
 *
 * @author fenix
 * @date 2025-09-20
 * @version 1.0
 */
public final class UserProfileConverter {

    /**
     * 工具类不允许实例化
     */
    private UserProfileConverter() { /* no-op */ }

    /**
     * 将实体 User 转换为 UserProfileDTO
     * 功能描述：拷贝安全可暴露的基础资料，并将实体中的 avatar 字段映射为 DTO 的 avatarUrl 字段
     * 采用技术：显式字段赋值，避免反射引入的性能与安全问题
     * 优势：转换过程清晰可控，便于审查与维护
     *
     * @param user 用户实体（来源于数据库）
     * @param roleCodes 角色编码列表（允许为 null，将按空列表处理）
     * @return UserProfileDTO 用于对外返回的用户资料 DTO
     */
    public static UserProfileDTO toDTO(User user, List<String> roleCodes) {
        if (user == null) {
            return null;
        }

        List<String> roles = roleCodes == null ? Collections.emptyList() : roleCodes;

        UserProfileDTO dto = new UserProfileDTO();
        dto.setUserId(user.getUserId() == null ? null : user.getUserId().toString());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setMobile(user.getMobile());
        dto.setAvatarUrl(user.getAvatar());
        dto.setRoles(roles);
        return dto;
    }

    /**
     * 将 UserProfileDTO 的可修改字段应用到实体 User（部分更新）
     * 功能描述：仅更新允许外部修改的字段（邮箱、手机号、头像），并将 DTO 的 avatarUrl 写回实体的 avatar 字段
     * 采用技术：空值跳过策略，避免前端未传值时意外覆盖
     * 优势：确保更新的幂等性与安全性，减少误覆盖
     *
     * @param targetUser 目标实体（来自数据库的持久化对象）
     * @param sourceDto 传入的资料 DTO（来自接口请求体）
     */
    public static void applyProfileUpdate(User targetUser, UserProfileDTO sourceDto) {
        if (targetUser == null || sourceDto == null) {
            return;
        }

        if (sourceDto.getEmail() != null) {
            targetUser.setEmail(sourceDto.getEmail());
        }
        if (sourceDto.getMobile() != null) {
            targetUser.setMobile(sourceDto.getMobile());
        }
        if (sourceDto.getAvatarUrl() != null) {
            targetUser.setAvatar(sourceDto.getAvatarUrl());
        }
    }

    /**
     * 将 User 转换为 LoginResponse.UserInfo
     * 功能描述：用于登录与刷新令牌等场景，统一头像字段映射与基础信息填充
     * 采用技术：显式映射，避免丢字段或命名不一致
     * 优势：与前端 UserInfo 契约一致，减少重复代码
     *
     * @param user 用户实体
     * @param roleCodes 角色编码列表（允许为 null）
     * @return LoginResponse.UserInfo 用户信息
     */
    public static LoginResponse.UserInfo toLoginUserInfo(User user, List<String> roleCodes) {
        if (user == null) {
            return null;
        }
        List<String> roles = roleCodes == null ? Collections.emptyList() : roleCodes;
        LoginResponse.UserInfo ui = new LoginResponse.UserInfo();
        ui.setId(user.getUserId() == null ? null : user.getUserId().toString());
        ui.setUsername(user.getUsername());
        ui.setEmail(user.getEmail());
        ui.setPhone(user.getMobile());
        ui.setAvatar(user.getAvatar());
        ui.setNickname(user.getNickname());
        ui.setRoles(roles);
        ui.setStatus(user.getStatus() != null && user.getStatus() == 1 ? "ACTIVE" : "INACTIVE");
        ui.setCreatedAt(user.getCreatedAt() == null ? null : String.valueOf(user.getCreatedAt()));
        ui.setLastLoginAt(user.getLastLoginTime() == null ? null : String.valueOf(user.getLastLoginTime()));
        return ui;
    }
}


