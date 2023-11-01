import molecule from '@dtinsight/molecule';
import { GlobalEvent } from '@dtinsight/molecule/esm/common/event';
import { message } from 'antd';
import type { Rules, RuleType, ValidateError } from 'async-validator';
import ValidSchema from 'async-validator';
import { cloneDeep, isObject } from 'lodash';
import type { mxCell } from 'mxgraph';
import { singleton } from 'tsyringe';
import 'reflect-metadata';

import api from '@/api';
import type { IGeometryPosition } from '@/components/mxGraph/container';
import {
    CATALOGUE_TYPE,
    CREATE_MODEL_TYPE,
    DATA_SOURCE_ENUM,
    FLINK_VERSIONS,
    NAME_SEPARATOR,
    rdbmsDaType,
    SOURCE_TIME_TYPE,
    SUPPROT_SUB_LIBRARY_DB_ARRAY,
    TASK_TYPE_ENUM,
} from '@/constant';
import type { IOfflineTaskProps } from '@/interface';
import { IComputeType } from '@/interface';
import { checkColumnsData } from '@/pages/editor/streamCollection/taskFunc';
import { isEditing } from '@/pages/editor/workflow';
import {
    isAvro,
    isHavePartition,
    isHavePrimaryKey,
    isHaveTableColumn,
    isHaveTableList,
    isHaveTopic,
    isHbase,
    isKafka,
    isLowerES,
    isRDB,
    isRedis,
    isS3,
} from '@/utils/is';
import viewStoreService from './viewStoreService';
import { breadcrumbService, catalogueService, rightBarService } from '.';

interface IParamsProps extends IOfflineTaskProps {
    // 接口要求的标记位
    preSave?: boolean;
    // 接口要求的标记位
    updateSource?: boolean;
    /**
     * the monaco editor content
     */
    value?: string;
}

export enum SaveEventKind {
    /**
     * 保存成功后的钩子
     */
    onSaveTask = 'onsave',
    /**
     * 保存前的钩子
     */
    onBeforeSave = 'onBeforeSave',
}

@singleton()
class TaskSaveService extends GlobalEvent {
    /**
     * Remove the separator in params' name
     */
    private removeSeparator<T extends Record<string, any> = {}>(raw: T) {
        if (Array.isArray(raw)) return raw;
        return Object.keys(raw).reduce((pre, cur) => {
            const val = isObject(raw[cur]) ? this.removeSeparator(raw[cur]) : raw[cur];

            if (cur.includes(NAME_SEPARATOR)) {
                const idx = cur.indexOf(NAME_SEPARATOR);
                pre[cur.substring(0, idx) as keyof T] = val;
            } else {
                pre[cur as keyof T] = val;
            }

            return pre;
        }, {} as T);
    }
    /**
     * 校验器，用于发起校验以及校验结束后提示错误信息
     */
    private dataValidator = async <T extends any[]>(
        currentPage: IOfflineTaskProps,
        data: T,
        validator: (item: T[number], version: IOfflineTaskProps['componentVersion']) => Promise<ValidateError[] | null>,
        text: string
    ) => {
        const { componentVersion } = currentPage;
        const errors = await Promise.all(data.map((item) => validator(item, componentVersion)));
        errors.forEach((error, index) => {
            if (error) {
                const tableName = data[index]?.tableName;
                message.error(`${text} ${index + 1} ${tableName ? `(${tableName})` : ''}: ${error[0].message}`);
            }
        });
        return errors;
    };

