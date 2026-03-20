<template>
  <div class="fade-in">
    <div class="flex justify-between items-center mb-6">
      <div>
        <h2 class="text-[20px] font-bold text-gray-800">我的消息</h2>
        <p class="text-gray-500 text-xs mt-1">
          未读消息 <span class="text-error font-bold">{{ unreadCount }}</span> 条
        </p>
      </div>
      <el-button 
        v-if="unreadCount > 0"
        type="primary" 
        text
        @click="handleMarkAllRead">
        全部标为已读
      </el-button>
    </div>

    <el-card shadow="never">
      <!-- 消息标签页 -->
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部消息" name="all">
          <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="mr-2"></el-badge>
        </el-tab-pane>
        <el-tab-pane label="合同相关" name="contract">
          <el-badge :value="1" :hidden="true"></el-badge>
        </el-tab-pane>
        <el-tab-pane label="续约提醒" name="renewal">
          <el-badge :value="2" :hidden="true"></el-badge>
        </el-tab-pane>
        <el-tab-pane label="系统通知" name="system"></el-tab-pane>
      </el-tabs>

      <!-- 消息列表 -->
      <el-timeline class="mt-4">
        <el-timeline-item
          v-for="message in filteredMessages"
          :key="message.id"
          :timestamp="message.time"
          placement="top"
          :type="message.isRead ? 'info' : 'primary'">
          <el-card 
            :class="{ 'bg-blue-50': !message.isRead }"
            shadow="hover"
            class="cursor-pointer"
            @click="handleMessageClick(message)">
            <div class="flex items-start justify-between">
              <div class="flex-1">
                <div class="flex items-center mb-2">
                  <el-tag :type="getMessageTypeTag(message.type)" size="small" class="mr-2">
                    {{ message.typeText }}
                  </el-tag>
                  <span class="font-medium text-gray-800">{{ message.title }}</span>
                  <el-badge v-if="!message.isRead" is-dot class="ml-2"></el-badge>
                </div>
                <p class="text-sm text-gray-600">{{ message.content }}</p>
              </div>
              <el-button 
                v-if="message.actionText"
                type="primary" 
                size="small"
                @click.stop="handleAction(message)">
                {{ message.actionText }}
              </el-button>
            </div>
          </el-card>
        </el-timeline-item>
      </el-timeline>

      <el-empty v-if="filteredMessages.length === 0" description="暂无消息" />
    </el-card>

    <!-- 消息详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="currentMessage?.title"
      width="600px">
      <div v-if="currentMessage">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="消息类型">
            <el-tag :type="getMessageTypeTag(currentMessage.type)" size="small">
              {{ currentMessage.typeText }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="发送时间">{{ currentMessage.time }}</el-descriptions-item>
          <el-descriptions-item label="消息内容">
            <div class="whitespace-pre-wrap">{{ currentMessage.detailContent || currentMessage.content }}</div>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
        <el-button 
          v-if="currentMessage?.actionText"
          type="primary"
          @click="handleAction(currentMessage)">
          {{ currentMessage.actionText }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('all')
const detailDialogVisible = ref(false)
const currentMessage = ref(null)

const messages = ref([
  {
    id: 1,
    type: 'renewal',
    typeText: '续约提醒',
    title: '合同即将到期提醒',
    content: '您的合同 CON-2024003 将于 30 天后到期，请及时办理续约手续。',
    detailContent: '尊敬的用户：\n\n您的合同 CON-2024003（氧气瓶监管服务）将于 2024-11-15 到期。\n\n为避免服务中断，请您尽快办理续约手续。如有疑问，请联系客服：400-123-4567。',
    time: '2024-10-16 09:30',
    isRead: false,
    actionText: '去续约'
  },
  {
    id: 2,
    type: 'contract',
    typeText: '合同通知',
    title: '新合同待审核',
    content: '您提交的新合同申请 CON-2024006 正在审核中，请耐心等待。',
    time: '2024-10-15 16:20',
    isRead: false,
    actionText: '查看详情'
  },
  {
    id: 3,
    type: 'system',
    typeText: '系统公告',
    title: '系统维护通知',
    content: '系统将于本周五晚 22:00-24:00 进行例行维护，期间可能影响部分功能使用。',
    time: '2024-10-15 10:00',
    isRead: false,
    actionText: null
  },
  {
    id: 4,
    type: 'contract',
    typeText: '合同通知',
    title: '合同签署完成',
    content: '合同 CON-2024005 已完成电子签署，您可以下载查看。',
    time: '2024-10-14 14:30',
    isRead: true,
    actionText: '下载合同'
  },
  {
    id: 5,
    type: 'renewal',
    typeText: '续约提醒',
    title: '续约审核通过',
    content: '您的续约申请已通过审核，请尽快完成缴费。',
    time: '2024-10-13 11:20',
    isRead: true,
    actionText: '去缴费'
  }
])

const unreadCount = computed(() => {
  return messages.value.filter(m => !m.isRead).length
})

const filteredMessages = computed(() => {
  if (activeTab.value === 'all') return messages.value
  return messages.value.filter(m => m.type === activeTab.value)
})

function getMessageTypeTag(type) {
  const tagMap = {
    'contract': 'primary',
    'renewal': 'warning',
    'system': 'info'
  }
  return tagMap[type] || 'info'
}

function handleTabChange(tab) {
  console.log('切换标签:', tab)
}

function handleMessageClick(message) {
  if (!message.isRead) {
    message.isRead = true
    userStore.unreadCount--
  }
  currentMessage.value = message
  detailDialogVisible.value = true
}

function handleAction(message) {
  ElMessage.success(`执行操作: ${message.actionText}`)
  detailDialogVisible.value = false
  
  if (message.actionText === '去续约') {
    router.push('/renewal')
  }
}

function handleMarkAllRead() {
  messages.value.forEach(m => m.isRead = true)
  userStore.markAllRead()
  ElMessage.success('已全部标记为已读')
}
</script>

<style scoped>
</style>
