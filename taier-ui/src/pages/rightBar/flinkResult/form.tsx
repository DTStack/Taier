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

import React, { useContext, useMemo, useRef, useState } from 'react';
import { CloseOutlined, DownOutlined,UpOutlined } from '@ant-design/icons';
import type { FormInstance } from 'antd';
import { Button, Checkbox, Form, Input, InputNumber, message, Popconfirm, Radio, Select, Table, Tooltip } from 'antd';
import Column from 'antd/lib/table/Column';
import { debounce, isUndefined } from 'lodash';

import Editor from '@/components/editor';
import {
    DATA_SOURCE_ENUM,
    DATA_SOURCE_TEXT,
    DATA_SOURCE_VERSION,
    DEFAULT_MAPPING_TEXT,
    defaultColsText,
    FLINK_VERSIONS,
    formItemLayout,
    hbaseColsText,
    hbaseColsText112,
    HELP_DOC_URL,
    KAFKA_DATA_LIST,
    KAFKA_DATA_TYPE,
} from '@/constant';
import type { IDataColumnsProps, IDataSourceUsedInSyncProps, IFlinkSinkProps } from '@/interface';
import DataPreviewModal from '@/pages/editor/streamCollection/source/dataPreviewModal';
import { taskRenderService } from '@/services';
import { FormContext } from '@/services/rightBarService';
import { getColumnsByColumnsText } from '@/utils';
import {
    isAvro,
    isES,
    isHaveCollection,
    isHaveDataPreview,
    isHaveParallelism,
    isHavePrimaryKey,
    isHaveTableColumn,
    isHaveTableList,
    isHaveTopic,
    isHaveUpdateMode,
    isHaveUpdateStrategy,
    isHaveUpsert,
    isHbase,
    isKafka,
    isRDB,
    isRedis,
    isShowBucket,
    isShowSchema,
    isSqlServer,
} from '@/utils/is';
import { CustomParams } from '../customParams';
import { NAME_FIELD } from '.';

const FormItem = Form.Item;
const { Option } = Select;

interface IResultProps {
    /**
     * Form 表单 name 前缀
     */
    index: number;
    /**
     * 数据源下拉菜单
     */
    dataSourceOptionList: IDataSourceUsedInSyncProps[];
    tableOptionType: Record<string, string[]>;
    /**
     * 表下拉菜单
     */
    tableColumnOptionType: IDataColumnsProps[];
    /**
     * topic 下拉菜单
     */
    topicOptionType: string[];
    /**
     * 当前 flink 版本
     */
    componentVersion?: Valueof<typeof FLINK_VERSIONS>;
    getTableType: (params: { sourceId: number; type: DATA_SOURCE_ENUM; schema?: string }, searchKey?: string) => void;
    /**
     * columns 改变触发的回调
     */
    onColumnsChange?: (changedValues: any, values: any) => void;
}

enum COLUMNS_OPERATORS {
    /**
     * 导入一条字段
     */
    ADD_ONE_LINE,
    /**
     * 导入全部字段
     */
    ADD_ALL_LINES,
    /**
     * 删除全部字段
     */
    DELETE_ALL_LINES,
    /**
     * 删除一条字段
     */
    DELETE_ONE_LINE,
    /**
     * 编辑字段
     */
    CHANGE_ONE_LINE,
}

/**
 * 是否需要禁用更新模式
 */
const isDisabledUpdateMode = (type: DATA_SOURCE_ENUM, isHiveTable?: boolean, version?: string): boolean => {
    if (type === DATA_SOURCE_ENUM.IMPALA) {
        if (isUndefined(isHiveTable) || isHiveTable === true) {
            return true;
        }
        if (isHiveTable === false) {
            return false;
        }

        return false;
    }

    return !isHaveUpsert(type, version);
};

/**
 * 根据 type 渲染不同模式的 Option 组件
 */
const originOption = (type: string, arrData: any[]) => {
    switch (type) {
        case 'currencyType':
            return arrData.map((v) => {
                return (
                    <Option key={v} value={`${v}`}>
                        <Tooltip placement="topLeft" title={v}>
                            <span className="panel-tooltip">{v}</span>
                        </Tooltip>
                    </Option>
                );
            });
        case 'columnType':
            return arrData.map((v) => {
                return (
                    <Option key={v.key} value={`${v.key}`}>
                        <Tooltip placement="topLeft" title={v.key}>
                            <span className="panel-tooltip">{v.key}</span>
                        </Tooltip>
                    </Option>
                );
            });
        case 'primaryType':
            return arrData.map((v) => {
                return (
                    <Option key={v.column} value={`${v.column}`}>
                        {v.column}
                    </Option>
                );
            });
        case 'kafkaPrimaryType':
            return arrData.map((v) => {
                return (
                    <Option key={v.field} value={`${v.field}`}>
                        {v.field}
                    </Option>
                );
            });
        default:
            return null;
    }
};

