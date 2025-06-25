import type { App } from 'vue'
import { Icon } from './Icon'
import { Permission } from './Permission'
import { BaseButton } from './Button'

/**
 * 常用自定义组件全局引入，无需在每个vue文件中单独引入
 * @param app Vue实例
 */
export const setupGlobCom = (app: App<Element>): void => {
  app.component('Icon', Icon)
  app.component('Permission', Permission)
  app.component('BaseButton', BaseButton)
}
