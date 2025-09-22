package com.fenix.shop.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fenix.shop.user.entity.UserRoleRel;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关联数据访问层接口
 * 功能描述：提供用户角色关联实体的数据库操作方法，管理用户与角色的多对多关系
 * 采用技术：MyBatis Plus BaseMapper，使用LambdaQueryWrapper和LambdaUpdateWrapper
 * 
 * @author fenix
 * @date 2025-01-27
 * @version 2.0 - 重构为纯MyBatis Plus实现
 */
@Mapper
public interface UserRoleRelMapper extends BaseMapper<UserRoleRel> {
    // 继承BaseMapper即可获得所有基础CRUD操作
    // 所有复杂查询通过Service层的LambdaQueryWrapper和LambdaUpdateWrapper实现
}