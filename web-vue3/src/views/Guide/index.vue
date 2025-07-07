<template>
  <div class="app-container">
    <aside>
      引导页：用户首次进入时，一步一步，引导用户操作。
      GitHub仓库：
      <a href="https://github.com/kamranahmedse/driver.js" target="_blank">driver.js</a>
      文档地址：
      <a href="https://driverjs.com/" target="_blank">官方文档</a>
    </aside>
    <el-button icon="el-icon-question" type="primary" @click.prevent.stop="guide">显示引导页</el-button>
  </div>
</template>

<script>
import Driver from 'driver.js' // import driver.js
import 'driver.js/dist/driver.min.css' // import driver.js css
import steps from './steps'

export default {
  name: 'Guide',
  data() {
    return {
      driver: null
    }
  },
  mounted() {
    // 初始化driver.js，设置按钮文字（更多配置看文档）
    this.driver = new Driver({nextBtnText: '下一步', prevBtnText: '上一步', doneBtnText: '完成', closeBtnText: '关闭'})
  },
  methods: {
    guide() {
      this.driver.defineSteps(steps)
      this.driver.start()
    }
  }
}
</script>
