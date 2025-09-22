package com.fenix.shop.common.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 统一缓存管理器
 * 提供标准化的缓存操作接口，确保数据一致性
 *
 * @author fenix
 * @date 2025-06-28
 */
@Slf4j
@Component("unifiedCacheManager")
@RequiredArgsConstructor
public class CacheManager {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 缓存键前缀
     */
    public static class CacheKeys {
        public static final String PRODUCT_PREFIX = "product:";
        public static final String USER_PREFIX = "user:";
        public static final String CATEGORY_PREFIX = "category:";
        public static final String BRAND_PREFIX = "brand:";
        public static final String SEARCH_PREFIX = "search:";
        public static final String CART_PREFIX = "cart:";
        public static final String ORDER_PREFIX = "order:";
        public static final String SESSION_PREFIX = "session:";
        public static final String LOCK_PREFIX = "lock:";
        public static final String COUNTER_PREFIX = "counter:";
    }

    /**
     * 缓存过期时间配置
     */
    public static class CacheExpiration {
        public static final Duration PRODUCT_DETAIL = Duration.ofHours(2);      // 商品详情：2小时
        public static final Duration PRODUCT_LIST = Duration.ofMinutes(30);     // 商品列表：30分钟
        public static final Duration USER_INFO = Duration.ofHours(1);           // 用户信息：1小时
        public static final Duration CATEGORY_TREE = Duration.ofHours(6);       // 分类树：6小时
        public static final Duration BRAND_LIST = Duration.ofHours(4);          // 品牌列表：4小时
        public static final Duration SEARCH_RESULT = Duration.ofMinutes(15);    // 搜索结果：15分钟
        public static final Duration CART_DATA = Duration.ofDays(7);            // 购物车：7天
        public static final Duration SESSION_DATA = Duration.ofHours(24);       // 会话：24小时
        public static final Duration HOT_DATA = Duration.ofHours(1);            // 热点数据：1小时
        public static final Duration LOCK_DATA = Duration.ofMinutes(5);         // 分布式锁：5分钟
    }

    /**
     * 设置缓存
     *
     * @param key        缓存键
     * @param value      缓存值
     * @param expiration 过期时间
     */
    public void set(String key, Object value, Duration expiration) {
        try {
            redisTemplate.opsForValue().set(key, value, expiration);
            log.debug("缓存设置成功: key={}, expiration={}", key, expiration);
        } catch (Exception e) {
            log.error("缓存设置失败: key={}", key, e);
        }
    }

