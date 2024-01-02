<template>
  <div>
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
    <div v-show="!uploadData" class="upload">
      <label for="fileInput" class="fileInputButton">
        <icon-show icon="el-icon-plus"></icon-show>
        选择文件
        <input type="file" id="fileInput" ref="fileInput" @change="handleFileChange" style="display:none"
               :accept="mimeTypes">
      </label>
      <el-button type="primary" icon="el-icon-upload2" size="small" @click="submitUpload">开始上传</el-button>
      <div v-if="currentFile" :key="currentFile.lastModified">
        <h2>{{ currentFile.name }}({{ formatSize(currentFile.size) }})</h2>
        <el-progress :text-inside="true" :stroke-width="20" :percentage="percentage" status="success"></el-progress>
      </div>
    </div>
  </div>
</template>
<script>
import SparkMD5 from 'spark-md5'
import IconShow from "@/components/IconShow/index.vue";
import {generateUUID} from '@/utils/tools'

export default {
  name: 'FileChunkResume',
  components: {IconShow},
  props: {
    // 上传地址
    uploadServerUrl: {type: String, required: true, default: '/upload/chunks/resume'},
    // 上传文件保存文件夹（可为空）
    uploadDir: {type: String, default: ''},
    // 分片大小(1mb)
    chunkSize: {type: Number, default: 1024 * 1024},
    // 允许上传的文件类型
    mimeTypes: {type: String, default: '.zip'},
    // 文件大小限制(10mb)
    maxFileSize: {type: Number, default: 1024 * 1024 * 10}
  },
  data() {
    return {
      // 当前选中的文件
      currentFile: null,
      // 上传进度
      percentage: 0,
      // 已上传完成内容
      uploadData: null,
    }
  },
  methods: {
    reloadUpload() {
      this.$refs.fileInput.value = ''
      this.currentFile = this.uploadData = null
      this.percentage = 0
    },
    handleFileChange(event) {
      this.currentFile = null
      if (this.beforeUpload(event.target.files[0])) {
        this.currentFile = event.target.files[0]
      }
    },
    submitUpload() {
      // 对文件分片，并上传
      this.chunkFile(this.currentFile, generateUUID())
    },
    async chunkFile(newFile, uploadId) {
      // 把文件分片
      const chunks = Math.ceil(newFile.size / this.chunkSize); // 计算分片数量
      for (let i = 0; i < chunks; i++) {
        const start = i * this.chunkSize;
        const end = start + this.chunkSize;
        const chunkFile = newFile.slice(start, end); // 使用slice方法获取分片
        // 上传分片
        await new Promise(async (resolve, reject) => {
          await this.getChunkFileMd5(chunkFile, async md5 => {
            const params = {
              chunkMD5: md5,
              chunk: i,
              chunks: chunks,
              name: newFile.name,
              uploadId: uploadId,
              path: this.uploadDir
            }
            await this.$request({
              url: this.uploadServerUrl, method: 'post', params, headers: {skipRepeatSubmitCheck: true}
            }).then(async (response) => {
              if (response.code === '200') {
                if (response.data !== null) {
                  // 上传成功处理
                  this.uploadProcess(response.data, i, chunks)
                } else {
                  // 分片不存在，新上传，更新状态
                  let data = {
                    chunk: i,
                    chunks: chunks,
                    name: newFile.name,
                    file: chunkFile,
                    uploadId: uploadId,
                    path: this.uploadDir
                  }
                  await this.$request({
                    url: this.uploadServerUrl, method: 'post', data,
                    headers: {skipRepeatSubmitCheck: true, 'Content-Type': 'multipart/form-data'}
                  }).then(async (response) => {
                    if (response.code === '200') {
                      // 上传成功处理
                      this.uploadProcess(response.data, i, chunks)
                    } else {
                      reject('上传失败，请重试')// 上传失败
                    }
                  })
                }
              } else {
                reject('上传失败，请重试')// 上传失败
              }
            })
            resolve()
          }).catch((e) => {
            console.log(e)
            resolve(e)
          })
        })
      }
    },
    uploadProcess(data, chunk, chunks) {
      // 判断是否上传成功
      if (data.merged) {
        // 最终上传完成
        this.uploadData = data
        this.percentage = 100
      } else {
        // 分片上传完成，更新进度条
        this.percentage = Number((chunk * 100 / chunks).toFixed(2))
      }
    },
    async getChunkFileMd5(chunkFile, callback) {
      const fileReader = new FileReader();
      const spark = new SparkMD5.ArrayBuffer();
      fileReader.onload = function (e) {
        spark.append(e.target.result) // 将文件块内容添加到MD5计算中
        callback(spark.end())// 计算完成，调用回调函数返回MD5值
      }
      fileReader.onerror = function () {
        console.error('文件读取出错')
        callback(null)
      }
      fileReader.readAsArrayBuffer(chunkFile) // 读取文件块内容
    },
    beforeUpload(file) {
      const fileExtension = file.name.slice(file.name.lastIndexOf('.'));
      if (!this.mimeTypes.split(',').includes(fileExtension)) {
        this.$message.error('只能上传' + this.mimeTypes + '格式的文件');
        return false
      }
      if (file.size > this.maxFileSize) {
        this.$message.error('文件大小不能超过' + this.formatSize(this.maxFileSize));
        return false
      }
      return true
    },
    formatSize(size) {
      let sizeStr = size + 'B';
      if (size >= 1024 * 1024 * 1024) {
        sizeStr = (size / 1024 / 1024 / 1024).toFixed(2) + 'GB'
      } else if (size >= 1024 * 1024) {
        sizeStr = (size / 1024 / 1024).toFixed(2) + 'MB'
      } else if (size >= 1024) {
        sizeStr = (size / 1024).toFixed(2) + 'KB'
      } else {
        sizeStr = size + 'B'
      }
      return sizeStr;
    }
  }
}
</script>
<style scoped lang="scss">
.upload {
  .fileInputButton {
    padding: 9px 15px;
    font-size: 12px;
    border-radius: 3px;
    display: inline-block;
    line-height: 1;
    white-space: nowrap;
    cursor: pointer;
    background: #fff;
    border: 1px solid #dcdfe6;
    -webkit-appearance: button;
    text-align: center;
    box-sizing: border-box;
    outline: 0;
    margin: 0;
    transition: .1s;
    font-weight: 500;
    -moz-user-select: none;
    -webkit-user-select: none;
    -ms-user-select: none;
    margin-right: 10px;
    color: #606266;

    &:hover {
      color: #4080ff;
      border-color: #c6d9ff;
      background-color: #ecf2ff;
    }
  }
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