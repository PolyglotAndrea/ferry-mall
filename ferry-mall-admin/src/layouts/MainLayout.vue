<template>
  <el-container class="layout">
    <el-aside width="220px" class="aside">
      <div class="brand">Ferry Mall</div>
      <el-menu router default-active="/dashboard" background-color="#111827" text-color="#cbd5e1" active-text-color="#fff">
        <el-menu-item index="/dashboard"><el-icon><DataBoard /></el-icon><span>控制台</span></el-menu-item>
        <el-menu-item index="/product/spu"><el-icon><Goods /></el-icon><span>商品列表</span></el-menu-item>
        <el-menu-item index="/order/list"><el-icon><Tickets /></el-icon><span>订单列表</span></el-menu-item>
        <el-menu-item index="/member/list"><el-icon><User /></el-icon><span>会员列表</span></el-menu-item>
        <el-menu-item index="/merchant/list"><el-icon><OfficeBuilding /></el-icon><span>商家审核</span></el-menu-item>
        <el-menu-item index="/store/list"><el-icon><Shop /></el-icon><span>店铺管理</span></el-menu-item>
        <el-menu-item index="/settlement/bill"><el-icon><Money /></el-icon><span>结算账单</span></el-menu-item>
        <el-menu-item index="/aftermarket/list"><el-icon><Service /></el-icon><span>售后管理</span></el-menu-item>
        <el-menu-item index="/system/user"><el-icon><Setting /></el-icon><span>系统用户</span></el-menu-item>
        <el-menu-item index="/marketing/coupon"><el-icon><Present /></el-icon><span>优惠券</span></el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">原创多端商城管理后台</el-header>
      <el-main><router-view /></el-main>
    </el-container>
  </el-container>
</template>
<style scoped>
.layout { min-height: 100%; }
.aside { background: #111827; }
.brand { color: #fff; font-weight: 700; font-size: 20px; height: 56px; display: flex; align-items: center; padding-left: 20px; }
.header { background: #fff; display: flex; align-items: center; box-shadow: 0 1px 8px rgba(15,23,42,.08); }
</style>
