const {NodeSSH} = require('node-ssh');
const {
  localProjectDir, remoteHost, remotePort, remoteUser,
  remotePassword, remoteHomeDirectory
} = require('./config');

// ***************************************** 部署配置 start *****************************************
// 设置本地目录路径（只拷贝lib/下的jar包和app.jar文件，不拷贝配置文件）
// 改成自己的本地打包目录
const localDirectory = localProjectDir + '\\module\\target\\package-jar';
// 改成自己的服务器端目录，例如/home/docker/springboot
const remoteDirectory = remoteHomeDirectory + '/springboot';
// 重启服务命令
const command = `docker restart springboot`;


// 使用 node-ssh 库进行目录拷贝
async function copyDirectory() {
  const ssh = new NodeSSH();
  try {
    await ssh.connect({
      host: remoteHost, username: remoteUser, password: remotePassword, port: remotePort
    });

    // 拷贝lib目录
    await ssh.putDirectory(localDirectory + '\\lib', remoteDirectory + '/lib', {
      recursive: true, concurrency: 10
    });

    // 拷贝jar文件
    await ssh.putFile(localDirectory + '\\app.jar', remoteDirectory + '/app.jar');

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
    await ssh.connect({
      host: remoteHost, username: remoteUser, password: remotePassword, port: remotePort
    });

    const result = await ssh.execCommand(command);

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