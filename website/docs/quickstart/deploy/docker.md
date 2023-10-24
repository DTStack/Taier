# Docker 部署

## 1. 使用taier镜像
仅使用taier的镜像，确保以下外部依赖服务正常:
- [x] Zookeeper
- [x] MySQL初始化[Taier初始数据](https://github.com/DTStack/Taier/blob/master/sql/init.sql)

获取taier镜像 
```shell
$ docker pull dtopensource/taier:latest
```

启动web容器,mysql和zookeeper的配置信息根据实际环境调整
```shell
docker run -itd -p 8090:8090 --env ZK_HOST=${ZK_HOST} \
--env ZK_PORT=2181 \
--env DB_HOST=${MYSQL_HOST} \
--env DB_PORT=3306 \
--env DB_ROOT=${DB_ROOT}  \
--env DB_PASSWORD=${DB_PASSWORD} \
dtopensource/taier:latest
```


## 2. 使用docker-compose
通过docker-compose启动
```yaml
version: '3'
services:
  taier-db:
    image: dtopensource/taier-mysql:latest
    environment:
      MYSQL_DATABASE: taier
      MYSQL_ROOT_PASSWORD: 123456
      TZ: Asia/Shanghai
    ports:
      - 3306:3306
  taier-zk:
    image: zookeeper:3.4.9
  taier:
    image: dtopensource/taier:latest
    environment:
      ZK_HOST: taier-zk
      ZK_PORT: 2181
      DB_HOST: taier-db
      DB_PORT: 3306
      DB_ROOT: root
      DB_PASSWORD: 123456
      TZ: Asia/Shanghai
    ports:
      - 8090:8090
```

进入docker-compose目录，执行
```shell
$ docker-compose up -d
```

:::tip 
当命令执行完成后，在浏览器上直接访问 镜像ip:8090 进行[快速上手](./quickstart/start.md)
:::

:::caution
由于docker镜像大小问题，Chunjun、Flink插件包相关文件并未打包到容器内，有使用Flink相关功能，需要下载Chunjun、Flink插件包。自行挂载相关目录，并在[Flink组件](./functions/component/flink-on-yarn.md)
上配置对应目录
:::