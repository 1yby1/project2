<template>
  <div class="fade-in max-w-4xl">
    <div class="mb-6">
      <h2 class="text-[20px] font-bold text-gray-800">个人中心</h2>
      <p class="text-gray-500 text-xs mt-1">管理您的个人信息和账号设置</p>
    </div>

    <!-- 个人信息卡片 -->
    <div class="grid grid-cols-3 gap-6 mb-8">
      <div class="col-span-2 bg-white rounded-lg shadow-sm border border-gray-200 p-6">
        <div class="flex justify-between items-start mb-4">
          <h3 class="font-bold text-gray-700">基本信息</h3>
          <button class="text-primary text-sm hover:underline" @click="showEditDialog = true">编辑</button>
        </div>
        <div class="space-y-3 text-sm">
          <div class="flex">
            <span class="text-gray-500 w-24">姓名</span>
            <span class="font-medium">{{ userName }}</span>
          </div>
          <div class="flex">
            <span class="text-gray-500 w-24">所属单位</span>
            <span class="font-medium">{{ userUnit || '未绑定' }}</span>
          </div>
          <div class="flex">
            <span class="text-gray-500 w-24">手机号</span>
            <span class="font-medium">{{ userInfo.phone || '未绑定' }}</span>
          </div>
          <div class="flex">
            <span class="text-gray-500 w-24">邮箱</span>
            <span class="font-medium">{{ userInfo.email || '未绑定' }}</span>
          </div>
        </div>
      </div>

      <div class="bg-gradient-to-br from-primary to-blue-500 rounded-lg shadow-sm p-6 text-white">
        <div class="text-center">
          <div class="w-20 h-20 bg-white rounded-full flex items-center justify-center mx-auto mb-3 text-primary text-2xl font-bold">
            A
          </div>
          <p class="font-bold text-lg">{{ userName }}</p>
          <p class="text-white/80 text-xs mt-1">{{ currentRole === 'admin' ? '管理员' : '单位用户' }}</p>
        </div>
      </div>
    </div>

    <!-- 安全设置 -->
    <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mb-8">
      <h3 class="font-bold text-gray-700 mb-4">安全设置</h3>
      <div class="space-y-4">
        <div class="flex justify-between items-center py-3 border-b">
          <div>
            <p class="font-medium text-sm">登录密码</p>
            <p class="text-xs text-gray-500 mt-1">定期更改密码可保护账号安全</p>
          </div>
          <button class="text-primary text-sm hover:underline" @click="showChangePasswordDialog = true">修改</button>
        </div>
        <div class="flex justify-between items-center py-3 border-b">
          <div>
            <p class="font-medium text-sm">手机绑定</p>
            <p class="text-xs text-gray-500 mt-1">
              {{ userInfo.phone ? `已绑定手机：${userInfo.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')}` : '未绑定手机' }}
            </p>
          </div>
          <button class="text-primary text-sm hover:underline" @click="showChangePhoneDialog = true">更换</button>
        </div>
        <div class="flex justify-between items-center py-3">
          <div>
            <p class="font-medium text-sm">邮箱绑定</p>
            <p class="text-xs text-gray-500 mt-1">
              {{ userInfo.email ? `已绑定邮箱：${userInfo.email.replace(/(\w{2})\w*(\w{2}@.*)/, '$1****$2')}` : '未绑定邮箱' }}
            </p>
          </div>
          <button class="text-primary text-sm hover:underline" @click="showChangeEmailDialog = true">更换</button>
        </div>
      </div>
    </div>

    <!-- 消息通知设置 -->
    <div class="bg-white rounded-lg shadow-sm border border-gray-200 p-6">
      <h3 class="font-bold text-gray-700 mb-4">消息通知设置</h3>
      <div class="space-y-4">
        <div class="flex justify-between items-center py-2">
          <div>
            <p class="font-medium text-sm">合同到期提醒</p>
            <p class="text-xs text-gray-500 mt-1">提前30天通知合同到期</p>
          </div>
          <label class="relative inline-flex items-center cursor-pointer">
            <input type="checkbox" class="sr-only peer" checked>
            <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-2 peer-focus:ring-primary rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary"></div>
          </label>
        </div>
        <div class="flex justify-between items-center py-2">
          <div>
            <p class="font-medium text-sm">续约提醒</p>
            <p class="text-xs text-gray-500 mt-1">及时通知续约进度</p>
          </div>
          <label class="relative inline-flex items-center cursor-pointer">
            <input type="checkbox" class="sr-only peer" checked>
            <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-2 peer-focus:ring-primary rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary"></div>
          </label>
        </div>
        <div class="flex justify-between items-center py-2">
          <div>
            <p class="font-medium text-sm">系统公告</p>
            <p class="text-xs text-gray-500 mt-1">接收平台重要公告</p>
          </div>
          <label class="relative inline-flex items-center cursor-pointer">
            <input type="checkbox" class="sr-only peer" checked>
            <div class="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-2 peer-focus:ring-primary rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-primary"></div>
          </label>
        </div>
      </div>
    </div>

    <!-- 个人信息编辑弹窗 -->
    <div v-if="showEditDialog" class="fixed inset-0 bg-gray-700/30 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg shadow-xl p-6 w-full max-w-md">
        <div class="flex justify-between items-center mb-4">
          <h3 class="font-bold text-lg text-gray-800">编辑个人信息</h3>
          <button @click="showEditDialog = false" class="text-gray-500 hover:text-gray-700">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>

        <form @submit.prevent="updateUserInfo">
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">真实姓名</label>
              <input type="text" v-model="editForm.realName" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary" placeholder="请输入真实姓名">
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">手机号</label>
              <div class="w-full px-3 py-2 border border-gray-300 rounded-md bg-gray-50 cursor-not-allowed">
                {{ editForm.phone ? editForm.phone : '未绑定' }}
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">邮箱</label>
              <div class="w-full px-3 py-2 border border-gray-300 rounded-md bg-gray-50 cursor-not-allowed">
                {{ editForm.email ? editForm.email : '未绑定' }}
              </div>
            </div>
          </div>

          <div class="flex justify-end space-x-3 mt-6">
            <button type="button" @click="showEditDialog = false" class="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50">取消</button>
            <button type="submit" class="px-4 py-2 bg-primary text-white rounded-md hover:bg-blue-600">保存</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 修改密码对话框 -->
    <div v-if="showChangePasswordDialog" class="fixed inset-0 bg-gray-700/30 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg shadow-xl p-6 w-full max-w-md">
        <div class="flex justify-between items-center mb-4">
          <h3 class="font-bold text-lg text-gray-800">修改登录密码</h3>
          <button @click="showChangePasswordDialog = false" class="text-gray-500 hover:text-gray-700">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>

        <form @submit.prevent="changePassword">
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">旧密码</label>
              <input type="password" v-model="changePasswordForm.oldPassword" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary" placeholder="请输入旧密码">
              <p v-if="passwordErrors.oldPassword" class="text-xs text-error mt-1">{{ passwordErrors.oldPassword }}</p>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">新密码</label>
              <input type="password" v-model="changePasswordForm.newPassword" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary" placeholder="请输入新密码">
              <p v-if="passwordErrors.newPassword" class="text-xs text-error mt-1">{{ passwordErrors.newPassword }}</p>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">确认新密码</label>
              <input type="password" v-model="changePasswordForm.confirmPassword" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary" placeholder="请再次输入新密码">
              <p v-if="passwordErrors.confirmPassword" class="text-xs text-error mt-1">{{ passwordErrors.confirmPassword }}</p>
            </div>
          </div>

          <div class="flex justify-end space-x-3 mt-6">
            <button type="button" @click="showChangePasswordDialog = false" class="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50">取消</button>
            <button type="submit" class="px-4 py-2 bg-primary text-white rounded-md hover:bg-blue-600">保存</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 更换手机号对话框 -->
    <div v-if="showChangePhoneDialog" class="fixed inset-0 bg-gray-700/30 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg shadow-xl p-6 w-full max-w-md">
        <div class="flex justify-between items-center mb-4">
          <h3 class="font-bold text-lg text-gray-800">更换手机号</h3>
          <button @click="showChangePhoneDialog = false" class="text-gray-500 hover:text-gray-700">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>

        <form @submit.prevent="changePhone">
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">新手机号</label>
              <input type="tel" v-model="changePhoneForm.newPhone" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary" placeholder="请输入新手机号">
              <p v-if="phoneErrors.newPhone" class="text-xs text-error mt-1">{{ phoneErrors.newPhone }}</p>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">验证码</label>
              <div class="flex space-x-2">
                <div class="relative flex-1">
                  <input type="text" v-model="changePhoneForm.code" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary" placeholder="请输入验证码">
                </div>
                <button type="button" @click="sendPhoneCode" :disabled="phoneCountdown > 0" class="px-3 py-2 bg-gray-50 border border-gray-300 rounded-md text-xs text-gray-600 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed">
                  {{ phoneCountdown > 0 ? `${phoneCountdown}s后重发` : '获取验证码' }}
                </button>
              </div>
              <p v-if="phoneErrors.code" class="text-xs text-error mt-1">{{ phoneErrors.code }}</p>
            </div>
          </div>

          <div class="flex justify-end space-x-3 mt-6">
            <button type="button" @click="showChangePhoneDialog = false" class="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50">取消</button>
            <button type="submit" class="px-4 py-2 bg-primary text-white rounded-md hover:bg-blue-600">保存</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 更换邮箱对话框 -->
    <div v-if="showChangeEmailDialog" class="fixed inset-0 bg-gray-700/30 flex items-center justify-center z-50">
      <div class="bg-white rounded-lg shadow-xl p-6 w-full max-w-md">
        <div class="flex justify-between items-center mb-4">
          <h3 class="font-bold text-lg text-gray-800">更换邮箱</h3>
          <button @click="showChangeEmailDialog = false" class="text-gray-500 hover:text-gray-700">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>

        <form @submit.prevent="changeEmail">
          <div class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">新邮箱</label>
              <input type="email" v-model="changeEmailForm.newEmail" class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-primary focus:border-primary" placeholder="请输入新邮箱">
              <p v-if="emailErrors.newEmail" class="text-xs text-error mt-1">{{ emailErrors.newEmail }}</p>
            </div>
          </div>

          <div class="flex justify-end space-x-3 mt-6">
            <button type="button" @click="showChangeEmailDialog = false" class="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50">取消</button>
            <button type="submit" class="px-4 py-2 bg-primary text-white rounded-md hover:bg-blue-600">保存</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useUserStore } from '@/stores/user'
