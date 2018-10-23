
import http from './http'
import req from '../consts/reqUrls'

export default {

    getCreateSQL(params) {
        return http.post(req.GET_CREATE_SQL, params);
    },

    createTable(params) {
        return http.post(req.CREATE_TABLE, params);
    },

    getTableByDB(params) {
        return http.post(req.GET_TABLE_BY_DB, params);
    },
  saveTableInfo(params){
      return http.post(req.SAVE_TABLE_INFO, params)
  },
  getTableDetail(params){
      console.log(params)
      return http.post(req.GET_TABLE_DETAIL, params)
  },
}