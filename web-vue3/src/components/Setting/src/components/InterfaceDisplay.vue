<template>
  <div :class="prefixCls">
    <el-divider>页头/页脚</el-divider>
    <!-- 标签页 -->
    <div class="flex justify-between items-center">
      <span class="text-14px">{{ t('setting.tagsViewSwitch') }}</span>
      <el-switch v-model="tagsView" @change="tagsViewChange"/>
    </div>
    <!-- 标签页图标 -->
    <div class="flex justify-between items-center">
      <span class="text-14px">{{ t('setting.tagsViewIconSwitch') }}</span>
      <el-switch v-model="tagsViewIcon" @change="tagsViewIconChange"/>
    </div>
    <!-- 固定头部 -->
    <div class="flex justify-between items-center">
      <span class="text-14px">
        {{ ['topLeft', 'cutMenu'].includes(layout) ? '固定头部（标签页栏）' : '固定头部（顶部工具栏+标签页栏）' }}
      </span>
      <el-switch v-model="fixedHeader" @change="fixedHeaderChange"/>
    </div>
    <!-- 页脚 -->
    <div class="flex justify-between items-center">
      <span class="text-14px">{{ t('setting.footerSwitch') }}</span>
      <el-switch v-model="footer" @change="footerChange"/>
    </div>
    <!-- 固定菜单(分栏模式) -->
    <div class="flex justify-between items-center" v-if="layout==='cutMenu'">
      <span class="text-14px">{{ t('setting.fixedMenuSwitch') }}</span>
      <el-switch v-model="fixedMenu" @change="fixedMenuChange"/>
    </div>

    <el-divider>各项控制图标</el-divider>
    <!-- Logo -->
    <div class="flex justify-between items-center">
      <span class="text-14px">{{ t('setting.logoSwitch') }}</span>
      <el-switch v-model="logo" @change="logoChange"/>
    </div>
    <!-- 折叠图标 -->
    <div class="flex justify-between items-center" v-if="showCollapse">
      <span class="text-14px">{{ t('setting.hamburgerIconSwitch') }}</span>
      <el-switch v-model="hamburger" @change="hamburgerChange"/>
    </div>
    <!-- 面包屑 -->
    <div class="flex justify-between items-center">
      <span class="text-14px">{{ t('setting.breadcrumbSwitch') }}</span>
      <el-switch v-model="breadcrumb" @change="breadcrumbChange"/>
    </div>
    <!-- 面包屑图标 -->
    <div class="flex justify-between items-center">
      <span class="text-14px">{{ t('setting.breadcrumbIconSwitch') }}</span>
      <el-switch v-model="breadcrumbIcon" @change="breadcrumbIconChange"/>
    </div>
    <!-- 全屏图标 -->
    <div class="flex justify-between items-center">
      <span class="text-14px">{{ t('setting.screenfullIconSwitch') }}</span>
      <el-switch v-model="screenfull" @change="screenfullChange"/>
    </div>
    <!-- 尺寸图标 -->
    <div class="flex justify-between items-center">
      <span class="text-14px">{{ t('setting.sizeIconSwitch') }}</span>
      <el-switch v-model="size" @change="sizeChange"/>
    </div>
    <!-- 多语言图标 -->
    <div class="flex justify-between items-center">
      <span class="text-14px">{{ t('setting.localeIconSwitch') }}</span>
      <el-switch v-model="locale" @change="localeChange"/>
    </div>
    <!-- 消息中心图标 -->
    <div class="flex justify-between items-center">
      <span class="text-14px">{{ t('setting.messageCenterIconSwitch') }}</span>
      <el-switch v-model="messageCenter" @change="messageCenterChange"/>
    </div>

    <el-divider>其他</el-divider>
    <!-- 菜单手风琴 -->
    <div class="flex justify-between items-center">
      <span class="text-14px">{{ t('setting.uniqueOpenedSwitch') }}</span>
      <el-switch v-model="uniqueOpened" @change="uniqueOpenedChange"/>
    </div>
    <!-- 灰色模式 -->
    <div class="flex justify-between items-center">
      <span class="text-14px">{{ t('setting.greyModeSwitch') }}</span>
      <el-switch v-model="greyMode" @change="greyModeChange"/>
    </div>

    <!-- 水印配置 -->
    <el-divider>水印配置</el-divider>
    <div class="flex justify-between items-center">
      <span class="text-14px">{{ t('setting.watermarkSwitch') }}</span>
      <el-switch v-model="watermark" @change="watermarkChange"/>
    </div>
    <div class="flex justify-between items-center">
      <span class="text-14px">{{ t('setting.watermarkTitleIpt') }}</span>
      <el-input v-model="watermarkTitle" @input="watermarkTitleChange" class="w-200px!" clearable/>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useI18n } from '@/hooks/web/useI18n'
