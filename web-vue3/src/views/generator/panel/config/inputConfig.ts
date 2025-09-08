export default [
  {
    __key: 'el-input',
    __docLink: 'https://element-plus.org/zh-CN/component/input.html#attributes',
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
    __docLink: 'https://element-plus.org/zh-CN/component/input-number.html#attributes',
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
  },
  {
    __key: 'el-input-tag',
    __docLink: 'https://element-plus.org/zh-CN/component/input-tag.html#api',
    __name: '标签输入框',
    __icon: 'form-textarea',
    __span: 24,
    __formItemAttrs: {
      label: '标签',
      rules: [{required: true, message: '标签不能为空'}]
    },
    __attrs: {
      placeholder: '请输入标签（按回车分割标签）'
    }
  }
]