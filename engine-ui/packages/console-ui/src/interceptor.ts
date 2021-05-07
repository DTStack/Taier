
import { message, notification } from 'antd'
import { hashHistory } from 'react-router'
import utils from 'dt-common/src/utils'
import localDb from 'dt-common/src/utils/localDb'
import UserApi from 'dt-common/src/api/user'
import { versionMonitor } from 'dt-common/src/funcs'

const maxHeightStyle: any = {
    maxHeight: '500px',
    overflowY: 'auto'
}

export function authBeforeFormate (response: any) {
    switch (response.status) {
        case 402:
        case 200:
        case 412:
            versionMonitor(response, ['/api/console', '/node'], 'DT_CONSOLE');
            return response;
        case 302:
            message.info('登录超时, 请重新登录！')
            return Promise.reject(response);
        case 500:
            message.error('服务器出现了点问题')
            return Promise.reject(response);
        default:
            if (process.env.NODE_ENV !== 'production') {
                console.error('Request error: ', response.code, response.message)
            }
            versionMonitor(response, ['/api/console', '/node'], 'DT_CONSOLE');
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
            return Promise.reject(response);
        case 16: // 需要重新进入Web首页选择项目，并进入
            hashHistory.push('/');
            return Promise.reject(response);
        default:
            if (response.message) {
                setTimeout(() => {
                    notification['error']({
                        message: '异常',
                        description: response.message,
                        style: { ...maxHeightStyle, wordBreak: 'break-all' }
                    });
                }, 0)
            }
            return response
    }
}

export function isSelectedProject () {
    const pid = utils.getCookie('project_id')
    if (!pid || pid === 'undefined') {
        utils.deleteCookie('project_id')
        // browserHistory.push('/')
    }
}

export function isLogin () {
    return localDb.get('session')
}
