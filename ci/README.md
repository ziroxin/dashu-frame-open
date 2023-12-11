## 持续集成模块 ===  用于快速部署演示程序
http://yanshi.java119.cn/


------------------------------------- 配置说明 -------------------------------------

### 使用说明
- 本模块用于快速部署项目，适用于linux环境，本机能够通过ssh正常连接的状态下使用。


### 项目和服务器配置脚本（重要！重要！重要！）说明
- config.js
- 首次运行，请详细阅读config.js中的相关配置说明。

该文件用于配置本地文件所在位置和linux服务器连接信息，包含：
本地项目路径：localProjectDir
远程服务器IP：remoteHost
远程服务器端口：remotePort
远程服务器用户名：remoteUser
远程服务器密码：remotePassword
远程服务器目录：remoteHomeDirectory
数据库用户名：mysqlUser
数据库密码：mysqlPassword
数据库名：mysqlDatabaseName


### 初始安装脚本说明
- 前2个脚本，用于初始化部署
即（1.ci-docker-install.js和2.ci-docker-start.js），
建议新服务器初次部署使用，每个脚本只需运行一次（多次运行不知道有什么问题，作者没试过，慎重！！！）

脚本：1.ci-docker-install.js，用于安装docker环境，拷贝相关文件
    详细配置，见脚本内部说明
脚本：2.ci-docker-start.js，用于初始化docker镜像，并启动docker
    需脚本1.ci-docker-install.js先执行完成，才能执行本脚本


### 更新脚本说明
- ci-java.js脚本
用于更新springboot后端项目，并重启docker
该脚本只拷贝app.jar和lib目录，不拷贝config目录的配置文件
如需修改配置文件，需连接服务器自行修改

- ci-vue.js脚本
用于更新web-vue2前端项目，并重启docker
该脚本拷贝dist目录下全部文件
建议拷贝前，把/form-generator/** 和 /swagger/** 建议删除









------------------------------------- 运行脚本说明 -------------------------------------

### 1. 使用本模块，第一步：初始化npm
npm init

### 2. 第二步：安装 node-ssh 库
npm install node-ssh

### 3. 修改配置文件
- 修改 config.js
相关配置说明在config.js内顶部，有详细信息说明，使用前必须仔细阅读

### 4. 初次部署
第一步：安装docker环境（需要拷贝和下载资源，运行可能较慢）
node 1.ci-docker-install.js

第二步：初始化docker镜像（需要拷贝和下载资源，运行可能较慢）
node 2.ci-docker-start.js

第三步：导入mysql数据库，并重启springboot
node 3.ci-mysql-restart.js


### 5. 后续更新
- 前端更新：
- 先打包前端项目，再更新前端页面：  
cd web-vue2
npm run build:prod
cd ../ci
node ci-vue.js  

- 后端更新：
- 先打包后台项目（maven），再更新springboot后端：
mvn clean package -DskipTests -pl module
node ci-java.js