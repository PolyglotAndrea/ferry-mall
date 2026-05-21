# Ferry-Mall 架构设计文档

> 项目名称：Ferry-Mall（渡船商城）
> 设计日期：2026-05-20
> 版本：v1.0.0
> 目标：原创代码，支持软件著作权申请

---

## 一、设计原则

1. **原创性优先**：所有代码独立设计实现，不复制任何开源商城项目源码，仅借鉴架构思路
2. **模块化可插拔**：业务模块独立，按需装配
3. **单体优先，微服务就绪**：默认单体部署，通过模块拆分预留微服务扩展能力
4. **技术选型前沿**：Java 21 + Spring Boot 3 + Vue 3 + Taro 4 + Bun

---

## 二、整体架构

```
                    +------------------+
                    |   用户终端        |
                    |  微信小程序/APP   |
                    +--------+---------+
                             |
                    +--------v---------+
                    |   Nginx / CDN    |
                    +--------+---------+
                             |
        +--------------------+--------------------+
        |                    |                    |
+-------v-------+   +--------v--------+   +-------v-------+
|  Admin 管理端  |   |  Gateway 网关   |   |  小程序前端    |
|   Vue 3 SPA   |   | Spring Cloud   |   |  Taro + Vue  |
+---------------+   |    Gateway     |   +---------------+
                    +--------+--------+
                             |
                    +--------v--------+
                    |  ferry-mall-    |
                    |    server       |
                    |  (聚合服务容器)  |
                    +--------+--------+
                             |
        +--------------------+--------------------+
        |                    |                    |
+-------v-------+   +--------v--------+   +-------v-------+
|    MySQL 8    |   |     Redis 7     |   |  MinIO / OSS  |
|   主数据库     |   |    缓存/会话    |   |   文件存储    |
+---------------+   +-----------------+   +---------------+
```

---

## 三、后端架构（ferry-mall-server）

### 3.1 模块划分（参考 yudao-cloud 模式）

```
ferry-mall-server/
├── ferry-dependencies/                 # BOM 统一依赖版本管理
├── ferry-framework/                    # 通用框架组件
│   ├── ferry-spring-boot-starter-web/  # Web 通用配置
│   ├── ferry-spring-boot-starter-mybatis/ # MyBatis-Plus 封装
│   ├── ferry-spring-boot-starter-redis/   # Redis 封装
│   ├── ferry-spring-boot-starter-security/ # 安全认证
│   └── ferry-spring-boot-starter-log/     # 日志规范
├── ferry-gateway/                      # API 网关（预留）
├── ferry-server/                       # 聚合服务容器
│   └── src/main/java/
│       └── com/ferry/mall/server/
│           └── FerryMallServer.java    # 启动类
│       └── resources/
│           └── application.yml
└── ferry-module/
    ├── ferry-module-system/            # 系统管理模块
    │   ├── ferry-module-system-api/    # API 接口与 DTO
    │   └── ferry-module-system-server/ # 业务实现
    ├── ferry-module-product/           # 商品中心
    │   ├── ferry-module-product-api/
    │   └── ferry-module-product-server/
    ├── ferry-module-order/             # 订单中心
    │   ├── ferry-module-order-api/
    │   └── ferry-module-order-server/
    ├── ferry-module-member/            # 会员中心
    │   ├── ferry-module-member-api/
    │   └── ferry-module-member-server/
    ├── ferry-module-marketing/         # 营销中心
    │   ├── ferry-module-marketing-api/
    │   └── ferry-module-marketing-server/
    ├── ferry-module-payment/           # 支付中心
    │   ├── ferry-module-payment-api/
    │   └── ferry-module-payment-server/
    ├── ferry-module-statistics/        # 数据中心
    │   ├── ferry-module-statistics-api/
    │   └── ferry-module-statistics-server/
    ├── ferry-module-merchant/          # 商家中心：入驻、审核、资质
    │   ├── ferry-module-merchant-api/
    │   └── ferry-module-merchant-server/
    ├── ferry-module-store/             # 店铺中心：店铺资料、评分、营业状态
    │   ├── ferry-module-store-api/
    │   └── ferry-module-store-server/
    ├── ferry-module-settlement/        # 结算中心：佣金、商家账单、打款状态
    │   ├── ferry-module-settlement-api/
    │   └── ferry-module-settlement-server/
    ├── ferry-module-logistics/         # 物流中心：发货、轨迹、配送公司
    │   ├── ferry-module-logistics-api/
    │   └── ferry-module-logistics-server/
    └── ferry-module-aftermarket/       # 售后中心：退款、退货、售后审核
        ├── ferry-module-aftermarket-api/
        └── ferry-module-aftermarket-server/
```

### 3.2 技术栈

