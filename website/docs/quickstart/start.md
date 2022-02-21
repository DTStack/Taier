---
title: 快速上手
sidebar_label: 快速上手
---
# 快速上手
## 访问页面
代码部署完成之后 就可以访问Taier
>  地址：http://localhost:8090/taier 用户名密码：admin@dtstack.com/admin123

![login](/img/readme/login.png)

## 选择租户
登陆成功之后，需要选择对应的租户，页面数据跟随选中的租户切换

![switch-tenant](/img/readme/switch-tenant.png)

## 新增集群
进入控制台>多集群管理>新增集群

![add-cluster](/img/readme/add-cluster.png)

## 配置集群
### SFTP组件

![sftp](/img/readme/sftp.png)

### YARN组件
> YARN 组件配置 依赖集群的hadoop相关配置文件  
> 将yarn-site.xml、core-site.xml 压缩成zip包上传 


![yarn](/img/readme/yarn.png)

### HDFS组件
> 将hdfs-site.xml、hive-site.xml 压缩成zip包上传  

![hdfs](/img/readme/hdfs.png)

### Flink组件
> flink组件需要确认flinkJarPath、flinkPluginRoot、zookeeper、prometheus、checkpoint、savepoint等信息

![flink](/img/readme/flink.png)


### Spark组件
> spark组件需要确认spark.eventLog.enabled、sparkPythonExtLibPath、sparkSqlProxyPath、sparkYarnArchive等路径

![spark](/img/readme/spark.png)

### Spark Thrift组件
> Spark Thrift组件需要jdbcUrl是否正确   

![spark-thrift](/img/readme/spark-thrift.png) 


### 配置kerberos
> 对接集群如果开启kerberos, 需要将集群的keytab、krb5.conf文件压缩上传

![kerberos](/img/readme/kerberos.png)

:::caution
集群配置完成之后 点击测试所有组件连通性，确保配置组件链接正常
:::


## 绑定集群
> 控制台>资源管理>绑定新租户 会初始化相关目录、schema、默认数据源信息  

![bing-tenant](/img/readme/bind-tenant.png)

## 新建任务

![add-task](/img/readme/add-task.png)

### Spark SQL

![spark-sql](/img/readme/spark-sql.png)

### 数据同步

![sync](/img/readme/sync.png)