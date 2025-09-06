<template>
  <el-tabs class="w-full" stretch model-value="componentTab">
    <!-- tab1组件属性 -->
    <el-tab-pane label="组件属性" name="componentTab">
      <div class="mx-10px">
        <el-form v-if="current?.__id" :model="current" label-width="auto" size="small">
          <!-- 组件类型 -->
          <el-form-item label="组件类型" prop="__key">
            <el-select v-model="current.__key" @change="componentChange" class="w-80%!">
              <el-option-group v-for="group in componentList" :key="group.name" :label="group.name">
                <el-option v-for="item in group.list" :key="item.__key" :label="item.__name" :value="item.__key">
                  <my-icon v-if="item.__icon" :icon="item.__icon"/>
                  {{ item.__name }}
                </el-option>
              </el-option-group>
            </el-select>
            <a :href="current.__docLink || 'https://element-plus.org/zh-CN/'" target="_blank"
               class="flex justify-center items-center w-20%!">
              <base-button type="primary" link icon="el-icon-link" size="default"/>
            </a>
          </el-form-item>
          <!-- input-文本类型 -->
          <el-form-item v-if="'el-input'===current.__key" label="文本类型">
            <el-select v-model="current.__attrs.type" @change="typeChange">
              <el-option label="单行文本框" value="text"/>
              <el-option label="多行文本框" value="textarea"/>
              <el-option label="密码框" value="password"/>
            </el-select>
          </el-form-item>

          <el-divider>通用属性</el-divider>
          <el-form-item label="字段名" required>
            <el-input v-model="current.__modelName" placeholder="字段名(建议驼峰格式)"/>
          </el-form-item>
          <el-form-item label="Label" required>
            <el-input v-model="current.__formItemAttrs.label"/>
          </el-form-item>
          <el-form-item v-if="formProps.__layout.layout" label="栅格">
            <el-slider v-model="current.__span" :min="0" :max="24" :marks="{12:''}"/>
          </el-form-item>
          <el-form-item v-if="current?.__attrs?.placeholder" label="占位提示">
            <el-input v-model="current.__attrs.placeholder"/>
          </el-form-item>

          <el-divider>更多属性</el-divider>
          <!-- input属性 -->
          <template v-if="'el-input'===current.__key">
            <el-form-item label="MaxLength">
              <el-input-number v-model="current.__attrs.maxlength"/>
            </el-form-item>
            <el-form-item label="清除按钮" v-if="'textarea'!==current.__attrs.type">
              <el-switch v-model="current.__attrs.clearable" active-text="显示" inactive-text="隐藏"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <el-form-item label="统计字数" v-if="'password'!==current.__attrs.type">
              <el-switch v-model="current.__attrs.showWordLimit" active-text="显示" inactive-text="隐藏"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <el-form-item label="查看密码图标" v-if="'password'===current.__attrs.type">
              <el-switch v-model="current.__attrs.showPassword" active-text="显示" inactive-text="隐藏"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <el-form-item label="行数" v-if="'textarea'===current.__attrs.type">
              <el-input-number v-model="current.__attrs.rows" placeholder="默认：2"/>
            </el-form-item>
            <el-form-item label="高度自适应" v-if="'textarea'===current.__attrs.type">
              <el-radio-group v-model="current.autosizeType">
                <el-radio-button label="不配置" value="noset"/>
                <el-radio-button label="boolean" value="boolean"/>
                <el-radio-button label="object" value="object"/>
              </el-radio-group>
              <el-switch v-if="current.autosizeType==='boolean'" active-text="是" inactive-text="否"
                         :active-value="true" :inactive-value="false" class="mt-5px"/>
              <div v-if="current.autosizeType==='object'" class="flex items-center mt-10px">
                <span class="text-12px text-#777 mr-2px">minRows</span>
                <el-input-number v-model="current.__attrs.autosize.minRows" :controls="false" class="w-40px!"/>
                <span class="text-12px text-#777 ml-7px mr-2px">- maxRows</span>
                <el-input-number v-model="current.__attrs.autosize.maxRows" :controls="false" class="w-40px!"/>
              </div>
            </el-form-item>
          </template>
          <!-- input-number属性 -->
          <template v-if="'el-input-number'===current.__key">
            <el-form-item label="最小值">
              <el-input-number v-model="current.__attrs.min"/>
            </el-form-item>
            <el-form-item label="最大值">
              <el-input-number v-model="current.__attrs.max"/>
            </el-form-item>
            <el-form-item label="步长step">
              <el-input-number v-model="current.__attrs.step"/>
            </el-form-item>
            <el-form-item label="严格模式">
              <el-switch v-model="current.__attrs.stepStrictly" active-text="是" inactive-text="否"
                         :active-value="true" :inactive-value="false"/>
              <el-tag :type="current.__attrs.stepStrictly?'success':'info'" class="ml-10px">只输入step的倍数</el-tag>
            </el-form-item>
            <el-form-item label="精度">
              <el-input-number v-model="current.__attrs.precision"/>
            </el-form-item>
            <el-form-item label="控制按钮">
              <el-switch v-model="current.__attrs.controls" active-text="显示" inactive-text="隐藏"
                         :active-value="true" :inactive-value="false"/>
            </el-form-item>
            <el-form-item label="控钮位置" v-if="current.__attrs.controls">
              <el-switch v-model="current.__attrs.controlsPosition" active-text="右侧" inactive-text="默认"
                         :active-value="'right'" :inactive-value="''"/>
            </el-form-item>
            <el-form-item label="文本对齐">
              <el-radio-group v-model="current.__attrs.align">
                <el-radio-button label="Left" value="left"/>
                <el-radio-button label="Center" value="center"/>
                <el-radio-button label="Right" value="right"/>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="科学计数">
              <el-switch v-model="current.__attrs.disabledScientific" active-text="禁用" inactive-text="启用"
                         :active-value="true" :inactive-value="false" class="mr-10px"/>
              <el-tag v-if="current.__attrs.disabledScientific" type="info" disable-transitions>不可输入'e'</el-tag>
              <el-tag v-else type="success" disable-transitions>可输入'e'</el-tag>
            </el-form-item>
          </template>
