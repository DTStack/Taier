import mc from 'mirror-creator';
import { message } from 'antd';
import utils from 'utils';
import Api from '../api/project';
import { commonActions } from '../actions/common'

const projectAction = mc([
    'GET_PROJECT',
    'GET_PROJECTS',
    'GET_ALL_PROJECTS',
    'SET_PROJECT',
    'GET_PROJECT_LIST',
    'STICK_PROJECT',
    'SET_PANEL_LOADING'
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
            dispatch(commonActions.getMenuList())
            dispatch(getProjects())
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
// 项目下拉列表
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

export function getProjectList (params?: any) {
    return function fn (dispatch: any) {
        dispatch({
            type: projectAction.SET_PANEL_LOADING,
            data: true
        })
        Api.getProjectListInfo(params).then((res: any) => {
            if (res.code === 1) {
                dispatch({
                    type: projectAction.GET_PROJECT_LIST,
                    data: res.data.data
                })
            }
            dispatch({
                type: projectAction.SET_PANEL_LOADING,
                data: false
            })
        })
    }
}

export function createProject (params: any) {
    return (dispatch: any) => {
        return (async () => {
            let res = await Api.createProject(params);
            if (res && res.code == 1) {
                setTimeout(() => {
                    dispatch(getProjectList());
                }, 500)
            }
            return res;
        })()
    }
}
export function setStickProject (params: any, callback: () => void) {
    return (dispatch: any) => {
        return (async () => {
            let res = await Api.setStickProject(params);
            if (res && res.code == 1) {
                callback();
                message.success('操作成功！')
            }
            return res;
        })()
    }
}

// Reducer
// 获取系统下登录用户有权限的项目(项目下拉列表)
export function projects (state: any = [], action: any) {
    switch (action.type) {
        case projectAction.GET_PROJECTS:
            return action.data || state
        default:
            return state
    }
}
// 获取系统所以项目（首页以及项目列表）
export function projectList (state: any = [], action: any) {
    switch (action.type) {
        case projectAction.GET_PROJECT_LIST:
            return action.data || state
        default:
            return state
    }
}

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

export function panelLoading (state: boolean = false, action: any) {
    switch (action.type) {
        case projectAction.SET_PANEL_LOADING: {
            return action.data
        }
        default:
            return state
    }
}
