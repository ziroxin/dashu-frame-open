import { myGlobalProperties } from '@/utils/global-properties'

/**
 * 全局属性使用说明，举例：
 *     useMyGP().gp.$baseServer
 *     useMyGP().gp.$storageKeys
 *
 * 说明：全局属性是在 /utils/global-properties.ts 中 setupGlobalProperties 导入的
 */
export const useMyGP = () => {
  const gp = myGlobalProperties
  return {gp}
}
