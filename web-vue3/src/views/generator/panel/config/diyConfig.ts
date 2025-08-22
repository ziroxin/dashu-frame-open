export default [
  {
    __key: 'my-wang-editor',
    __name: '富文本框',
    __icon: 'form-rich-text',
    __span: 24,
    __formItemAttrs: {
      label: '内容',
      rules: [{required: true, message: '内容不能为空'}]
    },
    __attrs: {
      placeholder: '请输入内容'
    }
  }
]