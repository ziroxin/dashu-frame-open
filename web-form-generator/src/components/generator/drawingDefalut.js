export default [
  {
    // 组件的自定义配置
    __config__: {
      label: '主键',
      isTableField: true,
      fieldType: 'varchar',
      fieldLength: '36',
      pointLength: '0',
      isKey: true,
      tag: 'el-key',
      required: true,
      layout: 'colFormItem',
    },
    // 组件的插槽属性
    __slot__: {
      prepend: '',
      append: ''
    },
    __vModel__: 'id'
  },
  {
    __config__: {
      label: '手机号',
      isTableField: true,
      fieldType: 'varchar',
      fieldLength: '20',
      pointLength: '0',
      isKey: false,
      labelWidth: null,
      showLabel: true,
      changeTag: true,
      tag: 'el-input',
      tagIcon: 'input',
      defaultValue: undefined,
      required: true,
      layout: 'colFormItem',
      span: 24,
      document: 'https://element.eleme.cn/#/zh-CN/component/input',
      // 正则校验规则
      regList: [{
        pattern: '/^1(3|4|5|7|8|9)\\d{9}$/',
        message: '手机号格式错误'
      }]
    },
    // 组件的插槽属性
    __slot__: {
      prepend: '',
      append: ''
    },
    __vModel__: 'mobile',
    placeholder: '请输入',
    style: {width: '100%'},
    clearable: true,
    'prefix-icon': 'el-icon-mobile',
    'suffix-icon': '',
    maxlength: 11,
    'show-word-limit': true,
    readonly: false,
    disabled: false
  },
  {
    __config__: {
      label: '顺序',
      isTableField: true,
      fieldType: 'int',
      fieldLength: '11',
      pointLength: '0',
      isKey: false,
      showLabel: true,
      changeTag: true,
      labelWidth: null,
      tag: 'el-input-number',
      tagIcon: 'number',
      defaultValue: 0,
      span: 24,
      layout: 'colFormItem',
      required: true,
      regList: [],
      document: 'https://element.eleme.cn/#/zh-CN/component/input-number'
    },
    placeholder: "请输入",
    min: 0,
    max: undefined,
    step: 1,
    'step-strictly': false,
    'controls-position': '',
    disabled: false,
    __vModel__: 'order_index'
  }
]
