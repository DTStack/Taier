import { hashHistory } from 'react-router'
import { message, notification } from 'antd'
import utils from 'utils'
import localDb from 'utils/localDb'

import UserApi from 'main/api/user'

export function authBeforeFormate (response: any) {
    switch (response.status) {
        case 402:
        case 200:
            return response;
        case 302:
            message.info('登录超时, 请重新登录！');
            return Promise.reject(response);
        default:
            if (process.env.NODE_ENV !== 'production') {
                console.error('Request error: ', response.code, response.message)
            }
            return response
    }
}

export function authAfterFormated (response: any) {
    switch (response.code) {
        case 1:
            return response;
        case 0: // 无权限，需要登录
            UserApi.logout()
            return response;
        case 3: // 功能无权限
            notification['error']({
                message: '权限通知',
                description: response.message
            });
            return response;
        case 16: // 需要重新进入Web首页选择项目，并进入
            hashHistory.push('/');
            return Promise.reject(response);
        default:
            if (response.message) {
                notification['error']({
                    message: '错误',
                    description: response.message
                });
            // message.error(response.message, 3) // 异常消息默认显示5s
            }
            return response
    }
}

export function isSelectedProject () {
    const pid = utils.getCookie('dq_project_id');
    const projectIdFromURL = utils.getParameterByName('pid');

    if ((!pid || pid === 'undefined') && !projectIdFromURL) {
        utils.deleteCookie('dq_project_id')
        hashHistory.push('/')
    }
}

export function isLogin () {
    return localDb.get('session')
}
