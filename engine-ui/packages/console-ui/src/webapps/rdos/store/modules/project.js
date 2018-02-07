import mc from 'mirror-creator';

import utils from 'utils'
import Api from '../../api'

const projectAction = mc([
    'GET_PROJECT',
    'GET_PROJECTS',
    'SET_PROJECT',
], { prefix: 'project/' })

const defaultProject = {
    id: 0,
    projectName: '项目选择',
}

// Action
export function getProject(id) {
    if (id) {
        utils.setCookie('project_id', id)
    }
    return (dispatch) => {
        Api.getProjectByID({
            projectId: id,
        }).then((res) => {
            return dispatch({
                type: projectAction.GET_PROJECT,
                data: res.data,
            })
        })
    }
}

export function setProject(data) {
    if (data && data.id) {
        utils.setCookie('project_id', data.id);
    }
    return {
        type: projectAction.SET_PROJECT,
        data,
    }
}

export function getProjects(params) {
    return function fn(dispatch) {
        Api.getProjects(params).then((res) => {
            return dispatch({
                type: projectAction.GET_PROJECTS,
                data: res.data,
            })
        })
    }
}

// Reducer
export function projects(state = [], action) {
    switch (action.type) {
    case projectAction.GET_PROJECTS:
        return action.data || state
    default:
        return state
    }
}

export function project(state = defaultProject, action) {
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
