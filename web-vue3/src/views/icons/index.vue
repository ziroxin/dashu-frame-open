<template>
  <div class="icons-container app-container">
    <aside>
      <a href="https://panjiachen.github.io/vue-element-admin-site/zh/guide/advanced/icon.html" target="_blank">
        添加新图标或使用图标说明文档
      </a>
    </aside>
    <el-tabs type="border-card">
      <el-tab-pane label="Icons">
        <div class="grid">
          <div v-for="item of svgIcons" :key="item" v-clipboard:copy="generateIconCode(item)">
            <el-tooltip placement="top">
              <template #content>
                <div>{{ generateIconCode(item) }}</div>
              </template>
              <div class="icon-item">
                <my-icon :icon="item" class-name="disabled"/>
                <span>{{ item }}</span>
              </div>
            </el-tooltip>
          </div>
        </div>
      </el-tab-pane>
      <el-tab-pane label="Element-UI Icons">
        <div class="grid">
          <div v-for="item of elementIcons" :key="item" v-clipboard:copy="generateElementIconCode(item)">
            <el-tooltip placement="top">
              <template #content>
                <div>{{ generateElementIconCode(item) }}</div>
              </template>
              <div class="icon-item">
                <my-icon :icon="'el-icon-' + item"/>
                <span>{{ item }}</span>
              </div>
            </el-tooltip>
          </div>
        </div>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import svgIcons from './svg-icons'
import elementIcons from './element-icons'
import { MyIcon } from '@/components/MyIcon'

export default {
  name: 'Icons',
  components: {MyIcon},
  data() {
    return {
      svgIcons,
      elementIcons
    }
  },
  methods: {
    generateIconCode(symbol) {
      return `<my-icon icon="${symbol}" />`
    },
    generateElementIconCode(symbol) {
      return `<my-icon icon="el-icon-${symbol}" />`
    }
  }
}
</script>

<style lang="less" scoped>
.icons-container {
  margin: 10px 20px 0;
  overflow: hidden;

  .grid {
    position: relative;
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  }

  .icon-item {
    margin: 20px;
    height: 85px;
    text-align: center;
    width: 100px;
    float: left;
    font-size: 30px;
    color: #24292e;
    cursor: pointer;
  }

  span {
    display: block;
    font-size: 16px;
    margin-top: 10px;
  }

  .disabled {
    pointer-events: none;
  }
}
</style>
