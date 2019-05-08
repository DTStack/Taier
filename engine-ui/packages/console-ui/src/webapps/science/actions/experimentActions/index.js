import { addTab, setCurrentTab } from '../base/tab';
import { message } from 'antd';
import { loadTreeData } from '../base/fileTree';
import { siderBarType } from '../../consts';
import api from '../../api/experiment';
import fileApi from '../../api/fileTree';
// import { cloneDeep } from 'lodash';
import { changeContent } from './runExperimentActions';
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
            let res = await api.deleteExperiment({ taskId: params.id });
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
export function updateTaskData (oldData, newData, isSilent = true) {
    return (dispatch, getState) => {
        dispatch(changeContent(newData, oldData, true, isSilent))
    }
}

export function openExperiment (id) {
    return dispatch => {
        return new Promise(async (resolve) => {
            let res = await api.openExperiment({ id });
            if (res && res.code == 1) {
                try {
                    res.data.graphData = JSON.parse(res.data.sqlText)
                } catch (error) {
                    res.data.graphData = []
                }
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
            tabData.sqlText = JSON.stringify(tabData.graphData);
            let res = await api.addExperiment(tabData);
            if (res && res.code == 1) {
                const tabs = getState().experiment.localTabs;
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
        return new Promise((resolve) => {
            api.cloneComponent({ taskId: copyCell.data.id }).then((res) => {
                if (res.code == 1) {
                    resolve(res.data);
                }
            })
        })
    }
}
export function getTaskDetailData (data, taskId) {
    return (dispatch) => {
        return new Promise((resolve) => {
            api.getExperimentTask({ id: taskId }).then((res) => {
                if (res.code === 1) {
                    const graphData = data.graphData;
                    const object = graphData.find(o => o.vertex && o.data.id === taskId);
                    object.data = res.data;
                    dispatch(changeContent(data, {}, false, true));
                    resolve(res.data);
                }
            })
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
