---
title: Flink SQL
sidebar_label: Flink SQL
---

Flink SQL 任务提供两种模式进行选择

## 向导模式

> 提供向导式的开发引导，通过可视化的填写和下一步的引导，帮助快速完成数据任务的配置工作。学习成本低，但无法使用到一些高级功能

### 新建任务

进入"开发目录"菜单，点击"新建任务"按钮，并填写新建任务弹出框中的配置项，配置项说明：

1. 任务名称：需输入英文字母、数字、下划线组成，不超过64个字符
2. 任务类型：选择Flink SQL
3. 存储位置：在页面左侧的任务存储结构中的位置
4. 描述：长度不超过200个的任意字符
5. 点击"保存"，弹窗关闭，即完成了新建任务

### 配置源表

1. 点击添加源表，若需要添加多路`Kafka`作为输入时，可继续在下面的模块点击添加
2. 选择数据源类型：目前只支持`Kafka`
3. 选择`Kafka Topic`
4. 映射表：由`Kafka Topi`c内的数据映射到`Flink`中的`表`，需在此输入`Flink`的表名,从而在`Flink`中以SQL的形式处理数据
5. 时间特征：Flink分为`ProcTime`和 `EventTime`两种时间特征  
   :::tip
   ProcTime:处理时间指执行对应Operation的设备的系统时间  
   EventTime:事件时间是每个单独事件在它的生产设备上发生的时间，若选择了EventTime，则还需补充时间列、偏移量和时区信息，这是Flink
   Watermark机制的要求

> 时间列必须是映射表中已声明的一列（当前仅支持为Timestamp类型），含义是基于该列生成Watermark，并且标识该列为Event
> Time列，可以在后续Query中用来定义窗口  
> 偏移量单位为毫秒，含义为Watermark值与Event time值的偏移量。通常一条记录中的某个字段就代表了该记录的发生时间  
> 通过配置作业的时区调整时间类型数据的输出结果。默认时区为东八区（Asia/Shanghai)  
:::

6. 并行度：算子的并发数，指的是Flink集群的Task Slot的数量

![add-source](/img/readme/flink-sql-source-table.png)

### 配置结果表

1. 点击 添加结果表 ，若需要添加多路输出时，可继续在下面的模块点击 添加结果表
2. 选择存储类型：目前可选择`MySQL`、`HBase`、`ElasticSearch`  
   :::tip
   若选择了MySQL，需选择MySQL中的一张表  
   若选择了HBase，需选择HBase中的一张表及rowkey  
   若选择了ElasticSearch，需选择ElasticSearch中的索引、id  
   :::
3. 映射表：由`Kafka Topic`内的数据映射到Flink中的`表`，需在此输入Flink的表名,从而在Flink中以SQL的形式处理数据
4. 字段信息：即Flink中此表对应的字段信息和类型。输入模式为 ` <源表字段名><字段类型>AS <源表映射字段名>` ，多字段信息通过回车进行分割
5. 并行度：算子的并发数，指的是Flink集群的Task Slot的数量
6. 数据输出时间:结果表输出数据的时间间隔，任务运行后每满足指定时间间隔就输出一次数据
7. 数据输出条数:结果表输出数据的条数间隔，任务运行后每满足指定条数就输出一次数据

![add-source](/img/readme/flink-sql-sink-table.png)

### 编写SQL

1. 编辑sql 保存，可至任务运维页面进行任务操作

![add-source](/img/readme/flink-sql.png)

## 脚本模式

> 通过直接编写SQL脚本来完成数据开发，适合高级用户，学习成本较高。脚本模式可以提供更丰富灵活的能力，做精细化的配置管理

### 脚本示例

```sql
CREATE TABLE source
(
    id        INT,
    name      STRING,
    money     DECIMAL(32, 2),
    dateone   timestamp,
    age       bigint,
    datethree timestamp,
    datesix   timestamp(6),
    datenigth timestamp(9),
    dtdate    date,
    dttime    time
) WITH (
      'connector' = 'stream-x',
      'number-of-rows' = '10', -- 输入条数，默认无限
      'rows-per-second' = '1' -- 每秒输入条数，默认不限制
      );

CREATE TABLE sink
(
    id        INT,
    name      STRING,
    money     DECIMAL(32, 2),
    dateone   timestamp,
    age       bigint,
    datethree timestamp,
    datesix   timestamp(6),
    datenigth timestamp(9),
    dtdate    date,
    dttime    time
) WITH (
      'connector' = 'stream-x',
      'print' = 'true'
      );

insert into sink
select *
from source;
```

:::tip
脚本模式sql语法 请参考 [**chunjun**](https://github.com/DTStack/chunjun/blob/master/docs/quickstart.md)
:::

## 语法校验

在任务提交运行前 检验sql语法的正确性

![add-source](/img/readme/flink-sql-grammy-check.png)

## 模式切换

当向导模式满足不了需求的时候 可以转换为脚本模式

![add-source](/img/readme/flink-script-convert.png)
![add-source](/img/readme/flink-script-convert-result.png)

## 任务运维

在实时运维中心，可以进行提交、停止、续跑等操作
:::tip
续跑：分为两种情况  
1.通过指定文件恢复并续跑  
2.选择 CheckPoint或SavePoint 续跑
> 在任务运行时会根据环境参数中execution.checkpointing.interval保存CheckPoint
:::

## 数据源支持

目前向导模式仅支持  
源表：`kafka`  
结果表: `hbase`、`mysql` 、`es`  
维表：`mysql`

:::caution
Flink SQL 依赖控制台 Flink组件 运行数据同步前请确保对应组件配置正确
:::