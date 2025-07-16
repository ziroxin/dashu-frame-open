/**
 * 数据字典工具类
 * 提供2个方法：
 *            1. clearDictList() 清除数据字典缓存
 *            2. getDict(code) 根据[code]获取数据字典
 */
import request from '@/utils/request'
import storageKeys from '@/utils/storage-keys'

/** 数据字典列表 */
let dictList: any[] = JSON.parse(localStorage.getItem(storageKeys.l_dictList) as string) || []

/**
 * 清除数据字典缓存
 */
export function clearDictList() {
  dictList = []
  localStorage.removeItem(storageKeys.l_dictList)
}

/**
 * 获取数据字典
 * @param code 字典类型code
 */
export function getDict(code: string): Promise<any> {
  const dict = dictList.filter(item => item.code === code)
  if (dict.length > 0) {
    return Promise.resolve(dict[0])
  } else {
    return new Promise((resolve, reject) => {
      request({url: '/dictData/zDictData/listCache', method: 'get', params: {typeCode: code}})
        .then((res) => {
          setDict(code, res.data)
          resolve(res.data)
        })
        .catch((err) => {
          console.log('获取字典数据失败，', err)
          reject(err)
        })
    })
  }
}


/**
 * 设置数据字典（并缓存到本地）
 * @param code 字典类型code
 * @param list 字典数据列表
 */
function setDict(code: string, list: any): void {
  if (code && list) {
    // 更新数据字典
    const dict = dictList.find(item => item.code === code)
    if (dict) {
      dict.list = list
    } else {
      dictList.push({code: code, list: list})
    }
    // 存储数据字典
    localStorage.setItem(storageKeys.l_dictList, JSON.stringify(dictList))
  }
}


