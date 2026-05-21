<template>
  <view class="edit-page">
    <view class="form-card">
      <view class="form-row">
        <text class="label">收货人</text>
        <input v-model="form.name" placeholder="请输入收货人姓名" maxlength="20" />
      </view>
      <view class="form-row">
        <text class="label">手机号</text>
        <input v-model="form.mobile" placeholder="请输入手机号" type="number" maxlength="11" />
      </view>
      <view class="form-row" @tap="showRegionPicker = true">
        <text class="label">所在地区</text>
        <text class="region-value" :class="{ placeholder: !regionText }">{{ regionText || '请选择省市区' }}</text>
        <text class="arrow">&gt;</text>
      </view>
      <view class="form-row textarea-row">
        <text class="label">详细地址</text>
        <textarea v-model="form.detail" placeholder="请输入街道、门牌号等详细地址" auto-height maxlength="200" />
      </view>
      <view class="form-row switch-row">
        <text class="label">设为默认地址</text>
        <switch :checked="form.isDefault === 1" @change="onSwitchChange" color="#2563eb" />
      </view>
    </view>
    <view class="submit-btn" @tap="onSubmit">保存</view>

    <!-- 省市区选择器 -->
    <view v-if="showRegionPicker" class="region-mask" @tap="showRegionPicker = false">
      <view class="region-panel" @tap.stop>
        <view class="region-header">
          <text class="region-cancel" @tap="showRegionPicker = false">取消</text>
          <text class="region-title">选择地区</text>
          <text class="region-confirm" @tap="confirmRegion">确定</text>
        </view>
        <picker-view class="region-picker" :value="regionIndex" @change="onRegionChange">
          <picker-view-column>
            <view v-for="p in provinces" :key="p" class="picker-item">{{ p }}</view>
          </picker-view-column>
          <picker-view-column>
            <view v-for="c in cities" :key="c" class="picker-item">{{ c }}</view>
          </picker-view-column>
          <picker-view-column>
            <view v-for="d in districts" :key="d" class="picker-item">{{ d }}</view>
          </picker-view-column>
        </picker-view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import Taro, { useLoad } from '@tarojs/taro'
import { ref, computed } from 'vue'
import { addAddress, updateAddress, getAddressList, type AddressItem } from '@/api/address'

// 简化版省市区数据（实际项目应从后端或本地 JSON 加载）
const regionData: Record<string, Record<string, string[]>> = {
  '北京市': {
    '北京市': ['东城区', '西城区', '朝阳区', '丰台区', '石景山区', '海淀区', '门头沟区', '房山区', '通州区', '顺义区', '昌平区', '大兴区', '怀柔区', '平谷区', '密云区', '延庆区']
  },
  '上海市': {
    '上海市': ['黄浦区', '徐汇区', '长宁区', '静安区', '普陀区', '虹口区', '杨浦区', '闵行区', '宝山区', '嘉定区', '浦东新区', '金山区', '松江区', '青浦区', '奉贤区', '崇明区']
  },
  '广东省': {
    '广州市': ['荔湾区', '越秀区', '海珠区', '天河区', '白云区', '黄埔区', '番禺区', '花都区', '南沙区', '从化区', '增城区'],
    '深圳市': ['罗湖区', '福田区', '南山区', '宝安区', '龙岗区', '盐田区', '龙华区', '坪山区', '光明区'],
    '东莞市': ['东城街道', '南城街道', '万江街道', '莞城街道']
  },
  '浙江省': {
    '杭州市': ['上城区', '下城区', '江干区', '拱墅区', '西湖区', '滨江区', '萧山区', '余杭区', '富阳区', '临安区', '桐庐县', '淳安县', '建德市'],
    '宁波市': ['海曙区', '江北区', '北仑区', '镇海区', '鄞州区', '奉化区', '余姚市', '慈溪市', '象山县', '宁海县']
  },
  '江苏省': {
    '南京市': ['玄武区', '秦淮区', '建邺区', '鼓楼区', '浦口区', '栖霞区', '雨花台区', '江宁区', '六合区', '溧水区', '高淳区'],
    '苏州市': ['虎丘区', '吴中区', '相城区', '姑苏区', '吴江区', '常熟市', '张家港市', '昆山市', '太仓市']
  },
  '四川省': {
    '成都市': ['锦江区', '青羊区', '金牛区', '武侯区', '成华区', '龙泉驿区', '青白江区', '新都区', '温江区', '双流区', '郫都区', '新津区', '金堂县', '大邑县', '蒲江县', '都江堰市', '彭州市', '邛崃市', '崇州市', '简阳市']
  }
}

const form = ref({ name: '', mobile: '', province: '', city: '', district: '', detail: '', isDefault: 0 })
const editId = ref<number>(0)
const showRegionPicker = ref(false)
const regionIndex = ref([0, 0, 0])

