import { notification, message } from 'antd';

import API from '../../api/database';
import workbenchAction from '../../consts/workbenchActionType';

import { updateModal, closeTab, openTab, loadCatalogue } from './comm';

/**
 * 打开数据库
 */
export const onGetDB = function(params) {
    return async dispatch => {
        const res = await API.getDBDetail(params);
        if (res.code === 1) {
            const database = res.data;
            // 添加Action标记
            database.actionType = workbenchAction.OPEN_DATABASE;
            database.tabName = `详情 ${database.name}`;
            dispatch(openTab(database));
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
            dispatch(closeTab(params.databaseId));
            dispatch(loadCatalogue());
        }
    }
}
