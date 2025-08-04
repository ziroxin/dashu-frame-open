<template>
  <el-backtop :class="prefixCls"
              :target="`.${variables.namespace}-layout-content-scrollbar .${variables.elNamespace}-scrollbar__wrap`"/>
</template>
<script setup lang="ts">
import { useDesign } from '@/hooks/web/useDesign'

const {getPrefixCls, variables} = useDesign()
const prefixCls = getPrefixCls('backtop')
</script>
