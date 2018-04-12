import { browserHistory } from 'react-router'
import { message, notification } from 'antd'
import utils from 'utils'
import localDb from 'utils/localDb'

import UserApi from './api/user'

/* eslint-disable */
export function authBeforeFormate(response) {
    switch (response.status) {
    case 402:
    case 200:
        return response;
    case 302:
        message.info('登录超时, 请重新登录！')
    case 500:
        message.error('网络异常')
    default:
        if (process.env.NODE_ENV !== 'production') {
            console.error('Request error: ', response.code, response.message)
        }   
        return response
    }
}

export function authAfterFormated(response) {
    switch (response.code) {
    case 1:
        return response;
    case 0: // 无权限，需要登录
 
        UserApi.openLogin()
        return Promise.reject(response);
    case 3: // 功能无权限
        notification['error']({
            message: '权限通知',
            description: response.message,
        });
        return Promise.reject(response);
    case 16: // 需要重新进入Web首页选择项目，并进入
        hashHistory.push('/');
    default:
        if (response.message) {
            message.error(response.message, 3) // 异常消息默认显示5s
        }
        return response
    }
}

export function isSelectedProject() {
    const pid = utils.getCookie('project_id')
    if (!pid || pid === 'undefined') {
        utils.deleteCookie('project_id')
        // browserHistory.push('/')
    }
}

export function isLogin() {
    return localDb.get('session')
}

/* eslint-enable */
