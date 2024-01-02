/**
 *  部署前请先阅读文档：http://docs.java119.cn/use/develop.html#%E5%AE%89%E8%A3%85docker
 *  看完文档后，根据文档，需要修改如下5个配置：
 *  （当前默认配置下，无需修改任何配置，可以直接运行，能正常跑当前打包版本 v1.1.9）
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
 *  4、结合配置3 ，拷贝最新的前端打包后的文件
 *     先打包web-vue2
 *     然后把/web-vue2/dist/** 拷贝到 /ci/docker/openresty/html/dashu-vue/**
 *     其中 /form-generator/** 和 /swagger/** 建议删除
 *     说明：可以不拷贝，使用当前默认版本，完成初始部署后，再用ci-vue.js去更新
 *  5、拷贝最新springboot后端打包后的文件
 *     先打包module模块
 *     然后把/module/target/package/** 拷贝到 /ci/docker/springboot/**
 *     最后修改application.yml配置
 *     重点修改：端口、上下文、mysql连接信息、redis连接信息
 *     说明：可以不拷贝，使用当前默认版本，完成初始部署后，再用ci-java.js去更新
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