import utils from 'utils';

import Api from '../api/project';
import projectType from '../consts/projectActionType';

export function createProject (params: any) {
    return (dispatch: any) => {
        return (async () => {
            let res = await Api.createProject(params);
            if (res && res.code == 1) {
                dispatch(getProjectList());
            }
            return res;
        })()
    }
}
export function getProjectList () {
    return (dispatch: any) => {
        return (async () => {
            let res = await Api.getAllProject();
            if (res && res.code == 1) {
                dispatch({
                    type: projectType.UPDATE_PROJECT_LIST,
                    payload: res.data
                })
            }
            return res;
        })()
    }
}
export function initCurrentProject () {
    return async (dispatch: any, getState: any) => {
        const state = getState();
        const currentProject = state.project.currentProject;
        if (currentProject) {
            let res = await Api.getProjectDetail({
                projectId: currentProject.id
            });
            if (res && res.code == 1) {
                dispatch({
                    type: projectType.SET_CURRENT_PROJECT,
                    payload: res.data
                });
            }
        }
    }
}
export function setProject (project: any) {
    return (dispatch: any) => {
        const projectKey = 'valid_project_id';
        const id = project.id;
        const oldProjectID = utils.getCookie(projectKey);

        // 如果为不同的项目
        if (id && id != oldProjectID) {
            utils.setCookie(projectKey, id);
            dispatch({
                type: projectType.SET_CURRENT_PROJECT,
                payload: project
            });
            dispatch(initCurrentProject());
        }
    }
}
