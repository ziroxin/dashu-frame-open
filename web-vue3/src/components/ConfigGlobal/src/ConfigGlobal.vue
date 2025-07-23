<template>
  <!-- ElementPlus的全局配置 @link https://element-plus.org/zh-CN/component/config-provider.html  -->
  <el-config-provider :namespace="variables.elNamespace" :message="currentMessage" :size="currentSize"
                      :link="currentLink" :locale="currentLocale">
    <slot></slot>
  </el-config-provider>
</template>
<script setup lang="ts">
import { useWindowSize } from '@vueuse/core'
import { useAppStore } from '@/store/modules/app'
import { setCssVar } from '@/utils'
import { useDesign } from '@/hooks/web/useDesign'
import { useLocaleStoreWithOut } from '@/store/modules/locale'

const {variables} = useDesign()
const appStore = useAppStore()

// 初始化所有主题色
onMounted(() => { appStore.setCssVarTheme() })

// 消息：{max:10(可同时显示的消息最大数量，默认不限制), grouping:true(合并内容相同的消息)}
const currentMessage = computed(() => ({grouping: true}))

// 主题大小：default, large, small
const currentSize = computed(() => appStore.getCurrentSize || 'default')

// 链接样式：{underline: 'hover' (控制下划线是否出现 'always' | 'hover' | 'never' | boolean)}
const currentLink = ref({underline: 'hover'})

// 设置语言
const currentLocale = unref(useLocaleStoreWithOut().currentLocale).elLocale

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
