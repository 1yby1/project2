# 瓶安保项目 (PingAnBao)

瓶安保是一个针对气瓶安全管理、合同签署及续费管理的综合性应用系统。本项目采用前后端分离架构，提供完整的气瓶流转追踪、合同管理、单位信息维护等功能。

## 项目预览

- **前端**: 基于 Vue 3 + TypeScript + Vite + Element Plus。
- **后端**: 基于 Spring Boot 3 + Java 17 + MyBatis Plus + MySQL。

---

## 功能模块

### 1. 合同管理 (`Contract`)
- **合同录入与导入**: 支持手动录入合同信息及 Excel 批量导入。
- **审核流**: 包含合同初审、签署、作废申请等审核流程。
- **电子签署**: 集成签署功能，支持预览与合同文件生成。
- **到期提醒**: 自动识别即将到期的有效合同。

### 2. 气瓶管理 (`Cylinder`)
- **档案管理**: 记录气瓶基础信息、注册日期及状态。
- **流转追踪**: 记录气瓶的动作轨迹（充装、检验、报废等）。
- **批量操作**: 支持气瓶登记信息的批量导入。

### 3. 单位与人员管理 (`Unit`)
- **单位信息**: 维护气瓶所有者或使用单位档案。
- **人员绑定**: 记录单位相关的业务办理人员。

### 4. 续费管理 (`Renewal`)
- **策略配置**: 灵活配置续费方案。
- **续费处理**: 针对即将到期的服务进行快捷续费操作。

### 5. 系统设置
- **用户权限**: 基于 Spring Security 的用户认证与鉴权（权限、角色分配）。
- **仪表盘**: 统计展示关键业务指标（如合同数量、气瓶存量等）。

---

## 技术栈

### 后端 (Project)
- **核心框架**: Spring Boot 3.5.3
- **持久层**: MyBatis Plus 3.5.7, Druid 数据库连接池
- **安全性**: Spring Security + JJWT (Token 验证)
- **数据库**: MySQL 8.x, Redis (缓存)
- **工具库**: Lombok, Apache POI (Excel 处理), Aliyun SDK (可能用于短信或存储)
- **开发工具**: Java 17, Maven

### 前端 (Front)
- **核心框架**: Vue 3.x (Composition API)
- **构建工具**: Vite
- **UI 组件库**: Element Plus
- **状态管理**: Pinia
- **路由管理**: Vue Router
- **样式**: TailwindCSS, Sass
- **网络请求**: Axios

---

## 快速开始

### 环境依赖
- JDK 17
- MySQL 8.0+
- Redis
- Node.js 18+ & npm/pnpm

### 后端配置 (`/project`)
1. 修改 `src/main/resources/application.yml` 中的数据库连接与 Redis 配置。
2. 运行 `pinganbao_db.sql` 初始化数据库结构及数据。
3. 执行 Maven 命令启动：
   ```bash
   mvn clean spring-boot:run
   ```

### 前端配置 (`/front`)
1. 进入前端目录：
   ```bash
   cd front
   ```
2. 安装依赖：
   ```bash
   npm install
   ```
3. 启动开发服务器：
   ```bash
   npm run dev
   ```


---

## 项目结构说明

```text
├── front/                  # 前端源代码
│   ├── src/
│   │   ├── api/            # 接口定义 (axios)
│   │   ├── views/          # 业务页面 (合同、气瓶、单位等)
│   │   └── components/     # 公共组件
├── project/                # 后端源代码
│   ├── src/main/java/      # Spring Boot 业务逻辑
│   ├── src/main/resources/ # 配置文件及 MyBatis Mapper
│   └── pinganbao_db.sql    # 数据库初始化脚本
└── 用户需求列表.xlsx        # 项目原始需求文档
```

## 注 阿里云的密钥和相关配置已被移除，请在实际部署时自行添加到 `application.yml` 中，并确保安全存储。