package com.fenix.shop.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fenix.shop.user.entity.UserRole;

import java.util.List;
import java.util.Map;

/**
 * 用户角色业务服务接口
 * 功能描述：定义角色管理的核心业务方法，包括角色创建、权限配置、角色分配等功能
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
public interface UserRoleService extends IService<UserRole> {

    /**
     * 创建新角色
     * 功能描述：创建新的用户角色，包括角色编码唯一性验证、权限配置、排序设置
     * 采用技术：Spring事务管理 + 业务规则验证 + 权限配置
     * 优势：保证数据一致性，支持灵活的权限配置，提供完整的角色管理
     * 
     * @param userRole 角色信息对象，包含角色编码、名称、权限等信息
     * @return 创建成功的角色对象，包含生成的角色ID
     * @throws IllegalArgumentException 当角色编码已存在或参数无效时抛出
     * @throws RuntimeException 当创建过程中发生系统错误时抛出
     */
    UserRole createRole(UserRole userRole);

    /**
     * 更新角色信息
     * 功能描述：更新角色的基本信息和权限配置，支持部分更新和完整更新
     * 采用技术：乐观锁更新 + 权限验证 + 业务规则检查
     * 优势：防止并发更新冲突，保证权限配置安全，支持灵活更新
     * 
     * @param userRole 包含更新信息的角色对象，必须包含角色ID
     * @return 更新后的角色对象
     * @throws IllegalArgumentException 当角色ID无效或数据格式错误时抛出
     * @throws RuntimeException 当更新失败或发生并发冲突时抛出
     */
    UserRole updateRole(UserRole userRole);

    /**
     * 删除角色
     * 功能描述：删除指定角色，包括关联关系清理和权限回收
     * 采用技术：逻辑删除 + 关联数据清理 + 事务管理
     * 优势：保留数据历史，确保数据一致性，支持安全删除
     * 
     * @param roleId 角色ID，不能为空
     * @param operatorId 操作人ID，用于审计记录
     * @return 是否删除成功
     * @throws IllegalArgumentException 当角色ID无效或角色正在使用时抛出
     * @throws RuntimeException 当删除过程中发生系统错误时抛出
     */
    boolean deleteRole(Long roleId, Long operatorId);

    /**
     * 根据角色编码查询角色
     * 功能描述：通过角色编码精确查询角色信息，用于权限验证和角色查找
     * 采用技术：数据访问层封装 + 缓存优化
     * 优势：简化业务调用，支持缓存加速，提高查询效率
     * 
     * @param roleCode 角色编码，不能为空，格式如ROLE_ADMIN
     * @return 角色对象，如果不存在返回null
     */
    UserRole getRoleByCode(String roleCode);

    /**
     * 获取所有启用状态的角色
     * 功能描述：查询所有可用的角色列表，用于角色选择和权限分配
     * 采用技术：状态过滤 + 排序优化 + 缓存支持
     * 优势：提供可用角色列表，支持角色选择界面，优化查询性能
     * 
     * @return 启用状态的角色列表，按排序字段和创建时间排序
     */
    List<UserRole> getEnabledRoles();

    /**
     * 获取系统内置角色
     * 功能描述：查询系统预定义的内置角色，用于系统初始化和核心权限管理
     * 采用技术：系统标识过滤 + 缓存优化
     * 优势：区分系统角色和自定义角色，保护核心权限配置
     * 
     * @return 系统内置角色列表，按排序字段排序
     */
    List<UserRole> getSystemRoles();

    /**
     * 获取自定义角色
     * 功能描述：查询用户自定义创建的角色，用于角色管理和权限配置
     * 采用技术：系统标识过滤 + 分页支持
     * 优势：区分系统角色和自定义角色，支持灵活的角色管理
     * 
     * @return 自定义角色列表，按排序字段和创建时间排序
     */
    List<UserRole> getCustomRoles();

    /**
     * 分页查询角色列表
     * 功能描述：按条件分页查询角色列表，支持多种筛选条件和排序方式
     * 采用技术：MyBatis Plus分页插件 + 动态查询条件
     * 优势：高效的分页查询，支持灵活的条件筛选，适合管理界面
     * 
     * @param page 分页参数对象，包含页码和页大小
     * @param status 角色状态筛选，可以为空
     * @param keyword 关键字搜索，支持角色名称、角色编码模糊匹配，可以为空
     * @param isSystem 是否系统角色筛选，可以为空
     * @return 分页结果对象，包含角色列表和分页信息
     */
    IPage<UserRole> getRolePage(Page<UserRole> page, Integer status, String keyword, Boolean isSystem);

    /**
     * 启用角色
     * 功能描述：启用被禁用的角色，恢复角色的正常使用权限
     * 采用技术：状态更新 + 权限恢复 + 操作日志记录
     * 优势：灵活的角色状态管理，支持角色生命周期管理
     * 
     * @param roleId 角色ID，不能为空
     * @param operatorId 操作人ID，用于审计记录
     * @return 是否启用成功
     * @throws IllegalArgumentException 当角色ID无效时抛出
     * @throws RuntimeException 当启用过程中发生系统错误时抛出
     */
    boolean enableRole(Long roleId, Long operatorId);

    /**
     * 禁用角色
     * 功能描述：禁用角色，阻止角色的使用和分配
     * 采用技术：状态更新 + 权限回收 + 操作日志记录
     * 优势：灵活的角色控制，支持临时禁用和管理操作
     * 
     * @param roleId 角色ID，不能为空
     * @param operatorId 操作人ID，用于审计记录
     * @return 是否禁用成功
     * @throws IllegalArgumentException 当角色ID无效时抛出
     * @throws RuntimeException 当禁用过程中发生系统错误时抛出
     */
    boolean disableRole(Long roleId, Long operatorId);

    /**
     * 批量更新角色状态
     * 功能描述：批量修改多个角色的状态，用于批量管理操作
     * 采用技术：批量更新 + 事务管理 + 操作日志记录
     * 优势：提高批量操作效率，保证操作原子性，支持批量管理
     * 
     * @param roleIds 角色ID列表，不能为空
     * @param status 目标状态值，0-禁用，1-启用
     * @param operatorId 操作人ID，用于审计记录
     * @return 成功更新的角色数量
     * @throws IllegalArgumentException 当参数无效时抛出
     * @throws RuntimeException 当批量更新过程中发生系统错误时抛出
     */
    int batchUpdateRoleStatus(List<Long> roleIds, Integer status, Long operatorId);

    /**
     * 更新角色权限
     * 功能描述：更新角色的权限配置，支持权限的增加、删除和修改
     * 采用技术：权限解析 + 配置验证 + 版本控制
     * 优势：灵活的权限管理，支持细粒度权限控制，保证配置安全
     * 
     * @param roleId 角色ID，不能为空
     * @param permissions 权限配置，JSON格式的权限列表
     * @param operatorId 操作人ID，用于审计记录
     * @return 是否更新成功
     * @throws IllegalArgumentException 当角色ID无效或权限配置格式错误时抛出
     * @throws RuntimeException 当更新过程中发生系统错误时抛出
     */
    boolean updateRolePermissions(Long roleId, String permissions, Long operatorId);

    /**
     * 验证角色编码是否可用
     * 功能描述：检查角色编码是否已被使用，用于创建和修改角色时的验证
     * 采用技术：唯一性检查 + 缓存优化
     * 优势：快速验证角色编码可用性，提高用户体验，减少重复提交
     * 
     * @param roleCode 待验证的角色编码，不能为空
     * @param excludeRoleId 排除的角色ID，用于修改时排除自身，可以为空
     * @return 是否可用，true表示可用，false表示已被使用
     */
    boolean isRoleCodeAvailable(String roleCode, Long excludeRoleId);

    /**
     * 根据数据权限范围查询角色
     * 功能描述：按数据权限范围查询角色，用于数据权限控制和角色筛选
     * 采用技术：权限范围过滤 + 缓存优化
     * 优势：支持数据权限分级管理，提高数据安全性
     * 
     * @param dataScope 数据权限范围，1-全部数据，2-部门数据，3-个人数据
     * @return 指定数据权限范围的角色列表
     */
    List<UserRole> getRolesByDataScope(Integer dataScope);

    /**
     * 获取角色统计信息
     * 功能描述：获取角色相关的统计数据，用于数据分析和监控面板
     * 采用技术：聚合查询 + 缓存优化 + 数据分析
     * 优势：提供实时统计数据，支持业务决策，优化查询性能
     * 
     * @return 统计信息Map，包含各种统计指标
     *         - totalRoles: 总角色数
     *         - enabledRoles: 启用角色数
     *         - disabledRoles: 禁用角色数
     *         - systemRoles: 系统角色数
     *         - customRoles: 自定义角色数
     */
    Map<String, Object> getRoleStatistics();

    /**
     * 复制角色
     * 功能描述：基于现有角色创建新角色，复制权限配置和基本设置
     * 采用技术：对象克隆 + 权限复制 + 唯一性处理
     * 优势：快速创建相似角色，减少重复配置，提高管理效率
     * 
     * @param sourceRoleId 源角色ID，不能为空
     * @param newRoleCode 新角色编码，不能为空
     * @param newRoleName 新角色名称，不能为空
     * @param operatorId 操作人ID，用于审计记录
     * @return 复制创建的新角色对象
     * @throws IllegalArgumentException 当源角色不存在或新角色编码已存在时抛出
     * @throws RuntimeException 当复制过程中发生系统错误时抛出
     */
    UserRole copyRole(Long sourceRoleId, String newRoleCode, String newRoleName, Long operatorId);

    /**
     * 获取角色的用户数量
     * 功能描述：统计指定角色拥有的用户数量，用于角色使用情况分析
     * 采用技术：关联查询 + 计数统计
     * 优势：提供角色使用统计，支持角色管理决策
     * 
     * @param roleId 角色ID，不能为空
     * @return 拥有该角色的用户数量
     */
    int getUserCountByRole(Long roleId);

    /**
     * 检查角色是否可以删除
     * 功能描述：检查角色是否正在被使用，用于删除前的安全检查
     * 采用技术：关联数据检查 + 业务规则验证
     * 优势：防止误删除正在使用的角色，保证系统稳定性
     * 
     * @param roleId 角色ID，不能为空
     * @return 是否可以删除，true表示可以删除，false表示正在使用不能删除
     */
    boolean canDeleteRole(Long roleId);
}