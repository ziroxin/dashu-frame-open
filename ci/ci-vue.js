const {exec} = require('child_process');
const path = require('path');
const {NodeSSH} = require('node-ssh');

// 设置本地目录路径（拷贝打包后的前端文件）
const localDirectory = 'E:\\......\\web-vue2\\dist';// 改成自己的本地打包目录
// 设置远程服务器信息
const remoteHost = 'xxx.xxx.xxx.xxx';// 改成自己的服务器ip
const remoteUser = 'xxx';// 改成自己的用户名，例如root
const remotePassword = 'xxx';// 改成自己的密码
const remoteDirectory = '/xxx/xxx/xxx';// 改成自己的服务器端目录，例如/home/docker/openresty/html

// 使用 node-ssh 库进行目录拷贝
async function copyDirectory() {
  const ssh = new NodeSSH();
  try {
    await ssh.connect({
      host: remoteHost, username: remoteUser, password: remotePassword
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
      host: remoteHost,
      username: remoteUser,
      password: remotePassword
    });

    const command = `docker restart openresty`;
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