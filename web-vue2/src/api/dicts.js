import request from "@/utils/request";
import {Message} from 'element-ui'

/**
 * 获取数据字典
 */
export function getDictData(typeCode) {
  return request({
    url: '/dictData/zDictData/listCache',
    method: 'get',
    params: {typeCode: typeCode}
  })
}

/**
 * 清空缓存数据
 */
export function clearDictCache(typeCode) {
  request({
    url: '/dictData/zDictData/clearCache',
    method: 'get',
    params: {typeCode: typeCode}
  }).then(() => {
    Message({message: '清空缓存数据成功！', type: 'success', duration: 3 * 1000})
  })
}
