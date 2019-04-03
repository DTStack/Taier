import { combineReducers } from 'redux';
import actionType from '../../../consts/actionType/projectType'
function projectList (state = [], action) {
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

function currentProject (state = null, action) {
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
    currentProject
});
