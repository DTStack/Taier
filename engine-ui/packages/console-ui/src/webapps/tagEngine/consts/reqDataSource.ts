import { TAG_ENGINE_URL } from 'config/base';

export default {
    TAG_CREATE_DATA_SOURCE: `${TAG_ENGINE_URL}/dataSource/createDataSource`, // 添加数据源
    TAG_UPDATE_DATA_SOURCE: `${TAG_ENGINE_URL}/dataSource/updateDataSource`, // 更新数据源
    TAG_TEST_DATA_SOURCE_CONNECTION: `${TAG_ENGINE_URL}/dataSource/checkConnection`, // 测试数据源连通性
    TAG_DELETE_DATA_SOURCE: `${TAG_ENGINE_URL}/dataSource/deleteDataSource`, // 删除数据源
    GET_TAG_DATASOURCE_LIST: `${TAG_ENGINE_URL}/dataSource/dataSourcePage`,
    GET_DATASOURCE_TYPES: `${TAG_ENGINE_URL}/dataSource/getTypes` // 获取数据源类型
}
