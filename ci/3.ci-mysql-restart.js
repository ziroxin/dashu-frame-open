const {NodeSSH} = require('node-ssh');
const {
  remoteHost, remotePort, remoteUser, remotePassword, remoteHomeDirectory,
  mysqlUser, mysqlPassword, mysqlDatabaseName
} = require('./config');


// 运行本脚本前，请确保先执行完成脚本1.ci-docker-install.js和2.ci-docker-start.js
// 本脚本用于：
//    1 创建数据库；
//    2 初始化数据库表；
//    3 重启springboot后端项目


// ***************************************** 部署配置 start *****************************************
// 创建数据库
const command1 = 'docker exec mariadb mysql -u ' + mysqlUser + ' -p' + mysqlPassword
  + ' -e "CREATE DATABASE ' + mysqlDatabaseName + ';"';
// 还原数据库脚本（/ci/docker/mysql/init_dashu_open.sql）
const command2 = 'docker exec -i mariadb mysql -u ' + mysqlUser + ' -p' + mysqlPassword
  + ' ' + mysqlDatabaseName + ' < ' + remoteHomeDirectory + '/mysql/init_dashu_open.sql';
// 重启springboot后端项目
const command3 = 'docker restart springboot';


// 执行远程命令
async function executeRemoteCommand() {
  const ssh = new NodeSSH();
  try {
    // 连接
    await ssh.connect({host: remoteHost, username: remoteUser, password: remotePassword, port: remotePort});
    // 执行
    console.log('正在创建数据库...')
    const result1 = await ssh.execCommand(command1);
    console.log('正在还原数据库脚本...')
    const result2 = await ssh.execCommand(command2);
    console.log('正在重启项目...')
    const result3 = await ssh.execCommand(command3);

    console.log('远程命令执行成功！');
  } catch (err) {
    console.error('远程命令执行失败:', err);
  } finally {
    ssh.dispose();
  }
}

// 执行拷贝和重启命令
executeRemoteCommand()
  .then(() => {
    console.log('All completed.');
  })
  .catch((err) => {
    console.error('脚本执行出错:', err);
  });