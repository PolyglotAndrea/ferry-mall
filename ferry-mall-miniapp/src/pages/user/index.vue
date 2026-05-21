<template>
  <view class="user-page">
    <!-- 头部 -->
    <view class="header">
      <view v-if="user.isLoggedIn" class="profile">
        <image :src="user.profile?.avatarUrl" class="avatar" mode="aspectFill" />
        <view class="info">
          <view class="nickname">{{ user.profile?.nickname || 'Ferry用户' }}</view>
          <view class="level">积分 {{ user.profile?.points || 0 }}</view>
        </view>
      </view>
      <view v-else class="profile" @tap="onLogin">
        <image src="https://dummyimage.com/160x160/e5e7eb/666&text=U" class="avatar" mode="aspectFill" />
        <view class="info">
          <view class="nickname">点击登录</view>
          <view class="level">登录后享受更多权益</view>
        </view>
      </view>
    </view>

    <!-- 签到 -->
    <view v-if="user.isLoggedIn" class="sign-card" @tap="user.sign">
      <text class="sign-text">每日签到 + 积分</text>
      <text class="sign-btn">签到</text>
    </view>

    <!-- 订单入口 -->
    <view class="menu-card">
      <view class="menu-title">我的订单</view>
      <view class="order-grid">
        <view class="order-entry" @tap="goOrders(0)">
          <text class="entry-icon">&#x1F4B3;</text>
          <text class="entry-label">待付款</text>
        </view>
        <view class="order-entry" @tap="goOrders(1)">
          <text class="entry-icon">&#x1F4E6;</text>
          <text class="entry-label">待发货</text>
        </view>
        <view class="order-entry" @tap="goOrders(2)">
          <text class="entry-icon">&#x1F69A;</text>
          <text class="entry-label">待收货</text>
        </view>
        <view class="order-entry" @tap="goOrders()">
          <text class="entry-icon">&#x1F4CB;</text>
          <text class="entry-label">全部订单</text>
        </view>
      </view>
    </view>

    <!-- 营销活动 -->
    <view class="menu-card">
      <view class="menu-title">营销活动</view>
      <view class="order-grid">
        <view class="order-entry" @tap="goSeckill">
          <text class="entry-icon">&#x23F0;</text>
          <text class="entry-label">限时秒杀</text>
        </view>
        <view class="order-entry" @tap="goGroupon">
          <text class="entry-icon">&#x1F91D;</text>
          <text class="entry-label">拼团活动</text>
        </view>
        <view class="order-entry" @tap="goBargain">
          <text class="entry-icon">&#x2702;&#xFE0F;</text>
          <text class="entry-label">好友砍价</text>
        </view>
        <view class="order-entry" @tap="goLive">
          <text class="entry-icon">&#x1F3A5;</text>
          <text class="entry-label">直播间</text>
        </view>
      </view>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-card">
      <view class="menu-title">常用功能</view>
      <view class="menu-list">
        <view class="menu-item" @tap="goFavorite">
          <text>&#x2764;&#xFE0F; 我的收藏</text>
          <text class="arrow">&gt;</text>
        </view>
        <view class="menu-item" @tap="goFootprint">
          <text>&#x1F463; 浏览足迹</text>
          <text class="arrow">&gt;</text>
        </view>
        <view class="menu-item" @tap="goAddress">
          <text>&#x1F3E0; 地址管理</text>
          <text class="arrow">&gt;</text>
        </view>
        <view class="menu-item" @tap="goIntegral">
          <text>&#x2B50; 积分明细</text>
          <text class="arrow">&gt;</text>
        </view>
        <view class="menu-item" @tap="goLevel">
          <text>&#x1F3C6; 会员等级</text>
          <text class="arrow">&gt;</text>
        </view>
        <view class="menu-item" @tap="goMyCoupons">
          <text>&#x1F9F7; 我的优惠券</text>
          <text class="arrow">&gt;</text>
        </view>
        <view class="menu-item" @tap="goCoupons">
          <text>&#x1F381; 领券中心</text>
          <text class="arrow">&gt;</text>
        </view>
        <view class="menu-item" @tap="goCommission">
          <text>&#x1F4B0; 推广中心</text>
          <text class="arrow">&gt;</text>
        </view>
        <view class="menu-item" @tap="goPointsMall">
          <text>&#x1F3AF; 积分商城</text>
          <text class="arrow">&gt;</text>
        </view>
        <view class="menu-item" @tap="goMessage">
          <text>&#x1F514; 消息通知</text>
          <text class="arrow">&gt;</text>
        </view>
        <view class="menu-item" @tap="goBindPhone">
          <text>&#x1F4F1; 绑定手机</text>
          <text class="arrow">&gt;</text>
        </view>
        <view v-if="user.isLoggedIn" class="menu-item" @tap="onLogout">
          <text>&#x1F6AA; 退出登录</text>
          <text class="arrow">&gt;</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import Taro from '@tarojs/taro'
