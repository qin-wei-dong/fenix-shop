package com.fenix.shop.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fenix.shop.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问层接口
 * 功能描述：提供用户实体的数据库操作方法，完全基于MyBatis Plus实现
 * 采用技术：MyBatis Plus BaseMapper，使用LambdaQueryWrapper和LambdaUpdateWrapper
 * 技术优势：
 * 1. 继承BaseMapper获得基础CRUD操作，减少重复代码
 * 2. 完全类型安全的查询构造器，避免字段名拼写错误
 * 3. 支持分页查询和条件构造器，提高查询灵活性
 * 4. 自动处理逻辑删除和乐观锁
 * 5. 无需手写SQL，降低维护成本
 * 6. 支持lambda表达式，代码更加简洁优雅
 * 
 * 注意：所有查询逻辑都通过Service层的LambdaQueryWrapper实现，
 *      不在Mapper层编写自定义SQL，保持数据层的纯净和统一
 * 
 * @author fenix
 * @date 2024-12-19
 * @version 2.0 - 重构为纯MyBatis Plus实现
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    // 继承BaseMapper即可获得所有基础CRUD操作
    // 所有复杂查询通过Service层的LambdaQueryWrapper和LambdaUpdateWrapper实现
    // 保持Mapper层的简洁性和统一性
}