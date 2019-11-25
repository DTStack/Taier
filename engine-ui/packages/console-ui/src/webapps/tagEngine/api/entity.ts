import req from '../consts/reqEntity';
import http from './http';

export default {
    getEntities (params?: any) {
        return http.post(req.GET_ENTITIES, params);
    },

    getDictList (params?: any) {
        return http.post(req.GET_DICTIONARIES, params);
    },
    addOrUpdateDict (params?: any) {
        return http.post(req.ADD_OR_UPDATE_DICT, params);
    },
    getDictDetail (params?: any) {
        return http.post(req.GET_DICT_DETAIL, params);
    },
    deleteDict (params?: any) {
        return http.post(req.DELETE_DICT, params);
    }
}
