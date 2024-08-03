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
        :style="{height: this.height, overflowY: 'hidden'}"
        :default-config="editorConfig"
        :mode="mode"
        @onCreated="onCreated"
    />
  </div>
</template>
<script>
import {Editor, Toolbar} from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css'
import {
  customParseImageSrc,
  customParseVideoSrc,
  imagesDefaultOptions,
  videosDefaultOptions
} from '@/components/MyWangEditor/myWangEditorConfig';

export default {
  name: 'MyWangEditor',
  components: {Editor, Toolbar},
  props: {
    // 双向绑定值
    value: {type: String, default: ''},
    // 空白提示
    placeholder: {type: String, default: '请输入...'},
    // 上传图片地址
    imageServer: {type: String, default: process.env.VUE_APP_BASE_API + '/upload/wang/images'},
    // 上传图片大小限制（默认：2MB，2*1024*1024）
    imageSizeLimit: {type: Number, default: 2 * 1024 * 1024},
    // 上传视频地址
    videoServer: {type: String, default: process.env.VUE_APP_BASE_API + '/upload/wang/videos'},
    // 上传视频大小限制（默认：50MB，2*1024*1024）
    videoSizeLimit: {type: Number, default: 50 * 1024 * 1024},
    // 更多配置 @see:https://www.wangeditor.com/v5/toolbar-config.html#toolbarkeys
    // 工具栏配置 例如：['bold', 'underline', 'italic']
    // 下方onCreated()方法中，可以获取全部key
    toolbarKeys: {type: Array, default: () => []},
    // 编辑器内容区高度
    height: {type: String, default: '400px'},
  },
  data() {
    return {
      editor: null,
      html: this.value,
      toolbarConfig: (this.toolbarKeys && this.toolbarKeys.length > 0) ? {toolbarKeys: this.toolbarKeys} : {},
      editorConfig: {
        placeholder: this.placeholder,
        // 菜单配置
        MENU_CONF: {
          // 图片上传配置
          uploadImage: imagesDefaultOptions(this.imageServer, this.imageSizeLimit, this.$message),
          // 图片地址
          insertImage: {parseImageSrc: customParseImageSrc},
          // 视频上传配置
          uploadVideo: videosDefaultOptions(this.videoServer, this.videoSizeLimit, this.$message),
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
      },
      mode: 'default' // or 'simple'
    }
  },
  watch: {
    html() {
      this.$emit('input', this.html)
    },
    value() {
      this.html = this.value ? this.value : '' // 防止value为空时报错
    }
  },
  beforeDestroy() {
    const editor = this.editor
    if (editor === null) return
    editor.destroy() // 组件销毁时，及时销毁编辑器
  },
  methods: {
    onCreated(editor) {
      // 一定要用 Object.seal() ，否则会报错
      // Object.seal() 封闭对象，不能改变对象的属性字段，但能改变属性值
      this.editor = Object.seal(editor)
      // 获取全部 toolbarKeys
      // console.log(this.editor.getAllMenuKeys())
    }
  }
}
</script>

<style scoped>

</style>
