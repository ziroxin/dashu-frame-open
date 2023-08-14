const {exec} = require('child_process');
const path = require('path');
const {NodeSSH} = require('node-ssh');

// 设置本地目录路径（只拷贝lib/下的jar包和app.jar文件，不拷贝配置文件）
const localDirectory = 'E:\\......\\module\\target\\package-jar';// 改成自己的本地打包目录
// 设置远程服务器信息
const remoteHost = 'xxx.xxx.xxx.xxx';// 改成自己的服务器ip
const remoteUser = 'xxx';// 改成自己的用户名，例如root
const remotePassword = 'xxx';// 改成自己的密码
const remoteDirectory = '/xxx/xxx/xxx';// 改成自己的服务器端目录，例如/home/docker/springboot

// 使用 node-ssh 库进行目录拷贝
async function copyDirectory() {
    try {
        const ssh = new NodeSSH();
        await ssh.connect({
            host: remoteHost,
            username: remoteUser,
            password: remotePassword
        });

        // 拷贝lib目录
        await ssh.putDirectory(localDirectory + '\\lib', remoteDirectory + '/lib', {
            recursive: true,
            concurrency: 10
        });
        // 拷贝jar文件
        await ssh.putFile(localDirectory + '\\app.jar', remoteDirectory + '/app.jar');

        console.log('目录拷贝成功！');
        ssh.dispose();
    } catch (err) {
        console.error('目录拷贝失败:', err);
    }
}

// 执行远程命令
function executeRemoteCommand() {
    const command = `docker restart springboot`;

    exec(`ssh ${remoteUser}@${remoteHost} ${command}`, (error, stdout, stderr) => {
        if (error) {
            console.error('远程命令执行失败:', error);
        } else {
            console.log('远程命令执行成功！');
        }
    });
}

// 执行拷贝和重启命令
copyDirectory()
    .then(() => {
        executeRemoteCommand();
    })
    .catch((err) => {
        console.error('脚本执行出错:', err);
    });