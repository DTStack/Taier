import mc from 'mirror-creator';
import API from '../../api';

// 公共actionTypes
const UploadAction = mc([
    'UPDATE',
    'RESET'
], { prefix: 'uploader/' })

const TIME_INTERVAL = 500; // 500毫秒

export const UPLOAD_STATUS = {
    SUCCES: 'success',
    PROGRESSING: 'progressing',
    READY: 'ready',
    FAIL: 'fail'
}

// Actions
export const getUploadStatus = (params, dispatch) => {
    let timeId;
    const getStatus = async () => {
        const res = await API.getUploadStatus(params);
        let status = UPLOAD_STATUS.PROGRESSING;
        if (res.code === 1) {
            clearInterval(timeId);
            status = UPLOAD_STATUS.SUCCES;
        }
        //  else {
        //     status = UPLOAD_STATUS.FAIL;
        // }
        dispatch({
            type: UploadAction.UPDATE,
            payload: {
                status: status
            }
        })
    }
    timeId = setInterval(getStatus, TIME_INTERVAL);
}

export const updateUploader = (payload) => {
    return {
        type: UploadAction.UPDATE,
        payload
    }
}

export const resetUploader = () => {
    return {
        type: UploadAction.RESET
    }
}

// Reducers
const initilaState = {
    status: UPLOAD_STATUS.READY, // 状态
    queryParams: '',
    fileName: 'testfile', // 文件名称
    percent: 20 // 进度百分比, 模拟进度
}
export function uploader (state = initilaState, action) {
    switch (action.type) {
        case UploadAction.UPDATE: {
            const data = action.payload;
            let percent = 20;
            if (data.status === UPLOAD_STATUS.SUCCES) {
                percent = 100;
            } else if (data.status === UPLOAD_STATUS.PROGRESSING) {
                percent = state.percent + 20
            }
            return Object.assign({
                percent
            }, state, data);
        }
        case UploadAction.RESET:
            return initilaState;
        default:
            return state;
    }
}
