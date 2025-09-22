package com.fenix.shop.common.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 用户认证信息DTO
 * 功能描述：用于在各业务模块中承载“当前登录用户”的关键信息（ID、用户名、角色、是否已认证）。
 * 采用了什么技术以及为什么采用：
 * - 纯Java可序列化对象：避免对具体安全实现的强耦合，便于在微服务间、缓存中传递。
 * - 只包含必要字段：降低耦合与泄露风险，符合最小必要原则。
 * 有什么优势：
 * - 解耦：不依赖具体的Spring Security类型，通用性更强。
 * - 易扩展：后续可平滑增加非敏感展示字段（如昵称）。
 * - 可序列化：便于跨层传递和缓存。
 *
 * @author fenix
 * @date 2025-09-20
 * @version 1.0
 */
@Data
public class CurrentUser implements Serializable {

    // 序列化版本号：功能描述：保证序列化兼容；技术：JDK序列化；优势：版本演进安全
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     * 功能描述：当前登录用户的唯一标识，来源于JWT或认证上下文
     * 技术：字符串存储，避免长整型精度问题
     * 优势：通用、安全、跨系统无损传递
     */
    private String userId;

    /**
     * 用户名
     * 功能描述：展示所需的用户名；如认证上下文仅有ID，可与userId相同
     * 技术：与认证上下文解耦的简单字符串
     * 优势：满足展示和审计日志需求
     */
    private String username;

    /**
     * 角色列表
     * 功能描述：当前用户具备的角色编码集合（如ROLE_USER、ROLE_ADMIN）
     * 技术：使用不可变/只读视图暴露
     * 优势：避免外部修改集合
     * -- SETTER --
     *  设置角色列表
     *
     * @param roles 角色编码列表

     */
    private List<String> roles;

    /**
     * 是否已认证
     * 功能描述：标记当前上下文是否处于认证状态
     * 技术：布尔标识
     * 优势：快捷判断，无需重复读取安全上下文
     */
    private boolean authenticated;

    /**
     * 获取角色列表（以只读视图形式返回）
     * @return 角色编码列表
     */
    public List<String> getRoles() {
        // 技术：返回不可变视图；优势：防御式编程
        return roles == null ? Collections.emptyList() : Collections.unmodifiableList(roles);
    }
}


