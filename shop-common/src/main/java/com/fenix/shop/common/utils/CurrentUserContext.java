package com.fenix.shop.common.utils;

import com.fenix.shop.common.model.dto.CurrentUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Objects;

/**
 * 当前用户上下文工具类
 * 功能描述：
 * 统一在common层提供“获取当前登录用户信息”的便捷入口，
 * 基于Spring Security的SecurityContextHolder从线程上下文中解析用户ID、角色等信息，
 * 并以通用DTO形式返回，供各业务模块使用。
 * 采用了什么技术以及为什么采用：
 * - SecurityContextHolder：Spring Security标准方式获取认证对象，线程安全且性能可靠。
 * - 只读视图与防御式编程：避免外部改写内部集合，提高健壮性。
 * - 与具体业务解耦：仅依赖安全上下文信息，便于跨模块通用复用。
 * 有什么优势：
 * - 统一入口：避免各处重复解析认证信息。
 * - 降低耦合：不直接依赖具体服务或实体模型。
 * - 安全稳健：空值与匿名用户处理完善，避免NPE与越权问题。
 * <p>
 * 使用约定：
 * - 本项目JWT过滤器将 userId 放入 Authentication.principal（字符串），credentials 为空，authorities 含角色。
 * - 如果后续主体结构调整，仅需在本工具类集中修改解析逻辑。
 *
 * @author fenix
 * @version 1.0
 * @date 2025-09-20
 */
public final class CurrentUserContext {

    // 私有构造防止实例化：工具类只提供静态方法
    private CurrentUserContext() {
    }

    /**
     * 获取当前已认证用户的基础信息
     * 功能描述：从SecurityContext中解析用户ID、角色与认证态，封装为AuthenticatedUser。
     * 采用技术：SecurityContextHolder + Java Stream 收集角色编码
     * 优势：一次调用即可获得常用信息
     *
     * @return CurrentUser 当前用户信息；若未认证返回authenticated=false的空对象
     */
    public static CurrentUser getCurrentUser() {
        // 获取认证对象；技术：线程上下文读取；优势：无侵入
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CurrentUser user = new CurrentUser();
        if (authentication == null || !authentication.isAuthenticated()) {
            // 标记未认证
            user.setAuthenticated(false);
            // 返回空态对象，避免NPE
            return user;
        }

        // principal 在本项目中为 userId（字符串），匿名用户为"anonymousUser"
        Object principal = authentication.getPrincipal();
        if (principal instanceof String p && !Objects.equals("anonymousUser", p)) {
            user.setUserId(p);
            // 当前过滤器未放入用户名，这里同ID；后续可调整
            user.setUsername(p);
            // 标记已认证
            user.setAuthenticated(true);
        } else {
            // 无有效主体，视为未认证
            user.setAuthenticated(false);
        }

        // 解析角色集合（GrantedAuthority#getAuthority）
        List<String> roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        // 设置角色列表
        user.setRoles(roles);
        // 返回构造完成的用户信息
        return user;
    }

    /**
     * 获取当前用户ID
     * 功能描述：便捷方法，仅返回用户ID
     * 采用技术：复用 getCurrentUser
     * 优势：减少重复解析逻辑
     *
     * @return 用户ID；未认证返回null
     */
    public static String getCurrentUserId() {
        CurrentUser user = getCurrentUser();
        return user.isAuthenticated() ? user.getUserId() : null;
    }

    /**
     * 判断当前上下文是否已认证
     * 功能描述：快速判断认证态
     * 采用技术：复用 getCurrentUser
     * 优势：调用简洁
     *
     * @return true 已认证；false 未认证
     */
    public static boolean isAuthenticated() {
        return getCurrentUser().isAuthenticated();
    }
}


