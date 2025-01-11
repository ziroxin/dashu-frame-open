<template>
  <div class="app-container" v-loading="isLoading">
    <div class="title">
      <span style="color: #dd1f29;margin-right: 20px;">
        # 开头表示注释；**结尾可以模糊匹配；
      </span>
      提示：使用快捷键 Ctrl+S 保存; Ctrl+Z 撤销; Ctrl+Y 重做;
      <el-button type="danger" @click="reloadXss" icon="el-icon-refresh"
                 size="small" style="float: right;">重新加载Xss忽略列表
      </el-button>
    </div>
    <el-input type="textarea" class="content" autosize spellcheck="false"
              placeholder="请输入内容" v-model="ignoreContent"></el-input>
  </div>
</template>
<script>
export default {
  data() {
    return {
      isLoading: false,
      ignoreContent: ''
    }
  },
  mounted() {
    this.readXssIgnoreList()
    document.addEventListener('keydown', this.handleKeyDown);
  },
  beforeDestroy() {
    document.removeEventListener('keydown', this.handleKeyDown);
  },
  methods: {
    // 重新加载Xss忽略列表
    reloadXss() {
      this.$request({url: '/xss/ignore/reload', method: 'get'}).then((response) => {
        this.$message({type: 'success', message: 'Xss忽略列表重新加载成功！'})
      })
    },
    // 监听 ctrl+s 快捷键，保存
    handleKeyDown(event) {
      if (event.ctrlKey && event.key === 's') {
        event.preventDefault(); // 阻止浏览器默认的保存操作
        // 在这里执行你的自定义操作
        this.isLoading = true
        console.log('Ctrl + S 被按下');
        const data = this.ignoreContent.split('\n')
        this.$request({url: '/xss/ignore/write', method: 'post', data}).then((response) => {
          this.isLoading = false
          this.$message({type: 'success', message: '保存成功！（只用于生产环境，需要重启服务才能生效）'})
        })
      }
    },
    // 读取忽略名单
    readXssIgnoreList() {
      this.isLoading = true
      this.$request({url: '/xss/ignore/read', method: 'get'}).then((response) => {
        this.isLoading = false
        const {data} = response
        this.ignoreContent = [...data].join('\n')
      })
    },
  }
}
</script>
<style scoped lang="scss">
.app-container {
  .title {
    line-height: 40px;
    font-size: 14px;
    color: #999;
    margin-left: 16px;
  }

  .content {
    width: 100%;
    height: 100%;
  }

}
</style>