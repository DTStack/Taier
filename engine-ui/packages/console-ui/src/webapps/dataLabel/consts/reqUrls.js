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
    LB_USER_ROLE_ADD: `${DL_BASE_URL}/roleUser/addRoleUser`,
    LB_USER_ROLE_DELETE: `${DL_BASE_URL}/roleUser/remove`,
    LB_USER_ROLE_UPDATE: `${DL_BASE_URL}/roleUser/updateUserRole`,
    LB_GET_USER_NOT_IN_PROJECT: `${DL_BASE_URL}/user/listNotIn`,

    // ===== 消息管理 ===== //
    MASSAGE_QUERY: `${DL_BASE_URL}/notify/pageQuery`,
    GET_MASSAGE_BY_ID: `${DL_BASE_URL}/notify/getOne`,
    MASSAGE_MARK_AS_READ: `${DL_BASE_URL}/notify/tabRead`,
    MASSAGE_MARK_AS_ALL_READ: `${DL_BASE_URL}/notify/allRead`,
    MASSAGE_DELETE: `${DL_BASE_URL}/notify/delete`,

    // ===== 总览 ===== //
    

    // ===== 数据源管理 ===== //
    GET_DATA_SOURCES: `${DL_BASE_URL}/dataSource/pageQuery`,          // 通过查询数据源
    CHECK_CONNECTION: `${DL_BASE_URL}/dataSource/checkConnection`,    // 检查数据库连接
    ADD_DATA_SOURCES: `${DL_BASE_URL}/dataSource/addSource`,          // 新增数据源
    UPDATE_DATA_SOURCES: `${DL_BASE_URL}/dataSource/updateSource`,    // 新增数据源
    DELETE_DATA_SOURCES: `${DL_BASE_URL}/dataSource/deleteSource`,    // 删除数据源

    GET_DATA_SOURCES_LIST: `${DL_BASE_URL}/dataSource/list`,          // 查询已添加的数据源类型
    GET_DATA_SOURCES_TYPE: `${DL_BASE_URL}/dataSource/getTypes`,      // 查询所有数据源类型
    GET_DATA_SOURCES_TABLE: `${DL_BASE_URL}/dataSource/tablelist`,    // 查询数据源下的表
    GET_DATA_SOURCES_PART: `${DL_BASE_URL}/dataSource/getPartValue`,  // 查询数据源下的分区
    GET_DATA_SOURCES_COLUMN: `${DL_BASE_URL}/dataSource/tablecolumn`, // 查询数据源下的列
    GET_DATA_SOURCES_PREVIEW: `${DL_BASE_URL}/dataSource/preview`,    // 预览数据源下的数据
    

  
    // ===== common ===== //
    LB_GET_USER_LIST: `${DL_BASE_URL}/user/list`,                     // 获取所有用户
}
