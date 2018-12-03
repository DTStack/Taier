import mc from 'mirror-creator';
import { assign } from 'lodash';

import utils from 'utils'
import localDb from 'utils/localDb'
import Api from '../../api'

const userAction = mc([
    'GET_USER', // 获取当前用户信息
    'UPDATE_USER', // 更新当前用户信息
    'GET_NOT_PROJECT_USERS', // 非项目用户
    'GET_PROJECT_USERS' // 项目用户列表
], { prefix: 'user/' })

// Action
export function getUser () {
    return (dispatch) => {
        Api.getLoginedUser().then(res => {
            if (res.code === 1) {
                return dispatch({
                    type: userAction.UPDATE_USER,
                    data: res.data
                })
            }
        })
    }
}

export function updateUser (fields) {
    return {
        type: userAction.UPDATE_USER,
        data: fields
    }
}

export function getNotProjectUsers (params) {
    return (dispatch) => {
        Api.getNotProjectUsers(params).then((res) => {
            return dispatch({
                type: userAction.GET_NOT_PROJECT_USERS,
                data: res.data
            })
        })
    }
}

export function getProjectUsers () {
    const pid = utils.getCookie('project_id')
    return (dispatch) => {
        Api.getProjectUsers({
            projectId: pid,
            currentPage: 1,
            pageSize: 100
        }).then((res) => {
            return dispatch({
                type: userAction.GET_PROJECT_USERS,
                data: res.data && res.data.data ? res.data.data : []
            })
        })
    }
}

export function user (state = {}, action) {
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

export function notProjectUsers (state = [], action) { // 非项目用户
    switch (action.type) {
        case userAction.GET_NOT_PROJECT_USERS:
            return action.data
        default:
            return state
    }
}

export function projectUsers (state = [], action) {
    switch (action.type) {
        case userAction.GET_PROJECT_USERS:
            return action.data
        default:
            return state
    }
}
