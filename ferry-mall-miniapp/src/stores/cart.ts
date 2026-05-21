import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import Taro from '@tarojs/taro'
import {
  getCartList, addCartItem, updateCartQuantity, removeCartItem,
  toggleCartSelect, selectAllCart, clearCart, getCartCount
} from '@/api/cart'
import type { CartItem } from '@/api/cart'

const CART_KEY = 'ferry_cart'

function loadLocalCart(): CartItem[] {
  try {
    const raw = Taro.getStorageSync(CART_KEY)
    return raw ? JSON.parse(raw) : []
  } catch { return [] }
}

function saveLocalCart(list: CartItem[]) {
  Taro.setStorageSync(CART_KEY, JSON.stringify(list))
}

export const useCartStore = defineStore('cart', () => {
  const items = ref<CartItem[]>([])
  const totalCount = computed(() => items.value.reduce((s, i) => s + i.quantity, 0))
  const checkedCount = computed(() => items.value.filter(i => i.selected === 1).reduce((s, i) => s + i.quantity, 0))
  const totalCent = computed(() => {
    // need to fetch product prices - for now return 0, will be calculated in page
    return 0
  })
  const isAllChecked = computed(() => items.value.length > 0 && items.value.every(i => i.selected === 1))

  async function fetchCart() {
    try {
      items.value = await getCartList()
    } catch {
      // fallback to localStorage
      items.value = loadLocalCart()
    }
  }

  async function add(spuId: number, skuId?: number, quantity = 1) {
    try {
      await addCartItem(spuId, skuId, quantity)
      await fetchCart()
    } catch {
      // fallback: save to localStorage
      const list = loadLocalCart()
      const exist = list.find(i => i.spuId === spuId && i.skuId === skuId)
      if (exist) {
        exist.quantity += quantity
      } else {
        list.push({ id: Date.now(), spuId, skuId, quantity, selected: 1, createdAt: new Date().toISOString() })
      }
      saveLocalCart(list)
      items.value = list
    }
  }

  async function updateQuantity(cartId: number, quantity: number) {
    if (quantity <= 0) { await remove(cartId); return }
    try {
      await updateCartQuantity(cartId, quantity)
      await fetchCart()
    } catch {
      const list = loadLocalCart()
      const item = list.find(i => i.id === cartId)
      if (item) {
        item.quantity = quantity
        saveLocalCart(list)
        items.value = list
      }
    }
  }

  async function remove(cartId: number) {
    try {
      await removeCartItem(cartId)
      await fetchCart()
    } catch {
      const list = loadLocalCart().filter(i => i.id !== cartId)
      saveLocalCart(list)
      items.value = list
    }
  }

  async function toggle(cartId: number) {
    const item = items.value.find(i => i.id === cartId)
    if (!item) return
    const newSelected = item.selected === 1 ? 0 : 1
    try {
      await toggleCartSelect(cartId, newSelected)
      await fetchCart()
    } catch {
      const list = loadLocalCart()
      const localItem = list.find(i => i.id === cartId)
      if (localItem) {
        localItem.selected = newSelected
        saveLocalCart(list)
        items.value = list
      }
    }
  }

  async function toggleAll() {
    const all = isAllChecked.value
    const newSelected = all ? 0 : 1
    try {
      await selectAllCart(newSelected)
      await fetchCart()
    } catch {
      const list = loadLocalCart()
      list.forEach(i => i.selected = newSelected)
      saveLocalCart(list)
      items.value = list
    }
  }

  async function clear() {
    try {
      await clearCart()
      await fetchCart()
    } catch {
      Taro.removeStorageSync(CART_KEY)
      items.value = []
    }
  }

  async function refreshCount() {
    try {
      const count = await getCartCount()
      return count
    } catch {
      return totalCount.value
    }
  }

  return {
    items, totalCount, checkedCount, totalCent, isAllChecked,
    fetchCart, add, updateQuantity, remove, toggle, toggleAll, clear, refreshCount
  }
})