export default function ResultForm({
    index,
    dataSourceOptionList = [],
    tableOptionType = {},
    tableColumnOptionType = [],
    topicOptionType = [],
    componentVersion = FLINK_VERSIONS.FLINK_1_12,
    getTableType,
    onColumnsChange,
}: IResultProps) {
    const { form } = useContext(FormContext) as {
        form?: FormInstance<{ [NAME_FIELD]: Partial<IFlinkSinkProps>[] }>;
    };
    const [visible, setVisible] = useState(false);
    const [params, setParams] = useState<Record<string, any>>({});
    const [showAdvancedParams, setShowAdvancedParams] = useState(false);
    const searchKey = useRef('');

    // 表远程搜索
    const debounceHandleTableSearch = debounce((value: string) => {
        const currentData = form?.getFieldsValue()[NAME_FIELD][index];
        if (currentData?.sourceId) {
            searchKey.current = value;
            getTableType(
                {
                    sourceId: currentData.sourceId,
                    type: currentData.type!,
                    schema: currentData.schema,
                },
                value
            );
        }
    }, 300);

    const getPlaceholder = (sourceType: DATA_SOURCE_ENUM) => {
        if (isHbase(sourceType)) {
            return componentVersion === FLINK_VERSIONS.FLINK_1_12 ? hbaseColsText112 : hbaseColsText;
        }
        return defaultColsText;
    };

    const showPreviewModal = () => {
        if (!form?.getFieldValue(NAME_FIELD)[index]) return;
        const { sourceId, index: tableIndex, table, type, schema } = form.getFieldValue(NAME_FIELD)[index];
        let nextParams: Record<string, any> = {};

        switch (type) {
            case DATA_SOURCE_ENUM.ES7: {
                if (!sourceId || !tableIndex) {
                    message.error('数据预览需要选择数据源和索引！');
                    return;
                }
                nextParams = { sourceId, tableName: tableIndex };
                break;
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
                    message.error('数据预览需要选择数据源和表！');
                    return;
                }
                nextParams = { sourceId, tableName: table };
                break;
            }
            case DATA_SOURCE_ENUM.ORACLE: {
                if (!sourceId || !table || !schema) {
                    message.error('数据预览需要选择数据源、表和schema！');
                    return;
                }
                nextParams = { sourceId, tableName: table, schema };
                break;
            }
            case DATA_SOURCE_ENUM.SQLSERVER:
            case DATA_SOURCE_ENUM.SQLSERVER_2017_LATER: {
                if (!sourceId || !table) {
                    message.error('数据预览需要选择数据源和表！');
                    return;
                }
                nextParams = { sourceId, tableName: table, schema };
                break;
            }
            default:
                break;
        }

        setVisible(true);
        setParams(nextParams);
    };

    const renderTableOptions = () =>
        tableOptionType[searchKey.current]?.map((v) => (
            <Option key={v} value={`${v}`}>
                {v}
            </Option>
        )) || [];

    const handleColumnsChanged = (
        ops: COLUMNS_OPERATORS,
        i?: number,
        value?: Partial<{
            type: string;
            column: string | number;
        }>
    ) => {
        switch (ops) {
            case COLUMNS_OPERATORS.ADD_ALL_LINES: {
                const nextValue = form!.getFieldsValue();
                nextValue[NAME_FIELD][index].columns = tableColumnOptionType.map((column) => ({
                    column: column.key,
                    type: column.type,
                }));
                form!.setFieldsValue({ ...nextValue });

                // 由于 setFieldsValue 不会触发表单的 onValuesChange 所以需要额外触发将 columns 保存到 tab 中
                const changedValue: any[] = [];
                changedValue[index] = {
                    columns: nextValue[NAME_FIELD][index].columns,
                };
                onColumnsChange?.({ [NAME_FIELD]: changedValue }, form?.getFieldsValue());
                break;
            }

            case COLUMNS_OPERATORS.DELETE_ALL_LINES: {
                const nextValue = form!.getFieldsValue();
                nextValue[NAME_FIELD][index].columns = [];
                nextValue[NAME_FIELD][index].primaryKey = [];
                form!.setFieldsValue({ ...nextValue });

                // 由于 setFieldsValue 不会触发表单的 onValuesChange 所以需要额外触发将 columns 保存到 tab 中
                const changedValue: any[] = [];
                changedValue[index] = {
                    columns: [],
                    primaryKey: [],
                };
                onColumnsChange?.({ [NAME_FIELD]: changedValue }, form?.getFieldsValue());
                break;
            }

            case COLUMNS_OPERATORS.ADD_ONE_LINE: {
                const nextValue = form!.getFieldsValue();
                nextValue[NAME_FIELD][index].columns = nextValue[NAME_FIELD][index].columns || [];
                nextValue[NAME_FIELD][index].columns!.push({});
                form!.setFieldsValue({ ...nextValue });

                // 由于 setFieldsValue 不会触发表单的 onValuesChange 所以需要额外触发将 columns 保存到 tab 中
                const changedValue: any[] = [];
                changedValue[index] = {
                    columns: nextValue[NAME_FIELD][index].columns,
                };
                onColumnsChange?.({ [NAME_FIELD]: changedValue }, form?.getFieldsValue());
                break;
            }

            case COLUMNS_OPERATORS.DELETE_ONE_LINE: {
                const nextValue = form!.getFieldsValue();
                const deleteCol = nextValue[NAME_FIELD][index].columns?.splice(i!, 1) || [];
                // 删除一条字段的副作用是若该行是 primaryKey 则删除
                if (deleteCol.length) {
                    const { primaryKey } = nextValue[NAME_FIELD][index];
                    if (
                        Array.isArray(primaryKey) &&
                        primaryKey.findIndex((key) => key === deleteCol[0].column) !== -1
                    ) {
                        const idx = primaryKey.findIndex((key) => key === deleteCol[0].column);
                        primaryKey.splice(idx, 1);
                    }
                }
                form!.setFieldsValue({ ...nextValue });

                // 由于 setFieldsValue 不会触发表单的 onValuesChange 所以需要额外触发将 columns 保存到 tab 中
                const changedValue: any[] = [];
                changedValue[index] = {
                    columns: nextValue[NAME_FIELD][index].columns,
                    primaryKey: nextValue[NAME_FIELD][index].primaryKey,
                };
                onColumnsChange?.({ [NAME_FIELD]: changedValue }, form?.getFieldsValue());

                break;
            }

            case COLUMNS_OPERATORS.CHANGE_ONE_LINE: {
                const nextValue = form!.getFieldsValue();
                nextValue[NAME_FIELD][index].columns![i!] = value!;
                form!.setFieldsValue({ ...nextValue });

                // 由于 setFieldsValue 不会触发表单的 onValuesChange 所以需要额外触发将 columns 保存到 tab 中
                const changedValue: any[] = [];
                changedValue[index] = {
                    columns: nextValue[NAME_FIELD][index].columns,
                };
                onColumnsChange?.({ [NAME_FIELD]: changedValue }, form?.getFieldsValue());
                break;
            }

            default:
                break;
        }
    };

    const topicOptionTypes = originOption('currencyType', topicOptionType);
    const tableColumnOptionTypes = originOption('columnType', tableColumnOptionType);

    // isShowPartition 为 false 为 kudu 表
    const disableUpdateMode = false;

    // 当前数据
    const data = form?.getFieldsValue()[NAME_FIELD][index];

    const schemaRequired = data?.type
        ? [
              DATA_SOURCE_ENUM.POSTGRESQL,
              DATA_SOURCE_ENUM.KINGBASE8,
              DATA_SOURCE_ENUM.SQLSERVER,
              DATA_SOURCE_ENUM.SQLSERVER_2017_LATER,
          ].includes(data?.type)
        : false;

    const isFlink112 = useMemo(() => componentVersion === FLINK_VERSIONS.FLINK_1_12, [componentVersion]);

    const primaryKeyOptionTypes = useMemo(
        () =>
            isFlink112 && isKafka(data?.type)
                ? originOption('kafkaPrimaryType', getColumnsByColumnsText(data?.columnsText))
                : originOption('primaryType', data?.columns || []),
        [isFlink112, data]
    );

    return (
        <>
            <FormItem label="存储类型" name={[index, 'type']} rules={[{ required: true, message: '请选择存储类型' }]}>
                <Select
                    className="right-select"
                    showSearch
                    style={{ width: '100%' }}
                    filterOption={(input, option) =>
                        option?.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                    }
                >
                    {taskRenderService.getState().supportSourceList.flinkSqlSinks.map((item) => (
                        <Option key={item} value={item}>
                            {DATA_SOURCE_TEXT[item]}
                        </Option>
                    ))}
                </Select>
            </FormItem>
            <FormItem label="数据源" name={[index, 'sourceId']} rules={[{ required: true, message: '请选择数据源' }]}>
                <Select
                    showSearch
                    placeholder="请选择数据源"
                    className="right-select"
                    filterOption={(input, option) =>
                        option?.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                    }
                >
                    {dataSourceOptionList.map((v) => (
                        <Option key={v.dataInfoId} value={v.dataInfoId}>
                            {v.dataName}
                            {DATA_SOURCE_VERSION[v.dataTypeCode] && ` (${DATA_SOURCE_VERSION[v.dataTypeCode]})`}
                        </Option>
                    ))}
                </Select>
            </FormItem>
            <FormItem noStyle dependencies={[[index, 'type']]}>
                {({ getFieldValue }) => (
                    <>
                        {isHaveCollection(getFieldValue(NAME_FIELD)[index].type) && (
                            <FormItem
                                label="Collection"
                                name={[index, 'collection']}
                                rules={[{ required: true, message: '请选择Collection' }]}
                            >
                                <Select
                                    showSearch
                                    allowClear
                                    placeholder="请选择Collection"
                                    className="right-select"
                                    filterOption={(input, option) =>
                                        option?.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                                    }
                                >
                                    {renderTableOptions()}
                                </Select>
                            </FormItem>
                        )}
                        {isShowBucket(getFieldValue(NAME_FIELD)[index].type) && (
                            <>
                                <FormItem
                                    label="Bucket"
                                    name={[index, 'bucket']}
                                    rules={[
                                        {
                                            required: Boolean(schemaRequired),
                                            message: '请选择Bucket',
                                        },
                                    ]}
                                >
                                    <Select showSearch allowClear placeholder="请选择Bucket" className="right-select">
                                        {renderTableOptions()}
                                    </Select>
                                </FormItem>
                                <FormItem
                                    label="ObjectName"
                                    name={[index, 'objectName']}
                                    rules={[{ required: true, message: '请输入ObjectName' }]}
                                    tooltip="默认以标准存储，txt格式保存至S3 Bucket内"
                                >
                                    <Input placeholder="请输入ObjectName" style={{ width: '90%' }} />
                                </FormItem>
                            </>
                        )}
                        {isShowSchema(getFieldValue(NAME_FIELD)[index].type) && (
                            <FormItem
                                label="Schema"
                                name={[index, 'schema']}
                                rules={[
                                    {
                                        required: Boolean(schemaRequired),
                                        message: '请输入Schema',
                                    },
                                ]}
                            >
                                <Select
                                    showSearch
                                    allowClear
                                    placeholder="请选择Schema"
                                    className="right-select"
                                    options={[]}
                                />
                            </FormItem>
                        )}
                        {isHaveTopic(getFieldValue(NAME_FIELD)[index].type) && (
                            <FormItem
                                label="Topic"
                                name={[index, 'topic']}
                                rules={[{ required: true, message: '请选择Topic' }]}
                            >
                                <Select placeholder="请选择Topic" className="right-select" showSearch>
                                    {topicOptionTypes}
                                </Select>
                            </FormItem>
                        )}
                        {isHaveTableList(getFieldValue(NAME_FIELD)[index].type) &&
                            ![DATA_SOURCE_ENUM.S3, DATA_SOURCE_ENUM.CSP_S3].includes(
                                getFieldValue(NAME_FIELD)[index].type
                            ) && (
                                <FormItem
                                    label="表"
                                    name={[index, 'table']}
                                    rules={[{ required: true, message: '请选择表' }]}
                                >
                                    <Select
                                        showSearch
                                        placeholder="请选择表"
                                        className="right-select"
                                        onSearch={(value: string) => debounceHandleTableSearch(value)}
                                        filterOption={false}
                                    >
                                        {renderTableOptions()}
                                    </Select>
                                </FormItem>
                            )}
                        {isRedis(getFieldValue(NAME_FIELD)[index].type) && (
                            <FormItem
                                label="表"
                                name={[index, 'table']}
                                rules={[{ required: true, message: '请输入表名' }]}
                            >
                                <Input placeholder="请输入表名" />
                            </FormItem>
                        )}
                        {isES(getFieldValue(NAME_FIELD)[index].type) && (
                            <FormItem
                                label="索引"
                                tooltip={
                                    <span>
                                        {'支持输入{column_name}作为动态索引，动态索引需包含在映射表字段中，更多请参考'}
                                        <a rel="noopener noreferrer" target="_blank" href={HELP_DOC_URL.INDEX}>
                                            帮助文档
                                        </a>
                                    </span>
                                }
                                name={[index, 'index']}
                                rules={[{ required: true, message: '请输入索引' }]}
                            >
                                <Input placeholder="请输入索引" />
                            </FormItem>
                        )}
                        {isHaveDataPreview(getFieldValue(NAME_FIELD)[index].type) && (
                            <FormItem
                                wrapperCol={{
                                    offset: formItemLayout.labelCol.sm.span,
                                    span: formItemLayout.wrapperCol.sm.span,
                                }}
                            >
                                <Button block type="link" onClick={showPreviewModal}>
                                    数据预览
                                </Button>
                            </FormItem>
                        )}
                        {isRedis(getFieldValue(NAME_FIELD)[index].type) && (
                            <FormItem
                                label="主键"
                                name={[index, 'primaryKey']}
                                rules={[{ required: true, message: '请输入主键' }]}
                            >
                                <Input placeholder="结果表主键，多个字段用英文逗号隔开" />
                            </FormItem>
                        )}
                        {isES(getFieldValue(NAME_FIELD)[index].type) && (
                            <FormItem
                                label="id"
                                tooltip="id生成规则：填写字段的索引位置（从0开始）"
                                name={[index, 'esId']}
                            >
                                <Input placeholder="请输入id" />
                            </FormItem>
                        )}
                        {[DATA_SOURCE_ENUM.ES, DATA_SOURCE_ENUM.ES6].includes(
                            getFieldValue(NAME_FIELD)[index].type
                        ) && (
                            <FormItem
                                label="索引类型"
                                name={[index, 'esType']}
                                rules={[{ required: true, message: '请输入索引类型' }]}
                            >
                                <Input placeholder="请输入索引类型" />
                            </FormItem>
                        )}
                        {getFieldValue(NAME_FIELD)[index].type === DATA_SOURCE_ENUM.HBASE && (
                            <FormItem
                                label="rowKey"
                                tooltip={
                                    isFlink112 ? (
                                        <>
                                            Hbase 表 rowkey 字段支持类型可参考&nbsp;
                                            <a href={HELP_DOC_URL.HBASE} target="_blank" rel="noopener noreferrer">
                                                帮助文档
                                            </a>
                                        </>
                                    ) : (
                                        "支持拼接规则：md5(fieldA+fieldB) + fieldC + '常量字符'"
                                    )
                                }
                            >
                                <div style={{ display: 'flex' }}>
                                    <FormItem
                                        style={{ flex: 1 }}
                                        name={[index, 'rowKey']}
                                        rules={[
                                            { required: true, message: '请输入rowKey' },
                                            isFlink112
                                                ? {
                                                      pattern: /^\w{1,64}$/,
                                                      message: '只能由字母，数字和下划线组成，且不超过64个字符',
                                                  }
                                                : {},
                                        ]}
                                    >
                                        <Input
                                            placeholder={
                                                isFlink112 ? '请输入 rowkey' : 'rowKey 格式：填写字段1+填写字段2'
                                            }
                                        />
                                    </FormItem>
                                    {isFlink112 && (
                                        <>
                                            <span>&nbsp; 类型：</span>
                                            <FormItem
                                                style={{ flex: 1 }}
                                                name={[index, 'rowKeyType']}
                                                rules={[
                                                    {
                                                        required: true,
                                                        message: '请输入rowKey类型',
                                                    },
                                                ]}
                                            >
                                                <Input placeholder="请输入类型" />
                                            </FormItem>
                                        </>
                                    )}
                                </div>
                            </FormItem>
                        )}
                        {[DATA_SOURCE_ENUM.TBDS_HBASE, DATA_SOURCE_ENUM.HBASE_HUAWEI].includes(
                            getFieldValue(NAME_FIELD)[index].type
                        ) && (
                            <FormItem
                                label="rowKey"
                                tooltip="支持拼接规则：md5(fieldA+fieldB) + fieldC + '常量字符'"
                                name={[index, 'rowKey']}
                                rules={[{ required: true, message: '请输入rowKey' }]}
                            >
                                <Input placeholder="rowKey 格式：填写字段1+填写字段2 " />
                            </FormItem>
                        )}
                    </>
                )}
            </FormItem>
            <FormItem
                label="映射表"
                name={[index, 'tableName']}
                rules={[{ required: true, message: '请输入映射表名' }]}
            >
                <Input placeholder="请输入映射表名" />
            </FormItem>
            {/* 隐藏 columns 字段，通过 table 修改 */}
            <FormItem hidden name="columns" />
            <FormItem
                label="字段"
                required
                dependencies={[
                    [index, 'type'],
                    [index, 'columns'],
                ]}
            >
                {({ getFieldValue }) =>
                    isHaveTableColumn(getFieldValue(NAME_FIELD)[index].type) ? (
                        <div className="column-container">
                            <Table<IFlinkSinkProps['columns'][number]>
                                rowKey="column"
                                dataSource={getFieldValue(NAME_FIELD)[index].columns || []}
                                pagination={false}
                                size="small"
                            >
                                <Column<IFlinkSinkProps['columns'][number]>
                                    title="字段"
                                    dataIndex="column"
                                    key="字段"
                                    width="45%"
                                    render={(text, record, i) => {
                                        return (
                                            <Select
                                                value={text}
                                                showSearch
                                                className="sub-right-select column-table__select"
                                                onChange={(val) =>
                                                    handleColumnsChanged(COLUMNS_OPERATORS.CHANGE_ONE_LINE, i, {
                                                        column: val,
                                                        // assign type automatically
                                                        type: tableColumnOptionType.find(
                                                            (c) => c.key.toString() === val
                                                        )?.type,
                                                    })
                                                }
                                            >
                                                {tableColumnOptionTypes}
                                            </Select>
                                        );
                                    }}
                                />
                                <Column<IFlinkSinkProps['columns'][number]>
                                    title="类型"
                                    dataIndex="type"
                                    key="类型"
                                    width="45%"
                                    render={(text: string, record, i) => (
                                        <span
                                            className={
                                                text?.toLowerCase() === 'Not Support'.toLowerCase() ? 'has-error' : ''
                                            }
                                        >
                                            <Tooltip
                                                title={text}
                                                trigger={'hover'}
                                                placement="topLeft"
                                                overlayClassName="numeric-input"
                                            >
                                                <Input
                                                    value={text}
                                                    className="column-table__input"
                                                    onChange={(e) => {
                                                        handleColumnsChanged(COLUMNS_OPERATORS.CHANGE_ONE_LINE, i, {
                                                            ...record,
                                                            type: e.target.value,
                                                        });
                                                    }}
                                                />
                                            </Tooltip>
                                        </span>
                                    )}
                                />
                                <Column
                                    key="delete"
                                    render={(_, __, i) => {
                                        return (
                                            <CloseOutlined
                                                style={{
                                                    fontSize: 12,
                                                    color: 'var(--editor-foreground)',
                                                }}
                                                onClick={() =>
                                                    handleColumnsChanged(COLUMNS_OPERATORS.DELETE_ONE_LINE, i)
                                                }
                                            />
                                        );
                                    }}
                                />
                            </Table>
                            <div className="column-btn">
                                <span>
                                    <a onClick={() => handleColumnsChanged(COLUMNS_OPERATORS.ADD_ONE_LINE)}>添加输入</a>
                                </span>
                                <span>
                                    <a
                                        onClick={() => handleColumnsChanged(COLUMNS_OPERATORS.ADD_ALL_LINES)}
                                        style={{ marginRight: 12 }}
                                    >
                                        导入全部字段
                                    </a>
                                    {getFieldValue(NAME_FIELD)[index]?.columns?.length ? (
                                        <Popconfirm
                                            title="确认清空所有字段？"
                                            onConfirm={() => handleColumnsChanged(COLUMNS_OPERATORS.DELETE_ALL_LINES)}
                                            okText="确认"
                                            cancelText="取消"
                                        >
                                            <a>清空</a>
                                        </Popconfirm>
                                    ) : (
                                        <a style={{ color: 'var(--editor-foreground)' }}>清空</a>
                                    )}
                                </span>
                            </div>
                        </div>
                    ) : (
                        <FormItem name="columnsText" noStyle>
                            <Editor
                                style={{
                                    minHeight: 202,
                                }}
                                sync
                                options={{
                                    minimap: {
                                        enabled: false,
                                    },
                                }}
                                placeholder={getPlaceholder(getFieldValue(NAME_FIELD)[index].type!)}
                            />
                        </FormItem>
                    )
                }
            </FormItem>
            <FormItem noStyle dependencies={[[index, 'type']]}>
                {({ getFieldValue }) => (
                    <>
                        {isKafka(getFieldValue(NAME_FIELD)[index].type) && (
                            <React.Fragment>
                                <FormItem
                                    label="输出类型"
                                    name={[index, 'sinkDataType']}
                                    rules={[{ required: true, message: '请选择输出类型' }]}
                                >
                                    <Select style={{ width: '100%' }}>
                                        {getFieldValue(NAME_FIELD)[index].type === DATA_SOURCE_ENUM.KAFKA_CONFLUENT ? (
                                            <Option
                                                value={KAFKA_DATA_TYPE.TYPE_AVRO_CONFLUENT}
                                                key={KAFKA_DATA_TYPE.TYPE_AVRO_CONFLUENT}
                                            >
                                                {KAFKA_DATA_TYPE.TYPE_AVRO_CONFLUENT}
                                            </Option>
                                        ) : (
                                            KAFKA_DATA_LIST.map(({ text, value }) => (
                                                <Option value={value} key={text + value}>
                                                    {text}
                                                </Option>
                                            ))
                                        )}
                                    </Select>
                                </FormItem>
                                <FormItem noStyle dependencies={[[index, 'sinkDataType']]}>
                                    {({ getFieldValue: getField }) =>
                                        isAvro(getField(NAME_FIELD)[index].sinkDataType) && (
                                            <FormItem
                                                label="Schema"
                                                name={[index, 'schemaInfo']}
                                                rules={[
                                                    {
                                                        required: !isFlink112,
                                                        message: '请输入Schema',
                                                    },
                                                ]}
                                            >
                                                <Input.TextArea
                                                    rows={9}
                                                    placeholder={`填写Avro Schema信息，示例如下：\n{\n\t"name": "testAvro",\n\t"type": "record",\n\t"fields": [{\n\t\t"name": "id",\n\t\t"type": "string"\n\t}]\n}`}
                                                />
                                            </FormItem>
                                        )
                                    }
                                </FormItem>
                            </React.Fragment>
                        )}
                        {isHaveUpdateMode(getFieldValue(NAME_FIELD)[index].type) && (
                            <>
                                <FormItem
                                    label="更新模式"
                                    name={[index, 'updateMode']}
                                    rules={[{ required: true, message: '请选择更新模式' }]}
                                >
                                    <Radio.Group
                                        disabled={isDisabledUpdateMode(
                                            getFieldValue(NAME_FIELD)[index].type,
                                            disableUpdateMode,
                                            componentVersion
                                        )}
                                        className="right-select"
                                    >
                                        <Radio value="append">追加(append)</Radio>
                                        <Radio
                                            value="upsert"
                                            disabled={
                                                getFieldValue(NAME_FIELD)[index].type === DATA_SOURCE_ENUM.CLICKHOUSE
                                            }
                                        >
                                            更新(upsert)
                                        </Radio>
                                    </Radio.Group>
                                </FormItem>
                                <FormItem noStyle dependencies={[[index, 'updateMode']]}>
                                    {({ getFieldValue: getField }) => (
                                        <>
                                            {getField(NAME_FIELD)[index].updateMode === 'upsert' &&
                                                isHaveUpdateStrategy(getFieldValue(NAME_FIELD)[index].type) && (
                                                    <FormItem
                                                        label="更新策略"
                                                        name={[index, 'allReplace']}
                                                        initialValue="false"
                                                        rules={[
                                                            {
                                                                required: true,
                                                                message: '请选择更新策略',
                                                            },
                                                        ]}
                                                    >
                                                        <Select className="right-select">
                                                            <Option key="true" value="true">
                                                                Null值替换原有数据
                                                            </Option>
                                                            <Option key="false" value="false">
                                                                Null值不替换原有数据
                                                            </Option>
                                                        </Select>
                                                    </FormItem>
                                                )}
                                            {getField(NAME_FIELD)[index].updateMode === 'upsert' &&
                                                (isHavePrimaryKey(getFieldValue(NAME_FIELD)[index].type) ||
                                                    !isDisabledUpdateMode(
                                                        getFieldValue(NAME_FIELD)[index].type,
                                                        disableUpdateMode,
                                                        componentVersion
                                                    )) && (
                                                    <FormItem
                                                        label="主键"
                                                        tooltip="主键必须存在于表字段中"
                                                        name={[index, 'primaryKey']}
                                                        rules={[
                                                            {
                                                                required: true,
                                                                message: '请输入主键',
                                                            },
                                                        ]}
                                                    >
                                                        <Select
                                                            className="right-select"
                                                            listHeight={200}
                                                            mode="multiple"
                                                            showSearch
                                                            showArrow
                                                            filterOption={(input, option) =>
                                                                option?.props.children
                                                                    .toLowerCase()
                                                                    .indexOf(input.toLowerCase()) >= 0
                                                            }
                                                        >
                                                            {primaryKeyOptionTypes}
                                                        </Select>
                                                    </FormItem>
                                                )}
                                        </>
                                    )}
                                </FormItem>
                            </>
                        )}
                    </>
                )}
            </FormItem>
            {/* 高级参数按钮 */}
            <FormItem wrapperCol={{ span: 24 }}>
                <Button block type="link" onClick={() => setShowAdvancedParams(!showAdvancedParams)}>
                    高级参数{showAdvancedParams ? <UpOutlined /> : <DownOutlined />}
                </Button>
            </FormItem>
            {/* 高级参数抽屉 */}
            <FormItem hidden={!showAdvancedParams} noStyle dependencies={[[index, 'type']]}>
                {({ getFieldValue }) => (
                    <>
                        {isHaveParallelism(getFieldValue(NAME_FIELD)[index].type) && (
                            <FormItem name={[index, 'parallelism']} label="并行度">
                                <InputNumber style={{ width: '100%' }} min={1} precision={0} />
                            </FormItem>
                        )}
                        {isES(getFieldValue(NAME_FIELD)[index].type) && isFlink112 && (
                            <FormItem name={[index, 'bulkFlushMaxActions']} label="数据输出条数">
                                <InputNumber style={{ width: '100%' }} min={1} max={10000} precision={0} />
                            </FormItem>
                        )}
                        {isKafka(getFieldValue(NAME_FIELD)[index].type) && (
                            <FormItem label="" name={[index, 'enableKeyPartitions']} valuePropName="checked">
                                <Checkbox style={{ marginLeft: 90 }} defaultChecked={false}>
                                    根据字段(Key)分区
                                </Checkbox>
                            </FormItem>
                        )}
                        {getFieldValue(NAME_FIELD)[index].type === DATA_SOURCE_ENUM.ES7 && (
                            <FormItem
                                label="索引映射"
                                tooltip={
                                    <span>
                                        ElasticSearch的索引映射配置，仅当动态索引时生效，更多请参考
                                        <a rel="noopener noreferrer" target="_blank" href={HELP_DOC_URL.INDEX}>
                                            帮助文档
                                        </a>
                                    </span>
                                }
                                name={[index, 'indexDefinition']}
                            >
                                <Input.TextArea placeholder={DEFAULT_MAPPING_TEXT} style={{ minHeight: '200px' }} />
                            </FormItem>
                        )}
                        <FormItem noStyle dependencies={[[index, 'enableKeyPartitions']]}>
                            {({ getFieldValue: getField }) =>
                                getField(NAME_FIELD)[index].enableKeyPartitions && (
                                    <FormItem
                                        label="分区字段"
                                        name={[index, 'partitionKeys']}
                                        rules={[{ required: true, message: '请选择分区字段' }]}
                                    >
                                        <Select
                                            className="right-select"
                                            mode="multiple"
                                            showSearch
                                            showArrow
                                            filterOption={(input, option) =>
                                                option?.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                                            }
                                        >
                                            {getColumnsByColumnsText(getField(NAME_FIELD)[index].columnsText).map(
                                                (column) => {
                                                    const fields = column.field?.trim();
                                                    return (
                                                        <Option value={fields} key={fields}>
                                                            {fields}
                                                        </Option>
                                                    );
                                                }
                                            )}
                                        </Select>
                                    </FormItem>
                                )
                            }
                        </FormItem>
                        {isRDB(getFieldValue(NAME_FIELD)[index].type) && (
                            <>
                                <FormItem
                                    label="数据输出时间"
                                    name={[index, 'batchWaitInterval']}
                                    rules={[{ required: true, message: '请输入数据输出时间' }]}
                                >
                                    <InputNumber
                                        style={{ width: '100%' }}
                                        min={0}
                                        max={600000}
                                        precision={0}
                                        addonAfter="ms/次"
                                    />
                                </FormItem>
                                <FormItem
                                    label="数据输出条数"
                                    name={[index, 'batchSize']}
                                    rules={[{ required: true, message: '请输入数据输出条数' }]}
                                >
                                    <InputNumber
                                        style={{ width: '100%' }}
                                        min={0}
                                        max={
                                            getFieldValue(NAME_FIELD)[index].type === DATA_SOURCE_ENUM.KUDU
                                                ? 100000
                                                : 10000
                                        }
                                        precision={0}
                                        addonAfter="条/次"
                                    />
                                </FormItem>
                            </>
                        )}
                        {!isHaveParallelism(getFieldValue(NAME_FIELD)[index].type) && (
                            <FormItem
                                label="分区类型"
                                tooltip="分区类型包括 DAY、HOUR、MINUTE三种。若分区不存在则会自动创建，自动创建的分区时间以当前任务运行的服务器时间为准"
                                name={[index, 'partitionType']}
                                initialValue="DAY"
                            >
                                <Select className="right-select">
                                    <Option value="DAY">DAY</Option>
                                    <Option value="HOUR">HOUR</Option>
                                    <Option value="MINUTE">MINUTE</Option>
                                </Select>
                            </FormItem>
                        )}
                        {/* 添加自定义参数 */}
                        {!isSqlServer(getFieldValue(NAME_FIELD)[index].type) && <CustomParams index={index} />}
                    </>
                )}
            </FormItem>
            <FormItem shouldUpdate noStyle>
                {({ getFieldValue }) => (
                    <DataPreviewModal
                        visible={visible}
                        type={getFieldValue(NAME_FIELD)[index].type}
                        onCancel={() => setVisible(false)}
                        params={params}
                    />
                )}
            </FormItem>
        </>
    );
}
