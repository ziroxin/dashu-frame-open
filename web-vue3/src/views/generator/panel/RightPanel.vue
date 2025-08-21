<template>
  <el-tabs class="w-full" stretch model-value="componentTab">
    <el-tab-pane label="组件属性" name="componentTab">
      <div class="mx-10px">
        <el-form v-if="props?.__key" :model="props" label-width="auto">
          <el-form-item label="组件类型" prop="__key">
            <el-select v-model="props.__key">
              <el-option-group v-for="group in componentList" :key="group.name" :label="group.name">
                <el-option v-for="item in group.list" :key="item.__key" :label="item.__name" :value="item.__key">
                  <my-icon v-if="item.__icon" :icon="item.__icon"/>
                  {{ item.__name }}
                </el-option>
              </el-option-group>
            </el-select>
          </el-form-item>
          <el-divider>基础属性</el-divider>
          <el-form-item v-if="'el-input'===props.__key" label="类型" required>
            <el-select v-model="props.__attrs.type">
              <el-option label="单行文本框" value="text"/>
              <el-option label="多行文本框" value="textarea"/>
              <el-option label="密码框" value="password"/>
            </el-select>
          </el-form-item>
          <el-form-item v-if="props?.__attrs?.placeholder" label="占位提示">
            <el-input v-model="props.__attrs.placeholder"/>
          </el-form-item>
        </el-form>
        <div v-else class="text-center text-14px color-gray mt-10%">
          请选择中间区域组件，配置组件属性
        </div>
      </div>
    </el-tab-pane>
    <el-tab-pane label="表单属性" name="formTab">
      <div class="mx-10px">
        表单属性
      </div>
    </el-tab-pane>
  </el-tabs>
</template>

<script setup lang="ts">
import inputConfig from '@/views/generator/panel/config/inputConfig'
import selectConfig from '@/views/generator/panel/config/selectConfig'
import diyConfig from '@/views/generator/panel/config/diyConfig'
import otherConfig from '@/views/generator/panel/config/otherConfig'
import { MyIcon } from '@/components/MyIcon'
// 组件配置
const componentList = ref([
  {name: '原生组件', icon: 'el-icon-edit', list: inputConfig},
  {name: '选择组件', icon: 'el-icon-news', list: selectConfig},
  {name: '自定义组件', icon: 'el-icon-menu', list: diyConfig},
  {name: '其他组件', icon: 'el-icon-set-up', list: otherConfig}
])
// 组件属性
const props = defineModel()
</script>

<style lang="less" scoped>

</style>