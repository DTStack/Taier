import api from "@/api";
import stream from "@/api/stream";
import { CAT_TYPE, COLLECT_TYPE, DATA_SOURCE_ENUM, DATA_SYNC_TYPE, FLINK_VERSIONS, KAFKA_DATA_TYPE, NODE_TYPE, QOS_TYPE, READ_MODE_TYPE, RESTFUL_METHOD, RESTFUL_PROPTOCOL, RESTFUL_RESP_MODE, SLOAR_CONFIG_TYPE, SOURCE_TIME_TYPE, SYNC_TYPE, TABLE_SOURCE, TASK_TYPE_ENUM } from "@/constant";
import { isRDB } from "@/utils";
import { havePartition, havePrimaryKey, haveTableColumn, haveTableList, haveTopic, isAvro, isKafka, isMysqlTypeSource } from "@/utils/enums";
import molecule from "@dtinsight/molecule";
import { message, Modal, Tag } from "antd";
import ValidSchema, { RuleType } from 'async-validator';
import { cloneDeep, isEmpty } from "lodash";

const confirm = Modal.confirm;
let _callbackList: any[] = [];

export const assetValidRules = {
    dbId: [
        { required: true, message: '请选择数据库' }
    ],
    tableId: [
        { required: true, message: '请选择数据表' }
    ],
    columns: [
        { required: true, message: '字段信息不能为空', type: 'array' as RuleType },
        { validator: checkColumnsData }
    ]
}

// 校验字段信息
export function checkColumnsData(rule: any, value: any, callback: any, source: any) {
    if (haveTableColumn(source?.type)) {
        if (isEmpty(value) || value?.some((item: any) => isEmpty(item))) {
            let err = '请填写字段信息';
            return callback(err);
        }
        if (value?.some((item: { type: string; }) => item.type?.toLowerCase() === 'Not Support'.toLowerCase())) {
            let err = '字段中存在不支持的类型，请重新填写';
            return callback(err);
        }
    }
    callback();
}

export const generateValidDesSource = function (data: any, componentVersion?: string) {
    const isFlink112 = componentVersion == FLINK_VERSIONS.FLINK_1_12
    const haveSchema = isKafka(data?.type) && isAvro(data?.sourceDataType) &&
        componentVersion !== FLINK_VERSIONS.FLINK_1_12;

    const isCreateByStream = data?.createType !== TABLE_SOURCE.DATA_ASSET;
    const createByStreamRules = {
        type: [
            { required: true, message: '请选择类型' }
        ],
        sourceId: [
            { required: true, message: '请选择数据源' }
        ],
        topic: [
            { required: true, message: '请选择Topic' }
        ],
        table: [
            { required: true, message: '请输入映射表名' }
        ],
        columnsText: [
            { required: true, message: '字段信息不能为空！' }
        ]
    }
    return Object.assign({}, isCreateByStream ? createByStreamRules : assetValidRules, {
        sourceDataType: [
            { required: isKafka(data?.type), message: '请选择读取类型' }
        ],
        schemaInfo: [
            { required: haveSchema, message: '请输入Schema' }
        ],
        timeColumn: [
            {
                required: (!isFlink112 && data?.timeType === SOURCE_TIME_TYPE.EVENT_TIME) ||
                    (isFlink112 && data?.timeTypeArr?.includes?.(SOURCE_TIME_TYPE.EVENT_TIME)),
                message: '请选择时间列'
            }
        ],
        offset: [
            {
                required: (!isFlink112 && data?.timeType === SOURCE_TIME_TYPE.EVENT_TIME) ||
                    (isFlink112 && data?.timeTypeArr?.includes?.(SOURCE_TIME_TYPE.EVENT_TIME)),
                message: '请输入最大延迟时间'
            }
        ]
    })
}

