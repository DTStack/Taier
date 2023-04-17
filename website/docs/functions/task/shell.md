---
title: Shell
sidebar_label: Shell
---

## 注意事项
1. Shell 任务依赖控制台 Script 组件，运行 Shell 任务前请确保对应组件配置正确。Shell任务目前支持on yarn 或 standalone模式。on yarn 模式将任务运行在集群配置的Hadoop集群上。standalone模式将任务运行Taier部署的服务器本地。
2. 控制台Script支持同时配置on yarn 和 standalone 模式
3. Script任务的运行模式由任务环境参数中runMode值决定，而不是取决于控制台配置的yarn还是standalone组件

## 新建任务
进入"开发目录"菜单，点击"新建任务"按钮，并填写新建任务弹出框中的配置项，配置项说明：
1. 任务名称：需输入英文字母、数字、下划线组成，不超过 64 个字符
2. 任务类型：选择 Shell
3. 存储位置：在页面左侧的任务存储结构中的位置
4. 描述：长度不超过 200 个的任意字符
5. 点击"确认"，弹窗关闭，即完成了新建任务

## 编辑任务
任务创建好后，可以在代码编辑器中编写 Shell 脚本，示例如下：
```shell
#!/bin/bash
echo 'Hello World!'
```
## 运行任务
点击右上角运行按钮，运行任务
## 查看结果
任务下方日志中将打印运行状态，并给出运行日志，方便排查问题。
### on yarn
![add-source](/img/readme/script_yarn.png)
### standalone
![add-source](/img/readme/script_standalone.png)
