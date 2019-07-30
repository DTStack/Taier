import { approvalActionType as ACTION_TYPE } from '../../consts/approvalActionType';
import API from '../../api/approval';

export const approvalActions: any = {

    // 获取市场api列表
    allApplyList(params: any) {
        return (dispatch: any) => {
            return API.allApplyList(params).then((res: any) => {
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
    handleApply(params: any) {
        return (dispatch: any) => {
            return API.handleApply(params).then((res: any) => {
                if (res.code === 1) {
                    return res;
                }
            });
        }
    }

}