export const generateValidDesOutPut = function (data: any, componentVersion?: string) {
    const isCreateByStream = data?.createType !== TABLE_SOURCE.DATA_ASSET;
    const isHbase = [DATA_SOURCE_ENUM.HBASE, DATA_SOURCE_ENUM.TBDS_HBASE, DATA_SOURCE_ENUM.HBASE_HUAWEI].includes(data?.type);
    const isRedis = [DATA_SOURCE_ENUM.REDIS, DATA_SOURCE_ENUM.UPRedis].includes(data?.type);
    const isESType = data?.type == DATA_SOURCE_ENUM.ES || data.type == DATA_SOURCE_ENUM.ES6;
    const schemaRequired = [DATA_SOURCE_ENUM.POSTGRESQL, DATA_SOURCE_ENUM.KINGBASE8, DATA_SOURCE_ENUM.SQLSERVER, DATA_SOURCE_ENUM.SQLSERVER_2017_LATER].includes(data?.type);
    const isS3 = [DATA_SOURCE_ENUM.S3, DATA_SOURCE_ENUM.CSP_S3].includes(data?.type);
    const isFlink112 = componentVersion === '1.12'
    const createByStreamRules = {
        type: [
            { required: true, message: '请选择存储类型' }
        ],
        sourceId: [
            { required: true, message: '请选择数据源' }
        ],
        topic: [
            { required: haveTopic(data?.type), message: '请选择Topic' }
        ],
        table: [
            { required: haveTableList(data?.type) && !isS3, message: '请选择表' }
        ],
        tableName: [
            { required: true, message: '请输入映射表名' }
        ],
        columns: [
            { required: haveTableColumn(data?.type), message: '字段信息不能为空', type: 'array' as RuleType },
            { validator: checkColumnsData }
        ],
        columnsText: [
            { required: !haveTableColumn(data?.type), message: '字段信息不能为空' }
        ]
    }
    return Object.assign({}, isCreateByStream ? createByStreamRules : assetValidRules, {
        createType: [
            { required: true, message: '请选择表来源' }
        ],
        collection: [
            { required: data?.type === DATA_SOURCE_ENUM.SOLR, message: '请选择Collection' }
        ],
        objectName: [
            { required: isS3, message: '请输入ObjectName' }
        ],
        schema: [
            { required: schemaRequired, message: '请选择schema' }
        ],
        partitionfields: [
            { required: havePartition(data?.type) && data?.isShowPartition && data?.havePartitionfields, message: '请选择分区' }
        ],
        'table-input': [
            { required: isRedis, message: '请输入表名' }
        ],
        index: [
            { required: isESType, message: '请输入索引' }
        ],
        'primaryKey-input': [
            { required: isRedis, message: '请输入主键' }
        ],
        esType: [
            { required: isESType, message: '请输入索引类型' }
        ],
        rowKey: [
            { required: isHbase, message: '请输入rowKey' }
        ],
        rowKeyType: [{ required: isHbase && isFlink112, message: '请输入rowKey类型' }],
        sinkDataType: [
            { required: isKafka(data?.type), message: '请选择输出类型！' }
        ],
        updateMode: [
            { required: true, message: '请选择更新模式' }
        ],
        primaryKey: [
            { required: data?.updateMode == 'upsert' && havePrimaryKey(data?.type), message: '请输入主键' }
        ],
        partitionKeys: [
            { required: data?.enableKeyPartitions, message: '请选择分区字段' }
        ],
        batchWaitInterval: [
            { required: isRDB(data?.type), message: '请输入数据输出时间' }
        ],
        batchSize: [
            { required: isRDB(data?.type), message: '请输入数据输出条数' }
        ]
    })
}
export const generateValidDesSide = function (data: any, componentVersion?: string) {
    const isCreateByStream = data?.createType !== TABLE_SOURCE.DATA_ASSET;
    const isRedis = [DATA_SOURCE_ENUM.REDIS, DATA_SOURCE_ENUM.UPRedis].includes(data?.type);
    const isMongoDB = data?.type === DATA_SOURCE_ENUM.MONGODB;
    const isHbase = [DATA_SOURCE_ENUM.HBASE, DATA_SOURCE_ENUM.TBDS_HBASE, DATA_SOURCE_ENUM.HBASE_HUAWEI].includes(data?.type);
    const isESType = data?.type === DATA_SOURCE_ENUM.ES || data?.type === DATA_SOURCE_ENUM.ES6;
    const isCacheLRU = data?.cache === 'LRU'
    const isCacheTLLMSReqiured = data?.cache === 'LRU' || data?.cache === 'ALL'
    const schemaRequired = [DATA_SOURCE_ENUM.POSTGRESQL, DATA_SOURCE_ENUM.KINGBASE8, DATA_SOURCE_ENUM.SQLSERVER, DATA_SOURCE_ENUM.SQLSERVER_2017_LATER].includes(data?.type);
    const isFlink112 = componentVersion === '1.12'
    const createByStreamRules = {
        type: [
            { required: true, message: '请选择存储类型' }
        ],
        sourceId: [
            { required: true, message: '请选择数据源' }
        ],
        table: [
            { required: haveTableList(data?.type), message: '请选择表' }
        ],
        tableName: [
            { required: true, message: '请输入映射表名' }
        ],
        columns: [
            { required: haveTableColumn(data?.type), message: '字段信息不能为空', type: 'array' as RuleType },
            { validator: checkColumnsData }
        ],
        columnsText: [
            { required: !haveTableColumn(data?.type), message: '字段信息不能为空' }
        ]
    }
    return Object.assign({}, isCreateByStream ? createByStreamRules : assetValidRules, {
        schema: [
            { required: schemaRequired, message: '请选择Schema' }
        ],
        'table-input': [
            { required: isRedis, message: '请输入表名' }
        ],
        index: [
            { required: isESType, message: '请输入索引' }
        ],
        esType: [
            { required: isESType, message: '请输入索引类型' }
        ],
        primaryKey: [
            { required: false, message: '请输入主键' }
        ],
        'primaryKey-input': [
            { required: isRedis || isMongoDB, message: '请输入主键' }
        ],
        hbasePrimaryKey: [
            { required: isHbase, message: '请输入主键' }
        ],
        hbasePrimaryKeyType: [
            { required: isHbase && isFlink112, message: '请输入主键类型' }
        ],
        cache: [
            { required: true, message: '请选择缓存策略' }
        ],
        cacheSize: [
            { required: isCacheLRU, message: '请输入缓存大小' }
        ],
        cacheTTLMs: [
            { required: isCacheTLLMSReqiured, message: '请输入缓存超时时间' }
        ]
    })
}



