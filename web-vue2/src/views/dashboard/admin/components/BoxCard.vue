<template>
  <el-card class="box-card-component" style="margin-left:8px;border-radius: 5px;">
    <div slot="header" class="box-card-header">
      <img src="@/assets/images/loginbg1.jpg" style="object-fit: cover;">
    </div>
    <div style="position:relative;">
      <pan-thumb class="panThumb"></pan-thumb>
      <mallki class-name="mallki-text" :text="settings.title"/>
      <div style="padding-top:35px;" class="progress-item">
        <span>Vue</span>
        <el-progress :percentage="70"/>
      </div>
      <div class="progress-item">
        <span>JavaScript</span>
        <el-progress :percentage="18"/>
      </div>
      <div class="progress-item">
        <span>CSS</span>
        <el-progress :percentage="12"/>
      </div>
      <div class="progress-item">
        <span>ESLint</span>
        <el-progress :percentage="100" status="success"/>
      </div>
      <div style="font-size:14px;color:#333333;margin-top:20px;">
        <!-- 使用插值语法时，可直接使用以下格式，不需要导入parseTime函数（应为在main.js中注册了filter） -->
        {{ new Date() | parseTime('yyyy-MM-dd HH:mm:ss SSS (ww)') }}
      </div>
      <div style="font-size:14px;color:#333333;margin-top:20px;">{{ currentTime }}</div>
    </div>
  </el-card>
</template>

<script>
import {mapGetters} from 'vuex'
import PanThumb from '@/components/PanThumb'
import Mallki from '@/components/TextHoverEffect/Mallki'
import {parseTime} from '@/utils'

export default {
  components: {PanThumb, Mallki},
  filters: {
    statusFilter(status) {
      const statusMap = {success: 'success', pending: 'danger'}
      return statusMap[status]
    }
  },
  data() {
    return {
      statisticsData: {
        article_count: 1024,
        pageviews_count: 1024
      },
      currentTime: ''
    }
  },
  mounted() {
    setInterval(() => {
      // 在js中使用parseTime函数时，需要先导入
      this.currentTime = parseTime(new Date(), 'yyyy-MM-dd HH:mm:ss')
    }, 1000)
  },
  computed: {
    ...mapGetters([
      'name',
      'avatar',
      'roles',
      'settings'
    ])
  }
}
</script>

<style lang="scss">
.box-card-component {
  .el-card__header {
    padding: 0px !important;
  }
}
</style>
<style lang="scss" scoped>
.box-card-component {
  .box-card-header {
    position: relative;
    height: 160px;

    img {
      width: 100%;
      height: 100%;
      transition: all 0.2s linear;

      &:hover {
        transform: scale(1.1, 1.1);
        filter: contrast(130%);
      }
    }
  }

  .mallki-text {
    position: absolute;
    top: 0px;
    right: 0px;
    font-size: 20px;
    font-weight: bold;
  }

  .panThumb {
    z-index: 100;
    height: 70px !important;
    width: 70px !important;
    position: absolute !important;
    top: -45px;
    left: 0px;
    border: 5px solid #ffffff;
    border-radius: 8px;
    background-color: #fff;
    margin: auto;
    box-shadow: none !important;

    ::v-deep .pan-info {
      box-shadow: none !important;
    }
  }

  .progress-item {
    margin-bottom: 10px;
    font-size: 14px;
  }

  @media only screen and (max-width: 1510px) {
    .mallki-text {
      font-size: 16px;
      font-weight: normal;
    }
  }
}
</style>
