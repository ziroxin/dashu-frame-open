<template>
  <el-container>
    <!-- 顶部 -->
    <el-header>
      <div class="header-logo">
        <img :src="logo" alt="java119.cn">
      </div>
      <div class="header-menu">
        <el-menu :default-active="activeIndex" class="el-menu-demo"
                 text-color="#999" active-text-color="#D7000F"
                 mode="horizontal" @select="handleSelect"
        >
          <el-menu-item index="1">首页</el-menu-item>
          <el-menu-item index="2">学习路线</el-menu-item>
          <el-menu-item index="3">问题交流区</el-menu-item>
          <el-submenu index="4">
            <template slot="title">更多</template>
            <el-menu-item index="2-1">选项1</el-menu-item>
            <el-menu-item index="2-2">选项2</el-menu-item>
            <el-menu-item index="2-3">选项3</el-menu-item>
          </el-submenu>
        </el-menu>
      </div>
    </el-header>
    <el-main>
      <web-main />
    </el-main>
    <el-footer>
      <el-link type="primary" :underline="false" target="_blank" href="//www.java119.cn">java119.cn</el-link>
      <el-link type="info" :underline="false">— All Rights Reserved.</el-link>
      <el-link type="info" :underline="false">@ 2022-2032</el-link>
      <el-link type="primary" target="_blank" href="https://beian.miit.gov.cn/">鲁ICP备2022008798号-1</el-link>
      <el-link type="info" :underline="false" style="float:right;color: #D7000F;">网站主色调：中国红（#D7000F）</el-link>
    </el-footer>
  </el-container>
</template>

<script>
import WebMain from '@/web/WebMain';

export default {
  name: 'WebIndex',
  components: {WebMain},
  data() {
    return {
      logo: 'http://www.java119.cn/img/site/logo.png',
      activeIndex: '1'
    }
  },
  methods: {
    handleSelect(key, keyPath) {
      console.log(key, keyPath);
    }
  }
}
</script>
<style lang="scss" scoped>
body {
  font-family: 'Hiragino Sans GB';
  letter-spacing: 5px!important;
}

.el-menu.el-menu--horizontal {
  border-bottom: 0px !important;
}

.el-container {
  letter-spacing: 3px!important;

  .el-header {
    height: 61px !important;
    border-bottom: 1px solid #D7000F;
    margin-bottom: 10px;

    .header-logo {
      float: left;
      display: inline-block;
      height: 60px;

      img {
        object-fit: cover;
        height: 60px;
      }
    }

    .header-menu {
      height: 68px;
      display: inline-block;
      margin-left: 100px;

      .el-menu-demo {
      }
    }
  }

  .el-main {
    min-height: calc(100vh - 120px);
  }

  .el-footer {
    max-height: 45px;
    line-height: 45px;
    background-color: #F1F2E5;
    font-size: 12px;
    text-align: center;
    border-top: 1px solid #D7000F;

    a {
      font-size: 12px;
      margin-left: 10px;
    }
  }
}
</style>
