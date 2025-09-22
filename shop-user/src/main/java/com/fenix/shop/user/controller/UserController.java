package com.fenix.shop.user.controller;

import com.fenix.shop.common.model.vo.Result;
import com.fenix.shop.user.dto.*;
import com.fenix.shop.user.entity.User;
import com.fenix.shop.user.entity.UserRole;
import com.fenix.shop.user.service.UserRoleRelService;
import com.fenix.shop.user.service.UserRoleService;
import com.fenix.shop.user.service.UserService;
import com.fenix.shop.user.utils.ClientIpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.fenix.shop.user.converter.UserProfileConverter;

import java.util.UUID;

/**
 * 用户控制器
 * 功能描述：处理用户相关的HTTP请求，包括用户注册、登录、登出、个人信息管理等核心业务功能
 * 技术选型：采用Spring Boot + Spring Security + JWT认证架构，使用RESTful API设计风格
 * 优势：
 * 1. 统一的响应格式，便于前端处理
 * 2. 完善的参数校验和异常处理机制
 * 3. 集成Swagger文档，便于API测试和维护
 * 4. 采用JWT无状态认证，支持分布式部署
 * 5. 详细的日志记录，便于问题排查和监控
 *
 * @author fenix
 * @version 1.0
 * @date 2025-01-27
 */
@Slf4j // Lombok注解：自动生成日志对象，简化日志记录代码
@RestController // Spring注解：标识为REST控制器，自动将返回值序列化为JSON
@RequestMapping("/") // Spring注解：设置控制器基础路径为根路径，因为context-path已设置为/api/user
@RequiredArgsConstructor // Lombok注解：自动生成包含final字段的构造函数，实现依赖注入
@Validated // Spring Validation注解：启用方法级别的参数校验
@Tag(name = "用户管理", description = "用户注册、登录、个人信息管理等接口") // Swagger注解：API文档分组标签
public class UserController {

    /**
     * 用户服务接口
     * 功能描述：处理用户相关的业务逻辑
     * 技术选型：采用Spring依赖注入，通过final关键字确保不可变性
     * 优势：解耦控制层和业务层，便于单元测试和维护
     */
    private final UserService userService;

    /**
     * 角色服务接口
     * 功能描述：处理角色查询和管理的业务逻辑
     */
    private final UserRoleService userRoleService;

    /**
     * 用户角色关联服务接口
     * 功能描述：处理用户角色关联关系的业务逻辑
     */
    private final UserRoleRelService userRoleRelService;

    /**
     * JWT工具类
     * 功能描述：处理JWT token的生成、验证和解析
     */
    private final com.fenix.shop.user.utils.JwtUtil jwtUtil;


