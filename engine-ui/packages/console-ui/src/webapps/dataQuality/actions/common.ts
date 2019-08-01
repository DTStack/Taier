import commonActionType from '../consts/commonActionType';
// import { message } from 'antd';
import API from '../api/common';

export const commonActions: any = {
    getUserList () {
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
    getAllDict () {
        return (dispatch: any) => {
            API.getAllDict().then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: commonActionType.GET_ALL_DICT,
                        payload: res.data
                    });
                }
            });
        }
    }

}
