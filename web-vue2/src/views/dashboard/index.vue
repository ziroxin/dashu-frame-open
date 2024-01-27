<template>
  <div class="dashboard-container">
    <admin-dashboard></admin-dashboard>
  </div>
</template>
<script>
import adminDashboard from './admin'

export default {
  name: 'Dashboard',
  components: {adminDashboard},
  data() {
    return {
      currentRole: 'adminDashboard'
    }
  },
  computed: {}
}
</script>
<style lang="scss" scoped>
.dashboard-container {
  margin-top: -4px;
}

.app-main-hor {
  .dashboard-container {
    margin-top: 40px;
  }
}
</style>
