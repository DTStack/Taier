// 从config文件全局读取
import { DL_BASE_URL } from 'config/base';

export default {

    // ===== 系统管理 ===== //
    LB_ROLE_QUERY: `${DL_BASE_URL}/role/pageQuery`,
    LB_ROLE_UPDATE: `${DL_BASE_URL}/role/addOrUpdateRole`,
    LB_ROLE_DELETE: `${DL_BASE_URL}/role/deleteRole`,
    LB_ROLE_PERMISSION_TREE: `${DL_BASE_URL}/permission/tree`,
    LB_ROLE_PERMISSION: `${DL_BASE_URL}/permission/getPermissionIdsByRoleId`,

    // ===== 用户相关 ===== //
    LB_GET_USER_BY_ID: `${DL_BASE_URL}/user/getUserById`, // 根据用户ID获取用户
    LB_GET_USER_PAGES: `${DL_BASE_URL}/user/pageQuery`,
    LB_GET_USER_NOT_IN_PROJECT: `${DL_BASE_URL}/user/listNotIn`,
    LB_USER_ROLE_ADD: `${DL_BASE_URL}/roleUser/addRoleUser`,
    LB_USER_ROLE_DELETE: `${DL_BASE_URL}/roleUser/remove`,
    LB_USER_ROLE_UPDATE: `${DL_BASE_URL}/roleUser/updateUserRole`,

    // ===== 消息管理 ===== //
    MASSAGE_QUERY: `${DL_BASE_URL}/notify/pageQuery`,
    GET_MASSAGE_BY_ID: `${DL_BASE_URL}/notify/getOne`,
    MASSAGE_MARK_AS_READ: `${DL_BASE_URL}/notify/tabRead`,
    MASSAGE_MARK_AS_ALL_READ: `${DL_BASE_URL}/notify/allRead`,
    MASSAGE_DELETE: `${DL_BASE_URL}/notify/delete`,

    // ===== 总览 ===== //

    // ===== 标签注册 ===== //
    QUERY_REGISTERED_LABEL: `${DL_BASE_URL}/tag/registerQuery`,       // 查询注册标签
    ADD_REGISTER_LABEL: `${DL_BASE_URL}/tag/registerAdd`,             // 新增注册标签
    UPDATE_LABEL: `${DL_BASE_URL}/tag/update`,                        // 更新标签
    DEL_LABEL: `${DL_BASE_URL}/tag/deleteTag`,                        // 删除标签

    // ===== 标签生成 ===== //
    QUERY_RESOLVED_LABEL: `${DL_BASE_URL}/tag/customQuery`,           // 查询生成标签
    ADD_LABEL: `${DL_BASE_URL}/tag/customAdd`,                        // 新增标签生成
    UPDATE_LABEL: `${DL_BASE_URL}/tag/update`,                        // 更新标签
    DEL_LABEL: `${DL_BASE_URL}/tag/deleteTag`,                        // 删除标签
    GET_RESOLVED_LABEL_RETAIL: `${DL_BASE_URL}/tag/getOne`,           // 标签生成详情
    EDIT_CONDITION: `${DL_BASE_URL}/tag/editCustomCondition`,         // 编辑过滤条件
    DELETE_CONDITION: `${DL_BASE_URL}/tag/deleteCustomCondition`,     // 删除过滤条件

    // ===== 数据源管理 ===== //
    GET_DATA_SOURCES: `${DL_BASE_URL}/dataSource/pageQuery`,          // 查询数据源
    CHECK_CONNECTION: `${DL_BASE_URL}/dataSource/checkConnection`,    // 检查连通性
    ADD_DATA_SOURCE: `${DL_BASE_URL}/dataSource/addSource`,           // 新增数据源
    UPDATE_DATA_SOURCE: `${DL_BASE_URL}/dataSource/updateSource`,     // 更新数据源
    DELETE_DATA_SOURCE: `${DL_BASE_URL}/dataSource/deleteSource`,     // 删除数据源
    GET_DATA_SOURCE_BY_ID: `${DL_BASE_URL}/dataSource/getBySourceId`, // 获取数据源By Id

    GET_DATA_SOURCES_LIST: `${DL_BASE_URL}/dataSource/list`,          // 查询已添加的数据源类型
    GET_DATA_SOURCES_TYPE: `${DL_BASE_URL}/dataSource/getTypes`,      // 查询所有数据源类型
    GET_DATA_SOURCES_TABLE: `${DL_BASE_URL}/dataSource/tablelist`,    // 查询数据源下的表
    GET_DATA_SOURCES_COLUMN: `${DL_BASE_URL}/dataSource/tablecolumn`, // 查询数据源下的列
    GET_DATA_SOURCES_PREVIEW: `${DL_BASE_URL}/dataSource/preview`,    // 预览数据源下的数据
    

    // ===== common ===== //
    LB_GET_USER_LIST: `${DL_BASE_URL}/user/list`,                     // 获取所有用户
}
