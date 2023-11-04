## 持续集成模块 ===  用于快速部署演示程序
http://yanshi.java119.cn/

### 使用前提
- 必须先在服务器端部署好了docker环境  
  也就是说，本功能只能更新部署，不能完成首次部署
- 本模块只进行2个操作：  
  1） 拷贝最新打包的文件（Java端不拷贝配置文件）  
  2） 重启docker

### 1. 初始化npm
npm init

### 2. 安装 node-ssh 库
npm install node-ssh

### 3. 修改配置文件
- 修改 ci-vue.js 相关配置
- 修改 ci-java.js 相关配置

### 4.运行
- 先打包好前端项目，运行命令，部署前端：  
cd web-vue2
npm run build:prod
cd ../ci
node ci-vue.js  
- 打包后台，运行命令，部署后台：
node ci-java.js