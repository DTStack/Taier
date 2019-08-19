
import { notebookTabType } from '../../../consts/actionType/tabType';
import { saveReducer } from '../../helper'
function localTabs (state: any[] = [], action: any) {
    state = state || [];
    const { type, payload } = action;
    switch (type) {
        case notebookTabType.ADD_TAB: {
            const tab = state.filter((tab: any) => {
                return tab.id == payload.id
            });
            if (tab.length) {
                return state;
            } else {
                return [...state, payload];
            }
        }
        case notebookTabType.DELETE_TAB: {
            return state.filter((tab: any) => {
                return tab.id != payload
            })
        }
        case notebookTabType.CHANGE_TAB: {
            const index = state.findIndex((tab: any) => {
                return tab.id == payload.id
            });
            if (index > -1) {
                state = [...state];
                state.splice(index, 1, payload);
            }
            return state;
        }
        case notebookTabType.DELETE_OTHER_TAB: {
            return state.filter((tab: any) => {
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
