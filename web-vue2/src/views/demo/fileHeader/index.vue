<template>
  <div class="app-container fileHeader">
    <div class="uploadPanel">
      <el-row>
        <el-col :span="12">
          <div v-loading="isLoading" class="left">
            <div class="title">
              <el-button type="primary" @click="saveFileTypeMap" icon="el-icon-plus"
                         size="small">保存
              </el-button>
              <el-button @click="refreshFileTypeMap" icon="el-icon-refresh"
                         size="small">刷新
              </el-button>
              <span class="info">注意：#开头的行是注释</span>
            </div>
            <el-input type="textarea" class="content" autosize spellcheck="false"
                      placeholder="请输入内容" v-model="fileTypeMap"
                      style="font-size: 12px;"></el-input>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="right">
            <div class="title">
              <el-upload :action="$baseServer+'/upload/fileHeader'" :headers="$store.getters.headerToken"
                         :on-success="uploadFileSuccess"
                         :show-file-list="false" :auto-upload="true">
                <el-button type="primary" icon="el-icon-upload2" size="small">点击选择文件上传，获取文件头</el-button>
              </el-upload>
            </div>
            <div class="info">
              <p v-if="currentHeaderHash">
                <span>
                  文件头：{{ currentHeaderHash[0] }}
                  <el-button type="text" size="small" style="color: #00b42a;"
                             v-clipboard:copy="currentHeaderHash[0]">
                    <i class="el-icon-document-copy"/>复制
                  </el-button>
                </span>
                <br/>
                <span v-if="currentHeaderHash.length>1">
                  扩展名：{{ currentHeaderHash[1] }}
                  <el-button type="text" size="small" style="color: #00b42a;"
                             v-clipboard:copy="currentHeaderHash[1]">
                    <i class="el-icon-document-copy"/>复制
                  </el-button>
                </span>
              </p>
              <p v-else>请先点击上方按钮上传文件</p>
            </div>
            <div class="help">
              1. 上传不同格式的文件，可获取文件头信息
              <br/>
              2. 文件头信息和左侧文件类型对比，如有需要可在左侧另起一行添加并保存
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script>
export default {
  data() {
    return {
      currentHeaderHash: null,
      fileTypeMap: '',
      isLoading: false
    }
  },
  mounted() {
    this.refreshFileTypeMap()
  },
  methods: {
    // 上传
    uploadFileSuccess(response) {
      if (response.code === '200') {
        this.currentHeaderHash = response.data.split('|')
        this.$message({type: 'success', message: '获取文件头成功！'})
      } else {
        this.currentHeaderHash = null
        this.$message({type: 'error', message: response.message})
      }
    },
    // 保存
    saveFileTypeMap() {
      this.isLoading = true
      const data = this.fileTypeMap.split('\n')
      this.$request({
        url: '/upload/fileType/write', method: 'post', data
      }).then((response) => {
        this.isLoading = false
        this.$message({type: 'success', message: '保存成功！'})
      })
    },
    // 刷新
    refreshFileTypeMap() {
      this.isLoading = true
      this.$request({
        url: '/upload/fileType/read', method: 'get'
      }).then((response) => {
        this.isLoading = false
        const {data} = response
        this.fileTypeMap = [...data].join('\n')
      })
    }
  }
}
</script>
<style scoped lang="scss">
.fileHeader {
  padding: 20px;

  h2 {
    text-align: center;
  }

  .uploadPanel {
    .left {
      padding-right: 20px;
      border-right: 1px dashed #dddddd;

      .title {
        margin: 0px auto 10px auto;

        .info {
          color: #dd1f29;
          font-size: 14px;
          line-height: 33px;
          float: right;
        }
      }
    }

    .right {
      margin-left: 20px;

      .title {
        text-align: center;
        margin: 0px auto 10px auto;
      }

      .info {
        padding: 15px 20px;
        border: 1px dashed #ccc;
        border-radius: 10px;
        min-height: 100px;

        p {
          font-size: 16px;
          color: #dd1f29;
          line-height: 26px;
        }
      }

      .help {
        margin-top: 10px;
        border-radius: 10px;
        background: #efefef;
        padding: 15px 20px;
        font-size: 12px;
        color: #0490cc;
        line-height: 26px;
      }
    }
  }
}
</style>