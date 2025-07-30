<template>
  <div :class="prefixCls" @click="drawer=true"
       class="fixed bottom-[10%] right-0 w-40px h-40px flex items-center justify-center bg-[var(--el-color-primary)] cursor-pointer z-10">
    <my-icon icon="vi-ant-design:setting-outlined" color="#fff"/>
  </div>
  <el-drawer v-model="drawer" direction="rtl" size="350px" :z-index="4000"
             header-class="mb-10px!" modal-class="bg-[#00000020]!">
    <template #header>
      <span class="text-16px font-700">{{ t('setting.projectSetting') }}</span>
    </template>

    <div class="text-center">
      <!-- 主题 -->
      <el-divider>{{ t('setting.theme') }}</el-divider>
      <theme-switch/>
      <!-- 布局 -->
      <el-divider>{{ t('setting.layout') }}</el-divider>
      <layout-radio-picker/>
      <!-- 系统主题 -->
      <el-divider>{{ t('setting.systemTheme') }}</el-divider>
      <div class="flex justify-start items-center flex-wrap lh-30px">
        <color-radio-picker v-model="systemTheme" @change="setSystemTheme"
                            :schema="['#409eff','#009688','#f5222d','#fa541c','#faad14','#13c2c2','#52c41a','#722ed1']"/>
        <br/>
        <div class="text-14px color-#333">自定义颜色：</div>
        <el-color-picker v-model="systemTheme" @change="setSystemTheme" :teleported="false" show-alpha/>
      </div>
      <!-- 头部主题 -->
      <el-divider>{{ t('setting.headerTheme') }}</el-divider>
      <div class="flex justify-start items-center">
        <color-radio-picker v-model="headerTheme" @change="setHeaderTheme" class="mt-8px"
                            :schema="['#ffffff','#151515','#001529']"/>
        <div class="text-14px color-#333 pl-30px">自定义颜色：</div>
        <el-color-picker v-model="headerTheme" @change="setHeaderTheme" :teleported="false" show-alpha/>
      </div>
      <!-- 菜单主题 -->
      <el-divider>{{ t('setting.menuTheme') }}</el-divider>
      <div class="flex justify-start items-center">
        <color-radio-picker v-model="menuTheme" @change="setMenuTheme" class="mt-8px"
                            :schema="['#ffffff','#151515','#001529']"/>
        <div class="text-14px color-#333 pl-30px">自定义颜色：</div>
        <el-color-picker v-model="menuTheme" @change="setMenuTheme" :teleported="false" show-alpha/>
      </div>
    </div>

    <!-- 更多配置 -->
    <interface-display/>

    <!-- 操作按钮 -->
    <el-divider/>
    <div>
      <base-button type="primary" class="w-full" @click="copyConfig">{{ t('setting.copy') }}</base-button>
    </div>
    <div class="mt-5px">
      <base-button type="danger" class="w-full" @click="clear">{{ t('setting.clearAndReset') }}</base-button>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import ColorRadioPicker from './components/ColorRadioPicker.vue'
import InterfaceDisplay from './components/InterfaceDisplay.vue'
import LayoutRadioPicker from './components/LayoutRadioPicker.vue'
import { ElMessage } from 'element-plus'
import { useI18n } from '@/hooks/web/useI18n'
import { ThemeSwitch } from '@/components/ThemeSwitch'
import { useClipboard, useCssVar } from '@vueuse/core'
import { useAppStore } from '@/store/modules/app'
import { setCssVar, trim } from '@/utils'
import { useDesign } from '@/hooks/web/useDesign'
import storageKeys from '@/utils/storage-keys'

const {t} = useI18n()
const prefixCls = useDesign().getPrefixCls('setting')
const appStore = useAppStore()

const drawer = ref(false)

// 主题色相关
const systemTheme = ref(appStore.getTheme.elColorPrimary)
const setSystemTheme = (color: string) => {
  setCssVar('--el-color-primary', color)
  appStore.setTheme({elColorPrimary: color})
  const leftMenuBgColor = useCssVar('--left-menu-bg-color', document.documentElement)
  setMenuTheme(trim(unref(leftMenuBgColor) as string))
}