    /**
     * 动态生成 Flink 结果表的校验规则
     */
    private generateValidDesOutPut = (
        data?: IOfflineTaskProps['sink'][number],
        componentVersion?: Valueof<typeof FLINK_VERSIONS>
    ): Rules => {
        const schemaRequired =
            data?.type &&
            [
                DATA_SOURCE_ENUM.POSTGRESQL,
                DATA_SOURCE_ENUM.KINGBASE8,
                DATA_SOURCE_ENUM.SQLSERVER,
                DATA_SOURCE_ENUM.SQLSERVER_2017_LATER,
            ].includes(data.type);
        const isFlink112 = componentVersion === FLINK_VERSIONS.FLINK_1_12;

        return {
            type: [{ required: true, message: '请选择存储类型' }],
            sourceId: [{ required: true, message: '请选择数据源' }],
            topic: [{ required: isHaveTopic(data?.type), message: '请选择Topic' }],
            table: [{ required: isHaveTableList(data?.type) && !isS3(data?.type), message: '请选择表' }],
            tableName: [{ required: true, message: '请输入映射表名' }],
            columns: [
                {
                    required: isHaveTableColumn(data?.type),
                    message: '字段信息不能为空',
                    type: 'array' as RuleType,
                },
                { validator: checkColumnsData },
            ],
            columnsText: [{ required: !isHaveTableColumn(data?.type), message: '字段信息不能为空' }],
            collection: [{ required: data?.type === DATA_SOURCE_ENUM.SOLR, message: '请选择Collection' }],
            objectName: [{ required: isS3(data?.type), message: '请输入ObjectName' }],
            schema: [{ required: schemaRequired, message: '请选择schema' }],
            partitionfields: [
                {
                    required:
                        isHavePartition(data?.type) &&
                        // @ts-ignore
                        data?.isShowPartition &&
                        // @ts-ignore
                        data?.havePartitionfields,
                    message: '请选择分区',
                },
            ],
            'table-input': [{ required: isRedis(data?.type), message: '请输入表名' }],
            index: [{ required: isLowerES(data?.type), message: '请输入索引' }],
            'primaryKey-input': [{ required: isRedis(data?.type), message: '请输入主键' }],
            esType: [{ required: isLowerES(data?.type), message: '请输入索引类型' }],
            rowKey: [{ required: isHbase(data?.type), message: '请输入rowKey' }],
            rowKeyType: [{ required: isHbase(data?.type) && isFlink112, message: '请输入rowKey类型' }],
            sinkDataType: [{ required: isKafka(data?.type), message: '请选择输出类型！' }],
            updateMode: [{ required: true, message: '请选择更新模式' }],
            primaryKey: [
                {
                    required: data?.updateMode === 'upsert' && isHavePrimaryKey(data?.type),
                    message: '请输入主键',
                },
            ],
            partitionKeys: [{ required: data?.enableKeyPartitions, message: '请选择分区字段' }],
            batchWaitInterval: [{ required: isRDB(data?.type), message: '请输入数据输出时间' }],
            batchSize: [{ required: isRDB(data?.type), message: '请输入数据输出条数' }],
        };
    };

    /**
     * 动态生成 Flink 的维表校验字段
     */
    private generateValidDesSide = (
        data: IOfflineTaskProps['side'][number],
        componentVersion?: Valueof<typeof FLINK_VERSIONS>
    ): Rules => {
        const isCacheLRU = data?.cache === 'LRU';
        const isCacheTLLMSReqiured = data?.cache === 'LRU' || data?.cache === 'ALL';
        const schemaRequired = [
            DATA_SOURCE_ENUM.POSTGRESQL,
            DATA_SOURCE_ENUM.KINGBASE8,
            DATA_SOURCE_ENUM.SQLSERVER,
            DATA_SOURCE_ENUM.SQLSERVER_2017_LATER,
        ].includes(data?.type);
        const isFlink112 = componentVersion === FLINK_VERSIONS.FLINK_1_12;

        return {
            type: [{ required: true, message: '请选择存储类型' }],
            sourceId: [{ required: true, message: '请选择数据源' }],
            table: [{ required: isHaveTableList(data?.type), message: '请选择表' }],
            tableName: [{ required: true, message: '请输入映射表名' }],
            columns: [
                {
                    required: isHaveTableColumn(data?.type),
                    message: '字段信息不能为空',
                    type: 'array',
                },
                { validator: checkColumnsData },
            ],
            columnsText: [{ required: !isHaveTableColumn(data?.type), message: '字段信息不能为空' }],
            schema: [{ required: schemaRequired, message: '请选择Schema' }],
            // 'table-input': [{ required: isRedis, message: '请输入表名' }],
            index: [{ required: isLowerES(data?.type), message: '请输入索引' }],
            esType: [{ required: isLowerES(data?.type), message: '请输入索引类型' }],
            primaryKey: [{ required: false, message: '请输入主键' }],
            // 'primaryKey-input': [{ required: isRedis || isMongoDB, message: '请输入主键' }],
            hbasePrimaryKey: [{ required: isHbase(data?.type), message: '请输入主键' }],
            hbasePrimaryKeyType: [{ required: isHbase(data?.type) && isFlink112, message: '请输入主键类型' }],
            cache: [{ required: true, message: '请选择缓存策略' }],
            cacheSize: [{ required: isCacheLRU, message: '请输入缓存大小' }],
            cacheTTLMs: [{ required: isCacheTLLMSReqiured, message: '请输入缓存超时时间' }],
        };
    };

