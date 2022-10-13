---
title: 快速上手
sidebar_label: 快速上手
---

# 快速上手

## 访问页面

前后端代码部署完成之后 就可以访问 Taier

> 地址：http://127.0.0.1:8090/taier 用户名密码：admin@dtstack.com/admin123

![login](/img/readme/login.png)

:::caution
点击登录 显示未登录 或者 浏览器显示 502 请参考 https://github.com/DTStack/Taier/issues/260  
这种情况一般是前后端 cookie 域的问题
:::

## 选择租户

登陆成功之后，需要选择对应的租户，页面数据跟随选中的租户切换

![switch-tenant](/img/readme/switch-tenant.png)

## 新增集群

1. 进入控制台 > 2. 多集群管理 > 3. 新增集群  
   配置集群参考 [集群配置](././functions/multi-cluster.md)  
   配置组件参考 [组件配置](././functions/component/sftp.md)

### 配置 kerberos

> 对接集群如果开启 kerberos, 需要将集群的 keytab、krb5.conf 文件压缩上传

![kerberos](/img/readme/kerberos.png)

:::caution
集群配置完成之后 点击测试所有组件连通性，确保配置组件连接正常
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
