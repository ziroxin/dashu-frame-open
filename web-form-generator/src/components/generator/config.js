// 表单属性【右面板】
export const formConf = {
  tableName: 'a_table',
  tableDecription: '表描述',
  basePackage: 'com.kg.module',
  tablePackage: 'atable',
  viewPath: '/atable',
  author: 'ziro',
  formRef: 'dataForm',
  formModel: 'temp',
  size: 'medium',
  labelPosition: 'right',
  labelWidth: 100,
  formRules: 'rules',
  gutter: 15,
  span: 24,
  formBtns: true
}

// 输入型组件 【左面板】
export const inputComponents = [
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
      tagIcon: 'key',
      required: true,
      layout: 'colFormItem',
    },
    // 组件的插槽属性
    __slot__: {
      prepend: '',
      append: ''
    }
  },
  {
    // 组件的自定义配置
    __config__: {
      label: '单行文本',
      isTableField: true,
      fieldType: 'varchar',
      fieldLength: '200',
      pointLength: '0',
      isKey: false,
      labelWidth: null,
      showLabel: true,
      changeTag: true,
      tag: 'el-input',
      tagIcon: 'input',
      defaultValue: undefined,
      required: false,
      layout: 'colFormItem',
      span: 24,
      document: 'https://element.eleme.cn/#/zh-CN/component/input',
      // 正则校验规则
      regList: []
    },
    // 组件的插槽属性
    __slot__: {
      prepend: '',
      append: ''
    },
    // 其余的为可直接写在组件标签上的属性
    placeholder: '请输入',
    style: {width: '100%'},
    clearable: true,
    'prefix-icon': '',
    'suffix-icon': '',
    maxlength: null,
    'show-word-limit': false,
    readonly: false,
    disabled: false
  },
  {
    __config__: {
      label: '多行文本',
      isTableField: true,
      fieldType: 'varchar',
      fieldLength: '1000',
      pointLength: '0',
      isKey: false,
      labelWidth: null,
      showLabel: true,
      tag: 'el-input',
      tagIcon: 'textarea',
      defaultValue: undefined,
      required: false,
      layout: 'colFormItem',
      span: 24,
      regList: [],
      changeTag: true,
      document: 'https://element.eleme.cn/#/zh-CN/component/input'
    },
    type: 'textarea',
    placeholder: '请输入',
    autosize: {
      minRows: 4,
      maxRows: 4
    },
    style: {width: '100%'},
    maxlength: null,
    'show-word-limit': false,
    readonly: false,
    disabled: false
  },
  {
    __config__: {
      label: '密码',
      isTableField: true,
      fieldType: 'varchar',
      fieldLength: '80',
      pointLength: '0',
      isKey: false,
      showLabel: true,
      labelWidth: null,
      changeTag: true,
      tag: 'el-input',
      tagIcon: 'password',
      defaultValue: undefined,
      layout: 'colFormItem',
      span: 24,
      required: false,
      regList: [],
      document: 'https://element.eleme.cn/#/zh-CN/component/input'
    },
    __slot__: {
      prepend: '',
      append: ''
    },
    placeholder: '请输入',
    'show-password': true,
    style: {width: '100%'},
    clearable: true,
    'prefix-icon': '',
    'suffix-icon': '',
    maxlength: null,
    'show-word-limit': false,
    readonly: false,
    disabled: false
  },
  {
    __config__: {
      label: '计数器',
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
      required: false,
      regList: [],
      document: 'https://element.eleme.cn/#/zh-CN/component/input-number'
    },
    placeholder: '',
    min: undefined,
    max: undefined,
    step: 1,
    'step-strictly': false,
    precision: undefined,
    'controls-position': '',
    disabled: false
  },
  {
    __config__: {
      label: '富文本框',
      isTableField: true,
      fieldType: 'longtext',
      fieldLength: '0',
      pointLength: '0',
      isKey: false,
      showLabel: true,
      changeTag: true,
      labelWidth: null,
      tag: 'my-wang-editor',
      tagIcon: 'rich-text',
      defaultValue: null,
      span: 24,
      layout: 'colFormItem',
      required: false,
      regList: [],
      document: 'https://www.wangeditor.com/'
    },
    placeholder: '请输入',
    height: 200, // 编辑器高度
  }
]