    /**
     * 校验 Flink 的源表表单值
     */
    private validDataSource = async (
        data: IOfflineTaskProps['source'][number],
        componentVersion?: Valueof<typeof FLINK_VERSIONS>
    ) => {
        const validDes = this.generateValidDesSource(data, componentVersion);
        const validator = new ValidSchema(validDes);
        const err = await new Promise<ValidateError[] | null>((resolve) => {
            validator.validate(data, (errors) => {
                resolve(errors);
            });
        });
        return err;
    };

    /**
     * 校验 Flink 的结果表
     */
    private validDataOutput = async (
        data: IOfflineTaskProps['sink'][number],
        componentVersion?: Valueof<typeof FLINK_VERSIONS>
    ) => {
        const validDes = this.generateValidDesOutPut(data, componentVersion);
        const validator = new ValidSchema(validDes);
        const err = await new Promise<ValidateError[] | null>((resolve) => {
            validator.validate(data, (errors) => {
                resolve(errors);
            });
        });
        return err;
    };

    /**
     * 校验 Flink 维表
     */
    private validDataSide = async (
        data: IOfflineTaskProps['side'][number],
        componentVersion?: Valueof<typeof FLINK_VERSIONS>
    ) => {
        const validDes = this.generateValidDesSide(data, componentVersion);
        const validator = new ValidSchema(validDes);
        const err = await new Promise<ValidateError[] | null>((resolve) => {
            validator.validate(data, (errors) => {
                resolve(errors);
            });
        });
        return err;
    };

    private validTableData = async (currentPage: IOfflineTaskProps) => {
        const VALID_FIELDS = ['source', 'sink', 'side'] as const;
        const FIELDS_MAPPING = { source: '源表', sink: '结果表', side: '维表' } as const;
        const FIELDS_VALID_FUNCTION_MAPPING = {
            source: this.validDataSource,
            sink: this.validDataOutput,
            side: this.validDataSide,
        } as const;
        return Promise.all(
            VALID_FIELDS.map((key) => {
                const tableData = currentPage[key];
                return this.dataValidator(
                    currentPage,
                    tableData,
                    // @ts-ignore
                    FIELDS_VALID_FUNCTION_MAPPING[key],
                    FIELDS_MAPPING[key]
                );
            })
        );
    };

    private checkSide = (sides: IOfflineTaskProps['side'], componentVersion: string) => {
        if (sides) {
            // TODO: Should check all sides but now only check the fist one, fix it
            // eslint-disable-next-line
            for (let i = 0; i < sides.length; i += 1) {
                const side = sides[i];
                const { type, primaryKey, hbasePrimaryKey, hbasePrimaryKeyType } = side;
                switch (type) {
                    case DATA_SOURCE_ENUM.REDIS:
                    case DATA_SOURCE_ENUM.UPRedis: {
                        if (!primaryKey || !primaryKey.length) {
                            return `维表${i + 1}中的主键不能为空`;
                        }
                        return null;
                    }
                    case DATA_SOURCE_ENUM.HBASE:
                    case DATA_SOURCE_ENUM.TBDS_HBASE:
                    case DATA_SOURCE_ENUM.HBASE_HUAWEI: {
                        if (!hbasePrimaryKey) {
                            return `维表${i + 1}中的主键不能为空`;
                        }
                        if (!hbasePrimaryKeyType && componentVersion === '1.12') {
                            return `维表${i + 1}中的主键类型不能为空`;
                        }
                        return null;
                    }
                    default:
                        return null;
                }
            }
        }
        return null;
    };

