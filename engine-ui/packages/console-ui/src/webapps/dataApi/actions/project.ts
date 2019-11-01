import mc from 'mirror-creator';
import utils from 'utils'
import Api from '../api/project';

const projectAction = mc([
    'GET_PROJECT',
    'GET_PROJECTS',
    'GET_ALL_PROJECTS',
    'SET_PROJECT'
], { prefix: 'project/' })

const defaultProject: any = {
    id: 0,
    projectName: '项目选择'
}

// Action
export function getProject (id: any) {
    return (dispatch: any) => {
        const projectKey = 'api_project_id';
        const oldProjectID = utils.getCookie(projectKey);

        // 如果为不同的项目
        if (id && id != oldProjectID) {
            utils.setCookie(projectKey, id)
        }
        Api.getProjectByID({
            projectId: id
        }).then((res: any) => {
            return dispatch({
                type: projectAction.GET_PROJECT,
                data: res.data
            })
        })
    }
}

export function setProject (data: any) {
    if (data && data.id) {
        utils.setCookie('api_project_id', data.id);
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
