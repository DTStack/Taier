import { message } from 'antd';

import { siderBarType } from '../../consts';
import { loadTreeData } from '../base/fileTree';
import api from '../../api/resource';
import actionType from '../../consts/actionType/resourceExt';

import fileApi from '../../api/fileTree';
export function addResource (resParams, resourceData, type) {
    return dispatch => {
        return new Promise(async (resolve) => {
            let res = await api.addResource(resParams);
            if (res && res.code == 1) {
                const msg = !type ? '新建成功' : '替换成功';
                message.success(msg);
                dispatch(loadTreeData(siderBarType.resource, resParams.nodePid || resourceData.parentId))
                resolve(res);
            }
        })
    }
}
export function deleteResource (params) {
    return (dispatch, getState) => {
        return new Promise(async (resolve) => {
            let res = await api.deleteResource({ resourceId: params.id });
            if (res && res.code == 1) {
                message.success('删除成功');
                dispatch(loadTreeData(siderBarType.resource, params.parentId))
                resolve(res);
            }
        })
    }
}

export function deleteResourceFolder (params) {
    return dispatch => {
        return new Promise(async (resolve) => {
            let res = await fileApi.deleteFolder(params);
            if (res && res.code == 1) {
                message.success('删除成功');
                dispatch(loadTreeData(siderBarType.resource, params.parentId))
                resolve(res);
            }
        })
    }
}
export function renameResource (params) {
    return dispatch => {
        return new Promise(async (resolve) => {
            let res = await api.renameResource({
                name: params.resReName,
                resourceId: params.id
            });
            if (res && res.code == 1) {
                message.success('重命名成功');
                dispatch(loadTreeData(siderBarType.resource, params.parentId))
                resolve(res);
            }
        })
    }
}
// 是否显示资源管理高度
export function getFixResource (params) {
    return dispatch => {
        dispatch({
            type: actionType.SHOW_FIX_RESOURCE,
            payload: params
        })
    }
}
