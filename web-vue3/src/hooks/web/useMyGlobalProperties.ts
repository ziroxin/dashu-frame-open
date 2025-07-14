import { myGlobalProperties } from '@/utils/global-properties'

/**
 * 使用全局属性
 * 例如：useMyGP().gp.$baseServer
 */
export const useMyGP = () => {
  const gp = myGlobalProperties

  return {
    gp
  }
}
