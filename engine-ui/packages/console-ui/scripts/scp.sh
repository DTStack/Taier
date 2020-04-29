#!/bin/bash

source="./dist/console"
target="/opt/dtstack/DTConsole/ConsoleFront"
server="172.16.100.168"

echo "Uploading $source to $server:$target"

### 上传文件到指定服务器
scp -P 22 -r $source root@$server:$target

echo "Upload over."
