---
title: 源码编译
sidebar_label: 源码编译
---

# 源码编译

- 下载代码
```shell
$ git clone https://github.com/DTStack/Taier
```

- build: 可以根据不同场景来选择编译脚本进行编译

```shell
|-- build
|---- mvn-build.sh  //编译Taier整个项目
|---- mvn-build-datadevelop.sh  //编译Taier Web 工程
|---- mvn-build-plugins.sh  //编译Taier插件包
```


- 根目录编译
```shell
$ build/mvn-build.sh
```

- 源码检查  
  通过脚本编译项目后会生成对应的

```shell
|-- lib/taier-data-develop-with-dependencies.jar   
|-- worker-plugins  
|-- datasource-plugins
```

:::tip 
插件编译失败，可以尝试maven的仓库地址设为[阿里云Maven地址](https://developer.aliyun.com/mvn/guide)

oracle 插件因为商业版本原因，官方没有提供 ojdbc 的驱动，开发者有两种可选方式进行解决：

1. 将代码中 oracle 插件 pom 文件中 ojdbc8 的依赖，对 scope 进行调整
2. 在编译后的 datasource-plugins/oracle 目录下，增加 ojdbc8 的驱动
   :::


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