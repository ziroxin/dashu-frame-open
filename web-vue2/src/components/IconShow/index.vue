<template>
  <i v-if="icon.includes('el-icon')" :class="icon" />
  <svg-icon v-else :icon-class="icon" />
</template>

<script>
export default {
  name: 'IconShow',
  props: {
    icon: {type: String, default: ''}
  }
}
</script>

<style scoped>

</style>
