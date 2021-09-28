import { Fetch } from 'ko-request';
import Restful from './restful';
import Swagger from './swagger';
import queryParse from '@/utils/queryParser';
const http = new Fetch();
const { keys } = Object;

function mapUrlObjToFuncObj(urlObj) {
  const API = {};
  keys(urlObj).forEach((key) => {
    const item = urlObj[key];
    API[key] = async function (params, config: any = {}) {
      const queryParams: any = queryParse(window.location.hash);
      // 所有的请求接口统一带上header
      if (queryParams.datasource_id) {
        config.headers = new Headers({
          dt_datasource_id: queryParams.datasource_id,
        });
      }
      return await http[item.method](item.url, params, config);
    };
  });
  return API;
}
function mapUrlObjToStrObj(urlObj) {
  const Url = {};
  keys(urlObj).forEach((key) => {
    const item = urlObj[key];
    Url[key] = item.url;
  });
  return Url;
}

export const API: any = mapUrlObjToFuncObj(Object.assign({}, Swagger, Restful));
export const URL: any = mapUrlObjToStrObj(Object.assign({}, Swagger, Restful));
