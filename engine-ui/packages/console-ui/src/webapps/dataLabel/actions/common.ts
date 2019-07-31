import commonActionType from '../consts/commonActionType';
import API from '../api/common';

export const commonActions: any = {
    getUserList (params: any) {
        return (dispatch: any) => {
            API.getUserList().then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: commonActionType.GET_USER_LIST,
                        payload: res.data
                    });
                }
            });
        }
    },
    getAllMenuList (params: any) {
        return (dispatch: any) => {
            API.getAllMenuList().then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: commonActionType.GET_ALL_MENU_LIST,
                        payload: res.data
                    });
                }
            });
        }
    },
    getPeriodType (params: any) {
        return (dispatch: any) => {
            API.getPeriodType().then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: commonActionType.GET_PERIOD_TYPE,
                        payload: res.data
                    });
                }
            });
        }
    },
    getNotifyType (params: any) {
        return (dispatch: any) => {
            API.getNotifyType().then((res: any) => {
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
