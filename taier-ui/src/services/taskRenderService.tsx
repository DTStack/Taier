import { lazy, Suspense } from 'react';
import molecule from '@dtinsight/molecule';
import { Component } from '@dtinsight/molecule/esm/react';
import type { FormInstance } from 'antd';
import { Modal } from 'antd';
import { singleton } from 'tsyringe';
import 'reflect-metadata';

import api from '@/api';
import http from '@/api/http';
import {
    ClickhouseIcon,
    DataCollectionIcon,
    DataxIcon,
    DorisIcon,
    FlinkIcon,
    FlinkSQLIcon,
    GreenPlumIcon,
    HadoopMRIcon,
    HiveSQLIcon,
    MysqlIcon,
    OceanBaseIcon,
    OracleSQLIcon,
    PostgreSqlIcon,
    PythonIcon,
    ShellIcon,
    SparkIcon,
    SparkSQLIcon,
    SqlServerIcon,
    TiDbIcon,
    VerticaIcon,
    VirtualIcon,
    WorkflowIcon,
} from '@/components/icon';
import notification from '@/components/notification';
import scaffolds from '@/components/scaffolds/create';
import editorActionsScaffolds from '@/components/scaffolds/editorActions';
import { DATA_SOURCE_ENUM, TASK_TYPE_ENUM } from '@/constant';
import type { ISupportJobTypes } from '@/context';
import type { IOfflineTaskProps } from '@/interface';
import { RightBarKind } from '@/interface';
import { prettierJSONstring } from '@/utils';
import { mappingTaskTypeToLanguage } from '@/utils/enums';
import { isTaskTab } from '@/utils/is';
import { breadcrumbService, editorActionBarService } from '.';

export interface ITaskRenderState {
    supportTaskList: ISupportJobTypes[];
    supportSourceList: {
        /**
         * 数据同步 writers 支持的数据源类型
         */
        writers: DATA_SOURCE_ENUM[];
        /**
         * 数据同步 readers 支持的数据源类型
         */
        readers: DATA_SOURCE_ENUM[];
        /**
         * 实时采集 writer 支持的数据源类型
         */
        dataAcquisitionWriter: DATA_SOURCE_ENUM[];
        /**
         * 实时采集 reader 支持的数据源类型
         */
        dataAcquisitionReader: DATA_SOURCE_ENUM[];
        /**
         * flinkSql 源表支持的数据源类型
         */
        flinkSqlSources: DATA_SOURCE_ENUM[];
        /**
         * flinkSql 结果表支持的数据源类型
         */
        flinkSqlSinks: DATA_SOURCE_ENUM[];
        /**
         * flinkSql 维表支持的数据源类型
         */
        flinkSqlSides: DATA_SOURCE_ENUM[];
    };
}

@singleton()
export default class TaskRenderService extends Component<ITaskRenderState> {
    state: ITaskRenderState = {
        /**
         * 当前支持的全部任务列表
         */
        supportTaskList: [],
        supportSourceList: {
            writers: [],
            readers: [],
            dataAcquisitionWriter: [],
            dataAcquisitionReader: [],
            flinkSqlSources: [],
            flinkSqlSinks: [],
            flinkSqlSides: [],
        },
    };

    constructor() {
        super();
        this.getTaskTypes();
        this.getSupportSource();
    }

    /**
     * Get the detail of task type
     */
    public getField(key: TASK_TYPE_ENUM) {
        return this.state.supportTaskList.find((i) => i.key === key);
    }

    /**
     * Get the renderKind in task type
     */
    public getRenderKind(key: TASK_TYPE_ENUM) {
        return this.getField(key)?.taskProperties.renderKind || 'editor';
    }

    // 获取当前支持的任务类型
    public getTaskTypes(silent = false) {
        if (silent) {
            http.verbose = false;
        }
        api.getTaskTypes({})
            .then((res) => {
                if (res.code === 1) {
                    this.setState({
                        supportTaskList: res.data || [],
                    });
                } else {
                    if (!silent) {
                        notification.error({
                            key: 'FailedJob',
                            message: `获取支持的类型失败，将无法创建新的任务！`,
                        });
                    }
                }
            })
            .finally(() => {
                http.verbose = true;
            });
    }

    public getSupportSource() {
        api.getSupportSource<ITaskRenderState['supportSourceList']>({}).then((res) => {
            if (res.code === 1) {
                const {
                    writers = [],
                    readers = [],
                    dataAcquisitionReader = [],
                    dataAcquisitionWriter = [],
                    flinkSqlSides = [],
                    flinkSqlSinks = [],
                    flinkSqlSources = [],
                } = res.data;
                this.setState({
                    supportSourceList: {
                        writers,
                        readers,
                        dataAcquisitionReader,
                        dataAcquisitionWriter,
                        flinkSqlSides,
                        flinkSqlSinks,
                        flinkSqlSources,
                    },
                });
            }
        });
    }

