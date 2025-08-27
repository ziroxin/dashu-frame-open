<template>
  <div class="w-full h-full">
    <div class="flex justify-between items-center b-b-1px b-b-solid b-b-#ccc px-10px">
      <el-tabs v-model="currentTab" stretch class="w-320px h-39px!">
        <el-tab-pane name="json" label="JSON"/>
        <el-tab-pane name="html" label="HTML"/>
        <el-tab-pane name="ts" label="TypeScript"/>
      </el-tabs>
      <div class="flex items-center">
        <base-button icon="el-icon-view" link type="primary" class="mr-10px"
                     @click="codeView=false">返回预览模式
        </base-button>
        <el-divider direction="vertical"/>
        <span class="text-12px color-gray mr-5px">主题：</span>
        <el-select v-model="theme" class="w-120px!" placeholder="请选择主题样式" size="small">
          <el-option v-for="item in ['vs','vs-dark','hc-black','hc-light']"
                     :key="item" :label="item" :value="item"/>
        </el-select>
      </div>
    </div>
    <div class="mt-10px w-full h-[calc(100%-50px)] flex">
      <code-editor :key="'ce-'+currentTab" class="flex-1"
                   :theme="theme" :language="currentTab" :content="getCode(currentTab)"/>
    </div>
  </div>
</template>

<script setup lang="ts">
import { CodeEditor } from '@/components/CodeEditor'

// 当前Tab
const currentTab = ref('json')
// 主题切换
const theme = ref('vs')
// 接收参数
const formItemList = defineModel({type: Array, default: () => []})
const formProps = defineModel('formProps', {type: Object, default: () => {}})
const codeView = defineModel('codeView', {type: Boolean, default: true})

// 计算
const getCode = (tab) => {
  if (tab === 'json') return getJsonCode()
  if (tab === 'html') return getHtmlCode()
  if (tab === 'ts') return getTsCode()
  return ''
}
const getJsonCode = () => JSON.stringify({formProps: formProps.value, formItemList: formItemList.value})
const getHtmlCode = () => {
  const f = formProps.value
  // 1. 遍历处理el-form-item
  const itemHtmlArr: string[] = []
  formItemList.value.forEach((item: any) => {
    const attrsStr = Object.keys(item.__attrs).map(key => `${key}="${item.__attrs[key]}"`).join(' ')
    const innerHtml = `<el-form-item label="${item.__formItemAttrs.label}" prop="${item.__modelName}"
                      :rules="${getRules(item.__formItemAttrs)}">
          <${item.__key} v-model="formData.${item.__modelName}" ${attrsStr}/>
        </el-form-item>`
    if (f.layout) {
      // 处理栅格布局
      itemHtmlArr.push(`<el-col :span="${item?.__span || 24}">
          ${innerHtml}
        </el-col>`)
    } else {
      itemHtmlArr.push(innerHtml)
    }
  })

  // 2. 处理el-form
  let formAttrs = ``
  if (f.labelPosition && f.labelPosition !== 'right') formAttrs += ` label-position="${f.labelPosition}"`
  formAttrs += f.labelWidth && f.labelWidth !== 'auto' ? ` label-width="${f.labelWidth}"` : ` label-width="auto"`
  if (f.labelSuffix) formAttrs += ` label-suffix="${f.labelSuffix}"`
  if (f.hideRequiredAsterisk) formAttrs += ` hide-required-asterisk`
  if (f.requireAsteriskPosition && f.requireAsteriskPosition !== 'left') formAttrs += ` require-asterisk-position="${f.requireAsteriskPosition}"`
  if (!f.showMessage) formAttrs += ` :show-message="false"`
  if (f.inlineMessage) formAttrs += ` inline-message`
  if (f.statusIcon) formAttrs += ` status-icon`
  if (f.size && f.size !== 'default') formAttrs += ` size="${f.size}"`
  if (f.disabled) formAttrs += ` disabled`
  if (!f.scrollToError) formAttrs += ` :scroll-to-error="false"`
  // 3.是否栅格布局
  let rowHtml = ``
  if (f.layout) {
    let rowAttrs = ``
    if (f.gutter && f.gutter !== 0) rowAttrs += ` :gutter="${f.gutter}"`
    if (f.justify && f.justify !== 'start') rowAttrs += ` justify="${f.justify}"`
    if (f.align) rowAttrs += ` align="${f.align}"`
    rowHtml = `
      <el-row ${rowAttrs}>
        ${itemHtmlArr.join('\n')}
      </el-row>`
  } else {
    rowHtml = itemHtmlArr.join('\n')
  }
  return `<el-form ref="dataFormRef" :model="formData" ${formAttrs} :disabled="dialogType==='view'">
      ${rowHtml}
    </el-form>`
}
const getTsCode = () => {
  const formDataDefault = formItemList.value.filter(item => item.__key === 'el-input-number')
      .map(item => `${item.__modelName}:0`).join(', ')
  return `
    // 表单
    const dataFormRef = ref()
    const formData = ref({${formDataDefault})
  `
}

// 获取验证规则
const getRules = (attrs: any) => {
  if (attrs.rules && attrs.rules.length > 0) {
    const result: string[] = []
    attrs.rules.forEach(item => {
      const triggerStr = item.trigger ? `,trigger:'${item.trigger}'` : ''
      if (item.required === true) {
        result.push(`{required:true,message:'${item.message}'${triggerStr}}`)
      } else if (item.required === 'pattern' && item.pattern) {
        result.push(`{pattern:${item.pattern},message:'${item.message}'${triggerStr}}`)
      } else if (item.required === 'validator' && item.validator) {
        result.push(`{validator:${item.validator}${triggerStr}}`)
      }
    })
    return `[${result.join(', ')}]`
  }
  return '[]'
}
</script>

<style lang="less" scoped>

</style>