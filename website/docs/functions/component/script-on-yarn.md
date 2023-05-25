---
title: Script-On-Yarn 
sidebar_label: Script-On-Yarn
---

## 配置 Script

已配置前置组件

- [x] SFTP
- [x] YARN
- [x] HDFS

Script-On-Yarn 计算组件用于运行 Shell、Python 等脚本任务， 若您有运行脚本任务的需求，请确保 Script-On-Yarn 组件正确配置

### 参数说明

| 参数项                    | 默认值                                   | 说明                                                  | 是否必填 |
| ------------------------ | ---------------------------------------  | ---------------------------------------------------- | ------- |
| script.java.opts         | -Dfile.encoding=UTF-8                    | script container jvm 扩展参数                         | 是       |
| script.am.memory         | 512m                                     | am container 使用的内存量                              | 是      |
| script.am.cores          | 1                                        | am container 使用的 cpu 核数                           | 是      |
| script.worker.memory     | 512m                                     | work container 使用的内存量                            | 是      |
| script.worker.cores      | 1                                        | work container 使用的 cpu 核数                         | 是      |
| script.worker.num        | 1                                        | work container 实例数量                                | 是       |
| container.staging.dir    | /insight/script/staging                  | 任务临时文件路径                                        | 是       |
| script.container.heartbeat.interval           | 10000               | am 和 work 之间的心跳间隔，单位毫秒                      | 是      |
| script.container.heartbeat.timeout            |  120000             | am 和 work 之间的心跳超时时间，单位毫秒                           | 是       |
| script.python2.path      | /data/miniconda2/bin/python2             | python2.x 二进制可执行文件地址                                | 是       |
| script.python3.path      | /data/miniconda3/bin/python3             | python3.x 二进制可执行文件地址                                 | 是       |

:::tip 任务运行在Hadoop的计算节点上，适合复杂的脚本和大量的任务
:::

:::caution 若您有运行 Python 任务的需求，请确保 script.python2.path、script.python3.path 对应的二进制可执行文件存在，否则无法运行 Python 任务
:::