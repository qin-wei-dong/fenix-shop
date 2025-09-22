package com.fenix.shop.common.service;

import com.fenix.shop.common.utils.SnowflakeIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

/**
 * ID生成器服务测试类
 * 功能：测试ID生成器服务的各项功能
 * 技术：使用JUnit 5和Mockito进行单元测试
 * 优势：确保服务层逻辑的正确性和可靠性
 * 
 * @author fenix
 * @date 2025-01-15
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ID生成器服务测试")
class IdGeneratorServiceTest {
    
    /**
     * 模拟的雪花算法ID生成器
     * 功能：提供测试用的ID生成器模拟对象
     * 技术：使用Mockito创建Mock对象
     * 优势：隔离依赖，专注测试服务层逻辑
     */
    @Mock
    private SnowflakeIdGenerator snowflakeIdGenerator;
    
    /**
     * 被测试的ID生成器服务
     * 功能：测试目标对象
     * 技术：使用@InjectMocks自动注入Mock依赖
     * 优势：自动化依赖注入，简化测试代码
     */
    @InjectMocks
    private IdGeneratorService idGeneratorService;
    
    /**
     * 测试用的ID值
     * 功能：提供测试用的固定ID值
     * 技术：使用常量定义测试数据
     * 优势：便于测试结果验证和维护
     */
    private static final Long TEST_ID = 1234567890123456789L;
    
    /**
     * 测试用的字符串ID值
     * 功能：提供测试用的字符串格式ID
     * 技术：将Long类型ID转换为字符串
     * 优势：便于字符串ID相关测试
     */
    private static final String TEST_STRING_ID = TEST_ID.toString();
    
    /**
     * 初始化测试环境
     * 功能：初始化测试环境
     * 技术：使用@BeforeEach注解在每个测试前执行
     * 优势：确保每个测试的独立性
     */
    @BeforeEach
    void setUp() {
        // 使用lenient模式避免不必要的stubbing错误
        lenient().when(snowflakeIdGenerator.nextId()).thenReturn(TEST_ID);
        lenient().when(snowflakeIdGenerator.nextIdAsString()).thenReturn(TEST_STRING_ID);
    }
    
    /**
     * 测试生成Long类型ID
     * 功能：验证能够正确生成Long类型的分布式ID
     * 技术：使用Mock对象模拟ID生成，验证返回值
     * 优势：确保基本ID生成功能正常
     */
    @Test
    @DisplayName("应该能够生成Long类型ID")
    void shouldGenerateLongId() {
        // 调用服务方法
        Long result = idGeneratorService.generateId();
        
        // 验证结果
        assertNotNull(result);
        assertEquals(TEST_ID, result);
        
        // 验证Mock调用
        verify(snowflakeIdGenerator, times(1)).nextId();
    }
    
    /**
     * 测试生成String类型ID
     * 功能：验证能够正确生成String类型的分布式ID
     * 技术：使用Mock对象模拟ID生成，验证返回值格式
     * 优势：确保字符串ID生成功能正常
     */
    @Test
    @DisplayName("应该能够生成String类型ID")
    void shouldGenerateStringId() {
        // 调用服务方法
        String result = idGeneratorService.generateStringId();
        
        // 验证结果
        assertNotNull(result);
        assertEquals(TEST_STRING_ID, result);
        
        // 验证Mock调用
        verify(snowflakeIdGenerator, times(1)).nextId();
    }
    
    /**
     * 测试批量生成ID
     * 功能：验证能够批量生成指定数量的ID
     * 技术：使用Mock对象模拟多次ID生成，验证数组长度和内容
     * 优势：确保批量操作功能正常
     */
    @Test
    @DisplayName("应该能够批量生成ID")
    void shouldGenerateMultipleIds() {
        // 测试参数
        int count = 5;
        
        // 调用服务方法
        Long[] result = idGeneratorService.generateIds(count);
        
        // 验证结果
        assertNotNull(result);
        assertEquals(count, result.length);
        
        // 验证每个ID都不为空
        for (Long id : result) {
            assertNotNull(id);
            assertEquals(TEST_ID, id);
        }
        
        // 验证Mock调用次数
        verify(snowflakeIdGenerator, times(count)).nextId();
    }
    
    /**
     * 测试批量生成ID的参数验证
     * 功能：验证批量生成ID时的参数有效性检查
     * 技术：使用无效参数调用方法，验证异常抛出
     * 优势：确保参数验证逻辑正确
     */
    @Test
    @DisplayName("批量生成ID时应该验证参数")
    void shouldValidateParametersForBatchGeneration() {
        // 测试count为0的情况
        assertThrows(IllegalArgumentException.class, () -> {
            idGeneratorService.generateIds(0);
        });
        
        // 测试count为负数的情况
        assertThrows(IllegalArgumentException.class, () -> {
            idGeneratorService.generateIds(-1);
        });
        
        // 验证Mock没有被调用
        verify(snowflakeIdGenerator, never()).nextId();
    }
    
    /**
     * 测试解析ID时间戳
     * 功能：验证能够正确解析ID中的时间戳信息
     * 技术：使用Mock对象模拟时间戳解析，验证返回值
     * 优势：确保时间戳解析功能正常
     */
    @Test
    @DisplayName("应该能够解析ID时间戳")
    void shouldParseTimestamp() {
        // 测试数据
        long expectedTimestamp = System.currentTimeMillis();
        when(snowflakeIdGenerator.getTimestamp(TEST_ID)).thenReturn(expectedTimestamp);
        
        // 调用服务方法
        long result = idGeneratorService.getTimestamp(TEST_ID);
        
        // 验证结果
        assertEquals(expectedTimestamp, result);
        
        // 验证Mock调用
        verify(snowflakeIdGenerator, times(1)).getTimestamp(TEST_ID);
    }
    
    /**
     * 测试解析ID机器ID
     * 功能：验证能够正确解析ID中的机器ID信息
     * 技术：使用Mock对象模拟机器ID解析，验证返回值
     * 优势：确保机器ID解析功能正常
     */
    @Test
    @DisplayName("应该能够解析ID机器ID")
    void shouldParseMachineId() {
        // 测试数据
        long expectedMachineId = 1L;
        when(snowflakeIdGenerator.getMachineId(TEST_ID)).thenReturn(expectedMachineId);
        
        // 调用服务方法
        long result = idGeneratorService.getMachineId(TEST_ID);
        
        // 验证结果
        assertEquals(expectedMachineId, result);
        
        // 验证Mock调用
        verify(snowflakeIdGenerator, times(1)).getMachineId(TEST_ID);
    }
    
    /**
     * 测试解析ID数据中心ID
     * 功能：验证能够正确解析ID中的数据中心ID信息
     * 技术：使用Mock对象模拟数据中心ID解析，验证返回值
     * 优势：确保数据中心ID解析功能正常
     */
    @Test
    @DisplayName("应该能够解析ID数据中心ID")
    void shouldParseDatacenterId() {
        // 测试数据
        long expectedDatacenterId = 1L;
        when(snowflakeIdGenerator.getDatacenterId(TEST_ID)).thenReturn(expectedDatacenterId);
        
        // 调用服务方法
        long result = idGeneratorService.getDatacenterId(TEST_ID);
        
        // 验证结果
        assertEquals(expectedDatacenterId, result);
        
        // 验证Mock调用
        verify(snowflakeIdGenerator, times(1)).getDatacenterId(TEST_ID);
    }
    
    /**
     * 测试解析ID序列号
     * 功能：验证能够正确解析ID中的序列号信息
     * 技术：使用Mock对象模拟序列号解析，验证返回值
     * 优势：确保序列号解析功能正常
     */
    @Test
    @DisplayName("应该能够解析ID序列号")
    void shouldParseSequence() {
        // 测试数据
        long expectedSequence = 123L;
        when(snowflakeIdGenerator.getSequence(TEST_ID)).thenReturn(expectedSequence);
        
        // 调用服务方法
        long result = idGeneratorService.getSequence(TEST_ID);
        
        // 验证结果
        assertEquals(expectedSequence, result);
        
        // 验证Mock调用
        verify(snowflakeIdGenerator, times(1)).getSequence(TEST_ID);
    }
    
    /**
     * 测试服务的异常处理
     * 功能：验证当底层组件抛出异常时的处理
     * 技术：使用Mock对象模拟异常抛出，验证异常传播
     * 优势：确保异常处理机制正确
     */
    @Test
    @DisplayName("应该正确处理底层异常")
    void shouldHandleUnderlyingExceptions() {
        // 设置Mock抛出异常
        RuntimeException expectedException = new RuntimeException("Test exception");
        when(snowflakeIdGenerator.nextId()).thenThrow(expectedException);
        
        // 验证异常传播
        RuntimeException actualException = assertThrows(RuntimeException.class, () -> {
            idGeneratorService.generateId();
        });
        
        // 验证异常信息
        assertEquals(expectedException.getMessage(), actualException.getMessage());
        
        // 验证Mock调用
        verify(snowflakeIdGenerator, times(1)).nextId();
    }
}