import {isArray} from 'util'
import {camelCaseUnderline, deepClone, exportDefault, removeUnderline, titleCase} from '@/utils/index'
import ruleTrigger from './ruleTrigger'

const units = {
  KB: '1024',
  MB: '1024 / 1024',
  GB: '1024 / 1024 / 1024'
}
let confGlobal
const inheritAttrs = {
  file: '',
  dialog: 'inheritAttrs: false,'
}

/**
 * 组装js 【入口函数】
 * @param {Object} formConfig 整个表单配置
 * @param {String} type 生成类型，文件或弹窗等
 */
export function makeUpJs(formConfig, type) {
  confGlobal = formConfig = deepClone(formConfig)
  const dataList = []
  const ruleList = []
  const optionsList = []
  const propsList = []
  const methodList = mixinMethod(type)
  const uploadVarList = []
  const created = []

  formConfig.fields.forEach(el => {
    buildAttributes(el, dataList, ruleList, optionsList, methodList, propsList, uploadVarList, created)
  })

  // 单独处理el-checkbox-group的数据转换问题
  const chkStr2Arr = []
  const chkArr2Str = [];
  formConfig.fields.forEach(el => {
    if (el.__config__.tag === 'el-checkbox-group') {
      chkStr2Arr.push(`this.${el.__vModel__}Array = this.${confGlobal.formModel} ? this.${confGlobal.formModel}.${el.__vModel__}.split(",") : [];`)
      chkArr2Str.push(`this.${confGlobal.formModel}.${el.__vModel__} = this.${el.__vModel__}Array.join(',');`)
    }
  })
  methodList.push(`loadChkStr2Arr() {${chkStr2Arr.join(' ')}},`)
  methodList.push(`loadChkArr2Str() {${chkArr2Str.join(' ')}},`)

  const script = buildexport(
    formConfig,
    type,
    dataList.join('\n'),
    ruleList.join('\n'),
    optionsList.join('\n'),
    uploadVarList.join('\n'),
    propsList.join('\n'),
    methodList.join('\n'),
    created.join('\n')
  )
  confGlobal = null
  return script
}

// 构建组件属性
function buildAttributes(scheme, dataList, ruleList, optionsList, methodList, propsList, uploadVarList, created) {
  const config = scheme.__config__
  const slot = scheme.__slot__
  buildData(scheme, dataList)
  buildRules(scheme, ruleList)

  // 特殊处理options属性
  if (scheme.options || (slot && slot.options && slot.options.length)) {
    buildOptions(scheme, optionsList)
    if (config.dataType === 'dynamic') {
      const model = `${scheme.__vModel__}Options`
      const options = titleCase(model)
      const methodName = `get${options}`
      buildOptionMethod(methodName, model, methodList, scheme)
      callInCreated(methodName, created)
    }
  }

  // 处理props
  if (scheme.props && scheme.props.props) {
    buildProps(scheme, propsList)
  }

  // 处理el-upload的action
  if (scheme.action && config.tag === 'el-upload') {
    uploadVarList.push(
      `${scheme.__vModel__}Action: '${scheme.action}',
      ${scheme.__vModel__}fileList: [],`
    )
    // 上传前条件判断
    methodList.push(buildBeforeUpload(scheme))
    // 上传成功
    methodList.push(buildUploadSuccess(scheme))
    // 移除
    methodList.push(buildUploadRemove(scheme))
    // 限制上传个数
    if (scheme.__config__.fileLimit != undefined && scheme.__config__.fileLimit > 0) {
      methodList.push(buildUploadExceed(scheme))
    }
    // 加载fileList
    methodList.push(buildLoadFileList(scheme))
    // 非自动上传时，生成手动上传的函数
    if (!scheme['auto-upload']) {
      methodList.push(buildSubmitUpload(scheme))
    }
  }

  // 构建子级组件属性
  if (config.children) {
    config.children.forEach(item => {
      buildAttributes(item, dataList, ruleList, optionsList, methodList, propsList, uploadVarList, created)
    })
  }
}

// 在Created调用函数
function callInCreated(methodName, created) {
  created.push(`this.${methodName}()`)
}

