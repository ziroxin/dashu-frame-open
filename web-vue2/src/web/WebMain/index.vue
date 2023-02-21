<template>
  <div class="main">
    <!-- 左侧导航 -->
    <div class="main-left">
      <div class="main-left-title">
        学习目录
      </div>
      <el-menu
        default-active="2"
        class="el-menu-vertical-demo"
      >
        <el-menu-item index="1">
          <i class="el-icon-menu" />
          <span slot="title">导航1</span>
        </el-menu-item>
        <el-menu-item index="2">
          <i class="el-icon-setting" />
          <span slot="title">导航2</span>
        </el-menu-item>
      </el-menu>
    </div>
    <!-- 右侧主要区域 -->
    <div class="main-right">
      <type-panel class-id="sdfsdf" class-name="Java基础" />
    </div>
  </div>
</template>

<script>
import TypePanel from '@/web/TypePanel';

export default {
  name: 'WebMain',
  components: {TypePanel},
  data() {
    return {}
  },
  methods: {}
}
</script>
<style lang="scss" scoped>
.el-menu {
  border-right: 0px !important;
}
.el-menu-item.is-active{
  color: #D7000F;
}

.main {
  .main-right {
    padding: 0px 0px 0px 20px;
    width: calc(100% - 200px);
    display: inline-block;
  }

  .main-left {
    width: 200px;
    vertical-align: top;
    display: inline-block;
    border: 1px solid #e8e8e8;
    border-radius: 10px;
    padding-bottom: 10px;
    box-shadow: 0 3px 5px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04);

    .main-left-title {
      font-size: 16px;
      width: 100%;
      border-radius: 10px 10px 0px 0px;
      border-bottom: 1px solid #e8e8e8;
      text-align: center;
      line-height: 30px;
      padding: 5px 20px;
      background-color: #f5f5f5;
    }
  }
}
</style>
