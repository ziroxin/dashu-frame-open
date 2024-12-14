<template>
  <div class="files-view" v-if="fileList&&fileList.length>0" v-loading="isLoading">
    <template v-for="file in fileList">
      <div class="file-item">
        <template v-if="'jpg,jpeg,png,gif,bpm'.includes(file.fileExtend.toLowerCase())">
          <el-image :src="$baseServer+file.fileUrl" fit="cover" class="icons"
                    :preview-src-list="[$baseServer+file.fileUrl]"/>
          <div class="file-name">
            <el-tooltip :content="file.fileOldName">
              <span style="width:100%;text-align:center;cursor:default;">点击图片查看大图</span>
            </el-tooltip>
          </div>
        </template>
        <template v-else-if="'xls,xlsx'.includes(file.fileExtend.toLowerCase())">
          <svg-icon icon-class="file-excel" class="icons"/>
          <div class="file-name">
            <el-tooltip :content="file.fileOldName"><span>{{ file.fileOldName }}</span></el-tooltip>
            <div class="file-btn">
              <span style="margin-right: 3px;" @click="handlePreview(file,'xlsx')">预览</span>
              <span @click="downloadFile(file)">下载</span>
            </div>
          </div>
        </template>
        <template v-else-if="'doc,docx'.includes(file.fileExtend.toLowerCase())">
          <svg-icon icon-class="file-word" class="icons"/>
          <div class="file-name">
            <el-tooltip :content="file.fileOldName"><span>{{ file.fileOldName }}</span></el-tooltip>
            <div class="file-btn">
              <span style="margin-right: 3px;" @click="handlePreview(file,'docx')">预览</span>
              <span @click="downloadFile(file)">下载</span>
            </div>
          </div>
        </template>
        <template v-else-if="'pdf'.includes(file.fileExtend.toLowerCase())">
          <svg-icon icon-class="file-pdf" class="icons"/>
          <div class="file-name">
            <el-tooltip :content="file.fileOldName"><span>{{ file.fileOldName }}</span></el-tooltip>
            <div class="file-btn">
              <span style="margin-right: 3px;" @click="handlePreview(file,'pdf')">预览</span>
              <span @click="downloadFile(file)">下载</span>
            </div>
          </div>
        </template>
        <template v-else-if="'rar,zip,7z'.includes(file.fileExtend.toLowerCase())">
          <svg-icon icon-class="file-zip" class="icons"/>
          <div class="file-name">
            <el-tooltip :content="file.fileOldName"><span>{{ file.fileOldName }}</span></el-tooltip>
            <div class="file-btn"><span @click="downloadFile(file)">下载</span></div>
          </div>
        </template>
        <template v-else>
          <svg-icon icon-class="file-other" class="icons"/>
          <div class="file-name">
            <el-tooltip :content="file.fileOldName"><span>{{ file.fileOldName }}</span></el-tooltip>
            <div class="file-btn"><span @click="downloadFile(file)">下载</span></div>
          </div>
        </template>
      </div>
    </template>
  </div>
  <el-empty v-else/>
</template>
<script>
import downloadUtil from "@/utils/download-util";

export default {
  name: "FilesView",
  props: {
    fileList: {type: Array, default: () => []}
  },
  data() {
    return {
      isLoading: false
    }
  },
  methods: {
    downloadFile(file) {
      downloadUtil.saveAs(this.$baseServer + file.fileUrl, file.fileOldName);
    },
    handlePreview(file, fileExtend) {
      if (fileExtend === 'pdf') {
        window.open(this.$baseServer + file.fileUrl);
      } else if (fileExtend === 'docx' || fileExtend === 'xlsx') {
        this.isLoading = true;
        const params = {fileUrl: file.fileUrl, fileExtend: fileExtend}
        this.$request({url: '/upload/preview', method: 'get', params}).then((response) => {
          window.open(this.$baseServer + response.data);
        }).finally(() => {
          this.isLoading = false;
        })
      } else {
        this.$message({type: 'error', message: '该文件暂不支持预览'})
      }
    }
  }
}
</script>
<style scoped lang="scss">
.files-view {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 15px;

  .file-item {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: stretch;

    .icons {
      width: 100%;
      height: 70%;
      border: 1px solid #ccc;
      border-radius: 5px;
    }

    .svg-icon {
      padding: 0% 25% !important;
    }

    .file-name {
      font-size: 12px;
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
</style>