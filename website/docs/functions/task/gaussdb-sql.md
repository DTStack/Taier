---
title: GaussDB SQL
sidebar_label: GaussDB SQL
---

## 新建任务
进入"开发目录"菜单，点击"新建任务"按钮，并填写新建任务弹出框中的配置项，配置项说明：
1. 任务名称：需输入英文字母、数字、下划线组成，不超过64个字符
2. 任务类型：选择GaussDB SQL
3. 存储位置：在页面左侧的任务存储结构中的位置
4. 描述：长度不超过200个的任意字符
   点击"保存"，弹窗关闭，即完成了新建任务
## 编辑任务
任务创建好后，可以在代码编辑器中编写SQL语句
编写的SQL语句示例如下：
```sql
select * from table_test;
```
:::tip
查询结果最多只展示5000条数据
:::
:::caution
GaussDB SQL 依赖GaussDB数据源 运行GaussDB SQL前请确保对应[GaussDB](https://www.huaweicloud.com/product/gaussdb.html) 数据源已配置

:::