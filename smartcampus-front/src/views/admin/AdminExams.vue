<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#0EA5E9,#0284C7);"><el-icon :size="20" color="#fff"><EditPen /></el-icon></div>
      <div><h2 class="page-title">考试管理</h2><p class="page-desc">管理考试安排与等级考试，支持新建、编辑与状态更新</p></div>
    </div>
    <div class="toolbar">
      <el-tabs v-model="tab">
        <el-tab-pane label="考试安排" name="exams"/>
        <el-tab-pane label="等级考试" name="level"/>
      </el-tabs>
    </div>
    <div class="card" v-if="tab==='exams'" v-loading="eLoad">
      <el-button type="primary" size="small" @click="openExam()" style="margin-bottom:12px">新增考试</el-button>
      <el-table :data="exams" stripe border size="small">
        <el-table-column prop="examName" label="考试名称" width="140"/>
        <el-table-column prop="courseName" label="课程" width="140"/>
        <el-table-column prop="semester" label="学期" width="110"/>
        <el-table-column prop="examDate" label="日期" width="110"/>
        <el-table-column label="时间" width="130"><template #default="{row}">{{ row.examTimeStart }}-{{ row.examTimeEnd }}</template></el-table-column>
        <el-table-column prop="location" label="地点" width="120"/>
        <el-table-column prop="supervisorName" label="监考教师" width="90"/>
        <el-table-column prop="studentCount" label="人数" width="60"/>
        <el-table-column label="状态" width="80"><template #default="{row}"><el-tag :type="row.status==='PUBLISHED'?'success':'info'" size="small">{{ row.status }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="140"><template #default="{row}"><el-button size="small" type="primary" plain @click="openExam(row)">编辑</el-button><el-button size="small" type="danger" plain @click="delExam(row.id)">删除</el-button></template></el-table-column>
      </el-table>
    </div>
    <div class="card" v-if="tab==='level'" v-loading="lLoad">
      <el-button type="primary" size="small" @click="openLevel()" style="margin-bottom:12px"><el-icon :size="14" style="margin-right:4px"><Plus /></el-icon>新增等级考试</el-button>
      <el-table :data="levels" stripe border>
        <el-table-column prop="examName" label="考试名称" min-width="180" />
        <el-table-column prop="examCode" label="代码" width="110" />
        <el-table-column prop="category" label="类别" width="90">
          <template #default="{row}"><el-tag size="small" type="info">{{ row.category || '—' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="level" label="等级" width="90">
          <template #default="{row}"><el-tag size="small" :type="row.level==='高级'?'danger':row.level==='中级'?'warning':'info'">{{ row.level || '—' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="examDate" label="考试日期" width="120" />
        <el-table-column label="费用" width="100"><template #default="{row}"><span class="fee">&yen;{{ row.fee }}</span></template></el-table-column>
        <el-table-column label="报名人数" width="100"><template #default="{row}">{{ row.registeredCount || 0 }}人</template></el-table-column>
        <el-table-column label="状态" width="110">
          <template #default="{row}">
            <el-tag :type="row.status==='REGISTERING'?'success':row.status==='CLOSED'?'warning':'info'" size="small">
              {{ row.status==='REGISTERING'?'报名中':row.status==='CLOSED'?'已截止':'已结束' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{row}">
            <el-button size="small" type="primary" plain @click="openLevel(row)">编辑</el-button>
            <el-button size="small" type="danger" plain @click="delLevel(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="eDv" :title="eEdit?'编辑考试':'新增考试'" width="550px">
      <el-form :model="ef" label-width="80px">
        <el-form-item label="名称"><el-input v-model="ef.examName"/></el-form-item>
        <el-form-item label="课程ID"><el-input-number v-model="ef.courseId"/></el-form-item>
        <el-form-item label="课程名"><el-input v-model="ef.courseName"/></el-form-item>
        <el-form-item label="学期"><el-input v-model="ef.semester"/></el-form-item>
        <el-form-item label="日期"><el-input v-model="ef.examDate" type="date"/></el-form-item>
        <el-form-item label="开始时间"><el-input v-model="ef.examTimeStart"/></el-form-item>
        <el-form-item label="结束时间"><el-input v-model="ef.examTimeEnd"/></el-form-item>
        <el-form-item label="地点"><el-input v-model="ef.location"/></el-form-item>
        <el-form-item label="监考教师ID"><el-input-number v-model="ef.supervisorId"/></el-form-item>
        <el-form-item label="监考教师名"><el-input v-model="ef.supervisorName"/></el-form-item>
        <el-form-item label="人数"><el-input-number v-model="ef.studentCount"/></el-form-item>
        <el-form-item label="状态"><el-select v-model="ef.status"><el-option label="已发布" value="PUBLISHED"/><el-option label="草稿" value="DRAFT"/></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="eDv=false">取消</el-button><el-button type="primary" @click="saveExam">保存</el-button></template>
    </el-dialog>

    <el-dialog v-model="lDv" :title="lEdit?'编辑等级考试':'新增等级考试'" width="520px">
      <el-form :model="lf" label-width="80px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="名称"><el-input v-model="lf.examName" placeholder="如：全国计算机等级考试"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="代码"><el-input v-model="lf.examCode" placeholder="如：NCRE"/></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="类别"><el-select v-model="lf.category" style="width:100%"><el-option label="计算机" value="计算机"/><el-option label="英语" value="英语"/><el-option label="职业资格" value="职业资格"/><el-option label="其他" value="其他"/></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="等级"><el-select v-model="lf.level" style="width:100%"><el-option label="初级" value="初级"/><el-option label="中级" value="中级"/><el-option label="高级" value="高级"/></el-select></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="日期"><el-input v-model="lf.examDate" type="date"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="费用(元)"><el-input-number v-model="lf.fee" :min="0" :step="10" style="width:100%"/></el-form-item></el-col>
        </el-row>
        <el-form-item label="状态"><el-select v-model="lf.status" style="width:100%"><el-option label="报名中" value="REGISTERING"/><el-option label="已截止" value="CLOSED"/><el-option label="已结束" value="FINISHED"/></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="lDv=false">取消</el-button><el-button type="primary" @click="saveLevel" :loading="lSaving">保存</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { EditPen, Plus } from '@element-plus/icons-vue'
const tab=ref('exams'),exams=ref([]),eLoad=ref(false),levels=ref([]),lLoad=ref(false)
const eDv=ref(false),eEdit=ref(false),eEid=ref(null),ef=ref({})
const lDv=ref(false),lEdit=ref(false),lEid=ref(null),lSaving=ref(false),lf=ref({})
const load=async()=>{
  eLoad.value=true;lLoad.value=true
  try{const r=await request.get('/api/student/exams',{params:{semester:'2025-2026-2'}});exams.value=r.data||[]}catch{}
  try{const r=await request.get('/api/student/level-exams');levels.value=r.data||[]}catch{}
  eLoad.value=false;lLoad.value=false
}
const openExam=(row)=>{if(row){eEdit.value=true;eEid.value=row.id;ef.value={...row}}else{eEdit.value=false;eEid.value=null;ef.value={examName:'',courseId:'',courseName:'',semester:'2025-2026-2',examDate:'',examTimeStart:'09:00',examTimeEnd:'11:00',location:'',supervisorId:'',supervisorName:'',studentCount:0,status:'PUBLISHED'}};eDv.value=true}
const saveExam=async()=>{try{if(eEdit.value)await request.put(`/api/admin/exams/${eEid.value}`,ef.value);else await request.post('/api/admin/exams',ef.value);ElMessage.success('OK');eDv.value=false;load()}catch{ElMessage.error('失败')}}
const delExam=async(id)=>{try{await request.delete(`/api/admin/exams/${id}`);ElMessage.success('已删除');load()}catch{}}
const openLevel=(row)=>{if(row){lEdit.value=true;lEid.value=row.id;lf.value={...row}}else{lEdit.value=false;lEid.value=null;lf.value={examName:'',examCode:'',category:'计算机',level:'初级',examDate:'',fee:0,status:'REGISTERING'}};lDv.value=true}
const saveLevel=async()=>{lSaving.value=true;try{if(lEdit.value)await request.put(`/api/admin/level-exams/${lEid.value}`,lf.value);else await request.post('/api/admin/level-exams',lf.value);ElMessage.success(lEdit.value?'修改成功':'新增成功');lDv.value=false;load()}catch{ElMessage.error('保存失败')}finally{lSaving.value=false}}
const delLevel=async(id)=>{try{await request.delete(`/api/admin/level-exams/${id}`);ElMessage.success('已删除');load()}catch{}}
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
.fee{font-weight:600;color:#F59E0B}
</style>
