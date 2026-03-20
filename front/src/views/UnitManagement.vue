<template>
  <div class="fade-in">
    <div class="flex justify-between items-center mb-6">
      <div>
        <h2 class="text-[20px] font-bold text-gray-800">单位管理</h2>
        <p class="text-gray-500 text-xs mt-1">管理签约单位基础信息及关联账号</p>
      </div>
      <el-button type="primary" :icon="Plus" @click="openAddModal">新增单位</el-button>
    </div>

    <!-- 搜索栏 -->
    <el-card shadow="never" class="mb-4">
      <div class="flex gap-4">
        <el-input
            v-model="searchKeyword"
            placeholder="输入单位名称 / 信用代码"
            prefix-icon="Search"
            style="width: 300px"
            clearable
        />
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="never">
      <el-table :data="units" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="单位名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="address" label="单位地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="creditCode" label="统一社会信用代码" width="220" show-overflow-tooltip />
        <el-table-column prop="manager" label="负责人" width="120" />
        <el-table-column prop="phone" label="联系电话" width="150" />
        <el-table-column prop="contractCount" label="合同数" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="info" effect="plain">{{ row.contractCount }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="creditLevel" label="信誉等级" width="80" show-overflow-tooltip align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === '正常' ? 'success' : 'danger'" effect="light">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" size="small" @click="handleManageUsers(row)">账号管理</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="flex justify-end mt-4">
        <el-pagination
            background
            layout="total, prev, pager, next, jumper"
            :total="total"
            :page-size="pageSize"
            :current-page="currentPage"
            @current-change="handlePageChange"
            @size-change="handleSizeChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑单位弹窗 -->
    <el-dialog
        v-model="dialogVisible"
        :title="isEdit ? '编辑单位' : '新增单位'"
        width="700px"
        destroy-on-close>

      <el-form :model="form" label-width="120px" :rules="rules" ref="formRef">
        <el-divider content-position="left">基本信息</el-divider>
        <el-form-item label="单位名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入单位全称"></el-input>
        </el-form-item>
        <el-form-item label="信用代码" prop="creditCode">
          <el-input v-model="form.creditCode" placeholder="请输入18位统一社会信用代码"></el-input>
        </el-form-item>
        <el-form-item label="气瓶类型" prop="gasTypes">
          <el-select v-model="form.gasTypes" multiple placeholder="请选择气瓶类型" style="width: 100%">
            <el-option label="氧气" value="氧气"></el-option>
            <el-option label="氮气" value="氮气"></el-option>
            <el-option label="氩气" value="氩气"></el-option>
            <el-option label="二氧化碳" value="二氧化碳"></el-option>
            <el-option label="液化天然气 (LNG)" value="LNG"></el-option>
            <el-option label="液化石油气 (LPG)" value="LPG"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="单位地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入详细地址"></el-input>
        </el-form-item>

        <el-divider content-position="left">负责人信息</el-divider>
        <div class="grid grid-cols-2 gap-4">
          <el-form-item label="负责人姓名" prop="manager">
            <el-input v-model="form.manager" placeholder="请输入姓名"></el-input>
          </el-form-item>
          <el-form-item label="联系电话" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入手机号"></el-input>
          </el-form-item>
        </div>
        <el-form-item label="身份证号" prop="managerIdCard">
          <el-input v-model="form.managerIdCard" placeholder="请输入负责人身份证号码"></el-input>
        </el-form-item>

        <el-divider content-position="left">财务信息</el-divider>
        <div class="grid grid-cols-2 gap-4">
          <el-form-item label="开户银行" prop="bankName">
            <el-input v-model="form.bankName" placeholder="例如：中国工商银行南京分行"></el-input>
          </el-form-item>
          <el-form-item label="银行卡号" prop="bankAccount">
            <el-input v-model="form.bankAccount" placeholder="请输入对公账户或卡号"></el-input>
          </el-form-item>
        </div>

        <!-- 仅新增时显示初始账号设置 -->
        <div v-if="!isEdit">
          <el-divider content-position="left">初始管理员账号</el-divider>
          <el-alert title="系统将自动为该单位创建一个管理员账号" type="info" :closable="false" class="mb-4" show-icon />
          <el-form-item label="登录账号" prop="adminAccount">
            <el-input v-model="form.adminAccount" placeholder="建议使用手机号或英文名"></el-input>
          </el-form-item>
          <el-form-item label="初始密码" prop="adminPassword">
            <el-input v-model="form.adminPassword" type="password" show-password placeholder="默认密码: 123456"></el-input>
          </el-form-item>
        </div>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Plus, Search } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as unitApi from '@/api/unit'

const searchKeyword = ref('')
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const units = ref([])

const form = ref({
  name: '',
  creditCode: '',
  gasTypes: [],
  manager: '',
  phone: '',
  managerIdCard: '',
  creditLevel: '',
  address: '',
  bankName: '',
  bankAccount: '',
  adminAccount: '',
  adminPassword: '',
  contractCount: 0
})

