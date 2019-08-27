import { hashHistory } from 'react-router'
import { message } from 'antd'
import localDb from 'utils/localDb'
import utils from 'utils'
import Api from './api'

import { singletonNotification } from 'funcs';

const maxHeightStyle: any = {
    maxHeight: '500px',
    overflowY: 'auto'
}

export function authBeforeFormate (response: any) {
    switch (response.status) {
        case 500:
        case 502:
        case 504:
            singletonNotification(
                '服务器异常',
                '服务器出现了点问题',
                'error'
            );
            return Promise.reject(response);
        case 402:
        case 200:
            return response;
        case 302:
            message.info('登录超时, 请重新登录！')
            return Promise.reject(response);
        case 413:
            singletonNotification(
                '异常',
                '请求内容过大！',
                'error'
            );
            return Promise.reject(response);
        default:
            if (process.env.NODE_ENV !== 'production') {
                console.error('Request error: ', response.code, response.message)
            }
            return response
    }
}

// TODO 状态码这块还是太乱
export function authAfterFormated (response: any) {
    switch (response.code) {
        case 548: // 获取分区失败，避免notification窗口提示
        case 1:
            return response;
        case 0: // 需要登录
            Api.logout()
            return response;
        case 3: { // 功能无权限
            // 通过判断dom数量，限制通知数量
            if (response.message) {
                singletonNotification(
                    '权限通知',
                    response.message,
                    'error',
                    maxHeightStyle
                );
            }
            return response;
        }
        case 16: // 项目不存在，需要重新进入Web首页选择项目，并进入
            hashHistory.push('/');
            return Promise.reject(response);
        default:
            if (response.message) {
                singletonNotification(
                    '异常',
                    response.message,
                    'error',
                    { ...maxHeightStyle, wordBreak: 'break-all' }
                );
            }
            return response
    }
}

export function isSelectedProject () {
    const pid = utils.getCookie('project_id');
    const projectIdFromURL = utils.getParameterByName('pid');

    if ((!pid || pid === 'undefined') && !projectIdFromURL) {
        utils.deleteCookie('project_id')
        hashHistory.push('/')
    }
}

export function isLogin () {
    return localDb.get('session')
}
