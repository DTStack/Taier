import mc from 'mirror-creator';

import utils from 'utils'
import Api from '../../api'

import {
    workbenchAction
} from '../../store/modules/offlineTask/actionType';

const projectAction = mc([
    'GET_PROJECT',
    'GET_PROJECTS',
    'GET_ALL_PROJECTS',
    'SET_PROJECT'
], { prefix: 'project/' })

const defaultProject = {
    id: 0,
    projectName: '项目选择'
}

// Action
export function getProject (id) {
    return (dispatch) => {
        const projectKey = 'project_id';
        const oldProjectID = utils.getCookie(projectKey);

        // 如果为不同的项目
        if (id && id != oldProjectID) {
            utils.setCookie(projectKey, id)
            dispatch({
                type: workbenchAction.CLOSE_ALL_TABS
            });
        }
        return Api.getProjectByID({
            projectId: id
        }).then((res) => {
            if (res && res.code == 1) {
                dispatch({
                    type: projectAction.GET_PROJECT,
                    data: res.data || {}
                })
                return res.data
            }
        })
    }
}

export function setProject (data) {
    if (data && data.id) {
        utils.setCookie('project_id', data.id);
    }
    return {
        type: projectAction.SET_PROJECT,
        data
    }
}

export function getProjects (params) {
    return function fn (dispatch) {
        Api.getProjects(params).then((res) => {
            return dispatch({
                type: projectAction.GET_PROJECTS,
                data: res.data
            })
        })
    }
}

export function getAllProjects (params) {
    return function fn (dispatch) {
        Api.getAllProjects(params).then((res) => {
            return dispatch({
                type: projectAction.GET_ALL_PROJECTS,
                data: res.data
            })
        })
    }
}

// Reducer
// 获取系统下登录用户有权限的项目
export function projects (state = [], action) {
    switch (action.type) {
        case projectAction.GET_PROJECTS:
            return action.data || state
        default:
            return state
    }
}

// 获取系统所以项目
export function allProjects (state = [], action) {
    switch (action.type) {
        case projectAction.GET_ALL_PROJECTS:
            return action.data || state
        default:
            return state
    }
}

export function project (state = defaultProject, action) {
    switch (action.type) {
        case projectAction.GET_PROJECT:
            return action.data
        case projectAction.SET_PROJECT: {
            return action.data || state
        }
        default:
            return state
    }
}
