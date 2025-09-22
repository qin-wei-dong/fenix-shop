package com.fenix.shop.common.exception;

/**
 * 时钟回拨异常类
 * 功能：检测并处理系统时钟回拨情况，保证ID生成的时间有序性和唯一性
 * 技术：继承RuntimeException，提供详细的异常信息包括回拨前后的时间戳
 * 优势：及时发现时钟回拨问题，避免生成重复ID，保证分布式系统的数据一致性
 * 
 * @author fenix
 * @date 2025-01-15
 * @version 1.0
 */
public class ClockBackwardsException extends RuntimeException {
    
    /**
     * 上次生成ID时的时间戳
     * 功能：记录上一次成功生成ID时的系统时间戳
     * 技术：使用long类型存储毫秒级时间戳
     * 优势：提供精确的时间对比基准，便于问题诊断
     */
    private final long lastTimestamp;
    
    /**
     * 当前检测到的时间戳
     * 功能：记录当前检测到的系统时间戳
     * 技术：使用long类型存储毫秒级时间戳
     * 优势：与上次时间戳对比，计算回拨的具体时间差
     */
    private final long currentTimestamp;
    
    /**
     * 构造时钟回拨异常
     * 功能：创建包含详细时间信息的时钟回拨异常实例
     * 技术：调用父类构造方法，格式化异常消息，保存时间戳信息
     * 优势：提供完整的异常上下文，便于问题定位和系统监控
     * 
     * @param lastTimestamp 上次生成ID时的时间戳（毫秒）
     * @param currentTimestamp 当前检测到的时间戳（毫秒）
     */
    public ClockBackwardsException(long lastTimestamp, long currentTimestamp) {
        // 调用父类构造方法，生成详细的异常消息
        super(String.format("Clock moved backwards. Last timestamp: %d, Current timestamp: %d, Backwards: %d ms", 
                           lastTimestamp, currentTimestamp, lastTimestamp - currentTimestamp));
        // 保存上次时间戳用于异常分析
        this.lastTimestamp = lastTimestamp;
        // 保存当前时间戳用于异常分析
        this.currentTimestamp = currentTimestamp;
    }
    
    /**
     * 获取上次生成ID时的时间戳
     * 功能：返回上一次成功生成ID时的系统时间戳
     * 技术：简单的getter方法，返回final字段值
     * 优势：提供异常分析所需的时间基准信息
     * 
     * @return 上次生成ID时的时间戳（毫秒）
     */
    public long getLastTimestamp() {
        return lastTimestamp;
    }
    
    /**
     * 获取当前检测到的时间戳
     * 功能：返回当前检测到的系统时间戳
     * 技术：简单的getter方法，返回final字段值
     * 优势：提供异常分析所需的当前时间信息
     * 
     * @return 当前检测到的时间戳（毫秒）
     */
    public long getCurrentTimestamp() {
        return currentTimestamp;
    }
    
    /**
     * 计算时钟回拨的时间差
     * 功能：计算系统时钟向后回拨的具体毫秒数
     * 技术：使用时间戳相减计算时间差，返回正数表示回拨程度
     * 优势：量化时钟回拨的严重程度，便于制定恢复策略
     * 
     * @return 时钟回拨的毫秒数（正数）
     */
    public long getBackwardsMillis() {
        return lastTimestamp - currentTimestamp;
    }
}