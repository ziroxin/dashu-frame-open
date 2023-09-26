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

    <el-divider content-position="center" v-if="uploadType==='normal'">文件普通上传</el-divider>
    <div class="content" v-if="uploadType==='normal'">
      <el-upload
          :action="$baseServer+'/upload/files'" :headers="$store.getters.headerToken"
          :on-success="uploadFileSuccess" accept=".zip,.rar"
          :show-file-list="false" :auto-upload="true">
        <el-button type="primary" icon="el-icon-upload2" size="small">点击选择文件上传</el-button>
      </el-upload>
    </div>

    <el-divider content-position="center" v-if="uploadType==='chunk'">文件分片上传</el-divider>
    <div class="content" v-if="uploadType==='chunk'">
      <plupload-chunk upload-server-url="/upload/chunks" max-file-size="300mb" chunk-size="10mb"
                      :mime-types="[{title: 'Zip files', extensions: 'zip'}]"
                      key="chunk"></plupload-chunk>
    </div>

    <el-divider content-position="center" v-if="uploadType==='chunkResume'">文件断点续传</el-divider>
    <div class="content" v-if="uploadType==='chunkResume'">
      <div style="font-size: 12px;color: #dd1f29;border-bottom: 1px dashed #eeeeee;margin-bottom: 15px;">
        说明：断点续传，是把文件分片，每次上传前，检测分片是否已上传。若已上传，则跳过；未上传的则继续上传。
      </div>
      <file-chunk-resume upload-server-url="/upload/chunks/resume"
                         mime-types=".zip,.rar"
                         :max-file-size="300*1024*1024" :chunk-size="10*1024*1024"
                         key="chunkResume"></file-chunk-resume>
    </div>

    <el-divider content-position="center" v-if="uploadType==='second'">文件秒传</el-divider>
    <div class="content" v-if="uploadType==='second'">
      使用文件秒传，系统会自动存入文件总表，业务表的文件，从该表中文件直接复制。需要管理文件总表，点击
      <el-button type="text" @click="$router.push('/files')">[文件秒传管理]</el-button>
      进入
    </div>
  </div>
</template>
<script>
// import SparkMD5 from 'spark-md5'

import PluploadChunk from "@/components/Upload/PluploadChunk.vue";
import FileChunkResume from "@/components/Upload/FileChunkResume.vue";

export default {
  components: {FileChunkResume, PluploadChunk},
  data() {
    return {
      uploadType: 'normal'
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
    // 分片上传


    /*handleFileChange(event) {
      const file = event.target.files[0];
      if (file) {
        this.calculateFileMD5(file, md5 => {
          console.log('文件MD5值:', md5);
        });
      }
    },

    calculateFileMD5(file, callback) {
      const chunkSize = 2 * 1024 * 1024; // 每次读取的文件块大小

      const fileReader = new FileReader();
      const spark = new SparkMD5.ArrayBuffer();

      let currentChunk = 0;

      fileReader.onload = function (e) {
        spark.append(e.target.result); // 将文件块内容添加到MD5计算中

        currentChunk++;

        if (currentChunk < chunks) {
          loadNextChunk();
        } else {
          callback(spark.end()); // 计算完成，调用回调函数返回MD5值
        }
      };

      fileReader.onerror = function () {
        console.error('文件读取出错');
        callback(null);
      };

      function loadNextChunk() {
        const start = currentChunk * chunkSize;
        const end = Math.min(start + chunkSize, file.size);
        const chunk = file.slice(start, end);
        fileReader.readAsArrayBuffer(chunk); // 读取文件块内容
      }

      const chunks = Math.ceil(file.size / chunkSize);
      loadNextChunk();
    },*/
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
</style>
