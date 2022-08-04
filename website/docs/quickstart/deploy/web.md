# 前端部署

:::tip
我们建议通过 [docker 部署](./docker.md)，解决环境安装的烦恼
:::

## 准备环境

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

## 配置代理

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

### 重启服务

配置完成后，重启 Nginx 服务。

```bash
nginx -s reload
```

此时访问 http://Your-IP-Address（例如：http://127.0.0.1/） 后仍然出现 `Welcome to nginx!` 的页面，原因是因为 Nginx 的默认监听端口和我们的 taier-ui 监听的端口都是 80 端口，此时 Nginx 通过 server_name 来区分需要转发到对应的应用中去。

那么我们接下来需要通过配置 hosts 将 `.taier.com` 转发到路由去。

## 配置 hosts

:::caution
这一步需要在主机电脑操作，并不是在服务器操作。即**打开浏览器的那一台电脑**。
:::

找到 `hosts` 文件所在的位置，windows 用户的 hosts 通常在 `C:\Windows\System32\drivers\etc\hosts`，而 Linux 通常在 `/etc/hosts`。

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

完成配置后，访问 http://www.taier.com 则可以看到页面。

## 其他版本安装

如果有需要安装低于 `v1.2.0` 的版本，我们**强烈建议**您通过 docker 部署。

:::caution
如果您执意要通过源码安装，则建议先学习前端知识，Linux 相关命令以及前端工程化相关知识。
:::
