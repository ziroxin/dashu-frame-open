<template>
  <div class="w-full h-full">
    <div class="flex justify-between items-center b-b-2px b-b-solid b-b-[var(--el-border-color-light)] px-10px">
      <el-tabs v-model="currentTab" stretch class="w-320px h-38px!">
        <el-tab-pane name="json" label="JSON"/>
        <el-tab-pane name="html" label="HTML"/>
        <el-tab-pane name="typescript" label="TypeScript"/>
      </el-tabs>
      <div class="flex items-center">
        <span class="text-12px color-gray mr-5px">主题：</span>
        <el-select v-model="theme" class="w-120px!" placeholder="请选择主题样式" size="small">
          <el-option v-for="item in ['vs','vs-dark','hc-black','hc-light']"
                     :key="item" :label="item" :value="item"/>
        </el-select>
        <el-divider direction="vertical"/>
        <base-button type="primary" link :icon="readonly?'el-icon-unlock':'el-icon-lock'" @click="readonly=!readonly">
          {{ readonly ? '允许编辑' : '切换只读' }}
        </base-button>
        <el-divider direction="vertical"/>
        <base-button type="primary" link icon="el-icon-refresh" @click="formatCode">格式化代码</base-button>
        <el-divider direction="vertical"/>
        <base-button icon="el-icon-close" link type="primary" class="mr-10px"
                     @click="closeCodeDialog">关闭
        </base-button>
      </div>
    </div>
    <code-editor :key="'ce-'+currentTab" class="mt-10px h-full" :content="getCode(currentTab)"
                 :theme="theme" :language="currentTab" :readonly="readonly"/>
  </div>
</template>

<script setup lang="ts">
import { cloneDeep } from 'lodash-es'
import { CodeEditor } from '@/components/CodeEditor'
import { objToStr } from '@/utils'
import { formatCode } from '@/components/CodeEditor/src/helper'

// 当前Tab
const currentTab = ref('html')
// 主题切换
const theme = ref('vs')
// 只读切换
const readonly = ref(true)
// 接收参数
const formItemList = defineModel({type: Array<any>, default: () => []})
const formProps = defineModel('formProps', {type: Object, default: () => {}})
// 关闭代码弹窗
const codeDialogVisible = defineModel('codeDialogVisible', {type: Boolean})
const closeCodeDialog = () => {codeDialogVisible.value = false}