import { onShow } from '@tarojs/taro'
import { useUserStore } from '@/stores/user'

const user = useUserStore()

onShow(() => {
  user.checkLogin()
})

async function onLogin() {
  await user.wxLogin()
}

function onLogout() {
  Taro.showModal({ title: '提示', content: '确定退出登录吗？', success: (res) => {
    if (res.confirm) {
      user.logout()
      Taro.showToast({ title: '已退出', icon: 'success' })
    }
  }})
}

function goOrders(status?: number) {
  const url = status !== undefined ? `/pages/order/list?status=${status}` : '/pages/order/list'
  Taro.navigateTo({ url })
}
function goAddress() { Taro.navigateTo({ url: '/pages/address/list' }) }
function goCoupons() { Taro.navigateTo({ url: '/pages/coupon/list' }) }
function goMyCoupons() { Taro.navigateTo({ url: '/pages/coupon/my' }) }
function goIntegral() { Taro.navigateTo({ url: '/pages/integral/list' }) }
function goLevel() { Taro.navigateTo({ url: '/pages/level/index' }) }
function goFavorite() { Taro.navigateTo({ url: '/pages/favorite/list' }) }
function goFootprint() { Taro.navigateTo({ url: '/pages/footprint/list' }) }
function goSeckill() { Taro.navigateTo({ url: '/pages/seckill/index' }) }
function goGroupon() { Taro.navigateTo({ url: '/pages/groupon/index' }) }
function goBargain() { Taro.navigateTo({ url: '/pages/bargain/index' }) }
function goCommission() { Taro.navigateTo({ url: '/pages/commission/index' }) }
function goPointsMall() { Taro.navigateTo({ url: '/pages/points/mall' }) }
function goMessage() { Taro.navigateTo({ url: '/pages/message/list' }) }
function goLive() { Taro.navigateTo({ url: '/pages/live/list' }) }
function goBindPhone() { Taro.navigateTo({ url: '/pages/bind/phone' }) }
</script>

<style scoped>
.user-page { min-height: 100vh; background: #f8fafc; padding-bottom: 40px; }
.header { background: linear-gradient(135deg, #2563eb, #1d4ed8); padding: 60px 32px 40px; }
.profile { display: flex; align-items: center; gap: 24px; }
.avatar { width: 120px; height: 120px; border-radius: 50%; border: 4px solid rgba(255,255,255,0.3); background: #fff; }
.nickname { font-size: 34px; font-weight: 700; color: #fff; }
.level { font-size: 26px; color: rgba(255,255,255,0.8); margin-top: 8px; }
.sign-card { display: flex; justify-content: space-between; align-items: center; padding: 20px 32px; margin: 20px; background: linear-gradient(135deg, #fef3c7, #fde68a); border-radius: 16px; }
.sign-text { font-size: 28px; color: #92400e; font-weight: 600; }
.sign-btn { padding: 10px 32px; background: #d97706; color: #fff; border-radius: 28px; font-size: 26px; }
.menu-card { margin: 0 20px 20px; background: #fff; border-radius: 16px; padding: 24px; }
.menu-title { font-size: 30px; font-weight: 700; margin-bottom: 20px; }
.order-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.order-entry { display: flex; flex-direction: column; align-items: center; padding: 16px 0; }
.entry-icon { font-size: 40px; }
.entry-label { font-size: 22px; color: #475569; margin-top: 8px; }
.menu-list { }
.menu-item { display: flex; justify-content: space-between; align-items: center; padding: 24px 0; border-bottom: 1px solid #f1f5f9; font-size: 28px; }
.menu-item:last-child { border-bottom: 0; }
.arrow { color: #94a3b8; font-size: 28px; }
</style>