import commonActionType from '../consts/commonActionType';
import { message } from 'antd';
import API from '../api/common';

export const commonActions = {
	getUserList(params) {
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
}
