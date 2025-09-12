<template>
  <div class="app-container flex flex-col justify-center items-center">
    <div class="color-gray-4 h-50px">Vue3 不再使用此功能！</div>
    <base-button type="primary" icon="el-icon-edit" @click="toEditor">点击打开《表单在线编辑》</base-button>
  </div>
</template>
<script setup lang="ts">
const {push} = useRouter()
const toEditor = () => { push('/generator/form') }
</script>