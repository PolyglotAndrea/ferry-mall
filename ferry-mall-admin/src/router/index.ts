import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: () => import('@/views/login/LoginView.vue') },
    {
      path: '/',
      component: MainLayout,
      redirect: '/dashboard',
      children: [
        { path: 'dashboard', component: () => import('@/views/dashboard/DashboardView.vue') },
        { path: 'product/spu', component: () => import('@/views/product/ProductListView.vue') },
        { path: 'product/category', component: () => import('@/views/product/CategoryManageView.vue') },
        { path: 'product/banner', component: () => import('@/views/product/BannerManageView.vue') },
        { path: 'product/comment', component: () => import('@/views/product/CommentManageView.vue') },
        { path: 'order/list', component: () => import('@/views/order/OrderListView.vue') },
        { path: 'member/list', component: () => import('@/views/member/MemberListView.vue') },
        { path: 'merchant/list', component: () => import('@/views/merchant/MerchantListView.vue') },
        { path: 'store/list', component: () => import('@/views/store/StoreListView.vue') },
        { path: 'settlement/bill', component: () => import('@/views/settlement/SettlementBillView.vue') },
        { path: 'aftermarket/list', component: () => import('@/views/aftermarket/AftermarketListView.vue') },
        { path: 'system/user', component: () => import('@/views/system/SystemUserListView.vue') },
        { path: 'marketing/coupon', component: () => import('@/views/marketing/CouponListView.vue') }
      ]
    }
  ]
})

export default router
