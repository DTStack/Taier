import req from '../consts/reqRelation';
import http from './http';

export default {
    getRelations (params?: any) {
        return http.post(req.GET_RELATIONS, params);
    },
    getRelation (params?: any) {
        return http.post(req.GET_RELATION, params);
    },
    createRelation (params?: any) {
        return http.post(req.CREATE_RELATION, params);
    },
    deleteRelation (params?: any) {
        return http.post(req.DELETE_RELATION, params);
    },
    updateRelation (params?: any) {
        return http.post(req.UPDATE_RELATION, params);
    }
}
