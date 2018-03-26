// 从config文件全局读取
import { DATA_API_BASE_URL } from 'config/base';

export default {

    // ===== common ===== //

    // ===== 数据源管理 ===== //
    GET_DATA_SOURCES: `${DATA_API_BASE_URL}/dataSource/pageQuery`,          // 通过查询数据源
    CHECK_CONNECTION: `${DATA_API_BASE_URL}/dataSource/checkConnection`,    // 检查数据库连接
    ADD_DATA_SOURCES: `${DATA_API_BASE_URL}/dataSource/addSource`,          // 新增数据源
    UPDATE_DATA_SOURCES: `${DATA_API_BASE_URL}/dataSource/updateSource`,    // 新增数据源
    DELETE_DATA_SOURCES: `${DATA_API_BASE_URL}/dataSource/deleteSource`,    // 删除数据源

    GET_DATA_SOURCES_LIST: `${DATA_API_BASE_URL}/dataSource/list`,          // 查询已添加的数据源类型
    GET_DATA_SOURCES_TYPE: `${DATA_API_BASE_URL}/dataSource/getTypes`,      // 查询所有数据源类型
    GET_DATA_SOURCES_TABLE: `${DATA_API_BASE_URL}/dataSource/tablelist`,    // 查询数据源下的表
    GET_DATA_SOURCES_COLUMN: `${DATA_API_BASE_URL}/dataSource/tablecolumn`, // 查询数据源下的列
    GET_DATA_SOURCES_PART: `${DATA_API_BASE_URL}/dataSource/getPartValue`,  // 查询数据源下的分区
    GET_DATA_SOURCES_PREVIEW: `${DATA_API_BASE_URL}/dataSource/preview`,    // 预览数据源下的数据
    
}
