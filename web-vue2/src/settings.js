module.exports = {
  title: '大树快速开发平台',

  /**
   * @type {boolean} true | false
   * @description 是否显示右侧面板（设置）
   */
  showSettings: true,

  /**
   * @type {boolean} true | false
   * @description 是否显示标签页
   */
  tagsView: false,

  /**
   * 布局：leftMenu:左侧菜单布局;topMenu:顶部菜单布局;topLeftMenu:综合布局;
   */
  layout: 'leftMenu',

  /**
   * @type {boolean} true | false
   * @description 是否固定头部
   */
  fixedHeader: false,

  /**
   * @type {boolean} true | false
   * @description 是否显示左侧logo
   */
  sidebarLogo: false,

  /**
   * @type {string} 默认:'#4080FF'
   * @description 主题颜色
   */
  theme: '#4080FF',

  /**
   * @type {string | array} 'production' | ['production', 'development']
   * @description 错误日志组件
   * 默认只在生产环境使用
   * 如果想在开发环境使用，可以配置成：['production', 'development']
   */
  errorLog: 'production'
}
