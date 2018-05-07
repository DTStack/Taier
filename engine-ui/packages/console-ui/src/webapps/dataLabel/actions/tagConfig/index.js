import { tagConfigActionType } from '../../consts/tagConfigActionType';
import API from '../../api/tagConfig';

export const tagConfigActions = {
	getRegisteredTagList(params) {
		return dispatch => {
			dispatch({
				type: tagConfigActionType.CHANGE_LOADING
			});
			API.getRegisteredTag(params).then((res) => {
				if (res.code === 1) {
					dispatch({
						type: tagConfigActionType.GET_REGISTERED_TAG_LIST,
						payload: res.data
					});
				}
				dispatch({
					type: tagConfigActionType.CHANGE_LOADING
				});
			});
		}
	},
	getRuleTagList(params) {
		return dispatch => {
			dispatch({
				type: tagConfigActionType.CHANGE_LOADING
			});
			API.getRuleTag(params).then((res) => {
				if (res.code === 1) {
					dispatch({
						type: tagConfigActionType.GET_RULE_TAG_LIST,
						payload: res.data
					});
				}
				dispatch({
					type: tagConfigActionType.CHANGE_LOADING
				});
			});
		}
	},
	
}