export const validDataSource = async (data: any, componentVersion?: string) => {
    const validDes = generateValidDesSource(data, componentVersion);
    for (let callback of _callbackList) {
        let err = await callback.func(data);
        if (err) {
            return err;
        }
    }
    const validator = new ValidSchema(validDes);
    const err = await new Promise((resolve) => {
        validator.validate(data, (errors: any, fields: any) => {
            resolve(errors)
        })
    })
    return err
}

export const validDataOutput = async (data: any, componentVersion?: string) => {
    const validDes = generateValidDesOutPut(data, componentVersion);
    for (let callback of _callbackList) {
        let err = await callback.func(data);
        if (err) {
            return err;
        }
    }
    var validator = new ValidSchema(validDes);
    const err = await new Promise((resolve) => {
        validator.validate(data, (errors: any, fields: any) => {
            resolve(errors)
        })
    })
    return err
}

export const validDataSide = async (data: any, componentVersion?: string) => {
    const validDes = generateValidDesSide(data, componentVersion);
    for (let callback of _callbackList) {
        let err = await callback.func(data);
        if (err) {
            return err;
        }
    }
    var validator = new ValidSchema(validDes);
    const err = await new Promise((resolve) => {
        validator.validate(data, (errors: any) => {
            resolve(errors)
        })
    })
    return err
}

export const dataValidator = async (currentPage: any, data: any[], validator: (arg0: any, arg1: any) => any, text: string) => {
    const { componentVersion } = currentPage;
    for (let i = 0; i < data.length; i++) {
        const item = data[i];
        const error: any = await validator(item, componentVersion);
        if (error) {
            console.error(error);
            const tableName = item?.tableName;
            message.error(`${text} ${i + 1} ${tableName ? `(${tableName})` : ''}: ${error[0].message}`);
            return error;
        }
    }
}

export const validTableData = async (currentPage: any, data: any) => {
    let error
    for (const key in data) {
        if (Object.prototype.hasOwnProperty.call(data, key)) {
            const tableData = data[key];
            switch (key) {
                case 'source':
                    error = await dataValidator(currentPage, tableData, validDataSource, '源表')
                    if (error) return error
                    break;
                case 'sink':
                    error = await dataValidator(currentPage, tableData, validDataOutput, '结果表')
                    if (error) return error
                    break;
                case 'side':
                    error = await dataValidator(currentPage, tableData, validDataSide, '维表')
                    if (error) return error
                    break;
                default:
                    return error
            }
        }
    }
}

