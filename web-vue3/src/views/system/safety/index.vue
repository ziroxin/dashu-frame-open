<template>
  <div class="app-container">
    <el-form ref="dataForm" :model="temp" label-position="right" label-width="120px" class="w-600px m-auto">
      <div class="tip">
        <my-icon icon="el-icon-lock" :size="20" class="mr-10px"/>
        密码复杂度设置
      </div>
      <div class="flex justify-start items-center">
        <el-form-item label="密码长度" prop="startLength"
                      :rules="[{required: true, message: '开始长度不能为空'}]">
          <el-input v-model="temp.startLength" class="w-150px!"/>
        </el-form-item>
        <div class="w-20px h-50px text-center">-</div>
        <el-form-item label="" label-width="0" prop="endLength"
                      :rules="[{required: true, message: '结束长度不能为空'}]">
          <el-input v-model="temp.endLength" class="w-150px!"/>
        </el-form-item>
      </div>
      <el-form-item label="密码强度" label-position="right">
        <el-radio-group v-model="temp.lowercase" class="mb-20px">
          <el-radio-button :value="2">可有可无</el-radio-button>
          <el-radio-button :value="1">必须有小写字母</el-radio-button>
          <el-radio-button :value="0">无小写字母</el-radio-button>
        </el-radio-group>
        <el-radio-group v-model="temp.uppercase" class="mb-20px">
          <el-radio-button :value="2">可有可无</el-radio-button>
          <el-radio-button :value="1">必须有大写字母</el-radio-button>
          <el-radio-button :value="0">无大写字母</el-radio-button>
        </el-radio-group>
        <el-radio-group v-model="temp.numbers" class="mb-20px">
          <el-radio-button :value="2">可有可无</el-radio-button>
          <el-radio-button :value="1">必须有数字</el-radio-button>
          <el-radio-button :value="0">无数字</el-radio-button>
        </el-radio-group>
        <el-radio-group v-model="temp.specialCharacters" class="mb-20px">
          <el-radio-button :value="2">可有可无</el-radio-button>
          <el-radio-button :value="1">必须有特殊字符</el-radio-button>
          <el-radio-button :value="0">无特殊字符</el-radio-button>
        </el-radio-group>
        <el-switch v-model="temp.banUsername" class="mb-20px"
                   active-text="不能包含用户名" :active-value="1" active-color="#ff4949"
                   inactive-text="可以包含用户名" :inactive-value="0"/>
      </el-form-item>

      <el-form-item label="规则不符提示" prop="prompt" :rules="[{required: true, message: '提示语不能为空'}]">
        <el-input v-model="temp.prompt" type="textarea" class="w-440px!" :rows="3"
                  maxlength="255" show-word-limit placeholder="请输入提示语"/>
      </el-form-item>
      <el-form-item label="密码有效期" prop="validTime"
                    :rules="[{required: true, message: '有效期不能为空，为空请填0'}]">
        <el-switch v-model="validTimeEnable" inactive-text="永不过期" active-text="过期时间"/>
        <el-input v-show="validTimeEnable" v-model="temp.validTime" class="w-150px! m-[0_20px]"
                  placeholder="请输入有效时间（为空请填0）"/>
        <span v-show="validTimeEnable">天</span>
      </el-form-item>

      <div class="tip">
        <my-icon icon="yzm" :size="24" class="mr-5px"/>
        登录安全设置
      </div>

      <el-form-item label="限制次数" prop="loginFailedTimes">
        <el-switch v-model="loginFailedTimesEnable" inactive-text="登录错误不锁定" active-text="登录错误几次锁定"/>
        <el-input v-show="loginFailedTimesEnable" v-model="temp.loginFailedTimes"
                  class="w-150px! m-[0_20px]" placeholder="请输入登录失败限制次数"/>
      </el-form-item>
      <el-form-item v-show="loginFailedTimesEnable" label="锁定时间" prop="lockTime">
        <el-switch v-model="lockTimeEnable" inactive-text="永久锁定" active-text="限时锁定"/>
        <el-input v-show="lockTimeEnable" v-model="temp.lockTime"
                  class="w-150px! m-[0_20px]" placeholder="请输入锁定时间（单位分钟）"/>
        <span v-show="lockTimeEnable">分钟</span>
      </el-form-item>
      <el-form-item label="默认密码" prop="defaultPassword" :rules="[{required: true, message: '默认密码不能为空'}]">
        <el-input v-model="temp.defaultPassword" class="w-200px! mr-20px" placeholder="请输入默认密码"/>
        <span>（重置密码时的默认密码）</span>
      </el-form-item>
      <el-divider/>
      <div class="text-center">
        <base-button type="primary" icon="el-icon-check" size="large" class="w-160px ml-[-10px]"
                     @click="saveData">保存配置
        </base-button>
      </div>
    </el-form>
  </div>
</template>

<script>
import request from '@/utils/request'
import { MyIcon } from '@/components/MyIcon'

export default {
  components: {MyIcon},
  data() {
    return {
      // 表单临时数据
      temp: {},
      // 是否限制密码有效期
      validTimeEnable: false,
      // 是否登录错误锁定
      loginFailedTimesEnable: false,
      // 是否永久锁定
      lockTimeEnable: false
    }
  },
  watch: {
    validTimeEnable(val) {
      if (val === false) this.temp.validTime = 0
    },
    loginFailedTimesEnable(val) {
      if (val === false) this.temp.loginFailedTimes = 0
    },
    lockTimeEnable(val) {
      if (val === false) this.temp.lockTime = 0
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    // 加载数据
    loadData() {
      request({url: '/zsafety/zSafety/getSafety', method: 'get'}).then((response) => {
        const {data} = response
        this.temp = Object.assign({}, data)
        this.validTimeEnable = this.temp.validTime > 0
        this.loginFailedTimesEnable = this.temp.loginFailedTimes > 0
        this.lockTimeEnable = this.temp.lockTime > 0
      })
    },
    // 修改保存
    saveData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          const data = this.temp
          if (this.validTimeEnable === true && data.validTime <= 0) {
            this.$message({type: 'error', message: '请输入密码有效期限制天数，必须大于0！'})
            return
          }
          if (this.loginFailedTimesEnable === true && data.loginFailedTimes <= 0) {
            this.$message({type: 'error', message: '请输入登录错误几次后锁定，必须大于0！'})
            return
          }
          if (this.lockTimeEnable === true && data.lockTime <= 0) {
            this.$message({type: 'error', message: '请输入登录错误后锁定时长，必须大于0！'})
            return
          }
          request({url: '/zsafety/zSafety/update', method: 'post', data}).then(response => {
            this.$message({type: 'success', message: '保存成功！'})
            this.loadData()
          })
        }
      })
    }
  }
}
</script>
<style scoped>
.tip {
  padding: 15px 16px;
  background-color: #ecf8ff;
  border-radius: 4px;
  border-left: 5px solid #50bfff;
  margin: 20px 0;
  display: flex;
  justify-content: start;
  align-items: center;
}
</style>
