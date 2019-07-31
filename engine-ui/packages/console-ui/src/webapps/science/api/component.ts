import http from './http'
import req from '../consts/reqUrls'

export default {
    loadTreeData (params: any) {
        return http.post(req.GET_COMPONENT_CATALOGUES, params);
    }
}
