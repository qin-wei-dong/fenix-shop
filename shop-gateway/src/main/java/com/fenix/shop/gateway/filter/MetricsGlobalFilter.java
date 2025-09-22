package com.fenix.shop.gateway.filter;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 网关监控指标全局过滤器
 * <p>
 * 收集网关的请求指标，包括请求数量、响应时间、错误率等
 *
 * @author fenix
 * @date 2025-06-27
 */
@Slf4j
@Component
public class MetricsGlobalFilter implements GlobalFilter, Ordered {

    @Autowired
    private MeterRegistry meterRegistry;

    // 缓存计数器和计时器，避免重复创建
    private final ConcurrentHashMap<String, Counter> requestCounters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Counter> errorCounters = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Timer> responseTimers = new ConcurrentHashMap<>();

    // 基础指标
    private Counter totalRequestsCounter;
    private Counter totalErrorsCounter;
    private Timer totalResponseTimer;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 初始化基础指标（如果还没有初始化）
        initBaseMetrics();

        // 记录请求开始时间
        long startTime = System.currentTimeMillis();
        Timer.Sample sample = Timer.start(meterRegistry);

        // 获取请求信息
        String path = exchange.getRequest().getPath().value();
        String method = exchange.getRequest().getMethod().name();
        String service = extractServiceName(path);

        // 记录请求开始
        recordRequestStart(service, method, path);

        return chain.filter(exchange)
                .doOnSuccess(aVoid -> {
                    // 请求成功完成
                    HttpStatusCode statusCode = exchange.getResponse().getStatusCode();
                    long duration = System.currentTimeMillis() - startTime;

                    recordRequestComplete(service, method, path, statusCode, duration, sample);
                })
                .doOnError(throwable -> {
                    // 请求发生错误
                    long duration = System.currentTimeMillis() - startTime;
                    
                    recordRequestError(service, method, path, throwable, duration, sample);
                });
    }

    /**
     * 初始化基础指标
     */
    private void initBaseMetrics() {
        if (totalRequestsCounter == null) {
            totalRequestsCounter = Counter.builder("gateway.requests.total")
                    .description("网关总请求数")
                    .register(meterRegistry);
        }
        
        if (totalErrorsCounter == null) {
            totalErrorsCounter = Counter.builder("gateway.errors.total")
                    .description("网关总错误数")
                    .register(meterRegistry);
        }
        
        if (totalResponseTimer == null) {
            totalResponseTimer = Timer.builder("gateway.response.duration")
                    .description("网关响应时间")
                    .register(meterRegistry);
        }
    }

    /**
     * 记录请求开始
     */
    private void recordRequestStart(String service, String method, String path) {
        // 总请求数
        totalRequestsCounter.increment();

        // 按服务分类的请求数
        getRequestCounter(service).increment();

        // 按方法分类的请求数
        getRequestCounterByMethod(method).increment();

        log.debug("记录请求开始: service={}, method={}, path={}", service, method, path);
    }

    /**
     * 记录请求完成
     */
    private void recordRequestComplete(String service, String method, String path,
                                     HttpStatusCode statusCode, long duration, Timer.Sample sample) {
        // 记录响应时间
        sample.stop(totalResponseTimer);
        getResponseTimer(service).record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);

        // 记录状态码
        getStatusCounter(service, statusCode).increment();

        // 如果是错误状态码，记录错误
        if (statusCode != null && statusCode.isError()) {
            recordError(service, method, statusCode.toString());
        }

        log.debug("记录请求完成: service={}, method={}, path={}, status={}, duration={}ms", 
                service, method, path, statusCode, duration);
    }

    /**
     * 记录请求错误
     */
    private void recordRequestError(String service, String method, String path, 
                                  Throwable error, long duration, Timer.Sample sample) {
        // 记录响应时间
        sample.stop(totalResponseTimer);
        getResponseTimer(service).record(duration, java.util.concurrent.TimeUnit.MILLISECONDS);

        // 记录错误
        recordError(service, method, error.getClass().getSimpleName());

        log.warn("记录请求错误: service={}, method={}, path={}, error={}, duration={}ms", 
                service, method, path, error.getMessage(), duration);
    }

    /**
     * 记录错误
     */
    private void recordError(String service, String method, String errorType) {
        totalErrorsCounter.increment();
        getErrorCounter(service).increment();
        getErrorCounterByType(service, errorType).increment();
    }

    /**
     * 从路径中提取服务名
     */
    private String extractServiceName(String path) {
        if (path == null || !path.startsWith("/api/")) {
            return "unknown";
        }
        
        String[] parts = path.split("/");
        if (parts.length >= 3) {
            return parts[2]; // /api/{service}/...
        }
        
        return "unknown";
    }

    /**
     * 获取按服务分类的请求计数器
     */
    private Counter getRequestCounter(String service) {
        return requestCounters.computeIfAbsent(service, s ->
                Counter.builder("gateway.requests.by_service")
                        .description("按服务分类的请求数")
                        .tag("service", s)
                        .register(meterRegistry));
    }

    /**
     * 获取按方法分类的请求计数器
     */
    private Counter getRequestCounterByMethod(String method) {
        return requestCounters.computeIfAbsent("method_" + method, key ->
                Counter.builder("gateway.requests.by_method")
                        .description("按HTTP方法分类的请求数")
                        .tag("method", method)
                        .register(meterRegistry));
    }

    /**
     * 获取按服务分类的错误计数器
     */
    private Counter getErrorCounter(String service) {
        return errorCounters.computeIfAbsent(service, s ->
                Counter.builder("gateway.errors.by_service")
                        .description("按服务分类的错误数")
                        .tag("service", s)
                        .register(meterRegistry));
    }

    /**
     * 获取按错误类型分类的计数器
     */
    private Counter getErrorCounterByType(String service, String errorType) {
        String key = service + "_" + errorType;
        return errorCounters.computeIfAbsent(key, k ->
                Counter.builder("gateway.errors.by_type")
                        .description("按错误类型分类的错误数")
                        .tag("service", service)
                        .tag("error_type", errorType)
                        .register(meterRegistry));
    }

    /**
     * 获取按状态码分类的计数器
     */
    private Counter getStatusCounter(String service, HttpStatusCode status) {
        int statusValue = status.value();
        String key = service + "_" + statusValue;
        return requestCounters.computeIfAbsent(key, k ->
                Counter.builder("gateway.responses.by_status")
                        .description("按状态码分类的响应数")
                        .tag("service", service)
                        .tag("status", String.valueOf(statusValue))
                        .tag("status_class", getStatusClass(statusValue))
                        .register(meterRegistry));
    }

    /**
     * 获取按服务分类的响应时间计时器
     */
    private Timer getResponseTimer(String service) {
        return responseTimers.computeIfAbsent(service, s ->
                Timer.builder("gateway.response.duration.by_service")
                        .description("按服务分类的响应时间")
                        .tag("service", s)
                        .register(meterRegistry));
    }

    /**
     * 获取状态码类别
     */
    private String getStatusClass(int statusCode) {
        if (statusCode >= 100 && statusCode < 200) {
            return "1xx";
        } else if (statusCode >= 200 && statusCode < 300) {
            return "2xx";
        } else if (statusCode >= 300 && statusCode < 400) {
            return "3xx";
        } else if (statusCode >= 400 && statusCode < 500) {
            return "4xx";
        } else if (statusCode >= 500 && statusCode < 600) {
            return "5xx";
        } else {
            return "unknown";
        }
    }

    @Override
    public int getOrder() {
        return -200; // 在其他过滤器之前执行，确保能记录所有请求
    }
}
