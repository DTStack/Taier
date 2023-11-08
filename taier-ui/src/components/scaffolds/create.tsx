import { useEffect, useState } from 'react';
import { usePrevious } from 'react-use';
import molecule from '@dtinsight/molecule';
import { Button, Empty, Form, Input, Radio, Select, Spin } from 'antd';

import api from '@/api';
import { CATALOGUE_TYPE, CREATE_MODEL_TYPE, DATA_SYNC_MODE, PythonVersionKind } from '@/constant';
import type { IDataSourceProps } from '@/interface';
import { dataSourceService, taskRenderService } from '@/services';
import resourceManagerTree from '@/services/resourceManagerService';
import FolderPicker from '../folderPicker';
import { syncModeHelp, syncTaskHelp } from '../helpDoc/docs';

interface ICreateFormProps {
    disabled?: boolean;
    onChange?: (value: string) => void;
}

/**
 * 配置模式物料
 */
const CreateModel = ({ disabled }: ICreateFormProps) => (
    <Form.Item
        label="配置模式"
        name="createModel"
        tooltip={syncTaskHelp}
        rules={[
            {
                required: true,
                message: '请选择配置模式',
            },
        ]}
        initialValue={CREATE_MODEL_TYPE.GUIDE}
    >
        <Radio.Group disabled={disabled}>
            <Radio value={CREATE_MODEL_TYPE.GUIDE}>向导模式</Radio>
            <Radio value={CREATE_MODEL_TYPE.SCRIPT}>脚本模式</Radio>
        </Radio.Group>
    </Form.Item>
);

/**
 * 同步模式物料
 */
const SyncModel = ({ disabled }: ICreateFormProps) => (
    <Form.Item
        label="同步模式"
        name={['sourceMap', 'syncModel']}
        tooltip={syncModeHelp}
        rules={[
            {
                required: true,
                message: '请选择配置模式',
            },
        ]}
        initialValue={DATA_SYNC_MODE.NORMAL}
    >
        <Radio.Group disabled={disabled}>
            <Radio value={DATA_SYNC_MODE.NORMAL}>无增量标识</Radio>
            <Radio value={DATA_SYNC_MODE.INCREMENT}>有增量标识</Radio>
        </Radio.Group>
    </Form.Item>
);

/**
 * 引擎版本物料
 */
const ComponentVersion = ({ onChange }: ICreateFormProps) => {
    const form = Form.useFormInstance();
    const taskType = Form.useWatch('taskType', form);
    const [versions, setVersions] = useState<{ label: string; value: string }[]>([]);

    useEffect(() => {
        if (taskType !== undefined) {
            api.getComponentVersionByTaskType<{ componentVersion: string; default: boolean; componentName: string }[]>({
                taskType,
            }).then((res) => {
                if (res.code === 1) {
                    setVersions(
                        res.data?.map((v) => ({
                            label: `${v.componentName}-${v.componentVersion}`,
                            value: v.componentVersion,
                        })) || []
                    );

                    const currentComponentVersion = form.getFieldValue('componentVersion');
                    if (
                        !currentComponentVersion ||
                        !res.data.find((v) => v.componentVersion === currentComponentVersion)
                    ) {
                        // reset initial value
                        form.setFieldValue('componentVersion', res.data.find((v) => v.default)?.componentVersion);
                    }
                }
            });
        }
    }, [taskType]);

    return (
        <Form.Item label="引擎版本" name="componentVersion">
            <Select
                onChange={onChange}
                showSearch
                options={versions}
                optionFilterProp="label"
                getPopupContainer={(node) => node.parentNode}
            />
        </Form.Item>
    );
};

/**
 * 资源下拉菜单物料
 */
const Resource = () => (
    <Form.Item
        label="资源"
        name={['resourceIdList', 0]}
        rules={[
            {
                required: true,
                message: '请选择关联资源',
            },
            {
                validator: (_, value) => {
                    return resourceManagerTree.checkNotDir(value);
                },
            },
        ]}
    >
        <FolderPicker dataType={CATALOGUE_TYPE.RESOURCE} showFile />
    </Form.Item>
);

/**
 * mainClass 物料
 */
const MainClass = () => (
    <Form.Item
        label="mainClass"
        name="mainClass"
        rules={[
            {
                required: true,
                message: '请选择 mainClass',
            },
        ]}
    >
        <Input placeholder="请输入 mainClass" />
    </Form.Item>
);