<!--
model-value / v-model	绑定值array—
max	可添加标签的最大数量number—
tag-type	标签类型	enum info
tag-effect	标签效果	enum light
trigger	触发输入标签的按键	enum Enter
draggable	是否可以拖动标签	boolean false
delimiter 2.9.9	在匹配分隔符时添加标签 string  /regex—
size	输入框尺寸	enum—
collapse-tags 2.11.0	多选时是否将选中值按文字的形式展示boolean false
collapse-tags-tooltip 2.11.0	当鼠标悬停于折叠标签的文本时，是否显示所有选中的标签。 要使用此功能，collapse-tags的值必须为true booleanfalse
save-on-blur 2.9.7	当输入失去焦点时是否保存输入值 boolean true
clearable	是否显示清除按钮 boolean false
clear-icon 2.11.0	自定义清除图标 string  / object CircleClose
disabled	是否禁用 boolean false
validate-event	是否触发表单验证 boolean true
readonly	等价于原生 readonly 属性 boolean false
autofocus	等价于原生  autofocus  属性 boolean false
id	等价于原生 input id 属性 string —
tabindex	等价于原生  tabindex  属性 string  / number —
max-collapse-tags 2.11.0	需要显示的 Tag 的最大数量 要使用此功能，collapse-tags的值必须为true  number 1
maxlength	等价于原生  maxlength  属性 string  / number —
minlength	等价于原生  minlength  属性string  / number—
placeholder	输入框占位文本string—
autocomplete	等价于原生  autocomplete  属性stringoff
aria-label a11y	等价于原生  aria-label  属性string
-->



          <el-divider>test</el-divider>
          <base-button type="primary" @click="()=>{console.log(JSON.stringify(current.__attrs))}">测试属性</base-button>


          <el-divider>表单规则</el-divider>
          <form-item-rules v-if="current?.__id" :key="current.__id" v-model="currentRules"/>

        </el-form>
        <!-- 未选择组件时显示 -->
        <div v-else class="text-center text-14px color-gray mt-10%">
          请选择中间区域组件，配置组件属性
        </div>
      </div>
    </el-tab-pane>

    <!-- tab2表单属性 -->
    <el-tab-pane label="表单属性" name="formTab">
      <div class="mx-10px">
        <el-form :model="formProps" label-width="auto" size="small">
          <el-divider>基础属性</el-divider>
          <el-form-item label="标签对齐">
            <el-radio-group v-model="formProps.__attrs.labelPosition">
              <el-radio-button label="左对齐" value="left"/>
              <el-radio-button label="右对齐" value="right"/>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="标签宽度">
            <el-input v-model="formProps.__attrs.labelWidth" placeholder="示例:auto、100px、10%"/>
          </el-form-item>
          <el-form-item label="标签后缀">
            <el-input v-model="formProps.__attrs.labelSuffix" placeholder="标签后缀"/>
          </el-form-item>
          <el-form-item label="必填项星号">
            <el-radio-group v-model="formProps.__attrs.hideRequiredAsterisk">
              <el-radio-button label="显示" :value="false"/>
              <el-radio-button label="隐藏" :value="true"/>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="必填星号位置">
            <el-radio-group v-model="formProps.__attrs.requireAsteriskPosition">
              <el-radio-button label="左侧" value="left"/>
              <el-radio-button label="右侧" value="right"/>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="尺寸">
            <el-radio-group v-model="formProps.__attrs.size">
              <el-radio-button label="Large" value="large"/>
              <el-radio-button label="Default" value="default"/>
              <el-radio-button label="Small" value="small"/>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="表单状态">
            <el-radio-group v-model="formProps.__attrs.disabled">
              <el-radio-button label="启用" :value="false"/>
              <el-radio-button label="禁用" :value="true"/>
            </el-radio-group>
          </el-form-item>
          <el-divider>校验相关属性</el-divider>
          <el-form-item label="校验错误提示">
            <el-radio-group v-model="formProps.__attrs.showMessage">
              <el-radio-button label="显示" :value="true"/>
              <el-radio-button label="隐藏" :value="false"/>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="校验错误位置">
            <el-radio-group v-model="formProps.__attrs.inlineMessage">
              <el-radio-button label="换行显示" :value="false"/>
              <el-radio-button label="行内显示" :value="true"/>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="校验反馈图标">
            <el-radio-group v-model="formProps.__attrs.statusIcon">
              <el-radio-button label="显示" :value="true"/>
              <el-radio-button label="隐藏" :value="false"/>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="定位校验错误">
            <el-radio-group v-model="formProps.__attrs.scrollToError">
              <el-radio-button label="自动聚焦" :value="true"/>
              <el-radio-button label="不聚焦" :value="false"/>
            </el-radio-group>
            <div class="w-full color-[var(--el-color-danger)] text-12px">
              {{ formProps.__attrs.scrollToError ? '校验失败时,滚动到第一个错误表单' : '校验失败时,不滚动' }}
            </div>
          </el-form-item>
          <el-divider>布局属性</el-divider>
          <el-form-item label="栅格布局">
            <el-radio-group v-model="formProps.__layout.layout">
              <el-radio-button label="普通布局" :value="false"/>
              <el-radio-button label="栅格布局" :value="true"/>
            </el-radio-group>
          </el-form-item>
          <template v-if="formProps.__layout.layout">
            <el-form-item label="栅格间隔">
              <el-input-number v-model="formProps.__layout.gutter"/>
            </el-form-item>
            <el-form-item label="水平排列方式">
              <el-select v-model="formProps.__layout.justify">
                <el-option label="Start" value="start"/>
                <el-option label="End" value="end"/>
                <el-option label="Center" value="center"/>
                <el-option label="SpaceAround" value="space-around"/>
                <el-option label="SpaceBetween" value="space-between"/>
                <el-option label="SpaceEvenly" value="space-evenly"/>
              </el-select>
            </el-form-item>
            <el-form-item label="垂直排列方式">
              <el-select v-model="formProps.__layout.align" clearable>
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
import { cloneDeep } from 'lodash-es'
import allConfig from '@/views/generator/panel/config/allConfig'
import FormItemRules from '@/views/generator/panel/FormItemRules'
import { BaseButton } from '@/components/BaseButton'
import { MyIcon } from '@/components/MyIcon'