import { storeToRefs } from 'pinia'
import { ref, onMounted, computed, watch } from 'vue'
import { getUserInfo, updateUserInfo as updateUserInfoApi, getUnitInfo, changePassword as changePasswordApi, updatePhone, updateEmail } from '@/api/profile'
import { sendCode } from '@/api/auth'
import router from '@/router'

const userStore = useUserStore()
const { currentRole } = storeToRefs(userStore)

// 用户信息数据
const userInfo = ref({
  realName: '',
  username: '',
  unitId: null,
  unitName: '',
  phone: '',
  email: ''
})

// 编辑对话框状态
const showEditDialog = ref(false)

// 编辑表单数据
const editForm = ref({
  realName: '',
  phone: '',
  email: ''
})

// 修改密码对话框状态
const showChangePasswordDialog = ref(false)

// 修改密码表单数据
const changePasswordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码验证错误信息
const passwordErrors = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 更换手机号对话框状态
const showChangePhoneDialog = ref(false)

// 更换手机号表单数据
const changePhoneForm = ref({
  newPhone: '',
  code: ''
})

// 验证码倒计时
const phoneCountdown = ref(0)

// 手机号验证错误信息
const phoneErrors = ref({
  newPhone: '',
  code: ''
})

// 更换邮箱对话框状态
const showChangeEmailDialog = ref(false)