    /**
     * 获取缓存
     *
     * @param key   缓存键
     * @param clazz 目标类型
     * @return 缓存值
     */
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return null;
            }
            
            if (clazz.isInstance(value)) {
                return clazz.cast(value);
            }
            
            // 如果是字符串，尝试JSON反序列化
            if (value instanceof String) {
                return objectMapper.readValue((String) value, clazz);
            }
            
            return objectMapper.convertValue(value, clazz);
        } catch (Exception e) {
            log.error("缓存获取失败: key={}", key, e);
            return null;
        }
    }

    /**
     * 删除缓存
     *
     * @param key 缓存键
     */
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
            log.debug("缓存删除成功: key={}", key);
        } catch (Exception e) {
            log.error("缓存删除失败: key={}", key, e);
        }
    }

    /**
     * 批量删除缓存
     *
     * @param pattern 键模式
     */
    public void deleteByPattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (!keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.debug("批量缓存删除成功: pattern={}, count={}", pattern, keys.size());
            }
        } catch (Exception e) {
            log.error("批量缓存删除失败: pattern={}", pattern, e);
        }
    }

    /**
     * 检查缓存是否存在
     *
     * @param key 缓存键
     * @return 是否存在
     */
    public boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("缓存存在性检查失败: key={}", key, e);
            return false;
        }
    }

    /**
     * 设置缓存过期时间
     *
     * @param key        缓存键
     * @param expiration 过期时间
     */
    public void expire(String key, Duration expiration) {
        try {
            redisTemplate.expire(key, expiration);
            log.debug("缓存过期时间设置成功: key={}, expiration={}", key, expiration);
        } catch (Exception e) {
            log.error("缓存过期时间设置失败: key={}", key, e);
        }
    }

    /**
     * 获取缓存剩余过期时间
     *
     * @param key 缓存键
     * @return 剩余时间（秒）
     */
    public long getExpire(String key) {
        try {
            Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            return expire != null ? expire : -1;
        } catch (Exception e) {
            log.error("获取缓存过期时间失败: key={}", key, e);
            return -1;
        }
    }

    /**
     * 原子递增
     *
     * @param key   缓存键
     * @param delta 增量
     * @return 递增后的值
     */
    public long increment(String key, long delta) {
        try {
            Long result = redisTemplate.opsForValue().increment(key, delta);
            return result != null ? result : 0;
        } catch (Exception e) {
            log.error("缓存递增失败: key={}, delta={}", key, delta, e);
            return 0;
        }
    }

    /**
     * 分布式锁
     *
     * @param lockKey    锁键
     * @param lockValue  锁值
     * @param expiration 过期时间
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, String lockValue, Duration expiration) {
        try {
            String fullKey = CacheKeys.LOCK_PREFIX + lockKey;
            Boolean result = redisTemplate.opsForValue().setIfAbsent(fullKey, lockValue, expiration);
            boolean success = Boolean.TRUE.equals(result);
            log.debug("分布式锁获取{}: key={}, value={}", success ? "成功" : "失败", fullKey, lockValue);
            return success;
        } catch (Exception e) {
            log.error("分布式锁获取失败: key={}", lockKey, e);
            return false;
        }
    }

    /**
     * 释放分布式锁
     *
     * @param lockKey   锁键
     * @param lockValue 锁值
     * @return 是否释放成功
     */
    public boolean releaseLock(String lockKey, String lockValue) {
        try {
            String fullKey = CacheKeys.LOCK_PREFIX + lockKey;
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Long result = redisTemplate.execute(
                    (org.springframework.data.redis.core.script.RedisScript<Long>) 
                    org.springframework.data.redis.core.script.RedisScript.of(script, Long.class),
                    List.of(fullKey),
                    lockValue
            );
            boolean success = Long.valueOf(1).equals(result);
            log.debug("分布式锁释放{}: key={}, value={}", success ? "成功" : "失败", fullKey, lockValue);
            return success;
        } catch (Exception e) {
            log.error("分布式锁释放失败: key={}", lockKey, e);
            return false;
        }
    }

    /**
     * 构建缓存键
     *
     * @param prefix 前缀
     * @param parts  键组成部分
     * @return 完整的缓存键
     */
    public static String buildKey(String prefix, Object... parts) {
        StringBuilder sb = new StringBuilder(prefix);
        for (Object part : parts) {
            sb.append(part).append(":");
        }
        // 移除最后一个冒号
        if (!sb.isEmpty() && sb.charAt(sb.length() - 1) == ':') {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 缓存预热
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public void warmUp(String key, Object value) {
        try {
            // 预热数据设置较长的过期时间
            set(key, value, CacheExpiration.HOT_DATA);
            log.info("缓存预热成功: key={}", key);
        } catch (Exception e) {
            log.error("缓存预热失败: key={}", key, e);
        }
    }

    /**
     * 获取缓存统计信息
     *
     * @param pattern 键模式
     * @return 统计信息
     */
    public CacheStats getStats(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            int totalKeys = keys != null ? keys.size() : 0;
            
            long totalMemory = 0;
            int expiredKeys = 0;
            
            if (keys != null) {
                for (String key : keys) {
                    long expire = getExpire(key);
                    if (expire == -2) { // 键不存在
                        expiredKeys++;
                    }
                }
            }
            
            return CacheStats.builder()
                    .pattern(pattern)
                    .totalKeys(totalKeys)
                    .expiredKeys(expiredKeys)
                    .activeKeys(totalKeys - expiredKeys)
                    .totalMemory(totalMemory)
                    .build();
        } catch (Exception e) {
            log.error("获取缓存统计失败: pattern={}", pattern, e);
            return CacheStats.builder().pattern(pattern).build();
        }
    }

    /**
     * 缓存统计信息
     */
    @lombok.Data
    @lombok.Builder
    public static class CacheStats {
        private String pattern;
        private int totalKeys;
        private int activeKeys;
        private int expiredKeys;
        private long totalMemory;
    }
}