    /**
     * 为 Flink 的源表表单生成校验规则
     */
    public generateValidDesSource = (
        data: IOfflineTaskProps['source'][number],
        componentVersion?: Valueof<typeof FLINK_VERSIONS>
    ) => {
        const isFlink112 = componentVersion === FLINK_VERSIONS.FLINK_1_12;
        const haveSchema =
            isKafka(data?.type) && isAvro(data?.sourceDataType) && componentVersion !== FLINK_VERSIONS.FLINK_1_12;

        return {
            type: [{ required: true, message: '请选择类型' }],
            sourceId: [{ required: true, message: '请选择数据源' }],
            topic: [{ required: true, message: '请选择Topic' }],
            table: [{ required: true, message: '请输入映射表名' }],
            columnsText: [{ required: true, message: '字段信息不能为空！' }],
            sourceDataType: [{ required: isKafka(data?.type), message: '请选择读取类型' }],
            schemaInfo: [{ required: !!haveSchema, message: '请输入Schema' }],
            timeColumn: [
                {
                    required:
                        (!isFlink112 && data?.timeType === SOURCE_TIME_TYPE.EVENT_TIME) ||
                        (isFlink112 && data?.timeTypeArr?.includes?.(SOURCE_TIME_TYPE.EVENT_TIME)),
                    message: '请选择时间列',
                },
            ],
            offset: [
                {
                    required:
                        (!isFlink112 && data?.timeType === SOURCE_TIME_TYPE.EVENT_TIME) ||
                        (isFlink112 && data?.timeTypeArr?.includes?.(SOURCE_TIME_TYPE.EVENT_TIME)),
                    message: '请输入最大延迟时间',
                },
            ],
        };
    };

    public transformTabDataToParams = (data: IOfflineTaskProps) => {
        const params: IOfflineTaskProps & { value?: string } = { ...data };
        params.sqlText = params.value || '';

        if (params.componentVersion === FLINK_VERSIONS.FLINK_1_12 && Array.isArray(params.source)) {
            params.source.forEach((form) => {
                if (form.timeTypeArr.includes(1)) {
                    form.procTime = form.procTime || 'proc_time';
                }
            });
        }

        return params;
    };