    /**
     * 根据任务类型获取创建任务所需要的 UI 界面
     */
    public renderCreateForm = (
        key: TASK_TYPE_ENUM,
        record?: Record<string, any>,
        form?: Omit<FormInstance, 'scrollToField' | '__INTERNAL__' | 'getFieldInstance'>
    ) => {
        const field = this.state.supportTaskList.find((i) => i.key === key)?.taskProperties;
        if (!field) {
            return null;
        }

        return (
            <>
                {field.formField?.map((f) => {
                    const DefinedComponent = scaffolds[f];
                    if (!DefinedComponent) {
                        notification.error({
                            key: 'UNDEFINED_DEFINED_COMPONENT',
                            message: `未定义的表单组件-「${f}」`,
                        });
                        return null;
                    }
                    return (
                        <DefinedComponent
                            key={f}
                            disabled={!!record}
                            onChange={() => {
                                Modal.confirm({
                                    title: '正在切换引擎版本',
                                    content: (
                                        <>
                                            <span style={{ color: 'red' }}>切换引擎版本后将重置环境参数</span>
                                            ，请确认是否继续？
                                        </>
                                    ),
                                    onCancel: () => {
                                        form?.resetFields(['componentVersion']);
                                    },
                                });
                            }}
                        />
                    );
                })}
            </>
        );
    };

    /**
     * 根据任务类型获取不同的目录树中的图标
     */
    public renderTaskIcon = (key: TASK_TYPE_ENUM) => {
        switch (key) {
            case TASK_TYPE_ENUM.SPARK_SQL:
                return <SparkSQLIcon style={{ color: '#519aba' }} />;
            case TASK_TYPE_ENUM.SYNC:
                return 'sync';
            case TASK_TYPE_ENUM.HIVE_SQL:
                return <HiveSQLIcon style={{ color: '#4291f0' }} />;
            case TASK_TYPE_ENUM.SQL:
                return <FlinkSQLIcon style={{ color: '#5655d8' }} />;
            case TASK_TYPE_ENUM.DATA_ACQUISITION:
                return <DataCollectionIcon style={{ color: '#3F87FF' }} />;
            case TASK_TYPE_ENUM.FLINK:
                return <FlinkIcon />;
            case TASK_TYPE_ENUM.OCEANBASE:
                return <OceanBaseIcon />;
            case TASK_TYPE_ENUM.VIRTUAL:
                return <VirtualIcon />;
            case TASK_TYPE_ENUM.WORK_FLOW:
                return <WorkflowIcon style={{ color: '#2491F7' }} />;
            case TASK_TYPE_ENUM.PYTHON:
                return <PythonIcon />;
            case TASK_TYPE_ENUM.SHELL:
                return <ShellIcon />;
            case TASK_TYPE_ENUM.CLICKHOUSE:
                return <ClickhouseIcon />;
            case TASK_TYPE_ENUM.SPARK:
                return <SparkIcon />;
            case TASK_TYPE_ENUM.DORIS:
                return <DorisIcon />;
            case TASK_TYPE_ENUM.MYSQL:
                return <MysqlIcon />;
            case TASK_TYPE_ENUM.ORACLE_SQL:
            	return <OracleSQLIcon />;
            case TASK_TYPE_ENUM.GREENPLUM:
                return <GreenPlumIcon />;
            case TASK_TYPE_ENUM.POSTGRE_SQL:
                return <PostgreSqlIcon />;
            case TASK_TYPE_ENUM.SQL_SERVER:
                return <SqlServerIcon style={{ color: '#bf4339' }} />;
            case TASK_TYPE_ENUM.TiDB:
                return <TiDbIcon style={{ color: '#bf4339' }} />;
            case TASK_TYPE_ENUM.VERTICA:
                return <VerticaIcon />;
            case TASK_TYPE_ENUM.HADOOP_MR:
                return <HadoopMRIcon />;
            case TASK_TYPE_ENUM.DATAX:
                return <DataxIcon />;
            default:
                return 'file';
        }
    };

