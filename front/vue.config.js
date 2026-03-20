const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave: false, // 禁用ESLint
  chainWebpack: config => {
    config.plugins.delete('fork-ts-checker');
  },
  devServer: {
    host:'localhost',
    port:8081,
    open:false
  }
})
