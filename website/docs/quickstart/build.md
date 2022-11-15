---
title: 源码编译
sidebar_label: 源码编译
---

# 源码编译

- 下载代码
> git clone https://github.com/DTStack/Taier

- build: 项目编译脚本,根据不同场景来编译项目

```shell
|-- build
|---- mvn-build.sh  //编译脚本
```


- 根目录编译

```shell
$ build/mvn-build.sh
```

- 源码检查  

  通过脚本编译项目后会生成对应的  
  - lib/taier-data-develop-with-dependencies.jar   
  - worker-plugins  
  - datasource-plugins 
  


- 前端编译  
  
   安装npm 并设置npm国内镜像地址  
   在taier-ui目录 执行
   ```shell
   $ npm install  
   $ npm run build  
   ```
   
   通过脚本编译taier-ui后会生成对应的前端文件 dist
   - taier-ui/dist

:::tip
编译前端代码，建议node版本为`v16.14.0`  
源码中包含部分scala的代码，通过idea源码启动需要添加scala的sdk 建议scala版本为`2.11.1`
:::