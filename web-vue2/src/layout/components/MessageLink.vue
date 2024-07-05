<template>
  <div class="message-link right-menu-item hover-effect">
    <el-badge :value="unreadCount" class="item" :hidden="!unreadCount||unreadCount<=0">
      <router-link to="/system/message">
        <i class="el-icon-message"/>
      </router-link>
    </el-badge>
  </div>
</template>
<script>
import {mapState} from 'vuex'

export default {
  name: "MessageLink",
  computed: {
    ...mapState({
      unreadCount: state => state.message.unreadCount,
    })
  },
  created() {
    // 刷新消息数量
    this.$store.dispatch('message/refreshMessageCount');
  }
}
</script>
<style scoped lang="scss">
.message-link {
  .item {
    margin-top: 12px;
  }

  i {
    margin-top: -24px;
    font-size: 28px;
    vertical-align: middle;
  }
}
</style>