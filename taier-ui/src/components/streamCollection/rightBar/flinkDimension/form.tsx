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

import { DATA_SOURCE_ENUM, defaultColsText, hbaseColsText, hbaseColsText112, HELP_DOC_URL, TABLE_SOURCE, TABLE_TYPE } from "@/constant";
import { formatSourceTypes } from "@/utils";
import { getFlinkDisabledSource, haveAsyncPoolSize, haveCustomParams, haveDataPreview, haveTableColumn, isCacheExceptLRU, isCacheOnlyAll, isES, isHbase, isSqlServer } from "@/utils/enums";
import { Col, Form, Input, InputNumber, message, Popconfirm, Radio, Row, Select, Switch, Table, Tooltip } from "antd";
import { QuestionCircleOutlined, CloseOutlined } from '@ant-design/icons'
import { debounce } from "lodash";
import React, { useEffect, useState } from "react";
import { AssetPanel } from "../component/assetPanel";
import Editor from "@/components/editor";
import { asyncTimeoutNumDoc, queryFault } from "@/components/helpDoc/docs";
import { CustomParams } from "../component/customParams";
import DataPreviewModal from "../../source/dataPreviewModal";
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
const Option = Select.Option;

interface IDimensionFormProps {
    isShow: boolean;
    sync: boolean;
    index: number;
    getTableType: (index: any, sourceId: any, schema?: any, searchKey?: any) => void;
    getAssetTableList: (index: any, dbId: any, searchKey?: string) => void;
    getSchemaData: (index: any, sourceId: any, searchKey?: any) => void;
    handleInputChange: (type: any, index: any, value?: any, subValue?: any) => void;
    dataBaseOptionType: any[];
    getDataBaseList: () => void;
    panelColumn: any[];
    dimensionTableTypes: any[]
    originOptionType: any[];
    tableOptionType: any[];
    assetTableOptionType: any[];
    schemaOptionType: any[];
    tableColumnOptionType: any[];
    onRef: (ref: any) => void;
    textChange: () => void;
    createTypes: any[];
    currentPage: any;
}

