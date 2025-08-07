import { Config, driver } from 'driver.js'
import 'driver.js/dist/driver.css'
import { useDesign } from '@/hooks/web/useDesign'
import { useI18n } from '@/hooks/web/useI18n'

const {variables} = useDesign()
const {t} = useI18n()

// 初始化driver.js，设置按钮文字（更多配置看文档 @link https://driverjs.com/docs/installation）
export const useGuide = (options?: Config) => {
  const driverObj = driver(
    options || {
      showProgress: true,// 显示进度
      nextBtnText: t('guide.nextGuideBtn'), // 下一步按钮
      prevBtnText: t('guide.prevGuideBtn'), // 上一步按钮
      doneBtnText: t('guide.doneGuideBtn'), // 结束按钮
      steps: [
        {
          element: `#${variables.namespace}-logo`,
          popover: {
            title: t('guide.logoStepTitle'),
            description: t('guide.logoStepDesc'),
            side: 'right'
          }
        }, {
          element: `#${variables.namespace}-menu`,
          popover: {
            title: t('guide.menuStepTitle'),
            description: t('guide.menuStepDesc'),
            side: 'right'
          }
        }, {
          element: `#${variables.namespace}-tool-header`,
          popover: {
            title: t('guide.toolStepTitle'),
            description: t('guide.toolStepDesc'),
            side: 'left'
          }
        }, {
          element: `#${variables.namespace}-my-breadcrumb`,
          popover: {
            title: t('guide.breadcrumbStepTitle'),
            description: t('guide.breadcrumbStepDesc'),
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
            title: t('guide.tagsViewStepTitle'),
            description: t('guide.tagsViewStepDesc'),
            side: 'bottom'
          }
        }, {
          element: `#${variables.namespace}-app-container`,
          popover: {
            title: t('guide.appContainerStepTitle'),
            description: t('guide.appContainerStepDesc'),
            side: 'top'
          }
        }
      ]
    }
  )

  return {...driverObj}
}