/**
 * 命令行参数物料
 */
const ExeArgs = () => (
    <Form.Item label="命令行参数" name="exeArgs">
        <Input placeholder="请输入命令行参数" />
    </Form.Item>
);

const PythonVersion = ({ disabled }: ICreateFormProps) => (
    <Form.Item
        label="Python 版本"
        name="pythonVersion"
        rules={[
            {
                required: true,
                message: '请选择 Python 版本',
            },
        ]}
        initialValue={PythonVersionKind.py2}
    >
        <Radio.Group disabled={disabled}>
            <Radio value={PythonVersionKind.py2}>Python 2.x</Radio>
            <Radio value={PythonVersionKind.py3}>Python 3.x</Radio>
        </Radio.Group>
    </Form.Item>
);

/**
 * 任务绑定对应数据源物料
 */
const DataSource = () => {
    const form = Form.useFormInstance();
    const taskType = Form.useWatch('taskType');
    const prevTaskType = usePrevious(taskType);
    const [dataSource, setDataSource] = useState<IDataSourceProps[]>([]);

    const handleGotoSourceCenter = () => {
        molecule.sidebar.setActive('dataSource');
        molecule.activityBar.setActive('dataSource');
    };

    useEffect(() => {
        setDataSource(dataSourceService.getDataSource());

        const listener = (_: any, next: { dataSource: IDataSourceProps[] }) => {
            setDataSource(next.dataSource || []);
        };

        dataSourceService.onUpdateState(listener);
        return () => {
            dataSourceService.removeOnUpdateState(listener);
        };
    }, []);

    useEffect(() => {
        if (taskType !== prevTaskType && prevTaskType !== undefined) {
            form.resetFields(['datasourceId']);
        }
    }, [taskType, prevTaskType]);

    return (
        <Form.Item
            label="数据源"
            name="datasourceId"
            rules={[
                {
                    required: true,
                    message: '请选择数据源',
                },
            ]}
        >
            <Select
                placeholder="请选择任务执行的对应数据源"
                optionFilterProp="label"
                getPopupContainer={(node) => node.parentNode}
                options={dataSource
                    .filter((source) =>
                        taskRenderService
                            .getState()
                            .supportTaskList.find((i) => i.key === taskType)
                            ?.taskProperties?.dataTypeCodes?.includes(source.dataTypeCode)
                    )
                    .map((i) => ({
                        label: `${i.dataName}(${i.dataType})`,
                        value: i.dataInfoId,
                    }))}
                notFoundContent={
                    <Empty
                        image={Empty.PRESENTED_IMAGE_SIMPLE}
                        description={
                            <span>
                                未找到
                                {taskRenderService.getState().supportTaskList.find((i) => i.key === taskType)?.value ||
                                    '未知'}
                                所支持的对应数据源，请先至
                                <Button type="link" size="small" onClick={handleGotoSourceCenter}>
                                    数据源中心
                                </Button>
                                配置
                            </span>
                        }
                    />
                }
            />
        </Form.Item>
    );
};

const QueueConfig = () => {
    const [resourceList, setResourceList] = useState<string[]>([]);
    const [fetching, setFetching] = useState(false);

    useEffect(() => {
        setFetching(true);
        api.getResourceByTenant({})
            .then((res) => {
                if (res.code === 1) {
                    setResourceList(res.data.queues?.map((q: any) => q.queueName));
                }
            })
            .finally(() => {
                setFetching(false);
            });
    }, []);

    return (
        <Form.Item label="YARN 队列" name="queueName">
            <Select
                placeholder="请选择 YARN 队列"
                getPopupContainer={(node) => node.parentNode}
                options={resourceList.map((r) => ({ label: r, value: r }))}
                showSearch
                optionFilterProp="label"
                notFoundContent={
                    fetching ? <Spin spinning={fetching} /> : <Empty image={Empty.PRESENTED_IMAGE_SIMPLE} />
                }
            />
        </Form.Item>
    );
};

/**
 * key 值为服务端字段名，value 为组件名
 */
export default {
    createModel: CreateModel,
    syncModel: SyncModel,
    componentVersion: ComponentVersion,
    resourceIdList: Resource,
    mainClass: MainClass,
    exeArgs: ExeArgs,
    pythonVersion: PythonVersion,
    datasource: DataSource,
    queue: QueueConfig,
} as Record<string, (...args: any[]) => JSX.Element>;
