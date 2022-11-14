---
title: 单机部署
sidebar_label: 单机部署
---

## 环境准备
- [x] JDK1.8+
- [x] Zookeeper
- [x] MySQL初始化[Taier初始数据](https://github.com/DTStack/Taier/blob/master/sql/init.sql)

## 服务部署

- 下载 [taier源码包](https://github.com/DTStack/Taier/releases)
```shell
$ wget https://github.com/DTStack/Taier/releases/download/${current.version}/taier.tar.gz
$ wget https://github.com/DTStack/Taier/releases/download/${current.version}/plugins.tar.gz
```
- 解压taier源码包
```shell
$ unzip taier.tar.gz
$ unzip plugins.tar.gz
```

- 修改配置信息
```shell
|-- conf 
|---- application.properties  //配置文件
```

在application.properties配置好正确的Zookeeper、MySQL信息
```properties
nodeZkAddress=127.0.0.1:2181/taier
jdbc.driverClassName=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://127.0.0.1:3306/taier?charset=utf8&autoReconnect=true&tinyInt1isBit=false&serverTimezone=Asia/Shanghai
jdbc.username=root
jdbc.password=

server.tomcat.uri-encoding = UTF-8
server.port = 8090
server.tomcat.basedir = ./tmpSave
```

* 启动:
```shell
$ ./bin/taier.sh start
```
* 停止:
```shell
$ ./bin/taier.sh stop
```

:::tip
低版本升级到高版本 执行[高版本目录](https://github.com/DTStack/Taier/tree/master/sql)下的`increment.sql`
:::
