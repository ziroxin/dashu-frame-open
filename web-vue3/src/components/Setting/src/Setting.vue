<template>
  <div :class="prefixCls" @click="drawer=true"
       class="fixed bottom-[10%] right-0 w-40px h-40px flex items-center justify-center bg-[var(--el-color-primary)] cursor-pointer z-10">
    <my-icon icon="vi-ant-design:setting-outlined" color="#fff"/>
  </div>
  <el-drawer v-model="drawer" direction="rtl" size="350px" :z-index="2000"
             header-class="mb-10px!" modal-class="bg-[#00000020]!">
    <template #header>
      <span class="text-16px font-700">{{ t('setting.projectSetting') }}</span>
    </template>

    <div class="text-center">
      <!-- 浅色/暗黑（深色模式） -->
      <el-divider>{{ t('setting.theme') }}</el-divider>
      <theme-switch/>
      <!-- 布局 -->
      <el-divider>{{ t('setting.layout') }}</el-divider>
      <layout-radio-picker/>
      <!-- 主题颜色（背景、按钮、文字等颜色） -->
      <el-divider>{{ t('setting.systemTheme') }}</el-divider>
      <div class="flex justify-start items-center flex-wrap lh-30px">
        <color-radio-picker v-model="systemTheme" @change="setSystemTheme"
                            :schema="['#409eff','#009688','#23ae6b','#f5222d','#fa541c','#faad14','#52c41a','#722ed1']"/>
        <br/>
        <div class="text-14px color-#333">自定义颜色：</div>
        <el-color-picker v-model="systemTheme" @change="setSystemTheme" :teleported="false" show-alpha/>
      </div>
      <!-- Header头部背景色 -->
      <el-divider>{{ t('setting.headerTheme') }}</el-divider>
      <div class="flex justify-start items-center">
        <color-radio-picker v-model="headerTheme" @change="setHeaderTheme" class="mt-8px"
                            :schema="['#ffffff','#151515','#001529','#d61c1c','#036acf']"/>
        <div class="text-14px color-#333 pl-20px">自定义：</div>
        <el-color-picker v-model="headerTheme" @change="setHeaderTheme" :teleported="false" show-alpha/>
      </div>
      <!-- 菜单容器背景色 -->
      <el-divider>{{ t('setting.menuTheme') }}</el-divider>
      <div class="flex justify-start items-center">
        <color-radio-picker v-model="menuTheme" @change="setMenuTheme" class="mt-8px"
                            :schema="['#ffffff','#151515','#001529','#d61c1c','#036acf']"/>
        <div class="text-14px color-#333 pl-20px">自定义：</div>
        <el-color-picker v-model="menuTheme" @change="setMenuTheme" :teleported="false" show-alpha/>
      </div>
    </div>

    <!-- 更多配置（页头/页脚/各种图标/手风琴/灰色模式等） -->
    <interface-display/>

    <!-- 保存和重置按钮 -->
    <el-divider/>
    <div class="flex justify-center items-center">
      <base-button type="primary" icon="el-icon-check" :loading="isLoading"
                   @click="saveTheme">{{ t('setting.saveText') }}
      </base-button>
      <base-button type="danger" icon="reset" @click="clear">{{ t('setting.clearAndReset') }}</base-button>
    </div>
    <div class="text-center mt-15px">
      <el-tooltip content="拷贝成功后，粘贴至[/src/store/modules/app.ts]中<br/>即可作为系统默认配置"
                  :raw-content="true" :teleported="false" placement="top">
        <base-button type="success" icon="el-icon-copy-document" class="w-230px" v-if="loadCopyButton"
                     v-clipboard:copy="copyTheme" v-clipboard:success="copySuccess">拷贝主题配置
        </base-button>
        <base-button type="success" icon="el-icon-copy-document" class="w-230px" v-else
                     :loading="!loadCopyButton">拷贝主题配置
        </base-button>
      </el-tooltip>
    </div>
  </el-drawer>
</template>

<script setup lang="ts">
import ColorRadioPicker from './components/ColorRadioPicker.vue'
import InterfaceDisplay from './components/InterfaceDisplay.vue'
import LayoutRadioPicker from './components/LayoutRadioPicker.vue'
import storageKeys from '@/utils/storage-keys'
import request from '@/utils/request'
import { ElMessageBox, ElNotification } from 'element-plus'
import { useI18n } from '@/hooks/web/useI18n'
import { ThemeSwitch } from '@/components/ThemeSwitch'
import { useCssVar } from '@vueuse/core'
import { useAppStore } from '@/store/modules/app'
import { setCssVar, trim } from '@/utils'
import { useDesign } from '@/hooks/web/useDesign'

