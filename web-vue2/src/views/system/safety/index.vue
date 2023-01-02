<template>
  <div class="app-container">
    <el-form ref="dataForm" :model="temp" label-position="right" label-width="120px"
             class="width600">

      <div class="tip">
        <i class="el-icon-set-up"></i> 密码复杂度设置
      </div>

      <el-form-item label="密码长度" style="width: 440px;">
        <el-col :span="11">
          <el-input v-model="temp.startLength" prop="startLength"
                    :rules="[{required: true, message: '开始长度不能为空'}]"/>
        </el-col>
        <el-col class="line" :span="2" style="text-align: center;">-</el-col>
        <el-col :span="11">
          <el-input v-model="temp.endLength" prop="endLength"
                    :rules="[{required: true, message: '结束长度不能为空'}]"/>
        </el-col>
      </el-form-item>
      <el-form-item label="密码强度" label-position="right">
        <el-radio-group v-model="temp.lowercase" prop="lowercase" style="margin-bottom: 20px;">
          <el-radio-button label="2">可有可无</el-radio-button>
          <el-radio-button label="1">必须有小写字母</el-radio-button>
          <el-radio-button label="0">无小写字母</el-radio-button>
        </el-radio-group>
        <el-radio-group v-model="temp.uppercase" prop="uppercase" style="margin-bottom: 20px;">
          <el-radio-button label="2">可有可无</el-radio-button>
          <el-radio-button label="1">必须有大写字母</el-radio-button>
          <el-radio-button label="0">无大写字母</el-radio-button>
        </el-radio-group>
        <el-radio-group v-model="temp.numbers" prop="numbers" style="margin-bottom: 20px;">
          <el-radio-button label="2">可有可无</el-radio-button>
          <el-radio-button label="1">必须有数字</el-radio-button>
          <el-radio-button label="0">无数字</el-radio-button>
        </el-radio-group>
        <el-radio-group v-model="temp.specialCharacters" prop="specialCharacters" style="margin-bottom: 20px;">
          <el-radio-button label="2">可有可无</el-radio-button>
          <el-radio-button label="1">必须有特殊字符</el-radio-button>
          <el-radio-button label="0">无特殊字符</el-radio-button>
        </el-radio-group>
        <el-switch style="margin-bottom: 20px;" v-model="temp.banUsername" prop="banUsername"
                   active-text="不能包含用户名" :active-value="1" active-color="#ff4949"
                   inactive-text="可以包含用户名" :inactive-value="0">
        </el-switch>
      </el-form-item>

      <el-form-item label="规则不符提示" prop="prompt">
        <el-input v-model="temp.prompt" :rules="[{required: true, message: '提示语不能为空'}]" type="textarea" maxlength="255"
                  placeholder="请输入提示语"/>
      </el-form-item>
      <el-form-item label="密码有效期" prop="validTime">
        <el-switch v-model="validTimeEnable" inactive-text="永不过期" active-text="过期时间"></el-switch>
        <el-input v-model="temp.validTime" v-show="validTimeEnable"
                  :rules="[{required: true, message: '有效期不能为空，为空请填0'}]"
                  style="width: 150px;margin:0px 20px;" placeholder="请输入有效时间（为空请填0）"/>
        <span v-show="validTimeEnable">天</span>
      </el-form-item>

      <div class="tip">
        <i class="el-icon-warning-outline"></i> 登录安全设置
      </div>

      <el-form-item label="限制次数">
        <el-switch v-model="loginFailedTimesEnable" inactive-text="登录错误不锁定" active-text="登录错误几次锁定"></el-switch>
        <el-input v-model="temp.loginFailedTimes" v-show="loginFailedTimesEnable" prop="loginFailedTimes"
                  style="width: 150px;margin:0px 20px;" placeholder="请输入登录失败限制次数"/>
      </el-form-item>
      <el-form-item label="锁定时间" v-show="loginFailedTimesEnable">
        <el-switch v-model="lockTimeEnable" inactive-text="永久锁定" active-text="限时锁定"></el-switch>
        <el-input v-model="temp.lockTime" v-show="lockTimeEnable" prop="lockTime"
                  style="width: 150px;margin:0px 20px;" placeholder="请输入锁定时间（单位分钟）"/>
        <span v-show="lockTimeEnable">分钟</span>
      </el-form-item>
      <el-form-item label="默认密码" prop="defaultPassword">
        <el-input v-model="temp.defaultPassword" :rules="[{required: true, message: '默认密码不能为空'}]"
                  style="width: 200px;margin-right:20px;" placeholder="请输入默认密码"/>
        <span>（重置密码时的默认密码）</span>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer width600">
      <el-button v-waves type="primary" @click="saveData" style="margin-left: 120px;">保存配置</el-button>
    </div>
  </div>
</template>

<script>
import waves from '@/directive/waves'
import request from '@/utils/request'

export default {
  directives: {waves},
  data() {
    return {
      // 表单临时数据
      temp: {},
      // 是否限制密码有效期
      validTimeEnable: false,
      // 是否登录错误锁定
      loginFailedTimesEnable: false,
      // 是否永久锁定
      lockTimeEnable: false,
    }
  },
  created() {
    this.loadData()
  },
  watch: {
    validTimeEnable(val) {
      if (val == false) this.temp.validTime = 0
    },
    loginFailedTimesEnable(val) {
      if (val == false) this.temp.loginFailedTimes = 0
    },
    lockTimeEnable(val) {
      if (val == false) this.temp.lockTime = 0
    },
  },
  methods: {
    // 加载数据
    loadData() {
      request({
        url: '/zsafety/zSafety/getSafety', method: 'get'
      }).then((response) => {
        const {data} = response
        this.temp = Object.assign({}, data)
        this.validTimeEnable = this.temp.validTime > 0 ? true : false;
        this.loginFailedTimesEnable = this.temp.loginFailedTimes > 0 ? true : false;
        this.lockTimeEnable = this.temp.lockTime > 0 ? true : false;
      })
    },
    // 修改保存
    saveData() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          var data = this.temp;
          console.dir(this.temp.banUsername)
          if (this.validTimeEnable == true && data.validTime <= 0) {
            this.$message({type: 'error', message: '请输入密码有效期限制天数，必须大于0！'})
            return
          }
          if (this.loginFailedTimesEnable == true && data.loginFailedTimes <= 0) {
            this.$message({type: 'error', message: '请输入登录错误几次后锁定，必须大于0！'})
            return
          }
          if (this.lockTimeEnable == true && data.lockTime <= 0) {
            this.$message({type: 'error', message: '请输入登录错误后锁定时长，必须大于0！'})
            return
          }
          request({
            url: '/zsafety/zSafety/update', method: 'post', data
          }).then(response => {
            this.$message({type: 'success', message: '保存成功！'})
            this.loadData()
          })
        }
      })
    },
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
}

.width600 {
  margin: 0px auto;
  width: 600px;
}
</style>
