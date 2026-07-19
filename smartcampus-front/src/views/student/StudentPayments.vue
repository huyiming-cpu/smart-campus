<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#5B9BD5,#4A8AD4);"><el-icon :size="20" color="#fff"><Wallet /></el-icon></div>
      <div><h2 class="page-title">学费缴费</h2><p class="page-desc">查看缴费明细，通过一卡通在线缴费</p></div>
    </div>
    <div class="card" v-loading="loading">
      <el-table :data="payments" stripe empty-text="暂无缴费记录" show-summary>
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
    </div>

    <el-dialog v-model="payVisible" title="一卡通缴费" width="400px">
      <p>将从一卡通余额中扣除 <strong>&yen;{{ payAmount }}</strong></p>
      <p style="color:#909399">当前余额：&yen;{{ cardBalance?.toFixed(2)||'0.00' }}</p>
      <p v-if="payAmount > (cardBalance||0)" style="color:#f56c6c">余额不足，请先充值</p>
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
import { Wallet } from '@element-plus/icons-vue'
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
.page{padding:20px 24px;max-width:1000px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:14px;margin-bottom:20px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;box-shadow:0 4px 12px rgba(91,155,213,.25)}
.page-title{font-size:20px;font-weight:700;color:#1A1A2E;margin:0}
.page-desc{font-size:13px;color:#9CA3AF;margin:2px 0 0}
.card{background:#fff;border:1px solid #EEF0F4;border-radius:14px;padding:20px;box-shadow:0 2px 12px rgba(0,0,0,.02)}
</style>
