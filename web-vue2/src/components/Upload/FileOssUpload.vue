<!--
 * Oss上传组件 - 返回值：数据库保存的fileId列表
 * 说明：
       1.上传前，调用API(/oss/client/upload/token)获取上传oss相关token
       2.上传成功后，回调后台，自动保存文件信息到数据库，返回数据库保存的fileId列表
       3.点击删除按钮，自动删除oss文件和数据库信息，删除API(/oss/client/upload/deleteFromCache)
 * 参数说明：
       ossFolder: 必选，oss上传的文件夹
       limit: 可选，上传个数限制（0表示不限制），默认：0
       fileList: 可选，已上传的文件列表（回显时需传入），默认：[]
       multiple: 可选，是否多选，默认：true
       showFileList: 可选，是否显示文件列表，默认：true
       accept: 可选，上传文件类型（格式举例：.jpg,.png）
       maxSize: 可选，上传文件大小限制，默认1Mb（单位：b/kb/mb/gb/tb，大小写都支持，格式举例：3mb、2GB、500Kb）
       btnTitle: 可选，上传按钮显示文字，默认：点击上传文件
       showTip: 可选，是否显示提示信息，默认：true
       tipInfo: 可选，提示信息，默认：只能上传【jpg、png】文件，文件大小不能超过1MB
 * @Author: ziro
 * @Date: 2024/12/14 16:00:40
 -->
<template>
  <el-upload class="upload-oss"
             :headers="$store.getters.headerToken"
             :data="ossTokenData"
             :action="ossTokenData.host"
             :before-upload="beforeUpload"
             :on-success="handleSuccess"
             :on-error="handleError"
             :on-remove="handleRemove"
             :multiple="multiple"
             :limit="limit"
             :accept="accept"
             :show-file-list="showFileList"
             :on-exceed="handleExceed"
             :file-list="fileList">
    <el-button size="small" type="primary">{{ btnTitle }}</el-button>
    <div slot="tip" class="el-upload__tip" v-if="showTip">{{ tipInfo }}</div>
  </el-upload>
</template>

<script>
export default {
  name: "FileOssUpload",
  props: {
    // oss上传文件夹
    ossFolder: {type: String, default: '', required: true},
    // 限制上传文件数量，0表示不限制
    limit: {type: Number, default: 0, required: false},
    // 上传文件列表（已上传的文件列表，回显时需传入）
    fileList: {type: Array, default: () => [], required: false},
    // 是否多选
    multiple: {type: Boolean, default: true, required: false},
    // 是否显示已上传文件列表
    showFileList: {type: Boolean, default: true, required: false},
    // 文件类型
    accept: {type: String, default: '.jpg,.png', required: false},
    // 文件大小
    maxSize: {type: String, default: '1Mb', required: false},
    // 上传按钮标题
    btnTitle: {type: String, default: '点击上传文件'},
    // 是否显示提示文字
    showTip: {type: Boolean, default: true, required: false},
    // 提示信息
    tipInfo: {type: String, default: '只能上传【jpg、png】文件，文件大小不能超过1MB'}
  },
  data() {
    return {
      // oss上传token数据
      ossTokenData: {
        host: '',
      },
      // 上传文件id列表
      fileIds: []
    }
  },
  methods: {
    beforeUpload(file) {
      if (!this.accept.toLowerCase().split(',')
          .includes(file.name.substring(file.name.lastIndexOf('.')).toLowerCase())) {
        this.$message.error('只能上传' + this.accept + '格式的文件!');
        return false
      }
      const max = this.getMaxFileSize()
      if (max < file.size) {
        this.$message.error('上传文件大小不能超过' + this.maxSize + '!')
        return false
      }
      // 上传前的钩子函数，调用api获取oss上传token
      return new Promise((resolve, reject) => {
        let params = {path: this.ossFolder, oldFileName: file.name, maxSize: max}
        this.$request({url: '/oss/client/upload/token', method: 'get', params}).then((response) => {
          this.ossTokenData = {...response.data, ...response.data.callbackVar}
          resolve(true)
        }).catch((error) => {
          reject(error)
        })
      })
    },
    handleSuccess(res, file, fileList) {
      if (res.data.success) {
        this.$message({type: 'success', message: res.data.msg})
        this.fileIds.push({fid: res.data.id, uid: file.uid})
        this.$emit('input', this.fileIds.map(o => o.fid));
      } else {
        this.$message({type: 'error', message: res.data.msg})
        this.fileList.splice(fileList.indexOf(file), 1)
      }
    },
    handleRemove(file, fileList) {
      // 尝试删除oss文件
      let params = {fileId: this.fileIds.find(o => o.uid === file.uid).fid}
      this.$request({url: '/oss/client/upload/deleteFromCache', method: 'post', params}).then((response) => {
        console.log(response)
      })
      // 移除fileId
      this.fileIds = this.fileIds.filter(o => o.uid !== file.uid)
      this.$emit('input', this.fileIds.map(o => o.fid));
    },
    handleError(error) {
      console.log(error)
      this.$message({type: 'error', message: '上传失败，请重试！'})
    },
    handleExceed() {
      if (this.limit > 0) {
        // 超过限制的钩子函数
        this.$message.error(`最多只能上传 ${this.limit} 个文件`)
      }
    },
    getMaxFileSize() {
      const units = {
        'b': 1,
        'kb': 1024,
        'mb': 1024 * 1024,
        'gb': 1024 * 1024 * 1024,
        'tb': 1024 * 1024 * 1024 * 1024
      };
      const maxSizeStr = this.maxSize.toLowerCase();
      const unit = maxSizeStr.match(/[a-z]+$/)[0];
      const value = parseInt(maxSizeStr.replace(unit, ''));
      return value * units[unit];
    }
  }
}
</script>
<style lang="scss" scoped>

</style>