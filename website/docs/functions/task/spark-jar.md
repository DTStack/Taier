---
title: Spark Jar 
sidebar_label: Spark Jar
---

## 上传Jar包资源

进入"资源管理"菜单，点击"上传资源"按钮，将对应的Jar上传到`HDFS`目录

## 新建任务

进入"开发目录"菜单，点击"新建任务"按钮，并填写新建任务弹出框中的配置项，配置项说明：

1. 任务名称：需输入英文字母、数字、下划线组成，不超过64个字符。
2. 任务类型：选择Spark Jar。
3. 资源：选择对应的资源Jar 任务
4. mainClass：Jar资源的主类入口
5. 命令后入口：Jar入口类的参数信息 点击"保存"，弹窗关闭，即完成了新建任务。

## 运行任务

任务创建好后，可以通过补数据方式运行

:::caution 
Spark Jar 依赖控制台 Spark 组件配置，运行前确保Spark 组件配置正常配置  
Spark Jar 暂时不支持临时运行
:::