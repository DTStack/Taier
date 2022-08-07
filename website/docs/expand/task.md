---
title: 任务
sidebar_label: 任务
---
# 任务
## 自定义开发任务

### 定义任务
在 `com.dtstack.taier.common.enums.EScheduleJobType` 枚举中定义一个新的任务类型

枚举中有对应的六个属性:  

| 属性   | 含义 | 能否为空 | 
| --------- | ------- | ----|
| type      |  任务类型值| 否|
| name      |  任务名 | 否|
| engineJobType      | EJobType|是
| sort     | 排序 |否
| componentType     | 任务执行依赖组件 |是
| computeType     | 批处理任务还是流处理任务 |否


:::caution
engineJobType主要用于同一个插件下，不同任务类型的支持  
如flink的插件支持flink sql 、flink jar 和 sync三种类型任务
主要通过engineJobType来区分
:::



这里我们以oceanBase为例:
如:
```java
OCEANBASE_SQL(8, "OceanBaseSQL", EJobType.SQL.getType(), 4, EComponentType.OCEAN_BASE, EComputeType.BATCH)
```


配置含义:
:::info
 任务名称为 OceanBaseSQL  
 是SQL类型任务，任务执行依赖控制台OceanBase组件的配置参数  
 为离线类型任务  
:::

新增完任务类型，我们在Taier里新增任务的时候 就会有对应的类型选择

:::tip
默认新增任务类型都会有`任务属性`,`调度依赖`,`任务参数`,`环境参数`等模块
:::

### 保存任务
在taier页面上配置好对应的属性参数和任务内容之后，点击保存即可  
默认会对sqlText内容去除注释
:::tip
如果需要对任务保存做自定义的参数拼接或处理   
可以基于 `com.dtstack.taier.develop.service.develop.ITaskSaver` 扩展自身逻辑
:::

:::caution
如果没有前端代码的开发能力，可以在Taier的任务界面通过json的格式来定义任务内容，后续寻找相应的前端同学一起完善向导模式的页面配置
:::

### 界面运行

任务配置好之后点击界面运行  
界面运行的流程主要涉及 `com.dtstack.taier.develop.service.develop.ITaskRunner`实现类
1. startSqlImmediately 运行任务
2. selectStatus 获取任务执行状态
3. selectData 任务执行完成之后获取数据
4. runLog 获取执行日志

如果是sql类型的任务 还需要实现`com.dtstack.taier.develop.service.develop.IJdbcService`实现类  
实现和`datasourcex`的插件对接

### 调度运行
任务提交会调用`com.dtstack.taier.develop.service.develop.ITaskRunner.readyForSyncImmediatelyJob`的方法提交参数  

调度运行需要依赖taier-worker下的plugins插件，在调度运行的时候需要确定任务由那个plugins去执行
`com.dtstack.taier.scheduler.service.ClusterService.pluginInfoJSON`  

会根据上面任务配置的对应的组件去获取pluginInfoStrategy
```java
  private ComponentPluginInfoStrategy convertPluginInfo(EComponentType componentType) {
      switch (componentType) {
          case FLINK:
              return new FlinkPluginInfoStrategy();
          case SPARK:
              return new SparkPluginInfoStrategy();
          case HIVE_SERVER:
              return new HivePluginInfoStrategy();
          default:
              return new DefaultPluginInfoStrategy(componentType);
      }
  }
```
根据组件拼接的任务pluginInfo会包含插件名称  
如oceanBase: 
```json
{
 "typeName":"oceanBase",
 "jdbcUrl":"",
 "username":"",
 "password":""
}
```
:::tip
typeName统一标识为插件名称， 会根据typeName去加载插件执行
:::

### 插件开发

插件开发需要实现`com.dtstack.taier.pluginapi.client.IClient`下对应的方法，用来完成任务的调度执行
```java
  //插件初始化
  void init(Properties prop) throws Exception;

  //执行任务
  JobResult submitJob(JobClient jobClient);
  
  //取消任务
  JobResult cancelJob(JobIdentifier jobIdentifier);

  //获取任务状态
  TaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException;
  
  //获取任务日志
  String getJobLog(JobIdentifier jobIdentifier);
  
  //判断任务能否执行
  JudgeResult judgeSlots(JobClient jobClient);
```
:::tip
IClient需要实现META-INF.services 才能加载到插件  
taier-plugins的插件在开发SQL内容的时候，大部分内容会和datasourcex的内容重复，1.3版本将会对重复插件进行融合
:::



