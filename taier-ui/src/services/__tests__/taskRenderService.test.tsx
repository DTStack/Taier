import React from 'react';
import molecule from '@dtinsight/molecule';
import { fireEvent, render, waitFor } from '@testing-library/react';
import { Form, Modal } from 'antd';
import '@testing-library/jest-dom';

import api from '@/api';
import notification from '@/components/notification';
import { TASK_TYPE_ENUM } from '@/constant';
import type { IOfflineTaskProps } from '@/interface';
import { RightBarKind } from '@/interface';
import TaskRenderService from '../taskRenderService';
import { editorActionBarService } from '..';

jest.mock('@/api');
jest.mock('@/api/http');
jest.mock('@/components/notification');

jest.mock('@/services', () => ({
    breadcrumbService: { getBreadcrumb: jest.fn() },
    editorActionBarService: { performSyncTaskActions: jest.fn() },
}));

jest.mock('@/components/scaffolds/create', () => ({
    datasource: ({ disabled, onChange }: any) => (
        <div data-testid="datasource" data-disabled={disabled} onClick={() => onChange()}>
            datasource
        </div>
    ),
    queue: () => <div>queue</div>,
}));

jest.mock('@/pages/editor/flink', () => () => {
    return <div>{`I'm flink`}</div>;
});

const mockSparkSQL = {
    key: 0,
    value: 'SparkSQL',
    computeType: 1,
    taskProperties: {
        renderCondition: null,
        renderKind: 'editor',
        formField: ['datasource', 'queue', 'test'],
        actionsCondition: null,
        actions: ['SAVE_TASK'],
        dataTypeCodes: [45],
        barItem: ['task', 'dependency', 'task_params', 'env_params'],
        barItemCondition: null,
    },
    jobType: 0,
};

const mockFlink = {
    key: 11,
    value: 'Flink',
    computeType: 0,
    taskProperties: {
        renderCondition: null,
        renderKind: 'flink',
        formField: ['resourceIdList', 'mainClass', 'exeArgs', 'componentVersion'],
        actionsCondition: null,
        actions: ['SAVE_TASK', 'SUBMIT_TASK', 'OPERATOR_TASK'],
        dataTypeCodes: null,
        barItem: ['env_params'],
        barItemCondition: null,
    },
    jobType: 1,
};

const mockSync = {
    key: 2,
    value: '数据同步',
    computeType: 1,
    taskProperties: {
        renderCondition: null,
        renderKind: 'dataSync1',
        formField: [],
        actionsCondition: {
            key: 'createModel',
            value: 0,
            actions: ['SAVE_TASK'],
        },
        actions: [],
        dataTypeCodes: null,
        barItem: [],
        barItemCondition: {
            barItem: ['task', 'env_params'],
            key: 'createModel',
            value: 0,
        },
    },
    jobType: 2,
};

beforeEach(() => {
    (api.getTaskTypes as jest.Mock).mockResolvedValue({
        code: 1,
        data: [mockSparkSQL, mockFlink, mockSync],
    });

    (api.getSupportSource as jest.Mock).mockResolvedValue({
        code: 1,
        data: {
            writers: [1],
            readers: [1],
            dataAcquisitionWriter: null,
            dataAcquisitionReader: null,
            flinkSqlSources: [26, 17, 37, 14],
            flinkSqlSinks: [1, 39, 33, 46],
            flinkSqlSides: [1],
        },
    });
});

