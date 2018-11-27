import { dataCheckActionType } from '../../consts/dataCheckActionType';
import { message } from 'antd';
import API from '../../api/dataCheck';
import DSAPI from '../../api/dataSource';

export const dataCheckActions = {
    getLists (params) {
        return dispatch => {
            dispatch({
                type: dataCheckActionType.CHANGE_LOADING
            });
            API.getLists(params).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: dataCheckActionType.GET_LIST,
                        payload: res.data
                    });
                }
                dispatch({
                    type: dataCheckActionType.CHANGE_LOADING
                });
            });
        }
    },
    getCheckDetail (params) {
        return dispatch => {
            API.getCheckDetail(params).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: dataCheckActionType.GET_CHECK_DETAIL,
                        payload: res.data
                    });
                }
            });
        }
    },
    getSourcePart (params, type) {
        return dispatch => {
            DSAPI.getDataSourcesPart(params).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: dataCheckActionType.GET_SOURCE_PART,
                        payload: {
                            data: res.data ? res.data : [],
                            type: type
                        }
                    });
                }
            });
        }
    },
    resetSourcePart (params) {
        return dispatch => {
            dispatch({
                type: dataCheckActionType.RESET_SOURCE_PART,
                payload: params
            });
        }
    },
    changeParams (params) {
        return dispatch => {
            dispatch({
                type: dataCheckActionType.CHANGE_PARAMS,
                payload: params
            });
        }
    },
    addCheck (params) {
        return dispatch => {
            API.addCheck(params).then((res) => {
                if (res.code === 1) {
                    message.success('操作成功', 2);
                }
            });
        }
    },
    updateCheck (params) {
        return dispatch => {
            API.updateCheck(params).then((res) => {
                if (res.code === 1) {
                    message.success('操作成功', 2);
                }
            });
        }
    },
    deleteCheck (params) {
        return dispatch => {
            API.deleteCheck(params).then((res) => {
                if (res.code === 1) {
                    message.success('删除成功', 2);
                }
            });
        }
    }
}
