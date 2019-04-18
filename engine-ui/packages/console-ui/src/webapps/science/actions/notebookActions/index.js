import { message } from 'antd';

import { setOutput, output, outputRes, removeRes, resetConsole } from '../editorActions';
import editorAction from '../../consts/editorActionType';
import { changeTab, addTab, setCurrentTab } from '../base/tab';
import { siderBarType, consoleKey } from '../../consts';
import { loadTreeData } from '../base/fileTree';
import api from '../../api/notebook';

export function changeContent (newContent, tab, isDirty = true) {
    return changeTab(siderBarType.notebook, {
        ...tab,
        ...newContent,
        isDirty
    })
}

export function changeText (text, tab) {
    return changeContent({
        sqlText: text
    }, tab)
}

export function removeNotebookRes (tabId, key) {
    return removeRes(tabId, key, siderBarType.notebook);
}

export function changeConsoleKey (tabId, activeKey) {
    return {
        type: editorAction.CHANGE_TABS_KEY,
        payload: {
            tabId,
            activeKey,
            siderType: siderBarType.notebook
        }
    }
}

export function resetNotebookConsole (tabId) {
    return resetConsole(tabId, siderBarType.notebook);
}

export function openNotebook (id) {
    return dispatch => {
        return new Promise(async (resolve) => {
            let res = await api.openNotebook({ id });
            if (res && res.code == 1) {
                dispatch(addTab(siderBarType.notebook, res.data));
                dispatch(setCurrentTab(siderBarType.notebook, id));
                resolve(res);
            }
        })
    }
}

export function addNotebook (params) {
    return dispatch => {
        return new Promise(async (resolve) => {
            let res = await api.addNotebook(params);
            if (res && res.code == 1) {
                message.success('新建成功');
                dispatch(loadTreeData(siderBarType.notebook, params.nodePid))
                resolve(res);
            }
        })
    }
}

export function showNotebookLog (tabId) {
    return changeConsoleKey(tabId, consoleKey);
}

export function setNotebookLog (tabId, log) {
    return setOutput(tabId, log, consoleKey, siderBarType.notebook, {
        name: '日志',
        disableClose: true
    });
}

export function appendNotebookLog (tabId, log) {
    return output(tabId, log, consoleKey, siderBarType.notebook);
}

export function setNotebookResult (tabId, jobId, data) {
    return outputRes(tabId, data, jobId, siderBarType.notebook, {
        name: '执行结果'
    })
}

export function saveNotebook (tabData) {
    return dispatch => {
        return new Promise(async (resolve) => {
            let res = await api.addNotebook(tabData);
            if (res && res.code == 1) {
                dispatch(changeContent(res.data, tabData, false));
                message.success('保存成功！')
            }
            resolve(res);
        })
    }
}

export function submitNotebook (tabData) {
    return dispatch => {
        return new Promise(async (resolve) => {
            let res = await api.submitNotebook(tabData);
            if (res && res.code == 1) {
                message.success('提交作业成功！')
                resolve(res)
            }
            resolve(false);
        })
    }
}

export function submitNotebookModel (tabData) {
    return dispatch => {
        return new Promise(async (resolve) => {
            let res = await api.submitNotebookModel(tabData);
            if (res && res.code == 1) {
                message.success('提交模型成功！')
                resolve(res);
            }
            resolve(false);
        })
    }
}
