<template>
  <div class="m-[var(--app-content-margin)]">
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
  </div>
</template>
<script setup lang="ts">
import PanelGroup from './components/PanelGroup'
import { Echart } from '@/components/Echart'
import { EChartsOption } from 'echarts'
import { barOptions, lineOptions, pieOptions } from './echarts-data'
import { set } from 'lodash-es'
import { useAppStore } from '@/store/modules/app'

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
    {value: 1000, name: '直接访问'}, {value: 310, name: '邮件营销'}, {value: 234, name: '联盟广告'},
    {value: 135, name: '视频广告'}, {value: 1548, name: '搜索引擎'}
  ]
  set(pieOptionsData, 'legend.data', userAccessSourceArr.map((v) => v.name))
  pieOptionsData!.series![0].data = userAccessSourceArr.map((v) => ({name: v.name, value: v.value}))
}

// 周活跃量
const barOptionsData = reactive<EChartsOption>(barOptions) as EChartsOption
const getWeeklyUserActivity = () => {
  const weeklyUserActivityArr = [
    {value: 1323, name: '周一'}, {value: 3423, name: '周二'}, {value: 2632, name: '周三'}, {value: 1340, name: '周四'},
    {value: 2464, name: '周五'}, {value: 1322, name: '周六'}, {value: 1324, name: '周日'}
  ]
  set(barOptionsData, 'xAxis.data', weeklyUserActivityArr.map((v) => v.name))
  set(barOptionsData, 'series', [{name: '活跃量', data: weeklyUserActivityArr.map((v) => v.value), type: 'bar'}])
}

// 每月销售总额
const lineOptionsData = reactive<EChartsOption>(lineOptions) as EChartsOption
const getMonthlySales = () => {
  const monthlySalesArr = [
    {estimate: 100, actual: 120, name: '一月'}, {estimate: 120, actual: 82, name: '二月'},
    {estimate: 161, actual: 91, name: '三月'}, {estimate: 134, actual: 154, name: '四月'},
    {estimate: 105, actual: 162, name: '五月'}, {estimate: 160, actual: 140, name: '六月'},
    {estimate: 165, actual: 145, name: '七月'}, {estimate: 114, actual: 250, name: '八月'},
    {estimate: 163, actual: 134, name: '九月'}, {estimate: 185, actual: 56, name: '十月'},
    {estimate: 118, actual: 99, name: '十一月'}, {estimate: 123, actual: 123, name: '十二月'}
  ]
  set(lineOptionsData, 'xAxis.data', monthlySalesArr.map((v) => v.name))
  set(lineOptionsData, 'series', [{
    name: '预计', smooth: true, type: 'line',
    data: monthlySalesArr.map((v) => v.estimate),
    animationDuration: 2800, animationEasing: 'cubicInOut'
  }, {
    name: '实际', smooth: true, type: 'line', itemStyle: {},
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
