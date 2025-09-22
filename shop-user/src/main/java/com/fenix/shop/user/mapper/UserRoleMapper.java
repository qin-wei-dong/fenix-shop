package com.fenix.shop.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fenix.shop.user.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色数据访问层接口
 * 功能描述：提供用户角色实体的数据库操作方法，完全基于MyBatis Plus实现
 * 采用技术：MyBatis Plus BaseMapper，使用LambdaQueryWrapper和LambdaUpdateWrapper
 * 
 * @author fenix
 * @date 2024-12-19
 * @version 2.0 - 重构为纯MyBatis Plus实现
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
    // 继承BaseMapper即可获得所有基础CRUD操作
    // 所有复杂查询通过Service层的LambdaQueryWrapper和LambdaUpdateWrapper实现
}