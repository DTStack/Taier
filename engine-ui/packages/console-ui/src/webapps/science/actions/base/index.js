import Api from '../../api';
import commonActionType from '../../consts/commonActionType';

export function getSysParams () {
    return (dispatch, getState) => {
        return new Promise(async () => {
            const state = getState();
            if (state.common.sysParams) {
                return;
            }
            let res = await Api.comm.getSysParams();
            if (res && res.code == 1) {
                dispatch({
                    type: commonActionType.SET_SYS_PARAMS,
                    payload: res.data
                });
            }
            return res;
        })
    }
}
