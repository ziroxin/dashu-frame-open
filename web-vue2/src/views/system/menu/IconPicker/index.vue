<!--图标选择器-->
<template>
  <div class="IconPickder">
    <el-popover width="600" trigger="click">
      <div style="max-height:400px;overflow-y: scroll;overflow-x:hidden">
        <el-button v-for="(item,index) in iconList" :key="index" plain style="margin:2px;" @click="iconName=item">
          <eIcon :icon-name="item" style="font-size: 20px;" />
        </el-button>
      </div>

      <el-button slot="reference">
        <span v-if="iconName===''">请选择图标</span>
        <span v-else>
          <eIcon :icon-name="iconName" />
        </span>
      </el-button>
    </el-popover>
    <el-button v-show="iconName!==''" type="text" style="margin-left:5px" @click="iconName=''">清空</el-button>
  </div>
</template>

<script>
import eIcon from './eIcon';
import svgIcons from '@/views/icons/svg-icons'
import elementIcons from '@/views/icons/element-icons'

export default {
  name: 'IconPicker',
  components: {
    eIcon
  },
  props: {
    value: String
  },
  data() {
    return {
      svgIcons,
      elementIcons,
      iconName: this.value,
      iconList: []
    };
  },
  watch: {
    iconName() {
      this.$emit('input', this.iconName)
    },
    value() {
      this.iconName = this.value
    }
  },
  mounted() {
    for (let i = 0; i < svgIcons.length; i++) {
      this.iconList.push(svgIcons[i])
    }
    for (let i = 0; i < elementIcons.length; i++) {
      this.iconList.push('el-icon-' + elementIcons[i])
    }
  }
};
</script>
