import assign from 'object-assign';
import { cloneDeep } from 'lodash';
import localDb from 'utils/localDb';
import { message } from 'antd';

import {
    workbenchAction,
} from './actionType';


const getCachedData = () => {
    let initialState = localDb.get('offline_workbench');
    if(!initialState) initialState = {
        tabs: [],
        currentTab: undefined,
        isCurrentTabNew: undefined,
        taskCustomParams: [],
    };
    return initialState;
}

export const workbenchReducer = (state = getCachedData(), action) => {
    let nextState;

    switch(action.type) {
        case workbenchAction.LOAD_TASK_DETAIL: {
            const tab = action.payload;

            nextState = assign({}, state, {
                tabs: [...state.tabs, tab],
                currentTab: tab.id
            });
            break;
        }

        case workbenchAction.LOAD_TASK_CUSTOM_PARAMS: {
            const data = action.payload;
            nextState = assign({}, state, {taskCustomParams: data});
            break;
        }

        case workbenchAction.OPEN_TASK_TAB: {
            const tabId = action.payload;

            nextState = assign({}, state, {currentTab: tabId});
            break;
        } 

        case workbenchAction.CLOSE_TASK_TAB: {
            const tabId = action.payload;
            let clone = cloneDeep(state);

            clone.tabs = clone.tabs.filter(tab => {
                return tab.id !== tabId
            });

            if(tabId === state.currentTab) {
                clone.currentTab = clone.tabs.length ? clone.tabs[0].id : undefined;
            }

            nextState = clone;
            break;
        }

        case workbenchAction.CLOSE_ALL_TABS: {
            const clone = cloneDeep(state);

            clone.tabs = [];
            clone.currentTab = undefined;

            nextState = clone;
            break;
        }

        case workbenchAction.CLOSE_OTHER_TABS: {
            const tabId = action.payload;
            let clone = cloneDeep(state);

            clone.tabs = clone.tabs.filter(tab => {
                return tab.id === tabId
            });

            nextState = clone;
            break;
        }

        case workbenchAction.CHANGE_SCHEDULE_CONF: {
            const newConf = action.payload;

            newConf.beginDate = newConf.beginDate.format('YYYY-MM-DD');
            newConf.endDate = newConf.endDate.format('YYYY-MM-DD');
            if(newConf.weekDay && _.isArray(newConf.weekDay)){
                newConf.weekDay = newConf.weekDay.join(',');
            }
            if(newConf.day && _.isArray(newConf.day)){
                newConf.day = newConf.day.join(',');
            }

            const clone = cloneDeep(state);
            clone.tabs = clone.tabs.map(tab => {
                if(tab.id === clone.currentTab) {
                    tab.scheduleConf = JSON.stringify(newConf);
                    tab.notSynced = true;
                    return tab;
                }
                else{
                    return tab;
                }
            });

            nextState = clone;
            break;
        }

        case workbenchAction.CHANGE_SCHEDULE_STATUS: {
            const status = action.payload;

            const clone = cloneDeep(state);
            clone.tabs = clone.tabs.map(tab => {
                if(tab.id === clone.currentTab) {
                    tab.scheduleStatus = status;
                    tab.notSynced = true;
                    return tab;
                }
                else{
                    return tab;
                }
            });

            nextState = clone;
            break;
        }

        case workbenchAction.CHANGE_TASK_SUBMITSTATUS: {
            const submitStatus = action.payload;

            const clone = cloneDeep(state);
            clone.tabs = clone.tabs.map(tab => {
                if(tab.id === clone.currentTab) {
                    tab.submitStatus = submitStatus;
                    return tab;
                }
                else{
                    return tab;
                }
            });

            nextState = clone;
            break;
        }

        case workbenchAction.ADD_VOS: {
            const newVOS = action.payload;
            const clone = cloneDeep(state);

            // debugger;
            clone.tabs = clone.tabs.map(tab => {
                if(tab.id === clone.currentTab) {
                    let taskVOS = tab.taskVOS || [];
                    let duplicated = false;

                    for(let o of taskVOS) {
                        if(o.id === newVOS.id) {
                            duplicated = true;
                            break;
                        }
                    }

                    if(duplicated) {
                        message.error('该依赖任务存在');
                        return tab;
                    }

                    tab.taskVOS = [...tab.taskVOS || [], newVOS];
                    tab.notSynced = true;
                    return tab;
                }
                else{
                    return tab;
                }
            });

            nextState = clone;
            break;
        }

        case workbenchAction.DEL_VOS: {
            const id = action.payload;
            const clone = cloneDeep(state);

            clone.tabs = clone.tabs.map(tab => {
                if(tab.id === clone.currentTab) {
                    tab.taskVOS = tab.taskVOS.filter(vos => {
                        return vos.id !== id;
                    });
                    tab.notSynced = true;
                    return tab;
                }
                else{
                    return tab;
                }
            });

            nextState = clone;
            break;
        }

        // 修改任务属性
        case workbenchAction.SET_TASK_FIELDS_VALUE: {
            const obj = action.payload;
            const clone = cloneDeep(state);

            clone.tabs = clone.tabs.map(tab => {
                if(tab.id === clone.currentTab) {
                    tab = assign(tab, obj);
                    tab.notSynced = true;
                    return tab;
                }
                else{
                    return tab;
                }
            });

            nextState = clone;
            break;
        }
        
        // 修改任务属性(代码编辑器)
        case workbenchAction.SET_TASK_FIELDS_VALUE_SILENT: {
            const obj = action.payload;

            // 故意不走immutable, 避免编辑器rerender导致光标回零
            state.tabs = state.tabs.map(tab => {
                if (tab.id === state.currentTab) {
                    tab = assign(tab, obj);
                    tab.notSynced = true;
                    return tab;
                }
                else {
                    return tab;
                }
            });

            nextState = state;
            break;
        }

        // 设置SQL
        case workbenchAction.SET_TASK_SQL_FIELD_VALUE: {
            const obj = action.payload;
            const clone = cloneDeep(state)

            clone.tabs = clone.tabs.map(tab => {
                if(tab.id === clone.currentTab) {
                    tab = assign(tab, obj);
                    tab.notSynced = true;
                    return tab;
                }
                else{
                    return tab;
                }
            });

            nextState = clone;
            break;
        }

        // 新建的dataSync任务标记
        case workbenchAction.SET_CURRENT_TAB_NEW: {
            nextState = assign({}, state, {
                isCurrentTabNew: true
            });
            break;
        }

        case workbenchAction.SET_CURRENT_TAB_SAVED: {
            nextState = assign({}, state, {
                isCurrentTabNew: undefined
            });
            break;
        }

        case workbenchAction.MAKE_TAB_DIRTY: {
            const clone = cloneDeep(state);

            clone.tabs = clone.tabs.map(tab => {
                if(tab.id === clone.currentTab) {
                    tab.notSynced = true;
                    return tab;
                }
                else{
                    return tab;
                }
            });

            nextState = clone;
            break;
        }

        case workbenchAction.MAKE_TAB_CLEAN: {
            const clone = cloneDeep(state);

            clone.tabs = clone.tabs.map(tab => {
                if(tab.id === clone.currentTab) {
                    tab.notSynced = undefined;
                    return tab;
                }
                else{
                    return tab;
                }
            });

            nextState = clone;
            break;
        }

        default:
            nextState = state;
            break;
    }

    localDb.set('offline_workbench', nextState);
    return nextState;
};
