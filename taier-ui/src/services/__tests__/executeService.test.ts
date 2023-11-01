import { waitFor } from '@testing-library/react';
import md5 from 'md5';

import api from '@/api';
import notification from '@/components/notification';
import type { CatalogueDataProps, IOfflineTaskProps } from '@/interface';
import ExecuteService, { EXECUTE_EVENT } from '../executeService';
import taskResultService from '../taskResultService';

jest.mock('../taskResultService', () => ({
    clearLogs: jest.fn(),
    appendLogs: jest.fn(),
    createLog: jest.fn((log, type) => `<${type}> ${log}`),
    createTitle: jest.fn((title) => title),
    setResult: jest.fn(),
}));
jest.mock('@/api');
jest.mock('@/services', () => ({}));
jest.mock('@/components/notification');

jest.useFakeTimers();

const joinLogs = (fn: jest.Mock) => fn.mock.calls.map((call) => call.join('\n')).join('\n');

describe('Test ExecuteService', () => {
    it('Should filled with some default value in constructor', () => {
        const executeService = new ExecuteService();

        expect(executeService.getState()).toEqual({});
        // @ts-ignore
        expect(executeService.stopSign).toBeInstanceOf(Map);
        // @ts-ignore
        expect(executeService.stopSign.size).toBe(0);

        // @ts-ignore
        expect(executeService.runningSql).toBeInstanceOf(Map);
        // @ts-ignore
        expect(executeService.runningSql.size).toBe(0);

        // @ts-ignore
        expect(executeService.intervalsStore).toBeInstanceOf(Map);
        // @ts-ignore
        expect(executeService.intervalsStore.size).toBe(0);
    });

    it('Should execSql failed without taskType', () => {
        (taskResultService.clearLogs as jest.Mock).mockReset();
        (taskResultService.appendLogs as jest.Mock).mockReset();

        const executeService = new ExecuteService();

        executeService.execSql(1, { id: 1 } as CatalogueDataProps & IOfflineTaskProps, {}, ['show tables']);

        // 1. set currentTabId into stopSign before start
        // @ts-ignore
        expect(executeService.stopSign.get(1)).toBe(false);
        // 2. emit onStartRun event handler
        expect(executeService.emit).toBeCalledWith(EXECUTE_EVENT.onStartRun, 1);
        // 3. clear logs
        expect(taskResultService.clearLogs).toBeCalledWith('1');
        // 4. append start sign log
        expect(joinLogs(taskResultService.appendLogs as jest.Mock)).toBe('1\n<info> 第1条任务开始执行');
    });

    it('Should execSql failed since request failed', async () => {
        (taskResultService.clearLogs as jest.Mock).mockReset();
        (taskResultService.appendLogs as jest.Mock).mockReset();
        (api.execSQLImmediately as jest.Mock).mockReset().mockResolvedValue({
            code: 101,
            message: 'invalid request',
        });

        const executeService = new ExecuteService();

        executeService.execSql(1, { id: 1, taskType: 0 } as CatalogueDataProps & IOfflineTaskProps, {}, [
            'show tables',
        ]);

        await waitFor(() => {
            expect(api.execSQLImmediately).toBeCalledWith(
                expect.objectContaining({ isEnd: true, sql: 'show tables', taskId: 1 })
            );
        });

        // 1. log
        expect(joinLogs(taskResultService.appendLogs as jest.Mock)).toBe(
            '1\n<info> 第1条任务开始执行\n1\n<error> invalid request\n1\n<error> 请求异常！'
        );

        await waitFor(() => {
            // 2. emit onEndRun event handler
            expect((executeService.emit as jest.Mock).mock.calls[1]).toEqual([EXECUTE_EVENT.onEndRun, 1]);
        });
    });

    it('Should execSql successful without jobId', async () => {
        (taskResultService.clearLogs as jest.Mock).mockReset();
        (taskResultService.appendLogs as jest.Mock).mockReset();
        (api.execSQLImmediately as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
            data: { sqlText: 'test', msg: 'success', status: 5, result: [] },
        });

        const executeService = new ExecuteService();

        executeService.execSql(1, { id: 1, taskType: 0 } as CatalogueDataProps & IOfflineTaskProps, {}, [
            'show tables',
        ]);

        await waitFor(() => {
            expect(api.execSQLImmediately).toBeCalled();
        });

        // 1. get logs
        expect(joinLogs(taskResultService.appendLogs as jest.Mock)).toBe(
            '1\n<info> 第1条任务开始执行\n1\n<info> success\n1\n任务信息\ntest\n\n1\n<info> 成功'
        );
        // 2. put result from request into logs if there is a result
        expect((taskResultService.setResult as jest.Mock).mock.calls[0]).toEqual([`1-${md5('test')}`, []]);
        // 3. emit onEndRun event handler
        await waitFor(() => {
            expect((executeService.emit as jest.Mock).mock.calls[1]).toEqual([EXECUTE_EVENT.onEndRun, 1]);
        });
    });

    it('Should support execSql multiply sqls', async () => {
        (taskResultService.clearLogs as jest.Mock).mockReset();
        (taskResultService.appendLogs as jest.Mock).mockReset();
        (api.execSQLImmediately as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
            data: { sqlText: 'test', msg: 'success', status: 5, result: [] },
        });

        const executeService = new ExecuteService();

        executeService.execSql(
            1,
            { id: 1, taskType: 0 } as CatalogueDataProps & IOfflineTaskProps,
            {},
            new Array(5).fill('show tables')
        );

        await waitFor(() => {
            expect(api.execSQLImmediately).toBeCalledTimes(5);
        });

        await waitFor(() => {
            expect(joinLogs(taskResultService.appendLogs as jest.Mock)).toBe(
                '1\n<info> 第1条任务开始执行\n1\n<info> success\n1\n任务信息\ntest\n\n1\n<info> 成功\n1\n<info> 第2条任务开始执行\n1\n<info> success\n1\n任务信息\ntest\n\n1\n<info> 成功\n1\n<info> 第3条任务开始执行\n1\n<info> success\n1\n任务信息\ntest\n\n1\n<info> 成功\n1\n<info> 第4条任务开始执行\n1\n<info> success\n1\n任务信息\ntest\n\n1\n<info> 成功\n1\n<info> 第5条任务开始执行\n1\n<info> success\n1\n任务信息\ntest\n\n1\n<info> 成功'
            );
        });

        // emit onEndRun event handler
        await waitFor(() => {
            expect((executeService.emit as jest.Mock).mock.calls[1]).toEqual([EXECUTE_EVENT.onEndRun, 1]);
        });
    });

    it('Should support abort request when there is a pending request without jobId', async () => {
        (taskResultService.clearLogs as jest.Mock).mockReset();
        (taskResultService.appendLogs as jest.Mock).mockReset();
        (api.execSQLImmediately as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
            data: { sqlText: 'test', msg: 'success', status: 5, result: [] },
        });

        const executeService = new ExecuteService();

        executeService.execSql(
            1,
            { id: 1, taskType: 0 } as CatalogueDataProps & IOfflineTaskProps,
            {},
            new Array(1).fill('show tables')
        );

        executeService.stopSql(1, { id: 1, taskType: 0 } as CatalogueDataProps & IOfflineTaskProps, false);

        // 1. emit onStop event handler
        expect(executeService.emit).toBeCalledWith(EXECUTE_EVENT.onStop, 1);
        // 2. set stop sign
        // @ts-ignore
        expect(executeService.stopSign.get(1)).toBe(true);

        await waitFor(() => {
            expect(api.execSQLImmediately).toBeCalled();
        });

        // 3. set stop sign
        // @ts-ignore
        expect(executeService.stopSign.get(1)).toBe(false);
        // 4. log error message
        expect(joinLogs(taskResultService.appendLogs as jest.Mock)).toBe(
            '1\n<info> 第1条任务开始执行\n1\n<error> 用户主动取消请求！'
        );
    });

    it('Should support polling request but without jobId', async () => {
        (taskResultService.clearLogs as jest.Mock).mockReset();
        (taskResultService.appendLogs as jest.Mock).mockReset();
        (notification.error as jest.Mock).mockReset();
        (api.execSQLImmediately as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
            data: { continue: true },
        });

        const executeService = new ExecuteService();

        executeService.execSql(
            1,
            { id: 1, taskType: 0 } as CatalogueDataProps & IOfflineTaskProps,
            {},
            new Array(1).fill('show tables')
        );

        await waitFor(() => {
            expect(api.execSQLImmediately).toBeCalled();
        });

        expect(notification.error).toBeCalledWith({
            key: 'CONTINUE_WITHOUT_JOBID',
            message: '当前任务执行需要轮训获取结果，但是未找到 jobId，轮训失败',
        });
    });

    it('Should support polling failed request', async () => {
        (taskResultService.clearLogs as jest.Mock).mockReset();
        (taskResultService.appendLogs as jest.Mock).mockReset();
        (api.execSQLImmediately as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
            data: { continue: true, jobId: 'asdf123' },
        });
        (api.selectStatus as jest.Mock).mockReset().mockResolvedValue({
            code: 0,
            data: {
                status: 9,
            },
        });

        const executeService = new ExecuteService();

        executeService.execSql(
            1,
            { id: 1, taskType: 0 } as CatalogueDataProps & IOfflineTaskProps,
            {},
            new Array(1).fill('show tables')
        );

        await waitFor(() => {
            expect(api.selectStatus).toBeCalled();
        });

        expect(joinLogs(taskResultService.appendLogs as jest.Mock)).toBe(
            '1\n<info> 第1条任务开始执行\n1\n<error> 请求异常！\n1\n<info> 提交失败'
        );
    });

    it('Should polling request 3 times and failed at end', async () => {
        (taskResultService.clearLogs as jest.Mock).mockReset();
        (taskResultService.appendLogs as jest.Mock).mockReset();
        (api.execSQLImmediately as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
            data: { continue: true, jobId: 'asdf123' },
        });
        (api.selectStatus as jest.Mock)
            .mockReset()
            .mockResolvedValueOnce({
                code: 1,
                data: {
                    status: 4,
                },
            })
            .mockResolvedValueOnce({
                code: 1,
                data: {
                    status: 4,
                },
            })
            .mockResolvedValue({
                code: 1,
                data: {
                    status: 8,
                },
            });

        (api.selectRunLog as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
            message: 'selectRunLog failed',
            msg: "selectRunLog's msg",
        });

        const executeService = new ExecuteService();

        executeService.execSql(
            1,
            { id: 1, taskType: 0 } as CatalogueDataProps & IOfflineTaskProps,
            {},
            new Array(1).fill('show tables')
        );

        await waitFor(() => {
            expect(api.execSQLImmediately).toBeCalledTimes(1);
        });

        // 1. set running sql signal
        // @ts-ignore
        expect(executeService.runningSql.get(1)).toBe('asdf123');

        // 2. polling request
        jest.runAllTimers();
        await waitFor(() => {
            expect(api.selectStatus).toBeCalledTimes(2);
        });

        jest.runAllTimers();
        await waitFor(() => {
            expect(api.selectStatus).toBeCalledTimes(3);
        });

        // 3. collect logs
        expect(joinLogs(taskResultService.appendLogs as jest.Mock)).toBe(
            '1\n<info> 第1条任务开始执行\n1\n<info> 运行中.....\n1\n<info> 运行中.....\n1\n<error> 运行失败\n1\n<error> selectRunLog failed'
        );
    });

    it('Should polling request and finished at end', async () => {
        (taskResultService.clearLogs as jest.Mock).mockReset();
        (taskResultService.appendLogs as jest.Mock).mockReset();
        (api.execSQLImmediately as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
            data: { continue: true, jobId: 'asdf123' },
        });
        (taskResultService.setResult as jest.Mock).mockReset();
        (api.selectStatus as jest.Mock)
            .mockReset()
            .mockResolvedValueOnce({
                code: 1,
                data: {
                    status: 4,
                },
            })
            .mockResolvedValueOnce({
                code: 1,
                data: {
                    status: 4,
                },
            })
            .mockResolvedValue({
                code: 1,
                data: {
                    status: 5,
                },
            });

        (api.selectRunLog as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
            message: 'selectRunLog failed',
            msg: "selectRunLog's msg",
        });

        (api.selectExecResultData as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
            data: {
                result: [],
            },
        });

        const executeService = new ExecuteService();

        executeService.execSql(
            1,
            { id: 1, taskType: 0 } as CatalogueDataProps & IOfflineTaskProps,
            {},
            new Array(1).fill('show tables')
        );

        await waitFor(() => {
            expect(api.execSQLImmediately).toBeCalledTimes(1);
        });

        // 1. set running sql signal
        // @ts-ignore
        expect(executeService.runningSql.get(1)).toBe('asdf123');

        // 2. polling request
        jest.runAllTimers();
        await waitFor(() => {
            expect(api.selectStatus).toBeCalledTimes(2);
        });

        jest.runAllTimers();
        await waitFor(() => {
            expect(api.selectStatus).toBeCalledTimes(3);
        });

        // 3. get result
        await waitFor(() => {
            expect(api.selectExecResultData).toBeCalled();
        });

        expect(taskResultService.setResult).toBeCalledWith(`1-${md5('sync')}`, []);

        // 4. collect logs
        expect(joinLogs(taskResultService.appendLogs as jest.Mock)).toBe(
            '1\n<info> 第1条任务开始执行\n1\n<info> 运行中.....\n1\n<info> 运行中.....\n1\n<info> 成功\n1\n<info> 获取结果成功'
        );
    });

    it('Should stop execution on polling request', async () => {
        (taskResultService.clearLogs as jest.Mock).mockReset();
        (taskResultService.appendLogs as jest.Mock).mockReset();
        (api.execSQLImmediately as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
            data: { continue: true, jobId: 'asdf123' },
        });
        (taskResultService.setResult as jest.Mock).mockReset();
        (api.stopSQLImmediately as jest.Mock).mockReset().mockResolvedValue({});
        (api.selectStatus as jest.Mock)
            .mockReset()
            .mockResolvedValueOnce({
                code: 1,
                data: {
                    status: 4,
                },
            })
            .mockResolvedValueOnce({
                code: 1,
                data: {
                    status: 4,
                },
            })
            .mockResolvedValue({
                code: 1,
                data: {
                    status: 5,
                },
            });

        (api.selectRunLog as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
            message: 'selectRunLog failed',
            msg: "selectRunLog's msg",
        });

        (api.selectExecResultData as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
            data: {
                result: [],
            },
        });

        const executeService = new ExecuteService();

        executeService.execSql(
            1,
            { id: 1, taskType: 0 } as CatalogueDataProps & IOfflineTaskProps,
            {},
            new Array(1).fill('show tables')
        );

        await waitFor(() => {
            expect(api.execSQLImmediately).toBeCalledTimes(1);
        });

        // 1. set running sql signal
        // @ts-ignore
        expect(executeService.runningSql.get(1)).toBe('asdf123');

        executeService.stopSql(1, { id: 1, taskType: 0 } as CatalogueDataProps & IOfflineTaskProps, false);

        // 2. stop request immediately
        await waitFor(() => {
            expect(api.stopSQLImmediately).toBeCalledTimes(1);
        });

        jest.runAllTimers();
        await waitFor(() => {
            expect(api.selectStatus).toBeCalled();
        });

        expect(joinLogs(taskResultService.appendLogs as jest.Mock)).toBe(
            '1\n<info> 第1条任务开始执行\n1\n<info> 运行中.....\n1\n<error> 用户主动取消请求！'
        );
    });

    it('Should execute sync task failed', async () => {
        (taskResultService.appendLogs as jest.Mock).mockReset();
        (api.execDataSyncImmediately as jest.Mock)
            .mockReset()
            .mockResolvedValueOnce({
                code: 0,
            })
            .mockResolvedValue({
                code: 1,
                data: {
                    msg: 'broken',
                    status: 8,
                },
                message: 'test',
            });

        const executeService = new ExecuteService();

        await executeService.execDataSync(1, {
            name: 'test',
        });

        // 1. request
        await waitFor(() => {
            expect(api.execDataSyncImmediately).toBeCalledTimes(1);
        });
        // 2. logs
        expect(joinLogs(taskResultService.appendLogs as jest.Mock)).toBe(
            '1\n同步任务【test】开始执行\n1\n<error> 请求异常！'
        );

        (taskResultService.appendLogs as jest.Mock).mockReset();
        await executeService.execDataSync(1, {
            name: 'test',
        });

        // 1. request
        await waitFor(() => {
            expect(api.execDataSyncImmediately).toBeCalledTimes(2);
        });

        // 2. logs
        expect(joinLogs(taskResultService.appendLogs as jest.Mock)).toBe(
            '1\n同步任务【test】开始执行\n1\n<error> test\n1\n<info> 已经成功发送执行请求...\n1\n<error> broken\n1\n<error> 执行返回结果异常'
        );
    });

    it('Should execute sync task successful', async () => {
        (taskResultService.appendLogs as jest.Mock).mockReset();
        (api.selectExecResultDataSync as jest.Mock)
            .mockReset()
            .mockResolvedValueOnce({
                code: 1,
                data: {
                    status: 4,
                },
            })
            .mockResolvedValueOnce({
                code: 1,
                data: {
                    status: 5,
                },
            });
        (api.execDataSyncImmediately as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
            data: {
                jobId: 'asdf123',
            },
        });

        const executeService = new ExecuteService();

        executeService.execDataSync(1, {
            name: 'test',
        });

        jest.runAllTimers();
        await waitFor(() => {
            expect(api.selectExecResultDataSync).toBeCalledTimes(1);
        });

        jest.runAllTimers();
        await waitFor(() => {
            expect(api.selectExecResultDataSync).toBeCalledTimes(2);
        });

        expect(joinLogs(taskResultService.appendLogs as jest.Mock)).toBe(
            '1\n同步任务【test】开始执行\n1\n<info> 已经成功发送执行请求...\n1\n<info> 成功'
        );
    });
});
