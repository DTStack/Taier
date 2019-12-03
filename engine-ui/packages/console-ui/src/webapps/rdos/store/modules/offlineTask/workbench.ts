import assign from 'object-assign';
import { cloneDeep, isArray } from 'lodash';
import { offlineWorkbenchDB as idb } from '../../../../../database';
import { message } from 'antd';

import { store } from '../../index';

import {
    workbenchAction
} from './actionType';

const getCachedData = () => {
    let initialState: any; // localDb.get('offline_workbench');

    if (!initialState) {
        initialState = {
            tabs: [],
            currentTab: undefined,
            isCurrentTabNew: undefined,
            taskCustomParams: [],
            showPanel: false
        };
    }
    return initialState;
}

export const workbenchReducer = (state = getCachedData(), action: any) => {
    let nextState: any;

    // 按原有逻辑读取项目信息，最好还是通过Action传递，由于对老代码业务逻辑有影响，
    // 先按这种测试
    let projectID = '';
    if (store) {
        const globalState = store.getState();
        projectID = globalState.project && globalState.project.id && globalState.project.id !== 0 ? globalState.project.id : ''
    }

    switch (action.type) {
        case workbenchAction.LOAD_TASK_DETAIL: {
            const tab = action.payload;
            const index = state.tabs.findIndex((t: any) => t.id === tab.id)
            if (index > -1) {
                const newTabs: any = [...state.tabs]
                newTabs[index] = tab;
                nextState = assign({}, state, {
                    tabs: newTabs,
                    currentTab: tab.id
                });
            } else {
                nextState = assign({}, state, {
                    tabs: [...state.tabs, tab],
                    currentTab: tab.id
                });
            }
            break;
        }

        case workbenchAction.LOAD_TASK_CUSTOM_PARAMS: {
            const data = action.payload;
            nextState = assign({}, state, { taskCustomParams: data }, { showPanel: true });
            break;
        }

        case workbenchAction.OPEN_TASK_TAB: {
            const tabId = action.payload;

            nextState = assign({}, state, { currentTab: tabId });
            break;
        }

        case workbenchAction.CLOSE_TASK_TAB: {
            const tabId = action.payload;
            const tabIndex = state.tabs.findIndex((tab: any) => tab.id === tabId);
            nextState = state;
            if (tabIndex > -1) {
                let clone = cloneDeep(state);
                if (tabId === state.currentTab) {
                    const nextTab = clone.tabs[tabIndex + 1] || clone.tabs[tabIndex - 1];
                    clone.currentTab = nextTab ? nextTab.id : clone.tabs[0].id;
                }
                // 删除
                clone.tabs.splice(tabIndex, 1);
                nextState = clone;
            }
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

            clone.tabs = clone.tabs.filter((tab: any) => {
                return tab.id === tabId
            });

            nextState = clone;
            break;
        }

        case workbenchAction.CHANGE_SCHEDULE_CONF: {
            const newConf = action.payload;

            newConf.beginDate = newConf.beginDate && newConf.beginDate.format
                ? newConf.beginDate.format('YYYY-MM-DD') : newConf.beginDate;

            newConf.endDate = newConf.endDate && newConf.endDate.format
                ? newConf.endDate.format('YYYY-MM-DD') : newConf.endDate;

            if (newConf.weekDay && isArray(newConf.weekDay)) {
                newConf.weekDay = newConf.weekDay.join(',');
            }
            if (newConf.day && isArray(newConf.day)) {
                newConf.day = newConf.day.join(',');
            }

            const clone = cloneDeep(state);
            clone.tabs = clone.tabs.map((tab: any) => {
                if (tab.id === clone.currentTab) {
                    tab.scheduleConf = JSON.stringify(newConf);
                    tab.notSynced = true;
                    return tab;
                } else {
                    return tab;
                }
            });

            nextState = clone;
            break;
        }

        case workbenchAction.CHANGE_SCHEDULE_STATUS: {
            const status = action.payload;

            const clone = cloneDeep(state);
            clone.tabs = clone.tabs.map((tab: any) => {
                if (tab.id === clone.currentTab) {
                    tab.scheduleStatus = status;
                    tab.notSynced = true;
                    return tab;
                } else {
                    return tab;
                }
            });

            nextState = clone;
            break;
        }

        case workbenchAction.CHANGE_TASK_SUBMITSTATUS: {
            const submitStatus = action.payload;

            const clone = cloneDeep(state);
            clone.tabs = clone.tabs.map((tab: any) => {
                if (tab.id === clone.currentTab) {
                    tab.submitStatus = submitStatus;
                    tab.notSynced = false;
                    return tab;
                } else {
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
            clone.tabs = clone.tabs.map((tab: any) => {
                if (tab.id === clone.currentTab) {
                    let taskVOS = tab.taskVOS || [];
                    let duplicated = false;

                    for (let o of taskVOS) {
                        if (o.id === newVOS.id) {
                            duplicated = true;
                            break;
                        }
                    }

                    if (duplicated) {
                        message.error('该依赖任务存在');
                        return tab;
                    }

                    tab.taskVOS = [...tab.taskVOS || [], newVOS];
                    tab.notSynced = true;
                    return tab;
                } else {
                    return tab;
                }
            });

            nextState = clone;
            break;
        }

        case workbenchAction.DEL_VOS: {
            const id = action.payload;
            const clone = cloneDeep(state);

            clone.tabs = clone.tabs.map((tab: any) => {
                if (tab.id === clone.currentTab) {
                    tab.taskVOS = tab.taskVOS.filter((vos: any) => {
                        return vos.id !== id;
                    });
                    tab.notSynced = true;
                    return tab;
                } else {
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

            clone.tabs = clone.tabs.map((tab: any) => {
                if (tab.id === clone.currentTab) {
                    // 对任务变量做特殊处理, 合并2个数组
                    if (obj.taskVariables && tab.taskVariables && obj.taskVariables.length > 0 && tab.taskVariables.length > 0) {
                        const varArr: any = [...obj.taskVariables]
                        obj.taskVariables.forEach((item: any, i: any) => {
                            const exist = tab.taskVariables.find((va: any) => va.paramName === item.paramName)
                            if (exist) {
                                varArr[i] = exist
                            }
                        })
                        obj.taskVariables = varArr
                    }
                    tab = assign(tab, obj);
                    tab.notSynced = true;
                    return tab;
                } else {
                    return tab;
                }
            });

            nextState = clone;
            break;
        }

        case workbenchAction.UPDATE_TASK_TAB: {
            const obj = action.payload;
            const clone = cloneDeep(state);

            clone.tabs = clone.tabs.map((tab: any) => {
                if (tab.id === obj.id) {
                    // 对任务变量做特殊处理, 合并2个数组
                    if (obj.taskVariables && tab.taskVariables && obj.taskVariables.length > 0 && tab.taskVariables.length > 0) {
                        const varArr: any = [...obj.taskVariables]
                        obj.taskVariables.forEach((item: any, i: any) => {
                            const exist = tab.taskVariables.find((va: any) => va.paramName === item.paramName)
                            if (exist) {
                                varArr[i] = exist
                            }
                        })
                        obj.taskVariables = varArr
                    }
                    tab = assign(tab, obj);
                    return tab;
                }
                return tab;
            });
            nextState = clone;
            break;
        }

        // 修改任务属性(代码编辑器)
        case workbenchAction.SET_TASK_FIELDS_VALUE_SILENT: {
            const obj = action.payload;

            // 故意不走immutable, 避免编辑器rerender导致光标回零
            state.tabs = state.tabs.map((tab: any) => {
                if (tab.id === state.currentTab) {
                    tab = assign(tab, obj);
                    tab.notSynced = true;
                    return tab;
                } else {
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

            clone.tabs = clone.tabs.map((tab: any) => {
                if (tab.id === clone.currentTab) {
                    tab = assign(tab, obj);
                    tab.notSynced = true;
                    return tab;
                } else {
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

        case workbenchAction.SAVE_DATASYNC_TO_TAB: {
            const index = state.tabs.findIndex((t: any) => t.id === action.payload.id);
            const newTabs: any = [...state.tabs];
            const data = action.payload.data ? action.payload.data : {};

            if (index > -1) {
                newTabs[index].dataSyncSaved = data;
                nextState = assign({}, state, {
                    tabs: newTabs
                });
            } else {
                nextState = state
            }

            break;
        }

        case workbenchAction.MAKE_TAB_DIRTY: {
            const clone = cloneDeep(state);

            clone.tabs = clone.tabs.map((tab: any) => {
                if (tab.id === clone.currentTab) {
                    tab.notSynced = true;
                    return tab;
                } else {
                    return tab;
                }
            });

            nextState = clone;
            break;
        }

        case workbenchAction.MAKE_TAB_CLEAN: {
            const clone = cloneDeep(state);

            clone.tabs = clone.tabs.map((tab: any) => {
                if (tab.id === clone.currentTab) {
                    tab.notSynced = undefined;
                    return tab;
                } else {
                    return tab;
                }
            });

            nextState = clone;
            break;
        }

        case workbenchAction.INIT_WORKBENCH: {
            if (action.payload) {
                return action.payload;
            }
            nextState = state;
            break;
        }
        default: {
            nextState = state;
            break;
        }
    }

    // localDb.set(`${projectID}_offline_workbench`, nextState);
    if (projectID && nextState) {
        idb.open().then((db) => {
            if (db) {
                idb.set(`${projectID}_offline_workbench`, nextState);
            }
        });
    }
    return nextState;
};
