#!/bin/bash

# 该脚本主要用来为 工作分支 与 dev分支 代码的进行同步
# 并自动推送到dev分支，rebase dev 分支代码到工作分支
#

function get_current_branch() {
    git branch --no-color | grep -E '^\*' | awk '{print $2}' \
    || echo "default_value"
    # or
    # git symbolic-ref --short -q HEAD || echo "default_value";
}

dev_branch='dev';
work_branch=`get_current_branch`;
echo 'Curent work branch is $work_branch.';

# Stash not commit content
git stash

git checkout $dev_branch;

git pull origin $dev_branch;

git merge $work_branch --no-ff;

git push origin $dev_branch;

git checkout $work_branch;
git rebase $dev_branch;

# Recover stash content
git stash pop;
