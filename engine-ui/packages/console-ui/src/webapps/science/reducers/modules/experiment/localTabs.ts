
import { experimentTabType } from '../../../consts/actionType/tabType';
import { saveReducer } from '../../helper'
function localTabs (state: any[] = [], action: any) {
    state = state || [];
    const { type, payload } = action;
    switch (type) {
        case experimentTabType.ADD_TAB: {
            let index = state.findIndex((tab: any) => {
                return tab.id == payload.id
            })
            if (index >= 0) {
                let currentStateList = [...state].splice(index, 1, payload)
                return currentStateList
            }
            return [...state, payload]
        }
        case experimentTabType.DELETE_TAB: {
            return state.filter((tab: any) => {
                return tab.id != payload
            })
        }
        case experimentTabType.CHANGE_TAB: {
            const index = state.findIndex((tab: any) => {
                return tab.id == payload.id
            });
            if (index > -1) {
                state = [...state];
                state.splice(index, 1, payload);
            }
            return state;
        }
        case experimentTabType.CHANGE_TAB_SLIENT: {
            const object = state.find((tab: any) => {
                return tab.id == payload.id
            });
            if (object) {
                Object.assign(object, payload)
            }
            return state;
        }
        case experimentTabType.DELETE_OTHER_TAB: {
            return state.filter((tab: any) => {
                return tab.id == payload
            })
        }
        case experimentTabType.DELETE_ALL_TAB: {
            return [];
        }
        case experimentTabType.CHANGE_TASK_STATUS: {
            const tab = state.find((tab: any) => {
                return tab.id == payload.tabId
            })
            for (const key in payload) {
                if (payload.hasOwnProperty(key)) {
                    const element = payload.status[key];
                    const object = tab.payload.find((o: any) => o.data.id == key);
                    object.data.status = element;
                }
            }
            return [].concat(state);
        }
        default: {
            return state;
        }
    }
}

export default saveReducer('experimentLocalTabs', localTabs);
