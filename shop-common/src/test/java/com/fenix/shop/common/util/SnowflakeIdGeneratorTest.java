package com.fenix.shop.common.util;

import com.fenix.shop.common.exception.SnowflakeConfigException;
import com.fenix.shop.common.properties.SnowflakeProperties;
import com.fenix.shop.common.utils.SnowflakeIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 雪花算法ID生成器测试类
 * 功能：全面测试雪花算法ID生成器的各项功能和边界条件
 * 技术：使用JUnit 5测试框架，支持嵌套测试和参数化测试
 * 优势：确保ID生成器的正确性、性能和线程安全性
 * 
 * @author fenix
 * @date 2025-01-15
 * @version 1.0
 */
@DisplayName("雪花算法ID生成器测试")
class SnowflakeIdGeneratorTest {
    
    /**
     * 雪花算法ID生成器实例
     * 功能：测试目标对象
     * 技术：使用默认配置创建生成器实例
     * 优势：便于各个测试方法复用
     */
    private SnowflakeIdGenerator generator;
    
    /**
     * 雪花算法配置属性
     * 功能：提供测试用的配置参数
     * 技术：使用SnowflakeProperties封装配置
     * 优势：便于配置管理和参数调整
     */
    private SnowflakeProperties properties;
    
    /**
     * 测试前置设置
     * 功能：初始化测试环境和测试对象
     * 技术：使用@BeforeEach注解在每个测试方法前执行
     * 优势：确保每个测试的独立性和一致性
     */
    @BeforeEach
    void setUp() {
        // 创建配置属性
        properties = new SnowflakeProperties();
        properties.setMachineId(1L);
        properties.setDatacenterId(1L);
        properties.setEpoch(1704067200000L); // 2024-01-01 00:00:00
        
        // 创建生成器实例
        generator = new SnowflakeIdGenerator(properties);
    }
    
    /**
     * 基础功能测试
     * 功能：测试ID生成器的基本功能
     * 技术：使用嵌套测试组织相关测试用例
     * 优势：便于测试用例的分类和管理
     */
    @Nested
    @DisplayName("基础功能测试")
    class BasicFunctionalityTests {
        
        /**
         * 测试ID生成功能
         * 功能：验证能够正常生成ID
         * 技术：调用nextId方法并验证返回值
         * 优势：确保基本功能正常
         */
        @Test
        @DisplayName("应该能够生成ID")
        void shouldGenerateId() {
            // 生成ID
            long id = generator.nextId();
            
            // 验证ID不为0
            assertNotEquals(0L, id);
            // 验证ID为正数
            assertTrue(id > 0);
        }
        
        /**
         * 测试字符串ID生成功能
         * 功能：验证能够生成字符串格式的ID
         * 技术：调用nextIdAsString方法并验证返回值
         * 优势：确保字符串格式ID的正确性
         */
        @Test
        @DisplayName("应该能够生成字符串格式ID")
        void shouldGenerateStringId() {
            // 生成字符串ID
            String id = generator.nextIdAsString();
            
            // 验证ID不为空
            assertNotNull(id);
            assertFalse(id.isEmpty());
            // 验证ID为数字字符串
            assertTrue(id.matches("\\d+"));
        }
        
        /**
         * 测试ID唯一性
         * 功能：验证生成的ID具有唯一性
         * 技术：生成多个ID并检查重复
         * 优势：确保ID的全局唯一性
         */
        @Test
        @DisplayName("生成的ID应该具有唯一性")
        void shouldGenerateUniqueIds() {
            // 生成ID集合
            Set<Long> ids = new HashSet<>();
            int count = 10000;
            
            // 生成指定数量的ID
            for (int i = 0; i < count; i++) {
                long id = generator.nextId();
                // 验证ID未重复
                assertTrue(ids.add(id), "发现重复ID: " + id);
            }
            
            // 验证生成的ID数量
            assertEquals(count, ids.size());
        }
    }
    
    /**
     * 配置验证测试
     * 功能：测试配置参数的验证逻辑
     * 技术：使用嵌套测试组织配置相关测试
     * 优势：确保配置参数的有效性验证
     */
    @Nested
    @DisplayName("配置验证测试")
    class ConfigurationValidationTests {
        
