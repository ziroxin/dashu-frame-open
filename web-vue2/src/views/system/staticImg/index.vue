<template>
  <div class="app-container">
    <el-tabs v-model="activeTab" class="tabs">
      <!-- 文件夹管理 -->
      <el-tab-pane label="文件夹" name="folderList" class="tabs-item">
        <list/>
      </el-tab-pane>
      <!-- 静态资源文件管理 -->
      <el-tab-pane label="静态资源管理" name="folderManagement" class="tabs-item" v-loading="isLoading">
        <el-collapse accordion style="border: 1px solid #dedede;border-radius: 5px;padding: 10px 20px;">
          <el-collapse-item :title="'文件夹：'+item.fileOldName" v-for="item in folders" :key="item.fileId">
            <!-- 文件显示 -->
            <files-view :file-list="item.children"></files-view>
            <!-- 文件上传 -->
            <el-divider></el-divider>
            <div style="display: flex;align-items: center;justify-content: center;">
              <el-radio-group v-model="item.autoUnzip" size="small"
                              style="margin-right: 20px;border: 1px solid #dedede;border-radius: 5px;padding: 8px 20px;">
                <el-radio :label="false">不解压</el-radio>
                <el-radio :label="true">自动解压</el-radio>
              </el-radio-group>
              <file-upload v-model="uploadFileList" :action="$baseServer+'/filesStatic/zFilesStatic/upload'"
                           :params-data="{autoUnzip: item.autoUnzip, parentId: item.fileId}"
                           accept=".jpg,.png,.gif,.zip" :limit-size="2048" :show-file-list="false"
                           tip-info="支持jpg、png、gif、zip格式文件，其他格式不支持上传，但允许上传压缩包自动解压"/>
            </div>
          </el-collapse-item>
        </el-collapse>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import List from "@/views/system/staticImg/list.vue";
import request from "@/utils/request";
import FilesView from "@/components/Upload/FilesView.vue";
import FileUpload from "@/components/Upload/FileUpload.vue";

export default {
  components: {FileUpload, FilesView, List},
  data() {
    return {
      activeTab: 'folderList',
      // 加载图片列表
      isLoading: false,
      folders: [],
      // 上传文件列表
      uploadFileList: [],
      autoUnzip: false,
    };
  },
  watch: {
    activeTab(val) {
      if (val === 'folderManagement') {
        this.loadFolders()
      }
    },
    uploadFileList(val) {
      if (val && val.length > 0) {
        // 上传文件成功后，刷新文件列表
        this.loadFolders()
        this.uploadFileList = []
      }
    }
  },
  methods: {
    loadFolders() {
      this.isLoading = true
      const params = {limit: 100, params: JSON.stringify({fileType: '0', hasChildren: 'true'})};
      request({url: '/filesStatic/zFilesStatic/list', method: 'get', params}).then((response) => {
        this.folders = response.data.records
        this.isLoading = false
      })
    }
  },
};
</script>

<style scoped lang="scss">
.app-container {
  .tabs {
    .tabs-item {
      padding: 10px 0px;
    }
  }
}
</style>
