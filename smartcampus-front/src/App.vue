<template>
  <router-view />
  <FloatingAi v-if="showAi" />
</template>
<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import FloatingAi from '@/components/FloatingAi.vue'
const route = useRoute()
const showAi = computed(() => route.path !== '/login')
</script>