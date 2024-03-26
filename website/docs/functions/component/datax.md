---
title: DataX 
sidebar_label: DataX
---

## 配置 DataX

DataX 计算组件用于运行 DataX 脚本任务,DataX不依赖任务前置组件

### 参数说明

| 参数项                    | 默认值                                   | 说明                                                  | 是否必填 |
| ------------------------ | ---------------------------------------  | ---------------------------------------------------- | ------- |
| DATAX.local.path            |               | DataX插件路径                           | 是       |
| DATAX.task.temp      |              | DataX 脚本临时目录                                    | 是       |
| execute.dir      |             | 执行目录文件夹                                | 是       |
| DATAX.python.path      |             | python 二进制可执行文件地址                                 | 是       |

:::tip 
DataX任务运行在Taier本地服务器,通过Taier来执行，并获取执行结果信息

:::

:::caution 
DataX任务内容为DataX的运行脚本
:::