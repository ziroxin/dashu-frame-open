<template>
  <!-- 图片裁剪组件 -->
  <div :class="prefixCls" class="flex justify-center items-center">
    <div>
      <div class="flex justify-center items-center w-400px h-300px">
        <img v-show="imageUrl" ref="imgRef" :src="imageUrl" class="block max-w-full" crossorigin="anonymous"/>
      </div>
      <div class="mt-10px flex items-center">
        <div class="flex items-center justify-between flex-1">
          <el-tooltip content="重置" placement="bottom">
            <base-button size="small" type="primary" @click="reset" icon="vi-ep:refresh"/>
          </el-tooltip>
          <el-tooltip content="逆时针旋转15°" placement="bottom">
            <base-button size="small" type="primary" @click="rotate(-15)" icon="vi-ant-design:rotate-left-outlined"/>
          </el-tooltip>
          <el-tooltip content="顺时针旋转15°" placement="bottom">
            <base-button size="small" type="primary" @click="rotate(15)" icon="vi-ant-design:rotate-right-outlined"/>
          </el-tooltip>
          <el-tooltip content="水平翻转" placement="bottom">
            <base-button size="small" type="primary" @click="scale('scaleX')" icon="vi-vaadin:arrows-long-h"/>
          </el-tooltip>
          <el-tooltip content="垂直翻转" placement="bottom">
            <base-button size="small" type="primary" @click="scale('scaleY')" icon="vi-vaadin:arrows-long-v"/>
          </el-tooltip>
          <el-tooltip content="放大" placement="bottom">
            <base-button size="small" type="primary" @click="zoom(0.1)" icon="vi-ant-design:zoom-in-outlined"/>
          </el-tooltip>
          <el-tooltip content="缩小" placement="bottom">
            <base-button size="small" type="primary" @click="zoom(-0.1)" icon="vi-ant-design:zoom-out-outlined"/>
          </el-tooltip>
        </div>
      </div>
    </div>
    <div v-if="imgBase64" class="ml-20px px-20px py-6px b-1px b-dashed b-#ccc">
      <div class="flex justify-center items-center">
        <img :src="imgBase64" class="rounded-[50%]" :style="{width:cropBoxWidth+'px',height:cropBoxHeight+'px'}"/>
      </div>
      <el-divider/>
      <div class="flex justify-center items-center">
        <img :src="imgBase64" class="rounded-[50%]" :style="getScaleSize(0.2)"/>
        <img :src="imgBase64" class="rounded-[50%] ml-20px" :style="getScaleSize(0.25)"/>
        <img :src="imgBase64" class="rounded-[50%] ml-20px" :style="getScaleSize(0.3)"/>
        <img :src="imgBase64" class="rounded-[50%] ml-20px" :style="getScaleSize(0.35)"/>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import Cropper from 'cropperjs'
import 'cropperjs/dist/cropper.min.css'
import { useDesign } from '@/hooks/web/useDesign'
import { useDebounceFn } from '@vueuse/core'

const prefixCls = useDesign().getPrefixCls('image-cropping')

// 传入参数
const props = defineProps({
  // 图片地址
  imageUrl: {type: String, default: '', required: true},
  // 裁剪框宽度
  cropBoxWidth: {type: Number, default: 200},
  // 裁剪框高度
  cropBoxHeight: {type: Number, default: 200}
})

// 图片裁剪方法
const getBase64 = useDebounceFn(() => { imgBase64.value = unref(cropperRef)?.getCroppedCanvas()?.toDataURL() ?? ''}, 80)
// 裁切后图片的base64
const imgBase64 = ref('')
// 重置方法
const resetCropBox = () => {
  const containerData = unref(cropperRef)?.getContainerData()
  unref(cropperRef)?.setCropBoxData({
    width: props.cropBoxWidth,
    height: props.cropBoxHeight,
    left: (containerData?.width || 0) / 2 - 100,
    top: (containerData?.height || 0) / 2 - 100
  })
  imgBase64.value = unref(cropperRef)?.getCroppedCanvas()?.toDataURL() ?? ''
}
// 获取对应的缩小倍数的宽高
const getScaleSize = (scale: number) => {
  return {
    width: props.cropBoxWidth * scale + 'px',
    height: props.cropBoxHeight * scale + 'px'
  }
}

// 初始化裁切组件
const imgRef = ref<HTMLImageElement>()
const cropperRef = ref<Cropper>()
const intiCropper = () => {
  if (!unref(imgRef)) return
  cropperRef.value = new Cropper(unref(imgRef)!, {
    aspectRatio: 1,
    viewMode: 1,
    dragMode: 'move',
    // cropBoxResizable: false,
    // cropBoxMovable: false,
    toggleDragModeOnDblclick: false,
    checkCrossOrigin: false,
    ready() { resetCropBox() },
    cropmove() { getBase64() },
    zoom() { getBase64() },
    crop() { getBase64() }
  })
}
onMounted(() => { intiCropper() })
watch(() => props.imageUrl, async (url) => {
  if (url) {
    unref(cropperRef)?.replace(url)
    await nextTick()
    resetCropBox()
  }
})
onBeforeUnmount(() => { unref(cropperRef)?.destroy() })

// 重置
const reset = () => { unref(cropperRef)?.reset()}
// 旋转
const rotate = (deg: number) => { unref(cropperRef)?.rotate(deg)}
// 翻转
const scaleX = ref(1)
const scaleY = ref(1)
const scale = (type: 'scaleX' | 'scaleY') => {
  if (type === 'scaleX') {
    scaleX.value = scaleX.value === 1 ? -1 : 1
    unref(cropperRef)?.[type](unref(scaleX))
  } else {
    scaleY.value = scaleY.value === 1 ? -1 : 1
    unref(cropperRef)?.[type](unref(scaleY))
  }
}
// 缩放
const zoom = (num: number) => {
  unref(cropperRef)?.zoom(num)
}
</script>
