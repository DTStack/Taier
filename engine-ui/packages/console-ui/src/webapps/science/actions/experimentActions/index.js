import { changeTab, addTab, setCurrentTab } from '../base/tab';
import { message } from 'antd';
import { siderBarType } from '../../consts';
import { loadTreeData } from '../base/fileTree';
import api from '../../api/experiment';
import fileApi from '../../api/fileTree';

export function changeContent (newContent, tab, isDirty = true) {
    return changeTab(siderBarType.experiment, {
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
    return dispatch => {
        return new Promise(async (resolve) => {
            let res = await api.addExperiment(tabData);
            if (res && res.code == 1) {
                dispatch(changeContent(res.data, tabData, false));
                message.success('保存成功！')
                resolve(res);
            }
        })
    }
}
