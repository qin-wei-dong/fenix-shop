package com.fenix.shop.common.enums;

/**
 * 统一错误码枚举
 * 定义系统中所有可能的错误码和错误消息
 * 
 * 错误码规范：
 * - 200: 成功
 * - 400-499: 客户端错误
 * - 500-599: 服务器错误
 * - 1000-1999: 用户相关错误
 * - 2000-2999: 商品相关错误
 * - 3000-3999: 订单相关错误
 * - 4000-4999: 支付相关错误
 * - 5000-5999: 搜索相关错误
 * - 6000-6999: 系统相关错误
 *
 * @author fenix
 * @date 2025-06-28
 */
public enum ErrorCode {
    
    // ========== 通用状态码 ==========
    SUCCESS(200, "操作成功"),
    
    // ========== 客户端错误 4xx ==========
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权访问"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    REQUEST_TIMEOUT(408, "请求超时"),
    CONFLICT(409, "资源冲突"),
    PAYLOAD_TOO_LARGE(413, "请求体过大"),
    UNSUPPORTED_MEDIA_TYPE(415, "不支持的媒体类型"),
    TOO_MANY_REQUESTS(429, "请求过于频繁"),
    
    // ========== 通用业务错误 ==========
    PARAM_ERROR(4001, "参数错误"),
    PARAM_FORMAT_INVALID(4002, "参数格式无效"),
    DATA_NOT_FOUND(4003, "数据不存在"),
    DUPLICATE_OPERATION(4004, "重复操作"),
    PARAM_REQUIRED(4005, "必需参数缺失"),
    OPERATION_LIMITED(4006, "操作受限"),
    SYSTEM_ERROR(5001, "系统错误"),
    
