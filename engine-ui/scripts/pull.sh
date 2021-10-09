#!/bin/bash

MYDIR=`pwd`

echo "\033[32m Update the Console UI git commit \033[0m";
cd $MYDIR/packages/console-ui
git pull origin master;

echo "\033[32m Update the molecule git commit \033[0m";
cd $MYDIR/packages/molecule
git pull origin main
# If necessary, yarn build is needed here

echo "\033[32m Update the IDE UI git commit \033[0m";
cd $MYDIR/packages/ide-ui
git pull origin master

echo "\033[32m Update the Data Source git commit \033[0m";
cd $MYDIR/packages/data-source
git pull origin master
