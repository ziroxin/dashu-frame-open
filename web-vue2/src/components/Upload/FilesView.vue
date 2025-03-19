<!--
 * 多种格式文件显示和预览组件
 * 参数说明：
       fileList: 可选，显示文件列表，默认[]
       showCheck: 可选，是否显示选择框，可选择，默认false
       value: 可选，选中的文件列表（通过v-model双向绑定），默认[]，可回显
              注意只有showCheck为true时有效，才会显示复选框，该参数才有效

 * @Author: ziro
 * @Date: 2025/2/8 11:28:47
 -->
<template>
  <!-- 显示文件列表 -->
  <div class="files-panel" v-if="fileList&&fileList.length>0" v-loading="isLoading">
    <div class="files-check-all">
      <el-checkbox v-if="showCheck" @change="handleCheckAllChange"
                   :indeterminate="selectedFileList.length>0&&selectedFileList.length<fileList.length"
                   :value="selectedFileList.length>=fileList.length"> 全选
      </el-checkbox>
    </div>
    <div class="files-view">
      <div class="file-item" v-for="file in fileList">
        <!-- 图片文件 -->
        <template v-if="'jpg,jpeg,png,gif,bpm'.includes(file.fileExtend.toLowerCase())">
          <div class="file-img">
            <el-checkbox v-if="showCheck" :value="selectedFileList.includes(file)" @change="checkFile($event,file)"/>
            <el-image :src="$baseServer+file.fileUrl" fit="cover" class="icons"
                      :preview-src-list="[$baseServer+file.fileUrl]"/>
          </div>
          <div class="file-name">
            <el-tooltip :content="file.fileOldName">
              <span style="width:100%;text-align:center;cursor:default;">点击图片查看大图</span>
            </el-tooltip>
          </div>
        </template>
        <!-- mp3/mp4/wmv/avi文件 -->
        <template v-else-if="'mp3,mp4,wmv,avi'.includes(file.fileExtend.toLowerCase())">
          <div class="file-img">
            <el-checkbox v-if="showCheck" :value="selectedFileList.includes(file)" @change="checkFile($event,file)"/>
            <video :src="$baseServer+file.fileUrl" controls="controls" class="icons"/>
          </div>
          <div class="file-name">
            <el-tooltip :content="file.fileOldName"><span>{{ file.fileOldName }}</span></el-tooltip>
            <div class="file-btn"><span @click="downloadFile(file)">下载</span></div>
          </div>
        </template>
        <!-- xls/xlsx文件 -->
        <template v-else-if="'xls,xlsx'.includes(file.fileExtend.toLowerCase())">
          <div class="file-img">
            <el-checkbox v-if="showCheck" :value="selectedFileList.includes(file)" @change="checkFile($event,file)"/>
            <svg-icon icon-class="file-excel" class="icons"/>
          </div>
          <div class="file-name">
            <el-tooltip :content="file.fileOldName"><span>{{ file.fileOldName }}</span></el-tooltip>
            <div class="file-btn">
              <span style="margin-right: 3px;" @click="handlePreview(file,'xlsx')">预览</span>
              <span @click="downloadFile(file)">下载</span>
            </div>
          </div>
        </template>
        <!-- doc/docx文件 -->
        <template v-else-if="'doc,docx'.includes(file.fileExtend.toLowerCase())">
          <div class="file-img">
            <el-checkbox v-if="showCheck" :value="selectedFileList.includes(file)" @change="checkFile($event,file)"/>
            <svg-icon icon-class="file-word" class="icons"/>
          </div>
          <div class="file-name">
            <el-tooltip :content="file.fileOldName"><span>{{ file.fileOldName }}</span></el-tooltip>
            <div class="file-btn">
              <span style="margin-right: 3px;" @click="handlePreview(file,'docx')">预览</span>
              <span @click="downloadFile(file)">下载</span>
            </div>
          </div>
        </template>
        <!-- pdf文件 -->
        <template v-else-if="'pdf'.includes(file.fileExtend.toLowerCase())">
          <div class="file-img">
            <el-checkbox v-if="showCheck" :value="selectedFileList.includes(file)" @change="checkFile($event,file)"/>
            <svg-icon icon-class="file-pdf" class="icons"/>
          </div>
          <div class="file-name">
            <el-tooltip :content="file.fileOldName"><span>{{ file.fileOldName }}</span></el-tooltip>
            <div class="file-btn">
              <span style="margin-right: 3px;" @click="handlePreview(file,'pdf')">预览</span>
              <span @click="downloadFile(file)">下载</span>
            </div>
          </div>
        </template>
        <!-- rar/zip/7z文件 -->
        <template v-else-if="'rar,zip,7z'.includes(file.fileExtend.toLowerCase())">
          <div class="file-img">
            <el-checkbox v-if="showCheck" :value="selectedFileList.includes(file)" @change="checkFile($event,file)"/>
            <svg-icon icon-class="file-zip" class="icons"/>
          </div>
          <div class="file-name">
            <el-tooltip :content="file.fileOldName"><span>{{ file.fileOldName }}</span></el-tooltip>
            <div class="file-btn"><span @click="downloadFile(file)">下载</span></div>
          </div>
        </template>
        <!-- 其他文件 -->
        <template v-else>
          <div class="file-img">
            <el-checkbox v-if="showCheck" :value="selectedFileList.includes(file)" @change="checkFile($event,file)"/>
            <svg-icon icon-class="file-other" class="icons"/>
          </div>
          <div class="file-name">
            <el-tooltip :content="file.fileOldName"><span>{{ file.fileOldName }}</span></el-tooltip>
            <div class="file-btn"><span @click="downloadFile(file)">下载</span></div>
          </div>
        </template>
      </div>
    </div>
  </div>
  <el-empty v-else/>
