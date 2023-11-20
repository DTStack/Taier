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
import React, { useContext, useState } from 'react';
import { DownOutlined,UpOutlined } from '@ant-design/icons';
import {
    Button,
    Cascader,
    Checkbox,
    Col,
    DatePicker,
    Form,
    Input,
    InputNumber,
    message,
    Radio,
    Row,
    Select,
} from 'antd';
import type { DefaultOptionType } from 'antd/lib/cascader';

import Editor from '@/components/editor';
import {
    CODE_TYPE,
    DATA_SOURCE_ENUM,
    DATA_SOURCE_TEXT,
    DATA_SOURCE_VERSION,
    FLINK_VERSIONS,
    formItemLayout,
    KAFKA_DATA_LIST,
    KAFKA_DATA_TYPE,
    SOURCE_TIME_TYPE,
} from '@/constant';
import type { IDataSourceUsedInSyncProps } from '@/interface';
import { taskRenderService } from '@/services';
import { FormContext } from '@/services/rightBarService';
import taskSaveService from '@/services/taskSaveService';
import { getColumnsByColumnsText } from '@/utils';
import { isAvro, isKafka, isShowTimeForOffsetReset } from '@/utils/is';
import DataPreviewModal from '../../editor/streamCollection/source/dataPreviewModal';
import { CustomParams } from '../customParams';
import { NAME_FIELD } from '.';

const FormItem = Form.Item;
const { Option } = Select;

interface ISourceFormProps {
    /**
     * 用于 form 的 name 前缀
     */
    index: number;
    componentVersion?: Valueof<typeof FLINK_VERSIONS>;
    topicOptionType: string[];
    originOptionType: IDataSourceUsedInSyncProps[];
    timeZoneData: DefaultOptionType[];
}

