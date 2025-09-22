package com.fenix.shop.common.utils;

import com.fenix.shop.common.exception.ClockBackwardsException;
import com.fenix.shop.common.exception.SnowflakeConfigException;
import com.fenix.shop.common.properties.SnowflakeProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 雪花算法ID生成器
 * 功能：基于雪花算法生成64位分布式唯一ID，支持高并发和多机器部署
 * 技术：采用标准雪花算法，64位结构（1位符号+41位时间戳+10位机器ID+12位序列号）
 * 优势：全局唯一、高性能、时间有序、分布式友好，支持每毫秒4096个ID生成
 * 
 * @author fenix
 * @date 2025-01-15
 * @version 1.0
 */
public class SnowflakeIdGenerator {
    
    /**
     * 日志记录器
     * 功能：记录ID生成过程中的关键信息和异常情况
     * 技术：使用SLF4J日志框架，支持多种日志实现
     * 优势：便于问题排查和系统监控
     */
    private static final Logger logger = LoggerFactory.getLogger(SnowflakeIdGenerator.class);
    
    // =============================== 位数分配常量 ===============================
    
    /**
     * 机器ID位数 (10位)
     * 功能：定义机器ID在64位ID中占用的位数
     * 技术：使用常量定义，便于算法调整和维护
     * 优势：支持1024个机器节点，满足大规模分布式部署需求
     */
    private static final long MACHINE_ID_BITS = 10L;
    
    /**
     * 数据中心ID位数 (5位)
     * 功能：定义数据中心ID在64位ID中占用的位数
     * 技术：使用常量定义，便于算法调整和维护
     * 优势：支持32个数据中心，满足多地域部署需求
     */
    private static final long DATACENTER_ID_BITS = 5L;
    
    /**
     * 序列号位数 (12位)
     * 功能：定义序列号在64位ID中占用的位数
     * 技术：使用常量定义，便于算法调整和维护
     * 优势：每毫秒支持4096个ID生成，满足高并发需求
     */
    private static final long SEQUENCE_BITS = 12L;
    
    // =============================== 最大值常量 ===============================
    
    /**
     * 机器ID最大值 (1023)
     * 功能：定义机器ID的最大允许值
     * 技术：使用位运算计算最大值 (-1L ^ (-1L << MACHINE_ID_BITS))
     * 优势：确保机器ID在有效范围内，避免位溢出
     */
    private static final long MAX_MACHINE_ID = -1L ^ (-1L << MACHINE_ID_BITS);
    
    /**
     * 数据中心ID最大值 (31)
     * 功能：定义数据中心ID的最大允许值
     * 技术：使用位运算计算最大值 (-1L ^ (-1L << DATACENTER_ID_BITS))
     * 优势：确保数据中心ID在有效范围内，避免位溢出
     */
    private static final long MAX_DATACENTER_ID = -1L ^ (-1L << DATACENTER_ID_BITS);
    
