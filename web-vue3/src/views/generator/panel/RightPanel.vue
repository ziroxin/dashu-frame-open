<template>
  <el-tabs class="w-full" stretch model-value="componentTab">
    <!-- 组件属性 -->
    <el-tab-pane label="组件属性" name="componentTab">
      <div class="mx-10px">
        <el-form v-if="current?.__id" :model="current" label-width="auto" size="small">
          <el-form-item label="组件类型" prop="__key">
            <el-select v-model="current.__key">
              <el-option-group v-for="group in componentList" :key="group.name" :label="group.name">
                <el-option v-for="item in group.list" :key="item.__key" :label="item.__name" :value="item.__key">
                  <my-icon v-if="item.__icon" :icon="item.__icon"/>
                  {{ item.__name }}
                </el-option>
              </el-option-group>
            </el-select>
          </el-form-item>
          <el-form-item v-if="'el-input'===current.__key" label="文本类型">
            <el-select v-model="current.__attrs.type">
              <el-option label="单行文本框" value="text"/>
              <el-option label="多行文本框" value="textarea"/>
              <el-option label="密码框" value="password"/>
            </el-select>
          </el-form-item>

          <el-divider>常用属性</el-divider>
          <el-form-item label="字段名" required>
            <el-input v-model="current.__modelName" placeholder="字段名(建议驼峰格式)"/>
          </el-form-item>
          <el-form-item label="Label" required>
            <el-input v-model="current.__formItemAttrs.label"/>
          </el-form-item>
          <el-form-item v-if="formProps.layout" label="栅格">
            <el-slider v-model="current.__span" :min="0" :max="24" :marks="{12:''}"/>
          </el-form-item>
          <el-form-item v-if="current?.__attrs?.placeholder" label="占位提示">
            <el-input v-model="current.__attrs.placeholder"/>
          </el-form-item>

          <el-divider>表单规则</el-divider>
          <form-item-rules v-if="current?.__id" :key="current.__id" v-model="currentRules"/>

        </el-form>
        <div v-else class="text-center text-14px color-gray mt-10%">
          请选择中间区域组件，配置组件属性
        </div>
      </div>
    </el-tab-pane>

    <!-- 表单属性 -->
    <el-tab-pane label="表单属性" name="formTab">
      <div class="mx-10px">
        <el-form :model="formProps" label-width="auto" size="small">
          <el-divider>基础属性</el-divider>
          <el-form-item label="标签对齐">
            <el-radio-group v-model="formProps.labelPosition">
              <el-radio-button label="左对齐" value="left"/>
              <el-radio-button label="右对齐" value="right"/>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="标签宽度">
            <el-input v-model="formProps.labelWidth" placeholder="示例:auto、100px、10%"/>
          </el-form-item>
          <el-form-item label="标签后缀">
            <el-input v-model="formProps.labelSuffix" placeholder="标签后缀"/>
          </el-form-item>
          <el-form-item label="必填项星号">
            <el-radio-group v-model="formProps.hideRequiredAsterisk">
              <el-radio-button label="显示" :value="false"/>
              <el-radio-button label="隐藏" :value="true"/>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="必填星号位置">
            <el-radio-group v-model="formProps.requireAsteriskPosition">
              <el-radio-button label="左侧" value="left"/>
              <el-radio-button label="右侧" value="right"/>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="尺寸">
            <el-radio-group v-model="formProps.size">
              <el-radio-button label="Large" value="large"/>
              <el-radio-button label="Default" value="default"/>
              <el-radio-button label="Small" value="small"/>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="表单状态">
            <el-radio-group v-model="formProps.disabled">
              <el-radio-button label="启用" :value="false"/>
              <el-radio-button label="禁用" :value="true"/>
            </el-radio-group>
          </el-form-item>
          <el-divider>校验相关属性</el-divider>
          <el-form-item label="校验错误提示">
            <el-radio-group v-model="formProps.showMessage">
              <el-radio-button label="显示" :value="true"/>
              <el-radio-button label="隐藏" :value="false"/>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="校验错误位置">
            <el-radio-group v-model="formProps.inlineMessage">
              <el-radio-button label="换行显示" :value="false"/>
              <el-radio-button label="行内显示" :value="true"/>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="校验反馈图标">
            <el-radio-group v-model="formProps.statusIcon">
              <el-radio-button label="显示" :value="true"/>
              <el-radio-button label="隐藏" :value="false"/>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="定位校验错误">
            <el-radio-group v-model="formProps.scrollToError">
              <el-radio-button label="自动聚焦" :value="true"/>
              <el-radio-button label="不聚焦" :value="false"/>
            </el-radio-group>
            <div class="w-full color-[var(--el-color-danger)] text-12px">
              {{ formProps.scrollToError ? '校验失败时,滚动到第一个错误表单' : '校验失败时,不滚动' }}
            </div>
          </el-form-item>
          <el-divider>布局属性</el-divider>
          <el-form-item label="栅格布局">
            <el-radio-group v-model="formProps.layout">
              <el-radio-button label="普通布局" :value="false"/>
              <el-radio-button label="栅格布局" :value="true"/>
            </el-radio-group>
          </el-form-item>
          <template v-if="formProps.layout">
            <el-form-item label="栅格间隔">
              <el-input-number v-model="formProps.gutter"/>
            </el-form-item>
            <el-form-item label="水平排列方式">
              <el-select v-model="formProps.justify">
                <el-option label="Start" value="start"/>
                <el-option label="End" value="end"/>
                <el-option label="Center" value="center"/>
                <el-option label="SpaceAround" value="space-around"/>
                <el-option label="SpaceBetween" value="space-between"/>
                <el-option label="SpaceEvenly" value="space-evenly"/>
              </el-select>
            </el-form-item>
            <el-form-item label="垂直排列方式">
              <el-select v-model="formProps.align" clearable>
                <el-option label="Top" value="top"/>
                <el-option label="Middle" value="middle"/>
                <el-option label="Bottom" value="bottom"/>
              </el-select>
            </el-form-item>
          </template>

        </el-form>
      </div>
    </el-tab-pane>
  </el-tabs>
</template>

<script setup lang="ts">
import FormItemRules from '@/views/generator/panel/FormItemRules.vue'
import inputConfig from '@/views/generator/panel/config/inputConfig'
import selectConfig from '@/views/generator/panel/config/selectConfig'
import diyConfig from '@/views/generator/panel/config/diyConfig'
import otherConfig from '@/views/generator/panel/config/otherConfig'
// 组件配置
const componentList = ref([
  {name: '原生组件', icon: 'el-icon-edit', list: inputConfig},
  {name: '选择组件', icon: 'el-icon-news', list: selectConfig},
  {name: '自定义组件', icon: 'el-icon-menu', list: diyConfig},
  {name: '其他组件', icon: 'el-icon-set-up', list: otherConfig}
])
// 组件属性
const current = defineModel('current', {type: Object, default: () => {}})
// 表单属性
const formProps = defineModel('formProps', {type: Object, default: () => {}})

// 监听组件变化时，更新表单验证规则
const currentRules = ref([])
watch(() => current.value.__id, () => {
  currentRules.value = current.value?.__formItemAttrs?.rules || []
})
</script>

<style lang="less" scoped>

</style>