const {NodeSSH} = require('node-ssh');
const {
  remoteHost, remotePort, remoteUser, remotePassword, remoteHomeDirectory
} = require('./config');


// 运行本脚本前，请确保先执行完成脚本1.ci-docker-install.js
// 本脚本用于：
//    1 设置springboot目录权限
//    2 设置mysql目录权限
//    3 下载docker镜像，并启动docker


// ***************************************** 部署配置 start *****************************************
// 设置springboot目录权限
const command2 = 'chown -R 1000:1000 ' + remoteHomeDirectory + '/springboot';
// 设置mysql目录权限
const command3 = 'chown -R 999:999 ' + remoteHomeDirectory + '/mysql';
// 下载docker镜像，并启动docker
const command4 = 'docker compose -f ' + remoteHomeDirectory + '/docker-compose.yml up -d';


// 执行远程命令
async function executeRemoteCommand() {
  const ssh = new NodeSSH();
  try {
    // 连接
    await ssh.connect({host: remoteHost, username: remoteUser, password: remotePassword, port: remotePort});
    // 执行
    const result2 = await ssh.execCommand(command2);
    console.log('更改目录权限1成功', result2)
    const result3 = await ssh.execCommand(command3);
    console.log('更改目录权限2成功', result3)
    console.log('启动docker中...（需下载镜像，可能执行较慢，请耐心等待）')
    const result4 = await ssh.execCommand(command4);
    console.log('执行结果：', result4)

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