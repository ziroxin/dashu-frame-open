import request from '@/utils/request'

// 查询消息数量数据
export function getMessageCount() {
  return request({
    url: '/message/zMessage/counts',
    method: 'get'
  })
}