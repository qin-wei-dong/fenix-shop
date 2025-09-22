package com.fenix.shop.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 用户服务启动类
 * <p>
 * 用户服务是凤凰电商平台的核心基础微服务，负责用户身份管理、认证授权和用户数据服务。
 * 作为整个微服务架构中的基础服务，用户服务为其他业务服务提供统一的用户身份信息和认证能力。
 * <p>
 * 核心功能：
 * 1. 用户账户管理 - 注册、登录、信息维护等用户全生命周期管理
 * 2. 身份认证 - 基于JWT的无状态认证，支持多种登录方式
 * 3. 用户数据服务 - 为其他微服务提供用户基础数据
 * 4. 安全审计 - 记录用户登录历史和关键操作，支持风险控制
 * <p>
 * 技术架构：
 * - 基于Spring Boot构建REST API
 * - 使用Spring Cloud实现微服务架构
 * - 集成Nacos用于服务注册与发现
 * - 采用MyBatis-Plus简化数据访问层
 * - 通过OpenFeign实现微服务间通信
 * - 基于Spring Security和JWT实现身份认证
 * <p>
 * 服务依赖：
 * - MySQL数据库：存储用户数据
 * - Redis：验证码存储、Token管理、缓存等
 * - Nacos：服务注册与配置中心
 * 
 * @Author fenix
 * @Date 2025/5/10
 */
@SpringBootApplication // 标记为Spring Boot应用程序
@EnableTransactionManagement // 启用声明式事务管理
@MapperScan("com.fenix.shop.user.mapper") // 扫描MyBatis Mapper接口
@ComponentScan({"com.fenix.shop.user", "com.fenix.shop.common"}) // 组件扫描范围
@EnableDiscoveryClient // 启用服务注册与发现
@EnableFeignClients// 启用Feign客户端
public class UserApplication {
    /**
     * 应用程序入口方法
     * <p>
     * 启动Spring Boot应用，初始化应用上下文，注册微服务到注册中心，
     * 使用户服务可被其他微服务发现和调用。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}