/* eslint-disable */
import mc from 'mirror-creator';
import { message } from 'antd';
import localDb from 'utils/localDb';

import API from '../../api';

// 公共actionTypes
const UploadAction = mc([
    'UPDATE',
    'RESET'
], { prefix: 'uploader/' })

const TIME_INTERVAL = 3600; // 1秒

export const UPLOAD_STATUS = {
    SUCCES: 'success',
    PROGRESSING: 'progressing',
    READY: 'ready',
    FAIL: 'fail'
}

// ============= Actions =============

export function getUploadStatus(params: any) {
    return (dispatch: any) => {
        let timeId: any;
        let status = UPLOAD_STATUS.PROGRESSING;
        dispatch({
            type: UploadAction.UPDATE,
            payload: { ...params, status: status }
        })
        const getStatus = async () => {
            const res = await API.getUploadStatus(params.queryParams);
            if (res.data === 'done') {
                message.success(`文件${params.fileName}上传成功!`);
                clearInterval(timeId);
                status = UPLOAD_STATUS.SUCCES;
                return dispatch(resetUploader());
            } else if (res.code > 1) {
                status = UPLOAD_STATUS.FAIL;
                clearInterval(timeId);
                setTimeout(() => {
                    dispatch(resetUploader());
                }, TIME_INTERVAL)
            } else if (res.data === 'exist') {
                status = UPLOAD_STATUS.PROGRESSING;
            }
            dispatch({
                type: UploadAction.UPDATE,
                payload: { ...params, status: status }
            })
        }
        timeId = setInterval(getStatus, TIME_INTERVAL);
    }
}

export const updateUploader = (payload: any) => {
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

// ============= Reducers =============

const defaultState: any = {
    status: UPLOAD_STATUS.READY, // 状态
    queryParams: '',
    fileName: '', // 文件名称
    percent: 20 // 进度百分比, 模拟进度
};
const getInitialData = function () {
    let initialState = localDb.get('uploader_cache');
    if (!initialState) {
        return defaultState;
    }
    return initialState;
}

export function uploader (state = getInitialData(), action: any) {
    let nextState: any;

    switch (action.type) {
        case UploadAction.UPDATE: {
            const data = action.payload;
            let percent = 20;
            if (data.status === UPLOAD_STATUS.SUCCES) {
                percent = 100;
            } else if (data.status === UPLOAD_STATUS.PROGRESSING) {
                percent = state.percent >= 80 ? state.percent : state.percent + 20
            }
            nextState = Object.assign({}, state, data, { percent });
            break;
        }
        case UploadAction.RESET:
            nextState = defaultState;
            break;
        default:
            nextState = state;
    }

    localDb.set('uploader_cache', nextState);
    return nextState;
}
