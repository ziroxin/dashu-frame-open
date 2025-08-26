export default [
  {
    __key: 'el-select',
    __name: '下拉框',
    __icon: 'form-select',
    __span: 24,
    __formItemAttrs: {
      label: '下拉框',
      rules: [{required: true, message: '下拉框必选'}]
    },
    __attrs: {
      placeholder: '请选择',
      filterable: true,
      clearable: true,
      multiple: false
    }
  },
  {
    __key: 'el-date-picker',
    __name: '日期选择',
    __icon: 'form-date',
    __span: 24,
    __formItemAttrs: {
      label: '日期选择器',
      rules: [{required: true, message: '日期必选'}]
    },
    __attrs: {
      type: 'date',// 常用：date|datetime|daterange|datetimerange
      placeholder: '请选择日期',
      format: 'YYYY-MM-DD',
      valueFormat: 'YYYY-MM-DD'
    }
  }
]