export const preparePage = (page: any) => {
    page = { ...page };
    const { taskType, createModel, sink = [] } = page;
    if (taskType == TASK_TYPE_ENUM.DATA_COLLECTION && createModel == DATA_SYNC_TYPE.GUIDE) {
        const { sourceMap = {} } = page;
        const { distributeTable } = sourceMap;
        /**
         * [ {name:'table', table: []} ] => {'table':[]}
         */
        if (distributeTable && distributeTable.length) {
            let newDistributeTable: any = {};
            distributeTable.map((table: any) => {
                newDistributeTable[table.name] = table.tables || [];
            })
            page.sourceMap = {
                ...sourceMap,
                distributeTable: newDistributeTable
            }
        }
    }

    // 对结果表中多余参数进行处理
    Array.isArray(sink) && sink.forEach((pane: any) => {
        if ('havePartitionfields' in pane) {
            delete pane.havePartitionfields;
        }
    })
    return page;
}

export function cleanCollectionParams(data: any) {
    let newData = cloneDeep(data);
    if (newData.taskType != TASK_TYPE_ENUM.DATA_COLLECTION) {
        return data;
    }
    const { sourceMap = {}, targetMap = {} } = newData;
    if (!sourceMap || !targetMap) {
        return data;
    }
    const isMysqlSource = isMysqlTypeSource(sourceMap.type)
    if (!isMysqlSource) {
        targetMap.analyticalRules = undefined;
    }
    return newData;
}

export function resolveGraphData(page: any) {
    const newPage = cloneDeep(page);
    const { graphData } = newPage;
    if (!graphData) {
        return [];
    }
    let newGraphData = [];
    let source;
    let sink;
    let edgesMap: any = {}; // 线
    let nameMap: any = {};
    for (let i = 0; i < graphData.length; i++) {
        const node = graphData[i];
        if (node.vertex) {
            if (nameMap[node.name]) {
                message.error('不能存在同名节点！')
                return;
            }
            nameMap[node.name] = true;
            /**
             * 转换到componentType
             */
            node.componentType = node.nodeType;
            if (node.data) {
                node.data.componentType = node.componentType;
            }
            /**
             * source和sink要单独拿出来
             */
            if (node.componentType == NODE_TYPE.KAFKA_11) {
                source = node;
            } else if (node.componentType == NODE_TYPE.MYSQL_RESULT) {
                sink = node;
            } else if (node.componentType == NODE_TYPE.FILTER) {
                if (!node?.data?.whereClause) {
                    message.error('Filter节点的过滤规则不能为空！');
                    return null;
                }
            } else if (node.componentType == NODE_TYPE.WINDOW) {
                const functions = node.data?.functions;
                if (functions) {
                    for (let func of functions) {
                        if (!func.alias) {
                            message.error('Window节点中缺少别名！');
                            return null;
                        }
                    }
                }
                if (!node.data?.interval?.interval || !node.data?.hopInterval?.interval) {
                    message.error('Window节点的周期属性不能为空');
                    return null;
                }
            }
        } else if (node.edge) {
            const sourceId = node.source.id;
            const targetId = node.target.id;
            edgesMap[sourceId] = targetId;
        }
    }
    if (!source) {
        message.error('数据源表必须存在');
        return null;
    }
    if (!sink) {
        message.error('数据结果表必须存在');
        return null;
    }
    newGraphData.push(source);
    let next = edgesMap[source.id]
    while (next) {
        const nextData = graphData.find((d: any) => {
            return d.id == next;
        });
        newGraphData.push(nextData);
        next = edgesMap[nextData.id];
    }
    if (newGraphData.length !== graphData.filter((d: any) => {
        return d.vertex
    }).length) {
        message.error('所有节点必须连线');
        return null;
    }
    newPage.graphData = newGraphData;
    newPage.source = [source.data];
    newPage.sink = [sink.data];
    return newPage;
}

