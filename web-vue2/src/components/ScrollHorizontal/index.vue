<template>
  <div class="container" :style="{width:containerWidth+'px',height:containerHeight+'px'}">
    <div class="content" ref="content" :style="{width:contentWidth+'px',marginLeft:contentMarginLeft+'px'}">
      <slot></slot>
    </div>
    <div class="scroll-btn left" @click="scrollLeft" v-if="showScroll"
         :class="{disabled:isLeftButtonDisabled}">
      <i class="el-icon-d-arrow-left"/>
    </div>
    <div class="scroll-btn right" @click="scrollRight" v-if="showScroll"
         :class="{disabled:isRightButtonDisabled}">
      <i class="el-icon-d-arrow-right"/>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ScrollHorizontal',
  props: {
    // 宽度
    containerWidth: {type: Number, default: 200},
    // 高度
    containerHeight: {type: Number, default: 100}
  },
  data() {
    return {
      // 左右按钮是否禁用
      isLeftButtonDisabled: false,
      isRightButtonDisabled: false,
      // 是否滚动（内容超出content容器时）
      showScroll: true
    };
  },
  computed: {
    contentWidth() {
      return this.showScroll ? this.containerWidth - 60 : this.containerWidth;
    },
    contentMarginLeft() {
      return this.showScroll ? 30 : 0;
    }
  },
  mounted() {
    console.log(999,this.containerWidth)
    // 判断是否需要滚动
    this.showScroll = this.containerWidth < this.$refs.content.scrollWidth;
    if (this.showScroll) {
      // 监听滚动事件，更新左右按钮状态
      this.$refs.content.addEventListener('scroll', this.updateButtonStatus);
      this.updateButtonStatus();
    }
  },
  methods: {
    scrollLeft() {
      // 左滑逻辑
      this.$refs.content.scrollBy({left: -(this.$refs.content.scrollWidth / 4), behavior: 'smooth'});
    },
    scrollRight() {
      // 右滑逻辑
      this.$refs.content.scrollBy({left: this.$refs.content.scrollWidth / 4, behavior: 'smooth'});
    },
    updateButtonStatus() {
      this.isLeftButtonDisabled = this.$refs.content.scrollLeft === 0;
      this.isRightButtonDisabled = this.$refs.content.scrollLeft + this.contentWidth >= this.$refs.content.scrollWidth;
    }
  },
  beforeDestroy() {
    try {
      // 移除滚动事件
      this.$refs.content.removeEventListener('scroll', this.updateButtonStatus);
    } catch (e) {
    }
  }
};
</script>

<style lang="scss" scoped>
.container {
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;

  .content {
    white-space: nowrap; /* 防止内容换行 */
    display: inline-block;
    overflow: hidden;
  }

  .scroll-btn {
    position: absolute;
    width: 20px;
    height: 30px;
    color: #888888;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1; /* 确保按钮在内容之上 */
    border-radius: 5px;

    &:hover {
      color: #666666;
      background: rgba(0, 0, 0, 0.1);
    }

    &.disabled {
      pointer-events: none; // 禁用点击事件
      opacity: 0.3; // 设置半透明显示
      cursor: not-allowed; // 设置鼠标样式为不可用
    }
  }

  .scroll-btn.left {
    left: 0;
  }

  .scroll-btn.right {
    right: 0;
  }
}
</style>