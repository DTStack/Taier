import { cloneDeep, assign } from 'lodash';
import localDb from 'utils/localDb';
import workbenchAction from '../../../consts/workbenchActionType';

const workbenchStoreKey = 'engine_workbench';
// 默认Tab栏数据
const defaultTabBarData = {
    tabs: [],
    currentTab: 0,
}
const getInitialCachedData = () => {
    let initialState = localDb.get(workbenchStoreKey);
    if(!initialState) initialState = defaultTabBarData;
    return initialState;
}

export default function mainBench(state = getInitialCachedData(), action) {
    const { type, payload } = action;

    switch (type) {

        case workbenchAction.SWITCH_TAB: {
            const nextStore = assign({}, state, {
                currentTab: payload,
            });
            localDb.set(workbenchStoreKey, nextStore);
            return nextStore;
        }

        case workbenchAction.OPEN_TAB: {
            if (payload) {
                const tabs = [...state.tabs];
                const isExist = tabs.find(item => item.id === payload.id);
                if (!isExist) {
                    tabs.push(payload);
                }
                tabs.map((o,i)=>{
                    if(o.id === payload.id)
                        tabs[i] = payload
                })
                // TODO 若tabs已存在新传入的payload, 则tabs拷贝多余
                const newStore = assign({}, state, {
                    currentTab: payload.id,
                    tabs,
                })
                localDb.set(workbenchStoreKey, newStore);
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
                localDb.set(workbenchStoreKey, clone);
                return clone;
            }
        }

        case workbenchAction.NEW_TABLE_INFO_CHANGE: {
            console.log(payload)
            let tabData = {};
            state.tabs.map(o=>{
                if(o.id === state.currentTab){
                    tabData = o;
                }
            })
            for(let item in payload){
                tabData.tableItem[payload[item].key] = payload[item].value
            }
            const newState = assign({},state)
            console.log(newState)

            localDb.set(workbenchStoreKey, newState);
            return newState;
        }
        case workbenchAction.NEXT_STEP: {
            let tabData = {};
            state.tabs.map(o=>{
                if(o.id === state.currentTab){
                    tabData = o;
                }
            })
            tabData.currentStep = tabData.currentStep+1;
            return assign({}, state)
        }
        case workbenchAction.LAST_STEP: {
            let tabData = {};
            state.tabs.map(o=>{
                if(o.id === state.currentTab){
                    tabData = o;
                }
            })
            tabData.currentStep = tabData.currentStep-1;
            return assign({}, state)
        }
        case workbenchAction.NEW_TABLE_SAVED: {
            let tabData = {};
            state.tabs.map(o=>{
                if(o.id === state.currentTab){
                    tabData = o;
                }
            })
            tabData.tableItem.newTableId = payload.id;
            tabData.currentStep = tabData.currentStep+1;

            return assign({}, state)
        }
        case workbenchAction.SAVE_EDITTABLE_INFO: {
            let tabData = {};
            state.tabs.map(o=>{
                if(o.id === state.currentTab){
                    tabData = o;
                }
            })
            for(let item in payload){
                tabData.tableDetail[payload[item].key] = payload[item].value
            }
            const newState = assign({},state)

            localDb.set(workbenchStoreKey, newState);
            return newState;
        }
        case workbenchAction.TABLE_INFO_MOTIFIED: {
            //保存完成
            console.log('新的表信息已保存')
            console.log(payload)
            
            let tabData = {};
            state.tabs.map(o=>{
                if(o.id === state.currentTab){
                    tabData = o;
                }
            })
            tabData.tableDetail = payload;
            const newStore = assign({}, state);
            return newStore;
        }
        case workbenchAction.CLOSE_OTHERS: {
            const newStore = assign({}, state);

            newStore.tabs = newStore.tabs.filter(item => {
                return item.id == newStore.currentTab;
            })

            localDb.set(workbenchStoreKey, newStore);
            return newStore;
        }

        case workbenchAction.CLOSE_ALL: {
            localDb.set(workbenchStoreKey, '');
            return defaultTabBarData;
        }

        case workbenchAction.UPDATE_TAB: {
            if (payload) {
                const tabs = [...state.tabs];
                const index = tabs.findIndex(item => item.id === payload.id);
                if (index > -1) {
                    tabs[index] = assign(tabs[index], payload);
                }

                const newStore = assign({}, state, {
                    tabs,
                })
                console.log('UPDATE_TAB:', newStore);
                localDb.set(workbenchStoreKey, newStore);
                return newStore;
            }
        }
        default:
            return state;
    }
}
