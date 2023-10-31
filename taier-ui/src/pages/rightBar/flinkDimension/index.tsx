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

import { useContext, useEffect, useMemo, useRef, useState } from 'react';
import { DeleteOutlined,PlusOutlined } from '@ant-design/icons';
import molecule from '@dtinsight/molecule';
import type { FormInstance } from 'antd';
import { Button, Collapse, Form, Popconfirm } from 'antd';
import classNames from 'classnames';

import stream from '@/api';
import { DATA_SOURCE_ENUM, formItemLayout } from '@/constant';
import type { IDataColumnsProps, IDataSourceUsedInSyncProps, IFlinkSideProps } from '@/interface';
import type { IRightBarComponentProps } from '@/services/rightBarService';
import { FormContext } from '@/services/rightBarService';
import { isCacheExceptLRU,isHaveSchema, isHaveTableColumn, isHaveTableList } from '@/utils/is';
import DimensionForm from './form';

const { Panel } = Collapse;

const DEFAULT_INPUT_VALUE: Partial<IFlinkSideProps> = {
    type: DATA_SOURCE_ENUM.MYSQL,
    columns: [],
    sourceId: undefined,
    table: undefined,
    tableName: undefined,
    primaryKey: undefined,
    hbasePrimaryKey: undefined,
    hbasePrimaryKeyType: undefined,
    errorLimit: undefined,
    esType: undefined,
    index: undefined,
    parallelism: 1,
    cache: 'LRU',
    cacheSize: 10000,
    cacheTTLMs: 60000,
    asyncPoolSize: 5,
};

/**
 * 表单收集的字段
 */
export const NAME_FIELD = 'panelColumn';

interface IFormFieldProps {
    [NAME_FIELD]: Partial<IFlinkSideProps>[];
}

