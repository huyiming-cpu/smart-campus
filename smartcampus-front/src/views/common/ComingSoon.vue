<template>
  <div class="coming-soon">
    <h2>🚧 功能开发中</h2>
    <p>该模块即将上线，敬请期待</p>
    <el-button type="primary" @click="$router.back()">返回</el-button>
  </div>
</template>

<style scoped>
.coming-soon { text-align: center; padding: 80px 0; color: #909399; }
.coming-soon h2 { font-size: 28px; margin-bottom: 12px; }
</style>
