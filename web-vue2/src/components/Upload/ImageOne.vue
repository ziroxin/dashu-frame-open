<!--
 * 上传单图 - 有预览和删除按钮，删除后可重新上传
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
    <!-- 图片预览 -->
    <el-dialog :visible.sync="dialogVisible" :fullscreen="true" @click.native="dialogVisible=false"
               append-to-body custom-class="img-preview-dialog">
      <img width="100%" :src="dialogImageUrl" alt="" @click.stop="dialogVisible=true"/>
      <div class="img-text-info">按ESC键或点击遮罩可关闭预览</div>
    </el-dialog>
    <!-- 图片上传 -->
    <el-upload ref="imageOne"
               :headers="$store.getters.headerToken"
               :data="{'path':folder,...paramsData}"
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
               accept="image/*">
      <i class="el-icon-plus"/>
    </el-upload>
  </div>
</template>

<script>
export default {
  name: 'ImageOne',
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
      if (this.value && this.value !== null && this.value !== '') {
        this.imgList[0] = {url: this.$baseServer + this.value}
      } else {
        this.$emit('input', '')
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
<style lang="scss">
.hidden-btn .el-upload--picture-card {
  display: none;
}

.img-preview-dialog {
  background-color: rgba(0, 0, 0, 0.1) !important;
  text-align: center;

  .el-dialog__close {
    color: #fff !important;
    font-size: 24px !important;
  }

  img {
    position: absolute;
    left: 3%;
    max-width: 94%;
    top: 50%;
    transform: translateY(-50%);
  }

  .img-text-info {
    position: absolute;
    bottom: 10%;
    left: 50%;
    transform: translateX(-50%);
    color: #ccc;
    font-size: 14px;
    background-color: #00000080;
    padding: 10px 30px;
    border-radius: 40px;
    cursor: pointer;
  }
}
</style>
