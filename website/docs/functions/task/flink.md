---
title: Flink Jar
sidebar_label: Flink Jar
---

### 新建任务
需补充如下信息： 
* 资源：基于原生Flink Java API编写的Jar包，需要用户提前上传（上传方式可参考资源管理）。
* mainClass：用户jar包的入口函数。
* 命令行参数：请输入对应MainClass的入口命令行参数，例如 String[] args。

![add-flink-jar](/img/readme/flink-jar.png)




---
title: Spark Jar
sidebar_label: Spark Jar
---

### 新建任务
交互方式与新建Flink Jar任务类似,Spark任务需引用的资源包，需提前经「资源管理」上传至平台。一个任务只能引用一个资源包。
，但还需补充如下信息：
* 资源：基于Spark的MapReduce编程接口（Java API或Scala API），并打为Jar包，提前将资源包通过「资源管理」模块上传至平台，之后创建Spark任务时引用此资源。
* mainClass：Jar包的入口类，格式为： org.apache.hadoop.examples 需填写完整类名
* 命令行参数：请输入对应MainClass的入口命令行参数，例如 String[] args。

  ``` 离线开发底层集成的Spark版本为2.1，需按照此版本的Spark API编写代码Spark类型任务支持编写Java或Scala代码 ```


