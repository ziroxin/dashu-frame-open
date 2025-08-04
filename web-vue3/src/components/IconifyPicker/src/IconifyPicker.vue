<template>
  <div :class="prefixCls" class="flex flex-wrap justify-center items-center">
    <!-- 图标选择器 -->
    <el-tabs v-model="currentTabName" @tab-change="filterItemIcons" class="w-full">
      <!-- 循环标签页（element-plus、ant-design、tdesign） -->
      <el-tab-pane v-for="item in iconAllList" :key="item.name" :name="item.prefix" :label="item.name">
        <!-- 搜索框 -->
        <div class="pb-15px my-10px w-full b-b-1px b-b-dashed b-b-#ccc flex justify-around items-center">
          <div>
            <el-input v-model="searchTxt" class="w-300px!" clearable placeholder="输入图标名称模糊搜索"/>
            <base-button class="ml-10px" icon="el-icon-search" type="primary" @click="filterItemIcons">查询
            </base-button>
          </div>
          <div class="flex justify-center items-center" v-if="currentIcon">
            <span class="mr-10px">当前选中图标：</span>
            <el-input v-model="currentIcon" clearable class="w-300px! mr-10px"/>
            <my-icon :icon="currentIcon" :size="30" class="mr-20px"/>
            <base-button icon="el-icon-copy-document" type="danger" v-clipboard:copy="currentIcon"
                         v-clipboard:success="()=>{$message.success('复制成功！图标：'+currentIcon)}">复制
            </base-button>
          </div>
        </div>
        <!-- 图标列表 -->
        <el-scrollbar class="h-[calc(90vh-200px)]!" v-loading="isLoading">
          <div class="flex flex-wrap box-border min-h-200px">
            <template v-if="currentIconList && currentIconList.length > 0">
              <div v-for="icon in currentIconList" :key="icon" @click="()=>{currentIcon=icon}"
                   class="w-10 h-10 flex justify-center items-center cursor-pointer m-2 p-2 hover:b-[var(--el-color-primary)]! hover:text-[var(--el-color-primary)]!"
                   :style="{border: `1px solid var(--el-border-color)`, borderRadius: '4px'}">
                <my-icon :icon="icon" :size="24"/>
              </div>
            </template>
            <el-empty v-else class="w-full h-full text-center"/>
          </div>
        </el-scrollbar>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup lang="ts">
import epIcons from './data/icons.element-plus'
import antIcons from './data/icons.ant-design'
import tIcons from './data/icons.tdesign'
import { useDesign } from '@/hooks/web/useDesign'
import { BaseButton } from '@/components/BaseButton'

const prefixCls = useDesign().getPrefixCls('icon-picker')
// 扫描全部图标（element-plus、ant-design、tdesign）
const iconAllList = [epIcons, antIcons, tIcons]
// 定义当前标签页
const currentTabName = ref(iconAllList[0].prefix)
// 定义当前图标列表
const currentIconList = ref([])
const currentIcon = ref('')
const isLoading = ref(false)

// 查询图标
const searchTxt = ref('')
// 过滤后的图标列表
const filterItemIcons = async () => {
  isLoading.value = true
  setTimeout(() => {
    currentIconList.value = iconAllList.find((o) => o.prefix === unref(currentTabName))?.icons
        .filter((o) => o.includes(unref(searchTxt))) || []
    isLoading.value = false
  }, 100)
}
filterItemIcons()
</script>

<style lang="less" scoped>
@prefix-cls: ~'@{adminNamespace}-icon-picker';

.@{prefix-cls} {
  :deep(.@{elNamespace}-input__wrapper) {
    border-top-right-radius: 0;
    border-bottom-right-radius: 0;
  }
}
</style>
