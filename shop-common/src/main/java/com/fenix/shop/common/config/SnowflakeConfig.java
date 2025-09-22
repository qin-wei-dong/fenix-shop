package com.fenix.shop.common.config;

import com.fenix.shop.common.properties.SnowflakeProperties;
import com.fenix.shop.common.utils.SnowflakeIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 雪花算法自动配置类
 * 功能：提供雪花算法ID生成器的Spring Boot自动配置，管理Bean的创建和依赖注入
 * 技术：使用Spring Boot Configuration和EnableConfigurationProperties机制
 * 优势：自动化配置管理，支持条件化Bean创建，便于集成和使用
 * 
 * @Configuration 注解标识这是一个Spring配置类
 * @EnableConfigurationProperties 注解启用SnowflakeProperties配置属性绑定
 * 
 * @author fenix
 * @date 2025-01-15
 * @version 1.0
 */
@Configuration
@EnableConfigurationProperties(SnowflakeProperties.class)
public class SnowflakeConfig {
    
    /**
     * 日志记录器
     * 功能：记录配置过程中的关键信息和异常情况
     * 技术：使用SLF4J日志框架，支持多种日志实现
     * 优势：便于配置问题排查和系统监控
     */
    private static final Logger logger = LoggerFactory.getLogger(SnowflakeConfig.class);
    
    /**
     * 创建雪花算法ID生成器Bean
     * 功能：根据配置属性创建SnowflakeIdGenerator实例，注册为Spring Bean
     * 技术：使用@Bean注解创建Bean，@ConditionalOnMissingBean确保单例
     * 优势：自动化Bean管理，支持依赖注入，避免重复创建
     * 
     * @Bean 注解将方法返回值注册为Spring Bean
     * @ConditionalOnMissingBean 注解确保只有在容器中不存在该类型Bean时才创建
     * 
     * @param properties 雪花算法配置属性，由Spring自动注入
     * @return SnowflakeIdGenerator实例
     */
    @Bean
    @ConditionalOnMissingBean
    public SnowflakeIdGenerator snowflakeIdGenerator(@Qualifier("fenix.snowflake-com.fenix.shop.common.properties.SnowflakeProperties") SnowflakeProperties properties) {
        // 记录配置开始日志
        logger.info("Configuring SnowflakeIdGenerator with properties: {}", properties);
        
        try {
            // 创建雪花算法ID生成器实例
            SnowflakeIdGenerator generator = new SnowflakeIdGenerator(properties);
            
            // 记录配置成功日志
            logger.info("SnowflakeIdGenerator configured successfully with machineId={}, datacenterId={}, epoch={}", 
                       properties.getMachineId(), properties.getDatacenterId(), properties.getEpoch());
            
            // 返回生成器实例
            return generator;
            
        } catch (Exception e) {
            // 记录配置失败日志
            logger.error("Failed to configure SnowflakeIdGenerator with properties: {}", properties, e);
            // 重新抛出异常，阻止应用启动
            throw e;
        }
    }
}