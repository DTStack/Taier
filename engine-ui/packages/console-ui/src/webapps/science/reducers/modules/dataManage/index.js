import { combineReducers } from 'redux';
import sourceAction from '../../../consts/sourceActionType';
import { UPLOAD_STATUS } from '../../../comm/const';
import localDb from 'utils/localDb';

const tables = (state = {}, action) => {
    const { type, key, payload } = action;
    const newState = Object.assign({}, state);
    switch (type) {
        case sourceAction.SET_TABLE_LIST: {
            newState[key] = payload
            return newState;
        }

        default: return newState;
    }
}

const defaultState = {
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

function uploader (state = getInitialData(), action) {
    let nextState;

    switch (action.type) {
        case sourceAction.UPDATE: {
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
        case sourceAction.RESET:
            nextState = defaultState;
            break;
        default:
            nextState = state;
    }

    localDb.set('uploader_cache', nextState);
    return nextState;
}

export default combineReducers({
    tables,
    uploader
});