// 选择型组件 【左面板】
export const selectComponents = [
  {
    __config__: {
      label: '下拉选择',
      isTableField: true,
      fieldType: 'varchar',
      fieldLength: '50',
      pointLength: '0',
      isKey: false,
      url: '/role/list',
      method: 'get',
      dataPath: 'data.records',
      dataConsumer: '__slot__.options',
      showLabel: true,
      labelWidth: null,
      tag: 'el-select',
      tagIcon: 'select',
      layout: 'colFormItem',
      defaultValue: null,
      dataType: 'dynamic',
      span: 24,
      required: false,
      regList: [],
      changeTag: true,
      document: 'https://element.eleme.cn/#/zh-CN/component/select'
    },
    __slot__: {
      options: [{
        label: '选项一',
        value: 1
      }, {
        label: '选项二',
        value: 2
      }]
    },
    placeholder: '请选择',
    style: {width: '100%'},
    props: {
      props: {
        label: 'roleName',
        value: 'roleId',
      }
    },
    disabled: false,
    clearable: true,
    filterable: false,
    multiple: false
  },
  {
    __config__: {
      label: '级联选择',
      isTableField: true,
      fieldType: 'varchar',
      fieldLength: '50',
      pointLength: '0',
      isKey: false,
      url: '/zorg/zOrganization/tree',
      method: 'get',
      dataPath: 'data',
      dataConsumer: 'options',
      showLabel: true,
      labelWidth: null,
      tag: 'el-cascader',
      tagIcon: 'cascader',
      layout: 'colFormItem',
      defaultValue: null,
      dataType: 'dynamic',
      span: 24,
      required: false,
      regList: [],
      changeTag: true,
      document: 'https://element.eleme.cn/#/zh-CN/component/cascader'
    },
    options: [{
      id: 1,
      value: 1,
      label: '选项1',
      children: [{
        id: 2,
        value: 2,
        label: '选项1-1'
      }]
    }],
    placeholder: '请选择',
    style: {width: '100%'},
    props: {
      props: {
        multiple: false,
        label: 'orgName',
        value: 'orgId',
        children: 'children'
      }
    },
    'show-all-levels': true,
    disabled: false,
    clearable: true,
    filterable: false,
    separator: '/'
  },
  {
    __config__: {
      label: '单选框组',
      isTableField: true,
      fieldType: 'varchar',
      fieldLength: '50',
      pointLength: '0',
      isKey: false,
      url: '/role/list',
      method: 'get',
      dataPath: 'data.records',
      dataConsumer: '__slot__.options',
      showLabel: true,
      labelWidth: null,
      tag: 'el-radio-group',
      tagIcon: 'radio',
      layout: 'colFormItem',
      defaultValue: null,
      dataType: 'dynamic',
      span: 24,
      required: false,
      optionType: 'default',
      regList: [],
      border: false,
      changeTag: true,
      document: 'https://element.eleme.cn/#/zh-CN/component/radio'
    },
    __slot__: {
      options: [{
        label: '选项一',
        value: 1
      }, {
        label: '选项二',
        value: 2
      }]
    },
    style: {},
    props: {
      props: {
        label: 'roleName',
        value: 'roleId',
      }
    },
    size: 'medium',
    disabled: false
  },
  {
    __config__: {
      label: '多选框组',
      isTableField: true,
      fieldType: 'varchar',
      fieldLength: '255',
      pointLength: '0',
      isKey: false,
      url: '/role/list',
      method: 'get',
      dataPath: 'data.records',
      dataConsumer: '__slot__.options',
      showLabel: true,
      labelWidth: null,
      tag: 'el-checkbox-group',
      tagIcon: 'checkbox',
      layout: 'colFormItem',
      defaultValue: [],
      dataType: 'dynamic',
      span: 24,
      optionType: 'default',
      required: false,
      regList: [],
      changeTag: true,
      border: false,
      document: 'https://element.eleme.cn/#/zh-CN/component/checkbox'
    },
    __slot__: {
      options: [{
        label: '选项一',
        value: 1
      }, {
        label: '选项二',
        value: 2
      }]
    },
    style: {},
    props: {
      props: {
        label: 'roleName',
        value: 'roleId',
      }
    },
    size: 'medium',
    min: null,
    max: null,
    disabled: false
  },
  {
    __config__: {
      label: '状态/开关',
      isTableField: true,
      fieldType: 'char',
      fieldLength: '1',
      pointLength: '0',
      isKey: false,
      tag: 'el-switch',
      tagIcon: 'switch',
      defaultValue: false,
      span: 24,
      showLabel: true,
      labelWidth: null,
      layout: 'colFormItem',
      required: false,
      regList: [],
      changeTag: true,
      document: 'https://element.eleme.cn/#/zh-CN/component/switch'
    },
    style: {},
    disabled: false,
    'active-text': '是',
    'inactive-text': '否',
    'active-color': null,
    'inactive-color': null,
    'active-value': '1',
    'inactive-value': '0'
  },
  {
    __config__: {
      label: '滑块',
      isTableField: true,
      fieldType: 'int',
      fieldLength: '11',
      pointLength: '0',
      isKey: false,
      tag: 'el-slider',
      tagIcon: 'slider',
      defaultValue: null,
      span: 24,
      showLabel: true,
      layout: 'colFormItem',
      labelWidth: null,
      required: false,
      regList: [],
      changeTag: true,
      document: 'https://element.eleme.cn/#/zh-CN/component/slider'
    },
    disabled: false,
    min: 0,
    max: 100,
    step: 1,
    'show-stops': false,
    range: false
  },
  {
    __config__: {
      label: '时间选择',
      isTableField: true,
      fieldType: 'varchar',
      fieldLength: '10',
      pointLength: '0',
      isKey: false,
      tag: 'el-time-picker',
      tagIcon: 'time',
      defaultValue: null,
      span: 24,
      showLabel: true,
      layout: 'colFormItem',
      labelWidth: null,
      required: false,
      regList: [],
      changeTag: true,
      document: 'https://element.eleme.cn/#/zh-CN/component/time-picker'
    },
    placeholder: '请选择',
    style: {width: '100%'},
    disabled: false,
    clearable: true,
    'picker-options': {
      selectableRange: '00:00:00-23:59:59'
    },
    format: 'HH:mm:ss',
    'value-format': 'HH:mm:ss'
  },
  {
    __config__: {
      label: '时间范围',
      isTableField: true,
      fieldType: 'varchar',
      fieldLength: '20',
      pointLength: '0',
      isKey: false,
      tag: 'el-time-picker',
      tagIcon: 'time-range',
      span: 24,
      showLabel: true,
      labelWidth: null,
      layout: 'colFormItem',
      defaultValue: null,
      required: false,
      regList: [],
      changeTag: true,
      document: 'https://element.eleme.cn/#/zh-CN/component/time-picker'
    },
    style: {width: '100%'},
    disabled: false,
    clearable: true,
    'is-range': true,
    'range-separator': '至',
    'start-placeholder': '开始时间',
    'end-placeholder': '结束时间',
    format: 'HH:mm:ss',
    'value-format': 'HH:mm:ss'
  },
  {
    __config__: {
      label: '日期选择',
      isTableField: true,
      fieldType: 'datetime',
      fieldLength: '0',
      pointLength: '0',
      isKey: false,
      tag: 'el-date-picker',
      tagIcon: 'date',
      defaultValue: null,
      showLabel: true,
      labelWidth: null,
      span: 24,
      layout: 'colFormItem',
      required: false,
      regList: [],
      changeTag: true,
      document: 'https://element.eleme.cn/#/zh-CN/component/date-picker'
    },
    placeholder: '请选择',
    type: 'date',
    style: {width: '100%'},
    disabled: false,
    clearable: true,
    format: 'yyyy-MM-dd',
    'value-format': 'yyyy-MM-dd',
    readonly: false
  },
  {
    __config__: {
      label: '日期范围',
      isTableField: true,
      fieldType: 'varchar',
      fieldLength: '50',
      pointLength: '0',
      isKey: false,
      tag: 'el-date-picker',
      tagIcon: 'date-range',
      defaultValue: null,
      span: 24,
      showLabel: true,
      labelWidth: null,
      required: false,
      layout: 'colFormItem',
      regList: [],
      changeTag: true,
      document: 'https://element.eleme.cn/#/zh-CN/component/date-picker'
    },
    style: {width: '100%'},
    type: 'daterange',
    'range-separator': '至',
    'start-placeholder': '开始日期',
    'end-placeholder': '结束日期',
    disabled: false,
    clearable: true,
    format: 'yyyy-MM-dd',
    'value-format': 'yyyy-MM-dd',
    readonly: false
  },
  {
    __config__: {
      label: '评分',
      isTableField: true,
      fieldType: 'tinyint',
      fieldLength: '4',
      pointLength: '0',
      isKey: false,
      tag: 'el-rate',
      tagIcon: 'rate',
      defaultValue: 0,
      span: 24,
      showLabel: true,
      labelWidth: null,
      layout: 'colFormItem',
      required: false,
      regList: [],
      changeTag: true,
      document: 'https://element.eleme.cn/#/zh-CN/component/rate'
    },
    style: {},
    max: 5,
    'allow-half': false,
    'show-text': false,
    'show-score': false,
    disabled: false
  },
  {
    __config__: {
      label: '颜色选择',
      isTableField: true,
      fieldType: 'varchar',
      fieldLength: '20',
      pointLength: '0',
      isKey: false,
      tag: 'el-color-picker',
      tagIcon: 'color',
      span: 24,
      defaultValue: null,
      showLabel: true,
      labelWidth: null,
      layout: 'colFormItem',
      required: false,
      regList: [],
      changeTag: true,
      document: 'https://element.eleme.cn/#/zh-CN/component/color-picker'
    },
    'show-alpha': false,
    'color-format': '',
    disabled: false,
    size: 'medium'
  },
  {
    __config__: {
      label: '上传',
      isTableField: false,
      fieldType: 'varchar',
      fieldLength: '200',
      pointLength: '0',
      isKey: false,
      tag: 'el-upload',
      tagIcon: 'upload',
      layout: 'colFormItem',
      defaultValue: null,
      showLabel: true,
      labelWidth: null,
      required: false,
      span: 24,
      showTip: false,
      buttonText: '点击上传',
      regList: [],
      changeTag: true,
      fileSize: 2,
      fileLimit: 0,
      sizeUnit: 'MB',
      document: 'https://element.eleme.cn/#/zh-CN/component/upload'
    },
    __slot__: {
      'list-type': true
    },
    action: '/upload/files',
    disabled: false,
    accept: '',
    name: 'file',
    'auto-upload': true,
    'list-type': 'text',
    multiple: false
  }
]

