// 从config文件全局读取
import { DQ_BASE_URL } from 'config/base';

export default {

    // ===== 系统管理 ===== //
    DQ_ROLE_QUERY: `${DQ_BASE_URL}/role/pageQuery`,
    DQ_ROLE_UPDATE: `${DQ_BASE_URL}/role/addOrUpdateRole`,
    DQ_ROLE_DELETE: `${DQ_BASE_URL}/role/deleteRole`,
    DQ_ROLE_PERMISSION_TREE: `${DQ_BASE_URL}/permission/tree`,
    DQ_ROLE_PERMISSION: `${DQ_BASE_URL}/permission/getPermissionIdsByRoleId`,

    // ===== 用户相关 ===== //
    DQ_GET_USER_BY_ID: `${DQ_BASE_URL}/user/getUserById`, // 根据用户ID获取用户
    DQ_GET_USER_LIST: `${DQ_BASE_URL}/user/list`,
    DQ_GET_USER_PAGES: `${DQ_BASE_URL}/user/pageQuery`,
    DQ_USER_ROLE_ADD: `${DQ_BASE_URL}/roleUser/addRoleUser`,
    DQ_USER_ROLE_DELETE: `${DQ_BASE_URL}/roleUser/remove`,
    DQ_USER_ROLE_UPDATE: `${DQ_BASE_URL}/roleUser/updateUserRole`,
    DQ_GET_USER_NOT_IN_PROJECT: `${DQ_BASE_URL}/user/listNotIn`,

    // ===== 消息管理 ===== //
    MASSAGE_QUERY: `${DQ_BASE_URL}/notify/pageQuery`,
    GET_MASSAGE_BY_ID: `${DQ_BASE_URL}/notify/getOne`,
    MASSAGE_MARK_AS_READ: `${DQ_BASE_URL}/notify/tabRead`,
    MASSAGE_MARK_AS_ALL_READ: `${DQ_BASE_URL}/notify/allRead`,
    MASSAGE_DELETE: `${DQ_BASE_URL}/notify/delete`,

    // ===== common ===== //
    DQ_GET_ALL_DICT: `${DQ_BASE_URL}/dict/allDicts`,
    
}
