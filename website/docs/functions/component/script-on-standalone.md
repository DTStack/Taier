---
title: Script-On-Standalone 
sidebar_label: Script-On-Standalone
---

## 配置 Script-On-Standalone

Script-On-Standalone 计算组件用于运行 Shell、Python 等脚本任务,Script-On-Standalone不依赖任务前置组件

### 参数说明

| 参数项                    | 默认值                                   | 说明                                                  | 是否必填 |
| ------------------------ | ---------------------------------------  | ---------------------------------------------------- | ------- |
| execute.dir            |  /tmp/dir             | 临时文件目录                           | 是       |
| script.python2.path      | /data/miniconda2/bin/python2             | python2.x 二进制可执行文件地址                                | 是       |
| script.python3.path      | /data/miniconda3/bin/python3             | python3.x 二进制可执行文件地址                                 | 是       |

:::tip 
任务运行在Taier本地服务器，适合做简易的脚本任务测试
:::

:::caution 
若您有运行 Python 任务的需求，请确保 script.python2.path、script.python3.path 对应的二进制可执行文件存在，否则无法运行 Python 任务
:::