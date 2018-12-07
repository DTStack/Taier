#!/bin/bash

# Default as minor, the argument major, minor or patch: 
release=$1||'minor';
echo "Release as $release"

# Release branch
master="master"
prefix="DTinsight_v"

git pull origin $master
echo "Current pull origin $master."

# Auto generate version number and tag
standard-version -r $release --tag-prefix $prefix --infile CHANGELOG.md

git push --follow-tags origin $master
git push origin $master

echo "Git push origin $master"
echo "Release finished."


