// 从config文件全局读取
import { ANALYTICS_ENGINE_BASE_URL } from 'config/base';

export default {
    GET_CATALOGUES: `${ANALYTICS_ENGINE_BASE_URL}/getCatalogue`, // 获取目录
    CREATE_OR_UPDATE_DB: `${ANALYTICS_ENGINE_BASE_URL}/createOrUpdateDB`, //创建或者修改数据库
    GET_CREATE_SQL: `${ANALYTICS_ENGINE_BASE_URL}/createSql`,
    CREATE_NEW_TABLE: `${ANALYTICS_ENGINE_BASE_URL}/dataBaseManagement/createTable`, //创建新表
    GET_TABLE_DETAIL: `${ANALYTICS_ENGINE_BASE_URL}/getTableDetail`,//获取表详情
    SAVE_TABLE_INFO: `${ANALYTICS_ENGINE_BASE_URL}/motifyTable`,//存储表详情
}
