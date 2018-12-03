import { message } from 'antd'
import utils from 'utils'
import localDb from 'utils/localDb'
import UserApi from 'main/api/user'

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

export function authAfterFormated(response,extOptions={}) {
    switch (response.code) {
    case 1:
        return response;
    case 0: // 无权限，需要登录
        UserApi.logout()
        return Promise.reject(response);
    case 16: // 项目不存在，需要重新进入Web首页选择项目，并进入
        // window.location.href = "/"
    default:
        if (response.message&&!extOptions.isSilent) {
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