describe('Test TaskRenderService', () => {
    it('Should filled with default value', async () => {
        const taskRenderService = new TaskRenderService();

        await waitFor(() => {
            expect(taskRenderService.getState().supportTaskList).toHaveLength(3);
            expect(taskRenderService.getState().supportSourceList.writers).toHaveLength(1);

            expect(taskRenderService.getState()).toMatchSnapshot();
        });

        expect(taskRenderService.getField(TASK_TYPE_ENUM.SPARK_SQL)).toEqual(mockSparkSQL);
        expect(taskRenderService.getRenderKind(TASK_TYPE_ENUM.SPARK_SQL)).toBe('editor');
    });

    it('Should get form field components', async () => {
        const taskRenderService = new TaskRenderService();
        const original = Modal.confirm;
        Modal.confirm = jest.fn();

        await waitFor(() => {
            expect(taskRenderService.getState().supportTaskList).toHaveLength(3);
            expect(taskRenderService.getState().supportSourceList.writers).toHaveLength(1);
        });

        // TASK_TYPE_ENUM.PY_SPARK does NOT have fields
        expect(taskRenderService.renderCreateForm(TASK_TYPE_ENUM.PY_SPARK)).toBe(null);

        // TASK_TYPE_ENUM.SPARK_SQL have ['datasource', 'queue', 'test'] field
        const { asFragment, getByTestId } = render(
            <Form>{taskRenderService.renderCreateForm(TASK_TYPE_ENUM.SPARK_SQL)!}</Form>
        );

        // 「test」 field is undefined so it should log error
        expect(notification.error).toBeCalledWith({
            key: 'UNDEFINED_DEFINED_COMPONENT',
            message: `未定义的表单组件-「test」`,
        });
        expect(asFragment()).toMatchSnapshot();

        const dataSourceEle = getByTestId('datasource');

        expect(dataSourceEle.dataset.disabled).toBe('false');

        fireEvent.click(dataSourceEle);
        expect(Modal.confirm).toBeCalledTimes(1);

        Modal.confirm = original;
    });

    it('Should all icons', () => {
        const taskRenderService = new TaskRenderService();

        expect(taskRenderService.renderTaskIcon(TASK_TYPE_ENUM.SPARK_SQL)).toMatchSnapshot();
        expect(taskRenderService.renderTaskIcon(TASK_TYPE_ENUM.SYNC)).toMatchSnapshot();
        expect(taskRenderService.renderTaskIcon(TASK_TYPE_ENUM.HIVE_SQL)).toMatchSnapshot();
        expect(taskRenderService.renderTaskIcon(TASK_TYPE_ENUM.SQL)).toMatchSnapshot();
        expect(taskRenderService.renderTaskIcon(TASK_TYPE_ENUM.DATA_ACQUISITION)).toMatchSnapshot();
        expect(taskRenderService.renderTaskIcon(TASK_TYPE_ENUM.FLINK)).toMatchSnapshot();
        expect(taskRenderService.renderTaskIcon(TASK_TYPE_ENUM.OCEANBASE)).toMatchSnapshot();
        expect(taskRenderService.renderTaskIcon(TASK_TYPE_ENUM.VIRTUAL)).toMatchSnapshot();
        expect(taskRenderService.renderTaskIcon(TASK_TYPE_ENUM.WORK_FLOW)).toMatchSnapshot();
        expect(taskRenderService.renderTaskIcon(TASK_TYPE_ENUM.PYTHON)).toMatchSnapshot();
        expect(taskRenderService.renderTaskIcon(TASK_TYPE_ENUM.SHELL)).toMatchSnapshot();
        expect(taskRenderService.renderTaskIcon(TASK_TYPE_ENUM.CLICKHOUSE)).toMatchSnapshot();
        expect(taskRenderService.renderTaskIcon(TASK_TYPE_ENUM.SPARK)).toMatchSnapshot();
        expect(taskRenderService.renderTaskIcon(TASK_TYPE_ENUM.DORIS)).toMatchSnapshot();
        expect(taskRenderService.renderTaskIcon(TASK_TYPE_ENUM.MYSQL)).toMatchSnapshot();
        expect(taskRenderService.renderTaskIcon(TASK_TYPE_ENUM.GREENPLUM)).toMatchSnapshot();
        expect(taskRenderService.renderTaskIcon(TASK_TYPE_ENUM.POSTGRE_SQL)).toMatchSnapshot();
        expect(taskRenderService.renderTaskIcon(TASK_TYPE_ENUM.SQL_SERVER)).toMatchSnapshot();
        expect(taskRenderService.renderTaskIcon(TASK_TYPE_ENUM.TiDB)).toMatchSnapshot();
        expect(taskRenderService.renderTaskIcon(TASK_TYPE_ENUM.VERTICA)).toMatchSnapshot();
        expect(taskRenderService.renderTaskIcon(100 as TASK_TYPE_ENUM)).toMatchSnapshot();
    });

    it('Should render specific tab on Editor', async () => {
        const taskRenderService = new TaskRenderService();

        await waitFor(() => {
            expect(taskRenderService.getState().supportTaskList).toHaveLength(3);
            expect(taskRenderService.getState().supportSourceList.writers).toHaveLength(1);
        });

        // Common task like sparkSQL
        expect(
            taskRenderService.renderTabOnEditor(TASK_TYPE_ENUM.SPARK_SQL, {
                id: 'sparkSQL',
                name: 'sparkSQL',
                taskType: TASK_TYPE_ENUM.SPARK_SQL,
                sqlText: '-- test',
            })
        ).toMatchSnapshot();

        // FlinkSQL doesn't have value and language in data
        const flinkTab = taskRenderService.renderTabOnEditor(TASK_TYPE_ENUM.FLINK, {
            id: 'flink',
            name: 'flink',
            taskType: TASK_TYPE_ENUM.FLINK,
        });
        expect(flinkTab).toMatchSnapshot();
        const { findByText } = render(<>{(flinkTab.renderPane as (data: any) => React.ReactNode)({ id: 'test' })}</>);
        expect(await findByText("I'm flink")).toBeInTheDocument();

        // Sync have value in data
        expect(
            taskRenderService.renderTabOnEditor(TASK_TYPE_ENUM.SYNC, {
                id: 'sync',
                name: 'sync',
                taskType: TASK_TYPE_ENUM.SYNC,
                value: '{"a": 1}',
            })
        ).toMatchSnapshot();
    });

    it('Should render specific right bar', async () => {
        const taskRenderService = new TaskRenderService();

        await waitFor(() => {
            expect(taskRenderService.getState().supportTaskList).toHaveLength(3);
            expect(taskRenderService.getState().supportSourceList.writers).toHaveLength(1);
        });

        (molecule.editor.getState as jest.Mock)
            .mockReset()
            .mockImplementationOnce(() => ({
                current: {
                    tab: {
                        id: 'edit-datasource',
                    },
                },
            }))
            .mockImplementationOnce(() => ({
                current: {
                    tab: {
                        id: 1,
                        data: {
                            taskType: 0,
                        },
                    },
                },
            }))
            .mockImplementationOnce(() => ({
                current: {
                    tab: {
                        id: 1,
                        data: {
                            taskType: 2,
                            createModel: 0,
                        },
                    },
                },
            }))
            .mockImplementationOnce(() => ({
                current: {
                    tab: {
                        id: 1,
                        data: {
                            taskType: 2,
                            createModel: 1,
                        },
                    },
                },
            }));

        // invalid tab
        expect(taskRenderService.renderRightBar()).toEqual([RightBarKind.TASK]);

        // sparkSQL tab
        expect(taskRenderService.renderRightBar()).toEqual([
            RightBarKind.TASK,
            'dependency',
            'task_params',
            'env_params',
        ]);

        // sync tab with createModel(0)
        expect(taskRenderService.renderRightBar()).toEqual([RightBarKind.TASK, 'env_params']);
        // sync tab with createModel(1)
        expect(taskRenderService.renderRightBar()).toEqual([RightBarKind.TASK]);
    });

    it('Should renderEditorActions', async () => {
        const taskRenderService = new TaskRenderService();

        await waitFor(() => {
            expect(taskRenderService.getState().supportTaskList).toHaveLength(3);
            expect(taskRenderService.getState().supportSourceList.writers).toHaveLength(1);
        });

        expect(taskRenderService.renderEditorActions(TASK_TYPE_ENUM.SPARK_SQL, {} as IOfflineTaskProps)).toEqual([
            {
                icon: 'save',
                id: 'task.save',
                name: 'Save Task',
                place: 'outer',
                title: '保存',
            },
        ]);

        expect(
            taskRenderService.renderEditorActions(TASK_TYPE_ENUM.SYNC, {
                createModel: 0,
            } as IOfflineTaskProps)
        ).toEqual([
            {
                icon: 'save',
                id: 'task.save',
                name: 'Save Task',
                place: 'outer',
                title: '保存',
            },
        ]);

        expect(
            taskRenderService.renderEditorActions(TASK_TYPE_ENUM.SYNC, {
                createModel: 1,
            } as IOfflineTaskProps)
        ).toEqual([]);
    });

    it('Should focus tab already opened', async () => {
        (molecule.editor.isOpened as jest.Mock).mockReset().mockImplementation(() => true);
        (molecule.editor.getGroupIdByTab as jest.Mock).mockReset().mockImplementation(() => 1);
        (molecule.editor.setActive as jest.Mock).mockReset();
        (editorActionBarService.performSyncTaskActions as jest.Mock).mockReset();

        const taskRenderService = new TaskRenderService();
        taskRenderService.openTask({ id: 1, taskType: 0 });
        expect(molecule.editor.setActive).toBeCalledWith(1, '1');
        await waitFor(() => {
            // Don't forget perform actions when tab changed
            expect(editorActionBarService.performSyncTaskActions).toBeCalled();
        });
    });

    it('Should open tab after request', async () => {
        (molecule.editor.isOpened as jest.Mock).mockReset().mockImplementation(() => false);
        (api.getOfflineTaskByID as jest.Mock).mockReset().mockResolvedValue({
            code: 1,
            data: {
                id: 'sparkSQL',
                name: 'sparkSQL',
                taskType: TASK_TYPE_ENUM.SPARK_SQL,
                sqlText: '-- test',
            },
        });
        (molecule.folderTree.setActive as jest.Mock).mockReset();
        (molecule.editor.open as jest.Mock).mockReset();
        (molecule.explorer.forceUpdate as jest.Mock).mockReset();

        const taskRenderService = new TaskRenderService();
        taskRenderService.openTask({ id: 1, taskType: 0 });

        await waitFor(() => {
            expect(molecule.folderTree.setActive).toBeCalledWith('sparkSQL');
            expect(molecule.editor.open).toBeCalled();
            expect(molecule.explorer.forceUpdate).toBeCalled();
        });
    });

    it('Should open tab without request', () => {
        (molecule.editor.isOpened as jest.Mock).mockReset().mockImplementation(() => false);

        (notification.error as jest.Mock).mockReset();

        (molecule.folderTree.setActive as jest.Mock).mockReset();
        (molecule.editor.open as jest.Mock).mockReset();
        (molecule.explorer.forceUpdate as jest.Mock).mockReset();

        const taskRenderService = new TaskRenderService();
        taskRenderService.openTask({ id: 1 }, { create: true });

        expect(notification.error).toBeCalledWith({
            key: 'OPEN_TASK_ERROR',
            message: `无法打开一个未知任务类型的任务，当前任务的任务类型为 undefined`,
        });

        taskRenderService.openTask({ id: 1, taskType: 0 }, { create: true });

        expect(notification.error).toBeCalledWith({
            key: 'OPEN_TASK_ERROR',
            message: `无法打开一个未知任务名称任务`,
        });

        taskRenderService.openTask({ id: 1, taskType: 0, name: 'test' }, { create: true });
        expect(molecule.folderTree.setActive).toBeCalledTimes(1);
        expect(molecule.editor.open).toBeCalled();
        expect(molecule.explorer.forceUpdate).toBeCalled();
    });

    it("Should log error when getting task's types failed", async () => {
        (api.getTaskTypes as jest.Mock).mockReset().mockResolvedValue({ code: 0 });
        (notification.error as jest.Mock).mockReset();

        const taskRenderService = new TaskRenderService();

        await waitFor(() => {
            expect(notification.error).toBeCalledWith({
                key: 'FailedJob',
                message: `获取支持的类型失败，将无法创建新的任务！`,
            });
        });

        taskRenderService.getTaskTypes(true);

        await waitFor(() => {
            // ONLY called once since doesn't log error at second scene
            expect(notification.error).toBeCalledTimes(1);
        });
    });
});