</template>
<script>
import downloadUtil from "@/utils/download-util";

export default {
  name: "FilesView",
  props: {
    // 显示文件列表
    fileList: {type: Array, default: () => []},
    // 是否可选择
    showCheck: {type: Boolean, default: false},
    // 默认选中的列表（当showCheck为true时有效）
    value: {type: Array, default: () => []},
  },
  data() {
    return {
      isLoading: false,
      // 选中的文件列表
      selectedFileList: [],
    }
  },
  watch: {
    value(val) {
      this.selectedFileList = val
    }
  },
  methods: {
    // 全选按钮
    handleCheckAllChange(e) {
      this.selectedFileList = e ? [...this.fileList] : []
      this.$emit('input', this.selectedFileList)
    },
    // 选中文件
    checkFile(e, file) {
      if (e) {
        this.selectedFileList.push(file)
      } else {
        this.selectedFileList.splice(this.selectedFileList.indexOf(file), 1)
      }
      this.$emit('input', this.selectedFileList)
    },
    // 下载文件
    downloadFile(file) {
      downloadUtil.saveAs(this.$baseServer + file.fileUrl, file.fileOldName)
    },
    // 预览文件
    handlePreview(file, fileExtend) {
      if (fileExtend === 'pdf') {
        window.open(this.$baseServer + file.fileUrl)
      } else if (fileExtend === 'docx' || fileExtend === 'xlsx') {
        this.isLoading = true
        const params = {fileUrl: file.fileUrl, fileExtend: fileExtend}
        this.$request({url: '/upload/preview', method: 'get', params}).then((response) => {
          window.open(this.$baseServer + response.data)
        }).finally(() => {
          this.isLoading = false
        })
      } else {
        this.$message({type: 'error', message: '该文件暂不支持预览'})
      }
    }
  }
}
</script>
<style scoped lang="scss">
.files-panel {
  width: 100%;

  .files-check-all {
    margin: 8px 0 0 8px;

    ::v-deep .el-checkbox__inner, {
      width: 24px;
      height: 24px;

      &::after {
        width: 5px;
        height: 12px;
        top: 3px;
        left: 8px;
      }
    }

    ::v-deep .el-checkbox__input.is-indeterminate .el-checkbox__inner::before {
      top: 10px;
      height: 3px;
    }
  }

  .files-view {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
    gap: 15px;
    min-height: 180px;

    .file-item {
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: stretch;

      .file-img {
        width: 100%;
        height: 70%;

        .el-checkbox {
          position: absolute;

          ::v-deep .el-checkbox__inner {
            margin: 8px;
            width: 24px;
            height: 24px;

            &::after {
              width: 5px;
              height: 12px;
              top: 3px;
              left: 8px;
            }
          }
        }

        .icons {
          width: 100%;
          height: 100%;
          border: 1px solid #ccc;
          border-radius: 5px;
        }

        .svg-icon {
          padding: 0% 25% !important;
        }
      }

      .file-name {
        font-size: 12px;
        margin-top: 8px;
        display: flex;
        justify-content: space-between;
        align-items: center;

        .el-tooltip {
          color: #666;
          white-space: nowrap; /* 不换行 */
          overflow: hidden; /* 超出部分隐藏 */
          text-overflow: ellipsis; /* 溢出文本显示省略号 */
          cursor: default;
        }

        .file-btn {
          min-width: 55px;
          color: #73bf73;
          cursor: pointer;
          text-align: right;
        }
      }
    }
  }
}
</style>