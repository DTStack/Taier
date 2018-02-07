#!/bin/bash

# git branch
branch="dev"

# Build dist, and push it to gitlab.
git pull origin $branch
npm run build
git add dist
git commit -m 'build'

git add src
git commit -m 'bug fixed'

git add -A
git commit -m 'update'

git push origin $branch

# Connect to ssh server
./scripts/ssh_server.sh
