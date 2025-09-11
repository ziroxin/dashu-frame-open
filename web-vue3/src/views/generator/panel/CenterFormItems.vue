<template>
  <el-form-item v-bind="{...item.__formItemAttrs}" :prop="item.__modelName"
                :rules="getRules(item.__formItemAttrs)">
    <!-- el-input -->
    <el-input v-if="'el-input'===item.__key" v-bind="{...itemAttrs}"
              v-model="formData[item.__modelName]"/>
    <!-- el-input-number -->
    <el-input-number v-if="'el-input-number'===item.__key" v-bind="{...itemAttrs}"
                     v-model="formData[item.__modelName]"/>
    <!-- el-input-tag -->
    <el-input-tag v-if="'el-input-tag'===item.__key" v-bind="{...itemAttrs}"
                  v-model="formData[item.__modelName]"/>
    <!-- el-radio-group -->
    <el-radio-group v-if="'el-radio-group'===item.__key" v-bind="{...itemAttrs}"
                    v-model="formData[item.__modelName]"/>
    <!-- el-radio -->
    <template v-if="'el-radio'===item.__key">
      <el-radio-group v-model="formData[item.__modelName]">
        <template v-if="item.radioType==='button'">
          <el-radio-button v-for="(r, i) in item.radioData" v-bind="{...itemAttrs}"
                           :key="i" :label="r[item.labelKey]" :value="r[item.valueKey]"/>
        </template>
        <template v-else>
          <el-radio v-for="(r, i) in item.radioData" v-bind="{...itemAttrs}"
                    :key="i" :label="r[item.labelKey]" :value="r[item.valueKey]"/>
        </template>
      </el-radio-group>
    </template>
    <!-- el-checkbox-group -->
    <el-checkbox-group v-if="'el-checkbox-group'===item.__key" v-bind="{...itemAttrs}"
                       v-model="formData[item.__modelName]"/>
    <!-- el-checkbox -->
    <template v-if="'el-checkbox'===item.__key">
      <el-checkbox-group v-model="formData[item.__modelName]">
        <template v-if="item.checkboxType==='button'">
          <el-checkbox-button v-for="(r, i) in item.checkboxData" v-bind="{...itemAttrs}"
                              :key="i" :label="r[item.labelKey]" :value="r[item.valueKey]"/>
        </template>
        <template v-else>
          <el-checkbox v-for="(r, i) in item.checkboxData" v-bind="{...itemAttrs}"
                       :key="i" :label="r[item.labelKey]" :value="r[item.valueKey]"/>
        </template>
      </el-checkbox-group>
    </template>
    <!-- el-select -->
    <el-select v-if="'el-select'===item.__key" v-bind="{...itemAttrs}"
               v-model="formData[item.__modelName]"/>
    <!-- el-cascader -->
    <el-cascader v-if="'el-cascader'===item.__key" v-bind="{...itemAttrs}"
                 v-model="formData[item.__modelName]"/>
    <!-- el-date-picker -->
    <el-date-picker v-if="'el-date-picker'===item.__key" v-bind="{...itemAttrs}"
                    v-model="formData[item.__modelName]"/>
    <!-- el-switch -->
    <el-switch v-if="'el-switch'===item.__key" v-bind="{...itemAttrs}"
               v-model="formData[item.__modelName]"/>
    <!-- my-wang-editor -->
    <my-wang-editor v-if="'my-wang-editor'===item.__key" v-bind="{...itemAttrs}"
                    v-model="formData[item.__modelName]"/>
    <!-- image-avatar -->
    <image-avatar v-if="'image-avatar'===item.__key" v-bind="{...itemAttrs}"
                  v-model="formData[item.__modelName]"/>
    <!-- image-one -->
    <image-one v-if="'image-one'===item.__key" v-bind="{...itemAttrs}"
               v-model="formData[item.__modelName]"/>
    <!-- image-upload -->
    <image-upload v-if="'image-upload'===item.__key" v-bind="{...itemAttrs}"
                  v-model="formData[item.__modelName]"/>
    <!-- file-upload -->
    <file-upload v-if="'file-upload'===item.__key" v-bind="{...itemAttrs}"
                 v-model="formData[item.__modelName]"/>
    <!-- el-slider -->
    <el-slider v-if="'el-slider'===item.__key" v-bind="{...itemAttrs}"
               v-model="formData[item.__modelName]"/>
    <!-- el-rate -->
    <el-rate v-if="'el-rate'===item.__key" v-bind="{...itemAttrs}"
             v-model="formData[item.__modelName]"/>
  </el-form-item>
</template>

<script setup lang="ts">
import { strToObj } from '@/utils'
import { MyWangEditor } from '@/components/MyWangEditor'
import ImageAvatar from '@/components/Upload/ImageAvatar'
import ImageOne from '@/components/Upload/ImageOne'
import ImageUpload from '@/components/Upload/ImageUpload'
import FileUpload from '@/components/Upload/FileUpload'

const formData: any = defineModel()
// 处理属性
const {item} = defineProps({item: {type: Object, required: true, default: () => ({})}})
const itemAttrs = computed(() => {
  return Object.keys(item.__attrs).reduce((acc, key) => {
    // 特殊处理，object以字符串传入的key
    if (item.__attrs[key] !== undefined && item.__attrs[key] !== null && item.__attrs[key] !== '') {
      if (['marks', 'texts'].includes(key)) {
        if (item.__attrs[key]) acc[key] = strToObj(item.__attrs[key])
      } else {
        acc[key] = item.__attrs[key]
      }
    }
    return acc
  }, {})
})
// 监听__attrs.type变更，清空数据（防止意外格式）
watch(() => item.__attrs?.type, () => { formData.value[item.__modelName] = '' })

const getRules = (attrs: any) => {
  let result = []
  if (attrs.rules && attrs.rules.length > 0) {
    result = attrs.rules.map(item => {
      const rule = {...item}
      if (rule.required === true) {
        delete rule.pattern
        delete rule.validator
      } else if (rule.required === 'pattern' && rule.pattern) {
        delete rule.required
        delete rule.validator
        rule.pattern = new RegExp(rule.pattern.slice(1, -1))
      } else if (rule.required === 'validator' && rule.validator) {
        delete rule.required
        delete rule.pattern
        delete rule.message
        rule.validator = eval(rule.validator)
      }
      if (!rule.trigger) delete rule.trigger
      return rule
    })
  }
  return result
}
</script>

<style lang="less" scoped>
</style>