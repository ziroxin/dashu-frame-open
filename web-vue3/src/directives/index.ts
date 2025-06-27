import type { App } from 'vue'
import permission from "@/directives/permission";

/**
 * 注册全局指令 v-xxx
 * @param app Vue实例
 */
export const setupDirectives = (app: App<Element>) => {
  // permission 按钮权限，用法: v-permission
  app.directive('permission', permission)
}