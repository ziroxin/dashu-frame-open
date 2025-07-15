import type { App } from 'vue'
import { MyIcon } from './MyIcon'
import { Permission } from './Permission'
import { BaseButton } from './BaseButton'

/**
 * 常用自定义组件全局引入，无需在每个vue文件中单独引入
 * @param app Vue实例
 */
export const setupGlobCom = (app: App<Element>): void => {
  app.component('MyIcon', MyIcon)
  app.component('Permission', Permission)
  app.component('BaseButton', BaseButton)
}
