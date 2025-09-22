package com.fenix.shop.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fenix.shop.user.entity.UserRoleRel;

import java.util.List;

/**
 * 用户角色关联业务服务接口
 * 功能描述：定义用户与角色关联关系的核心业务方法，包括角色分配、权限管理、关联查询等功能
 * 采用技术：MyBatis Plus IService + 自定义业务方法 + 关联查询优化
 * 技术优势：
 * 1. 继承IService获得基础CRUD操作，减少重复代码
 * 2. 专注于用户角色关联的业务逻辑处理
 * 3. 支持复杂的关联查询和权限验证
 * 4. 提供高效的批量操作和事务管理
 * 5. 便于权限系统的扩展和维护
 * 
 * @author fenix
 * @date 2025-01-27
 * @version 1.0
 */
public interface UserRoleRelService extends IService<UserRoleRel> {

    /**
     * 为用户分配角色
     * 功能描述：为指定用户分配一个或多个角色，支持权限验证和重复检查
     * 采用技术：批量插入 + 重复检查 + 事务管理 + 权限验证
     * 优势：高效的角色分配，防止重复分配，保证数据一致性
     * 
     * @param userId 用户ID，不能为空
     * @param roleIds 角色ID列表，不能为空
     * @param operatorId 操作人ID，用于审计记录
     * @return 成功分配的角色数量
     * @throws IllegalArgumentException 当用户ID或角色ID无效时抛出
     * @throws RuntimeException 当分配过程中发生系统错误时抛出
     */
    int assignRolesToUser(Long userId, List<Long> roleIds, Long operatorId);

    /**
     * 移除用户的角色
     * 功能描述：移除用户的指定角色，支持批量移除和权限验证
     * 采用技术：逻辑删除 + 批量操作 + 权限验证 + 事务管理
     * 优势：安全的角色移除，保留操作历史，支持批量处理
     * 
     * @param userId 用户ID，不能为空
     * @param roleIds 要移除的角色ID列表，不能为空
     * @param operatorId 操作人ID，用于审计记录
     * @return 成功移除的角色数量
     * @throws IllegalArgumentException 当用户ID或角色ID无效时抛出
     * @throws RuntimeException 当移除过程中发生系统错误时抛出
     */
    int removeRolesFromUser(Long userId, List<Long> roleIds, Long operatorId);

    /**
     * 获取用户的所有角色
     * 功能描述：查询用户拥有的所有有效角色关联，用于权限验证和角色展示
     * 采用技术：关联查询 + 状态过滤 + 缓存优化
     * 优势：高效的角色查询，支持权限系统，提供完整的角色信息
     * 
     * @param userId 用户ID，不能为空
     * @return 用户拥有的角色关联列表，按角色排序字段排序
     */
    List<UserRoleRel> getUserRoles(Long userId);

    /**
     * 获取用户的角色编码列表
     * 功能描述：获取用户拥有的所有角色编码，用于权限验证和快速检查
     * 采用技术：关联查询 + 字段提取 + 缓存优化
     * 优势：轻量级的权限查询，减少数据传输，提高验证效率
     * 
     * @param userId 用户ID，不能为空
     * @return 用户拥有的角色编码列表
     */
    List<String> getUserRoleCodes(Long userId);

    /**
     * 获取拥有指定角色的用户列表
     * 功能描述：查询拥有指定角色的所有用户，用于角色管理和用户分析
     * 采用技术：关联查询 + 分页支持 + 状态过滤
     * 优势：支持角色用户管理，提供完整的用户信息，支持分页查询
     * 
     * @param roleId 角色ID，不能为空
     * @return 拥有该角色的用户ID列表
     */
    List<Long> getUsersByRole(Long roleId);

    /**
     * 检查用户是否拥有指定角色
     * 功能描述：验证用户是否拥有指定的角色，用于权限验证和访问控制
     * 采用技术：快速查询 + 缓存优化 + 状态检查
     * 优势：高效的权限验证，支持实时检查，减少数据库访问
     * 
     * @param userId 用户ID，不能为空
     * @param roleId 角色ID，不能为空
     * @return 是否拥有该角色，true表示拥有，false表示没有
     */
    boolean hasRole(Long userId, Long roleId);

    /**
     * 检查用户是否拥有指定角色编码
     * 功能描述：通过角色编码验证用户权限，用于基于编码的权限控制
     * 采用技术：关联查询 + 编码匹配 + 缓存优化
     * 优势：基于编码的权限验证，支持灵活的权限配置
     * 
     * @param userId 用户ID，不能为空
     * @param roleCode 角色编码，不能为空，格式如ROLE_ADMIN
     * @return 是否拥有该角色，true表示拥有，false表示没有
     */
    boolean hasRoleByCode(Long userId, String roleCode);
}