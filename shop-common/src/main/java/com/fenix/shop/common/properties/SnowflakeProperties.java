package com.fenix.shop.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 雪花算法配置属性类
 * 功能：管理雪花算法相关的配置参数，支持从配置文件和环境变量读取
 * 技术：使用Spring Boot ConfigurationProperties机制，自动绑定配置属性
 * 优势：类型安全的配置管理，支持IDE智能提示，便于配置验证和环境适配
 * 
 * @ConfigurationProperties 注解用于绑定配置文件中以"fenix.snowflake"为前缀的属性
 * 通过@EnableConfigurationProperties自动创建Bean，无需@Component注解
 * 
 * @author fenix
 * @date 2025-01-15
 * @version 1.0
 */
@ConfigurationProperties(prefix = "fenix.snowflake")
public class SnowflakeProperties {
    
    /**
     * 机器ID (0-1023)
     * 功能：标识不同的机器节点，确保分布式环境下ID的唯一性
     * 技术：使用10位二进制表示，支持1024个不同的机器节点
     * 优势：支持大规模分布式部署，避免不同机器生成相同ID
     */
    private long machineId = 1;
    
    /**
     * 数据中心ID (0-31)
     * 功能：标识不同的数据中心，支持多数据中心部署架构
     * 技术：使用5位二进制表示，支持32个不同的数据中心
     * 优势：支持跨地域部署，提高系统的容灾能力和可扩展性
     */
    private long datacenterId = 1;
    
    /**
     * 起始时间戳 (2024-01-01 00:00:00)
     * 功能：雪花算法的时间基准点，用于计算相对时间戳
     * 技术：使用毫秒级时间戳，41位可支持约69年的使用期
     * 优势：减少时间戳位数，延长算法可用年限，提高ID生成效率
     */
    private long epoch = 1704067200000L; // 2024-01-01 00:00:00 UTC
    
    /**
     * 获取机器ID
     * 功能：返回当前配置的机器ID值
     * 技术：简单的getter方法，返回machineId字段值
     * 优势：提供类型安全的配置访问，支持IDE智能提示
     * 
     * @return 机器ID (0-1023)
     */
    public long getMachineId() {
        return machineId;
    }
    
    /**
     * 设置机器ID
     * 功能：设置机器ID值，通常由Spring Boot配置绑定自动调用
     * 技术：标准的setter方法，支持Spring Boot属性绑定
     * 优势：支持配置文件和环境变量的自动绑定
     * 
     * @param machineId 机器ID (0-1023)
     */
    public void setMachineId(long machineId) {
        this.machineId = machineId;
    }
    
    /**
     * 获取数据中心ID
     * 功能：返回当前配置的数据中心ID值
     * 技术：简单的getter方法，返回datacenterId字段值
     * 优势：提供类型安全的配置访问，支持多数据中心架构
     * 
     * @return 数据中心ID (0-31)
     */
    public long getDatacenterId() {
        return datacenterId;
    }
    
    /**
     * 设置数据中心ID
     * 功能：设置数据中心ID值，通常由Spring Boot配置绑定自动调用
     * 技术：标准的setter方法，支持Spring Boot属性绑定
     * 优势：支持配置文件和环境变量的自动绑定
     * 
     * @param datacenterId 数据中心ID (0-31)
     */
    public void setDatacenterId(long datacenterId) {
        this.datacenterId = datacenterId;
    }
    
    /**
     * 获取起始时间戳
     * 功能：返回雪花算法的时间基准点
     * 技术：简单的getter方法，返回epoch字段值
     * 优势：提供统一的时间基准，确保时间戳计算的一致性
     * 
     * @return 起始时间戳（毫秒）
     */
    public long getEpoch() {
        return epoch;
    }
    
    /**
     * 设置起始时间戳
     * 功能：设置雪花算法的时间基准点
     * 技术：标准的setter方法，支持Spring Boot属性绑定
     * 优势：支持不同环境使用不同的时间基准
     * 
     * @param epoch 起始时间戳（毫秒）
     */
    public void setEpoch(long epoch) {
        this.epoch = epoch;
    }
    
    /**
     * 重写toString方法
     * 功能：提供配置信息的字符串表示，便于日志输出和调试
     * 技术：使用StringBuilder构建格式化字符串
     * 优势：便于配置信息的查看和问题排查
     * 
     * @return 配置信息的字符串表示
     */
    @Override
    public String toString() {
        return "SnowflakeProperties{" +
                "machineId=" + machineId +
                ", datacenterId=" + datacenterId +
                ", epoch=" + epoch +
                '}';
    }
}