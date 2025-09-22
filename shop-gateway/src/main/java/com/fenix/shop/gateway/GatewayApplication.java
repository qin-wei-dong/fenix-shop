package com.fenix.shop.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API网关服务启动类
 * <p>
 * API网关是整个电商平台的统一入口，主要职责包括：
 * 1. 请求路由 - 将前端请求路由到相应的后端微服务
 * 2. 负载均衡 - 在多个服务实例之间分发请求
 * 3. 安全防护 - 统一的认证、授权、限流、防护
 * 4. 协议转换 - 支持不同协议之间的转换
 * 5. 监控日志 - 统一的请求监控和日志记录
 * <p>
 * 技术特性：
 * - 基于Spring Cloud Gateway响应式网关
 * - 支持服务发现和动态路由
 * - 集成限流、熔断、重试等保护机制
 * - 提供统一的跨域、安全、监控能力
 * - 支持自定义过滤器和扩展功能
 *
 * @author fenix
 * @date 2025-06-27
 */
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class,
    WebMvcAutoConfiguration.class,
    SecurityAutoConfiguration.class,
    SecurityFilterAutoConfiguration.class
})
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        System.out.println("API网关服务启动成功！");
    }
}
