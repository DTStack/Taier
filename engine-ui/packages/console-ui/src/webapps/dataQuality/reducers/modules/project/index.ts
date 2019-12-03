import { combineReducers } from 'redux';
import actionType from '../../../consts/projectActionType';
import { saveReducer } from '../../helper';

function projectList (state: any[] = [], action: any) {
    const { type, payload } = action;
    switch (type) {
        case actionType.UPDATE_PROJECT_LIST: {
            return payload || [];
        }
        default: {
            return state;
        }
    }
}

function currentProject (state: any = null, action: any) {
    const { type, payload } = action;
    switch (type) {
        case actionType.SET_CURRENT_PROJECT: {
            return payload;
        }
        default: {
            return state;
        }
    }
}

export default combineReducers({
    projectList,
    currentProject: saveReducer('currentProject', currentProject)
});
