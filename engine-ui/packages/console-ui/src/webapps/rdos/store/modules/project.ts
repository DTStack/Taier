import mc from 'mirror-creator';

import utils from 'utils'
import Api from '../../api'

import {
    workbenchAction
} from '../../store/modules/offlineTask/actionType';

import {
    workbenchActions
} from '../../store/modules/offlineTask/offlineAction';

const projectAction = mc([
    'GET_PROJECT',
    'GET_PROJECTS',
    'GET_ALL_PROJECTS',
    'GET_TENANT_PROJECTS',
    'SET_PROJECT',
    'GET_SUPPORT_ENGINE'
], { prefix: 'project/' })

const defaultProject: any = {
    id: 0,
    projectName: '选择项目'
}

// Action
export function getProject (id: any) {
    return (dispatch: any) => {
        const projectKey = 'project_id';
        const oldProjectID = utils.getCookie(projectKey);
        const wkActions = workbenchActions(dispatch);
        // 如果为不同的项目
        if (id && id != oldProjectID) {
            utils.setCookie(projectKey, id)
            dispatch({
                type: workbenchAction.CLOSE_ALL_TABS
            });
        }
        return Api.getProjectByID({
            projectId: id
        }).then((res: any) => {
            if (res && res.code == 1) {
                dispatch({
                    type: projectAction.GET_PROJECT,
                    data: res.data || {}
                })
                wkActions.initWorkbenchCacheData(id);
                return res.data
            }
        })
    }
}

export function setProject (data: any) {
    if (data && data.id) {
        utils.setCookie('project_id', data.id);
    }
    return {
        type: projectAction.SET_PROJECT,
        data
    }
}

export function getProjects (params?: any) {
    return function fn (dispatch: any) {
        Api.getProjects(params).then((res: any) => {
            return dispatch({
                type: projectAction.GET_PROJECTS,
                data: res.data
            })
        })
    }
}

export function getAllProjects (params?: any) {
    return function fn (dispatch: any) {
        Api.getAllProjects(params).then((res: any) => {
            return dispatch({
                type: projectAction.GET_ALL_PROJECTS,
                data: res.data
            })
        })
    }
}
// 获取租户下所有项目
export function getTenantProjects (params?: any) {
    return function fn (dispatch: any) {
        Api.getTenantProjects(params).then((res: any) => {
            return dispatch({
                type: projectAction.GET_TENANT_PROJECTS,
                data: res.data
            })
        })
    }
}
// 项目支持引擎
export function getProjectSupportEngine (params?: any) {
    return function fn (dispatch: any) {
        Api.getProjectSupportEngines(params).then((res: any) => {
            return dispatch({
                type: projectAction.GET_SUPPORT_ENGINE,
                data: res.data || []
            })
        })
    }
}

// Reducer
// 获取系统下登录用户有权限的项目
export function projects (state: any = [], action: any) {
    switch (action.type) {
        case projectAction.GET_PROJECTS:
            return action.data || state
        default:
            return state
    }
}

// 获取系统所以项目
export function allProjects (state: any = [], action: any) {
    switch (action.type) {
        case projectAction.GET_ALL_PROJECTS:
            return action.data || state
        default:
            return state
    }
}

export function project (state = defaultProject, action: any) {
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
// 获取系统所以项目
export function allTenantsProjects (state: any = [], action: any) {
    switch (action.type) {
        case projectAction.GET_TENANT_PROJECTS:
            return action.data || state
        default:
            return state
    }
}
// 获取项目支持的引擎类型
export function projectSuppoetEngines (state: any = [], action: any) {
    switch (action.type) {
        case projectAction.GET_SUPPORT_ENGINE:
            return action.data || state
        default:
            return state
    }
}
