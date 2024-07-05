<script>
import store from "@/store";

export default {
  name: 'MenuItem',
  functional: true,
  props: {
    icon: {type: String, default: ''},
    title: {type: String, default: ''},
    permissionName: {type: String, default: ''}
  },
  render(h, context) {
    const {icon, title, permissionName} = context.props
    const vnodes = []

    if (icon) {
      if (icon.includes('el-icon')) {
        vnodes.push(<i class={[icon, 'sub-el-icon']}/>)
      } else {
        vnodes.push(<svg-icon icon-class={icon}/>)
      }
    }

    if (title) {
      vnodes.push(<span slot='title'>{(title)}</span>)
    }

    if (permissionName) {
      const obj = store.state.message.permissionUnreadJson;
      if (obj.hasOwnProperty(permissionName) && obj[permissionName] > 0) {
        vnodes.push(<span class='menu-badge'>{obj[permissionName]}</span>)
      }
    }

    return vnodes
  }
}
</script>

<style scoped>
.sub-el-icon {
  color: currentColor;
  width: 1em;
  height: 1em;
}

.menu-badge {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  right: 5%;
  background: #f56c6c;
  color: #fff;
  border-radius: 100%;
  width: 18px;
  height: 18px;
  line-height: 18px;
  font-size: 12px;
  text-align: center;
}
</style>
