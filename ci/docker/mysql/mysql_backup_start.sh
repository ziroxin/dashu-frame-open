#!/bin/bash
basepath=`pwd`


exec_shell=$0
shell_name=`echo "$exec_shell" | awk -F "/" '{print $NF}'`
if [[ $exec_shell != "./"$shell_name && $exec_shell !=  $shell_name ]]
then
   echo "请到脚本所在目录执行！！！"
   exit
fi


grep -ri "$basepath" /var/spool/cron/root
if [ $? == 0 ]; then
   echo "数据库定时备份任务已经添加到crontab服务中！"
   exit

fi


echo "00 00 * * * cd $basepath && ./mysql_backup.sh" >> /var/spool/cron/root
if [ $? == 0 ]; then
   echo "数据库定时备份任务成功添加到crontab服务中！"

fi
