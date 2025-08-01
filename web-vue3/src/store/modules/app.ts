import { defineStore } from 'pinia'
import { store } from '../index'
import { humpToUnderline, setCssVar } from '@/utils'
import { colorIsDark, hexToRGB, lighten, mix } from '@/utils/color'
import { ComponentSize, ElMessage } from 'element-plus'
import { useCssVar, useDark } from '@vueuse/core'
import { unref } from 'vue'
import storageKeys from '@/utils/storage-keys'

interface AppState {
  sizeMap: ComponentSize[]
  currentSize: ComponentSize
  mobile: boolean
  title: string
  pageLoading: boolean
  collapse: boolean
  isDark: boolean
  layout: LayoutType
  theme: ThemeTypes
  tagsView: boolean
  tagsViewIcon: boolean
  fixedHeader: boolean
  footer: boolean
  logo: boolean
  hamburger: boolean
  breadcrumb: boolean
  breadcrumbIcon: boolean
  screenfull: boolean
  size: boolean
  locale: boolean
  uniqueOpened: boolean
  greyMode: boolean
  fixedMenu: boolean
  watermark: boolean
  watermarkTitle: string
}

export const useAppStore = defineStore('app', {
  state: (): AppState => {
    return {
      sizeMap: ['default', 'large', 'small'], // 尺寸选项
      currentSize: 'default', // 组件尺寸
      mobile: false, // 是否是移动端
      title: import.meta.env.VITE_APP_TITLE, // 标题
      pageLoading: true, // 路由跳转loading
      collapse: false, // 折叠菜单（是否折叠的状态）
      // =============== 在 “主题配置” 面板中拷贝的配置项，从这行往下 ↓ 拷贝 ===============
      // 主题相关设置项
      isDark: false, // 是否是暗黑模式
      layout: 'classic', // layout布局：'classic'=经典左右布局 | 'topLeft'=顶部左侧布局 | 'top'=顶部菜单布局 | 'cutMenu'=分栏菜单布局
      theme: {
        // 主题色
        elColorPrimary: '#409eff',
        // 左侧菜单边框颜色
        leftMenuBorderColor: '#eee',
        // 左侧菜单背景颜色
        leftMenuBgColor: '#ffffff',
        // 左侧菜单浅色背景颜色
        leftMenuBgLightColor: '#ffffff',
        // 左侧菜单选中背景颜色
        leftMenuBgActiveColor: 'RGBA(64,158,255,0.1)',
        // 左侧菜单收起选中背景颜色
        leftMenuCollapseBgActiveColor: 'RGBA(64,158,255,0.1)',
        // 左侧菜单字体颜色
        leftMenuTextColor: '#333',
        // 左侧菜单选中字体颜色
        leftMenuTextActiveColor: 'var(--el-color-primary)',
        // logo字体颜色
        logoTitleTextColor: 'var(--el-color-primary)',
        // logo边框颜色
        logoBorderColor: '#eee',
        // 头部背景颜色
        topHeaderBgColor: '#ffffff',
        // 头部字体颜色
        topHeaderTextColor: 'inherit',
        // 头部悬停颜色
        topHeaderHoverColor: '#f6f6f6',
        // 头部边框颜色
        topToolBorderColor: '#eee'
      },
      tagsView: true, // 标签页
      tagsViewIcon: true, // 是否显示标签图标
      fixedHeader: true, // 固定头部（头部+标签页）
      footer: true, // 显示页脚
      logo: true, // logo
      hamburger: true, // 折叠图标（是否显示折叠图标）
      breadcrumb: true, // 面包屑
      breadcrumbIcon: true, // 面包屑图标
      screenfull: true, // 全屏图标
      size: true, // 尺寸图标
      locale: true, // 多语言图标
      uniqueOpened: false, // 菜单手风琴（只展开一个子菜单）
      greyMode: false, // 是否开始灰色模式，用于特殊悼念日
      fixedMenu: false, // 是否固定菜单
      watermark: false, // 是否显示水印
      watermarkTitle: import.meta.env.VITE_APP_TITLE // 水印标题
      // =============== 在 “主题配置” 面板中拷贝的配置项，从这行往上 ↑ 拷贝 ===============
    }
  },
  getters: {
    getSizeMap(): ComponentSize[] { return this.sizeMap },
    getCurrentSize(): ComponentSize { return this.currentSize },
    getMobile(): boolean { return this.mobile },
    getTitle(): string { return this.title },
    getPageLoading(): boolean { return this.pageLoading },
    getCollapse(): boolean { return this.collapse },
    getIsDark(): boolean { return this.isDark },
    getLayout(): LayoutType { return this.layout },
    getTheme(): ThemeTypes { return this.theme },
    getTagsView(): boolean { return this.tagsView },
    getTagsViewIcon(): boolean { return this.tagsViewIcon },
    getFixedHeader(): boolean { return this.fixedHeader },
    getFooter(): boolean { return this.footer },
    getLogo(): boolean { return this.logo },
    getHamburger(): boolean { return this.hamburger },
    getBreadcrumb(): boolean { return this.breadcrumb },
    getBreadcrumbIcon(): boolean { return this.breadcrumbIcon },
    getScreenfull(): boolean { return this.screenfull },
    getSize(): boolean { return this.size },
    getLocale(): boolean { return this.locale },
    getUniqueOpened(): boolean { return this.uniqueOpened },
    getGreyMode(): boolean { return this.greyMode },
    getFixedMenu(): boolean { return this.fixedMenu },
    getWatermark(): boolean { return this.watermark },
    getWatermarkTitle(): string { return this.watermarkTitle }
  },
  actions: {
    setCurrentSize(currentSize: ComponentSize) { this.currentSize = currentSize },
    setMobile(mobile: boolean) { this.mobile = mobile },
    setTitle(title: string) { this.title = title },
    setPageLoading(pageLoading: boolean) { this.pageLoading = pageLoading },
    setCollapse(collapse: boolean) { this.collapse = collapse },
    setIsDark(isDark: boolean) {
      this.isDark = isDark
      if (this.isDark) {
        document.documentElement.classList.add('dark')
        document.documentElement.classList.remove('light')
      } else {
        document.documentElement.classList.add('light')
        document.documentElement.classList.remove('dark')
      }
      this.setPrimaryLight()
    },
    setLayout(layout: LayoutType) {
      if (this.mobile && layout !== 'classic') {
        ElMessage.warning('移动端模式下不支持切换其它布局')
        return
      }
      this.layout = layout
    },
    setTheme(theme: ThemeTypes) { this.theme = Object.assign(this.theme, theme) },
    setTagsView(tagsView: boolean) { this.tagsView = tagsView },
    setTagsViewIcon(tagsViewIcon: boolean) { this.tagsViewIcon = tagsViewIcon },
    setFixedHeader(fixedHeader: boolean) { this.fixedHeader = fixedHeader },
    setFooter(footer: boolean) { this.footer = footer },
    setLogo(logo: boolean) { this.logo = logo },
    setHamburger(hamburger: boolean) { this.hamburger = hamburger },
    setBreadcrumb(breadcrumb: boolean) { this.breadcrumb = breadcrumb },
    setBreadcrumbIcon(breadcrumbIcon: boolean) { this.breadcrumbIcon = breadcrumbIcon },
    setScreenfull(screenfull: boolean) { this.screenfull = screenfull },
    setSize(size: boolean) { this.size = size },
    setLocale(locale: boolean) { this.locale = locale },
    setUniqueOpened(uniqueOpened: boolean) { this.uniqueOpened = uniqueOpened },
    setGreyMode(greyMode: boolean) { this.greyMode = greyMode },
    setFixedMenu(fixedMenu: boolean) { this.fixedMenu = fixedMenu },
    setWatermark(watermark: boolean) { this.watermark = watermark },
    setWatermarkTitle(watermarkTitle: string) { this.watermarkTitle = watermarkTitle },
    setCssVarTheme() {
      for (const key in this.theme) {
        setCssVar(`--${humpToUnderline(key)}`, this.theme[key])
      }
      this.setPrimaryLight()
    },
    setPrimaryLight() {
      if (this.theme.elColorPrimary) {
        const elColorPrimary = this.theme.elColorPrimary
        const color = this.isDark ? '#000000' : '#ffffff'
        const lightList = [3, 5, 7, 8, 9]
        lightList.forEach((v) => {
          setCssVar(`--el-color-primary-light-${v}`, mix(color, elColorPrimary, v / 10))
        })
        setCssVar(`--el-color-primary-dark-2`, mix(color, elColorPrimary, 0.2))
      }
    },
    setMenuTheme(color: string) {
      const primaryColor = useCssVar('--el-color-primary', document.documentElement)
      const isDarkColor = colorIsDark(color)
      setCssVar(`--left-sub-menu-bg-color`, color.toLowerCase() === '#ffffff' ? '#00000006' : '#ffffff15')
      const theme: Recordable = {
        // 左侧菜单边框颜色
        leftMenuBorderColor: isDarkColor ? 'inherit' : '#eee',
        // 左侧菜单背景颜色
        leftMenuBgColor: color,
        // 左侧菜单浅色背景颜色
        leftMenuBgLightColor: isDarkColor ? lighten(color!, 6) : color,
        // 左侧菜单选中背景颜色
        leftMenuBgActiveColor: isDarkColor ? 'var(--el-color-primary)' : hexToRGB(unref(primaryColor) as string, 0.1),
        // 左侧菜单收起选中背景颜色
        leftMenuCollapseBgActiveColor: isDarkColor ? 'var(--el-color-primary)' : hexToRGB(unref(primaryColor) as string, 0.1),
        // 左侧菜单字体颜色
        leftMenuTextColor: isDarkColor ? '#dedede' : '#333',
        // 左侧菜单选中字体颜色
        leftMenuTextActiveColor: isDarkColor ? '#ffffff' : 'var(--el-color-primary)',
        // logo字体颜色
        logoTitleTextColor: isDarkColor ? '#ffffff' : 'var(--el-color-primary)',
        // logo边框颜色
        logoBorderColor: isDarkColor ? color : '#eee'
      }
      this.setTheme(theme)
      this.setCssVarTheme()
    },
    setHeaderTheme(color: string) {
      const isDarkColor = colorIsDark(color)
      const textColor = isDarkColor ? '#fff' : 'inherit'
      const textHoverColor = isDarkColor ? lighten(color!, 6) : '#f6f6f6'
      const topToolBorderColor = isDarkColor ? color : '#eee'
      setCssVar('--top-header-bg-color', color)
      setCssVar('--top-header-text-color', textColor)
      setCssVar('--top-header-hover-color', textHoverColor)
      this.setTheme({
        topHeaderBgColor: color,
        topHeaderTextColor: textColor,
        topHeaderHoverColor: textHoverColor,
        topToolBorderColor
      })
      if (this.getLayout === 'top') {
        this.setMenuTheme(color)
      }
    },
    initTheme() {
      const isDark = useDark({valueDark: 'dark', valueLight: 'light'})
      isDark.value = this.getIsDark
      const newTitle = import.meta.env.VITE_APP_TITLE
      newTitle !== this.getTitle && this.setTitle(newTitle)
    },
    loadTheme(themeObj: any) {
      this.setIsDark(themeObj.isDark)
      this.setLayout(themeObj.layout)
      this.setTheme(themeObj.theme)
      this.setTagsView(themeObj.tagsView)
      this.setTagsViewIcon(themeObj.tagsViewIcon)
      this.setFixedHeader(themeObj.fixedHeader)
      this.setFooter(themeObj.footer)
      this.setLogo(themeObj.logo)
      this.setHamburger(themeObj.hamburger)
      this.setBreadcrumb(themeObj.breadcrumb)
      this.setBreadcrumbIcon(themeObj.breadcrumbIcon)
      this.setScreenfull(themeObj.screenfull)
      this.setSize(themeObj.size)
      this.setLocale(themeObj.locale)
      this.setUniqueOpened(themeObj.uniqueOpened)
      this.setGreyMode(themeObj.greyMode)
      this.setFixedMenu(themeObj.fixedMenu)
      this.setWatermark(themeObj.watermark)
      this.setWatermarkTitle(themeObj.watermarkTitle)
    }
  },
  persist: {key: storageKeys.l_themeSetting}
})

export const useAppStoreWithOut = () => {
  return useAppStore(store)
}
