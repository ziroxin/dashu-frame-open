<template>
  <div class="app-container">
    <h2 class="title">文件普通上传、分片上传、秒传、断点续传</h2>
    <div style="text-align: center;margin-bottom: 50px;">
      <el-radio-group v-model="uploadType">
        <el-radio-button label="normal">普通上传</el-radio-button>
        <el-radio-button label="chunk">分片上传</el-radio-button>
        <el-radio-button label="chunkResume">断点续传</el-radio-button>
        <el-radio-button label="second">秒传</el-radio-button>
        <el-radio-button label="oss">OSS上传（阿里云）</el-radio-button>
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
      <div style="text-align: center;margin-top: 30px;">
        <a href="http://docs.java119.cn/use/comm-fileupload.html"
           target="_blank">
          <el-button type="danger" icon="el-icon-question" plain>使用帮助</el-button>
        </a>
      </div>

      <div style="margin-top: 100px;">
        <el-divider><span style="color:red;">文件普通上传 - 使用自定义下载组件的DEMO</span></el-divider>
        <div style="width: 600px;border:1px dashed #ccc;border-radius: 10px;padding: 10px;margin:20px auto;">
          <file-upload v-model="uploadFileList"></file-upload>
        </div>
        <div style="display: flex;border:1px dashed #ccc;border-radius: 10px;padding: 10px;">
          <el-button type="primary" plain style="margin-right: 10px;width:100px;">文件List</el-button>
          <div style="display: flex;flex-direction: column;">
            <div v-for="item in uploadFileList" type="info" style="font-size: 12px;color: #666;line-height: 20px;">
              【文件名：{{ item.fileOldName }}】 - 【大小：{{ item.fileSize }}】 - 【地址：{{ item.fileUrl }}】
            </div>
          </div>
        </div>
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
      <div style="text-align: center;margin-top: 30px;">
        <a href="http://docs.java119.cn/use/comm-fileupload2.html"
           target="_blank">
          <el-button type="danger" icon="el-icon-question" plain>使用帮助</el-button>
        </a>
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
      <div style="text-align: center;margin-top: 30px;">
        <a href="http://docs.java119.cn/use/comm-fileupload2.html#_2-%E6%96%87%E4%BB%B6%E6%96%AD%E7%82%B9%E7%BB%AD%E4%BC%A0"
           target="_blank">
          <el-button type="danger" icon="el-icon-question" plain>使用帮助</el-button>
        </a>
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
        <div style="text-align: center;margin-top: 30px;">
          <a href="http://docs.java119.cn/use/comm-fileupload3.html"
             target="_blank">
            <el-button type="danger" icon="el-icon-question" plain>使用帮助</el-button>
          </a>
        </div>
      </div>
    </div>

    <!-- OSS上传（阿里云） -->
    <el-divider content-position="center" v-if="uploadType==='oss'">OSS上传（阿里云）</el-divider>
    <div class="content" v-if="uploadType==='oss'">
      <div style="font-size: 12px;color: #dd1f29;border-bottom: 1px dashed #eeeeee;margin-bottom: 15px;">
        上传说明：Oss直传（从后台获取上传凭证，前端直接上传到oss，文件不经过服务器）
      </div>
      <div class="uploadPanel">
        <file-oss-upload v-model="ossFileIds" oss-folder="demoFolder" :limit="2"
                         accept=".jpg,.png,.mp4"></file-oss-upload>
        <el-divider></el-divider>
        <div style="font-size: 12px;margin-top: 10px;color: #666;">
          已上传的fileId：{{ ossFileIds }}
        </div>
      </div>
      <div style="font-size: 12px;color: #dd1f29;border-bottom: 1px dashed #eeeeee;margin-bottom: 15px;">
        下载说明：下载Oss文件分2种：1一种是配置公共读，可以直接下载（一定要配置防盗链）；2另一种使用STS临时凭证下载，下方示例为STS临时凭证下载。
      </div>
      <div class="uploadPanel">
        <div style="font-size: 12px;margin-top: 10px;color: #666;">
          oss存储文件夹+文件名：
          <el-input v-model="ossDemoFileName" style="width: 50%;" size="small"/>
          <el-button type="primary" size="small" style="margin-left: 10px;"
                     @click="openStsFile">获取STS临时凭证
          </el-button>
          <template v-if="ossDemoStsUrl">
            <img :src="ossDemoStsUrl" v-if="ossDemoStsType==='img'" style="max-width: 300px;"/>
            <video :src="ossDemoStsUrl" controls v-else-if="ossDemoStsType==='video'" style="max-width: 300px;"></video>
            <a :href="ossDemoStsUrl" v-else target="_blank">点击下载</a>
          </template>
        </div>
      </div>
      <el-divider></el-divider>
      <div style="font-size: 12px;color:#666666;border-bottom: 1px dashed #eeeeee;margin-bottom: 15px;">
        文件上传oss成功后，会回调后台，后台将上传信息存入redis，20分钟有效期，key是fileId。
        <br/>
        表单保存时，后台可调用【OssFileCacheUtils.get(fileId)或getBean(fileId,clazz)】方法，得到已上传oss的文件信息，并保存到数据库。
        <br/>
        文件信息DEMO：
        <json-viewer :value="demoJson"></json-viewer>
      </div>
      <div style="text-align: center;margin-top: 30px;">
        <a href="#"
           target="_blank">
          <el-button type="danger" icon="el-icon-question" plain>使用帮助</el-button>
        </a>
      </div>
    </div>
  </div>