    /**
     * 序列号最大值 (4095)
     * 功能：定义序列号的最大允许值
     * 技术：使用位运算计算最大值 (-1L ^ (-1L << SEQUENCE_BITS))
     * 优势：确保序列号在有效范围内，避免位溢出
     */
    private static final long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BITS);
    
    // =============================== 位移常量 ===============================
    
    /**
     * 机器ID左移位数 (12位)
     * 功能：定义机器ID在64位ID中的位置偏移
     * 技术：序列号位数，确保机器ID位于正确位置
     * 优势：保证ID结构的正确性和唯一性
     */
    private static final long MACHINE_ID_SHIFT = SEQUENCE_BITS;
    
    /**
     * 数据中心ID左移位数 (17位)
     * 功能：定义数据中心ID在64位ID中的位置偏移
     * 技术：序列号位数 + 机器ID位数，确保数据中心ID位于正确位置
     * 优势：保证ID结构的正确性和唯一性
     */
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS;
    
    /**
     * 时间戳左移位数 (22位)
     * 功能：定义时间戳在64位ID中的位置偏移
     * 技术：序列号位数 + 机器ID位数 + 数据中心ID位数，确保时间戳位于正确位置
     * 优势：保证ID结构的正确性和时间有序性
     */
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS + DATACENTER_ID_BITS;
    
    // =============================== 实例字段 ===============================
    
    /**
     * 机器ID
     * 功能：标识当前机器节点的唯一ID
     * 技术：从配置文件读取，范围0-1023
     * 优势：确保不同机器生成的ID不冲突
     */
    private final long machineId;
    
    /**
     * 数据中心ID
     * 功能：标识当前数据中心的唯一ID
     * 技术：从配置文件读取，范围0-31
     * 优势：支持多数据中心部署架构
     */
    private final long datacenterId;
    
    /**
     * 起始时间戳
     * 功能：雪花算法的时间基准点
     * 技术：从配置文件读取，默认2024-01-01 00:00:00
     * 优势：减少时间戳位数，延长算法使用年限
     */
    private final long epoch;
    
    /**
     * 序列号
     * 功能：同一毫秒内的ID序列计数器
     * 技术：使用volatile保证可见性，范围0-4095
     * 优势：支持同一毫秒内生成多个唯一ID
     */
    private volatile long sequence = 0L;
    
    /**
     * 上次生成ID的时间戳
     * 功能：记录上一次生成ID时的时间戳，用于时钟回拨检测
     * 技术：使用volatile保证可见性，初始值为-1
     * 优势：检测系统时钟回拨，保证ID的时间有序性
     */
    private volatile long lastTimestamp = -1L;
    
    /**
     * 构造雪花算法ID生成器
     * 功能：根据配置属性初始化ID生成器实例
     * 技术：从SnowflakeProperties读取配置，进行参数验证
     * 优势：确保配置正确，避免运行时错误
     * 
     * @param properties 雪花算法配置属性
     * @throws SnowflakeConfigException 当配置参数无效时抛出
     */
    public SnowflakeIdGenerator(SnowflakeProperties properties) {
        // 从配置属性中获取机器ID
        this.machineId = properties.getMachineId();
        // 从配置属性中获取数据中心ID
        this.datacenterId = properties.getDatacenterId();
        // 从配置属性中获取起始时间戳
        this.epoch = properties.getEpoch();
        
        // 验证所有配置参数的有效性
        SnowflakeConfigException.validateAll(machineId, datacenterId, epoch);
        
        // 记录初始化成功日志
        logger.info("SnowflakeIdGenerator initialized with machineId={}, datacenterId={}, epoch={}", 
                   machineId, datacenterId, epoch);
    }
    
    /**
     * 构造雪花算法ID生成器（直接参数版本）
     * 功能：使用直接参数初始化ID生成器实例
     * 技术：直接接收参数，进行参数验证
     * 优势：便于测试和特殊场景使用
     * 
     * @param machineId 机器ID (0-1023)
     * @param datacenterId 数据中心ID (0-31)
     * @param epoch 起始时间戳（毫秒）
     * @throws SnowflakeConfigException 当配置参数无效时抛出
     */
    public SnowflakeIdGenerator(long machineId, long datacenterId, long epoch) {
        // 设置机器ID
        this.machineId = machineId;
        // 设置数据中心ID
        this.datacenterId = datacenterId;
        // 设置起始时间戳
        this.epoch = epoch;
        
        // 验证所有配置参数的有效性
        SnowflakeConfigException.validateAll(machineId, datacenterId, epoch);
        
        // 记录初始化成功日志
        logger.info("SnowflakeIdGenerator initialized with machineId={}, datacenterId={}, epoch={}", 
                   machineId, datacenterId, epoch);
    }
    
    /**
     * 生成下一个ID（Long类型）
     * 功能：生成64位长整型的分布式唯一ID
     * 技术：使用synchronized确保线程安全，实现标准雪花算法
     * 优势：高性能、全局唯一、时间有序
     * 
     * @return 64位长整型ID
     * @throws ClockBackwardsException 当检测到时钟回拨时抛出
     */
    public synchronized long nextId() {
        // 获取当前时间戳
        long timestamp = System.currentTimeMillis();
        
        // 检测时钟回拨
        if (timestamp < lastTimestamp) {
            // 记录时钟回拨错误日志
            logger.error("Clock moved backwards. Last timestamp: {}, Current timestamp: {}", 
                        lastTimestamp, timestamp);
            // 抛出时钟回拨异常
            throw new ClockBackwardsException(lastTimestamp, timestamp);
        }
        
        // 如果是同一毫秒内生成ID
        if (lastTimestamp == timestamp) {
            // 序列号递增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 如果序列号溢出（达到4096）
            if (sequence == 0) {
                // 等待下一毫秒
                timestamp = waitForNextMillis(lastTimestamp);
            }
        } else {
            // 不同毫秒，序列号重置为0
            sequence = 0L;
        }
        
        // 更新上次时间戳
        lastTimestamp = timestamp;
        
        // 计算并返回最终ID
        // ID = (时间戳-起始时间) << 22 | 数据中心ID << 17 | 机器ID << 12 | 序列号
        return ((timestamp - epoch) << TIMESTAMP_LEFT_SHIFT) |
               (datacenterId << DATACENTER_ID_SHIFT) |
               (machineId << MACHINE_ID_SHIFT) |
               sequence;
    }
    
    /**
     * 生成下一个ID（String类型）
     * 功能：生成字符串格式的分布式唯一ID
     * 技术：调用nextId()方法生成Long类型ID，然后转换为字符串
     * 优势：与现有数据库varchar字段兼容，保持API接口不变
     * 
     * @return 字符串格式的ID
     * @throws ClockBackwardsException 当检测到时钟回拨时抛出
     */
    public String nextIdAsString() {
        // 生成Long类型ID并转换为字符串
        return String.valueOf(nextId());
    }
    
    /**
     * 等待下一毫秒
     * 功能：当同一毫秒内序列号用完时，等待下一毫秒
     * 技术：使用循环检查当前时间，直到时间戳发生变化
     * 优势：确保ID生成的连续性，避免序列号溢出
     * 
     * @param lastTimestamp 上次生成ID的时间戳
     * @return 下一毫秒的时间戳
     */
    private long waitForNextMillis(long lastTimestamp) {
        // 获取当前时间戳
        long timestamp = System.currentTimeMillis();
        // 循环等待直到时间戳发生变化
        while (timestamp <= lastTimestamp) {
            // 继续获取当前时间戳
            timestamp = System.currentTimeMillis();
        }
        // 返回新的时间戳
        return timestamp;
    }
    
    /**
     * 从ID中提取时间戳
     * 功能：从生成的ID中解析出时间戳信息
     * 技术：使用位运算提取时间戳部分，加上起始时间戳得到实际时间
     * 优势：便于ID分析和调试，支持时间范围查询
     * 
     * @param id 待解析的ID
     * @return 时间戳（毫秒）
     */
    public long getTimestamp(long id) {
        // 提取时间戳部分并加上起始时间戳
        return (id >> TIMESTAMP_LEFT_SHIFT) + epoch;
    }
    
    /**
     * 从ID中提取数据中心ID
     * 功能：从生成的ID中解析出数据中心ID信息
     * 技术：使用位运算提取数据中心ID部分
     * 优势：便于ID分析和调试，支持数据中心统计
     * 
     * @param id 待解析的ID
     * @return 数据中心ID
     */
    public long getDatacenterId(long id) {
        // 提取数据中心ID部分
        return (id >> DATACENTER_ID_SHIFT) & (-1L ^ (-1L << DATACENTER_ID_BITS));
    }
    
    /**
     * 从ID中提取机器ID
     * 功能：从生成的ID中解析出机器ID信息
     * 技术：使用位运算提取机器ID部分
     * 优势：便于ID分析和调试，支持机器统计
     * 
     * @param id 待解析的ID
     * @return 机器ID
     */
    public long getMachineId(long id) {
        // 提取机器ID部分
        return (id >> MACHINE_ID_SHIFT) & (-1L ^ (-1L << MACHINE_ID_BITS));
    }
    
    /**
     * 从ID中提取序列号
     * 功能：从生成的ID中解析出序列号信息
     * 技术：使用位运算提取序列号部分
     * 优势：便于ID分析和调试，支持并发统计
     * 
     * @param id 待解析的ID
     * @return 序列号
     */
    public long getSequence(long id) {
        // 提取序列号部分
        return id & (-1L ^ (-1L << SEQUENCE_BITS));
    }
    
    /**
     * 获取当前配置的机器ID
     * 功能：返回当前实例配置的机器ID
     * 技术：简单的getter方法
     * 优势：便于配置信息查询和调试
     * 
     * @return 机器ID
     */
    public long getCurrentMachineId() {
        return machineId;
    }
    
    /**
     * 获取当前配置的数据中心ID
     * 功能：返回当前实例配置的数据中心ID
     * 技术：简单的getter方法
     * 优势：便于配置信息查询和调试
     * 
     * @return 数据中心ID
     */
    public long getCurrentDatacenterId() {
        return datacenterId;
    }
    
    /**
     * 获取当前配置的起始时间戳
     * 功能：返回当前实例配置的起始时间戳
     * 技术：简单的getter方法
     * 优势：便于配置信息查询和调试
     * 
     * @return 起始时间戳（毫秒）
     */
    public long getCurrentEpoch() {
        return epoch;
    }
}