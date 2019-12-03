import {
    workbenchAction
} from '../actionType';

import { workbenchReducer } from '../workbench';
describe('workbench reducer', () => {
    const initial = () => {
        const obj: any = {
            tabs: [],
            currentTab: undefined,
            isCurrentTabNew: undefined,
            taskCustomParams: []
        }
        return obj
    }
    let initialState = initial();
    afterEach(() => {
        // 在执行完每一个test后重置state
        initialState = initial();
    });

    /**
     * @description 测试初始化State
     */
    test('default', () => {
        expect(workbenchReducer(undefined, {})).toEqual(initialState);
    })

    /**
     * @description 测试加载任务详情
     */
    test('LOAD_TASK_DETAIL', () => {
        // 当前state中不存在需要加载的task
        let payload: any = { id: 1, name: 'test' };
        let nextState = workbenchReducer(initialState, {
            type: workbenchAction.LOAD_TASK_DETAIL,
            payload
        })
        expect(nextState.tabs).toContain(payload);
        expect(nextState.currentTab).toEqual(payload.id);
        // 当前state中已存在需要加载的task
        payload = { id: 1, name: 'test_2' };
        nextState = workbenchReducer(nextState, {
            type: workbenchAction.LOAD_TASK_DETAIL,
            payload
        })
        expect(nextState.tabs).toContain(payload);
        expect(nextState.currentTab).toEqual(payload.id);
    })
    /**
     * @description
     */
    test('LOAD_TASK_CUSTOM_PARAMS', () => {
        const payload: any = { name: 'test' };
        let nextState = workbenchReducer(initialState, {
            type: workbenchAction.LOAD_TASK_CUSTOM_PARAMS,
            payload
        })
        expect(nextState).toEqual({
            ...initialState,
            taskCustomParams: payload,
            showPanel: true
        });
    })
    /**
     * @description
     */
    test('OPEN_TASK_TAB', () => {
        const payload = 1;
        let nextState = workbenchReducer(initialState, {
            type: workbenchAction.OPEN_TASK_TAB,
            payload
        })
        expect(nextState).toEqual({
            ...initialState,
            currentTab: payload
        });
    })
    /**
     * @description 测试关闭某一个任务
     */
    test('CLOSE_TASK_TAB', () => {
        // 当前state中不存在需要关闭的task
        let payload: any = { id: 3 };
        initialState.tabs = [{ id: 2 }, { id: 10 }];
        initialState.currentTab = 10;
        // 需要关闭的tabid不在tabs中
        let nextState = workbenchReducer(initialState, {
            type: workbenchAction.CLOSE_TASK_TAB,
            payload: payload.id
        })
        expect(nextState).toEqual(initialState);
        // 当前state中存在需要关闭的task，并且就是当前的task
        payload = { id: 10 };
        nextState = workbenchReducer(nextState, {
            type: workbenchAction.CLOSE_TASK_TAB,
            payload: payload.id
        })
        expect(nextState.tabs).not.toContain(payload);
        expect(nextState.currentTab).toEqual(2);
        // 当前state的tabs中只有一个tab
        payload = { id: 2 };
        nextState = workbenchReducer(nextState, {
            type: workbenchAction.CLOSE_TASK_TAB,
            payload: payload.id
        })
        expect(nextState.tabs).not.toContain(payload);
        expect(nextState.currentTab).toEqual(2);
    })
    /**
     * @description 测试关闭所有任务
     */
    test('CLOSE_ALL_TABS', () => {
        let nextState = workbenchReducer(initialState, {
            type: workbenchAction.CLOSE_ALL_TABS
        })
        expect(nextState.tabs).toEqual([]);
        expect(nextState.currentTab).toBeUndefined()
    })
    /**
     * @description 测试关闭其他任务
     */
    test('CLOSE_OTHER_TABS', () => {
        let payload: any = { id: 1 };
        initialState.tabs = [{ id: 1 }, { id: 2 }];
        initialState.currentTab = 1;
        let nextState = workbenchReducer(initialState, {
            type: workbenchAction.CLOSE_OTHER_TABS,
            payload: payload.id
        })
        expect(nextState.tabs).toEqual([payload]);
        expect(nextState.currentTab).toEqual(payload.id);
    })
    /**
     * @description 测试修改工作流的调度依赖配置
     */
    test('CHANGE_SCHEDULE_CONF', () => {
        initialState.tabs = [{
            id: 1,
            scheduleConf: '{"min":"0","hour":"11","periodType":"2","beginDate":"2001-01-01","endDate":"2121-01-01","selfReliance":0,"isFailRetry":true,"maxRetryNum":"3"}',
            notSynced: false
        }, {
            id: 2,
            scheduleConf: '{"min":"0","hour":"11","periodType":"2","beginDate":"2001-01-01","endDate":"2121-01-01","selfReliance":0,"isFailRetry":true,"maxRetryNum":"3"}',
            notSynced: false
        }];
        initialState.currentTab = 1;
        // 测试调度周期为周
        let scheduleConf: any = {
            'weekDay': [3, 4, 5],
            'min': 0,
            'hour': 23,
            'periodType': '3',
            'scheduleStatus': false,
            'beginDate': '2001-01-01',
            'endDate': '2121-01-01',
            'selfReliance': 0
        }
        let nextState = workbenchReducer(initialState, {
            type: workbenchAction.CHANGE_SCHEDULE_CONF,
            payload: scheduleConf
        })
        expect(nextState.tabs[0].scheduleConf).toEqual(JSON.stringify(scheduleConf));
        expect(nextState.tabs[0].notSynced).toBe(true);
        // 测试调度周期为月
        scheduleConf = {
            'day': [5, 6],
            'hour': '0',
            'min': '23',
            'periodType': '4',
            'beginDate': '2001-01-01',
            'endDate': '2121-01-01',
            'selfReliance': 0,
            'isFailRetry': true,
            'maxRetryNum': 3
        }
        nextState = workbenchReducer(nextState, {
            type: workbenchAction.CHANGE_SCHEDULE_CONF,
            payload: scheduleConf
        })
        expect(nextState.tabs[0].scheduleConf).toEqual(JSON.stringify(scheduleConf));
        expect(nextState.tabs[0].notSynced).toBe(true);
        expect(nextState.tabs[1]).toEqual(initialState.tabs[1]); // 不是当前的tab不变
    })
    /**
     * @description 修改工作流调度依赖的调度状态
     */
    test('CHANGE_SCHEDULE_STATUS', () => {
        initialState.tabs = [{
            id: 1,
            scheduleStatus: 1,
            notSynced: false
        }, {
            id: 2,
            scheduleStatus: 1,
            notSynced: false
        }];
        initialState.currentTab = 1;
        let nextState = workbenchReducer(initialState, {
            type: workbenchAction.CHANGE_SCHEDULE_STATUS,
            payload: 2
        })
        expect(nextState.tabs[0].scheduleStatus).toBe(2);
        expect(nextState.tabs[0].notSynced).toBe(true);
        expect(nextState.tabs[1]).toEqual(initialState.tabs[1]); // 不是当前的tab不变
    })
    /**
     * @description 测试提交任务
     */
    test('CHANGE_TASK_SUBMITSTATUS', () => {
        initialState.tabs = [{
            id: 1,
            submitStatus: 0,
            notSynced: true
        }, {
            id: 2,
            submitStatus: 0,
            notSynced: true
        }];
        initialState.currentTab = 1;
        let nextState = workbenchReducer(initialState, {
            type: workbenchAction.CHANGE_TASK_SUBMITSTATUS,
            payload: 1
        })
        expect(nextState.tabs[0].submitStatus).toBe(1);
        expect(nextState.tabs[0].notSynced).toBe(false);
        expect(nextState.tabs[1]).toEqual(initialState.tabs[1]); // 不是当前的tab不变
    })
    /**
     * @description 测试sql任务的自动推荐
     */
    test('ADD_VOS', () => {
        initialState.tabs = [{
            id: 1,
            taskVOS: [],
            notSynced: false
        }, {
            id: 2,
            taskVOS: [],
            notSynced: false
        }];
        initialState.currentTab = 1;
        // 测试自动推荐成功
        let payload: any = {
            'id': 1,
            'taskType': 2,
            'name': 'multi_part'
        };
        let nextState = workbenchReducer(initialState, {
            type: workbenchAction.ADD_VOS,
            payload
        })
        expect(nextState.tabs[0].taskVOS).toContainEqual(payload);
        expect(nextState.tabs[0].notSynced).toBe(true);
        // 测试依赖任务存在
        let anotherState = workbenchReducer(nextState, {
            type: workbenchAction.ADD_VOS,
            payload
        })
        expect(anotherState).toEqual(nextState);
        expect(anotherState.tabs[1]).toEqual(initialState.tabs[1]); // 不是当前的tab不变
    })
    /**
     * @description 测试删除sql任务的自动推荐
     */
    test('DEL_VOS', () => {
        initialState.tabs = [{
            id: 1,
            taskVOS: [{
                'id': 1,
                'taskType': 2,
                'name': 'multi_part'
            }],
            notSynced: false
        }, {
            id: 2,
            taskVOS: [],
            notSynced: false
        }];
        initialState.currentTab = 1;
        let nextState = workbenchReducer(initialState, {
            type: workbenchAction.DEL_VOS,
            payload: 1
        })
        expect(nextState.tabs[0].taskVOS).toEqual([]);
        expect(nextState.tabs[0].notSynced).toBe(true);
        expect(nextState.tabs[1]).toEqual(initialState.tabs[1]); // 不是当前的tab不变
    })
    /**
     * @description 测试修改任务属性
     */
    test('SET_TASK_FIELDS_VALUE', () => {
        initialState.tabs = [{
            id: 1,
            sqlText: `test`,
            taskVariables: [{
                paramName: 'test',
                paramCommand: 'test',
                type: 1
            }, {
                paramName: 'test_2',
                paramCommand: 'test_2',
                type: 1
            }],
            notSynced: false
        }, {
            id: 2,
            sqlText: '',
            taskVariables: [],
            notSynced: false
        }];
        initialState.currentTab = 1;
        const payload: any = {
            sqlText: 'sqlText',
            taskVariables: [{
                paramName: 'test',
                paramCommand: 'nextTest',
                type: 1
            }, {
                paramName: 'testName',
                paramCommand: 'testValue',
                type: 1
            }]
        };
        let nextState = workbenchReducer(initialState, {
            type: workbenchAction.SET_TASK_FIELDS_VALUE,
            payload
        })
        expect(nextState.tabs[0].sqlText).toEqual(payload.sqlText);
        expect(nextState.tabs[0].taskVariables).toEqual(expect.arrayContaining([
            {
                paramName: 'test',
                paramCommand: 'test',
                type: 1
            }, {
                paramName: 'testName',
                paramCommand: 'testValue',
                type: 1
            }
        ]));
        expect(nextState.tabs[0].notSynced).toBe(true);
        expect(nextState.tabs[1]).toEqual(initialState.tabs[1]); // 不是当前的tab不变
    })
    /**
     * @description 测试修改任务属性
     */
    test('UPDATE_TASK_TAB', () => {
        initialState.tabs = [{
            id: 1,
            sqlText: `test`,
            taskVariables: [{
                paramName: 'test',
                paramCommand: 'test',
                type: 1
            }, {
                paramName: 'test_2',
                paramCommand: 'test_2',
                type: 1
            }],
            notSynced: false
        }, {
            id: 2,
            sqlText: '',
            taskVariables: [],
            notSynced: false
        }];
        initialState.currentTab = 1;
        const payload: any = {
            id: 1,
            sqlText: 'sqlText',
            taskVariables: [{
                paramName: 'test',
                paramCommand: 'nextTest',
                type: 1
            }, {
                paramName: 'testName',
                paramCommand: 'testValue',
                type: 1
            }]
        };
        let nextState = workbenchReducer(initialState, {
            type: workbenchAction.UPDATE_TASK_TAB,
            payload
        })
        expect(nextState.tabs[0].sqlText).toEqual(payload.sqlText);
        expect(nextState.tabs[0].taskVariables).toEqual(expect.arrayContaining([
            {
                paramName: 'test',
                paramCommand: 'test',
                type: 1
            }, {
                paramName: 'testName',
                paramCommand: 'testValue',
                type: 1
            }
        ]));
        expect(nextState.tabs[0].notSynced).toBe(false);
        expect(nextState.tabs[1]).toEqual(initialState.tabs[1]); // 不是当前的tab不变
    })
    test('SET_TASK_FIELDS_VALUE_SILENT', () => {
        initialState.tabs = [{
            id: 1,
            sqlText: 'test',
            notSynced: false
        }, {
            id: 2,
            sqlText: 'test',
            notSynced: false
        }]
        initialState.currentTab = 1;
        const payload: any = {
            sqlText: 'fieldsValuesSilent'
        }
        let nextState = workbenchReducer(initialState, {
            type: workbenchAction.SET_TASK_FIELDS_VALUE_SILENT,
            payload
        })
        expect(nextState.tabs[0].sqlText).toEqual(payload.sqlText);
        expect(nextState.tabs[0].notSynced).toBe(true);
        expect(nextState.tabs[1]).toEqual(initialState.tabs[1]); // 不是当前的tab不变
    })
    /**
     * @description 测试修改sql任务的值
     */
    test('SET_TASK_SQL_FIELD_VALUE', () => {
        initialState.tabs = [{
            id: 1,
            sqlText: 'test',
            notSynced: false
        }, {
            id: 2,
            sqlText: 'test',
            notSynced: false
        }]
        initialState.currentTab = 1;
        const payload: any = {
            sqlText: 'sqlText'
        }
        let nextState = workbenchReducer(initialState, {
            type: workbenchAction.SET_TASK_SQL_FIELD_VALUE,
            payload
        })
        expect(nextState.tabs[0].sqlText).toEqual(payload.sqlText);
        expect(nextState.tabs[0].notSynced).toBe(true);
        expect(nextState.tabs[1]).toEqual(initialState.tabs[1]); // 不是当前的tab不变
    })
    /**
     * @description
     */
    test('SET_CURRENT_TAB_NEW', () => {
        let nextState = workbenchReducer(initialState, {
            type: workbenchAction.SET_CURRENT_TAB_NEW
        })
        expect(nextState.isCurrentTabNew).toBe(true);
    })
    /**
     * @description
     */
    test('SET_CURRENT_TAB_SAVED', () => {
        let nextState = workbenchReducer(initialState, {
            type: workbenchAction.SET_CURRENT_TAB_SAVED
        })
        expect(nextState.isCurrentTabNew).toBeUndefined();
    })
    /**
     * @description 测试保存数据同步
     */
    test('SAVE_DATASYNC_TO_TAB', () => {
        initialState.tabs = [{
            id: 1,
            name: 'dataSync'
        }];
        let payload: any = {
            id: 1,
            data: {
                tabId: 1111
            }
        };
        const nextState = workbenchReducer(initialState, {
            type: workbenchAction.SAVE_DATASYNC_TO_TAB,
            payload
        })
        expect(nextState.tabs[0].dataSyncSaved).toEqual(payload.data);
        // 测试state中不存在传入的id
        payload.id = 2;
        const anotherState = workbenchReducer(nextState, {
            type: workbenchAction.SAVE_DATASYNC_TO_TAB,
            payload
        })
        expect(anotherState.tabs).toEqual(nextState.tabs);
    })
    test('MAKE_TAB_DIRTY', () => {
        initialState.tabs = [{
            id: 1,
            notSynced: false
        }, {
            id: 2,
            notSynced: false
        }]
        initialState.currentTab = 1;
        const nextState = workbenchReducer(initialState, {
            type: workbenchAction.MAKE_TAB_DIRTY
        })
        expect(nextState.tabs[0].notSynced).toBe(true);
        expect(nextState.tabs[1]).toEqual(initialState.tabs[1]);
    })
    test('MAKE_TAB_CLEAN', () => {
        initialState.tabs = [{
            id: 1,
            notSynced: true
        }, {
            id: 2,
            notSynced: false
        }]
        initialState.currentTab = 1;
        const nextState = workbenchReducer(initialState, {
            type: workbenchAction.MAKE_TAB_CLEAN
        })
        expect(nextState.tabs[0].notSynced).toBeFalsy();
        expect(nextState.tabs[1]).toEqual(initialState.tabs[1]);
    })
})