const provinces = computed(() => Object.keys(regionData))
const cities = computed(() => {
  const p = provinces.value[regionIndex.value[0]] || ''
  return p ? Object.keys(regionData[p] || {}) : []
})
const districts = computed(() => {
  const p = provinces.value[regionIndex.value[0]] || ''
  const c = cities.value[regionIndex.value[1]] || ''
  return (regionData[p] && regionData[p][c]) || []
})

const regionText = computed(() => {
  const { province, city, district } = form.value
  return province && city && district ? `${province} ${city} ${district}` : ''
})

useLoad(async (query) => {
  if (query.id) {
    editId.value = Number(query.id)
    try {
      const list = await getAddressList()
      const item = list.find(a => a.id === editId.value)
      if (item) {
        form.value = {
          name: item.name,
          mobile: item.mobile,
          province: item.province,
          city: item.city,
          district: item.district,
          detail: item.detail,
          isDefault: item.isDefault
        }
        // 尝试匹配 picker 索引
        const pIdx = provinces.value.indexOf(item.province)
        if (pIdx >= 0) {
          regionIndex.value[0] = pIdx
          const cIdx = Object.keys(regionData[item.province] || {}).indexOf(item.city)
          if (cIdx >= 0) {
            regionIndex.value[1] = cIdx
            const dIdx = (regionData[item.province][item.city] || []).indexOf(item.district)
            if (dIdx >= 0) regionIndex.value[2] = dIdx
          }
        }
      }
    } catch { /* ignore */ }
  }
})

function onRegionChange(e: any) {
  const val = e.detail.value as number[]
  regionIndex.value = val
}

function onSwitchChange(e: any) {
  form.value.isDefault = e.detail.value ? 1 : 0
}

function confirmRegion() {
  const p = provinces.value[regionIndex.value[0]] || ''
  const c = cities.value[regionIndex.value[1]] || ''
  const d = districts.value[regionIndex.value[2]] || ''
  form.value.province = p
  form.value.city = c
  form.value.district = d
  showRegionPicker.value = false
}

async function onSubmit() {
  const { name, mobile, province, city, district, detail } = form.value
  if (!name.trim()) {
    Taro.showToast({ title: '请输入收货人姓名', icon: 'none' })
    return
  }
  if (!mobile.trim() || !/^1[3-9]\d{9}$/.test(mobile)) {
    Taro.showToast({ title: '请输入正确的手机号', icon: 'none' })
    return
  }
  if (!province || !city || !district) {
    Taro.showToast({ title: '请选择所在地区', icon: 'none' })
    return
  }
  if (!detail.trim()) {
    Taro.showToast({ title: '请输入详细地址', icon: 'none' })
    return
  }
  try {
    if (editId.value) {
      await updateAddress(editId.value, form.value)
    } else {
      await addAddress(form.value)
    }
    Taro.showToast({ title: '保存成功', icon: 'success' })
    setTimeout(() => Taro.navigateBack(), 800)
  } catch (e: any) {
    Taro.showToast({ title: e.message || '保存失败', icon: 'none' })
  }
}
</script>

<style scoped>
.edit-page { padding: 20px; min-height: 100vh; background: #f8fafc; }
.form-card { background: #fff; border-radius: 16px; padding: 0 24px; }
.form-row { display: flex; align-items: center; padding: 24px 0; border-bottom: 1px solid #f1f5f9; }
.form-row:last-child { border-bottom: 0; }
.textarea-row { align-items: flex-start; }
.switch-row { justify-content: space-between; }
.label { font-size: 28px; font-weight: 600; width: 160px; flex-shrink: 0; }
input, textarea { flex: 1; font-size: 28px; }
textarea { min-height: 80px; line-height: 1.5; }
.region-value { flex: 1; font-size: 28px; color: #1e293b; }
.region-value.placeholder { color: #94a3b8; }
.arrow { color: #94a3b8; font-size: 28px; }
.submit-btn { margin-top: 40px; text-align: center; padding: 24px 0; background: #ef4444; color: #fff; border-radius: 40px; font-size: 30px; font-weight: 600; }

/* 地区选择器 */
.region-mask { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); z-index: 200; display: flex; align-items: flex-end; }
.region-panel { width: 100%; background: #fff; border-radius: 24px 24px 0 0; padding-bottom: calc(24px + env(safe-area-inset-bottom)); }
.region-header { display: flex; justify-content: space-between; align-items: center; padding: 24px 32px; border-bottom: 1px solid #f1f5f9; }
.region-cancel { font-size: 28px; color: #64748b; }
.region-title { font-size: 30px; font-weight: 700; }
.region-confirm { font-size: 28px; color: #2563eb; font-weight: 600; }
.region-picker { height: 400px; }
.picker-item { line-height: 80px; text-align: center; font-size: 28px; }
</style>
