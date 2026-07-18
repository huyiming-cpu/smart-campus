<template>
  <div class="page">
    <div class="page-header"><h2>💰 学费缴费</h2></div>
    <el-card shadow="never" v-loading="loading">
      <el-table :data="payments" empty-text="暂无缴费记录" show-summary>
        <el-table-column prop="semester" label="学期" width="130"/>
        <el-table-column prop="paymentType" label="类型" width="100">
          <template #default="{row}"><el-tag size="small">{{ row.paymentType==='TUITION'?'学费':'住宿费' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="应缴" width="110"><template #default="{row}">&yen;{{ row.totalAmount?.toFixed(2) }}</template></el-table-column>
        <el-table-column label="已缴" width="110"><template #default="{row}"><span style="color:#67c23a">&yen;{{ row.paidAmount?.toFixed(2)||'0.00' }}</span></template></el-table-column>
        <el-table-column label="未缴" width="110"><template #default="{row}"><span style="color:#f56c6c">&yen;{{ (row.totalAmount-(row.paidAmount||0)).toFixed(2) }}</span></template></el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{row}"><el-tag :type="row.paymentStatus==='PAID'?'success':row.paymentStatus==='PARTIAL'?'warning':'danger'" size="small">{{ row.paymentStatus==='PAID'?'已缴':row.paymentStatus==='PARTIAL'?'部分':'未缴' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="paymentTime" label="缴费时间" width="170"/>
        <el-table-column label="操作" width="100">
          <template #default="{row}">
            <el-button v-if="row.paymentStatus!=='PAID'" type="primary" size="small" @click="payFromCard(row)">缴费</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="payVisible" title="一卡通缴费" width="400px">
      <p>将从一卡通余额中扣除 <strong>&yen;{{ payAmount }}</strong></p>
      <p style="color:#909399">当前余额：&yen;{{ cardBalance?.toFixed(2)||'0.00' }}</p>
      <p v-if="payAmount > (cardBalance||0)" style="color:#f56c6c">⚠ 余额不足，请先充值</p>
      <template #footer>
        <el-button @click="payVisible=false">取消</el-button>
        <el-button type="primary" @click="doPay" :disabled="payAmount>(cardBalance||0)" :loading="paying">确认缴费</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyPayments, getMyCard } from '@/api/card'
const loading=ref(false), payments=ref([]), cardBalance=ref(0), payVisible=ref(false), payAmount=ref(0), paying=ref(false), currentRow=ref(null)
const load=async()=>{loading.value=true;try{const r=await getMyPayments();payments.value=r.data||[]}catch{}finally{loading.value=false}}
const loadCard=async()=>{try{const r=await getMyCard();cardBalance.value=r.data?.balance||0}catch{}}
const payFromCard=(row)=>{currentRow.value=row;payAmount.value=row.totalAmount-(row.paidAmount||0);payVisible.value=true}
const doPay=async()=>{
  paying.value=true
  try{
    const r=await getMyCard()
    if((r.data?.balance||0)<payAmount.value){ElMessage.error('一卡通余额不足');return}
    ElMessage.success('缴费成功（模拟）')
    payVisible.value=false;load()
  }catch{ElMessage.error('缴费失败')}
  finally{paying.value=false}
}
onMounted(()=>{load();loadCard()})
</script>
<style scoped>
.page{max-width:1000px}.page-header{margin-bottom:20px}.page-header h2{margin:0;font-size:20px}
</style>
