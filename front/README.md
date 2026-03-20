# 瓶安保合同续约管理系统

基于 Vue 3 + Element Plus 的气瓶合同续约管理系统

## 技术栈

- **框架**: Vue 3 (Composition API)
- **构建工具**: Vue CLI
- **UI组件库**: Element Plus
- **路由**: Vue Router 4
- **状态管理**: Pinia
- **样式**: TailwindCSS + Element Plus
- **图标**: Font Awesome + Element Plus Icons

## 功能模块

### 已实现功能
- ✅ 登录/注册页面
- ✅ 首页概览（数据统计、服务展示）
- ✅ 合同管理（列表、详情、新建）
- ✅ 续约管理
- ✅ 单位管理（仅管理员）
- ✅ 用户管理（仅管理员）
- ✅ 个人中心
- ✅ 消息中心（时间轴、分类、未读提醒）
- ✅ 角色切换（管理员/单位用户）

## 快速开始

### 安装依赖
```bash
npm install
```

### 运行开发服务器
```bash
npm run serve
```

项目将运行在 `http://localhost:8080`

### 构建生产版本
```bash
npm run build
```

### Lints and fixes files
```bash
npm run lint
```

## 默认登录

由于是前端演示项目，输入任意手机号和密码即可登录。

**建议测试账号**:
- 手机号: 13800138000
- 密码: 123456

## 角色切换

登录后可在顶部导航栏切换：
- **管理员视角**: 访问所有功能
- **单位用户视角**: 仅查看自己单位数据

## 项目结构

```
src/
├── assets/          # 静态资源
├── layouts/         # 布局组件
├── router/          # 路由配置
├── stores/          # Pinia状态管理
├── views/           # 页面组件
│   ├── Login.vue
│   ├── Dashboard.vue
│   ├── Contract.vue
│   ├── Renewal.vue
│   ├── UnitManagement.vue
│   ├── UserManagement.vue
│   ├── Profile.vue
│   └── Message.vue
├── App.vue
└── main.js
```

## 待完善功能

- [ ] 后端API接口对接
- [ ] 表单验证
- [ ] 续约流程多步骤组件
- [ ] 文件上传功能
- [ ] PDF导出
- [ ] 电子签名

## Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).
