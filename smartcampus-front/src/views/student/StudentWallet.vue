<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#5B9BD5,#4A8AD4);"><el-icon :size="20" color="#fff"><Wallet /></el-icon></div>
      <div><h2 class="page-title">一卡通与缴费</h2><p class="page-desc">校园卡余额查询、在线充值及学费缴纳</p></div>
    </div>

    <div class="card">
      <el-tabs v-model="tab" class="custom-tabs">
        <el-tab-pane name="card">
          <template #label><span class="tab-label"><el-icon :size="16"><CreditCard /></el-icon> 一卡通</span></template>
        </el-tab-pane>
        <el-tab-pane name="pay">
          <template #label><span class="tab-label"><el-icon :size="16"><Tickets /></el-icon> 学费缴费</span></template>
        </el-tab-pane>
      </el-tabs>

      <!-- 一卡通 -->
      <div v-if="tab==='card'">
        <el-empty v-if="!card.cardNo" description="暂无校园卡信息" />
        <template v-else>
          <div class="balance-card">
            <div class="balance-label">当前余额</div>
            <div class="balance-value">&yen;{{ card.balance?.toFixed(2) || '0.00' }}</div>
            <div class="card-info">
              卡号：{{ card.cardNo }}
              <el-tag :type="card.cardStatus==='NORMAL'?'success':'danger'" size="small">
                {{ card.cardStatus==='NORMAL'?'正常':card.cardStatus }}
              </el-tag>
            </div>
            <el-button size="small" class="recharge-btn" @click="rechargeDv=true">在线充值</el-button>
          </div>

          <h4 class="table-title">最近消费清单</h4>
          <el-table :data="(card.transactions||[]).slice(0,5)" size="small" border stripe empty-text="暂无交易记录">
            <el-table-column label="类型" width="80">
              <template #default="{row}"><el-tag :type="row.transactionType==='CONSUME'?'danger':'success'" size="small">{{ row.transactionType==='CONSUME'?'消费':'充值' }}</el-tag></template>
            </el-table-column>
            <el-table-column label="金额" width="100">
              <template #default="{row}"><span :style="{color:row.transactionType==='CONSUME'?'#f56c6c':'#67c23a'}">{{ row.transactionType==='CONSUME'?'-':'+' }}&yen;{{ row.amount }}</span></template>
            </el-table-column>
            <el-table-column prop="balance" label="余额" width="90" />
            <el-table-column prop="merchant" label="商户" min-width="160" />
            <el-table-column prop="transactionTime" label="时间" width="180" />
          </el-table>
        </template>
      </div>

      <!-- 学费缴费 -->
      <div v-if="tab==='pay'" v-loading="pLoad">
        <div class="pay-summary" v-if="payments.length>0">
          <div class="pay-stat">
            <div class="pay-stat-icon" style="background:#FEF3E2;"><el-icon :size="16" color="#F59E0B"><Money /></el-icon></div>
            <div><div class="pay-stat-label">应缴总额</div><div class="pay-stat-val">&yen;{{ payTotal }}</div></div>
          </div>
          <div class="pay-stat">
            <div class="pay-stat-icon" style="background:#ECFDF5;"><el-icon :size="16" color="#10B981"><CircleCheckFilled /></el-icon></div>
            <div><div class="pay-stat-label">已缴总额</div><div class="pay-stat-val paid">&yen;{{ paidTotal }}</div></div>
          </div>
          <div class="pay-stat">
            <div class="pay-stat-icon" style="background:#EEF2FF;"><el-icon :size="16" color="#6366F1"><DataAnalysis /></el-icon></div>
            <div><div class="pay-stat-label">待缴费用</div><div class="pay-stat-val pending">&yen;{{ pendingTotal }}</div></div>
          </div>
        </div>
        <el-table :data="payments" border stripe empty-text="暂无缴费记录">
          <el-table-column prop="semester" label="学期" width="140" />
          <el-table-column label="类型" width="100">
            <template #default="{row}"><el-tag size="small" :type="row.paymentType==='TUITION'?'warning':'info'">{{ row.paymentType==='TUITION'?'学费':'住宿费' }}</el-tag></template>
          </el-table-column>
          <el-table-column label="应缴金额" width="120">
            <template #default="{row}"><span class="amt due">&yen;{{ row.totalAmount }}</span></template>
          </el-table-column>
          <el-table-column label="已缴金额" width="120">
            <template #default="{row}"><span class="amt paid">&yen;{{ row.paidAmount||0 }}</span></template>
          </el-table-column>
          <el-table-column label="缴纳进度" min-width="180">
            <template #default="{row}">
              <div class="pay-progress">
                <el-progress :percentage="payProgress(row)" :color="payProgress(row)>=100?'#10B981':'#F59E0B'" :stroke-width="8" />
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="110">
            <template #default="{row}">
              <el-tag :type="row.paymentStatus==='PAID'?'success':row.paymentStatus==='PARTIAL'?'warning':'danger'" size="small">
                {{ row.paymentStatus==='PAID'?'已缴清':row.paymentStatus==='PARTIAL'?'部分缴纳':'未缴纳' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="dueDate" label="截止日期" width="130" />
        </el-table>
        <el-empty v-if="payments.length===0" description="暂无缴费记录" />
      </div>
    </div>

    <!-- 充值弹窗 -->
    <el-dialog v-model="rechargeDv" title="在线充值" width="360px">
      <el-form-item label="充值金额">
        <el-input-number v-model="ramt" :min="1" :max="5000" :step="50" style="width:100%" />
      </el-form-item>
      <div class="recharge-preview">充值后余额：<b>&yen;{{ ((card.balance||0)+ramt).toFixed(2) }}</b></div>
      <template #footer>
        <el-button @click="rechargeDv=false">取消</el-button>
        <el-button type="primary" @click="doRecharge">确认充值</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Wallet, CreditCard, Tickets, Money, CircleCheckFilled, DataAnalysis } from '@element-plus/icons-vue'

const tab = ref('card'), card = ref({}), payments = ref([]), pLoad = ref(false)
const rechargeDv = ref(false), ramt = ref(100)

const payTotal = computed(() => payments.value.reduce((s, p) => s + (p.totalAmount || 0), 0).toFixed(2))
const paidTotal = computed(() => payments.value.reduce((s, p) => s + (p.paidAmount || 0), 0).toFixed(2))
const pendingTotal = computed(() => {
  const v = payments.value.reduce((s, p) => s + ((p.totalAmount || 0) - (p.paidAmount || 0)), 0)
  return Math.max(0, v).toFixed(2)
})
const payProgress = (row) => {
  if (!row.totalAmount) return 0
  return Math.min(100, Math.round(((row.paidAmount || 0) / row.totalAmount) * 100))
}

const load = async () => {
  try { const r = await request.get('/api/student/card'); card.value = r.data || {} } catch {}
  pLoad.value = true
  try { const r = await request.get('/api/student/payments'); payments.value = r.data || [] } catch {}
  finally { pLoad.value = false }
}

const doRecharge = async () => {
  try { await request.post('/api/student/card/recharge', { amount: ramt.value }); ElMessage.success('充值成功'); rechargeDv.value = false; load() }
  catch { ElMessage.error('充值失败') }
}

onMounted(load)
</script>

<style scoped>
.page { padding: 20px 24px; max-width: 1100px; margin: 0 auto; font-family: "Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif; }
.page-head { display: flex; align-items: center; gap: 14px; margin-bottom: 20px; }
.page-icon { width: 42px; height: 42px; border-radius: 12px; display: flex; align-items: center; justify-content: center; box-shadow: 0 4px 12px rgba(91,155,213,.25); }
.page-title { font-size: 20px; font-weight: 700; color: #1A1A2E; margin: 0; }
.page-desc { font-size: 13px; color: #9CA3AF; margin: 2px 0 0; }

.card { background: #fff; border: 1px solid #EEF0F4; border-radius: 14px; padding: 20px; box-shadow: 0 2px 12px rgba(0,0,0,.02); }
.custom-tabs :deep(.el-tabs__header) { margin-bottom: 20px; }
.custom-tabs :deep(.el-tabs__item) { font-size: 14px; font-weight: 500; color: #6B7280; }
.custom-tabs :deep(.el-tabs__item.is-active) { color: #5B9BD5; font-weight: 600; }
.custom-tabs :deep(.el-tabs__active-bar) { background: #5B9BD5; }
.tab-label { display: flex; align-items: center; gap: 6px; }

.balance-card {
  text-align: center; padding: 32px 24px 24px;
  background: linear-gradient(135deg, #5B9BD5 0%, #6DB3E8 100%);
  border-radius: 14px; color: #fff; margin-bottom: 24px;
  box-shadow: 0 4px 16px rgba(91,155,213,.25);
}
.balance-label { font-size: 14px; color: rgba(255,255,255,.8); }
.balance-value { font-size: 44px; font-weight: 700; margin: 10px 0 16px; letter-spacing: 1px; }
.card-info { font-size: 13px; color: rgba(255,255,255,.85); display: flex; align-items: center; justify-content: center; gap: 8px; }
.recharge-btn { margin-top: 18px; background: #fff; color: #5B9BD5; border: none; padding: 8px 32px; border-radius: 8px; font-weight: 600; }
.recharge-btn:hover { background: #EBF3FC; }

.table-title { margin: 0 0 12px; font-size: 15px; color: #333; }

.amt { font-weight: 600; color: #1A1A2E; }
.amt.paid { color: #10B981; }
.amt.due { color: #F59E0B; }

/* 缴费统计 */
.pay-summary { display: grid; grid-template-columns: repeat(3, 1fr); gap: 14px; margin-bottom: 20px; }
.pay-stat { background: #fff; border: 1px solid #EEF0F4; border-radius: 12px; padding: 14px 18px; display: flex; align-items: center; gap: 12px; }
.pay-stat-icon { width: 38px; height: 38px; border-radius: 10px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.pay-stat-label { font-size: 12px; color: #9CA3AF; }
.pay-stat-val { font-size: 22px; font-weight: 700; color: #1A1A2E; letter-spacing: .5px; }
.pay-stat-val.paid { color: #10B981; }
.pay-stat-val.pending { color: #EF4444; }

.pay-progress { display: flex; align-items: center; }

.recharge-preview { color: #6B7280; font-size: 14px; margin-top: 8px; text-align: center; }
.recharge-preview b { color: #5B9BD5; font-size: 16px; }

@media (max-width: 768px) { .pay-summary { grid-template-columns: 1fr; } }
</style>
