import mc from 'mirror-creator';

// 公共actionTypes
const commAction = mc([
    'REQ_LOADING',
    'REQ_SUCCESS',
    'REQ_ERROR'
], { prefix: 'comm/' })


// 请状态
export function req (state = 'success', action: any) {
    switch (action.type) {
        case commAction.REQ_LOADING:
            return 'loading';
        case commAction.REQ_SUCCESS:
            return 'success';
        case commAction.REQ_ERROR:
            return 'error';
        default:
            return state;
    }
}
