
import http from './http'
import req from '../consts/reqUrls'

export default{
  saveNewTable(params){
    return http.post(req.CREATE_NEW_TABLE, params)
  }
}