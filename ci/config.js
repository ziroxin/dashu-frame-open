/**
 *  部署前请先阅读文档：http://docs.java119.cn/use/develop.html#%E5%AE%89%E8%A3%85docker
 *  看完文档后，根据文档，需要修改如下5个配置：
 *  （当前默认配置下，无需修改任何配置，可以直接运行，能正常跑当前打包版本 v1.1.9）
 *
 *
 *  =================================== 1. 初始打包 ===================================
 *
 *  1、修改 /ci/docker/docker-compose.yml 配置
 *     配置openresty端口、springboot端口、mysql端口、mysql密码、redis端口
 *     详细说明见文档：http://docs.java119.cn/use/develop.html#_3-%E4%BF%AE%E6%94%B9-docker-compose-yml
 *  2、修改 /ci/docker/redis/conf/redis.conf 配置
 *     配置redis密码
 *     详细说明见文档：http://docs.java119.cn/use/develop.html#_3-%E4%BF%AE%E6%94%B9-docker-compose-yml
 *  3、修改 /ci/docker/openresty/conf/conf.d/default.conf 配置
 *     修改web-vue2后台打包后的静态文件路径、springboot后端映射端口和上下文
 *     详细说明在文档：http://docs.java119.cn/use/develop.html#%E9%83%A8%E7%BD%B2%E5%89%8D%E7%AB%AF-%E5%8C%85%E5%90%AB%E9%85%8D%E7%BD%AEapi%E6%98%A0%E5%B0%84
 *     建议：若不会nginx配置建议先不改默认配置
 *  4、前端项目位置：/ci/docker/openresty/html/dashu-vue/**
 *  5、后端项目位置：/ci/docker/springboot/**
 *     修改application.yml配置
 *     重点修改：端口、上下文、mysql连接信息、redis连接信息、文件上传目录等
 *     详见文档：http://docs.java119.cn/use/springboot.html
 *
 *
 *  =================================== 2. 更新打包 ===================================
 *  1、ci-vue.js 更新前端
 *     先打包web-vue2
 *     通过 copyFolders 可配置 /form-generator/** 和 /swagger/** 目录不拷贝（默认不拷贝）
 *     运行 node ci-vue.js 部署
 *  2、ci-java.js 更新后端
 *     先打包springboot后端（使用idea工具的package；或者mvn命令打包）
 *     只拷贝 app.jar 文件 和 lib/** 目录
 *     运行 node ci-java.js 部署
 *
 */

// 本地项目路径
const localProjectDir = 'E:\\......\\dashu-frame-open';
// 远程服务器IP（只支持ssh方式）
const remoteHost = 'xxx.xxx.xxx.xxx';
// 远程服务器端口，如：22
const remotePort = 22;
// 远程服务器用户名，如：root
const remoteUser = 'xxx';
// 远程服务器密码
const remotePassword = 'xxx';
// 远程服务器目录，如：/home/docker
const remoteHomeDirectory = '/home/docker';
// 数据库用户名
const mysqlUser = 'xxx';
// 数据库密码
const mysqlPassword = 'xxx';
// 数据库名
const mysqlDatabaseName = 'dashu_frame_open';


module.exports = {
  localProjectDir,
  remoteHost,
  remotePort,
  remoteUser,
  remotePassword,
  remoteHomeDirectory,
  mysqlUser,
  mysqlPassword,
  mysqlDatabaseName
};