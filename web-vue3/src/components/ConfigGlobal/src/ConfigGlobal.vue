<template>
  <!-- ElementPlus的全局配置 @link https://element-plus.org/zh-CN/component/config-provider.html  -->
  <el-config-provider :namespace="variables.elNamespace" :message="currentMessage" :size="currentSize"
                      :locale="currentLocale" :link="currentLink">
    <slot></slot>
  </el-config-provider>
</template>
<script setup lang="ts">
import { useWindowSize } from '@vueuse/core'
import { useAppStore } from '@/store/modules/app'
import { useLocaleStore } from '@/store/modules/locale'
import { setCssVar } from '@/utils'
import { useDesign } from '@/hooks/web/useDesign'

const {variables} = useDesign()
const appStore = useAppStore()
const localeStore = useLocaleStore()

// 消息：{max:1(可同时显示的消息最大数量,不设置为不限制), grouping:true(合并内容相同的消息)}
const currentMessage = ref({grouping: true})
// 主题大小：default, large, small
const currentSize = computed(() => appStore.getCurrentSize)
// 设置语言
const currentLocale = computed(() => localeStore.getCurrentLocale.elLocale)
// 链接样式：{underline: 'hover' (控制下划线是否出现 'always' | 'hover' | 'never' | boolean)}
const currentLink = ref({underline: 'hover'})

// 初始化所有主题色
onMounted(() => { appStore.setCssVarTheme() })

// 监听窗口变化，切换合适的主题模式
const {width} = useWindowSize()
watch(() => width.value, (width: number) => {
  if (width < 768) {
    // 移动端模式
    !appStore.getMobile ? appStore.setMobile(true) : undefined
    setCssVar('--left-menu-min-width', '0')
    appStore.setCollapse(true)
    appStore.getLayout !== 'classic' ? appStore.setLayout('classic') : undefined
  } else {
    // 其他模式
    appStore.getMobile ? appStore.setMobile(false) : undefined
    setCssVar('--left-menu-min-width', '64px')
  }
}, {immediate: true})
</script>
