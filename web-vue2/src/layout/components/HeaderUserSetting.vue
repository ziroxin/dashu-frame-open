<template>
  <div class="avatar-container right-menu-item hover-effect">
    <el-dropdown trigger="click">
      <div class="avatar-wrapper">
        <img v-if="avatar" :src="this.$baseServer + avatar" class="user-avatar">
        <img v-else src="@/assets/images/avatar.png" class="user-avatar">
        <i class="el-icon-caret-bottom"/>
      </div>
      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item @click.native="toMyUser">
          <span style="display:block;"><i class="el-icon-user"/>个人中心</span>
        </el-dropdown-item>
        <a @click="editPassword=true">
          <el-dropdown-item><i class="el-icon-key"/>修改密码</el-dropdown-item>
        </a>
        <el-dropdown-item divided @click.native="logout">
          <span style="display:block;"><svg-icon icon-class="exit"/> 退出</span>
        </el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
    <!-- 修改密码弹窗 -->
    <user-edit-password v-model="editPassword" :show-close-btn="true"/>
  </div>
</template>

<script>
import {mapGetters} from 'vuex';
import UserEditPassword from '@/views/system/user/UserEditPassword';

export default {
  name: 'HeaderUserSetting',
  components: {UserEditPassword},
  data() {
    return {
      // 个人用户修改密码
      editPassword: false
    }
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'avatar',
      'device'
    ])
  },
  methods: {
    async logout() {
      await this.$store.dispatch('user/logout')
      this.$router.push(`/login?redirect=${this.$route.fullPath}`)
    },
    toMyUser() {
      this.$router.replace('/system/user/MyUser')
    }
  }
}
</script>

<style lang="scss" scoped>
.avatar-container {
  margin-right: 20px;

  .avatar-wrapper {
    margin-top: 5px;
    position: relative;

    .user-avatar {
      cursor: pointer;
      width: 40px;
      height: 40px;
      border-radius: 10px;
      vertical-align: top !important;
    }

    .el-icon-caret-bottom {
      cursor: pointer;
      position: absolute;
      right: -20px;
      top: 25px;
      font-size: 12px;
    }
  }
}
</style>
