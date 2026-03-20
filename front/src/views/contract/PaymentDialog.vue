<template>
  <el-dialog
    v-model="visible"
    title="合同缴费"
    width="500px"
    append-to-body
    @update:model-value="handleClose">
    <div v-if="contract">
      <div class="bg-blue-50 p-4 rounded mb-4">
        <div class="flex justify-between mb-2">
          <span class="text-gray-600">合同编号：</span>
          <span class="font-bold">{{ contract.contractNo }}</span>
        </div>
        <div class="flex justify-between">
          <span class="text-gray-600">应缴金额：</span>
          <span class="text-error font-bold text-lg">¥ {{ contract.finalAmount }}</span>
        </div>
      </div>
      <div class="mb-4">
        <label class="block text-sm font-medium text-gray-700 mb-2">支付方式</label>
        <el-radio-group v-model="paymentMethod">
          <el-radio label="bank">银行转账</el-radio>
          <el-radio label="alipay">支付宝企业付</el-radio>
        </el-radio-group>
      </div>

      <div class="mb-4">
         <label class="block text-sm font-medium text-gray-700 mb-2">支付凭证</label>
         <el-upload
           class="upload-demo"
           action="#" 
           :auto-upload="false"
           :limit="1"
           :on-change="(file) => fileList = [file]"
           :file-list="fileList">
           <el-button type="primary" link icon="Upload">上传凭证(PDF/IMG)</el-button>
           <template #tip>
             <div class="el-upload__tip">请上传转账回单或支付截图</div>
           </template>
         </el-upload>
      </div>

      <div class="mb-4">
        <label class="block text-sm font-medium text-gray-700 mb-2">备注</label>
        <el-input v-model="remark" type="textarea" :rows="2" placeholder="如有特殊说明请填写"></el-input>
      </div>

      <div class="text-xs text-gray-500">
        <i class="fa-solid fa-circle-info mr-1"></i> 缴费成功后请等待管理员审核。
      </div>
    </div>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确认支付</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'


interface Contract {
  socialCreditCode: string
  amount: string
}


interface Props {
  modelValue: boolean
  contract: Contract | null
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'submit': [data: {
    paymentMethod: string,
    paymentAmount: number,
    paymentVoucherUrl: string,
    remark: string
  }]
}>()

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const paymentMethod = ref('bank')
const remark = ref('')
const fileList = ref([])

const handleUploadSuccess = (response: any) => {
  if (response.code === 200) {
    // 假设后端返回 data 是 URL
    // 如果是 String 直接用，如果是 Object 取 url 字段
    const url = typeof response.data === 'string' ? response.data : response.data.url
    // 这里简单处理，实际可能需要存储 url
    fileList.value = [{ name: 'payment_voucher.pdf', url: url }]
  }
}

const handleClose = () => {
  emit('update:modelValue', false)
  fileList.value = []
  remark.value = ''
}

const handleSubmit = () => {
  // Mock upload result if no real upload performed for demo
  const mockUrl = fileList.value.length > 0 ? fileList.value[0].url : 'http://mock.url/voucher.pdf'
  
  emit('submit', {
    paymentMethod: paymentMethod.value,
    paymentAmount: Number(props.contract?.amount || 0),
    paymentVoucherUrl: mockUrl,
    remark: remark.value
  })
}
</script>
