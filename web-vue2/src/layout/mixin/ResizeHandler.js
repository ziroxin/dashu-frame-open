import store from '@/store'

const {body} = document
const WIDTH = 992 // 宽度小于 WIDTH 时，响应式布局（切换成竖版，手机端模式）

export default {
  watch: {
    $route(route) {
      if (this.device === 'mobile' && this.sidebar.opened) {
        store.dispatch('app/closeSideBar', {withoutAnimation: false})
      }
    }
  },
  beforeMount() {
    window.addEventListener('resize', this.$_resizeHandler)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.$_resizeHandler)
  },
  mounted() {
    const isMobile = this.$_isMobile()
    if (isMobile) {
      store.dispatch('app/toggleDevice', 'mobile')
      store.dispatch('app/closeSideBar', {withoutAnimation: true})
      // 手机端默认纵向布局
      store.dispatch('settings/changeSetting', {key: 'layout', value: 'leftMenu'})
    }
  },
  methods: {
    // use $_ for mixins properties
    // https://vuejs.org/v2/style-guide/index.html#Private-property-names-essential
    $_isMobile() {
      const rect = body.getBoundingClientRect()
      return rect.width - 1 < WIDTH
    },
    $_resizeHandler() {
      if (!document.hidden) {
        const isMobile = this.$_isMobile()
        store.dispatch('app/toggleDevice', isMobile ? 'mobile' : 'desktop')

        if (isMobile) {
          store.dispatch('app/closeSideBar', {withoutAnimation: true})
          // 手机端默认纵向布局
          store.dispatch('settings/changeSetting', {key: 'layout', value: 'leftMenu'})
        }
      }
    }
  }
}
