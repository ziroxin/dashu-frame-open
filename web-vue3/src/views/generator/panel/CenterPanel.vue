<template>
  <el-form ref="formRef" label-width="100px" :model="formData">
    <div :animation="150" group="content" @click="()=>currentId=''"
         class="flex flex-col h-[calc(100vh-var(--top-tool-height)-30px)] overflow-auto">
      <template v-if="contentList&&contentList.length>0">
        <div v-for="item in contentList" :key="item.__id" @click.stop="()=>currentId=item.__id"
             :class="item.__id===currentId?'isActived':''" class="cursor-move m-5px p-10px b-rd-5px">
          <el-form-item v-bind="{...item.__formItemAttrs}">
            <el-input v-if="item.__key==='el-input'" v-bind="{...item.__attrs}"
                      v-model="formData[item.__modelName]"/>
            <el-input-number v-if="item.__key==='el-input-number'" v-bind="{...item.__attrs}"
                             v-model="formData[item.__modelName]"/>
            <el-select v-if="item.__key==='el-select'" v-bind="{...item.__attrs}"
                       v-model="formData[item.__modelName]"/>
            <el-date-picker v-if="item.__key==='el-date-picker'" v-bind="{...item.__attrs}"
                            v-model="formData[item.__modelName]"/>
            <!--            <my-wang-editor v-if="item.__key==='my-wang-editor'" v-bind="{...item.__attrs}"-->
            <!--                            v-model="formData[item.__modelName]"/>-->
          </el-form-item>
          <div v-if="item.__id===currentId" class="absolute bottom-5px right-10px z-9">
            <base-button @click="copyC(item)" type="primary" icon="el-icon-copy-document" plain circle size="small"/>
            <base-button @click="deleteC(item)" type="danger" icon="el-icon-delete" plain circle size="small"/>
          </div>
        </div>
      </template>
      <div v-else class="text-center text-gray-500 bg-gray-500/5 rounded p-10px">
        从左侧拖入组件进行表单设计
      </div>
    </div>
  </el-form>
  <div class="absolute left-0 bottom-0 bg-#ccc">
    <el-button type="primary" @click="submitForm">保存</el-button>
    <div>{{ formData }}</div>
  </div>
</template>

<script setup lang="ts">
import { generateUUID } from '@/utils/tools'

const modelValue = defineModel()

const contentList: any[] = defineModel()
const currentId = ref('')

const copyC = (item) => {
  const newId = generateUUID()
  contentList.value.push({...item, __id: newId})
  setTimeout(() => { currentId.value = newId }, 10)
}
const deleteC = (item) => {
  contentList.value.splice(contentList.value.indexOf(item), 1)
}

const formRef = ref()
const formData = ref({})
watch(contentList, () => {
  const result = {}
  contentList.value?.forEach(item => { result[item.__modelName] = item.__defaultValue })
  if (result && formRef.value) {
    console.log(222)
    formData.value = result
    setTimeout(() => { formRef.value.resetFields() }, 10)
  }
}, {immediate: true, deep: true})

const submitForm = () => {
  console.log(111, formData.value)
  formRef.value.validate((valid) => {
    if (valid) {
      console.log(formData)
    }
  })
}
</script>

<style lang="less" scoped>
.isActived {
  position: relative;
  background-color: var(--el-fill-color-light);
}
</style>