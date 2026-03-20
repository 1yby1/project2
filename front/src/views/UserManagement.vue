<template>
  <div class="fade-in">
    <div class="flex justify-between items-center mb-6">
      <div>
        <h2 class="text-[20px] font-bold text-gray-800">用户管理</h2>
        <p class="text-gray-500 text-xs mt-1">管理系统用户账号</p>
      </div>
      <el-button type="primary" :icon="Plus" @click="openAddModal">新增用户</el-button>
    </div>

    <!-- 搜索栏 -->
    <el-card shadow="never" class="mb-4">
      <div class="flex gap-4">
        <el-input
            v-model="searchKeyword"
            placeholder="输入姓名 / 手机号"
            prefix-icon="Search"
            style="width: 300px"
            clearable
        />
        <el-select v-model="searchRole" placeholder="选择角色" style="width: 150px" clearable>
          <el-option label="全部角色" :value="''" />
          <el-option
              v-for="option in roleOptions"
              :key="option.value"
              :label="option.label"
              :value="option.value"
          />
        </el-select>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="never">
      <el-table :data="users" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="用户名" min-width="100" />
        <el-table-column prop="email" label="邮箱" min-width="100" />
        <el-table-column prop="realName" label="真实姓名" min-width="100" />
        <el-table-column prop="phone" label="手机号" width="150" />
        <el-table-column prop="unitName" label="所属单位" min-width="150" show-overflow-tooltip />
        <el-table-column prop="roleLabel" label="角色" width="140">
          <template #default="{ row }">
            <el-tag :type="roleTagType[row.roleId] || 'info'" effect="plain">{{ row.roleLabel }}</el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="statusLabel" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType[row.statusCode] || 'info'" effect="light">{{ row.statusLabel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLogin" label="最近登录" width="180" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link :type="row.statusCode === 1 ? 'warning' : 'success'" size="small" @click="handleToggleStatus(row)">
              {{ row.statusCode === 1 ? '禁用' : '启用' }}
            </el-button>
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

    <!-- 新增/编辑用户弹窗 -->
    <el-dialog
        v-model="dialogVisible"
        :title="isEdit ? '编辑用户' : '新增用户'"
        width="500px"
        destroy-on-close>

      <el-form :model="form" label-width="100px" :rules="rules" ref="formRef">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="form.idCard" placeholder="请输入身份证号"></el-input>
        </el-form-item>
        <el-form-item label="所属单位" prop="unitId">
          <el-select v-model="form.unitId" placeholder="请选择单位" style="width: 100%">
            <el-option
                v-for="unit in units"
                :key="unit.id"
                :label="unit.name"
                :value="unit.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
          <el-select v-model="form.roleId" placeholder="请选择角色" style="width: 100%">
            <el-option
                v-for="option in roleOptions"
                :key="option.value"
                :label="option.label"
                :value="option.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="部门" prop="department">
          <el-input v-model="form.department" placeholder="请输入部门"></el-input>
        </el-form-item>


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
import * as userApi from '@/api/user'
import * as unitApi from '@/api/unit'

const roleLabelMap = {
  1: '管理员',
  2: '单位用户',
  3: '普通用户'
}

const roleOptions = [
  { value: 1, label: roleLabelMap[1] },
  { value: 2, label: roleLabelMap[2] },
  { value: 3, label: roleLabelMap[3] }
]

const roleTagType = {
  1: 'warning',
  2: 'primary',
  3: 'info'
}
const statusLabelMap = {
  1: '正常',
  0: '停用',
  '-1': '注销'
}

const statusTagType = {
  1: 'success',
  0: 'warning',
  '-1': 'danger'
}

const statusOptions = [
  { value: 1, label: statusLabelMap[1] },
  { value: 0, label: statusLabelMap[0] },
  { value: -1, label: statusLabelMap[-1] }
]
const searchKeyword = ref('')
const searchDepartment = ref('')
const searchRole = ref('')
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const users = ref([])
const units = ref([])

const form = ref({
  name: '',
  phone: '',
  idCard: '',
  unitId: '',
  roleId: null,
  department: ''
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }],
  unitId: [{ required: true, message: '请选择所属单位', trigger: 'change' }],
  roleId: [{ required: true, message: '请选择角色', trigger: 'change' }],
  department: [{ message: '请输入部门', trigger: 'blur' }]
}

