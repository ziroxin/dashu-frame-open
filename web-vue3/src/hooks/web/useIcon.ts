import type { VNode } from 'vue'
import { h } from 'vue'
import { IconTypes, MyIcon } from '@/components/MyIcon'

/**
 * 传入参数，返回一个渲染好的图标组件
 * @param props 图标相关参数
 */
export const useIcon = (props: IconTypes): VNode => {
  return h(MyIcon, props)
}
