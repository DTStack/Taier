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

import React, { useEffect, useState } from 'react';
import { Form, Input, Radio, Select, Table } from 'antd';

import stream from '@/api';
import { writeDataSequence, writeDocForADB } from '@/components/helpDoc/docs';
import { streamTaskActions } from '../../taskFunc';

const FormItem = Form.Item;
const Option = Select.Option;

export default (props: { collectionData: any }) => {
    const { collectionData } = props;
    const { isEdit, targetMap, sourceMap } = collectionData;
    const { tableMappingList, mappingType, sourceId, transferType } = targetMap;

    const [schemaList, setSchemaList] = useState([]);
    const [adbTableList, setAdbTableList] = useState([]);

    const getSchemaList = async (sourceId: any, schema?: string) => {
        setSchemaList([]);
        const res = await stream.getAllSchemas({ sourceId, isSys: false, schema });
        if (res?.code === 1) {
            setSchemaList(res.data || []);
        }
    };

    /**
     * @param syncSchema -  props 里的 schema 可能是老的，react 还没异步更新，用 syncSchema 传个最新的值
     */
    const getSchemaTableList = (searchKey?: string, syncSchema?: string) => {
        const { schema } = targetMap;
        setAdbTableList([]);
        stream
            .listTablesBySchema({
                sourceId,
                searchKey,
                schema: syncSchema || schema,
            })
            .then((res: any) => {
                if (res.code === 1) {
                    setAdbTableList(res.data || []);
                }
            });
    };
    const onChangeMap = (val: string, record: any) => {
        const newMap = tableMappingList.map((item: any) => {
            if (item.source === record.source) {
                item.sink = val;
            }
            return item;
        });
        const table = newMap.map((item: any) => {
            return item.sink;
        });
        const fields = {
            ...targetMap,
            tableMappingList: newMap,
            table,
        };
        streamTaskActions.updateTargetMap(fields, false);
    };

    useEffect(() => {
        if (sourceId) {
            getSchemaList(sourceId);
            transferType === 1 && getSchemaTableList();
        }
    }, [sourceId]);

    const columns = [
        {
            key: 'source',
            dataIndex: 'source',
            title: '来源表',
            render: (text: string) => {
                return (
                    <div style={{ position: 'relative' }}>
                        <Input value={text} readOnly />
                        <span style={{ position: 'absolute', right: -13, top: 7 }}>--</span>
                    </div>
                );
            },
        },
        {
            key: 'sink',
            dataIndex: 'sink',
            title: '目标表',
            width: '50%',
            render: (text: string, record: any) => {
                const value = record === text ? null : text;
                return (
                    <Select
                        getPopupContainer={(triggerNode: any) => triggerNode}
                        style={{ width: '100%' }}
                        placeholder="请选择目标表"
                        showSearch
                        value={value}
                        onChange={(e) => onChangeMap(e, record)}
                        onSearch={getSchemaTableList}
                    >
                        {adbTableList.map((table: any) => {
                            return (
                                <Option key={`${table}`} value={table}>
                                    {table}
                                </Option>
                            );
                        })}
                    </Select>
                );
            },
        },
    ];

    return (
        <React.Fragment>
            <FormItem name="schema" label="schema" rules={[{ required: true, message: '请选择schema' }]}>
                <Select
                    getPopupContainer={(triggerNode: any) => triggerNode}
                    style={{ width: '100%' }}
                    placeholder="请选择schema"
                    showSearch
                    onSelect={(schema: string) => {
                        const newMap = tableMappingList.map((item: any) => {
                            item.sink = null;
                            return item;
                        });
                        const fields = {
                            ...targetMap,
                            schema,
                            tableMappingList: newMap,
                        };
                        streamTaskActions.updateTargetMap(fields, false);
                        mappingType === 2 && getSchemaTableList('', schema);
                    }}
                >
                    {schemaList.map((schema: any) => {
                        return (
                            <Option key={`${schema}`} value={schema}>
                                {schema}
                            </Option>
                        );
                    })}
                </Select>
            </FormItem>
            <FormItem required label="表" name="mappingType" tooltip={writeDataSequence}>
                <Radio.Group
                    onChange={(e: any) => {
                        if (e.target.value === 2) {
                            getSchemaTableList();
                        }
                    }}
                    disabled={isEdit || sourceMap?.allTable}
                >
                    <Radio value={1}>根据源表名称映射</Radio>
                    <Radio value={2}>手动映射</Radio>
                </Radio.Group>
            </FormItem>
            {mappingType === 2 && <Table columns={columns} dataSource={tableMappingList || []} pagination={false} />}
            <FormItem
                label="写入模式"
                className="txt-left"
                name="writeMode"
                rules={[{ required: true }]}
                tooltip={writeDocForADB}
            >
                <Radio.Group>
                    <Radio value="APPEND">追加（Insert Into）</Radio>
                    <Radio value="REPLACE">更新（replace Into）</Radio>
                </Radio.Group>
            </FormItem>
        </React.Fragment>
    );
};