const rules = {
  name: [{ required: true, message: '请输入单位名称', trigger: 'blur' }],
  creditCode: [{ required: true, message: '请输入信用代码', trigger: 'blur' }],
  gasTypes: [{ required: true, message: '请选择气瓶类型', trigger: 'change' }],
  manager: [{ required: true, message: '请输入负责人', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  managerIdCard: [{ required: true, message: '请输入负责人身份证号', trigger: 'blur' }],
  adminAccount: [{ required: true, message: '请输入初始账号', trigger: 'blur' }]
}

// 获取单位列表
async function getUnitList() {
  loading.value = true
  try {
    const response = await unitApi.getUnitList({
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value
    })
    console.log('单位列表响应：', response)
    if (response.code === 200) {
      units.value = response.data.records.map(unit => ({
        id: unit.unitId,
        name: unit.unitName,
        creditCode: unit.socialCreditCode,
        manager: unit.unitPrincipalName,
        phone: unit.unitPrincipalPhone,
        managerIdCard: unit.unitPrincipalIdCard,
        address: unit.unitAddress,
        creditLevel: unit.creditLevel,
        status: unit.status === 1 ? '正常' : unit.status === 0 ? '停用' : '注销',
        contractCount: unit.contractCount // 暂时设为0，后续可从API获取
      }))
      total.value = response.data.total
    }
  } catch (error) {
    ElMessage.error('获取单位列表失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 页面加载时获取单位列表
onMounted(() => {
  getUnitList()
})

// 处理搜索
async function handleSearch() {
  currentPage.value = 1
  await getUnitList()
  ElMessage.success('查询成功')
}

// 重置搜索
async function resetSearch() {
  searchKeyword.value = ''
  currentPage.value = 1
  await getUnitList()
}

// 处理分页
async function handlePageChange(newPage) {
  currentPage.value = newPage
  await getUnitList()
}

// 处理每页条数变化
async function handleSizeChange(newSize) {
  pageSize.value = newSize
  currentPage.value = 1
  await getUnitList()
}

function openAddModal() {
  isEdit.value = false
  form.value = {
    name: '',
    creditCode: '',
    gasTypes: [],
    manager: '',
    phone: '',
    managerIdCard: '',
    address: '',
    bankName: '',
    bankAccount: '',
    adminAccount: '',
    adminPassword: '123456' // Default password
  }
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  form.value = { ...row }
  dialogVisible.value = true
}

async function handleDelete(row) {
  ElMessageBox.confirm(
      `确定要删除单位 "${row.name}" 吗？此操作不可恢复。`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
  )
      .then(async () => {
        try {
          // 调用API删除
          const response = await unitApi.deleteUnit(row.id)
          if (response.code === 200) {
            units.value = units.value.filter(u => u.id !== row.id)
            ElMessage.success('删除成功')
          } else {
            ElMessage.error('删除失败：' + (response.message || '未知错误'))
          }
        } catch (error) {
          ElMessage.error('删除失败：' + (error.message || '网络错误'))
        }
      })
      .catch(() => {})
}

function handleManageUsers(row) {
  ElMessage.info(`跳转到 ${row.name} 的账号管理页面`)
  // In a real app, this might route to UserManagement with a filter
}

async function handleSubmit() {
  formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (isEdit.value) {
          // Update logic - 调用API更新
          const unitData = {
            unitId: form.value.id,
            unitName: form.value.name,
            socialCreditCode: form.value.creditCode,
            unitPrincipalName: form.value.manager,
            unitPrincipalPhone: form.value.phone,
            unitPrincipalIdCard: form.value.managerIdCard,
            unitAddress: form.value.address,
            // 其他字段根据后端Unit实体类进行映射
          }

          const response = await unitApi.updateUnit(unitData)

          if (response.code === 200) {
            // 更新本地状态
            const index = units.value.findIndex(u => u.id === form.value.id)
            if (index !== -1) {
              units.value[index] = { ...form.value, status: units.value[index].status, contractCount: units.value[index].contractCount }
            }
            ElMessage.success('单位信息更新成功')
          } else {
            ElMessage.error('更新失败：' + (response.message || '未知错误'))
          }
        } else {
          // Create logic - 调用API创建
          const unitData = {
            unitName: form.value.name,
            socialCreditCode: form.value.creditCode,
            unitPrincipalName: form.value.manager,
            unitPrincipalPhone: form.value.phone,
            unitPrincipalIdCard: form.value.managerIdCard,
            unitAddress: form.value.address,
            bankName: form.value.bankName,
            bankAccount: form.value.bankAccount,
            // 其他字段根据后端Unit实体类进行映射
          }

          const response = await unitApi.createUnit(unitData)

          if (response.code === 200) {
            // 重新获取单位列表
            await getUnitList()
            ElMessage.success(`单位 "${form.value.name}" 及初始账号已创建`)
          } else {
            ElMessage.error('创建失败：' + (response.message || '未知错误'))
          }
        }
      } catch (error) {
        ElMessage.error('操作失败：' + (error.message || '网络错误'))
      } finally {
        submitting.value = false
        dialogVisible.value = false
      }
    }
  })
}</script>

<style scoped>
/* Add any specific styles here */
</style>