// 头部主题相关
const headerTheme = ref(appStore.getTheme.topHeaderBgColor || '')
const setHeaderTheme = (color: string) => {
  appStore.setHeaderTheme(color)
}

// 菜单主题相关
const menuTheme = ref(appStore.getTheme.leftMenuBgColor || '')
const setMenuTheme = (color: string) => {
  appStore.setMenuTheme(color)
}

// 拷贝当前主题配置
const copyConfig = async () => {
  const {copy, copied, isSupported} = useClipboard({
    source: `
      // 面包屑
      breadcrumb: ${appStore.getBreadcrumb},
      // 面包屑图标
      breadcrumbIcon: ${appStore.getBreadcrumbIcon},
      // 折叠图标
      hamburger: ${appStore.getHamburger},
      // 全屏图标
      screenfull: ${appStore.getScreenfull},
      // 尺寸图标
      size: ${appStore.getSize},
      // 多语言图标
      locale: ${appStore.getLocale},
      // 标签页
      tagsView: ${appStore.getTagsView},
      // 标签页图标
      getTagsViewIcon: ${appStore.getTagsViewIcon},
      // logo
      logo: ${appStore.getLogo},
      // 菜单手风琴
      uniqueOpened: ${appStore.getUniqueOpened},
      // 固定header
      fixedHeader: ${appStore.getFixedHeader},
      // 页脚
      footer: ${appStore.getFooter},
      // 灰色模式
      greyMode: ${appStore.getGreyMode},
      // layout布局
      layout: '${appStore.getLayout}',
      // 暗黑模式
      isDark: ${appStore.getIsDark},
      // 组件尺寸
      currentSize: '${appStore.getCurrentSize}',
      // 主题相关
      theme: {
        // 主题色
        elColorPrimary: '${appStore.getTheme.elColorPrimary}',
        // 左侧菜单边框颜色
        leftMenuBorderColor: '${appStore.getTheme.leftMenuBorderColor}',
        // 左侧菜单背景颜色
        leftMenuBgColor: '${appStore.getTheme.leftMenuBgColor}',
        // 左侧菜单浅色背景颜色
        leftMenuBgLightColor: '${appStore.getTheme.leftMenuBgLightColor}',
        // 左侧菜单选中背景颜色
        leftMenuBgActiveColor: '${appStore.getTheme.leftMenuBgActiveColor}',
        // 左侧菜单收起选中背景颜色
        leftMenuCollapseBgActiveColor: '${appStore.getTheme.leftMenuCollapseBgActiveColor}',
        // 左侧菜单字体颜色
        leftMenuTextColor: '${appStore.getTheme.leftMenuTextColor}',
        // 左侧菜单选中字体颜色
        leftMenuTextActiveColor: '${appStore.getTheme.leftMenuTextActiveColor}',
        // logo字体颜色
        logoTitleTextColor: '${appStore.getTheme.logoTitleTextColor}',
        // logo边框颜色
        logoBorderColor: '${appStore.getTheme.logoBorderColor}',
        // 头部背景颜色
        topHeaderBgColor: '${appStore.getTheme.topHeaderBgColor}',
        // 头部字体颜色
        topHeaderTextColor: '${appStore.getTheme.topHeaderTextColor}',
        // 头部悬停颜色
        topHeaderHoverColor: '${appStore.getTheme.topHeaderHoverColor}',
        // 头部边框颜色
        topToolBorderColor: '${appStore.getTheme.topToolBorderColor}'
      }
    `,
    legacy: true
  })
  if (!isSupported) {
    ElMessage.error(t('setting.copyFailed'))
  } else {
    await copy()
    if (unref(copied)) {
      ElMessage.success(t('setting.copySuccess'))
    }
  }
}

// 清空缓存
const clear = () => {
  // 清空非自定义的缓存
  Object.keys(localStorage).filter(k => !k.startsWith(storageKeys.key_prefix))
      .forEach(k => { localStorage.removeItem(k) })
  // 清空主题信息缓存
  localStorage.removeItem(storageKeys.l_themeSetting)
  // 刷新页面
  window.location.reload()
}
</script>

<style lang="less" scoped>
@prefix-cls: ~'@{adminNamespace}-setting';
.@{prefix-cls} {
  border-radius: 6px 0 0 6px;
}
</style>
