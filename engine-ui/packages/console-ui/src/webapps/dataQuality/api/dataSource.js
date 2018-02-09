import utils from 'utils'
import http from './http'
import req from '../consts/reqUrls'

export default {

    getDataSources(params) {
        return http.post(req.GET_DATA_SOURCES, params)
    },

}