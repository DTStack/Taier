---
title: 数据同步
sidebar_label: 数据同步
---
数据同步 任务提供两种模式进行选择
## 向导模式
> 向导模式的特点是便捷、简单，可视化字段映射，快速完成同步任务配置，无需关心chunjun的json格式，但需要针对每种数据源进行适配开发  

### 新建任务
进入"开发目录"菜单，点击"新建任务"按钮，并填写新建任务弹出框中的配置项，配置项说明：
1. 任务名称：需输入英文字母、数字、下划线组成，不超过64个字符
2. 任务类型：选择数据同步
3. 存储位置：在页面左侧的任务存储结构中的位置
4. 描述：长度不超过200个的任意字符
   点击"保存"，弹窗关闭，即完成了新建任务

### 任务配置
数据同步任务的配置共分为5个步骤：
1. 选择数据来源：选择已配置的数据源，系统会读取其中的数据
2. 选择数据目标：选择已配置的数据源，系统会向其写入数据
3. 字段映射：配置数据来源与数据目标之间的字段映射关系，不同的数据类型在这步有不同的配置方法
4. 通道控制：控制数据同步的执行速度、错误数据的处理方式等
5. 预览保存：再次确认已配置的规则并保存

![add-source](/img/readme/sync.png)

## 脚本模式
> 脚本模式的特点是全能、高效，可深度调优，支持全部数据源，完全兼容chunjun的json格式   

### 任务配置
![add-source](/img/readme/sync-json.png)

## 环境参数
```properties
## 任务运行方式：
## per_job:单独为任务创建flink yarn session，适用于低频率，大数据量同步
## session：多个任务共用一个flink yarn session，适用于高频率、小数据量同步，默认per_job
## flinkTaskRunMode=per_job
## per_job模式下jobManager配置的内存大小，默认1024（单位M)
## jobmanager.memory.mb=1024
## per_job模式下taskManager配置的内存大小，默认1024（单位M）
## taskmanager.memory.mb=1024
## per_job模式下每个taskManager 对应 slot的数量
## slots=1
## checkpoint保存时间间隔
## flink.checkpoint.interval=300000
## 任务优先级, 范围:1-1000
## job.priority=10
```

:::tip
右侧任务参数有数据同步的默认参数信息 可以手动调整数据同步的运行模式以及slot数量等参数   
数据同步同步任务默认为session模式
:::



## 向导模式支持的数据源
### 数据源
* MySQL
* ORACLE
* POSTGRESQL
* HIVE
* SPARK THRIFT

### 写入源
* MySQL
* ORACLE
* POSTGRESQL
* HIVE
* SPARK THRIFT



:::caution
数据同步 依赖控制台 Flink组件 运行数据同步前请确保对应组件配置正确
:::