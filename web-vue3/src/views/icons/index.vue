<template>
  <div>
    <el-affix :offset="10">
      <div class="app-container shadow b-1px b-solid b-#ddd">
        <div class="flex items-center justify-evenly">
          <el-input v-model="searchText" placeholder="搜索图标" class="w-300px! mr-30px" clearable/>
          <div class="mr-30px">
            <span class="text-14px text-#555">图标大小：</span>
            <el-input-number v-model="iconSize" :min="10" :step="5"/>
          </div>
          <el-link @click="iconDialogVisible=true" underline="always" type="danger">图标组件使用说明</el-link>
        </div>
      </div>
    </el-affix>
    <div class="app-container">
      <div class="grid-icon">
        <div v-for="item of showSvgIcons" :key="item" v-clipboard:copy="item">
          <el-tooltip placement="top">
            <template #content>
              图标组件：{{ generateSvgIconCode(item) }}
              <base-button type="primary" circle plain icon="el-icon-copy-document" size="small"
                           class="ml-10px" v-clipboard:copy="generateSvgIconCode(item)"/>
              <br/>图标名称：{{ item }}
              <base-button type="primary" circle plain icon="el-icon-copy-document" size="small"
                           class="ml-10px" v-clipboard:copy="item"/>
            </template>
            <div class="icon-item">
              <my-icon :icon="item" :size="iconSize"/>
              <span class="text-12px text-center mt-10px text-#555 w-100px">
              {{ item }}
            </span>
            </div>
          </el-tooltip>
        </div>
      </div>
    </div>
    <!-- 图标组件使用说明弹窗 -->
    <el-dialog v-model="iconDialogVisible" title="my-icon 组件使用说明" width="800" top="5vh" draggable>
      <div class="text-14px text-#666 line-height-40px m-[10px_20px]">
        <div class="text-20px font-bold">参数说明：</div>
        <div class="ml-20px">
          <div>icon: 图标名称，必填，字符串（以“svg-”开头或不加前缀 表示本地图标； 以“vi-”开头 表示在线图标）</div>
          <div>color: 图标颜色，非必填，字符串</div>
          <div>size: 图标大小，非必填，数字，默认16</div>
          <div>hoverColor: 鼠标悬停颜色，非必填，字符串</div>
        </div>
        <div class="text-20px font-bold">使用说明：</div>
        <div class="font-bold">1. 本地图标，svg-开头 或 不加前缀</div>
        <div class="ml-20px">
          <div>.svg文件存放目录：src/assets/svgs</div>
          <div class="flex items-center">示例：
            <my-icon icon="svg-exit" :size="30" class="m-10px"/>
            <span class="text-blue mr-10px">&lt;my-icon icon="<b>svg-exit</b>" :size="30" /&gt;</span>
            或
            <my-icon icon="exit" :size="30" class="m-10px"/>
            <span class="text-green">&lt;my-icon icon="<b>exit</b>" :size="30" /&gt;</span>
          </div>
        </div>
        <div class="font-bold">2. 在线图标，vi-开头</div>
        <div class="ml-20px">
          <div class="flex items-center">在网站
            <el-link href="https://icon-sets.iconify.design/" target="_blank" type="primary" class="m-[0_10px]">
              https://icon-sets.iconify.design/
            </el-link>
            搜索图标（由于是国外网站只支持英文搜索）
          </div>
          <div>点击想用的图标，拷贝图标的 “Icon name”，加上前缀 vi- 即可使用</div>
          <div class="flex items-center">示例：
            <my-icon icon="vi-twemoji:flag-china" :size="30" class="m-10px"/>
            <span class="text-red">&lt;my-icon icon="<b>vi-twemoji:flag-china</b>" :size="30" /&gt;</span>
          </div>
          <div>注意：build打包时会自动打包成本地svg，无需手动导入</div>
          <div class="font-bold mt-10px b-t-1px b-t-dashed b-t-#ccc">在线图标测试</div>
          <div class="flex items-center">
            <el-input v-model="testOnlineIcon" placeholder="输入图标名称" clearable class="w-300px! mr-20px">
              <template #prefix><span class="m-[0_10px]">vi-</span></template>
            </el-input>
            <my-icon :icon="'vi-'+testOnlineIcon" :size="iconSize"/>
            <span v-if="testOnlineIcon" class="ml-20px">icon="{{ 'vi-' + testOnlineIcon }}"</span>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import svgIcons from './svg-icons'

export default {
  name: 'Icons',
  data() {
    return {
      svgIcons,
      searchText: '',
      iconSize: 30,
      iconDialogVisible: false,
      testOnlineIcon: 'twemoji:flag-china'
    }
  },
  computed: {
    showSvgIcons() {
      return this.searchText ? this.svgIcons.filter(icon => icon.includes(this.searchText)) : this.svgIcons
    }
  },
  methods: {
    generateSvgIconCode(symbol) {
      return `<my-icon icon="${symbol}" :size="${this.iconSize}"/>`
    }
  }
}
</script>

<style lang="less" scoped>
.app-container {
  .grid-icon {
    position: relative;
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
    grid-gap: 15px;
    .icon-item {
      width: 120px;
      height: 120px;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      cursor: pointer;
      border: 1px dashed #ccc;
      border-radius: 5px;
    }
  }
}
</style>
