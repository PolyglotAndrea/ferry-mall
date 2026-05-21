<template>
  <view class="apply-page">
    <!-- 提示 -->
    <view class="tip-card">
      <text class="tip-text">填写以下信息申请成为商家，审核通过后即可开店</text>
    </view>

    <!-- 基本信息 -->
    <view class="form-card">
      <view class="section-title">基本信息</view>

      <view class="form-row">
        <text class="label required">商家名称</text>
        <input v-model="form.name" placeholder="请输入商家名称" maxlength="50" />
      </view>

      <view class="form-row">
        <text class="label required">联系人</text>
        <input v-model="form.contactName" placeholder="请输入联系人姓名" maxlength="20" />
      </view>

      <view class="form-row">
        <text class="label required">联系电话</text>
        <input v-model="form.contactMobile" placeholder="请输入手机号" type="number" maxlength="11" />
      </view>

      <view class="form-row block">
        <text class="label">商家简介</text>
      </view>
      <textarea
        v-model="form.intro"
        class="desc-area"
        placeholder="请简要介绍您的店铺经营内容（选填）"
        maxlength="300"
      />
      <view class="char-count">{{ form.intro.length }}/300</view>
    </view>

    <!-- 资质信息 -->
    <view class="form-card">
      <view class="section-title">资质信息</view>

      <!-- 商家 Logo -->
      <view class="form-row block">
        <text class="label">商家 Logo</text>
      </view>
      <view class="image-list">
        <view v-if="logoUrl" class="image-item">
          <image :src="logoUrl" class="upload-img" mode="aspectFill" @tap="previewImage(logoUrl)" />
          <text class="delete-icon" @tap.stop="logoUrl = ''">&#x2715;</text>
        </view>
        <view v-else class="image-item add" @tap="chooseLogo">
          <text class="add-icon">+</text>
          <text class="add-text">上传 Logo</text>
        </view>
      </view>

      <!-- 营业执照 -->
      <view class="form-row">
        <text class="label">营业执照号</text>
        <input v-model="form.licenseNo" placeholder="请输入营业执照编号（选填）" maxlength="50" />
      </view>

      <!-- 资质图片 -->
      <view class="form-row block">
        <text class="label">资质证明</text>
        <text class="sub-label">（营业执照等，最多3张）</text>
      </view>
      <view class="image-list">
        <view v-for="(img, i) in qualificationImages" :key="i" class="image-item">
          <image :src="img" class="upload-img" mode="aspectFill" @tap="previewImage(img)" />
          <text class="delete-icon" @tap.stop="removeQualification(i)">&#x2715;</text>
        </view>
        <view v-if="qualificationImages.length < 3" class="image-item add" @tap="chooseQualification">
          <text class="add-icon">+</text>
          <text class="add-text">添加图片</text>
        </view>
      </view>
    </view>

    <view class="submit-btn" @tap="onSubmit">提交入驻申请</view>
  </view>
</template>

<script setup lang="ts">
import Taro from '@tarojs/taro'
import { reactive, ref } from 'vue'
import { applyMerchant, type MerchantApplyReq } from '@/api/merchant'
import { uploadFile } from '@/utils/request'

const form = reactive({
  name: '',
  contactName: '',
  contactMobile: '',
  intro: '',
  licenseNo: ''
})

const logoUrl = ref('')
const qualificationImages = ref<string[]>([])

function chooseLogo() {
  Taro.chooseImage({
    count: 1,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera']
  }).then(async (res) => {
    try {
      logoUrl.value = await uploadFile(res.tempFilePaths[0])
    } catch (e: any) {
      Taro.showToast({ title: '上传失败', icon: 'none' })
    }
  })
}

function chooseQualification() {
  const remain = 3 - qualificationImages.value.length
  if (remain <= 0) return
  Taro.chooseImage({
    count: remain,
    sizeType: ['compressed'],
    sourceType: ['album', 'camera']
  }).then(async (res) => {
    for (const path of res.tempFilePaths) {
      try {
        const url = await uploadFile(path)
        qualificationImages.value.push(url)
      } catch (e: any) {
        Taro.showToast({ title: '上传失败', icon: 'none' })
      }
    }
  })
}

