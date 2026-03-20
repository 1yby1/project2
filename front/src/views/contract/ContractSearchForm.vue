<template>
  <el-card shadow="never" class="mb-4">
    <el-form :inline="true" :model="searchForm">
      <el-form-item label="单位" v-if="showUnitFilter">
        <el-select v-model="searchForm.unit" placeholder="请选择单位" clearable style="width: 200px">
          <el-option label="所有单位" value=""></el-option>
          <el-option 
            v-for="item in unitList" 
            :key="item.unit_id" 
            :label="item.unit_name" 
            :value="item.unit_name">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="关键词">
        <el-input 
          v-model="searchForm.keyword" 
          placeholder="合同编号 / 关键词" 
          clearable
          style="width: 250px"
          :prefix-icon="Search">
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup lang="ts">
import { reactive, ref, onMounted, watch } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { getUnitList } from '@/api/contract'

interface Props {
  showUnitFilter?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  showUnitFilter: false
})

const emit = defineEmits<{
  search: [searchForm: { unit: string; keyword: string }]
}>()

const searchForm = reactive({
  unit: '',
  keyword: ''
})

const unitList = ref<any[]>([])

// 加载单位列表
const loadUnitList = async () => {
  if (!props.showUnitFilter) return
  
  try {
    const res = await getUnitList()
    if (res.code === 200 && res.data) {
      unitList.value = res.data
    }
  } catch (error) {
    console.error('获取单位列表失败', error)
  }
}

// 监听 showUnitFilter 变化
watch(() => props.showUnitFilter, (newVal) => {
  if (newVal) {
    loadUnitList()
  }
})

onMounted(() => {
  loadUnitList()
})

const handleSearch = () => {
  emit('search', searchForm)
}
</script>
