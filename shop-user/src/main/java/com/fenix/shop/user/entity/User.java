package com.fenix.shop.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 功能描述：映射数据库t_user表，管理用户基础信息和认证信息
 * 采用技术：JPA + MyBatis Plus + Lombok
 * 技术优势：
 * 1. JPA注解提供标准化的ORM映射，增强代码可移植性
 * 2. MyBatis Plus注解提供强大的CRUD功能和分页支持
 * 3. Lombok自动生成getter/setter等方法，减少样板代码
 * 4. 支持乐观锁和逻辑删除，保证数据安全性
 * 
 * @author fenix
 * @date 2024-01-13
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     * 功能描述：用户唯一标识，建议使用雪花算法生成
     * 数据库约束：主键，非空，唯一
     */
    @TableId(value = "user_id", type = IdType.ASSIGN_ID)
    private Long userId;

    /**
     * 用户名
     * 功能描述：用户登录凭证，系统内唯一
     * 数据库约束：非空，唯一，长度3-50字符
     * 业务规则：只能包含字母、数字、下划线
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字、下划线")
    @TableField("username")
    private String username;

    /**
     * 密码哈希值
     * 功能描述：存储加密后的用户密码，使用BCrypt算法加密
     * 数据库约束：非空，长度最大255字符
     * 安全要求：明文密码经过BCrypt加密后存储
     */
    @NotBlank(message = "密码不能为空")
    @TableField("password")
    private String password;

    /**
     * 邮箱地址
     * 功能描述：用户邮箱，用于找回密码和接收通知
     * 数据库约束：可空，唯一，长度最大100字符
     * 业务规则：必须符合邮箱格式
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100字符")
    @TableField("email")
    private String email;

    /**
     * 手机号码
     * 功能描述：用户手机号，用于身份验证和接收短信
     * 数据库约束：可空，唯一，长度最大20字符
     * 业务规则：必须符合手机号格式
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Size(max = 20, message = "手机号长度不能超过20字符")
    @TableField("mobile")
    private String mobile;

    /**
     * 昵称
     * 功能描述：用户显示名称，用于界面展示
     * 数据库约束：可空，长度最大50字符
     */
    @Size(max = 50, message = "昵称长度不能超过50字符")
    @TableField("nickname")
    private String nickname;

    /**
     * 头像URL
     * 功能描述：用户头像图片地址
     * 数据库约束：可空，长度最大500字符
     */
    @Size(max = 500, message = "头像URL长度不能超过500字符")
    @TableField("avatar")
    private String avatar;

    /**
     * 账户状态
     * 功能描述：用户状态，1-正常，2-禁用，3-删除
     * 数据库约束：非空，默认值1，检查约束(1,2,3)
     */
    @NotNull(message = "账户状态不能为空")
    @Min(value = 1, message = "账户状态值必须在1-3之间")
    @Max(value = 3, message = "账户状态值必须在1-3之间")
    @TableField("status")
    private Integer status = 1;

    /**
     * 注册时间
     * 功能描述：用户注册时间
     * 数据库约束：非空，默认当前时间
     */
    @TableField("register_time")
    private LocalDateTime registerTime;

    /**
     * 最后登录时间
     * 功能描述：记录用户最后一次登录的时间
     * 数据库约束：可空
     */
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;

    /**
     * 逻辑删除标志
     * 功能描述：逻辑删除标志，0-未删除，1-已删除
     * 数据库约束：非空，默认值0，检查约束(0,1)
     */
    @TableField("deleted")
    private Integer deleted = 0;

    /**
     * 创建时间
     * 功能描述：记录数据创建时间，插入时自动填充
     * 数据库约束：非空，自动填充当前时间
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     * 功能描述：记录数据最后更新时间，插入和更新时自动填充
     * 数据库约束：非空，自动填充当前时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}