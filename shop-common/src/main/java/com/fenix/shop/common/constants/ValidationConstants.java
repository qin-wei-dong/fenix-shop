package com.fenix.shop.common.constants;

/**
 * 验证规则常量
 * 定义系统中所有的验证规则，确保前后端一致性
 * 
 * @author fenix
 * @date 2025-06-28
 */
public class ValidationConstants {

    // ========== 用户相关验证规则 ==========
    
    /**
     * 手机号正则表达式
     * 支持中国大陆手机号格式：1[3-9]xxxxxxxxx
     */
    public static final String MOBILE_PATTERN = "^1[3-9]\\d{9}$";
    public static final String MOBILE_MESSAGE = "手机号格式不正确";
    
    /**
     * 邮箱正则表达式
     * 支持标准邮箱格式
     */
    public static final String EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public static final String EMAIL_MESSAGE = "邮箱格式不正确";
    
    /**
     * 密码规则
     * 6-20位，包含字母、数字或特殊字符
     */
    public static final String PASSWORD_PATTERN = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]{6,20}$";
    public static final String PASSWORD_MESSAGE = "密码长度必须为6-20位，可包含字母、数字和特殊字符";
    public static final int PASSWORD_MIN_LENGTH = 6;
    public static final int PASSWORD_MAX_LENGTH = 20;
    
    /**
     * 用户名规则
     * 2-20位，支持中文、英文、数字、下划线
     */
    public static final String USERNAME_PATTERN = "^[\\u4e00-\\u9fa5a-zA-Z0-9_]{2,20}$";
    public static final String USERNAME_MESSAGE = "用户名长度为2-20位，支持中文、英文、数字、下划线";
    public static final int USERNAME_MIN_LENGTH = 2;
    public static final int USERNAME_MAX_LENGTH = 20;
    
    /**
     * 真实姓名规则
     * 2-10位中文或英文
     */
    public static final String REAL_NAME_PATTERN = "^[\\u4e00-\\u9fa5a-zA-Z]{2,10}$";
    public static final String REAL_NAME_MESSAGE = "姓名长度为2-10位，仅支持中文或英文";
    public static final int REAL_NAME_MIN_LENGTH = 2;
    public static final int REAL_NAME_MAX_LENGTH = 10;
    
    // ========== 商品相关验证规则 ==========
    
    /**
     * 商品名称规则
     * 1-100位，不能为空
     */
    public static final int PRODUCT_NAME_MIN_LENGTH = 1;
    public static final int PRODUCT_NAME_MAX_LENGTH = 100;
    public static final String PRODUCT_NAME_MESSAGE = "商品名称长度必须为1-100位";
    
    /**
     * 商品描述规则
     * 最大1000位
     */
    public static final int PRODUCT_DESCRIPTION_MAX_LENGTH = 1000;
    public static final String PRODUCT_DESCRIPTION_MESSAGE = "商品描述不能超过1000字";
    
    /**
     * 商品价格规则
     * 0.01-999999.99
     */
    public static final String PRICE_MIN = "0.01";
    public static final String PRICE_MAX = "999999.99";
    public static final String PRICE_MESSAGE = "价格必须在0.01-999999.99之间";
    public static final int PRICE_SCALE = 2; // 小数位数
    
    /**
     * 商品库存规则
     * 0-999999
     */
    public static final int STOCK_MIN = 0;
    public static final int STOCK_MAX = 999999;
    public static final String STOCK_MESSAGE = "库存数量必须在0-999999之间";
    
    /**
     * 商品SKU规则
     * 1-50位，字母数字下划线
     */
    public static final String SKU_PATTERN = "^[a-zA-Z0-9_]{1,50}$";
    public static final String SKU_MESSAGE = "SKU格式不正确，支持1-50位字母、数字、下划线";
    public static final int SKU_MIN_LENGTH = 1;
    public static final int SKU_MAX_LENGTH = 50;
    
    // ========== 订单相关验证规则 ==========
    
    /**
     * 订单号规则
     * 固定格式：年月日时分秒 + 6位随机数
     */
    public static final String ORDER_NO_PATTERN = "^\\d{14}\\d{6}$";
    public static final String ORDER_NO_MESSAGE = "订单号格式不正确";
    public static final int ORDER_NO_LENGTH = 20;
    
    /**
     * 收货地址规则
     * 5-200位
     */
    public static final int ADDRESS_MIN_LENGTH = 5;
    public static final int ADDRESS_MAX_LENGTH = 200;
    public static final String ADDRESS_MESSAGE = "收货地址长度必须为5-200位";
    
    /**
     * 收货人姓名规则
     * 2-20位
     */
    public static final int RECEIVER_NAME_MIN_LENGTH = 2;
    public static final int RECEIVER_NAME_MAX_LENGTH = 20;
    public static final String RECEIVER_NAME_MESSAGE = "收货人姓名长度必须为2-20位";
    
    // ========== 搜索相关验证规则 ==========
    
    /**
     * 搜索关键词规则
     * 1-50位，不能包含特殊字符
     */
    public static final String SEARCH_KEYWORD_PATTERN = "^[\\u4e00-\\u9fa5a-zA-Z0-9\\s]{1,50}$";
    public static final String SEARCH_KEYWORD_MESSAGE = "搜索关键词长度为1-50位，不能包含特殊字符";
    public static final int SEARCH_KEYWORD_MIN_LENGTH = 1;
    public static final int SEARCH_KEYWORD_MAX_LENGTH = 50;
    
    // ========== 分页相关验证规则 ==========
    
    /**
     * 分页参数规则
     */
    public static final int PAGE_NUM_MIN = 1;
    public static final int PAGE_NUM_MAX = 10000;
    public static final String PAGE_NUM_MESSAGE = "页码必须在1-10000之间";
    
    public static final int PAGE_SIZE_MIN = 1;
    public static final int PAGE_SIZE_MAX = 100;
    public static final String PAGE_SIZE_MESSAGE = "每页大小必须在1-100之间";
    
    // ========== 文件上传相关验证规则 ==========
    
    /**
     * 图片文件规则
     */
    public static final String[] IMAGE_EXTENSIONS = {"jpg", "jpeg", "png", "gif", "webp"};
    public static final long IMAGE_MAX_SIZE = 5 * 1024 * 1024; // 5MB
    public static final String IMAGE_SIZE_MESSAGE = "图片大小不能超过5MB";
    public static final String IMAGE_FORMAT_MESSAGE = "图片格式仅支持jpg、jpeg、png、gif、webp";
    
    /**
     * 文档文件规则
     */
    public static final String[] DOCUMENT_EXTENSIONS = {"pdf", "doc", "docx", "xls", "xlsx", "txt"};
    public static final long DOCUMENT_MAX_SIZE = 10 * 1024 * 1024; // 10MB
    public static final String DOCUMENT_SIZE_MESSAGE = "文档大小不能超过10MB";
    public static final String DOCUMENT_FORMAT_MESSAGE = "文档格式仅支持pdf、doc、docx、xls、xlsx、txt";
    
    // ========== 评论相关验证规则 ==========
    
    /**
     * 评论内容规则
     * 5-500位
     */
    public static final int COMMENT_MIN_LENGTH = 5;
    public static final int COMMENT_MAX_LENGTH = 500;
    public static final String COMMENT_MESSAGE = "评论内容长度必须为5-500位";
    
    /**
     * 评分规则
     * 1-5分
     */
    public static final int RATING_MIN = 1;
    public static final int RATING_MAX = 5;
    public static final String RATING_MESSAGE = "评分必须在1-5分之间";
    
    // ========== 优惠券相关验证规则 ==========
    
    /**
     * 优惠券码规则
     * 6-20位，字母数字
     */
    public static final String COUPON_CODE_PATTERN = "^[A-Z0-9]{6,20}$";
    public static final String COUPON_CODE_MESSAGE = "优惠券码格式不正确，支持6-20位大写字母和数字";
    public static final int COUPON_CODE_MIN_LENGTH = 6;
    public static final int COUPON_CODE_MAX_LENGTH = 20;
    
    // ========== 通用验证规则 ==========
    
    /**
     * ID规则
     * 正整数
     */
    public static final String ID_PATTERN = "^[1-9]\\d*$";
    public static final String ID_MESSAGE = "ID必须为正整数";
    
    /**
     * 状态码规则
     * 大写字母和下划线
     */
    public static final String STATUS_PATTERN = "^[A-Z_]+$";
    public static final String STATUS_MESSAGE = "状态码格式不正确";
    
    /**
     * 排序字段规则
     * 字母、数字、下划线
     */
    public static final String SORT_FIELD_PATTERN = "^[a-zA-Z0-9_]+$";
    public static final String SORT_FIELD_MESSAGE = "排序字段格式不正确";
    
    /**
     * 排序方向规则
     */
    public static final String[] SORT_ORDERS = {"ASC", "DESC"};
    public static final String SORT_ORDER_MESSAGE = "排序方向只能是ASC或DESC";
    
    // ========== 验证工具方法 ==========
    
    /**
     * 验证手机号
     */
    public static boolean isValidMobile(String mobile) {
        return mobile != null && mobile.matches(MOBILE_PATTERN);
    }
    
    /**
     * 验证邮箱
     */
    public static boolean isValidEmail(String email) {
        return email != null && email.matches(EMAIL_PATTERN);
    }
    
    /**
     * 验证密码强度
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.matches(PASSWORD_PATTERN);
    }
    
    /**
     * 验证用户名
     */
    public static boolean isValidUsername(String username) {
        return username != null && username.matches(USERNAME_PATTERN);
    }
    
    /**
     * 验证价格
     */
    public static boolean isValidPrice(java.math.BigDecimal price) {
        if (price == null) return false;
        java.math.BigDecimal min = new java.math.BigDecimal(PRICE_MIN);
        java.math.BigDecimal max = new java.math.BigDecimal(PRICE_MAX);
        return price.compareTo(min) >= 0 && price.compareTo(max) <= 0;
    }
    
    /**
     * 验证库存
     */
    public static boolean isValidStock(Integer stock) {
        return stock != null && stock >= STOCK_MIN && stock <= STOCK_MAX;
    }
    
    /**
     * 验证分页参数
     */
    public static boolean isValidPageNum(Integer pageNum) {
        return pageNum != null && pageNum >= PAGE_NUM_MIN && pageNum <= PAGE_NUM_MAX;
    }
    
    public static boolean isValidPageSize(Integer pageSize) {
        return pageSize != null && pageSize >= PAGE_SIZE_MIN && pageSize <= PAGE_SIZE_MAX;
    }
    
    /**
     * 验证文件扩展名
     */
    public static boolean isValidImageExtension(String extension) {
        if (extension == null) return false;
        String lowerExt = extension.toLowerCase();
        for (String validExt : IMAGE_EXTENSIONS) {
            if (validExt.equals(lowerExt)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isValidDocumentExtension(String extension) {
        if (extension == null) return false;
        String lowerExt = extension.toLowerCase();
        for (String validExt : DOCUMENT_EXTENSIONS) {
            if (validExt.equals(lowerExt)) {
                return true;
            }
        }
        return false;
    }
}
