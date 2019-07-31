"use strict";
exports.__esModule = true;
var antd_1 = require("antd");
var react_router_1 = require("react-router");
var utils_1 = require("utils");
var localDb_1 = require("utils/localDb");
var user_1 = require("main/api/user");
function authBeforeFormate(response) {
    switch (response.status) {
        case 402:
        case 200:
            return response;
        case 302:
            antd_1.message.info('登录超时, 请重新登录！');
            return response;
        default:
            if (process.env.NODE_ENV !== 'production') {
                console.error('Request error: ', response.code, response.message);
            }
            return response;
    }
}
exports.authBeforeFormate = authBeforeFormate;
function authAfterFormated(response) {
    switch (response.code) {
        case 1:
            return response;
        case 0: // 无权限，需要登录
            user_1["default"].logout();
            return response;
        case 3: // 功能无权限
            antd_1.notification['error']({
                message: '权限通知',
                description: response.message
            });
            return Promise.reject(response);
        case 16: // 需要重新进入Web首页选择项目，并进入
            react_router_1.hashHistory.push('/');
            return Promise.reject(response);
        default:
            if (response.message) {
                antd_1.notification['error']({
                    message: '错误',
                    description: response.message
                });
            }
            return response;
    }
}
exports.authAfterFormated = authAfterFormated;
function isSelectedProject() {
    var pid = utils_1["default"].getCookie('project_id');
    if (!pid || pid === 'undefined') {
        utils_1["default"].deleteCookie('project_id');
        // browserHistory.push('/')
    }
}
exports.isSelectedProject = isSelectedProject;
function isLogin() {
    return localDb_1["default"].get('session');
}
exports.isLogin = isLogin;
/* eslint-enable */
