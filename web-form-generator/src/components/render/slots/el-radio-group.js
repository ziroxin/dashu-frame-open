export default {
  options(h, conf, key) {
    const list = []
    let {label, value} = conf.props.props
    if (conf.__config__.dataType === 'static') {
      label = 'label'
      value = 'value'
    }
    conf.__slot__.options.forEach(item => {
      if (conf.__config__.optionType === 'button') {
        list.push(<el-radio-button label={item[value]}>{item[label]}</el-radio-button>)
      } else {
        list.push(<el-radio label={item[value]} border={conf.border}>{item[label]}</el-radio>)
      }
    })
    return list
  }
}
