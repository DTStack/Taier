import { approvalActionType as ACTION_TYPE } from '../../consts/approvalActionType';
import API from '../../api/approval';

export const approvalActions = {

    // 获取市场api列表
    allApplyList (params) {
        return (dispatch) => {
            return API.allApplyList(params).then((res) => {
                if (res.code === 1) {
                    dispatch({
                        type: ACTION_TYPE.GET_ALL_APPLY_LIST,
                        payload: res.data.data
                    });
                    return res;
                }
            });
        }
    },
    // 审批
    handleApply (params) {
        return (dispatch) => {
            return API.handleApply(params).then((res) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    }

}
