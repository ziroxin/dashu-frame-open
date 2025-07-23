<template>
  <el-form class="text-center">
    <el-form-item label="数据字典示例">
      <el-select v-model="sex">
        <el-option v-for="item in getDict('sys_user_sex')" :key="item.value"
                   :value="item.value" :label="item.label"/>
      </el-select>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { getDict } from '@/utils/dict-util'

const sex = ref('2')
// 输出测试
console.log(getDict('sys_user_sex'))
</script>
