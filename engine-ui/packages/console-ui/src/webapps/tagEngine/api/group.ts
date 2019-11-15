import req from '../consts/reqGroup';
import http from './http';

export default {
    getGroups (params?: any) {
        return http.post(req.GET_GROUPS, params);
    }
}
