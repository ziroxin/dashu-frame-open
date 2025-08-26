export default [
  {
    __key: 'el-input',
    __name: '文本框',
    __icon: 'form-input',
    __span: 24,
    __formItemAttrs: {
      label: '文本',
      rules: [{required: true, message: '文本不能为空'}]
    },
    __attrs: {
      type: 'text',// 常用：text|textarea|password
      placeholder: '请输入文本'
    }
  },
  {
    __key: 'el-input-number',
    __name: '数字框',
    __icon: 'form-number',
    __span: 24,
    __formItemAttrs: {
      label: '数字',
      rules: [{required: true, message: '数字不能为空'}]
    },
    __attrs: {
      controls: true,
      placeholder: '请输入数字',
      step: 1,
      precision: 0
    }
  }
]