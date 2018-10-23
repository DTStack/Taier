import { notification, message } from 'antd';

import API from '../../api';
import workbenchAction from '../../consts/workbenchActionType';

import { closeTab, openTab } from './comm';

/**
 * 移除数据地图
 * @param {Object} params DataMap参数
 */
export function onRemoveDataMap(params) {
    return async dispatch => {
        const res = await API.createOrUpdateDB(params);
        if (res.code === 1) {
            message.success('删除DataMap成功！');
            dispatch(closeTab(params.id));
        }
    }
}

export function onCreateDataMap() {
}

/**
 * 获取DataMap详情
 */
export function onGetDataMap(params) {

    return async dispatch => {

        const res = await API.createOrUpdateDB(params);
    
        if (res.code === 1) {
            const dataMapData = res.data;
            // 添加Action标记
            dataMapData.actionType = workbenchAction.OPEN_DATA_MAP,
            dispatch(openTab(dataMapData));
        } else {
            notification.error({
                message: '提示',
                description: res.message,
            });
        }
    }
}
