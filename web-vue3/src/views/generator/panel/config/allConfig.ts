import inputConfig from './inputConfig'
import selectConfig from './selectConfig'
import diyConfig from './diyConfig'
import otherConfig from './otherConfig'

// 导出组件配置
export default [
  {name: '原生组件', icon: 'el-icon-edit', list: inputConfig},
  {name: '选择组件', icon: 'el-icon-news', list: selectConfig},
  {name: '自定义组件', icon: 'el-icon-menu', list: diyConfig},
  {name: '其他组件', icon: 'el-icon-set-up', list: otherConfig}
]