package com.fenix.shop.common.exception;

import com.fenix.shop.common.model.vo.Result;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理所有 Controller 中的异常，提供一致的错误响应格式
 * 
 * @author fenix
 * @date 2025/5/24
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     * 自定义业务异常，包含明确的错误码和错误信息
     * 
     * @param e 业务异常
     * @return 业务错误响应
     */
    @ExceptionHandler(BizException.class)
    public Result<Void> handleBusinessException(BizException e) {
        logger.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常（@Valid 注解触发）
     * 当请求参数不满足 DTO 中定义的校验规则时触发
     * 
     * @param e 方法参数校验异常
     * @return 包含详细错误信息的响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        logger.warn("参数校验失败: {}", e.getMessage());
        
        // 收集所有字段的校验错误
        Map<String, String> errors = new HashMap<>();
        BindingResult bindingResult = e.getBindingResult();
        
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        
        // 构建错误消息 - 支持两种格式
        String detailedErrorMessage = errors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("; "));
        
        String simpleErrorMessage = bindingResult.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        logger.warn("参数校验详细错误: {}", detailedErrorMessage);
        
        return Result.fail(400, "参数校验失败: " + simpleErrorMessage);
    }

    /**
     * 处理参数绑定异常
     * 通常在表单数据绑定时发生
     * 
     * @param e 参数绑定异常
     * @return 绑定错误响应
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBindException(BindException e) {
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        logger.warn("参数绑定异常: {}", errorMsg);
        return Result.fail(400, errorMsg);
    }

    /**
     * 处理约束违反异常（路径参数校验）
     * 当路径参数不满足校验规则时触发，如 @Positive 注解
     * 
     * @param e 约束违反异常
     * @return 包含详细错误信息的响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        logger.warn("约束违反异常: {}", e.getMessage());
        
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        
        // 详细错误信息（包含属性路径）
        String detailedErrorMessage = violations.stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .collect(Collectors.joining("; "));
        
        // 简单错误信息（仅错误消息）
        String simpleErrorMessage = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        
        logger.warn("约束违反详细错误: {}", detailedErrorMessage);
        
        return Result.fail(400, "参数校验失败: " + simpleErrorMessage);
    }

    /**
     * 处理参数异常
     * 通常由业务逻辑中的参数校验触发
     * 
     * @param e 参数异常
     * @return 参数错误响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.warn("参数异常: {}", e.getMessage());
        return Result.fail(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    /**
     * 处理运行时异常
     * 通常表示资源未找到或业务状态异常
     * 
     * @param e 运行时异常
     * @return 错误响应
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        logger.warn("运行时异常: {}", e.getMessage());
        
        // 根据异常消息判断是否为资源未找到
        if (e.getMessage() != null && e.getMessage().contains("不存在")) {
            return Result.fail(HttpStatus.NOT_FOUND.value(), e.getMessage());
        }
        
        return Result.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), "操作失败: " + e.getMessage());
    }

    /**
     * 处理通用异常
     * 捕获所有未被其他异常处理器处理的异常
     * 
     * @param e 异常对象
     * @return 通用错误响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        logger.error("系统异常", e);
        return Result.fail(500, "系统异常，请稍后重试");
    }
} 