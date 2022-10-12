---
title: Python
sidebar_label: Python
---

## 新建任务
进入"开发目录"菜单，点击"新建任务"按钮，并填写新建任务弹出框中的配置项，配置项说明：
1. 任务名称：需输入英文字母、数字、下划线组成，不超过 64 个字符
2. 任务类型：选择 Python
3. Python 版本支持 2.x 和 3.x，请按需选择
4. 存储位置：在页面左侧的任务存储结构中的位置
5. 描述：长度不超过 200 个的任意字符
6. 点击"确认"，弹窗关闭，即完成了新建任务
   
## 编辑任务
任务创建好后，可以在代码编辑器中编写 Python 脚本，请确保脚本语法符合您选择的 Python 版本，示例如下：
```python
#!/usr/bin/python
# Write Python 3 code in this online editor and run it.
print("Hello, World!");
```

```python
# Write Python 2 code in this online editor and run it.
print 'Hello World!'
```

## 运行任务
点击右上角运行按钮，运行任务
## 查看结果
任务下方日志中将打印运行状态，并给出运行日志，方便排查问题。
![add-source](/img/readme/python-log.png)

:::caution
Python 任务依赖控制台 Script 组件，运行 Python 任务前请确保对应组件配置正确
:::