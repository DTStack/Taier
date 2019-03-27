import {
    workbenchAction
} from '../actionType';

import { workbenchReducer } from '../workbench';
expect.extend({
    toBeEmptyArray (received) {
        if (Array.isArray(received) && received.length === 0) {
            return {
                message: () =>
                    `expected is an empty array`,
                pass: true
            }
        } else {
            return {
                message: () =>
                    `expected is not an empty array`,
                pass: false
            }
        }
    }
})
describe('workbench reducer', () => {
    const initial = () => {
        return {
            tabs: [],
            currentTab: undefined,
            isCurrentTabNew: undefined,
            taskCustomParams: []
        };
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
        let payload = { id: 1, name: 'test' };
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
     * @description 测试关闭某一个任务
     */
    test('CLOSE_TASK_TAB', () => {
        // 当前state中不存在需要关闭的task
        let payload = { id: 3 };
        initialState.tabs = [{ id: 1 }, { id: 2 }, { id: 10 }];
        initialState.currentTab = 1;
        let nextState = workbenchReducer(initialState, {
            type: workbenchAction.CLOSE_TASK_TAB,
            payload: payload.id
        })
        expect(nextState).toEqual(initialState);
        // 当前state中存在需要关闭的task，并且就是当前的task
        payload = { id: 1 };
        nextState = workbenchReducer(nextState, {
            type: workbenchAction.CLOSE_TASK_TAB,
            payload: payload.id
        })
        expect(nextState.tabs).not.toContain(payload);
        expect(nextState.currentTab).toEqual(2);
        // 当前state中存在需要关闭的task，却不是当前的task
        payload = { id: 10 };
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
            type: workbenchAction.CLOSE_TASK_TAB
        })
        expect(nextState.tabs).toBeEmptyArray();
        expect(nextState.currentTab).toBeUndefined()
    })
    /**
     * @description 测试关闭其他任务
     */
    test('CLOSE_OTHER_TABS', () => {
        let payload = { id: 1 };
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
        let scheduleConf = {
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
            'day': '5,6',
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
})
