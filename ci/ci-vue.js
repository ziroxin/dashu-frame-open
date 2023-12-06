const {NodeSSH} = require('node-ssh');
const {
  localProjectDir, remoteHost, remotePort, remoteUser,
  remotePassword, remoteHomeDirectory
} = require('./config');


// ***************************************** 部署配置 start *****************************************
// 设置本地目录路径（拷贝打包后的前端文件）
// 改成自己的本地打包目录
const localDirectory = localProjectDir + '\\web-vue2\\dist';
// 改成自己的服务器端目录，例如/home/docker/openresty/html
const remoteDirectory = remoteHomeDirectory + '/openresty/html/dashu-vue';
// 重启服务命令
const command = `docker restart openresty`;


// 使用 node-ssh 库进行目录拷贝
async function copyDirectory() {
  const ssh = new NodeSSH();
  try {
    await ssh.connect({
      host: remoteHost, username: remoteUser, password: remotePassword, port: remotePort
    });

    await ssh.putDirectory(localDirectory, remoteDirectory, {
      recursive: true, concurrency: 10
    });

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