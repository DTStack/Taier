import { dataCheckActions as ACTION_TYPE } from '../../consts/dataCheckActions';
import { message } from 'antd';
import API from '../../api/dataCheck';

export const dataCheckActions = {
	getLists(params) {
		return dispatch => {
			dispatch({
				type: ACTION_TYPE.CHANGE_LOADING
			});
			API.getLists(params).then((res) => {
				if (res.code === 1) {
					dispatch({
						type: ACTION_TYPE.GET_LIST,
						payload: res.data
					});
				} 
				dispatch({
					type: ACTION_TYPE.CHANGE_LOADING
				});
			});
		}
	},
	getCheckDetail(params) {
		return dispatch => {
			API.getCheckDetail(params).then((res) => {
				if (res.code === 1) {
					dispatch({
						type: ACTION_TYPE.GET_CHECK_DETAIL,
						payload: res.data
					});
				}
			});
		}
	},
	changeParams(params) {
		return dispatch => {
			dispatch({
				type: ACTION_TYPE.CHANGE_PARAMS,
				payload: params
			});
		}
	},
	editCheck(params) {
		return dispatch => {
			API.editCheck(params).then((res) => {
				if (res.code === 1) {
					message.success("操作成功", 2);
				}
			});
		}
	},
	deleteCheck(params) {
		return dispatch => {
			API.deleteCheck(params).then((res) => {
				if (res.code === 1) {
					message.success("删除成功", 2);
				}
			});
		}
	},
}
