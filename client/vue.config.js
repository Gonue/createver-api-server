const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  outputDir: '../src/main/resources/static',
        devServer: {
          proxy: "https://localhost:8443"
        }
})
