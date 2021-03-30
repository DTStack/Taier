import { RDOS_BASE_URL } from '../consts/reqUrls';

/**
 * RDOS 全局接口请求地址
 */
export default {
    // ===== 消息管理 ===== //
    MASSAGE_QUERY: `${RDOS_BASE_URL}/common/notifyRecord/pageQuery`,
    GET_MASSAGE_BY_ID: `${RDOS_BASE_URL}/common/notifyRecord/getOne`,
    MASSAGE_MARK_AS_READ: `${RDOS_BASE_URL}/common/notifyRecord/tabRead`,
    MASSAGE_MARK_AS_ALL_READ: `${RDOS_BASE_URL}/common/notifyRecord/allRead`,
    MASSAGE_DELETE: `${RDOS_BASE_URL}/common/notifyRecord/delete`,
    GET_ROLE_LIST: `${RDOS_BASE_URL}/common/role/pageQuery`, // 获取角色列表
    UPDATE_USER_ROLE: `${RDOS_BASE_URL}/common/roleUser/updateUserRole`, // 更新用户角色
    UPDATE_ROLE: `${RDOS_BASE_URL}/common/role/addOrUpdateRole`, // 新建/更新角色
    DELETE_ROLE: `${RDOS_BASE_URL}/common/role/deleteRole`, // 删除角色
    GET_ROLE_TREE: `${RDOS_BASE_URL}/common/permission/tree`, // 编辑角色
    GET_ROLE_INFO: `${RDOS_BASE_URL}/common/permission/getPermissionIdsByRoleId`, // 获取角色信息
    GET_PROJECT_USERS: `${RDOS_BASE_URL}/common/project/getProjectUsers`, // 获取所在的项目所有用户
    REMOVE_USER_FROM_PROJECT: `${RDOS_BASE_URL}/common/roleUser/removeRoleUserFromProject`, // 删除项目用户接口
    GET_PROJECT_LIST: `${RDOS_BASE_URL}/common/project/getProjects` // 获取当前用户有权限的项目列表
}
