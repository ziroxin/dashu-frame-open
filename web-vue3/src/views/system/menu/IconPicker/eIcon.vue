<template>
  <el-tooltip class="item" effect="dark" :content="iconName" placement="top">
    <i v-if="iconName.startsWith('el-icon')" :class="iconName"/>
    <svg-icon v-else :name="iconName" :icon-class="iconName"/>
  </el-tooltip>
</template>

<script>
export default {
  name: 'EIcon',
  props: {
    iconName: {
      type: String,
      required: true
    }
  }
};
</script>
