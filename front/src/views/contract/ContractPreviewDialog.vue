<template>
  <el-dialog
    v-model="visible"
    title="合同预览"
    width="900px"
    append-to-body
    class="preview-dialog"
    @update:model-value="handleClose">
    <div class="preview-shell">
      <div class="preview-paper">
        <h1 class="contract-title">气瓶安全监管软件服务合同</h1>
        
        <div class="contract-header">
          <div class="header-row">
            <span>合同编号：{{ form.contractNo || '________________' }}</span>
            <span>签订日期：{{ today }}</span>
          </div>
          <div class="header-row">
            <span>有效期：{{ form.years }} 年</span>
            <span>（{{ form.startDate || 'YYYY-MM-DD' }} 至 {{ calculatedEndDate }}）</span>
          </div>
        </div>

        <div class="section-title">甲方（申请单位）信息</div>
        <div class="info-grid">
          <div class="label">单位名称：</div>
          <div class="value">{{ form.unitName || '________________' }}</div>
          
          <div class="label">社会信用代码：</div>
          <div class="value">{{ form.unitCode || '________________' }}</div>
          
          <div class="label">单位地址：</div>
          <div class="value full">{{ form.unitAddress || '________________' }}</div>
          
          <div class="label">法定代表人：</div>
          <div class="value">{{ form.unitPerson || '________________' }}</div>

          <div class="label">联系电话：</div>
          <div class="value">{{ form.unitPhone || '________________' }}</div>

          <div class="label">开户银行：</div>
          <div class="value">{{ form.unitBank || '________________' }}</div>

          <div class="label">银行账号：</div>
          <div class="value">{{ form.unitAccount || '________________' }}</div>

          <div class="label">经营气瓶类型：</div>
          <div class="value full">{{ form.unitTypes || '________________' }}</div>
        </div>

        <div class="section-title">乙方（瓶安保）信息</div>
        <div class="info-grid">
          <div class="label">公司名称：</div>
          <div class="value">{{ form.compName || '瓶安保科技有限公司' }}</div>
          
          <div class="label">统一信用代码：</div>
          <div class="value">{{ form.compCode || '91330100XXXXXXXXXX' }}</div>
          
          <div class="label">公司地址：</div>
          <div class="value full">{{ form.compAddress || '浙江省杭州市西湖区XXX街道XXX号' }}</div>
          
          <div class="label">联系方式：</div>
          <div class="value">{{ form.compPhone || '0571-88888888' }}</div>

          <div class="label">开户银行：</div>
          <div class="value">{{ form.compBank || '中国工商银行杭州分行' }}</div>

          <div class="label">银行账号：</div>
          <div class="value full">{{ form.compAccount || '1202020209900088888' }}</div>
        </div>

        <div class="contract-body">
          <p>甲乙双方本着平等互利、诚实信用的原则，经友好协商，就甲方使用乙方提供的“瓶安保”气瓶安全监管软件服务事宜，达成如下合同：</p>

          <h4>第一条：服务内容</h4>
          <p>1. 乙方向甲方提供气瓶安全监管软件系统，包括但不限于气瓶建档、充装记录、流转追踪、定期检验提醒等功能。</p>
          <p>2. 乙方负责软件系统的日常维护、技术升级及保障系统数据的安全与稳定。</p>
          <p>3. 乙方为甲方提供必要的操作培训及7x24小时远程技术支持服务。</p>

          <h4>第二条：服务期限</h4>
          <p>本合同服务期限为 <strong>{{ form.years }}</strong> 年，自 <strong>{{ form.startDate || '____年__月__日' }}</strong> 起至 <strong>{{ calculatedEndDate }}</strong> 止。</p>

          <h4>第三条：服务费用及支付方式</h4>
          <p>1. 本合同总费用为人民币（大写）：<strong class="money">{{ chineseAmount }}</strong>（¥ {{ calculatedAmount }}）。</p>
          <p>2. 支付方式：甲方应于合同签订之日起 5 个工作日内向乙方指定账户支付。</p>

          <h4>第四条：气瓶监管范围</h4>
          <p>本次纳入监管系统的气瓶信息如下：</p>
          <table class="fee-table">
            <thead>
              <tr>
                <th>气瓶类型</th>
                <th>数量（只）</th>
                <th>服务单价（元/只/年）</th>
                <th>年份</th>
                <th>小计（元）</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>{{ form.cylinderTypeName || '' }}</td>
                <td>{{ form.quantity }}</td>
                <td>{{ currentUnitPrice }}</td>
                <td>{{ form.years }}</td>
                <td>{{ calculatedAmount }}</td>
              </tr>
              <tr>
                <td colspan="4" style="text-align: right; font-weight: bold;">合计：</td>
                <td style="font-weight: bold;">¥ {{ calculatedAmount }}</td>
              </tr>
            </tbody>
          </table>

          <h4>第五条：双方权利义务</h4>
          <p>1. 甲方应确保录入系统的数据真实有效，并按规定规范使用气瓶。</p>
          <p>2. 乙方有权监督甲方的系统使用情况，并对异常数据进行核查。</p>

          <h4>第六条：违约责任</h4>
          <p>任何一方违反本合同约定，应赔偿因此给对方造成的损失。甲方逾期付款的，每日按应付金额的千分之三支付违约金。</p>

          <h4>第七条：争议解决</h4>
          <p>本合同履行过程中发生争议，双方应友好协商；协商不成的，提交乙方所在地人民法院诉讼解决。</p>

          <h4>第八条：其他约定</h4>
          <p>本合同一式两份，甲乙双方各执一份，具有同等法律效力。本合同自双方签字盖章之日起生效。</p>
        </div>

        <div class="signatures">
          <div class="sign-box">
            <p>甲方（盖章）：________________</p>
            <br>
            <p>授权代表（签字）：____________</p>
            <br>
            <p>日期：______年____月____日</p>
          </div>
          <div class="sign-box">
            <p>乙方（盖章）：________________</p>
            <br>
            <p>授权代表（签字）：____________</p>
            <br>
            <p>日期：______年____月____日</p>
          </div>
        </div>

      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">返回修改</el-button>
        <el-button type="primary" @click="handleSubmit">确认无误，提交申请</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const PRICE_MAP: Record<number, number> = {
  1: 100,  // LPG
  2: 100,  // CNG
  3: 120,  // O2
  4: 150,  // N2
  5: 90,   // CO2
  6: 110,  // AR
  7: 200,  // H2
  8: 180   // ACETYLENE
}

