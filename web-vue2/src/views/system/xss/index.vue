<template>
  <div class="app-container" v-loading="isLoading">
    <div class="title">
      <span style="color: #dd1f29;margin-right: 20px;">
        # 开头表示注释；**结尾可以模糊匹配；
      </span>
      提示：使用快捷键 Ctrl+S 保存; Ctrl+Z 撤销; Ctrl+Y 重做;
      <el-button type="danger" @click="restartJava" icon="el-icon-refresh"
                 size="small" style="float: right;">重启本项目
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
    // 重启项目
    restartJava() {
      this.$confirm('需要几分钟才能重启成功，重启期间无法正确获取数据，确定要重启吗?', '重启确认', {
        confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
      }).then(() => {
        this.$confirm('再次确认，确定要重启吗?', '重启确认', {
          confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
        }).then(() => {
          // 三次确认，重启项目
          this.$request({
            url: '/xss/ignore/restart', method: 'get'
          }).then((response) => {
            this.$message({
              type: 'success',
              message: '重启成功！需要几分钟才能重启成功，重启期间无法正确获取数据，请耐心等待！'
            })
          })
        })
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
        this.$request({
          url: '/xss/ignore/write', method: 'post', data
        }).then((response) => {
          this.isLoading = false
          this.$message({type: 'success', message: '保存成功！（只用于生产环境，需要重启服务才能生效）'})
        })
      }
    },
    // 读取忽略名单
    readXssIgnoreList() {
      this.isLoading = true
      this.$request({
        url: '/xss/ignore/read', method: 'get'
      }).then((response) => {
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