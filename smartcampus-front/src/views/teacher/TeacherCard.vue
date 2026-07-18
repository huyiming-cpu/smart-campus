<template>
  <div class="page">
    <h3>💳 一卡通信息</h3>
    <el-card v-if="card.cardNo" shadow="hover" class="balance-card">
      <div class="balance-label">当前余额</div>
      <div class="balance-value">&yen;{{ card.balance?.toFixed(2)||'0.00' }}</div>
      <div class="card-info">卡号：{{ card.cardNo }} | <el-tag :type="card.cardStatus==='NORMAL'?'success':'danger'" size="small">{{ card.cardStatus==='NORMAL'?'正常':card.cardStatus }}</el-tag></div>
      <el-button type="primary" size="small" style="margin-top:12px" @click="rechargeDv=true">在线充值</el-button>
    </el-card>
    <el-empty v-else description="暂无校园卡信息"/>

    <h4 style="margin-top:24px;margin-bottom:12px">最近消费清单</h4>
    <el-table :data="transactions" size="small" border empty-text="暂无交易记录">
      <el-table-column label="类型" width="80">
        <template #default="{row}"><el-tag :type="row.transactionType==='CONSUME'?'danger':'success'" size="small">{{row.transactionType==='CONSUME'?'消费':'充值'}}</el-tag></template>
      </el-table-column>
      <el-table-column label="金额" width="100">
        <template #default="{row}"><span :style="{color:row.transactionType==='CONSUME'?'#f56c6c':'#67c23a'}">{{row.transactionType==='CONSUME'?'-':'+'}}&yen;{{row.amount}}</span></template>
      </el-table-column>
      <el-table-column prop="balance" label="余额" width="80"/>
      <el-table-column prop="merchant" label="商户" min-width="150"/>
      <el-table-column prop="transactionTime" label="时间" width="170"/>
    </el-table>

    <el-dialog v-model="rechargeDv" title="在线充值" width="350px">
      <el-form-item label="金额"><el-input-number v-model="ramt" :min="1" :max="5000" :step="50"/></el-form-item>
      <div style="color:#999">充值后余额：&yen;{{((card.balance||0)+ramt).toFixed(2)}}</div>
      <template #footer><el-button @click="rechargeDv=false">取消</el-button><el-button type="primary" @click="doRecharge">确认</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const card = ref({}), transactions = ref([]), rechargeDv = ref(false), ramt = ref(100)

const load = async () => {
  try {
    const r = await request.get('/api/teacher/card')
    const d = r.data || {}
    card.value = { cardNo:d.cardNo, balance:d.balance, cardStatus:d.cardStatus }
    transactions.value = (d.transactions||[]).slice(0, 5)
  } catch { }
}

const doRecharge = async () => {
  try { await request.post('/api/student/card/recharge', {amount:ramt.value}); ElMessage.success('充值成功'); rechargeDv.value = false; load() }
  catch { ElMessage.error('充值失败') }
}

onMounted(load)
</script>
<style scoped>
.page{padding:20px;max-width:800px}h3{margin-bottom:16px}
.balance-card{text-align:center;padding:20px;margin-bottom:20px}
.balance-label{font-size:14px;color:#999}
.balance-value{font-size:48px;font-weight:bold;color:#2c7a47;margin:8px 0}
.card-info{color:#666;font-size:13px}
</style>
