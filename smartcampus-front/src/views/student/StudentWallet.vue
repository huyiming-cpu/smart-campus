<template>
  <div class="page"><h3>💳 一卡通与缴费</h3>
    <el-tabs v-model="tab">
      <el-tab-pane label="一卡通" name="card"/>
      <el-tab-pane label="学费缴费" name="pay"/>
    </el-tabs>
    <div v-if="tab==='card'">
      <el-card v-if="card.cardNo" shadow="hover" style="text-align:center;margin-bottom:16px">
        <div style="font-size:14px;color:#999">当前余额</div>
        <div style="font-size:42px;font-weight:bold;color:#409eff">&yen;{{card.balance?.toFixed(2)||'0.00'}}</div>
        <div style="color:#666;margin-top:8px">卡号：{{card.cardNo}} | <el-tag :type="card.cardStatus==='NORMAL'?'success':'danger'" size="small">{{card.cardStatus==='NORMAL'?'正常':card.cardStatus}}</el-tag></div>
        <el-button type="primary" size="small" style="margin-top:10px" @click="rechargeDv=true">充值</el-button>
      </el-card>
      <el-empty v-else description="暂无校园卡"/>
      <el-table :data="card.transactions||[]" size="small"><el-table-column label="类型" width="80"><template #default="{row}"><el-tag :type="row.transactionType==='CONSUME'?'danger':'success'" size="small">{{row.transactionType==='CONSUME'?'消费':'充值'}}</el-tag></template></el-table-column>
        <el-table-column label="金额" width="80"><template #default="{row}"><span :style="{color:row.transactionType==='CONSUME'?'#f56c6c':'#67c23a'}">{{row.transactionType==='CONSUME'?'-':'+'}}&yen;{{row.amount}}</span></template></el-table-column>
        <el-table-column prop="balance" label="余额" width="80"/><el-table-column prop="merchant" label="商户" min-width="120"/><el-table-column prop="transactionTime" label="时间" width="170"/>
      </el-table>
    </div>
    <div v-if="tab==='pay'">
      <el-table :data="payments" v-loading="pLoad" size="small">
        <el-table-column prop="semester" label="学期" width="120"/><el-table-column label="类型" width="80"><template #default="{row}">{{row.paymentType==='TUITION'?'学费':'住宿费'}}</template></el-table-column>
        <el-table-column label="应缴" width="80"><template #default="{row}">&yen;{{row.totalAmount}}</template></el-table-column>
        <el-table-column label="已缴" width="80"><template #default="{row}">&yen;{{row.paidAmount||0}}</template></el-table-column>
        <el-table-column label="状态" width="80"><template #default="{row}"><el-tag :type="row.paymentStatus==='PAID'?'success':row.paymentStatus==='PARTIAL'?'warning':'danger'" size="small">{{row.paymentStatus==='PAID'?'已缴':'未缴'}}</el-tag></template></el-table-column>
      </el-table>
    </div>
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
const tab=ref('card'),card=ref({}),payments=ref([]),pLoad=ref(false),rechargeDv=ref(false),ramt=ref(100)
const load=async()=>{
  try{const r=await request.get('/api/student/card');card.value=r.data||{}}catch{}
  pLoad.value=true;try{const r=await request.get('/api/student/payments');payments.value=r.data||[]}catch{}finally{pLoad.value=false}
}
const doRecharge=async()=>{try{await request.post('/api/student/card/recharge',{amount:ramt.value});ElMessage.success('充值成功');rechargeDv.value=false;load()}catch{ElMessage.error('充值失败')}}
onMounted(load)
</script>
<style scoped>.page{padding:20px}h3{margin-bottom:16px}</style>
