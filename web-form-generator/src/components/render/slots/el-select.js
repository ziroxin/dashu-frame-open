export default {
  options(h, conf, key) {
    const list = []
    let {label, value} = conf.props.props
    if (conf.__config__.dataType === 'static') {
      label = 'label'
      value = 'value'
    }
    conf.__slot__.options.forEach(item => {
      list.push(<el-option label={item[label]} value={item[value]} disabled={item.disabled}></el-option>)
    })
    return list
  }
}
