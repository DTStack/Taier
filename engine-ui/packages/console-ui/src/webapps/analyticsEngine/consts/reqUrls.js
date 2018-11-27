// 从config文件全局读取
import { ANALYTICS_ENGINE_BASE_URL } from 'config/base';

export default {

    // ================== Common ================== //
    EXEC_SQL: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/startSqlImmediately`, // 开始执行SQL
    STOP_EXEC_SQL: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/stopSql`, // 停止执行SQL
    FORMAT_SQL: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/formatSql`, // 格式化SQL
    DOWNLOAD_SQL_RESULT: `/api/analysis/download/dataBaseManagement/downloadSqlExeResult`, // 下载执行结果
    GET_SQL_RESULT: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/selectData`, // 获取SQL结果

    // ================== Database ================== //
    GET_DB_USER_LIST: `${ANALYTICS_ENGINE_BASE_URL}/user/pageQuery`, // 获取用户列表
    GET_DB_USER_ROLE_LIST: `${ANALYTICS_ENGINE_BASE_URL}/role/pageQuery`, // 获取用户角色列表
    GET_USERS_NOT_IN_DB: `${ANALYTICS_ENGINE_BASE_URL}/user/getUicUsersNotInProject`, // 获取未添加到项目的用户
    UPDATE_DB_USER_ROLE: `${ANALYTICS_ENGINE_BASE_URL}/roleUser/updateUserRole`, // 修改用户数据库角色
    ADD_DB_USER: `${ANALYTICS_ENGINE_BASE_URL}/roleUser/addRoleUserNew`, // 添加数据库用户
    DELETE_DB_USER: `${ANALYTICS_ENGINE_BASE_URL}/roleUser/removeRoleUserFromProject`, // 删除数据库用户

    GET_CATALOGUES: `${ANALYTICS_ENGINE_BASE_URL}/getCatalogue`, // 获取目录
    CREATE_DB: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/createDataBase`, // 创建或者修改数据库
    DROP_DB: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/dropDatabase`, // 删除数据库
    MODIFY_DB_PASSWORD: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/modifyPassword`, // 创建或者修改数据库
    GET_DB_LIST: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/listDatabases`, // 获取数据库列表
    GET_DB_DETAIL: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/getDatabaseInfo`, // 获取数据库详情

    // ================== Table ================== //
    CREATE_TABLE: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/createTable`, // 创建表
    GET_TABLE_DETAIL: `${ANALYTICS_ENGINE_BASE_URL}/getTableDetail`, // 获取表详情
    SAVE_TABLE_INFO: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/updateTable`, // 存储表详情
    GET_TABLE_BY_DB: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/getTablesByDatabaseId`, // 创建表
    GET_TABLE_LIST_BY_DB: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/getTablesByDatabaseId`, // 创建表
    GET_TABLE_BY_ID: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/getTableInfoById`, // 根据表ID查表
    GET_CREATE_SQL: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/createSql`, // 获取创建语句
    DROP_TABLE: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/dropTable`, // 删除表
    SEARCH_TABLES_BY_NAME: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/getTableInfosByName`, // 按表名搜索表
    CREATE_TABLE_BY_DDL: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/ddlCreateTable`, // ddl建表
    PARTITIONS_INFO: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/getPartitions`, // 获取分区信息
    PREVIEW_DATA: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/getData`, // 数据预览

    // ================== DataMap ================== //
    CREATE_DATA_MAP: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/createDataMap`, // 创建Datamap
    GET_DATAMAP_BY_TABLE: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/getDataMapsByTableId`, // 创建表
    GET_DATAMAP_DETAIL: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/getDataMapDetailInfo`, // 获取DataMap详情
    DROP_DATAMAP: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/dropDataMap` // 删除DataMap

}
