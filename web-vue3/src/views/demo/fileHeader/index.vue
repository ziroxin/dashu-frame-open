<template>
  <div class="app-container fileHeader">
    <div class="uploadPanel">
      <el-row>
        <el-col :span="12">
          <div v-loading="isLoading" class="left">
            <div class="title">
              <base-button type="primary" icon="el-icon-plus" @click="saveFileTypeMap">保存</base-button>
              <base-button icon="el-icon-refresh" @click="refreshFileTypeMap">刷新</base-button>
              <span class="info">注意：#开头的行是注释</span>
            </div>
            <el-input type="textarea" class="content text-12px!" autosize spellcheck="false"
                      placeholder="请输入内容" v-model="fileTypeMap"/>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="right">
            <div class="title">
              <el-upload :action="$baseServer+'/upload/fileHeader'" :headers="getTokenHeader()" :show-file-list="false"
                         :on-success="uploadFileSuccess" :before-upload="()=>{isLoading2=true}" :auto-upload="true">
                <base-button type="primary" icon="el-icon-upload2">点击选择文件上传，获取文件头</base-button>
              </el-upload>
              <a href="http://docs.java119.cn/use/comm-fileupload.html#_4-%E6%96%87%E4%BB%B6%E7%B1%BB%E5%9E%8B%E6%A3%80%E6%B5%8B"
                 target="_blank" class="help">
                <base-button type="danger" icon="el-icon-question" plain>使用帮助</base-button>
              </a>
            </div>
            <div class="info lh-40px! text-16px color-#dd1f29" v-loading="isLoading2">
              <div v-if="currentHeaderHash">
                <div>
                  文件头：{{ currentHeaderHash[0] }}
                  <base-button link icon="el-icon-document-copy" class="ml-10px color-#00b42a!"
                               v-clipboard:copy="currentHeaderHash[0]">复制
                  </base-button>
                </div>
                <div v-if="currentHeaderHash.length>1">
                  扩展名：{{ currentHeaderHash[1] }}
                  <base-button link icon="el-icon-document-copy" class="ml-10px color-#00b42a!"
                               v-clipboard:copy="currentHeaderHash[1]">复制
                  </base-button>
                </div>
              </div>
              <div v-else class="text-center">请先点击上方按钮上传文件</div>
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
import request from '@/utils/request'
import { getTokenHeader } from '@/utils/auth'

export default {
  data() {
    return {
      currentHeaderHash: null,
      fileTypeMap: '',
      isLoading: false,
      isLoading2: false
    }
  },
  mounted() {
    this.refreshFileTypeMap()
  },
  methods: {
    getTokenHeader,
    // 上传
    uploadFileSuccess(response) {
      this.isLoading2 = true
      this.currentHeaderHash = null
      if (response.code === '200') {
        setTimeout(() => {
          this.currentHeaderHash = response.data.split('|')
          this.$message({type: 'success', message: '获取文件头成功！'})
          this.isLoading2 = false
        }, 500)
      } else {
        this.isLoading2 = false
        this.$message({type: 'error', message: response.message})
      }
    },
    // 保存
    saveFileTypeMap() {
      this.isLoading = true
      const data = this.fileTypeMap.split('\n')
      request({url: '/upload/fileType/write', method: 'post', data}).then((response) => {
        this.isLoading = false
        this.$message({type: 'success', message: '保存成功！'})
      })
    },
    // 刷新
    refreshFileTypeMap() {
      this.isLoading = true
      request({url: '/upload/fileType/read', method: 'get'}).then((response) => {
        this.isLoading = false
        const {data} = response
        this.fileTypeMap = [...data].join('\n')
      })
    }
  }
}
</script>
<style scoped lang="less">
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
        text-align: left;
        margin: 0px auto 10px auto;
        .help {
          margin: 0px;
          padding: 0px;
          position: absolute;
          top: 0;
          right: 0;
        }
      }
      .info {
        padding: 15px 20px;
        border: 1px dashed #ccc;
        border-radius: 10px;
        min-height: 100px;
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