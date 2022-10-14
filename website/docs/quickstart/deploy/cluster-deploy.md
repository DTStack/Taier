---
title: 集群部署
sidebar_label: 集群部署
---

## 环境准备
参考[单机部署](./quickstart/deploy/deployment-quick.md)部署多个taier的实例
```shell
taier-node1
taier-node2
taier-node3
```


## nginx配置
1. 下载并安装nginx
2. 在nginx.conf文件中修改upstream和taier节点的配置  
```shell
upstream taier-server {
  ip_hash;
  server taier-node1:8090 max_fails=3 fail_timeout=20s weight=30;
  server taier-node2:8090 max_fails=3 fail_timeout=20s weight=30;
  server taier-node3:8090 max_fails=3 fail_timeout=20s weight=40;
}

server { 
  location / {
    root /var/taier-ui/dist;
    index  index.html;
    autoindex on;
  }

  location /taier {
    proxy_set_header X-Real-IP  $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header Host $host;
    proxy_pass http://taier-server;
  }
}
```

3. 配置前端文件
  在nginx所在服务器目录 /var/taier-ui/dist 放置前端源码文件

   
## 重启服务

配置完成后，重启 Nginx 服务。

```bash
nginx -s reload
```

## 访问服务
直接访问nginx的ip 即可看到页面[快速上手](./quickstart/start.md)

:::tip
集群模式部署下，不同节点的taier实例使用的MySQL和Zookeeper需要相同
:::