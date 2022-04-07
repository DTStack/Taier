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

import { IEditor } from "@dtinsight/molecule/esm/model";
import { Button, Collapse, Popover } from "antd";
import { PlusOutlined, ExclamationCircleFilled, DeleteOutlined } from '@ant-design/icons'
import { cloneDeep, isEmpty } from "lodash";
import { useEffect, useMemo, useReducer, useState } from "react";
import { streamTaskActions } from "../../taskFunc";
import { getCreateTypes, getDataBaseList } from "../panelData";
import classNames from "classnames";
import { TAB_WITHOUT_DATA } from "@/pages/rightBar";
import { DATA_SOURCE_ENUM, TABLE_SOURCE, TABLE_TYPE } from "@/constant";
import stream from "@/api/stream";
import LockPanel from "../lockPanel";
import { Utils } from "@dtinsight/dt-utils/lib";
import { changeCustomParams, initCustomParam } from "../customParamsUtil";
import { haveSchema, haveTableColumn, haveTableList, isCacheExceptLRU } from "@/utils/enums";
import DimensionForm from "./form";

const Panel = Collapse.Panel;
interface IDimensionState {
    tabTemplate: any[], // 模版存储,所有输出源(记录个数)
    panelActiveKey: any[], // 输出源是打开或关闭状态
    popoverVisible: any[], // 删除显示按钮状态
    panelColumn: any[], // 存储数据
    checkFormParams: any[], // 存储要检查的参数from
    originOptionType: any[], // 数据源选择数据
    schemaOptionType: any[], // schema 数据
    tableOptionType: any[], // 表选择数据
    assetTableOptionType: any[], // 数据资产 - 表选择数据
    tableColumnOptionType: any[] // 表字段选择的类型
}