export interface SettingMap {
    speed?: number | string;
    readerChannel?: number;
    writerChannel?: number;
    isSaveDirty?: boolean;
    sourceId?: number;
    tableName?: string;
    lifeDay?: number;
}
export const streamTaskActions = {
    initState: { // 实时任务初始化数据
        currentStep: null,
        sourceMap: {
            type: undefined,
            port: undefined,
            table: [],
            sourceId: undefined,
            collectType: COLLECT_TYPE.ALL,
            cat: [CAT_TYPE.INSERT, CAT_TYPE.UPDATE, CAT_TYPE.DELETE],
            pavingData: false,
            rdbmsDaType: SYNC_TYPE.BINLOG,
            startSCN: undefined,
            codec: 'plain',
            qos: QOS_TYPE.EXACTLY_ONCE,
            isCleanSession: true,
            parse: 'text',
            decoder: READ_MODE_TYPE.LENGTH,
            codecType: 'text',
            collectPoint: 'taskRun',
            requestMode: RESTFUL_METHOD[1].value,
            decode: RESTFUL_RESP_MODE[0].value,
            protocol: RESTFUL_PROPTOCOL[0].value,
            startLocation: undefined,
            temporary: false,
            slotConfig: Number(Object.keys(SLOAR_CONFIG_TYPE)[0]),
            mode: 'group-offsets'
        },
        targetMap: {
            sourceId: undefined,
            topic: undefined,
            isCleanSession: true,
            qos: QOS_TYPE.EXACTLY_ONCE
        },
        settingMap: { // 通道控制
            speed: '-1',
            readerChannel: '1',
            writerChannel: 1
        }
    },
    /**
     * 获取当前任务数据
     * @returns 
     */
    getCurrentPage() {
        const state = molecule.editor.getState()
        return cloneDeep(state.current?.tab?.data);
    },
    /**
     * 更新当前任务数据
     * @param key 字段名
     * @param value 对应字段值
     * @param isDirty 是否有修改任务
     */
    setCurrentPageValue(key: any, value: any, isDirty?: any) {
        const page = this.getCurrentPage()
        const state = molecule.editor.getState()
        const tab: any = state.current?.tab || {};

        page[key] = value;
        if (typeof isDirty == 'boolean') {
            page.notSynced = isDirty
        }
        
        tab['data'] = page;
        molecule.editor.updateTab(tab)
    },
    setCurrentPage(data: any) {
        const state = molecule.editor.getState()
        const tab: any = state.current?.tab || {};
        tab['data'] = data;
        molecule.editor.updateTab(tab)
    },
    updateCurrentPage(data: any) {
        let page = this.getCurrentPage()
        page = Object.assign({}, page, data);

        this.setCurrentPage(page)
    },
    navtoStep(step: number) {
        this.setCurrentPageValue('currentStep', step);
    },
    exchangeDistributeTable(distributeTable: any) {
        if (!distributeTable) {
            return [];
        }
        const KeyAndValue = Object.entries(distributeTable);
        return KeyAndValue.map(([name, tables]) => {
            return {
                name,
                tables,
                isSaved: true
            }
        })
    },
    getDataSource() {
        api.getOfflineDataSource({ pageSize: 500, currentPage: 1, name: '', groupTags: [] })
            .then((res: any) => {
                const data = res?.data?.data || []
                this.setCurrentPageValue('dataSourceList', data);
            });
    },
    /**
     * 获取实时采集task初始化信息
     * @param {Int} taskId
     */
    initCollectionTask(taskId: any) {
        const page = this.getCurrentPage();
        /**
         * 假如已经存在这个属性，则说明当前的task不是第一次打开，所以采用原来的数据
         */
        if (typeof page.currentStep != 'undefined') {
            return;
        }
        // initCurrentPage(dispatch);
        if (page.taskVersions && page.taskVersions.length) {
            this.setCurrentPageValue('isEdit', true);
        }
        api.getOfflineJobData({
            taskId
        }).then((res: any) => {
            if (!isEmpty(res.data)) {
                const { sourceMap, targetMap } = res.data;
                sourceMap.pavingData = !!sourceMap.pavingData;
                sourceMap.multipleTable = sourceMap.multipleTable || false;
                if (sourceMap.journalName || sourceMap.startSCN) {
                    sourceMap.collectType = COLLECT_TYPE.FILE;
                } else if (sourceMap.timestamp) {
                    sourceMap.collectType = COLLECT_TYPE.TIME;
                } else if (sourceMap.lsn) {
                    sourceMap.collectType = COLLECT_TYPE.LSN;
                } else {
                    sourceMap.collectType = COLLECT_TYPE.ALL;
                }
                if (sourceMap.distributeTable) {
                    sourceMap.distributeTable = this.exchangeDistributeTable(sourceMap.distributeTable);
                    sourceMap.multipleTable = true;
                }
                /**
                 * 针对 kafka 的 codec, timestamp 做处理
                 */
                if (isKafka(sourceMap.type)) {
                    sourceMap.collectType = undefined;
                    sourceMap.codec = sourceMap.codec || KAFKA_DATA_TYPE.TYPE_COLLECT_JSON;
                }
                if ([DATA_SOURCE_ENUM.KAFKA, DATA_SOURCE_ENUM.KAFKA_2X, DATA_SOURCE_ENUM.TBDS_KAFKA, DATA_SOURCE_ENUM.KAFKA_HUAWEI].includes(targetMap.type)) {
                    targetMap.dataSequence = targetMap.dataSequence || false;
                }
                this.updateSourceMap(res.data.sourceMap, false, true);
                this.updateTargetMap(res.data.targetMap, false, true);
                this.updateChannelControlMap(res.data.setting, false, true);
                this.setCurrentPageValue('currentStep', 3);
            } else {
                this.setCurrentPageValue('currentStep', 0);
            }
        })
    },

    updateSourceMap(params: any = {}, clear: any = false, notDirty: any = false) {
        const page = this.getCurrentPage();
        let { sourceMap = {} } = page;
        if (clear) {
            sourceMap = {
                ...this.initState.sourceMap,
                type: sourceMap.type,
                sourceId: sourceMap.sourceId,
                rdbmsDaType: sourceMap.rdbmsDaType,
                multipleTable: false,
                pavingData: sourceMap.type == DATA_SOURCE_ENUM.POSTGRESQL,
                codec: isKafka(sourceMap.type) ? KAFKA_DATA_TYPE.TYPE_COLLECT_JSON : 'plain'
            };
            this.setCurrentPageValue('targetMap', cloneDeep(this.initState.targetMap));
        }
        if (params.distributeTable) {
            let tables = params.distributeTable.reduce((prevValue: any, item: any) => {
                return prevValue.concat(item.tables);
            }, []);
            params.table = tables;
        }
        this.setCurrentPageValue('sourceMap',
            cloneDeep({
                ...sourceMap,
                ...params
            }),
            !notDirty
        )
    },

    updateTargetMap(params = {}, clear: any, notDirty: boolean = false) {
        const page = this.getCurrentPage();
        let { targetMap = {} } = page;
        if (clear) {
            targetMap = this.initState.targetMap;
        }
        this.setCurrentPageValue('targetMap',
            cloneDeep({
                ...targetMap,
                ...params
            }),
            notDirty ? undefined : true
        )
    },

    updateChannelControlMap(params: SettingMap = {}, clear: any = false, notDirty: boolean = false) {
        console.log(this)
        const page = this.getCurrentPage();
        let { settingMap = {} } = page;
        if (clear) {
            settingMap = this.initState.settingMap;
        }
        if (params.isSaveDirty == false) {
            params.sourceId = undefined;
            params.tableName = undefined;
            params.lifeDay = undefined;
        } else if (params.isSaveDirty) {
            params.lifeDay = 90;
        }
        this.setCurrentPageValue('settingMap',
            cloneDeep({
                ...settingMap,
                ...params
            }),
            !notDirty
        )
    }
}

