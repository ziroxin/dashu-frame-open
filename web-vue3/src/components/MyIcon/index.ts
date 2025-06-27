import MyIcon from './src/MyIcon.vue'

export { MyIcon }

/** 定义图标参数规则 */
export type IconTypes = {
  icon: string
  color?: string
  size?: number
  hoverColor?: string
}