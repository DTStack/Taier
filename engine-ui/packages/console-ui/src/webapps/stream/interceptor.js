"use strict";
var __assign = (this && this.__assign) || function () {
    __assign = Object.assign || function(t) {
        for (var s, i = 1, n = arguments.length; i < n; i++) {
            s = arguments[i];
            for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p))
                t[p] = s[p];
        }
        return t;
    };
    return __assign.apply(this, arguments);
};
exports.__esModule = true;
var react_router_1 = require("react-router");
var antd_1 = require("antd");
var localDb_1 = require("utils/localDb");
var utils_1 = require("utils");
var user_1 = require("main/api/user");
var funcs_1 = require("funcs");
var maxHeightStyle = {
    maxHeight: '500px',
    overflowY: 'auto'
};
function authBeforeFormate(response) {
    switch (response.status) {
        case 500:
        case 502:
        case 504:
            funcs_1.singletonNotification('服务器异常', '服务器出现了点问题', 'error');
            return Promise.reject(response);
        case 402:
        case 200:
            return response;
        case 302:
            antd_1.message.info('登录超时, 请重新登录！');
            return Promise.reject(response);
        case 413:
            funcs_1.singletonNotification('异常', '请求内容过大！', 'error');
            return Promise.reject(response);
        default:
            if (process.env.NODE_ENV !== 'production') {
                console.error('Request error: ', response.code, response.message);
            }
            return response;
    }
}
exports.authBeforeFormate = authBeforeFormate;
// TODO 状态码这块还是太乱
function authAfterFormated(response) {
    switch (response.code) {
        case 1:
            return response;
        case 0: // 需要登录
            user_1["default"].logout();
            return response;
        case 3: { // 功能无权限
            // 通过判断dom数量，限制通知数量
            var notifyMsgs = document.querySelectorAll('.ant-notification-notice');
            if (notifyMsgs.length === 0) {
                antd_1.notification['error']({
                    message: '权限通知',
                    description: response.message,
                    style: maxHeightStyle
                });
            }
            return response;
        }
        case 16: // 项目不存在，需要重新进入Web首页选择项目，并进入
            react_router_1.hashHistory.push('/');
            return Promise.reject(response);
        default:
            if (response.message) {
                antd_1.notification['error']({
                    message: '异常',
                    description: response.message,
                    style: __assign({}, maxHeightStyle, { wordBreak: 'break-all' })
                });
            }
            return response;
    }
}
exports.authAfterFormated = authAfterFormated;
function isSelectedProject() {
    var pid = utils_1["default"].getCookie('stream_project_id');
    if (!pid || pid === 'undefined') {
        utils_1["default"].deleteCookie('stream_project_id');
        react_router_1.hashHistory.push('/');
    }
}
exports.isSelectedProject = isSelectedProject;
function isLogin() {
    return localDb_1["default"].get('session');
}
exports.isLogin = isLogin;
