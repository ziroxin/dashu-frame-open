<!--
 * 上传单张图片 - 可删除，可放大
 * @Author: ziro
 * @Date: 2023/01/02 15:16:40
 -->
<template>
  <div>
    <!-- 图片预览 -->
    <el-dialog :visible.sync="dialogVisible" :fullscreen="true" append-to-body>
      <img width="100%" :src="dialogImageUrl" alt="">
    </el-dialog>
    <!-- 图片上传 -->
    <el-upload ref="imageOne"
               :data="{'path':path}"
               :name="name"
               :action="action===''?$baseServer+'/upload/images':action"
               :file-list.sync="imgList"
               :multiple="false"
               :on-preview="imgPreview"
               :before-upload="imgBeforeUpload"
               :on-success="imgUploadSuccess"
               :on-remove="imgRemove"
               :class="value!==''?'hidden-btn':''"
               list-type="picture-card"
               accept="image/*"
    >
      <i class="el-icon-plus" />
    </el-upload>
  </div>
</template>

<script>
export default {
  name: 'ImageOne',
  props: {
    // 上传图片路径
    value: {type: String, default: ''},
    // 表单名称
    name: {type: String, default: 'filename'},
    // 上传接口地址
    action: {type: String, default: ''},
    // 上传文件路径，可为空
    path: {type: String, default: ''},
    // 上传文件大小限制，单位：kb（默认1mb）
    limitSize: {type: Number, default: 1024}
  },
  data() {
    return {
      // 图片列表
      imgList: [],
      // 图片预览
      dialogVisible: false,
      dialogImageUrl: ''
    }
  },
  watch: {
    value() {
      this.loadImg()
    }
  },
  mounted() {
    this.loadImg()
  },
  methods: {
    loadImg() {
      this.imgList = []
      if (this.value !== '') {
        let file = {url: this.$baseServer + this.value}
        this.imgList[0] = file
      }
    },
    imgUploadSuccess(response, file, fileList) {
      // 给value赋值
      this.$emit('input', response.data[0].fileUrl)
    },
    imgRemove(file, fileList) {
      // 清空value
      this.$emit('input', '')
    },
    // 图片大小和格式限制
    imgBeforeUpload(file) {
      let isRightSize = file.size / 1024 < this.limitSize
      if (!isRightSize) {
        let sizeStr = this.limitSize + 'KB';
        if (this.limitSize >= 1024) {
          sizeStr = (this.limitSize / 1024) + 'MB';
        }
        this.$message.error('文件大小超过 ' + sizeStr)
      }
      let isAccept = new RegExp('image/*').test(file.type)
      if (!isAccept) {
        this.$message.error('请选择正确的图片！')
      }
      return isRightSize && isAccept
    },
    // 预览图片
    imgPreview(file) {
      this.dialogImageUrl = file.url;
      this.dialogVisible = true;
    }
  }
}
</script>

<style>
.hidden-btn .el-upload--picture-card {
  display: none;
}
</style>
