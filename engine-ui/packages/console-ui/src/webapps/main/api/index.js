import utils from 'utils'
import http from './http'
import localDb from 'utils/localDb'
import { MY_APPS } from 'consts'

import RdosApi from 'rdos/api'
import DqSysApi from 'dataQuality/api/sysAdmin'
import DqApi from 'dataQuality/api'

import req from '../consts/reqUrls'

export default {

    // =========== datasource数据源模块 ==================//
    addOrUpdateSource(source) {
        return http.post(offlineReq.SAVE_DATA_SOURCE, source)
    },

    deleteDataSource(params) {
        return http.post(offlineReq.DELETE_DATA_SOURCE, params)
    },
    queryDataSource(params) {
        return http.post(offlineReq.QUERY_DATA_SOURCE, params)
    },

    getDataSourceById(params) {
        return http.post(offlineReq.GET_DATA_SOURCE_BY_ID, params)
    },
    testDSConnection(params) {
        return http.post(offlineReq.TEST_DATA_SOURCE_CONNECTION, params)
    },
    getDataSourceTypes(params) {
        return http.post(offlineReq.GET_DATA_SOURCE_TYPES, params)
    },

    // ========== 角色相关 ==========
    queryRole(app, params) {
        switch( app ) {
            case MY_APPS.DATA_QUALITY:
            return DqSysApi.queryRole(params);
            case MY_APPS.RDOS: 
            default:
                return RdosApi.getRoleList(params);
        }
    },

    updateRole(app, params) {
        switch( app ) {
            case MY_APPS.RDOS: 
                return RdosApi.updateRole(params);
            case MY_APPS.DATA_QUALITY:
                return DqSysApi.queryRole(params);
            default: return ;
        }
    },

    deleteRole(app, params) {
        switch( app ) {
            case MY_APPS.RDOS: 
                return RdosApi.deleteRole(params);
            case MY_APPS.DATA_QUALITY:
                return DqSysApi.deleteRole(params);
            default: return ;
        }
    },

    getRoleInfo(app, params) {
        switch( app ) {
            case MY_APPS.RDOS: 
                return RdosApi.getRoleInfo(params);
            case MY_APPS.DATA_QUALITY:
                return DqSysApi.getRolePerission(params);
            default: return ;
        }
    },


    getRoleTree(app, params) {
        switch( app ) {
            case MY_APPS.RDOS: 
                return RdosApi.getRoleTree(params);
            case MY_APPS.DATA_QUALITY:
                return DqSysApi.getRolePerissions(params);
            default: return;
        }
    },

    // ========== 消息相关 ==========
    getMessage(app, params) {
        switch( app ) {
            case MY_APPS.DATA_QUALITY:
                return DqApi.getMessage(params);
            default: return null;
        }
    },

    getMsgById(app, params) {
        switch( app ) {
            case MY_APPS.DATA_QUALITY:
                return DqApi.getMessage(params);
            default: return null;
        }
    },

    markAsRead(app, params) {
        switch( app ) {
            case MY_APPS.DATA_QUALITY:
                return DqApi.markAsRead(params);
            default: return null;
        }
    },

    markAsAllRead(app,  params) {
        switch( app ) {
            case MY_APPS.DATA_QUALITY:
                return DqApi.markAsAllRead(params);
            default: return null;
        }
    },

    deleteMsgs(app,  params) {
        switch( app ) {
            case MY_APPS.DATA_QUALITY:
                return DqApi.deleteMsgs(params);
            default: return null;
        }
    },
    
    getProjects(app, params) {
        switch( app ) {
            case MY_APPS.RDOS: 
                return RdosApi.getProjects(params);
            default: return;
        }
    },

    // ========== 用户相关 ==========
    queryUser(app, params) {
        switch( app ) {
            case MY_APPS.RDOS: 
                return RdosApi.getProjectUsers(params);
            case MY_APPS.DATA_QUALITY:
                return DqSysApi.getUsers(params);
            default: return;
        }
    },

    loadUsersNotInProject(app, params) {
        switch( app ) {
            case MY_APPS.RDOS: 
                return RdosApi.getNotProjectUsers(params);
            case MY_APPS.DATA_QUALITY:
                return DqSysApi.getUsersNotInProject(params);
            default: return;
        }
    },

    addRoleUser(app, params) {
        switch( app ) {
            case MY_APPS.RDOS: 
                return RdosApi.addRoleUser(params);
            case MY_APPS.DATA_QUALITY:
                return DqSysApi.addUser(params);
            default: return;
        }
    },

    removeProjectUser(app, params) {
        switch( app ) {
            case MY_APPS.RDOS: 
                return RdosApi.removeProjectUser(params);
            case MY_APPS.DATA_QUALITY:
                return DqSysApi.delUser(params);
            default: return;
        }
    },

    updateUserRole(app, params) {
        switch( app ) {
            case MY_APPS.RDOS: 
                return RdosApi.updateUserRole(params);
            case MY_APPS.DATA_QUALITY:
                return DqSysApi.updateUser(params);
            default: return;
        }
    },

}
