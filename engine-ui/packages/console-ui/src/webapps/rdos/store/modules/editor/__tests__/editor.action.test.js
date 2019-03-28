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

import { TASK_TYPE, offlineTaskStatusFilter } from '../../../../comm/const'
import { createLog, createTitle, createLinkMark } from 'widgets/code-editor/utils'
import { respWithoutPoll, errorResp, respWithPoll, pollRespCollection } from './mockData';

jest.mock('../../../../api');

const middlewares = [thunk]
const mockStore = configureMockStore(middlewares)
function getStatusText (status) {
    for (let i = 0; i < offlineTaskStatusFilter.length; i++) {
        if (offlineTaskStatusFilter[i].value == status) {
            return offlineTaskStatusFilter[i].text;
        }
    }
}
describe('editor action', () => {
    let store = mockStore({
        console: {},
        running: []
    })

    const testSqls = ['select * from test;', 'show tables;'];
    beforeEach(() => {
        jest.resetAllMocks();
        store.clearActions();
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
    test('multiple script with poll', () => {
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
        api.execScript.mockResolvedValueOnce(respWithPoll).mockResolvedValueOnce(respWithoutPoll);
        pollRespCollection.reduce((mockFunc, value) => {
            return mockFunc.mockResolvedValueOnce(value)
        }, api.selectExecResultData)
        return execSql(currentTab, task, params, [testSqls[1]])(store.dispatch).then((isComplete) => {
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
                data: createLog(respWithPoll.message, 'error'),
                key: currentTab
            }, {
                type: editorAction.APPEND_CONSOLE_LOG,
                data: `${createTitle('任务信息')}\n${respWithPoll.data.sqlText}\n${createTitle('')}`,
                key: currentTab
            }, {
                type: editorAction.APPEND_CONSOLE_LOG,
                data: createLog(`${pollRespCollection[0].message}`, 'error'),
                key: currentTab
            }, {
                type: editorAction.APPEND_CONSOLE_LOG,
                data: createLog(`${getStatusText(pollRespCollection[0].data.status)}.....`, 'info'),
                key: currentTab
            }, {
                type: editorAction.APPEND_CONSOLE_LOG,
                data: createLog(`${getStatusText(pollRespCollection[1].data.status)}.....`, 'info'),
                key: currentTab
            }, {
                type: editorAction.APPEND_CONSOLE_LOG,
                data: createLog(`${pollRespCollection[2].message}`, 'error'),
                key: currentTab
            }, {
                type: editorAction.APPEND_CONSOLE_LOG,
                data: createLog(`${getStatusText(pollRespCollection[2].data.status)}.....`, 'info'),
                key: currentTab
            }, {
                type: editorAction.APPEND_CONSOLE_LOG,
                data: createLog('执行完成!', 'info'),
                key: currentTab
            }, {
                type: editorAction.UPDATE_RESULTS,
                data: { jobId: respWithPoll.data.jobId, data: pollRespCollection[3].data.result },
                key: currentTab
            }, {
                type: editorAction.APPEND_CONSOLE_LOG,
                data: `完整日志下载地址：${createLinkMark({ href: pollRespCollection[3].data.download, download: '' })}\n`,
                key: currentTab
            }, {
                type: editorAction.REMOVE_LOADING_TAB,
                data: {
                    id: currentTab
                }
            }]);
        })
    })
});
