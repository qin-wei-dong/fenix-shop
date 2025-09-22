package com.fenix.shop.user.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 客户端IP地址工具类
 *
 * @author fenix
 * @date 2025/09/20
 */
public class ClientIpUtil {

    /**
     * 获取客户端真实IP地址工具方法
     * <p>
     * 功能描述：
     * 从HTTP请求中获取客户端的真实IP地址，支持代理和负载均衡环境。
     * 按优先级检查多个HTTP头部字段，确保获取到真实的客户端IP。
     * <p>
     * 技术选型及原因：
     * 1. 优先检查X-Forwarded-For头部，支持多级代理环境
     * 2. 备选检查X-Real-IP头部，支持Nginx等反向代理
     * 3. 最后使用getRemoteAddr()作为兜底方案
     * 4. 采用private修饰符，作为内部工具方法使用
     * <p>
     * 优势：
     * 1. 兼容性强：支持多种代理和负载均衡配置
     * 2. 准确性高：按优先级获取最真实的客户端IP
     * 3. 安全性好：用于日志记录和安全审计
     * 4. 代码复用：作为工具方法供多个接口使用
     *
     * @param request HTTP请求对象，包含客户端请求信息
     * @return String 客户端真实IP地址
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        // 获取X-Forwarded-For头部，包含代理链中的IP地址
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        // 检查X-Forwarded-For是否有效
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            // 返回第一个IP地址（客户端真实IP）
            return xForwardedFor.split(",")[0].trim();
        }

        // 获取X-Real-IP头部，通常由Nginx等反向代理设置
        String xRealIp = request.getHeader("X-Real-IP");
        // 检查X-Real-IP是否有效
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            // 返回X-Real-IP中的IP地址
            return xRealIp;
        }
        // 兜底方案：返回直接连接的客户端IP地址
        return request.getRemoteAddr();
    }
}
