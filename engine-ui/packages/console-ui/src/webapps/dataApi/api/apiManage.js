import http from './http'
import req from '../consts/reqUrls'

export default {
    getAllApiList (params) {
        return http.post(req.GET_ALL_API_LIST, params);
    },
    getDataSourceByBaseInfo (params) {
        return http.post(req.GET_DATASOURCE_BASE_INFO, params);
    },
    deleteApi (params) {
        return http.post(req.DELETE_API, params);
    },
    updateApiStatus (params) {
        return http.post(req.UPDATE_API, params);
    },
    getApiCallUserRankList (params) {
        return http.post(req.GET_API_CALL_RANK, params);
    },
    getApiUserApplyList (params) {
        return http.post(req.GET_API_BUY_STATE, params);
    },
    getApiCallTime (params) {
        return http.post(req.GET_API_TIME_INFO_FOR_MANAGER, params);
    },
    updateCatalogue (params) {
        return http.post(req.UPDATE_CATAGORY, params);
    },
    addCatalogue (params) {
        return http.post(req.ADD_CATAGORY, params);
    },
    deleteCatalogue (params) {
        return http.post(req.DELETE_CATAGORY, params);
    },
    createApi (params) {
        return http.post(req.NEW_API, params);
    },
    tablelist (params) {
        return http.post(req.GET_TABLE_BY_DATASOURCE, params);
    },
    tablecolumn (params) {
        return http.post(req.GET_TABLE_COLUMNS_DETAIL, params);
    },
    previewData (params) {
        return http.post(req.GET_TABLE_PREVIEW_DATA, params);
    },
    updateApi (params) {
        return http.post(req.CHANGE_API, params);
    },
    getApiInfo (params) {
        return http.post(req.GET_API_DETAIL_INFO, params);
    },
    getApiCallErrorInfoForManager (params) {
        return http.post(req.GET_API_CALL_ERROR_INFO_ADMIN, params);
    },
    saveOrUpdateApiInfo (params) {
        return http.post(req.SAVE_OR_UPDATE_APIINFO, params);
    },
    sqlformat (params) {
        return http.post(req.FORMAT_SQL, params);
    },
    sqlParser (params) {
        return http.post(req.PARSER_SQL, params);
    },
    apiTest (params) {
        return http.post(req.TEST_API, params);
    },
    checkNameExist (params) {
        return http.post(req.CHECK_API_IS_EXIST, params);
    },
    getSecuritySimpleList (params) {
        return http.post(req.GET_SECURITY_SIMPLE_LIST, params)
    },
    updateLimiter (params) {
        return http.post(req.UPDATE_LIMITER, params)
    },
    listSecurityGroupByApiId (params) {
        return http.post(req.LIST_SECURITY_GROUP_BY_ID, params)
    },
    getApiConfigInfo (params) {
        return http.post(req.GET_API_CONFIG_INFO, params)
    },
    getRegisterInfo (params) {
        return http.post(req.GET_REGISTER_API_INFO, params)
    }
}
