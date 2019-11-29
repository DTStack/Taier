import req from '../consts/reqRelation';
import http from './http';
import { IQueryParams } from '../model/comm';
import { IRelation } from '../model/relation';

export default {
    getRelations (params?: IQueryParams) {
        return http.post(req.GET_RELATIONS, params);
    },
    getRelation (relation?: { relationId: number }) {
        return http.post(req.GET_RELATION, relation);
    },
    createRelation (relation?: IRelation) {
        return http.post(req.CREATE_RELATION, relation);
    },
    deleteRelation (relation?: { relationId: number }) {
        return http.post(req.DELETE_RELATION, relation);
    },
    updateRelation (relation?: IRelation) {
        return http.post(req.UPDATE_RELATION, relation);
    }
}
