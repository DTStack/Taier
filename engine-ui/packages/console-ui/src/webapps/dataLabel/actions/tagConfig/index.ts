import { tagConfigActionType } from '../../consts/tagConfigActionType';
import API from '../../api/tagConfig';

export const tagConfigActions: any = {
    getRuleTagList (params: any) {
        return (dispatch: any) => {
            dispatch({
                type: tagConfigActionType.CHANGE_LOADING
            });
            API.queryRuleTag(params).then((res: any) => {
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
    getAllIdentifyColumn (params: any) {
        return (dispatch: any) => {
            API.getAllIdentifyColumn(params).then((res: any) => {
                if (res.code === 1) {
                    dispatch({
                        type: tagConfigActionType.GET_ALL_IDENTIFY_COLUMN,
                        payload: res.data
                    });
                }
            });
        }
    }
}
