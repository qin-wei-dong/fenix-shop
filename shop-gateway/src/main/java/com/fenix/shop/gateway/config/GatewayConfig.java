package com.fenix.shop.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import reactor.core.publisher.Mono;

/**
 * 网关配置类
 * 提供Spring Cloud Gateway相关的Bean配置，包括限流策略、过滤器等核心功能
 * <p>
 * 作用：配置网关的核心功能组件，实现请求路由、限流、安全控制等功能
 * <p>
 * 为什么这样设计：
 * - 使用@Configuration注解标记为配置类，由Spring容器管理
 * - 提供多种限流策略，满足不同场景的限流需求
 * - 支持IP、用户、API三个维度的限流控制
 * - 考虑代理和负载均衡环境下的真实IP获取
 * - 使用响应式编程模型，提高网关性能
 * <p>
 * 好处：
 * 1. 多维度限流策略，有效防止系统过载
 * 2. 灵活的配置方式，便于根据业务需求调整
 * 3. 支持分布式环境，适应微服务架构
 * 4. 高性能的响应式设计，提升网关吞吐量
 * 5. 完善的IP获取逻辑，确保限流准确性
 *
 * @author fenix
 * @date 2025-06-27
 */
@Configuration // Spring注解，标记为配置类，用于定义Bean和配置信息
public class GatewayConfig {

    /**
     * IP限流Key解析器（主要的KeyResolver）
     * <p>
     * 作用：根据客户端IP地址生成限流Key，实现基于IP的访问频率控制
     * <p>
     * 为什么这样实现：
     * - 使用IP作为限流维度，防止单个IP的恶意请求或爬虫攻击
     * - 调用getClientIp方法获取真实IP，考虑代理环境
     * - 返回Mono对象，符合响应式编程模型
     * - 简单直接的限流策略，适用于大部分场景
     * - 设置为@Primary，作为默认的KeyResolver
     * <p>
     * 好处：
     * 1. 有效防止DDoS攻击和恶意请求
     * 2. 保护后端服务不被单个IP压垮
     * 3. 简单易理解的限流逻辑
     * 4. 支持动态配置限流参数
     *
     * @return KeyResolver IP限流Key解析器实例
     */
    @Bean // Spring注解，将方法返回值注册为Spring容器中的Bean
    @Primary // 标记为主要的KeyResolver Bean
    public KeyResolver ipKeyResolver() {
        // 返回Lambda表达式实现的KeyResolver
        return exchange -> {
            // 获取客户端真实IP地址，考虑代理和负载均衡情况
            String clientIp = getClientIp(exchange);
            // 返回包装在Mono中的IP地址作为限流Key
            return Mono.just(clientIp);
        };
    }

    /**
     * 用户限流Key解析器
     * <p>
     * 根据用户ID进行限流，需要用户登录后才能使用
     *
     * @return KeyResolver
     */
    @Bean("userKeyResolver")
    public KeyResolver userKeyResolver() {
        return exchange -> {
            // 从请求头中获取用户ID
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            if (userId == null || userId.isEmpty()) {
                // 如果没有用户ID，使用IP作为限流key
                return Mono.just(getClientIp(exchange));
            }
            return Mono.just("user:" + userId);
        };
    }

    /**
     * API限流Key解析器
     * <p>
     * 根据API路径进行限流，不同API有不同的限流策略
     *
     * @return KeyResolver
     */
    @Bean("apiKeyResolver")
    public KeyResolver apiKeyResolver() {
        return exchange -> {
            String path = exchange.getRequest().getPath().value();
            return Mono.just("api:" + path);
        };
    }

    /**
     * 获取客户端真实IP地址
     * <p>
     * 考虑代理、负载均衡等情况下的IP获取
     *
     * @param exchange ServerWebExchange
     * @return 客户端IP地址
     */
    private String getClientIp(org.springframework.web.server.ServerWebExchange exchange) {
        String xForwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            // X-Forwarded-For可能包含多个IP，取第一个
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = exchange.getRequest().getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        // 如果都没有，使用远程地址
        return exchange.getRequest().getRemoteAddress() != null ?
                exchange.getRequest().getRemoteAddress().getAddress().getHostAddress() : "unknown";
    }
}
