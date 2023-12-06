#! /bin/bash

function echo_red() {
  echo -e "\033[1;31m$1\033[0m"
}

function echo_green() {
  echo -e "\033[1;32m$1\033[0m"
}

function echo_yellow() {
  echo -e "\033[1;33m$1\033[0m"
}




#安装docker
function install_docker() {
  #检测docker是否已安装
  if [[ ! -f "/usr/bin/dockerd" ]]; then
    docker_exist=0
  else
    docker_exist=1
  fi

  if [[ "${docker_exist}" == "1" ]]; then
     echo_red "[WARN]docker已安装!"
     return
  fi


  echo_yellow "安装升级必要软件..."
  yum install -y yum-utils
  if [[ $? != 0 ]]; then
     echo_red "[ERROR]安装升级必要软件失败！！！"
     return
  fi

  echo_yellow "配置docker-ce的yum源..."
  yum-config-manager --add-repo http://mirrors.aliyun.com/docker-ce/linux/centos/docker-ce.repo

#  yum makecache fast #为了适配centos8  已经没有fast这个参数
  yum makecache
  if [[ $? != 0 ]]; then
     echo_red "[ERROR]配置docker-ce的yum源失败！！！"
     return
  fi

  echo_yellow "开始yum安装docker及相关包..."
  yum -y install docker-ce docker-ce-cli containerd.io docker-compose-plugin
  if [[ $? != 0 ]]; then
     echo_red "[ERROR]yum安装docker失败！！！"
     return
  fi

  echo_yellow "配置docker参数并启动dockeri..."
  mkdir -p /etc/docker

  echo_yellow "请输入docker使用的的网段,默认192.168.0.0/16，输入格式:xxx.xxx.xxx.xxx/xx"
  read -ep "请输入:" address_pools
  if [[ "" == "$address_pools" ]]; then
     address_pools="192.168.0.0/16"
  fi  

  cat > /etc/docker/daemon.json << EOF
  {
    "registry-mirrors": ["https://hqh2e0kr.mirror.aliyuncs.com"],
    "debug" : false,
    "default-address-pools" : [
      {
        "base" : "$address_pools",
        "size" : 24
      }
    ],
    "log-driver": "json-file",
    "log-opts": {
     "max-file": "3",
     "max-size": "500m"
    }
  
  }
 
EOF
  echo 'Asia/Shanghai' > /etc/timezone
  systemctl daemon-reload
  systemctl enable docker
  systemctl start docker

  if [[ $? != 0 ]]; then
     echo_red "[ERROR]启动docker失败！！！"
     return
  fi
 
}




function install_docker-compose() {
  echo_yellow "安装docker-compose..."
#sudo curl -L "https://github.com/docker/compose/releases/download/1.24.1/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
  if [[ -f "/usr/local/bin/docker-compose" ]]; then
     echo_red "[WARN]docker-compose已安装!"
     return
  fi

  if [[ ! -f "./docker-compose" ]]; then
     echo_red "[ERROR]docker-compose文件不存在!"
     return
  fi
  cp ./docker-compose /usr/local/bin/
  chmod +x /usr/local/bin/docker-compose
  ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose
  docker-compose --version

  echo_yellow "安装docker-compose完成"

}

#redis 操作系统参数优化
function redis_optimization() {
echo "vm.overcommit_memory = 1" >> /etc/sysctl.conf
sysctl -p
echo_yellow "执行当前操作可能比较耗时，大约2分钟，请耐心等待...."
echo never > /sys/kernel/mm/transparent_hugepage/enabled
echo "echo never > /sys/kernel/mm/transparent_hugepage/enabled" >> /etc/rc.d/rc.local
chmod +x /etc/rc.d/rc.local
}

function main() {
  echo_yellow "1. 安装Docker"
  install_docker
#  echo_yellow "\n2. 配置Docker-compose"
#  install_docker-compose
  echo_yellow "\n2. 针对redis优化修改操作系统内核参数"
  redis_optimization
}


if [[ "$0" == "${BASH_SOURCE[0]}" ]]; then
  main
fi

