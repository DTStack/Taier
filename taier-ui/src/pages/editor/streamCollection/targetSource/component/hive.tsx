// @ts-nocheck
/* eslint-disable */
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

import api from '@/api';
import { analyticalRules, partitionType } from '@/components/helpDoc/docs';
import { PARTITION_TYPE, WRITE_TABLE_TYPE } from '@/constant';
import { isMysqlTypeSource } from '@/utils/is';
import { Form, Input, InputNumber, Radio, Select } from 'antd';
import { debounce } from 'lodash';
import React, { useEffect, useState } from 'react';

const FormItem = Form.Item;
const Option = Select.Option;
const prefixRule = '${schema}_${table}';

export default (props: { collectionData: any }) => {
    const { collectionData } = props;
    const { targetMap = {}, sourceMap = {} } = collectionData;
    const { writeTableType, table, sourceId, writeMode } = targetMap;
    const isMysqlSource = isMysqlTypeSource(sourceMap.type);

    const [tableList, setTableList] = useState([]);
    const [partitions, setPartitions] = useState([]);

    const getTableList = (sourceId: number, searchKey?: string) => {
        setTableList([]);
        api.getStreamTablelist({
            sourceId,
            isSys: false,
            searchKey,
        }).then((res: any) => {
            if (res.code === 1) {
                setTableList(res.data || []);
            }
        });
    };

    const _searchTableList = (text: string) => {
        getTableList(sourceId, text);
    };
    const searchTableList = debounce(_searchTableList, 500);

    const onHiveTableChange = async (sourceId: any, tableName: any) => {
        // this.setState({
        //     partition: [],
        //     partitions: []
        // })
        setPartitions([]);
        if (!sourceId || !tableName) {
            return;
        }
        const res = await api.getHivePartitions({
            sourceId,
            tableName,
        });
        if (res && res.code == 1) {
            const partitions = res.data;
            if (partitions && partitions.length) {
                setPartitions(res.data);
            }
        }
    };

    useEffect(() => {
        if (sourceId && table) {
            onHiveTableChange(sourceId, table);
        }
    }, [sourceId, table]);
    useEffect(() => {
        if (sourceId) getTableList(sourceId);
    }, [sourceId]);

    return (
        <React.Fragment>
            <FormItem label="写入表" name="writeTableType" rules={[{ required: true }]} tooltip={writeTableType}>
                <Radio.Group disabled>
                    {isMysqlSource ? (
                        <Radio key={WRITE_TABLE_TYPE.AUTO} value={WRITE_TABLE_TYPE.AUTO} style={{ float: 'left' }}>
                            自动建表
                        </Radio>
                    ) : null}
                    <Radio key={WRITE_TABLE_TYPE.HAND} value={WRITE_TABLE_TYPE.HAND} style={{ float: 'left' }}>
                        手动选择分区表
                    </Radio>
                </Radio.Group>
            </FormItem>
            {writeTableType === WRITE_TABLE_TYPE.AUTO && (
                <React.Fragment>
                    <FormItem
                        label="表名拼装规则"
                        name="analyticalRules"
                        rules={[
                            {
                                required: false,
                                message: '该字段不能为空',
                            },
                            {
                                pattern: /^[^.&%\s]*$/,
                                message: '不能包含空格、小数点等特殊字符，需符合Hive表建表规范',
                            },
                        ]}
                        tooltip={analyticalRules}
                    >
                        <Input addonBefore={`stream_${prefixRule}`} />
                    </FormItem>
                    <FormItem
                        label="存储类型"
                        name="fileType"
                        rules={[{ required: true, message: '存储类型不能为空' }]}
                    >
                        <Radio.Group>
                            <Radio value="orc" style={{ float: 'left', marginRight: 18 }}>
                                orc
                            </Radio>
                            <Radio value="text" style={{ float: 'left', marginRight: 18 }}>
                                text
                            </Radio>
                            <Radio value="parquet" style={{ float: 'left' }}>
                                parquet
                            </Radio>
                        </Radio.Group>
                    </FormItem>
                </React.Fragment>
            )}
            <FormItem
                label="分区粒度"
                name="partitionType"
                rules={[{ required: false, message: '该字段不能为空' }]}
                tooltip={partitionType}
            >
                <Radio.Group>
                    <Radio value={PARTITION_TYPE.DAY} style={{ float: 'left', marginRight: 18 }}>
                        天
                    </Radio>
                    <Radio value={PARTITION_TYPE.HOUR} style={{ float: 'left' }}>
                        小时
                    </Radio>
                </Radio.Group>
            </FormItem>
            {writeTableType == WRITE_TABLE_TYPE.HAND && (
                <FormItem label="表" name="table" rules={[{ required: true, message: '请选择表' }]}>
                    <Select
                        showSearch
                        placeholder="请选择表"
                        getPopupContainer={(triggerNode: any) => triggerNode}
                        onSearch={(v) => {
                            searchTableList(v);
                        }}
                    >
                        {tableList.map((tableName: any) => {
                            return (
                                <Option key={tableName} value={tableName}>
                                    {tableName}
                                </Option>
                            );
                        })}
                    </Select>
                </FormItem>
            )}
            {writeTableType == WRITE_TABLE_TYPE.HAND && table && partitions && partitions.length && (
                <FormItem label="分区" name="partition" rules={[{ required: true, message: '请选择分区' }]}>
                    <Select>
                        {partitions.map((partition: any) => {
                            return (
                                <Option key={partition} value={partition}>
                                    {partition}
                                </Option>
                            );
                        })}
                    </Select>
                </FormItem>
            )}
            <FormItem
                label={'文件大小'}
                name="maxFileSize"
                rules={[
                    {
                        required: false,
                        message: '请输入文件大小',
                    },
                    {
                        validator: (rule: any, value: any, callback: any) => {
                            let errorMsg: any;
                            if (!value && value !== 0) {
                                callback();
                                return;
                            }
                            try {
                                value = parseFloat(value);
                                if (value <= 0) {
                                    errorMsg = '数字必须大于0';
                                } else if (value != parseInt(value, 10)) {
                                    errorMsg = '必须为整数';
                                }
                            } catch (e) {
                                errorMsg = '请填写大于0的有效数字';
                            } finally {
                                callback(errorMsg);
                            }
                        },
                    },
                ]}
                extra={
                    <div>
                        若要按时间大小进行写入，可修改【环境参数】中{' '}
                        <span className="mtk12">flink.checkpoint.interval</span> 参数
                    </div>
                }
            >
                <FormInputNumber />
            </FormItem>
            <FormItem
                label="写入模式"
                className="txt-left"
                name="writeMode"
                rules={[{ required: true }]}
                extra={
                    writeMode == 'replace' && (
                        <p style={{ color: 'red' }}>注意：切换为覆盖模式，任务启动时，将删除目标表和历史数据</p>
                    )
                }
            >
                <Radio.Group>
                    <Radio value="insert" style={{ float: 'left' }}>
                        追加（Insert Into）
                    </Radio>
                </Radio.Group>
            </FormItem>
        </React.Fragment>
    );
};

const FormInputNumber = (props: { value?: any; title?: any; onChange?: any }) => {
    const { value, title = 'MB', onChange } = props;
    return (
        <React.Fragment>
            <span style={{ marginRight: 8 }}>每隔</span>
            <InputNumber style={{ width: 170 }} value={value} onChange={onChange} />
            <span style={{ marginLeft: 8 }}>{title}写入一次</span>
        </React.Fragment>
    );
};
