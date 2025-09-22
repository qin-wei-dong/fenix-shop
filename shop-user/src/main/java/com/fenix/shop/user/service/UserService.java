package com.fenix.shop.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fenix.shop.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户业务服务接口
 * 功能描述：定义用户管理的核心业务方法，包括用户注册、登录、信息管理等功能
 * 采用技术：MyBatis Plus IService + 自定义业务方法
 * 技术优势：
 * 1. 继承IService获得基础CRUD操作，减少重复代码
 * 2. 定义业务层接口规范，支持多种实现方式
 * 3. 分离业务逻辑和数据访问，提高代码可维护性
 * 4. 支持事务管理和业务规则验证
 * 5. 便于单元测试和模拟测试
 * 
 * @author fenix
 * @date 2024-12-19
 * @version 1.0
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * 功能描述：创建新用户账户，包括用户名唯一性验证、密码加密、默认角色分配
     * 采用技术：Spring事务管理 + 密码加密 + 业务规则验证
     * 优势：保证数据一致性，提高账户安全性，支持业务规则扩展
     * 
     * @param user 用户信息对象，包含用户名、密码、邮箱等基本信息
     * @return 注册成功的用户对象，包含生成的用户ID
     * @throws IllegalArgumentException 当用户名已存在或参数无效时抛出
     * @throws RuntimeException 当注册过程中发生系统错误时抛出
     */
    User registerUser(User user);

    /**
     * 用户登录验证
     * 功能描述：验证用户登录凭据，支持用户名、邮箱、手机号多种登录方式
     * 采用技术：密码验证 + 登录失败计数 + 账户锁定机制
     * 优势：提高系统安全性，防止暴力破解，支持多种登录方式
     * 
     * @param loginName 登录名，可以是用户名、邮箱或手机号
     * @param password 登录密码，明文密码
     * @param loginIp 登录IP地址，用于安全记录
     * @return 登录成功的用户对象，包含完整用户信息
     * @throws IllegalArgumentException 当登录凭据无效时抛出
     * @throws RuntimeException 当账户被锁定或系统错误时抛出
     */
    User loginUser(String loginName, String password, String loginIp);

    /**
     * 根据用户名查询用户
     * 功能描述：通过用户名精确查询用户信息，用于登录验证和用户查找
     * 采用技术：数据访问层封装 + 缓存优化
     * 优势：简化业务调用，支持缓存加速，提高查询效率
     * 
     * @param username 用户名，不能为空
     * @return 用户对象，如果不存在返回null
     */
    User getUserByUsername(String username);

    /**
     * 根据邮箱查询用户
     * 功能描述：通过邮箱精确查询用户信息，用于邮箱登录和找回密码
     * 采用技术：数据访问层封装 + 缓存优化
     * 优势：支持邮箱登录场景，提高用户体验
     * 
     * @param email 邮箱地址，不能为空
     * @return 用户对象，如果不存在返回null
     */
    User getUserByEmail(String email);

    /**
     * 根据手机号查询用户
     * 功能描述：通过手机号精确查询用户信息，用于手机号登录和验证
     * 采用技术：数据访问层封装 + 缓存优化
     * 优势：支持手机号登录场景，满足移动端需求
     * 
     * @param phone 手机号码，不能为空
     * @return 用户对象，如果不存在返回null
     */
    User getUserByPhone(String phone);

    /**
     * 更新用户基本信息
     * 功能描述：更新用户的基本信息，如昵称、头像、个人资料等
     * 采用技术：乐观锁更新 + 数据验证 + 缓存同步
     * 优势：防止并发更新冲突，保证数据一致性，支持实时更新
     * 
     * @param user 包含更新信息的用户对象，必须包含用户ID
     * @return 更新后的用户对象
     * @throws IllegalArgumentException 当用户ID无效或数据格式错误时抛出
     * @throws RuntimeException 当更新失败或发生并发冲突时抛出
     */
    User updateUserInfo(User user);

    /**
     * 修改用户密码
     * 功能描述：修改用户登录密码，包括旧密码验证和新密码加密
     * 采用技术：密码验证 + 密码加密 + 安全日志记录
     * 优势：提高账户安全性，支持密码强度验证，记录安全操作
     * 
     * @param userId 用户ID，不能为空
     * @param oldPassword 旧密码，明文密码
     * @param newPassword 新密码，明文密码
     * @return 是否修改成功
     * @throws IllegalArgumentException 当旧密码错误或新密码不符合要求时抛出
     * @throws RuntimeException 当修改过程中发生系统错误时抛出
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 重置用户密码
     * 功能描述：管理员重置用户密码，用于密码找回和管理操作
     * 采用技术：权限验证 + 密码生成 + 通知机制
     * 优势：支持管理员操作，提供密码找回功能，保证操作安全性
     * 
     * @param userId 用户ID，不能为空
     * @param newPassword 新密码，明文密码
     * @param operatorId 操作人ID，用于审计记录
     * @return 是否重置成功
     * @throws IllegalArgumentException 当用户ID无效或密码不符合要求时抛出
     * @throws RuntimeException 当重置过程中发生系统错误时抛出
     */
    boolean resetPassword(Long userId, String newPassword, Long operatorId);

    /**
     * 锁定用户账户
     * 功能描述：锁定用户账户，防止用户登录，用于安全控制和管理操作
     * 采用技术：状态更新 + 锁定时间设置 + 操作日志记录
     * 优势：灵活的安全控制，支持临时锁定和永久锁定
     * 
     * @param userId 用户ID，不能为空
     * @param lockedUntil 锁定截止时间，null表示永久锁定
     * @param operatorId 操作人ID，用于审计记录
     * @return 是否锁定成功
     * @throws IllegalArgumentException 当用户ID无效时抛出
     * @throws RuntimeException 当锁定过程中发生系统错误时抛出
     */
    boolean lockUser(Long userId, LocalDateTime lockedUntil, Long operatorId);

    /**
     * 解锁用户账户
     * 功能描述：解除用户账户锁定，恢复用户正常登录权限
     * 采用技术：状态更新 + 失败计数重置 + 操作日志记录
     * 优势：灵活的账户管理，支持快速恢复用户权限
     * 
     * @param userId 用户ID，不能为空
     * @param operatorId 操作人ID，用于审计记录
     * @return 是否解锁成功
     * @throws IllegalArgumentException 当用户ID无效时抛出
     * @throws RuntimeException 当解锁过程中发生系统错误时抛出
     */
    boolean unlockUser(Long userId, Long operatorId);

    /**
     * 启用用户账户
     * 功能描述：启用被禁用的用户账户，恢复用户正常使用权限
     * 采用技术：状态更新 + 权限恢复 + 操作日志记录
     * 优势：灵活的账户状态管理，支持账户生命周期管理
     * 
     * @param userId 用户ID，不能为空
     * @param operatorId 操作人ID，用于审计记录
     * @return 是否启用成功
     * @throws IllegalArgumentException 当用户ID无效时抛出
     * @throws RuntimeException 当启用过程中发生系统错误时抛出
     */
    boolean enableUser(Long userId, Long operatorId);

    /**
     * 禁用用户账户
     * 功能描述：禁用用户账户，阻止用户登录和使用系统功能
     * 采用技术：状态更新 + 权限回收 + 操作日志记录
     * 优势：灵活的账户控制，支持临时禁用和管理操作
     * 
     * @param userId 用户ID，不能为空
     * @param operatorId 操作人ID，用于审计记录
     * @return 是否禁用成功
     * @throws IllegalArgumentException 当用户ID无效时抛出
     * @throws RuntimeException 当禁用过程中发生系统错误时抛出
     */
    boolean disableUser(Long userId, Long operatorId);

    /**
     * 分页查询用户列表
     * 功能描述：按条件分页查询用户列表，支持多种筛选条件和排序方式
     * 采用技术：MyBatis Plus分页插件 + 动态查询条件
     * 优势：高效的分页查询，支持灵活的条件筛选，适合管理界面
     * 
     * @param page 分页参数对象，包含页码和页大小
     * @param status 用户状态筛选，可以为空
     * @param keyword 关键字搜索，支持用户名、邮箱、手机号模糊匹配，可以为空
     * @return 分页结果对象，包含用户列表和分页信息
     */
    IPage<User> getUserPage(Page<User> page, Integer status, String keyword);

    /**
     * 批量更新用户状态
     * 功能描述：批量修改多个用户的状态，用于批量管理操作
     * 采用技术：批量更新 + 事务管理 + 操作日志记录
     * 优势：提高批量操作效率，保证操作原子性，支持批量管理
     * 
     * @param userIds 用户ID列表，不能为空
     * @param status 目标状态值，0-禁用，1-正常，2-锁定
     * @param operatorId 操作人ID，用于审计记录
     * @return 成功更新的用户数量
     * @throws IllegalArgumentException 当参数无效时抛出
     * @throws RuntimeException 当批量更新过程中发生系统错误时抛出
     */
    int batchUpdateUserStatus(List<Long> userIds, Integer status, Long operatorId);

    /**
     * 验证用户名是否可用
     * 功能描述：检查用户名是否已被使用，用于注册和修改用户名时的验证
     * 采用技术：唯一性检查 + 缓存优化
     * 优势：快速验证用户名可用性，提高用户体验，减少重复提交
     * 
     * @param username 待验证的用户名，不能为空
     * @param excludeUserId 排除的用户ID，用于修改时排除自身，可以为空
     * @return 是否可用，true表示可用，false表示已被使用
     */
    boolean isUsernameAvailable(String username, Long excludeUserId);

    /**
     * 验证邮箱是否可用
     * 功能描述：检查邮箱是否已被使用，用于注册和修改邮箱时的验证
     * 采用技术：唯一性检查 + 缓存优化
     * 优势：快速验证邮箱可用性，提高用户体验，减少重复提交
     * 
     * @param email 待验证的邮箱地址，不能为空
     * @param excludeUserId 排除的用户ID，用于修改时排除自身，可以为空
     * @return 是否可用，true表示可用，false表示已被使用
     */
    boolean isEmailAvailable(String email, Long excludeUserId);

    /**
     * 验证手机号是否可用
     * 功能描述：检查手机号是否已被使用，用于注册和修改手机号时的验证
     * 采用技术：唯一性检查 + 缓存优化
     * 优势：快速验证手机号可用性，提高用户体验，减少重复提交
     * 
     * @param phone 待验证的手机号码，不能为空
     * @param excludeUserId 排除的用户ID，用于修改时排除自身，可以为空
     * @return 是否可用，true表示可用，false表示已被使用
     */
    boolean isPhoneAvailable(String phone, Long excludeUserId);

    /**
     * 获取用户统计信息
     * 功能描述：获取用户相关的统计数据，用于数据分析和监控面板
     * 采用技术：聚合查询 + 缓存优化 + 数据分析
     * 优势：提供实时统计数据，支持业务决策，优化查询性能
     * 
     * @return 统计信息Map，包含各种统计指标
     *         - totalUsers: 总用户数
     *         - activeUsers: 活跃用户数
     *         - lockedUsers: 锁定用户数
     *         - disabledUsers: 禁用用户数
     *         - todayRegistrations: 今日注册数
     */
    Map<String, Object> getUserStatistics();

    /**
     * 查询指定时间范围内注册的用户
     * 功能描述：按注册时间范围查询用户，用于数据分析和运营统计
     * 采用技术：时间范围查询 + 数据分析
     * 优势：支持灵活的时间范围分析，便于运营数据统计
     * 
     * @param startTime 开始时间，不能为空
     * @param endTime 结束时间，不能为空
     * @return 指定时间范围内注册的用户列表
     */
    List<User> getUsersByRegistrationTime(LocalDateTime startTime, LocalDateTime endTime);
}