# Ferry-Mall 渡船商城

Ferry-Mall 是一套原创多端商城系统，参考 yudao-cloud 的模块化思想重新设计，包含完整的管理后台、后端服务和小程序端。

## 项目结构

```
ferry-mall/
├── ferry-mall-server/   # Java 21 + Spring Boot 3 后端服务
├── ferry-mall-admin/    # Vue 3 + Vite + Element Plus 管理后台
├── ferry-mall-miniapp/  # Taro 4 + Vue 3 + TypeScript 小程序端
└── design/              # 架构设计文档
```

## 技术栈

### 后端
- Java 21 + Spring Boot 3.3
- Maven 多模块架构（framework + module-api + module-server）
- MyBatis Plus + H2/MySQL 多租户
- JWT 认证 + RBAC 权限
- MinIO 文件存储
- XXL-Job 定时任务
- RocketMQ 消息队列
- Flowable 工作流引擎
- Micrometer 链路追踪

### 管理后台
- Vue 3 + Vite + TypeScript
- Element Plus UI
- Pinia 状态管理
- Axios 请求封装

### 小程序端
- Taro 4 + Vue 3 + TypeScript
- Pinia 状态管理
- 微信登录 + JWT Token
- 完整购物流程

## 功能模块

### 已完成功能

| 模块 | 功能 |
|------|------|
| **多租户** | TenantContext 隔离、MyBatis Plus 拦截器 |
| **RBAC** | 角色/菜单/部门、按钮级权限 @RequirePermission |
| **会员** | 微信登录、个人信息、签到、积分、等级 |
| **商品** | SPU/SKU、分类树、Banner、评价、收藏、足迹 |
| **订单** | 创建、支付、发货、收货、取消、退款售后 |
| **营销** | 优惠券（领取/券包）、秒杀、拼团、砍价 |
| **分销** | 推广员申请、佣金记录、团队列表 |
| **积分商城** | 积分商品兑换 |
| **消息通知** | 系统消息、未读计数 |
| **直播** | 直播间列表 |
| **商家** | 商家入驻申请 |
| **数据报表** | 交易概览、商品排行、会员增长 |
| **基础设施** | 文件上传、短信服务、定时任务、代码生成 |

## 前端平台支持

当前小程序端**默认支持微信小程序**，Taro 4 框架可扩展多平台：

| 平台 | 状态 | 命令 |
|------|------|------|
| 微信小程序 | 已支持 | `npm run dev:weapp` |
| H5 | 已支持 | `npm run dev:h5` |
| 支付宝小程序 | 需安装插件 | `npm i @tarojs/plugin-platform-alipay` |
| 百度小程序 | 需安装插件 | `npm i @tarojs/plugin-platform-swan` |
| 字节跳动 | 需安装插件 | `npm i @tarojs/plugin-platform-tt` |
| QQ 小程序 | 需安装插件 | `npm i @tarojs/plugin-platform-qq` |

在 `ferry-mall-miniapp/config/index.ts` 的 `plugins` 数组中添加对应插件即可启用。

## 快速启动

### 方式一：一键脚本（推荐）

```bash
# 启动全部服务（后端 + Admin + Redis）
./start.sh

# 或分别启动
./start.sh backend     # 仅启动后端
./start.sh admin       # 仅启动 Admin 前端
./start.sh miniapp     # 仅编译小程序（需微信开发者工具导入 dist 目录）
```

### 方式二：手动启动

#### 1. 启动 Redis（如未安装）

```bash
docker run -d -p 6379:6379 --name ferry-redis redis:7-alpine
```

#### 2. 启动后端

```bash
cd ferry-mall-server

# 本地开发模式（H2 内存数据库，自动初始化 schema + 示例数据）
SPRING_PROFILES_ACTIVE=local mvn spring-boot:run -pl ferry-server

# 或使用 MySQL（需提前创建数据库并修改 application-mysql.yml）
# SPRING_PROFILES_ACTIVE=mysql mvn spring-boot:run -pl ferry-server
```

服务启动后：
- API 地址：`http://localhost:48080`
- Swagger 文档：`http://localhost:48080/doc.html`
- H2 Console（local 模式）：`http://localhost:48080/h2-console`
- JDBC URL: `jdbc:h2:mem:ferry_mall`
- 账号: `sa` / 密码:（空）

#### 3. 启动管理后台

```bash
cd ferry-mall-admin
npm install
npm run dev
```

访问：`http://localhost:5173`

默认账号：`admin` / `admin123`

#### 4. 编译小程序

```bash
cd ferry-mall-miniapp
npm install
npm run dev:weapp   # 微信小程序
npm run dev:h5      # H5
```

微信小程序：用微信开发者工具导入 `ferry-mall-miniapp/dist` 目录

## 开发阶段

| 阶段 | 内容 | 状态 |
|------|------|------|
| P1 | 核心商城骨架（商品、订单、支付、用户） | 完成 |
| P2 | 服务端购物车、订单搜索筛选、营销活动管理、售后处理 | 完成 |
| P3 | 小程序端页面完善（营销活动、物流、消息、个人中心） | 完成 |
| P4 | Admin 管理后台核心页面（Dashboard、商品、订单、会员） | 完成 |
| P5 | 首页增强、搜索/分类完善、分销/积分商城/直播 | 进行中 |

## API 端点

| 端 | 前缀 | 认证 |
|----|------|------|
| 小程序 | `/app-api/**` | JWT Token（`Authorization: Bearer <token>`） |
| 管理后台 | `/admin-api/**` | JWT Token + RBAC 权限校验 |

### 关键接口

| 接口 | 说明 |
|------|------|
| `POST /app-api/member/auth/login` | 微信登录 |
| `GET /app-api/product/banner/list` | Banner 列表 |
| `GET /app-api/product/spu/page` | 商品分页（支持 keyword/sort） |
| `GET /app-api/product/spu/{id}` | 商品详情 |
| `GET /app-api/member/cart/list` | 购物车列表 |
| `POST /app-api/order/create` | 创建订单（支持 couponId） |
| `POST /app-api/payment/prepare` | 支付预下单 |
| `GET /admin-api/statistics/overview` | 运营概览 |
| `GET /admin-api/product/spu/page` | 商品管理列表 |
| `GET /admin-api/order/page` | 订单管理列表 |

## 数据库

默认使用 H2 内存数据库（开发环境），启动时自动执行 `schema.sql` + `data.sql` 初始化。生产环境可切换为 MySQL。

关键表：
- `sys_tenant` / `sys_user` / `sys_role` / `sys_menu` — 租户与权限
- `member_user` / `member_level` / `member_integral_record` — 会员体系
- `cart` / `member_favorite` / `member_footprint` — 购物车与互动
- `product_spu` / `product_sku` / `product_banner` / `product_comment` — 商品
- `order_info` / `order_item` — 订单
- `marketing_coupon` / `member_coupon` / `seckill_activity` / `groupon_activity` / `bargain_activity` — 营销
- `commission_user` / `commission_record` — 分销
- `points_product` / `points_exchange` — 积分商城
- `message_record` — 消息通知
- `live_room` — 直播间

## 原创合规说明

本项目仅借鉴通用架构分层思想，业务命名、领域模型、接口设计、前后端代码均重新设计实现，不复制开源商城源码，便于后续以二次深化版本申请软件著作权。

## License

MIT
