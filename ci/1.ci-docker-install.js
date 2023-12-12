const {NodeSSH} = require('node-ssh');
const {
  localProjectDir, remoteHost, remotePort, remoteUser,
  remotePassword, remoteHomeDirectory
} = require('./config');


// 运行本脚本前，请先修改config.js文件
// 本脚本用于：
//    1 拷贝docker相关文件到服务器
//    2 安装docker环境


// ***************************************** 部署配置 start *****************************************
// 拷贝docker相关文件到服务器
const localDirectory = localProjectDir + '\\ci\\docker';
// 安装docker环境
const command0 = 'mv ' + remoteHomeDirectory + '/install_docker ' + remoteHomeDirectory + '/install_docker.sh';
const command1 = 'chmod +x ' + remoteHomeDirectory + '/install_docker.sh';
const command2 = 'sudo ' + remoteHomeDirectory + '/install_docker.sh';


// 使用 node-ssh 库进行目录拷贝
async function copyDirectory() {
  const ssh = new NodeSSH();
  try {
    // 连接
    await ssh.connect({host: remoteHost, username: remoteUser, password: remotePassword, port: remotePort});
    // 拷贝
    console.log('正在拷贝目录...');
    await ssh.putDirectory(localDirectory, remoteHomeDirectory, {recursive: true, concurrency: 10});

    console.log('目录拷贝成功！');
  } catch (err) {
    console.error('目录拷贝失败:', err);
  } finally {
    ssh.dispose();
  }
}

// 执行远程命令
async function executeRemoteCommand() {
  const ssh = new NodeSSH();
  try {
    // 连接
    await ssh.connect({host: remoteHost, username: remoteUser, password: remotePassword, port: remotePort});
    // 执行
    console.log('开始执行远程命令1...（配置install_docker.sh为可执行文件）');
    const result0 = await ssh.execCommand(command0);
    const result1 = await ssh.execCommand(command1);
    console.log('执行结果1', result1)
    console.log('开始执行远程命令2...（执行install_docker.sh，安装docker环境，需下载，可能执行较慢，请耐心等待）');
    const result2 = await ssh.execCommand(command2);
    console.log('执行结果2', result2)

    console.log('远程命令执行成功！');
  } catch (err) {
    console.error('远程命令执行失败:', err);
  } finally {
    ssh.dispose();
  }
}

// 执行拷贝和重启命令
copyDirectory()
  .then(() => {
    executeRemoteCommand().then(() => {
      console.log('All completed.');
    });
  })
  .catch((err) => {
    console.error('脚本执行出错:', err);
  });