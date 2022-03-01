---
title: 部署依赖
sidebar_label: 部署依赖
---

# 部署依赖
## 集群依赖
### spark thrift
1. 下载`spark`安装包，我们选择的是[spark2.1.3b](https://archive.apache.org/dist/spark/spark-2.1.3/spark-2.1.3-bin-hadoop2.7.tgz)
2. 解压spark-2.1.3-bin-hadoop2.7.tgz
3. 将core-site.xml、hdfs-site.xml、yarn-site.xml、hive-site.xml拷贝到${SPARK_HOME}/conf目录下
4. 启动spark thriftserver:
```shell
$   cd ${SPARK_HOME}/sbin && sh start-thriftserver.sh
```

### flinkx
依赖`flinkx` [1.10](https://github.com/DTStack/flinkx/releases/tag/1.10.5) 版本
[flink源码编译](https://github.com/DTStack/flinkx/blob/master/docs/quickstart.md)

## maven依赖
### 构建依赖
Taier源码编译启动中依赖DatasourceX的core包以及自定义hive的jar包，maven仓库地址[地址](https://repo1.maven.org/maven2/com/dtstack/)  
如果构建拉取不到，请手动编译
## 编辑器依赖
源码中包含部分scala的代码，通过idea启动需要添加scala的sdk 
```editorconfig
open module setting -> global libraries
```
建议版本为`2.11.1`

