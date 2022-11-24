-- supported ftp source and target on the sync
UPDATE dict
SET dict_value = '{
  "children": [
    {
      "children": [
        {
          "name": "syncModel",
          "type": "number",
          "title": "同步模式",
          "noStyle": true
        },
        {
          "bind": {
            "field": "sourceMap.sourceId",
            "transformer": "{{optionCollections.sourceMap_sourceId#find.type}}"
          },
          "name": "type",
          "type": "number",
          "title": "类型",
          "noStyle": true
        },
        {
          "widget": "select",
          "name": "sourceId",
          "type": "number",
          "title": "数据源",
          "required": true,
          "props": {
            "method": "get",
            "name": "sourceMap_sourceId",
            "transformer": "sourceIdOnReader",
            "optionsFromRequest": true,
            "placeholder": "请选择数据源",
            "url": "/taier/api/dataSource/manager/total"
          }
        },
        {
          "widget": "select",
          "hidden": [
            {
              "field": "form.sourceMap.type",
              "isNot": true,
              "value": "2,4"
            }
          ],
          "depends": [
            "sourceMap.sourceId"
          ],
          "name": "schema",
          "type": "number",
          "title": "schema",
          "props": {
            "method": "post",
            "name": "sourcemap_schema",
            "transformer": "table",
            "optionsFromRequest": true,
            "placeholder": "请选择 schema",
            "params": {
              "sourceId": "{{form#sourceMap.sourceId}}"
            },
            "url": "/taier/api/dataSource/addDs/getAllSchemas",
            "required": [
              "sourceId"
            ]
          }
        },
        {
          "widget": "SelectWithPreviewer",
          "hidden": [
            {
              "field": "form.sourceMap.type",
               "isNot": true,
              "value": "1,2,3,4,7,8,25,27,45,50"
            }
          ],
          "depends": [
            "sourceMap.sourceId",
            "sourceMap.schema"
          ],
          "name": "table",
          "type": "string",
          "title": "表名",
          "required": true,
          "props": {
            "method": "post",
            "name": "sourcemap_table",
            "transformer": "table",
            "optionsFromRequest": true,
           "placeholder": "请选择表名",
            "params": {
              "sourceId": "{{form#sourceMap.sourceId}}",
              "schema": "{{form#sourceMap.schema}}",
              "isRead": true,
              "isSys": false
            },
            "url": "/taier/api/dataSource/addDs/tablelist",
            "required": [
              "sourceId"
            ]
          }
        },
        {
          "widget": "select",
          "hidden": [
            {
              "field": "form.sourceMap.type",
              "isNot": true,
              "value": "1,2,3,4,25"
            },
            {
              "field": "form.sourceMap.syncModel",
              "isNot": true,
              "value": "1"
            }
          ],
          "depends": [
            "sourceMap.table"
          ],
          "name": "increColumn",
          "type": "string",
          "title": "增量标识字段",
          "required": true,
          "props": {
            "method": "post",
            "name": "sourcemap_increColumn",
            "transformer": "incrementColumn",
            "optionsFromRequest": true,
            "placeholder": "请选择增量标识字段",
            "params": {
              "sourceId": "{{form#sourceMap.sourceId}}",
              "schema": "{{form#sourceMap.schema}}",
              "tableName": "{{form#sourceMap.table}}"
            },
            "url": "/taier/api/task/getIncreColumn",
            "required": [
              "sourceId",
              "tableName"
            ]
          }
        },
        {
          "widget": "InputWithColumns",
          "hidden": [
            {
              "field": "form.sourceMap.type",
              "isNot": true,
              "value": "6,9"
            }
          ],
          "name": "path",
          "type": "string",
          "title": "路径",
          "rules": [
            {
              "required": true,
              "message": "请输入路径"
            },
            {
              "max": 200,
              "message": "路径不得超过200个字符！"
            }
          ],
          "props": {
            "placeholder": "例如: /rdos/batch"
          }
        },
        {
          "widget": "select",
          "hidden": [
            {
              "field": "form.sourceMap.type",
              "isNot": true,
              "value": "9"
            }
          ],
          "name": "fileType|FTP",
          "type": "string",
          "title": "解析方式",
          "initialValue": "txt",
          "required": true,
          "props": {
            "allowClear": false,
            "options": [
              {
                "label": "CSV",
                "value": "csv"
              },
              {
                "label": "Excel",
                "value": "excel"
              },
              {
                "label": "TXT",
                "value": "txt"
              }
            ],
            "placeholder": "请选择解析方式"
          }
        },
        {
          "widget": "select",
          "hidden": [
            {
              "field": "form.sourceMap.type",
              "isNot": true,
              "value": "8,9"
            }
          ],
          "name": "encoding",
          "type": "string",
          "title": "编码",
          "initialValue": "utf-8",
          "required": true,
          "props": {
            "allowClear": false,
            "options": [
              {
                "label": "utf-8",
                "value": "utf-8"
              },
              {
                "label": "gdb",
                "value": "gdb"
              }
            ],
            "placeholder": "请选择编码"
          }
        },
        {
          "hidden": [
            {
              "field": "form.sourceMap.type",
              "isNot": true,
              "value": "8"
            }
          ],
          "name": "startRowkey",
          "type": "string",
          "title": "开始行健",
          "props": {
            "placeholder": "请输入开始行健"
          }
        },
        {
          "hidden": [
            {
              "field": "form.sourceMap.type",
              "isNot": true,
              "value": "8"
            }
          ],
          "name": "endRowkey",
          "type": "string",
          "title": "结束行健",
          "props": {
            "placeholder": "请输入结束行健"
          }
        },
        {
          "widget": "radio",
          "hidden": [
            {
              "field": "form.sourceMap.type",
              "isNot": true,
              "value": "8"
            }
          ],
          "name": "isBinaryRowkey",
          "type": "string",
          "title": "行健二进制转换",
          "initialValue": "0",
          "props": {
            "options": [
              {
                "label": "FALSE",
                "value": "0"
              },
              {
                "label": "TRUE",
                "value": "1"
              }
            ]
          }
        },
        {
          "widget": "inputNumber",
          "hidden": [
            {
              "field": "form.sourceMap.type",
              "isNot": true,
              "value": "8"
            }
          ],
          "name": "scanCacheSize",
          "type": "string",
          "title": "每次RPC请求获取行数",
          "props": {
            "min": 0,
            "placeholder": "请输入大小, 默认为256",
            "suffix": "行"
          }
        },
        {
          "widget": "inputNumber",
          "hidden": [
            {
              "field": "form.sourceMap.type",
              "isNot": true,
              "value": "8"
            }
          ],
          "name": "scanBatchSize",
          "type": "string",
          "title": "每次RPC请求获取列数",
          "props": {
            "min": 0,
            "placeholder": "请输入大小, 默认为100",
            "suffix": "列"
          }
        },
        {
          "widget": "textarea",
          "hidden": [
            {
              "field": "form.sourceMap.type",
              "isNot": true,
              "value": "1,2,3,4,25"
            }
          ],
          "name": "where",
          "rules": [
            {
              "max": 1000,
              "message": "过滤语句不可超过1000个字符!"
            }
          ],
          "type": "string",
          "title": "数据过滤",
          "props": {
            "autoSize": {
              "minRows": 2,
              "maxRows": 6
            },
            "placeholder": "请参考相关SQL语法填写where过滤语句（不要填写where关键字）。该过滤语句通常用作增量同步"
          }
        },
        {
          "widget": "select",
          "hidden": [
            {
              "field": "form.sourceMap.type",
              "isNot": true,
              "value": "1,2,3,4,25"
            }
          ],
          "depends": [
            "sourceMap.table"
          ],
          "name": "split",
          "type": "string",
          "title": "切分键",
          "props": {
            "method": "post",
            "name": "sourcemap_split",
            "transformer": "split",
            "optionsFromRequest": true,
            "placeholder": "请选择切分键",
            "params": {
              "sourceId": "{{form#sourceMap.sourceId}}",
              "schema": "{{form#sourceMap.schema}}",
              "tableName": "{{form#sourceMap.table#toArray}}"
            },
            "url": "/taier/api/dataSource/addDs/columnForSyncopate",
            "required": [
              "sourceId",
              "tableName"
            ]
          }
        },
        {
          "widget": "select",
          "hidden": [
            {
              "field": "form.sourceMap.type",
              "isNot": true,
              "value": "6"
            }
          ],
          "name": "fileType",
          "type": "string",
          "title": "文件类型",
          "initialValue": "text",
          "required": true,
          "props": {
            "options": [
              {
                "label": "orc",
                "value": "orc"
              },
              {
                "label": "text",
                "value": "text"
              },
              {
                "label": "parquet",
                "value": "parquet"
              }
            ],
            "placeholder": "请选择文件类型"
          }
        },
        {
          "hidden": [
            {
              "field": "form.sourceMap.type",
              "isNot": true,
              "value": "6"
            },
            {
              "field": "form.sourceMap.fileType",
              "isNot": true,
              "value": "text"
            }
          ],
          "name": "fieldDelimiter",
          "type": "string",
          "title": "列分隔符",
          "props": {
            "placeholder": "若不填写，则默认为\\\\001"
          }
        },
        {
          "name": "fieldDelimiter|FTP",
          "type": "string",
          "title": "列分隔符",
          "required": true,
          "hidden": [
            {
              "field": "form.sourceMap.type",
              "isNot": true,
              "value": "9"
            }
          ],
          "initialValue": ",",
          "props": {
            "placeholder": "若不填写，则默认为,"
          }
        },
        {
          "widget": "autoComplete",
          "hidden": [
            {
              "field": "form.sourceMap.type",
              "isNot": true,
              "value": "7,27,45,50"
            }
          ],
          "depends": [
            "sourceMap.table"
          ],
          "name": "partition",
          "type": "string",
          "title": "分区",
          "props": {
            "method": "post",
            "name": "sourcemap_partition",
            "transformer": "table",
            "optionsFromRequest": true,
            "placeholder": "请填写分区信息",
            "params": {
              "sourceId": "{{form#sourceMap.sourceId}}",
              "tableName": "{{form#sourceMap.table}}"
            },
            "url": "/taier/api/dataSource/addDs/getHivePartitions",
            "required": [
              "sourceId",
              "tableName"
            ]
          }
        },
        {
          "widget": "select",
          "hidden": [
            {
              "field": "form.sourceMap.type",
              "isNot": true,
              "value": "11,33,46"
            }
          ],
          "depends": [
            "sourceMap.sourceId"
          ],
          "name": "index",
          "type": "string",
          "title": "index",
          "required": true,
          "props": {
            "method": "post",
            "name": "sourcemap_schema",
            "transformer": "table",
            "optionsFromRequest": true,
            "placeholder": "请选择index",
            "params": {
              "sourceId": "{{form#sourceMap.sourceId}}"
            },
            "url": "/taier/api/dataSource/addDs/getAllSchemas",
            "required": [
              "sourceId"
            ]
          }
        },
        {
          "widget": "select",
          "hidden": [
            {
              "field": "form.sourceMap.type",
              "isNot": true,
              "value": "11,33"
            }
          ],
          "depends": [
            "sourceMap.index"
          ],
          "name": "indexType",
          "type": "string",
          "title": "type",
          "required": true,
          "props": {
            "method": "post",
            "name": "sourcemap_table",
            "transformer": "table",
            "optionsFromRequest": true,
            "placeholder": "请选择indexType！",
            "params": {
              "sourceId": "{{form#sourceMap.sourceId}}",
              "schema": "{{form#sourceMap.schema}}",
              "isRead": true,
              "isSys": false
            },
            "url": "/taier/api/dataSource/addDs/tablelist",
            "required": [
              "sourceId",
              "schema"
            ]
          }
        },
        {
          "widget": "textarea",
          "hidden": [
            {
              "field": "form.sourceMap.type",
              "isNot": true,
              "value": "11,33,46"
            }
          ],
          "name": "query",
          "rules": [
            {
              "max": 1024,
              "message": "仅支持1-1024个任意字符"
            }
          ],
          "type": "string",
          "title": "query",
          "props": {
            "autoSize": {
              "minRows": 2,
              "maxRows": 6
            },
            "placeholder": "\\"match_all\\":{}\\""
          }
        },
        {
          "widget": "radio",
          "hidden": [
            {
              "field": "form.sourceMap.type",
              "isNot": true,
              "value": "9"
            }
          ],
          "name": "isFirstLineHeader",
          "type": "string",
          "title": "是否包含表头",
          "initialValue": false,
          "required": true,
          "props": {
            "options": [
              {
                "label": "是",
                "value": true
              },
              {
                "label": "否",
                "value": false
              }
            ]
          }
        },
        {
          "widget": "textarea",
          "hidden": [
            {
              "field": "form.sourceMap.sourceId",
              "value": "undefined"
            }
          ],
          "name": "extralConfig",
          "validator": "json",
          "type": "string",
          "title": "高级配置",
          "props": {
            "autoSize": {
              "minRows": 2,
              "maxRows": 6
            },
            "placeholder": "以JSON格式添加高级参数，例如对关系型数据库可配置fetchSize"
          }
        },
        {
          "hidden": true,
          "name": "column",
          "type": "string",
          "title": "列"
        }
      ],
      "name": "sourceMap",
      "type": "object",
      "title": "数据来源"
    },
    {
      "children": [
        {
          "bind": {
            "field": "targetMap.sourceId",
            "transformer": "{{optionCollections.targetmap_sourceId#find.type}}"
          },
          "name": "type",
          "type": "number",
          "title": "类型",
          "noStyle": true
        },
        {
          "widget": "select",
          "name": "sourceId",
          "type": "number",
          "title": "数据源",
          "required": true,
          "props": {
            "method": "get",
            "name": "targetmap_sourceId",
            "transformer": "sourceIdOnWriter",
            "optionsFromRequest": true,
            "placeholder": "请选择数据源",
            "url": "/taier/api/dataSource/manager/total"
          }
        },
        {
          "widget": "select",
          "hidden": [
            {
              "field": "form.targetMap.type",
              "isNot": true,
              "value": "2,4,64"
            }
          ],
          "depends": [
            "targetMap.sourceId"
          ],
          "name": "schema",
          "type": "number",
          "title": "schema",
          "props": {
            "method": "post",
            "name": "targetmap_schema",
            "transformer": "table",
            "optionsFromRequest": true,
            "placeholder": "请选择 schema",
            "params": {
              "sourceId": "{{form#targetMap.sourceId}}"
            },
            "url": "/taier/api/dataSource/addDs/getAllSchemas",
            "required": [
              "sourceId"
            ]
          }
        },
        {
          "widget": "SelectWithCreate",
          "hidden": [
            {
              "field": "form.targetMap.type",
              "isNot": true,
              "value": "1,2,3,4,7,8,25,27,45,50,64"
            }
          ],
          "depends": [
            "targetMap.sourceId",
            "targetMap.schema"
          ],
          "name": "table",
          "type": "string",
          "title": "表名",
          "required": true,
          "props": {
            "method": "post",
            "name": "targetmap_table",
            "transformer": "table",
            "optionsFromRequest": true,
            "placeholder": "请选择表名",
            "params": {
              "sourceId": "{{form#targetMap.sourceId}}",
              "schema": "{{form#targetMap.schema}}",
              "isRead": true,
              "isSys": false
            },
            "url": "/taier/api/dataSource/addDs/tablelist",
            "required": [
              "sourceId"
            ]
          }
        },
        {
          "widget": "autoComplete",
          "hidden": [
            {
              "field": "form.targetMap.type",
              "isNot": true,
              "value": "7,27,45,50"
            }
          ],
          "depends": [
            "targetMap.table"
          ],
          "name": "partition",
          "type": "string",
          "title": "分区",
          "props": {
            "method": "post",
            "name": "targetmap_partition",
            "transformer": "table",
            "optionsFromRequest": true,
            "placeholder": "请填写分区信息",
            "params": {
              "sourceId": "{{form#targetMap.sourceId}}",
              "tableName": "{{form#targetMap.table}}"
            },
            "url": "/taier/api/dataSource/addDs/getHivePartitions",
            "required": [
              "sourceId",
              "tableName"
            ]
          }
        },
        {
          "widget": "textarea",
          "hidden": [
            {
              "field": "form.targetMap.type",
              "isNot": true,
              "value": "1,2,3,4,25,64"
            }
          ],
          "depends": [
            "targetMap.type"
          ],
          "name": "preSql",
          "type": "string",
          "title": "导入前准备语句",
          "props": {
            "autoSize": {
              "minRows": 2,
              "maxRows": 6
            },
            "placeholder": "请输入导入数据前执行的 SQL 脚本"
          }
        },
        {
          "widget": "textarea",
          "hidden": [
            {
              "field": "form.targetMap.type",
              "isNot": true,
              "value": "1,2,3,4,25,64"
            }
          ],
          "depends": [
            "targetMap.type"
          ],
          "name": "postSql",
          "type": "string",
          "title": "导入后准备语句",
          "props": {
            "autoSize": {
              "minRows": 2,
              "maxRows": 6
            },
            "placeholder": "请输入导入数据后执行的 SQL 脚本"
          }
        },
        {
          "hidden": [
            {
              "field": "form.targetMap.type",
              "isNot": true,
              "value": "6,9"
            }
          ],
          "name": "path",
          "rules": [
            {
              "max": 200,
              "message": "路径不得超过200个字符！"
            }
          ],
          "type": "string",
          "title": "路径",
          "required": true,
          "props": {
            "placeholder": "例如: /app/batch"
          }
        },
        {
          "hidden": [
            {
              "field": "form.targetMap.type",
              "isNot": true,
              "value": "6"
            }
          ],
          "name": "fileName",
          "type": "string",
          "title": "文件名",
          "required": true,
          "props": {
            "placeholder": "请输入文件名"
          }
        },
        {
          "widget": "select",
          "hidden": [
            {
              "field": "form.targetMap.type",
              "isNot": true,
              "value": "6"
            }
          ],
          "name": "fileType",
          "type": "string",
          "title": "文件类型",
          "initialValue": "orc",
          "required": true,
          "props": {
            "options": [
              {
                "label": "orc",
                "value": "orc"
              },
              {
                "label": "text",
                "value": "text"
              },
              {
                "label": "parquet",
                "value": "parquet"
              }
            ],
            "placeholder": "请选择文件类型"
          }
        },
        {
          "hidden": [
            {
              "field": "form.targetMap.type",
              "isNot": true,
              "value": "6,9"
            }
          ],
          "name": "fieldDelimiter",
          "type": "string",
          "title": "列分隔符",
          "initialValue": ",",
          "props": {
            "placeholder": "例如: 目标为 hive 则分隔符为\\001"
          }
        },
        {
          "widget": "select",
          "hidden": [
            {
              "field": "form.targetMap.type",
              "isNot": true,
              "value": "6,8,9"
            }
          ],
          "name": "encoding",
          "type": "string",
          "title": "编码",
          "initialValue": "utf-8",
          "required": true,
          "props": {
            "allowClear": false,
            "options": [
              {
                "label": "utf-8",
                "value": "utf-8"
              },
              {
                "label": "gdb",
                "value": "gdb"
              }
            ],
            "placeholder": "请选择编码"
          }
        },
        {
          "widget": "radio",
          "hidden": [
            {
              "field": "form.targetMap.type",
              "isNot": true,
              "value": "2,4,6,7,9,25,27,45,50"
            }
          ],
          "depends": [
            "targetMap.sourceId"
          ],
          "name": "writeMode",
          "type": "string",
          "title": "写入模式",
          "required": true,
          "props": {
            "options": [
              {
                "label": "覆盖（Insert Overwrite）",
                "value": "replace"
              },
              {
                "label": "追加（Insert Into）",
                "value": "insert"
              }
            ]
          }
        },
        {
          "widget": "radio",
          "hidden": [
            {
              "field": "form.targetMap.type",
              "isNot": true,
              "value": "8"
            }
          ],
          "name": "nullMode",
          "type": "string",
          "title": "读取为空时的处理方式",
          "initialValue": "skip",
          "props": {
            "options": [
              {
                "label": "SKIP",
                "value": "skip"
              },
              {
                "label": "EMPTY",
                "value": "empty"
              }
            ]
          }
        },
        {
          "widget": "inputNumber",
          "hidden": [
            {
              "field": "form.targetMap.type",
              "isNot": true,
              "value": "8"
            }
          ],
          "name": "writeBufferSize",
          "type": "string",
          "title": "写入缓存大小",
          "props": {
            "placeholder": "请输入缓存大小",
            "suffix": "KB"
          }
        },
        {
          "widget": "select",
          "hidden": [
            {
              "field": "form.targetMap.type",
              "isNot": true,
              "value": "11,33,46"
            }
          ],
          "depends": [
            "targetMap.sourceId"
          ],
          "name": "index",
          "type": "string",
          "title": "index",
          "required": true,
          "props": {
            "method": "post",
            "name": "targetmap_schema",
            "transformer": "table",
            "optionsFromRequest": true,
            "placeholder": "请选择index",
            "params": {
              "sourceId": "{{form#targetMap.sourceId}}"
            },
            "url": "/taier/api/dataSource/addDs/getAllSchemas",
            "required": [
              "sourceId"
            ]
          }
        },
        {
          "widget": "select",
          "hidden": [
            {
              "field": "form.targetMap.type",
              "isNot": true,
              "value": "11,33"
            }
          ],
          "depends": [
            "targetMap.index"
          ],
          "name": "indexType",
          "type": "string",
          "title": "type",
          "required": true,
          "props": {
            "method": "post",
            "name": "targetmap_table",
            "transformer": "table",
            "optionsFromRequest": true,
            "placeholder": "请选择indexType！",
            "params": {
              "sourceId": "{{form#targetMap.sourceId}}",
              "schema": "{{form#targetMap.schema}}",
              "isRead": true,
              "isSys": false
            },
            "url": "/taier/api/dataSource/addDs/tablelist",
            "required": [
              "sourceId",
              "schema"
            ]
          }
        },
        {
          "widget": "inputNumber",
          "hidden": [
            {
              "field": "form.targetMap.type",
              "isNot": true,
              "value": "11,33,46"
            }
          ],
          "name": "bulkAction",
          "type": "number",
          "title": "bulkAction",
          "initialValue": 100,
          "required": true,
          "props": {
            "min": 1,
            "max": 200000,
            "precision": 0,
            "placeholder": "请输入 bulkAction"
          }
        },
        {
          "widget": "textarea",
          "hidden": [
            {
              "field": "form.targetMap.sourceId",
              "value": "undefined"
            }
          ],
          "name": "extralConfig",
          "validator": "json",
          "type": "string",
          "title": "高级配置",
          "props": {
            "autoSize": {
              "minRows": 2,
              "maxRows": 6
            },
            "placeholder": "以JSON格式添加高级参数，例如对关系型数据库可配置fetchSize"
          }
        },
        {
          "hidden": true,
          "name": "column",
          "type": "string",
          "title": "列"
        }
      ],
      "name": "targetMap",
      "type": "object",
      "title": "选择目标"
    },
    {
      "children": [
        {
          "widget": "KeyMap",
          "type": "any"
        }
      ],
      "name": "mapping",
      "type": "object",
      "title": "字段映射"
    },
    {
      "children": [
        {
          "widget": "autoComplete",
          "name": "speed",
          "type": "string",
          "title": "作业速率上限",
          "initialValue": "不限制传输速率",
          "required": true,
          "props": {
            "options": [
              {
                "value": "不限制传输速率"
              },
              {
                "value": "1"
              },
              {
                "value": "2"
              },
              {
                "value": "3"
              },
              {
                "value": "4"
              },
              {
                "value": "5"
              },
              {
                "value": "6"
              },
              {
                "value": "7"
              },
              {
                "value": "8"
              },
              {
                "value": "9"
              },
              {
                "value": "10"
              }
            ],
            "placeholder": "请选择作业速率上限",
            "suffix": "MB/s"
          }
        },
        {
          "widget": "autoComplete",
          "name": "channel",
          "type": "string",
          "title": "作业并发数",
          "initialValue": "1",
          "required": true,
          "props": {
            "options": [
              {
                "value": "1"
              },
              {
                "value": "2"
              },
              {
                "value": "3"
              },
              {
                "value": "4"
              },
              {
                "value": "5"
              }
            ],
            "placeholder": "请选择作业并发数"
          }
        },
        {
          "hidden": [
            {
              "field": "form.sourceMap.type",
              "isNot": true,
              "value": "1,2,3,4,8,19,22,24,25,28,29,31,32,35,36,40,53,54,61,71,73"
            },
            {
              "field": "form.targetMap.type",
              "isNot": true,
              "value": "1,2,3,4,7,8,10,19,22,24,25,27,28,29,31,32,35,36,40,53,54,61,71,73"
            }
          ],
          "name": "isRestore",
          "type": "boolean",
          "title": "断点续传"
        },
        {
          "widget": "select",
          "hidden": [
            {
              "field": "form.settingMap.isRestore",
              "value": "false,undefined"
            }
          ],
          "name": "restoreColumnName",
          "type": "string",
          "title": "标识字段",
          "required": true,
          "props": {
            "method": "post",
            "name": "settingmap_restore",
            "transformer": "restore",
            "optionsFromRequest": true,
            "placeholder": "请选择标识字段",
            "params": {
              "sourceId": "{{form#sourceMap.sourceId}}",
              "schema": "{{form#sourceMap.schema}}",
              "tableName": "{{form#sourceMap.table}}"
            },
            "url": "/taier/api/task/getIncreColumn",
            "required": [
              "sourceId",
              "tableName"
            ]
          }
        }
      ],
      "name": "settingMap",
      "type": "object",
      "title": "通道控制"
    }
  ],
  "type": "object"
}'
WHERE `type` = 17 and dict_code = ''SYNC'';