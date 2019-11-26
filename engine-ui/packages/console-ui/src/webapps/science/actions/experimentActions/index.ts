import { addTab, setCurrentTab, closeTab } from '../base/tab';
import { message } from 'antd';
import { loadTreeData } from '../base/fileTree';
import { siderBarType } from '../../consts';
import api from '../../api/experiment';
import fileApi from '../../api/fileTree';
import { cloneDeep } from 'lodash';
import { changeContent } from './runExperimentActions';
import { removeMetadata } from '../helper';

export function changeText (text: any, tab: any) {
    return changeContent({
        sqlText: text
    }, tab)
}
export function addExperiment (params: any) {
    return (dispatch: any) => {
        return new Promise(async (resolve: any) => {
            let res = await api.addExperiment(params);
            if (res && res.code == 1) {
                message.success('新建成功');
                dispatch(loadTreeData(siderBarType.experiment, params.nodePid))
                resolve(res);
            }
        })
    }
}
export function deleteExperiment (params: any) {
    return (dispatch: any, getState: any) => {
        return new Promise(async (resolve: any) => {
            let res = await api.deleteExperiment({ taskId: params.id });
            if (res && res.code == 1) {
                const experiment = getState().experiment;
                message.success('删除成功');
                dispatch(loadTreeData(siderBarType.experiment, params.parentId))
                dispatch(closeTab(siderBarType.experiment, params.id, experiment.localTabs, experiment.currentTabIndex))
                resolve(res);
            }
        })
    }
}

export function deleteExperimentFolder (params: any) {
    return (dispatch: any) => {
        return new Promise(async (resolve: any) => {
            let res = await fileApi.deleteFolder(params);
            if (res && res.code == 1) {
                message.success('删除成功');
                dispatch(loadTreeData(siderBarType.experiment, params.parentId))
                resolve(res);
            }
        })
    }
}
export function updateTaskData (oldData: any, newData: any, isSilent = true) {
    return (dispatch: any, getState: any) => {
        dispatch(changeContent(newData, oldData, true, isSilent))
    }
}

export function openExperiment (id: any) {
    return (dispatch: any) => {
        return new Promise(async (resolve: any) => {
            let res = await api.openExperiment({ id });
            if (res && res.code == 1) {
                try {
                    res.data.graphData = JSON.parse(res.data.sqlText)
                } catch (error) {
                    res.data.graphData = []
                }
                res.data.graphData.forEach((element: any) => {
                    if (element.edge) {
                        const targetId = element.target.data.id;
                        const sourceId = element.source.data.id;
                        element.target.data = res.data.graphData.find((o: any) => o.vertex && o.data.id == targetId).data;
                        element.source.data = res.data.graphData.find((o: any) => o.vertex && o.data.id == sourceId).data;
                    }
                });
                dispatch(addTab(siderBarType.experiment, res.data));
                dispatch(setCurrentTab(siderBarType.experiment, id));
                resolve(res);
            }
        })
    }
}
export function saveExperiment (tabData: any, isMessage = true) {
    return (dispatch: any, getState: any) => {
        return new Promise(async (resolve: any) => {
            let tab = cloneDeep(tabData);
            tab = removeMetadata(tab);
            tab.graphData = tab.graphData.map((item: any) => {
                if (item.edge) {
                    item.source = { ...item.source, data: { id: item.source.data.id } };
                    item.target = { ...item.target, data: { id: item.target.data.id } };
                }
                return item;
            })
            tab.sqlText = JSON.stringify(tab.graphData);
            let res = await api.addExperiment(tab);
            if (res && res.code == 1) {
                // const tabs = getState().experiment.localTabs;
                dispatch(changeContent(res.data, tabData, false));
                resolve(res);
                isMessage && message.success('保存成功！')
            }
        })
    }
}

export function copyCell (tabData: any, copyCell: any) {
    return (dispatch: any) => {
        return new Promise((resolve: any, reject: any) => {
            api.cloneComponent({ taskId: copyCell.data.id }).then((res: any) => {
                if (res.code == 1) {
                    resolve(res.data);
                } else {
                    reject(res)
                }
            })
        })
    }
}
export function getTaskDetailData (data: any, taskId: any) {
    return (dispatch: any) => {
        return new Promise((resolve: any) => {
            api.getExperimentTask({ id: taskId }).then((res: any) => {
                if (res.code === 1) {
                    const graphData = data.graphData;
                    const object = graphData.find((o: any) => o.vertex && o.data.id === taskId);
                    object.data = { ...object.data, ...res.data };
                    dispatch(changeContent(data, {}, Boolean(data.isDirty), false));
                    resolve(res.data);
                }
            })
        })
    }
}

export function submitExperimentModel (params: any) {
    return (dispatch: any) => {
        return new Promise(async (resolve: any) => {
            let res = await api.submitExperimentModel(params);
            if (res && res.code == 1) {
                message.success('提交模型成功！')
                resolve(res);
            }
            resolve(false);
        })
    }
}

export function submitExperiment (tabData: any) {
    return (dispatch: any) => {
        return new Promise(async (resolve: any) => {
            let res = await api.submitExperiment(tabData);
            if (res && res.code == 1) {
                message.success('实验提交成功，可前往运维中心查看该实验')
                dispatch(changeContent(res.data, tabData, false));
                resolve(res)
            }
            resolve(false);
        })
    }
}

export * from './runExperimentActions';
