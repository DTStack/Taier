#!/bin/bash

# git branch
branch="dev"

# Dev server config
build_path="/home/admin/app/dtinsight-front-end/"

# Pull lastest dist version from gitlab.
su admin; # change user to admin
cd $build_path;bash;
git pull origin $branch;
git show;
echo "git pull origin $branch done.";
npm run build;