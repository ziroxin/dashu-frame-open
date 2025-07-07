<!--
 * 上传多图组件
 * 参数说明：
       value: 可选，上传图片路径（通过v-model双向绑定），默认为空，传入正确路径可回显
       paramsData: 可选，调用上传接口时传入后台的参数（JSON格式）
       name: 可选，file表单的name属性，默认：filename
       action: 可选，上传接口地址，默认：/upload/images
       multiple: 可选，是否多选，默认：true
       folder: 可选，服务端存储文件夹，默认空
       limitSize: 可选，上传文件大小限制，单位：b（默认10mb）
       limitCount: 可选，上传个数限制（0表示不限制），默认：0
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
    value: {type: Array, default: () => []},
    // 传入参数
    paramsData: {type: Object, default: () => ({})},
    // file表单名称
    name: {type: String, default: 'filename'},
    // 上传接口地址
    action: {type: String, default: ''},
    // 是否多选
    multiple: {type: Boolean, default: true},
    // 上传文件路径，可为空
    folder: {type: String, default: ''},
    // 上传文件大小限制，单位：b（默认10mb）
    limitSize: {type: Number, default: 1024 * 1024 * 10},
    // 上传个数限制（0表示不限制）
    limitCount: {type: Number, default: 0},
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
        this.fileShowList = [...this.value.map(item => ({
          ...item,
          name: item.fileOldName,
          url: this.$baseServer + item.fileUrl
        }))]
      }
    },
    // 文件图片前，校验图片大小和类型
    handleBeforeUpload(file) {
      // 判断图片大小
      let isRightSize = file.size < this.limitSize
      if (!isRightSize) {
        this.$message.error('图片大小不能超过 ' + this.formatSize(this.limitSize))
      }
      // 判断图片扩展名
      let isAccept = new RegExp('image/*').test(file.type)
      if (!isAccept) {
        this.$message.error('请选择正确的图片！')
      }
      // 判断上传个数
      let isLimitCount = this.limitCount <= 0 || this.fileList.length < this.limitCount
      if (!isLimitCount) {
        this.$message.error('上传数量超过限制！最多上传' + this.limitCount + '张图片！')
      }
      return isRightSize && isAccept && isLimitCount
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
