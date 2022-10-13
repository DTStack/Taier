---
title: YARN
sidebar_label: YARN
---

## 配置YARN

|文件|是否必须|
|----|----|
|core-site.xml|是|
|hdfs-site.xml|是|
|yarn-site.xml|是|
|hive-site.xml|否|

已配置前置组件

- [x] SFTP

将以上xml文件压缩成zip(`zip文件中不包含层级目录`) 上传之后保存即可

![YARN 配置](/img/readme/yarn.png)

:::tip
不同厂商的hadoop集群依赖jar包和参数不一样，可以根据选择的YARN 厂商版本去`自行开发适配`
目前来说 可以根据YARN的版本来选择 `Apache Hadoop2` 或 `Apache Hadoop3`
:::