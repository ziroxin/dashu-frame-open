<template>
  <div class="app-container">
    <h1 class="text-center">三级菜单测试页面</h1>
    <el-divider/>
    <ul class="text-16px line-height-40px color-#333333 list-decimal p-[0_20px]">
      <li>支持多级嵌套菜单，但必须有对应的页面路由配合</li>
      <li>
        例如：/views/demo/menu1/index.vue是一个空页面，作为【二级菜单】的组件
        <br/>
        其中menu1/index.vue中只有一个【&lt;router-view/&gt;】标签，用于加载页面
      </li>
    </ul>
  </div>
</template>