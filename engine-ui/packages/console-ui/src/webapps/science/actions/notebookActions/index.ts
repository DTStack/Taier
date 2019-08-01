import { message } from 'antd';

import { setOutput, output, outputRes, removeRes, resetConsole } from '../editorActions';
import editorAction from '../../consts/editorActionType';
import { changeTab, addTab, setCurrentTab, closeTab } from '../base/tab';
import { siderBarType, consoleKey, modelComponentType } from '../../consts';
import { loadTreeData } from '../base/fileTree';
import api from '../../api/notebook';
import fileApi from '../../api/fileTree';
import { removeMetadata } from '../helper';

export function changeContent (newContent: any, tab: any, isDirty = true) {
    return changeTab(siderBarType.notebook, {
        ...tab,
        ...newContent,
        isDirty
    })
}

export function changeText (text: any, tab: any) {
    return changeContent({
        sqlText: text
    }, tab)
}

export function removeNotebookRes (tabId: any, key: any) {
    return removeRes(tabId, key, siderBarType.notebook);
}

export function changeConsoleKey (tabId: any, activeKey: any) {
    return {
        type: editorAction.CHANGE_TABS_KEY,
        payload: {
            tabId,
            activeKey,
            siderType: siderBarType.notebook
        }
    }
}

export function resetNotebookConsole (tabId: any) {
    return resetConsole(tabId, siderBarType.notebook);
}

export function openNotebook (id: any) {
    return (dispatch: any) => {
        return new Promise(async (resolve: any) => {
            let res = await api.getTaskById({ id });
            if (res && res.code == 1) {
                dispatch(addTab(siderBarType.notebook, res.data));
                dispatch(setCurrentTab(siderBarType.notebook, id));
                resolve(res);
            }
        })
    }
}

export function addNotebook (params: any) {
    return (dispatch: any) => {
        return new Promise(async (resolve: any) => {
            let res = await api.addNotebook(params);
            if (res && res.code == 1) {
                message.success('新建成功');
                dispatch(loadTreeData(siderBarType.notebook, params.nodePid))
                resolve(res);
            }
        })
    }
}
export function deleteNotebook (params: any) {
    return (dispatch: any, getState: any) => {
        return new Promise(async (resolve: any) => {
            let res = await api.deleteNotebook({ taskId: params.id });
            if (res && res.code == 1) {
                const notebook = getState().notebook;
                message.success('删除成功');
                dispatch(loadTreeData(siderBarType.notebook, params.parentId))
                dispatch(closeTab(siderBarType.notebook, params.id, notebook.localTabs, notebook.currentTabIndex))
                resolve(res);
            }
        })
    }
}

export function deleteNotebookFolder (params: any) {
    return (dispatch: any) => {
        return new Promise(async (resolve: any) => {
            let res = await fileApi.deleteFolder(params);
            if (res && res.code == 1) {
                message.success('删除成功');
                dispatch(loadTreeData(siderBarType.notebook, params.parentId))
                resolve(res);
            }
        })
    }
}

export function showNotebookLog (tabId: any) {
    return changeConsoleKey(tabId, consoleKey);
}

export function setNotebookLog (tabId: any, log: any) {
    return setOutput(tabId, log, consoleKey, siderBarType.notebook, {
        name: '日志',
        disableClose: true
    });
}

export function appendNotebookLog (tabId: any, log: any) {
    return output(tabId, log, consoleKey, siderBarType.notebook);
}

export function setNotebookResult (tabId: any, jobId: any, data: any) {
    return outputRes(tabId, data, jobId, siderBarType.notebook, {
        name: '执行结果'
    })
}

export function saveNotebook (tabData: any) {
    return (dispatch: any, getState: any) => {
        return new Promise(async (resolve: any) => {
            tabData = removeMetadata(tabData);
            let res = await api.addNotebook({ ...tabData, componentType: modelComponentType.NOTEBOOK.value });
            if (res && res.code == 1) {
                const tabs = getState().notebook.localTabs;
                dispatch(changeContent(res.data, tabs.find((tab: any) => {
                    return tab.id == tabData.id
                }), false));
                message.success('保存成功！')
                resolve(res);
            }
        })
    }
}

export function submitNotebook (tabData: any) {
    return (dispatch: any) => {
        return new Promise(async (resolve: any) => {
            let res = await api.submitNotebook(tabData);
            if (res && res.code == 1) {
                message.success('任务提交成功，可前往运维中心查看该任务')
                dispatch(changeContent(res.data, tabData, false));
                resolve(res)
            }
            resolve(false);
        })
    }
}

export function submitNotebookModel (params: any) {
    return (dispatch: any) => {
        return new Promise(async (resolve: any) => {
            let res = await api.submitNotebookModel(params);
            if (res && res.code == 1) {
                message.success('提交模型成功！')
                resolve(res);
            }
            resolve(false);
        })
    }
}
