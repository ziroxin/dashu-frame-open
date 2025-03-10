<template>
  <el-breadcrumb class="app-breadcrumb" separator="/">
    <transition-group name="breadcrumb">
      <el-breadcrumb-item v-for="(item,index) in levelList" :key="item.path">
        <span v-if="item.redirect==='noRedirect'||index===levelList.length-1" class="no-redirect">
          {{ item.meta.title }}
        </span>
        <a v-else @click.prevent="handleLink(item)">{{ item.meta.title }}</a>
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script>
import pathToRegexp from 'path-to-regexp'

export default {
  data() {
    return {
      levelList: null
    }
  },
  watch: {
    $route(route) {
      if (route.path.startsWith('/redirect/')) {
        return // 如果是重定向，页面跳转了，不需要手动刷新面包屑
      }
      this.getBreadcrumb()
    }
  },
  created() {
    this.getBreadcrumb()
  },
  methods: {
    getBreadcrumb() {
      // 获取有效路由列表
      let matched = this.$route.matched.filter(item => item.meta && item.meta.title)
      // 配置首页路由（无首页，自动加上）
      const first = matched[0] || null
      if (!first || !first.name || first.name.trim().toLocaleLowerCase() !== 'dashboard') {
        matched = [{path: '/dashboard/index', meta: {title: '首页'}}].concat(matched)
      }
      // 过滤掉不需要显示面包屑的路由
      this.levelList = matched.filter(item => item.meta && item.meta.title && item.meta.breadcrumb !== false)
    },
    handleLink(item) {
      const {redirect, path} = item
      if (redirect) {
        this.$router.push(redirect) // 重定向
        return
      }
      // 支持:id方式传参 @see https://github.com/PanJiaChen/vue-element-admin/issues/561
      let pathReg = pathToRegexp.compile(path)
      const {params} = this.$route
      this.$router.push(pathReg(params))
    }
  }
}
</script>

<style lang="scss" scoped>
.app-breadcrumb.el-breadcrumb {
  display: inline-block;
  font-size: 14px;
  line-height: 50px;
  margin-left: 8px;

  .no-redirect {
    color: #97a8be;
    cursor: text;
  }
}
</style>
