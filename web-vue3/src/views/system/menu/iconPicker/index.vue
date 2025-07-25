<!--图标选择器-->
<template>
  <div>
    <el-popover width="600" trigger="click" :hide-after="0" :visible="isShow">
      <div v-click-outside="closePopover">
        <div class="w-100% mb-10px">
          <el-input v-model="searchText" placeholder="搜索图标" class="w-300px! mr-30px" clearable/>
        </div>
        <div class="grid grid-cols-10 gap-15px max-h-400px overflow-x-hidden overflow-y-scroll ">
          <div v-for="(item,index) in showSvgIcons" :key="index">
            <base-button plain :icon="item" :icon-size="20" class="p-8px!" @click="iconName=item"/>
          </div>
        </div>
      </div>
      <template #reference>
        <base-button @click="isShow=true">
          <span v-if="iconName===''">请选择图标</span>
          <span v-else><my-icon :icon="iconName"/></span>
        </base-button>
      </template>
    </el-popover>
    <base-button v-show="iconName!==''" link type="primary" class="ml-5px" @click="iconName=''">清空</base-button>
  </div>
</template>

<script>
import svgIcons from '@/views/icons/svg-icons'

export default {
  name: 'IconPicker',
  components: {},
  props: {
    modelValue: String
  },
  data() {
    return {
      svgIcons,
      iconName: this.modelValue,
      isShow: false,
      searchText: ''
    }
  },
  computed: {
    showSvgIcons() {
      return this.searchText ? this.svgIcons.filter(icon => icon.includes(this.searchText)) : this.svgIcons
    }
  },
  watch: {
    iconName() {
      this.$emit('update:modelValue', this.iconName)
      this.closePopover()
    },
    modelValue() {
      this.iconName = this.modelValue
    }
  },
  methods: {
    closePopover() {
      this.searchText = ''
      this.isShow = false
    }
  }
}
</script>
