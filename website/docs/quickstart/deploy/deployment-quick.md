---
title: 单机部署
sidebar_label: 单机部署
---

:::caution
在执行以下操作前，你需要先确定已完成Taier前的部署依赖准备工作
:::

## 环境准备

- 本地正确安装JDK1.8+
- 可用的zookeeper
- 下载好的[DatasourceX](https://github.com/DTStack/DatasourceX/releases/tag)
- MySQL初始化[Taier初始数据](https://github.com/DTStack/Taier/blob/master/sql/init.sql)

:::tip
低版本升级到高版本 执行[高版本目录](https://github.com/DTStack/Taier/tree/master/sql)下的`increment.sql`
:::

## 后端部署

-
下载 [taier-data-develop-with-dependencies.jar](https://github.com/DTStack/Taier/releases/download/v1.2.0/taier-data-develop-with-dependencies.jar)
- 下载 [taier-plugins插件包](https://github.com/DTStack/Taier/releases/download/v1.2.0/pluginLibs.tar.gz)

- 解压plugins插件包

- 配置文件目录

```
|-- conf 
|---- application.properties  //配置文件
|---- logback.xml             //日志配置
```

- 修改配置信息

完整的application.properties应该如下

```properties
nodeZkAddress=127.0.0.1:2181/taier
jdbc.driverClassName=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://127.0.0.1:3306/taier?charset=utf8&autoReconnect=true&tinyInt1isBit=false&serverTimezone=Asia/Shanghai
jdbc.username=
jdbc.password=
server.tomcat.uri-encoding=UTF-8
server.port=8090
server.tomcat.basedir=./tmpSave
datasource.plugin.path=/opt/dtstack/DTCommon/InsightPlugin/dataSourcePlugin
```

:::caution
jdbc需要指定`charset=utf8` 否则在对接完集群之后，获取开发目录可能会乱码  
:::

- 配置启动脚本

```shell
|-- bin
|---- base.sh     //jvm相关参数设置脚本
|---- taier.sh    //启动脚本
```

- 项目结构
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

* 启动:

```shell
$ ./bin/taier.sh start
```

* 停止:

```shell
$ ./bin/taier.sh stop
```

## 前端部署

### 安装 Nginx

[Nginx](https://www.nginx.com/) 是一个高性能的 Web 和反向代理服务器。

> 以下安装以 Centos7 为例，不同服务器命令会有差异。

```bash
rpm -ivh http://nginx.org/packages/centos/7/noarch/RPMS/nginx-release-centos-7-0.el7.ngx.noarch.rpm
yum -y install nginx
```

在安装完成后，可以在浏览器中输入服务器的 IP 地址，如果出现 `Welcome to nginx！`的字样则表示安装成功。

### 准备产物包

Nginx 默认的配置路径是 `/var/www/html/`，现在我们选择在 `/var/www/taier-ui` 这里放我们的 taier 前端项目。

```bash
cd /var
mkdir www && cd www
mkdir taier-ui && cd taier-ui
```

下载最新的 dist 压缩包至当前目录

```bash
# 当前最新版为 `v.2.0`
curl -LJO https://github.com/DTStack/Taier/releases/download/v1.2.0/dist.zip
```

若因部分原因无法通过 curl 下载该文件，可通过浏览器打开进行下载操作。

:::tip
其他版本的相关文件请通过 [release](https://github.com/DTStack/Taier/releases) 查看。

`v1.2.0` 版本之前的版本请使用 `docker` 安装，具体安装步骤见 [docker 部署](./docker.md)
:::

安装完成后在当前目录下会新增 `dist.zip` 文件

```bash
ls
# dist.zip
unzip dist.zip
```

执行完成后，目录下会新增 `dist` 文件夹

### 配置代理

```bash
cd /etc/nginx/conf.d
touch taier.conf
```

在 `/etc/nginx/conf.d` 目录下，我们新建一个叫 `taier.conf` 的文件，用文件是用来配置 Nginx 的代理用的。在该文件中添加如下内容

```nginx title=taier.conf
upstream server {
  # The server ip and port
  server 127.0.0.1:8090;
}


server {
  listen *:80;
  listen [::]:80;
  # The host name to respond to
  server_name .taier.com;
  client_max_body_size  100m;

  proxy_set_header   cache-control no-cache;
  proxy_ignore_headers Cache-Control Expires;
  proxy_set_header   Referer $http_referer;
  proxy_set_header   Host   $host;
  proxy_set_header   Cookie $http_cookie;
  proxy_set_header   X-Real-IP  $remote_addr;
  proxy_set_header X-Forwarded-Host $host;
  proxy_set_header X-Forwarded-Server $host;
  proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;


  location / {
    root /var/www/taier-ui/dist;
    index  index.html;
    autoindex on;
  }

  location /taier {
    proxy_set_header X-Real-IP  $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header Host $host;
    proxy_pass http://server;
 	}
}
```

#### 重启服务

配置完成后，重启 Nginx 服务。

```bash
nginx -s reload
```

此时访问 http://Your-IP-Address（例如：http://127.0.0.1/） 后仍然出现 `Welcome to nginx!` 的页面，原因是因为 Nginx
的默认监听端口和我们的 taier-ui 监听的端口都是 80 端口，此时 Nginx 通过 server_name 来区分需要转发到对应的应用中去。

那么我们接下来需要通过配置 hosts 将 `.taier.com` 转发到路由去。

### 配置 hosts

:::caution
这一步需要在主机电脑操作，并不是在服务器操作。即**打开浏览器的那一台电脑**。
:::

找到 `hosts` 文件所在的位置，windows 用户的 hosts 通常在 `C:\Windows\System32\drivers\etc\hosts`，而 Linux
通常在 `/etc/hosts`。

打开 hosts 文件，其内容通常是一组一组的键值对，如下:

```ini title="hosts"
127.0.0.1   localhost
::1         localhost
```

每一组的键值对都表示如果当前访问 localhost，则浏览器会通过 hosts 解析，将其 IP 地址解析为 127.0.0.1。

那接下来，我们在 `hosts` 文件的最后一行添加如下内容：

```nginx
# Your-IP-Address Your-Address
127.0.0.1 taier.com
```

上述代码的意思是，在浏览器中访问 http://www.taier.com 则系统会解析其对应的 IP 至 `127.0.0.1`。

:::note
这里需要填写你部署的 `taier-ui` 所在的服务器的 IP 地址。
:::

完成配置后，访问 http://www.taier.com 则可以看到页面[快速上手](./quickstart/start.md)

## 其他版本安装

如果有需要安装低于 `v1.2.0` 的版本，我们**强烈建议**您通过 docker 部署。

:::caution
如果您执意要通过源码安装，则建议先学习前端知识，Linux 相关命令以及前端工程化相关知识。
:::
