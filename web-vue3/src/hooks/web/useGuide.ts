import { Config, driver } from 'driver.js'
import 'driver.js/dist/driver.css'
import { useDesign } from '@/hooks/web/useDesign'
import { useI18n } from '@/hooks/web/useI18n'

const {t} = useI18n()
const {variables} = useDesign()

// 初始化driver.js，设置按钮文字（更多配置看文档 @link https://driverjs.com/docs/installation）
export const useGuide = (options?: Config) => {
  const driverObj = driver(
    options || {
      showProgress: true,// 显示进度
      nextBtnText: t('common.nextLabel'), // 上一步按钮
      prevBtnText: t('common.prevLabel'), // 下一步按钮
      doneBtnText: t('common.doneLabel'), // 结束按钮
      steps: [
        {
          element: `#${variables.namespace}-logo`,
          popover: {
            title: 'Logo',
            description: '用于显示系统Logo',
            side: 'right'
          }
        }, {
          element: `#${variables.namespace}-menu`,
          popover: {
            title: t('common.menu'),
            description: t('common.menuDes'),
            side: 'right'
          }
        }, {
          element: `#${variables.namespace}-tool-header`,
          popover: {
            title: t('common.tool'),
            description: t('common.toolDes'),
            side: 'left'
          }
        }, {
          element: `#${variables.namespace}-breadcrumb`,
          popover: {
            title: t('common.breadcrumb'),
            description: t('common.breadcrumbDes'),
            side: 'bottom'
          }
        }, {
          element: `#${variables.namespace}-screenFull`,
          popover: {
            title: '全屏',
            description: '用于切换全屏显示',
            side: 'left'
          }
        }, {
          element: `#${variables.namespace}-sizeSelect`,
          popover: {
            title: '文字大小',
            description: '用于切换页面文字大小',
            side: 'left'
          }
        }, {
          element: `#${variables.namespace}-langSelect`,
          popover: {
            title: '语言选择',
            description: '用于切换语言',
            side: 'left'
          }
        }, {
          element: `#${variables.namespace}-userInfo`,
          popover: {
            title: '用户信息',
            description: '用于显示用户信息，点击下拉菜单，显示更多操作',
            side: 'left'
          }
        }, {
          element: `#${variables.namespace}-tags-view`,
          popover: {
            title: t('common.tagsView'),
            description: t('common.tagsViewDes'),
            side: 'bottom'
          }
        }, {
          element: `#${variables.namespace}-app-container`,
          popover: {
            title: '主内容区',
            description: '页面内容显示区域',
            side: 'top'
          }
        }
      ]
    }
  )

  return {
    ...driverObj
  }
}
