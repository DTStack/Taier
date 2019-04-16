import { message } from 'antd';

import { setOutput, output } from '../editorActions';
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
export function setNotebookLog (tabId, log) {
    return setOutput(tabId, log, consoleKey, siderBarType.notebook, {
        name: '日志',
        disableClose: true
    });
}
export function appendNotebookLog (tabId, log) {
    return output(tabId, log, consoleKey, siderBarType.notebook);
}
