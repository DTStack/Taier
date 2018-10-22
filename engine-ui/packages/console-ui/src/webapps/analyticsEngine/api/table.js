
import http from './http'
import req from '../consts/reqUrls'

export default{
  saveNewTable(params){
    return http.post(req.CREATE_NEW_TABLE, params)
  },
  getTableDetail(params){
      console.log(params)
      return http.post(req.GET_TABLE_DETAIL, params)
  },
  saveTableInfo(params){
      return http.post(req.SAVE_TABLE_INFO, params)
  }
}