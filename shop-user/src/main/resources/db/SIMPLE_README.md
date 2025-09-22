# 简化版用户管理系统

## 概述

这是一个简化但完整的用户管理系统，专为快速启动项目设计。包含核心的用户管理功能，同时预留了后续扩展的空间。

## 🎯 设计目标

- **简单实用**：包含必要的用户管理功能
- **快速启动**：一个脚本完成所有初始化
- **预留扩展**：支持后续渐进式完善
- **生产可用**：满足基本的生产环境需求

## 📋 功能特性

### ✅ 已实现功能

1. **用户基础管理**
   - 用户注册、登录、信息管理
   - 用户名、手机号、邮箱唯一性约束
   - 用户状态管理（正常/禁用/删除）

2. **简单权限管理**
   - 基于角色的权限控制（RBAC）
   - 预置角色：超级管理员、管理员、普通用户
   - 用户角色关联管理

3. **安全特性**
   - 密码哈希存储（建议使用bcrypt）
   - 登录验证函数
   - 最后登录时间记录

4. **便民功能**
   - 自动更新时间戳
   - 用户信息视图（包含角色）
   - 常用查询函数

### 🔄 后续可扩展功能

1. **认证增强**
   - 多因子认证（MFA）
   - 登录历史记录
   - 设备管理

2. **业务扩展**
   - 用户画像标签
   - 积分会员体系
   - 个性化设置

3. **性能优化**
   - 分库分表
   - 读写分离
   - 缓存策略

## 🗃️ 表结构说明

### 1. t_user - 用户基础信息表
```sql
主要字段：
- user_id: 用户ID（主键）
- username: 用户名（唯一）
- password: 密码哈希
- mobile: 手机号（唯一）
- email: 邮箱（唯一）
- nickname: 昵称
- status: 状态（1正常 2禁用 3删除）
```

### 2. t_user_role - 用户角色表
```sql
主要字段：
- id: 角色ID（主键）
- role_name: 角色名称
- role_code: 角色编码（唯一）
- description: 角色描述
```

### 3. t_user_role_rel - 用户角色关联表
```sql
主要字段：
- user_id: 用户ID
- role_id: 角色ID
```

## 🚀 快速开始

### 1. 执行初始化脚本
```bash
psql -d your_database -f simple_user_system.sql
```

### 2. 默认账号
- **用户名**: admin
- **密码**: admin123
- **角色**: 超级管理员

### 3. 基本使用示例

#### 用户注册
```sql
INSERT INTO "public"."t_user" (
    user_id, username, password, nickname, mobile, email
) VALUES (
    2, 'testuser', '$2a$10$hashed_password', '测试用户', 
    '13800138000', 'test@example.com'
);
```

#### 用户登录验证
```sql
SELECT * FROM validate_user_login('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi');
```

#### 查询用户信息（包含角色）
```sql
SELECT * FROM v_user_info WHERE username = 'admin';
```

#### 为用户分配角色
```sql
INSERT INTO "public"."t_user_role_rel" (user_id, role_id) 
VALUES (2, 3);  -- 分配普通用户角色
```

## 🔧 常用函数

### 1. validate_user_login(username, password)
验证用户登录，返回用户信息和角色列表

### 2. update_last_login_time(user_id)
更新用户最后登录时间

### 3. check_username_exists(username)
检查用户名是否已存在

## 📈 扩展建议

### 阶段1：当前版本（满足基本需求）
- ✅ 用户基础管理
- ✅ 简单权限控制
- ✅ 基础安全特性

### 阶段2：安全增强（用户量增长后）
```sql
-- 添加登录历史表
CREATE TABLE t_user_login_history (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    login_time TIMESTAMP(3) DEFAULT CURRENT_TIMESTAMP,
    login_ip INET,
    user_agent TEXT,
    login_result SMALLINT  -- 1成功 2失败
);

-- 添加密码重置功能
ALTER TABLE t_user ADD COLUMN password_reset_token VARCHAR(128);
ALTER TABLE t_user ADD COLUMN password_reset_expire TIMESTAMP(3);
```

### 阶段3：业务扩展（业务复杂后）
```sql
-- 添加用户扩展信息表
CREATE TABLE t_user_profile (
    user_id BIGINT PRIMARY KEY,
    real_name VARCHAR(50),
    gender SMALLINT,
    birthday DATE,
    points INTEGER DEFAULT 0,
    member_level SMALLINT DEFAULT 1
);

-- 添加用户设置表
CREATE TABLE t_user_settings (
    user_id BIGINT PRIMARY KEY,
    language VARCHAR(10) DEFAULT 'zh-CN',
    timezone VARCHAR(50) DEFAULT 'Asia/Shanghai',
    notification_enabled BOOLEAN DEFAULT TRUE
);
```

### 阶段4：性能优化（大规模用户后）
- 分库分表策略
- 读写分离配置
- Redis缓存集成
- 搜索引擎集成

## 🔒 安全建议

### 1. 密码安全
```java
// 使用bcrypt加密密码
String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));

// 验证密码
boolean isValid = BCrypt.checkpw(plainPassword, hashedPassword);
```

### 2. 输入验证
- 用户名：3-50字符，字母数字下划线
- 密码：6-20字符，包含字母和数字
- 手机号：11位数字
- 邮箱：标准邮箱格式

### 3. 权限控制
```java
// 检查用户权限
public boolean hasPermission(Long userId, String permission) {
    // 查询用户角色
    List<String> roles = getUserRoles(userId);
    // 检查角色权限
    return checkRolePermission(roles, permission);
}
```

## 📊 监控建议

### 1. 关键指标
- 用户注册量
- 活跃用户数
- 登录成功率
- 密码重置频率

### 2. 性能监控
```sql
-- 查询慢查询
SELECT query, mean_time, calls 
FROM pg_stat_statements 
WHERE query LIKE '%t_user%' 
ORDER BY mean_time DESC;

-- 查看表大小
SELECT 
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) as size
FROM pg_tables 
WHERE tablename LIKE 't_user%';
```

## 🛠️ 开发建议

### 1. 代码结构
```
shop-user/
├── controller/     # 控制器层
├── service/        # 业务逻辑层
├── repository/     # 数据访问层
├── model/          # 数据模型
└── config/         # 配置类
```

### 2. API设计
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody UserRegisterDTO dto);
    
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto);
    
    @GetMapping("/{id}")
    public Result<UserVO> getUserInfo(@PathVariable Long id);
    
    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody UserUpdateDTO dto);
}
```

### 3. 配置示例
```yaml
# application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/fenix_shop
    username: your_username
    password: your_password
    
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    
# 用户相关配置
user:
  password:
    min-length: 6
    max-length: 20
  login:
    max-attempts: 5
    lock-duration: 30m
```

## 📞 技术支持

如果在使用过程中遇到问题：

1. **查看日志**：检查数据库和应用日志
2. **性能分析**：使用 `EXPLAIN ANALYZE` 分析慢查询
3. **数据一致性**：定期检查外键约束和数据完整性

---

**版本**: 1.0  
**适用场景**: 中小型项目快速启动  
**数据库**: PostgreSQL 12+  
**预计支持**: 10万用户以内