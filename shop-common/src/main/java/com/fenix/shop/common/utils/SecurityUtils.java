package com.fenix.shop.common.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

/**
 * 安全工具类
 * 
 * 功能描述：提供Spring Security认证上下文的便捷访问方法，主要用于获取当前登录用户信息
 * 技术选型：基于Spring Security的SecurityContextHolder实现，采用ThreadLocal机制确保线程安全
 * 优势：
 * 1. 线程安全：利用Spring Security的ThreadLocal机制，确保多线程环境下的数据隔离
 * 2. 简化调用：封装复杂的认证上下文操作，提供简洁的API接口
 * 3. 空值处理：完善的空值检查和异常处理，避免NPE问题
 * 4. 统一标准：为整个项目提供统一的用户身份获取方式
 * 
 * @author fenix
 * @date 2025-01-27
 * @version 1.0
 */
public class SecurityUtils {

    /**
     * 获取当前登录用户ID
     * 
     * 功能描述：从Spring Security认证上下文中提取当前登录用户的唯一标识
     * 技术实现：通过SecurityContextHolder获取Authentication对象，然后提取用户主体信息
     * 优势：
     * 1. 无侵入性：不需要在方法参数中传递用户信息
     * 2. 实时获取：直接从认证上下文获取，确保数据的实时性和准确性
     * 3. 异常安全：完善的空值检查，避免在未认证状态下调用时出现异常
     * 
     * @return 当前登录用户ID，如果未登录或认证信息无效则返回null
     */
    public static String getCurrentUserId() {
        // 从Spring Security上下文中获取认证对象
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // 检查认证对象是否存在且已认证
        if (authentication != null && authentication.isAuthenticated()) {
            // 获取用户主体信息（在JWT认证中，这里存储的是用户ID）
            Object principal = authentication.getPrincipal();
            
            // 检查主体信息是否为有效的字符串且不是匿名用户
            if (principal instanceof String && !"anonymousUser".equals(principal)) {
                String userId = (String) principal;
                // 验证用户ID是否为有效字符串
                if (StringUtils.hasText(userId)) {
                    return userId;
                }
            }
        }
        
        // 未登录或认证信息无效时返回null
        return null;
    }

    /**
     * 获取当前认证对象
     * 
     * 功能描述：获取完整的Spring Security认证对象，用于需要更多认证信息的场景
     * 技术实现：直接返回SecurityContextHolder中的Authentication对象
     * 优势：
     * 1. 完整信息：提供完整的认证对象，包含用户权限、认证状态等信息
     * 2. 灵活性：允许调用方根据需要提取不同的认证信息
     * 3. 原生支持：直接使用Spring Security原生对象，保持API的一致性
     * 
     * @return 当前认证对象，如果未认证则返回null
     */
    public static Authentication getCurrentAuthentication() {
        // 直接返回Spring Security上下文中的认证对象
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 检查当前用户是否已认证
     * 
     * 功能描述：判断当前请求是否来自已认证的用户
     * 技术实现：检查认证对象的存在性和认证状态
     * 优势：
     * 1. 快速判断：提供简单的布尔值返回，便于条件判断
     * 2. 准确性：综合考虑认证对象存在性和认证状态
     * 3. 易用性：避免重复的空值检查和状态判断逻辑
     * 
     * @return true-已认证，false-未认证
     */
    public static boolean isAuthenticated() {
        // 获取当前认证对象
        Authentication authentication = getCurrentAuthentication();
        
        // 检查认证对象是否存在、已认证且不是匿名用户
        return authentication != null 
            && authentication.isAuthenticated() 
            && !"anonymousUser".equals(authentication.getPrincipal());
    }

    /**
     * 获取当前用户名
     * 
     * 功能描述：获取当前登录用户的用户名信息
     * 技术实现：从认证对象中提取用户名，通常用于显示和日志记录
     * 优势：
     * 1. 便捷性：直接获取用户名，无需额外的数据库查询
     * 2. 安全性：只返回用户名信息，不暴露敏感的用户详情
     * 3. 通用性：适用于各种需要用户名的业务场景
     * 
     * @return 当前用户名，如果未认证则返回null
     */
    public static String getCurrentUsername() {
        // 获取当前认证对象
        Authentication authentication = getCurrentAuthentication();
        
        // 检查认证对象是否有效
        if (authentication != null && authentication.isAuthenticated()) {
            // 在当前项目中，用户名和用户ID可能相同，这里返回主体信息
            Object principal = authentication.getPrincipal();
            if (principal instanceof String && !"anonymousUser".equals(principal)) {
                return (String) principal;
            }
        }
        
        // 未认证时返回null
        return null;
    }
}