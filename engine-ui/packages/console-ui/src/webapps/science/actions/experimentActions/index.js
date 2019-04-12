import { changeTab } from '../base/tab';
import { message } from 'antd';
import { siderBarType } from '../../consts';
import { loadTreeData } from '../base/fileTree';
import api from '../../api/experiment';

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
