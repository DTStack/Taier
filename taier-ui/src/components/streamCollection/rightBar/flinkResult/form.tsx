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

import { DATA_SOURCE_ENUM, DATA_SOURCE_VERSION, defaultColsText, DEFAULT_MAPPING_TEXT, FLINK_VERSIONS, hbaseColsText, hbaseColsText112, HELP_DOC_URL, KAFKA_DATA_LIST, KAFKA_DATA_TYPE, TABLE_SOURCE, TABLE_TYPE } from "@/constant";
import { formatSourceTypes, isRDB } from "@/utils";
import { getFlinkDisabledSource, haveCollection, haveDataPreview, haveParallelism, havePartition, havePrimaryKey, haveTableColumn, haveTableList, haveTopic, haveUpdateMode, haveUpdateStrategy, haveUpsert, isAvro, isES, isHbase, isKafka, isSqlServer, mergeSourceType } from "@/utils/enums";
import { Checkbox, Col, Form, Input, InputNumber, message, Popconfirm, Radio, Row, Select, Table, Tooltip } from "antd";
import { CloseOutlined, UpOutlined, DownOutlined } from "@ant-design/icons"
import Column from "antd/lib/table/Column";
import { debounce, isUndefined } from "lodash";
import React, { useState } from "react";
import { AssetPanel } from "../component/assetPanel";
import { generateMapValues, getColumnsByColumnsText } from "../customParamsUtil";
import Editor from "@/components/editor";
import { CustomParams } from "../component/customParams";
import DataPreviewModal from "../../source/dataPreviewModal";
import { outputDefaultValue } from "../flinkHelper";
const formItemLayout: any = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 18 }
    }
};
const FormItem = Form.Item;
const Option = Select.Option
interface IResultProps {
    isShow: boolean;
    sync: boolean;
    index: number;
    getTableType: (index: any, sourceId: any, schema?: any, searchKey?: any) => void;
    getSchemaData: (index: any, sourceId: any, searchKey?: any) => void;
    handleInputChange: (type: any, index: any, value?: any, subValue?: any) => void;
    resultTableTypes: any[];
    dataBaseOptionType: any[];
    getDataBaseList: () => void;
    panelColumn: any[];
    originOptionType: any[];
    tableOptionType: any[];
    assetTableOptionType: any[];
    schemaOptionType: any[];
    tableColumnOptionType: any[];
    topicOptionType: any[];
    partitionOptionType: any[];
    onRef: (ref: any) => void;
    textChange: () => void;
    createTypes: any[];
    currentPage: any;
}

