import { ruleConfigActions as ACTION_TYPE } from '../../consts/ruleConfigActions';
import { message } from 'antd';
import API from '../../api/ruleConfig';

export const ruleConfigActions = {
	getRuleLists(params) {
		return dispatch => {
			dispatch({
				type: ACTION_TYPE.CHANGE_LOADING
			});
			API.getRuleLists(params).then((res) => {
				if (res.code === 1) {
					dispatch({
						type: ACTION_TYPE.GET_RULE_LIST,
						payload: res.data
					});
				} 
				dispatch({
					type: ACTION_TYPE.CHANGE_LOADING
				});
			});
		}
	}
}
