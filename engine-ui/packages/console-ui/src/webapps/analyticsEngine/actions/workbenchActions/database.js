import moment from 'moment';
import { notification, message } from 'antd';

import API from '../../api';
import workbenchAction from '../../consts/workbenchActionType';

import { updateModal, closeTab } from './comm';

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
 * 加载左侧树形目录数据
 */
export const loadCatalogue = function(params) {
    return async (dispatch) => {

        const defaultParams = {
            nodePid: 0,
            level: 2,
        }
        const reqParams = params ? Object.assign(defaultParams, params) : defaultParams;
        const res = await API.loadCatalogue(reqParams);

        if (res.code === 1) {
            dispatch({
                type: workbenchAction.LOAD_CATALOGUE_DATA,
                payload: res.data,
            })
        }
    }
}