// 对多资源附加资源 resourceTree 的处理
// 初始时，在基础上拼接没请求到的 additionalResourceList
export function joinAdditionTree (tree: any, addtionalTree: any[], resourceTree?: any[]) {
    const sourceTree = resourceTree ? [...addtionalTree, ...resourceTree] : addtionalTree;
    for (let i = 0; i < tree.length; i++) {
        const nodeTree = tree[i];
        const children = nodeTree.children = nodeTree.children || [];
        if (!(nodeTree.type === 'folder')) {
            break;
        }
        if (!nodeTree.children) {
            nodeTree.children = [];
        }
        if (children.length > 0) {
            joinAdditionTree(children, addtionalTree, resourceTree)
        }
        const childrenList = sourceTree.filter((item: any) => item.nodePid === nodeTree?.id);
        if (childrenList.length > 0) {
            childrenList.forEach((child: any) => {
                if (!children.map((item: any) => item.id).includes(child.id)) {
                    const nodeChild = formatTreeChild(nodeTree, child);
                    children.push(nodeChild);
                }
            })
        }
    }
    return tree;
}

// 对 node 参数稍作处理
function formatTreeChild (parent: any, child: any) {
    const result = {
        ...child,
        parentId: child.nodePid,
        name: child.resourceName,
        level: parent.level + 1,
        type: 'file',
        catalogueType: null,
        readWriteLockVO: null,
        children: null,
        taskType: null,
        createUser: child.createUser || ''
    };
    return result;
}

