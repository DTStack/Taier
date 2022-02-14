# 快速开始

## 基础软件安装
 * JDK8及以上
* MySQL：5.7.33
* Zookeeper：3.5.7
* Redis：5.0.7

#### DAGScheduleX依赖的其他基础组件
* DatasourceX（数据源插件）：[4.3.0](https://github.com/DTStack/DatasourceX/releases/tag/v4.3.0)
* SQLParser（SQL解析插件）

## 数据库操作
#### 数据库&表 初始化
1. 请登录 MySQL 数据库，创建名为 Taier 数据库
2. 初始化数据库，导入 `sql/init` 目录下的sql文件进行创建表及基础数据导入
    * 先执行 `sql/init/create`
    * 再执行 `sql/init/insert`
    
    <div align=center> 
     <img src= ./sqlinit.jpg width=300 />
    </div>

#### 表结构升级
导入 `sql/increament` 目录下文件夹内的sql文件进行表结构升级
* datadevelop 任务开发相关
* datasource 数据源相关
* engine 控制台、运维中心相关


## 运行DAGScheduleX中的数据同步任务
* Hadoop：2.7.6（Yarn、HDFS）
* Flink：[release-1.10.3](https://github.com/apache/flink/releases/tag/release-1.10.3)
* FlinkX：[1.10.4](https://github.com/DTStack/flinkx/releases/tag/1.10.4)
    
## 运行DAGScheduleX中的SparkSQL任务
* Hadoop：2.7.6（Yarn、HDFS）
* Spark：2.1.3
* ThriftServer
* Hive Metastore

## 如何编译
```Shell
    ./build/mvn-build.sh
```

### 运行目录检查
```
//启动脚本
|-- bin 
|---- base.sh
|---- centos_dagschedulex.sh

//配置文件
|-- conf 
|---- application.properties
|---- logback.xml

//编译engine-datadevelop 对应jar文件
|-- lib 
|---- engine-datadevelop-XXXX-with-dependencies.jar

//编译engine-plugin 对应jar文件
|-- pluginLibs 
|---- dummy
|---- flinkcommon
|---- yarn2-hdfs2-flink110
|---- .......

//日志目录
|-- logs 

//flink 任务运行时日志配置
|-- flinkconf 

//数据源插件
|-- datasourceplugin

//sql解析插件 
|-- sqlParserplugin 
```

## 启动/停止
* 启动：`./bin/centos_dagschedulex.sh start`
* 停止：`./bin/centos_dagschedulex.sh stop`


## 文档
* [什么是DAGScheduleX](https://github.com/DTStack/Taier/blob/master/docs/Taier%20github.pdf)
    * [Taier 任务提交快速开始](https://github.com/DTStack/Taier/blob/master/docs/submit_CH.md)
    

#### 插件文档
* [flink插件](https://github.com/DTStack/Taier/blob/master/docs/plugins/flink.md)
* [spark插件](https://github.com/DTStack/Taier/blob/master/docs/plugins/spark_yarn.md)
* [dtscript插件](https://github.com/DTStack/Taier/blob/master/docs/plugins/dtscript.md)
* [mysql插件](https://github.com/DTStack/Taier/blob/master/docs/plugins/mysql.md)
  * 使用 mysql插件 用户需要create 权限并且mysql配置要设置 enforce_gtid_consistency=OFF，gtid_mode=OFF
* [oracle插件](https://github.com/DTStack/Taier/blob/master/docs/plugins/oracle.md)
* [sqlserver插件](https://github.com/DTStack/Taier/blob/master/docs/plugins/sqlserver.md)
* [hive插件](https://github.com/DTStack/Taier/blob/master/docs/plugins/hive.md)
* [postgresql插件](https://github.com/DTStack/Taier/blob/master/docs/plugins/postgresql.md)

#### 其他参考资料
* [开源集群配置Kerberos认证](https://github.com/DTStack/Taier/blob/master/docs/hadoopWithKerberos.docx) 
* [在CDH集群中启用及配置Kerberos](https://github.com/DTStack/Taier/blob/master/docs/chdWhithKerberos.docx) 

