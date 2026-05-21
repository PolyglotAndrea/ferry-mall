import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import Taro from '@tarojs/taro'

export interface CartItem {
  spuId: number
  skuId?: number
  skuName?: string
  name: string
  coverUrl: string
  priceCent: number
  quantity: number
  checked: boolean
}

const CART_KEY = 'ferry_cart'

function loadCart(): CartItem[] {
  try {
    const raw = Taro.getStorageSync(CART_KEY)
    return raw ? JSON.parse(raw) : []
  } catch { return [] }
}

function saveCart(list: CartItem[]) {
  Taro.setStorageSync(CART_KEY, JSON.stringify(list))
}

export const useCartStore = defineStore('cart', () => {
  const items = ref<CartItem[]>(loadCart())

  const totalCount = computed(() => items.value.reduce((s, i) => s + i.quantity, 0))
  const checkedCount = computed(() => items.value.filter(i => i.checked).reduce((s, i) => s + i.quantity, 0))
  const totalCent = computed(() => items.value.filter(i => i.checked).reduce((s, i) => s + i.priceCent * i.quantity, 0))
  const isAllChecked = computed(() => items.value.length > 0 && items.value.every(i => i.checked))

  function add(item: CartItem) {
    const exist = items.value.find(i => i.spuId === item.spuId && i.skuId === item.skuId)
    if (exist) {
      exist.quantity += item.quantity
    } else {
      items.value.push({ ...item, checked: true })
    }
    saveCart(items.value)
  }

  function updateQuantity(spuId: number, skuId: number | undefined, quantity: number) {
    const item = items.value.find(i => i.spuId === spuId && i.skuId === skuId)
    if (!item) return
    if (quantity <= 0) {
      remove(spuId, skuId)
    } else {
      item.quantity = quantity
      saveCart(items.value)
    }
  }

  function remove(spuId: number, skuId?: number) {
    items.value = items.value.filter(i => !(i.spuId === spuId && i.skuId === skuId))
    saveCart(items.value)
  }

  function toggle(spuId: number, skuId?: number) {
    const item = items.value.find(i => i.spuId === spuId && i.skuId === skuId)
    if (item) {
      item.checked = !item.checked
      saveCart(items.value)
    }
  }

  function toggleAll() {
    const all = isAllChecked.value
    items.value.forEach(i => i.checked = !all)
    saveCart(items.value)
  }

  function clear() {
    items.value = []
    Taro.removeStorageSync(CART_KEY)
  }

  function getCheckedItems(): CartItem[] {
    return items.value.filter(i => i.checked)
  }

  return {
    items, totalCount, checkedCount, totalCent, isAllChecked,
    add, updateQuantity, remove, toggle, toggleAll, clear, getCheckedItems
  }
})