// 混入处理函数
function mixinMethod(type) {
  const list = [];
  const minxins = {
    file: confGlobal.formBtns ? {
      submitForm: `submitForm() {
        this.$refs['${confGlobal.formRef}'].validate(valid => {
          if(!valid) return
          // TODO 提交表单
        })
      },`,
      resetForm: `resetForm() {
        this.$refs['${confGlobal.formRef}'].resetFields()
      },`
    } : null,
    dialog: {
      onOpen: 'onOpen() {},',
      onClose: `onClose() {
        this.$refs['${confGlobal.formRef}'].resetFields()
      },`,
      close: `close() {
        this.$emit('update:visible', false)
      },`,
      handelConfirm: `handelConfirm() {
        this.$refs['${confGlobal.formRef}'].validate(valid => {
          if(!valid) return
          console.log('表单信息：',this.${confGlobal.formModel});
          this.close()
        })
      },`
    }
  }

  const methods = minxins[type]
  if (methods) {
    Object.keys(methods).forEach(key => {
      list.push(methods[key])
    })
  }

  return list
}

// 构建data
function buildData(scheme, dataList) {
  const config = scheme.__config__
  if (scheme.__vModel__ === undefined) return
  const defaultValue = JSON.stringify(config.defaultValue)
  dataList.push(`${scheme.__vModel__}: ${defaultValue},`)
}

// 构建校验规则
function buildRules(scheme, ruleList) {
  const config = scheme.__config__
  if (scheme.__vModel__ === undefined) return
  const rules = []
  if (ruleTrigger[config.tag]) {
    if (config.required) {
      const type = isArray(config.defaultValue) ? 'type: \'array\',' : ''
      let message = isArray(config.defaultValue) ? `请至少选择一个${config.label}` : scheme.placeholder
      if (message === undefined) message = `${config.label}不能为空`
      rules.push(`{ required: true, ${type} message: '${message}', trigger: '${ruleTrigger[config.tag]}' }`)
    }
    if (config.regList && isArray(config.regList)) {
      config.regList.forEach(item => {
        if (item.pattern) {
          rules.push(
            `{ pattern: ${eval(item.pattern)}, message: '${item.message}', trigger: '${ruleTrigger[config.tag]}' }`
          )
        }
      })
    }
    ruleList.push(`${scheme.__vModel__}: [${rules.join(',')}],`)
  }
}

// 构建options
function buildOptions(scheme, optionsList) {
  if (scheme.__vModel__ === undefined) return
  // el-cascader直接有options属性，其他组件都是定义在slot中，所以有两处判断
  let {options} = scheme
  if (!options) options = scheme.__slot__.options
  if (scheme.__config__.dataType === 'dynamic') {
    options = []
  }
  // options根据字段类型，转换value的类型
  if (['varchar', 'char'].findIndex(k => k === scheme.__config__.fieldType) >= 0) {
    options = options.map(o => {
      return {...o, value: String(o.value)}
    })
  }
  const str = `${scheme.__vModel__}Options: ${JSON.stringify(options)},`
  optionsList.push(str)

  // 处理el-checkbox-group的绑定值
  if (scheme.__config__.tag === 'el-checkbox-group') {
    const defaultValue = JSON.stringify(scheme.__config__.defaultValue)
    optionsList.push(`${scheme.__vModel__}Array: ${defaultValue},`)
  }
}

function buildProps(scheme, propsList) {
  const str = `${scheme.__vModel__}Props: ${JSON.stringify(scheme.props.props)},`
  propsList.push(str)
}

// el-upload的BeforeUpload
function buildBeforeUpload(scheme) {
  const config = scheme.__config__
  const unitNum = units[config.sizeUnit];
  let rightSizeCode = '';
  let acceptCode = '';
  const returnList = []
  if (config.fileSize) {
    rightSizeCode = `let isRightSize = file.size / ${unitNum} < ${config.fileSize}
    if(!isRightSize){
      this.$message.error('文件大小超过 ${config.fileSize}${config.sizeUnit}')
    }`
    returnList.push('isRightSize')
  }
  if (scheme.accept) {
    acceptCode = `let ext = file.name.substring((file.name.lastIndexOf('.')))
    let extArr = '${scheme.accept}'.split(',')
    let isAccept = new RegExp('${scheme.accept}').test(file.type) || extArr.includes(ext)
    if(!isAccept){
      this.$message.error('应该选择${scheme.accept}类型的文件')
    }`
    returnList.push('isAccept')
  }
  const str = `${scheme.__vModel__}BeforeUpload(file) {
    ${rightSizeCode}
    ${acceptCode}
    return ${returnList.join('&&')}
  },`
  return returnList.length ? str : ''
}

