<template>
  <div class="app-container">
    <h2 class="title">文件普通上传、分片上传、秒传、断点续传</h2>
    <div style="text-align: center;margin-bottom: 50px;">
      <el-radio-group v-model="uploadType">
        <el-radio-button label="normal">普通上传</el-radio-button>
        <el-radio-button label="chunk">分片上传</el-radio-button>
        <el-radio-button label="chunkResume">断点续传</el-radio-button>
        <el-radio-button label="second">秒传</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 文件普通上传 -->
    <el-divider content-position="center" v-if="uploadType==='normal'">文件普通上传</el-divider>
    <div class="content" v-if="uploadType==='normal'">
      <div class="uploadPanel">
        <el-upload
            :action="$baseServer+'/upload/files'" :headers="$store.getters.headerToken"
            :on-success="uploadFileSuccess" accept=".zip,.rar"
            :show-file-list="true" :auto-upload="true">
          <el-button type="primary" icon="el-icon-upload2" size="small">点击选择文件上传</el-button>
        </el-upload>
      </div>
    </div>

    <!-- 文件分片上传 -->
    <el-divider content-position="center" v-if="uploadType==='chunk'">文件分片上传</el-divider>
    <div class="content" v-if="uploadType==='chunk'">
      <div class="uploadPanel">
        <plupload-chunk upload-server-url="/upload/chunks" upload-dir="testChunks"
                        max-file-size="300mb" chunk-size="10mb"
                        :mime-types="[{title: 'Zip files', extensions: 'zip'}]"
                        key="chunk"></plupload-chunk>
      </div>
    </div>

    <!-- 文件断点续传 -->
    <el-divider content-position="center" v-if="uploadType==='chunkResume'">文件断点续传</el-divider>
    <div class="content" v-if="uploadType==='chunkResume'">
      <div style="font-size: 12px;color: #dd1f29;border-bottom: 1px dashed #eeeeee;margin-bottom: 15px;">
        说明：断点续传，是把文件分片，每次上传前，检测分片是否已上传。若已上传，则跳过；未上传的则继续上传。
      </div>
      <div class="uploadPanel">
        <file-chunk-resume upload-server-url="/upload/chunks/resume" upload-dir="testChunksResume"
                           mime-types=".zip,.rar"
                           :max-file-size="300*1024*1024" :chunk-size="10*1024*1024"
                           key="chunkResume"></file-chunk-resume>
      </div>
    </div>

    <!-- 文件秒传 -->
    <el-divider content-position="center" v-if="uploadType==='second'">文件秒传</el-divider>
    <div class="content" v-if="uploadType==='second'">
      <div style="font-size: 12px;border-bottom: 1px dashed #eeeeee;margin-bottom: 15px;">
        <div>
          使用本功能上传的文件，全部存入 “ 文件秒传表 ”。
          点击进入：
          <el-button type="text" @click="$router.push('/files')">[文件秒传管理]</el-button>
        </div>
        <div style="color: #dd1f29;">
          说明：上传前，先根据文件 md5 判断，表中是否存在：
          <el-tag type="success" size="mini" style="margin-right: 10px;">若已存在，则跳过，直接返回结果；</el-tag>
          <el-tag type="danger" size="mini">若不存在，则上传，并存入 “ 文件秒传表 ”，返回结果。</el-tag>
        </div>
      </div>
      <div>
        <div class="secondInfo">
          <el-switch v-model="isCopy" active-color="#13ce66" inactive-color="#ff4949"
                     active-text="拷贝文件，业务表用单独（和文件秒传表解耦）"
                     inactive-text="不拷贝文件（使用文件秒传表中的fileUrl，可能会被删除）"></el-switch>
        </div>
        <div class="uploadPanel">
          <file-second second-server-url="/upload/second/chunks"
                       second-md5-url="/upload/second/md5"
                       upload-dir="testSecond"
                       mime-types=".zip,.rar"
                       :max-file-size="300*1024*1024" :chunk-size="10*1024*1024"
                       :is-copy="isCopy"></file-second>
        </div>
      </div>
    </div>

  </div>
</template>
<script>
import PluploadChunk from "@/components/Upload/PluploadChunk.vue";
import FileChunkResume from "@/components/Upload/FileChunkResume.vue";
import FileSecond from "@/components/Upload/FileSecond.vue";

export default {
  components: {FileSecond, FileChunkResume, PluploadChunk},
  data() {
    return {
      // 上传类型：normal=普通上传;chunk=分片上传;chunkResume=断点续传;second=秒传
      uploadType: 'normal',
      // 是否拷贝：文件秒传
      isCopy: true,
    }
  },
  methods: {
    // 文件上传成功
    uploadFileSuccess(response) {
      if (response.code === '200') {
        this.$message({type: 'success', message: '文件上传成功！'})
      } else {
        this.$message({type: 'error', message: response.message})
      }
    },
  }
}
</script>
<style scoped lang="scss">
.title {
  text-align: center;
  margin-bottom: 50px;
}

.content {
  margin: 30px 20px 50px 20px;
  line-height: 40px;
}

.secondInfo {
  margin-top: 20px;
  border-radius: 10px;
  background: #efefef;
  padding: 10px 30px;
  text-align: center;
}

.uploadPanel {
  margin-top: 20px;
  padding: 20px;
  border: 1px dashed #ccc;
  border-radius: 10px;
  text-align: center;
}
</style>
