export default [
  {
    __key: 'my-wang-editor',
    __docLink: 'https://www.wangeditor.com/v5/getting-started.html',
    __name: '富文本框',
    __icon: 'form-rich-text',
    __span: 24,
    __formItemAttrs: {
      label: '内容',
      rules: [{required: true, message: '内容不能为空'}]
    },
    __attrs: {
      height: '400px',
      placeholder: '请输入内容'
    }
  },
  {
    __key: 'image-avatar',
    __name: '上传头像',
    __icon: 'form-upload-avatar',
    __span: 24,
    __formItemAttrs: {
      label: '头像',
      rules: [{required: true, message: '请上传头像'}]
    },
    __attrs: {
      limitSize: 1024 * 1024 * 10
    }
  },
  {
    __key: 'image-one',
    __name: '单图上传',
    __icon: 'form-upload-img1',
    __span: 24,
    __formItemAttrs: {
      label: '单图上传',
      rules: [{required: true, message: '请上传图片'}]
    },
    __attrs: {
      limitSize: 1024 * 1024 * 10
    }
  },
  {
    __key: 'image-upload',
    __name: '多图上传',
    __icon: 'form-upload-img2',
    __span: 24,
    __formItemAttrs: {
      label: '多图上传',
      rules: [{required: true, message: '请上传图片'}]
    },
    __attrs: {
      multiple: true,
      limitSize: 1024 * 1024 * 10,
      limitCount: 0
    }
  },
  {
    __key: 'file-upload',
    __name: '文件上传',
    __icon: 'form-upload-file',
    __span: 24,
    __formItemAttrs: {
      label: '文件上传',
      rules: [{required: true, message: '请上传文件'}]
    },
    __attrs: {
      accept: '.jpg,.png,.jpeg,.gif,.doc,.docx,.xls,.xlsx,.pdf,.zip,.rar',
      btnTitle: '点击上传文件',
      showTip: true,
      tipInfo: '支持图片、Word、Excel、Pdf、Rar、Zip格式的文件',
      multiple: true,
      showFileList: true,
      autoUpload: true,
      limitSize: 1024 * 1024 * 10,
      limitCount: 0
    }
  }
]