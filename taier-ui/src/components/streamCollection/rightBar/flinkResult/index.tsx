/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { TAB_WITHOUT_DATA } from "@/pages/rightBar";
import { IEditor } from "@dtinsight/molecule/esm/model";
import { Button, Collapse, Popover } from "antd";
import { DeleteOutlined, ExclamationCircleFilled, PlusOutlined } from "@ant-design/icons"
import classNames from "classnames";
import { cloneDeep, isEmpty } from "lodash";
import { useEffect, useMemo, useReducer, useState } from "react";
import { streamTaskActions } from "../../taskFunc";
import { DATA_SOURCE_ENUM, KAFKA_DATA_TYPE, TABLE_SOURCE, TABLE_TYPE } from "@/constant";
import { getCreateTypes, getDataBaseList } from "../panelData";
import stream from "@/api/stream";
import { haveCollection, havePartition, haveSchema, haveTableColumn, haveTableList, haveTopic, isAvro, isKafka, isSqlServer } from "@/utils/enums";
import LockPanel from "../lockPanel";
import { Utils } from "@dtinsight/dt-utils/lib";
import { changeCustomParams, initCustomParam } from "../customParamsUtil";
import { isRDB } from "@/utils";
import ResultForm from "./form";

const Panel = Collapse.Panel;

interface IResultState {
    tabTemplate: any[]; // 模版存储,所有输出源(记录个数)
    panelActiveKey: any[]; // 输出源是打开或关闭状态
    popoverVisible: any[]; // 删除显示按钮状态
    panelColumn: any[]; // 存储数据
    checkFormParams: any[]; // 存储要检查的参数from
    originOptionType: any[]; // 数据源选择数据
    schemaOptionType: any[]; // schema 数据
    tableOptionType: any[]; // 表选择数据
    assetTableOptionType: any[]; // 元数据 - 表选择数据
    topicOptionType: any[]; // topic 列表
    tableColumnOptionType: any[]; // 表字段选择的类型
    partitionOptionType: any[]; // 分区列表
    sync: boolean;
}

