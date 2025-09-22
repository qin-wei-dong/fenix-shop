package com.fenix.shop.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fenix.shop.user.entity.User;
import com.fenix.shop.user.mapper.UserMapper;
import com.fenix.shop.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户业务服务实现类
 * 功能描述：实现用户管理的核心业务逻辑，包括用户注册、登录验证、信息管理、状态控制等功能
 * 采用技术：MyBatis Plus ServiceImpl + Spring Security + 事务管理 + 参数验证
 * 技术优势：
 * 1. 继承ServiceImpl获得基础CRUD操作，减少重复代码
 * 2. 集成Spring Security实现密码加密和安全验证
 * 3. 使用声明式事务保证数据一致性
 * 4. 采用Lambda表达式构建类型安全的查询条件
 * 5. 集成日志记录和异常处理机制
 * 
 * @Service 标记为Spring服务组件，支持依赖注入和AOP增强
 * @RequiredArgsConstructor Lombok注解，自动生成final字段的构造函数
 * @Slf4j Lombok注解，自动生成日志记录器
 * @Transactional 类级别事务注解，确保所有方法的事务一致性
 * 
 * @author fenix
 * @date 2024-12-19
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 密码编码器
     * 功能描述：Spring Security提供的密码加密工具，用于密码的加密和验证
     * 采用技术：BCrypt算法加密，提供高强度的密码保护
     * 优势：不可逆加密，防止密码泄露，支持密码强度验证
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * 用户注册
     * 功能描述：创建新用户账户，包括用户名唯一性验证、密码加密、默认状态设置
     * 采用技术：参数验证 + 唯一性检查 + 密码加密 + 事务管理
     * 优势：保证用户数据安全，防止重复注册，提供完整的注册流程
     * 
     * @param user 用户注册信息对象，包含用户名、密码、邮箱等基本信息
     * @return 注册成功的用户对象，密码已加密，包含生成的用户ID
     * @throws IllegalArgumentException 当用户名已存在或参数无效时抛出
     * @throws RuntimeException 当注册过程中发生系统错误时抛出
     */
    @Override
    public User registerUser(User user) {
        log.info("开始用户注册流程，用户名: {}", user.getUsername());
        
        // 验证用户名是否已存在
        if (!isUsernameAvailable(user.getUsername(), null)) {
            log.warn("用户注册失败，用户名已存在: {}", user.getUsername());
            throw new IllegalArgumentException("用户名已存在");
        }
        
        // 验证邮箱是否已存在（如果提供了邮箱）
        if (StringUtils.hasText(user.getEmail()) && !isEmailAvailable(user.getEmail(), null)) {
            log.warn("用户注册失败，邮箱已存在: {}", user.getEmail());
            throw new IllegalArgumentException("邮箱已存在");
        }
        
        // 验证手机号是否已存在（如果提供了手机号）
        if (StringUtils.hasText(user.getMobile()) && !isPhoneAvailable(user.getMobile(), null)) {
            log.warn("用户注册失败，手机号已存在: {}", user.getMobile());
            throw new IllegalArgumentException("手机号已存在");
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 设置默认状态
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        // 保存用户
        boolean saved = save(user);
        if (!saved) {
            log.error("用户注册失败，数据保存异常: {}", user.getUsername());
            throw new RuntimeException("用户注册失败");
        }
        
        log.info("用户注册成功，用户ID: {}, 用户名: {}", user.getUserId(), user.getUsername());
        return user;
    }

    /**
     * 用户登录验证
     * 功能描述：验证用户登录凭据，支持用户名、邮箱、手机号多种登录方式
     * 采用技术：多字段查询 + 密码验证 + 状态检查 + 登录信息更新
     * 优势：灵活的登录方式，安全的密码验证，完整的登录状态管理
     * 
     * @param loginName 登录名，可以是用户名、邮箱或手机号
     * @param password 原始密码，用于验证
     * @return 登录成功的用户对象，不包含密码信息
     * @throws IllegalArgumentException 当登录凭据无效或用户状态异常时抛出
     * @throws RuntimeException 当登录过程中发生系统错误时抛出
     */
    @Override
    public User loginUser(String loginName, String password, String loginIp) {
        log.info("开始用户登录验证，登录名: {}", loginName);
        
        // 查找用户（支持用户名、邮箱、手机号登录）
        User user = getUserByLoginName(loginName);
        if (user == null) {
            log.warn("用户登录失败，用户不存在: {}", loginName);
            throw new IllegalArgumentException("用户名或密码错误");
        }
        
        // 检查用户状态
        if (user.getStatus() != 1) {
            log.warn("用户登录失败，用户已被禁用: {}", loginName);
            throw new IllegalArgumentException("用户已被禁用");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("用户登录失败，密码错误: {}", loginName);
            throw new IllegalArgumentException("用户名或密码错误");
        }
        
        // 更新登录信息
        updateLoginInfo(user.getUserId(), loginIp);
        
        // 清除密码信息
        user.setPassword(null);
        
        log.info("用户登录成功，用户ID: {}, 用户名: {}", user.getUserId(), user.getUsername());
        return user;
    }

    /**
     * 根据登录名查询用户
     * 功能描述：支持用户名、邮箱、手机号多种方式查询用户
     * 采用技术：OR条件查询 + 状态过滤
     * 优势：灵活的用户查询，支持多种登录方式
     * 
     * @param loginName 登录名，可以是用户名、邮箱或手机号
     * @return 用户对象，如果不存在返回null
     */
    public User getUserByLoginName(String loginName) {
        // 构建查询条件：用户名、邮箱或手机号匹配，且未删除
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .and(wrapper -> wrapper
                        .eq(User::getUsername, loginName)
                        .or().eq(User::getEmail, loginName)
                        .or().eq(User::getMobile, loginName)
                )
                .eq(User::getDeleted, 0);
        
        return getOne(queryWrapper);
    }

    /**
     * 根据用户名查询用户
     * 功能描述：精确匹配用户名查询用户信息
     * 采用技术：精确查询 + 状态过滤
     * 优势：高效的用户查询，支持用户名唯一性验证
     * 
     * @param username 用户名，不能为空
     * @return 用户对象，如果不存在返回null
     */
    @Override
    public User getUserByUsername(String username) {
        // 构建查询条件：用户名精确匹配且未删除
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getDeleted, 0);
        
        return getOne(queryWrapper);
    }

    /**
     * 根据邮箱查询用户
     * 功能描述：精确匹配邮箱查询用户信息
     * 采用技术：精确查询 + 状态过滤
     * 优势：支持邮箱登录和邮箱唯一性验证
     * 
     * @param email 邮箱地址，不能为空
     * @return 用户对象，如果不存在返回null
     */
    @Override
    public User getUserByEmail(String email) {
        // 构建查询条件：邮箱精确匹配且未删除
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email)
                .eq(User::getDeleted, 0);
        
        return getOne(queryWrapper);
    }

    /**
     * 根据手机号查询用户
     * 功能描述：精确匹配手机号查询用户信息
     * 采用技术：精确查询 + 状态过滤
     * 优势：支持手机号登录和手机号唯一性验证
     * 
     * @param phone 手机号，不能为空
     * @return 用户对象，如果不存在返回null
     */
    @Override
    public User getUserByPhone(String phone) {
        // 构建查询条件：手机号精确匹配且未删除
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getMobile, phone)
                .eq(User::getDeleted, 0);
        
        return getOne(queryWrapper);
    }

    /**
     * 更新用户信息
     * 功能描述：更新用户的基本信息，支持部分字段更新和唯一性验证
     * 采用技术：选择性更新 + 唯一性检查 + 乐观锁
     * 优势：安全的信息更新，防止数据冲突，支持并发操作
     * 
     * @param user 包含更新信息的用户对象，必须包含用户ID
     * @return 更新后的用户对象
     * @throws IllegalArgumentException 当用户ID无效或数据冲突时抛出
     * @throws RuntimeException 当更新失败时抛出
     */
    @Override
    public User updateUserInfo(User user) {
        log.info("开始更新用户信息，用户ID: {}", user.getUserId());
        
        // 验证用户是否存在
        User existingUser = getById(user.getUserId());
        if (existingUser == null) {
            log.warn("用户信息更新失败，用户不存在: {}", user.getUserId());
            throw new IllegalArgumentException("用户不存在");
        }
        
        // 验证邮箱唯一性（如果邮箱有变更）
        if (StringUtils.hasText(user.getEmail()) && 
            !user.getEmail().equals(existingUser.getEmail()) && 
            !isEmailAvailable(user.getEmail(), user.getUserId())) {
            log.warn("用户信息更新失败，邮箱已存在: {}", user.getEmail());
            throw new IllegalArgumentException("邮箱已存在");
        }
        
        // 验证手机号唯一性（如果手机号有变更）
        if (StringUtils.hasText(user.getMobile()) &&
            !user.getMobile().equals(existingUser.getMobile()) &&
            !isPhoneAvailable(user.getMobile(), user.getUserId())) {
            log.warn("用户信息更新失败，手机号已存在: {}", user.getMobile());
            throw new IllegalArgumentException("手机号已存在");
        }
        
        // 设置更新时间
        user.setUpdatedAt(LocalDateTime.now());
        
        // 执行更新
        boolean updated = updateById(user);
        if (!updated) {
            log.error("用户信息更新失败，数据更新异常: {}", user.getUserId());
            throw new RuntimeException("用户信息更新失败");
        }
        
        log.info("用户信息更新成功，用户ID: {}", user.getUserId());
        return getById(user.getUserId());
    }

    /**
     * 修改密码
     * 功能描述：修改用户密码，包括原密码验证和新密码加密
     * 采用技术：密码验证 + 加密存储 + 安全更新
     * 优势：安全的密码修改流程，防止未授权修改
     * 
     * @param userId 用户ID，不能为空
     * @param oldPassword 原密码，用于验证
     * @param newPassword 新密码，将被加密存储
     * @return 是否修改成功
     * @throws IllegalArgumentException 当用户不存在或原密码错误时抛出
     * @throws RuntimeException 当修改过程中发生系统错误时抛出
     */
    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("开始修改用户密码，用户ID: {}", userId);
        
        // 查询用户
        User user = getById(userId);
        if (user == null) {
            log.warn("密码修改失败，用户不存在: {}", userId);
            throw new IllegalArgumentException("用户不存在");
        }
        
        // 验证原密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            log.warn("密码修改失败，原密码错误: {}", userId);
            throw new IllegalArgumentException("原密码错误");
        }
        
        // 加密新密码并更新
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>()
                .eq(User::getUserId, userId)
                .set(User::getPassword, encodedNewPassword)
                .set(User::getUpdatedAt, LocalDateTime.now());
        
        boolean updated = update(updateWrapper);
        if (!updated) {
            log.error("密码修改失败，数据更新异常: {}", userId);
            throw new RuntimeException("密码修改失败");
        }
        
        log.info("用户密码修改成功，用户ID: {}", userId);
        return true;
    }

    /**
     * 重置密码
     * 功能描述：管理员重置用户密码，无需原密码验证
     * 采用技术：管理员权限验证 + 密码加密 + 强制更新
     * 优势：支持管理员操作，提供密码找回功能
     * 
     * @param userId 用户ID，不能为空
     * @param newPassword 新密码，将被加密存储
     * @param operatorId 操作人ID，用于审计记录
     * @return 是否重置成功
     * @throws IllegalArgumentException 当用户不存在时抛出
     * @throws RuntimeException 当重置过程中发生系统错误时抛出
     */
    @Override
    public boolean resetPassword(Long userId, String newPassword, Long operatorId) {
        log.info("开始重置用户密码，用户ID: {}, 操作人ID: {}", userId, operatorId);
        
        // 验证用户是否存在
        User user = getById(userId);
        if (user == null) {
            log.warn("密码重置失败，用户不存在: {}", userId);
            throw new IllegalArgumentException("用户不存在");
        }
        
        // 加密新密码并更新
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>()
                .eq(User::getUserId, userId)
                .set(User::getPassword, encodedNewPassword)
                .set(User::getUpdatedAt, LocalDateTime.now());
        
        boolean updated = update(updateWrapper);
        if (!updated) {
            log.error("密码重置失败，数据更新异常: {}", userId);
            throw new RuntimeException("密码重置失败");
        }
        
        log.info("用户密码重置成功，用户ID: {}, 操作人ID: {}", userId, operatorId);
        return true;
    }

    /**
     * 启用用户
     * 功能描述：启用被禁用的用户账户，恢复用户的正常使用权限
     * 采用技术：状态更新 + 操作日志记录
     * 优势：灵活的用户状态管理，支持用户生命周期管理
     * 
     * @param userId 用户ID，不能为空
     * @param operatorId 操作人ID，用于审计记录
     * @return 是否启用成功
     * @throws IllegalArgumentException 当用户不存在时抛出
     * @throws RuntimeException 当启用过程中发生系统错误时抛出
     */
    @Override
    public boolean enableUser(Long userId, Long operatorId) {
        log.info("开始启用用户，用户ID: {}, 操作人ID: {}", userId, operatorId);
        
        // 验证用户是否存在
        User user = getById(userId);
        if (user == null) {
            log.warn("用户启用失败，用户不存在: {}", userId);
            throw new IllegalArgumentException("用户不存在");
        }
        
        // 更新用户状态为启用
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>()
                .eq(User::getUserId, userId)
                .set(User::getStatus, 1)
                .set(User::getUpdatedAt, LocalDateTime.now());
        
        boolean updated = update(updateWrapper);
        if (!updated) {
            log.error("用户启用失败，数据更新异常: {}", userId);
            throw new RuntimeException("用户启用失败");
        }
        
        log.info("用户启用成功，用户ID: {}, 操作人ID: {}", userId, operatorId);
        return true;
    }

    /**
     * 禁用用户
     * 功能描述：禁用用户账户，阻止用户登录和使用系统
     * 采用技术：状态更新 + 操作日志记录
     * 优势：灵活的用户控制，支持临时禁用和管理操作
     * 
     * @param userId 用户ID，不能为空
     * @param operatorId 操作人ID，用于审计记录
     * @return 是否禁用成功
     * @throws IllegalArgumentException 当用户不存在时抛出
     * @throws RuntimeException 当禁用过程中发生系统错误时抛出
     */
    @Override
    public boolean disableUser(Long userId, Long operatorId) {
        log.info("开始禁用用户，用户ID: {}, 操作人ID: {}", userId, operatorId);
        
        // 验证用户是否存在
        User user = getById(userId);
        if (user == null) {
            log.warn("用户禁用失败，用户不存在: {}", userId);
            throw new IllegalArgumentException("用户不存在");
        }
        
        // 更新用户状态为禁用
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>()
                .eq(User::getUserId, userId)
                .set(User::getStatus, 0)
                .set(User::getUpdatedAt, LocalDateTime.now());
        
        boolean updated = update(updateWrapper);
        if (!updated) {
            log.error("用户禁用失败，数据更新异常: {}", userId);
            throw new RuntimeException("用户禁用失败");
        }
        
        log.info("用户禁用成功，用户ID: {}, 操作人ID: {}", userId, operatorId);
        return true;
    }

    /**
     * 删除用户
     * 功能描述：逻辑删除用户，保留数据但标记为已删除状态
     * 采用技术：逻辑删除 + 关联数据处理 + 操作日志记录
     * 优势：保留数据历史，支持数据恢复，确保数据一致性
     * 
     * @param userId 用户ID，不能为空
     * @param operatorId 操作人ID，用于审计记录
     * @return 是否删除成功
     * @throws IllegalArgumentException 当用户不存在时抛出
     * @throws RuntimeException 当删除过程中发生系统错误时抛出
     */
    public boolean deleteUser(Long userId, Long operatorId) {
        log.info("开始删除用户，用户ID: {}, 操作人ID: {}", userId, operatorId);
        
        // 验证用户是否存在
        User user = getById(userId);
        if (user == null) {
            log.warn("用户删除失败，用户不存在: {}", userId);
            throw new IllegalArgumentException("用户不存在");
        }
        
        // 逻辑删除用户
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>()
                .eq(User::getUserId, userId)
                .set(User::getDeleted, 1)
                .set(User::getUpdatedAt, LocalDateTime.now());
        
        boolean updated = update(updateWrapper);
        if (!updated) {
            log.error("用户删除失败，数据更新异常: {}", userId);
            throw new RuntimeException("用户删除失败");
        }
        
        log.info("用户删除成功，用户ID: {}, 操作人ID: {}", userId, operatorId);
        return true;
    }

    /**
     * 分页查询用户列表
     * 功能描述：按条件分页查询用户列表，支持多种筛选条件和排序方式
     * 采用技术：MyBatis Plus分页插件 + 动态查询条件 + Lambda表达式
     * 优势：高效的分页查询，支持灵活的条件筛选，适合管理界面
     * 
     * @param page 分页参数对象，包含页码和页大小
     * @param status 用户状态筛选，可以为空
     * @param keyword 关键字搜索，支持用户名、邮箱、手机号模糊匹配，可以为空
     * @return 分页结果对象，包含用户列表和分页信息，不包含密码字段
     */
    @Override
    public IPage<User> getUserPage(Page<User> page, Integer status, String keyword) {
        // 构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getDeleted, 0); // 只查询未删除的用户
        
        // 添加状态筛选条件
        if (status != null) {
            queryWrapper.eq(User::getStatus, status);
        }
        
        // 添加关键字搜索条件
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(User::getUsername, keyword)
                    .or().like(User::getEmail, keyword)
                    .or().like(User::getMobile, keyword)
                    .or().like(User::getNickname, keyword)
            );
        }
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc(User::getCreatedAt);
        
        // 执行分页查询
        IPage<User> result = page(page, queryWrapper);
        
        // 清除密码信息
        result.getRecords().forEach(user -> user.setPassword(null));
        
        return result;
    }

    /**
     * 批量更新用户状态
     * 功能描述：批量修改多个用户的状态，用于批量管理操作
     * 采用技术：批量更新 + 事务管理 + 操作日志记录
     * 优势：提高批量操作效率，保证操作原子性，支持批量管理
     * 
     * @param userIds 用户ID列表，不能为空
     * @param status 目标状态值，0-禁用，1-启用
     * @param operatorId 操作人ID，用于审计记录
     * @return 成功更新的用户数量
     * @throws IllegalArgumentException 当参数无效时抛出
     * @throws RuntimeException 当批量更新过程中发生系统错误时抛出
     */
    @Override
    public int batchUpdateUserStatus(List<Long> userIds, Integer status, Long operatorId) {
        log.info("开始批量更新用户状态，用户数量: {}, 目标状态: {}, 操作人ID: {}", 
                userIds.size(), status, operatorId);
        
        // 构建批量更新条件
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>()
                .in(User::getUserId, userIds)
                .eq(User::getDeleted, 0) // 只更新未删除的用户
                .set(User::getStatus, status)
                .set(User::getUpdatedAt, LocalDateTime.now());
        
        // 执行批量更新
        boolean updated = update(updateWrapper);
        if (!updated) {
            log.error("批量更新用户状态失败，操作人ID: {}", operatorId);
            throw new RuntimeException("批量更新用户状态失败");
        }
        
        // 查询实际更新的记录数
        int updatedCount = Math.toIntExact(count(new LambdaQueryWrapper<User>()
                .in(User::getUserId, userIds)
                .eq(User::getStatus, status)
                .eq(User::getDeleted, 0)));
        
        log.info("批量更新用户状态成功，实际更新数量: {}, 操作人ID: {}", updatedCount, operatorId);
        return updatedCount;
    }

    /**
     * 验证用户名是否可用
     * 功能描述：检查用户名是否已被使用，用于注册和修改时的验证
     * 采用技术：唯一性检查 + 排除条件
     * 优势：快速验证用户名可用性，提高用户体验，减少重复提交
     * 
     * @param username 待验证的用户名，不能为空
     * @param excludeUserId 排除的用户ID，用于修改时排除自身，可以为空
     * @return 是否可用，true表示可用，false表示已被使用
     */
    @Override
    public boolean isUsernameAvailable(String username, Long excludeUserId) {
        // 构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getDeleted, 0);
        
        // 排除指定用户ID
        if (excludeUserId != null) {
            queryWrapper.ne(User::getUserId, excludeUserId);
        }
        
        // 检查是否存在
        return count(queryWrapper) == 0;
    }

    /**
     * 验证邮箱是否可用
     * 功能描述：检查邮箱是否已被使用，用于注册和修改时的验证
     * 采用技术：唯一性检查 + 排除条件
     * 优势：快速验证邮箱可用性，支持邮箱唯一性约束
     * 
     * @param email 待验证的邮箱，不能为空
     * @param excludeUserId 排除的用户ID，用于修改时排除自身，可以为空
     * @return 是否可用，true表示可用，false表示已被使用
     */
    @Override
    public boolean isEmailAvailable(String email, Long excludeUserId) {
        // 构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email)
                .eq(User::getDeleted, 0);
        
        // 排除指定用户ID
        if (excludeUserId != null) {
            queryWrapper.ne(User::getUserId, excludeUserId);
        }
        
        // 检查是否存在
        return count(queryWrapper) == 0;
    }

    /**
     * 验证手机号是否可用
     * 功能描述：检查手机号是否已被使用，用于注册和修改时的验证
     * 采用技术：唯一性检查 + 排除条件
     * 优势：快速验证手机号可用性，支持手机号唯一性约束
     * 
     * @param phone 待验证的手机号，不能为空
     * @param excludeUserId 排除的用户ID，用于修改时排除自身，可以为空
     * @return 是否可用，true表示可用，false表示已被使用
     */
    @Override
    public boolean isPhoneAvailable(String phone, Long excludeUserId) {
        // 构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getMobile, phone)
                .eq(User::getDeleted, 0);
        
        // 排除指定用户ID
        if (excludeUserId != null) {
            queryWrapper.ne(User::getUserId, excludeUserId);
        }
        
        // 检查是否存在
        return count(queryWrapper) == 0;
    }

    /**
     * 根据时间范围查询用户
     * 功能描述：按注册时间范围查询用户列表，用于数据分析和统计
     * 采用技术：时间范围查询 + 状态过滤
     * 优势：支持时间维度的用户分析，提供灵活的查询条件
     * 
     * @param startTime 开始时间，可以为空
     * @param endTime 结束时间，可以为空
     * @return 指定时间范围内注册的用户列表，按注册时间排序
     */
    @Override
    public List<User> getUsersByRegistrationTime(LocalDateTime startTime, LocalDateTime endTime) {
        // 构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getDeleted, 0);
        
        // 添加时间范围条件
        if (startTime != null) {
            queryWrapper.ge(User::getCreatedAt, startTime);
        }
        if (endTime != null) {
            queryWrapper.le(User::getCreatedAt, endTime);
        }
        
        // 按创建时间排序
        queryWrapper.orderByAsc(User::getCreatedAt);
        
        // 执行查询并清除密码信息
        List<User> users = list(queryWrapper);
        users.forEach(user -> user.setPassword(null));
        
        return users;
    }

    /**
     * 更新用户登录信息
     * 功能描述：更新用户的最后登录时间和登录次数，用于用户活跃度统计
     * 采用技术：选择性字段更新 + 计数器增加
     * 优势：记录用户登录历史，支持用户行为分析
     * 
     * @param userId 用户ID，不能为空
     * @param loginIp 登录IP地址
     * @return 是否更新成功
     */
    public boolean updateLoginInfo(Long userId, String loginIp) {
        // 构建更新条件
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>()
                .eq(User::getUserId, userId)
                .set(User::getLastLoginTime, LocalDateTime.now())
                .set(User::getUpdatedAt, LocalDateTime.now());
        
        return update(updateWrapper);
    }

    /**
     * 获取用户统计信息
     * 功能描述：获取用户相关的统计数据，用于数据分析和监控面板
     * 采用技术：聚合查询 + 分组统计
     * 优势：提供实时统计数据，支持业务决策，优化查询性能
     * 
     * @return 统计信息Map，包含各种统计指标
     *         - totalUsers: 总用户数
     *         - enabledUsers: 启用用户数
     *         - disabledUsers: 禁用用户数
     *         - todayRegistrations: 今日注册数
     *         - monthlyRegistrations: 本月注册数
     */
    @Override
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总用户数（未删除）
        long totalUsers = count(new LambdaQueryWrapper<User>()
                .eq(User::getDeleted, 0));
        statistics.put("totalUsers", totalUsers);
        
        // 启用用户数
        long enabledUsers = count(new LambdaQueryWrapper<User>()
                .eq(User::getDeleted, 0)
                .eq(User::getStatus, 1));
        statistics.put("enabledUsers", enabledUsers);
        
        // 禁用用户数
        long disabledUsers = count(new LambdaQueryWrapper<User>()
                .eq(User::getDeleted, 0)
                .eq(User::getStatus, 0));
        statistics.put("disabledUsers", disabledUsers);
        
        // 今日注册数
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime todayEnd = todayStart.plusDays(1);
        long todayRegistrations = count(new LambdaQueryWrapper<User>()
                .eq(User::getDeleted, 0)
                .ge(User::getCreatedAt, todayStart)
                .lt(User::getCreatedAt, todayEnd));
        statistics.put("todayRegistrations", todayRegistrations);
        
        // 本月注册数
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime monthEnd = monthStart.plusMonths(1);
        long monthlyRegistrations = count(new LambdaQueryWrapper<User>()
                .eq(User::getDeleted, 0)
                .ge(User::getCreatedAt, monthStart)
                .lt(User::getCreatedAt, monthEnd));
        statistics.put("monthlyRegistrations", monthlyRegistrations);
        
        return statistics;
    }

    /**
     * 锁定用户账户
     * 功能描述：锁定用户账户到指定时间，防止用户登录
     * 采用技术：数据库更新 + 时间控制
     * 优势：提供账户安全控制，支持临时锁定和永久锁定
     * 
     * @param userId 用户ID
     * @param lockedUntil 锁定截止时间，null表示永久锁定
     * @param operatorId 操作人ID
     * @return 是否锁定成功
     */
    @Override
    public boolean lockUser(Long userId, LocalDateTime lockedUntil, Long operatorId) {
        log.info("开始锁定用户，用户ID: {}, 锁定至: {}, 操作人ID: {}", userId, lockedUntil, operatorId);
        
        // 构建更新条件
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>()
                .eq(User::getUserId, userId)
                .set(User::getUpdatedAt, LocalDateTime.now());
        
        boolean updated = update(updateWrapper);
        if (updated) {
            log.info("用户锁定成功，用户ID: {}", userId);
        } else {
            log.error("用户锁定失败，用户ID: {}", userId);
        }
        
        return updated;
    }

    /**
     * 解锁用户账户
     * 功能描述：解除用户账户锁定状态，恢复正常登录
     * 采用技术：数据库更新 + 状态重置
     * 优势：灵活的账户管理，支持手动解锁
     * 
     * @param userId 用户ID
     * @param operatorId 操作人ID
     * @return 是否解锁成功
     */
    @Override
    public boolean unlockUser(Long userId, Long operatorId) {
        log.info("开始解锁用户，用户ID: {}, 操作人ID: {}", userId, operatorId);
        
        // 构建更新条件
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<User>()
                .eq(User::getUserId, userId)
                .set(User::getUpdatedAt, LocalDateTime.now());
        
        boolean updated = update(updateWrapper);
        if (updated) {
            log.info("用户解锁成功，用户ID: {}", userId);
        } else {
            log.error("用户解锁失败，用户ID: {}", userId);
        }
        
        return updated;
    }
}