    /**
     * 根据任务类型获取不同的 tabData，用以渲染不同的编辑器内容
     */
    public renderTabOnEditor = (
        key: TASK_TYPE_ENUM,
        record: { id: number | string; name: string; taskType: TASK_TYPE_ENUM; [key: string]: any }
    ): molecule.model.IEditorTab => {
        const fields = this.getField(key)?.taskProperties;
        const renderKind = this.getRenderKind(key);

        const isWorkflow = !!record.flowId;

        const tabData: molecule.model.IEditorTab = {
            id: record.id.toString(),
            name: isWorkflow ? `${record.flowName}/${record.name}` : record.name,
            data: (() => {
                // 针对不同任务，data 中的值不一样
                switch (key) {
                    case TASK_TYPE_ENUM.FLINK: {
                        return { ...record };
                    }
                    case TASK_TYPE_ENUM.DATA_ACQUISITION:
                    case TASK_TYPE_ENUM.SYNC:
                        return {
                            ...record,
                            value: prettierJSONstring(record.sqlText),
                        };
                    default:
                        return {
                            ...record,
                            value: record.sqlText,
                        };
                }
            })(),
            icon: this.renderTaskIcon(record.taskType),
            breadcrumb: breadcrumbService.getBreadcrumb(record.id),
        };

        // 判断自定义渲染组件的渲染条件是否生效
        const isWork = fields?.renderCondition
            ? record[fields.renderCondition.key] === fields.renderCondition.value
            : true;

        // 如果是 editor 渲染，则需要声明 editor 的语言
        if (renderKind === 'editor' || !isWork) {
            tabData.data!.language = mappingTaskTypeToLanguage(record.taskType);
        } else {
            try {
                // 自定义渲染需要声明 renderPane 组件
                const PageComponent = lazy(() => import(`@/pages/editor/${renderKind}`));
                tabData.renderPane = (data) => (
                    <Suspense key={data.id} fallback={<div>loading...</div>}>
                        <PageComponent />
                    </Suspense>
                );
            } catch (err) {
                notification.error({
                    key: 'ModuleNotFound',
                    message: `${renderKind} 无法加载，请确认 pages/editor 目录下是否存在该模块`,
                });
            }
        }

        return tabData;
    };

    /**
     * 根据任务类型定义侧边栏
     */
    public renderRightBar = (): string[] => {
        const { current } = molecule.editor.getState();
        /**
         * 当前的 tab 是否不合法，如不合法则展示 Empty
         */
        const isInValidTab = !isTaskTab(current?.tab?.id);
        if (isInValidTab) {
            return [RightBarKind.TASK];
        }

        const record = current?.tab?.data as IOfflineTaskProps;

        const rightBarField = this.state.supportTaskList.find((i) => i.key === record.taskType)?.taskProperties;

        // That's default right bar for each taskType
        const defaultBarItem: string[] = [RightBarKind.TASK];

        if (rightBarField) {
            const isConditionTrue = rightBarField.barItemCondition
                ? record[rightBarField.barItemCondition.key] === rightBarField.barItemCondition.value
                : false;

            if (isConditionTrue) {
                return Array.from(new Set(defaultBarItem.concat(rightBarField.barItemCondition?.barItem || [])));
            }

            return Array.from(new Set(defaultBarItem.concat(rightBarField.barItem || [])));
        }

        return defaultBarItem;
    };

    /**
     * 根据任务类型渲染编辑器 actions
     */
    public renderEditorActions = (key: TASK_TYPE_ENUM, record: IOfflineTaskProps) => {
        const actionsField = this.state.supportTaskList.find((i) => i.key === key)?.taskProperties;

        if (actionsField) {
            const isConditionTrue = actionsField.actionsCondition
                ? record[actionsField.actionsCondition.key] === actionsField.actionsCondition.value
                : false;

            if (isConditionTrue) {
                const actions = Array.from(new Set(actionsField.actionsCondition?.actions || []));
                return actions.map((action) => editorActionsScaffolds[action]) || [];
            }

            const actions = Array.from(new Set(actionsField.actions || []));

            return actions.map((action) => editorActionsScaffolds[action]) || [];
        }

        return [];
    };

    /**
     * 根据 id 在编辑器区域打开任务 tab
     */
    public openTask = async (
        record: {
            id: string | number;
            taskType?: TASK_TYPE_ENUM;
            name?: string;
            [key: string]: any;
        },
        config: {
            /**
             * 标记是否需要进行接口的请求, true 表示是新建的任务不需要向服务端做接口请求
             */
            create: boolean;
        } = {
            create: false,
        }
    ) => {
        // prevent open same task in editor
        if (molecule.editor.isOpened(record.id.toString())) {
            const groupId = molecule.editor.getGroupIdByTab(record.id.toString())!;
            molecule.editor.setActive(groupId, record.id.toString());
            window.setTimeout(() => {
                editorActionBarService.performSyncTaskActions();
            }, 0);
            return;
        }

        if (!config.create) {
            const res = await api.getOfflineTaskByID<IOfflineTaskProps>({ id: record.id });
            if (res.code === 1) {
                const tabData = this.renderTabOnEditor(res.data.taskType, res.data);

                molecule.folderTree.setActive(tabData.id);
                molecule.editor.open(tabData);
                molecule.explorer.forceUpdate();
            }
        } else {
            if (record.taskType === undefined) {
                notification.error({
                    key: 'OPEN_TASK_ERROR',
                    message: `无法打开一个未知任务类型的任务，当前任务的任务类型为 ${record.taskType}`,
                });
                return;
            }

            if (record.name === undefined) {
                notification.error({
                    key: 'OPEN_TASK_ERROR',
                    message: `无法打开一个未知任务名称任务`,
                });
                return;
            }

            const tabData = this.renderTabOnEditor(record.taskType, record as Required<typeof record>);

            molecule.folderTree.setActive(tabData.id);
            molecule.editor.open(tabData);
            molecule.explorer.forceUpdate();
        }
    };
}
