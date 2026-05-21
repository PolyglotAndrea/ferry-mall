<template>
  <view v-if="store" class="card store">
    <image :src="store.logoUrl" />
    <view class="name">{{ store.name }}</view>
    <view class="desc">{{ store.description }}</view>
    <view>店铺评分：{{ store.score }}</view>
  </view>
</template>
<script setup lang="ts">
import { ref } from 'vue'
import { useLoad } from '@tarojs/taro'
import { getStoreDetail, type StoreResp } from '@/api/store'
const store = ref<StoreResp>()
useLoad(async (query) => { store.value = await getStoreDetail(Number(query.id || 1)) })
</script>
<style>.store{text-align:center}.store image{width:140px;height:140px;border-radius:28px}.name{font-size:36px;font-weight:800;margin:18px}.desc{color:#64748b;margin-bottom:18px}</style>
