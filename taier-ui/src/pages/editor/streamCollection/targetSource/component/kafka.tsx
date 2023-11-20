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
import { Form, Radio, RadioChangeEvent, Select } from 'antd';

import stream from '@/api';
import { writeDataSequence, writePartitionKey } from '@/components/helpDoc/docs';
import { DATA_SOURCE_ENUM, SYNC_TYPE } from '@/constant';
import { streamTaskActions } from '../../taskFunc';

const FormItem = Form.Item;
const Option = Select.Option;

export default (props: { collectionData: any }) => {
    const { collectionData } = props;
    const { isEdit, targetMap = {}, sourceMap = {} } = collectionData;
    const haveDataSequence = [
        DATA_SOURCE_ENUM.KAFKA,
        DATA_SOURCE_ENUM.KAFKA_2X,
        DATA_SOURCE_ENUM.TBDS_KAFKA,
        DATA_SOURCE_ENUM.KAFKA_HUAWEI,
    ].includes(targetMap.type);

    const [topicList, setTopicList] = useState([]);
    const [tableFieldsList, setTableFieldsList] = useState([]);

    const getTopicType = (sourceId: any) => {
        stream.getTopicType({ sourceId }).then((res: any) => {
            if (res.data) {
                setTopicList(res.data);
            }
        });
    };
    /**
     * 获取分区字段
     */
    const getTableFieldsList = async () => {
        const { sourceMap = {}, componentVersion } = collectionData;
        const { sourceId, schema, tableName } = sourceMap;
        const res = await stream.getStreamTableColumn({ sourceId, schema, tableName, flinkVersion: componentVersion });
        if (res.code === 1) {
            setTableFieldsList(res.data || []);
        }
    };
    // 开启数据有序时，将通道控制中的作业写入并发数置为 1
    const onDataSequenceChange = (e: RadioChangeEvent) => {
        if (e.target.value) {
            streamTaskActions.updateChannelControlMap({ ...collectionData.settingMap, writerChannel: '1' });
        }
    };
    // 是否可以填选 partition key
    const canPartitionKeyWrite = (sourceMap: any) => {
        return (
            [DATA_SOURCE_ENUM.MYSQL, DATA_SOURCE_ENUM.UPDRDB, DATA_SOURCE_ENUM.ORACLE].includes(sourceMap.type) &&
            sourceMap.rdbmsDaType === SYNC_TYPE.INTERVAL
        );
    };

    useEffect(() => {
        if (targetMap && targetMap.sourceId) {
            getTopicType(targetMap.sourceId);
        }
        if (canPartitionKeyWrite(sourceMap) && targetMap.type !== DATA_SOURCE_ENUM.ADB_FOR_PG) {
            getTableFieldsList();
        }
    }, []);

    useEffect(() => {
        setTableFieldsList([]);
        setTopicList([]);
    }, [targetMap.sourceId]);

    return (
        <React.Fragment>
            <FormItem name="topic" label="Topic" rules={[{ required: true, message: '请选择topic' }]}>
                <Select
                    getPopupContainer={(triggerNode: any) => triggerNode}
                    disabled={isEdit && targetMap.type === DATA_SOURCE_ENUM.KAFKA}
                    style={{ width: '100%' }}
                    placeholder="请选择topic"
                    showSearch
                >
                    {topicList.map((topic: any) => {
                        return (
                            <Option key={`${topic}`} value={topic}>
                                {topic}
                            </Option>
                        );
                    })}
                </Select>
            </FormItem>
            {haveDataSequence && (
                <FormItem required label="数据有序" name="dataSequence" tooltip={writeDataSequence}>
                    <Radio.Group onChange={onDataSequenceChange}>
                        <Radio value={false}>关闭</Radio>
                        <Radio value>开启</Radio>
                    </Radio.Group>
                </FormItem>
            )}
            {haveDataSequence && canPartitionKeyWrite(sourceMap) && (
                <FormItem label="Partition Key" name="partitionKey" tooltip={writePartitionKey}>
                    <Select
                        getPopupContainer={(triggerNode: any) => triggerNode}
                        disabled={isEdit}
                        placeholder="请选择Partition Key"
                        mode="multiple"
                        showArrow
                        style={{ width: '100%' }}
                    >
                        {Array.isArray(tableFieldsList) &&
                            tableFieldsList.map((item: any) => (
                                <Option key={item.key} value={item.key}>
                                    {item.key}
                                </Option>
                            ))}
                    </Select>
                </FormItem>
            )}
        </React.Fragment>
    );
};
