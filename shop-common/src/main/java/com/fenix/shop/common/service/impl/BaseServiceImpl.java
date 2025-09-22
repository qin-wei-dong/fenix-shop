package com.fenix.shop.common.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fenix.shop.common.model.entity.BaseEntity;
import com.fenix.shop.common.utils.SnowflakeIdGenerator;
import com.fenix.shop.common.service.BaseService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 基础服务实现类，所有服务实现类的父类
 * 实现通用的CRUD操作和ID生成
 *
 * @param <M> Mapper类型
 * @param <T> 实体类型
 * @author fenix
 * @date 2025/5/30
 */
public abstract class BaseServiceImpl<M extends BaseMapper<T>, T extends BaseEntity> extends ServiceImpl<M, T> implements BaseService<T> {

    @Autowired
    protected SnowflakeIdGenerator idGenerator;

    /**
     * 保存实体，使用雪花算法自动生成ID
     *
     * @param entity 实体对象
     * @return 是否保存成功
     */
    @Override
    public boolean saveWithId(T entity) {
        // 仅当ID为空时生成新ID
        if (entity.getId() == null) {
            entity.setId(idGenerator.nextId());
        }
        return this.save(entity);
    }

    /**
     * 保存实体，使用雪花算法自动生成带前缀的ID
     * 注意：前缀ID是字符串，需要在实体类中处理或者使用其他方式保存
     *
     * @param entity 实体对象
     * @param prefix 前缀
     * @return 是否保存成功
     */
    @Override
    public boolean saveWithPrefixId(T entity, String prefix) {
        // 仅当ID为空时生成新ID
        if (entity.getId() == null) {
            // 由于ID是Long类型，这里只生成数字部分
            entity.setId(idGenerator.nextId());
        }
        // 对于需要带前缀的场景，可以在业务层面另行处理
        return this.save(entity);
    }
}