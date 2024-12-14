<!--
 * 上传文件组件
 * @Author: ziro
 * @Date: 2024/12/14 11:20:52
 -->
<template>
  <el-upload ref="fileUploader" :name="name"
             :headers="$store.getters.headerToken"
             :data="{'path':folder}"
             :action="action===''?$baseServer+'/upload/files':action"
             :show-file-list="showFileList"
             :file-list="fileShowList"
             :multiple="multiple"
             :before-upload="handleBeforeUpload"
             :on-success="handleSuccess"
             :on-remove="handleRemove"
             :accept="accept"
             :auto-upload="autoUpload">
    <el-button type="primary" icon="el-icon-upload2" size="small">{{ btnTitle }}</el-button>
    <el-tag slot="tip" size="small" type="info" style="margin-left:10px" v-if="showTip">{{ tipInfo }}</el-tag>
  </el-upload>
</template>

<script>
export default {
  name: 'FileUpload',
  props: {
    // 上传图片路径
    value: {type: Array, default: []},
    // 表单名称
    name: {type: String, default: 'filename'},
    // 上传接口地址
    action: {type: String, default: ''},
    // 上传文件类型
    accept: {type: String, default: '.jpg,.png,.jpeg,.gif,.doc,.docx,.xls,.xlsx,.pdf,.zip,.rar'},
    // 上传按钮标题
    btnTitle: {type: String, default: '点击上传文件'},
    // 是否显示提示信息
    showTip: {type: Boolean, default: true},
    // 提示信息
    tipInfo: {type: String, default: '支持图片、Word、Excel、Pdf、Rar、Zip格式的文件'},
    // 是否多选
    multiple: {type: Boolean, default: true},
    // 是否显示文件列表
    showFileList: {type: Boolean, default: true},
    // 是否自动上传
    autoUpload: {type: Boolean, default: true},
    // 上传文件路径，可为空
    folder: {type: String, default: ''},
    // 上传文件大小限制，单位：kb（默认1mb）
    limitSize: {type: Number, default: 1024}
  },
  data() {
    return {
      // 文件列表
      fileList: [],
      // 回显文件列表
      fileShowList: [],
    }
  },
  mounted() {
    // 加载回显文件列表
    this.loadFileShowList()
  },
  methods: {
    // 加载回显文件列表
    loadFileShowList() {
      if (this.value && this.value.length > 0) {
        this.fileList = [...this.value]
        this.fileShowList = [...this.value.map(item => ({...item, name: item.fileOldName, url: item.fileUrl}))]
      }
    },
    // 文件上传前，校验文件大小和类型
    handleBeforeUpload(file) {
      // 判断文件大小
      let isRightSize = file.size / 1024 < this.limitSize
      if (!isRightSize) {
        let sizeStr = this.limitSize + 'KB';
        if (this.limitSize >= 1024) {
          sizeStr = (this.limitSize / 1024) + 'MB';
        }
        this.$message.error('文件大小超过 ' + sizeStr)
      }
      // 判断文件扩展名
      const fileExtend = file.name.substring(file.name.lastIndexOf('.')).toLowerCase()
      let isAccept = this.accept.split(',').some(extend => extend.toLowerCase() === fileExtend)
      if (!isAccept) {
        this.$message.error('请选择正确的格式的文件！')
      }
      return isRightSize && isAccept
    },
    // 文件上传成功
    handleSuccess(res, file, fileList) {
      if (res.code === '200') {
        this.$message({type: 'success', message: '文件上传成功！'})
      } else {
        this.$message({type: 'error', message: res.message})
        fileList.splice(fileList.indexOf(file), 1)
      }
      this.fileList = fileList.map(o => o.response && o.response.data.length > 0 ? o.response.data[0] : o)
      this.$emit('input', this.fileList)
    },
    handleRemove(file, fileList) {
      try {
        let params = {}
        if (file.response && file.response.data.length > 0) {
          params.fileUrl = file.response.data[0].fileUrl
        } else {
          params.fileUrl = file.fileUrl
        }
        this.$request({url: 'upload/deleteFile', method: 'get', params})
      } catch (e) {
      }
      this.fileList = fileList.map(o => o.response && o.response.data.length > 0 ? o.response.data[0] : o)
      this.$emit('input', this.fileList)
    },
  }
}
</script>
