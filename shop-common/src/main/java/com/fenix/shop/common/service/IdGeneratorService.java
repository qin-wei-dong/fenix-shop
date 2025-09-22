package com.fenix.shop.common.service;

import com.fenix.shop.common.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ID生成器服务类
 * 功能：提供统一的分布式ID生成服务，封装雪花算法的使用细节
 * 技术：使用Spring Service注解管理Bean生命周期，依赖注入雪花算法生成器
 * 优势：统一ID生成接口，便于业务层使用，支持未来扩展其他ID生成策略
 * 
 * @Service 注解标识这是一个Spring服务类
 * 
 * @author fenix
 * @date 2025-01-15
 * @version 1.0
 */
@Service
public class IdGeneratorService {
    
    /**
     * 雪花算法ID生成器
     * 功能：核心的分布式ID生成组件
     * 技术：通过Spring依赖注入获取SnowflakeIdGenerator实例
     * 优势：解耦业务逻辑与ID生成实现，便于测试和维护
     */
    @Autowired
    private SnowflakeIdGenerator snowflakeIdGenerator;
    
    /**
     * 生成Long类型的分布式ID
     * 功能：生成64位长整型的全局唯一ID
     * 技术：调用雪花算法生成器的nextId方法
     * 优势：高性能、全局唯一、时间有序
     * 
     * @return 64位长整型分布式ID
     */
    public Long generateId() {
        // 调用雪花算法生成器生成ID
        return snowflakeIdGenerator.nextId();
    }
    
    /**
     * 生成String类型的分布式ID
     * 功能：生成字符串格式的全局唯一ID，便于数据库存储
     * 技术：将Long类型ID转换为String格式
     * 优势：兼容现有数据库设计，便于前端处理
     * 
     * @return 字符串格式的分布式ID
     */
    public String generateStringId() {
        // 生成Long类型ID并转换为字符串
        return String.valueOf(generateId());
    }
    
    /**
     * 批量生成分布式ID
     * 功能：一次性生成多个分布式ID，提高批量操作性能
     * 技术：循环调用ID生成方法，返回ID数组
     * 优势：减少方法调用开销，适用于批量数据处理场景
     * 
     * @param count 需要生成的ID数量，必须大于0
     * @return Long类型ID数组
     * @throws IllegalArgumentException 当count小于等于0时抛出
     */
    public Long[] generateIds(int count) {
        // 验证参数有效性
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be greater than 0");
        }
        
        // 创建ID数组
        Long[] ids = new Long[count];
        
        // 循环生成ID
        for (int i = 0; i < count; i++) {
            ids[i] = generateId();
        }
        
        // 返回ID数组
        return ids;
    }
    
    /**
     * 解析分布式ID的时间戳
     * 功能：从雪花算法生成的ID中提取时间戳信息
     * 技术：调用雪花算法生成器的getTimestamp方法
     * 优势：便于ID的时间分析和调试
     * 
     * @param id 需要解析的分布式ID
     * @return ID生成时的时间戳（毫秒）
     */
    public long getTimestamp(Long id) {
        // 调用雪花算法生成器解析时间戳
        return snowflakeIdGenerator.getTimestamp(id);
    }
    
    /**
     * 解析分布式ID的机器ID
     * 功能：从雪花算法生成的ID中提取机器ID信息
     * 技术：调用雪花算法生成器的getMachineId方法
     * 优势：便于ID的来源分析和调试
     * 
     * @param id 需要解析的分布式ID
     * @return ID生成时的机器ID
     */
    public long getMachineId(Long id) {
        // 调用雪花算法生成器解析机器ID
        return snowflakeIdGenerator.getMachineId(id);
    }
    
    /**
     * 解析分布式ID的数据中心ID
     * 功能：从雪花算法生成的ID中提取数据中心ID信息
     * 技术：调用雪花算法生成器的getDatacenterId方法
     * 优势：便于ID的来源分析和调试
     * 
     * @param id 需要解析的分布式ID
     * @return ID生成时的数据中心ID
     */
    public long getDatacenterId(Long id) {
        // 调用雪花算法生成器解析数据中心ID
        return snowflakeIdGenerator.getDatacenterId(id);
    }
    
    /**
     * 解析分布式ID的序列号
     * 功能：从雪花算法生成的ID中提取序列号信息
     * 技术：调用雪花算法生成器的getSequence方法
     * 优势：便于ID的序列分析和调试
     * 
     * @param id 需要解析的分布式ID
     * @return ID生成时的序列号
     */
    public long getSequence(Long id) {
        // 调用雪花算法生成器解析序列号
        return snowflakeIdGenerator.getSequence(id);
    }
}