export default function SourceForm({
    index,
    componentVersion,
    originOptionType,
    topicOptionType,
    timeZoneData,
}: ISourceFormProps) {
    const { form } = useContext(FormContext);
    const [visible, setVisible] = useState(false);
    const [params, setParams] = useState({});
    const [showAdvancedParams, setShowAdvancedParams] = useState(false);

    const showPreviewModal = () => {
        const { sourceId, topic } = form?.getFieldValue(NAME_FIELD)?.[index] || {};
        let nextParam = {};
        if (!sourceId || !topic) {
            message.error('数据预览需要选择数据源和Topic！');
            return;
        }
        nextParam = { sourceId, topic };
        setVisible(true);
        setParams(nextParam);
    };

    const validDes = taskSaveService.generateValidDesSource(form?.getFieldValue(NAME_FIELD)?.[index], componentVersion);

    return (
        <>
            <FormItem label="类型" name={[index, 'type']} rules={validDes.type}>
                <Select<DATA_SOURCE_ENUM>
                    placeholder="请选择类型"
                    className="right-select"
                    showSearch
                    filterOption={(input, option) =>
                        !!option?.value?.toString().toUpperCase().includes(input.toUpperCase())
                    }
                    options={taskRenderService.getState().supportSourceList.flinkSqlSources.map((t) => ({
                        label: DATA_SOURCE_TEXT[t],
                        value: t,
                    }))}
                />
            </FormItem>
            <FormItem label="数据源" name={[index, 'sourceId']} rules={validDes.sourceId}>
                <Select
                    showSearch
                    placeholder="请选择数据源"
                    className="right-select"
                    filterOption={(input, option) =>
                        !!option?.value?.toString().toUpperCase().includes(input.toUpperCase())
                    }
                >
                    {originOptionType.map((v) => (
                        <Option key={v.dataInfoId} value={`${v.dataInfoId}`}>
                            {v.dataName}
                            {DATA_SOURCE_VERSION[v.dataTypeCode] && ` (${DATA_SOURCE_VERSION[v.dataTypeCode]})`}
                        </Option>
                    ))}
                </Select>
            </FormItem>
            <FormItem label="Topic" name={[index, 'topic']} rules={validDes.topic}>
                <Select<string>
                    placeholder="请选择Topic"
                    className="right-select"
                    showSearch
                    filterOption={(input, option) =>
                        !!option?.value?.toString().toUpperCase().includes(input.toUpperCase())
                    }
                >
                    {topicOptionType.map((v) => (
                        <Option key={v} value={`${v}`}>
                            {v}
                        </Option>
                    ))}
                </Select>
            </FormItem>
            <FormItem
                label="编码类型"
                style={{ marginBottom: '10px' }}
                name={[index, 'charset']}
                tooltip="编码类型：指Kafka数据的编码类型"
            >
                <Select placeholder="请选择编码类型" className="right-select" showSearch>
                    <Option value={CODE_TYPE.UTF_8}>{CODE_TYPE.UTF_8}</Option>
                    <Option value={CODE_TYPE.GBK_2312}>{CODE_TYPE.GBK_2312}</Option>
                </Select>
            </FormItem>
            <FormItem noStyle dependencies={[[index, 'type']]}>
                {({ getFieldValue }) =>
                    isKafka(getFieldValue(NAME_FIELD)?.[index].type) && (
                        <FormItem
                            {...formItemLayout}
                            label="读取类型"
                            className="right-select"
                            name={[index, 'sourceDataType']}
                            rules={validDes.sourceDataType}
                        >
                            <Select>
                                {getFieldValue(NAME_FIELD)?.[index].type === DATA_SOURCE_ENUM.KAFKA_CONFLUENT ? (
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
                    )
                }
            </FormItem>
            <FormItem noStyle dependencies={[[index, 'sourceDataType']]}>
                {({ getFieldValue }) =>
                    isAvro(getFieldValue(NAME_FIELD)?.[index].sourceDataType) && (
                        <FormItem
                            {...formItemLayout}
                            label="Schema"
                            name={[index, 'schemaInfo']}
                            rules={validDes.schemaInfo}
                        >
                            <Input.TextArea
                                rows={9}
                                placeholder={`填写Avro Schema信息，示例如下：\n{\n\t"name": "testAvro",\n\t"type": "record",\n\t"fields": [{\n\t\t"name": "id",\n\t\t"type": "string"\n\t}]\n}`}
                            />
                        </FormItem>
                    )
                }
            </FormItem>
            <FormItem
                wrapperCol={{
                    sm: {
                        offset: formItemLayout.labelCol.sm.span,
                        span: formItemLayout.wrapperCol.sm.span,
                    },
                }}
            >
                <Button block type="link" onClick={showPreviewModal}>
                    数据预览
                </Button>
            </FormItem>
            <FormItem
                label="映射表"
                name={[index, 'table']}
                rules={validDes.table}
                tooltip="该表是kafka中的topic映射而成，可以以SQL的方式使用它。"
            >
                <Input placeholder="请输入映射表名" className="right-input" />
            </FormItem>
            <FormItem noStyle dependencies={[[index, 'type']]}>
                {({ getFieldValue }) => (
                    <FormItem label="字段" required name={[index, 'columnsText']}>
                        <Editor
                            style={{ minHeight: 202 }}
                            className="bd"
                            options={{
                                fontSize: 12,
                                minimap: {
                                    enabled: false,
                                },
                            }}
                            language="plaintext"
                            placeholder={`字段 类型, 比如 id int 一行一个字段${
                                getFieldValue(NAME_FIELD)?.[index].type !== DATA_SOURCE_ENUM.KAFKA_CONFLUENT
                                    ? '\n\n仅支持JSON格式数据源，若为嵌套格式，\n字段名称由JSON的各层级key组合隔，例如：\n\nkey1.keya INT AS columnName \nkey1.keyb VARCHAR AS columnName'
                                    : ''
                            }`}
                        />
                    </FormItem>
                )}
            </FormItem>
            <FormItem noStyle dependencies={[[index, 'type']]}>
                {({ getFieldValue }) => (
                    <FormItem
                        label="Offset"
                        tooltip={
                            <div>
                                <p>latest：从Kafka Topic内最新的数据开始消费</p>
                                <p>earliest：从Kafka Topic内最老的数据开始消费</p>
                            </div>
                        }
                        name={[index, 'offsetReset']}
                    >
                        <Radio.Group className="right-select">
                            <Row>
                                <Col span={12}>
                                    <Radio value="latest">latest</Radio>
                                </Col>
                                <Col span={12}>
                                    <Radio value="earliest">earliest</Radio>
                                </Col>
                                {isShowTimeForOffsetReset(getFieldValue(NAME_FIELD)?.[index]?.type) && (
                                    <Col span={12}>
                                        <Radio value="timestamp">time</Radio>
                                    </Col>
                                )}
                                <Col span={12}>
                                    <Radio value="custom">自定义参数</Radio>
                                </Col>
                            </Row>
                        </Radio.Group>
                    </FormItem>
                )}
            </FormItem>
            <FormItem noStyle dependencies={[[index, 'offsetReset']]}>
                {({ getFieldValue }) =>
                    getFieldValue(NAME_FIELD)?.[index].offsetReset === 'timestamp' && (
                        <FormItem
                            label="选择时间"
                            name={[index, 'timestampOffset']}
                            rules={[{ required: true, message: '请选择时间' }]}
                        >
                            <DatePicker
                                showTime
                                placeholder="请选择时间"
                                format={'YYYY-MM-DD HH:mm:ss'}
                                style={{ width: '100%' }}
                            />
                        </FormItem>
                    )
                }
            </FormItem>
            <FormItem noStyle shouldUpdate>
                {({ getFieldValue }) =>
                    getFieldValue(NAME_FIELD)?.[index].offsetReset === 'custom' && (
                        <FormItem label="字段" required name={[index, 'offsetValue']}>
                            <Editor
                                style={{ minHeight: 202, height: '100%' }}
                                className="bd"
                                language="plaintext"
                                placeholder="分区 偏移量，比如pt 2 一行一对值"
                            />
                        </FormItem>
                    )
                }
            </FormItem>
            <FormItem
                label="时间特征"
                tooltip={
                    <div>
                        <p>ProcTime：按照Flink的处理时间处理</p>
                        <p>EventTime：按照流式数据本身包含的业务时间戳处理</p>
                        <p>
                            Flink1.12后，基于事件时间的时态表 Join 开发需勾选ProcTime详情可参考
                            <a
                                href="https://ci.apache.org/projects/flink/flink-docs-release-1.12/zh/dev/table/streaming/joins.html"
                                target="_blank"
                                rel="noopener noreferrer"
                            >
                                帮助文档
                            </a>
                        </p>
                    </div>
                }
                name={[index, componentVersion !== FLINK_VERSIONS.FLINK_1_12 ? 'timeType' : 'timeTypeArr']}
            >
                {componentVersion !== FLINK_VERSIONS.FLINK_1_12 ? (
                    <Radio.Group className="right-select">
                        <Radio value={1}>ProcTime</Radio>
                        <Radio value={2}>EventTime</Radio>
                    </Radio.Group>
                ) : (
                    <Checkbox.Group
                        options={[
                            { label: 'ProcTime', value: 1 },
                            { label: 'EventTime', value: 2 },
                        ]}
                    />
                )}
            </FormItem>
            <FormItem noStyle dependencies={[[index, 'timeTypeArr']]}>
                {({ getFieldValue }) =>
                    componentVersion === FLINK_VERSIONS.FLINK_1_12 &&
                    getFieldValue(NAME_FIELD)?.[index].timeTypeArr?.includes?.(SOURCE_TIME_TYPE.PROC_TIME) && (
                        <FormItem
                            label="ProcTime 名称"
                            name={[index, 'procTime']}
                            rules={[{ pattern: /^\w*$/, message: '仅支持输入英文、数字、下划线' }]}
                        >
                            <Input
                                className="right-input"
                                maxLength={64}
                                placeholder="自定义ProcTime名称，为空时默认为 proc_time"
                            />
                        </FormItem>
                    )
                }
            </FormItem>
            <FormItem
                noStyle
                dependencies={[
                    [index, 'timeType'],
                    [index, 'timeTypeArr'],
                    [index, 'columnsText'],
                ]}
            >
                {({ getFieldValue }) =>
                    ((componentVersion !== FLINK_VERSIONS.FLINK_1_12 &&
                        getFieldValue(NAME_FIELD)?.[index].timeType === SOURCE_TIME_TYPE.EVENT_TIME) ||
                        (componentVersion === FLINK_VERSIONS.FLINK_1_12 &&
                            getFieldValue(NAME_FIELD)?.[index].timeTypeArr?.includes?.(
                                SOURCE_TIME_TYPE.EVENT_TIME
                            ))) && (
                        <React.Fragment>
                            <FormItem label="时间列" name={[index, 'timeColumn']} rules={validDes.timeColumn}>
                                <Select
                                    placeholder="请选择"
                                    className="right-select"
                                    showSearch
                                    filterOption={(input: any, option: any) =>
                                        option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0
                                    }
                                >
                                    {getColumnsByColumnsText(getFieldValue(NAME_FIELD)?.[index].columnsText)?.map(
                                        (v) => (
                                            <Option key={v.field} value={v.field}>
                                                {v.field}
                                            </Option>
                                        )
                                    )}
                                </Select>
                            </FormItem>
                            <FormItem
                                label="最大延迟时间"
                                tooltip="当event time超过最大延迟时间时，系统自动丢弃此条数据"
                                name={[index, 'offset']}
                                rules={validDes.offset}
                            >
                                <InputNumber
                                    min={0}
                                    style={{
                                        width: '100%',
                                        height: '32px',
                                    }}
                                    addonAfter={
                                        componentVersion === FLINK_VERSIONS.FLINK_1_12 ? (
                                            <Form.Item name={[index, 'offsetUnit']} noStyle initialValue="SECOND">
                                                <Select className="right-select">
                                                    <Option value="SECOND">sec</Option>
                                                    <Option value="MINUTE">min</Option>
                                                    <Option value="HOUR">hour</Option>
                                                    <Option value="DAY">day</Option>
                                                    <Option value="MONTH">mon</Option>
                                                    <Option value="YEAR">year</Option>
                                                </Select>
                                            </Form.Item>
                                        ) : (
                                            'ms'
                                        )
                                    }
                                />
                            </FormItem>
                        </React.Fragment>
                    )
                }
            </FormItem>
            {/* 高级参数按钮 */}
            <FormItem wrapperCol={{ span: 24 }}>
                <Button block type="link" onClick={() => setShowAdvancedParams(!showAdvancedParams)}>
                    高级参数{showAdvancedParams ? <UpOutlined /> : <DownOutlined />}
                </Button>
            </FormItem>
            {/* 高级参数抽屉 */}
            <FormItem hidden={!showAdvancedParams} noStyle shouldUpdate>
                {() => (
                    <>
                        <FormItem name={[index, 'parallelism']} label="并行度">
                            <InputNumber style={{ width: '100%' }} min={1} />
                        </FormItem>
                        <FormItem
                            label="时区"
                            tooltip="注意：时区设置功能目前只支持时间特征为EventTime的任务"
                            name={[index, 'timeZone']}
                        >
                            <Cascader allowClear={false} placeholder="请选择时区" showSearch options={timeZoneData} />
                        </FormItem>
                        <CustomParams index={index} />
                    </>
                )}
            </FormItem>
            <FormItem noStyle shouldUpdate>
                {({ getFieldValue }) => (
                    <DataPreviewModal
                        visible={visible}
                        type={getFieldValue(NAME_FIELD)?.[index]?.type}
                        onCancel={() => {
                            setVisible(false);
                        }}
                        params={params}
                    />
                )}
            </FormItem>
        </>
    );
}
