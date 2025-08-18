<template>
  <el-tabs v-model="rightTabActive" class="w-full" stretch @tab-click="handleClick">
    <el-tab-pane label="组件属性" name="componentTab">
      <div class="mx-10px">
        组件属性
      </div>
    </el-tab-pane>
    <el-tab-pane label="表单属性" name="formTab">
      <div class="mx-10px">
        表单属性
      </div>
    </el-tab-pane>
    <el-tab-pane label="表配置" name="tableTab">
      <div class="mx-10px">
        表配置
      </div>
    </el-tab-pane>
  </el-tabs>
</template>

<script setup lang="ts">
const rightTabActive = ref('componentTab')
const handleClick = (tab, event) => {
  console.log(tab, event)
}
</script>

<style lang="less" scoped>

</style>