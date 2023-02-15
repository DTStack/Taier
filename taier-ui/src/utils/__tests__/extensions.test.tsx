import api from '@/api';
import { CATALOGUE_TYPE, TASK_TYPE_ENUM } from '@/constant';
import { executeService, taskRenderService } from '@/services';
import taskResultService, { createLog } from '@/services/taskResultService';
import molecule from '@dtinsight/molecule';
import { TreeViewUtil } from '@dtinsight/molecule/esm/common/treeUtil';
import { fileIcon, getParentNode, runTask, syntaxValidate } from '../extensions';

jest.mock('@/services/taskSaveService', () => {
    return {
        transformTabDataToParams: jest.fn(),
    };
});

jest.mock('@/services/taskResultService', () => {
    return {
        getState: jest.fn(() => ({ results: { test: 'test' } })),
        clearLogs: jest.fn(),
        appendLogs: jest.fn(),
        createLog: jest.fn(),
    };
});

jest.mock('@/services', () => {
    return {
        executeService: {
            execDataSync: jest.fn(),
            execSql: jest.fn(() => Promise.resolve()),
        },
        taskRenderService: {
            renderTaskIcon: jest.fn((type) => type),
            getField: jest.fn(),
            getRenderKind: jest.fn(),
        },
    };
});

jest.mock('@/api', () => {
    return {
        checkSyntax: jest.fn(),
    };
});