| 层级 | 技术选型 | 版本 |
|------|---------|------|
| 基础框架 | Spring Boot | 3.3.x |
| JDK | Java | 21 LTS |
| ORM | MyBatis-Plus | 3.5.7 |
| 数据库 | MySQL | 8.0 |
| 缓存 | Redis + Redisson | 7.x |
| 安全 | Spring Security + JWT | 6.x |
| 消息队列 | RocketMQ | 5.x |
| 搜索引擎 | Elasticsearch | 8.x |
| 定时任务 | XXL-Job | 2.4.x |
| 对象存储 | MinIO | 最新 |
| API 文档 | SpringDoc OpenAPI | 2.x |
| 构建工具 | Maven | 3.9+ |

### 3.3 核心设计规范

**统一响应格式**：
```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1716192000000
}
```

**统一异常处理**：
- 业务异常：`FerryBusinessException`
- 系统异常：`FerrySystemException`
- 统一由 GlobalExceptionHandler 处理

**统一分页**：
```java
public class PageParam {
    private Integer pageNo = 1;
    private Integer pageSize = 10;
}

public class PageResult<T> {
    private List<T> list;
    private Long total;
    private Integer pages;
}
```

---

## 四、Admin 管理端（ferry-mall-admin）

### 4.1 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.4+ | 框架 |
| Vite | 5.x | 构建工具 |
| TypeScript | 5.x | 类型系统 |
| Pinia | 2.x | 状态管理 |
| Vue Router | 4.x | 路由 |
| Element Plus | 2.x | UI 组件库 |
| Axios | 1.x | HTTP 客户端 |
| ECharts | 5.x | 数据可视化 |

### 4.2 目录结构

```
ferry-mall-admin/
├── public/
├── src/
│   ├── api/              # API 接口定义
│   │   ├── system/
│   │   ├── product/
│   │   ├── order/
│   │   ├── member/
│   │   ├── marketing/
│   │   └── statistics/
│   ├── assets/           # 静态资源
│   ├── components/       # 公共组件
│   │   ├── FerryTable/   # 统一表格
│   │   ├── FerryForm/    # 统一表单
│   │   ├── FerrySearch/  # 统一搜索
│   │   └── FerryUpload/  # 统一上传
│   ├── composables/      # 组合式函数
│   ├── directives/       # 自定义指令
│   ├── layouts/          # 布局组件
│   │   ├── MainLayout/   # 主布局
│   │   └── LoginLayout/  # 登录布局
│   ├── router/           # 路由配置
│   ├── stores/           # Pinia Store
│   │   ├── user.ts
│   │   ├── permission.ts
│   │   └── app.ts
│   ├── styles/           # 全局样式
│   ├── utils/            # 工具函数
│   │   ├── request.ts    # Axios 封装
│   │   ├── auth.ts       # 认证工具
│   │   └── format.ts     # 格式化
│   ├── views/            # 页面组件
│   │   ├── login/
│   │   ├── dashboard/
│   │   ├── system/       # 系统管理
│   │   ├── product/      # 商品管理
│   │   ├── order/        # 订单管理
│   │   ├── member/       # 会员管理
│   │   ├── marketing/    # 营销管理
│   │   └── statistics/   # 数据统计
│   ├── App.vue
│   └── main.ts
├── types/                # 全局类型定义
├── .env
├── .env.production
├── index.html
├── package.json
├── tsconfig.json
└── vite.config.ts
```

### 4.3 页面规划

| 模块 | 页面 |
|------|------|
| 系统管理 | 用户管理、角色管理、菜单管理、部门管理、字典管理、操作日志、登录日志 |
| 商品管理 | 商品分类、商品品牌、商品列表、SKU管理、商品审核、库存管理 |
| 订单管理 | 订单列表、订单详情、退款售后、发货管理、物流跟踪 |
| 会员管理 | 会员列表、会员等级、积分管理、会员统计 |
| 营销管理 | 优惠券、满减满折、秒杀活动、拼团活动、分销管理 |
| 数据统计 | 交易概览、商品分析、用户分析、营销效果 |

---

## 五、小程序端（ferry-mall-miniapp）

### 5.1 技术选型

| 技术 | 说明 |
|------|------|
| Taro 4 | 多端统一开发框架（支持 Vite） |
| Vue 3 | 框架 |
| TypeScript | 类型系统 |
| NutUI | 京东出品，适配小程序的 Vue 组件库 |
| Pinia | 状态管理 |
| **Bun** | 包管理器 + 运行时 + 构建工具 |

### 5.2 为什么选择 Taro + Bun

- Taro 4 支持 Vite 构建，与 Bun 兼容性更好
- NutUI 是京东风格组件库，电商场景匹配度高
- Bun 替代 npm 做包管理，速度提升 5-25 倍
- Bun 运行开发服务器和构建脚本

### 5.3 目录结构

