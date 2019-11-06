import mc from 'mirror-creator';

import utils from 'utils'
import Api from '../../api'

const userAction = mc([
    'GET_USER', // 获取当前用户信息
    'UPDATE_USER', // 更新当前用户信息
    'GET_NOT_PROJECT_USERS', // 非项目用户
    'GET_PROJECT_USERS' // 项目用户列表
], { prefix: 'user/' })

// Action
export function getUser () {
    return (dispatch: any) => {
        Api.getLoginedUser().then((res: any) => {
            if (res.code === 1) {
                return dispatch({
                    type: userAction.UPDATE_USER,
                    data: res.data
                })
            }
        })
    }
}

export function updateUser (fields: any) {
    return {
        type: userAction.UPDATE_USER,
        data: fields
    }
}

export function getNotProjectUsers (params: any) {
    return (dispatch: any) => {
        Api.getNotProjectUsers(params).then((res: any) => {
            return dispatch({
                type: userAction.GET_NOT_PROJECT_USERS,
                data: res.data
            })
        })
    }
}

export function getProjectUsers () {
    const pid = utils.getCookie('stream_project_id')
    return (dispatch: any) => {
        Api.getProjectUsers({
            projectId: pid,
            currentPage: 1,
            pageSize: 100
        }).then((res: any) => {
            return dispatch({
                type: userAction.GET_PROJECT_USERS,
                data: res.data && res.data.data ? res.data.data : []
            })
        })
    }
}

export function user (state: any = {}, action: any) {
    switch (action.type) {
        case userAction.GET_USER:
            return action.data
        case userAction.UPDATE_USER: {
            if (action.data !== null) {
                return { ...state, ...action.data }
            }
            return state;
        }
        default:
            return state
    }
}

export function notProjectUsers (state: any = [], action: any) { // 非项目用户
    switch (action.type) {
        case userAction.GET_NOT_PROJECT_USERS:
            return action.data
        default:
            return state
    }
}

export function projectUsers (state: any = [], action: any) {
    switch (action.type) {
        case userAction.GET_PROJECT_USERS:
            return action.data
        default:
            return state
    }
}
