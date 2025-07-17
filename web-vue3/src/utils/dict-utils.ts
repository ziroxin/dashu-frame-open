/**
 * 数据字典工具类
 * 提供2个方法：
 *            1. clearDictList() 清除数据字典缓存
 *            2. getDict(code) 根据[code]获取数据字典
 */
import request from '@/utils/request'
import storageKeys from '@/utils/storage-keys'
import { getToken } from '@/utils/auth'

/** 数据字典列表 */
let dictList: any[]

/**
 * 初始化数据字典列表
 */
export const setupDictList = () => {
  if (!dictList || dictList.length <= 0) {
    const obj = localStorage.getItem(storageKeys.l_dictList)
    if (obj) {
      dictList = JSON.parse(obj)
    } else {
      if (getToken()) {
        // 加载全部数据字典
        request({url: '/dictType/zDictType/listTreeCache', method: 'get'}).then(res => {
          dictList = res.data
          localStorage.setItem(storageKeys.l_dictList, JSON.stringify(res.data))
        }).catch(err => { console.error('加载数据字典列表出错:', err) })
      }
    }
  }
}

/**
 * 获取数据字典
 * @param code 字典类型code
 */
export function getDict(code: string) {
  const dict = dictList.filter(item => item.typeCode === code)
  return dict.length > 0 ? dict[0].children : []
}

/**
 * 清除数据字典缓存
 */
export function clearDictList() {
  dictList = []
  localStorage.removeItem(storageKeys.l_dictList)
}

