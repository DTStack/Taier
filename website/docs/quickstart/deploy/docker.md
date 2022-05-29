# docker 部署

:::tip
注意：taier的docker镜像，目前是通过目录挂载的去加载datasourcex和chunjun，以下操作默认插件包都已经下载
:::

以datasoucex为例 解压后目录结构为
``shell
/opt/dtstack/DTPlugin/InsightPlugin/dataSourcePlugin
├── aws_s3
├── clickhouse
├── db2
├── dmdb
├── doris
├── emq
├── es
├── es7
├── ftp
├── gbase
├── greenplum6
├── hbase
├── hbase2
├── hbase_gateway
├── hdfs
├── hive
├── hive1
├── hive3
├── impala
├── inceptor
├── influxdb
├── kafka
├── kingbase8
├── kudu
├── kylin
├── kylinrestful
├── libra
├── maxcompute
├── mongo
├── mysql5
├── mysql8
├── oceanbase
├── opentsdb
├── oracle
├── phoenix
├── phoenix4_8
├── phoenix5
├── postgresql
├── presto
├── redis
├── restful
├── s3
├── socket
├── solr
├── spark
├── sqlServer
├── sqlServer2017
├── vertica
└── websocket
``

## 1. 仅使用taier的web和ui镜像
仅使用taier的web和ui，确保已有外部的mysql数据源，初始化好相关taier的数据库  
                    确保已有外部的zookeeper，可以正常连接

获取taier镜像 
```shell
$ docker pull dtopensource/taier:1.1
$ docker pull dtopensource/taier-ui:1.1
```

启动web镜像
```shell
docker run -itd -p 8090:8090 --env ZK_HOST=172.16.85.111 \
--env ZK_PORT=2181 \
--env DB_HOST=172.16.101.187 \
--env DB_PORT=3306 \
--env DB_ROOT=root  \
--env DB_PASSWORD=123456 \
--env DATASOURCEX_PATH=/usr/taier/datasourcex \
-v /opt/dtstack/DTPlugin/InsightPlugin/dataSourcePlugin:/usr/taier/datasourcex \
dtopensource/taier:1.1
```

启动ui镜像
TAIER_IP 为启动web镜像ip
```shell
docker run -itd -p 80:80 --env TAIER_IP=172.16.100.38 \
--env TAIER_PORT=8090 \
dtopensource/taier-ui:1.1
```

当命令执行完成后，在浏览器上直接访问 127.0.0.1 即可


:::caution
访问页面 如果浏览器出现502，请手动确认ui容器是否和web镜像容器网络是否互通
:::

:::tip
如果web容器和ui容器都同台服务器上，ui容器需要访问宿主讥网络 请修改防火墙策略
``shell
firewall-cmd --zone=public --add-port=8090/tcp --permanent  
firewall-cmd --reload
``
:::

## 2. 使用taier docker-compose
获取taier最新的docker-compose文件
```yaml
version: '3'
services:
  taier-db:
    image: dtopensource/taier-mysql:1.1
    environment:
      MYSQL_DATABASE: taier
      MYSQL_ROOT_PASSWORD: 123456
  taier-zk:
    image: zookeeper:3.4.9
  taier-ui:
    image: dtopensource/taier-ui:1.1
    ports:
      - 80:80
    environment:
      TAIER_IP: taier
      TAIER_PORT: 8090
  taier:
    image: dtopensource/taier:1.1
    environment:
      ZK_HOST: taier-zk
      ZK_PORT: 2181
      DB_HOST: taier-db
      DB_PORT: 3306
      DB_ROOT: root
      DB_PASSWORD: 123456
      DATASOURCEX_PATH: /usr/taier/datasourcex
    volumes:
        - /data/datasourcex:/usr/taier/datasourcex
```

进入docker-compose目录，执行
```shell
$ docker-compose up -d
```
