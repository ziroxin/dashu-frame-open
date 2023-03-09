<template>
  <div class="importSwagger" style="margin: 10px;">
    <div id="swagger-ui" />
  </div>
</template>

<script>
import {SwaggerUIBundle, SwaggerUIStandalonePreset} from 'swagger-ui-dist'

export default {
  name: 'MySwaggerUi',
  data() {
    return {}
  },
  created() {
  },
  mounted() {
    this.$nextTick(() => {
      const ui = SwaggerUIBundle({
        url: this.$baseServer + '/v2/api-docs?group=news',
        dom_id: '#swagger-ui',
        deepLinking: true,
        presets: [
          SwaggerUIBundle.presets.apis,
          SwaggerUIStandalonePreset
        ],
        plugins: [
          SwaggerUIBundle.plugins.DownloadUrl
        ],
        layout: 'StandaloneLayout'
      })
    })
  }
}
</script>

<style scoped>
@import "~swagger-ui-dist/swagger-ui.css";
</style>