// 更换邮箱表单数据
const changeEmailForm = ref({
  newEmail: ''
})

// 邮箱验证错误信息
const emailErrors = ref({
  newEmail: ''
})

// 计算属性：用户名称（优先显示真实姓名，否则显示用户名）
const userName = computed(() => userInfo.value.realName || userInfo.value.username || '游客')

// 计算属性：所属单位（显示单位名称）
const userUnit = computed(() => userInfo.value.unitName || '未绑定')

// 监听编辑对话框显示状态，打开时填充表单数据
watch(showEditDialog, (newVal) => {
  if (newVal) {
    // 打开对话框时，将当前用户信息复制到编辑表单
    editForm.value = { ...userInfo.value }
  }
})

// 从后端获取用户信息
onMounted(async () => {
  try {
    // 调试：检查localStorage中的token
    const token = localStorage.getItem('token');
    console.log('localStorage中的token:', token);

    // 检查是否有token
    if (!token) {
      console.error('没有找到token，请先登录');
      // 跳转到登录页面
      router.push('/login');
      return;
    }

    console.log('准备发送GET /user/info请求...');

    const response = await getUserInfo()
    console.log('getUserInfo响应:', response);
    if (response.code === 200 && response.data) {
      userInfo.value = response.data
      console.log('用户信息更新成功:', userInfo.value);

      // 如果用户有单位ID，获取单位名称
      if (userInfo.value.unitId) {
        try {
          console.log('准备获取单位信息，unitId:', userInfo.value.unitId);
          const unitResponse = await getUnitInfo(userInfo.value.unitId);
          console.log('getUnitInfo响应:', unitResponse);
          if (unitResponse.code === 200 && unitResponse.data) {
            userInfo.value.unitName = unitResponse.data.unitName;
            console.log('单位名称更新成功:', userInfo.value.unitName);
          }
        } catch (unitError) {
          console.error('获取单位信息失败:', unitError);
        }
      }
    }
  } catch (error) {
    console.error('获取用户信息失败:', error);
    console.error('错误详情:', JSON.stringify(error));
    // 如果是401错误，跳转到登录页面
    if (error.response && error.response.status === 401) {
      console.error('token无效或已过期，请重新登录');
      // 清除无效的token
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      // 跳转到登录页面
      router.push('/login');
    }
  }
})

