import commonActionType from '../consts/commonActionType';
import { message } from 'antd';
import API from '../api/common';

export const commonActions = {
    getUserList (params) {
        return dispatch => {
            API.getUserList(params).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: commonActionType.GET_USER_LIST,
                        payload: res.data
                    });
                }
            });
        }
    },
    getAllMenuList (params) {
        return dispatch => {
            API.getAllMenuList(params).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: commonActionType.GET_ALL_MENU_LIST,
                        payload: res.data
                    });
                }
            });
        }
    },
    getPeriodType (params) {
        return dispatch => {
            API.getPeriodType(params).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: commonActionType.GET_PERIOD_TYPE,
                        payload: res.data
                    });
                }
            });
        }
    },
    getNotifyType (params) {
        return dispatch => {
            API.getNotifyType(params).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: commonActionType.GET_NOTIFY_TYPE,
                        payload: res.data
                    });
                }
            });
        }
    }

}
