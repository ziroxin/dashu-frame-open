<!--
 * 上传多图组件
 * 参数说明：
       value: 必选，已上传图片对象数组（[{fileUrl, fileOldName...}]，通过v-model双向绑定），数组顺序即图片顺序
       paramsData: 可选，调用上传接口时传入后台的参数（JSON格式）
       name: 可选，file表单的name属性，默认：filename
       action: 可选，上传接口地址，默认：/upload/images
       multiple: 可选，是否多选，默认：true
       folder: 可选，服务端存储文件夹，默认空
       limitSize: 可选，上传文件大小限制，单位：b（默认10mb）
       limitCount: 可选，上传个数限制（0表示不限制），默认：0
       imgWidth: 可选，图片卡片宽度，单位px（默认148）
       imgHeight: 可选，图片卡片高度，单位px（默认148；如3:2设 宽150 高100）
       sortable: 可选，是否排序（默认false）；true时卡片显示序号，可拖拽调整顺序；预览两种模式均为el-image官方查看器
 * @Author: ziro
 * @Date: 2024/01/23 09:20:52
 -->
<template>
  <div class="img-upload-size pic-flexbox" :class="hideTrigger ? 'hide-trigger' : ''" :style="imgSizeStyle">
    <!-- 图片卡片：el-image官方预览 + 删除；sortable时带序号且可拖拽排序 -->
    <div v-for="(item, i) in value" :key="'pic' + i" class="pic-card"
         :draggable="sortable && !isDisabled"
         @dragstart="handleDragStart(i)"
         @dragenter="handleDragEnter(i)"
         @dragover.prevent
         @dragend="handleDragEnd">
      <span v-if="sortable" class="pic-index">{{ i + 1 }}</span>
      <el-image class="pic-img" :src="$baseServer + item.fileUrl" :preview-src-list="picList" fit="cover"/>
      <i v-if="!isDisabled" class="el-icon-delete pic-del" @click.stop="removeAt(i)"></i>
    </div>
    <el-upload ref="imageUploader" :name="name"
             :headers="$store.getters.headerToken"
             :data="{'path':folder,...paramsData}"
             :action="action===''?$baseServer+'/upload/images':action"
             :multiple="multiple"
             :show-file-list="false"
             :disabled="isDisabled"
             :before-upload="handleBeforeUpload"
             :on-success="handleSuccess"
             list-type="picture-card"
             accept="image/*">
      <i class="el-icon-plus"/>
    </el-upload>
  </div>
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
    // 图片卡片宽度，单位px（默认148，正方形）
    imgWidth: {type: Number, default: 148},
    // 图片卡片高度，单位px（默认148，正方形；如3:2可设 宽150 高100）
    imgHeight: {type: Number, default: 148},
    // 是否排序：true时显示图片序号，支持拖拽调整顺序（v-model数组顺序即图片顺序）
    sortable: {type: Boolean, default: false}
  },
  inject: {elForm: {default: null}},
  computed: {
    imgSizeStyle() {
      return {'--img-w': this.imgWidth + 'px', '--img-h': this.imgHeight + 'px'}
    },
    // 表单disabled（查看详情等只读场景）联动禁用上传/拖拽/删除
    isDisabled() {
      return !!(this.elForm && this.elForm.disabled)
    },
    // 达到数量上限时隐藏上传按钮
    hideTrigger() {
      return this.limitCount > 0 && (this.value || []).length >= this.limitCount
    },
    // 查看器图片列表（预览时可多张切换）
    picList() {
      return (this.value || []).map(o => this.$baseServer + o.fileUrl)
    }
  },
  data() {
    return {
      // 拖拽中的序号
      dragIndex: -1,
    }
  },
  methods: {
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
      let isLimitCount = this.limitCount <= 0 || (this.value || []).length < this.limitCount
      if (!isLimitCount) {
        this.$message.error('上传数量超过限制！最多上传' + this.limitCount + '张图片！')
      }
      return isRightSize && isAccept && isLimitCount
    },
    // 图片上传成功：追加到v-model数组
    handleSuccess(res) {
      if (res.code === '200' && res.data && res.data.length > 0) {
        this.$message({type: 'success', message: '图片上传成功！'})
        this.$emit('input', [...(this.value || []), res.data[0]])
      } else {
        this.$message({type: 'error', message: res.message})
      }
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
    },
    // 拖拽排序开始
    handleDragStart(i) {
      if (!this.sortable) return
      this.dragIndex = i
    },
    // 拖拽进入其他卡片：实时移动位置并同步 v-model
    handleDragEnter(i) {
      if (!this.sortable || this.isDisabled || this.dragIndex < 0 || this.dragIndex === i) return
      const arr = [...(this.value || [])]
      const [moved] = arr.splice(this.dragIndex, 1)
      arr.splice(i, 0, moved)
      this.dragIndex = i
      this.$emit('input', arr)
    },
    // 拖拽结束
    handleDragEnd() {
      this.dragIndex = -1
    },
    // 排序模式删除某张图片
    removeAt(i) {
      const arr = [...(this.value || [])]
      const [removed] = arr.splice(i, 1)
      try {
        if (removed && removed.fileUrl) {
          this.$request({url: 'upload/deleteFile', method: 'get', params: {fileUrl: removed.fileUrl}})
        }
      } catch (e) {
      }
      this.$emit('input', arr)
    }
  }
}
</script>
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

.hide-trigger {
  ::v-deep .el-upload--picture-card {
    display: none;
  }
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

  &[draggable="true"] {
    cursor: move;
  }
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

.pic-index {
  position: absolute;
  top: 4px;
  left: 4px;
  z-index: 5;
  width: 20px;
  height: 20px;
  line-height: 20px;
  text-align: center;
  border-radius: 50%;
  background: #409eff;
  color: #fff;
  font-size: 12px;
}
</style>
