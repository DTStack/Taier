
import { notebookTabType } from '../../../consts/actionType/tabType';
import { saveReducer } from '../../helper'
function localTabs (state = [], action) {
    const { type, payload } = action;
    switch (type) {
        case notebookTabType.ADD_TAB: {
            return [...state, payload];
        }
        case notebookTabType.DELETE_TAB: {
            return state.filter((tab) => {
                return tab.id != payload
            })
        }
        case notebookTabType.CHANGE_TAB: {
            const index = state.findIndex((tab) => {
                return tab.id == payload.id
            });
            if (index > -1) {
                state = [...state];
                state.splice(index, 1, payload);
            }
            return state;
        }
        case notebookTabType.DELETE_OTHER_TAB: {
            return state.filter((tab) => {
                return tab.id == payload
            })
        }
        case notebookTabType.DELETE_ALL_TAB: {
            return [];
        }
        default: {
            return state;
        }
    }
}

export default saveReducer('notebookLocalTabs', localTabs);
