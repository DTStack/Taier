#!/bin/bash

echo "Start to initialize the output directory";

rm -rf ./out/;
mkdir ./out/;

echo "Start to build the console-ui";
lerna run microApp --scope console-ui

echo "Copy the console-ui dist to the out";
cp -R ./packages/console-ui/dist/* ./out/

echo "Start to build the ide-ui";
lerna run build --scope ide-ui

echo "Copy the ide-ui dist to the out";
cp -R ./packages/ide-ui/build/* ./out/
