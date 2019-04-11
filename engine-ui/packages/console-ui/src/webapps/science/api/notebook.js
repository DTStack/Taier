import http from './http'
import req from '../consts/reqUrls'

export default {
    loadTreeData (params) {
        return http.post(req.GET_NOTEBOOK_CATALOGUES, params);
    }
}
