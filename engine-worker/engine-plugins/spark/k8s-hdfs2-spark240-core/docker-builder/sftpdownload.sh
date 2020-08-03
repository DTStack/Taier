#!/bin/bash
##  从sftp下载文件
sftp_host=${sftp_host}
sftp_userName=${sftp_username}
sftp_passWord=${sftp_password}
sftp_port=${sftp_port}
sftp_remotePath=${sftp_remotePath}
sftp_localPath=${sftp_localPath}
current=$(date "+%Y-%m-%d %H:%M:%S")

echo "当前时间是：$current, sftp host: $sftp_host,  sftp远程路径: $sftp_remotePath, sftp本地路径: $sftp_localPath"

if [[ -z $sftp_host || -z $sftp_userName || -z $sftp_passWord || -z $sftp_remotePath ]]; then
    echo "缺少必要的参数配置，文件下载失败！"
    exit 0
fi

if [[ -z $sftp_port ]]; then
    sftp_port=22
fi

userJarDir=$sftp_localPath
if [[ ! -d userJarDir ]]; then
    mkdir -p $userJarDir
fi


sftpLoadPath=$sftp_remotePath
fileFilter=*.*

sftp_download()
{
    expect <<- EOF
    set timeout 120
    spawn sftp  -P $sftp_port $sftp_userName@$sftp_host

    expect {
        "(yes/no)?" {send "yes\r"; exp_continue }
        "*assword:" {send "$sftp_passWord\r"}
    }
    expect "sftp>"
    send "cd $sftpLoadPath \r"
    expect "sftp>"
    send "lcd $userJarDir \r"
    expect "sftp>"
    set timeout -1
    send "mget $fileFilter \r"
    expect "sftp>"
    send "bye\r"
    return 1
EOF

}


echo "执行sftp下载操作:"
sftp_download
exit $code