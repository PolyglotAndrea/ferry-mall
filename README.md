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

## 快速启动

### 后端

```bash
cd ferry-mall-server
mvn clean package -DskipTests
mvn -pl ferry-server spring-boot:run
```

默认端口 `48080`，自动初始化数据库（H2）。

### 管理后台

```bash
cd ferry-mall-admin
bun install
bun run dev
```

### 小程序端

```bash
cd ferry-mall-miniapp
bun install
bun run dev:weapp
```

## 接口文档

| 端点 | 说明 |
|------|------|
| `GET /admin-api/system/auth/profile` | 管理员信息 |
| `POST /app-api/member/auth/login` | 微信登录 |
| `GET /app-api/product/banner/list` | Banner 列表 |
| `GET /app-api/product/spu/page` | 商品分页 |
| `POST /app-api/order/create` | 创建订单 |

## 数据库

默认使用 H2 内存数据库（开发环境），schema 自动初始化。生产环境可切换为 MySQL。

关键表：
- `sys_tenant` / `sys_user` / `sys_role` / `sys_menu` — 租户与权限
- `member_user` / `member_level` / `member_integral_record` — 会员体系
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