// 全部组件类型
const componentList = cloneDeep(allConfig)

// 绑定组件属性
const current = defineModel('current', {type: Object, default: () => {}})
// 绑定表单属性
const formProps = defineModel('formProps', {type: Object, default: () => {}})

// 表单验证规则（切换当前组件时，自动更新）
const currentRules = ref([])
watch(() => current.value.__id, () => { currentRules.value = current.value?.__formItemAttrs?.rules || [] })

// 切换组件类型
const componentChange = (val) => {
  current.value.__attrs = {...componentList.flatMap(o => o.list).find(i => i.__key === val).__attrs}
}
// input组件，文本类型变化
const typeChange = (val) => {
  if (val === 'textarea') delete current.value.__attrs.clearable
  if (val !== 'textarea') {
    delete current.value.__attrs.rows
    delete current.value.__attrs.autosize
  }
  if (val === 'password') delete current.value.__attrs.showWordLimit
  if (val !== 'password') delete current.value.__attrs.showPassword
}
// textarea组件，高度自适应变化
watch(() => current.value.autosizeType, (val) => {
  if (val === 'boolean') current.value.__attrs.autosize = true
  else if (val === 'object') current.value.__attrs.autosize = {minRows: 2, maxRows: 5}
  else delete current.value.__attrs.autosize
})
// input-number监听控制按钮显示/隐藏
watch(() => current.value.__attrs?.controls, (val) => {
  current.value.__attrs.controlsPosition = ''
  if (!val) delete current.value.__attrs.controlsPosition
})
</script>

<style lang="less" scoped>

</style>