import type { App } from 'vue'
import clipboard from '@/directives/clipboard'
import permission from '@/directives/permission'
import { ClickOutside } from 'element-plus'

/**
 * 注册全局指令 v-xxx
 * @param app Vue实例
 */
export const setupDirectives = (app: App<Element>) => {
  // permission 按钮权限，用法: v-permission
  app.directive('permission', permission)
  // click-outside 点击外部区域指令
  app.directive('click-outside', ClickOutside)
  // clipboard 剪切板指令
  app.directive('clipboard', clipboard)
}