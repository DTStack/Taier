import mc from 'mirror-creator';

import {
    ANALYTICS_ENGINE_BASE_URL
} from 'config/base';

export default {
    // 消息管理
    ANALYENGINE_MASSAGE_QUERY: `${ANALYTICS_ENGINE_BASE_URL}/notifyRecord/pageQuery`,
    ANALYENGINE_GET_MASSAGE_BY_ID: `${ANALYTICS_ENGINE_BASE_URL}/notifyRecord/getOne`,
    ANALYENGINE_MASSAGE_MARK_AS_READ: `${ANALYTICS_ENGINE_BASE_URL}/notifyRecord/tabRead`,
    ANALYENGINE_MASSAGE_MARK_AS_ALL_READ: `${ANALYTICS_ENGINE_BASE_URL}/notifyRecord/allRead`,
    ANALYENGINE_MASSAGE_DELETE: `${ANALYTICS_ENGINE_BASE_URL}/notifyRecord/delete`,

    // 用户
    ANALYENGINE_QUERY_USER:`${ANALYTICS_ENGINE_BASE_URL}/project/getProjectUsers`,//查询系统用户信息
    ANALYENGINE_REMOVE_USER:`${ANALYTICS_ENGINE_BASE_URL}/roleUser/remove`,//删除用户
    ANALYENGINE_UPDATE_USER_ROLE:`${ANALYTICS_ENGINE_BASE_URL}/roleUser/updateUserRole`,//更改用户角色
    ANALYENGINE_ADD_USER: `${ANALYTICS_ENGINE_BASE_URL}/roleUser/addRoleUserNew`, // 添加用户
    ANALYENGINE_SEARCH_UIC_USERS: `${ANALYTICS_ENGINE_BASE_URL}/project/getUicUsersNotInProject`, // 获取UIC用户列表
    // 角色
    ANALYENGINE_ROLE_QUERY:`${ANALYTICS_ENGINE_BASE_URL}/role/pageQuery`,//角色列表
    ANALYENGINE_GET_ROLE_TREE:`${ANALYTICS_ENGINE_BASE_URL}/permission/tree`,//获取权限树
    ANALYENGINE_ROLE_PERMISSION:`${ANALYTICS_ENGINE_BASE_URL}/permission/getPermissionIdsByRoleId`,//获取角色的权限
    ANALYENGINE_ROLE_PERMISSION_ADD_OR_EDIT:`${ANALYTICS_ENGINE_BASE_URL}/role/addOrUpdateRole`,//更新或添加角色权限
    ANALYENGINE_REMOVE_ROLE:`${ANALYTICS_ENGINE_BASE_URL}/role/deleteRole`,//删除角色
    


}