import { useDesign } from '@/hooks/web/useDesign'
import { useAppStore } from '@/store/modules/app'
import { setCssVar } from '@/utils'
import { useWatermark } from '@/hooks/web/useWatermark'

const {t} = useI18n()
const prefixCls = useDesign().getPrefixCls('interface-display')
const appStore = useAppStore()
// 面包屑
const breadcrumb = ref(appStore.getBreadcrumb)
const breadcrumbChange = (show: boolean) => { appStore.setBreadcrumb(show) }
// 面包屑图标
const breadcrumbIcon = ref(appStore.getBreadcrumbIcon)
const breadcrumbIconChange = (show: boolean) => { appStore.setBreadcrumbIcon(show) }
// 折叠图标
const hamburger = ref(appStore.getHamburger)
const hamburgerChange = (show: boolean) => { appStore.setHamburger(show) }
// 全屏图标
const screenfull = ref(appStore.getScreenfull)
const screenfullChange = (show: boolean) => { appStore.setScreenfull(show) }
// 尺寸图标
const size = ref(appStore.getSize)
const sizeChange = (show: boolean) => { appStore.setSize(show) }
// 多语言图标
const locale = ref(appStore.getLocale)
const localeChange = (show: boolean) => { appStore.setLocale(show) }
// 消息中心图标
const messageCenter = ref(appStore.getMessageCenter)
const messageCenterChange = (show: boolean) => { appStore.setMessageCenter(show) }
// 标签页
const tagsView = ref(appStore.getTagsView)
const tagsViewChange = (show: boolean) => {
  // 切换标签栏显示时，同步切换标签栏的高度
  setCssVar('--tags-view-height', show ? '35px' : '0px')
  appStore.setTagsView(show)
}
// 标签页图标
const tagsViewIcon = ref(appStore.getTagsViewIcon)
const tagsViewIconChange = (show: boolean) => { appStore.setTagsViewIcon(show) }
// logo
const logo = ref(appStore.getLogo)
const logoChange = (show: boolean) => { appStore.setLogo(show) }
// 菜单手风琴
const uniqueOpened = ref(appStore.getUniqueOpened)
const uniqueOpenedChange = (uniqueOpened: boolean) => { appStore.setUniqueOpened(uniqueOpened) }
// 固定头部
const fixedHeader = ref(appStore.getFixedHeader)
const fixedHeaderChange = (show: boolean) => { appStore.setFixedHeader(show) }
// 页脚
const footer = ref(appStore.getFooter)
const footerChange = (show: boolean) => { appStore.setFooter(show) }
// 灰色模式
const greyMode = ref(appStore.getGreyMode)
const greyModeChange = (show: boolean) => { appStore.setGreyMode(show) }
// 固定菜单
const fixedMenu = ref(appStore.getFixedMenu)
const fixedMenuChange = (show: boolean) => { appStore.setFixedMenu(show) }

const {setWatermark, clearWatermark} = useWatermark()
// 水印
const watermark = ref(appStore.getWatermark)
const watermarkChange = (show: boolean) => {
  if (show) {
    setWatermark(watermarkTitle.value || import.meta.env.VITE_APP_TITLE)
    appStore.setWatermark(show)
  } else {
    clearWatermark()
    appStore.setWatermark(show)
  }
}
// 水印标题
const watermarkTitle = ref(appStore.getWatermarkTitle)
const watermarkTitleChange = () => {
  if (watermark.value) {
    setWatermark(watermarkTitle.value)
  }
  appStore.setWatermarkTitle(watermarkTitle.value)
}

const layout = computed(() => appStore.getLayout)
const showCollapse = ref(true)
watch(() => layout.value, (n) => {
  if (n === 'top' || n === 'cutMenu') {
    appStore.setCollapse(false)
    showCollapse.value = false
  } else {
    showCollapse.value = true
  }
})
</script>
