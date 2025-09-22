package com.fenix.shop.common.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体类，所有实体类的父类
 * 包含通用字段和属性
 *
 * @author fenix
 * @date 2025/5/30
 */
@Data
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     * 使用INPUT类型，表示ID会由代码生成并设置，而不是由数据库生成
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 创建时间（自动填充）
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间（自动填充）
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记（0-未删除，1-已删除）
     */
    @TableField("deleted")
    private Integer deleted;

}