export default function FlinkResultPanel({ current }: Pick<IEditor, 'current'>) {
    const currentPage = current?.tab?.data || {};
    const isLocked = currentPage.readWriteLockVO && !currentPage.readWriteLockVO.getLock;

    const [state, dispathState] = useReducer((state: IResultState, data: any)=> {
        let newState = cloneDeep(state);
        newState = Object.assign(newState, data)
        return newState;
    }, {
        tabTemplate: [],
        panelActiveKey: [],
        popoverVisible: [],
        panelColumn: [],
        checkFormParams: [],
        originOptionType: [],
        schemaOptionType: [],
        tableOptionType: [],
        assetTableOptionType: [],
        topicOptionType: [],
        tableColumnOptionType: [],
        partitionOptionType: [],
        sync: false
    });
    let { tabTemplate, panelActiveKey, panelColumn, originOptionType, schemaOptionType, popoverVisible,
        checkFormParams,
        tableOptionType, assetTableOptionType, tableColumnOptionType, topicOptionType, partitionOptionType, sync } = state;

    const [createTypes, setCreateTypes] = useState<any[]>([])
    const [dataBaseOptionType, setDataBaseOptionType] = useState<any>([]);
    const [resultTableTypes, setResultTableTypes] = useState<any>([]);
    const getCreateType = async () => {
        const list = await getCreateTypes();
        setCreateTypes(list);
        initDataBaseList(list)
    }
    const initDataBaseList = async (createTypes: any[] = []) => {
        const list = await getDataBaseList(createTypes);
        setDataBaseOptionType(list)
    }

    const getResultTableTypes = async () => {
        const res = await stream.getResultTableTypes();
        if (res.code == 1) {
            setResultTableTypes(res.data || [])
        }
    }

    const setOutputData = (data: any, notSynced: boolean = false) => {
        const dispatchSource: any = { ...state, ...data };
        streamTaskActions.setCurrentPageValue('sink', dispatchSource, notSynced)
    }
    const handleActiveKey = (key: any) => {
        setOutputData({ panelActiveKey: key })
        dispathState({
            panelActiveKey: key
        })
    }
    const handlePopoverVisibleChange = (e: any, index: any, visible: any) => {
        let popoverVisibleScop = popoverVisible
        popoverVisibleScop[index] = visible;
        if (e) {
            e.stopPropagation();// 阻止删除按钮点击后冒泡到panel
            if (visible) { // 只打开一个Popover提示
                popoverVisibleScop = popoverVisibleScop.map((v: any, i: any) => {
                    return index == i
                })
            }
        }
        setOutputData({ popoverVisible: popoverVisibleScop })
        dispathState({ popoverVisible: popoverVisibleScop })
    }
    /**
     * 获取数据源列表
     */
    const getTypeOriginData = (index: any, type: any) => {
        stream.getTypeOriginData({ type }).then((v: any) => {
            if (index === 'add') {
                if (v.code === 1) {
                    originOptionType.push(v.data)
                } else {
                    originOptionType.push([])
                }
            } else {
                if (v.code === 1) {
                    originOptionType[index] = v.data;
                } else {
                    originOptionType[index] = [];
                }
            }
            setOutputData({ originOptionType });
            dispathState({
                originOptionType
            })
        })
    }

    /**
     * 获取Schema列表
     */
    const getSchemaData = (index: any, sourceId: any, searchKey?: any) => {
        if (sourceId) {
            stream.listSchemas({ sourceId, searchKey }).then((v: any) => {
                if (index === 'add') {
                    if (v.code === 1) {
                        schemaOptionType.push(v.data)
                    } else {
                        schemaOptionType.push([])
                    }
                } else {
                    if (v.code === 1) {
                        schemaOptionType[index] = v.data;
                    } else {
                        schemaOptionType[index] = [];
                    }
                }
                setOutputData({ schemaOptionType });
                dispathState({
                    schemaOptionType
                })
            })
        } else {
            if (index === 'add') {
                schemaOptionType.push([]);
            } else {
                schemaOptionType[index] = [];
            }
            setOutputData({ schemaOptionType });
            dispathState({
                schemaOptionType
            })
        }
    }
    // 对下拉列表请求体进行统一处理
    const setOptionTypeList = (res: any, index: any, type: keyof IResultState) => {
        const optionType = cloneDeep(state[type] as any[]);
        if (index === 'add') {
            if (res.code === 1) {
                optionType.push(res.data);
            } else {
                optionType.push([]);
            }
        } else {
            if (res.code === 1) {
                optionType[index] = res.data;
            } else {
                optionType[index] = [];
            }
        }
        return optionType;
    }
    /**
     * 获取表列表
     */
    const getTableType = async (index: any, sourceId: any, schema?: any, searchKey?: any) => {
        // postgresql schema必填处理
        const disableReq = (panelColumn[index]?.type === DATA_SOURCE_ENUM.POSTGRESQL || panelColumn[index]?.type === DATA_SOURCE_ENUM.KINGBASE8 || isSqlServer(panelColumn[index]?.type)) && !schema;

        let tableOptionTypeScop = cloneDeep(tableOptionType);
        if (sourceId && !disableReq) {
            const res = await stream.listTablesBySchema({ sourceId, schema: schema || '', 'isSys': false, searchKey });
            tableOptionTypeScop = setOptionTypeList(res, index, 'tableOptionType')
        } else {
            if (index === 'add') {
                tableOptionTypeScop.push([]);
            } else {
                tableOptionTypeScop[index] = [];
            }
        }
        setOutputData({ tableOptionType: tableOptionTypeScop });
        dispathState({
            tableOptionType: tableOptionTypeScop
        })
    }
    /**
     * 获取表字段列表
     */
    const getTableColumns = (index: any, sourceId: any, tableName: any, schema = '') => {
        if (!sourceId || !tableName) {
            return;
        }
        stream.getStreamTableColumn({
            sourceId,
            tableName,
            schema,
            flinkVersion: currentPage?.componentVersion
        }).then((v: any) => {
            if (v.code === 1) {
                tableColumnOptionType[index] = v.data;
            } else {
                tableColumnOptionType[index] = []
            }
            setOutputData({ tableColumnOptionType })
            dispathState({
                tableColumnOptionType
            })
        })
    }
    const loadPartitions = async (index: any, sourceId: any, tableName: any) => {
        if (!sourceId || !tableName) {
            return;
        }

        // 先 false，防止正在请求时还无数据展示错误
        panelColumn[index]['isShowPartition'] = false

        // hive 数据源不用 getTableType，直接 getPartition
        if ([DATA_SOURCE_ENUM.HIVE, DATA_SOURCE_ENUM.INCEPTOR].includes(panelColumn[index].type)) {
            getPartitionLists(index, sourceId, tableName)
            return
        }
        const res = await stream.getTableType({ sourceId, tableName });
        if (res.code === 1) {
            const data = res.data || {};
            panelColumn[index]['storeType'] = data.tableLocationType == 'kudu' ? data.tableLocationType : data.fileType;
            if (data.tableLocationType === 'hive') { // hive表显示分区
                panelColumn[index]['isShowPartition'] = true;
                getPartitionLists(index, sourceId, tableName)
            } else {
                panelColumn[index]['isShowPartition'] = false;
            }
            setOutputData({ panelColumn })
            dispathState({
                panelColumn
            })
        }
    }
    // 获取分区接口
    const getPartitionApi = (type: number) => {
        let api;
        switch (type) {
            case DATA_SOURCE_ENUM.HIVE:
                api = stream.getHivePartitions;
                break;
            case DATA_SOURCE_ENUM.INCEPTOR:
                api = stream.getInceptorPartitions;
                break;
        }
        return api;
    }
    // 处理分区
    const splicePartitions = (data: any) => {
        if (!data) return;
        if (data.length > 0) {
            const partitionStr = data.map((item: any, index: number) => {
                if (index != data.length - 1) {
                    item = `${item}, `
                } else {
                    item = `${item}`
                }
                return item;
            }).join('');
            return [partitionStr]
        } else {
            return []
        }
    }
    // 获取分区列表
    const getPartitionLists = (index: any, sourceId: any, tableName: any) => {
        const { type } = panelColumn[index];
        if (!sourceId || !tableName) {
            return;
        }
        const partitionAPI = getPartitionApi(type);
        partitionAPI && partitionAPI({ sourceId, tableName }).then((v: any) => {
            if (v.code === 1) {
                const partitionOptionTypeList = splicePartitions(v.data);
                partitionOptionType[index] = partitionOptionTypeList;
                // panelColumn[index]['partitionfields'] = v.data.join(',');
                panelColumn[index]['havePartitionfields'] = partitionOptionTypeList && !!partitionOptionTypeList.length;
            } else {
                partitionOptionType[index] = [];
                panelColumn[index]['havePartitionfields'] = false;
            }
            // hive 数据源根据数组长度修改 isShowPartition
            if ([DATA_SOURCE_ENUM.HIVE, DATA_SOURCE_ENUM.INCEPTOR].includes(panelColumn[index].type)) {
                panelColumn[index]['isShowPartition'] = !!partitionOptionType[index].length
            }

            setOutputData({ partitionOptionType })
            dispathState({
                partitionOptionType,
                panelColumn
            })
        })
    }
    const getTopicType = (index: any, sourceId: any) => {
        if (sourceId) {
            stream.getTopicType({ sourceId }).then((v: any) => {
                if (index === 'add') {
                    if (v.code === 1) {
                        topicOptionType.push(v.data)
                    } else {
                        topicOptionType.push([])
                    }
                } else {
                    if (v.code === 1) {
                        topicOptionType[index] = v.data;
                    } else {
                        topicOptionType[index] = [];
                    }
                }
                dispathState({
                    topicOptionType
                })
            })
        } else {
            if (index === 'add') {
                topicOptionType.push([]);
            } else {
                topicOptionType[index] = [];
            }
            dispathState({
                topicOptionType
            })
        }
    }
    /**
     * 删除导致key改变,处理被改变key的值
     * @param index 索引
     * @returns 
     */
    const changeActiveKey = (index: any) => {
        const deleteActiveKey = `${index + 1}`;
        const deleteActiveKeyIndex = panelActiveKey.indexOf(deleteActiveKey);
        if (deleteActiveKeyIndex > -1) {
            panelActiveKey.splice(deleteActiveKeyIndex, 1)
        }
        return panelActiveKey.map((v: any) => {
            return Number(v) > Number(index) ? `${Number(v) - 1}` : v
        });
    }

    const changeInputTabs = (type: any, index?: any) => {
        const tableSourceCreate = createTypes?.find((item: any) => item.createType === TABLE_SOURCE.DATA_CREATE);
        const inputData: any = {
            createType: tableSourceCreate.valid ? TABLE_SOURCE.DATA_CREATE : TABLE_SOURCE.DATA_ASSET,
            type: DATA_SOURCE_ENUM.MYSQL,
            columns: [],
            sourceId: undefined,
            dbId: undefined,
            tableId: undefined,
            table: undefined,
            columnsText: undefined,
            esId: undefined,
            esType: undefined,
            writePolicy: undefined,
            index: undefined,
            id: undefined,
            parallelism: 1,
            bulkFlushMaxActions: 100,
            batchWaitInterval: 1000,
            batchSize: 100,
            tableName: undefined,
            primaryKey: undefined,
            rowKey: undefined,
            rowKeyType: undefined,
            partitionfields: undefined,
            isShowPartition: false,
            havePartitionfields: false,
            partitionKeys: undefined,
            enableKeyPartitions: false,
            updateMode: 'append',
            allReplace: 'false',
            sinkDataType: undefined
        }
        if (type === 'add') {
            tabTemplate.push('OutputForm');
            panelColumn.push(inputData);
            getTypeOriginData('add', inputData.type);
            getSchemaData('add', inputData.sourceId)
            getTableType('add', inputData.table);
            getTopicType('add', inputData.sourceId);
            tableColumnOptionType.push([]);
            let pushIndex = `${tabTemplate.length}`;
            panelActiveKey.push(pushIndex)
        } else {
            tabTemplate.splice(index, 1);
            panelColumn.splice(index, 1);
            originOptionType.splice(index, 1);
            schemaOptionType.splice(index, 1);
            topicOptionType.splice(index, 1);
            tableOptionType.splice(index, 1);
            assetTableOptionType.splice(index, 1);
            partitionOptionType.splice(index, 1);
            tableColumnOptionType.splice(index, 1);
            checkFormParams.pop();
            panelActiveKey = changeActiveKey(index);
            popoverVisible[index] = false;
        }
        setOutputData({
            tabTemplate,
            panelActiveKey,
            popoverVisible,
            panelColumn,
            tableColumnOptionType,
            topicOptionType,
            partitionOptionType
        }, true);
        dispathState({
            tabTemplate,
            panelActiveKey,
            popoverVisible,
            panelColumn,
            checkFormParams,
            originOptionType,
            schemaOptionType,
            tableOptionType,
            assetTableOptionType,
            topicOptionType,
            partitionOptionType,
            tableColumnOptionType
        })
    }
    const tableColumnType = (index: any, column: any) => {
        const filterColumn = tableColumnOptionType[index].filter((v: any) => {
            return v.key === column
        })
        return filterColumn[0].type
    }

    const filterPrimaryKey = (columns: any, primaryKeys: any) => { // 删除导致原始的primaryKey不存在
        return primaryKeys.filter((v: any) => {
            let flag = false;
            columns.map((value: any) => {
                if (value.column === v) {
                    flag = true
                }
            })
            return flag;
        })
    }

    const getAllColumn = (index: number) => {
        const columns = tableColumnOptionType[index] || [];
        return columns.map((column: { key: string; type: string }) => {
            return {
                column: column.key,
                type: column.type
            }
        })
    }
    /**
     * 获取元数据下对应表
     * @param index 
     * @param dbId 
     * @param searchKey 
     */
    const getAssetTableList = async (index: any, dbId: any, searchKey?: string) => {
        let assetTableOptionTypeScop = cloneDeep(assetTableOptionType)

        if (dbId) {
            const res = await stream.getAssetTableList({
                dbId,
                tableType: TABLE_TYPE.OUTPUT_TABLE,
                searchKey
            })
            assetTableOptionTypeScop = setOptionTypeList(res, index, 'assetTableOptionType');
        } else {
            if (index === 'add') {
                assetTableOptionTypeScop.push([]);
            } else {
                assetTableOptionTypeScop[index] = [];
            }
        }
        setOutputData({ assetTableOptionType: assetTableOptionTypeScop });
        dispathState({
            assetTableOptionType: assetTableOptionTypeScop
        });
    }
    /**
     * 获取元数据下所有信息
     * @param tableId 
     * @returns 
     */
    const getAssetData = async (tableId: any) => {
        const res = await stream.getAssetTableDetail({
            tableId
        });
        let sink = {};
        if (res.code === 1 && !isEmpty(res.data)) {
            const { sinkTableParam, columns } = res.data;
            sink = Object.assign({}, sinkTableParam, {
                columns: columns
            })
        }
        return sink;
    }
    /**
     * 监听数据改变
     * @param {String} type 改变的属性
     * @param {String} index 改变的panel序号
     */
    const handleInputChange = async (type: any, index: any, value: any, subValue: any) => {
        let shouldUpdateEditor = true;
        if (type === 'dbId') {
            const db = dataBaseOptionType?.find((item: any) => item.dbId === value);
            panelColumn[index]['assetsDbName'] = db?.dbName;
            panelColumn[index][type] = value;
        } else if (type === 'tableId') {
            // 获取当前元数据的类型，并设置
            const tableList = assetTableOptionType[index] || [];
            const tableData = tableList.find((item: any) => item.tableId === value);
            panelColumn[index]['type'] = tableData?.sourceType;
            panelColumn[index]['assetsTableName'] = tableData?.tableName;
            panelColumn[index][type] = value;
        } else if (type === 'columns') {
            panelColumn[index][type].push(value);
        } else if (type === 'targetCol') {
            // 去除空格汉字
            const reg = /[\u4E00-\u9FA5]|[\uFE30-\uFFA0]/gi;
            let val = subValue;
            if (subValue) {
                val = Utils.trimAll(subValue);
                if (reg.test(val)) {
                    val = subValue.replace(reg, '');
                }
            } else {
                val = undefined
            }
            panelColumn[index]['columns'][value].targetCol = val;
            panelColumn[index]['partitionKeys'] = undefined;
        } else if (type === 'primaryKey') {
            panelColumn[index][type] = value;
            if ([DATA_SOURCE_ENUM.REDIS, DATA_SOURCE_ENUM.UPRedis].includes(panelColumn[index].type)) {
                panelColumn[index]['primaryKey-input'] = value;
            }
        } else if (type === 'table') {
            panelColumn[index][type] = value;
            if ([DATA_SOURCE_ENUM.REDIS, DATA_SOURCE_ENUM.UPRedis].includes(panelColumn[index].type)) {
                panelColumn[index]['table-input'] = value;
            }
        } else if (type === 'deleteColumn') {
            panelColumn[index]['columns'].splice(value, 1);
            const filterPrimaryKeys = filterPrimaryKey(panelColumn[index]['columns'], panelColumn[index].primaryKey || []);
            panelColumn[index].primaryKey = filterPrimaryKeys;
        } else if (type === 'subColumn') {
            panelColumn[index]['columns'][value].column = subValue;
            const subType = tableColumnType(index, subValue);
            panelColumn[index]['columns'][value].type = subType;
        } else if (type === 'subType') {
            panelColumn[index]['columns'][value].type = subValue;
        } else if (type == 'addAllColumn') {
            panelColumn[index]['columns'] = getAllColumn(index);
        } else if (type == 'deleteAllColumn') {
            panelColumn[index]['columns'] = [];
        } else if (type == 'customParams') {
            changeCustomParams(panelColumn[index], value, subValue);
        } else {
            panelColumn[index][type] = value;
        }
        if (type === 'columnsText') {
            panelColumn[index]['partitionKeys'] = undefined;
        }
        const allParamsType: any = [
            'type', 'sourceId', 'schema',
            'table', 'table-input', 'columns',
            'columnsText', 'id',
            'index', 'writePolicy',
            'esId', 'esType', 'topic',
            'partitionKeys', 'enableKeyPartitions',
            'parallelism', 'tableName', 'advanConf',
            'primaryKey', 'primaryKey-input', 'rowKey', 'rowKeyType',
            'customParams', 'partitionfields', 'havePartitionfields',
            'isShowPartition', 'updateMode', 'allReplace',
            'batchWaitInterval', 'batchSize', 'storeType',
            'bucket', 'collection', 'objectName',
            'indexDefinition', 'sinkDataType', 'schemaInfo',
            'createType', 'dbId', 'assetsDbName',
            'tableId', 'assetsTableName', 'bulkFlushMaxActions'
        ];
        const sourceType = panelColumn[index].type;
        const schema = panelColumn[index]?.schema;
        const dbId = panelColumn[index]?.dbId;
        const tableId = panelColumn[index]?.tableId;
        /**
         * 这里开始处理改变操作，比如数据源改变要改变重置表名等
         */
        if (type === 'createType') {
            originOptionType[index] = [];
            schemaOptionType[index] = [];
            tableOptionType[index] = [];
            tableColumnOptionType[index] = [];
            panelColumn[index].columns = [];
            assetTableOptionType[index] = [];
            allParamsType.map((v: any) => {
                if (v !== 'createType') {
                    if (v === 'type') {
                        panelColumn[index][v] = DATA_SOURCE_ENUM.MYSQL;
                        if (value === TABLE_SOURCE.DATA_ASSET) {
                            return;
                        }
                        if (isKafka(value)) {
                            if (value === DATA_SOURCE_ENUM.KAFKA_CONFLUENT) {
                                panelColumn[index]['sinkDataType'] = KAFKA_DATA_TYPE.TYPE_AVRO_CONFLUENT;
                            } else {
                                panelColumn[index]['sinkDataType'] = 'dt_nest'
                            }
                        }
                        getTypeOriginData(index, DATA_SOURCE_ENUM.MYSQL);
                    } else if (v == 'parallelism') {
                        panelColumn[index][v] = 1
                    } else if (v == 'batchWaitInterval') {
                        panelColumn[index][v] = isRDB(panelColumn[index]['type']) ? 1000 : undefined;
                    } else if (v == 'batchSize') {
                        panelColumn[index][v] = isRDB(panelColumn[index]['type']) ? 100 : undefined;
                    } else if (v == 'columns') {
                        panelColumn[index][v] = [];
                    } else if (v == 'updateMode') {
                        panelColumn[index][v] = 'append';
                    } else if (v == 'allReplace') {
                        panelColumn[index][v] = 'false';
                    } else if (v === 'bulkFlushMaxActions') {
                        panelColumn[index][v] = 100
                    } else {
                        panelColumn[index][v] = undefined
                    }
                }
            });
        } else if (type === 'type') {
            originOptionType[index] = [];
            schemaOptionType[index] = [];
            tableOptionType[index] = [];
            tableColumnOptionType[index] = [];
            topicOptionType[index] = [];
            partitionOptionType[index] = [];
            allParamsType.map((v: any) => {
                if (v !== 'createType' && v !== 'type') {
                    if (v == 'parallelism') {
                        panelColumn[index][v] = 1
                    } else if (v == 'batchWaitInterval') {
                        panelColumn[index][v] = isRDB(value) ? 1000 : undefined;
                    } else if (v == 'batchSize') {
                        panelColumn[index][v] = isRDB(value) ? 100 : undefined;
                    } else if (v == 'columns') {
                        panelColumn[index][v] = [];
                    } else if (v == 'updateMode') {
                        panelColumn[index][v] = 'append';
                    } else if (v == 'allReplace') {
                        panelColumn[index][v] = 'false';
                    } else if (v == 'bulkFlushMaxActions') {
                        panelColumn[index][v] = 100
                    } else {
                        panelColumn[index][v] = undefined
                    }
                }
            })
            if (isKafka(value)) {
                if (value === DATA_SOURCE_ENUM.KAFKA_CONFLUENT) {
                    panelColumn[index]['sinkDataType'] = KAFKA_DATA_TYPE.TYPE_AVRO_CONFLUENT;
                } else {
                    panelColumn[index]['sinkDataType'] = 'dt_nest'
                }
            }
            getTypeOriginData(index, value);
        } else if (type === 'sourceId') {
            schemaOptionType[index] = [];
            tableOptionType[index] = [];
            tableColumnOptionType[index] = [];
            topicOptionType[index] = []; // 清空topic列表
            partitionOptionType[index] = [];
            allParamsType.map((v: any) => {
                if (['createType', 'type', 'sourceId', 'customParams', 'batchWaitInterval', 'batchSize'].indexOf(v) == -1) {
                    if (v == 'columns' || v == 'topic') {
                        panelColumn[index][v] = [];
                    } else if (v == 'parallelism') {
                        panelColumn[index][v] = 1
                    } else if (v == 'updateMode') {
                        panelColumn[index][v] = 'append';
                    } else if (v == 'allReplace') {
                        panelColumn[index][v] = 'false';
                    } else if (v == 'sinkDataType' && isKafka(panelColumn[index]['type'])) {
                        if (panelColumn[index]['type'] === DATA_SOURCE_ENUM.KAFKA_CONFLUENT) {
                            panelColumn[index][v] = KAFKA_DATA_TYPE.TYPE_AVRO_CONFLUENT;
                        } else {
                            panelColumn[index][v] = 'dt_nest'
                        }
                    } else if (v === 'bulkFlushMaxActions') {
                        panelColumn[index][v] = 100
                    } else {
                        panelColumn[index][v] = undefined
                    }
                }
            })
            if (haveCollection(sourceType)) {
                getTableType(index, value)
            }
            if (haveTableList(sourceType)) {
                getTableType(index, value)
                if (haveSchema(sourceType)) {
                    getSchemaData(index, value)
                }
            }
            if (haveTopic(sourceType)) {
                getTopicType(index, value)
            }
        } else if (type === 'schema') {
            tableOptionType[index] = [];
            tableColumnOptionType[index] = [];
            topicOptionType[index] = []; // 清空topic列表
            partitionOptionType[index] = [];
            const { sourceId } = panelColumn[index];
            allParamsType.map((v: any) => {
                if (['createType', 'type', 'sourceId', 'schema', 'customParams', 'batchWaitInterval', 'batchSize'].indexOf(v) == -1) {
                    if (v == 'columns' || v == 'topic') {
                        panelColumn[index][v] = [];
                    } else if (v == 'parallelism') {
                        panelColumn[index][v] = 1
                    } else if (v == 'updateMode') {
                        panelColumn[index][v] = 'append';
                    } else if (v == 'allReplace') {
                        panelColumn[index][v] = 'false';
                    } else if (v === 'bulkFlushMaxActions') {
                        panelColumn[index][v] = 100
                    } else {
                        panelColumn[index][v] = undefined
                    }
                }
            })
            if (haveTableList(sourceType)) {
                getTableType(index, sourceId, value)
            }
        } else if (type === 'table' || type === 'collection') {
            tableColumnOptionType[index] = [];
            partitionOptionType[index] = [];
            const { sourceId } = panelColumn[index];
            panelColumn[index].columns = [];
            allParamsType.map((v: any) => {
                const skip = ['createType', 'type', 'sourceId', 'schema', 'table', 'table-input', 'customParams', 'batchWaitInterval', 'batchSize']
                // 修改 collection 后不清空 collection
                if (type === 'collection') {
                    skip.push('collection')
                }
                if (!skip.includes(v)) {
                    if (v == 'columns') {
                        panelColumn[index][v] = [];
                    } else if (v == 'parallelism') {
                        panelColumn[index][v] = 1
                    } else if (v == 'updateMode') {
                        panelColumn[index][v] = 'append';
                    } else if (v == 'allReplace') {
                        panelColumn[index][v] = 'false';
                    } else if (v === 'bulkFlushMaxActions') {
                        panelColumn[index][v] = 100
                    } else {
                        panelColumn[index][v] = undefined
                    }
                }
            })
            if (haveTableColumn(panelColumn[index].type)) {
                getTableColumns(index, sourceId, value, schema)
            }
            if (havePartition(panelColumn[index].type)) {
                loadPartitions(index, sourceId, value)
            }
        } else if (type === 'dbId') {
            assetTableOptionType[index] = [];
            panelColumn[index].columns = [];
            allParamsType.map((v: any) => {
                if (v !== 'createType' && v !== 'dbId' && v !== 'assetsDbName') {
                    if (v === 'type') {
                        panelColumn[index][v] = DATA_SOURCE_ENUM.MYSQL;
                    } else if (v == 'columns') {
                        panelColumn[index][v] = [];
                    } else if (v == 'parallelism') {
                        panelColumn[index][v] = 1
                    } else if (v == 'batchWaitInterval') {
                        panelColumn[index][v] = isRDB(panelColumn[index]['type']) ? 1000 : undefined;
                    } else if (v == 'batchSize') {
                        panelColumn[index][v] = isRDB(panelColumn[index]['type']) ? 100 : undefined;
                    } else if (v == 'updateMode') {
                        panelColumn[index][v] = 'append';
                    } else if (v == 'allReplace') {
                        panelColumn[index][v] = 'false';
                    } else if (v === 'bulkFlushMaxActions') {
                        panelColumn[index][v] = 100
                    } else {
                        panelColumn[index][v] = undefined
                    }
                }
            })
            getAssetTableList(index, dbId);
        } else if (type === 'tableId') {
            panelColumn[index].columns = [];
            allParamsType.map((v: any) => {
                if (v !== 'createType' && v !== 'dbId' && v !== 'assetsDbName' && v !== 'tableId' && v !== 'assetsTableName' && v !== 'type') {
                    if (v == 'columns') {
                        panelColumn[index][v] = [];
                    } else if (v == 'parallelism') {
                        panelColumn[index][v] = 1
                    } else if (v == 'batchWaitInterval') {
                        panelColumn[index][v] = isRDB(panelColumn[index]['type']) ? 1000 : undefined;
                    } else if (v == 'batchSize') {
                        panelColumn[index][v] = isRDB(panelColumn[index]['type']) ? 100 : undefined;
                    } else if (v == 'updateMode') {
                        panelColumn[index][v] = 'append';
                    } else if (v == 'allReplace') {
                        panelColumn[index][v] = 'false';
                    } else if (v === 'bulkFlushMaxActions') {
                        panelColumn[index][v] = 100
                    } else {
                        panelColumn[index][v] = undefined
                    }
                }
            })
            const sink = await getAssetData(tableId);
            Object.assign(panelColumn[index], sink);
        } else {
            shouldUpdateEditor = false;
        }
        if (type === 'sinkDataType') {
            if (!isAvro(value)) {
                panelColumn[index].schemaInfo = undefined
            }
        }
        setOutputData({ panelColumn }, true)
        dispathState({
            panelColumn,
            sync: shouldUpdateEditor
        })
    }

    const currentInitData = (sink: any) => {
        sink.map((v: any, index: any) => {
            tabTemplate.push('OutputForm');
            initCustomParam(v);
            v['createType'] = v['createType'] === undefined ? TABLE_SOURCE.DATA_CREATE : v['createType'];
            panelColumn.push(v);
            if (v.createType === TABLE_SOURCE.DATA_ASSET) {
                getAssetTableList(index, v.dbId);
                return;
            }
            getTypeOriginData(index, v.type);
            if (haveCollection(v.type)) {
                getTableType(index, v.sourceId, v?.schema)
            }
            if (haveTableList(v.type)) {
                getTableType(index, v.sourceId, v?.schema)
                if (haveSchema(v.type)) {
                    getSchemaData(index, v.sourceId)
                }
                if (haveTableColumn(v.type)) {
                    getTableColumns(index, v.sourceId, v.table, v?.schema)
                }
            }
            if (haveTopic(v.type)) {
                getTopicType(index, v.sourceId)
            }
            if (havePartition(v.type)) {
                loadPartitions(index, v.sourceId, v.table)
            }
        })
        setOutputData({ tabTemplate, panelColumn })
        dispathState({
            tabTemplate,
            panelColumn
        })
    }
    useEffect(() => {
        getCreateType();
        getResultTableTypes()
    }, [])
    useEffect(() => {
        const { sink } = currentPage;
        if (sink && sink.length > 0) {
            currentInitData(sink);
        }
        dispathState({sync: true})
    }, [current?.id])

    const panelHeader = (index: any) => {
        const popoverContent = <div className="input-panel-title">
            <div style={{ padding: '8 0 12' }}>
                <ExclamationCircleFilled style={{ color: '#faad14', marginRight: 6 }}/>你确定要删除此结果表吗？
            </div>
            <div style={{ textAlign: 'right', padding: '0 0 8', marginTop: 12 }}>
                <Button style={{ marginRight: 8 }} size="small" onClick={() => { handlePopoverVisibleChange(null, index, false) }}>取消</Button>
                <Button type="primary" size="small" onClick={() => { changeInputTabs('delete', index) }}>确定</Button>
            </div>
        </div>
        const onClickFix = {
            onClick: (e: any) => { handlePopoverVisibleChange(e, index, !popoverVisible[index]) }
        }
        const tableName = panelColumn[index]?.tableName // 映射表名称
        return <div className="input-panel-title">
            {` 结果表 ${index + 1} ${tableName ? `(${tableName})` : ''}`}
            <Popover
                trigger="click"
                placement="topLeft"
                content={popoverContent}
                visible={!isLocked && popoverVisible[index]}
                {...onClickFix}
            >
                <DeleteOutlined className={classNames('title-icon', { 'lock-panel-icon': isLocked })} />
            </Popover>
        </div>
    }
    /**
     * 存储子组件的所有要检查的form表单
     * @param ref 
     */
    const recordForm = (ref: any) => {
        checkFormParams.push(ref);
        setOutputData({ checkFormParams })
        dispathState({
            checkFormParams
        })
    }
    /**
    * 当前的 tab 是否不合法，如不合法则展示 Empty
    */
    const isInValidTab = useMemo(
        () =>
            !current ||
            !current.activeTab ||
            TAB_WITHOUT_DATA.some((prefix) => current.activeTab?.toString().includes(prefix)),
        [current],
    );
    if (isInValidTab) {
        return <div className={classNames('text-center', 'mt-10px')}>无法获取任务详情</div>;
    }
    
    return <div className="m-taksdetail panel-content ouput-panel">
        <Collapse activeKey={panelActiveKey} bordered={false} onChange={handleActiveKey}>
            {
                tabTemplate.map((OutputPutOrigin: any, index: any) => {
                    return (
                        <Panel header={panelHeader(index)} key={index + 1} style={{ position: 'relative' }} className="input-panel">
                            <ResultForm
                                isShow={panelActiveKey.indexOf(index + 1 + '') > -1 && isInValidTab}
                                sync={sync}
                                index={index}
                                getTableType={getTableType}
                                getSchemaData={getSchemaData}
                                handleInputChange={handleInputChange}
                                resultTableTypes={resultTableTypes}
                                dataBaseOptionType={dataBaseOptionType}
                                getDataBaseList={getDataBaseList.bind(undefined, createTypes)}
                                panelColumn={panelColumn} 
                                originOptionType={originOptionType}
                                tableOptionType={tableOptionType}
                                assetTableOptionType={assetTableOptionType}
                                schemaOptionType={schemaOptionType}
                                tableColumnOptionType={tableColumnOptionType}
                                topicOptionType={topicOptionType}
                                partitionOptionType={partitionOptionType}
                                onRef={recordForm}
                                textChange={() => { dispathState({ sync: false }) }}
                                createTypes={createTypes}
                                currentPage={currentPage}
                            />
                            <LockPanel lockTarget={currentPage} />
                        </Panel>
                    )
                })
            }
        </Collapse>
        <Button
            disabled={isLocked}
            className="stream-btn"
            ghost
            onClick={() => { changeInputTabs('add') }}
            style={{ borderRadius: 5 }}
        ><PlusOutlined /><span> 添加结果表</span></Button>
    </div>
}