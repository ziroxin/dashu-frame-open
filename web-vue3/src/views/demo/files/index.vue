<template>
  <div class="app-container">
    <h2 class="title">文件普通上传、分片上传、秒传、断点续传</h2>
    <div class="text-center mb-50px">
      <el-radio-group v-model="uploadType">
        <el-radio-button value="normal">普通上传</el-radio-button>
        <el-radio-button value="chunk">分片上传</el-radio-button>
        <el-radio-button value="chunkResume">断点续传</el-radio-button>
        <el-radio-button value="second">秒传</el-radio-button>
        <el-radio-button value="oss">OSS上传（阿里云）</el-radio-button>
        <el-radio-button value="imgCrop">图片裁切</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 文件普通上传 -->
    <el-divider content-position="center" v-if="uploadType==='normal'">文件普通上传</el-divider>
    <div class="content" v-if="uploadType==='normal'">
      <div class="uploadPanel">
        <el-upload :action="$baseServer+'/upload/files'" :headers="getTokenHeader()"
                   @success="uploadFileSuccess" accept=".zip,.rar" :show-file-list="true" :auto-upload="true">
          <base-button type="primary" icon="el-icon-upload2">点击选择文件上传</base-button>
        </el-upload>
      </div>
      <div class="text-center mt-30px">
        <a href="http://docs.java119.cn/use/comm-fileupload.html" target="_blank">
          <base-button type="danger" icon="el-icon-question" plain>使用帮助</base-button>
        </a>
      </div>

      <div class="mt-100px">
        <el-divider><span class="color-red">文件普通上传 - 使用自定义下载组件的DEMO</span></el-divider>
        <div class="w-600px b-1 b-dashed  b-#ccc b-rd-10px p-10px m-[20px_auto]">
          <file-upload v-model="uploadFileList" :limit-count="2"/>
        </div>
        <div class="flex b-1 b-dashed b-#ccc b-rd-10px p-10px">
          <base-button type="primary" plain class="mr-10px mt-10px w-100px">文件List</base-button>
          <div class="flex flex-col">
            <div v-for="item in uploadFileList" :key="item.fileUrl" type="info" class="text-12px color-#666 lh-30px">
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
        <plupload-chunk upload-server-url="/upload/chunks" upload-dir="testChunks" key="chunk"
                        max-file-size="300mb" chunk-size="10mb"
                        :mime-types="[{title: 'Zip files', extensions: 'zip'}]"/>
      </div>
      <div class="text-center mt-30px">
        <a href="http://docs.java119.cn/use/comm-fileupload2.html" target="_blank">
          <base-button type="danger" icon="el-icon-question" plain>使用帮助</base-button>
        </a>
      </div>
    </div>

    <!-- 文件断点续传 -->
    <el-divider content-position="center" v-if="uploadType==='chunkResume'">文件断点续传</el-divider>
    <div class="content" v-if="uploadType==='chunkResume'">
      <div class="text-12px color-#dd1f29 b-b-1 b-b-dashed b-b-#eee mb-15px p-[0_10px]">
        说明：断点续传，是把文件分片，每次上传前，检测分片是否已上传。若已上传，则跳过；未上传的则继续上传。
      </div>
      <div class="uploadPanel">
        <file-upload-breakpoint-resume folder="testChunksResume" key="chunkResume" accept=".zip,.rar"
                                       tip-info="文件断点续传上传，最大支持300M，分片大小10M，支持zip、rar格式"
                                       :limit-size="300*1024*1024" :chunk-size="1*1024*1024"/>
      </div>
      <div class="text-center mt-30px">
        <a href="http://docs.java119.cn/use/comm-fileupload2.html#_2-%E6%96%87%E4%BB%B6%E6%96%AD%E7%82%B9%E7%BB%AD%E4%BC%A0"
           target="_blank">
          <base-button type="danger" icon="el-icon-question" plain>使用帮助</base-button>
        </a>
      </div>
    </div>

    <!-- 文件秒传 -->
    <el-divider content-position="center" v-if="uploadType==='second'">文件秒传</el-divider>
    <div class="content" v-if="uploadType==='second'">
      <div class="text-12px b-b-1 b-b-dashed b-b-#eee mb-15px ">
        <div>
          使用本功能上传的文件，全部存入 “ 文件秒传表 ”。
          点击进入：
          <base-button link type="primary" @click="$router.push('/files')">[文件秒传管理]</base-button>
        </div>
        <div class="text-#dd1f29">
          说明：上传前，先根据文件 md5 判断，表中是否存在：
          <el-tag type="success" size="small" class="mr-10px">若已存在，则跳过，直接返回结果；</el-tag>
          <el-tag type="danger" size="small">若不存在，则上传，并存入 “ 文件秒传表 ”，返回结果。</el-tag>
        </div>
      </div>
      <div>
        <div class="secondInfo">
          <el-switch v-model="isCopy" active-color="#13ce66" inactive-color="#ff4949"
                     active-text="拷贝文件（拷贝一个新文件，返回新的fileUrl）"
                     inactive-text="不拷贝文件（共用“文件秒传表”中的fileUrl，若表中文件被删除，可能会出错）"/>
        </div>
        <div class="uploadPanel">
          <file-second second-server-url="/upload/second/chunks" second-md5-url="/upload/second/md5"
                       upload-dir="testSecond" mime-types=".zip,.rar" :is-copy="isCopy"
                       :max-file-size="300*1024*1024" :chunk-size="10*1024*1024"/>
        </div>
        <div class="text-center mt-30px">
          <a href="http://docs.java119.cn/use/comm-fileupload3.html" target="_blank">
            <base-button type="danger" icon="el-icon-question" plain>使用帮助</base-button>
          </a>
        </div>
      </div>
    </div>

    <!-- OSS上传（阿里云） -->
    <el-divider content-position="center" v-if="uploadType==='oss'">OSS上传（阿里云）</el-divider>
    <div class="content" v-if="uploadType==='oss'">
      <div class="text-12px color-#dd1f29 b-b-1 b-b-dashed b-b-#eee mb-15px">
        上传说明：Oss直传（从后台获取上传凭证，前端直接上传到oss，文件不经过服务器）
      </div>
      <div class="uploadPanel">
        <file-oss-upload v-model="ossFileIds" oss-folder="demoFolder" :limit="2" accept=".jpg,.png,.mp4"/>
        <el-divider/>
        <div class="text-12px mt-10px color-#666">
          已上传的fileId：{{ ossFileIds }}
        </div>
      </div>
      <div class="text-12px color-#dd1f29 b-b-1 b-b-dashed b-b-#eee mb-15px">
        下载说明：下载Oss文件分2种：1一种是配置公共读，可以直接下载（一定要配置防盗链）；2另一种使用STS临时凭证下载，下方示例为STS临时凭证下载。
      </div>
      <div class="uploadPanel">
        <div class="text-12px mt-10px color-#666">
          oss存储文件夹+文件名：
          <el-input v-model="ossDemoFileName" class="w-50%!"/>
          <base-button type="primary" class="ml-10px" @click="openStsFile">获取STS临时凭证</base-button>
          <template v-if="ossDemoStsUrl">
            <img :src="ossDemoStsUrl" v-if="ossDemoStsType==='img'" class="max-w-300px"/>
            <video :src="ossDemoStsUrl" controls v-else-if="ossDemoStsType==='video'" class="max-w-300px"></video>
            <a :href="ossDemoStsUrl" v-else target="_blank">点击下载</a>
          </template>
        </div>
      </div>
      <el-divider/>
      <div class="text-12px color-#666 b-b-1 b-b-dashed b-b-#eee mb-15px">
        文件上传oss成功后，会回调后台，后台将上传信息存入redis，20分钟有效期，key是fileId。
        <br/>
        表单保存时，后台可调用【OssFileCacheUtils.get(fileId)或getBean(fileId,clazz)】方法，得到已上传oss的文件信息，并保存到数据库。
        <br/>
        文件信息DEMO：
        <json-editor v-model="demoJson"/>
      </div>
      <div class="text-center mt-30px">
        <a href="#" target="_blank">
          <base-button type="danger" icon="el-icon-question" plain>使用帮助</base-button>
        </a>
      </div>
    </div>

    <!-- 图片裁切 -->
    <el-divider content-position="center" v-if="uploadType==='imgCrop'">图片裁切 - DEMO</el-divider>
    <div class="content" v-if="uploadType==='imgCrop'">
      <image-cropping image-url="https://hips.hearstapps.com/hmg-prod/images/%E5%AE%8B%E6%99%BA%E5%AD%9D-1597774015.jpg"/>
    </div>
  </div>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import FileOssUpload from '@/components/Upload/FileOssUpload.vue'
