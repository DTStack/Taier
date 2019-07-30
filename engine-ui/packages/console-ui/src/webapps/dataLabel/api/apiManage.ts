
import http from './http'
import req from '../consts/reqUrls'

export default {
    getAllApiList(params: any) {
        return http.post(req.GET_ALL_API_LIST, params);
    },
    getDataSourceByBaseInfo(params: any) {
        return http.post(req.GET_DATASOURCE_BASE_INFO, params);
    },
    deleteApi(params: any) {
        return http.post(req.DELETE_API, params);
    },
    updateApiStatus(params: any) {
        return http.post(req.UPDATE_API, params);
    },
    getApiCallUserRankList(params: any) {
        return http.post(req.GET_API_CALL_RANK, params);
    },
    getApiUserApplyList(params: any) {
        return http.post(req.GET_API_BUY_STATE, params);
    },
    updateCatalogue(params: any) {
        return http.post(req.UPDATE_CATAGORY, params);
    },
    addCatalogue(params: any) {
        return http.post(req.ADD_CATAGORY, params);
    },
    deleteCatalogue(params: any) {
        return http.post(req.DELETE_CATAGORY, params);
    },
    createApi(params: any) {
        return http.post(req.NEW_API, params);
    },
    tablelist(params: any) {
        return http.post(req.GET_TABLE_BY_DATASOURCE, params);
    },
    tablecolumn(params: any) {
        return http.post(req.GET_TABLE_COLUMNS_DETAIL, params);
    },
    previewData(params: any) {
        return http.post(req.GET_TABLE_PREVIEW_DATA, params);
    },
    updateApi(params: any) {
        return http.post(req.CHANGE_API, params);
    },
    getApiInfo(params: any) {
        return http.post(req.GET_API_DETAIL_INFO, params);
    },
    getApiCallErrorInfoForManager(params: any) {
        return http.post(req.GET_API_CALL_ERROR_INFO_ADMIN, params);
    }

}
