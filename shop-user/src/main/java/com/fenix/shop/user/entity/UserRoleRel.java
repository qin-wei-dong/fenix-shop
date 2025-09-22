package com.fenix.shop.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户角色关联实体类
 * 功能描述：映射数据库t_user_role_rel表，管理用户与角色的多对多关联关系
 * 采用技术：JPA + MyBatis Plus + Lombok
 * 技术优势：
 * 1. JPA注解提供标准化的ORM映射，增强代码可移植性
 * 2. MyBatis Plus注解提供强大的CRUD功能和分页支持
 * 3. Lombok自动生成getter/setter等方法，减少样板代码
 * 4. 支持乐观锁和逻辑删除，保证数据安全性
 * 5. 用户角色关联管理，支持灵活的权限分配
 * 
 * @author fenix
 * @date 2025-01-27
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_user_role_rel")
public class UserRoleRel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联ID
     * 功能描述：用户角色关联唯一标识，使用自增ID
     * 数据库约束：主键，非空，唯一
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     * 功能描述：关联的用户ID，外键关联到用户表
     * 数据库约束：非空，外键约束
     */
    @NotNull(message = "用户ID不能为空")
    @TableField("user_id")
    private Long userId;

    /**
     * 角色ID
     * 功能描述：关联的角色ID，外键关联到角色表
     * 数据库约束：非空，外键约束
     */
    @NotNull(message = "角色ID不能为空")
    @TableField("role_id")
    private Long roleId;

    /**
     * 创建时间
     * 功能描述：记录创建时间，自动填充
     * 数据库约束：非空，默认当前时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdTime;
}