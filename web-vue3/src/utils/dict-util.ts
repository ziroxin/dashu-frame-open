/**
 * 数据字典工具类
 */
import request from '@/utils/request'
import storageKeys from '@/utils/storage-keys'
import { getToken } from '@/utils/auth'
import { ElMessage } from 'element-plus'

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
  if (!dictList || dictList.length <= 0) {
    setupDictList()
  }
  const dict = dictList.filter(item => item.typeCode === code)
  return dict.length > 0 ? dict[0].children : []
}

/**
 * 清除数据字典缓存
 */
export function clearDictCache() {
  // 清除数据字典缓存
  dictList = []
  localStorage.removeItem(storageKeys.l_dictList)
  // 清除服务器端字典缓存
  request({url: '/dictData/zDictData/clearCache', method: 'get'}).then(() => {
    // 重新初始化数据字典
    setupDictList()
    ElMessage({message: '更新缓存数据成功！', type: 'success', duration: 3 * 1000})
  })
}

