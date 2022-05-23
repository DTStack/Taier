



---







# Kubernetes 快速部署

使用此操作步骤，默认认为部署者已经有了一套可用的 Kubernetes 环境，并且有配置了NFS、k8s的storageclass.

```bash
# 查看 k8s 集群
$ kubectl get nodes
NAME            STATUS                     ROLES                  AGE    VERSION
k8s-n5          Ready                      <none>                 164d   v1.19.4
k8s-n4          Ready                      <none>                 164d   v1.19.4
k8s-n3          Ready                      control-plane,master   165d   v1.19.4
k8s-n2          Ready                      control-plane,master   165d   v1.19.4
k8s-n1          Ready,SchedulingDisabled   master                 165d   v1.19.4

# 查看动态供应
$ kubectl get sc 
NAME         PROVISIONER                                               RECLAIMPOLICY   VOLUMEBINDINGMODE   ALLOWVOLUMEEXPANSION   AGE
nfs-client   cluster.local/nfs-client-release-nfs-client-provisioner   Delete          Immediate           true                   165d
```



## 1. 说明

快速部署的组件

| 服务              | 版本  |
| ----------------- | ----- |
| mysql（单机版）   | 5.7   |
| zookeeper（集群） | 3.5.7 |
| taier 前后端      | 1.11  |

## 2. 快速部署

```bash
# 从 github 拉 Taier 代码
$ git clone https://github.com/DTStack/Taier.git

# 进入目录
$ cd Taier/k8s

# 部署
$ kubectl apply -f taier.yaml

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

## 3. 打包部署

如果需要对taier代码做二次开发，需要自己编译代码重新打包，部署则采用如下方式

```bash
## 1. building Taier code
$ cd $TAIER_HOME
$ mvn clean package -Dmaven.test.skip=true

## 2. 构建好代码之后, 编写 Dockerfile 文件
$ vim Dockerfile
FROM openjdk:8u332-oraclelinux7

ENV SERVICE_NAME taier-service 
ENV LC_ALL zh_CN.UTF-8
ENV plugin_path /opt/datasourceX

WORKDIR /opt/

# create dir
RUN mkdir   -p /opt/logs
RUN mkdir   -p /opt/run
RUN mkdir   -p /opt/tmpSave

# 减小容器大小，仅拷贝必要文件进容器, datasourceX 等用数据卷挂载进去
COPY start.sh  /opt/
COPY bin       /opt/bin
COPY conf      /opt/conf
COPY lib       /opt/lib

RUN chmod +x start.sh
RUN touch /var/log/1.log

# 设置时区
RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo "Asia/shanghai" >> /etc/timezone

CMD ./start.sh && tail -F /var/log/1.log

$ vim start.sh
#! /bin/sh
sh ./bin/taier.sh start


## 3. 手动打镜像
docker build -t dtopensource/taier:2.0 -f .

## 4. 部署(需要手动把挂载的文件提前放好)
### 将 taier.yaml 文件中的 <4. taier 后端服务> 改为如下内容
# 4. taier 后端服务
apiVersion: apps/v1
kind: Deployment
metadata:
  name: taier
  namespace: dt-taier
spec:
  selector:
    matchLabels:
      app: taier
  replicas: 1
  strategy:
    rollingUpdate:
      maxSurge: 25%
      maxUnavailable: 25%
    type: RollingUpdate
  template:
    metadata:
      labels:
        app: taier
    spec:
      containers:
      - name: taier
        image: dtopensource/taier:2.0
        resources:
          requests:
            cpu: 1
            memory: "1Gi"
          limits:
            cpu: 2
            memory: "4Gi"
        livenessProbe:
          tcpSocket:
            port: 8090
          initialDelaySeconds: 5
          timeoutSeconds: 5
          successThreshold: 1
          failureThreshold: 3
          periodSeconds: 10
        readinessProbe:
          tcpSocket:
            port: 8090
          initialDelaySeconds: 5
          timeoutSeconds: 2
          successThreshold: 1
          failureThreshold: 3
          periodSeconds: 10
        ports:
        - containerPort: 8090
          name: taier
        env:
          - name: NODE_ZKADDRESS
            value: zookeeper
          - name: MYSQL_IP
            value: mysql-svc
        volumeMounts:
          - mountPath: /var/tmp
            name: tmp-volume
          - mountPath: /opt/pluginLibs/
            name: plugin-volume
          - mountPath: /opt/datasourceX/
            name: datasourcex-volume
          - mountPath: /opt/logs/
            name: log-volume
          - mountPath: /opt/flink-lib/
            name: flink-lib
          - mountPath: /opt/chunjun-plugins/
            name: chunjun-plugins
      volumes:
        - hostPath:
            path: /opt/log/taier/logs
            type: DirectoryOrCreate
          name: log-volume
        - hostPath:
            path: /opt/tmp
            type: DirectoryOrCreate
          name: tmp-volume
        - hostPath:
            path: /app/cephfs/taier/datasourceX/
            type: DirectoryOrCreate
          name: datasourcex-volume
        - hostPath:
            path: /app/cephfs/taier/pluginLibs/
            type: DirectoryOrCreate
          name: plugin-volume
        - hostPath:
            path: /app/cephfs/taier/flink/flink113/flinklib/
            type: DirectoryOrCreate
          name: flink-lib
        - hostPath:
            path: /app/cephfs/taier/chunjun/chunjun112/syncplugin/
            type: DirectoryOrCreate
          name: chunjun-plugins
      restartPolicy: Always

$ kubectl apply -f taier.yaml
```

挂载参数说明：

- `chunjun-plugins`：chunjun 编译的插件；
- `flink-lib`: flink 的 lib 目录；
- `plugin-volume`: Taier plugins；
- `datasourcex-volume`: datasourcex 插件的目录；

## 4. 配置代理
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


























