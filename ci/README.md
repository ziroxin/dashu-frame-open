## 持续集成模块 ===  用于快速部署演示程序
http://yanshi.java119.cn/

### 使用前提
- 必须先在服务器端部署好了docker环境
- 本模块只进行2个操作：  
  1） 拷贝最新打包的文件（Java端不拷贝配置文件）  
  2） 重启docker

### 1. 初始化npm
npm init

### 2. 安装 node-ssh 库
npm install node-ssh

### 3. 修改配置文件
- 修改ci-vue.js相关配置
- 修改ci-java.js相关配置

### 4.运行
node ci-vue.js 
node ci-java.js 
- 运行命令时，需要输入操作系统密码，来执行重启docker命令