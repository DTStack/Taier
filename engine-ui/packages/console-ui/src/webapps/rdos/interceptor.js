import { hashHistory } from 'react-router'
import { message } from 'antd'
import localDb from 'utils/localDb'
import utils from 'utils'
import Api from './api'

/* eslint-disable */
export function authBeforeFormate(response) {
    switch (response.status) {
    case 200:
        return response;
    case 302:
        message.info('登录超时, 请重新登录！')
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
        Api.logout()
        return Promise.reject(response);
    case 16: // 项目不存在，需要重新进入Web首页选择项目，并进入
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
        hashHistory.push('/')
    }
}

export function isLogin() {
    return localDb.get('session')
}

/* eslint-enable */