// 获取用户列表
async function getUserList() {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      keyword: searchKeyword.value
    }

    // 只有当部门筛选条件不为空时，才添加到参数中
    if (searchDepartment.value && searchDepartment.value.trim()) {
      params.department = searchDepartment.value
    }

    // 添加角色筛选参数
    if (searchRole.value !== '' && searchRole.value !== null && searchRole.value !== undefined) {
      params.roleId = searchRole.value
    }

    const response = await userApi.getUserList(params)
    console.log('用户列表响应：', response)
    if (response.code === 200) {
      users.value = response.data.records.map(user => {
        const roleId = Number(user.roleId)
        const statusCode = Number(user.status)
        return {
          id: user.id,
          name: user.username,
          phone: user.phone,
          unitName: user.unitName || '瓶安保技术有限公司',
          email: user.email,
          idCard: user.idCard,
          roleId,
          roleLabel: roleLabelMap[roleId] || '未知角色',
          statusCode,
          statusLabel: statusLabelMap[statusCode] || '未知状态',
          department: user.department || '',
          lastLogin: user.lastLogin,
          realName: user.realName || ''
        }
      })
      total.value = response.data.total
    }
  } catch (error) {
    ElMessage.error('获取用户列表失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

// 获取单位列表（用于用户表单中的单位选择）
async function getUnitList() {
  try {
    const response = await unitApi.getUnitList({
      page: 1,
      size: 1000 // 获取所有单位
    })

    if (response.code === 200) {
      units.value = response.data.records.map(unit => ({
        id: unit.unitId,
        name: unit.unitName
      }))
    }
  } catch (error) {
    ElMessage.error('获取单位列表失败：' + (error.message || '未知错误'))
  }
}

// 页面加载时获取数据
onMounted(() => {
  getUserList()
  getUnitList()
})

// 处理搜索
async function handleSearch() {
  currentPage.value = 1
  await getUserList()
  ElMessage.success('查询成功')
}

// 重置搜索
async function resetSearch() {
  searchKeyword.value = ''
  searchDepartment.value = ''
  searchRole.value = ''
  currentPage.value = 1
  await getUserList()
}

// 处理分页
async function handlePageChange(newPage) {
  currentPage.value = newPage
  await getUserList()
}

// 处理每页条数变化
async function handleSizeChange(newSize) {
  pageSize.value = newSize
  currentPage.value = 1
  await getUserList()
}

function openAddModal() {
  isEdit.value = false
  form.value = {
    name: '',
    phone: '',
    idCard: '',
    unitId: '',
    roleId: null,
    department: ''
  }
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  form.value = {
    id: row.id,
    name: row.name,
    phone: row.phone,
    idCard: row.idCard || '',
    unitId: units.value.find(u => u.name === row.unitName)?.id || '',
    roleId: row.roleId,
    department: row.department || ''
  }
  dialogVisible.value = true
}

async function handleToggleStatus(row) {
  const newStatus = row.statusCode === 1 ? 0 : 1
  const newStatusLabel = statusLabelMap[newStatus]

  try {
    const response = await userApi.updateUserStatus(row.id, newStatus)

    if (response.code === 200) {
      // 更新本地状态
      row.statusCode = newStatus
      row.statusLabel = newStatusLabel
      ElMessage.success(`用户已${newStatusLabel}`)
    } else {
      ElMessage.error(`修改状态失败：` + (response.message || '未知错误'))
    }
  } catch (error) {
    ElMessage.error(`修改状态失败：` + (error.message || '网络错误'))
  }
}

async function handleDelete(row) {
  ElMessageBox.confirm(
      `确定要删除用户 "${row.name}" 吗？此操作不可恢复。`,
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
          const response = await userApi.deleteUser(row.id)
          if (response.code === 200) {
            users.value = users.value.filter(u => u.id !== row.id)
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

async function handleSubmit() {
  formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        if (isEdit.value) {
          // Update logic - 调用API更新
          const userData = {
            id: form.value.id,
            name: form.value.name,
            phone: form.value.phone,
            idCard: form.value.idCard,
            unitId: form.value.unitId,
            roleId: form.value.roleId,
            department: form.value.department
          }

          const response = await userApi.updateUser(userData)

          if (response.code === 200) {
            // 更新本地状态
            const index = users.value.findIndex(u => u.id === form.value.id)
            if (index !== -1) {
              const updatedUser = { ...users.value[index], ...form.value }
              updatedUser.roleId = form.value.roleId
              updatedUser.roleLabel = roleLabelMap[form.value.roleId] || '未知角色'
              updatedUser.unitName = units.value.find(u => u.id === form.value.unitId)?.name || users.value[index].unitName
              users.value[index] = updatedUser
            }
            ElMessage.success('用户信息更新成功')
          } else {
            ElMessage.error('更新失败：' + (response.message || '未知错误'))
          }
        } else {
          // Create logic - 调用API创建
          const userData = {
            name: form.value.name,
            phone: form.value.phone,
            idCard: form.value.idCard,
            unitId: form.value.unitId,
            roleId: form.value.roleId,
            department: form.value.department
          }

          const response = await userApi.createUser(userData)

          if (response.code === 200) {
            // 重新获取用户列表
            await getUserList()
            ElMessage.success('用户创建成功')
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
}
</script>

<style scoped>
/* Add any specific styles here */
</style>