export default function FlinkDimensionPanel({ current }: Pick<IEditor, 'current'>) {
    const currentPage = current?.tab?.data || {};
    const isLocked = currentPage.readWriteLockVO && !currentPage.readWriteLockVO.getLock;
    const [state, dispathState] = useReducer((state: IDimensionState, data: any)=> {
        let newState = cloneDeep(state);
        newState = Object.assign(newState, data)
        return newState;
    }, {
        tabTemplate: [], // 模版存储,所有输出源(记录个数)
        panelActiveKey: [], // 输出源是打开或关闭状态
        popoverVisible: [], // 删除显示按钮状态
        panelColumn: [], // 存储数据
        checkFormParams: [], // 存储要检查的参数from
        originOptionType: [], // 数据源选择数据
        schemaOptionType: [], // schema 数据
        tableOptionType: [], // 表选择数据
        assetTableOptionType: [], // 数据资产 - 表选择数据
        tableColumnOptionType: [] // 表字段选择的类型
    });
    let {
        tabTemplate,
        panelActiveKey,
        popoverVisible,
        panelColumn,
        checkFormParams,
        originOptionType,
        schemaOptionType,
        tableOptionType,
        assetTableOptionType,
        tableColumnOptionType,
    } = state;

    const [createTypes, setCreateTypes] = useState<any[]>([]);
    const [sync, setSync] = useState(false);
    const [dataBaseOptionType, setDataBaseOptionType] = useState<any>([]);
    const [dimensionTableTypes, setDimensionTableTypes] = useState<any>([])

    const getCreateType = async () => {
        const list = await getCreateTypes();
        setCreateTypes(list);
        initDataBaseList(list)
    }
    const initDataBaseList = async (createTypes: any[] = []) => {
        const list = await getDataBaseList(createTypes);
        setDataBaseOptionType(list)
    }
    const getDimensionTableTypes = async () => {
        const res = await stream.getDimensionTableTypes();
        if (res.code == 1) {
            setDimensionTableTypes(res.data || [])
        }
    }
    
    const setDimensionData = (data: any, notSynced: boolean = false) => {
        const dispatchSource: any = { ...state, ...data };
        streamTaskActions.setCurrentPageValue('side', dispatchSource, notSynced)
    };
    const handleActiveKey = (key: any) => {
        setDimensionData({ panelActiveKey: key });
        dispathState({
            panelActiveKey: key
        });
    };

    const getTypeOriginData = (index: any, type: any) => {
        stream.getTypeOriginData({ type }).then((v: any) => {
            if (index === 'add') {
                if (v.code === 1) {
                    originOptionType.push(v.data);
                } else {
                    originOptionType.push([]);
                }
            } else {
                if (v.code === 1) {
                    originOptionType[index] = v.data;
                } else {
                    originOptionType[index] = [];
                }
            }
            setDimensionData({ originOptionType });
            dispathState({
                originOptionType
            });
        });
    };
    /**
     * 获取Schema列表
     */
    const getSchemaData = async (index: any, sourceId: any, searchKey?: any) => {
        let schemaOptionTypeScop = cloneDeep(schemaOptionType);

        if (sourceId) {
            const v = await stream.listSchemas({ sourceId, isSys: false, searchKey });
            schemaOptionTypeScop = setOptionTypeList(v, index, 'schemaOptionType');
        } else {
            if (index === 'add') {
                schemaOptionTypeScop.push([]);
            } else {
                schemaOptionTypeScop[index] = [];
            }
        }

        setDimensionData({ schemaOptionType: schemaOptionTypeScop });
        dispathState({
            schemaOptionType: schemaOptionTypeScop
        })
    }
    // 对下拉列表请求体进行统一处理
    const setOptionTypeList = (res: any, index: any, type: keyof IDimensionState) => {
        const optionType = cloneDeep(state[type]);
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

    // 获取表
    const getTableType = async (index: any, sourceId: any, schema?: any, searchKey?: string) => {
        // postgresql kingbasees8 schema必填处理
        const disableReq = (panelColumn[index]?.type === DATA_SOURCE_ENUM.POSTGRESQL || panelColumn[index]?.type === DATA_SOURCE_ENUM.KINGBASE8) && !schema;

        let tableOptionType = cloneDeep(state.tableOptionType);

        if (sourceId && !disableReq) {
            const v = await stream.listTablesBySchema({ sourceId, schema: schema || '', isSys: false, searchKey });
            tableOptionType = setOptionTypeList(v, index, 'tableOptionType')
        } else {
            if ((index == 'add')) {
                tableOptionType.push([]);
            } else {
                tableOptionType[index] = [];
            }
        }
        setDimensionData({ tableOptionType });
        dispathState({
            tableOptionType
        });
    };

    // 获取元数据下对应表
    const getAssetTableList = async (index: any, dbId: any, searchKey?: string) => {
        let assetTableOptionType = cloneDeep(state.assetTableOptionType)

        if (dbId) {
            const res = await stream.getAssetTableList({
                dbId,
                tableType: TABLE_TYPE.DIMENSION_TABLE,
                searchKey
            })
            assetTableOptionType = setOptionTypeList(res, index, 'assetTableOptionType');
        } else {
            if (index === 'add') {
                assetTableOptionType.push([]);
            } else {
                assetTableOptionType[index] = [];
            }
        }
        setDimensionData({ assetTableOptionType });
        dispathState({
            assetTableOptionType
        });
    }
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
                tableColumnOptionType[index] = [];
            }
            setDimensionData({ tableColumnOptionType });
            dispathState({
                tableColumnOptionType
            });
        });
    };

    // 获取元数据下所有信息
    const getAssetData = async (tableId: any) => {
        const res = await stream.getAssetTableDetail({
            tableId
        });
        let side = {};
        if (res.code === 1 && !isEmpty(res.data)) {
            const { sideTableParam, columns } = res.data;
            side = Object.assign({}, sideTableParam, {
                columns: columns
            })
        }
        return side;
    }
    const changeActiveKey = (index: any) => {
        // 删除导致key改变,处理被改变key的值
        const deleteActiveKey = `${index + 1}`;
        const deleteActiveKeyIndex = panelActiveKey.indexOf(deleteActiveKey);
        if (deleteActiveKeyIndex > -1) {
            panelActiveKey.splice(deleteActiveKeyIndex, 1);
        }
        return panelActiveKey.map((v: any) => {
            return Number(v) > Number(index) ? `${Number(v) - 1}` : v;
        });
    };
    const changeInputTabs = (type: any, index?: any) => {
        const tableSourceCreate = createTypes?.find((item: any) => item.createType === TABLE_SOURCE.DATA_CREATE);
        const inputData: any = {
            createType: tableSourceCreate.valid ? TABLE_SOURCE.DATA_CREATE : TABLE_SOURCE.DATA_ASSET,
            type: DATA_SOURCE_ENUM.MYSQL,
            columns: [],
            sourceId: undefined,
            table: undefined,
            dbId: undefined,
            tableId: undefined,
            columnsText: undefined,
            tableName: undefined,
            primaryKey: undefined,
            hbasePrimaryKey: undefined,
            hbasePrimaryKeyType: undefined,
            errorLimit: undefined,
            esType: undefined,
            index: undefined,
            parallelism: 1,
            cache: 'LRU',
            cacheSize: '10000',
            cacheTTLMs: '60000',
            asyncPoolSize: 5
        };
        if (type === 'add') {
            tabTemplate.push('OutputForm');
            panelColumn.push(inputData);
            getTypeOriginData('add', inputData.type);
            getSchemaData('add', inputData.sourceId);
            getTableType('add', inputData.table);
            getAssetTableList('add', inputData.tableId)
            tableColumnOptionType.push([]);
            let pushIndex = `${tabTemplate.length}`;
            panelActiveKey.push(pushIndex);
        } else {
            tabTemplate.splice(index, 1);
            panelColumn.splice(index, 1);
            originOptionType.splice(index, 1);
            schemaOptionType.splice(index, 1);
            tableOptionType.splice(index, 1);
            assetTableOptionType.splice(index, 1);
            tableColumnOptionType.splice(index, 1);
            checkFormParams.pop();
            panelActiveKey = changeActiveKey(index);
            popoverVisible[index] = false;
        }
        setDimensionData({
            tabTemplate,
            panelActiveKey,
            popoverVisible,
            panelColumn,
            checkFormParams,
            originOptionType,
            tableOptionType,
            assetTableOptionType,
            tableColumnOptionType
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
            tableColumnOptionType
        });
    };

    const handlePopoverVisibleChange = (e: any, index: any, visible: any) => {
        let popoverVisibleScop = popoverVisible
        popoverVisibleScop[index] = visible;
        if (e) {
            e.stopPropagation(); // 阻止删除按钮点击后冒泡到panel
            if (visible) {
                // 只打开一个Popover提示
                popoverVisibleScop = popoverVisibleScop.map((v: any, i: any) => {
                    return index == i;
                });
            }
        }
        setDimensionData({ popoverVisible: popoverVisibleScop });
        dispathState({ popoverVisible: popoverVisibleScop });
    };
    const tableColumnType = (index: any, column: any) => {
        const filterColumn = tableColumnOptionType[index].filter((v: any) => {
            return v.key === column;
        });
        return filterColumn[0].type;
    };

    const filterPrimaryKey = (columns: any, primaryKeys: any) => {
        // 删除导致原始的primaryKey不存在
        return primaryKeys.filter((v: any) => {
            let flag = false;
            columns.map((value: any) => {
                if (value.column === v) {
                    flag = true;
                }
            });
            return flag;
        });
    };
    const getAllColumn = (index: number) => {
        const columns = tableColumnOptionType[index] || [];
        return columns.map((column: { key: string; type: string }) => {
            return {
                column: column.key,
                type: column.type
            }
        })
    }

    const handleInputChange = async (type: any, index: any, value: any, subValue: any) => {
        // 监听数据改变
        let shouldUpdateEditor = true;
        const primarykeyInputList = [DATA_SOURCE_ENUM.REDIS, DATA_SOURCE_ENUM.UPRedis, DATA_SOURCE_ENUM.MONGODB, DATA_SOURCE_ENUM.ES6, DATA_SOURCE_ENUM.ES7];
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
        }
        if (type == 'keyFilter') {
            if (!value) {
                panelColumn[index]['keyField'] = undefined;
            } else {
                panelColumn[index]['keyField'] = panelColumn[index]['primaryKey'];
            }
        }
        if (type == 'primaryKey') {
            panelColumn[index]['keyField'] = value;
            if (primarykeyInputList.includes(panelColumn[index]['type'])) {
                panelColumn[index]['primaryKey-input'] = value;
            }
        }
        if (type === 'table') {
            panelColumn[index][type] = value;
            if ([DATA_SOURCE_ENUM.REDIS, DATA_SOURCE_ENUM.UPRedis].includes(panelColumn[index]['type'])) {
                panelColumn[index]['table-input'] = value;
            }
        }
        if (type === 'columns') {
            panelColumn[index][type].push(value);
        } else if (type === 'deleteColumn') {
            panelColumn[index]['columns'].splice(value, 1);
            const filterPrimaryKeys = filterPrimaryKey(
                panelColumn[index]['columns'],
                panelColumn[index].primaryKey || []
            );
            panelColumn[index].primaryKey = filterPrimaryKeys;
        } else if (type === 'subColumn') {
            panelColumn[index]['columns'][value].column = subValue;
            const subType = tableColumnType(index, subValue);
            panelColumn[index]['columns'][value].type = subType;
        } else if (type === 'subType') {
            panelColumn[index]['columns'][value].type = subValue;
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
        } else if (type == 'addAllColumn') {
            panelColumn[index]['columns'] = getAllColumn(index);
        } else if (type == 'deleteAllColumn') {
            panelColumn[index]['columns'] = [];
        } else if (type == 'customParams') {
            changeCustomParams(panelColumn[index], value, subValue);
        } else if (type === 'errorLimit') {
            panelColumn[index]['errorLimit'] = value === 0 ? value : (value || undefined);
        } else {
            panelColumn[index][type] = value;
        }
        const allParamsType: any = [
            'type',
            'sourceId',
            'schema',
            'table',
            'table-input',
            'columns',
            'columnsText',
            'parallelism',
            'cache',
            'cacheSize',
            'hbasePrimaryKey',
            'hbasePrimaryKeyType',
            'cacheTTLMs',
            'errorLimit',
            'index',
            'esType',
            'tableName',
            'primaryKey',
            'primaryKey-input',
            'customParams',
            'createType',
            'dbId',
            'assetsDbName',
            'tableId',
            'assetsTableName'
        ];
        const { sourceId, dbId, tableId, schema = '' } = panelColumn[index];
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
                        getTypeOriginData(index, DATA_SOURCE_ENUM.MYSQL);
                    } else if (v == 'parallelism') {
                        panelColumn[index][v] = 1;
                    } else if (v == 'columns') {
                        panelColumn[index][v] = [];
                    } else if (v == 'cache') {
                        panelColumn[index][v] = 'LRU';
                    } else if (v == 'cacheSize') {
                        panelColumn[index][v] = '10000';
                    } else if (v == 'cacheTTLMs') {
                        panelColumn[index][v] = '60000';
                    } else {
                        panelColumn[index][v] = undefined;
                    }
                }
            });
        } else if (type === 'type') {
            originOptionType[index] = [];
            schemaOptionType[index] = [];
            tableOptionType[index] = [];
            tableColumnOptionType[index] = [];
            allParamsType.map((v: any) => {
                if (v !== 'createType' && v != 'type') {
                    if (v == 'parallelism') {
                        panelColumn[index][v] = 1;
                    } else if (v == 'columns') {
                        panelColumn[index][v] = [];
                    } else if (v == 'cache') {
                        if (isCacheExceptLRU(value)) {
                            panelColumn[index][v] = 'ALL';
                        } else {
                            panelColumn[index][v] = 'LRU';
                        }
                    } else if (v == 'cacheSize') {
                        panelColumn[index][v] = '10000';
                    } else if (v == 'cacheTTLMs') {
                        panelColumn[index][v] = '60000';
                    } else {
                        panelColumn[index][v] = undefined;
                    }
                }
            });
            getTypeOriginData(index, value);
        } else if (type === 'sourceId') {
            schemaOptionType[index] = [];
            tableOptionType[index] = [];
            tableColumnOptionType[index] = [];
            panelColumn[index].columns = [];

            allParamsType.map((v: any) => {
                if (v !== 'createType' && v != 'type' && v != 'sourceId' && v != 'customParams') {
                    if (v == 'parallelism') {
                        panelColumn[index][v] = 1;
                    } else if (v == 'columns') {
                        panelColumn[index][v] = [];
                    } else if (v == 'cache') {
                        if (isCacheExceptLRU(panelColumn[index].type)) {
                            panelColumn[index][v] = 'ALL';
                        } else {
                            panelColumn[index][v] = 'LRU';
                        }
                    } else if (v == 'cacheSize') {
                        panelColumn[index][v] = '10000';
                    } else if (v == 'cacheTTLMs') {
                        panelColumn[index][v] = '60000';
                    } else {
                        panelColumn[index][v] = undefined;
                    }
                }
            });
            // this.clearCurrentInfo(type,index,value)
            if (haveTableList(panelColumn[index].type)) {
                getTableType(index, value);
                if (haveSchema(panelColumn[index].type)) {
                    getSchemaData(index, value);
                }
            }
        } else if (type === 'schema') {
            tableOptionType[index] = [];
            tableColumnOptionType[index] = [];
            panelColumn[index].columns = [];

            allParamsType.map((v: any) => {
                if (v !== 'createType' && v != 'type' && v != 'sourceId' && v != 'customParams' && v != 'schema') {
                    if (v == 'parallelism') {
                        panelColumn[index][v] = 1;
                    } else if (v == 'columns') {
                        panelColumn[index][v] = [];
                    } else if (v == 'cache') {
                        if (isCacheExceptLRU(panelColumn[index].type)) {
                            panelColumn[index][v] = 'ALL';
                        } else {
                            panelColumn[index][v] = 'LRU';
                        }
                    } else if (v == 'cacheSize') {
                        panelColumn[index][v] = '10000';
                    } else if (v == 'cacheTTLMs') {
                        panelColumn[index][v] = '60000';
                    } else {
                        panelColumn[index][v] = undefined;
                    }
                }
            });
            if (haveTableList(panelColumn[index].type)) {
                getTableType(index, sourceId, value)
            }
        } else if (type === 'dbId') {
            assetTableOptionType[index] = [];
            panelColumn[index].columns = [];
            allParamsType.map((v: any) => {
                if (v !== 'createType' && v !== 'dbId' && v !== 'assetsDbName') {
                    if (v === 'type') {
                        panelColumn[index][v] = DATA_SOURCE_ENUM.MYSQL;
                    } else if (v == 'parallelism') {
                        panelColumn[index][v] = 1;
                    } else if (v == 'columns') {
                        panelColumn[index][v] = [];
                    } else if (v == 'cache') {
                        if (isCacheExceptLRU(panelColumn[index].type)) {
                            panelColumn[index][v] = 'ALL';
                        } else {
                            panelColumn[index][v] = 'LRU';
                        }
                    } else if (v == 'cacheSize') {
                        panelColumn[index][v] = '10000';
                    } else if (v == 'cacheTTLMs') {
                        panelColumn[index][v] = '60000';
                    } else {
                        panelColumn[index][v] = undefined;
                    }
                }
            })
            getAssetTableList(index, dbId);
        } else if (type === 'tableId') {
            panelColumn[index].columns = [];
            allParamsType.map((v: any) => {
                if (v !== 'createType' && v !== 'dbId' && v !== 'assetsDbName' && v !== 'tableId' && v !== 'assetsTableName' && v !== 'type') {
                    if (v == 'parallelism') {
                        panelColumn[index][v] = 1;
                    } else if (v == 'columns') {
                        panelColumn[index][v] = [];
                    } else if (v == 'cache') {
                        if (isCacheExceptLRU(panelColumn[index].type)) {
                            panelColumn[index][v] = 'ALL';
                        } else {
                            panelColumn[index][v] = 'LRU';
                        }
                    } else if (v == 'cacheSize') {
                        panelColumn[index][v] = '10000';
                    } else if (v == 'cacheTTLMs') {
                        panelColumn[index][v] = '60000';
                    } else {
                        panelColumn[index][v] = undefined;
                    }
                }
            })
            const side = await getAssetData(tableId);
            Object.assign(panelColumn[index], side);
        } else if (type === 'table') {
            tableColumnOptionType[index] = [];
            allParamsType.map((v: any) => {
                if (v !== 'createType' && v != 'type' && v != 'sourceId' && v != 'table' && v != 'table-input' && v != 'customParams' && v != 'schema') {
                    if (v == 'parallelism') {
                        panelColumn[index][v] = 1;
                    } else if (v == 'columns') {
                        panelColumn[index][v] = [];
                    } else if (v == 'cache') {
                        if (isCacheExceptLRU(panelColumn[index].type)) {
                            panelColumn[index][v] = 'ALL';
                        } else {
                            panelColumn[index][v] = 'LRU';
                        }
                    } else if (v == 'cacheSize') {
                        panelColumn[index][v] = '10000';
                    } else if (v == 'cacheTTLMs') {
                        panelColumn[index][v] = '60000';
                    } else {
                        panelColumn[index][v] = undefined;
                    }
                }
            });
            if (haveTableColumn(panelColumn[index].type)) {
                getTableColumns(index, sourceId, value, schema);
            }
        } else {
            shouldUpdateEditor = false;
        }
        setDimensionData({ panelColumn }, true);
        dispathState({
            panelColumn,
            sync: shouldUpdateEditor
        });
    };
    const panelHeader = (index: any) => {
        const popoverContent = (
            <div className="input-panel-title">
                <div style={{ padding: '8 0 12' }}>
                    <ExclamationCircleFilled style={{ color: '#faad14', marginRight: 6 }}/>
                    你确定要删除此维表吗？
                </div>
                <div style={{ textAlign: 'right', padding: '0 0 8', marginTop: 12 }}>
                    <Button 
                        style={{ marginRight: 8 }}
                        size="small"
                        onClick={() => { handlePopoverVisibleChange(null, index, false); }}
                    >
                        取消
                    </Button>
                    <Button
                        type="primary"
                        size="small"
                        onClick={() => { changeInputTabs('delete', index); }}
                    >
                        确定
                    </Button>
                </div>
            </div>
        );
        const onClickFix = {
            onClick: (e: any) => {
                handlePopoverVisibleChange(
                    e,
                    index,
                    !popoverVisible[index]
                );
            }
        }
        const tableName = panelColumn[index]?.tableName // 映射表名称
        return (
            <div className="input-panel-title">
                {` 维表 ${index + 1} ${tableName ? `(${tableName})` : ''}`}
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
        );
    };
    /**
     * 存储子组件的所有要检查的form表单
     * @param ref 
     */
     const recordForm = (ref: any) => {
        checkFormParams.push(ref);
        setDimensionData({ checkFormParams })
        dispathState({
            checkFormParams
        })
    }
    // 回显时初始化维表相关信息，例如数据源表等
    const currentInitData = (side: any) => {
        side.map((v: any, index: any) => {
            tabTemplate.push('OutputForm');
            initCustomParam(v)
            v['createType'] = v['createType'] === undefined ? TABLE_SOURCE.DATA_CREATE : v['createType'];
            panelColumn.push(v);
            if (v.createType === TABLE_SOURCE.DATA_ASSET) {
                getAssetTableList(index, v.dbId);
                return;
            }
            getTypeOriginData(index, v.type);
            if (haveTableList(v.type)) {
                getTableType(index, v.sourceId, v?.schema);
                if (haveSchema(v.type)) {
                    getSchemaData(index, v.sourceId);
                }
                if (haveTableColumn(v.type)) {
                    getTableColumns(index, v.sourceId, v.table, v?.schema);
                }
            }
        });
        setDimensionData({ tabTemplate, panelColumn });
        dispathState({
            tabTemplate,
            panelColumn
        });
    };

    useEffect(() => {
        getCreateType();
        getDimensionTableTypes();
        
    }, [])
    useEffect(() => {
        const { side } = currentPage;
        if (side && side.length > 0) {
            currentInitData(side);
        }
        setSync(true)
    }, [current?.id])
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
    return <div className="m-taksdetail panel-content">
    <Collapse
        activeKey={panelActiveKey}
        bordered={false}
        onChange={handleActiveKey}
    >
        {tabTemplate.map((OutputPutOrigin: any, index: any) => {
            return (
                <Panel
                    header={panelHeader(index)}
                    key={index + 1}
                    style={{ position: 'relative' }}
                    className="input-panel"
                >
                    <DimensionForm
                        isShow={panelActiveKey.indexOf(index + 1 + '') > -1 && isInValidTab}
                        index={index}
                        sync={sync}
                        getTableType={getTableType}
                        getAssetTableList={getAssetTableList}
                        getSchemaData={getSchemaData}
                        handleInputChange={handleInputChange}
                        panelColumn={panelColumn}
                        dimensionTableTypes={dimensionTableTypes}
                        originOptionType={originOptionType}
                        schemaOptionType={schemaOptionType}
                        tableOptionType={tableOptionType}
                        dataBaseOptionType={dataBaseOptionType}
                        getDataBaseList={getDataBaseList.bind(undefined, createTypes)}
                        assetTableOptionType={assetTableOptionType}
                        tableColumnOptionType={tableColumnOptionType}
                        onRef={recordForm}
                        textChange={() => { setSync(false) }}
                        createTypes={createTypes}
                        currentPage={currentPage}
                    />
                    <LockPanel lockTarget={currentPage} />
                </Panel>
            );
        })}
    </Collapse>
    <Button
        disabled={isLocked}
        className="stream-btn"
        onClick={() => {
            changeInputTabs('add');
        }}
        style={{ borderRadius: 5 }}
        ghost
    >
        <PlusOutlined />
        <span>添加维表</span>
    </Button>
</div>
}