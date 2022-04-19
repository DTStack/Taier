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

import api from "@/api";
import stream from "@/api/stream";
import { dirtySource, jobSpeedLimit, recordDirtyStream, writerChannel } from "@/components/helpDoc/docs";
import { DATA_SOURCE_ENUM, FLINK_VERSIONS, formItemLayout } from "@/constant";
import { isKafka } from "@/utils/enums";
import molecule from "@dtinsight/molecule";
import { connect as moleculeConnect } from '@dtinsight/molecule/esm/react';
import { AutoComplete, Button, Checkbox, Form, FormInstance, FormProps, Input, Select } from "antd";
import { CheckboxChangeEvent } from "antd/lib/checkbox";
import { get } from "lodash";
import React from "react"
import { SettingMap, streamTaskActions } from "../taskFunc";

interface IProps extends FormProps {
    collectionData?: any;
    readonly?: boolean;
}
class CollectionChannel extends React.Component<IProps, any> {
    state: any = {
        dirtySourceList: [],
        topicPartitionNum: 5
    }
    formRef = React.createRef<FormInstance>(); 
    constructor(props: any) {
        super(props);
    }

    componentDidMount() {
        const settingMap: SettingMap = get(this.props, 'collectionData.settingMap.isSaveDirty');
        if (settingMap) {
            this.loadSource();
        }
        this.getTopicPartitionNum();
    }

    getTopicPartitionNum = async () => {
        const { collectionData } = this.props;
        const { sourceId, topic, type } = collectionData?.sourceMap;
        if (!isKafka(type)) return
        const res = await stream.getTopicPartitionNum({ sourceId, topic })
        if (!res || res.code !== 1) return;
        this.setState({
            topicPartitionNum: res?.data
        })
    }
    recordDirtyChange(e: CheckboxChangeEvent) {
        if (e.target.checked) {
            this.loadSource();
        }
    }
    async loadSource() {
        this.setState({
            dirtySourceList: []
        })
        const res = await api.getOfflineDataSource({
            sourceType: DATA_SOURCE_ENUM.HIVE
        });
        if (res && res.code == 1) {
            this.setState({
                dirtySourceList: res?.data
            })
        }
    }

    renderOptions(max: number, min = 1) {
        const result = [];
        for (let i = min; i <= max; i++) {
            result.push({
                value: i.toString(),
                label: i,
            })
        }
        return result;
    }
    handleValueChange = (values: any, allValues: any) => {
        streamTaskActions.updateChannelControlMap(values, false);
    }
    prev () {
        streamTaskActions.navtoStep(1)
    }

    next () {
        this.formRef.current!.validateFields().then(values => {
            streamTaskActions.navtoStep(3)
        })
    }
    render(): React.ReactNode {
        const { collectionData, readonly } = this.props;
        const { dirtySourceList, topicPartitionNum } = this.state;
        const settingMap: SettingMap = get(collectionData, 'settingMap', {});
        const sourceMap = get(collectionData, 'sourceMap', {});
        const targetMap = get(collectionData, 'targetMap', {});
        const componentVersion = get(collectionData, 'componentVersion', '');
        const { isSaveDirty } = settingMap;

        // const unLimitedOption: any[] = [
        //     <Select.Option value='-1' key={-1}>不限制上传速率</Select.Option>
        // ]
        const unLimitedOption: any[] = [
			{
                value: '不限制上传速率'
			},
		];
        const speedOption: any = this.renderOptions(20);
        const kafkaDataSequence = (isKafka(targetMap?.type) && targetMap?.dataSequence) || (sourceMap?.type === DATA_SOURCE_ENUM.ORACLE && sourceMap?.transferType === 1);
        
        settingMap.speed = settingMap.speed === -1 ? '不限制上传速率' : settingMap.speed
        
        return (<div className="g-step4">
            <Form 
                {...formItemLayout}
                initialValues={settingMap}
                ref={this.formRef}
                onValuesChange={this.handleValueChange}
            >
                <Form.Item
                    name='speed'
                    label="作业速率上限"
                    rules={[{ required: true }]}
                    tooltip={jobSpeedLimit}
                >
                    <AutoComplete options={unLimitedOption.concat(speedOption)}>
                        <Input suffix="MB/s" />
                    </AutoComplete>
                </Form.Item>
                <Form.Item
                    name='readerChannel'
                    initialValue={1}
                    label="作业读取并发数"
                >
                    <Select showSearch disabled={!isKafka(sourceMap?.type)} options={this.renderOptions(topicPartitionNum)}>
                        
                    </Select>
                </Form.Item>
                <Form.Item
                    name='writerChannel'
                    initialValue={1}
                    label="作业写入并发数"
                    tooltip={writerChannel}
                >
                    <Select showSearch disabled={kafkaDataSequence} options={this.renderOptions(20)}>
                    </Select>
                </Form.Item>
                {componentVersion !== FLINK_VERSIONS.FLINK_1_12 && (
                    <React.Fragment>
                        <Form.Item
                            name='isSaveDirty'
                            valuePropName="checked"
                            label="错误记录数"
                            tooltip={recordDirtyStream}
                        >
                            <Checkbox onChange={this.recordDirtyChange.bind(this)}>记录保存</Checkbox>
                        </Form.Item>
                        {isSaveDirty ? (
                            <React.Fragment>
                                <Form.Item
                                    name='sourceId'
                                    rules={[{ required: true, message: '请选择脏数据写入的hive库' }]}
                                    label="脏数据写入hive库"
                                >
                                    <Select placeholder='请选择脏数据写入的hive库'>
                                        {dirtySourceList.map((source: any) => {
                                            return <Select.Option key={source.id} value={source.id}>{source.dataName}</Select.Option>
                                        })}
                                    </Select>
                                </Form.Item>
                                <Form.Item
                                    name='tableName'
                                    label="脏数据写入hive表"
                                    tooltip={dirtySource}
                                >
                                    <Input placeholder='系统默认分配' />
                                </Form.Item>
                            </React.Fragment>
                        ) : null}
                    </React.Fragment>
                )}
            </Form>
            {!readonly && (
                <div className="steps-action">
                    <Button style={{ marginRight: 8 }} onClick={() => this.prev()}>上一步</Button>
                    <Button type="primary" onClick={() => this.next()}>下一步</Button>
                </div>
            )}
        </div>)
    }
}

export default moleculeConnect(molecule.editor, CollectionChannel);