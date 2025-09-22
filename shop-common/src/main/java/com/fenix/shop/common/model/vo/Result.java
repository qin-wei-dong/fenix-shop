package com.fenix.shop.common.model.vo;

import java.io.Serializable;
import java.util.Map;

import com.fenix.shop.common.enums.ErrorCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用API响应对象
 * 提供标准化的API响应格式，确保前后端数据交互的一致性
 * <p>
 * 作用：统一所有API接口的响应格式，包含状态码、消息、数据和成功标识
 * <p>
 * 为什么这样设计：
 * - 使用泛型T支持任意类型的数据返回，提高代码复用性
 * - 提供静态工厂方法，简化Result对象的创建过程
 * - 包含success字段，便于前端快速判断请求是否成功
 * - 标准化错误码和消息，便于错误处理和国际化
 * - 实现Serializable接口，支持序列化传输和缓存
 * <p>
 * 好处：
 * 1. 统一响应格式，降低前后端对接成本
 * 2. 简化错误处理，提供清晰的成功/失败标识
 * 3. 支持泛型，类型安全且灵活
 * 4. 工厂方法模式，使用简单且不易出错
 * 5. 支持多种错误场景，覆盖常见HTTP状态码
 *
 * @param <T> 响应数据类型，支持任意业务对象
 * @author fenix
 * @date 2025-06-28
 */
@Data // Lombok注解，自动生成getter、setter、toString、equals、hashCode方法
@NoArgsConstructor // Lombok注解，生成无参构造函数，便于反序列化
@AllArgsConstructor // Lombok注解，生成全参构造函数，便于快速创建对象
public class Result<T> implements Serializable {

    /**
     * 序列化版本号
     * 确保序列化和反序列化的兼容性，避免版本冲突
     */
    private static final long serialVersionUID = 1L;

    /**
     * 响应状态码
     * 遵循HTTP状态码规范：200成功，400客户端错误，500服务器错误
     * 便于前端根据状态码进行不同的处理逻辑
     */
    private Integer code;

    /**
     * 响应消息
     * 成功时显示操作成功信息，失败时显示具体错误原因
     * 支持国际化，可根据用户语言返回不同语言的消息
     */
    private String message;

    /**
     * 响应数据
     * 泛型设计，可以承载任意类型的业务数据
     * 成功时包含业务数据，失败时通常为null或错误详情
     */
    private T data;

    /**
     * 请求是否成功标识
     * true: 操作成功, false: 操作失败
     * 便于前端快速判断请求结果，无需解析状态码
     */
    private boolean success;

    /**
     * 字段级错误信息
     * 用于表单验证失败时返回具体字段的错误信息
     * Map结构：字段名 -> 错误消息
     */
    private Map<String, String> fieldErrors;
    
    /**
     * 成功响应（无数据）
     * <p>
     * 作用：创建无数据的成功响应，适用于删除、更新等操作
     * <p>
     * 为什么这样实现：
     * - 使用Void泛型，明确表示无返回数据
     * - 设置标准的成功状态码200和通用成功消息
     * - 将data设为null，避免不必要的数据传输
     * <p>
     * 好处：
     * 1. 语义清晰，明确表示操作成功但无数据返回
     * 2. 减少网络传输，提高性能
     * 3. 统一成功响应格式，便于前端处理
     *
     * @return Result<Void> 无数据的成功响应对象
     */
    public static Result<Void> success() {
        // 创建新的Result对象实例
        Result<Void> result = new Result<>();
        // 设置HTTP成功状态码200
        result.code = 200;
        // 设置通用成功消息
        result.message = "操作成功";
        // 明确设置数据为null，表示无返回数据
        result.data = null;
        // 设置成功标识为true
        result.success = true;
        // 返回构建完成的结果对象
        return result;
    }

    /**
     * 成功响应（带数据）
     * <p>
     * 作用：创建包含业务数据的成功响应，适用于查询、创建等操作
     * <p>
     * 为什么这样实现：
     * - 使用泛型方法，支持任意类型的数据返回
     * - 自动推断数据类型，无需显式指定泛型参数
     * - 保持与无数据版本一致的状态码和消息格式
     * <p>
     * 好处：
     * 1. 类型安全，编译时检查数据类型
     * 2. 使用简单，自动类型推断
     * 3. 统一响应格式，便于前端解析
     *
     * @param data 要返回的业务数据
     * @param <T> 数据类型，由传入参数自动推断
     * @return Result<T> 包含数据的成功响应对象
     */
    public static <T> Result<T> success(T data) {
        // 创建新的Result对象实例，泛型类型由参数推断
        Result<T> result = new Result<>();
        // 设置HTTP成功状态码200
        result.code = 200;
        // 设置通用成功消息
        result.message = "操作成功";
        // 设置返回的业务数据
        result.data = data;
        // 设置成功标识为true
        result.success = true;
        // 返回构建完成的结果对象
        return result;
    }

    /**
     * 成功响应（自定义消息和数据）
     * <p>
     * 作用：创建包含自定义消息和业务数据的成功响应
     * <p>
     * 为什么这样实现：
     * - 支持自定义成功消息，提供更具体的操作反馈
     * - 同时包含数据和消息，满足复杂业务场景需求
     * - 保持统一的响应结构和成功状态码
     * <p>
     * 好处：
     * 1. 灵活的消息定制，提升用户体验
     * 2. 支持业务数据返回，满足查询需求
     * 3. 统一的响应格式，便于前端处理
     *
     * @param message 自定义成功消息
     * @param data 要返回的业务数据
     * @param <T> 数据类型，由传入参数自动推断
     * @return Result<T> 包含自定义消息和数据的成功响应对象
     */
    public static <T> Result<T> success(String message, T data) {
        // 创建新的Result对象实例
        Result<T> result = new Result<>();
        // 设置HTTP成功状态码200
        result.code = 200;
        // 设置自定义的成功消息
        result.message = message;
        // 设置返回的业务数据
        result.data = data;
        // 设置成功标识为true
        result.success = true;
        // 返回构建完成的结果对象
        return result;
    }
    
