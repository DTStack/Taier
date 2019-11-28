import http from './http'

import RdosApi from 'rdos/api'
import StreamApi from 'stream/api'
import ScienceApi from 'science/api'
import DqSysApi from 'dataQuality/api/sysAdmin'
import DqApi from 'dataQuality/api'
import TagApi from 'tagEngine/api'
import { MY_APPS } from '../consts';
import req from '../consts/reqUrls';
import rdosUrls from '../consts/rdosUrls';
import {
    UIC_BASE_URL
} from 'config/base';
// 分析引擎
import analyEngineUrls from '../consts/analyEngineUrls';

export default {
    checkRoot (params?: any) {
        return http.post(req.CHECKISROOT, params, { isSilent: true });
    },
    // 查看license目录
    getLicenseApp () {
        return http.get(`${UIC_BASE_URL}/v2/license/menu/RDOS`)
    },
    // 检查是否过期
    checkisOverdue (params?: any) {
        return http.get(req.CHECK_IS_OVERDUE, params);
    },
    // ================== 公共模块 ==================//
    getProjects (app: any, params?: any) {
        switch (app) {
            case MY_APPS.RDOS:
                return RdosApi.getProjects(params);
            case MY_APPS.STREAM:
                return StreamApi.getProjects(params);
            case MY_APPS.SCIENCE:
                return ScienceApi.comm.getAllProject(params);
            case MY_APPS.TAG:
                return TagApi.getAllProjects(params);
            default:
        }
    },

    // 获取数据库列表
    getDatabase (app: any, params?: any) {
        switch (app) {
            case MY_APPS.ANALYTICS_ENGINE:
                return http.post(analyEngineUrls.ANALYENGINE_GET_DBLIST, params);
            default:
        }
    },

    // ================== 角色相关 ==================//
    queryRole (app: any, params?: any) {
        switch (app) {
            case MY_APPS.DATA_QUALITY:
                return DqSysApi.queryRole(params);
            case MY_APPS.STREAM:
                return http.post(req.STREAM_ROLE_QUERY, params);
            case MY_APPS.ANALYTICS_ENGINE:
                return http.post(analyEngineUrls.ANALYENGINE_ROLE_QUERY, params);
            case MY_APPS.API:
                return http.post(req.DATAAPI_ROLE_QUERY, params);
            case MY_APPS.TAG:
                return http.post(req.DL_ROLE_QUERY, params);
            case MY_APPS.SCIENCE:
                return http.post(req.SCIENCE_ROLE_QUERY, params);
            case MY_APPS.RDOS:
            default:
                return RdosApi.getRoleList(params);
        }
    },

    updateRole (app: any, params?: any) {
        switch (app) {
            case MY_APPS.RDOS:
                return RdosApi.updateRole(params);
            case MY_APPS.STREAM:
                return http.post(req.STREAM_ROLE_PERMISSION_ADD_OR_EDIT, params);
            case MY_APPS.ANALYTICS_ENGINE:
                return http.post(analyEngineUrls.ANALYENGINE_ROLE_PERMISSION_ADD_OR_EDIT, params);
            case MY_APPS.DATA_QUALITY:
                return DqSysApi.updateRole(params);
            case MY_APPS.API:
                return http.post(req.DATAAPI_ROLE_PERMISSION_ADD_OR_EDIT, params)
            case MY_APPS.TAG:
                return http.post(req.DL_ROLE_PERMISSION_ADD_OR_EDIT, params);
            case MY_APPS.SCIENCE:
                return http.post(req.SCIENCE_ROLE_PERMISSION_ADD_OR_EDIT, params);
            default:
        }
    },

    deleteRole (app: any, params?: any) {
        switch (app) {
            case MY_APPS.RDOS:
                return RdosApi.deleteRole(params);
            case MY_APPS.STREAM:
                return http.post(req.STREAM_REMOVE_ROLE, params);
            case MY_APPS.ANALYTICS_ENGINE:
                return http.post(analyEngineUrls.ANALYENGINE_REMOVE_USER, params);
            case MY_APPS.DATA_QUALITY:
                return DqSysApi.deleteRole(params);
            case MY_APPS.API:
                return http.post(req.DATAAPI_REMOVE_ROLE, params);
            case MY_APPS.TAG:
                return http.post(req.DL_REMOVE_ROLE, params)
            case MY_APPS.SCIENCE:
                return http.post(req.SCIENCE_REMOVE_ROLE, params)
            default:
        }
    },

    getRoleInfo (app: any, params?: any) {
        switch (app) {
            case MY_APPS.RDOS:
                return RdosApi.getRoleInfo(params);
            case MY_APPS.STREAM:
                return http.post(req.STREAM_ROLE_PERMISSION, params);
            case MY_APPS.ANALYTICS_ENGINE:
                return http.post(analyEngineUrls.ANALYENGINE_ROLE_PERMISSION, params);
            case MY_APPS.DATA_QUALITY:
                return DqSysApi.getRolePerission(params);
            case MY_APPS.API:
                return http.post(req.DATAAPI_ROLE_PERMISSION, params);
            case MY_APPS.TAG:
                return http.post(req.DL_ROLE_PERMISSION, params);
            case MY_APPS.SCIENCE:
                return http.post(req.SCIENCE_ROLE_PERMISSION, params)
            default:
        }
    },

    getRoleTree (app: any, params?: any) {
        switch (app) {
            case MY_APPS.RDOS:
                return RdosApi.getRoleTree(params);
            case MY_APPS.STREAM:
                return http.post(req.STREAM_GET_ROLE_TREE, params);
            case MY_APPS.ANALYTICS_ENGINE:
                return http.post(analyEngineUrls.ANALYENGINE_GET_ROLE_TREE, params);
            case MY_APPS.DATA_QUALITY:
                return DqSysApi.getRolePerissions(params);
            case MY_APPS.API:
                return http.post(req.DATAAPI_GET_ROLE_TREE, params);
            case MY_APPS.TAG:
                return http.post(req.DL_GET_ROLE_TREE, params);
            case MY_APPS.SCIENCE:
                return http.post(req.SCIENCE_GET_ROLE_TREE, params)
            default:
        }
    },

    // ================== 消息相关 ================== //
    getMessage (app: any, params?: any) {
        switch (app) {
            case MY_APPS.RDOS:
                return http.post(rdosUrls.MASSAGE_QUERY, params)
            case MY_APPS.STREAM:
                return http.post(req.STREAM_MASSAGE_QUERY, params)
            case MY_APPS.ANALYTICS_ENGINE:
                return http.post(analyEngineUrls.ANALYENGINE_MASSAGE_QUERY, params)
            case MY_APPS.DATA_QUALITY:
                return DqApi.getMessage(params);
            case MY_APPS.API:
                return http.post(req.DATAAPI_MASSAGE_QUERY, params)
            case MY_APPS.TAG:
                return http.post(req.DL_MASSAGE_QUERY, params)
            case MY_APPS.SCIENCE:
                return http.post(req.SCIENCE_MASSAGE_QUERY, params)
            default: return null;
        }
    },

    getMsgById (app: any, params?: any) {
        switch (app) {
            case MY_APPS.RDOS:
                return http.post(rdosUrls.GET_MASSAGE_BY_ID, params)
            case MY_APPS.STREAM:
                return http.post(req.STREAM_GET_MASSAGE_BY_ID, params)
            case MY_APPS.ANALYTICS_ENGINE:
                return http.post(analyEngineUrls.ANALYENGINE_GET_MASSAGE_BY_ID, params)
            case MY_APPS.DATA_QUALITY:
                return DqApi.getMsgById(params);
            case MY_APPS.API:
                return http.post(req.DATAAPI_GET_MASSAGE_BY_ID, params)
            case MY_APPS.TAG:
                return http.post(req.DL_GET_MASSAGE_BY_ID, params);
            case MY_APPS.SCIENCE:
                return http.post(req.SCIENCE_GET_MASSAGE_BY_ID, params)
            default: return null;
        }
    },

    markAsRead (app: any, params?: any) {
        switch (app) {
            case MY_APPS.RDOS:
                return http.post(rdosUrls.MASSAGE_MARK_AS_READ, params)
            case MY_APPS.STREAM:
                return http.post(req.STREAM_MASSAGE_MARK_AS_READ, params)
            case MY_APPS.ANALYTICS_ENGINE:
                return http.post(analyEngineUrls.ANALYENGINE_MASSAGE_MARK_AS_READ, params)
            case MY_APPS.DATA_QUALITY:
                return DqApi.markAsRead(params);
            case MY_APPS.API:
                return http.post(req.DATAAPI_MASSAGE_MARK_AS_READ, params)
            case MY_APPS.TAG:
                return http.post(req.DL_MASSAGE_MARK_AS_READ, params)
            case MY_APPS.SCIENCE:
                return http.post(req.SCIENCE_MASSAGE_MARK_AS_READ, params)
            default: return null;
        }
    },

    markAsAllRead (app: any, params?: any) {
        switch (app) {
            case MY_APPS.RDOS:
                return http.post(rdosUrls.MASSAGE_MARK_AS_ALL_READ, params)
            case MY_APPS.STREAM:
                return http.post(req.STREAM_MASSAGE_MARK_AS_ALL_READ, params)
            case MY_APPS.ANALYTICS_ENGINE:
                return http.post(analyEngineUrls.ANALYENGINE_MASSAGE_MARK_AS_ALL_READ, params)
            case MY_APPS.DATA_QUALITY:
                return DqApi.markAsAllRead(params);
            case MY_APPS.API:
                return http.post(req.DATAAPI_MASSAGE_MARK_AS_ALL_READ, params);
            case MY_APPS.TAG:
                return http.post(req.DL_MASSAGE_MARK_AS_ALL_READ, params);
            case MY_APPS.SCIENCE:
                return http.post(req.SCIENCE_MASSAGE_MARK_AS_ALL_READ, params)
            default: return null;
        }
    },

    deleteMsgs (app: any, params?: any) {
        switch (app) {
            case MY_APPS.RDOS:
                return http.post(rdosUrls.MASSAGE_DELETE, params)
            case MY_APPS.STREAM:
                return http.post(req.STREAM_MASSAGE_DELETE, params)
            case MY_APPS.ANALYTICS_ENGINE:
                return http.post(analyEngineUrls.ANALYENGINE_MASSAGE_DELETE, params)
            case MY_APPS.DATA_QUALITY:
                return DqApi.deleteMsgs(params);
            case MY_APPS.API:
                return http.post(req.DATAAPI_MASSAGE_DELETE, params)
            case MY_APPS.TAG:
                return http.post(req.DL_MASSAGE_DELETE, params)
            case MY_APPS.SCIENCE:
                return http.post(req.SCIENCE_MASSAGE_DELETE, params)
            default: return null;
        }
    },

    // ================== 用户相关 ================== //
    queryUser (app: any, params?: any) {
        switch (app) {
            case MY_APPS.RDOS:
                return RdosApi.getProjectUsers(params);
            case MY_APPS.STREAM:
                return http.post(req.STREAM_QUERY_USER, params);
            case MY_APPS.ANALYTICS_ENGINE:
                return http.post(analyEngineUrls.ANALYENGINE_QUERY_USER, params);
            case MY_APPS.DATA_QUALITY:
                return DqSysApi.getUserPages(params);
            case MY_APPS.API:
                return http.post(req.DATAAPI_QUERY_USER, params)
            case MY_APPS.TAG:
                return http.post(req.DL_QUERY_USER, params)
            case MY_APPS.SCIENCE:
                return http.post(req.SCIENCE_QUERY_USER, params)
            default:
        }
    },

    /**
     * 获取待添加到项目中的用户列表
     */
    loadUsersNotInProject (app: any, params?: any) {
        switch (app) {
            case MY_APPS.RDOS:
                return http.post(req.RDOS_SEARCH_UIC_USERS, params)
            case MY_APPS.STREAM:
                return http.post(req.STREAM_SEARCH_UIC_USERS, params);
            case MY_APPS.ANALYTICS_ENGINE:
                return http.post(analyEngineUrls.ANALYENGINE_SEARCH_UIC_USERS, params);
            case MY_APPS.DATA_QUALITY:
                return http.post(req.DQ_SEARCH_UIC_USERS, params)
            case MY_APPS.TAG:
                return http.post(req.DL_SEARCH_UIC_USERS, params)
            case MY_APPS.API:
                return http.post(req.DATAAPI_SEARCH_UIC_USERS, params)
            case MY_APPS.SCIENCE:
                return http.post(req.SCIENCE_SEARCH_UIC_USERS, params)
            default:
        }
    },

    addRoleUser (app: any, params?: any) {
        switch (app) {
            case MY_APPS.RDOS:
                return http.post(req.RDOS_ADD_USER, params)
            case MY_APPS.STREAM:
                return http.post(req.STREAM_ADD_USER, params)
            case MY_APPS.ANALYTICS_ENGINE:
                return http.post(analyEngineUrls.ANALYENGINE_ADD_USER, params);
            case MY_APPS.DATA_QUALITY:
                return http.post(req.DQ_ADD_USER, params)
            case MY_APPS.TAG:
                return http.post(req.DL_ADD_USER, params)
            case MY_APPS.API:
                return http.post(req.DATAAPI_ADD_USER, params)
            case MY_APPS.SCIENCE:
                return http.post(req.SCIENCE_ADD_USER, params)
            default:
        }
    },

    removeProjectUser (app: any, params?: any) {
        switch (app) {
            case MY_APPS.RDOS:
                return RdosApi.removeProjectUser(params);
            case MY_APPS.STREAM:
                return http.post(req.STREAM_REMOVE_USER, params)
            case MY_APPS.ANALYTICS_ENGINE:
                return http.post(analyEngineUrls.ANALYENGINE_REMOVE_USER, params);
            case MY_APPS.DATA_QUALITY:
                return DqSysApi.delUser(params);
            case MY_APPS.API:
                return http.post(req.DATAAPI_REMOVE_USER, params);
            case MY_APPS.TAG:
                return http.post(req.DL_REMOVE_USER, params);
            case MY_APPS.SCIENCE:
                return http.post(req.SCIENCE_REMOVE_USER, params)
            default:
        }
    },

    updateUserRole (app: any, params?: any) {
        switch (app) {
            case MY_APPS.RDOS:
                return RdosApi.updateUserRole(params);
            case MY_APPS.STREAM:
                return http.post(req.STREAM_UPDATE_USER_ROLE, params)
            case MY_APPS.ANALYTICS_ENGINE:
                return http.post(analyEngineUrls.ANALYENGINE_UPDATE_USER_ROLE, params);
            case MY_APPS.DATA_QUALITY:
                return DqSysApi.updateUser(params);
            case MY_APPS.API:
                return http.post(req.DATAAPI_UPDATE_USER_ROLE, params);
            case MY_APPS.TAG:
                return http.post(req.DL_UPDATE_USER_ROLE, params);
            case MY_APPS.SCIENCE:
                return http.post(req.SCIENCE_UPDATE_USER_ROLE, params)
            default:
        }
    },

    getSafeAuditList (app: any, params?: any) {
        switch (app) {
            case MY_APPS.RDOS:
                params.appTag = 'BATCH'; // app类型：BATCH,STREAM,ANALYZE,QUALITY,API,SCIENCE
                return http.post(req.GET_AUDIT_LIST, params)
            case MY_APPS.API:
                params.appTag = 'API'; // app类型：BATCH,STREAM,ANALYZE,QUALITY,API,SCIENCE
                return http.post(req.GET_AUDIT_LIST, params)
            default:
        }
    },
    getOperationList (app: any, params: any = {}) {
        switch (app) {
            case MY_APPS.API:
                params.appTag = 'API';
                return http.post(req.GET_OPERATION_LIST, params)
            default:
        }
    }
}