// 计算
const getCode = (tab) => {
  if (tab === 'json') return getJsonCode()
  if (tab === 'html') return getHtmlCode()
  if (tab === 'typescript') return getTsCode()
  return ''
}
const getJsonCode = () => JSON.stringify({formProps: formProps.value, formItemList: formItemList.value})
const getHtmlCode = () => {
  const fl = formProps.value.__layout
  // 1. 遍历处理el-form-item
  const itemHtmlArr: string[] = []
  formItemList.value.forEach((r: any) => {
    const item = cloneDeep(r)
    // 1.1处理表单及属性
    // 1.1.1 数据
    let propsStr = ''
    let optionsStr = ''
    if (['el-select', 'el-cascader', 'el-radio-group', 'el-checkbox-group'].includes(item.__key)) {
      if (['dynamic', 'dict'].includes(item.dataType)) {
        // 刷新动态数据或字典数据
        propsStr = `:props="${item.__modelName}Props"`
        optionsStr = `:options="${item.__modelName}Options"`
        delete item.__attrs.props
        delete item.__attrs.options
      }
    }
    // 1.1.2 其他属性
    const attrsStr = Object.keys(item.__attrs).map(key => {
      const val = item.__attrs[key]
      if (!['boolean', 'number', 'object', 'string'].includes(typeof val)) console.log('意外的数据类型：', typeof val)
      if (val !== undefined && val !== null && val !== '') {
        if (['number', 'object'].includes(typeof val)) return `:${key}="${objToStr(val)}"`
        if ('boolean' === typeof val) return val ? `${key}` : `:${key}="false"`
        return `${key}="${val}"`
      }
      return null
    }).filter(attr => attr !== null).join(' ')
    // 1.1.3 wangEditor特殊属性
    const wangEditorDisable = item.__key === 'my-wang-editor' ? ` :disabled="dialogType==='view'"` : ''
    // 1.1.4 单独处理 el-radio和el-checkbox
    let itemHtml = `<${item.__key} v-model="formData.${item.__modelName}" ${attrsStr} ${wangEditorDisable} ${propsStr} ${optionsStr}/>`
    if ('el-radio' === item.__key) {
      const rData = ['dynamic', 'dict'].includes(item.dataType) ? `${item.__modelName}Options` : objToStr(item.radioData)
      const tagHtml = item.radioType === 'button' ? `el-radio-button` : `el-radio`
      itemHtml = `<el-radio-group v-model="formData.${item.__modelName}">
            <${tagHtml} v-for="(r, i) in ${rData}" ${attrsStr}
                    :key="i" :label="r.${item.labelKey}" :value="r.${item.valueKey}"/>
        </el-radio-group>`
    } else if ('el-checkbox' === item.__key) {
      const cData = ['dynamic', 'dict'].includes(item.dataType) ? `${item.__modelName}Options` : objToStr(item.checkboxData)
      const tagHtml = item.checkboxType === 'button' ? `el-checkbox-button` : `el-checkbox`
      itemHtml = `<el-checkbox-group v-model="formData.${item.__modelName}">
            <${tagHtml} v-for="(r, i) in ${cData}" ${attrsStr}
                    :key="i" :label="r.${item.labelKey}" :value="r.${item.valueKey}"/>
        </el-checkbox-group>`
    }
    // 1.1.5 组装el-form-item
    const innerHtml = `<el-form-item label="${item.__formItemAttrs.label}" prop="${item.__modelName}"
                      :rules="${getRules(item.__formItemAttrs)}">
          ${itemHtml}
        </el-form-item>`

    // 1.2处理栅格布局el-col
    if (fl.layout) {
      itemHtmlArr.push(`<el-col :span="${item?.__span || 24}">
          ${innerHtml}
        </el-col>`)
    } else {
      itemHtmlArr.push(innerHtml)
    }
  })

  // 2.处理栅格布局el-row
  let rowHtml = ``
  if (fl.layout) {
    let rowAttrs = ``
    if (fl.gutter && fl.gutter !== 0) rowAttrs += ` :gutter="${fl.gutter}"`
    if (fl.justify && fl.justify !== 'start') rowAttrs += ` justify="${fl.justify}"`
    if (fl.align) rowAttrs += ` align="${fl.align}"`
    rowHtml = `<el-row ${rowAttrs}>
        ${itemHtmlArr.join('\n')}
      </el-row>`
  } else {
    rowHtml = itemHtmlArr.join('\n')
  }

  // 3. 处理el-form
  const f = formProps.value.__attrs
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

  // 4. 组装el-form
  return `<el-form ref="dataFormRef" :model="formData" ${formAttrs} :disabled="dialogType==='view'">
      ${rowHtml}
    </el-form>`
}

const getTsCode = () => {
  const list = formItemList.value
  // 1表单数据默认值
  const formDataDefault = list.filter(o => o.__key === 'el-input-number').map(item => `${item.__modelName}:0`).join(', ')
  // 2自定义组件导入
  const wangEditorImport = list.some(o => o.__key === 'my-wang-editor') ? `import { MyWangEditor } from '@/components/MyWangEditor'` : ''
  // 3数据
  let requestImport = ''
  let dictImport = ''
  let onMountedStr = ''
  let dataStr = ''
  const arr1 = ['el-select', 'el-cascader', 'el-radio', 'el-radio-group', 'el-checkbox', 'el-checkbox-group']
  list.filter(o => arr1.includes(o.__key)).forEach((r: any) => {
    const item = cloneDeep(r)
    if ('dynamic' === item.dataType) {
      requestImport = `import request from '@/utils/request'`
      onMountedStr += `
                      load${item.__modelName}Data()`
      dataStr += `
          // ${item.__modelName}数据
          const ${item.__modelName}Props = ref(${objToStr(item.__attrs.props || {})})
          const ${item.__modelName}Options = ref([])
          const load${item.__modelName}Data = () => {
            request({url: '${item.dataDynamic.url}', method: 'get'}).then((response) => {
              ${item.__modelName}Options.value = response.${item.dataDynamic.dataKey} || []
            })
          }`
    } else if ('dict' === item.dataType) {
      dictImport = `import { getDict } from '@/utils/dict-util'`
      onMountedStr += `
                      load${item.__modelName}Data()`
      dataStr += `
          // ${item.__modelName}数据
          const ${item.__modelName}Props = ref(${objToStr(item.__attrs.props || {})})
          const ${item.__modelName}Options = ref([])
          const load${item.__modelName}Data = () => {
            ${item.__modelName}Options.value = getDict('${item.dictCode}')
          }`
    }
  })

  // 4返回ts代码
  return `${wangEditorImport}
    ${requestImport}
    ${dictImport}

    // 生命周期页面加载数据
    onMounted(() => {
      ${onMountedStr}
    })

    // 表单
    const dataFormRef = ref()
    const formData = ref({${formDataDefault}})

    ${dataStr}
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