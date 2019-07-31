
import { combineReducers } from 'redux';
import { experimentTabType } from '../../../consts/actionType/tabType';
import { saveReducer } from '../../helper';

import expandedKeys from './expandedKeys';
import files from './files';
import localTabs from './localTabs';

function currentTabIndex (state = null, action: any) {
    const { type, payload } = action;
    switch (type) {
        case experimentTabType.SET_CURRENT_TAB: {
            return payload
        }
        case experimentTabType.DELETE_TAB: {
            if (state == payload) {
                return null;
            }
            return state;
        }
        case experimentTabType.DELETE_ALL_TAB: {
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
    expandedKeys,
    currentTabIndex: saveReducer('experimentCurrentTabIndex', currentTabIndex)
});
