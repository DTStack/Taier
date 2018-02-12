import mc from 'mirror-creator';

const BASE_URL = '/api/service'; // 从config文件全局读取

export default {

    // ===== 用户模块 ===== //
    LOGOUT: `/uic/api/v2/logout`,
    APP_LOGOUT: `${BASE_URL}/login/out`,

    // ===== 数据源管理 ===== //
    SAVE_DATA_SOURCE: `${BASE_URL}/batch/batchDataSource/addOrUpdateSource`, // 添加或者更新数据源
    DELETE_DATA_SOURCE: `${BASE_URL}/batch/batchDataSource/deleteSource`, // 删除数据源
    QUERY_DATA_SOURCE: `${BASE_URL}/batch/batchDataSource/pageQuery`, // 查询数据源接口
    GET_DATA_SOURCE_BY_ID: `${BASE_URL}/batch/batchDataSource/getBySourceId`, // 根据ID查询数据源接口
    TEST_DATA_SOURCE_CONNECTION: `${BASE_URL}/batch/batchDataSource/checkConnection`, // 测试数据源连通性
    GET_DATA_SOURCE_TYPES: `${BASE_URL}/batch/batchDataSource/getTypes`, // 获取数据源类型列表
    GET_HBASE_COLUMN_FAMILY: `${BASE_URL}/batch/batchDataSource/columnfamily`, // 获取Hbase数据表列族

}

