package com.fenix.shop.gateway.filter;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

/**
 * 认证全局过滤器
 * <p>
 * 对所有请求进行认证检查，白名单内的请求直接放行
 *
 * @author fenix
 * @date 2025-06-27
 */
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 不需要认证的白名单路径
     */
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/api/user/login",                         // 用户登录接口
            "/api/user/register",                      // 用户注册接口
            "/api/user/check-username",                // 检查用户名接口
            "/api/user/check-email",                   // 检查邮箱接口
            "/api/user/refresh",                       // 刷新令牌接口
            "/api/user/verification-code",             // 获取验证码接口
            "/api/user/check-mobile",                  // 检查手机号接口
            "/api/product/api/v1/products",             // 商品列表接口
            "/api/product/api/v1/products/",            // 商品相关接口
            "/api/product/api/v1/products/hot",         // 热门商品接口
            "/api/product/api/v1/products/flash-sale",  // 秒杀商品接口
            "/api/product/api/v1/categories",           // 商品分类接口
            "/api/product/api/v1/categories/main",      // 主分类接口
            "/api/product/api/v1/brands",               // 品牌接口
            "/api/product/api/v1/brands/featured",      // 推荐品牌接口
            "/api/search/products/advanced",            // 高级搜索接口
            "/api/search/products/suggestions",         // 搜索建议接口
            "/actuator/health"                          // 健康检查接口
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // 检查是否在白名单中
        if (isWhiteList(path)) {
            return chain.filter(exchange);
        }

        // 检查认证信息
        String token = getToken(request);
        if (token == null || token.isEmpty()) {
            return unauthorizedResponse(exchange, "未提供认证令牌");
        }

        // 这里可以添加JWT令牌验证逻辑
        // 暂时简单验证token不为空
        if (!isValidToken(token)) {
            return unauthorizedResponse(exchange, "无效的认证令牌");
        }

        // 在请求头中添加用户信息（从token中解析）
        ServerHttpRequest modifiedRequest = request.mutate()
                .header("X-User-Id", getUserIdFromToken(token))
                .header("X-User-Role", getUserRoleFromToken(token))
                .build();

        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    }

    /**
     * 检查路径是否在白名单中
     */
    private boolean isWhiteList(String path) {
        return WHITE_LIST.stream().anyMatch(whitePath -> 
            path.equals(whitePath) || path.startsWith(whitePath));
    }

    /**
     * 从请求中获取token
     */
    private String getToken(ServerHttpRequest request) {
        String authorization = request.getHeaders().getFirst("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        return request.getHeaders().getFirst("token");
    }

    /**
     * 验证token是否有效
     * 这里应该实现真正的JWT验证逻辑
     */
    private boolean isValidToken(String token) {
        // 简单验证，实际应该验证JWT签名、过期时间等
        return token != null && token.length() > 10;
    }

    /**
     * 从token中获取用户ID
     */
    private String getUserIdFromToken(String token) {
        try {
            // 解析JWT token获取用户ID
            if (token == null || token.isEmpty()) {
                System.err.println("[AuthGlobalFilter] Token为空，无法解析用户ID");
                return "1001"; // 默认值
            }
            
            // 如果是JWT token，尝试解析
            if (token.contains(".")) {
                try {
                    String[] parts = token.split("\\.");
                    if (parts.length >= 2) {
                        // 解析payload，处理Base64 URL编码
                        String payloadBase64 = parts[1];
                        // 添加必要的填充字符
                        while (payloadBase64.length() % 4 != 0) {
                            payloadBase64 += "=";
                        }
                        // 替换URL安全字符
                        payloadBase64 = payloadBase64.replace("-", "+").replace("_", "/");
                        
                        String payload = new String(java.util.Base64.getDecoder().decode(payloadBase64));
                        System.out.println("[AuthGlobalFilter] 解析到的JWT payload: " + payload);
                        
                        // 使用更精确的JSON解析，寻找 userId、sub 或 id 字段
                        String userId = extractJsonValue(payload, "userId");
                        if (userId != null) {
                            System.out.println("[AuthGlobalFilter] 从 JWT token 中解析出用户ID(userId): " + userId);
                            return userId;
                        }
                        
                        userId = extractJsonValue(payload, "sub");
                        if (userId != null) {
                            System.out.println("[AuthGlobalFilter] 从 JWT token 中解析出用户ID(sub): " + userId);
                            return userId;
                        }
                        
                        userId = extractJsonValue(payload, "id");
                        if (userId != null) {
                            System.out.println("[AuthGlobalFilter] 从 JWT token 中解析出用户ID(id): " + userId);
                            return userId;
                        }
                        
                        System.err.println("[AuthGlobalFilter] JWT payload中未找到userId、sub或id字段");
                    } else {
                        System.err.println("[AuthGlobalFilter] JWT token格式不正确，部分数量: " + parts.length);
                    }
                } catch (Exception e) {
                    System.err.println("[AuthGlobalFilter] 解析JWT token失败: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.err.println("[AuthGlobalFilter] Token不是有效的JWT格式");
            }
            
            // 如果解析失败，返回默认值
            System.out.println("[AuthGlobalFilter] 无法解析JWT token中的用户ID，使用默认值 1001");
            return "1001";
            
        } catch (Exception e) {
            System.err.println("[AuthGlobalFilter] 获取用户ID失败: " + e.getMessage());
            e.printStackTrace();
            return "1001";
        }
    }
    
    /**
     * 从JSON字符串中提取指定字段的值
     */
    private String extractJsonValue(String json, String key) {
        try {
            String searchKey = "\"" + key + "\":";
            int start = json.indexOf(searchKey);
            if (start == -1) {
                return null;
            }
            
            start += searchKey.length();
            // 跳过空白字符
            while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
                start++;
            }
            
            if (start >= json.length()) {
                return null;
            }
            
            char firstChar = json.charAt(start);
            int end;
            
            if (firstChar == '"') {
                // 字符串值
                start++; // 跳过开始的引号
                end = json.indexOf('"', start);
                if (end == -1) {
                    return null;
                }
                return json.substring(start, end);
            } else {
                // 数字值或其他值
                end = start;
                while (end < json.length()) {
                    char c = json.charAt(end);
                    if (c == ',' || c == '}' || c == ']' || Character.isWhitespace(c)) {
                        break;
                    }
                    end++;
                }
                return json.substring(start, end).trim();
            }
        } catch (Exception e) {
            System.err.println("[AuthGlobalFilter] 解析JSON字段失败: " + key + ", error: " + e.getMessage());
            return null;
        }
    }

    /**
     * 从token中获取用户角色
     */
    private String getUserRoleFromToken(String token) {
        // 这里应该解析JWT获取用户角色
        // 暂时返回模拟值
        return "USER";
    }

    /**
     * 返回未授权响应
     */
    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("message", message);
        result.put("data", null);
        result.put("timestamp", System.currentTimeMillis());

        try {
            String body = objectMapper.writeValueAsString(result);
            DataBuffer buffer = response.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            return response.setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -100; // 优先级较高，在其他过滤器之前执行
    }
}
