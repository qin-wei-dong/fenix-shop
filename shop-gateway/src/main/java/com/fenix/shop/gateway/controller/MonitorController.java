package com.fenix.shop.gateway.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 监控面板控制器
 * 提供网关监控数据的REST API接口，为监控面板提供实时的系统状态和性能指标
 * <p>
 * 作用：收集和暴露网关的运行状态、性能指标、请求统计等监控数据
 * <p>
 * 为什么这样设计：
 * - 使用@RestController提供RESTful API接口
 * - 集成Micrometer指标收集，获取准确的性能数据
 * - 使用统一的响应格式，便于前端解析和展示
 * - 提供多个维度的监控数据，满足不同的监控需求
 * - 集成Swagger文档，便于API调试和文档维护
 * <p>
 * 好处：
 * 1. 实时监控数据，及时发现系统问题
 * 2. 标准化的API接口，便于集成各种监控工具
 * 3. 详细的性能指标，支持系统优化决策
 * 4. 灵活的数据格式，适应不同的展示需求
 * 5. 完善的日志记录，便于问题追踪
 *
 * @author fenix
 * @date 2025-06-27
 */
@Slf4j // Lombok注解，自动生成日志记录器，用于记录监控操作和错误信息
@Tag(name = "网关监控接口", description = "提供网关监控数据和统计信息") // Swagger标签，用于API分组
@RestController // Spring注解，标记为REST控制器，自动序列化返回值为JSON
@RequestMapping("/api/monitor") // 设置控制器的基础路径，所有监控API都以此为前缀
public class MonitorController {

    /**
     * 指标注册表
     * Micrometer的核心组件，用于收集和管理各种性能指标
     * 通过依赖注入获取Spring Boot自动配置的实例
     */
    @Autowired // Spring注解，自动注入MeterRegistry实例
    private MeterRegistry meterRegistry;

    /**
     * 获取网关概览信息
     * <p>
     * 作用：提供网关的整体运行状态和关键性能指标概览
     * <p>
     * 为什么这样实现：
     * - 使用GET方法，符合RESTful API规范
     * - 返回Map结构，便于前端灵活处理数据
     * - 集成多种指标数据，提供全面的系统概览
     * - 使用try-catch确保异常情况下的稳定性
     * - 记录操作日志，便于监控API本身的使用情况
     * <p>
     * 好处：
     * 1. 一次请求获取多种关键指标，提高效率
     * 2. 统一的数据格式，便于前端展示
     * 3. 实时数据更新，反映当前系统状态
     * 4. 异常处理确保API稳定性
     *
     * @return Map<String, Object> 包含网关概览信息的数据结构
     */
    @Operation(summary = "获取网关概览", description = "获取网关的整体运行状态和关键指标") // Swagger注解
    @GetMapping("/overview") // Spring注解，映射GET请求到/api/monitor/overview路径
    public Map<String, Object> getOverview() {
        // 记录API调用日志，便于监控API使用情况
        log.info("获取网关监控概览");

        // 创建返回数据的Map容器
        Map<String, Object> overview = new HashMap<>();

        try {
            // 设置基本服务信息
            overview.put("service_name", "API Gateway");    // 服务名称标识
            overview.put("status", "RUNNING");              // 当前运行状态
            overview.put("timestamp", System.currentTimeMillis()); // 数据生成时间戳

            // 创建请求统计数据容器
            Map<String, Object> requests = new HashMap<>();
            // 从指标注册表中查找总请求数计数器
            Counter totalRequests = meterRegistry.find("gateway.requests.total").counter();
            // 设置总请求数，如果计数器不存在则默认为0
            requests.put("total", totalRequests != null ? totalRequests.count() : 0);
            
            Counter totalErrors = meterRegistry.find("gateway.errors.total").counter();
            requests.put("errors", totalErrors != null ? totalErrors.count() : 0);
            
            double errorRate = 0.0;
            if (totalRequests != null && totalRequests.count() > 0) {
                errorRate = (totalErrors != null ? totalErrors.count() : 0) / totalRequests.count();
            }
            requests.put("error_rate", errorRate);
            overview.put("requests", requests);
            
            // 响应时间统计
            Map<String, Object> responseTime = new HashMap<>();
            Timer responseTimer = meterRegistry.find("gateway.response.duration").timer();
            if (responseTimer != null) {
                responseTime.put("count", responseTimer.count());
                responseTime.put("mean_ms", responseTimer.mean(TimeUnit.MILLISECONDS));
                responseTime.put("max_ms", responseTimer.max(TimeUnit.MILLISECONDS));
                responseTime.put("p95_ms", responseTimer.percentile(0.95, TimeUnit.MILLISECONDS));
                responseTime.put("p99_ms", responseTimer.percentile(0.99, TimeUnit.MILLISECONDS));
            } else {
                responseTime.put("count", 0);
                responseTime.put("mean_ms", 0);
                responseTime.put("max_ms", 0);
                responseTime.put("p95_ms", 0);
                responseTime.put("p99_ms", 0);
            }
            overview.put("response_time", responseTime);
            
            // JVM信息
            Map<String, Object> jvm = new HashMap<>();
            Runtime runtime = Runtime.getRuntime();
            jvm.put("max_memory", runtime.maxMemory());
            jvm.put("total_memory", runtime.totalMemory());
            jvm.put("free_memory", runtime.freeMemory());
            jvm.put("used_memory", runtime.totalMemory() - runtime.freeMemory());
            jvm.put("memory_usage", (double)(runtime.totalMemory() - runtime.freeMemory()) / runtime.maxMemory());
            overview.put("jvm", jvm);
            
            return overview;
        } catch (Exception e) {
            log.error("获取网关监控概览失败", e);
            overview.put("error", e.getMessage());
            return overview;
        }
    }

