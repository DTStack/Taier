import http from './http'
import req from '../consts/reqUrls'

export default {
    getAllApiList (params: any) {
        return http.post(req.GET_ALL_API_LIST, params);
    },
    getDataSourceByBaseInfo (params: any) {
        return http.post(req.GET_DATASOURCE_BASE_INFO, params);
    },
    deleteApi (params: any) {
        return http.post(req.DELETE_API, params);
    },
    updateApiStatus (params: any) {
        return http.post(req.UPDATE_API, params);
    },
    getApiCallUserRankList (params: any) {
        return http.post(req.GET_API_CALL_RANK, params);
    },
    getApiUserApplyList (params: any) {
        return http.post(req.GET_API_BUY_STATE, params);
    },
    getApiCallTime (params: any) {
        return http.post(req.GET_API_TIME_INFO_FOR_MANAGER, params);
    },
    updateCatalogue (params: any) {
        return http.post(req.UPDATE_CATAGORY, params);
    },
    addCatalogue (params: any) {
        return http.post(req.ADD_CATAGORY, params);
    },
    deleteCatalogue (params: any) {
        return http.post(req.DELETE_CATAGORY, params);
    },
    createApi (params: any) {
        return http.post(req.NEW_API, params);
    },
    tablelist (params: any) {
        return http.post(req.GET_TABLE_BY_DATASOURCE, params);
    },
    tablecolumn (params: any) {
        return http.post(req.GET_TABLE_COLUMNS_DETAIL, params);
    },
    previewData (params: any) {
        return http.post(req.GET_TABLE_PREVIEW_DATA, params);
    },
    updateApi (params: any) {
        return http.post(req.CHANGE_API, params);
    },
    getApiInfo (params: any) {
        return http.post(req.GET_API_DETAIL_INFO, params);
    },
    getPageInfo (params: any) {
        return http.post(req.GET_PAGE_INFO, params);
    },
    getApiCallErrorInfoForManager (params: any) {
        return http.post(req.GET_API_CALL_ERROR_INFO_ADMIN, params);
    },
    saveOrUpdateApiInfo (params: any) {
        return http.post(req.SAVE_OR_UPDATE_APIINFO, params);
    },
    sqlformat (params: any) {
        return http.post(req.FORMAT_SQL, params);
    },
    sqlParser (params: any) {
        return http.post(req.PARSER_SQL, params);
    },
    apiTest (params: any) {
        return http.post(req.TEST_API, params);
    },
    checkNameExist (params: any) {
        return http.post(req.CHECK_API_IS_EXIST, params);
    },
    checkHostVaild (params: any) {
        return http.post(req.CHECK_HOST_PATH_IS_EXIST, params);
    },
    getSecuritySimpleList (params: any) {
        return http.post(req.GET_SECURITY_SIMPLE_LIST, params)
    },
    updateLimiter (params: any) {
        return http.post(req.UPDATE_LIMITER, params)
    },
    listSecurityGroupByApiId (params: any) {
        return http.post(req.LIST_SECURITY_GROUP_BY_ID, params)
    },
    getApiConfigInfo (params: any) {
        return http.post(req.GET_API_CONFIG_INFO, params)
    },
    getRegisterInfo (params: any) {
        return http.post(req.GET_REGISTER_API_INFO, params)
    },
    getWsdlXml (params: any) {
        return http.post(req.GET_WSD_XML, params)
    }
}
