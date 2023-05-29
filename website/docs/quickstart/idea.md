---
title: 本地启动
sidebar_label: 本地启动
---

# 本地启动

## IDEA本地启动

- 下载代码

```shell
$ git clone https://github.com/DTStack/Taier
```

- 本地启动需要提前编译好Taier插件包，在根目录执行编译脚本，脚本编译完成后项目后会生成对应的插件目录

```shell
$ build/mvn-build-plugins.sh
```

- 插件目录

```shell 
|-- worker-plugins  
|-- datasource-plugins
```

- 在conf/application.properties配置好正确的Zookeeper、MySQL信息
- IDEA选择TaierApplication启动项目
  ![idea-run](/img/readme/idea-run.png)

:::tip 
源码中包含部分scala的代码，通过idea源码启动需要添加scala的sdk 建议scala版本为`2.11.1`  
Module Setting > Global Libraries > scala sdk
:::