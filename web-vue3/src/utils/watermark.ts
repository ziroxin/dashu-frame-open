/**
 * 水印工具类
 */
import { useAppStoreWithOut } from '@/store/modules/app'
import { useWatermark } from '@/hooks/web/useWatermark'

/**
 * 初始化水印数据
 */
export const setupWatermark = () => {
  const appStoreWithOut = useAppStoreWithOut()
  const watermark = appStoreWithOut.getWatermark
  if (watermark) {
    // 开启水印
    const watermarkTitle = appStoreWithOut.getWatermarkTitle
    const {setWatermark} = useWatermark()
    setWatermark(watermarkTitle)
  } else {
    // 关闭水印
    const {clearWatermark} = useWatermark()
    clearWatermark()
  }
}