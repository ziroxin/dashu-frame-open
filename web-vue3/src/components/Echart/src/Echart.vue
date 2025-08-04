<template>
  <div ref="elRef" :class="[$attrs.class, prefixCls]" :style="styles"></div>
</template>

<script setup lang="ts">
import type { EChartsOption } from 'echarts'
import echarts from '@/plugins/echarts'
import 'echarts-wordcloud'
import { debounce } from 'lodash-es'
import { useAppStore } from '@/store/modules/app'
import { isString } from '@/utils/is'
import { useDesign } from '@/hooks/web/useDesign'

const {getPrefixCls, variables} = useDesign()
const prefixCls = getPrefixCls('echart')
// 传入参数
const props = defineProps({
  options: {type: Object as EChartsOption, required: true},
  width: {type: [Number, String], default: '100%'},
  height: {type: [Number, String], default: '500px'}
})

const appStore = useAppStore()
const isDark = computed(() => appStore.getIsDark)
const theme = computed(() => unref(isDark) ? 'dark' : 'auto')
const options = computed(() => ({...props.options, darkMode: unref(theme)}))

const elRef = ref<ElRef>()
let echartRef: Nullable<echarts.ECharts> = null
const contentEl = ref<Element>()

const styles = computed(() => {
  const width = isString(props.width) ? props.width : `${props.width}px`
  const height = isString(props.height) ? props.height : `${props.height}px`
  return {width, height}
})

const initChart = () => {
  if (unref(elRef) && props.options) {
    echartRef = echarts.init(unref(elRef) as HTMLElement)
    echartRef?.setOption(unref(options))
  }
}

watch(() => options.value, (options) => { if (echartRef) echartRef?.setOption(options) }, {deep: true})
const resizeHandler = debounce(() => { if (echartRef) echartRef.resize() }, 100)
const contentResizeHandler = async (e: TransitionEvent) => { if (e.propertyName === 'width') resizeHandler() }

onMounted(() => {
  setTimeout(() => { initChart() }, 10)
  window.addEventListener('resize', resizeHandler)
  contentEl.value = document.getElementsByClassName(`${variables.namespace}-layout-content`)[0]
  unref(contentEl) && (unref(contentEl) as Element).addEventListener('transitionend', contentResizeHandler)
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeHandler)
  unref(contentEl) && (unref(contentEl) as Element).removeEventListener('transitionend', contentResizeHandler)
})
onActivated(() => { if (echartRef) echartRef.resize() })
</script>
