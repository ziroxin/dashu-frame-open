<!--
 * 上传单图 - 有预览和删除按钮，删除后可重新上传
 * 参数说明：
       value: 可选，上传图片路径（通过v-model双向绑定），默认为空，传入正确路径可回显
       paramsData: 可选，调用上传接口时传入后台的参数（JSON格式）
       name: 可选，file表单的name属性，默认：filename
       action: 可选，上传接口地址，默认：/upload/images
       folder: 可选，服务端存储文件夹，默认空
       limitSize: 可选，上传文件大小限制，单位：b（默认10mb）
       imgWidth: 可选，图片卡片宽度，单位px（默认148）
       imgHeight: 可选，图片卡片高度，单位px（默认148；如3:2设 宽150 高100）
 * @Author: ziro
 * @Date: 2023/01/02 15:16:40
 -->
<template>
  <div class="img-upload-size pic-flexbox" :style="imgSizeStyle">
    <!-- 已上传图片：点击打开el-image官方查看器，hover右上角删除 -->
    <div v-if="value" class="pic-card">
      <el-image class="pic-img" :src="$baseServer + value" :preview-src-list="[$baseServer + value]" fit="cover"/>
      <i v-if="!isDisabled" class="el-icon-delete pic-del" @click="imgRemove"></i>
    </div>
    <!-- 图片上传 -->
    <el-upload ref="imageOne"
               :headers="$store.getters.headerToken"
               :data="{'path':folder,...paramsData}"
               :name="name"
               :action="action===''?$baseServer+'/upload/images':action"
               :multiple="false"
               :show-file-list="false"
               :disabled="isDisabled"
               :before-upload="imgBeforeUpload"
               :on-success="imgUploadSuccess"
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
    // 上传文件大小限制，单位：b（默认10mb）
    limitSize: {type: Number, default: 1024 * 1024 * 10},
    // 图片卡片宽度，单位px（默认148，正方形）
    imgWidth: {type: Number, default: 148},
    // 图片卡片高度，单位px（默认148，正方形；如3:2可设 宽150 高100）
    imgHeight: {type: Number, default: 148}
  },
  inject: {elForm: {default: null}},
  computed: {
    imgSizeStyle() {
      return {'--img-w': this.imgWidth + 'px', '--img-h': this.imgHeight + 'px'}
    },
    // 表单disabled（只读场景）联动禁用上传/删除
    isDisabled() {
      return !!(this.elForm && this.elForm.disabled)
    }
  },
  methods: {
    imgUploadSuccess(response, file, fileList) {
      // 给value赋值
      this.$emit('input', response.data[0].fileUrl)
    },
    imgRemove() {
      // 清空value
      this.$emit('input', '')
    },
    // 图片大小和格式限制
    imgBeforeUpload(file) {
      // 判断文件大小
      let isRightSize = file.size < this.limitSize
      if (!isRightSize) {
        this.$message.error('图片大小不能超过 ' + this.formatSize(this.limitSize))
      }
      // 判断图片扩展名
      let isAccept = new RegExp('image/*').test(file.type)
      if (!isAccept) {
        this.$message.error('请选择正确的图片！')
      }
      return isRightSize && isAccept
    },
    formatSize(size) {
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
<style lang="scss">
.hidden-btn {
  line-height: 0 !important;
}

.hidden-btn .el-upload--picture-card {
  display: none;
}
</style>
<style lang="scss" scoped>
.img-upload-size {
  ::v-deep .el-upload--picture-card {
    width: var(--img-w, 148px);
    height: var(--img-h, 148px);
    line-height: var(--img-h, 148px);
  }
}

.pic-flexbox {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-start;
}

.pic-card {
  position: relative;
  width: var(--img-w, 148px);
  height: var(--img-h, 148px);
  border: 1px solid #c0ccda;
  border-radius: 6px;
  overflow: hidden;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  margin: 0 8px 8px 0;
}

.pic-img {
  width: 100%;
  height: 100%;
  display: block;
  cursor: pointer;

  ::v-deep img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
  }
}

.pic-del {
  position: absolute;
  top: 4px;
  right: 4px;
  z-index: 6;
  width: 22px;
  height: 22px;
  line-height: 22px;
  text-align: center;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  font-size: 12px;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.3s;
}

.pic-card:hover .pic-del {
  opacity: 1;
}
</style>
