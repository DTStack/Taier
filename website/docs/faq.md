---
title: FAQ
sidebar_label: FAQ
---


## FAQ

### Q: 项目依赖dt-insight-hive-shade 下载不下来
A: maven 仓库地址可以下载 https://repo1.maven.org/maven2/com/dtstack/

### Q: datasource.plugin.path怎么配置
A: https://github.com/DTStack/DatasourceX/releases/download/v4.3.0/datasourceX.zip 直接下载 解压配置即可

### Q: 集群配置 yarn hadoop版本没有对应的厂商或版本怎么办
A: 优先选择厂商，大版本相近的 区分厂商和版本是为了后续添加不同hadoop版本适配参数，目前版本无影响。如果没有对应的 选择apache hadoop即可

### Q: flink sql 支持吗
A: 支持，使用问题可以提issue

### Q: 集群配置测试连通性失败，提示can not found client
A: 确认Taier目录下 是否有pluginLibs目录


### Q: 集群绑定租户失败，提示client exception
A: 确认Taier配置文件datasource.plugin.path 对应配置路径是否有datasourcex文件目录

### Q: 添加数据源失败
A: 确认Taier目录下application.properties datasource.plugin.path是否配置正确

### Q: 获取目录失败,请联系管理员
A: 是否正确配置集群，集群是否正确获取到了队列，租户和集群是否绑定成功 参考[快速上手](./quickstart/start.md)

### Q: 页面访问报错, 无法登陆用户
A: 确认下前端配置的后端接口是否正确 参考[快速上手](./quickstart/deploy/web.md)

### Q:绑定新租户时初始化时提示数据库已存在
A: 绑定租户新增schema选择创建 绑定租户已有schema直接选择对接 

### Q:spark sql任务执行提示class not found
A: 编译完对应的spark210插件 需要将对应的sqlProxy的jar包 放到对应集群spark组件下sparkSqlProxyPath路径下

### Q:spark sql任务执行提示sftp downloadDir error
A: 确认下sftp组件配置是否正确 sftp组件配置路径 + confPath + 集群名称 是否有对应的xml配置文件


