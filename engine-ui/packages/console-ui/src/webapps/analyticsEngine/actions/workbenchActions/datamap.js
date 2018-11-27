import moment from 'moment';
import { notification, message } from 'antd';

import API from '../../api/datamap';
import workbenchAction from '../../consts/workbenchActionType';

import { closeTab, openTab, loadCatalogue } from './comm';
import { CATALOGUE_TYPE } from '../../consts';

/**
 * 移除数据地图
 * @param {Object} params DataMap参数
 */
export function onRemoveDataMap (params) {
    return async dispatch => {
        const res = await API.deleteDataMap(params);
        if (res.code === 1) {
            message.success('删除DataMap成功！');
            dispatch(closeTab(params.id));
            dispatch(loadCatalogue({
                id: params.tableId,
                databaseId: params.databaseId
            }, CATALOGUE_TYPE.TABLE));
        }
    }
}

/**
 * 获取DataMap详情
 */
export function onCreateDataMap (params) {
    console.log('onCreateDataMap:', params);
    return (dispatch, getStore) => {
        const { workbench } = getStore();
        const { tabs } = workbench.mainBench;

        let createTabIndex = 0;
        for (let i = 0; i < tabs.length; i++) {
            if (tabs[i].actionType === workbenchAction.CREATE_DATA_MAP) {
                if (tabs[i].tabIndex > createTabIndex) {
                    createTabIndex = tabs[i].tabIndex;
                }
            }
        }
        const defaultCreateDataMapData = {
            id: moment().valueOf(),
            tabName: `新建DataMap - ${createTabIndex + 1}`,
            tabIndex: createTabIndex + 1,
            actionType: workbenchAction.CREATE_DATA_MAP,
            tableId: params.id,
            databaseId: params.databaseId,
            tableName: params.tableName
        }

        dispatch(openTab(defaultCreateDataMapData));
    }
}

/**
 * 获取DataMap详情
 */
export function onGetDataMap (params) {
    return async dispatch => {
        const res = await API.getDataMapDetail(params);

        if (res.code === 1) {
            const dataMapData = res.data;
            // 添加Action标记
            dataMapData.actionType = workbenchAction.OPEN_DATA_MAP;
            dataMapData.tabName = `详情 ${dataMapData.name}`;
            dispatch(openTab(dataMapData));
        } else {
            notification.error({
                message: '提示',
                description: res.message
            });
        }
    }
}