        /**
         * 测试无效机器ID
         * 功能：验证无效机器ID会抛出异常
         * 技术：使用assertThrows验证异常抛出
         * 优势：确保配置参数的边界检查
         */
        @Test
        @DisplayName("无效机器ID应该抛出异常")
        void shouldThrowExceptionForInvalidMachineId() {
            // 测试负数机器ID
            properties.setMachineId(-1L);
            assertThrows(SnowflakeConfigException.class, () -> {
                new SnowflakeIdGenerator(properties);
            });
            
            // 测试超出范围的机器ID
            properties.setMachineId(1024L);
            assertThrows(SnowflakeConfigException.class, () -> {
                new SnowflakeIdGenerator(properties);
            });
        }
        
        /**
         * 测试无效数据中心ID
         * 功能：验证无效数据中心ID会抛出异常
         * 技术：使用assertThrows验证异常抛出
         * 优势：确保配置参数的边界检查
         */
        @Test
        @DisplayName("无效数据中心ID应该抛出异常")
        void shouldThrowExceptionForInvalidDatacenterId() {
            // 测试负数数据中心ID
            properties.setDatacenterId(-1L);
            assertThrows(SnowflakeConfigException.class, () -> {
                new SnowflakeIdGenerator(properties);
            });
            
            // 测试超出范围的数据中心ID
            properties.setDatacenterId(32L);
            assertThrows(SnowflakeConfigException.class, () -> {
                new SnowflakeIdGenerator(properties);
            });
        }
        
        /**
         * 测试无效起始时间戳
         * 功能：验证无效起始时间戳会抛出异常
         * 技术：使用assertThrows验证异常抛出
         * 优势：确保时间戳参数的有效性
         */
        @Test
        @DisplayName("无效起始时间戳应该抛出异常")
        void shouldThrowExceptionForInvalidEpoch() {
            // 测试未来时间戳
            properties.setEpoch(System.currentTimeMillis() + 86400000L); // 明天
            assertThrows(SnowflakeConfigException.class, () -> {
                new SnowflakeIdGenerator(properties);
            });
        }
    }
    
    /**
     * 并发测试
     * 功能：测试多线程环境下的ID生成
     * 技术：使用线程池和CountDownLatch进行并发测试
     * 优势：确保线程安全性和并发性能
     */
    @Nested
    @DisplayName("并发测试")
    class ConcurrencyTests {
        
        /**
         * 测试多线程ID生成
         * 功能：验证多线程环境下ID的唯一性
         * 技术：使用ExecutorService创建线程池进行并发测试
         * 优势：确保线程安全和ID唯一性
         */
        @Test
        @DisplayName("多线程环境下应该生成唯一ID")
        void shouldGenerateUniqueIdsInMultiThreadedEnvironment() throws InterruptedException {
            // 并发参数设置
            int threadCount = 10;
            int idsPerThread = 1000;
            
            // 创建线程池和同步工具
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch latch = new CountDownLatch(threadCount);
            Set<Long> allIds = new HashSet<>();
            AtomicInteger duplicateCount = new AtomicInteger(0);
            
            // 提交并发任务
            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        Set<Long> threadIds = new HashSet<>();
                        
                        // 每个线程生成指定数量的ID
                        for (int j = 0; j < idsPerThread; j++) {
                            long id = generator.nextId();
                            threadIds.add(id);
                        }
                        
                        // 同步检查ID唯一性
                        synchronized (allIds) {
                            for (Long id : threadIds) {
                                if (!allIds.add(id)) {
                                    duplicateCount.incrementAndGet();
                                }
                            }
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }
            
            // 等待所有线程完成
            latch.await();
            executor.shutdown();
            
            // 验证结果
            assertEquals(0, duplicateCount.get(), "发现重复ID");
            assertEquals(threadCount * idsPerThread, allIds.size());
        }
    }
    
    /**
     * ID解析测试
     * 功能：测试ID的解析功能
     * 技术：使用嵌套测试组织解析相关测试
     * 优势：确保ID解析的正确性
     */
    @Nested
    @DisplayName("ID解析测试")
    class IdParsingTests {
        
