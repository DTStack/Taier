import commonActionType from "../consts/commonActionType";
import { message } from "antd";
import API from "../api";

const commonActions = {
    getUserList(params) {
        return dispatch => {
            API.getUserList(params).then(res => {
                if (res.code === 1) {
                    dispatch({
                        type: commonActionType.GET_USER_LIST,
                        payload: res.data
                    });
                }
            });
        };
    },
    getAllTable(params) {
        return dispatch => {
            API.searchTable({name: ''}).then(res => {
                if (res.code === 1) {
                    dispatch({
                        type: commonActionType.GET_TABLE_LIST,
                        payload: res.data
                    });
                }
            });
        };
    },
    getAllDict(params) {
        return dispatch => {
            API.getAllDict(params).then(res => {
                if (res.code === 1) {
                    dispatch({
                        type: commonActionType.GET_ALL_DICT,
                        payload: res.data
                    });
                }
            });
        };
    }
};

export default commonActions;
