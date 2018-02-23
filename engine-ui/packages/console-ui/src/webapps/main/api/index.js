import utils from 'utils'
import http from './http'
import localDb from 'utils/localDb'
import { MY_APPS } from 'consts'

import RdosApi from 'rdos/api'
import DqApi from 'dataQuality/api/sysAdmin'

import req from '../consts/reqUrls'

function getReqFunc(app) {
    switch( app ) {
        case MY_APPS.RDOS: 
            return RdosApi.getProjectUsers;
        case MY_APPS.DATA_QUALITY:
            return DqApi.getProjectUsers;
        default:
            return '';
    }
}

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

    queryRole(app, params) {
        switch( app ) {
            case MY_APPS.RDOS: 
                return RdosApi.getRoleList(params);
            case MY_APPS.DATA_QUALITY:
                return DqApi.queryRole(params);
            default: return ;
        }
    },

    updateRole(app, params) {
        switch( app ) {
            case MY_APPS.RDOS: 
                return RdosApi.updateRole(params);
            case MY_APPS.DATA_QUALITY:
                return DqApi.queryRole(params);
            default: return ;
        }
    },

    deleteRole(app, params) {
        switch( app ) {
            case MY_APPS.RDOS: 
                return RdosApi.deleteRole(params);
            case MY_APPS.DATA_QUALITY:
                return DqApi.deleteRole(params);
            default: return ;
        }
    },

    getRoleInfo(app, params) {
        switch( app ) {
            case MY_APPS.RDOS: 
                return RdosApi.getRoleInfo(params);
            case MY_APPS.DATA_QUALITY:
                return DqApi.getRolePerission(params);
            default: return ;
        }
    },

    queryUser(app, params) {
        switch( app ) {
            case MY_APPS.RDOS: 
                return RdosApi.getProjectUsers(params);
            case MY_APPS.DATA_QUALITY:
                return DqApi.getProjectUsers(params);
            default: return;
        }
    },

    getRoleTree(app, params) {
        switch( app ) {
            case MY_APPS.RDOS: 
                return RdosApi.getRoleTree(params);
            case MY_APPS.DATA_QUALITY:
                return DqApi.getRolePerissions(params);
            default: return;
        }
    },
    
}
