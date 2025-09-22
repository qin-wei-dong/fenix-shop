package com.fenix.shop.common.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 数据类型转换工具类
 * 提供统一的数据类型转换和格式化方法
 *
 * @author fenix
 * @date 2025-06-28
 */
public class DataTypeUtils {

    /**
     * 价格精度：2位小数
     */
    public static final int PRICE_SCALE = 2;
    
    /**
     * 价格舍入模式：四舍五入
     */
    public static final RoundingMode PRICE_ROUNDING_MODE = RoundingMode.HALF_UP;
    
    /**
     * ISO日期时间格式
     */
    public static final DateTimeFormatter ISO_DATETIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    /**
     * 标准日期时间格式：yyyy-MM-dd HH:mm:ss
     */
    public static final DateTimeFormatter STANDARD_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 安全转换为BigDecimal价格
     * 
     * @param value 原始值（可能是String、Double、Float、Integer等）
     * @return 标准化的BigDecimal价格，失败时返回null
     */
    public static BigDecimal toBigDecimalPrice(Object value) {
        if (value == null) {
            return null;
        }
        
        try {
            BigDecimal result;
            if (value instanceof BigDecimal) {
                result = (BigDecimal) value;
            } else if (value instanceof String) {
                String str = ((String) value).trim();
                if (str.isEmpty()) {
                    return null;
                }
                result = new BigDecimal(str);
            } else if (value instanceof Number) {
                result = BigDecimal.valueOf(((Number) value).doubleValue());
            } else {
                result = new BigDecimal(value.toString());
            }
            
            // 设置精度和舍入模式
            return result.setScale(PRICE_SCALE, PRICE_ROUNDING_MODE);
            
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 安全转换为Double（用于兼容旧系统）
     * 
     * @param value 原始值
     * @return Double值，失败时返回null
     */
    public static Double toDouble(Object value) {
        if (value == null) {
            return null;
        }
        
        try {
            if (value instanceof Double) {
                return (Double) value;
            } else if (value instanceof Number) {
                return ((Number) value).doubleValue();
            } else if (value instanceof String) {
                String str = ((String) value).trim();
                if (str.isEmpty()) {
                    return null;
                }
                return Double.valueOf(str);
            } else {
                return Double.valueOf(value.toString());
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 安全转换为Float
     * 
     * @param value 原始值
     * @return Float值，失败时返回null
     */
    public static Float toFloat(Object value) {
        if (value == null) {
            return null;
        }
        
        try {
            if (value instanceof Float) {
                return (Float) value;
            } else if (value instanceof Number) {
                return ((Number) value).floatValue();
            } else if (value instanceof String) {
                String str = ((String) value).trim();
                if (str.isEmpty()) {
                    return null;
                }
                return Float.valueOf(str);
            } else {
                return Float.valueOf(value.toString());
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 安全转换为Integer
     * 
     * @param value 原始值
     * @return Integer值，失败时返回null
     */
    public static Integer toInteger(Object value) {
        if (value == null) {
            return null;
        }
        
        try {
            if (value instanceof Integer) {
                return (Integer) value;
            } else if (value instanceof Number) {
                return ((Number) value).intValue();
            } else if (value instanceof String) {
                String str = ((String) value).trim();
                if (str.isEmpty()) {
                    return null;
                }
                return Integer.valueOf(str);
            } else {
                return Integer.valueOf(value.toString());
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 安全转换为Long
     * 
     * @param value 原始值
     * @return Long值，失败时返回null
     */
    public static Long toLong(Object value) {
        if (value == null) {
            return null;
        }
        
        try {
            if (value instanceof Long) {
                return (Long) value;
            } else if (value instanceof Number) {
                return ((Number) value).longValue();
            } else if (value instanceof String) {
                String str = ((String) value).trim();
                if (str.isEmpty()) {
                    return null;
                }
                return Long.valueOf(str);
            } else {
                return Long.valueOf(value.toString());
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 安全转换为LocalDateTime
     * 
     * @param value 原始值（支持String、Long时间戳等）
     * @return LocalDateTime值，失败时返回null
     */
    public static LocalDateTime toLocalDateTime(Object value) {
        if (value == null) {
            return null;
        }
        
        try {
            if (value instanceof LocalDateTime) {
                return (LocalDateTime) value;
            } else if (value instanceof String) {
                String str = ((String) value).trim();
                if (str.isEmpty()) {
                    return null;
                }
                
                // 尝试ISO格式
                try {
                    return LocalDateTime.parse(str, ISO_DATETIME_FORMATTER);
                } catch (DateTimeParseException e1) {
                    // 尝试标准格式
                    try {
                        return LocalDateTime.parse(str, STANDARD_DATETIME_FORMATTER);
                    } catch (DateTimeParseException e2) {
                        // 尝试默认解析
                        return LocalDateTime.parse(str);
                    }
                }
            } else if (value instanceof Long) {
                // 假设是毫秒时间戳
                return LocalDateTime.ofEpochSecond(((Long) value) / 1000, 0, java.time.ZoneOffset.UTC);
            }
        } catch (Exception e) {
            // 忽略转换错误
        }
        
        return null;
    }

    /**
     * 格式化价格为字符串（用于前端显示）
     * 
     * @param price 价格
     * @return 格式化的价格字符串，如 "123.45"
     */
    public static String formatPrice(BigDecimal price) {
        if (price == null) {
            return "0.00";
        }
        return price.setScale(PRICE_SCALE, PRICE_ROUNDING_MODE).toString();
    }

    /**
     * 格式化日期时间为ISO字符串
     * 
     * @param dateTime 日期时间
     * @return ISO格式字符串
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(ISO_DATETIME_FORMATTER);
    }

    /**
     * 格式化日期时间为标准字符串
     * 
     * @param dateTime 日期时间
     * @return 标准格式字符串 "yyyy-MM-dd HH:mm:ss"
     */
    public static String formatDateTimeStandard(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(STANDARD_DATETIME_FORMATTER);
    }

    /**
     * 验证价格是否有效
     * 
     * @param price 价格
     * @return 是否有效
     */
    public static boolean isValidPrice(BigDecimal price) {
        return price != null && price.compareTo(BigDecimal.ZERO) >= 0;
    }

    /**
     * 比较两个价格是否相等（考虑精度）
     * 
     * @param price1 价格1
     * @param price2 价格2
     * @return 是否相等
     */
    public static boolean priceEquals(BigDecimal price1, BigDecimal price2) {
        if (price1 == null && price2 == null) {
            return true;
        }
        if (price1 == null || price2 == null) {
            return false;
        }
        return price1.setScale(PRICE_SCALE, PRICE_ROUNDING_MODE)
                .equals(price2.setScale(PRICE_SCALE, PRICE_ROUNDING_MODE));
    }
}
