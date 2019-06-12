import http from './http';
import req from '../consts/reqUrls'
export default {
    getLoginedUser () {
        return http.post(req.GET_USER_BY_ID)
    }
}
