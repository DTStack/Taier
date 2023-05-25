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
```

:::caution ${current.version} 需要替换为对应版本值 如1.4
:::

- 解压taier.tar.gz源码包

```shell
$ unzip taier.tar.gz
````

- 解压后结构如下

```shell
|-- bin
|-- conf
|-- sql
|-- flinkconf
|-- sparkconf
|-- lib
|-- logs
|-- run
```

:::caution 由于plugins相关包过大 建议自行下载源码并通过build/mvn-build-plugins 脚本编译
:::

- plugins相关包路径

```shell
|-- worker-plugins
|-- datasource-plugins
```

- 完整的项目启动目录为

```shell
|-- bin
|-- conf
|-- sql
|-- flinkconf
|-- sparkconf
|-- lib
|-- logs
|-- run
|-- worker-plugins
|-- datasource-plugins
```

- 修改数据库相关配置信息

```shell
|-- conf 
|---- application.properties  //配置文件
```

- 在application.properties配置好正确的Zookeeper、MySQL信息

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

- 启动项目:
```shell
$ ./bin/taier.sh start
```

- 停止项目:
```shell
$ ./bin/taier.sh stop
```

:::tip 低版本升级到高版本 执行[高版本目录](https://github.com/DTStack/Taier/tree/master/sql)下的`increment.sql`。
初次部署 直接执行[最新版本](https://github.com/DTStack/Taier/tree/master/sql)下的`init.sql`
> 如1.3版本升级到1.4版本 执行1.4目录下的`increment.sql`
> 第一次部署执行 sql/init.sql
:::
