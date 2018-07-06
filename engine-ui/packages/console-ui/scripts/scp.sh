#!/bin/bash

source="./dist"
target="/home/admin/app/rdos.front/dist"
server="172.16.8.104"

echo "Uploading $source to $server:$target"

### 上传文件到指定服务器
scp -P 22 -r $source root@$server:$target

echo "Upload over."
