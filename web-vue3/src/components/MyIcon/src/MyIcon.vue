<!--
  自定义图标组件
  参数说明：
    icon: 图标名称，必填，字符串（以“svg-”开头或不加前缀 表示本地图标； 以“vi-”开头 表示在线图标）
    color: 图标颜色，非必填，字符串
    size: 图标大小，非必填，数字，默认16
    hoverColor: 鼠标悬停颜色，非必填，字符串
  使用说明：
    1. 本地图标，svg-开头或不加前缀
        .svg文件存放目录：src/assets/svgs
        示例：<my-icon icon="svg-404" /> 或 <my-icon icon="404" />
    2. 在线图标，vi-开头
        在网站 https://icon-sets.iconify.design/ 搜索图标（由于是国外网站只支持英文搜索）
        点击想用的图标，拷贝图标的 “Icon name”，加上前缀 vi- 即可使用
        示例：<my-icon icon="vi-twemoji:flag-china" />
        注意：build打包时会自动打包成本地svg，无需手动导入
-->
<template>
  <el-icon :class="prefixCls" :size="size" :color="color">
    <Icon v-if="icon?.startsWith('vi-')" :icon="icon?.replace(/^vi-/, '')" :style="{fontSize: `${size}px`,color}"/>
    <svg v-else aria-hidden="true">
      <use :xlink:href="`#icon-${icon?.startsWith('svg-') ? icon?.replace(/^svg-/, '') : icon}`"/>
    </svg>
  </el-icon>
</template>

<script setup lang="ts">
import { useDesign } from '@/hooks/web/useDesign'
import { Icon } from '@iconify/vue'

const prefixCls = useDesign().getPrefixCls('icon')

const {icon, color, size, hoverColor} = defineProps({
  icon: String,
  color: String,
  size: {type: Number, default: 16},
  hoverColor: String
})
</script>

<style lang="less" scoped>
@prefix-cls: ~'@{adminNamespace}-icon';

.@{prefix-cls},
.iconify {
  :deep(svg) {
    &:hover {
      color: v-bind(hoverColor) !important;
    }
  }
}

.iconify {
  &:hover {
    color: v-bind(hoverColor) !important;
  }
}
</style>
