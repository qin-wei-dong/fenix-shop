# fenix-shop
凤凰商城

## 前端构建说明
- 本仓库采用后端（Maven 多模块）+ 前端子项目（shop-portal）结构。
- 前端的依赖安装与构建仅在 `shop-portal/` 目录执行。

### 常用命令（在 shop-portal/ 目录执行）：
- 安装依赖：`npm ci`（或 `npm install`）
- 本地开发：`npm run dev`
- 生产构建：`npm run build`
- 预览构建：`npm run preview`

## CI 说明（仅针对 shop-portal）
- 工作流文件：`.github/workflows/portal-ci.yml`
- 触发条件：当 `shop-portal/**` 或工作流文件本身发生变更时（push/PR）
- 执行环境：Node.js 20，使用 npm cache（锁定 `shop-portal/package-lock.json`）
- 步骤：`npm ci` → `npm run lint` → `npm run build`
- 并发：同一分支的重复运行会自动取消上一次未完成的任务

### 变更记录
- 2025-09-20：移除 Qodana 代码质量扫描配置与工作流（`qodana.yaml`、`.github/workflows/qodana_code_quality.yml`）。当前仅保留 `shop-portal` 的 CI（lint + build）。

## 后端上传目录与访问（shop-user）
- 访问路径：`/api/user/uploads/**`
- 静态映射来源（双通道）：
  - `classpath:/uploads/`
  - `file:${app.upload.base-dir}`（可配置，默认：`${user.dir}/shop-user/uploads`）
- 配置项：在 `shop-user/src/main/resources/application.yml` 中可通过
  - `app.upload.base-dir: ${user.dir}/shop-user/uploads`
  进行覆盖（生产建议设置为持久化路径，如 `/data/fenix/uploads`）。
- 目录说明：
  - `shop-user/src/main/resources/uploads/`：打包内置的静态资源（示例/兜底图）。
  - `shop-user/uploads/`：运行时写入目录（默认），头像等文件会保存到此处（子目录 `avatars/`）。
- 版本控制：已在 `.gitignore` 忽略通用 `uploads/`，但保留 `shop-user/src/main/resources/uploads/` 入库。
