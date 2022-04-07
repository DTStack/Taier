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
import { Button, Collapse, Popconfirm } from "antd";
import { PlusOutlined, DeleteOutlined } from "@ant-design/icons"
import LockPanel from "../lockPanel";
import { useEffect, useMemo, useState } from "react";
import { getCreateTypes, getDataBaseList, getTimeZoneList } from "../panelData";
import { CODE_TYPE, DATA_SOURCE_ENUM, KAFKA_DATA_TYPE, TABLE_SOURCE, TABLE_TYPE } from "@/constant";
import classNames from "classnames";
import './index.scss'
import { Utils } from "@dtinsight/dt-utils/lib";
import stream from "@/api/stream";
import { cloneDeep, isEmpty } from "lodash";
import { streamTaskActions } from "../../taskFunc";
import { TAB_WITHOUT_DATA } from "@/pages/rightBar";
import { isAvro, isKafka } from "@/utils/enums";
import { changeCustomParams } from "../customParamsUtil";
import { parseColumnText } from "../flinkHelper";
import SourceForm from "./form";

const Panel = Collapse.Panel;
const DEFAULT_TABLE_SOURCE = TABLE_SOURCE.DATA_CREATE
const DEFAULT_TYPE = DATA_SOURCE_ENUM.KAFKA_11;

