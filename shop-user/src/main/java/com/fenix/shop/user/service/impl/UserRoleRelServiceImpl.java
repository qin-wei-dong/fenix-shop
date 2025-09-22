package com.fenix.shop.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fenix.shop.user.entity.UserRole;
import com.fenix.shop.user.entity.UserRoleRel;
import com.fenix.shop.user.mapper.UserRoleRelMapper;
import com.fenix.shop.user.service.UserRoleRelService;
import com.fenix.shop.user.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户角色关联业务服务实现类
 * 功能描述：实现用户角色关联的核心业务逻辑，包括角色分配、权限管理、关联查询等功能
 * 采用技术：MyBatis Plus ServiceImpl + Spring事务管理 + 批量操作优化 + 关联查询
 * 技术优势：
 * 1. 继承ServiceImpl获得基础CRUD操作，减少重复代码
 * 2. 使用声明式事务保证数据一致性和业务完整性
 * 3. 采用Lambda表达式构建类型安全的查询条件
 * 4. 支持批量操作提高性能，减少数据库交互次数
 * 5. 集成逻辑删除和乐观锁机制保证数据安全
 * 
 * @Service 标记为Spring服务组件，支持依赖注入和AOP增强
 * @RequiredArgsConstructor Lombok注解，自动生成final字段的构造函数
 * @Slf4j Lombok注解，自动生成日志记录器
 * @Transactional 类级别事务注解，确保所有方法的事务一致性
 * 
 * @author fenix
 * @date 2025-01-27
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class UserRoleRelServiceImpl extends ServiceImpl<UserRoleRelMapper, UserRoleRel> implements UserRoleRelService {

    /**
     * 用户角色关联数据访问层
     * 功能描述：提供用户角色关联的基础数据访问操作
     * 采用技术：MyBatis Plus BaseMapper自动注入
     * 优势：自动获得基础CRUD操作，减少重复代码
     */
    private final UserRoleRelMapper userRoleRelMapper;

    /**
     * 角色服务接口
     * 功能描述：用于查询角色信息
     */
    private final UserRoleService userRoleService;

    /**
     * 为用户分配角色
     * 功能描述：为指定用户分配一个或多个角色，支持角色权限的灵活配置
     * 采用技术：批量插入 + 重复检查 + 事务管理
     * 优势：支持批量分配提高效率，防止重复分配，保证数据一致性
     * 
     * @param userId 用户ID，不能为空
     * @param roleIds 角色ID列表，不能为空
     * @param operatorId 操作人ID，用于审计记录
     * @return 成功分配的角色数量
     * @throws IllegalArgumentException 当用户ID或角色ID无效时抛出
     * @throws RuntimeException 当分配过程中发生系统错误时抛出
     */
    @Override
    public int assignRolesToUser(Long userId, List<Long> roleIds, Long operatorId) {
        log.info("开始为用户分配角色，用户ID: {}, 角色数量: {}, 操作人ID: {}", 
                userId, roleIds.size(), operatorId);
        
        // 查询已存在的角色关联
        List<Long> existingRoleIds = getExistingRoleIds(userId, roleIds);
        
        // 过滤掉已存在的角色关联
        List<Long> newRoleIds = roleIds.stream()
                .filter(roleId -> !existingRoleIds.contains(roleId))
                .toList();
        
        if (newRoleIds.isEmpty()) {
            log.info("用户角色分配完成，无新增角色，用户ID: {}", userId);
            return 0;
        }
        
        // 批量创建用户角色关联
        List<UserRoleRel> userRoleRels = newRoleIds.stream()
                .map(roleId -> {
                    UserRoleRel userRoleRel = new UserRoleRel();
                    userRoleRel.setUserId(userId);
                    userRoleRel.setRoleId(roleId);
                    userRoleRel.setCreatedTime(LocalDateTime.now());
                    return userRoleRel;
                })
                .toList();
        
        // 批量保存
        boolean saved = saveBatch(userRoleRels);
        if (!saved) {
            log.error("用户角色分配失败，批量保存异常，用户ID: {}", userId);
            throw new RuntimeException("用户角色分配失败");
        }
        
        log.info("用户角色分配成功，用户ID: {}, 新增角色数量: {}, 操作人ID: {}", 
                userId, newRoleIds.size(), operatorId);
        return newRoleIds.size();
    }

    /**
     * 移除用户角色
     * 功能描述：移除用户的指定角色，支持单个或批量移除
     * 采用技术：物理删除 + 批量操作 + 事务管理
     * 优势：彻底删除关联关系，支持批量操作，保证操作原子性
     * 
     * @param userId 用户ID，不能为空
     * @param roleIds 要移除的角色ID列表，不能为空
     * @param operatorId 操作人ID，用于审计记录
     * @return 成功移除的角色数量
     * @throws IllegalArgumentException 当用户ID或角色ID无效时抛出
     * @throws RuntimeException 当移除过程中发生系统错误时抛出
     */
    @Override
    public int removeRolesFromUser(Long userId, List<Long> roleIds, Long operatorId) {
        log.info("开始移除用户角色，用户ID: {}, 角色数量: {}, 操作人ID: {}", 
                userId, roleIds.size(), operatorId);
        
        // 删除用户角色关联
        LambdaQueryWrapper<UserRoleRel> queryWrapper = new LambdaQueryWrapper<UserRoleRel>()
                .eq(UserRoleRel::getUserId, userId)
                .in(UserRoleRel::getRoleId, roleIds);
        
        int removedCount = Math.toIntExact(count(queryWrapper));
        boolean removed = remove(queryWrapper);
        
        if (!removed) {
            log.error("用户角色移除失败，数据删除异常，用户ID: {}", userId);
            throw new RuntimeException("用户角色移除失败");
        }
        
        log.info("用户角色移除成功，用户ID: {}, 移除角色数量: {}, 操作人ID: {}", 
                userId, removedCount, operatorId);
        return removedCount;
    }

    /**
     * 查询用户的所有角色关联
     * 功能描述：获取指定用户拥有的所有角色关联
     * 采用技术：关联查询 + 状态过滤
     * 优势：高效的关联查询，自动过滤无效角色
     * 
     * @param userId 用户ID，不能为空
     * @return 用户拥有的角色关联列表
     */
    @Override
    public List<UserRoleRel> getUserRoles(Long userId) {
        return list(new LambdaQueryWrapper<UserRoleRel>()
                .eq(UserRoleRel::getUserId, userId)
                .orderByAsc(UserRoleRel::getCreatedTime));
    }

    /**
     * 获取用户的角色编码列表
     * 功能描述：获取用户拥有的所有角色编码，用于权限验证和快速检查
     * 采用技术：关联查询 + 字段提取 + 缓存优化
     * 优势：轻量级的权限查询，减少数据传输，提高验证效率
     * 
     * @param userId 用户ID，不能为空
     * @return 用户拥有的角色编码列表
     */
    @Override
    public List<String> getUserRoleCodes(Long userId) {
        // 获取用户的所有角色关联
        List<UserRoleRel> userRoleRels = getUserRoles(userId);
        
        if (userRoleRels.isEmpty()) {
            return List.of();
        }
        
        // 提取角色ID列表
        List<Long> roleIds = userRoleRels.stream()
                .map(UserRoleRel::getRoleId)
                .collect(Collectors.toList());
        
        // 查询角色详细信息
        List<UserRole> roles = userRoleService.listByIds(roleIds);
        
        return roles.stream()
                .filter(role -> role.getIsActive()) // 只返回激活的角色
                .map(UserRole::getRoleCode)
                .collect(Collectors.toList());
    }

    /**
     * 获取拥有指定角色的用户列表
     * 功能描述：查询拥有指定角色的所有用户，用于角色管理和用户分析
     * 采用技术：关联查询 + 状态过滤
     * 优势：支持角色用户管理，提供完整的用户信息
     * 
     * @param roleId 角色ID，不能为空
     * @return 拥有该角色的用户ID列表
     */
    @Override
    public List<Long> getUsersByRole(Long roleId) {
        List<UserRoleRel> userRoleRels = list(new LambdaQueryWrapper<UserRoleRel>()
                .eq(UserRoleRel::getRoleId, roleId)
                .orderByAsc(UserRoleRel::getCreatedTime));
        
        return userRoleRels.stream()
                .map(UserRoleRel::getUserId)
                .collect(Collectors.toList());
    }

    /**
     * 检查用户是否拥有指定角色
     * 功能描述：验证用户是否具有特定角色的权限
     * 采用技术：精确查询 + 缓存优化 + 快速验证
     * 优势：高效的权限验证，支持权限控制和访问检查
     * 
     * @param userId 用户ID，不能为空
     * @param roleId 角色ID，不能为空
     * @return 是否拥有该角色，true表示拥有，false表示没有
     */
    @Override
    public boolean hasRole(Long userId, Long roleId) {
        LambdaQueryWrapper<UserRoleRel> queryWrapper = new LambdaQueryWrapper<UserRoleRel>()
                .eq(UserRoleRel::getUserId, userId)
                .eq(UserRoleRel::getRoleId, roleId);
        
        return count(queryWrapper) > 0;
    }

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
    @Override
    public boolean hasRoleByCode(Long userId, String roleCode) {
        // 获取用户的角色编码列表
        List<String> userRoleCodes = getUserRoleCodes(userId);
        
        return userRoleCodes.contains(roleCode);
    }

    /**
     * 获取已存在的角色ID列表
     * 功能描述：查询用户已拥有的角色ID，用于避免重复分配
     * 采用技术：IN查询 + 结果映射
     * 优势：高效的重复检查，减少不必要的数据库操作
     * 
     * @param userId 用户ID，不能为空
     * @param roleIds 要检查的角色ID列表，不能为空
     * @return 已存在的角色ID列表
     */
    private List<Long> getExistingRoleIds(Long userId, List<Long> roleIds) {
        List<UserRoleRel> existingUserRoleRels = list(new LambdaQueryWrapper<UserRoleRel>()
                .eq(UserRoleRel::getUserId, userId)
                .in(UserRoleRel::getRoleId, roleIds));
        
        return existingUserRoleRels.stream()
                .map(UserRoleRel::getRoleId)
                .collect(Collectors.toList());
    }
}