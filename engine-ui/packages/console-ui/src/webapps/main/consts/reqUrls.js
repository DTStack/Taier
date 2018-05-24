import mc from 'mirror-creator';

import {
     UIC_BASE_URL, 
     DATA_API_BASE_URL,
     DL_BASE_URL,
} from 'config/base';

export default {
    // ===== 用户模块 ===== //
    LOGOUT: `${UIC_BASE_URL}/v2/logout`,

    //===== 数据质量 ====//


    //===== 数据api ====//
    //**消息**//
    DATAAPI_MASSAGE_QUERY: `${DATA_API_BASE_URL}/notify/pageQuery`,
    DATAAPI_GET_MASSAGE_BY_ID: `${DATA_API_BASE_URL}/notify/getOne`,
    DATAAPI_MASSAGE_MARK_AS_READ: `${DATA_API_BASE_URL}/notify/tabRead`,
    DATAAPI_MASSAGE_MARK_AS_ALL_READ: `${DATA_API_BASE_URL}/notify/allRead`,
    DATAAPI_MASSAGE_DELETE: `${DATA_API_BASE_URL}/notify/delete`,

    //** 用户角色 *//
    DATAAPI_QUERY_USER:`${DATA_API_BASE_URL}/user/pageQuery`,//查询系统用户信息
    DATAAPI_REMOVE_USER:`${DATA_API_BASE_URL}/roleUser/remove`,//删除用户
    DATAAPI_UPDATE_USER_ROLE:`${DATA_API_BASE_URL}/roleUser/updateUserRole`,//更改用户角色
    DATAAPI_ROLE_QUERY:`${DATA_API_BASE_URL}/role/pageQuery`,//角色列表
    DATAAPI_GET_ROLE_TREE:`${DATA_API_BASE_URL}/permission/tree`,//获取权限树
    DATAAPI_ROLE_PERMISSION:`${DATA_API_BASE_URL}/permission/getPermissionIdsByRoleId`,//获取角色的权限
    DATAAPI_ROLE_PERMISSION_ADD_OR_EDIT:`${DATA_API_BASE_URL}/role/addOrUpdateRole`,//更新或添加角色权限
    DATAAPI_REMOVE_ROLE:`${DATA_API_BASE_URL}/role/deleteRole`,//删除角色


    //===== 数据标签 ====//
    //**消息**//
    DL_MASSAGE_QUERY: `${DL_BASE_URL}/notify/pageQuery`,
    DL_GET_MASSAGE_BY_ID: `${DL_BASE_URL}/notify/getOne`,
    DL_MASSAGE_MARK_AS_READ: `${DL_BASE_URL}/notify/tabRead`,
    DL_MASSAGE_MARK_AS_ALL_READ: `${DL_BASE_URL}/notify/allRead`,
    DL_MASSAGE_DELETE: `${DL_BASE_URL}/notify/delete`,

    //** 用户角色 *//
    DL_QUERY_USER:`${DL_BASE_URL}/user/pageQuery`,//查询系统用户信息
    DL_REMOVE_USER:`${DL_BASE_URL}/roleUser/remove`,//删除用户
    DL_UPDATE_USER_ROLE:`${DL_BASE_URL}/roleUser/updateUserRole`,//更改用户角色
    DL_ROLE_QUERY:`${DL_BASE_URL}/role/pageQuery`, // 角色列表
    DL_GET_ROLE_TREE:`${DL_BASE_URL}/permission/tree`,//获取权限树
    DL_ROLE_PERMISSION:`${DL_BASE_URL}/permission/getPermissionIdsByRoleId`,//获取角色的权限
    DL_ROLE_PERMISSION_ADD_OR_EDIT:`${DL_BASE_URL}/role/addOrUpdateRole`,//更新或添加角色权限
    DL_REMOVE_ROLE:`${DL_BASE_URL}/role/deleteRole`,//删除角色
}

