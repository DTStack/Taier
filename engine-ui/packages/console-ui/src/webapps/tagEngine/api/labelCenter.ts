import http from './http';
import LabelCenter from '../consts/labelCenter';

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

export const API: any = mapUrlObjToFuncObj(Object.assign({}, LabelCenter));
export const URL: any = mapUrlObjToStrObj(Object.assign({}, LabelCenter));
