<template>
  <el-form style="text-align: center;">
    <el-form-item label="数据字典示例">
      <el-select v-model="sex">
        <el-option v-for="item in dict.dict.sys_user_sex" :key="item.value"
                   :value="item.value" :label="item.label"></el-option>
      </el-select>
    </el-form-item>
  </el-form>
</template>

<script>
export default {
  name: 'dictDataDemo',
  dicts: ['sys_user_sex'],
  data() {
    return {
      sex: '2'
    }
  },
  mounted() {
    // 输出字典数据
    console.log(this.dict.dict.sys_user_sex);
  }
}
</script>