function removeQualification(index: number) {
  qualificationImages.value.splice(index, 1)
}

function previewImage(url: string) {
  Taro.previewImage({ urls: [url], current: url })
}

function validateMobile(mobile: string): boolean {
  return /^1[3-9]\d{9}$/.test(mobile)
}

function buildLicenseNo(): string {
  const parts: string[] = []
  if (form.licenseNo) parts.push(form.licenseNo)
  if (logoUrl.value) parts.push(`logo:${logoUrl.value}`)
  if (qualificationImages.value.length) parts.push(`qual:${qualificationImages.value.join(',')}`)
  if (form.intro) parts.push(`intro:${form.intro}`)
  return parts.join('|')
}

async function onSubmit() {
  if (!form.name.trim()) {
    Taro.showToast({ title: '请输入商家名称', icon: 'none' })
    return
  }
  if (!form.contactName.trim()) {
    Taro.showToast({ title: '请输入联系人姓名', icon: 'none' })
    return
  }
  if (!form.contactMobile.trim()) {
    Taro.showToast({ title: '请输入联系电话', icon: 'none' })
    return
  }
  if (!validateMobile(form.contactMobile)) {
    Taro.showToast({ title: '请输入正确的手机号', icon: 'none' })
    return
  }

  const payload: MerchantApplyReq = {
    name: form.name.trim(),
    contactName: form.contactName.trim(),
    contactMobile: form.contactMobile.trim(),
    licenseNo: buildLicenseNo() || undefined
  }

  try {
    await applyMerchant(payload)
    Taro.showToast({ title: '申请已提交，请等待审核', icon: 'success' })
    setTimeout(() => Taro.navigateBack(), 800)
  } catch (e: any) {
    Taro.showToast({ title: e.message || '提交失败', icon: 'none' })
  }
}
</script>

<style scoped>
.apply-page { padding: 20px; padding-bottom: 40px; min-height: 100vh; background: #f8fafc; }

.tip-card { padding: 20px 24px; background: #dbeafe; border-radius: 12px; margin-bottom: 20px; }
.tip-text { font-size: 26px; color: #1e40af; }

.form-card { padding: 0 24px; background: #fff; border-radius: 16px; margin-bottom: 20px; }
.section-title { font-size: 30px; font-weight: 700; padding: 24px 0 12px; color: #1e293b; }

.form-row { display: flex; align-items: center; padding: 24px 0; border-bottom: 1px solid #f1f5f9; }
.form-row:last-child { border-bottom: 0; }
.form-row.block { display: block; padding-bottom: 12px; border-bottom: 0; }

.label { font-size: 28px; font-weight: 600; width: 180px; flex-shrink: 0; color: #1e293b; }
.label.required::before { content: '* '; color: #ef4444; }

input { flex: 1; font-size: 28px; color: #1e293b; }
input::placeholder { color: #94a3b8; }

.desc-area { width: 100%; height: 180px; background: #f8fafc; border-radius: 12px; padding: 20px; font-size: 26px; color: #1e293b; }
.char-count { text-align: right; font-size: 22px; color: #94a3b8; padding: 8px 0 16px; }

.sub-label { font-size: 24px; color: #94a3b8; margin-left: 8px; font-weight: normal; }

.image-list { display: flex; flex-wrap: wrap; gap: 16px; padding-bottom: 24px; }
.image-item { position: relative; width: 160px; height: 160px; border-radius: 12px; overflow: hidden; background: #f1f5f9; }
.upload-img { width: 100%; height: 100%; }
.delete-icon { position: absolute; top: 4px; right: 4px; width: 36px; height: 36px; background: rgba(0,0,0,0.5); color: #fff; font-size: 20px; display: flex; align-items: center; justify-content: center; border-radius: 50%; }
.image-item.add { display: flex; flex-direction: column; align-items: center; justify-content: center; border: 2px dashed #cbd5e1; background: #fff; }
.add-icon { font-size: 48px; color: #94a3b8; line-height: 1; }
.add-text { font-size: 22px; color: #94a3b8; margin-top: 4px; }

.submit-btn { margin-top: 40px; text-align: center; padding: 24px 0; background: #2563eb; color: #fff; border-radius: 40px; font-size: 30px; font-weight: 600; }
</style>
