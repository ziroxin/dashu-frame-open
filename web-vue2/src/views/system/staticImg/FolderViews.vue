<template>
  <div v-if="currentParentId">
    <div v-for="item in folders" v-loading="isLoading">
      <el-divider>当前文件夹：{{ item.fileOldName }}</el-divider>
      <!-- 文件显示 -->
      <files-view :file-list="item.children" :show-check="true" v-model="selectedFileList"/>
      <!-- 文件上传 -->
      <el-divider></el-divider>
      <div style="display: flex;justify-content: space-between;align-items: center;">
        <div style="margin-right: 15px;">
          <el-button v-if="selectedFileList.length > 0" type="danger" @click="deleteFileList"
                     icon="el-icon-delete" size="small">删除选中的文件
          </el-button>
          <el-button type="info" v-else icon="el-icon-delete" size="small">请选择要删除的文件</el-button>
        </div>
        <file-upload v-model="uploadFileList" :action="$baseServer+'/filesStatic/zFilesStatic/upload'"
                     :params-data="{parentId: item.fileId}" :limit-size="1024*1024*2" :show-file-list="false"
                     accept=".jpg,.jpeg,.png,.gif,.pdf" tip-info="只允许上传jpg、jpeg、png、gif、pdf格式文件，最大2MB"/>
      </div>
    </div>
  </div>
  <el-empty v-else/>
</template>

<script>
import request from "@/utils/request";
import FileUpload from "@/components/Upload/FileUpload.vue";
import FilesView from "@/components/Upload/FilesView.vue";

export default {
  props: ['currentParentId'],
  components: {FilesView, FileUpload},
  data() {
    return {
      // 加载图片列表
      isLoading: false,
      folders: [],
      // 上传文件列表
      uploadFileList: [],
      selectedFileList: [],
    }
  },
  watch: {
    uploadFileList(val) {
      if (val && val.length > 0) {
        // 上传文件成功后，刷新文件列表
        this.loadFolders();
      }
    }
  },
  mounted() {
    this.loadFolders()
  },
  methods: {
    // 加载文件夹列表
    loadFolders() {
      this.isLoading = true
      const params = {
        limit: 100, params: JSON.stringify({fileType: '0', hasChildren: 'true'})
      };
      request({url: '/filesStatic/zFilesStatic/list', method: 'get', params}).then((response) => {
        this.folders = response.data.records.filter(r => r.fileId === this.currentParentId)
        this.selectedFileList = []
        this.isLoading = false
      })
    },
    // 删除选中的文件
    deleteFileList() {
      if (this.selectedFileList.length <= 0) {
        this.$message({type: 'warning', message: '请选择要删除的文件！'})
        return
      }
      this.$confirm('确定要删除吗?', '删除提醒', {
        confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning'
      }).then(() => {
        // 执行删除
        const data = this.selectedFileList.map(r => r.fileId)
        request({url: '/filesStatic/zFilesStatic/delete', method: 'post', data}).then(response => {
          this.$message({type: 'success', message: '删除成功！'})
          this.loadFolders()
        })
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
