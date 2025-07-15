<!--
 * 文件上传断点续传组件
 * 参数说明：
       modelValue: 可选，上传文件路径（通过v-model双向绑定），默认为空，传入正确路径可回显
       paramsData: 可选，调用上传接口时传入后台的参数（JSON格式）
       name: 可选，file表单的name属性，默认：filename
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
    <label v-if="isUploading" class="el-button is-plain mr-10px">
      <my-icon icon="el-icon-loading"/>
      正在上传...
    </label>
    <label v-else for="fileIpt" class="el-button is-plain mr-10px">
      <my-icon icon="el-icon-upload2"/>
      {{ btnTitle }}
      <input type="file" id="fileIpt" ref="fileIpt" @change="handleFileSelected" class="hidden" :accept="accept"/>
    </label>
    <!-- 操作提示 -->
    <el-tag size="small" type="info" v-if="showTip">{{ tipInfo }}</el-tag>
    <!-- 文件列表 -->
    <template v-for="item in fileList" :key="item.fileId">
      <div class="fileProgress">
        <!-- 文件信息 -->
        <div class="fileInfo">
          <!-- 文件名称 -->
          <div class="text-14px flex items-center">
            <my-icon icon="el-icon-document" class="mr-5px"/>
            {{ item.fileOldName + ' - ' || '' }}{{ formatSize(item.fileSize) }}
          </div>
          <!-- 文件操作 -->
          <div class="operate">
            <my-icon v-if="item.percentage===100" icon="el-icon-success" color="#67c23a" class="mr-10px"/>
            <my-icon icon="el-icon-close" @click.stop="handleRemove(item)"/>
          </div>
        </div>
        <!-- 文件上传进度条 -->
        <el-progress :stroke-width="2" :percentage="item.percentage" v-if="item.percentage<100"
                     style="margin-top:5px;"/>
      </div>
    </template>
  </div>
</template>
<script>
import SparkMD5 from 'spark-md5'
import { MyIcon } from '@/components/MyIcon'
import { generateUUID } from '@/utils/tools'
import request from '@/utils/request'

export default {
  name: 'FileUploadBreakpointResume',
  components: {MyIcon},
  props: {
    // 绑定值
    modelValue: {type: Array, default: () => []},
    // 传入参数
    paramsData: {type: Object, default: () => ({})},
    // file表单名称
    name: {type: String, default: 'filename'},
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
      // 是否正在上传
      isUploading: false
    }
  },
  mounted() {
    // 加载回显文件列表
    if (this.modelValue && this.modelValue.length > 0) {
      this.fileList = [...this.modelValue]
    }
  },
  methods: {
    // 选择文件上传
    async handleFileSelected(event) {
      // 上传前检测文件大小和类型等
      if (this.beforeUpload(event.target.files[0])) {
        this.isUploading = true
        const f = event.target.files[0]
        const fid = generateUUID()
        this.fileList.push({file: f, percentage: 0, fileId: fid, fileOldName: f.name, fileSize: f.size})
        await this.chunkFile(f, fid)
        this.$refs.fileIpt.value = '' // 清空选择文件框
        this.isUploading = false
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
          this.isUploading = false
          break // 文件已移除（手动点击删除按钮），停止上传
        }
        try {
          const md5 = await this.getChunkFileMd5(cFile)
          // 上传分片：分2步
          // 1. 只传递文件分片的MD5值，服务端根据MD5值判断分片是否已上传过，若已上传过，则直接返回已上传的分片信息
          const res1 = await request({
            url: '/upload/chunks/resume/first', method: 'get', headers: {skipRepeatSubmitCheck: true},
            params: {
              chunkNumber: i, // 分片索引
              totalChunks: chunks, // 总分片数
              tempFileName: file.name, // 原始文件名
              uploadId: uId, // 文件ID（断点续传唯一标识，用于合并分片文件等）
              path: this.folder, // 服务端存储路径，可为空
              chunkMD5: md5, // 分片文件MD5值
              ...this.paramsData // 其他参数
            }
          })
          // 2. 判断是否返回分片信息
          if (res1.data !== null) {
            // 2.1 后台返回分片信息，说明分片已存在，无需上传
            this.uploadProcess(res1.data, i, chunks, uId)
          } else {
            // 2.2 后台返回null，说明分片不存在，则带着文件上传该分片
            const res2 = await request({
              url: '/upload/chunks/resume/second', method: 'post',
              headers: {skipRepeatSubmitCheck: true, 'Content-Type': 'multipart/form-data'},
              data: {
                chunkNumber: i, // 分片索引
                totalChunks: chunks, // 总分片数
                tempFileName: file.name, // 原始文件名
                uploadId: uId, // 文件ID（断点续传唯一标识，用于合并分片文件等）
                path: this.folder, // 服务端存储路径，可为空
                file: cFile, // 分片文件
                ...this.paramsData // 其他参数
              }
            })
            this.uploadProcess(res2.data, i, chunks, uId)
          }
        } catch (e) {
          console.error('文件上传出错', e)
        }
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
        this.$emit('update:modelValue', this.fileList)
        this.isUploading = false
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
        const params = {fileUrl: file.fileUrl}
        request({url: 'upload/deleteFile', method: 'get', params})
      } catch (e) {
        console.log(e)
      }
      this.fileList = this.fileList.filter(item => item.fileId !== file.fileId)
      this.$emit('update:modelValue', this.fileList)
    },
    // 获取分片文件MD5值
    getChunkFileMd5(chunkFile) {
      return new Promise((resolve, reject) => {
        const fileReader = new FileReader()
        const spark = new SparkMD5.ArrayBuffer()
        fileReader.onload = function (e) {
          spark.append(e.target.result) // 将文件块内容添加到MD5计算中
          resolve(spark.end()) // 计算完成，调用回调函数返回MD5值
        }
        fileReader.onerror = function (e) {
          console.error('文件读取出错', e)
          reject(e)
        }
        fileReader.readAsArrayBuffer(chunkFile) // 读取文件块内容
      })
    },
    // 文件上传前，校验文件大小和类型
    beforeUpload(file) {
      // 判断文件大小
      const isRightSize = file.size < this.limitSize
      if (!isRightSize) {
        this.$message.error('文件大小不能超过' + this.formatSize(this.limitSize))
      }
      // 判断文件扩展名
      const fileExt = file.name.slice(file.name.lastIndexOf('.')).toLowerCase()
      const isAccept = this.accept.split(',').some(ext => ext.toLowerCase() === fileExt)
      if (!isAccept) {
        this.$message.error('上传文件格式错误！只能上传' + this.accept + '格式的文件')
      }
      // 判断上传个数
      const isLimitCount = this.limitCount <= 0 || this.fileList.length < this.limitCount
      if (!isLimitCount) {
        this.$message.error('上传数量超过限制！最多上传' + this.limitCount + '个文件！')
      }
      return isRightSize && isAccept && isLimitCount
    },
    // 格式化文件大小
    formatSize(size) {
      if (!size) return ''
      let sizeStr = size + 'B'
      if (size >= 1024 * 1024 * 1024) {
        sizeStr = (size / 1024 / 1024 / 1024).toFixed(2) + 'GB'
      } else if (size >= 1024 * 1024) {
        sizeStr = (size / 1024 / 1024).toFixed(2) + 'MB'
      } else if (size >= 1024) {
        sizeStr = (size / 1024).toFixed(2) + 'KB'
      } else {
        sizeStr = size + 'B'
      }
      return sizeStr
    }
  }
}
</script>
<style scoped lang="less">
.upload {
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
    }
  }
}
</style>