describe('utils/extensions', () => {
    beforeAll(() => {
        (molecule.panel.getState as jest.Mock)
            .mockReset()
            .mockImplementation(() => ({ data: [{ id: 'panel.output.log' }] }));
    });

    it('Should render file icon', () => {
        expect(fileIcon(null, CATALOGUE_TYPE.FUNCTION)).toBe('code');
        expect(fileIcon(null, CATALOGUE_TYPE.RESOURCE)).toMatchSnapshot();
        expect(fileIcon(TASK_TYPE_ENUM.SQL, CATALOGUE_TYPE.TASK)).toBe(TASK_TYPE_ENUM.SQL);

        expect(taskRenderService.renderTaskIcon).toBeCalled();
    });

    it('Should run task with some effects', async () => {
        // Mock the molecule.layout.getState
        (molecule.layout.getState as jest.Mock)
            .mockReset()
            .mockImplementationOnce(() => ({ panel: { hidden: false } }))
            .mockImplementation(() => ({ panel: { hidden: true } }));

        (molecule.panel.getPanel as jest.Mock)
            .mockReset()
            .mockImplementationOnce(() => undefined)
            .mockImplementation(() => ({ id: 'test' }));

        (taskRenderService.getField as jest.Mock).mockReset().mockImplementation(() => ({
            key: 0,
            value: 'SparkSQL',
            computeType: 1,
            jobType: 0,
        }));

        (taskRenderService.getRenderKind as jest.Mock).mockReset().mockImplementation(() => 'editor');

        runTask({
            id: 1,
            tab: {
                id: 1,
                data: {
                    id: 'test',
                    name: 'test',
                    taskParams: [],
                    taskType: TASK_TYPE_ENUM.SYNC,
                },
            },
        });

        // At first time called, the panel is visible because of the return value of molecule.layout.getState
        expect(molecule.layout.togglePanelVisibility).not.toBeCalled();
        // Whatever the log panel is hidden or visible, the activated panel must be the log panel
        expect(molecule.panel.setState).toBeCalledWith({ current: { id: 'panel.output.log' } });

        expect(executeService.execDataSync).toBeCalledWith('test', {
            taskId: 'test',
            name: 'test',
            taskParams: [],
        });

        const current = {
            id: 1,
            tab: {
                id: 1,
                data: {
                    id: 'test',
                    name: 'test',
                    taskParams: [],
                    taskVariables: [],
                    taskType: TASK_TYPE_ENUM.SPARK_SQL,
                    value: 'show tables; select * from A;',
                },
            },
        };
        // First time the getSelection returns undefined, so execute all sqls
        runTask(current);

        // Toggle the log panel and set this panel active since this panel is hidden
        expect(molecule.layout.togglePanelVisibility).toBeCalled();

        expect(executeService.execSql).toBeCalledWith(
            'test',
            current.tab.data,
            {
                taskVariables: [],
                singleSession: false,
                taskParams: [],
            },
            ['show tables', 'select * from A']
        );
        // Delay expect because then is a micro task
        await new Promise<void>((resolve) => {
            setTimeout(() => {
                expect(molecule.panel.add).toBeCalledWith(
                    expect.objectContaining({
                        id: 'test',
                        name: '结果 1',
                        closable: true,
                    })
                );
                expect((molecule.panel.add as jest.Mock).mock.calls[0][0].renderPane()).toMatchSnapshot();
                resolve();
            }, 0);
        });

        // Second time the getSelection returns same point, so execute all sqls
        runTask(current);
        expect(executeService.execSql).toBeCalledWith(
            'test',
            current.tab.data,
            {
                taskVariables: [],
                singleSession: false,
                taskParams: [],
            },
            ['show tables', 'select * from A']
        );

        // Third time the getSelection returns correct selection, so execute selected sql
        runTask(current);
        expect(executeService.execSql).toBeCalledWith(
            'test',
            current.tab.data,
            {
                taskVariables: [],
                singleSession: false,
                taskParams: [],
            },
            ['show tables']
        );
    });

    it('Should validate syntax failed', async () => {
        (molecule.layout.togglePanelVisibility as jest.Mock).mockClear();

        (molecule.layout.getState as jest.Mock).mockReset().mockImplementation(() => ({ panel: { hidden: false } }));

        (api.checkSyntax as jest.Mock).mockReset().mockImplementation(() => Promise.reject());

        const originalTrace = console.trace;
        console.trace = jest.fn();

        syntaxValidate({
            id: 1,
            tab: {
                id: 1,
                data: {
                    id: 'test',
                },
            },
        });

        // To disabled syntax button
        expect(molecule.editor.updateActions).toBeCalledWith([
            {
                id: 'task.syntax',
                icon: 'loading~spin',
                disabled: true,
            },
        ]);

        // At first time called, the molecule.layout.getState returns false
        expect(molecule.layout.togglePanelVisibility).not.toBeCalled();
        // Whatever the log panel is hidden or visible, the activated panel must be the log panel
        expect(molecule.panel.setState).toBeCalledWith({ current: { id: 'panel.output.log' } });

        await new Promise<void>((resolve) => {
            setTimeout(() => {
                // Reject would call console.trace
                expect(console.trace).toBeCalled();

                expect(createLog).toBeCalledWith('语法检查失败！', 'error');
                expect(taskResultService.appendLogs).toBeCalled();

                // To restore the button
                expect(molecule.editor.updateActions).toBeCalledWith([
                    expect.objectContaining({
                        id: 'task.syntax',
                        disabled: false,
                    }),
                ]);

                console.trace = originalTrace;
                resolve();
            }, 0);
        });
    });

    it('Should validate syntax success', async () => {
        (molecule.layout.togglePanelVisibility as jest.Mock).mockClear();

        (molecule.layout.getState as jest.Mock).mockReset().mockImplementation(() => ({ panel: { hidden: true } }));

        (api.checkSyntax as jest.Mock)
            .mockReset()
            .mockImplementationOnce(() => Promise.resolve({ code: 100, message: 'test' }))
            .mockImplementationOnce(() => Promise.resolve({ code: 1, data: { code: 100, errorMsg: 'errorMsg' } }))
            .mockImplementation(() => Promise.resolve({ code: 1, data: { code: 1 } }));

        (createLog as jest.Mock).mockClear();
        (taskResultService.appendLogs as jest.Mock).mockClear();

        syntaxValidate({
            id: 1,
            tab: {
                id: 1,
                data: {
                    id: 'test',
                },
            },
        });

        expect(molecule.layout.togglePanelVisibility).toBeCalled();

        await new Promise<void>((resolve) => {
            setTimeout(() => {
                expect(createLog).toBeCalledWith('test', 'error');
                expect(taskResultService.appendLogs).toBeCalled();

                expect(createLog).toBeCalledWith('语法检查失败！', 'error');
                expect(taskResultService.appendLogs).toBeCalled();
                resolve();
            }, 0);
        });

        syntaxValidate({
            id: 1,
            tab: {
                id: 1,
                data: {
                    id: 'test',
                },
            },
        });

        await new Promise<void>((resolve) => {
            setTimeout(() => {
                expect(createLog).toBeCalledWith('errorMsg', 'error');
                expect(taskResultService.appendLogs).toBeCalled();

                expect(createLog).toBeCalledWith('语法检查失败！', 'error');
                expect(taskResultService.appendLogs).toBeCalled();
                resolve();
            }, 0);
        });

        // reset the count of log functions
        (createLog as jest.Mock).mockClear();
        (taskResultService.appendLogs as jest.Mock).mockClear();

        // succeed
        syntaxValidate({
            id: 1,
            tab: {
                id: 1,
                data: {
                    id: 'test',
                },
            },
        });

        await new Promise<void>((resolve) => {
            setTimeout(() => {
                expect(createLog).toBeCalledWith('语法检查通过', 'info');
                expect(taskResultService.appendLogs).toBeCalledTimes(2);

                expect(createLog).not.toBeCalledWith('语法检查失败！', 'error');
                expect(taskResultService.appendLogs).toBeCalledTimes(2);
                resolve();
            }, 0);
        });
    });

    it('Should support to find parent node', () => {
        const mockNode = jest.fn();
        (TreeViewUtil as jest.Mock)
            .mockImplementationOnce(() => ({
                getHashMap: jest.fn(),
                getNode: jest.fn((node) => node),
            }))
            .mockImplementation(() => ({
                getHashMap: jest.fn().mockImplementation(() => ({
                    parent: mockNode,
                })),
                getNode: jest.fn((node) => node),
            }));

        expect(
            getParentNode(
                {
                    id: '1',
                },
                {
                    id: '2',
                }
            )
        ).toBe(null);

        expect(
            getParentNode(
                {
                    id: '1',
                },
                {
                    id: '1',
                }
            )
        ).toBe(mockNode);
    });
});
