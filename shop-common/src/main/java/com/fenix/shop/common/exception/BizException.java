package com.fenix.shop.common.exception;

import lombok.Getter;

/**
 * 业务异常类
 * 用于业务逻辑中的异常处理，包含错误码和错误信息
 * 
 * @author fenix
 * @date 2025/5/24
 */
@Getter
public class BizException extends RuntimeException {
    
    /**
     * 错误码
     * -- GETTER --
     *  获取错误码
     *
     * @return 错误码

     */
    private final Integer code;
    
    /**
     * 错误消息
     */
    private final String message;

    /**
     * 构造函数 - 使用默认错误码 400
     * 
     * @param message 错误信息
     */
    public BizException(String message) {
        super(message);
        this.code = 400;
        this.message = message;
    }
    
    /**
     * 构造函数 - 指定错误码和错误信息
     * 
     * @param code 错误码
     * @param message 错误信息
     */
    public BizException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造函数 - 包含原因异常
     * 
     * @param code 错误码
     * @param message 错误信息
     * @param cause 原因异常
     */
    public BizException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    /**
     * 获取错误消息
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 静态工厂方法 - 创建业务异常
     * 
     * @param message 错误信息
     * @return 业务异常实例
     */
    public static BizException of(String message) {
        return new BizException(message);
    }

    /**
     * 静态工厂方法 - 创建业务异常
     * 
     * @param code 错误码
     * @param message 错误信息
     * @return 业务异常实例
     */
    public static BizException of(Integer code, String message) {
        return new BizException(code, message);
    }
} 