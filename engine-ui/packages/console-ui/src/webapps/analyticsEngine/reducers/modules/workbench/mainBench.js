import { cloneDeep, assign } from 'lodash';
import localDb from 'utils/localDb';
import workbenchAction from '../../../consts/workbenchActionType';

const getInitialCachedData = () => {
    let initialState = localDb.get('engine_workbench');
    if(!initialState) initialState = {
        tabs: [
            {
                id: 1,
                name: 'testData',
                actionType: 'workbench/OPEN_DATABASE',
            },{
                id: 2,
                name: 'testData1',
                actionType: 'workbench/CREATE_TABLE',
            }, {
                id: 3,
                name: 'testData2',
                actionType: 'workbench/CREATE_DATA_MAP',
            }
        ],
        currentTab: 1,
    };
    return initialState;
}

export default function mainBench(state = getInitialCachedData(), action) {
    const { type, payload } = action;

    switch (type) {

        case workbenchAction.SWITCH_TAB: {
            return assign({}, state, {
                currentTab: payload,
            })
        }

        case workbenchAction.OPEN_TAB: {
            if (payload) {
                const tabs = [...state.tabs];
                tabs.push(payload);
                const newStore = assign({}, state, {
                    currentTab: payload.id,
                    tabs,
                })
                localDb.set('engine_workbench', newStore);
                return newStore;
            }
        }

        case workbenchAction.CLOSE_TAB: {
         
            const tabId = action.payload;
            const tabIndex = state.tabs.findIndex(tab => tab.id === tabId);

            if (tabIndex > -1) {
                let clone = cloneDeep(state);
                if (tabId === state.currentTab) {
                    const nextTab = state.tabs[tabIndex + 1] || state.tabs[tabIndex - 1];
                    console.log('nextTab:', tabIndex, state.tabs, nextTab)
                    clone.currentTab = nextTab ? nextTab.id : clone.tabs[0].id;
                }
                // 删除
                clone.tabs.splice(tabIndex, 1);
                return clone;
            }
        }
        default:
            return state;
    }
}