const {t} = useI18n()
const prefixCls = useDesign().getPrefixCls('setting')
const appStore = useAppStore()

const drawer = ref(false)
const isLoading = ref(false)

// 复制主题按钮
const loadCopyButton = ref(false)
watch(appStore, () => {
  loadCopyButton.value = false
  setTimeout(() => {loadCopyButton.value = true}, 500)
}, {immediate: true, deep: true})

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

// 保存当前主题配置
const saveTheme = () => {
  isLoading.value = true
  // 保存用户主题数据到数据库
  const themeObj = {
    isDark: appStore.getIsDark,
    layout: appStore.getLayout,
    theme: appStore.getTheme,
    tagsView: appStore.getTagsView,
    tagsViewIcon: appStore.getTagsViewIcon,
    fixedHeader: appStore.getFixedHeader,
    footer: appStore.getFooter,
    logo: appStore.getLogo,
    hamburger: appStore.getHamburger,
    breadcrumb: appStore.getBreadcrumb,
    breadcrumbIcon: appStore.getBreadcrumbIcon,
    screenfull: appStore.getScreenfull,
    size: appStore.getSize,
    locale: appStore.getLocale,
    uniqueOpened: appStore.getUniqueOpened,
    greyMode: appStore.getGreyMode,
    fixedMenu: appStore.getFixedMenu,
    watermark: appStore.getWatermark,
    watermarkTitle: appStore.getWatermarkTitle
  }
  const data = {themeType: 'vue3', themeJson: JSON.stringify(themeObj)}
  request({url: '/userTheme/zUserTheme/updateByUser', method: 'post', data}).then(() => {
    isLoading.value = false
    ElNotification({message: '保存用户配置成功', title: '操作成功', type: 'success', position: 'top-left'})
  })
}

const copySuccess = () => {
  ElNotification({message: '拷贝主题配置成功！', title: '操作成功', type: 'success', position: 'top-left'})
}
// 拷贝配置
const copyTheme = computed(() => {
  return `
      // 主题相关设置项
      isDark: ${appStore.getIsDark}, // 是否是暗黑模式
      layout: '${appStore.getLayout}', // layout布局：'classic'=经典左右布局 | 'topLeft'=顶部左侧布局 | 'top'=顶部菜单布局 | 'cutMenu'=分栏菜单布局
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
      },
      tagsView: ${appStore.getTagsView}, // 标签页
      tagsViewIcon: ${appStore.getTagsViewIcon}, // 是否显示标签图标
      fixedHeader: ${appStore.getFixedHeader}, // 固定头部（头部+标签页）
      footer: ${appStore.getFooter}, // 显示页脚
      logo: ${appStore.getLogo}, // logo
      hamburger: ${appStore.getHamburger}, // 折叠图标（是否显示折叠图标）
      breadcrumb: ${appStore.getBreadcrumb}, // 面包屑
      breadcrumbIcon: ${appStore.getBreadcrumbIcon}, // 面包屑图标
      screenfull: ${appStore.getScreenfull}, // 全屏图标
      size: ${appStore.getSize}, // 尺寸图标
      locale: ${appStore.getLocale}, // 多语言图标
      uniqueOpened: ${appStore.getUniqueOpened}, // 菜单手风琴（只展开一个子菜单）
      greyMode: ${appStore.getGreyMode}, // 是否开始灰色模式，用于特殊悼念日
      fixedMenu: ${appStore.getFixedMenu}, // 是否固定菜单
      watermark: ${appStore.getWatermark}, // 是否显示水印
      watermarkTitle: '${appStore.getWatermarkTitle}' // 水印标题
  `
})

// 重置默认配置
const clear = () => {
  ElMessageBox.confirm('确定要重置成默认主题吗?', '重置提醒', {
    confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning', customClass: 'z-5000!'
  }).then(() => {
    // 清空非自定义的缓存
    Object.keys(localStorage).filter(k => !k.startsWith(storageKeys.key_prefix))
        .forEach(k => { localStorage.removeItem(k) })
    // 清空主题信息缓存
    localStorage.removeItem(storageKeys.l_themeSetting)
    // 刷新页面
    window.location.reload()
  })
}
</script>

<style lang="less" scoped>
@prefix-cls: ~'@{adminNamespace}-setting';
.@{prefix-cls} {
  border-radius: 6px 0 0 6px;
}
</style>
