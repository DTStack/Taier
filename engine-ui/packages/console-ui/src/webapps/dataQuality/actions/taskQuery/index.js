import { taskQueryActionType } from '../../consts/taskQueryActionType';
import { message } from 'antd';
import API from '../../api/taskQuery';

export const taskQueryActions = {
	getTaskList(params) {
		return dispatch => {
			dispatch({
				type: taskQueryActionType.CHANGE_LOADING
			});
			API.getTaskList(params).then((res) => {
				if (res.code === 1) {
					dispatch({
						type: taskQueryActionType.GET_TASK_LIST,
						payload: res.data
					});
				} 
				dispatch({
					type: taskQueryActionType.CHANGE_LOADING
				});
			});
		}
	},
	getTaskDetail(params) {
		return dispatch => {
			API.getTaskDetail(params).then((res) => {
				if (res.code === 1) {
					dispatch({
						type: taskQueryActionType.GET_TASK_DETAIL,
						payload: res.data
					});
				}
			});
		}
	},
}