    // ========== 服务器错误 5xx ==========
    INTERNAL_SERVER_ERROR(500, "系统内部错误"),
    NOT_IMPLEMENTED(501, "功能未实现"),
    BAD_GATEWAY(502, "网关错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    GATEWAY_TIMEOUT(504, "网关超时"),
    
    // ========== 用户相关错误 1xxx ==========
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    INVALID_CREDENTIALS(1003, "用户名或密码错误"),
    USER_DISABLED(1004, "用户已被禁用"),
    USER_LOCKED(1005, "用户已被锁定"),
    PASSWORD_EXPIRED(1006, "密码已过期"),
    INVALID_TOKEN(1007, "无效的访问令牌"),
    TOKEN_EXPIRED(1008, "访问令牌已过期"),
    INSUFFICIENT_PERMISSIONS(1009, "权限不足"),
    INVALID_VERIFICATION_CODE(1010, "验证码错误"),
    VERIFICATION_CODE_EXPIRED(1011, "验证码已过期"),
    MOBILE_ALREADY_EXISTS(1012, "手机号已被注册"),
    EMAIL_ALREADY_EXISTS(1013, "邮箱已被注册"),
    INVALID_MOBILE_FORMAT(1014, "手机号格式不正确"),
    INVALID_EMAIL_FORMAT(1015, "邮箱格式不正确"),
    PASSWORD_TOO_WEAK(1016, "密码强度不足"),
    
    // ========== 商品相关错误 2xxx ==========
    PRODUCT_NOT_FOUND(2001, "商品不存在"),
    PRODUCT_OUT_OF_STOCK(2002, "商品库存不足"),
    PRODUCT_OFF_SHELF(2003, "商品已下架"),
    PRODUCT_DELETED(2004, "商品已删除"),
    INVALID_PRICE_RANGE(2005, "价格区间无效"),
    INVALID_PRODUCT_STATUS(2006, "商品状态无效"),
    PRODUCT_SKU_NOT_FOUND(2007, "商品规格不存在"),
    PRODUCT_CATEGORY_NOT_FOUND(2008, "商品分类不存在"),
    PRODUCT_BRAND_NOT_FOUND(2009, "商品品牌不存在"),
    INVALID_PRODUCT_QUANTITY(2010, "商品数量无效"),
    PRODUCT_PRICE_CHANGED(2011, "商品价格已变更"),
    PRODUCT_LIMIT_EXCEEDED(2012, "超出商品购买限制"),
    
    // ========== 订单相关错误 3xxx ==========
    ORDER_NOT_FOUND(3001, "订单不存在"),
    ORDER_STATUS_INVALID(3002, "订单状态无效"),
    ORDER_CANNOT_CANCEL(3003, "订单无法取消"),
    ORDER_CANNOT_MODIFY(3004, "订单无法修改"),
    ORDER_ALREADY_PAID(3005, "订单已支付"),
    ORDER_EXPIRED(3006, "订单已过期"),
    ORDER_AMOUNT_MISMATCH(3007, "订单金额不匹配"),
    INVALID_ORDER_STATUS_TRANSITION(3008, "无效的订单状态变更"),
    ORDER_ITEM_NOT_FOUND(3009, "订单项不存在"),
    
    // ========== 支付相关错误 4xxx ==========
    PAYMENT_FAILED(4001, "支付失败"),
    PAYMENT_TIMEOUT(4002, "支付超时"),
    PAYMENT_CANCELLED(4003, "支付已取消"),
    PAYMENT_METHOD_NOT_SUPPORTED(4004, "不支持的支付方式"),
    INSUFFICIENT_BALANCE(4005, "余额不足"),
    PAYMENT_AMOUNT_INVALID(4006, "支付金额无效"),
    PAYMENT_ALREADY_EXISTS(4007, "支付记录已存在"),
    REFUND_FAILED(4008, "退款失败"),
    REFUND_AMOUNT_EXCEEDED(4009, "退款金额超出限制"),
    
    // ========== 搜索相关错误 5xxx ==========
    SEARCH_SERVICE_UNAVAILABLE(5001, "搜索服务不可用"),
    INVALID_SEARCH_KEYWORD(5002, "搜索关键词无效"),
    SEARCH_TIMEOUT(5003, "搜索超时"),
    SEARCH_INDEX_ERROR(5004, "搜索索引错误"),
    TOO_MANY_SEARCH_RESULTS(5005, "搜索结果过多"),
    SEARCH_SYNTAX_ERROR(5006, "搜索语法错误"),
    
    // ========== 系统相关错误 6xxx ==========
    DATABASE_ERROR(6001, "数据库错误"),
    CACHE_ERROR(6002, "缓存错误"),
    NETWORK_ERROR(6003, "网络错误"),
    FILE_UPLOAD_ERROR(6004, "文件上传失败"),
    FILE_NOT_FOUND(6005, "文件不存在"),
    FILE_SIZE_EXCEEDED(6006, "文件大小超出限制"),
    INVALID_FILE_FORMAT(6007, "文件格式不支持"),
    CONFIGURATION_ERROR(6008, "配置错误"),
    EXTERNAL_SERVICE_ERROR(6009, "外部服务错误"),
    DATA_VALIDATION_ERROR(6010, "数据验证失败"),
    CONCURRENT_MODIFICATION_ERROR(6011, "并发修改冲突"),
    RATE_LIMIT_EXCEEDED(6012, "访问频率超出限制"),
    
    // ========== 购物车相关错误 7xxx ==========
    CART_NOT_FOUND(7001, "购物车不存在"),
    CART_ITEM_NOT_FOUND(7002, "购物车商品不存在"),
    CART_ITEM_LIMIT_EXCEEDED(7003, "购物车商品数量超出限制"),
    CART_EMPTY(7004, "购物车为空"),
    
    // ========== 物流相关错误 8xxx ==========
    LOGISTICS_INFO_NOT_FOUND(8001, "物流信息不存在"),
    INVALID_TRACKING_NUMBER(8002, "无效的快递单号"),
    LOGISTICS_SERVICE_ERROR(8003, "物流服务错误"),
    
    // ========== 优惠券相关错误 9xxx ==========
    COUPON_NOT_FOUND(9001, "优惠券不存在"),
    COUPON_EXPIRED(9002, "优惠券已过期"),
    COUPON_USED(9003, "优惠券已使用"),
    COUPON_NOT_APPLICABLE(9004, "优惠券不适用"),
    COUPON_LIMIT_EXCEEDED(9005, "优惠券使用次数超出限制");
    
    private final int code;
    private final String message;
    
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
    
    /**
     * 根据错误码获取错误枚举
     * 
     * @param code 错误码
     * @return 错误枚举，如果不存在则返回null
     */
    public static ErrorCode getByCode(int code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getCode() == code) {
                return errorCode;
            }
        }
        return null;
    }
    
    /**
     * 判断是否为成功状态码
     * 
     * @param code 状态码
     * @return 是否成功
     */
    public static boolean isSuccess(int code) {
        return code == SUCCESS.getCode();
    }
    
    /**
     * 判断是否为客户端错误
     * 
     * @param code 状态码
     * @return 是否为客户端错误
     */
    public static boolean isClientError(int code) {
        return code >= 400 && code < 500;
    }
    
    /**
     * 判断是否为服务器错误
     * 
     * @param code 状态码
     * @return 是否为服务器错误
     */
    public static boolean isServerError(int code) {
        return code >= 500 && code < 600;
    }
    
    /**
     * 判断是否为业务错误
     * 
     * @param code 状态码
     * @return 是否为业务错误
     */
    public static boolean isBusinessError(int code) {
        return code >= 1000;
    }
}
