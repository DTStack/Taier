import { Fetch } from 'ko-request';
import Restful from './restful';
import Swagger from './swagger';
const http = new Fetch({
  initConfig:{
    // headers: {
    //   'gg': 'xxx'
    // },
  }
});
const { keys } = Object;
function mapUrlObjToFuncObj(urlObj) {
  const API = {};
  keys(urlObj).forEach(key => {
    const item = urlObj[key];
    API[key] = async function (params) {
      return await http[item.method](item.url, params);
    };
  });
  return API;
}
function mapUrlObjToStrObj(urlObj) {
  const Url = {};
  keys(urlObj).forEach(key => {
    const item = urlObj[key];
    Url[key] = item.url;
  });
  return Url;
}

export const API: any = mapUrlObjToFuncObj(Object.assign({}, Swagger, Restful));
export const URL: any = mapUrlObjToStrObj(Object.assign({}, Swagger, Restful));