    @Operation(summary = "获取服务统计", description = "获取各个后端服务的请求统计")
    @GetMapping("/services")
    public Map<String, Object> getServiceStats() {
        log.info("获取服务统计信息");
        
        Map<String, Object> serviceStats = new HashMap<>();
        
        try {
            // 获取所有服务的请求统计
            String[] services = {"product", "search", "user", "cart", "order", "payment"};
            
            for (String service : services) {
                Map<String, Object> stats = new HashMap<>();
                
                // 请求数
                Counter requestCounter = meterRegistry.find("gateway.requests.by_service")
                        .tag("service", service).counter();
                stats.put("requests", requestCounter != null ? requestCounter.count() : 0);
                
                // 错误数
                Counter errorCounter = meterRegistry.find("gateway.errors.by_service")
                        .tag("service", service).counter();
                stats.put("errors", errorCounter != null ? errorCounter.count() : 0);
                
                // 错误率
                double requests = requestCounter != null ? requestCounter.count() : 0;
                double errors = errorCounter != null ? errorCounter.count() : 0;
                stats.put("error_rate", requests > 0 ? errors / requests : 0);
                
                // 响应时间
                Timer responseTimer = meterRegistry.find("gateway.response.duration.by_service")
                        .tag("service", service).timer();
                if (responseTimer != null) {
                    stats.put("avg_response_time", responseTimer.mean(TimeUnit.MILLISECONDS));
                    stats.put("p95_response_time", responseTimer.percentile(0.95, TimeUnit.MILLISECONDS));
                } else {
                    stats.put("avg_response_time", 0);
                    stats.put("p95_response_time", 0);
                }
                
                serviceStats.put(service, stats);
            }
            
            return serviceStats;
        } catch (Exception e) {
            log.error("获取服务统计信息失败", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return error;
        }
    }

    @Operation(summary = "获取HTTP状态码统计", description = "获取各种HTTP状态码的分布统计")
    @GetMapping("/status-codes")
    public Map<String, Object> getStatusCodeStats() {
        log.info("获取HTTP状态码统计");
        
        Map<String, Object> statusStats = new HashMap<>();
        
        try {
            // 按状态码类别统计
            Map<String, Double> statusClasses = new HashMap<>();
            statusClasses.put("2xx", getStatusClassCount("2xx"));
            statusClasses.put("3xx", getStatusClassCount("3xx"));
            statusClasses.put("4xx", getStatusClassCount("4xx"));
            statusClasses.put("5xx", getStatusClassCount("5xx"));
            
            statusStats.put("by_class", statusClasses);
            
            // 具体状态码统计
            Map<String, Double> specificCodes = new HashMap<>();
            String[] commonCodes = {"200", "201", "400", "401", "403", "404", "500", "502", "503"};
            
            for (String code : commonCodes) {
                double count = getStatusCodeCount(code);
                if (count > 0) {
                    specificCodes.put(code, count);
                }
            }
            
            statusStats.put("by_code", specificCodes);
            
            return statusStats;
        } catch (Exception e) {
            log.error("获取HTTP状态码统计失败", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return error;
        }
    }

    @Operation(summary = "获取实时指标", description = "获取网关的实时性能指标")
    @GetMapping("/realtime")
    public Map<String, Object> getRealtimeMetrics() {
        log.info("获取实时指标");
        
        Map<String, Object> realtime = new HashMap<>();
        
        try {
            // 当前时间戳
            realtime.put("timestamp", System.currentTimeMillis());
            
            // 总请求数
            Counter totalRequests = meterRegistry.find("gateway.requests.total").counter();
            realtime.put("total_requests", totalRequests != null ? totalRequests.count() : 0);
            
            // 总错误数
            Counter totalErrors = meterRegistry.find("gateway.errors.total").counter();
            realtime.put("total_errors", totalErrors != null ? totalErrors.count() : 0);
            
            // 当前错误率
            double requests = totalRequests != null ? totalRequests.count() : 0;
            double errors = totalErrors != null ? totalErrors.count() : 0;
            realtime.put("current_error_rate", requests > 0 ? errors / requests : 0);
            
            // 平均响应时间
            Timer responseTimer = meterRegistry.find("gateway.response.duration").timer();
            if (responseTimer != null) {
                realtime.put("avg_response_time", responseTimer.mean(TimeUnit.MILLISECONDS));
                realtime.put("request_count", responseTimer.count());
            } else {
                realtime.put("avg_response_time", 0);
                realtime.put("request_count", 0);
            }
            
            // 系统资源
            Runtime runtime = Runtime.getRuntime();
            Map<String, Object> system = new HashMap<>();
            system.put("cpu_cores", runtime.availableProcessors());
            system.put("memory_usage", (double)(runtime.totalMemory() - runtime.freeMemory()) / runtime.maxMemory());
            system.put("used_memory_mb", (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024);
            system.put("max_memory_mb", runtime.maxMemory() / 1024 / 1024);
            realtime.put("system", system);
            
            return realtime;
        } catch (Exception e) {
            log.error("获取实时指标失败", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return error;
        }
    }

    @Operation(summary = "重置监控指标", description = "重置网关的监控指标（仅用于测试）")
    @PostMapping("/reset")
    public Map<String, Object> resetMetrics() {
        log.warn("重置网关监控指标");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 注意：Micrometer的计数器和计时器通常不支持重置
            // 这里只是返回成功响应，实际的重置需要重启应用
            result.put("success", true);
            result.put("message", "监控指标重置请求已接收（需要重启应用才能完全重置）");
            result.put("timestamp", System.currentTimeMillis());
            
            return result;
        } catch (Exception e) {
            log.error("重置监控指标失败", e);
            result.put("success", false);
            result.put("error", e.getMessage());
            return result;
        }
    }

    /**
     * 获取指定状态码类别的计数
     */
    private double getStatusClassCount(String statusClass) {
        return meterRegistry.find("gateway.responses.by_status")
                .tag("status_class", statusClass)
                .counters()
                .stream()
                .mapToDouble(Counter::count)
                .sum();
    }

    /**
     * 获取指定状态码的计数
     */
    private double getStatusCodeCount(String statusCode) {
        return meterRegistry.find("gateway.responses.by_status")
                .tag("status", statusCode)
                .counters()
                .stream()
                .mapToDouble(Counter::count)
                .sum();
    }
}