export default function DimensionForm({
    isShow,
    index,
    sync,
    getTableType,
    handleInputChange,
    panelColumn,
    dimensionTableTypes,
    originOptionType,
    schemaOptionType,
    tableOptionType,
    dataBaseOptionType,
    getDataBaseList,
    assetTableOptionType,
    tableColumnOptionType,
    onRef,
    textChange,
    createTypes,
    currentPage,
}: IDimensionFormProps) {
    let _editorRef: any;
    const { componentVersion } = currentPage || {};
    const [form] = Form.useForm();
    const [visible, setVisible] = useState(false);
    const [params, setParams] = useState({});

    const editorParamsChange = (a: any, b: any, c: any) => {
        textChange();
        handleInputChange('columnsText', index, b);
    }
    const debounceEditorChange = debounce(editorParamsChange, 300, { 'maxWait': 2000 })

    // 表远程搜索
    const handleTableSearch = (value: string, index: number) => {
        const sourceId = form.getFieldValue('sourceId');
        const schema = form.getFieldValue('schema');
        if (sourceId) {
            getTableType(index, sourceId, schema, value);
        }
    }
    const debounceHandleTableSearch = debounce(handleTableSearch, 300)
    const refreshEditor = () => {
        if (_editorRef) {
            _editorRef.refresh();
        }
    }

    const originOption = (type: any, arrData: any) => {
        switch (type) {
            case 'dataSource':
                const allow110List = [DATA_SOURCE_ENUM.TBDS_HBASE, DATA_SOURCE_ENUM.TBDS_KAFKA, DATA_SOURCE_ENUM.KAFKA_HUAWEI, DATA_SOURCE_ENUM.HBASE_HUAWEI];
                const allow112List = [DATA_SOURCE_ENUM.UPRedis, DATA_SOURCE_ENUM.INCEPTOR];
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
                    return <Option key={v.value} value={v.value} disabled={ONLY_FLINK_1_12_DISABLED || ONLY_ALLOW_FLINK_1_10_DISABLED || ONLY_ALLOW_FLINK_1_12_DISABLED}>{v.name}</Option>
                })
            case 'originType':
                return arrData.map((v: any) => {
                    return <Option key={v} value={`${v.id}`}>{v.name}</Option>
                });
            case 'currencyType':
                return arrData.map((v: any) => {
                    return (
                        <Option key={v} value={`${v}`}>
                            <Tooltip placement="topLeft" title={v}>
                                <span className="panel-tooltip">{v}</span>
                            </Tooltip>
                        </Option>
                    );
                });
            case 'columnType':
                return arrData.map((v: any, index: any) => {
                    return (
                        <Option key={index} value={`${v.key}`}>
                            <Tooltip placement="topLeft" title={v.key}>
                                <span className="panel-tooltip">{v.key}</span>
                            </Tooltip>
                        </Option>
                    );
                });
            case 'primaryType':
                return arrData.map((v: any, index: any) => {
                    return (
                        <Option key={index} value={`${v.column}`}>
                            {v.column}
                        </Option>
                    );
                });
            case 'schemaType':
                return arrData.map((v: any) => {
                    return <Option key={v} value={`${v}`}>{v}</Option>
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
    /**
     * 
     * @returns 数据预览
     */
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
                tableType: TABLE_TYPE.DIMENSION_TABLE
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
    const getPlaceholder = (sourceType: string) => {
        if (isHbase(sourceType)) {
            return componentVersion === '1.12' ? hbaseColsText112 : hbaseColsText
        } else {
            return defaultColsText
        }
    }
    // 公共部分
    const initCommonElements = () => {
        const elements = (
            haveDataPreview(panelColumn[index].type) ? <Row>
                <Col offset={6} style={{ marginBottom: 12 }}>
                    <a style={{ color: '#3f87ff' }} onClick={showPreviewModal}>数据预览</a>
                </Col>
            </Row> : undefined
        );
        return elements;
    }

    useEffect(()=>{
        refreshEditor()
    })

    const data = panelColumn[index];
    const dataSourceOptionTypes = originOption(
        'dataSource',
        formatSourceTypes(dimensionTableTypes) || []
    );
    const originOptionTypes = originOption(
        'originType',
        originOptionType[index] || []
    );
    const tableOptionTypes = originOption(
        'currencyType',
        tableOptionType[index] || []
    );
    const dataBaseOptionTypes = originOption(
        'database',
        dataBaseOptionType || []
    )
    const assetTableOptionTypes = originOption(
        'assetTable',
        assetTableOptionType[index] || []
    )
    const tableColumnOptionTypes = originOption(
        'columnType',
        tableColumnOptionType[index] || []
    );
    const primaryKeyOptionTypes = originOption(
        'primaryType',
        panelColumn[index].columns || []
    );
    const schemaOptionTypes = originOption('schemaType', schemaOptionType[index] || []);
    const customParams = panelColumn[index].customParams || [];
    const showSchema = data.type == DATA_SOURCE_ENUM.ORACLE || data.type == DATA_SOURCE_ENUM.POSTGRESQL || data.type == DATA_SOURCE_ENUM.KINGBASE8 || isSqlServer(data.type);
    const schemaRequired = [DATA_SOURCE_ENUM.POSTGRESQL, DATA_SOURCE_ENUM.KINGBASE8, DATA_SOURCE_ENUM.SQLSERVER, DATA_SOURCE_ENUM.SQLSERVER_2017_LATER].includes(data.type);
    const targetColText = '别名指字段的别名，如select  order_sales as order_amont from  shop_order，order_sales字段的别名即为order_amont';
    const asyncPoolSizeArr = Array.from(new Array(20).keys()).map(item => item + 1);
    const isFlink112 = currentPage?.componentVersion === '1.12'
    return <Row className="title-content">
        <Form
            {...formItemLayout}
            form={form}
        // initialValues={mapPropsToFields()}
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
            {
                data?.createType === TABLE_SOURCE.DATA_ASSET
                    ? <AssetPanel
                        pIndex={index}
                        panelColumn={panelColumn}
                        handleInputChange={handleInputChange}
                        assetTableOptionTypes={assetTableOptionTypes}
                        dataBaseOptionTypes={dataBaseOptionTypes}
                        getDataBaseList={getDataBaseList}
                        embedElm={initCommonElements()}
                    />
                    : <React.Fragment>
                        <FormItem
                        label="存储类型"
                        name='type'
                        rules={[{ required: true, message: '请选择存储类型' }]}
                        >
                                <Select
                                    className="right-select"
                                    onChange={(v: any) => {
                                        handleInputChange('type', index, v);
                                    }}
                                    showSearch
                                    filterOption={(input: any, option: any) =>
                                        option.props.children
                                            .toLowerCase()
                                            .indexOf(input.toLowerCase()) >= 0
                                    }
                                >
                                    {dataSourceOptionTypes}
                                </Select>
                        </FormItem>
                        <FormItem
                        label="数据源"
                        name='sourceId'
                        rules={[{ required: true, message: '请选择数据源' }]}
                        >
                                <Select
                                    className="right-select"
                                    placeholder="请选择数据源"
                                    onChange={(v: any) => {
                                        handleInputChange('sourceId', index, v);
                                    }}
                                    showSearch
                                    filterOption={(input: any, option: any) =>
                                        option.props.children
                                            .toLowerCase()
                                            .indexOf(input.toLowerCase()) >= 0
                                    }
                                >
                                    {originOptionTypes}
                                </Select>
                        </FormItem>
                        { showSchema ? <FormItem
                                    label="Schema"
                                    name='schema'
                                    rules={[{ required: Boolean(schemaRequired), message: '请输入Schema' }]}
                                >
                                        <Select
                                            showSearch
                                            placeholder="请选择Schema"
                                            className="right-select"
                                            allowClear
                                            onChange={(v: any) => { handleInputChange('schema', index, v) }}
                                            filterOption={(input: any, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                                        >
                                            {
                                                schemaOptionTypes
                                            }
                                        </Select>
                                </FormItem> : ''
                        }
                        {(() => {
                            switch (panelColumn[index].type) {
                                case DATA_SOURCE_ENUM.REDIS:
                                case DATA_SOURCE_ENUM.UPRedis: {
                                    return (
                                        <FormItem
                                            label="表"
                                            name='table-input'
                                            rules={[{ required: true, message: '请输入表名' }]}
                                        >
                                                <Input onChange={(v: any) => { handleInputChange('table', index, v.target.value) }} />
                                        </FormItem>
                                    )
                                }
                                case DATA_SOURCE_ENUM.ES6:
                                case DATA_SOURCE_ENUM.ES7: {
                                    return null;
                                }
                                default: {
                                    return (
                                        <FormItem
                                        label="表"
                                        name='table'
                                        rules={[{ required: true, message: '请选择表' }]}
                                        >
                                                <Select
                                                    className="right-select"
                                                    onChange={(v: any) => {
                                                        handleInputChange('table', index, v);
                                                    }}
                                                    onSearch={(value: string) => debounceHandleTableSearch(value, index)}
                                                    filterOption={false}
                                                    showSearch
                                                >
                                                    {tableOptionTypes}
                                                </Select>
                                        </FormItem>
                                    )
                                }
                            }
                        })()}
                        {isES(panelColumn[index].type)
                            ? <FormItem
                                label="索引"
                                name='index'
                                rules={[{ required: true, message: '请输入索引' }]}
                            >
                                    <Input placeholder="请输入索引" onChange={(e: any) => handleInputChange('index', index, e.target.value)} />
                            </FormItem> : null}
                        {initCommonElements()}
                        {isES(panelColumn[index].type) && panelColumn[index].type !== DATA_SOURCE_ENUM.ES7
                            ? <FormItem
                                label="索引类型"
                                name='esType'
                                rules={[{ required: true, message: '请输入索引类型' }]}
                            >
                                    <Input placeholder="请输入索引类型" onChange={(e: any) => handleInputChange('esType', index, e.target.value)} />
                            </FormItem> : null}
                        <FormItem
                        label="映射表"
                        name='tableName'
                        rules={[{ required: true, message: '请输入映射表名' }]}
                        >
                                <Input
                                    placeholder="请输入映射表名"
                                    onChange={(e: any) =>
                                        handleInputChange(
                                            'tableName',
                                            index,
                                            e.target.value
                                        )
                                    }
                                />
                        </FormItem>
                        <Row>
                            <div className="ant-form-item-label ant-col-xs-24 ant-col-sm-6 required-tip">
                                <label className="required-tip">字段</label>
                            </div>
                            {haveTableColumn(panelColumn[index].type)
                                ? <Col span={18} style={{ marginBottom: 20 }}>
                                    <div className="column-container">
                                        <Table
                                            rowKey="column"
                                            dataSource={panelColumn[index].columns}
                                            pagination={false}
                                            size="small"
                                        >
                                            <Table.Column
                                                title="字段"
                                                dataIndex="column"
                                                key="字段"
                                                width="35%"
                                                render={(text: any, record: any, subIndex: any) => {
                                                    return (
                                                        <Select
                                                            className="sub-right-select column-table__select"
                                                            value={text}
                                                            onChange={(v: any) => {
                                                                handleInputChange('subColumn', index, subIndex, v);
                                                            }}
                                                            style={{ maxWidth: 74 }}
                                                            showSearch
                                                        >
                                                            {tableColumnOptionTypes}
                                                        </Select>
                                                    );
                                                }}
                                            />
                                            <Table.Column
                                                title="类型"
                                                dataIndex="type"
                                                key="类型"
                                                width="25%"
                                                render={(text: any, record: any, subIndex: any) => (
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
                                            <Table.Column
                                                title={
                                                    <div>
                                                        <Tooltip placement="top" title={targetColText} arrowPointAtCenter>
                                                            <span>别名 &nbsp;
                                                                <QuestionCircleOutlined />
                                                            </span>
                                                        </Tooltip>
                                                    </div>
                                                }
                                                dataIndex="targetCol"
                                                key="别名"
                                                width="30%"
                                                render={(text: any, record: any, subIndex: any) => {
                                                    return <Input
                                                        className="column-table__input"
                                                        value={text}
                                                        onChange={(e: any) =>
                                                            handleInputChange('targetCol', index, subIndex, e.target.value)
                                                        }
                                                    />;
                                                }}
                                            />
                                            <Table.Column
                                                key="delete"
                                                render={(text: any, record: any, subIndex: any) => {
                                                    return <CloseOutlined
                                                        style={{ fontSize: 12, color: '#999' }}
                                                        onClick={() => { handleInputChange('deleteColumn', index, subIndex) }}/>
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
                                                    <Popconfirm
                                                        title="确认清空所有字段？"
                                                        onConfirm={() => { handleInputChange('deleteAllColumn', index) }} okText="确认"
                                                        cancelText="取消"
                                                    >
                                                        <a>清空</a>
                                                    </Popconfirm>
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                </Col>
                                : (
                                    <Col span={18} style={{ marginBottom: 20, height: 200 }}>
                                        {isShow && (
                                            <Editor
                                                style={{
                                                    minHeight: 202,
                                                    border: '1px solid #ddd',
                                                    height: '100%'
                                                }}
                                                key="params-editor"
                                                sync={sync}
                                                placeholder={getPlaceholder(panelColumn[index].type)}
                                                value={panelColumn[index].columnsText}
                                                onChange={debounceEditorChange}
                                                editorRef={(ref: any) => {
                                                    _editorRef = ref;
                                                }}
                                            />
                                        )}
                                    </Col>
                                )
                            }
                        </Row>
                    </React.Fragment>
            }
            {(() => {
                switch (panelColumn[index].type) {
                    case DATA_SOURCE_ENUM.KUDU:
                    case DATA_SOURCE_ENUM.POSTGRESQL:
                    case DATA_SOURCE_ENUM.CLICKHOUSE:
                    case DATA_SOURCE_ENUM.ORACLE:
                    case DATA_SOURCE_ENUM.POLAR_DB_For_MySQL:
                    case DATA_SOURCE_ENUM.MYSQL:
                    case DATA_SOURCE_ENUM.UPDRDB:
                    case DATA_SOURCE_ENUM.TIDB:
                    case DATA_SOURCE_ENUM.IMPALA:
                    case DATA_SOURCE_ENUM.INCEPTOR:
                    case DATA_SOURCE_ENUM.KINGBASE8:
                    case DATA_SOURCE_ENUM.SQLSERVER:
                    case DATA_SOURCE_ENUM.SQLSERVER_2017_LATER: {
                        return (
                            <FormItem
                            label="主键"
                            name='primaryKey'
                            >
                                    <Select
                                        className="right-select"
                                        onChange={(v: any) => {
                                            handleInputChange('primaryKey', index, v);
                                        }}
                                        mode="multiple"
                                        showSearch
                                        showArrow
                                        filterOption={(input: any, option: any) =>
                                            option.props.children
                                                .toLowerCase()
                                                .indexOf(input.toLowerCase()) >= 0
                                        }
                                    >
                                        {primaryKeyOptionTypes}
                                    </Select>
                            </FormItem>
                        )
                    }
                    case DATA_SOURCE_ENUM.ES6:
                    case DATA_SOURCE_ENUM.ES7: {
                        return (
                            <FormItem name='primaryKey-input' label="主键">
                                    <Input
                                        placeholder="请输入主键"
                                        onChange={(e: any) =>
                                            handleInputChange(
                                                'primaryKey',
                                                index,
                                                e.target.value
                                            )
                                        }
                                    />
                            </FormItem>
                        )
                    }
                    case DATA_SOURCE_ENUM.MONGODB:
                    case DATA_SOURCE_ENUM.REDIS:
                    case DATA_SOURCE_ENUM.UPRedis: {
                        return (
                            <FormItem
                            label="主键"
                            name='primaryKey-input'
                            rules={[{ required: true, message: '请选择主键' }]}
                            >
                                    <Input
                                        placeholder={panelColumn[index].type === DATA_SOURCE_ENUM.MONGODB ? '请输入主键' : '维表主键，多个字段用英文逗号隔开'}
                                        onChange={(e: any) =>
                                            handleInputChange(
                                                'primaryKey',
                                                index,
                                                e.target.value
                                            )
                                        }
                                    />
                            </FormItem>
                        )
                    }
                    case DATA_SOURCE_ENUM.HBASE:
                    case DATA_SOURCE_ENUM.TBDS_HBASE:
                    case DATA_SOURCE_ENUM.HBASE_HUAWEI: {
                        return (
                            <FormItem
                                label='主键'
                                tooltip={isFlink112 && <React.Fragment>
                                    Hbase 表主键字段支持类型可参考&nbsp;
                                    <a
                                        href={HELP_DOC_URL.HBASE}
                                        target="_blank"
                                        rel="noopener noreferrer"
                                    >帮助文档</a>
                                </React.Fragment>}
                            >
                                <div style={{ display: 'flex' }}>
                                    <FormItem
                                    style={{ flex: 1 }}
                                    name='hbasePrimaryKey'
                                    rules={[
                                        { required: true, message: '请输入主键' },
                                        isFlink112 ? { pattern: /^\w{1,64}$/, message: '只能由字母，数字和下划线组成，且不超过64个字符' } : {}
                                    ]}
                                    >
                                            <Input
                                                placeholder="请输入主键"
                                                onChange={e => handleInputChange('hbasePrimaryKey', index, e.target.value)}
                                            />
                                    </FormItem>
                                    {isFlink112 && <>
                                        <span>&nbsp; 类型：</span>
                                        <FormItem
                                        style={{ flex: 1 }}
                                        name='hbasePrimaryKeyType'
                                        rules={[{ required: true, message: '请输入类型' }]}
                                        >
                                                <Input
                                                    placeholder="请输入类型"
                                                    onChange={e => handleInputChange('hbasePrimaryKeyType', index, e.target.value)}
                                                />
\                                        </FormItem>
                                    </>}
                                </div>
                            </FormItem>
                        )
                    }
                    default: {
                        return null;
                    }
                }
            })()}
            <FormItem name='parallelism' label="并行度">
                    <InputNumber
                        className="number-input"
                        min={1}
                        onChange={(value: any) =>
                            handleInputChange('parallelism', index, value)
                        }
                    />
            </FormItem>
            <FormItem
            label="缓存策略"
            name='cache'
            rules={[{ required: true, message: '请选择缓存策略' }]}
            >
                    <Select
                        placeholder="请选择"
                        className="right-select"
                        onChange={(v: any) => {
                            handleInputChange('cache', index, v);
                        }}
                        showSearch
                        filterOption={(input: any, option: any) =>
                            option.props.children
                                .toLowerCase()
                                .indexOf(input.toLowerCase()) >= 0
                        }
                    >
                        <Option key="None" value="None" disabled={isCacheOnlyAll(panelColumn[index].type)}>
                            None
                        </Option>
                        <Option key="LRU" value="LRU" disabled={isCacheExceptLRU(panelColumn[index].type)}>
                            LRU
                        </Option>
                        <Option key="ALL" value="ALL">
                            ALL
                        </Option>
                    </Select>
            </FormItem>

            {panelColumn[index].cache === 'LRU' && <>
                <FormItem
                label="缓存大小(行)"
                name='cacheSize'
                rules={[{ required: true, message: '请输入缓存大小' }]}
                >
                        <InputNumber
                            className="number-input"
                            min={0}
                            onChange={(value: any) =>
                                handleInputChange('cacheSize', index, `${value || ''}`)
                            }
                        />
                </FormItem>
                <FormItem
                label="缓存超时时间"
                name='cacheTTLMs'
                rules={[{ required: true, message: '请输入缓存超时时间' }]}
                >
                        <InputNumber
                            className="number-input"
                            min={0}
                            onChange={(value: any) =>
                                handleInputChange(
                                    'cacheTTLMs',
                                    index,
                                    `${value || ''}`
                                )
                            }
                            addonAfter='ms'
                        />
                </FormItem>
            </>}

            {panelColumn[index].cache === 'ALL' &&
                <FormItem
                label="缓存超时时间"
                name='cacheTTLMs'
                rules={[{ required: true, message: '请输入缓存超时时间' }]}
                >
                        <InputNumber
                            min={0}
                            className="number-input"
                            onChange={(value: any) =>
                                handleInputChange('cacheTTLMs', index, `${value || ''}`)
                            }
                            addonAfter='ms'
                        />
                    <span style={{ marginLeft: 8, color: '#999' }}>ms</span>
                </FormItem>}

            <FormItem
                label='允许错误数据'
                tooltip={asyncTimeoutNumDoc}
                name='errorLimit'
            >
                    <InputNumber
                        className="number-input"
                        placeholder="默认为无限制"
                        min={0}
                        onChange={(value: any) =>
                            handleInputChange('errorLimit', index, value)
                        }
                    />
            </FormItem>
            {
                panelColumn[index].type == DATA_SOURCE_ENUM.KUDU &&
                <FormItem
                    label='查询容错'
                    tooltip={queryFault}
                    name='isFaultTolerant'
                >
                        <Switch
                            defaultChecked={panelColumn[index].isFaultTolerant}
                            onChange={(checked: any) =>
                                handleInputChange('isFaultTolerant', index, checked)
                            }
                        />
                </FormItem>
            }
            { haveAsyncPoolSize(panelColumn[index].type) ? (
                    <FormItem name='asyncPoolSize' label="异步线程池">
                            <Select
                                style={{ width: '100%' }}
                                onChange={(v: any) => { handleInputChange('asyncPoolSize', index, v) }}
                            >
                                {asyncPoolSizeArr.map(opt => {
                                    return <Option key={opt} value={opt}>{opt}</Option>
                                })}
                            </Select>
                    </FormItem>
                ) : null
            }
            {haveCustomParams(panelColumn[index].type) && <CustomParams
                formItemLayout={formItemLayout}
                customParams={customParams}
                onChange={(type: any, id: any, value: any) => { handleInputChange('customParams', index, value, { id, type }) }}
            />}
            <DataPreviewModal
                visible={visible}
                type={panelColumn[index]?.type}
                isAssetCreate={panelColumn[index]?.createType === TABLE_SOURCE.DATA_ASSET}
                onCancel={() => { setVisible(false) }}
                params={params}
            />
        </Form>
    </Row>
}