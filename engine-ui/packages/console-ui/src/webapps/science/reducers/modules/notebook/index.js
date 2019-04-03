
import { combineReducers } from 'redux';
import { notebookTabType } from '../../../consts/actionType/tabType';
import { saveReducer } from '../../helper';

import files from './files';
import localTabs from './localTabs';

function currentTabIndex (state = null, action) {
    const { type, payload } = action;
    switch (type) {
        case notebookTabType.SET_CURRENT_TAB: {
            return payload
        }
        case notebookTabType.DELETE_TAB: {
            if (state == payload) {
                return null;
            }
            return state;
        }
        case notebookTabType.DELETE_ALL_TAB: {
            return null;
        }
        default: {
            return state;
        }
    }
}

export default combineReducers({
    files,
    localTabs,
    currentTabIndex: saveReducer('notebookCurrentTabIndex', currentTabIndex)
});
