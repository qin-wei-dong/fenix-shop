-- =====================================================
-- 简化版用户管理系统 (PostgreSQL版本)
-- 功能: 核心用户管理功能，支持后续渐进式扩展
-- 设计理念: 简单实用 + 预留扩展 + 渐进演进
-- =====================================================

-- =====================================================
-- 1. 用户基础信息表 (核心表)
-- =====================================================
DROP TABLE IF EXISTS "public"."t_user" CASCADE;

CREATE TABLE "public"."t_user" (
    "user_id" BIGINT NOT NULL,                           -- 用户ID，建议使用雪花算法
    "username" VARCHAR(50) NOT NULL,                     -- 用户名，唯一
    "password" VARCHAR(128) NOT NULL,                    -- 密码哈希
    "mobile" VARCHAR(20),                                -- 手机号
    "email" VARCHAR(100),                                -- 邮箱
    "nickname" VARCHAR(50),                              -- 昵称
    "avatar" VARCHAR(255),                               -- 头像URL
    "status" SMALLINT NOT NULL DEFAULT 1,                -- 状态：1正常 2禁用 3删除
    "register_time" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "last_login_time" TIMESTAMP(3),                      -- 最后登录时间
    "created_time" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_time" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 设置表所有者
ALTER TABLE "public"."t_user" OWNER TO "qinweidong";

-- 添加注释
COMMENT ON TABLE "public"."t_user" IS '用户基础信息表 - 简化版，支持后续扩展';
COMMENT ON COLUMN "public"."t_user"."user_id" IS '用户ID，主键';
COMMENT ON COLUMN "public"."t_user"."username" IS '用户名，全局唯一';
COMMENT ON COLUMN "public"."t_user"."password" IS '密码哈希值，建议使用bcrypt';
COMMENT ON COLUMN "public"."t_user"."mobile" IS '手机号，可用于登录';
COMMENT ON COLUMN "public"."t_user"."email" IS '邮箱地址，可用于登录';
COMMENT ON COLUMN "public"."t_user"."nickname" IS '用户昵称，显示名称';
COMMENT ON COLUMN "public"."t_user"."avatar" IS '用户头像URL';
COMMENT ON COLUMN "public"."t_user"."status" IS '用户状态：1正常 2禁用 3删除';
COMMENT ON COLUMN "public"."t_user"."register_time" IS '注册时间';
COMMENT ON COLUMN "public"."t_user"."last_login_time" IS '最后登录时间';
COMMENT ON COLUMN "public"."t_user"."created_time" IS '创建时间';
COMMENT ON COLUMN "public"."t_user"."updated_time" IS '更新时间';

-- 创建主键和约束
ALTER TABLE "public"."t_user" ADD CONSTRAINT "pk_user" PRIMARY KEY ("user_id");
ALTER TABLE "public"."t_user" ADD CONSTRAINT "uk_user_username" UNIQUE ("username");
ALTER TABLE "public"."t_user" ADD CONSTRAINT "uk_user_mobile" UNIQUE ("mobile");
ALTER TABLE "public"."t_user" ADD CONSTRAINT "uk_user_email" UNIQUE ("email");

-- 创建索引
CREATE INDEX "idx_user_status" ON "public"."t_user" USING btree ("status");
CREATE INDEX "idx_user_register_time" ON "public"."t_user" USING btree ("register_time");
CREATE INDEX "idx_user_last_login" ON "public"."t_user" USING btree ("last_login_time");

-- 添加检查约束
ALTER TABLE "public"."t_user" ADD CONSTRAINT "chk_user_status" 
    CHECK ("status" IN (1, 2, 3));

-- =====================================================
-- 2. 用户角色表 (简单的权限管理)
-- =====================================================
DROP TABLE IF EXISTS "public"."t_user_role" CASCADE;

CREATE TABLE "public"."t_user_role" (
    "id" BIGSERIAL NOT NULL,
    "role_name" VARCHAR(50) NOT NULL,                    -- 角色名称
    "role_code" VARCHAR(50) NOT NULL,                    -- 角色编码
    "description" TEXT,                                  -- 角色描述
    "is_active" BOOLEAN DEFAULT TRUE,                    -- 是否激活
    "created_time" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updated_time" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE "public"."t_user_role" OWNER TO "qinweidong";
COMMENT ON TABLE "public"."t_user_role" IS '用户角色表';

-- 主键和约束
ALTER TABLE "public"."t_user_role" ADD CONSTRAINT "pk_user_role" PRIMARY KEY ("id");
ALTER TABLE "public"."t_user_role" ADD CONSTRAINT "uk_user_role_code" UNIQUE ("role_code");

-- =====================================================
-- 3. 用户角色关联表
-- =====================================================
DROP TABLE IF EXISTS "public"."t_user_role_rel" CASCADE;

CREATE TABLE "public"."t_user_role_rel" (
    "id" BIGSERIAL NOT NULL,
    "user_id" BIGINT NOT NULL,
    "role_id" BIGINT NOT NULL,
    "created_time" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE "public"."t_user_role_rel" OWNER TO "qinweidong";
COMMENT ON TABLE "public"."t_user_role_rel" IS '用户角色关联表';

-- 主键和约束
ALTER TABLE "public"."t_user_role_rel" ADD CONSTRAINT "pk_user_role_rel" PRIMARY KEY ("id");
ALTER TABLE "public"."t_user_role_rel" ADD CONSTRAINT "uk_user_role_rel" UNIQUE ("user_id", "role_id");

-- 外键约束
ALTER TABLE "public"."t_user_role_rel" ADD CONSTRAINT "fk_user_role_rel_user" 
    FOREIGN KEY ("user_id") REFERENCES "public"."t_user"("user_id") ON DELETE CASCADE;
ALTER TABLE "public"."t_user_role_rel" ADD CONSTRAINT "fk_user_role_rel_role" 
    FOREIGN KEY ("role_id") REFERENCES "public"."t_user_role"("id") ON DELETE CASCADE;

-- 创建索引
CREATE INDEX "idx_user_role_rel_user_id" ON "public"."t_user_role_rel" USING btree ("user_id");
CREATE INDEX "idx_user_role_rel_role_id" ON "public"."t_user_role_rel" USING btree ("role_id");

-- =====================================================
-- 4. 创建更新时间触发器
-- =====================================================
CREATE OR REPLACE FUNCTION update_updated_time_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- 为用户表创建触发器
CREATE TRIGGER trigger_user_updated_time 
    BEFORE UPDATE ON "public"."t_user" 
    FOR EACH ROW EXECUTE FUNCTION update_updated_time_column();

CREATE TRIGGER trigger_user_role_updated_time 
    BEFORE UPDATE ON "public"."t_user_role" 
    FOR EACH ROW EXECUTE FUNCTION update_updated_time_column();

-- =====================================================
-- 5. 基础功能函数
-- =====================================================

-- 用户登录验证函数
CREATE OR REPLACE FUNCTION validate_user_login(
    p_username VARCHAR(50),
    p_password VARCHAR(128)
) RETURNS TABLE(
    user_id BIGINT,
    username VARCHAR(50),
    nickname VARCHAR(50),
    status SMALLINT,
    roles TEXT[]
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        u.user_id,
        u.username,
        u.nickname,
        u.status,
        ARRAY_AGG(r.role_code) as roles
    FROM "public"."t_user" u
    LEFT JOIN "public"."t_user_role_rel" urr ON u.user_id = urr.user_id
    LEFT JOIN "public"."t_user_role" r ON urr.role_id = r.id AND r.is_active = TRUE
    WHERE u.username = p_username 
    AND u.password = p_password 
    AND u.status = 1
    GROUP BY u.user_id, u.username, u.nickname, u.status;
END;
$$ LANGUAGE plpgsql;

-- 更新最后登录时间
CREATE OR REPLACE FUNCTION update_last_login_time(p_user_id BIGINT)
RETURNS VOID AS $$
BEGIN
    UPDATE "public"."t_user" 
    SET "last_login_time" = CURRENT_TIMESTAMP,
        "updated_time" = CURRENT_TIMESTAMP
    WHERE "user_id" = p_user_id;
END;
$$ LANGUAGE plpgsql;

-- 检查用户名是否存在
CREATE OR REPLACE FUNCTION check_username_exists(p_username VARCHAR(50))
RETURNS BOOLEAN AS $$
DECLARE
    user_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO user_count
    FROM "public"."t_user"
    WHERE "username" = p_username AND "status" != 3;
    
    RETURN user_count > 0;
END;
$$ LANGUAGE plpgsql;

-- =====================================================
-- 6. 初始化基础数据
-- =====================================================

-- 插入默认角色
INSERT INTO "public"."t_user_role" ("role_name", "role_code", "description") VALUES 
('超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限'),
('管理员', 'ADMIN', '系统管理员，拥有管理权限'),
('普通用户', 'USER', '普通用户，基础权限')
ON CONFLICT ("role_code") DO NOTHING;

-- 插入测试管理员用户 (密码: admin123)
INSERT INTO "public"."t_user" (
    "user_id", "username", "password", "nickname", "status"
) VALUES (
    1, 'admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', '系统管理员', 1
) ON CONFLICT ("user_id") DO NOTHING;

-- 为管理员分配角色
INSERT INTO "public"."t_user_role_rel" ("user_id", "role_id") 
SELECT 1, id FROM "public"."t_user_role" WHERE "role_code" = 'SUPER_ADMIN'
ON CONFLICT ("user_id", "role_id") DO NOTHING;

-- =====================================================
-- 7. 创建视图 (便于查询)
-- =====================================================

-- 用户信息视图（包含角色）
CREATE OR REPLACE VIEW "public"."v_user_info" AS
SELECT 
    u.user_id,
    u.username,
    u.mobile,
    u.email,
    u.nickname,
    u.avatar,
    u.status,
    u.register_time,
    u.last_login_time,
    ARRAY_AGG(r.role_code) FILTER (WHERE r.role_code IS NOT NULL) as roles,
    ARRAY_AGG(r.role_name) FILTER (WHERE r.role_name IS NOT NULL) as role_names
FROM "public"."t_user" u
LEFT JOIN "public"."t_user_role_rel" urr ON u.user_id = urr.user_id
LEFT JOIN "public"."t_user_role" r ON urr.role_id = r.id AND r.is_active = TRUE
WHERE u.status != 3  -- 排除已删除用户
GROUP BY u.user_id, u.username, u.mobile, u.email, u.nickname, u.avatar, 
         u.status, u.register_time, u.last_login_time;

COMMENT ON VIEW "public"."v_user_info" IS '用户信息视图，包含角色信息';

-- =====================================================
-- 8. 扩展预留字段 (可选)
-- =====================================================

-- 如果需要为后续扩展预留字段，可以添加以下字段
/*
ALTER TABLE "public"."t_user" ADD COLUMN "ext_data" JSONB;
COMMENT ON COLUMN "public"."t_user"."ext_data" IS '扩展数据字段，JSON格式，用于存储额外信息';

-- 为JSONB字段创建GIN索引
CREATE INDEX "idx_user_ext_data" ON "public"."t_user" USING gin ("ext_data");
*/

-- =====================================================
-- 9. 数据验证
-- =====================================================

-- 验证初始化结果
DO $$
DECLARE
    user_count INTEGER;
    role_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO user_count FROM "public"."t_user";
    SELECT COUNT(*) INTO role_count FROM "public"."t_user_role";
    
    RAISE NOTICE '=== 简化版用户系统初始化完成 ===';
    RAISE NOTICE '用户数量: %', user_count;
    RAISE NOTICE '角色数量: %', role_count;
    RAISE NOTICE '默认管理员: admin/admin123';
    RAISE NOTICE '✅ 系统可以开始使用了！';
END $$;

-- 更新统计信息
ANALYZE "public"."t_user";
ANALYZE "public"."t_user_role";
ANALYZE "public"."t_user_role_rel";

-- =====================================================
-- 使用示例
-- =====================================================

/*
-- 1. 用户注册
INSERT INTO "public"."t_user" (user_id, username, password, nickname, mobile, email) 
VALUES (2, 'testuser', '$2a$10$hashed_password', '测试用户', '13800138000', 'test@example.com');

-- 2. 用户登录验证
SELECT * FROM validate_user_login('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi');

-- 3. 更新登录时间
SELECT update_last_login_time(1);

-- 4. 查询用户信息
SELECT * FROM v_user_info WHERE username = 'admin';

-- 5. 检查用户名是否存在
SELECT check_username_exists('admin');

-- 6. 为用户分配角色
INSERT INTO "public"."t_user_role_rel" (user_id, role_id) VALUES (2, 3);
*/