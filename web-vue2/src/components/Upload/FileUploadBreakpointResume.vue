<!--
 * 文件上传断点续传组件
 * 参数说明：
       value: 可选，上传文件路径（通过v-model双向绑定），默认为空，传入正确路径可回显
       paramsData: 可选，调用上传接口时传入后台的参数（JSON格式）
       name: 可选，file表单的name属性，默认：filename
       action: 可选，上传接口地址，默认：/upload/chunks/resume
       accept: 可选，上传文件类型（格式举例：.zip,.rar）
       btnTitle: 可选，上传按钮显示文字，默认：点击上传文件
       showTip: 可选，是否显示提示信息，默认：true
       tipInfo: 可选，提示信息，默认：支持图片、Word、Excel、Pdf、Rar、Zip格式的文件
       folder: 可选，服务端存储文件夹，默认空
       chunkSize: 可选，分片大小，单位：b（默认5mb）
       limitSize: 可选，上传文件大小限制，单位：b（默认100mb）
       limitCount: 可选，上传个数限制（0表示不限制），默认：0

 * @Author: ziro
 * @Date: 2025/4/21 13:47:52
-->
<template>
  <div class="upload">
    <!-- 选择文件上传按钮 -->
    <label for="fileIpt" class="fileInputButton">
      <el-icon class="el-icon-upload2"/>
      {{ btnTitle }}
      <input type="file" id="fileIpt" ref="fileIpt" @change="handleFileSelected" style="display:none" :accept="accept">
    </label>
    <!-- 操作提示 -->
    <el-tag size="small" type="info" v-if="showTip">{{ tipInfo }}</el-tag>
    <!-- 文件列表 -->
    <template v-for="item in fileList">
      <div :key="item.fileId" class="fileProgress">
        <!-- 文件信息 -->
        <div class="fileInfo">
          <!-- 文件名称 -->
          <div class="title">
            <el-icon class="el-icon-document"/>
            {{ item.fileOldName + ' - ' || '' }}{{ formatSize(item.fileSize) }}
          </div>
          <!-- 文件操作 -->
          <div class="operate">
            <el-icon class="el-icon-success" v-if="item.percentage===100"/>
            <el-icon class="el-icon-close" @click.native="handleRemove(item)"/>
          </div>
        </div>
        <!-- 文件上传进度条 -->
        <el-progress :stroke-width="2" :percentage="item.percentage" v-if="item.percentage<100"/>
      </div>
    </template>
  </div>
</template>
<script>
import SparkMD5 from 'spark-md5'
import IconShow from "@/components/IconShow/index.vue";
import {generateUUID} from '@/utils/tools'

