import {
    ANALYTICS_ENGINE_BASE_URL
} from 'config/base';

export default {
    // 消息管理
    ANALYENGINE_MASSAGE_QUERY: `${ANALYTICS_ENGINE_BASE_URL}/notify/pageQuery`,
    ANALYENGINE_GET_MASSAGE_BY_ID: `${ANALYTICS_ENGINE_BASE_URL}/notifyRecord/getOne`,
    ANALYENGINE_MASSAGE_MARK_AS_READ: `${ANALYTICS_ENGINE_BASE_URL}/notifyRecord/tabRead`,
    ANALYENGINE_MASSAGE_MARK_AS_ALL_READ: `${ANALYTICS_ENGINE_BASE_URL}/notifyRecord/allRead`,
    ANALYENGINE_MASSAGE_DELETE: `${ANALYTICS_ENGINE_BASE_URL}/notifyRecord/delete`,

    // 用户
    // GET_DB_USER_LIST: `${ANALYTICS_ENGINE_BASE_URL}/service/user/pageQuery`, // 获取用户列表
    // GET_DB_USER_ROLE_LIST: `${ANALYTICS_ENGINE_BASE_URL}/service/roleUser/pageQuery`, // 获取用户角色列表
    // GET_USERS_NOT_IN_DB: `${ANALYTICS_ENGINE_BASE_URL}/service/user/getUicUsersNotInProject`, // 获取未添加到项目的用户
    // UPDATE_DB_USER_ROLE: `${ANALYTICS_ENGINE_BASE_URL}/service/roleUser/updateUserRole`, // 修改用户数据库角色
    // ADD_DB_USER: `${ANALYTICS_ENGINE_BASE_URL}/service/roleUser/addRoleUserNew`, // 添加数据库用户

    ANALYENGINE_GET_DBLIST: `${ANALYTICS_ENGINE_BASE_URL}/service/dataBaseManagement/listAllDatabases`, // 获取数据库列表

    ANALYENGINE_QUERY_USER: `${ANALYTICS_ENGINE_BASE_URL}/service/user/pageQuery`, // 查询系统用户信息
    ANALYENGINE_REMOVE_USER: `${ANALYTICS_ENGINE_BASE_URL}/service/roleUser/removeRoleUserFromProject`, // 删除用户
    ANALYENGINE_UPDATE_USER_ROLE: `${ANALYTICS_ENGINE_BASE_URL}/service/roleUser/updateUserRole`, // 更改用户角色
    ANALYENGINE_ADD_USER: `${ANALYTICS_ENGINE_BASE_URL}/service/roleUser/addRoleUserNew`, // 添加用户
    ANALYENGINE_SEARCH_UIC_USERS: `${ANALYTICS_ENGINE_BASE_URL}/service/user/getUicUsersNotInProject`, // 获取UIC用户列表 获取未添加到项目的用户
    // 角色
    ANALYENGINE_ROLE_QUERY: `${ANALYTICS_ENGINE_BASE_URL}/service/role/pageQuery`, // 角色列表
    ANALYENGINE_GET_ROLE_TREE: `${ANALYTICS_ENGINE_BASE_URL}/service/permission/tree`, // 获取权限树
    ANALYENGINE_ROLE_PERMISSION: `${ANALYTICS_ENGINE_BASE_URL}/service/permission/getPermissionIdsByRoleId`, // 获取角色的权限
    ANALYENGINE_ROLE_PERMISSION_ADD_OR_EDIT: `${ANALYTICS_ENGINE_BASE_URL}/role/addOrUpdateRole`, // 更新或添加角色权限
    ANALYENGINE_REMOVE_ROLE: `${ANALYTICS_ENGINE_BASE_URL}/role/deleteRole`// 删除角色

}
