<!--
 * 树形下拉框
 * 数据格式：[{value:'-1',label:'顶级',children:[{value:'1',label:'一级'}]}]
 * @Author: ziro
 * @Date: 2023/01/13 09:15:40
 -->
<template>
  <el-select class="main-select-tree" ref="selectTree" v-model="selectValue">
    <el-option :label="emptyText" :value="emptyValue" :key="emptyValue"></el-option>
    <el-option v-for="item in options" v-show="false"
               :key="item.value" :label="item.label" :value="item.value"/>
    <el-tree class="main-select-el-tree" ref="selectElTree"
             :data="data"
             :current-node-key="selectValue"
             node-key="value"
             highlight-current
             :props="props"
             @node-click="handleNodeClick"
             :expand-on-click-node="false"
             default-expand-all/>
  </el-select>
</template>

<script>
export default {
  name: 'select-tree',
  props: {
    // 选中值
    value: {type: String, default: ''},
    // 空值的value
    emptyValue: {type: String, default: ''},
    // 控制的text
    emptyText: {type: String, default: '请选择'},
    // 选中名称
    name: {type: String, default: ''},
    // 数据
    data: {type: Array, default: []},
    // Tree的props
    props: {type: Object, default: {children: 'children', label: 'label'}}
  },
  data() {
    return {
      selectValue: '',
      options: [],
    }
  },
  mounted() {
    console.log('mounted', this.value, "|", this.selectValue)
    this.selectValue = this.value
  },
  watch: {
    data() {
      this.options = []
      this.formatOptions(this.data)
    },
    selectValue() {
      console.log('watch selectvalue', this.value, "|", this.selectValue)
      this.$emit("input", this.selectValue)
    },
    value() {
      console.log('watch value', this.value, "|", this.selectValue)
      this.selectValue = this.value
    }
  },
  methods: {
    // 格式化下拉选项数据
    formatOptions(data) {
      data.forEach((item, key) => {
        this.options.push({label: item.label, value: item.value});
        if (item.children) {
          this.formatOptions(item.children)
        }
      });
    },
    handleNodeClick(node) {
      this.selectValue = node.value
      // 离开焦点，隐藏树
      this.$refs.selectTree.blur();
    }
  }
}
</script>
<style>
.main-select-el-tree .el-tree-node .is-current > .el-tree-node__content {
  font-weight: bold;
  color: #409eff;
}

.main-select-el-tree .el-tree-node.is-current > .el-tree-node__content {
  font-weight: bold;
  color: #409eff;
}
</style>
