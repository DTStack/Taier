import { RDOS_BASE_URL } from 'config/base';

export default {
    STREAM_SAVE_DATA_SOURCE: `${RDOS_BASE_URL}/stream/streamDataSource/addOrUpdateSource`, // 添加或者更新数据源
    STREAM_TEST_DATA_SOURCE_CONNECTION: `${RDOS_BASE_URL}/stream/streamDataSource/checkConnection`, // 测试数据源连通性
    STREAM_DELETE_DATA_SOURCE: `${RDOS_BASE_URL}/stream/streamDataSource/deleteSource`, // 删除数据源
    GET_STREAM_TABLELIST: `${RDOS_BASE_URL}/stream/streamDataSource/tablelist`,
    STREAM_QUERY_DATA_SOURCE: `${RDOS_BASE_URL}/stream/streamDataSource/pageQuery`, // 查询数据源接口
    GET_STREAM_TABLELIST: `${RDOS_BASE_URL}/stream/streamDataSource/list`,
}
