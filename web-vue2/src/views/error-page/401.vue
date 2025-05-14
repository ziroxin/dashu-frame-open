<template>
  <div class="errPage-container">
    <el-row>
      <el-col :span="14">
        <div class="bullshit">
          <div class="bullshit__oops">OOPS!</div>
          <div class="bullshit__headline">401权限不足！</div>
          <div class="bullshit__info">请检查您输入的网址是否正确！<br/>或点击 ↓↓↓↓ 下方按钮返回主页</div>
          <a @click="$router.go(-1)" class="bullshit__return-home"
             style="border:1px solid #1482f0;color:#1482f0;background:#fff;">返回上一页</a>
          <a @click="$router.push('/')" class="bullshit__return-home">去主页</a>
        </div>
      </el-col>
      <el-col :span="10">
        <img src="@/assets/401_images/401.gif" style="border-radius: 10px;box-shadow: 0 0 10px rgba(0,0,0,0.1);"
             width="313" height="428"/>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'Page401'
}
</script>

<style lang="scss" scoped>
.errPage-container {
  transform: translate(-50%, -50%);
  position: absolute;
  top: 40%;
  left: 50%;

  .bullshit {
    position: relative;
    float: left;
    width: 400px;
    padding: 30px 0;
    overflow: hidden;

    &__oops {
      font-size: 32px;
      font-weight: bold;
      line-height: 40px;
      color: #1482f0;
      opacity: 0;
      margin-bottom: 20px;
      animation-name: slideUp;
      animation-duration: 0.5s;
      animation-fill-mode: forwards;
    }

    &__headline {
      font-size: 20px;
      line-height: 24px;
      color: #222;
      font-weight: bold;
      opacity: 0;
      margin-bottom: 10px;
      animation-name: slideUp;
      animation-duration: 0.5s;
      animation-delay: 0.1s;
      animation-fill-mode: forwards;
    }

    &__info {
      font-size: 13px;
      line-height: 21px;
      color: grey;
      opacity: 0;
      margin-bottom: 30px;
      animation-name: slideUp;
      animation-duration: 0.5s;
      animation-delay: 0.2s;
      animation-fill-mode: forwards;
    }

    &__return-home {
      margin: 0px 10px;
      display: block;
      float: left;
      width: 110px;
      height: 36px;
      background: #1482f0;
      border-radius: 100px;
      text-align: center;
      color: #ffffff;
      opacity: 0;
      font-size: 14px;
      line-height: 36px;
      cursor: pointer;
      animation-name: slideUp;
      animation-duration: 0.5s;
      animation-delay: 0.3s;
      animation-fill-mode: forwards;
    }

    @keyframes slideUp {
      0% {
        transform: translateY(60px);
        opacity: 0;
      }
      100% {
        transform: translateY(0);
        opacity: 1;
      }
    }
  }
}
</style>