// 更新用户信息
const updateUserInfo = async () => {
  try {
    const response = await updateUserInfoApi(editForm.value)
    if (response.code === 200) {
      // 更新成功后，关闭对话框并更新本地用户信息
      showEditDialog.value = false
      // 重新获取最新的用户信息
      const userResponse = await getUserInfo()
      if (userResponse.code === 200 && userResponse.data) {
        userInfo.value = userResponse.data
      }
      // 可以添加成功提示，如使用Element Plus的message组件
      // ElMessage.success('个人信息更新成功')
    }
  } catch (error) {
    console.error('更新用户信息失败:', error)
    // ElMessage.error('更新失败，请重试')
  }
}

// 验证密码表单
const validatePasswordForm = () => {
  let isValid = true

  // 重置错误信息
  passwordErrors.value = {
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  }

  // 验证旧密码
  if (!changePasswordForm.value.oldPassword) {
    passwordErrors.value.oldPassword = '请输入旧密码'
    isValid = false
  }

  // 验证新密码
  if (!changePasswordForm.value.newPassword) {
    passwordErrors.value.newPassword = '请输入新密码'
    isValid = false
  } else if (changePasswordForm.value.newPassword === changePasswordForm.value.oldPassword) {
    passwordErrors.value.newPassword = '新密码与旧密码不能一样'
    isValid = false
  }

  // 验证确认新密码
  if (!changePasswordForm.value.confirmPassword) {
    passwordErrors.value.confirmPassword = '请确认新密码'
    isValid = false
  } else if (changePasswordForm.value.confirmPassword !== changePasswordForm.value.newPassword) {
    passwordErrors.value.confirmPassword = '两次输入的新密码不一致'
    isValid = false
  }

  return isValid
}

// 修改密码
const changePassword = async () => {
  // 验证表单
  if (!validatePasswordForm()) {
    return
  }

  try {
    // 调用修改密码API
    const response = await changePasswordApi(changePasswordForm.value)
    if (response.code === 200) {
      // 修改成功后，关闭对话框并重置表单
      showChangePasswordDialog.value = false
      changePasswordForm.value = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
      // 可以添加成功提示，如使用Element Plus的message组件
      // ElMessage.success('密码修改成功')
    }
  } catch (error) {
    console.error('修改密码失败:', error)
    // 处理旧密码不正确的情况
    if (error.response && error.response.status === 400 && error.response.data.message.includes('旧密码')) {
      passwordErrors.value.oldPassword = '旧密码不正确'
    } else {
      // ElMessage.error('修改密码失败，请重试')
    }
  }
}

