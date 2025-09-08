<template>
  <el-form-item v-bind="{...item.__formItemAttrs}" :prop="item.__modelName"
                :rules="getRules(item.__formItemAttrs)">
    <el-input v-if="item.__key==='el-input'" v-bind="{...itemAttrs}"
              v-model="formData[item.__modelName]"/>
    <el-input-number v-if="item.__key==='el-input-number'" v-bind="{...itemAttrs}"
                     v-model="formData[item.__modelName]"/>
    <el-input-tag v-if="item.__key==='el-input-tag'" v-bind="{...itemAttrs}"
                  v-model="formData[item.__modelName]"/>
    <el-select v-if="item.__key==='el-select'" v-bind="{...itemAttrs}"
               v-model="formData[item.__modelName]"/>
    <el-date-picker v-if="item.__key==='el-date-picker'" v-bind="{...itemAttrs}"
                    v-model="formData[item.__modelName]"/>
    <my-wang-editor v-if="item.__key==='my-wang-editor'" v-bind="{...itemAttrs}"
                    v-model="formData[item.__modelName]"/>
  </el-form-item>
</template>

<script setup lang="ts">
import { MyWangEditor } from '@/components/MyWangEditor'

const formData: any = defineModel()
const {item} = defineProps({item: {type: Object, required: true, default: () => ({})}})
const itemAttrs = computed(() => {
  return Object.keys(item.__attrs).reduce((acc, key) => {
    if (item.__attrs[key] !== undefined && item.__attrs[key] !== null && item.__attrs[key] !== '') {
      acc[key] = item.__attrs[key]
    }
    return acc
  }, {})
})

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