</template>
<script>
import PluploadChunk from "@/components/Upload/PluploadChunk.vue";
import FileChunkResume from "@/components/Upload/FileChunkResume.vue";
import FileSecond from "@/components/Upload/FileSecond.vue";
import FileOssUpload from "@/components/Upload/FileOssUpload.vue";
import JsonViewer from 'vue-json-viewer'
import FileUpload from "@/components/Upload/FileUpload.vue";

export default {
  components: {FileUpload, FileOssUpload, FileSecond, FileChunkResume, PluploadChunk, JsonViewer},
  data() {
    return {
      // 上传类型：normal=普通上传;chunk=分片上传;chunkResume=断点续传;second=秒传;oss=OSS上传（阿里云）
      uploadType: 'normal',
      // 是否拷贝：文件秒传
      isCopy: true,
      // oss上传的文件id
      ossFileIds: [],
      demoJson: {
        "fileName": "demo/xxx.jpg",
        "fileSize": "1024",
        "fileOldName": "xx.jpg",
        "md5": "xxx",
        "fileId": "xxx",
        "fileUrl": "https://xxx.oss-xxx.aliyuncs.com/demo/xxx.jpg",
        "fileExtend": "jpg"
      },
      ossDemoFileName: '',
      ossDemoStsUrl: '',
      ossDemoStsType: 'other',
      // 文件上传列表（测试回显数据）
      uploadFileList: [
        {
          "fileUrl": "/upload/files/20241214/2d0e2c8b4eee4250a53df3c9340041d0.jpg",
          "fileOldName": "111.jpg",
          "fileName": "2d0e2c8b4eee4250a53df3c9340041d0.jpg",
          "fileExtend": "jpg",
          "fileSize": 148522
        }, {
          "fileUrl": "/upload/files/20241214/2d0e2c8b4eee4250a53df3c9340041d0.jpg",
          "fileOldName": "222.jpg",
          "fileName": "2d0e2c8b4eee4250a53df3c9340041d0.jpg",
          "fileExtend": "jpg",
          "fileSize": 148522
        }, {
          "fileUrl": "/upload/files/20241214/2d0e2c8b4eee4250a53df3c9340041d0.jpg",
          "fileOldName": "333.jpg",
          "fileName": "2d0e2c8b4eee4250a53df3c9340041d0.jpg",
          "fileExtend": "jpg",
          "fileSize": 148522
        },
      ]
    }
  },
  watch: {
    ossDemoFileName(val) {
      this.ossDemoStsUrl = '';
      this.ossDemoStsType = val.endsWith('.jpg') || val.endsWith('.png') ? 'img' : (val.endsWith('.mp4') ? 'video' : 'other');
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
    openStsFile() {
      const params = {fileName: this.ossDemoFileName}
      this.$request({
        url: '/oss/file/read/sts/url', method: 'get', params
      }).then(({data}) => {
        this.ossDemoStsUrl = data;
      })
    }
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
