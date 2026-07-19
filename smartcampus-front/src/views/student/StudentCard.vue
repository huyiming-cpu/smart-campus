<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#5B9BD5,#4A8AD4);"><el-icon :size="20" color="#fff"><CreditCard /></el-icon></div>
      <div><h2 class="page-title">一卡通</h2><p class="page-desc">校园卡余额查询与在线充值</p></div>
    </div>

    <div class="card info-card" v-loading="loading">
      <div v-if="card.cardNo" class="balance-section">
        <div class="balance-label">当前余额</div>
        <div class="balance-amount">&yen; {{ card.balance?.toFixed(2) || '0.00' }}</div>
        <div class="card-detail">卡号：{{ card.cardNo }} | 状态：<el-tag :type="card.cardStatus==='NORMAL'?'success':'danger'" size="small">{{ card.cardStatus==='NORMAL'?'正常':card.cardStatus }}</el-tag></div>
        <el-button type="primary" size="small" style="margin-top:12px" @click="showRecharge=true">充值</el-button>
      </div>
      <el-empty v-else description="暂无校园卡信息，请联系管理员办理"/>
    </div>

    <el-dialog v-model="showRecharge" title="在线充值" width="350px">
      <el-form>
        <el-form-item label="充值金额">
          <el-input-number v-model="rechargeAmount" :min="1" :max="5000" :step="50" controls-position="right" style="width:100%"/>
        </el-form-item>
        <div style="color:#909399;font-size:13px">充值后余额：&yen;{{ ((card.balance||0)+rechargeAmount).toFixed(2) }}</div>
      </el-form>
      <template #footer>
        <el-button @click="showRecharge=false">取消</el-button>
        <el-button type="primary" @click="doRecharge" :loading="recharging">确认充值</el-button>
      </template>
    </el-dialog>

    <div class="card" style="margin-top:20px" v-loading="loading">
      <el-table :data="card.transactions||[]" stripe empty-text="暂无消费记录">
        <el-table-column label="类型" width="100">
          <template #default="{row}"><el-tag :type="row.transactionType==='CONSUME'?'danger':'success'" size="small">{{ row.transactionType==='CONSUME'?'消费':'充值' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="金额" width="120">
          <template #default="{row}"><span :class="row.transactionType==='CONSUME'?'consume':'recharge'">{{ row.transactionType==='CONSUME'?'-':'+' }}&yen;{{ row.amount?.toFixed(2) }}</span></template>
        </el-table-column>
        <el-table-column label="交易后余额" width="120">
          <template #default="{row}">&yen;{{ row.balance?.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="merchant" label="商户" min-width="160"/>
        <el-table-column prop="transactionTime" label="时间" width="170"/>
      </el-table>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyCard } from '@/api/card'
import request from '@/utils/request'
import { CreditCard } from '@element-plus/icons-vue'
const loading=ref(false), card=ref({}), showRecharge=ref(false), rechargeAmount=ref(100), recharging=ref(false)
const load=async()=>{loading.value=true;try{const r=await getMyCard();card.value=r.data||{}}catch{}finally{loading.value=false}}
const doRecharge=async()=>{
  recharging.value=true
  try{const r=await request.post('/api/student/card/recharge',{amount:rechargeAmount.value});if(r.code===200){ElMessage.success('充值成功');showRecharge.value=false;load()}}
  catch{ElMessage.error('充值失败')}
  finally{recharging.value=false}
}
onMounted(load)
</script>
<style scoped>
.page{padding:20px 24px;max-width:1200px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:14px;margin-bottom:20px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;box-shadow:0 4px 12px rgba(91,155,213,.25)}
.page-title{font-size:20px;font-weight:700;color:#1A1A2E;margin:0}
.page-desc{font-size:13px;color:#9CA3AF;margin:2px 0 0}
.card{background:#fff;border:1px solid #EEF0F4;border-radius:14px;padding:20px;box-shadow:0 2px 12px rgba(0,0,0,.02)}
.info-card{text-align:center;padding:30px 20px}
.balance-label{font-size:14px;color:#909399}.balance-amount{font-size:48px;font-weight:bold;color:#409eff;margin:10px 0}
.card-detail{font-size:13px;color:#606266;margin-top:8px}
.consume{color:#f56c6c}.recharge{color:#67c23a}
</style>
