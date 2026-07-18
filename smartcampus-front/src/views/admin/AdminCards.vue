<template>
  <div class="page"><h3>💳 一卡通与缴费</h3>
    <el-tabs v-model="tab">
      <el-tab-pane label="一卡通" name="cards"/>
      <el-tab-pane label="缴费管理" name="payments"/>
    </el-tabs>
    <div v-if="tab==='cards'">
      <el-button type="primary" size="small" @click="txDv=true" style="margin-bottom:12px">添加交易</el-button>
      <el-button size="small" @click="sync">同步数据</el-button>
      <el-table :data="cards" v-loading="cLoading" border size="small" style="margin-top:12px">
        <el-table-column label="用户" width="110"><template #default="{row}">{{ getName(row.userId) }} ({{ row.userId }})</template></el-table-column>
        <el-table-column prop="cardNo" label="卡号" width="140"/>
        <el-table-column label="余额" width="80"><template #default="{row}">&yen;{{ row.balance }}</template></el-table-column>
        <el-table-column label="状态" width="70"><template #default="{row}"><el-tag :type="row.cardStatus==='NORMAL'?'success':'danger'" size="small">{{ row.cardStatus==='NORMAL'?'正常':row.cardStatus }}</el-tag></template></el-table-column>
        <el-table-column prop="dailyLimit" label="日限额" width="70"/><el-table-column prop="issueDate" label="发卡日" width="100"/>
      </el-table>
      <h4 style="margin-top:20px">交易流水</h4>
      <el-table :data="txs" v-loading="tLoading" border size="small">
        <el-table-column prop="userId" label="UID" width="60"/><el-table-column prop="cardNo" label="卡号" width="140"/>
        <el-table-column label="类型" width="70"><template #default="{row}"><el-tag :type="row.transactionType==='CONSUME'?'danger':'success'" size="small">{{ row.transactionType==='CONSUME'?'消费':'充值' }}</el-tag></template></el-table-column>
        <el-table-column prop="amount" label="金额" width="80"/><el-table-column prop="balance" label="余额" width="80"/>
        <el-table-column prop="merchant" label="商户" min-width="120"/><el-table-column prop="transactionTime" label="时间" width="170"/>
      </el-table>
    </div>
    <div v-if="tab==='payments'">
      <el-button type="primary" size="small" @click="payDv=true" style="margin-bottom:12px">添加缴费</el-button>
      <el-table :data="payments" v-loading="pLoading" border size="small">
        <el-table-column label="学生" width="110"><template #default="{row}">{{ getName(row.studentId) }} ({{ row.studentId }})</template></el-table-column>
        <el-table-column prop="semester" label="学期" width="120"/><el-table-column prop="paymentType" label="类型" width="70"/>
        <el-table-column label="应缴" width="80"><template #default="{row}">&yen;{{ row.totalAmount }}</template></el-table-column>
        <el-table-column label="已缴" width="80"><template #default="{row}">&yen;{{ row.paidAmount }}</template></el-table-column>
        <el-table-column label="状态" width="70"><template #default="{row}"><el-tag :type="row.paymentStatus==='PAID'?'success':'danger'" size="small">{{ row.paymentStatus }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="70"><template #default="{row}"><el-button size="small" type="danger" @click="delPayment(row.id)">删</el-button></template></el-table-column>
      </el-table>
    </div>
    <el-dialog v-model="txDv" title="添加交易" width="400px">
      <el-form :model="txf" label-width="80px">
        <el-form-item label="用户ID"><el-input-number v-model="txf.userId"/></el-form-item>
        <el-form-item label="卡号"><el-input v-model="txf.cardNo"/></el-form-item>
        <el-form-item label="类型"><el-select v-model="txf.type"><el-option label="消费" value="CONSUME"/><el-option label="充值" value="RECHARGE"/></el-select></el-form-item>
        <el-form-item label="金额"><el-input-number v-model="txf.amount" :min="0"/></el-form-item>
        <el-form-item label="商户"><el-input v-model="txf.merchant"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="txDv=false">取消</el-button><el-button type="primary" @click="addTx">确定</el-button></template>
    </el-dialog>
    <el-dialog v-model="payDv" title="添加缴费" width="400px">
      <el-form :model="pf" label-width="80px">
        <el-form-item label="学生ID"><el-input-number v-model="pf.studentId"/></el-form-item>
        <el-form-item label="学期"><el-input v-model="pf.semester"/></el-form-item>
        <el-form-item label="类型"><el-select v-model="pf.paymentType"><el-option label="学费" value="TUITION"/><el-option label="住宿费" value="DORM"/></el-select></el-form-item>
        <el-form-item label="应缴"><el-input-number v-model="pf.totalAmount" :min="0"/></el-form-item>
        <el-form-item label="已缴"><el-input-number v-model="pf.paidAmount" :min="0"/></el-form-item>
        <el-form-item label="状态"><el-select v-model="pf.paymentStatus"><el-option label="已缴" value="PAID"/><el-option label="未缴" value="UNPAID"/><el-option label="部分" value="PARTIAL"/></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="payDv=false">取消</el-button><el-button type="primary" @click="addPay">确定</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
const tab=ref('cards'),cards=ref([]),cLoading=ref(false),txs=ref([]),tLoading=ref(false),payments=ref([]),pLoading=ref(false)
const users=ref([]),txDv=ref(false),txf=ref({userId:'',cardNo:'',type:'CONSUME',amount:0,merchant:'管理端'})
const payDv=ref(false),pf=ref({studentId:'',semester:'2025-2026-2',paymentType:'TUITION',totalAmount:5000,paidAmount:0,paymentStatus:'UNPAID'})
const getName=(id)=>{const u=users.value.find(x=>x.id===Number(id));return u?u.name:id}
const load=async()=>{
  try{const r=await request.get('/api/admin/users');users.value=r.data||[]}catch{}
  cLoading.value=tLoading.value=pLoading.value=true
  try{const r=await request.get('/api/admin/cards');cards.value=r.data||[]}catch{}
  try{const r=await request.get('/api/admin/cards/transactions');if(r.data)txs.value=r.data.slice(0,50)}catch{}
  try{const r=await request.get('/api/admin/payments');payments.value=r.data||[]}catch{}
  cLoading.value=tLoading.value=pLoading.value=false
}
const addTx=async()=>{try{await request.post('/api/admin/cards/transaction',txf.value);ElMessage.success('OK');txDv.value=false;load()}catch{ElMessage.error('失败')}}
const sync=async()=>{try{await request.post('/api/admin/cards/sync');ElMessage.success('同步完成')}catch{}}
const addPay=async()=>{try{await request.post('/api/admin/payments',pf.value);ElMessage.success('OK');payDv.value=false;load()}catch{ElMessage.error('失败')}}
const delPayment=async(id)=>{try{await request.delete(`/api/admin/payments/${id}`);ElMessage.success('已删除');load()}catch{}}
onMounted(load)
</script>
<style scoped>.page{padding:20px}h3{margin-bottom:16px}</style>
