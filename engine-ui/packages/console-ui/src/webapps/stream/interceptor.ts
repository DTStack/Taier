import { hashHistory } from 'react-router'
import { message, notification } from 'antd'
import localDb from 'utils/localDb'
import utils from 'utils'
import UserApi from 'main/api/user'

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
            message.info('登录超时, 请重新登录！');
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
        case 1:
            return response;
        case 0: // 需要登录
            UserApi.logout()
            return response;
        case 3: { // 功能无权限
            // 通过判断dom数量，限制通知数量
            const notifyMsgs = document.querySelectorAll('.ant-notification-notice');
            if (notifyMsgs.length === 0) {
                notification['error']({
                    message: '权限通知',
                    description: response.message,
                    style: maxHeightStyle
                });
            }
            return response;
        }
        case 16: // 项目不存在，需要重新进入Web首页选择项目，并进入
            hashHistory.push('/');
            return Promise.reject(response);
        default:
            if (response.message) {
                notification['error']({
                    message: '异常',
                    description: response.message,
                    style: { ...maxHeightStyle, wordBreak: 'break-all' }
                });
            }
            return response
    }
}

export function isSelectedProject () {
    const pid = utils.getCookie('stream_project_id');
    const projectIdFromURL = utils.getParameterByName('pid');

    if ((!pid || pid === 'undefined') && !projectIdFromURL) {
        utils.deleteCookie('stream_project_id')
        hashHistory.push('/')
    }
}

export function isLogin () {
    return localDb.get('session')
}
