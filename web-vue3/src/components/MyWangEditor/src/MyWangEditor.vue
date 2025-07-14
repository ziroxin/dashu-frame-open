<!--
* 富文本 WangEditor 组件
* 参数说明：
    modelValue: 富文本内容，通过v-model双向绑定
    placeholder: 空白提示，默认：'请输入...'
    imageServer: 上传图片api地址，默认：/upload/wang/images
    imageSizeLimit: 上传图片大小限制，默认：2MB
    videoServer: 上传视频地址，默认：/upload/wang/videos
    videoSizeLimit: 上传视频大小限制，默认：50MB
    toolbarKeys: 工具栏配置，例如：['bold', 'underline', 'italic']
    height: 编辑器内容区高度，默认：'400px'

* 注意事项：
    若放在新增、编辑弹窗中使用，建议增加 :key="dialogFormVisible"，每次打开弹窗时重载编辑器，否则会有未知异常
    例如：<my-wang-editor v-model="temp.content" :key="dialogFormVisible" />

* 更多配置：@see https://www.wangeditor.com/v5/toolbar-config.html#toolbarkeys
* @Author: ziro
* @Date: 2025/04/26 15:40:52
-->
<template>
  <div style="border: 1px solid #ccc;z-index: 9999;">
    <Toolbar
        style="border-bottom: 1px solid #ccc"
        :editor="editor"
        :default-config="toolbarConfig"
        :mode="mode"
    />
    <Editor
        v-model="html"
        :style="{height: height, overflowY: 'hidden'}"
        :default-config="editorConfig"
        :mode="mode"
        @onCreated="handleEditorCreated"
    />
  </div>
</template>

<script setup>
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css'
import { customParseImageSrc, customParseVideoSrc, imagesOptions, videosOptions } from './myWangEditorConfig.ts'
import { useMyGP } from '@/hooks/web/useMyGlobalProperties'

// 定义 props
const props = defineProps({
  // 双向绑定值
  modelValue: {type: String, default: ''},
  // 空白提示
  placeholder: {type: String, default: '请输入...'},
  // 上传图片地址
  imageServer: {type: String, default: useMyGP().gp.$baseServer + '/upload/wang/images'},
  // 上传图片大小限制（默认：2MB，2*1024*1024）
  imageSizeLimit: {type: Number, default: 2 * 1024 * 1024},
  // 上传视频地址
  videoServer: {type: String, default: useMyGP().gp.$baseServer + '/upload/wang/videos'},
  // 上传视频大小限制（默认：50MB，2*1024*1024）
  videoSizeLimit: {type: Number, default: 50 * 1024 * 1024},
  // 工具栏配置 例如：['bold', 'underline', 'italic']
  toolbarKeys: {type: Array, default: () => []},
  // 编辑器内容区高度
  height: {type: String, default: '400px'}
})

// 解码html
const jsDecodeHtml = (val) => {
  if (/&[a-z]+;|&#\d+;|&#x[a-f0-9]+;/i.test(val)) {
    const tempDiv = document.createElement('div')
    tempDiv.innerHTML = val
    return tempDiv.textContent || tempDiv.innerText || ''
  } else {
    return val
  }
}

// 定义数据
const editor = ref(null)
const html = ref(jsDecodeHtml(props.modelValue))
const toolbarConfig = ref((props.toolbarKeys && props.toolbarKeys.length > 0) ? {toolbarKeys: props.toolbarKeys} : {})
const editorConfig = ref({
  placeholder: props.placeholder,
  // 菜单配置
  MENU_CONF: {
    // 图片上传配置
    uploadImage: imagesOptions(props.imageServer, props.imageSizeLimit),
    // 图片地址
    insertImage: {parseImageSrc: customParseImageSrc},
    // 视频上传配置
    uploadVideo: videosOptions(props.videoServer, props.videoSizeLimit),
    // 视频地址
    insertVideo: {parseVideoSrc: customParseVideoSrc},
    // 代码块语言
    codeSelectLang: {
      codeLangs: [
        {text: 'JAVA', value: 'java'},
        {text: 'HTML', value: 'html'},
        {text: 'JS', value: 'javascript'},
        {text: 'CSS', value: 'css'}
      ]
    }
  }
})
const mode = ref('default') // or 'simple'

// 定义 emits
const emit = defineEmits(['update:modelValue'])
// 监听 html 变化
watch(html, (newHtml) => { emit('update:modelValue', newHtml) })
// 监听 value 变化（防止value为空时报错）
watch(() => props.modelValue, (newValue) => { html.value = newValue ? newValue : '' })

// 处理编辑器创建
const handleEditorCreated = (editorInstance) => {
  // 一定要用 Object.seal() ，否则会报错
  // Object.seal() 封闭对象，不能改变对象的属性字段，但能改变属性值
  editor.value = Object.seal(editorInstance)
  // 获取全部 toolbarKeys
  // console.log(editor.value.getAllMenuKeys())
}

// 组件销毁时，及时销毁编辑器
onBeforeUnmount(() => {
  const editorInstance = editor.value
  if (editorInstance) {
    editorInstance.destroy()
  }
})
</script>