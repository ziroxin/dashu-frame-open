export default {
  // Form属性
  __attrs: {
    labelPosition: 'right',// 标签对齐
    labelWidth: 'auto',// 标签宽度
    labelSuffix: ':',// 标签后缀
    hideRequiredAsterisk: false,// 是否隐藏必填项的星号
    requireAsteriskPosition: 'left',// 必填项星号位置
    showMessage: true,// 是否显示校验错误信息
    inlineMessage: false,// 是否行内显示校验错误信息
    statusIcon: false,// 是否在输入框中显示校验结果反馈图标
    size: 'default',// 用于控制该表单内组件的尺寸
    disabled: false,// 是否禁用该表单内的所有组件
    scrollToError: true// 是否当校验失败时，滚动到第一个错误表单项
  },
  // 布局属性（非form属性）
  __layout: {
    layout: false,// 是否使用栅格布局
    gutter: 0,// 栅格间隔
    justify: 'start',// 栅格水平排列方式
    align: 'middle'// 栅格垂直排列方式
  }
}