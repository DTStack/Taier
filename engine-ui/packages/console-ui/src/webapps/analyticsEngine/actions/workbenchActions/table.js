import { notification, message } from 'antd';

import API from '../../api';
import workbenchAction from '../../consts/workbenchActionType';

import { resetModal, openTab, updateModal } from './comm';


export function onCreateTable() {

}

/**
 * 获取DataMap详情
 */
export function onGetTable(params) {

    return async dispatch => {

        const res = await API.createOrUpdateDB(params);
    
        if (res.code === 1) {
            const tableData = res.data;
            // 添加Action标记
            tableData.actionType = workbenchAction.OPEN_TABLE,
            dispatch(openTab(tableData));
        } else {
            notification.error({
                message: '提示',
                description: res.message,
            });
        }
    }
}

/**
 * 生成建表语句
 */
export function onGenerateCreateSQL(tableId) {

    return async dispatch => {

        const res = await API.getCreateSQL({
            tableId,
        });
    
        if (res.code === 1) {
            const modalValue = {
                visibleModal: workbenchAction.GENERATE_CREATE_SQL,
                modalData: res.data,
            }
            return dispatch(updateModal(modalValue))
        } else {
            notification.error({
                message: '提示',
                description: '生成建表语句失败！'
            });
        }
        return dispatch(resetModal());
    }
}
