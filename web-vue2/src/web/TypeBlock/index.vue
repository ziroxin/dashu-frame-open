<template>
  <div class="block">
    <div class="block-info"
         :style="{background:'linear-gradient(rgba(40,140,234,0.5),rgba(40,140,234,0.3)),url('+img+') no-repeat 120px 0px/ 150px 150px'}"
    >
      <div class="block-title">
        <div class="block-index">{{ index }}</div>
        <span style="float: right">{{ title }}</span>
      </div>
      <div class="block-content">{{ content }}</div>
    </div>
    <el-progress v-if="progress" :text-inside="true" :stroke-width="20"
                 :percentage="progress" :color="customColors"
    />
  </div>
</template>

<script>
export default {
  name: 'TypeBlock',
  props: ['img', 'title', 'content', 'index', 'progress'],
  data() {
    return {
      customColors: [
        {color: '#f56c6c', percentage: 25},
        {color: '#e6a23c', percentage: 50},
        {color: '#1989fa', percentage: 75},
        {color: '#5cb87a', percentage: 100}
      ],
      imgUrl: this.img
    }
  },
  methods: {}
}
</script>
<style lang="scss" scoped>
.block {
  display: inline-block;
  width: 280px;
  height: 180px;
  margin: 5px 10px;
  vertical-align: top;

  .block-info {
    border-radius: 10px;
    line-height: 40px;
    padding: 10px;
    margin-bottom: 10px;

    .block-title {
      height: 50px;
      line-height: 50px;

      img {
        float: right;
        width: 50px;
        height: 50px;
        object-fit: cover;
      }

      span {
        float: right;
        font-size: 18px;
        font-weight: bold;
      }

      .block-index {
        float: left;
        width: 30px;
        height: 30px;
        line-height: 30px;
        font-size: 12px;
        border-radius: 100%;
        background-color: #D7000F;
        color: #fff;
        text-align: center;
      }
    }

    .block-content {
      height: 80px;
      line-height: 40px;
      font-size: 14px;
      color: #00afff;
    }
  }
}
</style>