    /**
     * 失败响应（自定义状态码）
     * <p>
     * 作用：创建包含自定义状态码和错误消息的失败响应
     * <p>
     * 为什么这样实现：
     * - 支持自定义HTTP状态码，适应不同的错误场景
     * - 提供详细的错误信息，便于问题定位和用户提示
     * - 保持与其他错误响应方法的一致性
     * <p>
     * 好处：
     * 1. 灵活的错误码设置，支持RESTful API规范
     * 2. 详细的错误信息，提升用户体验
     * 3. 统一的错误处理格式，简化前端逻辑
     *
     * @param code 自定义的HTTP状态码
     * @param message 具体的错误信息
     * @param <T> 数据类型，失败时通常为Void
     * @return Result<T> 自定义状态码的失败响应对象
     */
    public static <T> Result<T> fail(Integer code, String message) {
        // 创建新的Result对象实例
        Result<T> result = new Result<>();
        // 设置自定义的HTTP状态码
        result.code = code;
        // 设置具体的错误消息
        result.message = message;
        // 设置成功标识为false，表示操作失败
        result.success = false;
        // 返回构建完成的错误结果对象
        return result;
    }

    /**
     * 失败响应（仅消息）
     * <p>
     * 作用：创建包含错误消息的失败响应，使用默认的服务器错误状态码
     * <p>
     * 为什么这样实现：
     * - 使用默认的服务器错误状态码500，适用于一般业务异常
     * - 简化错误响应创建，只需提供错误消息
     * - 内部调用带状态码的fail方法，保持代码一致性
     * <p>
     * 好处：
     * 1. 简化错误响应创建，只需提供错误消息
     * 2. 统一错误格式，便于前端错误处理
     * 3. 明确失败状态，避免前端误判
     *
     * @param message 具体的错误信息
     * @param <T> 数据类型，失败时通常为Void
     * @return Result<T> 失败响应对象
     */
    public static <T> Result<T> fail(String message) {
        // 调用带状态码的fail方法，使用默认的500状态码
        return fail(500, message);
    }

    /**
     * 客户端错误响应
     *
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 客户端错误的响应结果
     */
    public static <T> Result<T> badRequest(String message) {
        return fail(400, message);
    }

    /**
     * 未授权响应
     *
     * @param <T> 数据类型
     * @return 未授权的响应结果
     */
    public static <T> Result<T> unauthorized() {
        return fail(401, "未授权");
    }

    /**
     * 禁止访问响应
     *
     * @param <T> 数据类型
     * @return 禁止访问的响应结果
     */
    public static <T> Result<T> forbidden() {
        return fail(403, "禁止访问");
    }

    /**
     * 资源不存在响应
     *
     * @param <T> 数据类型
     * @return 资源不存在的响应结果
     */
    public static <T> Result<T> notFound() {
        return fail(404, "资源不存在");
    }

    // ========== 使用ErrorCode的新方法 ==========

    /**
     * 根据错误码创建失败响应
     *
     * @param errorCode 错误码枚举
     * @param <T> 数据类型
     * @return 失败响应结果
     */
    public static <T> Result<T> error(ErrorCode errorCode) {
        Result<T> result = new Result<>();
        result.code = errorCode.getCode();
        result.message = errorCode.getMessage();
        result.success = false;
        return result;
    }

    /**
     * 根据错误码创建失败响应（自定义消息）
     *
     * @param errorCode 错误码枚举
     * @param customMessage 自定义错误消息
     * @param <T> 数据类型
     * @return 失败响应结果
     */
    public static <T> Result<T> error(ErrorCode errorCode, String customMessage) {
        Result<T> result = new Result<>();
        result.code = errorCode.getCode();
        result.message = customMessage;
        result.success = false;
        return result;
    }

    /**
     * 根据错误码创建失败响应（带数据）
     *
     * @param errorCode 错误码枚举
     * @param data 错误数据
     * @param <T> 数据类型
     * @return 失败响应结果
     */
    public static <T> Result<T> error(ErrorCode errorCode, T data) {
        Result<T> result = new Result<>();
        result.code = errorCode.getCode();
        result.message = errorCode.getMessage();
        result.data = data;
        result.success = false;
        return result;
    }

    // ========== 常用错误响应的便捷方法 ==========

    /**
     * 参数错误响应（无参数版本）
     */
    public static <T> Result<T> badRequest() {
        return error(ErrorCode.BAD_REQUEST);
    }

    /**
     * 资源不存在响应（带资源名称）
     */
    public static <T> Result<T> notFound(String resource) {
        return error(ErrorCode.NOT_FOUND, resource + "不存在");
    }

    /**
     * 服务器内部错误响应
     */
    public static <T> Result<T> internalError() {
        return error(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * 服务器内部错误响应（自定义消息）
     */
    public static <T> Result<T> internalError(String message) {
        return error(ErrorCode.INTERNAL_SERVER_ERROR, message);
    }
} 