// 遍历树形节点，用新节点替换老节点
export function replaceTreeNode (treeNode: any, replace: any) {
    if (
        treeNode.id === parseInt(replace.id, 10) && treeNode.type == replace.type
    ) {
        // 多级目录的情况处理
        const replaceChildren = replace.children || [];
        const regionChildren = treeNode.children || [];
        replaceChildren.forEach((child: any) => {
            const sameChild = regionChildren.find((item: any) => item.id === child.id);
            if (sameChild && sameChild.children && sameChild.children.length && !child.children) {
                child.children = sameChild.children;
            }
        })
        treeNode = Object.assign(treeNode, replace);
        return;
    }
    if (treeNode.children) {
        const children = treeNode.children
        for (let i = 0; i < children.length; i += 1) {
            replaceTreeNode(children[i], replace)
        }
    }
}

interface Task {
    componentVersion: string;
    source: {
        offset: number;
        offsetUnit: string;
        timeType: number;
        timeTypeArr: number[];
        procTime: string;
    }[];
}

/**
 * 切换 flink 版本时，源表的时间特征会在 timeType 和 timeTypeArr 两个字段切换
 * flink1.12 时 timeTypeArr 字段只在前端使用
 * @param oldTask - 用于比较 componentVersion 是否变化
 * @param newTask - 用于接口传参的 task，会改变 newTask 本身！
 */
export function transformTimeType (oldTask: Task, newTask: Task) {
    // 切换为 1.12 时，根据 timeType 修改 timeTypeArr
    if (oldTask.componentVersion !== '1.12' && newTask.componentVersion === '1.12') {
        for (const form of (newTask?.source || [])) {
            form.timeTypeArr = form.timeType === SOURCE_TIME_TYPE.EVENT_TIME ? [1, 2] : [SOURCE_TIME_TYPE.PROC_TIME]
            // timeType 勾选 procTime 时，带上默认名称
            form.procTime = 'proc_time'
        }
    }
    // 1.12 切换为其他版本时，根据 timeTypeArr 修改 timeType
    if (oldTask.componentVersion === '1.12' && newTask.componentVersion !== '1.12') {
        for (const form of (newTask?.source || [])) {
            form.timeType = form.timeTypeArr?.includes(SOURCE_TIME_TYPE.EVENT_TIME) ? SOURCE_TIME_TYPE.EVENT_TIME : SOURCE_TIME_TYPE.PROC_TIME
        }
    }
}

/**
 * 切换 flink 版本时，源表的 offset 的时间单位需要进行转化
 * @param oldTask - 用于比较 componentVersion 是否变化
 * @param newTask - 用于接口传参的 task，会改变 newTask 本身！
 */
export function transformOffsetUnit (oldTask: Task, newTask: Task) {
    // 切换为 1.12 时，时间单位由 ms 变为 s
    if (oldTask.componentVersion !== '1.12' && newTask.componentVersion === '1.12') {
        for (const form of (newTask?.source || [])) {
            form.offset = form.offset / 1000
            form.offsetUnit = 'SECOND'
        }
    }
    // 1.12 切换为其他版本时，单位转化为 ms
    if (oldTask.componentVersion === '1.12' && newTask.componentVersion !== '1.12') {
        // 各单位转换为秒
        const unit: any = {
            SECOND: 1,
            MINUTE: 60,
            HOUR: 3600,
            DAY: 86400,
            MONTH: 86400 * 30,
            YEAR: 86400 * 365
        }
        for (const form of (newTask?.source || [])) {
            form.offset = unit[form.offsetUnit] * form.offset * 1000
            // flink 版本不是 1.12 也不会读取这个字段，不改也行
            form.offsetUnit = 'ms'
        }
    }
}


