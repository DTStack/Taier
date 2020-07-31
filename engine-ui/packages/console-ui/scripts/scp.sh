#!/bin/bash
source $(dirname "$0")/const.sh

echo "Uploading $source to $server:$target"

### 上传文件到指定服务器
scp -P 22 -r $source root@$server:$target

echo "Upload over."
