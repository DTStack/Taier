---
title: 后端部署
sidebar_label: 后端部署
---

## 数据库操作

### 库
请登录 **MySQL** 数据库，创建名为 `taier` 数据库
### 表结构
初始化数据库，导入 `sql/create.sql` sql文件进行创建表
### 表数据
初始化数据库，导入 `sql/insert.sql` sql文件进行基础数据导入

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
|---- mvn-build.sh           //编译项目及任务提交脚本 : Taier启动jar和任务提交插件jar
|---- mvn-build-datadevelop  //编译项目脚本 : Taier启动jar
|---- mvn-build-plugins.sh   //编译任务提交脚本 : Taier任务提交插件jar
```
:::tip
Taier 页面功能依赖data-develop.jar 任务提交依赖plugins相关jar
:::

### 检查编译结果
通过脚本编译项目后会生成对应的源码文件，在项目目录下我们可以检查编译的结果
``` shell
|-- lib 
|---- taier-data-develop-xxxx-with-dependencies.jar

//编译 Taier-plugins 对应jar文件
|-- pluginLibs 
|---- dummy
|---- flinkcommon
|---- yarn2-hdfs2-flink110
|---- .......
```
:::caution
pluginLibs目录需要放到Taier进程的根目录 或者通过在application.properties指定pluginLibs的路径 否则会导致集群测试连通性找不到插件
:::

## 配置文件

### 配置文件目录

```
|-- conf 
|---- application.properties  //配置文件
|---- logback.xml             //日志配置
```


### conf/application.properties配置
:::info
application.properties中部分配置依赖前文依赖组件中的部分组件
:::

#### 配置zookeeper
```properties
nodeZkAddress=127.0.0.1:2181/taier
```

#### 配置mysql
```properties
jdbc.driverClassName=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://127.0.0.1:3306/taier?charset=utf8&autoReconnect=true&tinyInt1isBit=false&serverTimezone=Asia/Shanghai
jdbc.username=root
jdbc.password=
```
:::caution
jdbc需要指定charset=utf8 否则在对接完集群之后，获取开发目录可能会乱码
:::

#### 配置web
```properties
server.tomcat.uri-encoding = UTF-8
server.port = 8090
server.tomcat.basedir = ./tmpSave
```

#### 配置DatasourceX插件地址
```properties
datasource.plugin.path=/opt/dtstack/DTCommon/InsightPlugin/dataSourcePlugin
```

:::caution
DatasourceX的依赖版本为v4.3.0 [DatasourceX](https://github.com/DTStack/DatasourceX/releases/tag/v4.3.0) 需要依赖DatasourceX 去获取数据源表、字段信息
:::

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


## 启动/停止
* 启动:
```shell
$ ./bin/taier.sh start
```
* 停止:
```shell
$ ./bin/taier.sh stop
```