<!--图标选择器-->
<template>
  <div class="IconPickder">
    <el-popover width="600" trigger="click">
      <div class="max-h-400px overflow-x-hidden overflow-y-scroll">
        <base-button v-for="(item,index) in iconList" :key="index" plain class="m-2px" @click="iconName=item">
          <my-icon :icon="item" class="text-20px"/>
        </base-button>
      </div>
      <template #reference>
        <base-button>
          <span v-if="iconName===''">请选择图标</span>
          <span v-else><el-icon :icon="iconName"/></span>
        </base-button>
      </template>
    </el-popover>
    <base-button v-show="iconName!==''" link type="primary" class="ml-5px" @click="iconName=''">清空</base-button>
  </div>
</template>

<script>
import eIcon from './eIcon'
import svgIcons from '@/views/icons/svg-icons'
import elementIcons from '@/views/icons/element-icons'
import { MyIcon } from '@/components/MyIcon'

export default {
  name: 'IconPicker',
  components: {
    MyIcon,
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
    }
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
}
</script>