    /**
     * 保存当前 tab
     */
    public save = async () => {
        const currentTask = molecule.editor.getState().current?.tab;
        if (!currentTask) return Promise.reject();
        const data = currentTask.data as IParamsProps;

        const { taskType } = data;
        switch (taskType) {
            case TASK_TYPE_ENUM.SYNC: {
                return new Promise((resolve, reject) => {
                    const doSaveFn = () => {
                        const params = this.removeSeparator({ ...data });
                        // 工作流中的数据同步保存
                        if (params.flowId) {
                            // 如果是 workflow__ 开头的，表示还没有保存过的工作流节点
                            if (params.id?.toString().startsWith('workflow__')) {
                                Reflect.deleteProperty(params, 'id');
                            }
                            params.computeType = IComputeType.BATCH;
                            // 如果是没保存过的工作流节点，会获取不到 nodePid，则通过 flowId 去拿 nodePid
                            params.nodePid = params.nodePid || molecule.folderTree.get(data.flowId)?.data.parentId;
                        }

                        const isSupportSub = SUPPROT_SUB_LIBRARY_DB_ARRAY.includes(params.sourceMap.type!);

                        if (params.settingMap) {
                            params.settingMap.speed = /[\u4e00-\u9fa5]/.test(params.settingMap!.speed)
                                ? '-1'
                                : params.settingMap?.speed;
                        }

                        api.saveOfflineJobData({
                            ...params,
                            // 修改task配置时接口要求的标记位
                            preSave: true,
                            sqlText: params.value || '',
                            computeType: IComputeType.BATCH,
                            sourceMap: {
                                ...params.sourceMap,
                                sourceList: isSupportSub
                                    ? [
                                          {
                                              key: 'main',
                                              tables: params.sourceMap.table,
                                              type: params.sourceMap.type,
                                              name: params.sourceMap.name,
                                              sourceId: params.sourceMap.sourceId,
                                          },
                                      ]
                                    : [],
                                rdbmsDaType: rdbmsDaType.Poll,
                                // TODO only used in FTP source
                                halfStructureDaType: 0,
                            },
                        }).then((res) => {
                            if (res.code === 1) {
                                message.success('保存成功！');
                                this.emit(SaveEventKind.onSaveTask, res.data);
                                resolve(res);
                            }
                        });
                    };

                    // 向导模式需要 form 表单校验
                    if (data.createModel === CREATE_MODEL_TYPE.GUIDE) {
                        this.emit(SaveEventKind.onBeforeSave, {
                            continue: () => {
                                doSaveFn();
                            },
                            stop: () => {
                                reject(new Error('请检查数据同步任务是否填写正确'));
                            },
                        });
                    } else {
                        // 脚本模式直接保存
                        doSaveFn();
                    }
                });
            }
            case TASK_TYPE_ENUM.SQL: {
                const params: IParamsProps = cloneDeep(data);
                const { componentVersion, createModel, side = [] } = params;
                const isFlinkSQLGuide = createModel === CREATE_MODEL_TYPE.GUIDE || !createModel;

                /**
                 * 如果是向导模式，校验源表和结果表和维表
                 */
                if (isFlinkSQLGuide) {
                    const componentForm = rightBarService.getForm();
                    if (componentForm) {
                        // 如果 componentForm 存在表示当前 rightBar 处于展开状态并且存在 form 表单，需要先校验表单的值
                        // 后面会对校验结果做抛出，这里只需要 Form 组件里面把错误信息的状态展示出来
                        try {
                            await componentForm.validateFields();
                        } catch {}
                    }

                    // errors 的二维数组，第一维区分源表结果表维表，第二维区分具体表中的某一个源
                    const errors = await this.validTableData(params);

                    // 如果所有的结果都是 null 则表示校验全通过,否则不通过
                    if (!errors.every((tableErrors) => tableErrors.every((e) => e === null))) {
                        return Promise.reject();
                    }

                    const err = this.checkSide(side, componentVersion);
                    if (err) {
                        return Promise.reject(new Error(err));
                    }

                    params.preSave = true;
                    // 后端区分右键编辑保存
                    params.updateSource = true;

                    const realParams = this.transformTabDataToParams(params);
                    const res = await api.saveTask(realParams);

                    if (res.code === 1) {
                        message.success('保存成功！');
                        this.emit(SaveEventKind.onSaveTask, res.data);
                        return res;
                    }
                    return Promise.reject();
                }

                const { value, ...restParams } = params;
                const res = await api.saveTask({
                    ...restParams,
                    sqlText: value,
                    preSave: true,
                    // 后端区分右键编辑保存
                    updateSource: true,
                });

                if (res.code === 1) {
                    message.success('保存成功！');
                    this.emit(SaveEventKind.onSaveTask, res.data);
                    return res;
                }
                return Promise.reject();
            }
            case TASK_TYPE_ENUM.DATA_ACQUISITION: {
                const params: IParamsProps = cloneDeep(data);
                const { sourceMap, targetMap = {}, createModel } = params;

                const componentForm = rightBarService.getForm();

                if (componentForm) {
                    await componentForm.validateFields();
                }

                /**
                 * 当目标数据源为Hive时，必须勾选Json平铺
                 */
                const haveJson =
                    isKafka(sourceMap?.type) ||
                    sourceMap?.type === DATA_SOURCE_ENUM.EMQ ||
                    sourceMap?.type === DATA_SOURCE_ENUM.SOCKET;
                if (targetMap?.type === DATA_SOURCE_ENUM.HIVE && !sourceMap.pavingData && !haveJson) {
                    message.error('请勾选嵌套Json平铺后重试');
                    return Promise.reject();
                }

                params.preSave = true;
                // 后端区分右键编辑保存
                params.updateSource = true;
                params.sqlText = params.value || '';

                if (createModel === CREATE_MODEL_TYPE.GUIDE) {
                    const { distributeTable } = sourceMap;
                    /**
                     * [ {name:'table', table: []} ] => {'table':[]}
                     */
                    if (distributeTable && distributeTable.length) {
                        const newDistributeTable: any = {};
                        distributeTable.forEach((table: any) => {
                            newDistributeTable[table.name] = table.tables || [];
                        });
                        params.sourceMap = {
                            ...sourceMap,
                            distributeTable: newDistributeTable,
                        };
                    }

                    Reflect.deleteProperty(params, 'sourceParams');
                    Reflect.deleteProperty(params, 'sinkParams');
                    Reflect.deleteProperty(params, 'sideParams');
                }

                const res = await api.saveTask(params);

                if (res.code === 1) {
                    message.success('保存成功！');
                    this.emit(SaveEventKind.onSaveTask, res.data);
                    return res;
                }
                return Promise.reject();
            }
            case TASK_TYPE_ENUM.VIRTUAL: {
                const { flowId, ...restProps } = data;

                const params: Record<string, any> = {
                    ...restProps,
                    computeType: IComputeType.BATCH,
                    taskType,
                };

                // 是工作流，需要额外传 flowId 和 nodePid
                if (flowId) {
                    // 如果是 workflow__ 开头的，表示还没有保存过的工作流节点
                    if (params.id?.toString().startsWith('workflow__')) {
                        Reflect.deleteProperty(params, 'id');
                    }

                    params.flowId = flowId;
                    // 如果是没保存过的工作流节点，会获取不到 nodePid，则通过 flowId 去拿 nodePid
                    params.nodePid = params.nodePid || molecule.folderTree.get(data.flowId)?.data.parentId;
                }

                const res = await api.addOfflineTask(params);

                if (res.code === 1) {
                    message.success('保存成功！');
                    this.emit(SaveEventKind.onSaveTask, res.data);
                    return res;
                }

                return Promise.reject();
            }

            case TASK_TYPE_ENUM.WORK_FLOW: {
                let { cells } = viewStoreService.getViewStorage<{
                    cells: mxCell[];
                    geometry: IGeometryPosition;
                }>(data.id.toString());

                const unsavedCell = cells.filter((cell) => cell.vertex && cell.value[isEditing]);

                if (unsavedCell.length) {
                    const results = await Promise.all([
                        ...unsavedCell.map((cell) => this.saveTab(cell.value, { verbose: false })),
                    ]);

                    if (results.some((res) => res.code !== 1)) {
                        results
                            .filter((res) => res.code !== 1)
                            .forEach((errorRes) => {
                                message.error(errorRes.message);
                            });
                        return Promise.reject();
                    }

                    // 保存过未保存的节点后，cell 需要重新获取，因为有更新
                    cells = viewStoreService.getViewStorage<{
                        cells: mxCell[];
                        geometry: IGeometryPosition;
                    }>(data.id.toString()).cells;
                }

                const nodeMap = cells.reduce<Record<number, number[]>>((pre, cur) => {
                    if (cur.edge) {
                        const { source, target } = cur;
                        pre[target.value.id] = pre[target.value.id] ?? [];
                        pre[target.value.id].push(source.value.id);
                    }

                    if (cur.vertex && !pre[cur.value.id]) {
                        pre[cur.value.id] = [];
                    }

                    return pre;
                }, {});

                const res = await api.addOfflineTask({ ...data, nodeMap });

                if (res.code === 1) {
                    message.success('保存成功！');
                    this.emit(SaveEventKind.onSaveTask, res.data);
                    return res;
                }

                return Promise.reject();
            }
            case TASK_TYPE_ENUM.SPARK:
            case TASK_TYPE_ENUM.FLINK: {
                const params = {
                    ...data,
                    taskType,
                    updateSource: false,
                    preSave: false,
                };
                const res = await api.addOfflineTask(params);

                if (res.code === 1) {
                    message.success('保存成功！');
                    this.updateFolderAndTabAfterSave(
                        molecule.folderTree.get(params.id)?.data.parentId,
                        params.nodePid,
                        params.id,
                        params.name
                    );
                    this.emit(SaveEventKind.onSaveTask, res.data);
                    return res;
                }

                return Promise.reject();
            }
            case TASK_TYPE_ENUM.SPARK_SQL:
            case TASK_TYPE_ENUM.HIVE_SQL:
            default: {
                // 默认保存，通过把 editor 中的值给到 sqlText 进行保存
                const { value, ...restData } = data;

                // 是工作流，需要额外传 flowId 和 nodePid
                if (restData.flowId) {
                    // 如果是 workflow__ 开头的，表示还没有保存过的工作流节点
                    if (restData.id?.toString().startsWith('workflow__')) {
                        Reflect.deleteProperty(restData, 'id');
                    }

                    restData.computeType = IComputeType.BATCH;
                    // 如果是没保存过的工作流节点，会获取不到 nodePid，则通过 flowId 去拿 nodePid
                    restData.nodePid = restData.nodePid || molecule.folderTree.get(restData.flowId)?.data.parentId;
                }

                const res = await api.saveOfflineJobData({
                    ...restData,
                    sqlText: value || '',
                    // 修改task配置时接口要求的标记位
                    preSave: true,
                });

                if (res.code === 1) {
                    message.success('保存成功！');
                    this.emit(SaveEventKind.onSaveTask, res.data);
                    return res;
                }
                return Promise.reject();
            }
        }
    };

