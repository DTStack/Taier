import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import {
    execSql,
    stopSql
} from '../editorAction';

import api from '../../../../api';

import {
    editorAction
} from '../actionTypes';

import { TASK_TYPE } from '../../../../comm/const'
import { createLog, createTitle } from 'widgets/code-editor/utils'

jest.mock('../../../../api');

const middlewares = [thunk]
const mockStore = configureMockStore(middlewares)
describe('editor action', () => {
    let store = mockStore({
        console: {},
        running: []
    })
    let respWithoutPoll = {
        code: 1,
        data: {
            'result': [
                [
                    'database',
                    'tableName',
                    'isTemporary'
                ],
                [
                    'mufeng_0129',
                    'ads_ct_ks_bank_card_info',
                    false
                ]
            ],
            'jobId': null,
            'sqlText': 'show tables'
        },
        message: 'error-text',
        space: 348
    };
    let errorResp = {
        code: 2,
        data: null,
        message: 'error-text',
        space: 348
    };
    const testSqls = ['select * from test;', 'show tables;'];
    beforeEach(() => {
        jest.resetAllMocks();
    });
    test('single script without poll', () => {
        let currentTab = 666;
        let task = {
            id: 666,
            type: TASK_TYPE.SQL
        };
        let params = {
            isCheckDDL: 0,
            projectId: 141,
            scriptId: 666
        };
        let sqls = testSqls.slice(0, 1)
        api.execScript.mockResolvedValue(respWithoutPoll);
        return execSql(currentTab, task, params, sqls)(store.dispatch).then((isComplete) => {
            expect(isComplete).toBeTruthy();
            const allActions = store.getActions();
            expect(allActions).toEqual([{
                type: editorAction.ADD_LOADING_TAB,
                data: {
                    id: currentTab
                }
            }, {
                type: editorAction.APPEND_CONSOLE_LOG,
                data: createLog(`第1条任务开始执行`, 'info'),
                key: currentTab
            }, {
                type: editorAction.APPEND_CONSOLE_LOG,
                data: createLog(respWithoutPoll.message, 'error'),
                key: currentTab
            }, {
                type: editorAction.APPEND_CONSOLE_LOG,
                data: `${createTitle('任务信息')}\n${respWithoutPoll.data.sqlText}\n${createTitle('')}`,
                key: currentTab
            }, {
                type: editorAction.APPEND_CONSOLE_LOG,
                data: createLog('执行完成!', 'info'),
                key: currentTab
            }, {
                type: editorAction.UPDATE_RESULTS,
                data: { jobId: undefined, data: respWithoutPoll.data.result },
                key: currentTab
            }, {
                type: editorAction.REMOVE_LOADING_TAB,
                data: {
                    id: currentTab
                }
            }]);
        }).then(() => {
            store.clearActions();
            api.execScript.mockResolvedValue(errorResp);
            return execSql(currentTab, task, params, sqls)(store.dispatch)
        }).then((isComplete) => {
            expect(isComplete).toBeTruthy();
            const allActions = store.getActions();
            expect(allActions).toEqual([{
                type: editorAction.ADD_LOADING_TAB,
                data: {
                    id: currentTab
                }
            }, {
                type: editorAction.APPEND_CONSOLE_LOG,
                data: createLog(`第1条任务开始执行`, 'info'),
                key: currentTab
            }, {
                type: editorAction.APPEND_CONSOLE_LOG,
                data: createLog(respWithoutPoll.message, 'error'),
                key: currentTab
            }, {
                type: editorAction.APPEND_CONSOLE_LOG,
                data: createLog(`请求异常！`, 'error'),
                key: currentTab
            }, {
                type: editorAction.REMOVE_LOADING_TAB,
                data: {
                    id: currentTab
                }
            }]);
        })
    })
    test('multiple script', () => {

    })
});
