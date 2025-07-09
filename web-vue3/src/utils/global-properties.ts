import type { App } from 'vue'
import storageKeys from './storage-keys'

/**
 * 注册全局属性/全局变量
 * 使用方法：
 *     1. 在 html 中使用方法： <div :label="key">{{ key }}</div>
 *     2. 在 setup 中使用方法： inject('key')
 * @param app
 */
export const setupGlobalProperties = (app: App<Element>) => {
  // 全局变量1：api调用根路径
  setGlobalPropertie(app, '$baseServer', import.meta.env.VITE_API_BASE_PATH)
  // 全局变量2：浏览器cookie/storage的key同一管理
  setGlobalPropertie(app, '$storageKeys', storageKeys)
}

const setGlobalPropertie = (app: App<Element>, key: string, value: any) => {
  // 1. 在html中使用，例如：<div :label="$baseServer">{{ $baseServer }}</div>
  app.config.globalProperties[key] = value
  // 2. 在setup中使用，例如：inject('$baseServer')
  app.provide(key, value)
}