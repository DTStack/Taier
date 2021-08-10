import configureMockStore from 'redux-mock-store';
import thunk from 'redux-thunk';

import {
    execSql
    // stopSql
} from '../editorAction';

import api from '../../../../api';

import {
    editorAction
} from '../actionTypes';

// import { TASK_TYPE, offlineTaskStatusFilter } from '../../../../comm/const'
// import { createLog, createTitle, createLinkMark } from 'dt-common/src/widgets/code-editor/utils'
// import { respWithoutPoll, errorResp, respWithPoll, pollRespCollection } from './mockData';

import { TASK_TYPE } from '../../../../comm/const'
import { createLog, createTitle } from 'dt-common/src/widgets/code-editor/utils'
import { respWithoutPoll, errorResp } from './mockData';

jest.mock('../../../../api');

const middlewares: any = [thunk]
const mockStore = configureMockStore(middlewares)
// function getStatusText (status: any) {
//     for (let i = 0; i < offlineTaskStatusFilter.length; i++) {
//         if (offlineTaskStatusFilter[i].value == status) {
//             return offlineTaskStatusFilter[i].text;
//         }
//     }
// }
describe('editor action', () => {
    let store = mockStore({
        console: {},
        running: []
    })

    const testSqls: any = ['select * from test;', 'show tables;'];
    beforeEach(() => {
        jest.resetAllMocks();
        store.clearActions();
    });
    function mockTaskWithoutPoll (isScript: any) {
        let currentTab = 666;
        let task: any = {
            id: 666
        };
        if (isScript) {
            task.type = TASK_TYPE.SQL;
            (api.execScript as any).mockResolvedValue(respWithoutPoll);
        } else {
            task.taskType = TASK_TYPE.SQL;
            (api.execSQLImmediately as any).mockResolvedValue(respWithoutPoll);
        }
        let params: any = {
            isCheckDDL: 0,
            projectId: 141,
            scriptId: 666
        };
        let sqls = testSqls.slice(0, 1)

        return execSql(currentTab, task, params, sqls)(store.dispatch).then((isComplete: any) => {
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
            if (isScript) {
                task.type = TASK_TYPE.SQL;
                (api.execScript as any).mockResolvedValue(errorResp);
            } else {
                task.taskType = TASK_TYPE.SQL;
                (api.execSQLImmediately as any).mockResolvedValue(errorResp);
            }
            return execSql(currentTab, task, params, sqls)(store.dispatch)
        }).then((isComplete: any) => {
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
    }
    // function mockMultipleTaskWithPoll (isScript?: any, testStop?: any) {
    //     let currentTab = 666;
    //     let task: any = {
    //         id: 666
    //     };
    //     if (isScript) {
    //         task.type = TASK_TYPE.SQL;
    //         (api.execScript as any).mockResolvedValueOnce(respWithPoll).mockResolvedValueOnce(respWithoutPoll);
    //     } else {
    //         task.taskType = TASK_TYPE.SQL;
    //         (api.execSQLImmediately as any).mockResolvedValueOnce(respWithPoll).mockResolvedValueOnce(respWithoutPoll);
    //     }
    //     let params: any = {
    //         isCheckDDL: 0,
    //         projectId: 141,
    //         scriptId: 666
    //     };
    //     if (testStop) {
    //         let mockFunc = pollRespCollection.slice(0, pollRespCollection.length - 1).reduce((mockFunc: any, value: any) => {
    //             return mockFunc.mockResolvedValueOnce(value)
    //         }, api.selectExecResultData)
    //         /**
    //          * 这边要延迟返回成功结果，测试停止效果
    //          */
    //         mockFunc.mockImplementationOnce(() => {
    //             return new Promise((resolve: any, reject: any) => {
    //                 stopSql(currentTab, task, true)(store.dispatch, () => {
    //                     return {
    //                         editor: {
    //                             running: [currentTab]
    //                         }
    //                     }
    //                 })
    //                 resolve(pollRespCollection[pollRespCollection.length - 1])
    //             })
    //         })
    //     } else {
    //         pollRespCollection.reduce((mockFunc: any, value: any) => {
    //             return mockFunc.mockResolvedValueOnce(value)
    //         }, api.selectExecResultData)
    //     }
    //     return execSql(currentTab, task, params, testSqls)(store.dispatch).then((isComplete: any) => {
    //         expect(isComplete).toBeTruthy();
    //         const allActions = store.getActions();
    //         let normalExpectActions: any = [{
    //             type: editorAction.ADD_LOADING_TAB,
    //             data: {
    //                 id: currentTab
    //             }
    //         }, {
    //             type: editorAction.APPEND_CONSOLE_LOG,
    //             data: createLog(`第1条任务开始执行`, 'info'),
    //             key: currentTab
    //         }, {
    //             type: editorAction.APPEND_CONSOLE_LOG,
    //             data: createLog(respWithPoll.message, 'error'),
    //             key: currentTab
    //         }, {
    //             type: editorAction.APPEND_CONSOLE_LOG,
    //             data: `${createTitle('任务信息')}\n${respWithPoll.data.sqlText}\n${createTitle('')}`,
    //             key: currentTab
    //         }, {
    //             type: editorAction.APPEND_CONSOLE_LOG,
    //             data: createLog(`${pollRespCollection[0].message}`, 'error'),
    //             key: currentTab
    //         }, {
    //             type: editorAction.APPEND_CONSOLE_LOG,
    //             data: createLog(`${getStatusText(pollRespCollection[0].data.status)}.....`, 'info'),
    //             key: currentTab
    //         }, {
    //             type: editorAction.APPEND_CONSOLE_LOG,
    //             data: createLog(`${getStatusText(pollRespCollection[1].data.status)}.....`, 'info'),
    //             key: currentTab
    //         }, {
    //             type: editorAction.APPEND_CONSOLE_LOG,
    //             data: createLog(`${pollRespCollection[2].message}`, 'error'),
    //             key: currentTab
    //         }, {
    //             type: editorAction.APPEND_CONSOLE_LOG,
    //             data: createLog(`${getStatusText(pollRespCollection[2].data.status)}.....`, 'info'),
    //             key: currentTab
    //         }, {
    //             type: editorAction.APPEND_CONSOLE_LOG,
    //             data: createLog('执行完成!', 'info'),
    //             key: currentTab
    //         }, {
    //             type: editorAction.UPDATE_RESULTS,
    //             data: { jobId: respWithPoll.data.jobId, data: pollRespCollection[3].data.result },
    //             key: currentTab
    //         }, {
    //             type: editorAction.APPEND_CONSOLE_LOG,
    //             data: `完整日志下载地址：${createLinkMark({ href: pollRespCollection[3].data.download, download: '' })}\n`,
    //             key: currentTab
    //         }, {
    //             type: editorAction.APPEND_CONSOLE_LOG,
    //             data: createLog(`第2条任务开始执行`, 'info'),
    //             key: currentTab
    //         }, {
    //             type: editorAction.APPEND_CONSOLE_LOG,
    //             data: createLog(respWithoutPoll.message, 'error'),
    //             key: currentTab
    //         }, {
    //             type: editorAction.APPEND_CONSOLE_LOG,
    //             data: `${createTitle('任务信息')}\n${respWithoutPoll.data.sqlText}\n${createTitle('')}`,
    //             key: currentTab
    //         }, {
    //             type: editorAction.APPEND_CONSOLE_LOG,
    //             data: createLog('执行完成!', 'info'),
    //             key: currentTab
    //         }, {
    //             type: editorAction.UPDATE_RESULTS,
    //             data: { jobId: undefined, data: respWithoutPoll.data.result },
    //             key: currentTab
    //         }, {
    //             type: editorAction.REMOVE_LOADING_TAB,
    //             data: {
    //                 id: currentTab
    //             }
    //         }]
    //         if (testStop) {
    //             normalExpectActions.splice(9, 99999, {
    //                 type: editorAction.APPEND_CONSOLE_LOG,
    //                 data: createLog('执行停止', 'warning'),
    //                 key: currentTab
    //             }, {
    //                 type: editorAction.REMOVE_LOADING_TAB,
    //                 data: {
    //                     id: currentTab
    //                 }
    //             }, {
    //                 type: editorAction.REMOVE_LOADING_TAB,
    //                 data: {
    //                     id: currentTab
    //                 }
    //             })
    //             expect(allActions).toEqual(normalExpectActions);
    //         } else {
    //             expect(allActions).toEqual(normalExpectActions);
    //         }
    //     })
    // }
    test('single script without poll', () => {
        return mockTaskWithoutPoll(true)
    })
    test('single job without poll', () => {
        return mockTaskWithoutPoll(false)
    })
    // test('multiple script with poll', () => {
    //     return mockMultipleTaskWithPoll(true)
    // })
    // test('multiple job with poll', () => {
    //     return mockMultipleTaskWithPoll(false)
    // })
    // test('stop job', () => {
    //     return mockMultipleTaskWithPoll(false, true)
    // })
});