// 发送手机号验证码
async function sendPhoneCode() {
  const phoneRegex = /^1[3-9]\d{9}$/
  if (!changePhoneForm.value.newPhone || !phoneRegex.test(changePhoneForm.value.newPhone)) {
    phoneErrors.value.newPhone = '请输入有效的手机号'
    return
  }
  if (phoneCountdown.value > 0) return

  try {
    await sendCode({ phone: changePhoneForm.value.newPhone })
    alert('验证码已发送')

    phoneCountdown.value = 60
    const timer = setInterval(() => {
      phoneCountdown.value--
      if (phoneCountdown.value <= 0) {
        clearInterval(timer)
      }
    }, 1000)
  } catch (error) {
    alert('发送验证码失败: ' + (error.message || '未知错误'))
  }
}

// 验证更换手机号表单
const validateChangePhoneForm = () => {
  let isValid = true
  const phoneRegex = /^1[3-9]\d{9}$/

  // 重置错误信息
  phoneErrors.value = {
    newPhone: '',
    code: ''
  }

  // 验证新手机号
  if (!changePhoneForm.value.newPhone) {
    phoneErrors.value.newPhone = '请输入新手机号'
    isValid = false
  } else if (!phoneRegex.test(changePhoneForm.value.newPhone)) {
    phoneErrors.value.newPhone = '请输入有效的手机号'
    isValid = false
  }

  // 验证验证码
  if (!changePhoneForm.value.code) {
    phoneErrors.value.code = '请输入验证码'
    isValid = false
  }

  return isValid
}

// 更换手机号
const changePhone = async () => {
  // 验证表单
  if (!validateChangePhoneForm()) {
    return
  }

  try {
    // 调用更换手机号API
    const response = await updatePhone({
      phone: changePhoneForm.value.newPhone,
      code: changePhoneForm.value.code
    })

    if (response.code === 200) {
      // 更换成功后，关闭对话框并重置表单
      showChangePhoneDialog.value = false
      changePhoneForm.value = {
        newPhone: '',
        code: ''
      }
      phoneCountdown.value = 0

      // 重新获取用户信息
      const userResponse = await getUserInfo()
      if (userResponse.code === 200 && userResponse.data) {
        userInfo.value = userResponse.data
      }

      alert('手机号更换成功')
    }
  } catch (error) {
    console.error('更换手机号失败:', error)
    // 处理验证码不正确的情况
    if (error.response && error.response.status === 400 && error.response.data.message.includes('验证码')) {
      phoneErrors.value.code = '验证码不正确'
    } else {
      alert('更换手机号失败，请重试')
    }
  }
}

// 验证邮箱格式
const validateEmail = (email) => {
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return emailRegex.test(email)
}

// 验证更换邮箱表单
const validateEmailForm = () => {
  let isValid = true

  // 重置错误信息
  emailErrors.value = {
    newEmail: ''
  }

  // 验证新邮箱
  if (!changeEmailForm.value.newEmail) {
    emailErrors.value.newEmail = '请输入新邮箱'
    isValid = false
  } else if (!validateEmail(changeEmailForm.value.newEmail)) {
    emailErrors.value.newEmail = '请输入有效的邮箱地址'
    isValid = false
  }

  return isValid
}

// 更换邮箱
const changeEmail = async () => {
  // 验证表单
  if (!validateEmailForm()) {
    return
  }

  try {
    // 调用更换邮箱API
    const response = await updateEmail({
      email: changeEmailForm.value.newEmail
    })

    if (response.code === 200) {
      // 更换成功后，关闭对话框并重置表单
      showChangeEmailDialog.value = false
      changeEmailForm.value = {
        newEmail: ''
      }

      // 重新获取用户信息
      const userResponse = await getUserInfo()
      if (userResponse.code === 200 && userResponse.data) {
        userInfo.value = userResponse.data
      }

      alert('邮箱更换成功')
    }
  } catch (error) {
    console.error('更换邮箱失败:', error)
    // 处理邮箱相关错误
    if (error.response && error.response.status === 400 && error.response.data.message.includes('邮箱')) {
      emailErrors.value.newEmail = error.response.data.message
    } else {
      alert('更换邮箱失败，请重试')
    }
  }
}
</script>

<style scoped>
</style>
