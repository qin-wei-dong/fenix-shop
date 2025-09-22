package com.fenix.shop.common.exception;

/**
 * 雪花算法配置异常类
 * 功能：验证雪花算法配置参数的有效性，确保系统启动时配置正确
 * 技术：继承RuntimeException，提供静态验证方法，在Bean初始化时进行参数检查
 * 优势：启动时发现配置问题，避免运行时错误，保证分布式ID生成的正确性
 * 
 * @author fenix
 * @date 2025-01-15
 * @version 1.0
 */
public class SnowflakeConfigException extends RuntimeException {
    
    /**
     * 构造雪花算法配置异常
     * 功能：创建包含详细错误信息的配置异常实例
     * 技术：调用父类构造方法，传递错误消息
     * 优势：提供清晰的错误描述，便于快速定位配置问题
     * 
     * @param message 异常错误消息
     */
    public SnowflakeConfigException(String message) {
        // 调用父类构造方法，设置异常消息
        super(message);
    }
    
    /**
     * 构造带原因的雪花算法配置异常
     * 功能：创建包含原始异常信息的配置异常实例
     * 技术：调用父类构造方法，传递错误消息和原始异常
     * 优势：保留完整的异常链，便于深度问题分析
     * 
     * @param message 异常错误消息
     * @param cause 原始异常
     */
    public SnowflakeConfigException(String message, Throwable cause) {
        // 调用父类构造方法，设置异常消息和原因
        super(message, cause);
    }
    
    /**
     * 验证机器ID的有效性
     * 功能：检查机器ID是否在有效范围内（0-1023）
     * 技术：使用静态方法进行参数验证，超出范围时抛出配置异常
     * 优势：统一的验证逻辑，确保机器ID符合雪花算法10位限制
     * 
     * @param machineId 待验证的机器ID
     * @throws SnowflakeConfigException 当机器ID超出有效范围时抛出
     */
    public static void validateMachineId(long machineId) {
        // 检查机器ID是否在0-1023范围内（10位二进制的最大值）
        if (machineId < 0 || machineId > 1023) {
            // 抛出配置异常，提供详细的错误信息
            throw new SnowflakeConfigException(
                String.format("Machine ID must be between 0 and 1023 (10 bits), but got: %d", machineId));
        }
    }
    
    /**
     * 验证数据中心ID的有效性
     * 功能：检查数据中心ID是否在有效范围内（0-31）
     * 技术：使用静态方法进行参数验证，超出范围时抛出配置异常
     * 优势：统一的验证逻辑，确保数据中心ID符合雪花算法5位限制
     * 
     * @param datacenterId 待验证的数据中心ID
     * @throws SnowflakeConfigException 当数据中心ID超出有效范围时抛出
     */
    public static void validateDatacenterId(long datacenterId) {
        // 检查数据中心ID是否在0-31范围内（5位二进制的最大值）
        if (datacenterId < 0 || datacenterId > 31) {
            // 抛出配置异常，提供详细的错误信息
            throw new SnowflakeConfigException(
                String.format("Datacenter ID must be between 0 and 31 (5 bits), but got: %d", datacenterId));
        }
    }
    
    /**
     * 验证起始时间戳的有效性
     * 功能：检查起始时间戳是否合理（不能大于当前时间）
     * 技术：使用静态方法进行参数验证，比较起始时间与当前时间
     * 优势：确保时间戳计算的正确性，避免负数时间戳
     * 
     * @param epoch 待验证的起始时间戳（毫秒）
     * @throws SnowflakeConfigException 当起始时间戳大于当前时间时抛出
     */
    public static void validateEpoch(long epoch) {
        // 获取当前时间戳
        long currentTime = System.currentTimeMillis();
        // 检查起始时间戳是否大于当前时间
        if (epoch > currentTime) {
            // 抛出配置异常，提供详细的错误信息
            throw new SnowflakeConfigException(
                String.format("Epoch timestamp cannot be greater than current time. Epoch: %d, Current: %d", 
                             epoch, currentTime));
        }
        // 检查起始时间戳是否过于久远（超过69年前）
        long maxAge = 69L * 365 * 24 * 3600 * 1000; // 69年的毫秒数
        if (currentTime - epoch > maxAge) {
            // 抛出配置异常，提示时间戳过于久远
            throw new SnowflakeConfigException(
                String.format("Epoch timestamp is too old (more than 69 years ago). Epoch: %d, Current: %d", 
                             epoch, currentTime));
        }
    }
    
    /**
     * 验证所有雪花算法配置参数
     * 功能：一次性验证所有配置参数的有效性
     * 技术：调用各个单独的验证方法，统一进行参数检查
     * 优势：提供便捷的一站式验证，确保所有参数都符合要求
     * 
     * @param machineId 机器ID
     * @param datacenterId 数据中心ID
     * @param epoch 起始时间戳
     * @throws SnowflakeConfigException 当任何参数无效时抛出
     */
    public static void validateAll(long machineId, long datacenterId, long epoch) {
        // 验证机器ID
        validateMachineId(machineId);
        // 验证数据中心ID
        validateDatacenterId(datacenterId);
        // 验证起始时间戳
        validateEpoch(epoch);
    }
}