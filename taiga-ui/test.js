{
  "swagger": "2.0",
  "info": {
    "version": "1.0.0",
    "title": "DAGScheduleX接口文档",
    "contact": {
      "name": "DAGScheduleX",
      "email": "engine@dtstack.com"
    }
  },
  "host": "172.16.101.187:8090",
  "basePath": "/",
  "tags": [
    {
      "name": "上传接口",
      "description": "Upload Controller"
    },
    {
      "name": "下载接口",
      "description": "Download Controller"
    },
    {
      "name": "任务依赖管理",
      "description": "Batch Task Task Controller"
    },
    {
      "name": "任务实例管理",
      "description": "Batch Job Controller"
    },
    {
      "name": "任务管理",
      "description": "Batch Task Controller"
    },
    {
      "name": "函数管理",
      "description": "Batch Function Controller"
    },
    {
      "name": "执行选中的sql或者脚本",
      "description": "Batch Select Sql Controller"
    },
    {
      "name": "控制台接口",
      "description": "Console Controller"
    },
    {
      "name": "数据源中心-数据源管理",
      "description": "Data Source Controller"
    },
    {
      "name": "数据源中心-数据源表单模版化",
      "description": "Datasource Form Controller"
    },
    {
      "name": "数据源中心-新增数据源",
      "description": "Add Datasource Controller"
    },
    {
      "name": "日志管理",
      "description": "Batch Server Log Controller"
    },
    {
      "name": "用户接口",
      "description": "User Controller"
    },
    {
      "name": "目录管理",
      "description": "Batch Catalogue Controller"
    },
    {
      "name": "租户接口",
      "description": "Tenant Controller"
    },
    {
      "name": "组件接口",
      "description": "Component Controller"
    },
    {
      "name": "读写锁",
      "description": "Read Write Lock Controller"
    },
    {
      "name": "资源任务管理",
      "description": "Batch Task Resource Controller"
    },
    {
      "name": "资源管理",
      "description": "Batch Resource Controller"
    },
    {
      "name": "运维中心---任务依赖相关接口",
      "description": "Operation Schedule Task Task Controller"
    },
    {
      "name": "运维中心---任务动作相关接口",
      "description": "Operation Action Controller"
    },
    {
      "name": "运维中心---任务相关接口",
      "description": "Operation Schedule Task Controller"
    },
    {
      "name": "运维中心---周期实例依赖关系相关接口",
      "description": "Operation Schedule Job Job Controller"
    },
    {
      "name": "运维中心---周期实例相关接口",
      "description": "Operation Schedule Job Controller"
    },
    {
      "name": "运维中心---补数据相关接口",
      "description": "Operation Fill Data Job Controller"
    },
    {
      "name": "集群接口",
      "description": "Cluster Controller"
    },
    {
      "name": "集群组件信息管理",
      "description": "Batch Component Controller"
    }
  ],
  "paths": {
    "/api/rdos/batch/batchCatalogue/addCatalogue": {
      "post": {
        "tags": [
          "目录管理"
        ],
        "summary": "新增目录",
        "operationId": "addCatalogueUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/目录添加信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«目录结果信息»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchCatalogue/deleteCatalogue": {
      "post": {
        "tags": [
          "目录管理"
        ],
        "summary": "删除目录",
        "operationId": "deleteCatalogueUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/目录添加信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«Void»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchCatalogue/getCatalogue": {
      "post": {
        "tags": [
          "目录管理"
        ],
        "summary": "获取目录",
        "operationId": "getCatalogueUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/目录获取信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«目录结果信息»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchCatalogue/updateCatalogue": {
      "post": {
        "tags": [
          "目录管理"
        ],
        "summary": "更新目录",
        "operationId": "updateCatalogueUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/目录更新信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«Void»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchFunction/addOrUpdateFunction": {
      "post": {
        "tags": [
          "函数管理"
        ],
        "summary": "添加函数 or 修改函数",
        "operationId": "addOrUpdateFunctionUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/函数添加信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«函数结果信息»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchFunction/deleteFunction": {
      "post": {
        "tags": [
          "函数管理"
        ],
        "summary": "删除函数",
        "operationId": "deleteFunctionUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/函数删除信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«Void»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchFunction/getAllFunctionName": {
      "post": {
        "tags": [
          "函数管理"
        ],
        "summary": "获取所有函数名",
        "operationId": "getAllFunctionNameUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/函数名称信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«string»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchFunction/getFunction": {
      "post": {
        "tags": [
          "函数管理"
        ],
        "summary": "获取函数",
        "operationId": "getFunctionUsingPOST_1",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/函数基础信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«函数查询结果信息»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchFunction/moveFunction": {
      "post": {
        "tags": [
          "函数管理"
        ],
        "summary": "移动函数",
        "operationId": "moveFunctionUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/函数移动信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«Void»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchJob/getSyncTaskStatus": {
      "post": {
        "tags": [
          "任务实例管理"
        ],
        "summary": "获取同步任务运行状态",
        "operationId": "getSyncTaskStatusUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/同步任务信息相关"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«获取同步任务运行状态返回信息»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchJob/startSqlImmediately": {
      "post": {
        "tags": [
          "任务实例管理"
        ],
        "summary": "运行sql",
        "operationId": "startSqlImmediatelyUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/实例运行sql信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«运行sql返回信息»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchJob/startSyncImmediately": {
      "post": {
        "tags": [
          "任务实例管理"
        ],
        "summary": "运行同步任务",
        "operationId": "startSyncImmediatelyUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/运行同步任务"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«运行同步任务返回信息»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchJob/stopSqlImmediately": {
      "post": {
        "tags": [
          "任务实例管理"
        ],
        "summary": "停止通过sql任务执行的sql查询语句",
        "operationId": "stopSqlImmediatelyUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/同步任务信息相关"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«Void»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchJob/stopSyncJob": {
      "post": {
        "tags": [
          "任务实例管理"
        ],
        "summary": "停止同步任务",
        "operationId": "stopSyncJobUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/同步任务信息相关"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«Void»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchResource/addResource": {
      "post": {
        "tags": [
          "资源管理"
        ],
        "summary": "添加资源",
        "operationId": "addResourceUsingPOST",
        "consumes": [
          "multipart/form-data"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "computeType",
            "in": "query",
            "description": "计算类型 0实时，1 离线",
            "required": false,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "createUserId",
            "in": "query",
            "description": "新建资源的用户ID",
            "required": true,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "file",
            "in": "formData",
            "description": "file",
            "required": false,
            "type": "file"
          },
          {
            "name": "id",
            "in": "query",
            "description": "资源ID",
            "required": false,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "modifyUserId",
            "in": "query",
            "description": "修改资源的用户ID",
            "required": true,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "nodePid",
            "in": "query",
            "description": "资源存放的目录ID",
            "required": false,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "productCode",
            "in": "query",
            "description": "项目代号",
            "required": false,
            "type": "string"
          },
          {
            "name": "resourceDesc",
            "in": "query",
            "description": "资源描述",
            "required": false,
            "type": "string"
          },
          {
            "name": "resourceName",
            "in": "query",
            "description": "资源名称",
            "required": false,
            "type": "string"
          },
          {
            "name": "resourceType",
            "in": "query",
            "description": "资源类型",
            "required": true,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "tenantId",
            "in": "query",
            "description": "租户ID",
            "required": false,
            "type": "integer",
            "format": "int64",
            "x-example": 1
          },
          {
            "name": "userId",
            "in": "query",
            "description": "用户ID",
            "required": false,
            "type": "integer",
            "format": "int64",
            "x-example": 1
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«目录结果信息»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchResource/deleteResource": {
      "post": {
        "tags": [
          "资源管理"
        ],
        "summary": "删除资源",
        "operationId": "deleteResourceUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "batchResourceBaseVO",
            "description": "batchResourceBaseVO",
            "required": false,
            "schema": {
              "$ref": "#/definitions/资源基础信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«long»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchResource/getResourceById": {
      "post": {
        "tags": [
          "资源管理"
        ],
        "summary": "获取资源详情",
        "operationId": "getResourceByIdUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "batchResourceBaseVO",
            "description": "batchResourceBaseVO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/资源基础信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/BatchResourceVO"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchResource/replaceResource": {
      "post": {
        "tags": [
          "资源管理"
        ],
        "summary": "替换资源",
        "operationId": "replaceResourceUsingPOST",
        "consumes": [
          "multipart/form-data"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "computeType",
            "in": "query",
            "description": "计算类型 0实时，1 离线",
            "required": false,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "createUserId",
            "in": "query",
            "description": "新建资源的用户ID",
            "required": true,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "file",
            "in": "formData",
            "description": "file",
            "required": false,
            "type": "file"
          },
          {
            "name": "id",
            "in": "query",
            "description": "资源ID",
            "required": false,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "modifyUserId",
            "in": "query",
            "description": "修改资源的用户ID",
            "required": true,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "nodePid",
            "in": "query",
            "description": "资源存放的目录ID",
            "required": false,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "productCode",
            "in": "query",
            "description": "项目代号",
            "required": false,
            "type": "string"
          },
          {
            "name": "resourceDesc",
            "in": "query",
            "description": "资源描述",
            "required": false,
            "type": "string"
          },
          {
            "name": "resourceName",
            "in": "query",
            "description": "资源名称",
            "required": false,
            "type": "string"
          },
          {
            "name": "resourceType",
            "in": "query",
            "description": "资源类型",
            "required": true,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "tenantId",
            "in": "query",
            "description": "租户ID",
            "required": false,
            "type": "integer",
            "format": "int64",
            "x-example": 1
          },
          {
            "name": "userId",
            "in": "query",
            "description": "用户ID",
            "required": false,
            "type": "integer",
            "format": "int64",
            "x-example": 1
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«Void»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchSelectSql/selectData": {
      "post": {
        "tags": [
          "执行选中的sql或者脚本"
        ],
        "summary": "获取执行结果",
        "operationId": "selectDataUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "sqlVO",
            "description": "sqlVO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/执行选中的sql或者脚本"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«sql或者脚本执行结果信息»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchSelectSql/selectRunLog": {
      "post": {
        "tags": [
          "执行选中的sql或者脚本"
        ],
        "summary": "获取执行日志",
        "operationId": "selectRunLogUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "sqlVO",
            "description": "sqlVO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/执行选中的sql或者脚本"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«sql或者脚本执行日志信息»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchSelectSql/selectStatus": {
      "post": {
        "tags": [
          "执行选中的sql或者脚本"
        ],
        "summary": "获取执行状态",
        "operationId": "selectStatusUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "sqlVO",
            "description": "sqlVO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/执行选中的sql或者脚本"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«sql或者脚本执行状态信息»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchServerLog/getLogsByAppId": {
      "post": {
        "tags": [
          "日志管理"
        ],
        "summary": "根据appId获取日志",
        "operationId": "getLogsByAppIdUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/根据appId获取日志信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«JSONObject»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchServerLog/getLogsByAppLogType": {
      "post": {
        "tags": [
          "日志管理"
        ],
        "summary": "根据类型获取日志",
        "operationId": "getLogsByAppLogTypeUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/根据类型获取日志"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«根据类型获取日志返回信息»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchServerLog/getLogsByJobId": {
      "post": {
        "tags": [
          "日志管理"
        ],
        "summary": "根据jobId获取日志",
        "operationId": "getLogsByJobIdUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/根据jobId获取日志信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«根据jobId获取日志结果信息»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchTask/addOrUpdateTask": {
      "post": {
        "tags": [
          "任务管理"
        ],
        "summary": "数据开发-新建/更新 任务",
        "operationId": "addOrUpdateTaskUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "paramVO",
            "description": "paramVO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/任务信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«任务目录信息»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchTask/allProductGlobalSearch": {
      "post": {
        "tags": [
          "任务管理"
        ],
        "summary": "所有产品的已提交任务查询",
        "operationId": "allProductGlobalSearchUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "allProductGlobalSearchVO",
            "description": "allProductGlobalSearchVO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/任务信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«返回可依赖的任务»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchTask/checkIsLoop": {
      "post": {
        "tags": [
          "任务管理"
        ],
        "summary": "检查task与依赖的task是否有构成有向环",
        "operationId": "checkIsLoopUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "infoVO",
            "description": "infoVO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/任务信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«任务信息»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchTask/checkName": {
      "post": {
        "tags": [
          "任务管理"
        ],
        "summary": "新增离线任务/脚本/资源/自定义脚本，校验名称",
        "operationId": "checkNameUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "detailVO",
            "description": "detailVO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/任务信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«Void»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchTask/deleteTask": {
      "post": {
        "tags": [
          "任务管理"
        ],
        "summary": "删除任务",
        "operationId": "deleteTaskUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "detailVO",
            "description": "detailVO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/任务信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«long»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchTask/forceUpdate": {
      "post": {
        "tags": [
          "任务管理"
        ],
        "summary": "覆盖更新",
        "operationId": "forceUpdateUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "paramVO",
            "description": "paramVO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/任务信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«任务目录信息»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchTask/getByName": {
      "post": {
        "tags": [
          "任务管理"
        ],
        "summary": "根据名称查询任务",
        "operationId": "getByNameUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "detailVO",
            "description": "detailVO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/任务信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«任务信息»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchTask/getChildTasks": {
      "post": {
        "tags": [
          "任务管理"
        ],
        "summary": "获取子任务",
        "operationId": "getChildTasksUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "tasksVO",
            "description": "tasksVO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/表字段信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«删除任务前置判断»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchTask/getComponentVersionByTaskType": {
      "post": {
        "tags": [
          "任务管理"
        ],
        "summary": "获取组件版本号",
        "operationId": "getComponentVersionByTaskTypeUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "getComponentVersionVO",
            "description": "getComponentVersionVO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/获取组件版本号"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«组件版本号»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchTask/getSysParams": {
      "post": {
        "tags": [
          "任务管理"
        ],
        "summary": "获取所有系统参数",
        "operationId": "getSysParamsUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«Collection«系统参数»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchTask/getTaskById": {
      "post": {
        "tags": [
          "任务管理"
        ],
        "summary": "数据开发-根据任务id，查询详情",
        "operationId": "getTaskByIdUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "batchScheduleTaskVO",
            "description": "batchScheduleTaskVO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/调度任务信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«任务信息»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchTask/getTaskVersionRecord": {
      "post": {
        "tags": [
          "任务管理"
        ],
        "summary": "获取任务版本",
        "operationId": "getTaskVersionRecordUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "detailVO",
            "description": "detailVO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/任务信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«任务版本详细信息»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchTask/guideToTemplate": {
      "post": {
        "tags": [
          "任务管理"
        ],
        "summary": "向导模式转模版",
        "operationId": "guideToTemplateUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "paramVO",
            "description": "paramVO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/任务信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«任务目录信息»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchTask/publishTask": {
      "post": {
        "tags": [
          "任务管理"
        ],
        "summary": "任务发布",
        "operationId": "publishTaskUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "detailVO",
            "description": "detailVO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/任务信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«发布信息信息»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchTask/taskVersionScheduleConf": {
      "post": {
        "tags": [
          "任务管理"
        ],
        "summary": "获取任务版本列表",
        "operationId": "taskVersionScheduleConfUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "detailVO",
            "description": "detailVO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/任务信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«任务版本详细信息»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchTask/trace": {
      "post": {
        "tags": [
          "任务管理"
        ],
        "summary": "追踪",
        "operationId": "traceUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/数据源追踪信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«JSONObject»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchTaskResource/getResources": {
      "post": {
        "tags": [
          "资源任务管理"
        ],
        "summary": "获得 资源-任务 列表",
        "operationId": "getResourcesUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/资源任务信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«资源信息»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/batchTaskTask/addOrUpdateTaskTask": {
      "post": {
        "tags": [
          "任务依赖管理"
        ],
        "summary": "添加或者修改任务依赖",
        "operationId": "addOrUpdateTaskTaskUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "taskVO",
            "description": "taskVO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/任务血缘关系信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«Void»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/batch/component/getAllDatabases": {
      "post": {
        "tags": [
          "集群组件信息管理"
        ],
        "summary": "获取所有的databases",
        "operationId": "getFunctionUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/集群组件database信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«string»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/common/readWriteLock/getLock": {
      "post": {
        "tags": [
          "读写锁"
        ],
        "summary": "获取锁",
        "operationId": "getLockUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "lockVO",
            "description": "lockVO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/读写锁信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«读写锁信息»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/api/rdos/common/readWriteLock/getReadWriteLock": {
      "post": {
        "tags": [
          "读写锁"
        ],
        "summary": "获取读写锁",
        "operationId": "getReadWriteLockUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "lockVO",
            "description": "lockVO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/读写锁信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«读写锁信息»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/action/batchStopJobs": {
      "post": {
        "tags": [
          "运维中心---任务动作相关接口"
        ],
        "summary": "批量停止任务",
        "operationId": "batchStopJobsUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "jobIds",
            "in": "query",
            "description": "选择的实例id",
            "required": true,
            "type": "ref"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«int»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/action/queryJobLog": {
      "post": {
        "tags": [
          "运维中心---任务动作相关接口"
        ],
        "summary": "查看实例日志",
        "operationId": "queryJobLogUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/QueryJobLogVO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«ReturnJobLogVO»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/action/restartJob": {
      "post": {
        "tags": [
          "运维中心---任务动作相关接口"
        ],
        "summary": "重跑任务",
        "operationId": "restartJobUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "jobIds",
            "in": "query",
            "description": "选择的实例id",
            "required": true,
            "type": "ref"
          },
          {
            "name": "restartType",
            "in": "query",
            "description": "重跑当前节点: RESTART_CURRENT_NODE(0)\n重跑及其下游: RESTART_CURRENT_AND_DOWNSTREAM_NODE(1)\n置成功并恢复调度:SET_SUCCESSFULLY_AND_RESUME_SCHEDULING(2)\n",
            "required": true,
            "type": "ref"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/action/status": {
      "post": {
        "tags": [
          "运维中心---任务动作相关接口"
        ],
        "summary": "查看实例状态",
        "operationId": "statusUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "jobId",
            "in": "query",
            "description": "实例id",
            "required": true,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«int»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/action/stopFillDataJobs": {
      "post": {
        "tags": [
          "运维中心---任务动作相关接口"
        ],
        "summary": "按照补数据停止任务",
        "operationId": "stopFillDataJobsUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "fillId",
            "in": "query",
            "description": "选择的实例id",
            "required": true,
            "type": "ref"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«int»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/action/stopJobByCondition": {
      "post": {
        "tags": [
          "运维中心---任务动作相关接口"
        ],
        "summary": "按照添加停止任务",
        "operationId": "stopJobByConditionUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/杀死任务实例信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«int»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/cluster/addCluster": {
      "post": {
        "tags": [
          "集群接口"
        ],
        "summary": "addCluster",
        "description": "创建集群",
        "operationId": "addClusterUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "clusterName",
            "in": "query",
            "description": "集群名称",
            "required": true,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/cluster/deleteCluster": {
      "post": {
        "tags": [
          "集群接口"
        ],
        "summary": "deleteCluster",
        "description": "删除集群",
        "operationId": "deleteClusterUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "clusterId",
            "in": "query",
            "description": "集群id",
            "required": true,
            "type": "integer",
            "format": "int64"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/cluster/getAllCluster": {
      "get": {
        "tags": [
          "集群接口"
        ],
        "summary": "getAllCluster",
        "description": "获取所有集群名称",
        "operationId": "getAllClusterUsingGET",
        "produces": [
          "*/*"
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«ClusterInfoVO»»"
            }
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/cluster/getCluster": {
      "get": {
        "tags": [
          "集群接口"
        ],
        "summary": "getCluster",
        "description": "获取集群详细信息 包含组件",
        "operationId": "getClusterUsingGET",
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "clusterId",
            "in": "query",
            "description": "集群id",
            "required": true,
            "type": "integer",
            "format": "int64"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«ClusterVO»"
            }
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/cluster/getClusterEngine": {
      "get": {
        "tags": [
          "集群接口"
        ],
        "summary": "getClusterEngine",
        "description": "获取单个集群详细信息包含引擎",
        "operationId": "getClusterEngineUsingGET",
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "clusterId",
            "in": "query",
            "description": "集群id",
            "required": true,
            "type": "integer",
            "format": "int64"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«ClusterEngineVO»"
            }
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/cluster/getMetaComponent": {
      "get": {
        "tags": [
          "集群接口"
        ],
        "summary": "getMetaComponent",
        "description": "获取单个集群meta属性的组件标识",
        "operationId": "getMetaComponentUsingGET",
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "clusterId",
            "in": "query",
            "description": "集群id",
            "required": true,
            "type": "integer",
            "format": "int64"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«int»"
            }
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/cluster/pageQuery": {
      "post": {
        "tags": [
          "集群接口"
        ],
        "summary": "pageQuery",
        "description": "集群列表",
        "operationId": "pageQueryUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "currentPage",
            "in": "query",
            "description": "当前页",
            "required": true,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "pageSize",
            "in": "query",
            "description": "页面大小",
            "required": true,
            "type": "integer",
            "format": "int32"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«PageResult«List«ClusterInfoVO»»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/component/closeKerberos": {
      "post": {
        "tags": [
          "组件接口"
        ],
        "summary": "关闭kerberos配置",
        "operationId": "closeKerberosUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "componentId",
            "in": "query",
            "description": "组件id",
            "required": true,
            "type": "integer",
            "format": "int64"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«Void»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/component/delete": {
      "post": {
        "tags": [
          "组件接口"
        ],
        "summary": "删除组件",
        "operationId": "deleteUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "componentId",
            "in": "query",
            "description": "组件id",
            "required": true,
            "type": "integer",
            "format": "int64"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«Void»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/component/getComponentStore": {
      "post": {
        "tags": [
          "组件接口"
        ],
        "summary": "获取对应的组件能选择的存储组件类型",
        "operationId": "getComponentStoreUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "clusterName",
            "in": "query",
            "description": "集群名称",
            "required": true,
            "type": "string"
          },
          {
            "name": "componentType",
            "in": "query",
            "description": "组件code",
            "required": true,
            "type": "integer",
            "format": "int32"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«int»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/component/getComponentVersion": {
      "get": {
        "tags": [
          "组件接口"
        ],
        "summary": "获取对应的组件能版本信息",
        "operationId": "getComponentVersionUsingGET",
        "produces": [
          "*/*"
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«Map»"
            }
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/component/getKerberosConfig": {
      "post": {
        "tags": [
          "组件接口"
        ],
        "summary": "getKerberosConfig",
        "description": "获取kerberos配置信息",
        "operationId": "getKerberosConfigUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "clusterId",
            "in": "query",
            "description": "集群id",
            "required": true,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "componentType",
            "in": "query",
            "description": "组件code",
            "required": true,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "componentVersion",
            "in": "query",
            "description": "组件版本",
            "required": true,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«KerberosConfigVO»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/component/loadTemplate": {
      "post": {
        "tags": [
          "组件接口"
        ],
        "summary": "加载各个组件的前端渲染模版",
        "operationId": "loadTemplateUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "clusterId",
            "in": "query",
            "description": "集群id",
            "required": true,
            "type": "integer",
            "format": "int64",
            "x-example": "-1L"
          },
          {
            "name": "componentType",
            "in": "query",
            "description": "组件code",
            "required": true,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "deployType",
            "in": "query",
            "description": "deploy类型",
            "required": true,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "storeType",
            "in": "query",
            "description": "存储组件code",
            "required": true,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "versionName",
            "in": "query",
            "description": "组件版本名称",
            "required": true,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«ClientTemplate»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/component/testConnect": {
      "post": {
        "tags": [
          "组件接口"
        ],
        "summary": "测试单个组件连通性",
        "operationId": "testConnectUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "clusterName",
            "in": "query",
            "description": "集群名称",
            "required": true,
            "type": "string"
          },
          {
            "name": "componentType",
            "in": "query",
            "description": "组件code",
            "required": true,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "versionName",
            "in": "query",
            "description": "组件版本",
            "required": true,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«ComponentTestResult»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/component/testConnects": {
      "post": {
        "tags": [
          "组件接口"
        ],
        "summary": "测试所有组件连通性",
        "operationId": "testConnectsUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "clusterName",
            "in": "query",
            "description": "集群名称",
            "required": true,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«ComponentMultiTestResult»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/component/updateKrb5Conf": {
      "post": {
        "tags": [
          "组件接口"
        ],
        "summary": "updateKrb5Conf",
        "description": "更新krb5配置内容",
        "operationId": "updateKrb5ConfUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "krb5Content",
            "in": "query",
            "description": "krb5配置内容",
            "required": true,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«Void»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/console/clusterResources": {
      "post": {
        "tags": [
          "控制台接口"
        ],
        "summary": "clusterResources",
        "operationId": "clusterResourcesUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "clusterName",
            "in": "query",
            "description": "clusterName",
            "required": false,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«ClusterResource»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/console/groupDetail": {
      "post": {
        "tags": [
          "控制台接口"
        ],
        "summary": "groupDetail",
        "operationId": "groupDetailUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "currentPage",
            "in": "query",
            "description": "currentPage",
            "required": false,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "jobResource",
            "in": "query",
            "description": "jobResource",
            "required": false,
            "type": "string"
          },
          {
            "name": "nodeAddress",
            "in": "query",
            "description": "nodeAddress",
            "required": false,
            "type": "string"
          },
          {
            "name": "pageSize",
            "in": "query",
            "description": "pageSize",
            "required": false,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "stage",
            "in": "query",
            "description": "stage",
            "required": false,
            "type": "integer",
            "format": "int32"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«PageResult»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/console/jobResources": {
      "post": {
        "tags": [
          "控制台接口"
        ],
        "summary": "jobResources",
        "operationId": "jobResourcesUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«string»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/console/jobStick": {
      "post": {
        "tags": [
          "控制台接口"
        ],
        "summary": "jobStick",
        "operationId": "jobStickUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "jobId",
            "in": "query",
            "description": "jobId",
            "required": false,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/console/listNames": {
      "post": {
        "tags": [
          "控制台接口"
        ],
        "summary": "listNames",
        "operationId": "listNamesUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "jobName",
            "in": "query",
            "description": "jobName",
            "required": false,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«string»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/console/nodeAddress": {
      "post": {
        "tags": [
          "控制台接口"
        ],
        "summary": "nodeAddress",
        "operationId": "nodeAddressUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«string»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/console/overview": {
      "post": {
        "tags": [
          "控制台接口"
        ],
        "summary": "根据计算引擎类型显示任务",
        "operationId": "overviewUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "clusterName",
            "in": "query",
            "description": "clusterName",
            "required": false,
            "type": "string"
          },
          {
            "name": "nodeAddress",
            "in": "query",
            "description": "nodeAddress",
            "required": false,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«Collection«Map«string,object»»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/console/searchJob": {
      "post": {
        "tags": [
          "控制台接口"
        ],
        "summary": "searchJob",
        "operationId": "searchJobUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "jobName",
            "in": "query",
            "description": "jobName",
            "required": false,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«ConsoleJobVO»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/console/stopAll": {
      "post": {
        "tags": [
          "控制台接口"
        ],
        "summary": "概览，杀死全部",
        "operationId": "stopAllUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "jobResource",
            "in": "query",
            "description": "jobResource",
            "required": false,
            "type": "string"
          },
          {
            "name": "nodeAddress",
            "in": "query",
            "description": "nodeAddress",
            "required": false,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«Void»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/console/stopJob": {
      "post": {
        "tags": [
          "控制台接口"
        ],
        "summary": "stopJob",
        "operationId": "stopJobUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "jobId",
            "in": "query",
            "description": "jobId",
            "required": false,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«Void»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/console/stopJobList": {
      "post": {
        "tags": [
          "控制台接口"
        ],
        "summary": "stopJobList",
        "operationId": "stopJobListUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "jobIdList",
            "in": "query",
            "description": "jobIdList",
            "required": false,
            "type": "array",
            "items": {
              "type": "string"
            },
            "collectionFormat": "multi"
          },
          {
            "name": "jobResource",
            "in": "query",
            "description": "jobResource",
            "required": false,
            "type": "string"
          },
          {
            "name": "nodeAddress",
            "in": "query",
            "description": "nodeAddress",
            "required": false,
            "type": "string"
          },
          {
            "name": "stage",
            "in": "query",
            "description": "stage",
            "required": false,
            "type": "integer",
            "format": "int32"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«Void»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/datasource/addDs/addOrUpdateSource": {
      "post": {
        "tags": [
          "数据源中心-新增数据源"
        ],
        "summary": "添加和修改数据源",
        "operationId": "addOrUpdateSourceUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "addDataSourceParam",
            "description": "addDataSourceParam",
            "required": true,
            "schema": {
              "$ref": "#/definitions/新增数据源整体入参"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«long»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/datasource/addDs/addOrUpdateSourceWithKerberos": {
      "post": {
        "tags": [
          "数据源中心-新增数据源"
        ],
        "summary": "上传Kerberos添加和修改数据源",
        "operationId": "addOrUpdateSourceWithKerberosUsingPOST",
        "consumes": [
          "multipart/form-data"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "file",
            "in": "formData",
            "description": "上传文件",
            "required": true,
            "type": "file"
          },
          {
            "name": "params",
            "in": "query",
            "description": "params",
            "required": false,
            "type": "object"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«long»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/datasource/addDs/columnForSyncopate": {
      "post": {
        "tags": [
          "数据源中心-新增数据源"
        ],
        "summary": "返回切分键需要的列名",
        "operationId": "columnForSyncopateUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/数据同步-返回切分键需要的列名"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«Set«JSONObject»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/datasource/addDs/getAllSchemas": {
      "post": {
        "tags": [
          "数据源中心-新增数据源"
        ],
        "summary": "获取所有schema",
        "operationId": "getAllSchemasUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/数据源表存储信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«string»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/datasource/addDs/getHivePartitions": {
      "post": {
        "tags": [
          "数据源中心-新增数据源"
        ],
        "summary": "获取hive分区",
        "operationId": "getHivePartitionsUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/数据源表存储信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«Set«string»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/datasource/addDs/getPrincipalsWithConf": {
      "post": {
        "tags": [
          "数据源中心-新增数据源"
        ],
        "summary": "解析kerberos文件获取principal列表",
        "operationId": "getPrincipalsWithConfUsingPOST",
        "consumes": [
          "multipart/form-data"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "file",
            "in": "query",
            "description": "file",
            "required": false,
            "type": "file"
          },
          {
            "name": "params",
            "in": "query",
            "description": "params",
            "required": false,
            "type": "object"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«string»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/datasource/addDs/preview": {
      "post": {
        "tags": [
          "数据源中心-新增数据源"
        ],
        "summary": "获取预览数据",
        "operationId": "previewUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/数据源预览信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«JSONObject»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/datasource/addDs/queryDsClassifyList": {
      "post": {
        "tags": [
          "数据源中心-新增数据源"
        ],
        "summary": "获取数据源分类类目列表",
        "operationId": "queryDsClassifyListUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«数据源分类类目模型»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/datasource/addDs/queryDsTypeByClassify": {
      "post": {
        "tags": [
          "数据源中心-新增数据源"
        ],
        "summary": "根据分类获取数据源类型",
        "operationId": "queryDsTypeByClassifyUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "searchParam",
            "description": "searchParam",
            "required": true,
            "schema": {
              "$ref": "#/definitions/搜索数据源类型参数"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«数据源类型视图类»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/datasource/addDs/queryDsVersionByType": {
      "post": {
        "tags": [
          "数据源中心-新增数据源"
        ],
        "summary": "根据数据源类型获取版本列表",
        "operationId": "queryDsVersionByTypeUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "searchParam",
            "description": "searchParam",
            "required": true,
            "schema": {
              "$ref": "#/definitions/搜索数据源版本入参"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«数据源版本视图类»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/datasource/addDs/tablecolumn": {
      "post": {
        "tags": [
          "数据源中心-新增数据源"
        ],
        "summary": "获取表字段信息",
        "operationId": "tablecolumnUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/数据源表字段信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«JSONObject»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/datasource/addDs/tablelist": {
      "post": {
        "tags": [
          "数据源中心-新增数据源"
        ],
        "summary": "获取表列表",
        "operationId": "tablelistUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "sourceVO",
            "description": "sourceVO",
            "required": false,
            "schema": {
              "$ref": "#/definitions/数据源表列表信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«string»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/datasource/addDs/testCon": {
      "post": {
        "tags": [
          "数据源中心-新增数据源"
        ],
        "summary": "测试联通性",
        "operationId": "testConUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "addDataSourceParam",
            "description": "addDataSourceParam",
            "required": true,
            "schema": {
              "$ref": "#/definitions/新增数据源整体入参"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/datasource/addDs/testConWithKerberos": {
      "post": {
        "tags": [
          "数据源中心-新增数据源"
        ],
        "summary": "上传Kerberos测试联通性",
        "operationId": "testConWithKerberosUsingPOST",
        "consumes": [
          "multipart/form-data"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "file",
            "in": "formData",
            "description": "上传文件",
            "required": false,
            "type": "file"
          },
          {
            "name": "params",
            "in": "query",
            "description": "params",
            "required": false,
            "type": "object"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/datasource/dataSource/delete": {
      "post": {
        "tags": [
          "数据源中心-数据源管理"
        ],
        "summary": "删除一条数据源实例",
        "operationId": "deleteByIdUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "dsInfoIdParam",
            "description": "dsInfoIdParam",
            "required": true,
            "schema": {
              "$ref": "#/definitions/基础服务入参基类"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/datasource/dataSource/detail": {
      "post": {
        "tags": [
          "数据源中心-数据源管理"
        ],
        "summary": "获取数据源基本详情",
        "operationId": "dsDetailUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "dsInfoIdParam",
            "description": "dsInfoIdParam",
            "required": true,
            "schema": {
              "$ref": "#/definitions/基础服务入参基类"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«数据源基本信息»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/datasource/dataSource/page": {
      "post": {
        "tags": [
          "数据源中心-数据源管理"
        ],
        "summary": "数据源列表分页信息",
        "operationId": "dsPageUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "dsListParam",
            "description": "dsListParam",
            "required": true,
            "schema": {
              "$ref": "#/definitions/数据源列表查询参数"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«PageResult«List«数据源列表信息»»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/datasource/dataSource/queryByTenantId": {
      "get": {
        "tags": [
          "数据源中心-数据源管理"
        ],
        "summary": "根据租户id查询数据源列表",
        "operationId": "queryByTenantIdUsingGET",
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "tenantId",
            "in": "query",
            "description": "tenantId",
            "required": false,
            "type": "integer",
            "format": "int64"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«数据源列表信息»»"
            }
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/datasource/dataSource/type/list": {
      "post": {
        "tags": [
          "数据源中心-数据源管理"
        ],
        "summary": "数据源类型下拉列表",
        "operationId": "dsTypeListUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«当前租户支持的数据源类型列表»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/datasource/dsForm/findFormByTypeVersion": {
      "post": {
        "tags": [
          "数据源中心-数据源表单模版化"
        ],
        "summary": "根据数据库类型和版本查找表单模版",
        "operationId": "findTemplateByTypeVersionUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "param",
            "description": "param",
            "required": true,
            "schema": {
              "$ref": "#/definitions/数据源类型和版本统一入参"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«数据表单模版视图类»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/download/component/downloadFile": {
      "get": {
        "tags": [
          "下载接口"
        ],
        "summary": "下载文件",
        "operationId": "handleDownloadUsingGET",
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "clusterId",
            "in": "query",
            "description": "clusterId",
            "required": false,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "componentId",
            "in": "query",
            "description": "componentId",
            "required": false,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "componentType",
            "in": "query",
            "description": "componentType",
            "required": false,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "deployType",
            "in": "query",
            "description": "deployType",
            "required": false,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "type",
            "in": "query",
            "description": "0:kerberos配置文件 1:配置文件 2:模板文件",
            "required": true,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "versionName",
            "in": "query",
            "description": "versionName",
            "required": false,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«Void»"
            }
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/fill/fillData": {
      "post": {
        "tags": [
          "运维中心---补数据相关接口"
        ],
        "summary": "补数据接口:支持批量补数据和工程补数据",
        "operationId": "fillDataUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "scheduleFillJobParticipateVO",
            "description": "scheduleFillJobParticipateVO",
            "required": true,
            "schema": {
              "$ref": "#/definitions/ScheduleFillJobParticipateVO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«long»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/fill/queryFillDataJobList": {
      "post": {
        "tags": [
          "运维中心---补数据相关接口"
        ],
        "summary": "fillDataJobList",
        "operationId": "fillDataJobListUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/QueryFillDataJobListVO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«PageResult«ReturnFillDataJobListVO»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/fill/queryFillDataList": {
      "post": {
        "tags": [
          "运维中心---补数据相关接口"
        ],
        "summary": "fillDataList",
        "operationId": "fillDataListUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/QueryFillDataListVO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«PageResult«List«ReturnFillDataListVO»»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/scheduleJob/displayOffSpring": {
      "post": {
        "tags": [
          "运维中心---周期实例依赖关系相关接口"
        ],
        "summary": "displayOffSpring",
        "operationId": "displayOffSpringUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/QueryJobDisplayVO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«ReturnJobDisplayVO»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/scheduleJob/displayOffSpringWorkFlow": {
      "post": {
        "tags": [
          "运维中心---周期实例依赖关系相关接口"
        ],
        "summary": "为工作流节点展开子节点",
        "operationId": "displayOffSpringWorkFlowUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "jobId",
            "in": "query",
            "description": "实例id",
            "required": true,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«ReturnJobDisplayVO»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/scheduleJob/queryDisplayPeriods": {
      "post": {
        "tags": [
          "运维中心---周期实例相关接口"
        ],
        "summary": "queryDisplayPeriods",
        "operationId": "queryDisplayPeriodsUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "isAfter",
            "in": "query",
            "description": "isAfter",
            "required": false,
            "type": "boolean"
          },
          {
            "name": "jobId",
            "in": "query",
            "description": "jobId",
            "required": false,
            "type": "string"
          },
          {
            "name": "limit",
            "in": "query",
            "description": "limit",
            "required": false,
            "type": "integer",
            "format": "int32"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«ReturnDisplayPeriodVO»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/scheduleJob/queryFlowWorkSubJobs": {
      "post": {
        "tags": [
          "运维中心---周期实例相关接口"
        ],
        "summary": "获取工作流节点",
        "operationId": "queryFlowWorkSubJobsUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "jobId",
            "in": "query",
            "description": "实例id",
            "required": true,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«ReturnJobListVO»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/scheduleJob/queryJobs": {
      "post": {
        "tags": [
          "运维中心---周期实例相关接口"
        ],
        "summary": "任务运维 - 搜索",
        "operationId": "queryJobsUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/QueryJobListVO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«PageResult«List«ReturnJobListVO»»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/scheduleJob/queryJobsStatusStatistics": {
      "post": {
        "tags": [
          "运维中心---周期实例相关接口"
        ],
        "summary": "任务状态统计",
        "operationId": "queryJobsStatusStatisticsUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/QueryJobStatusStatisticsVO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«ReturnJobStatusStatisticsVO»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/scheduleTaskShade/frozenTask": {
      "post": {
        "tags": [
          "运维中心---任务相关接口"
        ],
        "summary": "运维中心任务管理 -> 冻结和解冻任务",
        "operationId": "frozenTaskUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "scheduleStatus",
            "in": "query",
            "description": " 调度状态：0 正常 1冻结 2停止",
            "required": true,
            "type": "ref"
          },
          {
            "name": "taskIdList",
            "in": "query",
            "description": "任务id",
            "required": true,
            "type": "ref"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/scheduleTaskShade/queryFlowWorkSubTasks": {
      "post": {
        "tags": [
          "运维中心---任务相关接口"
        ],
        "summary": "查询工作流下子节点",
        "operationId": "queryFlowWorkSubTasksUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "taskId",
            "in": "query",
            "description": "任务id",
            "required": true,
            "type": "integer",
            "format": "int64"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«ReturnScheduleTaskVO»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/scheduleTaskShade/querySupportJobTypes": {
      "post": {
        "tags": [
          "运维中心---任务相关接口"
        ],
        "summary": "查询所有任务类型",
        "operationId": "querySupportJobTypesUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«ReturnTaskSupportTypesVO»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/scheduleTaskShade/queryTasks": {
      "post": {
        "tags": [
          "运维中心---任务相关接口"
        ],
        "summary": "运维中心任务管理 -> 任务列表接口",
        "operationId": "queryTasksUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/QueryTaskListVO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«PageResult«List«ReturnScheduleTaskVO»»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/scheduleTaskTaskShade/displayOffSpring": {
      "post": {
        "tags": [
          "运维中心---任务依赖相关接口"
        ],
        "summary": "displayOffSpring",
        "operationId": "displayOffSpringUsingPOST_1",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/QueryTaskDisplayVO"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«ReturnTaskDisplayVO»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/scheduleTaskTaskShade/getAllFlowSubTasks": {
      "post": {
        "tags": [
          "运维中心---任务依赖相关接口"
        ],
        "summary": "查询工作流全部节点信息 -- 依赖树",
        "operationId": "getAllFlowSubTasksUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "taskId",
            "in": "query",
            "description": "任务id",
            "required": true,
            "type": "integer",
            "format": "int64"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«ReturnTaskDisplayVO»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/tenant/addTenant": {
      "post": {
        "tags": [
          "租户接口"
        ],
        "summary": "bindingTenant",
        "operationId": "bindingTenantUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "tenantName",
            "in": "query",
            "description": "tenantName",
            "required": false,
            "type": "string"
          },
          {
            "in": "body",
            "name": "userId",
            "description": "userId",
            "required": false,
            "schema": {
              "type": "integer",
              "format": "int64"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«Void»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/tenant/bindingQueue": {
      "post": {
        "tags": [
          "租户接口"
        ],
        "summary": "bindingQueue",
        "operationId": "bindingQueueUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "queueId",
            "in": "query",
            "description": "queueId",
            "required": false,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "tenantId",
            "in": "query",
            "description": "tenantId",
            "required": false,
            "type": "integer",
            "format": "int64"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«Void»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/tenant/bindingTenant": {
      "post": {
        "tags": [
          "租户接口"
        ],
        "summary": "bindingTenant",
        "operationId": "bindingTenantUsingPOST_1",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "in": "body",
            "name": "vo",
            "description": "vo",
            "required": true,
            "schema": {
              "$ref": "#/definitions/租户对接集群信息"
            }
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«Void»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/tenant/listTenant": {
      "get": {
        "tags": [
          "租户接口"
        ],
        "summary": "listTenant",
        "operationId": "listTenantUsingGET",
        "produces": [
          "*/*"
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«TenantVO»»"
            }
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/tenant/pageQuery": {
      "post": {
        "tags": [
          "租户接口"
        ],
        "summary": "pageQuery",
        "operationId": "pageQueryUsingPOST_1",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "clusterId",
            "in": "query",
            "description": "clusterId",
            "required": false,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "currentPage",
            "in": "query",
            "description": "currentPage",
            "required": false,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "pageSize",
            "in": "query",
            "description": "pageSize",
            "required": false,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "tenantName",
            "in": "query",
            "description": "tenantName",
            "required": false,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«PageResult«List«ClusterTenantVO»»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/upload/component/addOrUpdateComponent": {
      "post": {
        "tags": [
          "上传接口"
        ],
        "summary": "addOrUpdateComponent",
        "operationId": "addOrUpdateComponentUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "clusterId",
            "in": "query",
            "description": "clusterId",
            "required": false,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "componentCode",
            "in": "query",
            "description": "componentCode",
            "required": false,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "componentConfig",
            "in": "query",
            "description": "componentConfig",
            "required": false,
            "type": "string"
          },
          {
            "name": "componentTemplate",
            "in": "query",
            "description": "componentTemplate",
            "required": false,
            "type": "string"
          },
          {
            "name": "deployType",
            "in": "query",
            "description": "deployType",
            "required": false,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "isDefault",
            "in": "query",
            "description": "isDefault",
            "required": false,
            "type": "boolean"
          },
          {
            "name": "isMetadata",
            "in": "query",
            "description": "isMetadata",
            "required": false,
            "type": "boolean"
          },
          {
            "name": "kerberosFileName",
            "in": "query",
            "description": "kerberosFileName",
            "required": false,
            "type": "string"
          },
          {
            "name": "principal",
            "in": "query",
            "description": "principal",
            "required": false,
            "type": "string"
          },
          {
            "name": "principals",
            "in": "query",
            "description": "principals",
            "required": false,
            "type": "string"
          },
          {
            "name": "resources1",
            "in": "query",
            "description": "resources1",
            "required": false,
            "type": "array",
            "items": {
              "type": "file"
            },
            "collectionFormat": "multi"
          },
          {
            "name": "resources2",
            "in": "query",
            "description": "resources2",
            "required": false,
            "type": "array",
            "items": {
              "type": "file"
            },
            "collectionFormat": "multi"
          },
          {
            "name": "storeType",
            "in": "query",
            "description": "storeType",
            "required": false,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "versionName",
            "in": "query",
            "description": "versionName",
            "required": false,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«ComponentVO»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/upload/component/config": {
      "post": {
        "tags": [
          "上传接口"
        ],
        "summary": "解析zip中xml或者json",
        "operationId": "uploadUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "autoDelete",
            "in": "query",
            "description": "autoDelete",
            "required": false,
            "type": "boolean"
          },
          {
            "name": "componentType",
            "in": "query",
            "description": "componentType",
            "required": false,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "fileName",
            "in": "query",
            "description": "fileName",
            "required": false,
            "type": "array",
            "items": {
              "type": "file"
            },
            "collectionFormat": "multi"
          },
          {
            "name": "version",
            "in": "query",
            "description": "version",
            "required": false,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«object»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/upload/component/parseKerberos": {
      "post": {
        "tags": [
          "上传接口"
        ],
        "summary": "解析kerberos文件中信息",
        "operationId": "parseKerberosUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "fileName",
            "in": "query",
            "description": "fileName",
            "required": false,
            "type": "array",
            "items": {
              "type": "file"
            },
            "collectionFormat": "multi"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«string»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/upload/component/uploadKerberos": {
      "post": {
        "tags": [
          "上传接口"
        ],
        "summary": "uploadKerberos",
        "operationId": "uploadKerberosUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "clusterId",
            "in": "query",
            "description": "clusterId",
            "required": false,
            "type": "integer",
            "format": "int64"
          },
          {
            "name": "componentCode",
            "in": "query",
            "description": "componentCode",
            "required": false,
            "type": "integer",
            "format": "int32"
          },
          {
            "name": "componentVersion",
            "in": "query",
            "description": "componentVersion",
            "required": false,
            "type": "string"
          },
          {
            "name": "kerberosFile",
            "in": "query",
            "description": "kerberosFile",
            "required": false,
            "type": "array",
            "items": {
              "type": "file"
            },
            "collectionFormat": "multi"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«string»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/user/login": {
      "post": {
        "tags": [
          "用户接口"
        ],
        "summary": "login",
        "operationId": "loginUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "password",
            "in": "query",
            "description": "password",
            "required": false,
            "type": "string"
          },
          {
            "name": "username",
            "in": "query",
            "description": "username",
            "required": false,
            "type": "string"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«string»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    },
    "/node/user/logout": {
      "get": {
        "tags": [
          "用户接口"
        ],
        "summary": "logout",
        "operationId": "logoutUsingGET",
        "produces": [
          "*/*"
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«boolean»"
            }
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      },
      "head": {
        "tags": [
          "用户接口"
        ],
        "summary": "logout",
        "operationId": "logoutUsingHEAD",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«boolean»"
            }
          },
          "204": {
            "description": "No Content"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          }
        },
        "deprecated": false
      },
      "post": {
        "tags": [
          "用户接口"
        ],
        "summary": "logout",
        "operationId": "logoutUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      },
      "put": {
        "tags": [
          "用户接口"
        ],
        "summary": "logout",
        "operationId": "logoutUsingPUT",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«boolean»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      },
      "delete": {
        "tags": [
          "用户接口"
        ],
        "summary": "logout",
        "operationId": "logoutUsingDELETE",
        "produces": [
          "*/*"
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«boolean»"
            }
          },
          "204": {
            "description": "No Content"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          }
        },
        "deprecated": false
      },
      "options": {
        "tags": [
          "用户接口"
        ],
        "summary": "logout",
        "operationId": "logoutUsingOPTIONS",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«boolean»"
            }
          },
          "204": {
            "description": "No Content"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          }
        },
        "deprecated": false
      },
      "patch": {
        "tags": [
          "用户接口"
        ],
        "summary": "logout",
        "operationId": "logoutUsingPATCH",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«boolean»"
            }
          },
          "204": {
            "description": "No Content"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          }
        },
        "deprecated": false
      }
    },
    "/node/user/queryUser": {
      "get": {
        "tags": [
          "用户接口"
        ],
        "summary": "queryUser",
        "operationId": "queryUserUsingGET",
        "produces": [
          "*/*"
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«UserVO»»"
            }
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      },
      "head": {
        "tags": [
          "用户接口"
        ],
        "summary": "queryUser",
        "operationId": "queryUserUsingHEAD",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«UserVO»»"
            }
          },
          "204": {
            "description": "No Content"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          }
        },
        "deprecated": false
      },
      "post": {
        "tags": [
          "用户接口"
        ],
        "summary": "queryUser",
        "operationId": "queryUserUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«UserVO»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      },
      "put": {
        "tags": [
          "用户接口"
        ],
        "summary": "queryUser",
        "operationId": "queryUserUsingPUT",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«UserVO»»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      },
      "delete": {
        "tags": [
          "用户接口"
        ],
        "summary": "queryUser",
        "operationId": "queryUserUsingDELETE",
        "produces": [
          "*/*"
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«UserVO»»"
            }
          },
          "204": {
            "description": "No Content"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          }
        },
        "deprecated": false
      },
      "options": {
        "tags": [
          "用户接口"
        ],
        "summary": "queryUser",
        "operationId": "queryUserUsingOPTIONS",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«UserVO»»"
            }
          },
          "204": {
            "description": "No Content"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          }
        },
        "deprecated": false
      },
      "patch": {
        "tags": [
          "用户接口"
        ],
        "summary": "queryUser",
        "operationId": "queryUserUsingPATCH",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«List«UserVO»»"
            }
          },
          "204": {
            "description": "No Content"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          }
        },
        "deprecated": false
      }
    },
    "/node/user/switchTenant": {
      "post": {
        "tags": [
          "用户接口"
        ],
        "summary": "switchTenant",
        "operationId": "switchTenantUsingPOST",
        "consumes": [
          "application/json"
        ],
        "produces": [
          "*/*"
        ],
        "parameters": [
          {
            "name": "tenantId",
            "in": "query",
            "description": "tenantId",
            "required": false,
            "type": "integer",
            "format": "int64"
          }
        ],
        "responses": {
          "200": {
            "description": "OK",
            "schema": {
              "$ref": "#/definitions/R«string»"
            }
          },
          "201": {
            "description": "Created"
          },
          "401": {
            "description": "Unauthorized"
          },
          "403": {
            "description": "Forbidden"
          },
          "404": {
            "description": "Not Found"
          }
        },
        "deprecated": false
      }
    }
  },
  "definitions": {
    "BatchResourceVO": {
      "type": "object",
      "properties": {
        "createUser": {
          "$ref": "#/definitions/User"
        },
        "createUserId": {
          "type": "integer",
          "format": "int64"
        },
        "gmtCreate": {
          "$ref": "#/definitions/Timestamp"
        },
        "gmtModified": {
          "$ref": "#/definitions/Timestamp"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "isDeleted": {
          "type": "integer",
          "format": "int32"
        },
        "modifyUser": {
          "$ref": "#/definitions/User"
        },
        "modifyUserId": {
          "type": "integer",
          "format": "int64"
        },
        "nodePid": {
          "type": "integer",
          "format": "int64"
        },
        "originFileName": {
          "type": "string"
        },
        "resourceDesc": {
          "type": "string"
        },
        "resourceName": {
          "type": "string"
        },
        "resourceType": {
          "type": "integer",
          "format": "int32"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64"
        },
        "url": {
          "type": "string"
        }
      },
      "title": "BatchResourceVO"
    },
    "ClientTemplate": {
      "type": "object",
      "properties": {
        "dependencyKey": {
          "type": "string",
          "description": "依赖配置"
        },
        "dependencyValue": {
          "type": "string",
          "description": "依赖配置的值"
        },
        "deployTypes": {
          "type": "array",
          "items": {
            "type": "integer",
            "format": "int32"
          }
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "key": {
          "type": "string",
          "description": "前端界面展示 名称"
        },
        "required": {
          "type": "boolean",
          "description": "是否必填 默认非必须"
        },
        "type": {
          "type": "string",
          "description": "前端界面展示类型  0: 输入框 1:单选:"
        },
        "value": {
          "type": "object",
          "description": "默认值"
        },
        "values": {
          "type": "array",
          "description": "前端界面展示 多选值",
          "items": {
            "$ref": "#/definitions/ClientTemplate"
          }
        }
      },
      "title": "ClientTemplate"
    },
    "ClusterEngineVO": {
      "type": "object",
      "properties": {
        "clusterId": {
          "type": "integer",
          "format": "int64"
        },
        "clusterName": {
          "type": "string"
        },
        "engines": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/EngineVO"
          }
        },
        "gmtCreate": {
          "$ref": "#/definitions/Timestamp"
        },
        "gmtModified": {
          "$ref": "#/definitions/Timestamp"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "isDeleted": {
          "type": "integer",
          "format": "int32"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64"
        }
      },
      "title": "ClusterEngineVO"
    },
    "ClusterInfoVO": {
      "type": "object",
      "properties": {
        "canModifyMetadata": {
          "type": "boolean",
          "description": "是否能修改切换metadata"
        },
        "clusterId": {
          "type": "integer",
          "format": "int64",
          "description": "集群id"
        },
        "clusterName": {
          "type": "string",
          "description": "集群名称"
        },
        "gmtCreate": {
          "$ref": "#/definitions/Timestamp"
        },
        "gmtModified": {
          "$ref": "#/definitions/Timestamp"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "isDeleted": {
          "type": "integer",
          "format": "int32"
        }
      },
      "title": "ClusterInfoVO"
    },
    "ClusterResource": {
      "type": "object",
      "properties": {
        "nodes": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/NodeDescription"
          }
        },
        "queues": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/JSONObject"
          }
        },
        "resourceMetrics": {
          "$ref": "#/definitions/ResourceMetrics"
        },
        "scheduleInfo": {
          "type": "object",
          "additionalProperties": {
            "type": "object"
          }
        }
      },
      "title": "ClusterResource"
    },
    "ClusterResourceDescription": {
      "type": "object",
      "properties": {
        "queueDescriptions": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/QueueDescription"
          }
        },
        "totalCores": {
          "type": "integer",
          "format": "int32"
        },
        "totalMemory": {
          "type": "integer",
          "format": "int32"
        },
        "totalNode": {
          "type": "integer",
          "format": "int32"
        }
      },
      "title": "ClusterResourceDescription"
    },
    "ClusterTenantVO": {
      "type": "object",
      "properties": {
        "maxCapacity": {
          "type": "string"
        },
        "minCapacity": {
          "type": "string"
        },
        "queue": {
          "type": "string"
        },
        "queueId": {
          "type": "integer",
          "format": "int64"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64"
        },
        "tenantName": {
          "type": "string"
        }
      },
      "title": "ClusterTenantVO"
    },
    "ClusterVO": {
      "type": "object",
      "properties": {
        "canModifyMetadata": {
          "type": "boolean",
          "description": "是否能修改metadata组件"
        },
        "clusterId": {
          "type": "integer",
          "format": "int64"
        },
        "clusterName": {
          "type": "string"
        },
        "gmtCreate": {
          "$ref": "#/definitions/Timestamp"
        },
        "gmtModified": {
          "$ref": "#/definitions/Timestamp"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "isDeleted": {
          "type": "integer",
          "format": "int32"
        },
        "scheduling": {
          "type": "array",
          "description": "组件类型",
          "items": {
            "$ref": "#/definitions/SchedulingVo"
          }
        }
      },
      "title": "ClusterVO"
    },
    "ComponentBindDBVO": {
      "type": "object",
      "properties": {
        "componentCode": {
          "type": "integer",
          "format": "int32"
        },
        "createFlag": {
          "type": "boolean"
        },
        "dbName": {
          "type": "string"
        }
      },
      "title": "ComponentBindDBVO"
    },
    "ComponentMultiTestResult": {
      "type": "object",
      "properties": {
        "componentTypeCode": {
          "type": "integer",
          "format": "int32"
        },
        "errorMsg": {
          "type": "string"
        },
        "multiVersion": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/ComponentTestResult"
          }
        },
        "result": {
          "type": "boolean"
        }
      },
      "title": "ComponentMultiTestResult"
    },
    "ComponentTestResult": {
      "type": "object",
      "properties": {
        "clusterResourceDescription": {
          "$ref": "#/definitions/ClusterResourceDescription"
        },
        "componentTypeCode": {
          "type": "integer",
          "format": "int32"
        },
        "componentVersion": {
          "type": "string"
        },
        "errorMsg": {
          "type": "string"
        },
        "result": {
          "type": "boolean"
        }
      },
      "title": "ComponentTestResult"
    },
    "ComponentVO": {
      "type": "object",
      "properties": {
        "clusterId": {
          "type": "integer",
          "format": "int64"
        },
        "clusterName": {
          "type": "string"
        },
        "componentConfig": {
          "type": "string"
        },
        "componentName": {
          "type": "string"
        },
        "componentTemplate": {
          "type": "string"
        },
        "componentTypeCode": {
          "type": "integer",
          "format": "int32"
        },
        "default": {
          "type": "boolean"
        },
        "deployType": {
          "type": "integer",
          "format": "int32"
        },
        "gmtCreate": {
          "$ref": "#/definitions/Timestamp"
        },
        "gmtModified": {
          "$ref": "#/definitions/Timestamp"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "isDefault": {
          "type": "boolean"
        },
        "isDeleted": {
          "type": "integer",
          "format": "int32"
        },
        "isMetadata": {
          "type": "integer",
          "format": "int32"
        },
        "kerberosFileName": {
          "type": "string"
        },
        "mergeKrb5Content": {
          "type": "string"
        },
        "principal": {
          "type": "string"
        },
        "principals": {
          "type": "string"
        },
        "storeType": {
          "type": "integer",
          "format": "int32"
        },
        "uploadFileName": {
          "type": "string"
        },
        "versionName": {
          "type": "string"
        },
        "versionValue": {
          "type": "string"
        }
      },
      "title": "ComponentVO"
    },
    "ConsoleJobInfoVO": {
      "type": "object",
      "properties": {
        "execStartTime": {
          "$ref": "#/definitions/Timestamp"
        },
        "generateTime": {
          "type": "string",
          "format": "date-time"
        },
        "paramAction": {
          "$ref": "#/definitions/ParamAction"
        },
        "status": {
          "type": "integer",
          "format": "int32"
        },
        "tenantName": {
          "type": "string"
        },
        "waitTime": {
          "type": "string"
        }
      },
      "title": "ConsoleJobInfoVO"
    },
    "ConsoleJobVO": {
      "type": "object",
      "properties": {
        "nodeAddress": {
          "type": "string"
        },
        "theJob": {
          "$ref": "#/definitions/ConsoleJobInfoVO"
        },
        "theJobIdx": {
          "type": "integer",
          "format": "int32"
        }
      },
      "title": "ConsoleJobVO"
    },
    "EngineVO": {
      "type": "object",
      "properties": {
        "clusterId": {
          "type": "integer",
          "format": "int64"
        },
        "components": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/ComponentVO"
          }
        },
        "engineName": {
          "type": "string"
        },
        "engineType": {
          "type": "integer",
          "format": "int32"
        },
        "gmtCreate": {
          "$ref": "#/definitions/Timestamp"
        },
        "gmtModified": {
          "$ref": "#/definitions/Timestamp"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "isDeleted": {
          "type": "integer",
          "format": "int32"
        },
        "queues": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/QueueVO"
          }
        }
      },
      "title": "EngineVO"
    },
    "FillDataChooseTaskVO": {
      "type": "object",
      "properties": {
        "taskId": {
          "type": "integer",
          "format": "int64"
        }
      },
      "title": "FillDataChooseTaskVO"
    },
    "FillDataJobVO": {
      "type": "object",
      "properties": {
        "cycTime": {
          "type": "string",
          "example": "2021-12-24 16:11:53",
          "description": "计划时间"
        },
        "endExecTime": {
          "type": "string",
          "example": "2021-12-24 16:11:53",
          "description": "结束时间"
        },
        "execTime": {
          "type": "string",
          "example": 0,
          "description": "运行时长"
        },
        "flowJobId": {
          "type": "string",
          "example": 1,
          "description": "工作流id"
        },
        "jobId": {
          "type": "string",
          "description": "实例id"
        },
        "ownerId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "责任人id"
        },
        "ownerName": {
          "type": "string",
          "example": "admin@dtstack.com",
          "description": "责任人"
        },
        "retryNum": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "重试次数"
        },
        "startExecTime": {
          "type": "string",
          "example": "2021-12-24 16:11:53",
          "description": "开始时间"
        },
        "status": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "实例状态"
        },
        "taskName": {
          "type": "string",
          "example": 0,
          "description": "任务名称"
        },
        "taskType": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "任务类型"
        }
      },
      "title": "FillDataJobVO"
    },
    "IComponentVO": {
      "type": "object",
      "properties": {
        "componentTypeCode": {
          "type": "integer",
          "format": "int32"
        }
      },
      "title": "IComponentVO"
    },
    "JSONObject": {
      "type": "object",
      "title": "JSONObject",
      "additionalProperties": {
        "type": "object"
      }
    },
    "JobNodeVO": {
      "type": "object",
      "properties": {
        "childNode": {
          "type": "array",
          "description": "子节点",
          "items": {
            "$ref": "#/definitions/JobNodeVO"
          }
        },
        "cycTime": {
          "type": "string",
          "description": "计划时间"
        },
        "jobId": {
          "type": "string",
          "description": "实例id"
        },
        "ownerId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "责任人id"
        },
        "ownerName": {
          "type": "string",
          "example": 1,
          "description": "责任人名称"
        },
        "parentNode": {
          "type": "array",
          "description": " 父节点",
          "items": {
            "$ref": "#/definitions/JobNodeVO"
          }
        },
        "status": {
          "type": "integer",
          "format": "int32",
          "description": "实例状态"
        },
        "taskGmtCreate": {
          "description": "发布时间",
          "$ref": "#/definitions/Timestamp"
        },
        "taskId": {
          "type": "integer",
          "format": "int64",
          "description": "任务id"
        },
        "taskName": {
          "type": "string",
          "description": "任务名称"
        },
        "taskType": {
          "type": "integer",
          "format": "int32",
          "description": "任务类型"
        }
      },
      "title": "JobNodeVO"
    },
    "KerberosConfigVO": {
      "type": "object",
      "properties": {
        "clusterId": {
          "type": "integer",
          "format": "int64",
          "description": "集群id"
        },
        "componentType": {
          "type": "integer",
          "format": "int32",
          "description": "组件类型"
        },
        "componentVersion": {
          "type": "string",
          "description": "组件版本"
        },
        "krbName": {
          "type": "string",
          "description": "krb名称"
        },
        "mergeKrbContent": {
          "type": "string",
          "description": "合并krb内容"
        },
        "name": {
          "type": "string",
          "description": "keytab名称"
        },
        "principal": {
          "type": "string",
          "description": "principal"
        },
        "principals": {
          "type": "string",
          "description": "可选择的principal"
        }
      },
      "title": "KerberosConfigVO"
    },
    "Map": {
      "type": "object",
      "title": "Map",
      "additionalProperties": {
        "type": "object"
      }
    },
    "Map«string,object»": {
      "type": "object",
      "title": "Map«string,object»",
      "additionalProperties": {
        "type": "object"
      }
    },
    "NodeDescription": {
      "type": "object",
      "properties": {
        "memory": {
          "type": "integer",
          "format": "int32"
        },
        "nodeName": {
          "type": "string"
        },
        "usedMemory": {
          "type": "integer",
          "format": "int32"
        },
        "usedVirtualCores": {
          "type": "integer",
          "format": "int32"
        },
        "virtualCores": {
          "type": "integer",
          "format": "int32"
        }
      },
      "title": "NodeDescription"
    },
    "PageResult": {
      "type": "object",
      "properties": {
        "currentPage": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "object"
        },
        "pageSize": {
          "type": "integer",
          "format": "int32"
        },
        "totalCount": {
          "type": "integer",
          "format": "int64"
        },
        "totalPage": {
          "type": "integer",
          "format": "int32"
        }
      },
      "title": "PageResult"
    },
    "PageResult«List«ClusterInfoVO»»": {
      "type": "object",
      "properties": {
        "currentPage": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/ClusterInfoVO"
          }
        },
        "pageSize": {
          "type": "integer",
          "format": "int32"
        },
        "totalCount": {
          "type": "integer",
          "format": "int64"
        },
        "totalPage": {
          "type": "integer",
          "format": "int32"
        }
      },
      "title": "PageResult«List«ClusterInfoVO»»"
    },
    "PageResult«List«ClusterTenantVO»»": {
      "type": "object",
      "properties": {
        "currentPage": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/ClusterTenantVO"
          }
        },
        "pageSize": {
          "type": "integer",
          "format": "int32"
        },
        "totalCount": {
          "type": "integer",
          "format": "int64"
        },
        "totalPage": {
          "type": "integer",
          "format": "int32"
        }
      },
      "title": "PageResult«List«ClusterTenantVO»»"
    },
    "PageResult«List«ReturnFillDataListVO»»": {
      "type": "object",
      "properties": {
        "currentPage": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/ReturnFillDataListVO"
          }
        },
        "pageSize": {
          "type": "integer",
          "format": "int32"
        },
        "totalCount": {
          "type": "integer",
          "format": "int64"
        },
        "totalPage": {
          "type": "integer",
          "format": "int32"
        }
      },
      "title": "PageResult«List«ReturnFillDataListVO»»"
    },
    "PageResult«List«ReturnJobListVO»»": {
      "type": "object",
      "properties": {
        "currentPage": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/ReturnJobListVO"
          }
        },
        "pageSize": {
          "type": "integer",
          "format": "int32"
        },
        "totalCount": {
          "type": "integer",
          "format": "int64"
        },
        "totalPage": {
          "type": "integer",
          "format": "int32"
        }
      },
      "title": "PageResult«List«ReturnJobListVO»»"
    },
    "PageResult«List«ReturnScheduleTaskVO»»": {
      "type": "object",
      "properties": {
        "currentPage": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/ReturnScheduleTaskVO"
          }
        },
        "pageSize": {
          "type": "integer",
          "format": "int32"
        },
        "totalCount": {
          "type": "integer",
          "format": "int64"
        },
        "totalPage": {
          "type": "integer",
          "format": "int32"
        }
      },
      "title": "PageResult«List«ReturnScheduleTaskVO»»"
    },
    "PageResult«List«数据源列表信息»»": {
      "type": "object",
      "properties": {
        "currentPage": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/数据源列表信息"
          }
        },
        "pageSize": {
          "type": "integer",
          "format": "int32"
        },
        "totalCount": {
          "type": "integer",
          "format": "int64"
        },
        "totalPage": {
          "type": "integer",
          "format": "int32"
        }
      },
      "title": "PageResult«List«数据源列表信息»»"
    },
    "PageResult«ReturnFillDataJobListVO»": {
      "type": "object",
      "properties": {
        "currentPage": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/ReturnFillDataJobListVO"
        },
        "pageSize": {
          "type": "integer",
          "format": "int32"
        },
        "totalCount": {
          "type": "integer",
          "format": "int64"
        },
        "totalPage": {
          "type": "integer",
          "format": "int32"
        }
      },
      "title": "PageResult«ReturnFillDataJobListVO»"
    },
    "ParamAction": {
      "type": "object",
      "properties": {
        "applicationId": {
          "type": "string"
        },
        "componentVersion": {
          "type": "string"
        },
        "computeType": {
          "type": "integer",
          "format": "int32"
        },
        "deployMode": {
          "type": "string"
        },
        "engineTaskId": {
          "type": "string"
        },
        "engineType": {
          "type": "string"
        },
        "exeArgs": {
          "type": "string"
        },
        "externalPath": {
          "type": "string"
        },
        "generateTime": {
          "type": "integer",
          "format": "int64"
        },
        "groupName": {
          "type": "string"
        },
        "jobId": {
          "type": "string"
        },
        "lackingCount": {
          "type": "integer",
          "format": "int64"
        },
        "maxRetryNum": {
          "type": "integer",
          "format": "int32"
        },
        "name": {
          "type": "string"
        },
        "pluginInfo": {
          "type": "object"
        },
        "priority": {
          "type": "integer",
          "format": "int64"
        },
        "retryIntervalTime": {
          "type": "integer",
          "format": "int64"
        },
        "sqlText": {
          "type": "string"
        },
        "submitExpiredTime": {
          "type": "integer",
          "format": "int64"
        },
        "taskParams": {
          "type": "string"
        },
        "taskType": {
          "type": "integer",
          "format": "int32"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64"
        },
        "type": {
          "type": "integer",
          "format": "int32"
        }
      },
      "title": "ParamAction"
    },
    "QueryFillDataJobListVO": {
      "type": "object",
      "required": [
        "fillId"
      ],
      "properties": {
        "currentPage": {
          "type": "integer",
          "format": "int32"
        },
        "cycEndDay": {
          "type": "integer",
          "format": "int64",
          "description": "计算执行的结束时间"
        },
        "cycSort": {
          "type": "string",
          "description": "按计划时间排序"
        },
        "cycStartDay": {
          "type": "integer",
          "format": "int64",
          "description": "计算执行的开始时间"
        },
        "execEndSort": {
          "type": "string",
          "description": "结束时间"
        },
        "execStartSort": {
          "type": "string",
          "description": "按开始时间排序"
        },
        "execTimeSort": {
          "type": "string",
          "description": "按运行时长排序"
        },
        "fillId": {
          "type": "integer",
          "format": "int64",
          "description": "补数据id"
        },
        "jobStatusList": {
          "type": "array",
          "description": "状态类型,多个用逗号隔开",
          "items": {
            "type": "integer",
            "format": "int32"
          }
        },
        "ownerId": {
          "type": "integer",
          "format": "int64",
          "description": "用户ID 责任人"
        },
        "pageSize": {
          "type": "integer",
          "format": "int32"
        },
        "retryNumSort": {
          "type": "string",
          "description": "按重试次数排序"
        },
        "taskName": {
          "type": "string",
          "description": "任务名称"
        },
        "taskTypeList": {
          "type": "array",
          "description": "任务类型,多个用逗号隔开",
          "items": {
            "type": "integer",
            "format": "int32"
          }
        }
      },
      "title": "QueryFillDataJobListVO"
    },
    "QueryFillDataListVO": {
      "type": "object",
      "properties": {
        "currentPage": {
          "type": "integer",
          "format": "int32"
        },
        "jobName": {
          "type": "string",
          "description": "补数据名称"
        },
        "ownerId": {
          "type": "integer",
          "format": "int64",
          "description": "操作人用户id"
        },
        "pageSize": {
          "type": "integer",
          "format": "int32"
        },
        "runDay": {
          "type": "string",
          "description": "补数据运行 格式yyyy-MM-dd"
        }
      },
      "title": "QueryFillDataListVO"
    },
    "QueryJobDisplayVO": {
      "type": "object",
      "required": [
        "directType",
        "jobId"
      ],
      "properties": {
        "directType": {
          "type": "integer",
          "format": "int32",
          "description": "查询方向:\nFATHER(1):向上查询 \nCHILD(2):向下查询"
        },
        "jobId": {
          "type": "string",
          "description": "任务id"
        },
        "level": {
          "type": "integer",
          "format": "int32",
          "description": "查询层级"
        }
      },
      "title": "QueryJobDisplayVO"
    },
    "QueryJobListVO": {
      "type": "object",
      "properties": {
        "currentPage": {
          "type": "integer",
          "format": "int32"
        },
        "cycEndDay": {
          "type": "integer",
          "format": "int64",
          "description": "计划结束时间"
        },
        "cycSort": {
          "type": "string",
          "description": "按计划时间排序"
        },
        "cycStartDay": {
          "type": "integer",
          "format": "int64",
          "description": "计划开始时间"
        },
        "execEndSort": {
          "type": "string",
          "description": "结束时间"
        },
        "execStartSort": {
          "type": "string",
          "description": "按开始时间排序"
        },
        "execTimeSort": {
          "type": "string",
          "description": " 按运行时长排序"
        },
        "jobStatusList": {
          "type": "array",
          "description": "任务状态",
          "items": {
            "type": "integer",
            "format": "int32"
          }
        },
        "ownerId": {
          "type": "integer",
          "format": "int64",
          "description": "用户ID 责任人"
        },
        "pageSize": {
          "type": "integer",
          "format": "int32"
        },
        "retryNumSort": {
          "type": "string",
          "description": "按重试次数排序"
        },
        "taskName": {
          "type": "string",
          "description": "任务名称"
        },
        "taskPeriodTypeList": {
          "type": "array",
          "description": "调度周期类型",
          "items": {
            "type": "integer",
            "format": "int32"
          }
        },
        "taskTypeList": {
          "type": "array",
          "description": "任务类型",
          "items": {
            "type": "integer",
            "format": "int32"
          }
        }
      },
      "title": "QueryJobListVO"
    },
    "QueryJobLogVO": {
      "type": "object",
      "required": [
        "jobId"
      ],
      "properties": {
        "jobId": {
          "type": "string",
          "example": 1,
          "description": "任务实例ID"
        },
        "pageInfo": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "页数 默认 1"
        }
      },
      "title": "QueryJobLogVO"
    },
    "QueryJobStatusStatisticsVO": {
      "type": "object",
      "properties": {
        "cycEndDay": {
          "type": "integer",
          "format": "int64",
          "description": "计划结束时间"
        },
        "cycStartDay": {
          "type": "integer",
          "format": "int64",
          "description": "计划开始时间"
        },
        "fillId": {
          "type": "integer",
          "format": "int64",
          "description": "补数据id"
        },
        "jobStatusList": {
          "type": "array",
          "description": "状态",
          "items": {
            "type": "integer",
            "format": "int32"
          }
        },
        "ownerId": {
          "type": "integer",
          "format": "int64",
          "description": "用户ID 责任人"
        },
        "taskName": {
          "type": "string",
          "description": "任务名称"
        },
        "taskPeriodTypeList": {
          "type": "array",
          "description": "调度周期类型",
          "items": {
            "type": "integer",
            "format": "int32"
          }
        },
        "taskTypeList": {
          "type": "array",
          "description": "任务类型",
          "items": {
            "type": "integer",
            "format": "int32"
          }
        },
        "type": {
          "type": "integer",
          "format": "int32",
          "description": "实例类型 周期实例：0, 补数据实例:1;"
        }
      },
      "title": "QueryJobStatusStatisticsVO"
    },
    "QueryTaskDisplayVO": {
      "type": "object",
      "required": [
        "taskId"
      ],
      "properties": {
        "directType": {
          "type": "integer",
          "format": "int32",
          "description": "查询方向:\nFATHER(1):向上查询 \nCHILD(2):向下查询"
        },
        "level": {
          "type": "integer",
          "format": "int32",
          "description": "查询层级: 默认查询一层,该值范围 0<level<20"
        },
        "taskId": {
          "type": "integer",
          "format": "int64",
          "description": "任务id"
        }
      },
      "title": "QueryTaskDisplayVO"
    },
    "QueryTaskListVO": {
      "type": "object",
      "properties": {
        "currentPage": {
          "type": "integer",
          "format": "int32"
        },
        "endModifiedTime": {
          "type": "integer",
          "format": "int64",
          "description": "最近修改的结束时间 单位毫秒"
        },
        "name": {
          "type": "string",
          "description": "任务名称"
        },
        "ownerId": {
          "type": "integer",
          "format": "int64",
          "description": "所属用户"
        },
        "pageSize": {
          "type": "integer",
          "format": "int32"
        },
        "scheduleStatus": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "调度状态：0 正常 1冻结 2停止"
        },
        "startModifiedTime": {
          "type": "integer",
          "format": "int64",
          "description": "最近修改的开始时间 单位毫秒"
        },
        "taskTypeList": {
          "type": "array",
          "description": "任务类型",
          "items": {
            "type": "integer",
            "format": "int32"
          }
        }
      },
      "title": "QueryTaskListVO"
    },
    "QueueDescription": {
      "type": "object",
      "properties": {
        "capacity": {
          "type": "string"
        },
        "childQueues": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/QueueDescription"
          }
        },
        "maximumCapacity": {
          "type": "string"
        },
        "queueName": {
          "type": "string"
        },
        "queuePath": {
          "type": "string"
        },
        "queueState": {
          "type": "string"
        }
      },
      "title": "QueueDescription"
    },
    "QueueVO": {
      "type": "object",
      "properties": {
        "queueId": {
          "type": "integer",
          "format": "int64"
        },
        "queueName": {
          "type": "string"
        }
      },
      "title": "QueueVO"
    },
    "ResourceMetrics": {
      "type": "object",
      "properties": {
        "coresRate": {
          "type": "number",
          "format": "double"
        },
        "memRate": {
          "type": "number",
          "format": "double"
        },
        "totalCores": {
          "type": "integer",
          "format": "int32"
        },
        "totalMem": {
          "type": "number",
          "format": "double"
        },
        "usedCores": {
          "type": "integer",
          "format": "int32"
        },
        "usedMem": {
          "type": "number",
          "format": "double"
        }
      },
      "title": "ResourceMetrics"
    },
    "ReturnDisplayPeriodVO": {
      "type": "object",
      "properties": {
        "cycTime": {
          "type": "string",
          "description": "计划时间"
        },
        "jobId": {
          "type": "integer",
          "format": "int64",
          "example": 123123,
          "description": "实例id"
        },
        "status": {
          "type": "integer",
          "format": "int32",
          "example": 5,
          "description": "实例状态"
        }
      },
      "title": "ReturnDisplayPeriodVO"
    },
    "ReturnFillDataJobListVO": {
      "type": "object",
      "properties": {
        "fillDataJobVOLists": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/FillDataJobVO"
          }
        },
        "fillDataName": {
          "type": "string",
          "example": 123123,
          "description": "补数据名称"
        },
        "fillGenerateStatus": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "REALLY_GENERATED(1,\"表示正在生成\"),FILL_FINISH(2,\"完成生成补数据实例\"),FILL_FAIL(3,\"生成补数据失败\"),"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "补数据ID"
        }
      },
      "title": "ReturnFillDataJobListVO"
    },
    "ReturnFillDataListVO": {
      "type": "object",
      "properties": {
        "allJobSum": {
          "type": "integer",
          "format": "int64",
          "description": "所有job数量"
        },
        "doneJobSum": {
          "type": "integer",
          "format": "int64",
          "description": "完成的job数量"
        },
        "fillDataName": {
          "type": "string",
          "description": "补数据名称"
        },
        "finishedJobSum": {
          "type": "integer",
          "format": "int64",
          "description": "成功job数量"
        },
        "fromDay": {
          "type": "string",
          "example": "2021-12-23",
          "description": "补数据开始时间"
        },
        "gmtCreate": {
          "type": "string",
          "example": "2021-12-24 16:01:02",
          "description": "补数据生成时间"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "补数据标识"
        },
        "ownerId": {
          "type": "integer",
          "format": "int64",
          "description": "操作人id"
        },
        "ownerName": {
          "type": "string",
          "example": "admin@dtstack.com",
          "description": "操作人名称"
        },
        "runDay": {
          "type": "string",
          "example": "2021-12-23",
          "description": "运行日期"
        },
        "toDay": {
          "type": "string",
          "example": "2021-12-23",
          "description": "补数据结束时间"
        }
      },
      "title": "ReturnFillDataListVO"
    },
    "ReturnJobDisplayVO": {
      "type": "object",
      "properties": {
        "directType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "查询方向:\nFATHER(1):向上查询 \nCHILD(2):向下查询"
        },
        "rootNode": {
          "$ref": "#/definitions/JobNodeVO"
        }
      },
      "title": "ReturnJobDisplayVO"
    },
    "ReturnJobListVO": {
      "type": "object",
      "properties": {
        "cycTime": {
          "type": "string",
          "example": "2021-12-21 21:00:00",
          "description": "计划时间"
        },
        "endExecTime": {
          "type": "string",
          "example": "2021-12-21 21:00:00",
          "description": "结束时间"
        },
        "execTime": {
          "type": "string",
          "example": "38秒",
          "description": "运行时长"
        },
        "jobId": {
          "type": "string",
          "example": 123123,
          "description": "实例id"
        },
        "ownerId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "责任人id"
        },
        "ownerName": {
          "type": "string",
          "example": 1,
          "description": "责任人名称"
        },
        "periodType": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "调度类型"
        },
        "retryNum": {
          "type": "integer",
          "format": "int32",
          "example": 3,
          "description": "当前重试次数"
        },
        "startExecTime": {
          "type": "string",
          "example": "2021-12-21 21:00:00",
          "description": "开始时间"
        },
        "status": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "实例状态"
        },
        "taskId": {
          "type": "integer",
          "format": "int64",
          "example": 0,
          "description": "任务id"
        },
        "taskName": {
          "type": "string",
          "example": "任务名称",
          "description": "任务名称"
        },
        "taskType": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "任务类型"
        }
      },
      "title": "ReturnJobListVO"
    },
    "ReturnJobLogVO": {
      "type": "object",
      "properties": {
        "engineLog": {
          "type": "string",
          "example": 123,
          "description": "引擎日志"
        },
        "logInfo": {
          "type": "string",
          "example": {
            "jobid": "application_1634090560347_1335",
            "msg_info": "2021-10-15 00:00:01:submit job is success"
          },
          "description": "提交日志"
        },
        "pageIndex": {
          "type": "integer",
          "format": "int32",
          "example": 3,
          "description": "当前次数"
        },
        "pageSize": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "重试次数"
        },
        "sqlText": {
          "type": "string",
          "example": "select...",
          "description": "任务信息"
        }
      },
      "title": "ReturnJobLogVO"
    },
    "ReturnJobStatusStatisticsVO": {
      "type": "object",
      "properties": {
        "count": {
          "type": "integer",
          "format": "int64",
          "example": 30,
          "description": "状态数量"
        },
        "statusKey": {
          "type": "string",
          "example": "FAILED",
          "description": "状态key"
        }
      },
      "title": "ReturnJobStatusStatisticsVO"
    },
    "ReturnScheduleTaskVO": {
      "type": "object",
      "properties": {
        "gmtModified": {
          "example": 12312333333,
          "description": "提交时间",
          "$ref": "#/definitions/Timestamp"
        },
        "name": {
          "type": "string",
          "example": "这是一个任务",
          "description": "任务名称"
        },
        "ownerUserId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "责任人ID"
        },
        "ownerUserName": {
          "type": "string",
          "example": "admin@dtstack.com",
          "description": "责任人名称"
        },
        "periodType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "调度类型"
        },
        "scheduleStatus": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "调度状态：0 正常 1冻结 2停止"
        },
        "taskId": {
          "type": "integer",
          "format": "int64",
          "example": "1L",
          "description": "任务id"
        },
        "taskType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "任务类型"
        }
      },
      "title": "ReturnScheduleTaskVO"
    },
    "ReturnTaskDisplayVO": {
      "type": "object",
      "properties": {
        "directType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "查询方向:\nFATHER(1):向上查询 \nCHILD(2):向下查询"
        },
        "rootTaskNode": {
          "description": "顶节点（就是vo传过来的节点）",
          "$ref": "#/definitions/TaskNodeVO"
        }
      },
      "title": "ReturnTaskDisplayVO"
    },
    "ReturnTaskSupportTypesVO": {
      "type": "object",
      "properties": {
        "taskTypeCode": {
          "type": "integer",
          "format": "int32"
        },
        "taskTypeName": {
          "type": "string"
        }
      },
      "title": "ReturnTaskSupportTypesVO"
    },
    "R«ClusterEngineVO»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/ClusterEngineVO"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«ClusterEngineVO»"
    },
    "R«ClusterResource»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/ClusterResource"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«ClusterResource»"
    },
    "R«ClusterVO»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/ClusterVO"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«ClusterVO»"
    },
    "R«Collection«Map«string,object»»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/Map«string,object»"
          }
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«Collection«Map«string,object»»»"
    },
    "R«Collection«系统参数»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/系统参数"
          }
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«Collection«系统参数»»"
    },
    "R«ComponentTestResult»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/ComponentTestResult"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«ComponentTestResult»"
    },
    "R«ComponentVO»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/ComponentVO"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«ComponentVO»"
    },
    "R«ConsoleJobVO»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/ConsoleJobVO"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«ConsoleJobVO»"
    },
    "R«JSONObject»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "object",
          "additionalProperties": {
            "type": "object"
          }
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«JSONObject»"
    },
    "R«KerberosConfigVO»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/KerberosConfigVO"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«KerberosConfigVO»"
    },
    "R«List«ClientTemplate»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/ClientTemplate"
          }
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«List«ClientTemplate»»"
    },
    "R«List«ClusterInfoVO»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/ClusterInfoVO"
          }
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«List«ClusterInfoVO»»"
    },
    "R«List«ComponentMultiTestResult»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/ComponentMultiTestResult"
          }
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«List«ComponentMultiTestResult»»"
    },
    "R«List«JSONObject»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/JSONObject"
          }
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«List«JSONObject»»"
    },
    "R«List«ReturnDisplayPeriodVO»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/ReturnDisplayPeriodVO"
          }
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«List«ReturnDisplayPeriodVO»»"
    },
    "R«List«ReturnJobListVO»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/ReturnJobListVO"
          }
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«List«ReturnJobListVO»»"
    },
    "R«List«ReturnJobStatusStatisticsVO»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/ReturnJobStatusStatisticsVO"
          }
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«List«ReturnJobStatusStatisticsVO»»"
    },
    "R«List«ReturnScheduleTaskVO»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/ReturnScheduleTaskVO"
          }
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«List«ReturnScheduleTaskVO»»"
    },
    "R«List«ReturnTaskSupportTypesVO»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/ReturnTaskSupportTypesVO"
          }
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«List«ReturnTaskSupportTypesVO»»"
    },
    "R«List«TenantVO»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/TenantVO"
          }
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«List«TenantVO»»"
    },
    "R«List«UserVO»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/UserVO"
          }
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«List«UserVO»»"
    },
    "R«List«int»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "type": "integer",
            "format": "int32"
          }
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«List«int»»"
    },
    "R«List«object»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "type": "object"
          }
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«List«object»»"
    },
    "R«List«string»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "type": "string"
          }
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«List«string»»"
    },
    "R«List«任务版本详细信息»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/任务版本详细信息"
          }
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«List«任务版本详细信息»»"
    },
    "R«List«删除任务前置判断»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/删除任务前置判断"
          }
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«List«删除任务前置判断»»"
    },
    "R«List«当前租户支持的数据源类型列表»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/当前租户支持的数据源类型列表"
          }
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«List«当前租户支持的数据源类型列表»»"
    },
    "R«List«数据源分类类目模型»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/数据源分类类目模型"
          }
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«List«数据源分类类目模型»»"
    },
    "R«List«数据源列表信息»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/数据源列表信息"
          }
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«List«数据源列表信息»»"
    },
    "R«List«数据源版本视图类»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/数据源版本视图类"
          }
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«List«数据源版本视图类»»"
    },
    "R«List«数据源类型视图类»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/数据源类型视图类"
          }
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«List«数据源类型视图类»»"
    },
    "R«List«组件版本号»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/组件版本号"
          }
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«List«组件版本号»»"
    },
    "R«List«资源信息»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/资源信息"
          }
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«List«资源信息»»"
    },
    "R«List«返回可依赖的任务»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/返回可依赖的任务"
          }
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«List«返回可依赖的任务»»"
    },
    "R«Map»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "object"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«Map»"
    },
    "R«PageResult«List«ClusterInfoVO»»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/PageResult«List«ClusterInfoVO»»"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«PageResult«List«ClusterInfoVO»»»"
    },
    "R«PageResult«List«ClusterTenantVO»»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/PageResult«List«ClusterTenantVO»»"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«PageResult«List«ClusterTenantVO»»»"
    },
    "R«PageResult«List«ReturnFillDataListVO»»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/PageResult«List«ReturnFillDataListVO»»"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«PageResult«List«ReturnFillDataListVO»»»"
    },
    "R«PageResult«List«ReturnJobListVO»»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/PageResult«List«ReturnJobListVO»»"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«PageResult«List«ReturnJobListVO»»»"
    },
    "R«PageResult«List«ReturnScheduleTaskVO»»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/PageResult«List«ReturnScheduleTaskVO»»"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«PageResult«List«ReturnScheduleTaskVO»»»"
    },
    "R«PageResult«List«数据源列表信息»»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/PageResult«List«数据源列表信息»»"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«PageResult«List«数据源列表信息»»»"
    },
    "R«PageResult«ReturnFillDataJobListVO»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/PageResult«ReturnFillDataJobListVO»"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«PageResult«ReturnFillDataJobListVO»»"
    },
    "R«PageResult»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/PageResult"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«PageResult»"
    },
    "R«ReturnJobDisplayVO»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/ReturnJobDisplayVO"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«ReturnJobDisplayVO»"
    },
    "R«ReturnJobLogVO»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/ReturnJobLogVO"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«ReturnJobLogVO»"
    },
    "R«ReturnTaskDisplayVO»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/ReturnTaskDisplayVO"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«ReturnTaskDisplayVO»"
    },
    "R«Set«JSONObject»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/JSONObject"
          }
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«Set«JSONObject»»"
    },
    "R«Set«string»»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "array",
          "items": {
            "type": "string"
          }
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«Set«string»»"
    },
    "R«Void»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«Void»"
    },
    "R«boolean»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "boolean"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«boolean»"
    },
    "R«int»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "integer",
          "format": "int32"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«int»"
    },
    "R«long»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "integer",
          "format": "int64"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«long»"
    },
    "R«sql或者脚本执行日志信息»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/sql或者脚本执行日志信息"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«sql或者脚本执行日志信息»"
    },
    "R«sql或者脚本执行状态信息»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/sql或者脚本执行状态信息"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«sql或者脚本执行状态信息»"
    },
    "R«sql或者脚本执行结果信息»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/sql或者脚本执行结果信息"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«sql或者脚本执行结果信息»"
    },
    "R«string»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "type": "string"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«string»"
    },
    "R«任务信息»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/任务信息"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«任务信息»"
    },
    "R«任务版本详细信息»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/任务版本详细信息"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«任务版本详细信息»"
    },
    "R«任务目录信息»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/任务目录信息"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«任务目录信息»"
    },
    "R«函数查询结果信息»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/函数查询结果信息"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«函数查询结果信息»"
    },
    "R«函数结果信息»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/函数结果信息"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«函数结果信息»"
    },
    "R«发布信息信息»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/发布信息信息"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«发布信息信息»"
    },
    "R«数据源基本信息»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/数据源基本信息"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«数据源基本信息»"
    },
    "R«数据表单模版视图类»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/数据表单模版视图类"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«数据表单模版视图类»"
    },
    "R«根据jobId获取日志结果信息»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/根据jobId获取日志结果信息"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«根据jobId获取日志结果信息»"
    },
    "R«根据类型获取日志返回信息»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/根据类型获取日志返回信息"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«根据类型获取日志返回信息»"
    },
    "R«目录结果信息»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/目录结果信息"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«目录结果信息»"
    },
    "R«获取同步任务运行状态返回信息»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/获取同步任务运行状态返回信息"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«获取同步任务运行状态返回信息»"
    },
    "R«获取资源返回信息»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/获取资源返回信息"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«获取资源返回信息»"
    },
    "R«读写锁信息»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/读写锁信息"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«读写锁信息»"
    },
    "R«运行sql返回信息»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/运行sql返回信息"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«运行sql返回信息»"
    },
    "R«运行同步任务返回信息»": {
      "type": "object",
      "properties": {
        "code": {
          "type": "integer",
          "format": "int32"
        },
        "data": {
          "$ref": "#/definitions/运行同步任务返回信息"
        },
        "message": {
          "type": "string"
        },
        "space": {
          "type": "integer",
          "format": "int64"
        },
        "success": {
          "type": "boolean"
        },
        "version": {
          "type": "string"
        }
      },
      "title": "R«运行同步任务返回信息»"
    },
    "ScheduleFillDataInfoVO": {
      "type": "object",
      "required": [
        "fillDataType"
      ],
      "properties": {
        "fillDataType": {
          "type": "integer",
          "format": "int32",
          "description": "补数据类型： 0 批量补数据fillDataType = 0时，taskIds字段有效"
        },
        "rootTaskId": {
          "description": "批量补数据任务列表:fillDataType = 0有效,rootTaskId优先级大于taskIds",
          "$ref": "#/definitions/FillDataChooseTaskVO"
        },
        "taskIds": {
          "type": "array",
          "description": "批量补数据任务列表:fillDataType = 0 且 rootTaskId == null的时候，有效",
          "items": {
            "$ref": "#/definitions/FillDataChooseTaskVO"
          }
        }
      },
      "title": "ScheduleFillDataInfoVO"
    },
    "ScheduleFillJobParticipateVO": {
      "type": "object",
      "required": [
        "endDay",
        "fillName",
        "startDay",
        "tenantId",
        "userId"
      ],
      "properties": {
        "beginTime": {
          "type": "string",
          "description": "结束时间：精确到日 时间格式： yyyy-MM-dd"
        },
        "endDay": {
          "type": "string",
          "description": "结束时间：精确到日 时间格式： yyyy-MM-dd"
        },
        "endTime": {
          "type": "string",
          "description": "每天补数据的结束时间 时间格式：HH:mm"
        },
        "fillDataInfo": {
          "$ref": "#/definitions/ScheduleFillDataInfoVO"
        },
        "fillName": {
          "type": "string",
          "description": "补数据名称"
        },
        "startDay": {
          "type": "string",
          "description": "开始日期：精确到日 时间格式： yyyy-MM-dd"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "description": "触发补数据事件的用户Id"
        }
      },
      "title": "ScheduleFillJobParticipateVO"
    },
    "SchedulingVo": {
      "type": "object",
      "properties": {
        "components": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/IComponentVO"
          }
        },
        "schedulingCode": {
          "type": "integer",
          "format": "int32"
        },
        "schedulingName": {
          "type": "string"
        }
      },
      "title": "SchedulingVo"
    },
    "TaskNodeVO": {
      "type": "object",
      "properties": {
        "childNode": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/TaskNodeVO"
          }
        },
        "gmtCreate": {
          "description": "发布时间",
          "$ref": "#/definitions/Timestamp"
        },
        "isFlowTask": {
          "type": "boolean",
          "description": "是否是工作流任务"
        },
        "parentNode": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/TaskNodeVO"
          }
        },
        "scheduleStatus": {
          "type": "integer",
          "format": "int32",
          "description": "调度状态：0 正常 1冻结 2停止"
        },
        "taskId": {
          "type": "integer",
          "format": "int64",
          "description": "任务id"
        },
        "taskName": {
          "type": "string",
          "description": "任务名称"
        },
        "taskType": {
          "type": "integer",
          "format": "int32",
          "description": "任务类型"
        }
      },
      "title": "TaskNodeVO"
    },
    "TenantVO": {
      "type": "object",
      "properties": {
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        },
        "tenantName": {
          "type": "string",
          "description": "租户名称"
        }
      },
      "title": "TenantVO"
    },
    "Timestamp": {
      "type": "object",
      "properties": {
        "date": {
          "type": "integer",
          "format": "int32"
        },
        "day": {
          "type": "integer",
          "format": "int32"
        },
        "hours": {
          "type": "integer",
          "format": "int32"
        },
        "minutes": {
          "type": "integer",
          "format": "int32"
        },
        "month": {
          "type": "integer",
          "format": "int32"
        },
        "nanos": {
          "type": "integer",
          "format": "int32"
        },
        "seconds": {
          "type": "integer",
          "format": "int32"
        },
        "time": {
          "type": "integer",
          "format": "int64"
        },
        "timezoneOffset": {
          "type": "integer",
          "format": "int32"
        },
        "year": {
          "type": "integer",
          "format": "int32"
        }
      },
      "title": "Timestamp"
    },
    "User": {
      "type": "object",
      "properties": {
        "email": {
          "type": "string"
        },
        "gmtCreate": {
          "$ref": "#/definitions/Timestamp"
        },
        "gmtModified": {
          "$ref": "#/definitions/Timestamp"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "isDeleted": {
          "type": "integer",
          "format": "int32"
        },
        "password": {
          "type": "string"
        },
        "phoneNumber": {
          "type": "string"
        },
        "status": {
          "type": "integer",
          "format": "int32"
        },
        "userName": {
          "type": "string"
        }
      },
      "title": "User"
    },
    "UserVO": {
      "type": "object",
      "properties": {
        "email": {
          "type": "string"
        },
        "id": {
          "type": "integer",
          "format": "int64"
        },
        "phoneNumber": {
          "type": "string"
        },
        "status": {
          "type": "integer",
          "format": "int32"
        },
        "userName": {
          "type": "string"
        }
      },
      "title": "UserVO"
    },
    "sql或者脚本执行日志信息": {
      "type": "object",
      "properties": {
        "download": {
          "type": "string",
          "description": "下载路径"
        },
        "jobId": {
          "type": "string",
          "example": 3,
          "description": "任务 ID"
        },
        "msg": {
          "type": "string",
          "example": "test",
          "description": "消息"
        },
        "retryLog": {
          "type": "boolean",
          "example": false,
          "description": "是否需要重新获取日志"
        },
        "sqlText": {
          "type": "string",
          "example": "select * from test",
          "description": "sql"
        },
        "taskType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "引擎类别"
        }
      },
      "title": "sql或者脚本执行日志信息"
    },
    "sql或者脚本执行状态信息": {
      "type": "object",
      "properties": {
        "jobId": {
          "type": "string",
          "example": 3,
          "description": "任务 ID"
        },
        "sqlText": {
          "type": "string",
          "example": "select * from test",
          "description": "sql"
        },
        "status": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "状态"
        },
        "taskType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "引擎类别"
        }
      },
      "title": "sql或者脚本执行状态信息"
    },
    "sql或者脚本执行结果信息": {
      "type": "object",
      "properties": {
        "jobId": {
          "type": "string",
          "example": 3,
          "description": "任务 ID"
        },
        "result": {
          "type": "array",
          "description": "执行结果",
          "items": {
            "type": "object"
          }
        },
        "sqlText": {
          "type": "string",
          "example": "select * from test",
          "description": "sql"
        },
        "taskType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "引擎类别"
        }
      },
      "title": "sql或者脚本执行结果信息"
    },
    "任务依赖的任务信息": {
      "type": "object",
      "required": [
        "id"
      ],
      "properties": {
        "appType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "产品类型"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "任务 ID"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "租户 ID"
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "用户id"
        }
      },
      "title": "任务依赖的任务信息"
    },
    "任务信息": {
      "type": "object",
      "required": [
        "computeType",
        "dependencyTaskId",
        "gmtModified",
        "ignoreCheck",
        "isFile",
        "pageNo",
        "pageSize",
        "pid",
        "scheduleStatus",
        "sqlText",
        "taskType",
        "type"
      ],
      "properties": {
        "appType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "平台类别"
        },
        "componentVersion": {
          "type": "string",
          "example": 111,
          "description": "组件版本号"
        },
        "computeType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "计算类型 0实时，1 离线"
        },
        "createModel": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "0-向导模式,1-脚本模式"
        },
        "createUser": {
          "description": "任务创建人信息",
          "$ref": "#/definitions/根据任务id详情返回信息"
        },
        "createUserId": {
          "type": "integer",
          "format": "int64",
          "example": 3,
          "description": "新建task的用户"
        },
        "cron": {
          "type": "string",
          "example": "* 0/1 * * * *",
          "description": "定时周期表达式"
        },
        "currentProject": {
          "type": "boolean",
          "example": true,
          "description": "是否是当前项目"
        },
        "dataSourceId": {
          "type": "integer",
          "format": "int64",
          "example": 24,
          "description": "数据源 ID"
        },
        "dependencyTaskId": {
          "type": "integer",
          "format": "int64",
          "example": 3,
          "description": "任务依赖 ID"
        },
        "dependencyTasks": {
          "type": "array",
          "description": "依赖任务信息",
          "items": {
            "$ref": "#/definitions/任务基本信息"
          }
        },
        "empyt": {
          "type": "string"
        },
        "engineType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "执行引擎类型 0 flink, 1 spark"
        },
        "exeArgs": {
          "type": "string",
          "example": 1,
          "description": "参数 ID"
        },
        "extraInfo": {
          "type": "string",
          "description": "扩展信息"
        },
        "flowId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": " 所属工作流id"
        },
        "flowName": {
          "type": "string",
          "example": "数据同步test",
          "description": "工作流名称"
        },
        "forceUpdate": {
          "type": "boolean",
          "example": false,
          "description": "是否覆盖更新"
        },
        "gmtCreate": {
          "example": "2020-12-29T11:39:13.000+00:00",
          "description": "创建时间",
          "$ref": "#/definitions/Timestamp"
        },
        "gmtModified": {
          "description": "修改时间",
          "$ref": "#/definitions/Timestamp"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "主键 ID"
        },
        "ignoreCheck": {
          "type": "boolean",
          "example": true,
          "description": "忽略检查"
        },
        "increColumn": {
          "type": "string",
          "description": "自增字段"
        },
        "input": {
          "type": "string",
          "description": "输入数据文件的路径"
        },
        "isDeleted": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "是否删除"
        },
        "isEditBaseInfo": {
          "type": "boolean",
          "example": false,
          "description": "是否是右键编辑任务"
        },
        "isExpire": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "是否过期"
        },
        "isFile": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "是否是文件"
        },
        "isPublishToProduce": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "是否发布到了生产环境"
        },
        "learningType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "0-TensorFlow,1-MXNet"
        },
        "lockVersion": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "锁版本 ID"
        },
        "mainClass": {
          "type": "string",
          "example": 1,
          "description": "入口类"
        },
        "modifyUser": {
          "description": "任务修改人信息",
          "$ref": "#/definitions/根据任务id详情返回信息"
        },
        "modifyUserId": {
          "type": "integer",
          "format": "int64",
          "example": 3,
          "description": "最后修改task的用户"
        },
        "name": {
          "type": "string",
          "example": "test",
          "description": "任务名称"
        },
        "nodePName": {
          "type": "string",
          "example": "数据开发",
          "description": "节点名称"
        },
        "nodePid": {
          "type": "integer",
          "format": "int64",
          "example": 7,
          "description": "节点 ID"
        },
        "operateModel": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "操作模式 0-资源模式，1-编辑模式D"
        },
        "options": {
          "type": "string",
          "description": "脚本的命令行参数"
        },
        "output": {
          "type": "string",
          "description": "输出模型的路径"
        },
        "ownerUser": {
          "description": "任务责任人信息",
          "$ref": "#/definitions/根据任务id详情返回信息"
        },
        "ownerUserId": {
          "type": "integer",
          "format": "int64",
          "example": 111,
          "description": "负责人id"
        },
        "pageNo": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "当前页"
        },
        "pageSize": {
          "type": "integer",
          "format": "int32",
          "example": 10,
          "description": "展示条数"
        },
        "parentReadWriteLockVersion": {
          "type": "integer",
          "format": "int32",
          "example": 43,
          "description": "工作流父任务版本号  用于子任务获取父任务锁"
        },
        "periodType": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "周期类型"
        },
        "pid": {
          "type": "integer",
          "format": "int32",
          "example": 3,
          "description": "父id"
        },
        "preSave": {
          "type": "boolean",
          "example": false
        },
        "projectId": {
          "type": "integer",
          "format": "int64",
          "example": 5,
          "description": "项目 ID"
        },
        "projectName": {
          "type": "string",
          "example": "dev开发",
          "description": "项目名称"
        },
        "projectScheduleStatus": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "启动:0 停止:1"
        },
        "publishDesc": {
          "type": "string",
          "example": "test",
          "description": "发布描述"
        },
        "pythonVersion": {
          "type": "integer",
          "format": "int32",
          "example": 2,
          "description": "2-python2.x,3-python3.xD"
        },
        "readWriteLockVO": {
          "description": "读写锁",
          "$ref": "#/definitions/读写锁信息"
        },
        "refResourceIdList": {
          "type": "array",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "refResourceList": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/资源信息"
          }
        },
        "relatedTasks": {
          "type": "array",
          "description": "任务信息",
          "items": {
            "$ref": "#/definitions/调度任务信息"
          }
        },
        "resourceIdList": {
          "type": "array",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "resourceList": {
          "type": "array",
          "items": {
            "$ref": "#/definitions/资源信息"
          }
        },
        "scheduleConf": {
          "type": "string",
          "description": "调度配置"
        },
        "scheduleStatus": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "调度状态"
        },
        "selectTenantId": {
          "type": "integer",
          "format": "int64",
          "description": "选择的租户id"
        },
        "settingMap": {
          "type": "object",
          "description": "设置"
        },
        "sourceMap": {
          "type": "object"
        },
        "sqlText": {
          "type": "string",
          "example": "show tables",
          "description": "sql 文本"
        },
        "status": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "任务发布状态，前端使用D"
        },
        "subNodes": {
          "description": "任务信息",
          "$ref": "#/definitions/调度任务信息"
        },
        "subTaskVOS": {
          "type": "array",
          "description": "任务信息",
          "items": {
            "$ref": "#/definitions/调度任务信息"
          }
        },
        "submitStatus": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "提交状态"
        },
        "syncModel": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "同步模式 0-无增量标识，1-有增量标识"
        },
        "targetMap": {
          "type": "object"
        },
        "taskDesc": {
          "type": "string",
          "example": "tes",
          "description": "任务描述"
        },
        "taskId": {
          "type": "integer",
          "format": "int64",
          "description": "当前需要添加依赖的任务"
        },
        "taskName": {
          "type": "string",
          "description": "任务名称"
        },
        "taskParams": {
          "type": "string",
          "example": {},
          "description": "任务参数"
        },
        "taskPeriodId": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "任务周期 ID"
        },
        "taskPeriodType": {
          "type": "string",
          "example": 2,
          "description": "任务周期类别"
        },
        "taskType": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "任务类型 0 sql，1 mr，2 sync ，3 python"
        },
        "taskVOS": {
          "type": "array",
          "description": "任务信息",
          "items": {
            "$ref": "#/definitions/调度任务信息"
          }
        },
        "taskVariables": {
          "type": "array",
          "description": "任务版本 ID",
          "items": {
            "$ref": "#/definitions/Map"
          }
        },
        "taskVersions": {
          "type": "array",
          "description": "任务版本信息",
          "items": {
            "$ref": "#/definitions/任务版本详细信息"
          }
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "租户id"
        },
        "tenantName": {
          "type": "string",
          "example": "dev租户",
          "description": "租户名称"
        },
        "toUpdateTasks": {
          "type": "array",
          "description": "任务流中待更新的子任务D",
          "items": {
            "$ref": "#/definitions/任务信息"
          }
        },
        "type": {
          "type": "string",
          "example": 1,
          "description": "类别"
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "用户id"
        },
        "version": {
          "type": "integer",
          "format": "int32",
          "example": 14,
          "description": "任务版本 ID"
        },
        "versionId": {
          "type": "integer",
          "format": "int32",
          "example": 23,
          "description": "batchJob执行的时候的vesion版本"
        }
      },
      "title": "任务信息"
    },
    "任务基本信息": {
      "type": "object",
      "required": [
        "computeType",
        "engineType",
        "gmtModified",
        "name",
        "periodType",
        "scheduleConf",
        "scheduleStatus",
        "sqlText",
        "taskParams",
        "taskType"
      ],
      "properties": {
        "appType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "平台类别"
        },
        "computeType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "计算类型 0实时，1 离线"
        },
        "createUserId": {
          "type": "integer",
          "format": "int64",
          "example": 3,
          "description": "新建task的用户"
        },
        "engineType": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "执行引擎类型 0 flink, 1 spark"
        },
        "exeArgs": {
          "type": "string",
          "example": "1,2",
          "description": "参数"
        },
        "flowId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "所属工作流id"
        },
        "gmtModified": {
          "description": "修改时间",
          "$ref": "#/definitions/Timestamp"
        },
        "isExpire": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "是否过期"
        },
        "mainClass": {
          "type": "string",
          "example": "Abc.java",
          "description": "入口类"
        },
        "modifyUserId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "最后修改task的用户"
        },
        "name": {
          "type": "string",
          "example": "spark_test",
          "description": "任务名称"
        },
        "nodePid": {
          "type": "integer",
          "format": "int64",
          "example": 7,
          "description": "节点父ID"
        },
        "ownerUserId": {
          "type": "integer",
          "format": "int64",
          "example": 11,
          "description": "负责人id"
        },
        "periodType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "周期类型"
        },
        "scheduleConf": {
          "type": "string",
          "example": {
            "selfReliance": false
          },
          "description": "调度配置"
        },
        "scheduleStatus": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "调度状态"
        },
        "sqlText": {
          "type": "string",
          "example": "shwo tables;",
          "description": "sql 文本"
        },
        "status": {
          "type": "integer",
          "format": "int32",
          "example": "spark_test",
          "description": "任务发布状态，前端使用"
        },
        "submitStatus": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "提交状态"
        },
        "taskDesc": {
          "type": "string",
          "example": "测试",
          "description": "任务描述"
        },
        "taskParams": {
          "type": "string",
          "description": "任务参数"
        },
        "taskType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "任务类型 0 sql，1 mr，2 sync ，3 python"
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "用户id"
        },
        "version": {
          "type": "integer",
          "format": "int32",
          "example": 11,
          "description": "task版本"
        }
      },
      "title": "任务基本信息"
    },
    "任务版本详细信息": {
      "type": "object",
      "properties": {
        "appType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "平台类型"
        },
        "createUserId": {
          "type": "integer",
          "format": "int64",
          "example": 35,
          "description": "新建task的用户"
        },
        "dependencyTaskIds": {
          "type": "string",
          "example": 23,
          "description": "依赖的任务id"
        },
        "dependencyTaskNames": {
          "type": "array",
          "description": "依赖任务名称",
          "items": {
            "type": "string"
          }
        },
        "dependencyTasks": {
          "type": "object",
          "description": "依赖任务信息",
          "additionalProperties": {
            "type": "object"
          }
        },
        "gmtCreate": {
          "example": "2020-12-29T11:39:13.000+00:00",
          "description": "创建时间",
          "$ref": "#/definitions/Timestamp"
        },
        "gmtModified": {
          "example": "2020-12-29T11:39:13.000+00:00",
          "description": "修改时间",
          "$ref": "#/definitions/Timestamp"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "主键id"
        },
        "isDeleted": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "是否删除"
        },
        "originSql": {
          "type": "string",
          "description": "sql"
        },
        "projectId": {
          "type": "integer",
          "format": "int64",
          "example": 3,
          "description": "项目 ID"
        },
        "publishDesc": {
          "type": "string",
          "example": "test",
          "description": "发布备注"
        },
        "scheduleConf": {
          "type": "string",
          "description": "调度信息"
        },
        "scheduleStatus": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "调度状态"
        },
        "sqlText": {
          "type": "string",
          "example": "use dev",
          "description": "sql 文本"
        },
        "taskId": {
          "type": "integer",
          "format": "int64",
          "example": 3,
          "description": "任务 ID"
        },
        "taskParams": {
          "type": "string",
          "description": "环境参数"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "example": 3,
          "description": "数栈租户 ID"
        },
        "userName": {
          "type": "string",
          "example": "测试用户",
          "description": "用户名"
        },
        "version": {
          "type": "integer",
          "format": "int32",
          "example": 23,
          "description": "task版本"
        }
      },
      "title": "任务版本详细信息"
    },
    "任务目录信息": {
      "type": "object",
      "properties": {
        "catalogueType": {
          "type": "string",
          "example": "SystemFunction",
          "description": "目录类型"
        },
        "catalogues": {
          "type": "array",
          "description": "目录信息",
          "items": {
            "$ref": "#/definitions/目录信息"
          }
        },
        "children": {
          "type": "array",
          "description": "子目录列表",
          "items": {
            "$ref": "#/definitions/目录结果信息"
          }
        },
        "createUser": {
          "type": "string",
          "example": "test",
          "description": "创建用户"
        },
        "dependencyTasks": {
          "type": "array",
          "description": "依赖任务",
          "items": {
            "$ref": "#/definitions/任务信息"
          }
        },
        "engineType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "engine类型"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "目录id"
        },
        "isSubTask": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "是否为子任务"
        },
        "learningType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "learning类型"
        },
        "level": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "目录层级"
        },
        "lists": {
          "type": "array",
          "description": "列表信息",
          "items": {
            "type": "array",
            "items": {
              "type": "object"
            }
          }
        },
        "name": {
          "type": "string",
          "example": "name",
          "description": "目录名称"
        },
        "operateModel": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "操作模式"
        },
        "orderVal": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "节点值"
        },
        "parentId": {
          "type": "integer",
          "format": "int64",
          "example": 0,
          "description": "父目录id"
        },
        "projectAlias": {
          "type": "string",
          "example": "project_alia",
          "description": "项目别名"
        },
        "pythonVersion": {
          "type": "integer",
          "format": "int32",
          "example": 2,
          "description": "python版本"
        },
        "readWriteLockVO": {
          "description": "读写锁",
          "$ref": "#/definitions/读写锁信息"
        },
        "resourceType": {
          "type": "integer",
          "format": "int32",
          "description": "资源类型"
        },
        "scheduleStatus": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "调度状态"
        },
        "scriptType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "脚本类型"
        },
        "status": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "任务状态"
        },
        "submitStatus": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "提交状态"
        },
        "taskType": {
          "type": "integer",
          "format": "int32",
          "description": "任务类别"
        },
        "tasks": {
          "type": "array",
          "description": "任务信息",
          "items": {
            "$ref": "#/definitions/任务信息"
          }
        },
        "type": {
          "type": "string",
          "example": "folder",
          "description": "目录类型"
        },
        "version": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "版本"
        }
      },
      "title": "任务目录信息"
    },
    "任务血缘关系信息": {
      "type": "object",
      "required": [
        "dependencyVOS",
        "taskId"
      ],
      "properties": {
        "dependencyVOS": {
          "type": "array",
          "description": "父任务列表",
          "items": {
            "$ref": "#/definitions/任务依赖的任务信息"
          }
        },
        "taskId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "任务 ID"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "租户id"
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "用户id"
        }
      },
      "title": "任务血缘关系信息"
    },
    "函数任务信息": {
      "type": "object",
      "properties": {
        "appType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "平台类别"
        },
        "computeType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "计算类型 0实时，1 离线"
        },
        "createUserId": {
          "type": "integer",
          "format": "int64",
          "example": 3,
          "description": "新建task的用户"
        },
        "engineType": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "执行引擎类型 0 flink, 1 spark"
        },
        "exeArgs": {
          "type": "string",
          "example": "1,2",
          "description": "参数"
        },
        "flowId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "所属工作流id"
        },
        "gmtCreate": {
          "description": "创建时间",
          "$ref": "#/definitions/Timestamp"
        },
        "gmtModified": {
          "description": "修改时间",
          "$ref": "#/definitions/Timestamp"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "id"
        },
        "isDeleted": {
          "type": "integer",
          "format": "int32",
          "description": "是否删除"
        },
        "isExpire": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "是否过期"
        },
        "mainClass": {
          "type": "string",
          "example": "Abc.java",
          "description": "入口类"
        },
        "modifyUserId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "最后修改task的用户"
        },
        "name": {
          "type": "string",
          "example": "spark_test",
          "description": "任务名称"
        },
        "nodePid": {
          "type": "integer",
          "format": "int64",
          "example": 7,
          "description": "节点父ID"
        },
        "ownerUserId": {
          "type": "integer",
          "format": "int64",
          "example": 11,
          "description": "负责人id"
        },
        "periodType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "周期类型"
        },
        "scheduleConf": {
          "type": "string",
          "example": {
            "selfReliance": false
          },
          "description": "调度配置"
        },
        "scheduleStatus": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "调度状态"
        },
        "sqlText": {
          "type": "string",
          "example": "shwo tables;",
          "description": "sql 文本"
        },
        "status": {
          "type": "integer",
          "format": "int32",
          "example": "spark_test",
          "description": "任务发布状态，前端使用"
        },
        "submitStatus": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "提交状态"
        },
        "taskDesc": {
          "type": "string",
          "example": "测试",
          "description": "任务描述"
        },
        "taskParams": {
          "type": "string",
          "description": "任务参数"
        },
        "taskType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "任务类型 0 sql，1 mr，2 sync ，3 python"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户 ID"
        },
        "version": {
          "type": "integer",
          "format": "int32",
          "example": 11,
          "description": "task版本"
        }
      },
      "title": "函数任务信息"
    },
    "函数删除信息": {
      "type": "object",
      "required": [
        "functionId",
        "userId"
      ],
      "properties": {
        "functionId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "函数id"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "租户id"
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "用户id"
        }
      },
      "title": "函数删除信息"
    },
    "函数名称信息": {
      "type": "object",
      "required": [
        "taskType"
      ],
      "properties": {
        "taskType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "任务类型"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "租户id"
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "用户id"
        }
      },
      "title": "函数名称信息"
    },
    "函数基础信息": {
      "type": "object",
      "required": [
        "functionId"
      ],
      "properties": {
        "functionId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "函数id"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "租户id"
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "用户id"
        }
      },
      "title": "函数基础信息"
    },
    "函数查询结果信息": {
      "type": "object",
      "properties": {
        "appType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "app类型"
        },
        "className": {
          "type": "string",
          "example": "class_name",
          "description": "main函数类名"
        },
        "commandFormate": {
          "type": "string",
          "example": "test",
          "description": "函数命令格式"
        },
        "createUser": {
          "description": "创建用户",
          "$ref": "#/definitions/函数用户信息"
        },
        "createUserId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "创建用户id"
        },
        "engineType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "engine类型"
        },
        "gmtCreate": {
          "description": "创建时间",
          "$ref": "#/definitions/Timestamp"
        },
        "gmtModified": {
          "description": "修改时间",
          "$ref": "#/definitions/Timestamp"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "id"
        },
        "isDeleted": {
          "type": "integer",
          "format": "int32",
          "description": "是否删除"
        },
        "modifyUser": {
          "description": "修改用户",
          "$ref": "#/definitions/函数用户信息"
        },
        "modifyUserId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "修改用户id"
        },
        "name": {
          "type": "string",
          "example": "name",
          "description": "函数名称"
        },
        "nodePid": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "父文件夹id"
        },
        "paramDesc": {
          "type": "string",
          "example": "name",
          "description": "函数参数说明"
        },
        "purpose": {
          "type": "string",
          "example": "name",
          "description": "函数用途"
        },
        "resourceName": {
          "type": "string",
          "example": "test_name",
          "description": "函数资源名称"
        },
        "resources": {
          "type": "integer",
          "format": "int64",
          "description": "关联资源的id"
        },
        "sqlText": {
          "type": "string",
          "example": "test_name",
          "description": "存储过程sql"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户id"
        },
        "type": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "函数类型 0自定义 1系统 2存储过程"
        }
      },
      "title": "函数查询结果信息"
    },
    "函数添加信息": {
      "type": "object",
      "required": [
        "appType",
        "className",
        "gmtCreate",
        "gmtModified",
        "id",
        "isDeleted",
        "name",
        "nodePid",
        "resourceId",
        "taskType",
        "type"
      ],
      "properties": {
        "appType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "app类型 RDOS(1) DQ(2), API(3) TAG(4) MAP(5) CONSOLE(6) STREAM(7) DATASCIENCE(8)"
        },
        "className": {
          "type": "string",
          "example": "class_name",
          "description": "main函数类名"
        },
        "commandFormate": {
          "type": "string",
          "example": "test",
          "description": "函数命令格式"
        },
        "createUserId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "创建用户id"
        },
        "gmtCreate": {
          "example": "2020-08-14 14:41:55",
          "description": "创建时间",
          "$ref": "#/definitions/Timestamp"
        },
        "gmtModified": {
          "example": "2020-08-14 14:41:55",
          "description": "修改时间",
          "$ref": "#/definitions/Timestamp"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "脱敏id"
        },
        "isDeleted": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "是否删除"
        },
        "modifyUserId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "修改用户id"
        },
        "name": {
          "type": "string",
          "example": "name",
          "description": "函数名称"
        },
        "nodePid": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "父文件夹id"
        },
        "paramDesc": {
          "type": "string",
          "example": "name",
          "description": "函数参数说明"
        },
        "purpose": {
          "type": "string",
          "example": "name",
          "description": "函数用途"
        },
        "resourceId": {
          "type": "integer",
          "format": "int64",
          "description": "资源id列表"
        },
        "resourceName": {
          "type": "string",
          "example": "test_name",
          "description": "函数资源名称"
        },
        "sqlText": {
          "type": "string",
          "example": "test_name",
          "description": "存储过程sql"
        },
        "taskType": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "任务类型"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "租户id"
        },
        "type": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "函数类型 0自定义 1系统 2存储过程"
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "用户id"
        }
      },
      "title": "函数添加信息"
    },
    "函数用户信息": {
      "type": "object",
      "properties": {
        "email": {
          "type": "string",
          "example": "1208686186@qq.com",
          "description": "邮箱"
        },
        "gmtCreate": {
          "example": "2020-12-30 11:42:14",
          "description": "创建时间",
          "$ref": "#/definitions/Timestamp"
        },
        "gmtModified": {
          "example": "2020-12-30 11:42:14",
          "description": "修改时间",
          "$ref": "#/definitions/Timestamp"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "用户 ID"
        },
        "isDeleted": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "是否删除"
        },
        "phoneNumber": {
          "type": "string",
          "example": 110,
          "description": "手机号"
        },
        "status": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "用户状态"
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "example": "1L",
          "description": "用户 ID"
        },
        "userName": {
          "type": "string",
          "example": "admin",
          "description": "用户名称"
        }
      },
      "title": "函数用户信息"
    },
    "函数目录结果信息": {
      "type": "object",
      "properties": {
        "catalogueType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "目录类型"
        },
        "createUserId": {
          "type": "integer",
          "format": "int64",
          "description": "创建用户"
        },
        "engineType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "engine类型"
        },
        "level": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "目录层级 0:一级 1:二级 n:n+1级"
        },
        "nodeName": {
          "type": "string",
          "example": "a",
          "description": "节点名称"
        },
        "nodePid": {
          "type": "integer",
          "format": "int64",
          "example": 3,
          "description": "节点父id"
        },
        "orderVal": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "节点值"
        },
        "parentCatalogue": {
          "description": "父目录",
          "$ref": "#/definitions/函数目录结果信息"
        }
      },
      "title": "函数目录结果信息"
    },
    "函数移动信息": {
      "type": "object",
      "required": [
        "functionId",
        "nodePid"
      ],
      "properties": {
        "functionId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "函数id"
        },
        "nodePid": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "父文件夹id"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "租户id"
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "用户id"
        }
      },
      "title": "函数移动信息"
    },
    "函数结果信息": {
      "type": "object",
      "properties": {
        "catalogueType": {
          "type": "string",
          "example": "SystemFunction",
          "description": "目录类型"
        },
        "catalogues": {
          "type": "array",
          "description": "函数目录列表",
          "items": {
            "$ref": "#/definitions/函数目录结果信息"
          }
        },
        "children": {
          "type": "array",
          "description": "子目录列表",
          "items": {
            "$ref": "#/definitions/目录结果信息"
          }
        },
        "createUser": {
          "type": "string",
          "example": "admin",
          "description": "创建用户"
        },
        "dependencyTasks": {
          "type": "array",
          "description": "依赖任务信息",
          "items": {
            "$ref": "#/definitions/函数任务信息"
          }
        },
        "engineType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "engine类型"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "目录id"
        },
        "isSubTask": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "是否为子任务"
        },
        "learningType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "learning类型"
        },
        "level": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "目录层级"
        },
        "lists": {
          "type": "array",
          "description": "依赖任务信息",
          "items": {
            "type": "array",
            "items": {
              "type": "object"
            }
          }
        },
        "name": {
          "type": "string",
          "description": "目录名称"
        },
        "operateModel": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "操作模式"
        },
        "orderVal": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "节点值"
        },
        "parentId": {
          "type": "integer",
          "format": "int64",
          "description": "父目录id"
        },
        "pythonVersion": {
          "type": "integer",
          "format": "int32",
          "example": 2,
          "description": "python版本"
        },
        "readWriteLockVO": {
          "description": "读写锁",
          "$ref": "#/definitions/读写锁信息"
        },
        "resourceType": {
          "type": "integer",
          "format": "int32",
          "description": "资源类型"
        },
        "scheduleStatus": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "调度状态"
        },
        "scriptType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "脚本类型"
        },
        "status": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "任务状态"
        },
        "submitStatus": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "提交状态"
        },
        "taskType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "任务类型"
        },
        "tasks": {
          "type": "array",
          "description": "依赖任务信息",
          "items": {
            "$ref": "#/definitions/函数任务信息"
          }
        },
        "type": {
          "type": "string",
          "example": "folder",
          "description": "目录类型"
        },
        "version": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "版本"
        }
      },
      "title": "函数结果信息"
    },
    "删除任务前置判断": {
      "type": "object",
      "properties": {
        "name": {
          "type": "string",
          "example": "task",
          "description": "任务名称"
        },
        "taskId": {
          "type": "string",
          "example": 33,
          "description": "任务ID"
        }
      },
      "title": "删除任务前置判断"
    },
    "发布信息信息": {
      "type": "object",
      "properties": {
        "errorMessage": {
          "type": "string",
          "description": "错误信息"
        },
        "errorSign": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "标识  0 无错误 1 权限校验错误 2 语法校验错误"
        }
      },
      "title": "发布信息信息"
    },
    "同步任务信息相关": {
      "type": "object",
      "required": [
        "jobId"
      ],
      "properties": {
        "jobId": {
          "type": "string",
          "example": "bd0619ba",
          "description": "任务实例Id"
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "用户id"
        }
      },
      "title": "同步任务信息相关"
    },
    "基础服务入参基类": {
      "type": "object",
      "properties": {
        "dataInfoId": {
          "type": "integer",
          "format": "int64",
          "description": "数据源Id"
        }
      },
      "title": "基础服务入参基类"
    },
    "实例运行sql信息": {
      "type": "object",
      "required": [
        "isCheckDDL",
        "isEnd",
        "sql",
        "taskId",
        "taskParams",
        "taskVariables",
        "uniqueKey"
      ],
      "properties": {
        "isCheckDDL": {
          "type": "integer",
          "format": "int32",
          "example": false,
          "description": "是否是DDL语句"
        },
        "isEnd": {
          "type": "boolean",
          "example": false,
          "description": "是否终止"
        },
        "sql": {
          "type": "string",
          "example": "show tables;",
          "description": "sql语句"
        },
        "taskId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "任务Id"
        },
        "taskParams": {
          "type": "string",
          "example": 1,
          "description": "任务参数"
        },
        "taskVariables": {
          "type": "array",
          "description": "任务前置执行语句",
          "items": {
            "$ref": "#/definitions/Map"
          }
        },
        "uniqueKey": {
          "type": "string",
          "example": "标识",
          "description": "唯一标识"
        }
      },
      "title": "实例运行sql信息"
    },
    "当前租户支持的数据源类型列表": {
      "type": "object",
      "properties": {
        "dataType": {
          "type": "string",
          "example": "如Mysql, Oracle, Hive",
          "description": "数据源类型"
        }
      },
      "title": "当前租户支持的数据源类型列表"
    },
    "执行选中的sql或者脚本": {
      "type": "object",
      "required": [
        "jobId",
        "needResult",
        "sqlId",
        "taskId",
        "type"
      ],
      "properties": {
        "jobId": {
          "type": "string",
          "example": 3,
          "description": "工作任务 ID"
        },
        "needResult": {
          "type": "boolean",
          "example": false,
          "description": "是否需要结果 默认是false"
        },
        "sqlId": {
          "type": "string",
          "example": 5,
          "description": "SQL ID"
        },
        "taskId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "任务 ID"
        },
        "type": {
          "type": "integer",
          "format": "int32",
          "example": 2,
          "description": "类别"
        }
      },
      "title": "执行选中的sql或者脚本"
    },
    "搜索数据源版本入参": {
      "type": "object",
      "required": [
        "dataType"
      ],
      "properties": {
        "dataType": {
          "type": "string",
          "description": "数据源类型编码"
        }
      },
      "title": "搜索数据源版本入参"
    },
    "搜索数据源类型参数": {
      "type": "object",
      "required": [
        "classifyId"
      ],
      "properties": {
        "classifyId": {
          "type": "integer",
          "format": "int64",
          "description": "数据源分类主键id"
        },
        "search": {
          "type": "string",
          "description": "数据源类目名称搜索"
        }
      },
      "title": "搜索数据源类型参数"
    },
    "数据同步-返回切分键需要的列名": {
      "type": "object",
      "required": [
        "sourceId",
        "tableName"
      ],
      "properties": {
        "schema": {
          "type": "string",
          "example": "test",
          "description": "查询的schema"
        },
        "sourceId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "数据源id"
        },
        "tableName": {
          "type": "string",
          "description": "表名称"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "租户id"
        }
      },
      "title": "数据同步-返回切分键需要的列名"
    },
    "数据源分类类目模型": {
      "type": "object",
      "properties": {
        "classifyCode": {
          "type": "string",
          "description": "数据源类目编码"
        },
        "classifyId": {
          "type": "integer",
          "format": "int64",
          "description": "类目主键id"
        },
        "classifyName": {
          "type": "string",
          "description": "类目名称"
        },
        "sorted": {
          "type": "integer",
          "format": "int32",
          "description": "类型栏排序字段"
        }
      },
      "title": "数据源分类类目模型"
    },
    "数据源列表信息": {
      "type": "object",
      "properties": {
        "dataDesc": {
          "type": "string",
          "description": "数据源描述"
        },
        "dataInfoId": {
          "type": "integer",
          "format": "int64",
          "description": "数据源Id"
        },
        "dataName": {
          "type": "string",
          "example": "mysql",
          "description": "数据源名称"
        },
        "dataType": {
          "type": "string",
          "description": "数据源类型"
        },
        "dataTypeCode": {
          "type": "integer",
          "format": "int32",
          "description": "数据源类型枚举"
        },
        "dataVersion": {
          "type": "string",
          "description": "数据源版本号"
        },
        "gmtModified": {
          "type": "string",
          "format": "date-time",
          "description": "最近修改时间"
        },
        "isImport": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "是否应用，0为未应用，1为已应用"
        },
        "isMeta": {
          "type": "integer",
          "format": "int32",
          "description": "是否有meta标志 0-否 1-是"
        },
        "linkJson": {
          "type": "string",
          "description": "数据源连接信息"
        },
        "schemaName": {
          "type": "string",
          "description": "schema名称，离线创建的meta数据源才有"
        },
        "status": {
          "type": "integer",
          "format": "int32",
          "description": "连接状态 0-连接失败, 1-正常"
        }
      },
      "title": "数据源列表信息"
    },
    "数据源列表查询参数": {
      "type": "object",
      "properties": {
        "currentPage": {
          "type": "integer",
          "format": "int32"
        },
        "dataTypeList": {
          "type": "array",
          "description": "数据源类型",
          "items": {
            "type": "string"
          }
        },
        "isMeta": {
          "type": "integer",
          "format": "int32",
          "description": "是否显示默认数据库，0为不显示，1为显示"
        },
        "pageSize": {
          "type": "integer",
          "format": "int32"
        },
        "search": {
          "type": "string",
          "description": "搜索参数"
        },
        "sort": {
          "type": "string"
        },
        "sortColumn": {
          "type": "string"
        },
        "status": {
          "type": "array",
          "description": "连接状态",
          "items": {
            "type": "integer",
            "format": "int32"
          }
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "用户id"
        }
      },
      "title": "数据源列表查询参数"
    },
    "数据源基本信息": {
      "type": "object",
      "properties": {
        "dataDesc": {
          "type": "string",
          "description": "数据源描述"
        },
        "dataInfoId": {
          "type": "integer",
          "format": "int64",
          "description": "数据源id"
        },
        "dataJson": {
          "type": "string",
          "description": "数据源信息"
        },
        "dataName": {
          "type": "string",
          "description": "数据源名称"
        },
        "dataType": {
          "type": "string",
          "example": "Mysql",
          "description": "数据源类型"
        },
        "dataVersion": {
          "type": "string",
          "description": "数据源报表"
        },
        "isMeta": {
          "type": "string",
          "description": "0普通，1默认数据源，2从控制台添加的"
        }
      },
      "title": "数据源基本信息"
    },
    "数据源版本视图类": {
      "type": "object",
      "properties": {
        "dataType": {
          "type": "string",
          "description": "数据源类型"
        },
        "dataVersion": {
          "type": "string",
          "description": "数据源版本"
        },
        "sorted": {
          "type": "integer",
          "format": "int32",
          "description": "排序字段"
        }
      },
      "title": "数据源版本视图类"
    },
    "数据源类型和版本统一入参": {
      "type": "object",
      "properties": {
        "dataType": {
          "type": "string",
          "description": "数据源类型 如MySql, Oracle"
        },
        "dataVersion": {
          "type": "string",
          "description": "数据源版本, 可为空"
        }
      },
      "title": "数据源类型和版本统一入参"
    },
    "数据源类型视图类": {
      "type": "object",
      "properties": {
        "dataType": {
          "type": "string",
          "description": "数据源类型唯一编码"
        },
        "haveVersion": {
          "type": "boolean",
          "description": "该数据源是否含有版本"
        },
        "imgUrl": {
          "type": "string",
          "description": "数据源图片url"
        },
        "typeId": {
          "type": "integer",
          "format": "int64",
          "description": "数据源类型主键id"
        }
      },
      "title": "数据源类型视图类"
    },
    "数据源表列表信息": {
      "type": "object",
      "required": [
        "sourceId"
      ],
      "properties": {
        "name": {
          "type": "string",
          "example": "table_name",
          "description": "模糊查询表名"
        },
        "schema": {
          "type": "string",
          "example": "test",
          "description": "查询的schema"
        },
        "sourceId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "数据源id"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "租户id"
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "用户id"
        }
      },
      "title": "数据源表列表信息"
    },
    "数据源表单属性": {
      "type": "object",
      "properties": {
        "defaultValue": {
          "type": "string",
          "description": "表单属性中默认值"
        },
        "invisible": {
          "type": "integer",
          "format": "int32",
          "description": "是否为隐藏 0-否 1-隐藏"
        },
        "isLink": {
          "type": "integer",
          "format": "int32",
          "description": "是否为数据源需要展示的连接信息字段。0-否; 1-是"
        },
        "label": {
          "type": "string",
          "description": "属性前label名称"
        },
        "name": {
          "type": "string",
          "description": "表单属性名称"
        },
        "options": {
          "type": "array",
          "description": "select组件下拉内容",
          "items": {
            "$ref": "#/definitions/Map"
          }
        },
        "placeHold": {
          "type": "string",
          "description": "输入框placeHold, 默认为空"
        },
        "regex": {
          "type": "string",
          "description": "正则校验表达式"
        },
        "requestApi": {
          "type": "string",
          "description": "请求数据Api接口地址，一般用于关联下拉框类型，如果不需要请求则为空"
        },
        "required": {
          "type": "integer",
          "format": "int32",
          "description": "是否必填 0-非必填 1-必填"
        },
        "style": {
          "type": "string",
          "description": "前端表单属性style参数"
        },
        "tooltip": {
          "type": "string",
          "description": "输入框后问号的提示信息"
        },
        "validInfo": {
          "type": "string",
          "description": "校验返回信息文案"
        },
        "widget": {
          "type": "string",
          "description": "属性格式"
        }
      },
      "title": "数据源表单属性"
    },
    "数据源表字段信息": {
      "type": "object",
      "required": [
        "isIncludePart",
        "schema",
        "sourceId",
        "tableName"
      ],
      "properties": {
        "isIncludePart": {
          "type": "boolean",
          "example": false,
          "description": "是否包含分区"
        },
        "schema": {
          "type": "string",
          "example": "test",
          "description": "查询的schema"
        },
        "sourceId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "数据源id"
        },
        "tableName": {
          "type": "string",
          "example": "table_name",
          "description": "表名称"
        }
      },
      "title": "数据源表字段信息"
    },
    "数据源表存储信息": {
      "type": "object",
      "required": [
        "sourceId",
        "tableName"
      ],
      "properties": {
        "schema": {
          "type": "string",
          "example": "数据库名字",
          "description": "schema名称"
        },
        "sourceId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "数据源id"
        },
        "tableName": {
          "type": "string",
          "example": "table_name",
          "description": "表名称"
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "用户id"
        }
      },
      "title": "数据源表存储信息"
    },
    "数据源追踪信息": {
      "type": "object",
      "required": [
        "taskId"
      ],
      "properties": {
        "taskId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "任务id"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "租户id"
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "用户id"
        }
      },
      "title": "数据源追踪信息"
    },
    "数据源预览信息": {
      "type": "object",
      "required": [
        "schema",
        "sourceId",
        "tableName"
      ],
      "properties": {
        "isRoot": {
          "type": "boolean",
          "example": false,
          "description": "是否为root"
        },
        "partition": {
          "type": "string",
          "example": "test",
          "description": "分区"
        },
        "schema": {
          "type": "string",
          "example": "test",
          "description": "查询的schema"
        },
        "sourceId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "数据源id"
        },
        "tableName": {
          "type": "string",
          "example": "table_name",
          "description": "表名称"
        }
      },
      "title": "数据源预览信息"
    },
    "数据表单模版视图类": {
      "type": "object",
      "properties": {
        "dataType": {
          "type": "string",
          "description": "数据源类型"
        },
        "dataVersion": {
          "type": "string",
          "description": "数据源版本 可为空"
        },
        "fromFieldVoList": {
          "type": "array",
          "description": "模版表单属性详情列表",
          "items": {
            "$ref": "#/definitions/数据源表单属性"
          }
        }
      },
      "title": "数据表单模版视图类"
    },
    "新增数据源整体入参": {
      "type": "object",
      "required": [
        "dataDesc",
        "dataJsonString",
        "dataName",
        "dataType"
      ],
      "properties": {
        "dataDesc": {
          "type": "string",
          "description": "数据源描述"
        },
        "dataJsonString": {
          "type": "string",
          "description": "数据源表单填写数据JsonString"
        },
        "dataName": {
          "type": "string",
          "description": "数据源名称"
        },
        "dataType": {
          "type": "string",
          "description": "数据源类型"
        },
        "dataVersion": {
          "type": "string",
          "description": "数据源版本"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "数据源主键id"
        }
      },
      "title": "新增数据源整体入参"
    },
    "杀死任务实例信息": {
      "type": "object",
      "required": [
        "cycEndDay",
        "cycStartDay"
      ],
      "properties": {
        "cycEndDay": {
          "type": "integer",
          "format": "int64",
          "example": 1609084800,
          "description": "执行结束日期"
        },
        "cycEndTime": {
          "$ref": "#/definitions/Timestamp"
        },
        "cycStartDay": {
          "type": "integer",
          "format": "int64",
          "example": 1609084800,
          "description": "执行开始日期"
        },
        "cycStartTime": {
          "$ref": "#/definitions/Timestamp"
        },
        "taskIds": {
          "type": "array",
          "description": "选择指定任务时，需要传该字段",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "taskPeriods": {
          "type": "array",
          "description": "调度周期",
          "items": {
            "type": "integer",
            "format": "int32"
          }
        },
        "type": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "0周期任务；1补数据实例 默认 1"
        }
      },
      "title": "杀死任务实例信息"
    },
    "根据appId获取日志信息": {
      "type": "object",
      "required": [
        "jobId",
        "taskType"
      ],
      "properties": {
        "jobId": {
          "type": "string",
          "example": 1,
          "description": "任务实例ID"
        },
        "taskType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "任务类型"
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "用户id"
        }
      },
      "title": "根据appId获取日志信息"
    },
    "根据jobId获取日志信息": {
      "type": "object",
      "required": [
        "jobId",
        "pageInfo"
      ],
      "properties": {
        "jobId": {
          "type": "string",
          "example": 1,
          "description": "任务实例ID"
        },
        "pageInfo": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "页数"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "租户id"
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "用户id"
        }
      },
      "title": "根据jobId获取日志信息"
    },
    "根据jobId获取日志结果信息": {
      "type": "object",
      "properties": {
        "computeType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "计算类型"
        },
        "dirtyPercent": {
          "type": "number",
          "format": "float",
          "example": 0,
          "description": "目录"
        },
        "downloadLog": {
          "type": "string",
          "example": 1,
          "description": "下载日志"
        },
        "execEndTime": {
          "example": "2020-07-20 10:50:46",
          "description": "结束时间",
          "$ref": "#/definitions/Timestamp"
        },
        "execStartTime": {
          "example": "2020-07-20 10:50:46",
          "description": "开始时间",
          "$ref": "#/definitions/Timestamp"
        },
        "execTime": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "exe时间"
        },
        "logInfo": {
          "type": "string",
          "example": 1,
          "description": "日志详情"
        },
        "name": {
          "type": "string",
          "example": 1,
          "description": "日志类型"
        },
        "pageIndex": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "当前页"
        },
        "pageSize": {
          "type": "integer",
          "format": "int32",
          "example": 10,
          "description": "总页数"
        },
        "readNum": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "读取数量"
        },
        "subNodeDownloadLog": {
          "type": "object",
          "description": "sub节点下载日志",
          "additionalProperties": {
            "type": "string"
          }
        },
        "taskType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "任务类型"
        },
        "writeNum": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "写入数量"
        }
      },
      "title": "根据jobId获取日志结果信息"
    },
    "根据任务id详情返回信息": {
      "type": "object",
      "properties": {
        "defaultProjectId": {
          "type": "integer",
          "format": "int64",
          "example": "1L",
          "description": "默认项目 ID"
        },
        "email": {
          "type": "string",
          "example": "1208686186@qq.com",
          "description": "邮箱"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "example": 0,
          "description": "用户 ID"
        },
        "phoneNumber": {
          "type": "string",
          "example": 110,
          "description": "手机号"
        },
        "status": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "用户状态"
        },
        "userName": {
          "type": "string",
          "example": "admin",
          "description": "用户名称"
        }
      },
      "title": "根据任务id详情返回信息"
    },
    "根据类型获取日志": {
      "type": "object",
      "required": [
        "jobId",
        "logType",
        "taskType"
      ],
      "properties": {
        "jobId": {
          "type": "string",
          "example": 1,
          "description": "任务实例ID"
        },
        "logType": {
          "type": "string",
          "example": 1,
          "description": "日志类型"
        },
        "taskType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "任务类型"
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "用户id"
        }
      },
      "title": "根据类型获取日志"
    },
    "根据类型获取日志返回信息": {
      "type": "object",
      "properties": {
        "download": {
          "type": "string",
          "example": "/api/",
          "description": "下载地址"
        },
        "msg": {
          "type": "string",
          "example": "msg",
          "description": "信息"
        }
      },
      "title": "根据类型获取日志返回信息"
    },
    "用户信息": {
      "type": "object",
      "properties": {
        "appType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "平台类别"
        },
        "defaultProjectId": {
          "type": "integer",
          "format": "int64",
          "example": 3,
          "description": "默认项目ID"
        },
        "email": {
          "type": "string",
          "example": "xxx@163.com",
          "description": "邮箱"
        },
        "gmtCreate": {
          "example": "2020-12-29T11:39:13.000+00:00",
          "description": "创建时间",
          "$ref": "#/definitions/Timestamp"
        },
        "gmtModified": {
          "example": "2020-12-29T11:39:13.000+00:00",
          "description": "修改时间",
          "$ref": "#/definitions/Timestamp"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "主键id"
        },
        "isDeleted": {
          "type": "integer",
          "format": "int32",
          "example": "dtstack",
          "description": "是否删除"
        },
        "phoneNumber": {
          "type": "string",
          "example": "135xxxxx892",
          "description": "电话号码"
        },
        "projectId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "项目ID"
        },
        "rootUser": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "rootUser"
        },
        "status": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "状态"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "example": 3,
          "description": "租户ID"
        },
        "userName": {
          "type": "string",
          "example": "dtstack",
          "description": "用户姓名"
        }
      },
      "title": "用户信息"
    },
    "目录信息": {
      "type": "object",
      "properties": {
        "appType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "平台类别"
        },
        "catalogueType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "类目类别"
        },
        "createUserId": {
          "type": "integer",
          "format": "int64",
          "example": 3,
          "description": "创建用户"
        },
        "engineType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "引擎类型"
        },
        "gmtCreate": {
          "description": "创建时间",
          "$ref": "#/definitions/Timestamp"
        },
        "gmtModified": {
          "description": "修改时间",
          "$ref": "#/definitions/Timestamp"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "ID"
        },
        "isDeleted": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "是否删除"
        },
        "level": {
          "type": "integer",
          "format": "int32",
          "example": 3,
          "description": "目录层级"
        },
        "nodeName": {
          "type": "string",
          "example": "数据开发",
          "description": "文件夹名"
        },
        "nodePid": {
          "type": "integer",
          "format": "int64",
          "example": 23,
          "description": "父文件夹 ID"
        },
        "orderVal": {
          "type": "integer",
          "format": "int32",
          "example": 2,
          "description": "序号"
        },
        "projectId": {
          "type": "integer",
          "format": "int64",
          "description": "项目 ID"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "租户 ID"
        }
      },
      "title": "目录信息"
    },
    "目录更新信息": {
      "type": "object",
      "required": [
        "nodeName",
        "nodePid",
        "type"
      ],
      "properties": {
        "appType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "app类型"
        },
        "catalogueType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "目录类型"
        },
        "createUserId": {
          "type": "integer",
          "format": "int64",
          "example": 5,
          "description": "创建用户"
        },
        "engineType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "engine类型"
        },
        "gmtCreate": {
          "description": "创建时间",
          "$ref": "#/definitions/Timestamp"
        },
        "gmtModified": {
          "description": "修改时间",
          "$ref": "#/definitions/Timestamp"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "id"
        },
        "isDeleted": {
          "type": "integer",
          "format": "int32",
          "description": "是否删除"
        },
        "level": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "目录层级 0:一级 1:二级 n:n+1级"
        },
        "nodeName": {
          "type": "string",
          "example": "a",
          "description": "节点名称"
        },
        "nodePid": {
          "type": "integer",
          "format": "int64",
          "example": 3,
          "description": "节点父id"
        },
        "orderVal": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "节点值"
        },
        "parentCatalogue": {
          "description": "父目录",
          "$ref": "#/definitions/目录更新信息"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "租户id"
        },
        "type": {
          "type": "string",
          "example": "file",
          "description": "文件类型 folder file"
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "用户id"
        }
      },
      "title": "目录更新信息"
    },
    "目录添加信息": {
      "type": "object",
      "required": [
        "catalogueType",
        "engineType",
        "level",
        "nodeName",
        "nodePid"
      ],
      "properties": {
        "catalogueType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "目录类型"
        },
        "createUserId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "创建用户id"
        },
        "engineType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "engine类型"
        },
        "gmtCreate": {
          "description": "创建时间",
          "$ref": "#/definitions/Timestamp"
        },
        "gmtModified": {
          "description": "修改时间",
          "$ref": "#/definitions/Timestamp"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "id"
        },
        "level": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "目录层级 0:一级 1:二级 n:n+1级"
        },
        "nodeName": {
          "type": "string",
          "example": "a",
          "description": "节点名称"
        },
        "nodePid": {
          "type": "integer",
          "format": "int64",
          "example": 3,
          "description": "节点父id"
        },
        "orderVal": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "节点值"
        },
        "parentCatalogue": {
          "description": "父目录",
          "$ref": "#/definitions/目录添加信息"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "租户id"
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "用户id"
        }
      },
      "title": "目录添加信息"
    },
    "目录结果信息": {
      "type": "object",
      "properties": {
        "catalogueType": {
          "type": "string",
          "example": "SystemFunction",
          "description": "目录类型"
        },
        "children": {
          "type": "array",
          "description": "子目录列表",
          "items": {
            "$ref": "#/definitions/目录结果信息"
          }
        },
        "createUser": {
          "type": "string",
          "example": "test",
          "description": "创建用户"
        },
        "engineType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "engine类型"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "目录id"
        },
        "isSubTask": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "是否为子任务"
        },
        "learningType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "learning类型"
        },
        "level": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "目录层级"
        },
        "name": {
          "type": "string",
          "example": "name",
          "description": "目录名称"
        },
        "operateModel": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "操作模式"
        },
        "orderVal": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "节点值"
        },
        "parentId": {
          "type": "integer",
          "format": "int64",
          "example": 0,
          "description": "父目录id"
        },
        "pythonVersion": {
          "type": "integer",
          "format": "int32",
          "example": 2,
          "description": "python版本"
        },
        "readWriteLockVO": {
          "description": "读写锁",
          "$ref": "#/definitions/读写锁信息"
        },
        "resourceType": {
          "type": "integer",
          "format": "int32",
          "description": "资源类型"
        },
        "scriptType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "脚本类型"
        },
        "status": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "任务状态"
        },
        "taskType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "任务类型"
        },
        "type": {
          "type": "string",
          "example": "folder",
          "description": "目录类型"
        },
        "version": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "版本"
        }
      },
      "title": "目录结果信息"
    },
    "目录获取信息": {
      "type": "object",
      "required": [
        "catalogueType",
        "nodePid"
      ],
      "properties": {
        "catalogueType": {
          "type": "string",
          "example": 1,
          "description": "目录类型"
        },
        "isGetFile": {
          "type": "boolean",
          "example": false,
          "description": "是否获取文件"
        },
        "nodePid": {
          "type": "integer",
          "format": "int64",
          "example": 3,
          "description": "节点父id"
        },
        "parentId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "父id"
        },
        "taskType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "任务类型"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "租户id"
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "用户id"
        }
      },
      "title": "目录获取信息"
    },
    "租户对接集群信息": {
      "type": "object",
      "required": [
        "clusterId",
        "tenantId"
      ],
      "properties": {
        "bindDBList": {
          "type": "array",
          "description": "计算引擎对接信息",
          "items": {
            "$ref": "#/definitions/ComponentBindDBVO"
          }
        },
        "clusterId": {
          "type": "integer",
          "format": "int64",
          "example": 2,
          "description": "集群ID"
        },
        "queueId": {
          "type": "integer",
          "format": "int64",
          "example": 12,
          "description": "队列ID"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "租户ID"
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "用户id"
        }
      },
      "title": "租户对接集群信息"
    },
    "系统参数": {
      "type": "object",
      "properties": {
        "id": {
          "type": "integer",
          "format": "int64",
          "description": "id"
        },
        "isDeleted": {
          "type": "integer",
          "format": "int32",
          "description": "是否删除"
        },
        "paramCommand": {
          "type": "string",
          "description": "命令"
        },
        "paramName": {
          "type": "string",
          "description": "参数名称"
        }
      },
      "title": "系统参数"
    },
    "组件版本号": {
      "type": "object",
      "properties": {
        "componentVersion": {
          "type": "string",
          "example": 1.1,
          "description": "版本号"
        },
        "isDefault": {
          "type": "boolean",
          "example": true,
          "description": "是否默认版本"
        }
      },
      "title": "组件版本号"
    },
    "获取同步任务运行状态返回信息": {
      "type": "object",
      "properties": {
        "download": {
          "type": "string",
          "example": 1,
          "description": "下载"
        },
        "jobId": {
          "type": "string",
          "example": 1,
          "description": "任务实例ID"
        },
        "msg": {
          "type": "string",
          "example": 1,
          "description": "信息"
        },
        "status": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "状态"
        }
      },
      "title": "获取同步任务运行状态返回信息"
    },
    "获取组件版本号": {
      "type": "object",
      "properties": {
        "taskType": {
          "type": "integer",
          "format": "int32",
          "description": "任务类型"
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "用户id"
        }
      },
      "title": "获取组件版本号"
    },
    "获取资源返回信息": {
      "type": "object",
      "properties": {
        "appType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "app类型"
        },
        "createUser": {
          "description": "创建用户",
          "$ref": "#/definitions/返回用户信息"
        },
        "createUserId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "创建用户 ID"
        },
        "gmtCreate": {
          "example": "2020-08-14 14:41:55",
          "description": "创建时间",
          "$ref": "#/definitions/Timestamp"
        },
        "gmtModified": {
          "example": "2020-08-14 14:41:55",
          "description": "修改时间",
          "$ref": "#/definitions/Timestamp"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "ID"
        },
        "isDeleted": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "是否删除"
        },
        "modifyUser": {
          "description": "修改用户",
          "$ref": "#/definitions/返回用户信息"
        },
        "modifyUserId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "修改用户的ID"
        },
        "nodePid": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "父文件夹id"
        },
        "originFileName": {
          "type": "string",
          "example": "我是源文件名",
          "description": "源文件名称"
        },
        "resourceDesc": {
          "type": "string",
          "example": "我是描述",
          "description": "资源描述"
        },
        "resourceName": {
          "type": "string",
          "example": "我是资源",
          "description": "资源名称"
        },
        "resourceType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "资源类型"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "租户id"
        },
        "url": {
          "type": "string",
          "example": "hdfs://ns1/rdos/batch/***",
          "description": "资源路径"
        }
      },
      "title": "获取资源返回信息"
    },
    "表字段信息": {
      "type": "object",
      "properties": {
        "taskId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "任务ID"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "租户id"
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "用户id"
        }
      },
      "title": "表字段信息"
    },
    "读写锁信息": {
      "type": "object",
      "required": [
        "fileId",
        "type"
      ],
      "properties": {
        "fileId": {
          "type": "integer",
          "format": "int64",
          "example": 132412,
          "description": "文件 ID"
        },
        "getLock": {
          "type": "boolean",
          "example": false,
          "description": "是否持有锁"
        },
        "gmtCreate": {
          "example": "2020-12-29T11:39:13.000+00:00",
          "description": "创建时间",
          "$ref": "#/definitions/Timestamp"
        },
        "gmtModified": {
          "example": "2020-12-29T11:39:13.000+00:00",
          "description": "修改时间",
          "$ref": "#/definitions/Timestamp"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "主键id"
        },
        "isDeleted": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "是否删除"
        },
        "lastKeepLockUserName": {
          "type": "string",
          "example": 5,
          "description": "上一个持有锁的用户名"
        },
        "lockName": {
          "type": "string",
          "description": "锁名称"
        },
        "lockVersion": {
          "type": "integer",
          "format": "int32",
          "example": 2,
          "description": "锁版本"
        },
        "modifyUserId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "修改的用户"
        },
        "relationId": {
          "type": "integer",
          "format": "int64",
          "example": 3,
          "description": "任务 ID"
        },
        "result": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "检查结果"
        },
        "subFileIds": {
          "type": "array",
          "description": "依赖文件ID",
          "items": {
            "type": "integer",
            "format": "int64"
          }
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "租户id"
        },
        "type": {
          "type": "string",
          "example": 1,
          "description": "类别"
        },
        "version": {
          "type": "integer",
          "format": "int32",
          "example": 9,
          "description": "乐观锁"
        }
      },
      "title": "读写锁信息"
    },
    "调度任务信息": {
      "type": "object",
      "properties": {
        "appType": {
          "type": "integer",
          "format": "int32",
          "example": 11,
          "description": "平台类型"
        },
        "computeType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "计算类型 0实时，1 离线"
        },
        "createModel": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "创建模式 0-向导模式，1-脚本模式"
        },
        "createUser": {
          "description": "创建用户",
          "$ref": "#/definitions/用户信息"
        },
        "createUserId": {
          "type": "integer",
          "format": "int64",
          "example": 3,
          "description": "新建task的用户"
        },
        "cron": {
          "type": "string",
          "example": "* 0/1 * * * *",
          "description": "定时周期表达式"
        },
        "currentProject": {
          "type": "boolean",
          "example": true,
          "description": "是否是当前项目"
        },
        "dataSourceId": {
          "type": "integer",
          "format": "int64",
          "example": 2,
          "description": "数据源 ID"
        },
        "engineType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "执行引擎类型 0 flink, 1 spark"
        },
        "exeArgs": {
          "type": "string",
          "description": "启动参数"
        },
        "existsOnRule": {
          "type": "boolean",
          "description": "是否存在开启的质量任务"
        },
        "extraInfo": {
          "type": "string",
          "description": "扩展信息"
        },
        "flowId": {
          "type": "integer",
          "format": "int64",
          "example": 32,
          "description": "所属工作流id"
        },
        "flowName": {
          "type": "string",
          "example": "数据同步test",
          "description": "工作流名称"
        },
        "forceUpdate": {
          "type": "boolean",
          "example": true,
          "description": "是否覆盖更新"
        },
        "gmtCreate": {
          "example": "2020-12-29T11:39:13.000+00:00",
          "description": "创建时间",
          "$ref": "#/definitions/Timestamp"
        },
        "gmtModified": {
          "example": "2020-12-29T11:39:13.000+00:00",
          "description": "修改时间",
          "$ref": "#/definitions/Timestamp"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "主键 ID"
        },
        "increColumn": {
          "type": "string"
        },
        "input": {
          "type": "string",
          "example": "/usr/opt/a",
          "description": "输入数据文件的路径"
        },
        "isDeleted": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "是否删除"
        },
        "isExpire": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "是否过期"
        },
        "isPublishToProduce": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "是否发布到了生产环境"
        },
        "learningType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "0-TensorFlow,1-MXNet"
        },
        "lockVersion": {
          "type": "integer",
          "format": "int32",
          "example": 11,
          "description": "锁版本"
        },
        "mainClass": {
          "type": "string",
          "description": "入口类"
        },
        "modifyUser": {
          "description": "创建用户",
          "$ref": "#/definitions/用户信息"
        },
        "modifyUserId": {
          "type": "integer",
          "format": "int64",
          "example": 5,
          "description": "最后修改task的用户"
        },
        "name": {
          "type": "string",
          "example": "dev_test",
          "description": "任务名称(name)必填"
        },
        "nodePName": {
          "type": "string",
          "example": "数据开发",
          "description": "节点名称"
        },
        "nodePid": {
          "type": "integer",
          "format": "int64",
          "example": 13,
          "description": "节点 id"
        },
        "operateModel": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "操作模式 0-资源模式，1-编辑模式"
        },
        "options": {
          "type": "string",
          "description": "脚本的命令行参数"
        },
        "output": {
          "type": "string",
          "example": "/usr/opt/a",
          "description": "输出模型的路径"
        },
        "ownerUser": {
          "description": "所属用户",
          "$ref": "#/definitions/用户信息"
        },
        "ownerUserId": {
          "type": "integer",
          "format": "int64",
          "example": 3,
          "description": "负责人id"
        },
        "periodType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "周期类型"
        },
        "projectAlias": {
          "type": "string",
          "example": "dev",
          "description": "项目别名"
        },
        "projectId": {
          "type": "integer",
          "format": "int64",
          "example": 5,
          "description": "项目 ID"
        },
        "projectName": {
          "type": "string",
          "example": "dev开发",
          "description": "项目名称"
        },
        "projectScheduleStatus": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "启动:0 停止:1"
        },
        "pythonVersion": {
          "type": "integer",
          "format": "int32",
          "example": 2,
          "description": "python版本 2-python2.x,3-python3.x式"
        },
        "relatedTasks": {
          "type": "array",
          "description": "任务信息",
          "items": {
            "$ref": "#/definitions/调度任务信息"
          }
        },
        "scheduleConf": {
          "type": "string",
          "description": "调度配置"
        },
        "scheduleStatus": {
          "type": "integer",
          "format": "int32",
          "example": 2,
          "description": "调度状态"
        },
        "sqlText": {
          "type": "string",
          "example": "select * from test",
          "description": "sql 文本"
        },
        "subNodes": {
          "description": "任务信息",
          "$ref": "#/definitions/调度任务信息"
        },
        "subTaskVOS": {
          "type": "array",
          "description": "任务信息",
          "items": {
            "$ref": "#/definitions/调度任务信息"
          }
        },
        "submitStatus": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "提交状态"
        },
        "syncModel": {
          "type": "integer",
          "format": "int32",
          "example": 2,
          "description": "同步模式"
        },
        "taskDesc": {
          "type": "string",
          "example": "测试任务",
          "description": "任务描述"
        },
        "taskId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "任务 ID"
        },
        "taskParams": {
          "type": "string",
          "example": "job.executor:1",
          "description": "任务参数"
        },
        "taskPeriodId": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "任务周期 ID"
        },
        "taskPeriodType": {
          "type": "string",
          "example": 2,
          "description": "任务周期类别"
        },
        "taskRuleList": {
          "type": "array",
          "description": "关联的规则任务",
          "items": {
            "$ref": "#/definitions/调度任务信息"
          }
        },
        "taskType": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "任务类型(taskType)必填"
        },
        "taskVOS": {
          "type": "array",
          "description": "任务信息",
          "items": {
            "$ref": "#/definitions/调度任务信息"
          }
        },
        "taskVariables": {
          "type": "array",
          "description": "任务参数",
          "items": {
            "$ref": "#/definitions/Map"
          }
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "example": 3,
          "description": "租户 ID"
        },
        "tenantName": {
          "type": "string",
          "example": "dev租户",
          "description": "租户名称"
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "example": 2,
          "description": "用户 ID"
        },
        "versionId": {
          "type": "integer",
          "format": "int32",
          "example": 23,
          "description": "batchJob执行的时候的vesion版本"
        }
      },
      "title": "调度任务信息"
    },
    "资源任务信息": {
      "type": "object",
      "required": [
        "taskId",
        "type"
      ],
      "properties": {
        "taskId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "任务 ID"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "租户id"
        },
        "type": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "类型"
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "用户id"
        }
      },
      "title": "资源任务信息"
    },
    "资源信息": {
      "type": "object",
      "properties": {
        "appType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "平台类型"
        },
        "createUserId": {
          "type": "integer",
          "format": "int64",
          "example": 5,
          "description": "创建人 ID"
        },
        "gmtCreate": {
          "example": "2020-12-29T11:39:13.000+00:00",
          "description": "创建时间",
          "$ref": "#/definitions/Timestamp"
        },
        "gmtModified": {
          "example": "2020-12-29T11:39:13.000+00:00",
          "description": "修改时间",
          "$ref": "#/definitions/Timestamp"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "主键id"
        },
        "isDeleted": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "是否删除"
        },
        "modifyUserId": {
          "type": "integer",
          "format": "int64",
          "example": 7,
          "description": "修改人 ID"
        },
        "nodePid": {
          "type": "integer",
          "format": "int64",
          "example": 3,
          "description": "父节点 ID"
        },
        "originFileName": {
          "type": "string",
          "example": "测试",
          "description": "源文件名"
        },
        "projectId": {
          "type": "integer",
          "format": "int64",
          "description": "项目ID"
        },
        "resourceDesc": {
          "type": "string",
          "example": "test",
          "description": "资源备注"
        },
        "resourceName": {
          "type": "string",
          "example": "开发测试",
          "description": "资源名称"
        },
        "resourceType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "资源类型 1,jar 2 sql"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "description": "数栈租户ID"
        },
        "url": {
          "type": "string",
          "example": "/usr/tmp",
          "description": "资源路径"
        }
      },
      "title": "资源信息"
    },
    "资源基础信息": {
      "type": "object",
      "required": [
        "resourceId"
      ],
      "properties": {
        "resourceId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "资源 ID"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "租户 ID"
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "用户 ID"
        }
      },
      "title": "资源基础信息"
    },
    "运行sql返回信息": {
      "type": "object",
      "properties": {
        "download": {
          "type": "string",
          "example": 1,
          "description": "下载"
        },
        "isContinue": {
          "type": "boolean",
          "example": false,
          "description": "是否继续"
        },
        "jobId": {
          "type": "string",
          "example": 1,
          "description": "发送到引擎生成的jobid"
        },
        "msg": {
          "type": "string",
          "example": 1,
          "description": "信息"
        },
        "result": {
          "type": "array",
          "description": "结果",
          "items": {
            "type": "object"
          }
        },
        "sqlText": {
          "type": "string",
          "example": 1,
          "description": "sql文本"
        },
        "status": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "状态"
        },
        "taskType": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "任务类型"
        }
      },
      "title": "运行sql返回信息"
    },
    "运行同步任务": {
      "type": "object",
      "required": [
        "taskId",
        "taskParams"
      ],
      "properties": {
        "taskId": {
          "type": "integer",
          "format": "int64",
          "example": "1L",
          "description": "任务Id"
        },
        "taskParams": {
          "type": "string",
          "example": "我是参数",
          "description": "任务参数"
        }
      },
      "title": "运行同步任务"
    },
    "运行同步任务返回信息": {
      "type": "object",
      "properties": {
        "jobId": {
          "type": "string",
          "example": 1,
          "description": "任务实例ID"
        },
        "msg": {
          "type": "string",
          "example": 1,
          "description": "信息"
        },
        "status": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "状态"
        }
      },
      "title": "运行同步任务返回信息"
    },
    "返回可依赖的任务": {
      "type": "object",
      "properties": {
        "taskId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "任务id"
        },
        "taskName": {
          "type": "string",
          "example": 123,
          "description": "任务名称"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "租户id"
        },
        "tenantName": {
          "type": "string",
          "example": 1,
          "description": "租户名称"
        }
      },
      "title": "返回可依赖的任务"
    },
    "返回用户信息": {
      "type": "object",
      "properties": {
        "defaultProjectId": {
          "type": "integer",
          "format": "int64",
          "example": 3,
          "description": "默认项目ID"
        },
        "email": {
          "type": "string",
          "example": "zhangsan@dtstack.com",
          "description": "邮箱"
        },
        "gmtCreate": {
          "example": "2020-12-28 09:22:03",
          "description": "创建时间",
          "$ref": "#/definitions/Timestamp"
        },
        "gmtModified": {
          "example": "2020-12-28 09:22:03",
          "description": "修改时间",
          "$ref": "#/definitions/Timestamp"
        },
        "id": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "主键id"
        },
        "isDeleted": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "是否删除"
        },
        "phoneNumber": {
          "type": "string",
          "example": 110,
          "description": "电话号码"
        },
        "status": {
          "type": "integer",
          "format": "int32",
          "example": 0,
          "description": "状态"
        },
        "userName": {
          "type": "string",
          "example": "ruomu",
          "description": "用户姓名"
        }
      },
      "title": "返回用户信息"
    },
    "集群组件database信息": {
      "type": "object",
      "required": [
        "clusterId",
        "componentTypeCode"
      ],
      "properties": {
        "clusterId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "集群ID"
        },
        "componentTypeCode": {
          "type": "integer",
          "format": "int32",
          "example": 1,
          "description": "组件类型ID"
        },
        "tenantId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "租户id"
        },
        "userId": {
          "type": "integer",
          "format": "int64",
          "example": 1,
          "description": "用户id"
        }
      },
      "title": "集群组件database信息"
    }
  }
}