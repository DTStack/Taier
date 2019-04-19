
import { experimentTabType } from '../../../consts/actionType/tabType';
import { saveReducer } from '../../helper'
function localTabs (state = [], action) {
    state = state || [];
    const { type, payload } = action;
    switch (type) {
        case experimentTabType.ADD_TAB: {
            const tab = state.filter((tab) => {
                return tab.id == payload.id
            });
            if (tab.length) {
                return state;
            } else {
                return [...state, payload];
            }
        }
        case experimentTabType.DELETE_TAB: {
            return state.filter((tab) => {
                return tab.id != payload
            })
        }
        case experimentTabType.CHANGE_TAB: {
            const index = state.findIndex((tab) => {
                return tab.id == payload.id
            });
            if (index > -1) {
                state = [...state];
                state.splice(index, 1, payload);
            }
            return state;
        }
        case experimentTabType.DELETE_OTHER_TAB: {
            return state.filter((tab) => {
                return tab.id == payload
            })
        }
        case experimentTabType.DELETE_ALL_TAB: {
            return [];
        }
        default: {
            return [{
                id: 1,
                name: 'test'
            }];
            // return state;
        }
    }
}

export default saveReducer('experimentLocalTabs', localTabs);
