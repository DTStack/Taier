import { changeTab } from '../base/tab';
import { message } from 'antd';
import { siderBarType } from '../../consts';
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
