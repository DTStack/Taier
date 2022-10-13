---
title: 任务参数
sidebar_label: 任务参数
---

## 参数配置

### 概述

为使任务自动周期运行时能动态适配环境变化，taier提供了参数配置的功能，参数的应用场景十分广泛，例如同步任务中将增量数据写入Hive表的天分区，需要在分区填写栏支持系统变量。SQL任务中，需要将每天的数据写入新的分区，也需要引用变量

参数配置可分为**系统参数**和**自定义参数**

:::tip
系统参数是内置的，用户可在各任务类型中直接引用，当系统参数的格式或处理方式无法满足要求时，taier支持自定义参数
:::

### 系统参数

| 变量参数                | 变量含义                   | 日期格式       |
| ----------------------- | -------------------------- | -------------- |
| ${bdp.system.bizdate}   | 默认为计划运行日期的前一天 | yyyyMMdd       |
| ${bdp.system.bizdate2}  | 默认为计划运行日期的前一天 | yyyy-MM-dd     |
| ${bdp.system.cyctime}   | 计划时间                   | yyyyMMddHHmmss |
| ${bdp.system.premonth}  | 计划运行时间上个月         | yyyyMM         |
| ${bdp.system.currmonth} | 计划运行时间的本月         | yyyyMM         |
| ${bdp.system.runtime}   | 实际运行时间               | yyyyMMddHHmmss |

### 自定义参数

#### 定义

在代码中引用 `${key1}` 、`${key2}`， 然后在**任务参数->自定义参数配置**编辑框设置`key1=`,`key2=`

在对应的编辑框中填入对应的替换格式

#### 时间基准

在对自定义参数进行增减之前，需明确取值的时间基准，Taier支持2种基准线，在右侧的参数面板中分别使用`$[]`、` $()`
2种不同的括号，区分2种时间基准，下面举例说明：

实例的计划运行时间：2022-02-06 12:10:00 实例的实际运行时间：2022-02-06 12:13:31

#### 替换规则

| 基准线   | 引用方式 | 替换格式               | 输出结果            |
| -------- | -------- | ---------------------- | ------------------- |
| 计划时间 | $[]      | $[yyyy-MM-dd HH:mm:ss] | 2022-02-06 12:10:00 |
| 运行时间 | $()      | $(yyyy-MM-dd HH:mm:ss) | 2022-02-06 12:13:31 |

#### 时间操作

基于计划时间取值的时间增减如下，基于业务日期、运行时间的时间增减方式类似，不再列出

- 后N年：$[add_months(yyyyMMdd,12*N)]，输出yyyyMMdd
- 前N年：$[add_months(yyyyMMdd,-12*N)]，输出yyyyMMdd
- 后N月：$[add_months(yyyyMMdd,N)]，输出yyyyMMdd
- 前N月：$[add_months(yyyyMMdd,-N)]，输出yyyyMMdd
- 后N周：$[yyyyMMdd+7*N]，输出yyyyMMdd
- 前N周：$[yyyyMMdd-7*N]，输出yyyyMMdd
- 后N天：$[yyyyMMdd+N]，输出yyyyMMdd
- 前N天：$[yyyyMMdd-N]，输出yyyyMMdd
- 后N小时：$[hh24miss+N/24]，输出yyyyMMddHHmmss
- 前N小时：$[hh24miss-N/24]，输出yyyyMMddHHmmss
- 后N分钟：$[hh24miss+N/24/60]，输出yyyyMMddHHmmss
- 前N分钟：$[hh24miss-N/24/60]，输出yyyyMMddHHmmss

### 分隔符

在时间增减的基础上，可增加各时间元素之间的分隔符，如下例：
后N年：$[add_months(yyyyMMdd,12*N,-)]，输出yyyy-MM-dd，在完成日期加减后，后面可输入 `-` 其中的 `-`
表示各元素的分隔符，例如$[add_months(yyyyMMdd,12*N,-)]

:::tip
此规则适用于时间操作的所有格式
:::