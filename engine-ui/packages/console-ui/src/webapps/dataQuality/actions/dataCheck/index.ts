import { dataCheckActionType } from '../../consts/dataCheckActionType';
import { message } from 'antd';
import API from '../../api/dataCheck';
import DSAPI from '../../api/dataSource';

export const dataCheckActions: any = {
    getLists (params: any) {
        return (dispatch: any) => {
            dispatch({
                type: dataCheckActionType.CHANGE_LOADING
            });
            API.getLists(params).then((res: any) => {
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
    getCheckDetail (params: any) {
        return (dispatch: any) => {
            API.getCheckDetail(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: dataCheckActionType.GET_CHECK_DETAIL,
                        payload: res.data
                    });
                }
            });
        }
    },
    getSourcePart (params: any, type: any) {
        return (dispatch: any) => {
            DSAPI.getDataSourcesPart(params).then((res: any) => {
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
    resetSourcePart (params: any) {
        return (dispatch: any) => {
            dispatch({
                type: dataCheckActionType.RESET_SOURCE_PART,
                payload: params
            });
        }
    },
    changeParams (params: any) {
        return (dispatch: any) => {
            dispatch({
                type: dataCheckActionType.CHANGE_PARAMS,
                payload: params
            });
        }
    },
    addCheck (params: any) {
        return (dispatch: any) => {
            API.addCheck(params).then((res: any) => {
                if (res.code === 1) {
                    message.success('操作成功', 2);
                }
            });
        }
    },
    updateCheck (params: any) {
        return (dispatch: any) => {
            API.updateCheck(params).then((res: any) => {
                if (res.code === 1) {
                    message.success('操作成功', 2);
                }
            });
        }
    },
    deleteCheck (params: any) {
        return (dispatch: any) => {
            API.deleteCheck(params).then((res: any) => {
                if (res.code === 1) {
                    message.success('删除成功', 2);
                }
            });
        }
    }
}