// 布局型组件 【左面板】
export const layoutComponents = [
  {
    __config__: {
      label: '行容器',
      isTableField: false,
      layout: 'rowFormItem',
      tagIcon: 'row',
      layoutTree: true,
      document: 'https://element.eleme.cn/#/zh-CN/component/layout#row-attributes'
    },
    type: 'default',
    justify: 'start',
    align: 'top'
  },
  {
    __config__: {
      label: '按钮',
      isTableField: false,
      showLabel: true,
      changeTag: true,
      labelWidth: null,
      tag: 'el-button',
      tagIcon: 'button',
      span: 24,
      layout: 'colFormItem',
      document: 'https://element.eleme.cn/#/zh-CN/component/button'
    },
    __slot__: {
      default: '主要按钮'
    },
    type: 'primary',
    icon: 'el-icon-search',
    round: false,
    size: 'medium',
    plain: false,
    circle: false,
    disabled: false
  },
  {
    __config__: {
      label: '表格[开发中]',
      isTableField: false,
      layout: 'colFormItem',
      tagIcon: 'table',
      tag: 'el-table',
      document: 'https://element.eleme.cn/#/zh-CN/component/table',
      span: 24,
      formId: 101,
      renderKey: 1595761764203,
      componentName: 'row101',
      showLabel: true,
      changeTag: true,
      labelWidth: null,
      dataType: 'dynamic',
      method: 'get',
      dataPath: 'list',
      dataConsumer: 'data',
      url: 'https://www.fastmock.site/mock/f8d7a54fb1e60561e2f720d5a810009d/fg/tableData',
      children: [{
        __config__: {
          layout: 'raw',
          tag: 'el-table-column',
          renderKey: 15957617660153
        },
        prop: 'date',
        label: '日期'
      }, {
        __config__: {
          layout: 'raw',
          tag: 'el-table-column',
          renderKey: 15957617660152
        },
        prop: 'address',
        label: '地址'
      }, {
        __config__: {
          layout: 'raw',
          tag: 'el-table-column',
          renderKey: 15957617660151
        },
        prop: 'name',
        label: '名称'
      }, {
        __config__: {
          layout: 'raw',
          tag: 'el-table-column',
          renderKey: 1595774496335,
          children: [
            {
              __config__: {
                label: '按钮',
                tag: 'el-button',
                tagIcon: 'button',
                layout: 'raw',
                renderKey: 1595779809901
              },
              __slot__: {
                default: '主要按钮'
              },
              type: 'primary',
              icon: 'el-icon-search',
              round: false,
              size: 'medium'
            }
          ]
        },
        label: '操作'
      }]
    },
    data: [],
    directives: [{
      name: 'loading',
      value: true
    }],
    border: true,
    type: 'default',
    justify: 'start',
    align: 'top'
  }
]
