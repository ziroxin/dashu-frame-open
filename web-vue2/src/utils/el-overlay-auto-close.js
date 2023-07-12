import {Dialog} from 'element-ui'

const Dialog_Patched = {
  extends: Dialog,
  data() {
    return {
      wrapperMouseDowned: false
    }
  },
  mounted() {
    this.$el.onmousedown = (e) => {
      this.wrapperMouseDowned = e.target.classList.contains('el-dialog__wrapper')
    }
  },
  methods: {
    handleWrapperClick() {
      if (!this.closeOnClickModal || !this.wrapperMouseDowned) return;
      this.handleClose();
    }
  }
}
export default {
  install(Vue) {
    Vue.component(Dialog.name, Dialog_Patched)
  }
}
