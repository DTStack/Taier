import { ruleConfigActionType } from '../../consts/ruleConfigActionType';
import { message } from 'antd';
import API from '../../api/ruleConfig';

export const ruleConfigActions = {
	getMonitorLists(params) {
		return dispatch => {
			dispatch({
				type: ruleConfigActionType.CHANGE_LOADING
			});
			API.getMonitorLists(params).then((res) => {
				if (res.code === 1) {
					dispatch({
						type: ruleConfigActionType.GET_MONITOR_LIST,
						payload: res.data
					});
				} 
				dispatch({
					type: ruleConfigActionType.CHANGE_LOADING
				});
			});
		}
	},
	getRuleFunction(params) {
		return dispatch => {
			API.getRuleFunction(params).then((res) => {
				if (res.code === 1) {
					dispatch({
						type: ruleConfigActionType.GET_RULE_FUNCTION,
						payload: res.data
					});
				}
			});
		}
	},
	addMonitor(params) {
		return dispatch => {
			API.addMonitor(params).then((res) => {
				if (res.code === 1) {
					message.success('添加成功！');
				}
			});
		}
	},
	getMonitorRule(params) {
		return dispatch => {
			API.getMonitorRule(params).then((res) => {
				if (res.code === 1) {
					dispatch({
						type: ruleConfigActionType.GET_MONITOR_RULE,
						payload: res.data
					});
				}
			});
		}
	},
	changeMonitorStatus(params) {
		return dispatch => {
			API.changeMonitorStatus(params).then((res) => {
				if (res.code === 1) {
					message.success('操作成功！');
				}
			});
		}
	},
	getMonitorDetail(params) {
		return dispatch => {
			API.getMonitorDetail(params).then((res) => {
				if (res.code === 1) {
					dispatch({
						type: ruleConfigActionType.GET_MONITOR_DETAIL,
						payload: res.data
					});
				}
			});
		}
	},
	executeMonitor(params) {
		return dispatch => {
			API.executeMonitor(params).then((res) => {
				if (res.code === 1) {
					message.success('操作成功！');
				}
			});
		}
	},
}
