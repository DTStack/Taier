import { Fetch } from 'ko-request';
import Restful from './restful';
import Swagger from './swagger';
const http = new Fetch();
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

export const API = mapUrlObjToFuncObj(Object.assign({}, Swagger, Restful));
export const URL = mapUrlObjToStrObj(Object.assign({}, Swagger, Restful));
