declare global {
  // 定义一个泛型接口Fn，用于描述一个函数，该函数可以接受任意数量的参数，并返回与参数类型相同的值
  declare interface Fn<T = any> {
    (...arg: T[]): T
  }

  // 定义一个泛型类型Recordable，它基于内置的Record类型，但允许键的类型默认为string，或者在指定时可以为null或undefined
  declare type Recordable<T = any, K = string> = Record<K extends null | undefined ? string : K, T>

  // 定义一个泛型类型ComponentRef，它用于获取传入组件构造函数（T）的实例类型
  declare type ComponentRef<T> = InstanceType<T>

  // 定义局部化类型LocaleType，表示支持的语言环境，当前仅支持中文（zh-CN）和英文（en）
  declare type LocaleType = 'zh-CN' | 'en'

  // 定义布局类型LayoutType，列举了几种可能的布局选项，例如经典布局（classic）、顶部左侧布局（topLeft）、顶部布局（top）和分栏布局（cutMenu）
  declare type LayoutType = 'classic' | 'topLeft' | 'top' | 'cutMenu'

  // 定义主题类型ThemeTypes，包含了一组可选的字符串属性，用于配置应用程序的主题样式，包括但不限于主色调、菜单边框颜色、背景颜色、文本颜色等
  declare interface ThemeTypes {
    elColorPrimary?: string
    leftMenuBorderColor?: string
    leftMenuBgColor?: string
    leftMenuBgLightColor?: string
    leftMenuBgActiveColor?: string
    leftMenuCollapseBgActiveColor?: string
    leftMenuTextColor?: string
    leftMenuTextActiveColor?: string
    logoTitleTextColor?: string
    logoBorderColor?: string
    topHeaderBgColor?: string
    topHeaderTextColor?: string
    topHeaderHoverColor?: string
    topToolBorderColor?: string
  }

  // 定义ImportMetaEnv接口，用于描述在Vite构建的应用程序中，可以通过import.meta.env访问的环境变量。这些环境变量通常是自定义的VITE_前缀的变量，在Vite配置文件中定义
  declare interface ImportMetaEnv {
    readonly VITE_NODE_ENV: string
    readonly VITE_APP_TITLE: string
    readonly VITE_APP_KEY_PREFIX: string
    readonly VITE_API_BASE_PATH: string
    readonly VITE_BASE_PATH: string
    readonly VITE_DROP_DEBUGGER: string
    readonly VITE_DROP_CONSOLE: string
    readonly VITE_SOURCEMAP: string
    readonly VITE_OUT_DIR: string
    readonly VITE_USE_BUNDLE_ANALYZER: string
    readonly VITE_USE_ALL_ELEMENT_PLUS_STYLE: string
    readonly VITE_USE_CSS_SPLIT: string
    readonly VITE_USE_ONLINE_ICON: string
    readonly VITE_ICON_PREFIX: string
    readonly VITE_HIDE_GLOBAL_SETTING: string
  }
}
