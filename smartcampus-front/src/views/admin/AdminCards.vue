<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#0EA5E9,#0284C7);"><el-icon :size="20" color="#fff"><CreditCard /></el-icon></div>
      <div><h2 class="page-title">一卡通与缴费</h2><p class="page-desc">管理校园一卡通卡片、交易流水与缴费记录</p></div>
    </div>
    <div class="toolbar">
      <el-tabs v-model="tab">
        <el-tab-pane label="一卡通" name="cards"/>
        <el-tab-pane label="缴费管理" name="payments"/>
      </el-tabs>
    </div>

    <!-- 一卡通 -->
    <div class="card" v-if="tab==='cards'" v-loading="cLoading">
      <div class="card-actions">
        <el-button type="primary" size="small" @click="txDv=true"><el-icon :size="14" style="margin-right:4px"><Plus /></el-icon>添加交易</el-button>
        <el-button size="small" plain @click="sync"><el-icon :size="14" style="margin-right:4px"><Refresh /></el-icon>同步数据</el-button>
      </div>
      <div class="section-title">卡片列表</div>
      <el-table :data="cards" stripe border>
        <el-table-column label="用户" min-width="130"><template #default="{row}">{{ getName(row.userId) }} <span class="uid">({{ row.userId }})</span></template></el-table-column>
        <el-table-column prop="cardNo" label="卡号" width="150" />
        <el-table-column label="余额" width="100"><template #default="{row}"><span class="amt">&yen;{{ row.balance }}</span></template></el-table-column>
        <el-table-column label="状态" width="90"><template #default="{row}"><el-tag :type="row.cardStatus==='NORMAL'?'success':'danger'" size="small">{{ row.cardStatus==='NORMAL'?'正常':row.cardStatus }}</el-tag></template></el-table-column>
        <el-table-column prop="dailyLimit" label="日限额" width="90" />
        <el-table-column prop="issueDate" label="发卡日期" width="120" />
      </el-table>

      <div class="section-title" style="margin-top:24px">交易流水</div>
      <el-table :data="txs" stripe border empty-text="暂无交易记录">
        <el-table-column prop="userId" label="UID" width="70" />
        <el-table-column prop="cardNo" label="卡号" width="150" />
        <el-table-column label="类型" width="90">
          <template #default="{row}"><el-tag :type="row.transactionType==='CONSUME'?'danger':'success'" size="small">{{ row.transactionType==='CONSUME'?'消费':'充值' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="100"><template #default="{row}"><span :style="{color:row.transactionType==='CONSUME'?'#f56c6c':'#67c23a',fontWeight:'600'}">{{ row.transactionType==='CONSUME'?'-':'+' }}&yen;{{ row.amount }}</span></template></el-table-column>
        <el-table-column prop="balance" label="余额" width="100" />
        <el-table-column prop="merchant" label="商户" min-width="140" />
        <el-table-column prop="transactionTime" label="时间" width="180" />
      </el-table>
    </div>

    <!-- 缴费管理 -->
    <div class="card" v-if="tab==='payments'" v-loading="pLoading">
      <div class="card-actions">
        <el-button type="primary" size="small" @click="payDv=true"><el-icon :size="14" style="margin-right:4px"><Plus /></el-icon>添加缴费</el-button>
      </div>
      <el-table :data="payments" stripe border>
        <el-table-column label="学生" min-width="130"><template #default="{row}">{{ getName(row.studentId) }} <span class="uid">({{ row.studentId }})</span></template></el-table-column>
        <el-table-column prop="semester" label="学期" width="130" />
        <el-table-column label="类型" width="100">
          <template #default="{row}"><el-tag size="small" :type="row.paymentType==='TUITION'?'warning':'info'">{{ row.paymentType==='TUITION'?'学费':'住宿费' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="应缴金额" width="110"><template #default="{row}"><span class="amt due">&yen;{{ row.totalAmount }}</span></template></el-table-column>
        <el-table-column label="已缴金额" width="110"><template #default="{row}"><span class="amt paid">&yen;{{ row.paidAmount }}</span></template></el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{row}">
            <el-tag :type="row.paymentStatus==='PAID'?'success':row.paymentStatus==='PARTIAL'?'warning':'danger'" size="small">
              {{ row.paymentStatus==='PAID'?'已缴清':row.paymentStatus==='PARTIAL'?'部分缴纳':'未缴纳' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="90" fixed="right">
          <template #default="{row}"><el-button size="small" type="danger" plain @click="delPayment(row.id)"><el-icon :size="14"><Delete /></el-icon></el-button></template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 添加交易 -->
    <el-dialog v-model="txDv" title="添加交易" width="420px">
      <el-form :model="txf" label-width="80px">
        <el-form-item label="用户ID"><el-input-number v-model="txf.userId" style="width:100%"/></el-form-item>
        <el-form-item label="卡号"><el-input v-model="txf.cardNo"/></el-form-item>
        <el-form-item label="类型"><el-select v-model="txf.type" style="width:100%"><el-option label="消费" value="CONSUME"/><el-option label="充值" value="RECHARGE"/></el-select></el-form-item>
        <el-form-item label="金额"><el-input-number v-model="txf.amount" :min="0" style="width:100%"/></el-form-item>
        <el-form-item label="商户"><el-input v-model="txf.merchant"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="txDv=false">取消</el-button><el-button type="primary" @click="addTx">确定</el-button></template>
    </el-dialog>

    <!-- 添加缴费 -->
    <el-dialog v-model="payDv" title="添加缴费" width="420px">
      <el-form :model="pf" label-width="80px">
        <el-form-item label="学生ID"><el-input-number v-model="pf.studentId" style="width:100%"/></el-form-item>
        <el-form-item label="学期"><el-input v-model="pf.semester"/></el-form-item>
        <el-form-item label="类型"><el-select v-model="pf.paymentType" style="width:100%"><el-option label="学费" value="TUITION"/><el-option label="住宿费" value="DORM"/></el-select></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="应缴"><el-input-number v-model="pf.totalAmount" :min="0" style="width:100%"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="已缴"><el-input-number v-model="pf.paidAmount" :min="0" style="width:100%"/></el-form-item></el-col>
        </el-row>
        <el-form-item label="状态"><el-select v-model="pf.paymentStatus" style="width:100%"><el-option label="已缴" value="PAID"/><el-option label="未缴" value="UNPAID"/><el-option label="部分缴纳" value="PARTIAL"/></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="payDv=false">取消</el-button><el-button type="primary" @click="addPay">确定</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { CreditCard, Plus, Refresh, Delete } from '@element-plus/icons-vue'
const tab=ref('cards'),cards=ref([]),cLoading=ref(false),txs=ref([]),tLoading=ref(false),payments=ref([]),pLoading=ref(false)
const users=ref([]),txDv=ref(false),txf=ref({userId:'',cardNo:'',type:'CONSUME',amount:0,merchant:'管理端'})
const payDv=ref(false),pf=ref({studentId:'',semester:'2025-2026-2',paymentType:'TUITION',totalAmount:5000,paidAmount:0,paymentStatus:'UNPAID'})
const getName=(id)=>{const u=users.value.find(x=>x.id===Number(id));return u?u.name:id}
const load=async()=>{
  try{const r=await request.get('/api/admin/users');users.value=r.data||[]}catch{}
  cLoading.value=true;tLoading.value=true;pLoading.value=true
  try{const r=await request.get('/api/admin/cards');cards.value=r.data||[]}catch{}
  try{const r=await request.get('/api/admin/cards/transactions');if(r.data)txs.value=r.data.slice(0,50)}catch{}
  try{const r=await request.get('/api/admin/payments');payments.value=r.data||[]}catch{}
  cLoading.value=false;tLoading.value=false;pLoading.value=false
}
const addTx=async()=>{try{await request.post('/api/admin/cards/transaction',txf.value);ElMessage.success('OK');txDv.value=false;load()}catch{ElMessage.error('失败')}}
const sync=async()=>{try{await request.post('/api/admin/cards/sync');ElMessage.success('同步完成')}catch{}}
const addPay=async()=>{try{await request.post('/api/admin/payments',pf.value);ElMessage.success('OK');payDv.value=false;load()}catch{ElMessage.error('失败')}}
const delPayment=async(id)=>{try{await request.delete(`/api/admin/payments/${id}`);ElMessage.success('已删除');load()}catch{}}
onMounted(load)
</script>
<style scoped>
.page{padding:20px 24px;max-width:1200px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:14px;margin-bottom:20px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;flex-shrink:0}
.page-title{font-size:20px;font-weight:700;margin:0;line-height:1.3;color:#1a1a2e}
.page-desc{font-size:13px;color:#8c8c8c;margin:2px 0 0}
.toolbar{margin-bottom:16px}
.card{border:1px solid #EEF0F4;border-radius:14px;padding:20px;box-shadow:0 2px 12px rgba(0,0,0,.02);background:#fff}
.card-actions{display:flex;gap:8px;margin-bottom:16px}
.section-title{font-size:15px;font-weight:600;color:#1a1a2e;margin-bottom:12px}
.uid{color:#9CA3AF;font-size:12px}
.amt{font-weight:600;color:#1A1A2E}
.amt.paid{color:#10B981}
.amt.due{color:#F59E0B}
</style>
