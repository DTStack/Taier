import Api from '../../api';
import commonActionType from '../../consts/commonActionType';
import { experimentFilesType, componentFilesType, notebookFilesType } from '../../consts/actionType/filesType';
import projectType from '../../consts/actionType/projectType';
import { deleteAllTab } from './tab';
import { siderBarType } from '../../consts';
import utils from 'utils';

export function getSysParams () {
    return (dispatch, getState) => {
        return new Promise(async (resolve) => {
            const state = getState();
            if (state.common.sysParams) {
                resolve(state.common.sysParams);
            }
            let res = await Api.comm.getSysParams();
            if (res && res.code == 1) {
                dispatch({
                    type: commonActionType.SET_SYS_PARAMS,
                    payload: res.data
                });
            }
            resolve(res.data);
        })
    }
}
export function changeSiderBar (key) {
    return {
        type: commonActionType.CHANGE_SIDERBAR_KEY,
        payload: key
    }
}
export function createProject (params) {
    return dispatch => {
        return (async () => {
            let res = await Api.comm.createProject(params);
            if (res && res.code == 1) {
                dispatch(getProjectList());
            }
            return res;
        })()
    }
}
export function getProjectList () {
    return dispatch => {
        return (async () => {
            let res = await Api.comm.getAllProject();
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
    return async (dispatch, getState) => {
        const state = getState();
        const currentProject = state.project.currentProject;
        if (currentProject) {
            let res = await Api.comm.getProjectDetail({
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
export function setProject (project) {
    return dispatch => {
        const projectKey = 'science_project_id';
        const id = project.id;
        const oldProjectID = utils.getCookie(projectKey);

        // 如果为不同的项目
        if (id && id != oldProjectID) {
            utils.setCookie(projectKey, id)
            dispatch(deleteAllTab(siderBarType.notebook));
            dispatch(deleteAllTab(siderBarType.experiment));
            dispatch({
                type: experimentFilesType.CLEAR_TREE
            })
            dispatch({
                type: componentFilesType.CLEAR_TREE
            })
            dispatch({
                type: notebookFilesType.CLEAR_TREE
            })
            dispatch({
                type: projectType.SET_CURRENT_PROJECT,
                payload: project
            });
            dispatch(initCurrentProject());
        }
    }
}