    /**
     * 保存指定 tab 的方法, 如果保存当前 tab，请使用 `taskSaveService.save()`
     * @params `verbose` 是否输出 message
     */
    public saveTab = async (params: IOfflineTaskProps, config = { verbose: true }) => {
        const { id, nodePid, ...restParams } = params;
        const res = await api.addOfflineTask({
            ...restParams,
            id: id.toString().startsWith('workflow__') ? undefined : id,
            // 如果是没保存过的工作流节点，会获取不到 nodePid，则通过 flowId 去拿 nodePid
            nodePid: nodePid || molecule.folderTree.get(restParams.flowId)?.data.parentId,
            computeType: IComputeType.BATCH,
        });

        if (res.code === 1) {
            if (config.verbose) {
                message.success('保存成功！');
            }

            const { data, code } = await api.getOfflineTaskByID<IOfflineTaskProps>({
                id: res.data.id,
            });

            // 1. 更新目标 tab 中的内容
            const isOpen = molecule.editor.isOpened(res.data.id.toString());
            if (isOpen) {
                if (code === 1) {
                    molecule.editor.updateTab({
                        id: res.data.id.toString(),
                        data,
                        status: undefined,
                    });
                }
            }

            // 2. 更新 cell 中的内容
            if (data.flowId) {
                const { cells } = viewStoreService.getViewStorage<{ cells: mxCell[] }>(data.flowId.toString());

                const targetCell = cells.find((i) => i.vertex && i.value.id === params.id);
                if (targetCell) {
                    targetCell.setValue({
                        ...targetCell.value,
                        ...data,
                        [isEditing]: false,
                    });

                    // 更新 cell 后触发工作流 tab 的视图更新
                    viewStoreService.emiStorageChange(data.flowId.toString());
                }
            }
        }

        return res;
    };

