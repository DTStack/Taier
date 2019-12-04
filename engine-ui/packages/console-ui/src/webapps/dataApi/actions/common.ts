import commonActionType from '../consts/commonActionType';
import API from '../api/common';

export const commonActions: any = {
    getUserList (params: any) {
        return (dispatch: any) => {
            API.getUserList(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: commonActionType.GET_USER_LIST,
                        payload: res.data
                    });
                }
            });
        }
    },
    getMenuList () {
        return async (dispatch: any) => {
            const res = await API.getMenuList();
            if (res.code === 1) {
                dispatch({
                    type: commonActionType.GET_ALL_MENU_LIST,
                    payload: res.data
                });
            }
        }
    }
}
