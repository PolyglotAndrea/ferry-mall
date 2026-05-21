<template>
  <div class="page-card">
    <h2>商品列表</h2>

    <!-- 搜索栏 -->
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="商品名称">
        <el-input v-model="searchForm.keyword" placeholder="请输入商品名称" clearable />
      </el-form-item>
      <el-form-item label="商品分类">
        <el-select v-model="searchForm.categoryId" placeholder="请选择分类" clearable>
          <el-option
            v-for="cat in categories"
            :key="cat.id"
            :label="cat.name"
            :value="cat.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作栏 -->
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">新增商品</el-button>
    </div>

    <!-- 表格 -->
    <el-table :data="products" row-key="id" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="封面图" width="100">
        <template #default="{ row }">
          <el-image
            v-if="row.coverUrl"
            :src="row.coverUrl"
            style="width: 60px; height: 60px"
            fit="cover"
          />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column prop="name" label="商品名称" min-width="150" show-overflow-tooltip />
      <el-table-column prop="subtitle" label="卖点" min-width="150" show-overflow-tooltip />
      <el-table-column label="价格" width="100">
        <template #default="{ row }">
          ¥{{ (row.priceCent / 100).toFixed(2) }}
        </template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column prop="sales" label="销量" width="80" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button
            size="small"
            :type="row.status === 1 ? 'warning' : 'success'"
            @click="handleToggleStatus(row)"
          >
            {{ row.status === 1 ? '下架' : '上架' }}
          </el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <el-pagination
      v-model:current-page="pageNo"
      v-model:page-size="pageSize"
      :total="total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      class="pagination"
      @size-change="handleSizeChange"
      @current-change="handlePageChange"
    />

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" label-width="100px" :rules="formRules" ref="formRef">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="卖点" prop="subtitle">
          <el-input v-model="form.subtitle" placeholder="请输入卖点" />
        </el-form-item>
        <el-form-item label="封面图URL" prop="coverUrl">
          <el-input v-model="form.coverUrl" placeholder="请输入封面图URL" />
        </el-form-item>
        <el-form-item label="价格(元)" prop="priceYuan">
          <el-input-number v-model="form.priceYuan" :min="0.01" :precision="2" placeholder="请输入价格" style="width: 100%" />
        </el-form-item>
        <el-form-item label="市场价(元)" prop="marketPriceYuan">
          <el-input-number v-model="form.marketPriceYuan" :min="0" :precision="2" placeholder="请输入市场价" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" :precision="0" placeholder="请输入库存" style="width: 100%" />
        </el-form-item>
        <el-form-item label="商品分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="上下架状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saveLoading">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import {
  productPageApi,
  createProductApi,
  updateProductApi,
  deleteProductApi,
  toggleProductStatusApi,
  type ProductSpu,
  type ProductCreateReq,
  type ProductUpdateReq,
} from '@/api/product'
import { request } from '@/utils/request'

interface Category {
  id: number
  name: string
}

const products = ref<ProductSpu[]>([])
const categories = ref<Category[]>([])
const loading = ref(false)
const pageNo = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = reactive({
  keyword: '',
  categoryId: undefined as number | undefined,
})

const dialogVisible = ref(false)
const dialogTitle = computed(() => (form.id ? '编辑商品' : '新增商品'))
const saveLoading = ref(false)
const formRef = ref<FormInstance>()

const form = reactive({
  id: 0,
  name: '',
  subtitle: '',
  coverUrl: '',
  priceYuan: 0,
  marketPriceYuan: 0,
  stock: 0,
  categoryId: undefined as number | undefined,
  status: 1,
})

const formRules: FormRules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  priceYuan: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
}

async function fetchCategories() {
  try {
    const list: any[] = await request.get('/admin-api/product/category/tree')
    categories.value = list.map((item) => ({ id: item.id, name: item.name }))
  } catch (e: any) {
    ElMessage.error(e.message || '获取分类失败')
  }
}

async function fetchProducts() {
  loading.value = true
  try {
    const res = await productPageApi(
      searchForm.keyword || undefined,
      searchForm.categoryId,
      pageNo.value,
      pageSize.value
    )
    products.value = res.list
    total.value = res.total
  } catch (e: any) {
    ElMessage.error(e.message || '获取商品列表失败')
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  pageNo.value = 1
  fetchProducts()
}

function resetSearch() {
  searchForm.keyword = ''
  searchForm.categoryId = undefined
  pageNo.value = 1
  fetchProducts()
}

function handleSizeChange(val: number) {
  pageSize.value = val
  pageNo.value = 1
  fetchProducts()
}

function handlePageChange(val: number) {
  pageNo.value = val
  fetchProducts()
}

function resetForm() {
  form.id = 0
  form.name = ''
  form.subtitle = ''
  form.coverUrl = ''
  form.priceYuan = 0
  form.marketPriceYuan = 0
  form.stock = 0
  form.categoryId = undefined
  form.status = 1
}

function handleAdd() {
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row: ProductSpu) {
  form.id = row.id
  form.name = row.name
  form.subtitle = row.subtitle || ''
  form.coverUrl = row.coverUrl || ''
  form.priceYuan = row.priceCent / 100
  form.marketPriceYuan = (row.marketPriceCent || 0) / 100
  form.stock = row.stock
  form.categoryId = row.categoryId
  form.status = row.status
  dialogVisible.value = true
}

async function handleSave() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    saveLoading.value = true
    try {
      const priceCent = Math.round(form.priceYuan * 100)
      const marketPriceCent = Math.round(form.marketPriceYuan * 100)
      if (form.id) {
        const req: ProductUpdateReq = {
          categoryId: form.categoryId!,
          name: form.name,
          subtitle: form.subtitle,
          coverUrl: form.coverUrl,
          priceCent,
          marketPriceCent,
          stock: form.stock,
          status: form.status,
        }
        await updateProductApi(form.id, req)
        ElMessage.success('更新成功')
      } else {
        const req: ProductCreateReq = {
          categoryId: form.categoryId!,
          name: form.name,
          subtitle: form.subtitle,
          coverUrl: form.coverUrl,
          priceCent,
          marketPriceCent,
          stock: form.stock,
        }
        await createProductApi(req)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      fetchProducts()
    } catch (e: any) {
      ElMessage.error(e.message || '保存失败')
    } finally {
      saveLoading.value = false
    }
  })
}

async function handleToggleStatus(row: ProductSpu) {
  try {
    await toggleProductStatusApi(row.id)
    ElMessage.success('状态切换成功')
    fetchProducts()
  } catch (e: any) {
    ElMessage.error(e.message || '状态切换失败')
  }
}

async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('确定删除该商品吗？删除后不可恢复。', '提示', { type: 'warning' })
    await deleteProductApi(id)
    ElMessage.success('删除成功')
    fetchProducts()
  } catch (e: any) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '删除失败')
    }
  }
}

onMounted(() => {
  fetchCategories()
  fetchProducts()
})
</script>

<style scoped>
.search-form {
  margin-bottom: 16px;
}
.toolbar {
  margin-bottom: 16px;
}
.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
