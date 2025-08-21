<template>
  <el-form ref="dataFormRef" :model="formData" label-width="auto">
    <template v-for="item in formItemList" :key="item.__id">
      <div :class="current.__id===item.__id?'isActived':''" class="cursor-move m-5px p-10px b-rd-5px"
           @click.stop="()=>current={...item}">
        <el-form-item v-bind="{...item.__formItemAttrs}">
          <el-input v-if="item.__key==='el-input'" v-bind="{...item.__attrs}"
                    v-model="formData[item.__modelName]"/>
          <el-input-number v-if="item.__key==='el-input-number'" v-bind="{...item.__attrs}"
                           v-model="formData[item.__modelName]"/>
          <el-select v-if="item.__key==='el-select'" v-bind="{...item.__attrs}"
                     v-model="formData[item.__modelName]"/>
          <el-date-picker v-if="item.__key==='el-date-picker'" v-bind="{...item.__attrs}"
                          v-model="formData[item.__modelName]"/>
          <my-wang-editor v-if="item.__key==='my-wang-editor'" v-bind="{...item.__attrs}"
                          v-model="formData[item.__modelName]"/>
        </el-form-item>
        <div v-if="current.__id===item.__id" class="absolute bottom-5px right-10px z-9">
          <base-button @click="copyC(item)" type="primary" icon="el-icon-copy-document" plain circle size="small"/>
          <base-button @click="deleteC(item)" type="danger" icon="el-icon-delete" plain circle size="small"/>
        </div>
      </div>
    </template>
    <div v-if="!formItemList||formItemList.length<=0" class="text-center text-gray-500 bg-gray-500/5 rounded p-10px">
      点击左侧组件添加表单项
    </div>
  </el-form>
</template>

<script setup lang="ts">
import { MyWangEditor } from '@/components/MyWangEditor'

// 编辑器Model
const formItemList = defineModel()
const current = defineModel('current', {type: Object, default: () => {}})
const html = defineModel('html', {type: String, default: ''})
const ts = defineModel('ts', {type: String, default: ''})

onMounted(() => {
})


const dataFormRef = ref()
const formData = ref({})
const copyC = (item) => {
  const newItem = {...item, __id: generateUUID()}
  formItemList.value.push(newItem)
  setTimeout(() => { current.value = newItem }, 10)
}
const deleteC = (item) => {
  formItemList.value.splice(formItemList.value.indexOf(item), 1)
}

</script>

<style lang="less" scoped>
.isActived {
  position: relative;
  background-color: var(--el-fill-color-light);
}
</style>