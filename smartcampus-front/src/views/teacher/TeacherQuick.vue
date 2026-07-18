<template>
  <div class="page">
    <h3>🚀 快速入口</h3>
    <el-row :gutter="16">
      <el-col :span="8" v-for="item in links" :key="item.path" style="margin-bottom:16px">
        <el-card shadow="hover" class="quick-card" @click="$router.push(item.path)">
          <div class="icon">{{ item.icon }}</div>
          <div class="title">{{ item.title }}</div>
          <div class="desc">{{ item.desc }}</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
<script setup>
import { useRouter } from 'vue-router'
const router = useRouter()
const links = [
  { icon:'📝', title:'成绩录入', desc:'录入学生成绩', path:'/teacher/scores' },
  { icon:'📅', title:'任课信息', desc:'查看本周课程安排', path:'/teacher/schedule' },
  { icon:'👥', title:'班级名单', desc:'查看授课班级学生', path:'/teacher/students' },
  { icon:'📋', title:'考试安排', desc:'查看监考安排', path:'/teacher/exams' },
  { icon:'🔬', title:'科研管理', desc:'管理科研项目', path:'/teacher/research' },
  { icon:'🏆', title:'奖助贷管理', desc:'审核学生申请', path:'/teacher/awards' },
]
</script>
<style scoped>
.page{padding:20px;max-width:900px}h3{margin-bottom:16px}
.quick-card{cursor:pointer;text-align:center;padding:24px;transition:transform .2s}.quick-card:hover{transform:translateY(-4px)}
.icon{font-size:40px;margin-bottom:8px}.title{font-size:16px;font-weight:bold}.desc{font-size:12px;color:#909399;margin-top:6px}
</style>
