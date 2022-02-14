---
title: 后端部署
sidebar_label: 后端部署
---

## 数据库操作
### 表结构
1. 请登录 **MySQL** 数据库，创建名为 `taier` 数据库
2. 初始化数据库，导入 `sql/init` 目录下的sql文件进行创建表
    * 先执行 `sql/create`
    * 再执行 `sql/insert`

<div align="center"> 
    <img src="/img/readme/sqlinit.jpg" width="300" />
</div>

### 表数据
3. 初始化数据库，导入 `sql/init` 目录下的sql文件进行基础数据导入
   * 再执行 `sql/insert`

<div align="center"> 
    <img src="/img/readme/sqlinit.jpg" width="300" />
</div>

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
|---- mvn-build.sh           //编译项目及任务提交脚本 : taier启动jar和任务提交插件jar
|---- mvn-build-datadevelop  //编译项目脚本 : taier启动jar
|---- mvn-build-plugins.sh   //编译任务提交脚本 : taier任务提交插件jar
```
:::tip
taier 启动jar和任务提交jar是息息相关的,任务运行离不开二者
:::

### 检查编译结果
通过脚本编译项目后会生成对应的源码文件，在项目目录下我们可以检查编译的结果
```shell
|-- lib 
|---- taier-data-develop-xxxx-with-dependencies.jar

//编译 taier-plugins 对应jar文件
|-- pluginLibs 
|---- dummy
|---- flinkcommon
|---- yarn2-hdfs2-flink110
|---- .......
```

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

:::info
DatasourceX的依赖版本为v4.3.0 [DatasourceX](https://github.com/DTStack/DatasourceX/releases/tag/v4.3.0)
:::

完整的application.properties应该如下
```properties
nodeZkAddress=127.0.0.1:2181/taier
jdbc.driverClassName=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://127.0.0.1:3306/taier?charset=utf8&autoReconnect=true&tinyInt1isBit=false&serverTimezone=Asia/Shanghai
jdbc.username=root
jdbc.password=

server.tomcat.uri-encoding = UTF-8
server.port = 8090
server.tomcat.basedir = ./tmpSave
datasource.plugin.path=/opt/dtstack/DTCommon/InsightPlugin/dataSourcePlugin
```


## 启动/停止
* 启动:
```shell
./bin/taier.sh start
```
* 停止:
```shell
./bin/taier.sh stop
```