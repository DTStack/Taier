



---







# Kubernetes 快速部署

此操作步骤，默认部署者有了一套可用的 Kubernetes 环境。

```bash
# 查看 k8s 集群
$ kubectl get nodes
NAME            STATUS                     ROLES                  AGE    VERSION
k8s-n5          Ready                      <none>                 164d   v1.19.4
k8s-n4          Ready                      <none>                 164d   v1.19.4
k8s-n3          Ready                      control-plane,master   165d   v1.19.4
k8s-n2          Ready                      control-plane,master   165d   v1.19.4
k8s-n1          Ready,SchedulingDisabled   master                 165d   v1.19.4
```


## 1. 说明

快速部署的组件

| 服务              | 版本     |
| ----------------- |--------|
| mysql（单机版）   | 5.7    |
| zookeeper（集群） | 3.5.7  |
| taier 前后端      | master |

## 2. 快速部署

共有4个镜像

- taier-db是mysql数据库镜像
- taier-zk是zk的镜像
- taier-ui是taier前端的镜像
- taier是taier后端的镜像


参考命令：
```bash
# 从 github 拉 Taier 代码
$ git clone https://github.com/DTStack/Taier.git

# 进入目录
$ cd Taier/k8s

# 部署
$ kubectl apply -f taier.yaml
namespace/dt-taier created
service/zookeeper created
service/zookeeper-headless created
poddisruptionbudget.policy/zookeeper-pod-disruption-budget created
statefulset.apps/zookeeper created
service/mysql-svc created
deployment.apps/mysql created
deployment.apps/taier-web created
service/taier-web-svc created
deployment.apps/taier created
service/taier-svc created
configmap/taier-nginx-cm created
configmap/nginxconf created
deployment.apps/dmp-nginx created
service/nginx created

# 部署成功后会看到如下信息
$ kubectl get all -n dt-taier
NAME                             READY   STATUS    RESTARTS   AGE
pod/dmp-nginx-668c469657-hvhb4   1/1     Running   0          8h
pod/mysql-789db7c598-khg9m       1/1     Running   0          8h
pod/taier-646d4c587-g2w6q        1/1     Running   0          8h
pod/taier-web-d55bf9866-brz7h    1/1     Running   0          8h
pod/zookeeper-0                  1/1     Running   0          8h
pod/zookeeper-1                  1/1     Running   0          8h
pod/zookeeper-2                  1/1     Running   0          8h

NAME                         TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)             AGE
service/mysql-svc            NodePort    10.96.201.155   <none>        3306:31264/TCP      8h
service/nginx                ClusterIP   10.96.94.139    <none>        80/TCP,443/TCP      8h
service/taier-svc            ClusterIP   10.96.72.138    <none>        8090/TCP            8h
service/taier-web-svc        ClusterIP   10.96.230.106   <none>        80/TCP              8h
service/zookeeper            ClusterIP   10.96.9.196     <none>        2181/TCP,7000/TCP   8h
service/zookeeper-headless   ClusterIP   None            <none>        2888/TCP,3888/TCP   8h

NAME                        READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/dmp-nginx   1/1     1            1           8h
deployment.apps/mysql       1/1     1            1           8h
deployment.apps/taier       1/1     1            1           8h
deployment.apps/taier-web   1/1     1            1           8h

NAME                                   DESIRED   CURRENT   READY   AGE
replicaset.apps/dmp-nginx-668c469657   1         1         1       8h
replicaset.apps/mysql-789db7c598       1         1         1       8h
replicaset.apps/taier-646d4c587        1         1         1       8h
replicaset.apps/taier-web-d55bf9866    1         1         1       8h

NAME                         READY   AGE
statefulset.apps/zookeeper   3/3     8h
```


## 3. 配置代理
### nginx

```bash
# 查看 k8s -> namespace=dt-taier 的 nginx CLUSTER-IP
$ kubectl get svc -n dt-taier | grep nginx
NAME                 TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)             AGE
nginx                ClusterIP   10.96.94.139    <none>        80/TCP,443/TCP      8h

# 在宿主机上
server {
    listen 80;

    server_name dt.taier.cn;

    charset utf-8;
    proxy_http_version 1.1;
    proxy_set_header Upgrade $http_upgrade;
    proxy_set_header Connection "upgrade";

    location / {
       proxy_set_header   X-Real-IP $remote_addr;
       proxy_set_header Host  $host;
       proxy_set_header   X-Forwarded-Server $host;
       proxy_set_header x_request_id $request_id;
       proxy_pass http://10.96.94.139;   ##  这里需要根据实际情况填nginx 的 CLUSTER-IP
    }

    location /taier {
       proxy_set_header   X-Real-IP $remote_addr;
       proxy_set_header Host  $host;
       proxy_set_header   X-Forwarded-Server $host;
       proxy_set_header x_request_id $request_id;
       proxy_pass http://10.96.94.139;   ##  这里需要根据实际情况填nginx 的 CLUSTER-IP
    }
}
```

## 4. 访问

先在机器上配置host文件，如下:

```bash
nginx_机器_ip dt.taier.cn
```

访问地址：http://dt.taier.cn



## 可能出现的问题
第一次部署时，可能会出现 taier 的后端服务探针失败导致容器重启，此时需要等待几分钟，查看 MySQL 容器是否初始化完成(容器启动成功, 并且数据库和表初始化完整)，待 MySQL 容器初始化完成后，删掉当前 taier 后端服务的 pod 等待重启完成。


























