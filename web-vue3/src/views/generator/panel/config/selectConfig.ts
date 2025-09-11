export default [
  {
    __key: 'el-radio-group',
    __docLink: 'https://element-plus.org/zh-CN/component/radio.html#radiogroup-api',
    __name: '单选组',
    __icon: 'form-radio',
    __span: 24,
    __formItemAttrs: {
      label: '单选组',
      rules: [{required: true, message: '单选组不能为空'}]
    },
    __attrs: {}
  },
  {
    __key: 'el-radio',
    __docLink: 'https://element-plus.org/zh-CN/component/radio.html#radio-api',
    __name: '单选按钮/边框',
    __icon: 'form-radio',
    __span: 24,
    __formItemAttrs: {
      label: '单选按钮/边框',
      rules: [{required: true, message: '单选按钮/边框不能为空'}]
    },
    __attrs: {
      border: true
    }
  },
  {
    __key: 'el-checkbox-group',
    __docLink: 'https://element-plus.org/zh-CN/component/checkbox.html#checkboxgroup-api',
    __name: '多选',
    __icon: 'form-checkbox',
    __span: 24,
    __formItemAttrs: {
      label: '多选',
      rules: [{required: true, message: '多选不能为空'}]
    },
    __attrs: {}
  },
  {
    __key: 'el-checkbox',
    __docLink: 'https://element-plus.org/zh-CN/component/checkbox.html#checkbox-api',
    __name: '多选按钮/边框',
    __icon: 'form-checkbox',
    __span: 24,
    __formItemAttrs: {
      label: '多选按钮/边框',
      rules: [{required: true, message: '多选按钮/边框不能为空'}]
    },
    __attrs: {
      border: true
    }
  },
  {
    __key: 'el-select',
    __docLink: 'https://element-plus.org/zh-CN/component/select.html#select-attributes',
    __name: '下拉框',
    __icon: 'form-select',
    __span: 24,
    __formItemAttrs: {
      label: '下拉框',
      rules: [{required: true, message: '下拉框不能为空'}]
    },
    __attrs: {
      placeholder: '请选择',
      filterable: true,
      clearable: true,
      multiple: false
    }
  },
  {
    __key: 'el-cascader',
    __docLink: 'https://element-plus.org/zh-CN/component/cascader.html#cascader-api',
    __name: '级联选择',
    __icon: 'form-cascader',
    __span: 24,
    __formItemAttrs: {
      label: '级联选择',
      rules: [{required: true, message: '级联选择不能为空'}]
    },
    __attrs: {
      placeholder: '请选择',
      filterable: true,
      clearable: true
    }
  },
  {
    __key: 'el-date-picker',
    __docLink: 'https://element-plus.org/zh-CN/component/date-picker.html#api',
    __name: '日期选择',
    __icon: 'form-date',
    __span: 24,
    __formItemAttrs: {
      label: '日期选择器',
      rules: [{required: true, message: '日期不能为空'}]
    },
    __attrs: {
      type: 'date',// 常用：date|datetime|daterange|datetimerange
      placeholder: '请选择日期',
      clearable: true,
      format: 'YYYY-MM-DD',
      valueFormat: 'YYYY-MM-DD'
    }
  },
  {
    __key: 'el-switch',
    __docLink: 'https://element-plus.org/zh-CN/component/switch.html#api',
    __name: 'Switch开关',
    __icon: 'form-switch',
    __span: 24,
    __formItemAttrs: {
      label: 'Switch开关',
      rules: [{required: true, message: 'Switch开关不能为空'}]
    },
    __attrs: {
      activeValue: true,
      inactiveValue: false
    },
    __valueType: 'boolean'
  }
]