import actionType from '../../../consts/actionType/resourceExt';

function isShowFixResource (state = false, action: any) {
    const { type, payload } = action;
    switch (type) {
        case actionType.SHOW_FIX_RESOURCE: {
            return payload
        }
        default: {
            return state;
        }
    }
}
export default isShowFixResource;
