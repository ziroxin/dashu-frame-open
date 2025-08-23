<template>
  <template v-if="ruleList?.length>0">
    <template v-for="(item, idx) in ruleList" :key="'rule-'+(idx+1)">
      <div class="bg-[var(--el-fill-color-light)] px-10px pt-5px b-rd-5px">
        <div class="mb-8px flex justify-center items-center">
          <span class="color-[var(--el-color-primary)] text-12px">规则 {{ idx + 1 }}</span>
          <my-icon icon="el-icon-delete" :size="14" color="#f56c6c" class="ml-10px cursor-pointer"
                   @click="ruleList.splice(idx, 1)"/>
        </div>
        <el-form-item label="required">
          <el-radio-group v-model="item.required" size="small" @change="changeRequired(item)">
            <el-radio-button label="Required" :value="true"/>
            <el-radio-button label="正则" value="pattern"/>
            <el-radio-button label="函数" value="validator"/>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="pattern" v-if="item.required==='pattern'">
          <el-input v-model="item.pattern" placeholder="正则表达式" size="small"/>
          <div class="flex items-center">
            <span class="color-gray text-12px">示例：</span>
            <base-button type="primary" link @click="changePattern(item,'phone')" size="small">手机</base-button>
            <base-button type="primary" link @click="changePattern(item,'email')" size="small">邮箱</base-button>
          </div>
        </el-form-item>
        <el-form-item label="validator" label-position="top" v-if="item.required==='validator'">
          <div class="mt-[-35px] ml-80px flex items-center">
            <span class="color-gray text-12px lh-18px">示例：</span>
            <base-button type="primary" link @click="changeValidate(item,'rePwd')" size="small">确认密码</base-button>
          </div>
          <el-input type="textarea" v-model="item.validator" autosize placeholder="验证函数" size="small"/>
        </el-form-item>
        <el-form-item label="message" v-if="item.required!=='validator'">
          <el-input v-model="item.message" placeholder="错误提示信息" size="small"/>
        </el-form-item>
        <el-form-item label="trigger" class="pb-10px mb-10px!">
          <el-radio-group v-model="item.trigger" size="small">
            <el-radio-button label="默认" :value="''"/>
            <el-radio-button label="blur" value="blur"/>
            <el-radio-button label="change" value="change"/>
          </el-radio-group>
        </el-form-item>
      </div>
    </template>
  </template>
  <div class="flex justify-evenly items-center">
    <base-button type="primary" link icon="el-icon-plus" @click="addNewRule">添加规则</base-button>
    <el-tooltip v-if="ruleList?.length>0" :content="getViewContent()">
      <base-button type="danger" link icon="el-icon-view">预览规则</base-button>
    </el-tooltip>
  </div>
</template>

<script setup lang="ts">
const ruleList: any = defineModel()
const changeRequired = (item) => {
  item.message = ''
  if (item.required === true) {
    item.pattern = ''
    item.validator = ''
    item.message = '不能为空'
  }
}
const changePattern = (item, type) => {
  if (type === 'phone') {
    item.pattern = '/^1[3456789]\\d{9}$/'
    item.message = '请输入正确的手机号'
  } else if (type === 'email') {
    item.pattern = '/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$/'
    item.message = '请输入正确的邮箱'
  }
}
const changeValidate = (item, type) => {
  if (type === 'rePwd') {
    item.validator = `(rule,value,callback)=>{if(value!==formData.value.password) callback(new Error(\'两次密码输入不一致\'))}`
    item.message = ''
  }
}

const addNewRule = () => {
  ruleList.value.push({required: true, message: '不能为空'})
}

const getViewContent = () => {
  let result = []
  if (ruleList.value && ruleList.value.length > 0) {
    result = ruleList.value.map(item => {
      const rule = {...item}
      if (rule.required !== true) delete rule.required
      if (!rule.pattern) delete rule.pattern
      if (!rule.validator) delete rule.validator
      if (!rule.message) delete rule.message
      if (!rule.trigger) delete rule.trigger
      return rule
    })
  }
  return JSON.stringify(result)
}
</script>