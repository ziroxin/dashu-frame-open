<template>
  <div>
    <div v-show="isSupport">
      <div v-if="uploadData">
        <el-button type="danger" icon="el-icon-refresh" size="small" @click="reloadUpload">重新上传</el-button>
        <div class="uploadDataInfo">
          <div>上传成功 <a :href="$baseServer+uploadData.fileUrl" target="_blank">[点击下载]</a> 其他文件信息：</div>
          <div>原文件名:{{ uploadData.fileOldName }}</div>
          <div>服务器文件名:{{ uploadData.fileName }}</div>
          <div>文件扩展名:{{ uploadData.fileExtend }}</div>
          <div>文件大小:{{ formatSize(uploadData.fileSize) }}</div>
        </div>
      </div>
      <div v-show="!uploadData" class="plupload">
        <div>
          <el-button id="selectFileBtn" icon="el-icon-plus" size="small">选择文件</el-button>
          <el-button type="primary" icon="el-icon-upload2" size="small" @click="uploadChunk">开始上传</el-button>
        </div>
        <div v-if="currentFile" :key="currentFile.id">
          <h2>{{ currentFile.name }}({{ formatSize(currentFile.size) }})</h2>
          <el-progress :text-inside="true" :stroke-width="20" :percentage="percentage" status="success"></el-progress>
        </div>
      </div>
    </div>
    <div v-show="!isSupport" class="noSupport">
      您的浏览器不支持Flash,Silverlight或者HTML5！
    </div>
  </div>
</template>
<script>
import plupload from 'plupload'
import {generateUUID} from '@/utils/tools'

export default {
  name: 'PluploadChunk',
  props: {
    // 上传地址
    uploadServerUrl: {type: String, required: true, default: '/upload/chunks'},
    // 上传文件保存的文件夹（可为空）
    uploadDir: {type: String, default: ''},
    // 分片大小
    chunkSize: {type: String, default: '1mb'},
    // 允许上传的文件类型
    mimeTypes: {type: Array, default: () => [{title: 'Zip files', extensions: 'zip'}]},
    // 文件大小限制
    maxFileSize: {type: String, default: '10mb'}
  },
  data() {
    return {
      // 是否支持plupload
      isSupport: false,
      // 当前选中的文件
      currentFile: null,
      // 分片数量
      totalChunks: 0,
      // 上传进度
      percentage: 0,
      // 已上传完成内容
      uploadData: null,
      // 上传组件
      uploader: null,
    }
  },
  mounted() {
    this.init()
  },
  methods: {
    uploadChunk() {
      this.uploader.setOption("multipart_params", {uploadId: generateUUID()})
      this.uploader.start();
    },
    formatSize(size) {
      return plupload.formatSize(size)
    },
    reloadUpload() {
      this.currentFile = this.uploadData = null
      this.totalChunks = this.percentage = 0
      this.uploader.refresh()
    },
    async init() {
      this.uploader = new plupload.Uploader({
        runtimes: 'html5,flash,silverlight',
        browse_button: document.getElementById("selectFileBtn"),
        url: this.$baseServer + this.uploadServerUrl + "?path=" + this.uploadDir, // 替换为你的上传处理URL
        filters: {
          mime_types: this.mimeTypes,
          max_file_size: this.maxFileSize, // 最大只能上传100mb的文件
          prevent_duplicates: true
        },
        chunk_size: this.chunkSize,
        multipart: true,
        file_data_name: 'file',
        multi_selection: false,
        init: {
          // 初始化上传组件
          PostInit: () => {
            this.isSupport = true // 上传支持
          },
          // 选择文件
          FilesAdded: (up, files) => {
            const upFiles = [...up.files]
            upFiles.forEach((file) => {
              if (file.id === files[0].id) {
                this.currentFile = file
              } else {
                up.removeFile(file)
              }
            })
          },
          // 上传进度
          UploadProgress: (up, file) => {
            console.log('upload progress', file.percent)
            this.percentage = file.percent // 上传进度
          },
          // 上传完成
          FileUploaded: (up, file, info) => {
            console.log('file uploaded', info)
            const data = JSON.parse(info.response)
            if (info.status === 200 && data.code === "200") {
              this.uploadData = data.data
              this.$message({type: 'success', message: '文件上传成功！'})
            } else {
              this.$message({type: 'error', message: '文件上传失败，请刷新后重新上传！'})
            }
          },
          // 错误
          Error: (up, err) => {
            if (err.code === -600) {
              this.$message({type: 'error', message: '选择的文件太大了，限制：' + this.maxFileSize})
            } else if (err.code === -601) {
              this.$message({type: 'error', message: '选择的文件类型不支持'})
            } else if (err.code === -602) {
              this.$message({type: 'error', message: '文件重复！请重新选择'})
            } else {
              this.$message({type: 'error', message: '上传错误，错误信息:' + err.response})
            }
          }
        }
      })
      this.uploader.init();
    },
  },
  destroyed() {
    // 销毁
    this.uploader.destroy();
  }
}
</script>
<style scoped lang="scss">
.noSupport {
  text-align: center;
  color: #dd1f29;
}

.uploadDataInfo {
  line-height: 30px;
  font-size: 14px;
  margin-top: 10px;
  border: 1px dashed #ccc;
  border-radius: 10px;
  padding: 20px;
  cursor: pointer;

  &:hover {
    background-color: rgba(185, 185, 185, 0.1);
  }

  a {
    margin: auto 10px;
    color: #2C7EEA;
  }
}
</style>