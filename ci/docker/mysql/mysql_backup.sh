#! /bin/bash

exec_shell=$0
shell_name=`echo "$exec_shell" | awk -F "/" '{print $NF}'`
if [[ $exec_shell != "./"$shell_name && $exec_shell !=  $shell_name ]]
then
   echo "请到脚本所在目录执行！！！"
   exit
fi


dir_name=`pwd |awk -F "/" '{print $NF}'`


mysql_home=`pwd`
BackPath="/home/data_backup/db/$dir_name" #数据库备份路径
BackName="mysql"-`date +%Y%m%d` #数据库保存名称，格式：mysql-2017-12-18.tar.gz

if [ ! -d $BackPath ]; then
mkdir -p $BackPath
fi

cd $mysql_home
tar zcf $BackPath/$BackName.tar.gz data
#删除7天的sql备份文件
find $BackPath -name "*.tar.gz" -mtime +7 -exec rm -rf {} \;

