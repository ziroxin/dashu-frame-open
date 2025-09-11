export default [
  {
    __key: 'el-slider',
    __docLink: 'https://element-plus.org/zh-CN/component/slider.html#api',
    __name: '滑块',
    __icon: 'form-slider',
    __span: 24,
    __formItemAttrs: {
      label: '滑块',
      rules: [{required: true, message: '滑块不能为空'}]
    },
    __attrs: {}
  },
  {
    __key: 'el-rate',
    __docLink: 'https://element-plus.org/zh-CN/component/rate.html#api',
    __name: '评分',
    __icon: 'form-rate',
    __span: 24,
    __formItemAttrs: {
      label: '评分',
      rules: [{required: true, message: '评分不能为空'}]
    },
    __attrs: {
      max: 5,
      lowThreshold: 2,
      highThreshold: 4,
      colors: ['#f7ba2a', '#f7ba2a', '#f7ba2a']
    }
  },
  {
    __key: 'el-color-picker',
    __docLink: 'https://element-plus.org/zh-CN/component/color-picker.html#api',
    __name: '颜色选择',
    __icon: 'form-color',
    __span: 24,
    __formItemAttrs: {
      label: '颜色选择',
      rules: [{required: true, message: '颜色选择不能为空'}]
    },
    __attrs: {
      showAlpha: false
    }
  }
]