export default function ResultForm({
    isShow,
    sync,
    index,
    getTableType,
    getSchemaData,
    handleInputChange,
    resultTableTypes,
    dataBaseOptionType,
    getDataBaseList,
    panelColumn,
    originOptionType,
    tableOptionType,
    assetTableOptionType,
    schemaOptionType,
    tableColumnOptionType,
    topicOptionType,
    partitionOptionType,
    onRef,
    textChange,
    createTypes,
    currentPage,
}: IResultProps) {
    const { componentVersion } = currentPage || {};
    const [form] = Form.useForm();
    const [visible, setVisible] = useState(false);
    const [params, setParams] = useState({});
    const [editorRef, setEditorRef] = useState()
    const [showAdvancedParams, setShowAdvancedParams] = useState(false)

    const originOption = (type: any, arrData: any) => {
        switch (type) {
            case 'dataSource':
                const allow110List = [DATA_SOURCE_ENUM.TBDS_HBASE, DATA_SOURCE_ENUM.TBDS_KAFKA, DATA_SOURCE_ENUM.KAFKA_HUAWEI, DATA_SOURCE_ENUM.HBASE_HUAWEI];
                const allow112List = [DATA_SOURCE_ENUM.CSP_S3, DATA_SOURCE_ENUM.UPRedis, DATA_SOURCE_ENUM.HIVE, DATA_SOURCE_ENUM.INCEPTOR, DATA_SOURCE_ENUM.KAFKA_CONFLUENT];
                const disabled112List = [DATA_SOURCE_ENUM.POLAR_DB_For_MySQL, DATA_SOURCE_ENUM.TIDB, DATA_SOURCE_ENUM.IMPALA, DATA_SOURCE_ENUM.S3]
                return arrData.map((v: { name: string; value: number }) => {
                    const {
                        ONLY_FLINK_1_12_DISABLED,
                        ONLY_ALLOW_FLINK_1_10_DISABLED,
                        ONLY_ALLOW_FLINK_1_12_DISABLED
                    } = getFlinkDisabledSource({
                        version: componentVersion,
                        value: v.value,
                        disabled112List,
                        allow110List,
                        allow112List
                    });
                    return <Option key={v.value} value={v.value} disabled={ONLY_FLINK_1_12_DISABLED || ONLY_ALLOW_FLINK_1_12_DISABLED || ONLY_ALLOW_FLINK_1_10_DISABLED}>{v.name}</Option>
                })
            case 'originType':
                return arrData.map((v: any) => {
                    return <Option key={v} value={`${v.id}`}>{v.name}{DATA_SOURCE_VERSION[v.type as DATA_SOURCE_ENUM] && ` (${DATA_SOURCE_VERSION[v.type as DATA_SOURCE_ENUM]})`}</Option>
                })
            case 'schemaType':
                return arrData.map((v: any) => {
                    return <Option key={v} value={`${v}`}>{v}</Option>
                })
            case 'currencyType':
                return arrData.map((v: any) => {
                    return <Option key={v} value={`${v}`}>
                        <Tooltip placement="topLeft" title={v}>
                            <span className="panel-tooltip">{v}</span>
                        </Tooltip>
                    </Option>
                })
            case 'columnType':
                return arrData.map((v: any, index: any) => {
                    return <Option key={index} value={`${v.key}`}>
                        <Tooltip placement="topLeft" title={v.key}>
                            <span className="panel-tooltip">{v.key}</span>
                        </Tooltip>
                    </Option>
                })
            case 'primaryType':
                return arrData.map((v: any, index: any) => {
                    return <Option key={index} value={`${v.column}`}>{v.column}</Option>
                })
            case 'kafkaPrimaryType':
                return arrData.map((v: any, index: any) => {
                    return <Option key={index} value={`${v.field}`}>{v.field}</Option>
                })
            case 'partitionType':
                return arrData.map((v: any, index: any) => {
                    return <Option key={index} value={`${v}`}>{v}</Option>
                })
            case 'database':
                return arrData.map((db: any) => (
                    <Option key={db.dbId} value={db.dbId}>{db.dbName}</Option>
                ))
            case 'assetTable':
                return arrData.map((table: any) => (
                    <Option key={table.tableId} value={table.tableId}>{table.tableName}</Option>
                ))
            default:
                return null;
        }
    }
    // 表远程搜索
    const handleTableSearch = (value: string, index: number) => {
        const sourceId = form.getFieldValue('sourceId');
        const schema = form.getFieldValue('schema');
        if (sourceId) {
            getTableType(index, sourceId, schema, value);
        }
    }

    const debounceHandleTableSearch = debounce(handleTableSearch, 300);

    const editorParamsChange = (a: any, b: any, c: any) => {
        textChange();
        handleInputChange('columnsText', index, b);
    }

    const debounceEditorChange = debounce(editorParamsChange, 300, { 'maxWait': 2000 })


    const getPlaceholder = (sourceType: string) => {
        if (isHbase(sourceType)) {
            return componentVersion === FLINK_VERSIONS.FLINK_1_12 ? hbaseColsText112 : hbaseColsText
        } else {
            return defaultColsText
        }
    }
    const isDisabledUpdateMode = (type: any, isHiveTable: boolean | undefined, version?: string): boolean => {
        if (type === DATA_SOURCE_ENUM.IMPALA) {
            if (isUndefined(isHiveTable) || isHiveTable === true) {
                return true
            } else if (isHiveTable === false) {
                return false
            }
        } else {
            return !haveUpsert(type, version)
        }
        return false
    }

    const showPreviewModal = () => {
        const { sourceId, index: tableIndex, table, type, schema, createType, tableId } = panelColumn[index]
        let params = {}

        if (createType === TABLE_SOURCE.DATA_ASSET) {
            if (!tableId) {
                message.error('数据预览需要选择数据表！')
                return
            }
            setVisible(true);
            setParams({
                tableId,
                tableType: TABLE_TYPE.OUTPUT_TABLE
            })
            return;
        }
        switch (type) {
            case DATA_SOURCE_ENUM.ES7: {
                if (!sourceId || !tableIndex) {
                    message.error('数据预览需要选择数据源和索引！')
                    return
                }
                params = { sourceId, tableName: tableIndex }
                break
            }
            case DATA_SOURCE_ENUM.REDIS:
            case DATA_SOURCE_ENUM.UPRedis:
            case DATA_SOURCE_ENUM.HBASE:
            case DATA_SOURCE_ENUM.TBDS_HBASE:
            case DATA_SOURCE_ENUM.HBASE_HUAWEI:
            case DATA_SOURCE_ENUM.MYSQL:
            case DATA_SOURCE_ENUM.UPDRDB:
            case DATA_SOURCE_ENUM.HIVE:
            case DATA_SOURCE_ENUM.INCEPTOR: {
                if (!sourceId || !table) {
                    message.error('数据预览需要选择数据源和表！')
                    return
                }
                params = { sourceId, tableName: table }
                break
            }
            case DATA_SOURCE_ENUM.ORACLE: {
                if (!sourceId || !table || !schema) {
                    message.error('数据预览需要选择数据源、表和schema！')
                    return
                }
                params = { sourceId, tableName: table, schema }
                break
            }
            case DATA_SOURCE_ENUM.SQLSERVER:
            case DATA_SOURCE_ENUM.SQLSERVER_2017_LATER: {
                if (!sourceId || !table) {
                    message.error('数据预览需要选择数据源和表！')
                    return
                }
                params = { sourceId, tableName: table, schema }
                break
            }
        }

        setVisible(true);
        setParams(params)
    }
    // 公共部分
    const initCommonElements = () => {
        const element = (
            haveDataPreview(panelColumn[index].type) ? <Row>
                <Col offset={6} style={{ marginBottom: 12 }}>
                    <a style={{ color: '#3f87ff' }} onClick={showPreviewModal}>数据预览</a>
                </Col>
            </Row> : undefined
        )
        return element;
    }

    const mapPropsToFields = () => {
        const {
            sourceId, type, table, primaryKey, customParams, sinkDataType,
            dbId, assetsDbName, tableId, assetsTableName
        } = panelColumn[index];
        const assetTableOption = assetTableOptionType[index];
        const initialSourceIdValue = originOptionType.length ? sourceId : undefined;
        let values = {};
        Object.entries(panelColumn[index]).forEach(([key, value]) => {
            if (outputDefaultValue(key)) {
                values = Object.assign({}, values, {
                    [key]: value
                })
            }
        })

        let initialDbId = (dataBaseOptionType && dataBaseOptionType.length > 0) ? dbId : assetsDbName;
        let initialTableId = (assetTableOption && assetTableOption.length > 0 ? tableId : assetsTableName)

        return {
            sourceId: initialSourceIdValue,
            type: mergeSourceType(parseInt(type)),
            'table-input': table,
            'primaryKey-input': primaryKey,
            sinkDataType: sinkDataType,
            ...values,
            ...generateMapValues(customParams),
            dbId: initialDbId,
            tableId: initialTableId
        }
    }

    const data = panelColumn[index];
    const isAssetCreate = data?.createType === TABLE_SOURCE.DATA_ASSET;
    const partitionData = partitionOptionType[index] || [];
    const dataSourceOptionTypes = originOption('dataSource', formatSourceTypes(resultTableTypes) || []);
    const originOptionTypes = originOption('originType', originOptionType[index] || []);
    const tableOptionTypes = originOption('currencyType', tableOptionType[index] || []);
    const topicOptionTypes = originOption('currencyType', topicOptionType[index] || []);
    const tableColumnOptionTypes = originOption('columnType', tableColumnOptionType[index] || []);
    const primaryKeyOptionTypes = componentVersion === FLINK_VERSIONS.FLINK_1_12 && isKafka(panelColumn[index]?.type)
        ? originOption('kafkaPrimaryType', getColumnsByColumnsText(panelColumn[index]?.columnsText) || [])
        : originOption('primaryType', panelColumn[index].columns || [])
    const partitionOptionTypes = originOption('partitionType', partitionData);
    const schemaOptionTypes = originOption('schemaType', schemaOptionType[index] || []);
    const dataBaseOptionTypes = originOption('database', dataBaseOptionType || [])
    const assetTableOptionTypes = originOption('assetTable', assetTableOptionType[index] || [])
    const disableUpdateMode = panelColumn[index]['isShowPartition']; // isShowPartition 为 false 为 kudu 表
    const showBucket = [DATA_SOURCE_ENUM.S3, DATA_SOURCE_ENUM.CSP_S3].includes(data.type);
    const schemaRequired = [DATA_SOURCE_ENUM.POSTGRESQL, DATA_SOURCE_ENUM.KINGBASE8, DATA_SOURCE_ENUM.SQLSERVER, DATA_SOURCE_ENUM.SQLSERVER_2017_LATER].includes(data.type);
    const showSchema = data.type == DATA_SOURCE_ENUM.ORACLE || data.type == DATA_SOURCE_ENUM.POSTGRESQL || data.type == DATA_SOURCE_ENUM.KINGBASE8 || isSqlServer(data.type);
    const isFlink112 = componentVersion === FLINK_VERSIONS.FLINK_1_12;
    const partitionfieldsNoEdit = [DATA_SOURCE_ENUM.HIVE, DATA_SOURCE_ENUM.INCEPTOR].includes(data.type);
    const partitionKeyLists = isAssetCreate
        ? (data?.columns || []).map((item: any) => ({ field: item.targetCol || item.column, type: item.type }))
        : getColumnsByColumnsText(data?.columnsText);
    return <Row className="title-content">
        <Form
            {...formItemLayout}
            form={form}
            initialValues={mapPropsToFields()}
        >
            <FormItem
                label="表来源"
                style={{ display: createTypes?.length > 1 ? 'block' : 'none' }}
                name='createType'
                rules={[{ required: true, message: '请选择表来源' }]}
            >
                <Radio.Group onChange={(e: any) => { handleInputChange('createType', index, e.target.value) }}>
                    {Array.isArray(createTypes) && createTypes.map((item: any) => (
                        <Radio key={item.createType} value={item.createType} disabled={!item.valid}>{item.label}</Radio>
                    ))}
                </Radio.Group>
            </FormItem>
            {isAssetCreate ? <AssetPanel
                pIndex={index}
                panelColumn={panelColumn}
                handleInputChange={handleInputChange}
                assetTableOptionTypes={assetTableOptionTypes}
                dataBaseOptionTypes={dataBaseOptionTypes}
                getDataBaseList={getDataBaseList}
                embedElm={initCommonElements()}
                hideTargetCol={true}
            />
                : <React.Fragment>
                    <FormItem
                        label="存储类型"
                        name='type'
                        rules={[{ required: true, message: '请选择存储类型' }]}
                    >
                        <Select
                            className="right-select"
                            onChange={(v: any) => { handleInputChange('type', index, v) }}
                            showSearch
                            filterOption={(input: any, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                        >
                            {dataSourceOptionTypes}
                        </Select>
                    </FormItem>
                    <FormItem
                        label="数据源"
                        name='sourceId'
                        initialValue={'∂disabled'}
                        rules={[{ required: true, message: '请选择数据源' }]}
                    >
                        <Select
                            showSearch
                            placeholder="请选择数据源"
                            className="right-select"
                            onChange={(v: any) => { handleInputChange('sourceId', index, v) }}
                            filterOption={(input: any, option: any) => option.props.children.toString().toLowerCase().indexOf(input.toLowerCase()) >= 0}
                        >
                            {
                                originOptionTypes
                            }
                        </Select>
                    </FormItem>
                    {
                        haveCollection(panelColumn[index].type) && <FormItem
                            label="Collection"
                            name='collection'
                            rules={[{ required: true, message: '请选择Collection' }]}
                        >
                            <Select
                                showSearch
                                allowClear
                                placeholder="请选择Collection"
                                className="right-select"
                                onChange={(v: any) => { handleInputChange('collection', index, v) }}
                                filterOption={(input: any, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                            >
                                {tableOptionTypes}
                            </Select>
                        </FormItem>
                    }
                    {
                        showBucket
                            ? <FormItem
                                label="Bucket"
                                name='bucket'
                                rules={[{ required: Boolean(schemaRequired), message: '请选择Bucket' }]}
                            >
                                <Select
                                    showSearch
                                    allowClear
                                    placeholder="请选择Bucket"
                                    className="right-select"
                                    onChange={(v: any) => { handleInputChange('bucket', index, v) }}
                                >
                                    {tableOptionTypes}
                                </Select>
                            </FormItem> : ''
                    }
                    {
                        [DATA_SOURCE_ENUM.S3, DATA_SOURCE_ENUM.CSP_S3].includes(panelColumn[index].type)
                            ? <FormItem
                                label="ObjectName"
                                name='objectName'
                                rules={[{ required: true, message: '请输入ObjectName' }]}
                                tooltip='默认以标准存储，txt格式保存至S3 Bucket内'
                            >
                                <Input
                                    placeholder="请输入ObjectName"
                                    style={{ width: '90%' }}
                                    onChange={(e: any) => handleInputChange('objectName', index, e.target.value)}
                                />
                            </FormItem> : ''
                    }
                    {
                        showSchema
                            ? <FormItem
                                label="Schema"
                                name='schema'
                                rules={[{ required: Boolean(schemaRequired), message: '请输入Schema' }]}
                            >
                                <Select
                                    showSearch
                                    allowClear
                                    placeholder="请选择Schema"
                                    className="right-select"
                                    onChange={(v: any) => { handleInputChange('schema', index, v) }}
                                    filterOption={(input: any, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                                >
                                    {
                                        schemaOptionTypes
                                    }
                                </Select>
                            </FormItem> : ''
                    }
                    {
                        haveTopic(panelColumn[index].type)
                            ? <FormItem
                                label="Topic"
                                name='topic'
                                rules={[{ required: true, message: '请选择Topic' }]}
                            >
                                <Select
                                    placeholder="请选择Topic"
                                    className="right-select"
                                    onChange={(v: any) => { handleInputChange('topic', index, v) }}
                                    showSearch
                                >
                                    {
                                        topicOptionTypes
                                    }
                                </Select>
                            </FormItem> : ''
                    }
                    {haveTableList(panelColumn[index].type) && ![DATA_SOURCE_ENUM.S3, DATA_SOURCE_ENUM.CSP_S3].includes(panelColumn[index].type)
                        ? <FormItem
                            label="表"
                            name='table'
                            initialValue='disabled'
                            rules={[{ required: true, message: '请选择表' }]}
                        >
                            <Select
                                showSearch
                                className="right-select"
                                onChange={(v: any) => { handleInputChange('table', index, v) }}
                                onSearch={(value: string) => debounceHandleTableSearch(value, index)}
                                filterOption={false}
                            >
                                {
                                    tableOptionTypes
                                }
                            </Select>
                        </FormItem> : ''
                    }
                    {
                        [DATA_SOURCE_ENUM.REDIS, DATA_SOURCE_ENUM.UPRedis].includes(panelColumn[index].type)
                            ? <FormItem
                                label="表"
                                name='table-input'
                                initialValue={'disabled'}
                                rules={[{ required: true, message: '请输入表名' }]}
                            >
                                <Input onChange={(v: any) => { handleInputChange('table', index, v.target.value) }} />
                            </FormItem> : ''
                    }
                    {isES(panelColumn[index].type) &&
                        <FormItem
                            label='索引'
                            tooltip={<span>
                                {'支持输入{column_name}作为动态索引，动态索引需包含在映射表字段中，更多请参考'}
                                <a
                                    rel="noopener noreferrer"
                                    target="_blank"
                                    href={HELP_DOC_URL.INDEX}>
                                    帮助文档
                                </a>
                            </span>}
                            name='index'
                            rules={[{ required: true, message: '请输入索引' }]}
                        >
                            <Input placeholder="请输入索引" onChange={(e: any) => handleInputChange('index', index, e.target.value)} />
                        </FormItem>
                    }
                    {initCommonElements()}
                    {[DATA_SOURCE_ENUM.REDIS, DATA_SOURCE_ENUM.UPRedis].includes(panelColumn[index].type) &&
                        <FormItem
                            label="主键"
                            name='primaryKey-input'
                            rules={[{ required: true, message: '请输入主键' }]}
                        >
                            <Input
                                placeholder="结果表主键，多个字段用英文逗号隔开"
                                onChange={(e: any) => handleInputChange('primaryKey', index, e.target.value)}
                            />
                        </FormItem>
                    }
                    {isES(panelColumn[index].type) &&
                        <FormItem
                            label='id'
                            tooltip='id生成规则：填写字段的索引位置（从0开始）'
                            name='esId'
                        >
                            <Input placeholder="请输入id" onChange={(e: any) => handleInputChange('esId', index, e.target.value)} />
                        </FormItem>
                    }
                    {(panelColumn[index].type == DATA_SOURCE_ENUM.ES || panelColumn[index].type == DATA_SOURCE_ENUM.ES6) &&
                        <FormItem
                            label="索引类型"
                            name='esType'
                            rules={[{ required: true, message: '请输入索引类型' }]}
                        >
                            <Input placeholder="请输入索引类型" onChange={(e: any) => handleInputChange('esType', index, e.target.value)} />
                        </FormItem>}
                    {
                        panelColumn[index].type == DATA_SOURCE_ENUM.HBASE &&
                        <FormItem
                            label='rowKey'
                            tooltip={isFlink112 ? <>
                                Hbase 表 rowkey 字段支持类型可参考&nbsp;
                                <a
                                    href={HELP_DOC_URL.HBASE}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                >帮助文档</a>
                            </>
                                : '支持拼接规则：md5(fieldA+fieldB) + fieldC + \'常量字符\''}
                            name=''
                        >
                            <div style={{ display: 'flex' }}>
                                <FormItem
                                    style={{ flex: 1 }}
                                    name='rowKey'
                                    rules={[
                                        { required: true, message: '请输入rowKey' },
                                        isFlink112 ? { pattern: /^\w{1,64}$/, message: '只能由字母，数字和下划线组成，且不超过64个字符' } : {}
                                    ]}
                                >
                                    <Input
                                        placeholder={isFlink112 ? '请输入 rowkey' : 'rowKey 格式：填写字段1+填写字段2'}
                                        onChange={e => handleInputChange('rowKey', index, e.target.value)}
                                    />
                                </FormItem>
                                {isFlink112 && <>
                                    <span>&nbsp; 类型：</span>
                                    <FormItem
                                        style={{ flex: 1 }}
                                        name='rowKeyType'
                                        rules={[{ required: true, message: '请输入rowKey类型' }]}
                                    >
                                        <Input
                                            placeholder="请输入类型"
                                            onChange={e => handleInputChange('rowKeyType', index, e.target.value)}
                                        />
                                    </FormItem>
                                </>}
                            </div>
                        </FormItem>
                    }
                    {
                        [DATA_SOURCE_ENUM.TBDS_HBASE, DATA_SOURCE_ENUM.HBASE_HUAWEI].includes(panelColumn[index].type)
                            ? <FormItem
                                label='rowKey'
                                tooltip="支持拼接规则：md5(fieldA+fieldB) + fieldC + '常量字符'"
                                name='rowKey'
                                rules={[{ required: true, message: '请输入rowKey' }]}
                            >
                                <Input
                                    placeholder="rowKey 格式：填写字段1+填写字段2 "
                                    onChange={(e: any) => handleInputChange('rowKey', index, e.target.value)}
                                />
                            </FormItem> : ''
                    }
                    <FormItem
                        label="映射表"
                        name='tableName'
                        rules={[{ required: true, message: '请输入映射表名' }]}
                    >
                        <Input placeholder="请输入映射表名" onChange={(e: any) => handleInputChange('tableName', index, e.target.value)} />
                    </FormItem>
                    <Row>
                        <div className="ant-form-item-label ant-col-xs-24 ant-col-sm-6 required-tip">
                            <label className="required-tip">字段</label>
                        </div>
                        {
                            haveTableColumn(panelColumn[index].type)
                                ? <Col span={18} style={{ marginBottom: 20 }}>
                                    <div className="column-container">
                                        <Table rowKey="column" dataSource={panelColumn[index].columns} pagination={false} size="small">
                                            <Column
                                                title="字段"
                                                dataIndex="column"
                                                key="字段"
                                                width="45%"
                                                render={(text: any, record: any, subIndex: any) => {
                                                    return <Select
                                                        value={text}
                                                        showSearch
                                                        className="sub-right-select column-table__select"
                                                        onChange={(v: any) => { handleInputChange('subColumn', index, subIndex, v) }}
                                                    >
                                                        {
                                                            tableColumnOptionTypes
                                                        }
                                                    </Select>
                                                }}
                                            />
                                            <Column
                                                title="类型"
                                                dataIndex="type"
                                                key="类型"
                                                width="45%"
                                                render={(text: string, record: any, subIndex: any) => (
                                                    <span className={text?.toLowerCase() === 'Not Support'.toLowerCase() ? 'has-error' : ''}>
                                                        <Tooltip
                                                            title={text}
                                                            trigger={'hover'}
                                                            placement="topLeft"
                                                            overlayClassName="numeric-input"
                                                        >
                                                            <Input
                                                                value={text}
                                                                className="column-table__input"
                                                                onChange={(e: any) => { handleInputChange('subType', index, subIndex, e.target.value) }}
                                                            />
                                                        </Tooltip>
                                                    </span>
                                                )}
                                            />
                                            <Column
                                                key="delete"
                                                render={(text: any, record: any, subIndex: any) => {
                                                    return <CloseOutlined
                                                        style={{ fontSize: 12, color: '#999' }}
                                                        onClick={() => { handleInputChange('deleteColumn', index, subIndex) }} />
                                                }}
                                            />
                                        </Table>
                                        <div style={{ padding: '0 20 20' }}>
                                            <div className="stream-btn column-btn" style={{ borderRadius: 5 }}>
                                                <span>
                                                    <a onClick={() => { handleInputChange('columns', index, {}) }}>添加输入</a>
                                                </span>
                                                <span>
                                                    <a
                                                        onClick={() => { handleInputChange('addAllColumn', index) }}
                                                        style={{ marginRight: 12 }}
                                                    >导入全部字段</a>
                                                    {panelColumn[index]?.columns?.length > 0 ? <Popconfirm
                                                        title="确认清空所有字段？"
                                                        onConfirm={() => { handleInputChange('deleteAllColumn', index) }}
                                                        okText="确认"
                                                        cancelText="取消"
                                                    >
                                                        <a>清空</a>
                                                    </Popconfirm> : <a style={{ color: '#999' }}>清空</a>}
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                </Col>
                                : <Col span={18} style={{ marginBottom: 20, height: 200 }}>
                                    {isShow && (
                                        <Editor
                                            style={{ minHeight: 202, border: '1px solid #ddd', height: '100%' }}
                                            sync={sync}
                                            placeholder={getPlaceholder(panelColumn[index].type)}
                                            value={panelColumn[index].columnsText}
                                            onChange={debounceEditorChange}
                                            editorRef={(ref: any) => {
                                                setEditorRef(ref)
                                            }}
                                        />
                                    )}
                                </Col>
                        }
                    </Row>
                    {
                        havePartition(panelColumn[index].type) && panelColumn[index].storeType &&
                        <Row className="row-content">
                            <Col span={6}>表类型<span>:</span></Col>
                            <Col span={18}>{panelColumn[index].storeType}</Col>
                        </Row>
                    }
                    {
                        havePartition(panelColumn[index].type) &&
                        panelColumn[index].isShowPartition &&
                        !!partitionData.length &&
                        <FormItem
                            label='分区'
                            tooltip={partitionfieldsNoEdit
                                ? '表的分区字段，默认读取，不可编辑'
                                : ('默认写入所有表分区字段，可使用$' + '{pt}的方式声明静态分区')
                            }
                            name='partitionfields'
                            rules={[{ required: true, message: '请选择分区' }]}
                        >
                            <Select
                                className="right-select"
                                mode={partitionfieldsNoEdit ? undefined : 'multiple'}
                                onChange={(v: any) => { handleInputChange('partitionfields', index, v) }}
                                showSearch
                                filterOption={(input: any, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                            >
                                {partitionOptionTypes}
                            </Select>
                        </FormItem>
                    }
                </React.Fragment>
            }
            {isKafka(panelColumn[index].type) && (
                <React.Fragment>
                    <FormItem
                        label="输出类型"
                        name='sinkDataType'
                        rules={[{ required: true, message: '请选择输出类型' }]}
                    >
                        <Select style={{ width: '100%' }} onChange={(v: any) => { handleInputChange('sinkDataType', index, v) }}>
                            {panelColumn[index].type === DATA_SOURCE_ENUM.KAFKA_CONFLUENT
                                ? <Option value={KAFKA_DATA_TYPE.TYPE_AVRO_CONFLUENT} key={KAFKA_DATA_TYPE.TYPE_AVRO_CONFLUENT}>{KAFKA_DATA_TYPE.TYPE_AVRO_CONFLUENT}</Option>
                                : KAFKA_DATA_LIST.map(({ text, value }) => <Option value={value} key={text + value}>{text}</Option>)
                            }
                        </Select>
                    </FormItem>
                    {isAvro(form.getFieldValue('sinkDataType')) && <FormItem
                        label="Schema"
                        name='schemaInfo'
                        rules={[{ required: !isFlink112, message: '请输入Schema' }]}
                    >
                        <Input.TextArea
                            rows={9}
                            placeholder={`填写Avro Schema信息，示例如下：\n{\n\t"name": "testAvro",\n\t"type": "record",\n\t"fields": [{\n\t\t"name": "id",\n\t\t"type": "string"\n\t}]\n}`}
                            onChange={(e: any) => handleInputChange('schemaInfo', index, e.target.value)}
                        />
                    </FormItem>}
                </React.Fragment>
            )}
            {haveUpdateMode(panelColumn[index].type) && (
                <FormItem
                    label="更新模式"
                    name='updateMode'
                    rules={[{ required: true, message: '请选择更新模式' }]}
                >
                    <Radio.Group
                        disabled={isDisabledUpdateMode(panelColumn[index].type, disableUpdateMode, componentVersion)}
                        className="right-select"
                        onChange={(v: any) => { handleInputChange('updateMode', index, v.target.value) }}
                    >
                        <Radio value="append">追加(append)</Radio>
                        <Radio value="upsert" disabled={data.type == DATA_SOURCE_ENUM.CLICKHOUSE}>更新(upsert)</Radio>
                    </Radio.Group>
                </FormItem>
            )}
            {panelColumn[index].updateMode == 'upsert' && (haveUpdateStrategy(panelColumn[index].type)) &&
                <FormItem
                    label="更新策略"
                    name='allReplace'
                    initialValue='false'
                    rules={[{ required: true, message: '请选择更新策略' }]}
                >
                    <Select className="right-select" onChange={(v: any) => { handleInputChange('allReplace', index, v) }}
                    >
                        <Option key="true" value="true">Null值替换原有数据</Option>
                        <Option key="false" value="false">Null值不替换原有数据</Option>
                    </Select>
                </FormItem>}
            {
                panelColumn[index].updateMode == 'upsert' &&
                (havePrimaryKey(panelColumn[index].type) || !isDisabledUpdateMode(panelColumn[index].type, disableUpdateMode, componentVersion)) &&
                <FormItem
                    label='主键'
                    tooltip='主键必须存在于表字段中'
                    name='primaryKey'
                    rules={[{ required: true, message: '请输入主键' }]}
                >
                    <Select
                        className="right-select"
                        listHeight={200}
                        onChange={(v: any) => { handleInputChange('primaryKey', index, v) }}
                        mode="multiple"
                        showSearch
                        showArrow
                        filterOption={(input: any, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                    >
                        {primaryKeyOptionTypes}
                    </Select>
                </FormItem>
            }
            {/* 高级参数按钮 */}
            <div style={{ margin: '12px 0', textAlign: 'center' }}>
                <span
                    style={{ cursor: 'pointer', color: '#666666' }}
                    onClick={() => { setShowAdvancedParams(!showAdvancedParams) }}>
                    高级参数&nbsp;
                    {showAdvancedParams ? <UpOutlined /> : <DownOutlined />}
                </span>
            </div>
            {/* 高级参数抽屉 */}
            <div style={{ display: showAdvancedParams ? 'block' : 'none' }}>
                {haveParallelism(panelColumn[index].type) && <FormItem name='parallelism' label="并行度">
                    <InputNumber
                        className="number-input"
                        min={1}
                        precision={0}
                        onChange={(value: number) => handleInputChange('parallelism', index, value)} />
                </FormItem>}
                {isES(panelColumn[index].type) && isFlink112 && <FormItem name='bulkFlushMaxActions' label="数据输出条数">
                    <InputNumber
                        className="number-input"
                        min={1}
                        max={10000}
                        precision={0}
                        onChange={value => handleInputChange('bulkFlushMaxActions', index, value)} />
                </FormItem>}
                {isKafka(panelColumn[index].type) && <FormItem
                    label=""
                    name='enableKeyPartitions'
                    valuePropName="checked"
                >
                    <Checkbox
                        style={{ marginLeft: 90 }}
                        defaultChecked={false}
                        onChange={(e: any) => handleInputChange('enableKeyPartitions', index, e.target.checked)}
                    >
                        根据字段(Key)分区
                    </Checkbox>
                </FormItem>}
                {panelColumn[index].type == DATA_SOURCE_ENUM.ES7 &&
                    <FormItem
                        label='索引映射'
                        tooltip={<span>
                            {'ElasticSearch的索引映射配置，仅当动态索引时生效，更多请参考 '}
                            <a
                                rel="noopener noreferrer"
                                target="_blank"
                                href={HELP_DOC_URL.INDEX}>
                                帮助文档
                            </a>
                        </span>}
                        name='indexDefinition'
                    >
                        <Input.TextArea
                            placeholder={DEFAULT_MAPPING_TEXT}
                            style={{ minHeight: '200px' }}
                            onChange={(e: any) => { handleInputChange('indexDefinition', index, e.target.value) }}
                        />
                    </FormItem>}
                {panelColumn[index].enableKeyPartitions && <FormItem
                    label="分区字段"
                    name='partitionKeys'
                    rules={[{ required: true, message: '请选择分区字段' }]}
                >
                    <Select
                        className="right-select"
                        onChange={(v: any) => { handleInputChange('partitionKeys', index, v) }}
                        mode="multiple"
                        showSearch
                        showArrow
                        filterOption={(input: any, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                    >
                        {
                            Array.isArray(partitionKeyLists) && partitionKeyLists.map((column) => {
                                const fields = column.field?.trim();
                                return <Option value={fields} key={fields}>{fields}</Option>
                            })
                        }
                    </Select>
                </FormItem>}
                {isRDB(panelColumn[index].type) && <>
                    <FormItem
                        label="数据输出时间"
                        name='batchWaitInterval'
                        rules={[{ required: true, message: '请输入数据输出时间' }]}
                    >
                        <InputNumber
                            className="number-input"
                            min={0}
                            max={600000}
                            precision={0}
                            onChange={(value: any) => handleInputChange('batchWaitInterval', index, value)}
                            addonAfter='ms/次'
                        />
                    </FormItem>
                    <FormItem
                        label="数据输出条数"
                        name='batchSize'
                        rules={[{ required: true, message: '请输入数据输出条数' }]}
                    >
                        <InputNumber
                            className="number-input"
                            min={0}
                            max={panelColumn[index].type === DATA_SOURCE_ENUM.KUDU ? 100000 : 10000}
                            precision={0}
                            onChange={(value: any) => handleInputChange('batchSize', index, value)}
                            addonAfter='条/次'
                        />
                    </FormItem>
                </>}
                {!haveParallelism(panelColumn[index].type) &&
                    <FormItem
                        label='分区类型'
                        tooltip='分区类型包括 DAY、HOUR、MINUTE三种。若分区不存在则会自动创建，自动创建的分区时间以当前任务运行的服务器时间为准'
                        name='partitionType'
                        initialValue='DAY'
                    >
                        <Select
                            className="right-select"
                            onChange={v => { handleInputChange('partitionType', index, v) }}
                        >
                            <Option value="DAY">DAY</Option>
                            <Option value="HOUR">HOUR</Option>
                            <Option value="MINUTE">MINUTE</Option>
                        </Select>
                    </FormItem>}
                {/* 添加自定义参数 */}
                {!isSqlServer(panelColumn[index].type) && <CustomParams
                    formItemLayout={formItemLayout}
                    customParams={panelColumn[index].customParams || []}
                    onChange={(type: any, id: any, value: any) => { handleInputChange('customParams', index, value, { id, type }) }}
                />}
            </div>
            <DataPreviewModal
                visible={visible}
                type={panelColumn[index]?.type}
                isAssetCreate={isAssetCreate}
                onCancel={() => { setVisible(false) }}
                params={params}
            />
        </Form>
    </Row>
}