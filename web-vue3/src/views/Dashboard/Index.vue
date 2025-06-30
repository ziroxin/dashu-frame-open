<template>
  <panel-group/>
  <el-row :gutter="20" justify="space-between">
    <el-col :xl="10" :lg="10" :md="24" :sm="24" :xs="24">
      <el-card shadow="hover" class="mb-20px">
        <el-skeleton :loading="isLoading" animated>
          <Echart :options="pieOptionsData" :height="300"/>
        </el-skeleton>
      </el-card>
    </el-col>
    <el-col :xl="14" :lg="14" :md="24" :sm="24" :xs="24">
      <el-card shadow="hover" class="mb-20px">
        <el-skeleton :loading="isLoading" animated>
          <Echart :options="barOptionsData" :height="300"/>
        </el-skeleton>
      </el-card>
    </el-col>
    <el-col :span="24">
      <el-card shadow="hover" class="mb-20px">
        <el-skeleton :loading="isLoading" animated :rows="4">
          <Echart :options="lineOptionsData" :height="350"/>
        </el-skeleton>
      </el-card>
    </el-col>
  </el-row>
</template>
<script setup lang="ts">
import PanelGroup from './components/PanelGroup.vue'
import { Echart } from '@/components/Echart'
import { barOptions, lineOptions, pieOptions } from './echarts-data'
import { set } from 'lodash-es'
import { EChartsOption } from 'echarts'
import { useI18n } from '@/hooks/web/useI18n'
import { useAppStore } from '@/store/modules/app'

const {t} = useI18n()


const isDark = computed(() => useAppStore().getIsDark)
// 是否加载中
const isLoading = ref(true)
setTimeout(() => {
  // 加载模拟数据
  getUserAccessSource()
  getWeeklyUserActivity()
  getMonthlySales()
  isLoading.value = false
}, 1000)

// 用户来源
const pieOptionsData = reactive<EChartsOption>(pieOptions) as EChartsOption
const getUserAccessSource = () => {
  const userAccessSourceArr = [
    {value: 1000, name: 'analysis.directAccess'}, {value: 310, name: 'analysis.mailMarketing'},
    {value: 234, name: 'analysis.allianceAdvertising'}, {value: 135, name: 'analysis.videoAdvertising'},
    {value: 1548, name: 'analysis.searchEngines'}
  ]
  set(pieOptionsData, 'legend.data', userAccessSourceArr.map((v) => t(v.name)))
  pieOptionsData!.series![0].data = userAccessSourceArr.map((v) => ({name: t(v.name), value: v.value}))
}

// 周活跃量
const barOptionsData = reactive<EChartsOption>(barOptions) as EChartsOption
const getWeeklyUserActivity = () => {
  const weeklyUserActivityArr = [
    {value: 13253, name: 'analysis.monday'}, {value: 34235, name: 'analysis.tuesday'},
    {value: 26321, name: 'analysis.wednesday'}, {value: 12340, name: 'analysis.thursday'},
    {value: 24643, name: 'analysis.friday'}, {value: 1322, name: 'analysis.saturday'},
    {value: 1324, name: 'analysis.sunday'}
  ]
  set(barOptionsData, 'xAxis.data', weeklyUserActivityArr.map((v) => t(v.name)))
  set(barOptionsData, 'series',
      [{name: t('analysis.activeQuantity'), data: weeklyUserActivityArr.map((v) => v.value), type: 'bar'}])
}

// 每月销售总额
const lineOptionsData = reactive<EChartsOption>(lineOptions) as EChartsOption
const getMonthlySales = () => {
  const monthlySalesArr = [
    {estimate: 100, actual: 120, name: 'analysis.january'}, {estimate: 120, actual: 82, name: 'analysis.february'},
    {estimate: 161, actual: 91, name: 'analysis.march'}, {estimate: 134, actual: 154, name: 'analysis.april'},
    {estimate: 105, actual: 162, name: 'analysis.may'}, {estimate: 160, actual: 140, name: 'analysis.june'},
    {estimate: 165, actual: 145, name: 'analysis.july'}, {estimate: 114, actual: 250, name: 'analysis.august'},
    {estimate: 163, actual: 134, name: 'analysis.september'}, {estimate: 185, actual: 56, name: 'analysis.october'},
    {estimate: 118, actual: 99, name: 'analysis.november'}, {estimate: 123, actual: 123, name: 'analysis.december'}
  ]
  set(lineOptionsData, 'xAxis.data', monthlySalesArr.map((v) => t(v.name)))
  set(lineOptionsData, 'series', [{
    name: t('analysis.estimate'), smooth: true, type: 'line',
    data: monthlySalesArr.map((v) => v.estimate),
    animationDuration: 2800, animationEasing: 'cubicInOut'
  }, {
    name: t('analysis.actual'), smooth: true, type: 'line', itemStyle: {},
    data: monthlySalesArr.map((v) => v.actual),
    animationDuration: 2800, animationEasing: 'quadraticOut'
  }])
}

/**
 * 更新 legend.textStyle
 */
const updateLegendTextStyle = (options) => {
  const newTextStyle = {color: isDark.value ? '#ccc' : '#333'}
  const inactiveColor = isDark.value ? '#abacac' : '#ccc'
  set(options, 'title.textStyle', newTextStyle)
  if (options !== barOptionsData) {
    set(options, 'legend.textStyle', newTextStyle)
    set(options, 'legend.inactiveColor', inactiveColor)
  }
  options === pieOptionsData && set(options, 'series[0].emptyCircleStyle.color', inactiveColor)
}

// 监听暗黑模式变化并重新更新样式
watch(isDark, () => {
  updateLegendTextStyle(pieOptionsData)
  updateLegendTextStyle(barOptionsData)
  updateLegendTextStyle(lineOptionsData)
})
onMounted(() => {
  updateLegendTextStyle(pieOptionsData)
  updateLegendTextStyle(barOptionsData)
  updateLegendTextStyle(lineOptionsData)
})
</script>
