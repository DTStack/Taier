import { ruleConfigActionType } from '../../consts/ruleConfigActionType';
import { message } from 'antd';
import API from '../../api/ruleConfig';

export const ruleConfigActions = {
	getRuleLists(params) {
		return dispatch => {
			dispatch({
				type: ruleConfigActionType.CHANGE_LOADING
			});
			API.getRuleLists(params).then((res) => {
				if (res.code === 1) {
					dispatch({
						type: ruleConfigActionType.GET_RULE_LIST,
						payload: res.data
					});
				} 
				dispatch({
					type: ruleConfigActionType.CHANGE_LOADING
				});
			});
		}
	},
	getMonitorFunction(params) {
		return dispatch => {
			API.getMonitorFunction(params).then((res) => {
				if (res.code === 1) {
					dispatch({
						type: ruleConfigActionType.GET_RULE_FUNCTION,
						payload: res.data
					});
				}
			});
		}
	},
	addRule(params) {
		return dispatch => {
			API.addRule(params).then((res) => {
				if (res.code === 1) {
					dispatch({
						type: ruleConfigActionType.GET_RULE_FUNCTION,
						payload: res.data
					});
				}
			});
		}
	}

}