const initInputData: any = {
    createType: DEFAULT_TABLE_SOURCE,
    type: DEFAULT_TYPE,
    sourceId: undefined,
    topic: [],
    dbId: undefined,
    tableId: undefined,
    charset: CODE_TYPE.UTF_8,
    table: undefined,
    timeType: 1,
    timeTypeArr: [1],
    timeZone: 'Asia/Shanghai', // 默认时区值
    timeColumn: undefined,
    offset: 0,
    offsetUnit: 'SECOND',
    columnsText: undefined,
    parallelism: 1,
    offsetReset: 'latest',
    timestampOffset: null,
    sourceDataType: 'dt_nest'
}
export default function FlinkSourcePanel({ current }: Pick<IEditor, 'current'>) {
    const currentPage = current?.tab?.data || {};
    const isLocked = currentPage.readWriteLockVO && !currentPage.readWriteLockVO.getLock;
    const [createTypes, setCreateTypes] = useState<any[]>([])
    const [inputData, setInputData] = useState(initInputData);
    const [panelActiveKey, setPanelActiveKey] = useState<string[]>([]);
    const [panelColumn, setPanelColumn] = useState(currentPage?.source || []);
    const [originOptionType, setOriginOptionType] = useState<any>({});
    const [assetTableOptionType, setAssetTableOptionType] = useState<any>({});
    const [topicOptionType, setTopicOptionType] = useState<any>({});
    const [sync, setSync] = useState(false);
    const [timeZoneData, setTimeZoneData] = useState<any>([]);
    const [dataBaseOptionType, setDataBaseOptionType] = useState<any>([]);
    const [sourceTableTypes, setSourceTableTypes] = useState<any>([]);

    const getCreateType = async () => {
        const list = await getCreateTypes();
        setCreateTypes(list);
        initData(list);
        initDataBaseList(list)
    }
    const initData = (createTypes: any[] = []) => {
        const tableSourceCreate = createTypes?.find((item: any) => item.createType === TABLE_SOURCE.DATA_CREATE);
        if (tableSourceCreate) {
            setInputData({
                ...inputData,
                createType: tableSourceCreate.valid ? TABLE_SOURCE.DATA_CREATE : TABLE_SOURCE.DATA_ASSET
            })
        }
    }
   const initTimeZoneList = async () => {
        const list = await getTimeZoneList()
        setTimeZoneData(list)
    }
    const initDataBaseList = async (createTypes: any[] = []) => {
        const list = await getDataBaseList(createTypes);
        setDataBaseOptionType(list)
    }
    const getSourceTableTypes = async () => {
        const res = await stream.getSourceTableTypes();
        if (res.code == 1) {
            setSourceTableTypes(res.data || [])
        }
    }
    const handleActiveKey = (key: any) => {
        setPanelActiveKey(key);
    }

     // 获取数据源
    const getTypeOriginData = async (type: any) => {
        if (type) {
            const existData = originOptionType[type];
            if (existData) {
                return;
            }
            let v = await stream.getTypeOriginData({ type });
            if (v.code === 1) {
                originOptionType[type] = v.data
            } else {
                originOptionType[type] = []
            }
            // 没有新建对象来 setState，当有多个源表同时请求数据源的话，新建对象的话会导致旧对象会被新对象覆盖掉
            setOriginOptionType(originOptionType)
        }
    }

    // 获取元数据下对应表
    const getAssetTableList = async (dbId: any, searchKey?: string) => {
        if (dbId) {
            const existData = assetTableOptionType[dbId];
            if (existData) {
                return;
            }
            let v = await stream.getAssetTableList({
                dbId: dbId,
                tableType: TABLE_TYPE.SOURCE_TABLE
            });
            if (v.code == 1) {
                setAssetTableOptionType({
                    ...assetTableOptionType,
                    [dbId]: v.data
                })
            }
        }
    }

    // 获取元数据下所有信息
    const getAssetData = async (tableId: any) => {
        let params = {};
        const res = await stream.getAssetTableDetail({
            tableId
        })
        if (res.code === 1 && !isEmpty(res.data)) {
            const { sourceTableParam, columns, charset } = res.data;
            params = Object.assign(sourceTableParam, {
                columns: columns,
                charset
            })
        }
        return params;
    }

    const getTopicType = async (sourceId: any) => {
        if (sourceId) {
            const existTopic = topicOptionType[sourceId];
            if (existTopic) {
                return;
            }
            let v = await stream.getTopicType({ sourceId });
            if (v.code == 1) {
                setTopicOptionType({
                    ...topicOptionType,
                    [sourceId]: v.data
                })
            }
        }
    }

    const changeInputTabs = (type: any, panelKey?: any) => {
        let panelActiveKeyScop = panelActiveKey;
        let panelColumnScop = panelColumn;
        if (type === 'add') {
            const key = Utils.generateAKey();
            panelColumnScop.push({
                ...inputData,
                _panelKey: key
            });
            getTypeOriginData(inputData.type);
            getTopicType(inputData.sourceId);
            getAssetTableList(inputData.dbId);
            panelActiveKeyScop.push(key)
        } else {
            panelColumnScop = panelColumnScop.filter((panel: any) => {
                return panelKey != panel._panelKey
            });
            panelActiveKeyScop = panelActiveKeyScop.filter((key) => {
                return panelKey != key
            });
        }
        streamTaskActions.setCurrentPageValue('source', panelColumnScop, true)
        setPanelActiveKey(panelActiveKeyScop);
        setPanelColumn(panelColumnScop);
    }
    // 时区不做处理
    const handleInputChange = async (index: any, type: any, value: any, subValue: any) => { // 监听数据改变
        let panelColumnSocp = cloneDeep(panelColumn);
        let panel = panelColumnSocp[index];
        let shouldUpdateEditor = false;
        switch (type) {
            case 'createType': {
                panel = panelColumnSocp[index] = { ...inputData, _panelKey: panel._panelKey, sourceDataType: panel.sourceDataType };
                panel[type] = value;
                if (value === TABLE_SOURCE.DATA_CREATE) {
                    panelColumnSocp['type'] = DEFAULT_TYPE;
                    getTypeOriginData(DEFAULT_TYPE);
                }
                shouldUpdateEditor = true;
                break;
            }
            case 'dbId': {
                const db = dataBaseOptionType.find((item: any) => item.dbId === value);
                panel = panelColumnSocp[index] = {
                    ...inputData,
                    createType: panel.createType,
                    [type]: value,
                    assetsDbName: db?.dbName,
                    _panelKey: panel._panelKey,
                    sourceDataType: panel.sourceDataType
                };
                getAssetTableList(value);
                shouldUpdateEditor = true;
                break;
            }
            case 'tableId': {
                // 获取当前元数据的类型，并设置
                const tableList = assetTableOptionType[panel.dbId] || [];
                const tableData = tableList.find((item: any) => item.tableId === value);
                const params = await getAssetData(value);
                panel = panelColumnSocp[index] = {
                    ...inputData,
                    ...params,
                    createType: panel.createType,
                    dbId: panel.dbId,
                    assetsDbName: panel.assetsDbName,
                    [type]: value,
                    assetsTableName: tableData?.tableName,
                    _panelKey: panel._panelKey,
                    sourceDataType: panel.sourceDataType
                };
                shouldUpdateEditor = true;
                break;
            }
            case 'targetCol': {
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
                panel['columns'][value].targetCol = val;

                let columns = panel.columns.map(({ column, targetCol }: any) => ({ column: targetCol || column }));
                panel.timeColumn = timeColumnCheck(columns);
                break;
            }
            case 'type': {
                panel = panelColumnSocp[index] = { ...inputData, createType: panel.createType, _panelKey: panel._panelKey };
                panel[type] = value;
                getTypeOriginData(value);
                shouldUpdateEditor = true;
                if (isKafka(value)) {
                    if (value === DATA_SOURCE_ENUM.KAFKA_CONFLUENT) {
                        panel['sourceDataType'] = KAFKA_DATA_TYPE.TYPE_AVRO_CONFLUENT;
                    } else {
                        panel['sourceDataType'] = KAFKA_DATA_TYPE.TYPE_JSON;
                    }
                }

                break;
            }
            case 'sourceId': {
                panel = panelColumnSocp[index] = {
                    ...inputData,
                    createType: panel.createType,
                    type: panel.type,
                    _panelKey: panel._panelKey,
                    sourceDataType: panel.sourceDataType
                };
                panel[type] = value;
                getTopicType(value);
                shouldUpdateEditor = true;
                break;
            }
            case 'customParams': {
                changeCustomParams(panel, value, subValue);
                break;
            }
            case 'columnsText': {
                let columns = parseColumnText(value);
                panel[type] = value;
                panel.timeColumn = timeColumnCheck(columns);
                break;
            }
            case 'sourceDataType':
                panel[type] = value;
                if (!isAvro(value)) {
                    panel.schemaInfo = undefined;
                }
                break;
            case 'timeTypeArr':
                panel[type] = value;
                // timeTypeArr 这个字段只有前端用，根据 timeTypeArr ，清空相应字段
                // 不勾选 ProcTime，不传 procTime 名称字段
                // 不勾选 EventTime，不传时间列、最大延迟时间字段
                if (currentPage.componentVersion === '1.12') {
                    if (!value.includes(1)) {
                        panel.procTime = undefined
                    }
                    if (!value.includes(2)) {
                        panel.timeColumn = undefined
                        panel.offset = undefined
                    }
                }
                break
            default: {
                panel[type] = value;
            }
        }
        streamTaskActions.setCurrentPageValue('source', panelColumnSocp, true)
        setSync(shouldUpdateEditor);
        setPanelColumn(panelColumnSocp)

        // timeColumn 是否需要重置
        function timeColumnCheck (columns: any) {
            if (panel.timeColumn) {
                if (!columns.find((c: any) => {
                    return c.column == panel.timeColumn
                })) {
                    return undefined;
                }
            }
            return panel.timeColumn;
        }
    }

    useEffect(() => {
        getCreateType();
        initTimeZoneList();
        getSourceTableTypes();
    }, [])
    const panelHeader = (index: number, panelKey: any, panelData: any) => {
        const tableName = panelData.table;
        return <div className="input-panel-title">
            <span>{` 源表 ${index + 1} ${tableName ? `(${tableName})` : ''}`}</span>
            <Popconfirm
                disabled={isLocked}
                placement="topLeft"
                title="你确定要删除此源表吗？"
                onConfirm={(e) => {
                    changeInputTabs('delete', panelKey)
                }}
                {...{
                    onClick: (e: any) => {
                        e.stopPropagation()
                    }
                }}
            >
                <DeleteOutlined className={classNames('title-icon', { 'lock-panel-icon': isLocked })} />
            </Popconfirm>
        </div>
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
    return <div className="m-taksdetail panel-content">
        <Collapse activeKey={panelActiveKey} bordered={false} onChange={handleActiveKey}>
            {
                panelColumn.map((panelColumnData: any, index: any) => {
                    const key = panelColumnData._panelKey;
                    return (
                        <Panel
                            header={panelHeader(index, key, panelColumnData)}
                            key={key}
                            style={{ position: 'relative' }}
                            className="input-panel"
                        >
                            <SourceForm
                                isShow={panelActiveKey.indexOf(key) > -1 && isInValidTab}
                                sync={sync}
                                handleInputChange={handleInputChange.bind(undefined, index)}
                                panelColumn={panelColumnData}
                                createTypes={createTypes || []}
                                sourceTableTypes={sourceTableTypes}
                                dataBaseOptionType={dataBaseOptionType}
                                getDataBaseList={initDataBaseList.bind(undefined, createTypes)}
                                topicOptionType={topicOptionType[panelColumnData.sourceId] || []}
                                assetTableOptionType={assetTableOptionType[panelColumnData.dbId] || []}
                                originOptionType={originOptionType[panelColumnData.type] || []}
                                timeZoneData={timeZoneData}
                                currentPage={currentPage}
                                textChange={() => { setSync(false) }}
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
        ><PlusOutlined /><span> 添加源表</span></Button>
    </div>
}