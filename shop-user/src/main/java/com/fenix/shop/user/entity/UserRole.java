package com.fenix.shop.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户角色实体类
 * 功能描述：映射数据库t_user_role表，管理系统角色信息和权限配置
 * 采用技术：JPA + MyBatis Plus + Lombok
 * 技术优势：
 * 1. JPA注解提供标准化的ORM映射，增强代码可移植性
 * 2. MyBatis Plus注解提供强大的CRUD功能和分页支持
 * 3. Lombok自动生成getter/setter等方法，减少样板代码
 * 4. 支持乐观锁和逻辑删除，保证数据安全性
 * 5. 角色权限分离设计，支持灵活的权限管理
 * 
 * @author fenix
 * @date 2025-01-27
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_user_role")
public class UserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色ID
     * 功能描述：角色唯一标识，使用自增ID
     * 数据库约束：主键，非空，唯一
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long roleId;

    /**
     * 角色编码
     * 功能描述：角色唯一编码，用于程序中的权限判断
     * 数据库约束：非空，唯一，长度最大50字符
     * 业务规则：建议使用ROLE_前缀，如ROLE_ADMIN、ROLE_USER
     */
    @NotBlank(message = "角色编码不能为空")
    @Size(max = 50, message = "角色编码长度不能超过50字符")
    @TableField("role_code")
    private String roleCode;

    /**
     * 角色名称
     * 功能描述：角色显示名称，用于界面展示
     * 数据库约束：非空，长度最大50字符
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称长度不能超过50字符")
    @TableField("role_name")
    private String roleName;

    /**
     * 角色描述
     * 功能描述：角色功能和权限的详细描述
     * 数据库约束：可空
     */
    @TableField("description")
    private String description;

    /**
     * 是否激活
     * 功能描述：角色状态，true-激活，false-禁用
     * 数据库约束：非空，默认值true
     */
    @NotNull(message = "角色状态不能为空")
    @TableField("is_active")
    private Boolean isActive = true;

    /**
     * 创建时间
     * 功能描述：记录创建时间，自动填充
     * 数据库约束：非空，默认当前时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     * 功能描述：记录最后更新时间，自动填充
     * 数据库约束：非空，默认当前时间，自动更新
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}