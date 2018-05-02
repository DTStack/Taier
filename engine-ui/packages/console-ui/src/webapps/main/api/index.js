import utils from 'utils'
import http from './http'
import localDb from 'utils/localDb'

import RdosApi from 'rdos/api'
import DqSysApi from 'dataQuality/api/sysAdmin'
import DqApi from 'dataQuality/api'

import { MY_APPS } from '../consts';
import req from '../consts/reqUrls';
import rdosUrls from '../consts/rdosUrls';

export default {

    // ================== 公共模块 ==================//
    getProjects(app, params) {
        switch (app) {
            case MY_APPS.RDOS:
                return RdosApi.getProjects(params);
            default: return;
        }
    },

    // ================== 角色相关 ==================//
    queryRole(app, params) {
        switch (app) {
            case MY_APPS.DATA_QUALITY:
                return DqSysApi.queryRole(params);
            case MY_APPS.API:
                return http.post(req.DATAAPI_ROLE_QUERY, params)
            case MY_APPS.RDOS:
            default:
                return RdosApi.getRoleList(params);
        }
    },

    updateRole(app, params) {
        switch (app) {
            case MY_APPS.RDOS:
                return RdosApi.updateRole(params);
            case MY_APPS.DATA_QUALITY:
                return DqSysApi.updateRole(params);
            case MY_APPS.API:
                return http.post(req.DATAAPI_ROLE_PERMISSION_ADD_OR_EDIT, params)
            default: return;
        }
    },

    deleteRole(app, params) {
        switch (app) {
            case MY_APPS.RDOS:
                return RdosApi.deleteRole(params);
            case MY_APPS.DATA_QUALITY:
                return DqSysApi.deleteRole(params);
            case MY_APPS.API:
                return http.post(req.DATAAPI_REMOVE_ROLE, params);
            default: return;
        }
    },

    getRoleInfo(app, params) {
        switch (app) {
            case MY_APPS.RDOS:
                return RdosApi.getRoleInfo(params);
            case MY_APPS.DATA_QUALITY:
                return DqSysApi.getRolePerission(params);
            case MY_APPS.API:
                return http.post(req.DATAAPI_ROLE_PERMISSION,params)
            default: return;
        }
    },


    getRoleTree(app, params) {
        switch (app) {
            case MY_APPS.RDOS:
                return RdosApi.getRoleTree(params);
            case MY_APPS.DATA_QUALITY:
                return DqSysApi.getRolePerissions(params);
            case MY_APPS.API:
                return http.post(req.DATAAPI_GET_ROLE_TREE, params)
            default: return;
        }
    },

    // ================== 消息相关 ================== //
    getMessage(app, params) {
    
        switch (app) {
            case MY_APPS.RDOS:
                return http.post(rdosUrls.MASSAGE_QUERY, params)
            case MY_APPS.DATA_QUALITY:
                return DqApi.getMessage(params);
            case MY_APPS.API:
                return http.post(req.DATAAPI_MASSAGE_QUERY, params)
            default: return null;
        }
    },

    getMsgById(app, params) {
        switch (app) {
            case MY_APPS.RDOS:
                return http.post(rdosUrls.GET_MASSAGE_BY_ID, params)
            case MY_APPS.DATA_QUALITY:
                return DqApi.getMsgById(params);
            case MY_APPS.API:
                return http.post(req.DATAAPI_GET_MASSAGE_BY_ID, params)
            default: return null;
        }
    },

    markAsRead(app, params) {
        switch (app) {
            case MY_APPS.RDOS:
                return http.post(rdosUrls.MASSAGE_MARK_AS_READ, params)
            case MY_APPS.DATA_QUALITY:
                return DqApi.markAsRead(params);
            case MY_APPS.API:
                return http.post(req.DATAAPI_MASSAGE_MARK_AS_READ, params)
            default: return null;
        }
    },

    markAsAllRead(app, params) {
        switch (app) {
            case MY_APPS.RDOS:
                return http.post(rdosUrls.MASSAGE_MARK_AS_ALL_READ, params)
            case MY_APPS.DATA_QUALITY:
                return DqApi.markAsAllRead(params);
            case MY_APPS.API:
                return http.post(req.DATAAPI_MASSAGE_MARK_AS_ALL_READ, params)
            default: return null;
        }
    },

    deleteMsgs(app, params) {
        switch (app) {
            case MY_APPS.RDOS:
                return http.post(rdosUrls.MASSAGE_DELETE, params)
            case MY_APPS.DATA_QUALITY:
                return DqApi.deleteMsgs(params);
            case MY_APPS.API:
                return http.post(req.DATAAPI_MASSAGE_DELETE, params)
            default: return null;
        }
    },

    // ================== 用户相关 ================== //
    queryUser(app, params) {
        switch (app) {
            case MY_APPS.RDOS:
                return RdosApi.getProjectUsers(params);
            case MY_APPS.DATA_QUALITY:
                return DqSysApi.getUserPages(params);
            case MY_APPS.API:
                return http.post(req.DATAAPI_QUERY_USER, params)
            default: return;
        }
    },

    loadUsersNotInProject(app, params) {
        switch (app) {
            case MY_APPS.RDOS:
                return RdosApi.getNotProjectUsers(params);
            case MY_APPS.DATA_QUALITY:
                return DqSysApi.getUsersNotInProject(params);
            default: return;
        }
    },

    addRoleUser(app, params) {
        switch (app) {
            case MY_APPS.RDOS:
                return RdosApi.addRoleUser(params);
            case MY_APPS.DATA_QUALITY:
                return DqSysApi.addUser(params);
            default: return;
        }
    },

    removeProjectUser(app, params) {
        switch (app) {
            case MY_APPS.RDOS:
                return RdosApi.removeProjectUser(params);
            case MY_APPS.DATA_QUALITY:
                return DqSysApi.delUser(params);
            case MY_APPS.API:
                return http.post(req.DATAAPI_REMOVE_USER, params);
            default: return;
        }
    },

    updateUserRole(app, params) {
        switch (app) {
            case MY_APPS.RDOS:
                return RdosApi.updateUserRole(params);
            case MY_APPS.DATA_QUALITY:
                return DqSysApi.updateUser(params);
            case MY_APPS.API:
                return http.post(req.DATAAPI_UPDATE_USER_ROLE, params);
            default: return;
        }
    },

}
