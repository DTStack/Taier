import { cloneDeep, assign } from 'lodash';
import localDb from 'utils/localDb';
import workbenchAction from '../../../consts/workbenchActionType';

const workbenchStoreKey = 'engine_workbench';
// 默认Tab栏数据
const defaultTabBarData = {

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
            table: {
                id: 1,
                name: 'testTable'
            },
            actionType: 'workbench/CREATE_DATA_MAP',
        },{
            id: 4,
            name: 'testData4',
            actionType: 'workbench/OPEN_TABLE_EDITOR',
        }
    ],
    currentTab: 1,
    currentStep: 0,
    newanalyEngineTableDataList: {},//新建的多个表数据
    editTableInfoList: {},//正在编辑的多个表数据
    tableDetail: {},//表详情
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
                return clone;
            }
        }

        case workbenchAction.NEW_TABLE_INFO_CHANGE: {

            let newanalyEngineTableDataList = state.newanalyEngineTableDataList || {};
            newanalyEngineTableDataList[`tableItem${state.currentTab}`] = newanalyEngineTableDataList[`tableItem${state.currentTab}`] || {}
            for(let item in payload){
                newanalyEngineTableDataList[`tableItem${state.currentTab}`][payload[item].key] = payload[item].value
            }
            const newState = assign({},state,{
                newanalyEngineTableDataList: newanalyEngineTableDataList
            })
            localDb.set('engine_workbench', newState);

            console.log(newState)
            return newState;
        }
        case workbenchAction.NEXT_STEP: {
            console.log('NEXT')
            // let clone = cloneDeep(state);
            console.log(state)

            let currentStep = state.currentStep + 1;
            console.log(state)
            return assign({}, state, {
                currentStep: currentStep,
            })
        }




        case workbenchAction.LAST_STEP: {
            let currentStep = state.currentStep - 1;
            return assign({},state,{
                currentStep: currentStep
            })
        }
        case workbenchAction.NEW_TABLE_SAVED: {
            // let currentStep = state.currentStep + 1;
            // return assign({},state,{
            //     currentStep: currentStep
            // })
            console.log('表已保存')
            //保存完成
        }
        case workbenchAction.SAVE_EDITTABLE_INFO: {
            let editTableInfoList = state.editTableInfoList || {};
            editTableInfoList[`tableInfo${state.currentTab}`] = editTableInfoList[`tableInfo${state.currentTab}`] || {};
            for(let item in payload){
                editTableInfoList[`tableInfo${state.currentTab}`][payload[item].key] = payload[item].value
            }
            const newState = assign({},state,{
                editTableInfoList: editTableInfoList
            })
            localDb.set('engine_workbench', newState);

            return newState;
        }
        case workbenchAction.TABLE_INFO_MOTIFIED: {
            //保存完成
            console.log('新的表信息已保存')
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
        default:
            return state;
    }
}
