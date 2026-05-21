# Ferry-Mall API 设计

## 路径分层

| 前缀 | 说明 |
|------|------|
| `/admin-api` | 管理后台接口 |
| `/app-api` | 小程序 / H5 / APP 接口 |
| `/public-api` | 无需登录的公共接口 |

## 统一响应

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1716192000000
}
```

## 管理端核心接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/admin-api/system/auth/login` | 后台登录 |
| GET | `/admin-api/system/auth/profile` | 当前管理员信息 |
| GET | `/admin-api/system/menu/tree` | 菜单树 |
| GET | `/admin-api/product/spu/page` | 商品分页 |
| POST | `/admin-api/product/spu/create` | 创建商品 |
| GET | `/admin-api/order/page` | 订单分页 |
| GET | `/admin-api/statistics/overview` | 运营概览 |

## 小程序核心接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/app-api/product/category/tree` | 分类树 |
| GET | `/app-api/product/spu/page` | 商品列表 |
| GET | `/app-api/product/spu/{id}` | 商品详情 |
| POST | `/app-api/member/auth/login` | 小程序登录 |
| GET | `/app-api/member/profile` | 会员资料 |
| POST | `/app-api/order/create` | 创建订单 |
| GET | `/app-api/order/page` | 我的订单 |
| POST | `/app-api/payment/prepare` | 发起支付 |
