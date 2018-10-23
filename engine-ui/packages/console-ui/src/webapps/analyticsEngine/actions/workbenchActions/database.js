import { notification, message } from 'antd';

import API from '../../api/database';
import workbenchAction from '../../consts/workbenchActionType';

import { updateModal, closeTab } from './comm';

/**
 * 打开数据库
 */
export const onGetDB = function(params) {
    return async dispatch => {
        const res = await API.getDBDetail(params);
        if (res.code === 1) {
            const database = res.data;
            // 添加Action标记
            database.actionType = workbenchAction.OPEN_DATABASE,
            dispatch(openTab(dataMapData));
        } else {
            notification.error({
                message: '提示',
                description: res.message,
            });
        }
    }
}


/**
 * 创建数据库
 */
export const onCreateDB = function() {
    const modalValue = {
        visibleModal: workbenchAction.OPEN_CREATE_DATABASE,
        modalData: null,
    }
    return updateModal(modalValue);
}

/**
 * 移除数据库
 * @param {Object} params Database参数
 */
export function onRemoveDB(params) {
    return async dispatch => {
        const res = await API.deleteDB(params);
        if (res.code === 1) {
            message.success('删除数据库成功！');
            dispatch(closeTab(params.id));
        }
    }
}

/**
 * 移除数据库
 * @param {Object} params Database参数
 */
export function createDB(params) {
    return async dispatch => {
        const res = await API.createDB(params);
        if (res.code === 1) {
            message.success('删除数据库成功！');
            dispatch(closeTab(params.id));
        }
    }
}

/**
 * 加载左侧树形目录数据
 */
export const loadCatalogue = function(params) {
    return async (dispatch) => {

        // const res = await API.getDatabases(params);
        const res = await API.loadCatalogue(params);

        if (res.code === 1) {
            dispatch({
                type: workbenchAction.LOAD_CATALOGUE_DATA,
                payload: res.data,
            })
        }
    }
}