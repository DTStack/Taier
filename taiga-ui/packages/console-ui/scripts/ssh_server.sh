#!/bin/bash

$order

# git branch
branch="dev"

# Test server config
deploy_host="172.16.8.104"
deploy_server="root@$deploy_host"
deploy_path="/home/admin/app/rdos.front"
home_page="http://rdos.dtstack.net/"

# SSH login test server
# Pull lastest dist version from gitlab.
ssh -t $deploy_server "
cd $deploy_path;
git pull origin $branch;
echo 'git pull origin $branch done.';bash; 
"
# Open index page.
open "$home_page"