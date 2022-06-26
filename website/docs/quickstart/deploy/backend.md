---
title: 后端部署
sidebar_label: 后端部署
---


## 数据库操作

### 库
请登录 **MySQL** 数据库，创建名为 `taier` 数据库
### 表数据
#### 版本升级
低版本升级到高版本 执行高版本目录下的`increment.sql`

如: 1.0升级到1.1 执行`sql/1.1/1.1_increment.sql`
#### 初次部署
直接执行sql下的`init.sql`创建新库
## 项目编译

### 编译脚本

- bin: 启动脚本均放在该目录下，该目录下有两个文件sh
```shell
|-- bin 
|---- base.sh     //jvm相关参数设置脚本
|---- taier.sh    //启动脚本
```

:::tip
启动脚本里面java_home、heap_size可以通过实际环境来动态调整
:::

- build: 项目编译脚本,根据不同场景来编译项目
```shell
|-- build
|---- mvn-build.sh           //编译项目及任务提交脚本
|---- mvn-build-datadevelop  //编译项目脚本 : Taier后端data-develop启动jar
|---- mvn-build-plugins.sh   //编译插件脚本 : Taier任务提交pluginLibs插件jar
```
:::caution
Taier 页面功能依赖data-develop.jar 任务提交依赖pluginsLibs相关jar  
通过脚本编译项目后会生成对应的taier-data-develop-with-dependencies.jar和pluginLibs源码文件，在项目目录下我们可以检查编译的结果
:::


## 配置文件

### 配置文件目录

```
|-- conf 
|---- application.properties  //配置文件
|---- logback.xml             //日志配置
```


### conf/application.properties配置

完整的application.properties应该如下
```properties
nodeZkAddress=127.0.0.1:2181/taier
jdbc.driverClassName=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://127.0.0.1:3306/taier?charset=utf8&autoReconnect=true&tinyInt1isBit=false&serverTimezone=Asia/Shanghai
jdbc.username=
jdbc.password=

server.tomcat.uri-encoding = UTF-8
server.port = 8090
server.tomcat.basedir = ./tmpSave
datasource.plugin.path=/opt/dtstack/DTCommon/InsightPlugin/dataSourcePlugin
```

:::caution
需要依赖[DatasourceX](https://github.com/DTStack/DatasourceX/releases/tag) 去获取数据源表、字段信息  
可以直接下载datasourceX源码 解压之后在配置文件中配置`datasource.plugin.path`即可  
jdbc需要指定`charset=utf8` 否则在对接完集群之后，获取开发目录可能会乱码  
:::


## 启动/停止
### 项目结构
完整的项目结构如下
``` shell
├── bin
│   ├── base.sh
│   ├── taier.sh
├── conf
│   ├── application.properties
│   ├── java.policy
│   └── logback.xml
├── flinkconf
│   ├── debug
│   ├── error
│   ├── fatal
│   ├── info
│   ├── info-tmp
│   ├── log4j2
│   └── warn
├── lib
│   └── taier-data-develop-with-dependencies.jar
├── logs
│   ├── taier_flink_monitor.log
│   ├── taier.log
│   ├── taier_request.log
│   ├── taier_schedule.log
│   └── taier_zk.log
├── pluginLibs
│   ├── dummy
│   ├── flinkcommon
│   ├── hdfs2
│   ├── hdfs3
│   ├── hive
│   ├── hive2
│   ├── hive3
│   ├── yarn2
│   ├── yarn2-hdfs2-flink112
│   ├── yarn2-hdfs2-hadoop2
│   ├── yarn2-hdfs2-spark210
│   ├── yarn3
│   ├── yarn3-hdfs3-flink112
│   ├── yarn3-hdfs3-hadoop3
│   └── yarn3-hdfs3-spark210
├── run
│   └── rdos.pid
```

依赖组件正常
- [x] zookeeper
- [x] mysql
- [x] datasourcex


* 启动:
```shell
$ ./bin/taier.sh start
```
* 停止:
```shell
$ ./bin/taier.sh stop
```