#!/bin/bash

# git branch
dev="dev"
master="master"

# Build dist, and push it to gitlab.
git pull origin $dev
echo "git pull origin $dev."

git add -A
git commit -m 'update'

git checkout $master
echo "git branch is $master."
git merge $dev
echo "git branch master merged  $dev."

git push origin $master
echo "git push origin $master."

git checkout $dev
echo "current branch is $dev."


