#!/bin/bash

# 该脚本主要用来做hotfix后，
# 对 hotfix 代码向其他长线分支
#（dev, dev_main, dev_ide, dev_valid, dev_api, dev_tag) 
# 进行代码同步操作
#

$branch=$b || 'hotfix';

echo '$branch is merging codes to other main branch.';

git add .
git commit -m 'hotfix'

# Git merge to dev
git checkout dev
git merge $branch

echo "Merge to dev is succeed."

# # Git merge to dev_main
# git checkout dev_main
# git merge $branch

# # Git merge to dev_ide
# git checkout dev_ide
# git merge $branch

# # Git merge to dev_tag
# git checkout dev_tag
# git merge $branch

# # Git merge to dev_valid
# git checkout dev_valid
# git merge $branch

# # Git merge to dev_api
# git checkout dev_api
# git merge $branch

# # Git merge to master
# git checkout master
# git pull origin master
# git merge $branch

# # 最后推送到代码到 master
# git push origin master
