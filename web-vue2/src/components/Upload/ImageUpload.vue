<!--
 * 上传图片组件
 * @Author: ziro
 * @Date: 2024/01/23 09:20:52
 -->
<template>
  <el-upload ref="imageUploader" :name="name"
             :headers="$store.getters.headerToken"
             :data="{'path':folder,...paramsData}"
             :action="action===''?$baseServer+'/upload/images':action"
             :file-list="fileShowList"
             :multiple="multiple"
             :before-upload="handleBeforeUpload"
             :on-success="handleSuccess"
             :on-remove="handleRemove"
             list-type="picture-card"
             accept="image/*">
    <i class="el-icon-plus"/>
  </el-upload>
</template>

<script>
export default {
  name: 'ImageUpload',
  props: {
    // 绑定值
    value: {type: Array, default: []},
    // 传入参数
    paramsData: {type: Object, default: {}},
    // file表单名称
    name: {type: String, default: 'imagefilename'},
    // 上传接口地址
    action: {type: String, default: ''},
    // 是否多选
    multiple: {type: Boolean, default: true},
    // 上传文件路径，可为空
    folder: {type: String, default: ''},
    // 上传文件大小限制，单位：kb（默认1mb）
    limitSize: {type: Number, default: 1024}
  },
  data() {
    return {
      // 图片列表
      fileList: [],
      // 回显图片列表
      fileShowList: [],
    }
  },
  mounted() {
    // 加载回显图片列表
    this.loadFileShowList()
  },
  methods: {
    // 加载回显图片列表
    loadFileShowList() {
      if (this.value && this.value.length > 0) {
        this.fileList = [...this.value]
        this.fileShowList = [...this.value.map(item => ({...item, name: item.fileOldName, url: item.fileUrl}))]
      }
    },
    // 文件图片前，校验图片大小和类型
    handleBeforeUpload(file) {
      // 判断图片大小
      let isRightSize = file.size / 1024 < this.limitSize
      if (!isRightSize) {
        let sizeStr = this.limitSize + 'KB';
        if (this.limitSize >= 1024) {
          sizeStr = (this.limitSize / 1024) + 'MB';
        }
        this.$message.error('图片大小超过 ' + sizeStr)
      }
      // 判断图片扩展名
      let isAccept = new RegExp('image/*').test(file.type)
      if (!isAccept) {
        this.$message.error('请选择正确的图片！')
      }
      return isRightSize && isAccept
    },
    // 图片上传成功
    handleSuccess(res, file, fileList) {
      if (res.code === '200') {
        this.$message({type: 'success', message: '图片上传成功！'})
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