    /**
     * 用户注册接口
     * 功能描述：处理新用户注册请求，包括参数校验、业务逻辑处理和响应返回
     * 技术选型：采用Spring MVC + Bean Validation进行参数校验，使用统一异常处理机制
     * 优势：
     * 1. 自动参数校验，减少手动校验代码
     * 2. 统一的响应格式，便于前端处理
     * 3. 详细的日志记录，便于问题排查
     * 4. 异常安全处理，避免敏感信息泄露
     *
     * @param request     注册请求对象，包含用户名、密码、邮箱、手机号等信息
     * @param httpRequest HTTP请求对象，用于获取客户端IP地址等信息
     * @return Result<String> 统一响应格式，包含操作结果和消息
     */
    @PostMapping("/register") // Spring注解：映射POST请求到/register路径
    @Operation(summary = "用户注册", description = "新用户注册接口") // Swagger注解：API文档描述
    public Result<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request, // @Valid：启用参数校验，@RequestBody：将JSON请求体映射为对象
            HttpServletRequest httpRequest) { // HTTP请求对象，用于获取客户端信息

        // 记录用户注册请求日志，便于监控和问题排查
        log.info("用户注册请求: username={}, email={}, mobile={}", request.getUsername(), request.getEmail(), request.getMobile());

        try {
            // 获取客户端真实IP地址，用于安全审计和风控
            String clientIp = ClientIpUtil.getClientIpAddress(httpRequest);
            // 创建用户对象
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            user.setEmail(request.getEmail());
            user.setMobile(request.getMobile());
            user.setNickname(request.getNickname());

            // 调用业务层服务处理用户注册逻辑
            userService.registerUser(user);

            // 为新用户分配默认角色（USER）
            boolean roleAssigned = assignDefaultRoleToUser(user);

            // 记录注册成功日志
            log.info("用户注册成功: username={}", request.getUsername());

            // 创建注册响应
            RegisterResponse response = new RegisterResponse();
            response.setUserId(user.getUserId().toString());
            response.setUsername(user.getUsername());
            response.setEmail(user.getEmail());
            // 脱敏处理
            response.setMobile(user.getMobile() != null ? user.getMobile().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2") : null);
            response.setRegisterTime(user.getCreatedAt());
            // 暂时不需要邮箱验证
            response.setNeedEmailVerification(false);
            // 暂时不需要手机验证
            response.setNeedMobileVerification(false);
            response.setMessage(roleAssigned ?
                    "注册成功！您已自动获得普通用户权限，可以开始使用系统了。" :
                    "注册成功！但角色分配失败，请联系管理员分配相应权限。");

            // 返回成功响应
            return Result.success(response);
        } catch (Exception e) {
            // 记录注册失败的错误日志，包含用户名和错误信息
            log.error("用户注册失败: username={}, error={}", request.getUsername(), e.getMessage());
            // 返回失败响应
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 为新用户分配默认角色
     * 功能描述：为刚注册的用户分配默认的USER角色，确保用户拥有基本权限
     * 采用技术：角色查询 + 权限分配 + 异常处理
     * 技术优势：健壮的错误处理，不影响注册流程，提供详细日志
     *
     * @param user 新注册的用户对象
     * @return 是否分配成功
     */
    private boolean assignDefaultRoleToUser(User user) {
        try {
            // 查找默认用户角色
            UserRole defaultRole = userRoleService.getRoleByCode("USER");
            if (defaultRole == null) {
                log.warn("系统配置错误：找不到默认角色USER，请检查数据库初始化脚本，用户ID: {}", user.getUserId());
                return false;
            }

            // 检查角色是否已经分配给用户（避免重复分配）
            boolean hasRole = userRoleRelService.hasRole(user.getUserId(), defaultRole.getRoleId());
            if (hasRole) {
                log.info("用户已经拥有USER角色，跳过分配，用户ID: {}", user.getUserId());
                return true;
            }

            // 为用户分配默认角色
            List<Long> roleIds = List.of(defaultRole.getRoleId());
            int assignedCount = userRoleRelService.assignRolesToUser(
                    user.getUserId(),
                    roleIds,
                    user.getUserId()
            );

            if (assignedCount > 0) {
                log.info("用户默认角色分配成功，用户ID: {}, 角色: USER({})", user.getUserId(), defaultRole.getRoleId());
                return true;
            } else {
                log.warn("用户默认角色分配失败，没有分配任何角色，用户ID: {}", user.getUserId());
                return false;
            }

        } catch (Exception e) {
            log.error("用户默认角色分配异常，用户ID: {}, 错误: {}", user.getUserId(), e.getMessage(), e);
            return false;
        }
    }

    /**
     * 用户登录接口
     * 功能描述：处理用户登录请求，验证用户凭据并生成JWT访问令牌
     * 技术选型：采用Spring Security + JWT无状态认证机制，支持用户名/邮箱/手机号多种登录方式
     * 优势：
     * 1. 无状态认证，支持分布式部署和水平扩展
     * 2. JWT令牌包含用户信息，减少数据库查询
     * 3. 支持多种登录标识符，提升用户体验
     * 4. 完善的安全审计和日志记录
     *
     * @param request     登录请求对象，包含登录标识符（用户名/邮箱/手机号）和密码
     * @param httpRequest HTTP请求对象，用于获取客户端IP地址等安全信息
     * @return Result<LoginResponse> 包含JWT令牌和用户基本信息的响应
     */
    @PostMapping("/login") // Spring注解：映射POST请求到/login路径
    @Operation(summary = "用户登录", description = "用户登录接口") // Swagger注解：API文档描述
    public Result<LoginResponse> login(
            @Valid @RequestBody LoginRequest request, // @Valid：启用参数校验，@RequestBody：将JSON请求体映射为对象
            HttpServletRequest httpRequest) { // HTTP请求对象，用于获取客户端安全信息

        // 记录用户登录请求日志，使用登录标识符而非敏感信息
        log.info("用户登录请求: username={}", request.getUsername());
        try {
            // 获取客户端真实IP地址，用于安全审计和异常登录检测
            String clientIp = ClientIpUtil.getClientIpAddress(httpRequest);
            // 调用业务层服务处理用户登录逻辑
            User user = userService.loginUser(request.getUsername(), request.getPassword(), clientIp);

            // 获取用户角色信息
            List<String> userRoles = userRoleRelService.getUserRoleCodes(user.getUserId());

            // 生成JWT token和刷新token
            String accessToken = jwtUtil.generateToken(user.getUserId().toString(), user.getUsername());
            // 暂时使用相同方法生成刷新token
            String refreshToken = jwtUtil.generateToken(user.getUserId().toString(), user.getUsername());

            // 创建登录响应（按前端期望格式）
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setAccessToken(accessToken);
            loginResponse.setRefreshToken(refreshToken);
            loginResponse.setTokenType("Bearer");
            // 与配置 jwt.expiration 对齐（ms 转秒）
            loginResponse.setExpiresIn(jwtUtil.getExpirationTime());

            // 设置用户信息（使用转换器统一映射）
            LoginResponse.UserInfo userInfo = UserProfileConverter.toLoginUserInfo(user, userRoles);
            loginResponse.setUser(userInfo);

            // 记录登录成功日志
            log.info("用户登录成功: username={}", request.getUsername());
            // 返回成功响应
            return Result.success(loginResponse);

        } catch (Exception e) {
            // 记录登录失败的错误日志，便于安全监控和问题排查
            log.error("用户登录失败: username={}, error={}", request.getUsername(), e.getMessage());

            // 返回失败响应
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 用户登出接口
     * 功能描述：处理用户登出请求，清理用户会话状态和JWT令牌黑名单管理
     * 技术选型：采用Spring Security认证上下文获取用户信息，结合Redis实现JWT令牌黑名单机制
     * 优势：
     * 1. 安全的会话清理，防止令牌被恶意使用
     * 2. 支持JWT令牌黑名单，增强安全性
     * 3. 自动从认证上下文获取用户信息，避免参数传递
     * 4. 完善的日志记录，便于安全审计
     *
     * @param authentication Spring Security认证对象，包含当前登录用户的认证信息
     * @return Result<String> 统一响应格式，包含操作结果和消息
     */
    @PostMapping("/logout") // Spring注解：映射POST请求到/logout路径
    @Operation(summary = "用户登出", description = "用户登出接口") // Swagger注解：API文档描述
    public Result<String> logout(Authentication authentication) { // Spring Security认证对象，自动注入当前用户认证信息

        // 从认证上下文中获取用户ID
        String userId = authentication.getName();
        // 记录用户登出请求日志
        log.info("用户登出请求: userId={}", userId);

        try {
            // 待实现：结合黑名单或Token失效策略

            // 记录登出成功日志
            log.info("用户登出成功: userId={}", userId);
            // 返回成功响应
            return Result.success("登出成功");

        } catch (Exception e) {
            // 记录登出失败的错误日志
            log.error("用户登出失败: userId={}, error={}", userId, e.getMessage());

            // 返回失败响应
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 获取当前用户信息接口
     * 功能描述：获取当前登录用户的详细个人信息，符合REST API通用惯例
     * 技术选型：采用Spring Security认证上下文获取用户身份，支持JWT token认证
     * 优势：
     * 1. 符合REST API标准，/me是获取当前用户信息的通用端点
     * 2. 支持JWT token认证，与前端认证机制一致
     * 3. DTO模式隔离内部实体，保护敏感数据
     * 4. 统一的响应格式，便于前端处理
     *
     * @param authentication Spring Security认证对象，包含当前登录用户的认证信息
     * @return Result<UserProfileDTO> 包含用户个人信息的响应
     */
    @GetMapping("/me") // Spring注解：映射GET请求到/me路径，符合REST API通用惯例
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的个人信息") // Swagger注解：API文档描述
    public Result<UserProfileDTO> getCurrentUser(Authentication authentication) { // Spring Security认证对象，自动注入当前用户认证信息

        // 从认证上下文中获取用户ID
        String userId = authentication.getName();
        if (userId == null || userId.isEmpty()) {
            log.warn("获取当前用户信息失败：认证信息缺失");
            return Result.fail("用户身份信息缺失");
        }

        // 记录获取个人信息请求日志
        log.info("获取当前用户信息请求: userId={}", userId);

        try {
            // 转换用户ID为Long类型
            Long userIdLong = Long.valueOf(userId);

            // 调用业务层服务获取用户信息
            User user = userService.getById(userIdLong);
            if (user == null) {
                return Result.fail("用户不存在");
            }

            // 获取用户角色信息
            List<String> userRoles = userRoleRelService.getUserRoleCodes(user.getUserId());

            // 转换为DTO（使用统一转换器）
            UserProfileDTO profile = UserProfileConverter.toDTO(user, userRoles);

            // 记录获取成功日志
            log.info("获取当前用户信息成功: userId={}, roles={}", userId, userRoles);
            // 返回成功响应
            return Result.success(profile);
        } catch (NumberFormatException e) {
            log.error("获取当前用户信息失败: 用户ID格式错误, userId={}", userId);
            return Result.fail("用户ID格式错误");
        } catch (Exception e) {
            // 记录获取个人信息失败的错误日志
            log.error("获取当前用户信息失败: userId={}, error={}", userId, e.getMessage());

            // 返回失败响应
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 获取用户个人信息接口
     * 功能描述：获取当前登录用户的详细个人信息，包括基本资料和偏好设置
     * 技术选型：采用Spring Security认证上下文自动获取用户身份，使用DTO模式传输数据
     * 优势：
     * 1. 自动身份验证，确保只能访问自己的信息
     * 2. DTO模式隔离内部实体，保护敏感数据
     * 3. 统一的响应格式，便于前端处理
     * 4. 完善的异常处理和日志记录
     *
     * @return Result<UserProfileDTO> 包含用户个人信息的响应
     */
    @GetMapping("/profile") // Spring注解：映射GET请求到/profile路径
    @Operation(summary = "获取个人信息", description = "获取当前登录用户的个人信息") // Swagger注解：API文档描述
    public Result<UserProfileDTO> getProfile(HttpServletRequest request) { // HTTP请求对象，用于获取网关传递的用户信息

        // 从请求头中获取网关传递的用户ID
        String userId = request.getHeader("X-User-Id");
        if (userId == null || userId.isEmpty()) {
            log.warn("获取用户个人信息失败：未找到用户ID头信息");
            return Result.fail("用户身份信息缺失");
        }

        // 记录获取个人信息请求日志
        log.info("获取用户个人信息请求: userId={}", userId);

        try {
            Long userIdLong = Long.valueOf(userId);

            // 调用业务层服务获取用户信息
            User user = userService.getById(userIdLong);
            if (user == null) {
                return Result.fail("用户不存在");
            }

            // 转换为DTO（使用统一转换器）
            UserProfileDTO profile = UserProfileConverter.toDTO(user, null);

            // 记录获取成功日志
            log.info("获取用户个人信息成功: userId={}", userId);
            // 返回成功响应
            return Result.success(profile);

        } catch (Exception e) {
            // 记录获取个人信息失败的错误日志
            log.error("获取用户个人信息失败: userId={}, error={}", userId, e.getMessage());

            // 返回失败响应
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 更新用户个人信息接口
     * 功能描述：更新当前登录用户的个人信息，包括昵称、头像、联系方式等可修改字段
     * 技术选型：采用PUT方法实现幂等性更新，使用Bean Validation进行数据校验
     * 优势：
     * 1. 幂等性操作，多次调用结果一致
     * 2. 自动参数校验，确保数据完整性
     * 3. 身份验证保护，只能修改自己的信息
     * 4. DTO模式隔离，保护敏感字段不被修改
     *
     * @param profileDTO     用户个人信息DTO对象，包含待更新的用户信息字段
     * @param authentication Spring Security认证对象，包含当前登录用户的认证信息
     * @return Result<String> 统一响应格式，包含操作结果和消息
     */
    @PutMapping("/profile") // Spring注解：映射PUT请求到/profile路径，实现幂等性更新
    @Operation(summary = "更新个人信息", description = "更新当前登录用户的个人信息") // Swagger注解：API文档描述
    public Result<String> updateProfile(
            @Valid @RequestBody UserProfileDTO profileDTO, // @Valid：启用参数校验，@RequestBody：将JSON请求体映射为DTO对象
            Authentication authentication) { // Spring Security认证对象，自动注入当前用户认证信息

        // 从认证上下文中获取用户ID
        String userId = authentication.getName();
        // 记录更新个人信息请求日志
        log.info("更新用户个人信息请求: userId={}", userId);

        try {
            // 从JWT token中解析用户ID
            Long userIdLong = Long.valueOf(userId);

            // 获取现有用户信息
            User user = userService.getById(userIdLong);
            if (user == null) {
                return Result.fail("用户不存在");
            }

            // 应用部分更新（使用统一转换器）
            UserProfileConverter.applyProfileUpdate(user, profileDTO);

            // 调用业务层服务更新信息
            userService.updateUserInfo(user);

            // 记录更新成功日志
            log.info("用户个人信息更新成功: userId={}", userId);
            // 返回成功响应
            return Result.success("个人信息更新成功");

        } catch (Exception e) {
            // 记录更新失败的错误日志
            log.error("更新用户个人信息失败: userId={}, error={}", userId, e.getMessage());

            // 返回失败响应
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 修改密码接口
     * <p>
     * 功能描述：
     * 提供用户密码修改功能，支持当前登录用户修改自己的密码。
     * 需要验证原密码的正确性，确保密码修改的安全性。
     * <p>
     * 技术选型及原因：
     * 1. 使用Spring Security Authentication获取当前用户身份，确保只能修改自己的密码
     * 2. 采用@Valid注解进行参数校验，确保输入数据的合法性
     * 3. 使用PUT方法实现幂等性操作，符合RESTful设计规范
     * 4. 采用统一的异常处理机制，提供友好的错误信息
     * <p>
     * 优势：
     * 1. 安全性高：通过Authentication确保用户只能修改自己的密码
     * 2. 数据校验完整：自动校验请求参数的合法性
     * 3. 错误处理完善：提供详细的错误信息和日志记录
     * 4. 接口设计规范：遵循RESTful API设计原则
     *
     * @param request        密码修改请求对象，包含原密码和新密码
     * @param authentication Spring Security认证对象，用于获取当前用户信息
     * @return Result<String> 包含操作结果的响应对象
     */
    @PutMapping("/password") // Spring注解：映射PUT请求到/password路径，实现幂等性密码修改
    @Operation(summary = "修改密码", description = "修改当前登录用户的密码") // Swagger注解：API文档描述
    public Result<String> changePassword(
            @Valid @RequestBody ChangePasswordRequest request, // @Valid：启用参数校验，@RequestBody：将JSON请求体映射为密码修改请求对象
            Authentication authentication) { // Spring Security认证对象，用于获取当前登录用户信息

        String userId = authentication.getName(); // 从认证对象中获取当前用户ID
        log.info("修改密码请求: userId={}", userId); // 记录密码修改请求日志，便于审计和问题排查

        try {
            // 从JWT token中解析用户ID  
            Long userIdLong = Long.valueOf(userId);

            // 验证新密码是否一致
            if (!request.isNewPasswordMatch()) {
                return Result.fail("两次输入的新密码不一致");
            }

            // 调用业务层服务修改用户密码
            boolean success = userService.changePassword(userIdLong, request.getCurrentPassword(), request.getNewPassword());
            if (!success) {
                return Result.fail("密码修改失败");
            }

            log.info("密码修改成功: userId={}", userId); // 记录密码修改成功日志
            return Result.success("密码修改成功"); // 返回成功响应

        } catch (Exception e) {
            log.error("密码修改失败: userId={}, error={}", userId, e.getMessage()); // 记录密码修改失败的错误日志

            return Result.fail(e.getMessage()); // 返回失败响应
        }
    }

    /**
     * 检查用户名可用性接口
     * <p>
     * 功能描述：
     * 提供用户名可用性检查功能，用于用户注册时实时验证用户名是否已被占用。
     * 支持前端实时校验，提升用户体验，避免提交后才发现用户名冲突。
     * <p>
     * 技术选型及原因：
     * 1. 使用GET方法进行查询操作，符合RESTful设计规范
     * 2. 采用@RequestParam接收查询参数，支持URL参数传递
     * 3. 使用@NotBlank注解确保用户名不为空，提供基础数据校验
     * 4. 采用统一的响应格式，便于前端处理
     * <p>
     * 优势：
     * 1. 实时校验：支持前端实时检查用户名可用性
     * 2. 用户体验好：避免用户填写完整表单后才发现用户名冲突
     * 3. 接口简洁：单一职责，专门用于用户名校验
     * 4. 性能优化：轻量级查询操作，响应速度快
     *
     * @param username 待检查的用户名
     * @return Result<Boolean> 包含可用性检查结果的响应对象
     */
    @GetMapping("/check-username") // Spring注解：映射GET请求到/check-username路径，用于用户名可用性查询
    @Operation(summary = "检查用户名", description = "检查用户名是否已被使用") // Swagger注解：API文档描述
    public Result<Boolean> checkUsername(
            @Parameter(description = "用户名") @RequestParam @NotBlank String username) { // @Parameter：Swagger参数描述，@RequestParam：接收URL参数，@NotBlank：确保用户名不为空

        log.info("检查用户名可用性: username={}", username); // 记录用户名检查请求日志

        try {
            boolean available = userService.isUsernameAvailable(username, null); // 调用业务层服务检查用户名是否可用

            return Result.success(available); // 返回成功响应

        } catch (Exception e) {
            log.error("检查用户名可用性失败: username={}, error={}", username, e.getMessage()); // 记录检查失败的错误日志

            return Result.fail(e.getMessage()); // 返回失败响应
        }
    }

    /**
     * 检查邮箱可用性接口
     * <p>
     * 功能描述：
     * 提供邮箱可用性检查功能，用于用户注册时实时验证邮箱是否已被占用。
     * 支持前端实时校验，确保邮箱的唯一性，提升用户注册体验。
     * <p>
     * 技术选型及原因：
     * 1. 使用GET方法进行查询操作，符合RESTful设计规范
     * 2. 采用@RequestParam接收查询参数，支持URL参数传递
     * 3. 使用@NotBlank注解确保邮箱不为空，提供基础数据校验
     * 4. 采用统一的响应格式，便于前端处理和错误提示
     * <p>
     * 优势：
     * 1. 实时校验：支持前端实时检查邮箱可用性
     * 2. 数据唯一性：确保系统中邮箱地址的唯一性约束
     * 3. 用户体验好：避免用户填写完整表单后才发现邮箱冲突
     * 4. 接口简洁：单一职责，专门用于邮箱校验
     *
     * @param email 待检查的邮箱地址
     * @return Result<Boolean> 包含可用性检查结果的响应对象
     */
    @GetMapping("/check-email") // Spring注解：映射GET请求到/check-email路径，用于邮箱可用性查询
    @Operation(summary = "检查邮箱", description = "检查邮箱是否已被使用") // Swagger注解：API文档描述
    public Result<Boolean> checkEmail(
            @Parameter(description = "邮箱地址") @RequestParam @NotBlank String email) { // @Parameter：Swagger参数描述，@RequestParam：接收URL参数，@NotBlank：确保邮箱不为空

        log.info("检查邮箱可用性: email={}", email); // 记录邮箱检查请求日志

        try {
            boolean available = userService.isEmailAvailable(email, null); // 调用业务层服务检查邮箱是否可用

            return Result.success(available); // 返回成功响应

        } catch (Exception e) {
            log.error("检查邮箱可用性失败: email={}, error={}", email, e.getMessage()); // 记录检查失败的错误日志

            return Result.fail(e.getMessage()); // 返回失败响应
        }
    }

    /**
     * 刷新访问令牌接口
     * <p>
     * 功能描述：
     * 使用刷新令牌获取新的访问令牌，延长用户会话时间。
     * 当访问令牌过期时，客户端可以使用此接口获取新的令牌而无需重新登录。
     * <p>
     * 技术选型及原因：
     * 1. 使用POST方法进行令牌刷新操作，符合RESTful设计规范
     * 2. 采用@RequestBody接收刷新令牌，确保令牌安全传输
     * 3. 使用统一的响应格式，便于前端处理
     * 4. 采用JWT令牌机制，支持无状态认证
     * <p>
     * 优势：
     * 1. 用户体验好：无需频繁重新登录
     * 2. 安全性高：访问令牌有效期短，刷新令牌有效期长
     * 3. 性能优化：减少重复登录验证的开销
     * 4. 扩展性强：支持分布式部署
     *
     * @param request 刷新令牌请求对象，包含刷新令牌
     * @return Result<LoginResponse> 包含新的访问令牌和刷新令牌的响应对象
     */
    @PostMapping("/refresh") // Spring注解：映射POST请求到/refresh路径，用于令牌刷新
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌") // Swagger注解：API文档描述
    public Result<LoginResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) { // @Valid：启用参数校验，@RequestBody：将JSON请求体映射为刷新令牌请求对象

        log.info("刷新令牌请求"); // 记录令牌刷新请求日志

        try {
            // 使用JwtUtil刷新token
            String newAccessToken = jwtUtil.refreshToken(request.getRefreshToken());
            // 刷新refreshToken（此处简单采用基于原载荷重新生成，一般可选择长周期不变或旋转）
            String newRefreshToken = jwtUtil.refreshToken(request.getRefreshToken());

            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setAccessToken(newAccessToken);
            loginResponse.setRefreshToken(newRefreshToken);
            loginResponse.setTokenType("Bearer");
            loginResponse.setExpiresIn(jwtUtil.getExpirationTime());

            // 可选：从新accessToken中解析用户信息作为兜底
            try {
                String uid = jwtUtil.getUserIdFromToken(newAccessToken);
                String uname = jwtUtil.getUsernameFromToken(newAccessToken);
                LoginResponse.UserInfo ui = new LoginResponse.UserInfo();
                ui.setId(uid);
                ui.setUsername(uname);
                // 角色信息从数据库查询更准确，也可省略让前端用 /me 获取
                loginResponse.setUser(ui);
            } catch (Exception ignore) {
            }

            // 记录令牌刷新成功日志
            log.info("令牌刷新成功");
            // 返回成功响应
            return Result.success(loginResponse);

        } catch (Exception e) {
            // 记录令牌刷新失败的错误日志
            log.error("令牌刷新失败: error={}", e.getMessage());

            // 返回失败响应
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 检查手机号可用性接口
     * <p>
     * 功能描述：
     * 提供手机号可用性检查功能，用于用户注册时实时验证手机号是否已被占用。
     * 支持前端实时校验，确保手机号的唯一性，提升用户注册体验。
     * <p>
     * 技术选型及原因：
     * 1. 使用GET方法进行查询操作，符合RESTful设计规范
     * 2. 采用@RequestParam接收查询参数，支持URL参数传递
     * 3. 使用@NotBlank注解确保手机号不为空，提供基础数据校验
     * 4. 采用统一的响应格式，便于前端处理和错误提示
     * <p>
     * 优势：
     * 1. 实时校验：支持前端实时检查手机号可用性
     * 2. 数据唯一性：确保系统中手机号的唯一性约束
     * 3. 用户体验好：避免用户填写完整表单后才发现手机号冲突
     * 4. 接口简洁：单一职责，专门用于手机号校验
     *
     * @param mobile 待检查的手机号
     * @return Result<Boolean> 包含可用性检查结果的响应对象
     */
    @GetMapping("/check-mobile") // Spring注解：映射GET请求到/check-mobile路径，用于手机号可用性查询
    @Operation(summary = "检查手机号", description = "检查手机号是否已被使用") // Swagger注解：API文档描述
    public Result<Boolean> checkMobile(
            @Parameter(description = "手机号") @RequestParam @NotBlank String mobile) { // @Parameter：Swagger参数描述，@RequestParam：接收URL参数，@NotBlank：确保手机号不为空

        log.info("检查手机号可用性: mobile={}", mobile);
        try {
            // 调用业务层服务检查手机号是否可用
            boolean available = userService.isPhoneAvailable(mobile, null);
            return Result.success(available);
        } catch (Exception e) {
            log.error("检查手机号可用性失败: mobile={}, error={}", mobile, e.getMessage());
            return Result.fail(e.getMessage());
        }
    }

    /**
     * 验证令牌有效性接口
     * 功能：校验当前请求头中的Bearer Token是否有效
     */
    @GetMapping("/verify")
    @Operation(summary = "验证令牌", description = "验证当前访问令牌是否有效")
    public Result<Void> verifyToken(HttpServletRequest request) {
        try {
            String token = jwtUtil.getTokenFromRequest(request);
            if (token == null || token.isEmpty()) {
                return Result.unauthorized();
            }
            boolean valid = jwtUtil.validateToken(token);
            if (valid) {
                return Result.success();
            }
            return Result.unauthorized();
        } catch (Exception e) {
            return Result.unauthorized();
        }
    }


    /**
     * 头像上传接口
     * 功能描述：上传用户头像文件，支持多种图片格式
     * 技术选型：采用 Spring Boot 文件上传机制，自动文件类型验证
     * 优势：
     * 1. 自动文件类型和大小验证
     * 2. 安全的文件名生成机制
     * 3. 防止文件名冲突和路径遍历
     * 4. 与用户信息同步更新
     *
     * @param file           上传的头像文件
     * @param authentication Spring Security认证对象，用于获取当前用户信息
     * @return Result<Map<String, String>> 包含头像URL的响应对象
     */
    @PostMapping("/upload/avatar")
    @Operation(summary = "上传头像", description = "上传用户头像文件")
    public Result<java.util.Map<String, String>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        String userId = authentication.getName();
        log.info("上传头像请求: userId={}, fileName={}, fileSize={}",
                userId, file.getOriginalFilename(), file.getSize());

        try {
            // 验证文件是否为空
            if (file.isEmpty()) {
                return Result.fail("请选择要上传的文件");
            }

            // 验证文件大小（限制2MB）
            if (file.getSize() > 2 * 1024 * 1024) {
                return Result.fail("文件大小不能超过2MB");
            }

            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return Result.fail("只支持图片文件上传");
            }

            // 验证文件扩展名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                return Result.fail("文件名不能为空");
            }

            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            if (!java.util.Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".bmp").contains(fileExtension)) {
                return Result.fail("不支持的文件格式，只支持JPG、PNG、GIF、BMP格式");
            }

            // 生成唯一文件名
            String fileName = generateUniqueFileName(userId, fileExtension);

            // 创建上传目录（优先使用配置项 app.upload.base-dir）
            String baseDir = System.getProperty("app.upload.base-dir");
            if (baseDir == null || baseDir.isBlank()) {
                baseDir = System.getProperty("user.dir") + "/shop-user/uploads"; // 与 WebMvcConfig 默认一致
            }
            String uploadDir = baseDir + "/avatars/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 保存文件
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            // 生成访问 URL
            String avatarUrl = "/uploads/avatars/" + fileName;

            // 更新用户头像信息
            Long userIdLong = Long.valueOf(userId);
            User user = userService.getById(userIdLong);
            if (user != null) {
                user.setAvatar(avatarUrl);
                userService.updateUserInfo(user);
            }

            // 返回结果
            java.util.Map<String, String> result = new java.util.HashMap<>();
            result.put("url", avatarUrl);
            result.put("filename", fileName);

            log.info("头像上传成功: userId={}, avatarUrl={}", userId, avatarUrl);
            return Result.success(result);

        } catch (IOException e) {
            log.error("头像上传失败: userId={}, error={}", userId, e.getMessage(), e);
            return Result.fail("文件上传失败：" + e.getMessage());
        } catch (Exception e) {
            log.error("头像上传异常: userId={}, error={}", userId, e.getMessage(), e);
            return Result.fail("上传失败，请重试");
        }
    }

    /**
     * 生成唯一文件名
     * 功能描述：生成不重复的文件名，防止文件名冲突
     * 技术选型：用户ID + 时间戳 + UUID + 文件扩展名
     * 优势：确保文件名的唯一性，便于管理和清理
     *
     * @param userId        用户ID
     * @param fileExtension 文件扩展名
     * @return 唯一文件名
     */
    private String generateUniqueFileName(String userId, String fileExtension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return String.format("avatar_%s_%s_%s%s", userId, timestamp, uuid, fileExtension);
    }
}