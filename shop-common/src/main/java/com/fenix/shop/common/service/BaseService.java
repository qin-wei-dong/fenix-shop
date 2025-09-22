package com.fenix.shop.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fenix.shop.common.model.entity.BaseEntity;

/**
 * 基础服务接口，所有服务接口的父接口
 * 
 * @author fenix
 * @date 2025/5/30
 * @param <T> 实体类型
 */
public interface BaseService<T extends BaseEntity> extends IService<T> {
    
    /**
     * 保存实体，自动生成ID
     * 
     * @param entity 实体对象
     * @return 是否保存成功
     */
    boolean saveWithId(T entity);
    
    /**
     * 保存实体，自动生成带前缀的ID
     * 
     * @param entity 实体对象
     * @param prefix 前缀
     * @return 是否保存成功
     */
    boolean saveWithPrefixId(T entity, String prefix);
} 