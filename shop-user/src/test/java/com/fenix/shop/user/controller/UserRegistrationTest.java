package com.fenix.shop.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fenix.shop.user.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户注册功能测试类
 * 功能描述：测试用户注册接口，特别是默认角色分配功能
 * 采用技术：Spring Boot Test + MockMvc + JUnit 5
 * 技术优势：集成测试，真实环境模拟，完整功能验证
 * 
 * @author fenix
 * @date 2025-01-27
 * @version 1.0
 */
@SpringBootTest
@ActiveProfiles("test")
public class UserRegistrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    /**
     * 初始化MockMvc
     */
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    /**
     * 测试用户注册和默认角色分配
     * 功能描述：验证用户注册成功后自动分配USER角色的功能
     */
    @Test
    public void testUserRegistrationWithDefaultRole() throws Exception {
        setup();
        
        // 准备测试数据
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser" + System.currentTimeMillis());
        registerRequest.setPassword("TestPassword123!");
        registerRequest.setConfirmPassword("TestPassword123!");
        registerRequest.setEmail("test" + System.currentTimeMillis() + "@example.com");
        registerRequest.setMobile("138" + String.format("%08d", (int)(Math.random() * 100000000)));
        registerRequest.setNickname("测试用户");
        registerRequest.setAgreeTerms(true);

        // 执行注册请求
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").exists())
                .andExpect(jsonPath("$.data.username").value(registerRequest.getUsername()))
                .andExpect(jsonPath("$.data.message").exists());

        System.out.println("用户注册测试完成，请查看日志确认角色分配情况");
    }

    /**
     * 测试重复用户名注册
     * 功能描述：验证用户名唯一性校验功能
     */
    @Test
    public void testDuplicateUsernameRegistration() throws Exception {
        setup();
        
        String duplicateUsername = "duplicate_test_user";
        
        // 第一次注册
        RegisterRequest firstRequest = new RegisterRequest();
        firstRequest.setUsername(duplicateUsername);
        firstRequest.setPassword("TestPassword123!");
        firstRequest.setConfirmPassword("TestPassword123!");
        firstRequest.setEmail("first@example.com");
        firstRequest.setMobile("13800000001");
        firstRequest.setNickname("第一个用户");
        firstRequest.setAgreeTerms(true);

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(firstRequest)))
                .andExpect(status().isOk());

        // 第二次注册相同用户名，应该失败
        RegisterRequest secondRequest = new RegisterRequest();
        secondRequest.setUsername(duplicateUsername);
        secondRequest.setPassword("TestPassword456!");
        secondRequest.setConfirmPassword("TestPassword456!");
        secondRequest.setEmail("second@example.com");
        secondRequest.setMobile("13800000002");
        secondRequest.setNickname("第二个用户");
        secondRequest.setAgreeTerms(true);

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(secondRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户名已存在"));

        System.out.println("重复用户名注册测试完成");
    }
}