export default function FlinkDimensionPanel({ current }: IRightBarComponentProps) {
    const { form } = useContext(FormContext) as { form?: FormInstance<IFormFieldProps> };

    const [panelActiveKey, setPanelKey] = useState<string[]>([]);
    // 数据源下拉列表，用 key 值缓存结果
    const [dataSourceOptions, setDataSourceOptions] = useState<Record<string, IDataSourceUsedInSyncProps[]>>({});
    /**
     * 表下拉列表,以 soureceId-schema 拼接作 key 值，后以 searchKey 作为 key 值，最后是搜索结果
     * {
     * 	[`${sourceId}-${schema}`]: {[`${searchKey}`]: string[]}}
     * }
     */
    const [tableOptions, setTableOptions] = useState<Record<string, Record<string, string[]>>>({});
    // 表字段列下拉列表
    const [columnsOptions, setColOptions] = useState<Record<string, IDataColumnsProps[]>>({});

    // 用于表示添加或删除的标志位
    const isAddOrRemove = useRef(false);

    const getTableColumns = (sourceId: number, tableName: string, schema = '') => {
        if (!sourceId || !tableName) {
            return;
        }
        const uniqKey = `${sourceId}-${tableName}-${schema}`;
        if (!columnsOptions[uniqKey]) {
            stream
                .getStreamTableColumn({
                    sourceId,
                    tableName,
                    schema,
                    flinkVersion: current?.tab?.data?.componentVersion,
                })
                .then((v) => {
                    if (v.code === 1) {
                        setColOptions((options) => {
                            const next = options;
                            next[uniqKey] = v.data;
                            return { ...next };
                        });
                    }
                });
        }
    };

    /**
     * 获取Schema列表
     * @deprecated 暂时没有数据源会有请求 schema 列表
     */
    const getSchemaData = async () => {};

    const getTypeOriginData = (type: DATA_SOURCE_ENUM) => {
        if (!dataSourceOptions[type]) {
            stream.getTypeOriginData({ type }).then((v) => {
                if (v.code === 1) {
                    setDataSourceOptions((options) => {
                        const next = options;
                        next[type] = v.data;
                        return { ...next };
                    });
                }
            });
        }
    };

    // 获取表
    const getTableType = async (type: DATA_SOURCE_ENUM, sourceId: number, schema?: string, searchKey?: string) => {
        const uniqKey = `${sourceId}-${schema}`;
        // postgresql kingbasees8 schema必填处理
        const disableReq = (type === DATA_SOURCE_ENUM.POSTGRESQL || type === DATA_SOURCE_ENUM.KINGBASE8) && !schema;
        if (disableReq) {
            return;
        }
        stream
            .listTablesBySchema({
                sourceId,
                schema: schema || '',
                isSys: false,
                searchKey,
            })
            .then((res) => {
                if (res.code === 1) {
                    setTableOptions((options) => {
                        const next = options;
                        if (next[uniqKey]) {
                            next[uniqKey][searchKey || ''] = res.data;
                        } else {
                            next[uniqKey] = { [searchKey || '']: res.data };
                        }
                        return { ...next };
                    });
                }
            });
    };

    const handleSyncFormToTab = () => {
        const side = form?.getFieldsValue()[NAME_FIELD];
        // 将表单的值保存至 tab 中
        molecule.editor.updateTab({
            id: current!.tab!.id,
            data: {
                ...current!.tab!.data,
                side,
            },
        });
    };

    const handleFormValuesChange = (changedValues: IFormFieldProps, values: IFormFieldProps) => {
        if (isAddOrRemove.current) {
            isAddOrRemove.current = false;
            handleSyncFormToTab();
            return;
        }

        // 当前正在修改的数据索引
        const changeIndex = changedValues[NAME_FIELD].findIndex((col) => col);
        const checkedKeys = Object.keys(changedValues[NAME_FIELD][changeIndex]);

        if (checkedKeys.includes('type')) {
            const value = changedValues[NAME_FIELD][changeIndex].type;
            getTypeOriginData(value!);

            // reset all fields
            const nextValue = { ...values };
            nextValue[NAME_FIELD][changeIndex] = {
                ...DEFAULT_INPUT_VALUE,
                type: value,
                cache: isCacheExceptLRU(value) ? 'ALL' : 'LRU',
            };
            form?.setFieldsValue(nextValue);
        }

        if (checkedKeys.includes('sourceId')) {
            const value = changedValues[NAME_FIELD][changeIndex].sourceId;
            const nextValue = { ...values };
            // reset fields
            nextValue[NAME_FIELD][changeIndex] = {
                ...DEFAULT_INPUT_VALUE,
                type: nextValue[NAME_FIELD][changeIndex].type,
                sourceId: value,
                cache: isCacheExceptLRU(nextValue[NAME_FIELD][changeIndex].type) ? 'ALL' : 'LRU',
            };
            form?.setFieldsValue(nextValue);

            const panel = nextValue[NAME_FIELD][changeIndex];
            if (isHaveTableList(panel.type)) {
                getTableType(panel.type!, panel.sourceId!, panel.schema);

                if (isHaveSchema(panel.type)) {
                    getSchemaData();
                }
            }
        }

        if (checkedKeys.includes('schema')) {
            const value = changedValues[NAME_FIELD][changeIndex].schema;
            const nextValue = { ...values };
            // reset fields
            nextValue[NAME_FIELD][changeIndex] = {
                ...DEFAULT_INPUT_VALUE,
                type: nextValue[NAME_FIELD][changeIndex].type,
                sourceId: nextValue[NAME_FIELD][changeIndex].sourceId,
                customParams: nextValue[NAME_FIELD][changeIndex].customParams,
                schema: value,
                cache: isCacheExceptLRU(nextValue[NAME_FIELD][changeIndex].type) ? 'ALL' : 'LRU',
            };
            form?.setFieldsValue(nextValue);

            const panel = nextValue[NAME_FIELD][changeIndex];
            if (isHaveTableList(panel.type)) {
                getTableType(panel.type!, panel.sourceId!, panel.schema);
            }
        }

        if (checkedKeys.includes('table')) {
            const value = changedValues[NAME_FIELD][changeIndex].table;
            const nextValue = { ...values };
            // reset fields
            nextValue[NAME_FIELD][changeIndex] = {
                ...DEFAULT_INPUT_VALUE,
                type: nextValue[NAME_FIELD][changeIndex].type,
                sourceId: nextValue[NAME_FIELD][changeIndex].sourceId,
                customParams: nextValue[NAME_FIELD][changeIndex].customParams,
                schema: nextValue[NAME_FIELD][changeIndex].schema,
                table: value,
                cache: isCacheExceptLRU(nextValue[NAME_FIELD][changeIndex].type) ? 'ALL' : 'LRU',
            };

            form?.setFieldsValue(nextValue);

            const panel = nextValue[NAME_FIELD][changeIndex];
            if (isHaveTableColumn(panel.type)) {
                getTableColumns(panel.sourceId!, panel.table!, panel.schema);
            }
        }

        handleSyncFormToTab();
    };

    const handlePanelChanged = (type: 'add' | 'delete', panelKey?: string) => {
        if (type === 'add') {
            getTypeOriginData(DEFAULT_INPUT_VALUE.type!);
        } else {
            setPanelKey((keys) => keys.filter((key) => panelKey !== key));
        }

        isAddOrRemove.current = true;
    };

    useEffect(() => {
        current?.tab?.data?.side?.forEach((item: IFlinkSideProps) => {
            if (item.type) {
                getTypeOriginData(item.type);
            }
            if (item.type && item.sourceId) {
                getTableType(item.type, item.sourceId, item.schema);
            }
            if (item.sourceId && item.table) {
                getTableColumns(item.sourceId, item.table, item.schema);
            }
        });
    }, [current]);

    const initialValues = useMemo<IFormFieldProps>(() => {
        return {
            [NAME_FIELD]: (current?.tab?.data?.side as IFlinkSideProps[]) || [],
        };
    }, []);

    return (
        <molecule.component.Scrollbar>
            <div className="panel-content">
                <Form<IFormFieldProps>
                    {...formItemLayout}
                    form={form}
                    onValuesChange={handleFormValuesChange}
                    initialValues={initialValues}
                >
                    <Form.List name={NAME_FIELD}>
                        {(fields, { add, remove }) => (
                            <>
                                <Collapse
                                    activeKey={panelActiveKey}
                                    bordered={false}
                                    onChange={(key) => setPanelKey(key as string[])}
                                    destroyInactivePanel
                                >
                                    {fields.map((field, index) => {
                                        const col = form?.getFieldValue(NAME_FIELD)[index] || {};
                                        return (
                                            <Panel
                                                header={
                                                    <div className="input-panel-title">
                                                        <span>{` 维表 ${index + 1} ${
                                                            col.table ? `(${col.table})` : ''
                                                        }`}</span>
                                                    </div>
                                                }
                                                key={field.key.toString()}
                                                extra={
                                                    <Popconfirm
                                                        placement="topLeft"
                                                        title="你确定要删除此维表吗？"
                                                        onConfirm={() => {
                                                            handlePanelChanged('delete', field.key.toString());
                                                            remove(field.name);
                                                        }}
                                                        {...{
                                                            onClick: (e: any) => {
                                                                e.stopPropagation();
                                                            },
                                                        }}
                                                    >
                                                        <DeleteOutlined className={classNames('title-icon')} />
                                                    </Popconfirm>
                                                }
                                                style={{ position: 'relative' }}
                                                className="input-panel"
                                            >
                                                <DimensionForm
                                                    index={index}
                                                    sourceOptions={dataSourceOptions[col.type!]}
                                                    tableOptions={tableOptions[`${col.sourceId}-${col.schema}`]}
                                                    columnsOptions={
                                                        columnsOptions[
                                                            `${col.sourceId}-${col.table}-${col.schema || ''}`
                                                        ]
                                                    }
                                                    onTableSearch={getTableType}
                                                    onColumnsChange={handleFormValuesChange}
                                                />
                                            </Panel>
                                        );
                                    })}
                                </Collapse>
                                <Button
                                    size="large"
                                    block
                                    onClick={() => {
                                        handlePanelChanged('add');
                                        add({ ...DEFAULT_INPUT_VALUE });
                                    }}
                                    icon={<PlusOutlined />}
                                >
                                    <span>添加维表</span>
                                </Button>
                            </>
                        )}
                    </Form.List>
                </Form>
            </div>
        </molecule.component.Scrollbar>
    );
}
