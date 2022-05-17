# docker 部署

## 1. 使用docker-compose快速部署

:::tip
注意：使用docker部署的时候，必须先要安装docker和docker-compose
:::

1. 可以先去git上面拉去taier工程，或者直接在master分支下载docker-compose.yml文件，docker-compose.yml文件存放在项目的根目录下
2. 使用终端进入到docker-compose.yml所在的目录，然后执行命令 
```shell
$ docker-compose up -d
```
3. 获取hub的上面的镜像并部署，当命令执行完成后，在浏览器上直接访问 127.0.0.1 即可

docker-compose.yml文件内容如下
```yaml
version: '3'
services:
  taier-db:
    image: dtopensource/taier-mysql:1.10
#    ports:
#      - 3306:3306
    environment:
      MYSQL_DATABASE: taier
      MYSQL_ROOT_PASSWORD: 123456
  taier-zk:
    image: zookeeper:3.4.9
  taier-ui:
    image: dtopensource/taier-ui:1.10
    ports:
      - 80:80
    environment:
      TAIER_IP: taier
  taier:
    image: dtopensource/taier:1.10
#    ports:
#      - 8090:8090
    environment:
      NODE_ZKADDRESS: taier-zk
      MYSQL_IP: taier-db
```

共有4个镜像 
* taier-db是mysql数据库镜像 
* taier-zk是zk的镜像 
* taier-ui是taier前端的镜像 
* taier是taier后端的镜像

## 2. 直接获取hub上面镜像部署
如果想要使用自己mysql和zk，只需要下载taier-ui镜像和taier镜像
```shell
// taier地址
docker pull dtopensource/taier:1.0

// taier-ui地址
docker pull dtopensource/taier-ui:1.0
```

可以在taier镜像中配置环境变量mysql和zk地址如下
```shell
MYSQL_ROOT: 数据库用户名
MYSQL_ROOT_PASSWORD 数据库密码
MYSQL_IP 数据库ip
MYSQL_PORT 数据库端口
NODE_ZKADDRESS zk的地址
```
参考命令：
```shell
docker run -itd -p 端口号:端口号 --env MYSQL_ROOT=环境变量参数 --network 网段 dtopensource/taier:1.0 /bin/bash
```

taier-ui和taier同理，但是taier-ui只需要配置taier后端的地址即可 --env TAIER_IP=
```shell
TAIER_IP: taier后端的地址
```

## 3. 使用Dockerfile构建镜像
   如果想使用Dockerfile构建镜像，则需要准备一些第三方jar
* 下载taier源码，执行脚本mvn-build.sh 编辑出taier后端所需要的jar包。
* 下载第三方jar,chunjun和datasourceX并把这两个第三方jar打包成chunjun.tar.gz和datasourceX.tar.gz放在项目的更目录下。（chunjun和datasourceX的jar获取请参考后端快速部署）
* 执行在项目的更目录下执行
```shell 
$ docker build -t tag:version . 
```

taier-ui的docker打包方式
* 首先进入到ui的项目工程里面 
``` shell
$ cd taier-ui
```
* 执行命令 
```shell 
$ yarn install 
``` 
执行完成后继续执行 
```shell 
$ yarn build 
``` 
执行完成后，项目工程会多出来一个目录dist，说明前端打包成功。
* 然后在ui目录下执行 
```shell 
$ docker build -t tag:version . 
```

启动镜像请参考步骤二











