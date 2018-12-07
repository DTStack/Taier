#!/bin/bash

# git branch
dev="dev"
master="master"
currentTime="$(date +"%Y_%m_%d_%H_%M_%S")"
# Build dist, and push it to gitlab.
git pull origin $dev
echo "Git pull origin $dev."

git add -A
git commit -m "update_$currentTime"

git checkout $master
echo "Current branch is $master."
git merge $dev
echo "Current branch master merged $dev."

git push origin $master
echo "Git push origin $master."

git checkout $dev
echo "Current branch is $dev."


