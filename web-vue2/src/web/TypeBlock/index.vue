<template>
  <div class="block">
    <div class="block-info">
      <div class="block-title">
        <div class="block-index">{{ index }} [{{ title }}]</div>
        <img :src="img">
      </div>
      <div class="block-content">{{ content }}</div>
    </div>

    <el-progress v-if="progress" :text-inside="true" style="margin:0px 20px;"
                 :stroke-width="15" :percentage="progress" color="#"
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
.block:hover {
  background-color: rgba(24, 144, 255, 0.1);
}

.block {
  display: inline-block;
  width: 280px;
  height: 150px;
  margin: 20px 10px;
  vertical-align: top;
  border: 1px solid #ccc;
  border-radius: 10px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  cursor: pointer;

  .block-info {
    border-radius: 10px;
    line-height: 40px;
    padding: 0px 10px;
    margin-bottom: 10px;

    .block-title {
      height: 50px;
      line-height: 50px;
      color: #D7000F;

      img {
        max-width: 50px;
        max-height: 50px;
        line-height: 50px;
        vertical-align: middle;
        object-fit: cover;
        margin-left: 10px;
      }

      .block-index {
        display: inline-block;
        font-size: 18px;
        border-radius: 5px;
        text-align: center;
      }
    }

    .block-content {
      height: 60px;
      line-height: 30px;
      font-size: 14px;
      color: #303133;
    }
  }
}
</style>