```
ferry-mall-miniapp/
├── src/
│   ├── api/              # API 接口
│   ├── assets/           # 静态资源
│   ├── components/       # 公共组件
│   │   ├── ProductCard/  # 商品卡片
│   │   ├── OrderItem/    # 订单项
│   │   ├── CartItem/     # 购物车项
│   │   └── FerryTabbar/  # 自定义 Tabbar
│   ├── composables/      # 组合式函数
│   ├── pages/            # 页面
│   │   ├── index/        # 首页
│   │   ├── category/     # 分类
│   │   ├── cart/         # 购物车
│   │   ├── user/         # 我的
│   │   ├── product/      # 商品详情
│   │   ├── order/        # 订单相关
│   │   └── search/       # 搜索
│   ├── stores/           # Pinia Store
│   ├── utils/            # 工具函数
│   ├── app.ts            # 应用入口
│   └── app.config.ts     # 全局配置
├── types/                # 类型定义
├── config/               # Taro 配置
│   ├── index.ts          # 开发配置
│   ├── dev.ts
│   └── prod.ts
├── package.json
├── tsconfig.json
└── bunfig.toml           # Bun 配置
```

### 5.4 页面规划

| Tab | 页面 |
|-----|------|
| 首页 | 首页（banner、分类入口、推荐商品、秒杀入口） |
| 分类 | 分类列表（左侧分类树 + 右侧商品列表） |
| 购物车 | 购物车列表、编辑、结算 |
| 我的 | 个人信息、订单入口、优惠券、地址管理、客服 |
| 商品详情 | SKU选择、加入购物车、立即购买 |
| 订单确认 | 地址选择、优惠券、支付方式 |
| 订单列表 | 全部/待付款/待发货/待收货/已完成 |
| 搜索 | 搜索页、搜索历史、热门搜索 |

---

## 六、数据库设计概览

### 6.1 核心表

```
-- 系统模块
sys_user          系统用户表
sys_role          角色表
sys_menu          菜单表
sys_dept          部门表
sys_dict          字典表
sys_dict_item     字典项表
sys_log_operation 操作日志表
sys_log_login     登录日志表

-- 会员模块
member_user       会员用户表
member_level      会员等级表
member_address    会员收货地址表
member_integral   会员积分记录表

-- 商品模块
product_category  商品分类表
product_brand     商品品牌表
product_spu       商品SPU表
product_sku       商品SKU表
product_sku_attr  SKU属性表
product_sku_value SKU属性值表

-- 订单模块
order_info        订单主表
order_item        订单商品项表
order_log         订单日志表
order_refund      退款申请表

-- 营销模块
marketing_coupon      优惠券表
marketing_coupon_log  优惠券领取记录表
marketing_seckill     秒杀活动表
marketing_seckill_item 秒杀商品表
marketing_group       拼团活动表

-- 支付模块
payment_record    支付记录表
payment_channel   支付渠道配置表
```

---

## 七、API 设计规范

### 7.1 接口路径规范

```
/admin-api/     # 管理端接口（需登录）
/app-api/       # 小程序/APP接口（需登录）
/public-api/    # 公共接口（无需登录）
```

### 7.2 接口示例

```
GET    /admin-api/system/user/page          # 用户分页列表
GET    /admin-api/system/user/{id}          # 用户详情
POST   /admin-api/system/user/create        # 创建用户
PUT    /admin-api/system/user/update        # 更新用户
DELETE /admin-api/system/user/{id}          # 删除用户

GET    /app-api/product/spu/page            # 商品分页列表
GET    /app-api/product/spu/{id}            # 商品详情
GET    /app-api/product/category/list       # 分类列表

POST   /app-api/order/create                # 创建订单
GET    /app-api/order/page                  # 订单列表
GET    /app-api/order/{id}                  # 订单详情
POST   /app-api/order/pay                   # 订单支付
```

---

## 八、部署方案

### 8.1 开发环境（Docker Compose）

```yaml
# docker-compose.dev.yml
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: ferry123
      MYSQL_DATABASE: ferry_mall
  redis:
    image: redis:7-alpine
  minio:
    image: minio/minio
```

### 8.2 生产环境

- 后端：Docker 容器化部署，Nginx 反向代理
- Admin：静态资源托管到 CDN 或 Nginx
- 小程序：编译后上传至微信小程序后台

---

## 九、开发计划

| 阶段 | 内容 | 预估工期 |
|------|------|---------|
| Phase 1 | 框架搭建（dependencies + framework + system模块） | 3天 |
| Phase 2 | 商品中心 + 会员中心 | 5天 |
| Phase 3 | 订单中心 + 支付中心 | 5天 |
| Phase 4 | 营销中心 + 数据中心 | 4天 |
| Phase 5 | Admin 管理端开发 | 7天 |
| Phase 6 | 小程序前端开发 | 7天 |
| Phase 7 | 联调测试 + 部署 | 3天 |
| **总计** | | **约 34 天** |
