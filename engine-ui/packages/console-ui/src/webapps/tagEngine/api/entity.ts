import req from '../consts/reqEntity';
import http from './http';

export default {
    getEntities (params?: any) {
        return http.post(req.GET_ENTITIES, params);
    }
}
