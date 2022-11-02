---
title: Flink-standalone
sidebar_label: Flink-standalone
---

## 启动Flink Standalone环境

**1、下载 flink**
* flink官网下载 [release 1.12.7](https://flink.apache.org/downloads.html)

**2、chunjun的插件**
* 拉取最新的chunjun [1.12](https://github.com/DTStack/chunjun/releases/tag/v1.12.3) 版本项目，打包 mvn clean package -DskipTests, 将生成的chunjun-dist 拷贝至 $FLINK_HOME/lib
* chunjun 有些依赖包依赖是provided，需要将缺省的jar放到$FLINK_HOME/lib
例如：在chunjun-core最新版本中将logback的依赖改为了provided,  需要将以下logback包放到$FLINK_HOME/lib

  > logback-core-1.2.11.jar
  > logback-classic-1.2.11.jar

**3、flink1.12 standalone服务参数**
所有节点都需要配置
```
# 类加载配置
classloader.resolve-order: parent-first
classloader.check-leaked-classloader: false

# 高可用配置(单机 None)
high-availability: zookeeper
high-availability.zookeeper.quorum: kudu1:2181,kudu2:2181,kudu3:2181
high-availability.zookeeper.path.root: /flink112
high-availability.storageDir: hdfs://ns1/dtInsight/flink112/ha
high-availability.cluster-id: /standalone_ha

# 指标配置
metrics.reporter.promgateway.class: org.apache.flink.metrics.prometheus.PrometheusPushGatewayReporter
metrics.reporter.promgateway.host: 172.16.23.25
metrics.reporter.promgateway.port: 9091
metrics.reporter.promgateway.jobName: flink112
metrics.reporter.promgateway.randomJobNameSuffix: true
metrics.reporter.promgateway.deleteOnShutdown: false
```

## Flink1.12 on Standalone 控制台参数

| 参数名 | 含义 | 是否必填 * 为必填 | 默认值 |
| --- | --- | --- | --- |
| clusterMode               |flink部署模式          | * | standalone |
| flinkLibDir               | flink libs的路径      | * | /data/flink112_lib |
| chunjunDistDir            | chunjun 插件地址       | * | /data/chunjun/chunjun-dist |
| remoteChunjunDistDir       | chunjun 插件远端地址    | * | /data/chunjun/chunjun-dist |
| pluginLoadMode            | 加载文件的方式          | * | classpath |
| jobmanager.rpc.address     | jobmanager rpc地址     |   |  |
| jobmanager.rpc.port        | jobmanager rpc端口号   |   |  |
| rest.port                 | ui端口号	             |   |  |
| high-availability          | 高可用服务类型		    |   | NONE |
| high-availability.zookeeper.quorum     | zookeeper集群地址	 |  |  |
| high-availability.zookeeper.path.root  | flink存储状态在zookeeper的根节点路径		 |  |  |
| high-availability.storageDir          | flink高可用模式下存储元数据的文件系统路径(URI) |  |  |
| high-availability.cluster-id          | flink集群的id， 用于区分多个flink集群		 |  |  |
| prometheusHost  | prometheus地址 | * |  |
| prometheusPort  | prometheus端口 | * |  |
| state.backend   | 状态后端       |   | jobmanager |



## 新增集群

1. 进入控制台 > 2. 多集群管理 > 3. 新增集群  
   配置集群参考 [集群配置](././functions/multi-cluster.md)  
   配置组件参考 [组件配置](././functions/component/sftp.md)

## 绑定集群

> 控制台>资源管理>绑定新租户 会初始化相关目录、schema、默认数据源信息

![bing-tenant](/img/readme/bind-tenant.png)



