<template>
  <div class="left-title">
    <my-icon icon="el-icon-edit" class="mr-5px"/>
    输入组件
  </div>
  <div :sort="false" :clone="addToCenter" class="grid gap-5px w-full grid-cols-[1fr_1fr] mt-10px mb-15px">
    <div v-for="item in inputList" :key="item.id" class="left-component-item" @click="addToCenter(item)">
      {{ item.name }}
    </div>
  </div>

  <div class="left-title">
    <my-icon icon="el-icon-news" class="mr-5px"/>
    选择组件
  </div>
  <div :sort="false" :clone="addToCenter" class="grid gap-5px w-full grid-cols-[1fr_1fr] mt-10px mb-15px">
    <div v-for="item in selectList" :key="item.id" class="left-component-item">
      {{ item.name }}
    </div>
  </div>

  <div class="left-title">
    <my-icon icon="el-icon-menu" class="mr-5px"/>
    自定义组件
  </div>
  <div :sort="false" :clone="addToCenter" class="grid gap-5px w-full grid-cols-[1fr_1fr] mt-10px mb-15px">
    <div v-for="item in diyList" :key="item.id" class="left-component-item">
      {{ item.name }}
    </div>
  </div>

  <div class="left-title">
    <my-icon icon="el-icon-set-up" class="mr-5px"/>
    布局组件
  </div>
  <div :sort="false" :clone="addToCenter" class="grid gap-5px w-full grid-cols-[1fr_1fr] mt-10px mb-15px">
    <div v-for="item in otherList" :key="item.id" class="left-component-item">
      {{ item.name }}
    </div>
  </div>
</template>

<script setup lang="ts">
const inputList = ref([{name: 's1', id: 's1'}, {name: 's2', id: 's2'}])
const selectList = ref([{name: 's1', id: 's1'}, {name: 's2', id: 's2'}])
const diyList = ref([{name: 'l1', id: 'l1'}, {name: 'l2', id: 'l2'}])
const otherList = ref([{name: 'l1', id: 'l1'}, {name: 'l2', id: 'l2'}])

const addToCenter = (item) => {
  console.log(item)
  return {...item}
}
</script>

<style lang="less" scoped>
.left-title {
  display: flex;
  justify-content: start;
  align-items: center;
  font-size: 14px;
}

.left-component-item {
  padding: 8px 5px;
  background-color: var(--el-bg-color);
  border-radius: 6px;
  font-size: 13px;
  text-align: center;
  cursor: grab;
  box-shadow: 0 2px 8px #0000000d;
  transition: transform .2s, box-shadow .2s;
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 10px #0000001a;
  }
}
</style>