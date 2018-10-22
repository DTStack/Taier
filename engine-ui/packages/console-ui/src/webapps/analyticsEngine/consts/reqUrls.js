// 从config文件全局读取
import { ANALYTICS_ENGINE_BASE_URL } from 'config/base';

export default {

    // ================== Database ================== //
    GET_CATALOGUES: `${ANALYTICS_ENGINE_BASE_URL}/getCatalogue`, // 获取目录
    CREATE_OR_UPDATE_DB: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/createDataBase`, //创建或者修改数据库
    MODIFY_DB_PASSWORD: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/modifyPassword`, //创建或者修改数据库
    GET_DATABASES: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/listDatabases`, // 获取数据库列表

    // ================== Table ================== //
    CREATE_TABLE: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/createTable`, //创建表
    GET_CREATE_SQL: `${ANALYTICS_ENGINE_BASE_URL}/createSql`,
    CREATE_NEW_TABLE: `${ANALYTICS_ENGINE_BASE_URL}/createTable`, //创建新表

    // ================== DataMap ================== //
    CREATE_DATA_MAP: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/createDataMap`, //创建表

}
