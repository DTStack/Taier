---
title: DataX 
sidebar_label: DataX
---

## 注意事项

DataX 任务依赖控制台 DataX 组件，运行 DataX 任务前请确保对应组件配置正确,DataX任务运行Taier部署的服务器本地。

## 新建任务

进入"开发目录"菜单，点击"新建任务"按钮，并填写新建任务弹出框中的配置项，配置项说明：

1. 任务名称：需输入英文字母、数字、下划线组成，不超过 64 个字符
2. 任务类型：选择 DataX
3. 存储位置：在页面左侧的任务存储结构中的位置
4. 描述：长度不超过 200 个的任意字符
5. 点击"确认"，弹窗关闭，即完成了新建任务

## 编辑任务

任务创建好后，可以在代码编辑器中填写DataX脚本，示例如下：

```shell
{
    "job":{
        "content":[
            {
                "reader":{
                    "name":"mysqlreader",
                    "parameter":{
                        "column":[
                            "id",
                            "name"
                        ],
                        "connection":[
                            {
                                "jdbcUrl":[
                                    "jdbc:mysql://127.0.0.1:3306/dq"
                                ],
                                "table":[
                                    "table1"
                                ]
                            }
                        ],
                        "password":"123456",
                        "username":"root"
                    }
                },
                "writer":{
                    "name":"mysqlwriter",
                    "parameter":{
                        "column":[
                            "id",
                            "name"
                        ],
                        "connection":[
                            {
                                "jdbcUrl":"jdbc:mysql://ip地址:端口/test",
                                "table":[
                                    "table2"
                                ]
                            }
                        ],
                        "password":"123456",
                        "username":"root"
                    }
                }
            }
        ],
        "setting":{
            "speed":{
                "channel":"1"
            }
        }
    }
}
```

## 运行任务

点击右上角运行按钮，运行任务

## 查看结果

任务下方日志中将打印运行状态，并给出运行日志，方便排查问题
