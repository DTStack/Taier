import http from './http';
import LabelCenter from './labelCenter';
import Project from './project';
import User from './User';

let combineApi = Object.assign({}, User, Project, LabelCenter);

const { keys } = Object;
function mapUrlObjToFuncObj (urlObj) {
    const API = {};
    keys(urlObj).forEach((key) => {
        const item = urlObj[key]
        API[key] = async function (params) {
            // eslint-disable-next-line no-return-await
            return await http[item.method.toLowerCase()](item.url, params)
        }
    });
    return API;
}
function mapUrlObjToStrObj (urlObj) {
    const Url = {};
    keys(urlObj).forEach((key) => {
        const item = urlObj[key]
        Url[key] = item.url
    });
    return Url;
}

export const API: any = mapUrlObjToFuncObj(combineApi);
export const URL: any = mapUrlObjToStrObj(combineApi);
