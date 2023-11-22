<template>
  <div class="app-container">
    <iframe :src="htmlPageURL" frameborder="0" width="100%"
            :height="$windowHeight-150"></iframe>
  </div>
</template>
<script>
export default {
  data() {
    return {
      htmlPageURL: 'form-generator/index.html'
    }
  }
}
</script>