import { addTab, setCurrentTab } from '../base/tab';
import { message } from 'antd';
import { loadTreeData } from '../base/fileTree';
import { siderBarType } from '../../consts';
import api from '../../api/experiment';
import fileApi from '../../api/fileTree';
import { cloneDeep } from 'lodash';
import { changeContent } from './runExperimentActions';
const mockData = require('./mocks/data.json');
export function changeText (text, tab) {
    return changeContent({
        sqlText: text
    }, tab)
}
export function addExperiment (params) {
    return dispatch => {
        return new Promise(async (resolve) => {
            let res = await api.addExperiment(params);
            if (res && res.code == 1) {
                message.success('新建成功');
                dispatch(loadTreeData(siderBarType.experiment, params.nodePid))
                resolve(res);
            }
        })
    }
}
export function deleteExperiment (params) {
    return dispatch => {
        return new Promise(async (resolve) => {
            let res = await api.deleteExperiment(params);
            if (res && res.code == 1) {
                message.success('删除成功');
                dispatch(loadTreeData(siderBarType.experiment, params.parentId))
                resolve(res);
            }
        })
    }
}

export function deleteExperimentFolder (params) {
    return dispatch => {
        return new Promise(async (resolve) => {
            let res = await fileApi.deleteFolder(params);
            if (res && res.code == 1) {
                message.success('删除成功');
                dispatch(loadTreeData(siderBarType.experiment, params.parentId))
                resolve(res);
            }
        })
    }
}
export function getTaskData (data, currentTab) {
    return (dispatch, getState) => {
        // TODO 请求接口
        const res = mockData;
        if (res && res.code == 1) {
            const graphData = res.data ? JSON.parse(res.data.sqlText) : [];
            data.graphData = graphData;
            dispatch(changeContent(data, {}, false))
        }
    }
}
export function updateTaskData (oldData, newData) {
    return (dispatch, getState) => {
        dispatch(changeContent(oldData, newData, true, true))
    }
}

export function openExperiment (id) {
    return dispatch => {
        return new Promise(async (resolve) => {
            let res = await api.openExperiment({ id });
            if (res && res.code == 1) {
                dispatch(addTab(siderBarType.experiment, res.data));
                dispatch(setCurrentTab(siderBarType.experiment, id));
                resolve(res);
            }
        })
    }
}
export function saveExperiment (tabData) {
    return (dispatch, getState) => {
        return new Promise(async (resolve) => {
            let res = await api.addExperiment(tabData);
            if (res && res.code == 1) {
                const tabs = getState().notebook.localTabs;
                dispatch(changeContent(res.data, tabs.find((tab) => {
                    return tab.id == tabData.id
                }), false));
                message.success('保存成功！')
                resolve(res);
            }
        })
    }
}

export function copyCell (tabData, copyCell) {
    return (dispatch) => {
        const data = cloneDeep(tabData);
        // TODO
        const res = {
            code: 1,
            data: {
                ...copyCell,
                id: 1111
            }
        }
        if (res.code == 1) {
            data.graphData.push(res.data);
            dispatch(changeContent(data, {}, true));
        }
    }
}
export function getTaskDetailData (data, taskId) {
    return (dispatch) => {
        api.getExperimentTask(taskId).then((res) => {
            if (res.code === 1) {
                data.detailData = res.data;
                dispatch(changeContent(data, {}, false));
            }
        })
    }
}

export function submitExperimentModel (params) {
    return dispatch => {
        return new Promise(async (resolve) => {
            let res = await api.submitExperimentModel(params);
            if (res && res.code == 1) {
                message.success('提交模型成功！')
                resolve(res);
            }
            resolve(false);
        })
    }
}

export function submitExperiment (tabData) {
    return dispatch => {
        return new Promise(async (resolve) => {
            let res = await api.submitExperiment(tabData);
            if (res && res.code == 1) {
                message.success('提交实验成功！')
                dispatch(changeContent(res.data, tabData, false));
                resolve(res)
            }
            resolve(false);
        })
    }
}

export * from './runExperimentActions';