        /**
         * 测试时间戳解析
         * 功能：验证能够正确解析ID中的时间戳
         * 技术：生成ID后解析时间戳并验证合理性
         * 优势：确保时间戳解析的准确性
         */
        @Test
        @DisplayName("应该能够正确解析时间戳")
        void shouldParseTimestampCorrectly() {
            // 记录生成前的时间
            long beforeGeneration = System.currentTimeMillis();
            
            // 生成ID
            long id = generator.nextId();
            
            // 记录生成后的时间
            long afterGeneration = System.currentTimeMillis();
            
            // 解析时间戳
            long parsedTimestamp = generator.getTimestamp(id);
            
            // 验证时间戳在合理范围内
            assertTrue(parsedTimestamp >= beforeGeneration);
            assertTrue(parsedTimestamp <= afterGeneration);
        }
        
        /**
         * 测试机器ID解析
         * 功能：验证能够正确解析ID中的机器ID
         * 技术：生成ID后解析机器ID并与配置对比
         * 优势：确保机器ID解析的准确性
         */
        @Test
        @DisplayName("应该能够正确解析机器ID")
        void shouldParseMachineIdCorrectly() {
            // 生成ID
            long id = generator.nextId();
            
            // 解析机器ID
            long parsedMachineId = generator.getMachineId(id);
            
            // 验证机器ID正确
            assertEquals(properties.getMachineId(), parsedMachineId);
        }
        
        /**
         * 测试数据中心ID解析
         * 功能：验证能够正确解析ID中的数据中心ID
         * 技术：生成ID后解析数据中心ID并与配置对比
         * 优势：确保数据中心ID解析的准确性
         */
        @Test
        @DisplayName("应该能够正确解析数据中心ID")
        void shouldParseDatacenterIdCorrectly() {
            // 生成ID
            long id = generator.nextId();
            
            // 解析数据中心ID
            long parsedDatacenterId = generator.getDatacenterId(id);
            
            // 验证数据中心ID正确
            assertEquals(properties.getDatacenterId(), parsedDatacenterId);
        }
        
        /**
         * 测试序列号解析
         * 功能：验证能够正确解析ID中的序列号
         * 技术：生成ID后解析序列号并验证范围
         * 优势：确保序列号解析的准确性
         */
        @Test
        @DisplayName("应该能够正确解析序列号")
        void shouldParseSequenceCorrectly() {
            // 生成ID
            long id = generator.nextId();
            
            // 解析序列号
            long parsedSequence = generator.getSequence(id);
            
            // 验证序列号在有效范围内
            assertTrue(parsedSequence >= 0);
            assertTrue(parsedSequence < 4096); // 2^12
        }
    }
    
    /**
     * 性能测试
     * 功能：测试ID生成器的性能表现
     * 技术：使用时间测量和吞吐量计算
     * 优势：确保性能满足要求
     */
    @Nested
    @DisplayName("性能测试")
    class PerformanceTests {
        
        /**
         * 测试ID生成性能
         * 功能：测量ID生成的吞吐量
         * 技术：生成大量ID并计算耗时
         * 优势：验证性能是否满足业务需求
         */
        @Test
        @DisplayName("ID生成性能应该满足要求")
        void shouldMeetPerformanceRequirements() {
            // 性能测试参数
            int warmupCount = 1000;
            int testCount = 100000;
            
            // 预热
            for (int i = 0; i < warmupCount; i++) {
                generator.nextId();
            }
            
            // 性能测试
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < testCount; i++) {
                generator.nextId();
            }
            long endTime = System.currentTimeMillis();
            
            // 计算性能指标
            long duration = endTime - startTime;
            double throughput = (double) testCount / duration * 1000; // IDs per second
            
            // 验证性能要求（每秒至少10万个ID）
            assertTrue(throughput >= 100000, 
                      String.format("吞吐量不足: %.2f IDs/sec, 期望: >= 100000 IDs/sec", throughput));
            
            System.out.printf("性能测试结果: 生成 %d 个ID 耗时 %d ms, 吞吐量: %.2f IDs/sec%n", 
                             testCount, duration, throughput);
        }
    }
}