import FileUpload from '@/components/Upload/FileUpload.vue'
import FileSecond from '@/views/demo/files/FileSecond.vue'
import PluploadChunk from '@/views/demo/files/PluploadChunk.vue'
import FileUploadBreakpointResume from '@/components/Upload/FileUploadBreakpointResume.vue'
import { JsonEditor } from '@/components/JsonEditor'
import { ImageCropping } from '@/components/ImageCropping'
import request from '@/utils/request'
import { getTokenHeader } from '@/utils/auth'

// 上传类型：normal=普通上传;chunk=分片上传;chunkResume=断点续传;second=秒传;oss=OSS上传（阿里云）
const uploadType = ref('normal')
// 是否拷贝：文件秒传
const isCopy = ref(true)
// oss上传的文件id
const ossFileIds = ref([])
const demoJson = ref({
  fileName: 'demo/xxx.jpg', fileSize: '1024', fileOldName: 'xx.jpg', md5: 'xxx', fileId: 'xxx',
  fileUrl: 'https://xxx.oss-xxx.aliyuncs.com/demo/xxx.jpg', fileExtend: 'jpg'
})
const ossDemoFileName = ref('')
const ossDemoStsUrl = ref('')
const ossDemoStsType = ref('other')
// 文件上传列表（测试回显数据）
const uploadFileList = ref([{
  fileUrl: '/upload/files/20241214/2d0e2c8b4eee4250a53df3c9340041d0.jpg', fileOldName: '111.jpg',
  fileName: '2d0e2c8b4eee4250a53df3c9340041d0.jpg', fileExtend: 'jpg', fileSize: 148522
}, {
  fileUrl: '/upload/files/20241214/2d0e2c8b4eee4250a53df3c9340041d0.jpg', fileOldName: '222.jpg',
  fileName: '2d0e2c8b4eee4250a53df3c9340041d0.jpg', fileExtend: 'jpg', fileSize: 148522
}, {
  fileUrl: '/upload/files/20241214/2d0e2c8b4eee4250a53df3c9340041d0.jpg', fileOldName: '333.jpg',
  fileName: '2d0e2c8b4eee4250a53df3c9340041d0.jpg', fileExtend: 'jpg', fileSize: 148522
}])

watch(ossDemoFileName, (val) => {
  ossDemoStsUrl.value = ''
  ossDemoStsType.value = val.endsWith('.jpg') || val.endsWith('.png') ? 'img' : (val.endsWith('.mp4') ? 'video' : 'other')
})

// 文件上传成功
const uploadFileSuccess = (response) => {
  if (response.code === '200') {
    ElMessage({type: 'success', message: '文件上传成功！'})
  } else {
    ElMessage({type: 'error', message: response.message})
  }
}

const openStsFile = () => {
  const params = {fileName: ossDemoFileName.value}
  request({url: '/oss/file/read/sts/url', method: 'get', params}).then(({data}) => {
    ossDemoStsUrl.value = data
  })
}
</script>

<style scoped lang="less">
.title {
  text-align: center;
  margin: 20px auto 30px auto;
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
