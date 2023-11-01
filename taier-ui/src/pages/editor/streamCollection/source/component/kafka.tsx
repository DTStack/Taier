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

import React, { useState } from 'react';
import { DownOutlined } from '@ant-design/icons';
import { DatePicker, Form, FormInstance, Radio, Select } from 'antd';

import stream from '@/api';
import { DATA_SOURCE_ENUM, DATA_SOURCE_VERSION, KAFKA_DATA_TYPE } from '@/constant';
import { IDataSourceUsedInSyncProps } from '@/interface';
import DataPreviewModal from '../dataPreviewModal';
import FormItemEditor from './formItemEditor';

const FormItem = Form.Item;
const Option = Select.Option;

export default (props: { collectionData: any; sourceList: IDataSourceUsedInSyncProps[]; form: FormInstance }) => {
    const { collectionData, sourceList, form } = props;
    const { isEdit, sourceMap } = collectionData;
    const { getFieldValue } = form;
    const { type } = sourceMap;
    const [topicList, setTopicList] = useState([]);
    const [previewParams, setPreviewParams] = useState({});
    const [previewVisible, setPreviewVisible] = useState(false);

    const getTopicType = (sourceId: any) => {
        stream
            .getTopicType({
                sourceId,
            })
            .then((res: any) => {
                if (res.data) {
                    setTopicList(res.data || []);
                }
            });
    };
    const loadPreview = () => {
        setPreviewParams({
            sourceId: sourceMap.sourceId,
            topic: sourceMap.topic,
        });
        setPreviewVisible(true);
    };

    const onSourceChange = (sourceId: any) => {
        getTopicType(sourceId);
    };

    const sourceDataOptions = sourceList?.map?.((o: any) => {
        return (
            <Option key={o.id} value={o.id}>
                {o.name}
                {DATA_SOURCE_VERSION[o.type as DATA_SOURCE_ENUM] &&
                    ` (${DATA_SOURCE_VERSION[o.type as DATA_SOURCE_ENUM]})`}
            </Option>
        );
    });
    return (
        <React.Fragment>
            <FormItem name="sourceId" label="数据源" rules={[{ required: true, message: '请选择数据源' }]}>
                <Select
                    getPopupContainer={(triggerNode: any) => triggerNode}
                    disabled={isEdit}
                    showSearch
                    placeholder="请选择数据源"
                    className="right-select"
                    onChange={onSourceChange}
                    filterOption={(input: any, option: any) =>
                        option.props.children.toString().toLowerCase().indexOf(input.toLowerCase()) >= 0
                    }
                >
                    {sourceDataOptions}
                </Select>
            </FormItem>
            <FormItem name="topic" label="Topic" rules={[{ required: true, message: '请选择topic' }]}>
                <Select
                    getPopupContainer={(triggerNode: any) => triggerNode}
                    disabled={isEdit}
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
            <FormItem name="codec" label="读取类型" rules={[{ required: true, message: '请选择读取类型' }]}>
                <Select
                    getPopupContainer={(triggerNode: any) => triggerNode}
                    disabled={isEdit && type === DATA_SOURCE_ENUM.KAFKA}
                    style={{ width: '100%' }}
                    placeholder="请选择读取类型"
                    showSearch
                >
                    <Option value={KAFKA_DATA_TYPE.TYPE_COLLECT_JSON}>json</Option>
                    <Option value={KAFKA_DATA_TYPE.TYPE_COLLECT_TEXT}>text</Option>
                </Select>
            </FormItem>
            {[
                DATA_SOURCE_ENUM.KAFKA_2X,
                DATA_SOURCE_ENUM.KAFKA,
                DATA_SOURCE_ENUM.TBDS_KAFKA,
                DATA_SOURCE_ENUM.KAFKA_HUAWEI,
            ].includes(type) && (
                <React.Fragment>
                    <FormItem required label="Offset" name="mode">
                        <Radio.Group className="right-select">
                            <Radio value="group-offsets">group</Radio>
                            <Radio value="latest-offset">latest</Radio>
                            <Radio value="earliest-offset">earliest</Radio>
                            <Radio value="timestamp">time</Radio>
                            <Radio value="specific-offsets">specific</Radio>
                        </Radio.Group>
                    </FormItem>
                    {getFieldValue('mode') === 'timestamp' && (
                        <FormItem label="选择时间" name="timestamp" rules={[{ required: true, message: '请选择时间' }]}>
                            <DatePicker
                                showTime
                                placeholder="请选择起始时间"
                                format="YYYY-MM-DD HH:mm:ss"
                                style={{ width: '100%' }}
                            />
                        </FormItem>
                    )}
                    {getFieldValue('mode') === 'specific-offsets' && (
                        <FormItem
                            label="偏移量"
                            className="collection-source_form-offsetvalue"
                            name="offset"
                            rules={[{ required: true, message: '请输入偏移量' }]}
                        >
                            <FormItemEditor
                                style={{ minHeight: 202, height: '100%' }}
                                className="bd"
                                sync={false}
                                placeholder="partition:0,offset:42;partition:1,offset:33;"
                            />
                        </FormItem>
                    )}
                </React.Fragment>
            )}
            <FormItem key="preview">
                <p className="txt-center">
                    <a style={{ cursor: 'pointer' }} onClick={loadPreview}>
                        数据预览 <DownOutlined />
                    </a>
                </p>
            </FormItem>
            <DataPreviewModal
                visible={previewVisible}
                type={type}
                onCancel={() => {
                    setPreviewParams({});
                    setPreviewVisible(false);
                }}
                params={previewParams}
            />
        </React.Fragment>
    );
};
