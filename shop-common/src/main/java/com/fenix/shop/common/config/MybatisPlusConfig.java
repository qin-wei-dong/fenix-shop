package com.fenix.shop.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.fenix.shop.common.utils.SecurityUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * @Description  MyBatis-Plus配置类
 * @Author fenix
 * @Date 2025/5/31
 **/
@Configuration
public class MybatisPlusConfig {

    /**
     * 配置MyBatis-Plus插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 添加分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());

        // 添加乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        return interceptor;
    }

    /**
     * 字段自动填充处理器
     * 用于自动填充创建时间和更新时间
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                // 获取当前登录用户ID，用于审计字段填充
                String currentUserId = SecurityUtils.getCurrentUserId();
                
                // 插入时，填充创建时间和更新时间
                this.strictInsertFill(metaObject, "created_time", LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "updated_time", LocalDateTime.class, LocalDateTime.now());
                // 初始化删除标记为0（未删除）
                this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
                
                // 自动填充创建人字段，如果当前用户已登录
                if (StringUtils.hasText(currentUserId)) {
                    this.strictInsertFill(metaObject, "created_by", String.class, currentUserId);
                }
                
                // 自动填充更新人字段，如果当前用户已登录
                if (StringUtils.hasText(currentUserId)) {
                    this.strictInsertFill(metaObject, "updated_by", String.class, currentUserId);
                }
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                // 获取当前登录用户ID，用于审计字段填充
                String currentUserId = SecurityUtils.getCurrentUserId();
                
                // 更新时，填充更新时间
                this.strictUpdateFill(metaObject, "updated_time", LocalDateTime.class, LocalDateTime.now());
                
                // 自动填充更新人字段，如果当前用户已登录
                if (StringUtils.hasText(currentUserId)) {
                    this.strictUpdateFill(metaObject, "updated_by", String.class, currentUserId);
                }
            }
        };
    }
}
