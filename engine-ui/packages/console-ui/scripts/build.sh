#!/bin/bash

# git branch
dev="dev"
master="master"
currentTime="$(date +"%Y_%m_%d_%H_%M_%S")"

git checkout $master
echo "Current branch is $master."
git pull origin $master
echo "Current pull origin $master."

# Auto generate version number and tag
standard-version

git push --follow-tags origin master

git push origin $master
echo "Git push origin $master."

echo "Release finished."