// el-upload的on-success
function buildUploadSuccess(scheme) {
  if (scheme.__config__.isTableField) {
    // 存主表
    return `${scheme.__vModel__}OnSuccess(response, file, fileList) {
      if(response.data&&response.data.length>0){
        this.${confGlobal.formModel}.${scheme.__vModel__} = response.data[0].fileUrl
      }
    },`
  } else {
    // 存子表
    return `${scheme.__vModel__}OnSuccess(response, file, fileList) {
      this.${scheme.__vModel__}fileList = fileList
      if(this.${confGlobal.formModel}.${removeUnderline(scheme.__config__.childTableName)}List){
        this.${confGlobal.formModel}.${removeUnderline(scheme.__config__.childTableName)}List.push(response.data[0])
      } else {
        this.${confGlobal.formModel}.${removeUnderline(scheme.__config__.childTableName)}List = [response.data[0]]
      }
    },`
  }

}

// el-upload的on-remove
function buildUploadRemove(scheme) {
  if (scheme.__config__.isTableField) {
    // 存主表
    return `${scheme.__vModel__}OnRemove(file, fileList) {
      this.${confGlobal.formModel}.${scheme.__vModel__} = ''
    },`
  } else {
    // 存子表
    return `${scheme.__vModel__}OnRemove(file, fileList) {
      this.${scheme.__vModel__}fileList = fileList
      this.${confGlobal.formModel}.${removeUnderline(scheme.__config__.childTableName)}List = this.${confGlobal.formModel}.${removeUnderline(scheme.__config__.childTableName)}List.filter(f=>f.fileName!=file.fileName)
    },`
  }
}

// el-upload的on-exceed
function buildUploadExceed(scheme) {
  return `${scheme.__vModel__}OnExceed(files, fileList) {
    this.$message.error('最多上传 ${scheme.__config__.fileLimit} 个文件')
  },`
}

// el-upload，加载文件列表
function buildLoadFileList(scheme) {
  if (scheme.__config__.isTableField) {
    // 存主表
    return `load${scheme.__vModel__}FileList() {
      if (this.${confGlobal.formModel}.${scheme.__vModel__}) {
        this.${scheme.__vModel__}fileList = {name: '附件', url: this.${confGlobal.formModel}.${scheme.__vModel__}}
      } else {
        this.${scheme.__vModel__}fileList = []
      }
    },`
  } else {
    // 存子表
    return `load${camelCaseUnderline(scheme.__config__.childTableName)}FileList() {
      if (this.${confGlobal.formModel}.${removeUnderline(scheme.__config__.childTableName)}List) {
        this.${scheme.__vModel__}fileList = this.${confGlobal.formModel}.${removeUnderline(scheme.__config__.childTableName)}List.map(f => {
          return {name: f.fileOldName, url: f.fileUrl, ...f}
        })
      } else {
        this.${scheme.__vModel__}fileList = []
      }
    },`
  }
}

// el-upload的submit
function buildSubmitUpload(scheme) {
  return `${scheme.__vModel__}SubmitUpload() {
    this.$refs['${scheme.__vModel__}'].submit()
  },`
}

function buildOptionMethod(methodName, model, methodList, scheme) {
  const config = scheme.__config__
  const str = `${methodName}() {
    // 注意：this.$request是通过Vue.prototype.$request挂载产生的
    this.$request({
      method: '${config.method}',
      url: '${config.url}'
    }).then(resp => {
      this.${model} = resp.${config.dataPath}
    })
  },`
  methodList.push(str)
}

// js整体拼接
function buildexport(conf, type, data, rules, selectOptions, uploadVar, props, methods, created) {
  const str = `${exportDefault}{
  ${inheritAttrs[type]}
  components: {},
  props: [],
  data () {
    return {
      ${conf.formModel}: {
        ${data}
      },
      ${conf.formRules}: {
        ${rules}
      },
      ${uploadVar}
      ${selectOptions}
      ${props}
    }
  },
  computed: {},
  watch: {},
  created () {
    ${created}
  },
  mounted () {},
  methods: {
    ${methods}
  }
}`
  return str
}
