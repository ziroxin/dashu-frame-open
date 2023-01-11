<template>
  <div>
    <i v-if="iconName.startsWith('el-icon')" :class="iconName" />
    <svg-icon v-else :name="iconName" :icon-class="iconName" />
  </div>
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
