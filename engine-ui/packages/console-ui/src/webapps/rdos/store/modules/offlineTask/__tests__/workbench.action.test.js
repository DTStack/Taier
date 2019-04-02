import configureMockStore from 'redux-mock-store'; // eslint-disable-line
import thunk from 'redux-thunk';
import api from '../../../../api';
import { message } from 'antd';
import { MENU_TYPE } from '../../../../comm/const';
import {
    workbenchActions
} from '../offlineAction'

import {
    workbenchAction
} from '../actionType';

jest.mock('../../../../api');
const middlewares = [thunk]
const mockStore = configureMockStore(middlewares)
function $ (className) {
    return document.body.querySelectorAll(className);
}
describe('workbenchActions', () => {
    afterEach(() => {
        // 重置store
        jest.resetAllMocks();
        document.getElementsByTagName('html')[0].innerHTML = ''; // 清除dom元素
        store = mockStore({});
        actions = workbenchActions(store.dispatch);
    })
    let store = mockStore({});
    let actions = workbenchActions(store.dispatch);
    const task = { id: 10, name: 'testTask' };
    test('reloadTaskTab action', async () => {
        const response = { code: 1, data: task };
        api.getOfflineTaskDetail
            .mockResolvedValue(response)
            .mockResolvedValueOnce({ ...response, code: 0 })
            .mockResolvedValueOnce({ ...response, code: 1 })

        const expectedActions = [{
            type: workbenchAction.UPDATE_TASK_TAB,
            payload: response.data
        }];
        // 测试失败的情况
        await actions.reloadTaskTab();
        let nextActions = store.getActions();
        expect(nextActions).toEqual([]);
        // 测试成功的情况
        await actions.reloadTaskTab();
        nextActions = store.getActions();
        expect(nextActions).toEqual(expectedActions)
    })

    test('updateTabData', () => {
        const expectedActions = [{
            type: workbenchAction.UPDATE_TASK_TAB,
            payload: task
        }];
        actions.updateTabData(task);
        const nextActions = store.getActions();
        expect(nextActions).toEqual(expectedActions)
    })
    test('publishTask without submitStatus', () => {
        const expectedActions = [{
            type: workbenchAction.CHANGE_TASK_SUBMITSTATUS,
            payload: 1
        }, {
            type: workbenchAction.MAKE_TAB_CLEAN
        }]
        // 测试有入参但是没有submitStatus
        actions.publishTask({ data: {} });
        const nextActions = store.getActions();
        expect(nextActions).toEqual(expectedActions);
    })

    test('publishTask with submitStatus', () => {
        const submitStatus = 2;
        const expectedActions = [{
            type: workbenchAction.CHANGE_TASK_SUBMITSTATUS,
            payload: submitStatus
        }, {
            type: workbenchAction.MAKE_TAB_CLEAN
        }]
        // 测试有入参且存在submitStatus
        actions.publishTask({ data: { submitStatus } });
        const nextActions = store.getActions();
        expect(nextActions).toEqual(expectedActions);
    })

    test('updateTaskField', () => {
        const expectedActions = [{
            type: workbenchAction.SET_TASK_FIELDS_VALUE,
            payload: task
        }];
        actions.updateTaskField(task);
        const nextActions = store.getActions();
        expect(nextActions).toEqual(expectedActions)
    })

    test('updateDataSyncVariables', () => {
        /* eslint-disable */
        const sourceMap = {
            column: [{
                format: '',
                key: '${test.column}',
                type: 'string',
                value: 'values'
            }],
            type: {
                type: 1,
                where: 'pt=${test.where}',
                partition: 'pt=${test.partition}',
                path:['${test.path}']
            }
        }
        const targetMap = {
            type: {
                preSql: '${test.preSql}',
                postSql: '${test.postSql}',
                partition: 'pt=${bdp.system.premonth}',
            },
            path:['test.path']
        }
        /* eslint-enable */
        const taskCustomParams = [
            {
                'id': 4,
                'paramName': 'bdp.system.premonth',
                'paramCommand': 'yyyyMM-1',
                'isDeleted': 0
            }
        ]
        const payload = {
            taskVariables: [
                { 'paramName': 'test.column', 'paramCommand': '', 'type': 1 },
                { 'paramName': 'test.where', 'paramCommand': '', 'type': 1 },
                { 'paramName': 'test.partition', 'paramCommand': '', 'type': 1 },
                { 'paramName': 'test.postSql', 'paramCommand': '', 'type': 1 },
                { 'paramName': 'test.preSql', 'paramCommand': '', 'type': 1 },
                { 'paramName': 'test.path', 'paramCommand': '', 'type': 1 },
                { 'paramName': 'bdp.system.premonth', 'paramCommand': 'yyyyMM-1', 'type': 0 }
            ]
        };
        const expectedActions = [{
            type: workbenchAction.SET_TASK_FIELDS_VALUE,
            payload
        }];
        actions.updateDataSyncVariables(sourceMap, targetMap, taskCustomParams);
        const nextActions = store.getActions();
        // 分开测试是因为taskVariables的顺序不同也会影响测试结果
        expect(nextActions).toHaveLength(1);
        expect(nextActions[0].type).toEqual(expectedActions[0].type);
        expect(nextActions[0].payload.taskVariables).toEqual(expect.arrayContaining(expectedActions[0].payload.taskVariables))
    })

    test('openTaskInDev', async () => {
        const response = { code: 1, data: task };
        const id = 1;
        api.getOfflineTaskDetail
            .mockResolvedValue(response)
            .mockResolvedValueOnce({ ...response, code: 0 })
            .mockResolvedValueOnce({ ...response, code: 1 })
        const expectedActions = [{
            type: workbenchAction.LOAD_TASK_DETAIL,
            payload: response.data
        }, {
            type: workbenchAction.OPEN_TASK_TAB,
            payload: id
        }];
        // 测试失败的情况
        await actions.openTaskInDev(id);
        let nextActions = store.getActions();
        expect(nextActions).toEqual([]);
        // 测试成功的情况
        await actions.openTaskInDev(id);
        nextActions = store.getActions();
        expect(nextActions).toEqual(expectedActions)
    })

    test('saveTask success when lockStatus equals to 0', async () => {
        const data = {
            readWriteLockVO: {
                result: 0
            },
            name: 'test',
            version: 'v1.0.0'
        }
        const response = { code: 1, data };
        const argument = {
            id: 1,
            taskVersions: 'taskVersions',
            preSave: true,
            submitStatus: 0,
            taskVOS: ['taskVOS1', 'taskVOS2']
        }
        api.saveOfflineJobData
            .mockResolvedValue(response)
            .mockResolvedValueOnce({ ...response, code: 0 })
            .mockResolvedValueOnce({ ...response, code: 1 });
        const expectedActions = [{
            type: workbenchAction.UPDATE_TASK_TAB,
            payload: {
                id: argument.id,
                name: response.data.name,
                version: response.data.version,
                readWriteLockVO: response.data.readWriteLockVO
            }
        }]
        // 测试失败的情况
        await actions.saveTask(argument);
        let nextActions = store.getActions();
        expect(nextActions).toEqual([]);
        // 测试成功并且locakstatsu为0
        const messageSuccess = jest.spyOn(message, 'success').mockImplementation(() => { });
        await actions.saveTask(argument);
        nextActions = store.getActions();
        expect(nextActions).toEqual(expectedActions);
        expect(messageSuccess).toHaveBeenCalled();
        messageSuccess.mockReset(); // 重置mock的方法，测试noMsg参数是否有效
        await actions.saveTask(argument, true);
        expect(messageSuccess).not.toHaveBeenCalled();
    })

    test('saveTask success when lockStatus equals to 1', async () => {
        const data = {
            readWriteLockVO: {
                result: 1
            },
            name: 'test',
            version: 'v1.0.0'
        }
        const response = { code: 1, data };
        const argument = {
            id: 1,
            taskVersions: 'taskVersions',
            preSave: true,
            submitStatus: 0,
            taskVOS: ['taskVOS1', 'taskVOS2']
        }
        api.saveOfflineJobData
            .mockResolvedValue(response)
        api.forceUpdateOfflineTask
            .mockResolvedValue(response)
            .mockResolvedValueOnce({ ...response, code: 0 })
            .mockResolvedValueOnce({ ...response, code: 1 });
        const expectedActions = [{
            type: workbenchAction.UPDATE_TASK_TAB,
            payload: {
                id: argument.id,
                name: response.data.name,
                version: response.data.version,
                readWriteLockVO: response.data.readWriteLockVO
            }
        }]
        // 测试成功并且locakstatsu为1 但是forceUpdateOfflineTask请求失败
        await actions.saveTask(argument);
        $('.ant-btn-danger')[0].click()
        let nextActions = store.getActions();
        expect(api.forceUpdateOfflineTask).toHaveBeenCalled();
        expect(nextActions).toEqual([]);
        // 测试成功并且locakstatsu为1 并且forceUpdateOfflineTask请求成功
        await actions.saveTask(argument);
        $('.ant-btn-danger')[0].click()
        nextActions = store.getActions();
        expect(api.forceUpdateOfflineTask).toHaveBeenCalled();
        expect(nextActions).toEqual(expectedActions);
    })

    test('saveTask success when lockStatus equals to 2', async () => {
        const data = {
            readWriteLockVO: {
                result: 2
            },
            name: 'test',
            version: 'v1.0.0'
        }
        const response = { code: 1, data };
        const argument = {
            id: 1,
            taskVersions: 'taskVersions',
            preSave: true,
            submitStatus: 0,
            taskVOS: ['taskVOS1', 'taskVOS2']
        }
        api.saveOfflineJobData
            .mockResolvedValue(response);
        api.getOfflineTaskDetail
            .mockResolvedValue(response)
            .mockResolvedValueOnce({ ...response, code: 0 })
            .mockResolvedValueOnce({ ...response, code: 1 });
        const expectedActions = [{
            type: workbenchAction.UPDATE_TASK_TAB,
            payload: {
                id: argument.id,
                name: response.data.name,
                version: response.data.version,
                readWriteLockVO: response.data.readWriteLockVO
            }
        }]
        // 测试成功并且locakstatsu为2 但是getOfflineTaskDetail请求失败
        await actions.saveTask(argument);
        $('.ant-btn-danger')[0].click()
        let nextActions = store.getActions();
        expect(api.getOfflineTaskDetail).toHaveBeenCalled();
        expect(nextActions).toEqual([]);
        // 测试成功并且locakstatsu为2 并且getOfflineTaskDetail请求成功
        await actions.saveTask(argument);
        $('.ant-btn-danger')[0].click()
        nextActions = store.getActions();
        expect(api.getOfflineTaskDetail).toHaveBeenCalled();
        expect(nextActions).toEqual(expectedActions);
    })
    test('saveTab', () => {
        // todo
    })

    test('openTab when currentTab equals to id and id exists in tabs', async () => {
        const data = {
            id: 2,
            tabs: [{
                id: 2
            }],
            currentTab: 2
        }
        await actions.openTab(data);
        let nextActions = store.getActions();
        expect(nextActions).toEqual(expect.arrayContaining([]));
    })
    test('openTab when currentTab not equals to id and id exists in tabs', async () => {
        const data = {
            id: 2,
            tabs: [{
                id: 2
            }],
            currentTab: 2
        }
        let expectedActions = [{
            type: workbenchAction.OPEN_TASK_TAB,
            payload: data.id
        }]
        // isExist === true && id !== currentTab
        data.currentTab = 1;
        await actions.openTab(data);
        const nextActions = store.getActions();
        expect(nextActions).toEqual(expect.arrayContaining(expectedActions))
    })
    test('openTab when id not exists in tabs and there is no treeType', async () => {
        const data = {
            id: 1,
            tabs: [{
                id: 2
            }],
            currentTab: 2
        }
        const expectedActions = [{
            type: workbenchAction.LOAD_TASK_DETAIL,
            payload: task
        }]
        // res.code !== 1
        const response = { code: 1, data: task };
        api.getOfflineTaskDetail
            .mockResolvedValue(response)
            .mockResolvedValueOnce({ ...response, code: 0 })
            .mockResolvedValueOnce({ ...response, code: 1 });
        await actions.openTab(data);
        let nextActions = store.getActions();
        expect(nextActions).toEqual(expect.arrayContaining([]));
        // res.code === 1
        await actions.openTab(data);
        nextActions = store.getActions();
        expect(nextActions).toEqual(expect.arrayContaining(expectedActions));
    })
    test('openTab when id not exists in tabs and there is a treeType which not equals to MENU_TYPE.SCRIPT', async () => {
        const data = {
            id: 1,
            tabs: [{
                id: 2
            }],
            currentTab: 2,
            treeType: MENU_TYPE.SYSFUC
        }
        const response = { code: 1, data: task };
        api.getOfflineTaskDetail
            .mockResolvedValue(response);
        const expectedActions = [{
            type: workbenchAction.LOAD_TASK_DETAIL,
            payload: task
        }]
        await actions.openTab(data);
        const nextActions = store.getActions();
        expect(api.getOfflineTaskDetail).toHaveBeenCalled();
        expect(nextActions).toEqual(expect.arrayContaining(expectedActions));
    })
    test('openTab when id not exists in tabs and there is a treeType which equals to MENU_TYPE.SCRIPT', async () => {
        const data = {
            id: 1,
            tabs: [{
                id: 2
            }],
            currentTab: 2,
            treeType: MENU_TYPE.SCRIPT
        }
        const response = { code: 1, data: task };
        api.getScriptById
            .mockResolvedValue(response)
            .mockResolvedValueOnce({ ...response, code: 0 })
            .mockResolvedValueOnce({ ...response, code: 1 });
        const expectedActions = [{
            type: workbenchAction.LOAD_TASK_DETAIL,
            payload: task
        }]
        // res.code !== 1
        await actions.openTab(data);
        let nextActions = store.getActions();
        expect(api.getScriptById).toHaveBeenCalled();
        expect(nextActions).toEqual(expect.arrayContaining([]));
        // res.code === 1
        await actions.openTab(data);
        nextActions = store.getActions();
        expect(api.getScriptById).toHaveBeenCalled();
        expect(nextActions).toEqual(expect.arrayContaining(expectedActions));
    })
    test('closeTab when data is not dirty', () => {
        const parameters = {
            tabId: 1,
            tabs: [{
                id: 1,
                notSynced: false
            }]
        }
        const expectedActions = [{
            type: workbenchAction.CLOSE_TASK_TAB,
            payload: parameters.tabId
        }]
        store = mockStore({
            editor: {
                running: [3]
            }
        }); // mock state的数据，在stopSql任务中用到
        actions = workbenchActions(store.dispatch);
        actions.closeTab(parameters.tabId, parameters.tabs);
        const nextActions = store.getActions();
        expect(nextActions).toEqual(expectedActions)
    })
    test('closeTab when data is dirty', () => {
        const parameters = {
            tabId: 1,
            tabs: [{
                id: 1,
                notSynced: true
            }]
        }
        const expectedActions = [{
            type: workbenchAction.CLOSE_TASK_TAB,
            payload: parameters.tabId
        }]
        store = mockStore({
            editor: {
                running: [3]
            }
        }); // mock state的数据，在stopSql任务中用到
        actions = workbenchActions(store.dispatch);
        actions.closeTab(parameters.tabId, parameters.tabs);
        $('.ant-btn-primary')[0].click();
        const nextActions = store.getActions();
        expect(nextActions).toEqual(expectedActions)
    })
    test('closeAllorOthers when actions equal to ALL and all is clean', () => {
        store = mockStore({
            editor: {
                running: [1, 2]
            }
        }); // mock state的数据，在stopSql任务中用到
        actions = workbenchActions(store.dispatch);
        const parameters = {
            action: 'ALL',
            tabs: [{
                id: 1,
                notSynced: false
            }],
            currentTab: 1
        }
        const expectedActions = [{
            type: workbenchAction.CLOSE_ALL_TABS
        }]
        actions.closeAllorOthers(parameters.action, parameters.tabs, parameters.currentTab);
        const nextActions = store.getActions();
        expect(nextActions).toEqual(expect.arrayContaining(expectedActions))
    })
    test('closeAllorOthers when actions equal to OTHER and all is clean', () => {
        store = mockStore({
            editor: {
                running: [1, 2]
            }
        }); // mock state的数据，在stopSql任务中用到
        actions = workbenchActions(store.dispatch);
        const parameters = {
            action: 'OTHER',
            tabs: [{
                id: 1,
                notSynced: false
            }, {
                id: 2,
                notSynced: false
            }],
            currentTab: 1
        }
        const expectedActions = [{
            type: workbenchAction.CLOSE_OTHER_TABS,
            payload: parameters.currentTab
        }]
        actions.closeAllorOthers(parameters.action, parameters.tabs, parameters.currentTab);
        const nextActions = store.getActions();
        expect(nextActions).toEqual(expect.arrayContaining(expectedActions))
    })
    test('closeAllorOthers when actions equal to ALL and all is not clean', () => {
        store = mockStore({
            editor: {
                running: [1, 2]
            }
        }); // mock state的数据，在stopSql任务中用到
        actions = workbenchActions(store.dispatch);
        const parameters = {
            action: 'ALL',
            tabs: [{
                id: 1,
                notSynced: true
            }, {
                id: 2,
                notSynced: false
            }],
            currentTab: 1
        }
        const expectedActions = [{
            type: workbenchAction.CLOSE_ALL_TABS
        }]
        actions.closeAllorOthers(parameters.action, parameters.tabs, parameters.currentTab);
        $('.ant-btn-primary')[0].click();
        const nextActions = store.getActions();
        expect(nextActions).toEqual(expect.arrayContaining(expectedActions))
    })
    test('closeAllorOthers when actions equal to OTHER and all is not clean', () => {
        store = mockStore({
            editor: {
                running: [1, 2]
            }
        }); // mock state的数据，在stopSql任务中用到
        actions = workbenchActions(store.dispatch);
        const parameters = {
            action: 'OTHER',
            tabs: [{
                id: 1,
                notSynced: true
            }, {
                id: 2,
                notSynced: false
            }],
            currentTab: 2
        }
        const expectedActions = [{
            type: workbenchAction.CLOSE_OTHER_TABS,
            payload: parameters.currentTab
        }]
        actions.closeAllorOthers(parameters.action, parameters.tabs, parameters.currentTab);
        $('.ant-btn-primary')[0].click();
        const nextActions = store.getActions();
        expect(nextActions).toEqual(expect.arrayContaining(expectedActions))
    })
    test('delOfflineTask', async () => {
        const parameters = {
            params: {},
            nodePid: 1
        }
        const response = { code: 1, data: task };
        api.delOfflineTask
            .mockResolvedValue(response)
            .mockResolvedValueOnce({ ...response, code: 0 })
            .mockResolvedValueOnce({ ...response, code: 1 })
        const expectedActions = [{
            type: workbenchAction.CLOSE_TASK_TAB,
            payload: response.data
        }];
        // 测试失败的情况
        await actions.delOfflineTask(parameters.params, parameters.nodePid);
        let nextActions = store.getActions();
        expect(nextActions).toEqual([]);
        // 测试成功的情况
        await actions.delOfflineTask(parameters.params, parameters.nodePid);
        nextActions = store.getActions();
        expect(nextActions).toEqual(expect.arrayContaining(expectedActions));
    })

    test('delOfflineScript', async () => {
        const parameters = {
            params: {
                scriptId: 1
            },
            nodePid: 1
        }
        const response = { code: 1, data: task };
        api.deleteScript
            .mockResolvedValue(response)
            .mockResolvedValueOnce({ ...response, code: 0 })
            .mockResolvedValueOnce({ ...response, code: 1 })
        const expectedActions = [{
            type: workbenchAction.CLOSE_TASK_TAB,
            payload: parameters.params.scriptId
        }];
        // 测试失败的情况
        await actions.delOfflineScript(parameters.params, parameters.nodePid);
        let nextActions = store.getActions();
        expect(nextActions).toEqual([]);
        // 测试成功的情况
        await actions.delOfflineScript(parameters.params, parameters.nodePid);
        nextActions = store.getActions();
        expect(nextActions).toEqual(expect.arrayContaining(expectedActions));
    })

    test('loadTaskParams', async () => {
        const response = { code: 1, data: task };
        api.getCustomParams
            .mockResolvedValue(response)
            .mockResolvedValueOnce({ ...response, code: 0 })
            .mockResolvedValueOnce({ ...response, code: 1 })
        const expectedActions = [{
            type: workbenchAction.LOAD_TASK_CUSTOM_PARAMS,
            payload: response.data
        }];
        // 测试失败的情况
        await actions.loadTaskParams();
        let nextActions = store.getActions();
        expect(nextActions).toEqual([]);
        // 测试成功的情况
        await actions.loadTaskParams();
        nextActions = store.getActions();
        expect(nextActions).toEqual(expectedActions);
    })
})