interface Props {
  modelValue: boolean
  form: any
}

const props = defineProps<Props>()
const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'submit': []
}>()

const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const getUnitPrice = (typeId: number) => {
  return PRICE_MAP[typeId] || PRICE_MAP[1]
}

const currentUnitPrice = computed(() => {
  return getUnitPrice(props.form.cylinderTypeId)
})

const calculatedAmount = computed(() => {
  const price = getUnitPrice(props.form.cylinderTypeId)
  const qty = Number(props.form.quantity) || 0
  const yrs = Number(props.form.years) || 0
  return price * qty * yrs
})

const chineseAmount = computed(() => {
  const n = calculatedAmount.value
  if (n === 0) return '零元整'
  return `人民币${n}元整` 
})

const calculatedEndDate = computed(() => {
  if (!props.form.startDate) return '____年__月__日'
  const start = new Date(props.form.startDate)
  const end = new Date(start)
  end.setFullYear(end.getFullYear() + Number(props.form.years || 0))
  end.setDate(end.getDate() - 1)
  return end.toISOString().split('T')[0]
})

const today = computed(() => {
  const d = new Date()
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`
})

const handleClose = () => {
  emit('update:modelValue', false)
}

const handleSubmit = () => {
  emit('submit')
}
</script>

<style scoped>
.preview-dialog :deep(.el-dialog__body) {
  padding: 0;
  background-color: #525659;
}

.preview-shell {
  padding: 24px;
  max-height: 75vh;
  overflow-y: auto;
  /* 移除 flex 布局，避免长内容滚动时高度计算问题导致阴影截断 */
  /* display: flex; */
  /* justify-content: center; */
}

.preview-paper {
  background: white;
  width: 100%;
  max-width: 800px;
  margin: 0 auto; /* 使用 margin 居中 */
  padding: 40px 50px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  font-family: "SimSun", "Songti SC", serif;
  color: #000;
  font-size: 14pt;
  line-height: 1.8;
  min-height: 800px; /* 确保最小高度 */
}

.contract-title {
  text-align: center;
  font-size: 22pt;
  font-weight: bold;
  margin-bottom: 30px;
  font-family: "SimHei", "Heiti SC", sans-serif;
}

.contract-header {
  margin-bottom: 25px;
  border-bottom: 2px solid #000;
  padding-bottom: 10px;
}

.header-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.section-title {
  font-weight: bold;
  background-color: #f0f0f0;
  padding: 5px 10px;
  margin-top: 20px;
  margin-bottom: 10px;
  border-left: 4px solid #333;
}

.info-grid {
  display: grid;
  grid-template-columns: 100px 1fr 120px 1fr;
  gap: 8px 10px;
  font-size: 12pt;
  margin-bottom: 20px;
}

.info-grid .label {
  text-align: right;
  color: #666;
  white-space: nowrap;
}

.info-grid .value {
  border-bottom: 1px solid #ccc;
  min-height: 24px;
  padding-left: 5px;
}

.info-grid .value.full {
  grid-column: span 3;
}

.contract-body h4 {
  font-size: 15pt;
  font-weight: bold;
  margin-top: 20px;
  margin-bottom: 10px;
  font-family: "SimHei", "Heiti SC", sans-serif;
}

.contract-body p {
  text-indent: 2em;
  margin-bottom: 8px;
  text-align: justify;
}

.money {
  text-decoration: underline;
  padding: 0 5px;
}

.fee-table {
  width: 100%;
  border-collapse: collapse;
  margin: 15px 0;
  font-size: 12pt;
}

.fee-table th, .fee-table td {
  border: 1px solid #000;
  padding: 8px;
  text-align: center;
}

.fee-table th {
  background-color: #f8f8f8;
}

.signatures {
  margin-top: 50px;
  display: flex;
  justify-content: space-between;
  padding: 0 20px;
}

.sign-box {
  width: 45%;
}
</style>
