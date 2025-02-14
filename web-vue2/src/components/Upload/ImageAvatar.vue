<!--
 * 上传头像（单图） - 无删除按钮，点击替换
 * 参数说明：
       value: 可选，上传图片路径（通过v-model双向绑定），默认为空，传入正确路径可回显
       paramsData: 可选，调用上传接口时传入后台的参数（JSON格式）
       name: 可选，file表单的name属性，默认：filename
       action: 可选，上传接口地址，默认：/upload/images
       folder: 可选，服务端存储文件夹，默认空
       limitSize: 可选，上传文件大小限制，单位：kb（默认1mb）
 * @Author: ziro
 * @Date: 2023/01/02 15:16:40
 -->
<template>
  <div>
    <el-upload ref="imageAvatar" class="avatar-uploader"
               :headers="$store.getters.headerToken"
               :data="{'path':folder,...paramsData}"
               :name="name"
               :action="action===''?$baseServer+'/upload/images':action"
               :show-file-list="false"
               :multiple="false"
               :before-upload="imgBeforeUpload"
               :on-success="imgUploadSuccess"
               accept="image/*">
      <img v-if="dialogImageUrl" :src="dialogImageUrl" class="avatar">
      <i v-else class="el-icon-plus avatar-uploader-icon"/>
    </el-upload>
  </div>
</template>

<script>
export default {
  name: 'ImageAvatar',
  props: {
    // 上传图片路径
    value: {type: String, default: ''},
    // 传入参数
    paramsData: {type: Object, default: () => ({})},
    // 表单名称
    name: {type: String, default: 'filename'},
    // 上传接口地址
    action: {type: String, default: ''},
    // 上传文件路径，可为空
    folder: {type: String, default: ''},
    // 上传文件大小限制，单位：kb（默认1mb）
    limitSize: {type: Number, default: 1024}
  },
  data() {
    return {
      // 图片列表
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
      this.dialogImageUrl = ''
      if (this.value && this.value !== null && this.value !== '') {
        this.dialogImageUrl = this.$baseServer + this.value
      }
    },
    imgUploadSuccess(response, file, fileList) {
      // 给value赋值
      this.$emit('input', response.data[0].fileUrl)
      this.dialogImageUrl = URL.createObjectURL(file.raw)
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
    }
  }
}
</script>
<style>
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>
