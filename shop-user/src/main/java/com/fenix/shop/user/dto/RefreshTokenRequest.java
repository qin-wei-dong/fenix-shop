package com.fenix.shop.user.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 刷新令牌请求DTO
 * 功能描述：用于接收客户端发送的刷新令牌请求，包含需要刷新的令牌信息
 * 技术选型：采用Lombok简化代码，使用Bean Validation进行参数校验
 * 优势：
 * 1. 自动生成getter/setter方法，减少样板代码
 * 2. 参数校验确保数据完整性和安全性
 * 3. 明确的字段定义，便于API文档生成
 * 4. 符合RESTful API设计规范
 * 
 * @author fenix
 * @date 2025-01-27
 * @version 1.0
 */
@Data // Lombok注解：自动生成getter、setter、toString、equals和hashCode方法
public class RefreshTokenRequest {

    /**
     * 刷新令牌
     * 功能描述：客户端存储的刷新令牌，用于获取新的访问令牌
     * 校验规则：不能为空，确保请求的有效性
     */
    @NotBlank(message = "刷新令牌不能为空") // Bean Validation注解：确保字段不为null且去除空白字符后长度大于0
    private String refreshToken;
}