export default {
  name: 'FileUploadBreakpointResume',
  components: {IconShow},
  props: {
    // 绑定值
    value: {type: Array, default: () => []},
    // 传入参数
    paramsData: {type: Object, default: () => ({})},
    // file表单名称
    name: {type: String, default: 'filename'},
    // 上传接口地址
    action: {type: String, default: '/upload/chunks/resume'},
    // 上传文件类型
    accept: {type: String, default: '.zip,.rar'},
    // 上传按钮标题
    btnTitle: {type: String, default: '点击上传文件'},
    // 是否显示提示信息
    showTip: {type: Boolean, default: true},
    // 提示信息
    tipInfo: {type: String, default: '支持Rar、Zip格式，单个视频最大100MB'},
    // 上传文件路径，可为空
    folder: {type: String, default: ''},
    // 分片大小，单位：b（默认5mb）
    chunkSize: {type: Number, default: 1024 * 1024 * 5},
    // 上传文件大小限制，单位：b（默认100mb）
    limitSize: {type: Number, default: 1024 * 1024 * 100},
    // 上传文件个数限制（0表示不限制）
    limitCount: {type: Number, default: 0}
  },
  data() {
    return {
      // 已上传完成内容
      fileList: [],
    }
  },
  mounted() {
    // 加载回显文件列表
    if (this.value && this.value.length > 0) {
      this.fileList = [...this.value]
    }
  },
  methods: {
    // 选择文件上传
    handleFileSelected(event) {
      // 上传前检测文件大小和类型等
      if (this.beforeUpload(event.target.files[0])) {
        let f = event.target.files[0]
        let fid = generateUUID()
        this.fileList.push({file: f, percentage: 0, fileId: fid, fileOldName: f.name, fileSize: f.size})
        this.chunkFile(f, fid)
      }
    },
    // 分片上传方法
    async chunkFile(file, uId) {
      // 计算分片数量
      const chunks = Math.ceil(file.size / this.chunkSize)
      // 循环上传分片
      for (let i = 0; i < chunks; i++) {
        const start = i * this.chunkSize
        const end = start + this.chunkSize
        const cFile = file.slice(start, end) // 使用slice方法获取分片
        if (!this.fileList.some(item => item.fileId === uId)) {
          break // 文件已移除，停止上传
        }
        await new Promise(async (resolve, reject) => { // 上传分片
          await this.getChunkFileMd5(cFile, async md5 => {
            let params = {
              chunk: i, chunks: chunks, name: file.name, uploadId: uId, path: this.folder,
              chunkMD5: md5, ...this.paramsData
            }
            let header1 = {skipRepeatSubmitCheck: true}
            await this.$request({url: this.action, method: 'post', params, headers: header1}).then(async res1 => {
              if (res1.code !== '200') {
                reject('上传失败，请重试')
              }
              if (res1.data !== null) {
                // 分片已存在（redis缓存），无需上传，直接更新状态
                this.uploadProcess(res1.data, i, chunks, uId)
              } else {
                // 分片不存在，新上传，然后更新状态
                let data = {
                  chunk: i, chunks: chunks, name: file.name, uploadId: uId, path: this.folder,
                  file: cFile, ...this.paramsData
                }
                let header2 = {skipRepeatSubmitCheck: true, 'Content-Type': 'multipart/form-data'}
                await this.$request({url: this.action, method: 'post', data, headers: header2}).then(async res2 => {
                  if (res2.code !== '200') {
                    reject('上传失败，请重试')
                  }
                  this.uploadProcess(res2.data, i, chunks, uId)
                })
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
    // 分片上传进度处理方法
    uploadProcess(data, chunk, chunks, uId) {
      if (data.merged) {
        // 最终上传完成
        this.$message({type: 'success', message: '文件上传成功！'})
        this.fileList = this.fileList.map(item => {
          if (item.fileId === uId) {
            item = {...item, ...data, percentage: 100}
            delete item.file
          }
          return item
        })
        this.$emit('input', this.fileList)
      } else {
        // 分片上传完成，更新进度条
        this.fileList.forEach(item => {
          if (item.fileId === uId) item.percentage = Number((chunk * 100 / chunks).toFixed(2))
        })
      }
    },
    // 移除已上传文件
    handleRemove(file) {
      try {
        let params = {fileUrl: file.fileUrl}
        this.$request({url: 'upload/deleteFile', method: 'get', params})
      } catch (e) {
      }
      this.fileList = this.fileList.filter(item => item.fileId !== file.fileId)
      this.$emit('input', this.fileList)
    },
    // 获取分片文件MD5值
    async getChunkFileMd5(chunkFile, callback) {
      const fileReader = new FileReader();
      const spark = new SparkMD5.ArrayBuffer();
      fileReader.onload = function (e) {
        spark.append(e.target.result) // 将文件块内容添加到MD5计算中
        callback(spark.end()) // 计算完成，调用回调函数返回MD5值
      }
      fileReader.onerror = function () {
        console.error('文件读取出错')
        callback(null)
      }
      fileReader.readAsArrayBuffer(chunkFile) // 读取文件块内容
    },
    // 文件上传前，校验文件大小和类型
    beforeUpload(file) {
      // 判断文件大小
      let isRightSize = file.size < this.limitSize
      if (!isRightSize) {
        this.$message.error('文件大小不能超过' + this.formatSize(this.limitSize));
      }
      // 判断文件扩展名
      const fileExt = file.name.slice(file.name.lastIndexOf('.')).toLowerCase()
      let isAccept = this.accept.split(',').some(ext => ext.toLowerCase() === fileExt)
      if (!isAccept) {
        this.$message.error('上传文件格式错误！只能上传' + this.accept + '格式的文件');
      }
      // 判断上传个数
      let isLimitCount = this.limitCount <= 0 || this.fileList.length < this.limitCount
      if (!isLimitCount) {
        this.$message.error('上传数量超过限制！最多上传' + this.limitCount + '个文件！')
      }
      return isRightSize && isAccept && isLimitCount
    },
    // 格式化文件大小
    formatSize(size) {
      if (!size) return ''
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
    padding: 10px 20px;
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

  // 上传进度展示
  .fileProgress {
    .fileInfo {
      display: flex;
      justify-content: space-between;
      align-items: center;
      border-radius: 5px;
      background-color: #fafafa;
      color: #717377;
      margin: 5px auto 0 auto;
      padding: 0px 10px;

      &:hover {
        background-color: #f3f3f3;
        cursor: pointer;
      }

      .title {
        font-size: 14px;
      }

      .operate {
        font-size: 18px;

        .el-icon-success {
          color: #67c23a;
          margin-right: 10px;
        }
      }
    }

  }
}
</style>