import { STREAM_BASE_URL } from 'config/base';

export default {
    STREAM_SAVE_DATA_SOURCE: `${STREAM_BASE_URL}/streamDataSource/addOrUpdateSource`, // 添加或者更新数据源
    STREAM_TEST_DATA_SOURCE_CONNECTION: `${STREAM_BASE_URL}/streamDataSource/checkConnection`, // 测试数据源连通性
    STREAM_DELETE_DATA_SOURCE: `${STREAM_BASE_URL}/streamDataSource/deleteSource`, // 删除数据源
    GET_STREAM_TABLELIST: `${STREAM_BASE_URL}/streamDataSource/tablelist`,
    STREAM_QUERY_DATA_SOURCE: `${STREAM_BASE_URL}/streamDataSource/pageQuery`, // 查询数据源接口
    GET_STREAM_DATASOURCE_LIST: `${STREAM_BASE_URL}/streamDataSource/list`,
    GET_STREAM_TABLECOLUMN: `${STREAM_BASE_URL}/streamDataSource/tablecolumn`,//输出tablecolumn
    GET_TASK_LIST_OF_STREAM_SOURCE:`${STREAM_BASE_URL}/streamDataSource/getSourceTaskRef`,//获取实时数据源的任务
    LINK_SOURCE:`${STREAM_BASE_URL}/streamDataSource/linkDataSource`,//关联映射数据源

    GET_REALTIME_JOBDATA: `${STREAM_BASE_URL}/streamDataSource/trace`,
    GET_SYNC_SCRIPT_TEMPLATE: `${STREAM_BASE_URL}/streamTask/getJsonTemplate`,//获取数据采集脚本的模版
}
