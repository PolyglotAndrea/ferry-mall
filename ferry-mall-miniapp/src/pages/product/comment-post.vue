<template>
  <view class="post-page">
    <view class="product-info" v-if="productName">
      <text class="label">评价商品</text>
      <text class="name">{{ productName }}</text>
    </view>

    <view class="rating-row">
      <text class="label">商品评分</text>
      <view class="stars">
        <text v-for="n in 5" :key="n" class="star" :class="{ active: n <= rating }" @tap="rating = n">
          {{ n <= rating ? '★' : '☆' }}
        </text>
      </view>
      <text class="rating-text">{{ ratingText }}</text>
    </view>

    <textarea v-model="content" class="content-area" placeholder="分享您的使用体验，帮助更多买家~" maxlength="500" />
    <view class="char-count">{{ content.length }}/500</view>

    <view class="image-section">
      <view v-for="(img, idx) in images" :key="idx" class="img-wrapper">
        <image :src="img" class="preview-img" mode="aspectFill" @tap="preview(idx)" />
        <text class="del-img" @tap="removeImg(idx)">×</text>
      </view>
      <view v-if="images.length < 6" class="upload-btn" @tap="chooseImage">
        <text>+</text>
        <text class="upload-tip">{{ images.length }}/6</text>
      </view>
    </view>

    <view class="submit-btn" @tap="onSubmit">提交评价</view>
  </view>
</template>

<script setup lang="ts">
import Taro, { useLoad } from '@tarojs/taro'
import { ref, computed } from 'vue'
import { createProductComment } from '@/api/product'
import { uploadFile } from '@/utils/request'

const spuId = ref(0)
const productName = ref('')
const rating = ref(5)
const content = ref('')
const images = ref<string[]>([])

const ratingText = computed(() => {
  const texts = ['非常差', '差', '一般', '好', '非常好']
  return texts[rating.value - 1]
})

useLoad((query) => {
  spuId.value = Number(query.spuId || 0)
  productName.value = (query.productName as string) || ''
})

function chooseImage() {
  Taro.chooseImage({ count: 6 - images.value.length, sizeType: ['compressed'], sourceType: ['album', 'camera'] }).then(async (res) => {
    for (const path of res.tempFilePaths) {
      try {
        const url = await uploadFile(path)
        images.value.push(url)
      } catch (e: any) {
        Taro.showToast({ title: '上传失败', icon: 'none' })
      }
    }
  })
}

function removeImg(idx: number) {
  images.value.splice(idx, 1)
}

function preview(idx: number) {
  Taro.previewImage({ current: images.value[idx], urls: images.value })
}

async function onSubmit() {
  if (!content.value.trim()) {
    Taro.showToast({ title: '请输入评价内容', icon: 'none' })
    return
  }
  try {
    await createProductComment(spuId.value, content.value, rating.value)
    Taro.showToast({ title: '评价成功', icon: 'success' })
    setTimeout(() => Taro.navigateBack(), 800)
  } catch (e: any) {
    Taro.showToast({ title: e.message || '提交失败', icon: 'none' })
  }
}
</script>

<style scoped>
.post-page { padding: 20px; min-height: 100vh; background: #f8fafc; }
.product-info { padding: 20px; background: #fff; border-radius: 16px; margin-bottom: 16px; }
.product-info .label { font-size: 24px; color: #94a3b8; }
.product-info .name { display: block; font-size: 28px; font-weight: 700; margin-top: 8px; }
.rating-row { display: flex; align-items: center; gap: 16px; padding: 20px; background: #fff; border-radius: 16px; margin-bottom: 16px; }
.rating-row .label { font-size: 28px; font-weight: 600; }
.stars { display: flex; gap: 8px; }
.star { font-size: 40px; color: #e2e8f0; }
.star.active { color: #f59e0b; }
.rating-text { font-size: 24px; color: #f59e0b; margin-left: 8px; }
.content-area { width: 100%; height: 200px; background: #fff; border-radius: 16px; padding: 20px; font-size: 28px; }
.char-count { text-align: right; font-size: 22px; color: #94a3b8; margin-top: 8px; }
.image-section { display: flex; flex-wrap: wrap; gap: 16px; margin-top: 20px; }
.img-wrapper { position: relative; width: 180px; height: 180px; }
.preview-img { width: 100%; height: 100%; border-radius: 12px; background: #f1f5f9; }
.del-img { position: absolute; top: -8px; right: -8px; width: 36px; height: 36px; background: #ef4444; color: #fff; border-radius: 50%; text-align: center; line-height: 36px; font-size: 24px; }
.upload-btn { width: 180px; height: 180px; background: #fff; border-radius: 12px; border: 2px dashed #cbd5e1; display: flex; flex-direction: column; align-items: center; justify-content: center; font-size: 48px; color: #94a3b8; }
.upload-tip { font-size: 22px; margin-top: 4px; }
.submit-btn { margin-top: 40px; text-align: center; padding: 24px 0; background: #ef4444; color: #fff; border-radius: 40px; font-size: 30px; font-weight: 600; }
</style>
