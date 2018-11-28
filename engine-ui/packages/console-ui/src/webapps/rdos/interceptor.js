import { hashHistory } from 'react-router'
import { message, notification } from 'antd'
import localDb from 'utils/localDb'
import utils from 'utils'
import Api from './api'

import { singletonNotification } from 'funcs';

const maxHeightStyle = {
    maxHeight: "500px",
    overflowY: "auto"
}

/* eslint-disable */
export function authBeforeFormate(response) {
    switch (response.status) {
        case 500:
        case 502:
        case 504:
            notification['error']({
                message: '服务器异常',
                description: '服务器出现了点问题',
            });
        case 402:
        case 200:
            return response;
        case 302:
            message.info('登录超时, 请重新登录！')
        case 413:
            notification['error']({
                message: '异常',
                description: "请求内容过大！",
                style: maxHeightStyle
            });
        default:
            if (process.env.NODE_ENV !== 'production') {
                console.error('Request error: ', response.code, response.message)
            }
            return response
    }
}

// TODO 状态码这块还是太乱
export function authAfterFormated(response) {
    switch (response.code) {
        case 1:
            return response;
        case 0: // 需要登录
            Api.openLogin()
            return Promise.reject(response);
        case 3: { // 功能无权限
            // 通过判断dom数量，限制通知数量
            const notifyMsgs = document.querySelectorAll('.ant-notification-notice');
            if (notifyMsgs.length === 0) {
                singletonNotification(
                    '权限通知', 
                    response.message, 
                    'error', 
                    maxHeightStyle,
                );
            }
            return Promise.reject(response);
        }
        case 16: // 项目不存在，需要重新进入Web首页选择项目，并进入
            hashHistory.push('/');
        default:
            if (response.message) {
                singletonNotification(
                    '异常', 
                    response.message, 
                    'error', 
                    { ...maxHeightStyle, wordBreak: "break-all" },
                );
            }
            return response
    }
}

export function isSelectedProject() {
    const pid = utils.getCookie('project_id')
    if (!pid || pid === 'undefined') {
        utils.deleteCookie('project_id')
        hashHistory.push('/')
    }
}

export function isLogin() {
    return localDb.get('session')
}

/* eslint-enable */