    /**
     * Call it after saving, this function would check the update of folder and tab
     */
    public async updateFolderAndTabAfterSave(
        beforeParentId: number,
        afterParentId: number,
        taskId: number,
        nextName: string,
        editTabId?: string
    ) {
        // 等待更新的文件夹目录
        const pendingUpdateFolderId = new Set([
            // 当前节点变更之前所在的文件夹
            beforeParentId,
            // 当前节点变更后所在的文件夹
            afterParentId,
        ]);

        // 1. Update these two folders' children
        await Promise.all(
            Array.from(pendingUpdateFolderId).map((id) => {
                const folderNode = molecule.folderTree.get(`${id}-folder`);
                if (folderNode) {
                    return catalogueService.loadTreeNode(
                        {
                            id: folderNode.data?.id,
                            catalogueType: folderNode.data?.catalogueType,
                        },
                        CATALOGUE_TYPE.TASK
                    );
                }
                return Promise.resolve();
            })
        );

        // 2. Ensure update the opened tab
        const isOpened = molecule.editor.isOpened(taskId.toString());
        if (isOpened) {
            molecule.editor.updateTab({
                id: taskId.toString(),
                name: nextName,
                breadcrumb: breadcrumbService.getBreadcrumb(taskId),
            });
        }

        // 3. Close edit tab
        if (editTabId) {
            const groupId = molecule.editor.getGroupIdByTab(editTabId)!;
            molecule.editor.closeTab(editTabId, groupId);
        }
    }

    /**
     * 保存任务成功的回调函数
     */
    onSaveTask = (listener: (task: IOfflineTaskProps) => void) => {
        this.subscribe(SaveEventKind.onSaveTask, listener);
    };

    /**
     * 任务保存之前的回调函数，需要执行 `action.continue` 才会继续执行后续保存操作
     */
    onBeforeSave = (listener: (action: { continue: () => void; stop: () => void }) => void) => {
        this.subscribe(SaveEventKind.onBeforeSave, listener);
    };

    unsubScribeOnBeforeSave = (listener: (action: { continue: () => void; stop: () => void }) => void) => {
        this.unsubscribe(SaveEventKind.onBeforeSave, listener);
    };
}

export default new TaskSaveService();
