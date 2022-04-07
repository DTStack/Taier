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
import { CODE_TYPE, DATA_SOURCE_ENUM, DATA_SOURCE_VERSION, KAFKA_DATA_LIST, KAFKA_DATA_TYPE, SOURCE_TIME_TYPE, TABLE_SOURCE, TABLE_TYPE } from "@/constant";
import { formatSourceTypes } from "@/utils";
import { getFlinkDisabledSource, isAvro, isKafka, mergeSourceType, showTimeForOffsetReset } from "@/utils/enums";
import { Cascader, Checkbox, Col, DatePicker, Form, Input, InputNumber, message, Radio, Row, Select, Tooltip } from "antd";
import { UpOutlined, DownOutlined } from "@ant-design/icons";
import React, { useState } from "react";
import { generateSourceValidDes, inputDefaultValue, parseColumnText } from "../flinkHelper";
import Editor from "@/components/editor";
import { debounce, isString } from "lodash";
import { CustomParams } from "../component/customParams";
import { AssetPanel } from "../component/assetPanel";
import DataPreviewModal from "../../source/dataPreviewModal";
import { formatOffsetResetTime, generateMapValues } from "../customParamsUtil";

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
interface ISourceFormProps {
    createTypes: any[];
    isShow: boolean;
    sync: boolean;
    handleInputChange: (type: any, value: any, subValue?: any) => void;
    panelColumn: any;
    sourceTableTypes: any[];
    dataBaseOptionType: any[];
    getDataBaseList: () => void;
    topicOptionType: any[];
    assetTableOptionType: any[];
    originOptionType: any[];
    timeZoneData: any[];
    currentPage: any;
    textChange: () => void;
}
export default function SourceForm({
    createTypes = [],
    isShow,
    handleInputChange,
    sync,
    panelColumn,
    sourceTableTypes = [],
    dataBaseOptionType = [],
    getDataBaseList,
    currentPage,
    topicOptionType = [],
    assetTableOptionType = [],
    originOptionType = [],
    timeZoneData = [],
    textChange,
}: ISourceFormProps) {
    const [form] = Form.useForm();
    const { getFieldValue } = form;
    const { componentVersion } = currentPage || {};

    const [visible, setVisible] = useState(false);
    const [params, setParams] = useState({});
    const [showAdvancedParams, setShowAdvancedParams] = useState(false)

    const originOption = (type: any, arrData: any) => {
        switch (type) {
            case 'dataSource':
                return arrData.map((v: { name: string; value: number }) => {
                    const allow110List = [DATA_SOURCE_ENUM.TBDS_HBASE, DATA_SOURCE_ENUM.TBDS_KAFKA, DATA_SOURCE_ENUM.KAFKA_HUAWEI, DATA_SOURCE_ENUM.HBASE_HUAWEI];
                    const allow112List = [DATA_SOURCE_ENUM.KAFKA_CONFLUENT]
                    const { ONLY_ALLOW_FLINK_1_10_DISABLED, ONLY_ALLOW_FLINK_1_12_DISABLED } = getFlinkDisabledSource({
                        version: componentVersion,
                        value: v.value,
                        allow110List,
                        allow112List
                    })
                    return <Option key={v.value} value={v.value} disabled={ONLY_ALLOW_FLINK_1_10_DISABLED || ONLY_ALLOW_FLINK_1_12_DISABLED}>{v.name}</Option>
                })
            case 'originType':
                return arrData.map((v: any) => {
                    return <Option key={v} value={`${v.id}`}>{v.name}{DATA_SOURCE_VERSION[v.type as DATA_SOURCE_ENUM] && ` (${DATA_SOURCE_VERSION[v.type as DATA_SOURCE_ENUM]})`}</Option>
                })
            case 'currencyType':
                return arrData.map((v: any) => {
                    return <Option key={v} value={`${v}`}>{v}</Option>
                })
            case 'eventTime':
                return arrData.map((v: any, index: any) => {
                    return <Option key={index} value={`${v.column}`}>{v.column}</Option>
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
    // 获取时间列
    const getEventTimeOptionTypes = () => {
        const { createType, columnsText, columns } = panelColumn;
        if (createType === TABLE_SOURCE.DATA_ASSET) {
            return (columns || []).map((item: any) => ({ column: item.targetCol || item.column, type: item.type }));
        }
        return parseColumnText(columnsText) || []
    }
    const showPreviewModal = () => {
        const { sourceId, topic, createType, tableId } = panelColumn;
        let params = {};
        if (createType === TABLE_SOURCE.DATA_ASSET) {
            if (!tableId) {
                message.error('数据预览需要选择表！')
                return;
            }
            params = {
                tableId,
                tableType: TABLE_TYPE.SOURCE_TABLE
            }
        } else {
            if (!sourceId || !topic) {
                message.error('数据预览需要选择数据源和Topic！')
                return;
            }
            params = { sourceId, topic }
        }
        setVisible(false);
        setParams(params);
    }
    const editorParamsChange = (type: any, a: any, b: any, c: any) => {
        textChange();
        handleInputChange(type, b);
    }

    const debounceEditorChange = debounce(editorParamsChange, 300, { 'maxWait': 2000 })

    const changeTimeTypeArr = (timeTypeArr: any[]) => {
        // 勾选 EventTime 时需同时勾选 ProcTime
        if (!panelColumn?.timeTypeArr?.includes(SOURCE_TIME_TYPE.EVENT_TIME) && timeTypeArr?.includes(SOURCE_TIME_TYPE.EVENT_TIME)) {
            timeTypeArr = [1, 2]
        }
        form.setFieldsValue({ timeTypeArr })
        handleInputChange('timeTypeArr', timeTypeArr)
    }
    // 初始化公用的元素
    const initCommonElements = (validDes: any) => {
        const commonElements = (
            <React.Fragment>
                <FormItem
                    label='编码类型'
                    style={{ marginBottom: '10px' }}
                    name='charset'
                    tooltip='编码类型：指Kafka数据的编码类型'
                >
                    <Select
                        placeholder="请选择编码类型"
                        className="right-select"
                        onChange={(v: any) => { handleInputChange('charset', v) }}
                        showSearch
                    >
                        <Option value={CODE_TYPE.UTF_8}>{CODE_TYPE.UTF_8}</Option>
                        <Option value={CODE_TYPE.GBK_2312}>{CODE_TYPE.GBK_2312}</Option>
                    </Select>
                </FormItem>
                {isKafka(panelColumn.type) && <FormItem
                    {...formItemLayout}
                    label="读取类型"
                    className="right-select"
                    name='sourceDataType'
                    rules={validDes.sourceDataType}
                >
                    <Select
                        disabled={panelColumn?.createType === TABLE_SOURCE.DATA_ASSET}
                        onChange={(v: any) => { handleInputChange('sourceDataType', v) }}
                    >
                        {panelColumn.type === DATA_SOURCE_ENUM.KAFKA_CONFLUENT
                            ? <Option value={KAFKA_DATA_TYPE.TYPE_AVRO_CONFLUENT} key={KAFKA_DATA_TYPE.TYPE_AVRO_CONFLUENT}>{KAFKA_DATA_TYPE.TYPE_AVRO_CONFLUENT}</Option>
                            : KAFKA_DATA_LIST.map(({ text, value }) => <Option value={value} key={text + value}>{text}</Option>)
                        }
                    </Select>
                </FormItem>
                }
                {isAvro(getFieldValue('sourceDataType')) && <FormItem
                    {...formItemLayout}
                    label="Schema"
                    name='schemaInfo'
                    rules={validDes.schemaInfo}
                >
                    <Input.TextArea
                        rows={9}
                        placeholder={`填写Avro Schema信息，示例如下：\n{\n\t"name": "testAvro",\n\t"type": "record",\n\t"fields": [{\n\t\t"name": "id",\n\t\t"type": "string"\n\t}]\n}`}
                        onChange={(e: any) => handleInputChange('schemaInfo', e.target.value)}
                    />
                </FormItem>}
                <Row>
                    <div className="ant-form-item-label ant-col-xs-24 ant-col-sm-6"></div>
                    <Col offset={6} style={{ marginBottom: 12 }}>
                        <a onClick={showPreviewModal}>数据预览</a>
                    </Col>
                </Row>
            </React.Fragment>
        )
        return commonElements;
    }

    const mapPropsToFields = () => {
        const {
            sourceId, type, topic, timeZone, customParams, timestampOffset, sourceDataType,
            dbId, assetsDbName, tableId, assetsTableName
        } = panelColumn;
        const initialTimeZoneValue = timeZone && isString(timeZone) ? timeZone.split('/') : ['Asia', 'Shanghai'];
        const initialSourceIdValue = originOptionType.length ? sourceId : undefined;
        let values = {};
        Object.entries(panelColumn).forEach(([key, value]) => {
            if (inputDefaultValue(key)) {
                values = Object.assign({}, values, {
                    [key]: value
                })
            }
        })
        let initialDbId = (dataBaseOptionType && dataBaseOptionType.length > 0) ? dbId : assetsDbName;
        let initialTableId = (assetTableOptionType && assetTableOptionType.length > 0 ? tableId : assetsTableName)

        return {
            sourceId: initialSourceIdValue,
            type: mergeSourceType(parseInt(type)),
            topic_input: topic,
            timeZone: initialTimeZoneValue,
            timestampOffset: timestampOffset ? formatOffsetResetTime(parseInt(timestampOffset)) : null,
            sourceDataType: sourceDataType,
            ...values,
            ...generateMapValues(customParams),
            dbId: initialDbId,
            tableId: initialTableId
        }
    }

    const dataSourceOptionTypes = originOption('dataSource', formatSourceTypes(sourceTableTypes) || []);
    const originOptionTypes = originOption('originType', originOptionType || []);
    const topicOptionTypes = originOption('currencyType', topicOptionType || []);
    const assetTableOptionTypes = originOption('assetTable', assetTableOptionType || []);
    const dataBaseOptionTypes = originOption('database', dataBaseOptionType || [])
    const eventTimeOptionType = originOption('eventTime', getEventTimeOptionTypes());

    const validDes: any = generateSourceValidDes(panelColumn, componentVersion);
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
                <Radio.Group onChange={(e: any) => { handleInputChange('createType', e.target.value) }}>
                    {Array.isArray(createTypes) && createTypes.map((item: any) => (
                        <Radio key={item.createType} value={item.createType} disabled={!item.valid}>{item.label}</Radio>
                    ))}
                </Radio.Group>
            </FormItem>
            {panelColumn?.createType === TABLE_SOURCE.DATA_ASSET
                ? <AssetPanel
                    panelColumn={panelColumn}
                    handleInputChange={handleInputChange}
                    assetTableOptionTypes={assetTableOptionTypes}
                    dataBaseOptionTypes={dataBaseOptionTypes}
                    getDataBaseList={getDataBaseList}
                    embedElm={initCommonElements(validDes)}
                />
                : <React.Fragment>
                    <FormItem
                        label="类型"
                        name='type'
                        rules={validDes.type}
                    >
                        <Select
                            placeholder="请选择"
                            className="right-select"
                            onChange={(v: any) => { handleInputChange('type', v) }}
                            showSearch
                            filterOption={(input: any, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                        >
                            {dataSourceOptionTypes}
                        </Select>
                    </FormItem>
                    <FormItem
                        label="数据源"
                        name='sourceId'
                        rules={validDes.sourceId}
                    >
                        <Select
                            showSearch
                            placeholder="请选择数据源"
                            className="right-select"
                            onChange={(v: any) => { handleInputChange('sourceId', v) }}
                            filterOption={(input: any, option: any) => option.props.children.toString().toLowerCase().indexOf(input.toLowerCase()) >= 0}
                        >
                            {originOptionTypes}
                        </Select>
                    </FormItem>
                    <FormItem
                        label="Topic"
                        name='topic'
                        rules={validDes.topic}
                    >
                        <Select
                            placeholder="请选择Topic"
                            className="right-select"
                            onChange={(v: any) => { handleInputChange('topic', v) }}
                            showSearch
                            filterOption={(input: any, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}

                        >
                            {
                                topicOptionTypes
                            }
                        </Select>
                    </FormItem>
                    {initCommonElements(validDes)}
                    <FormItem
                        label='映射表'
                        name='table'
                        rules={validDes.table}
                        tooltip='该表是kafka中的topic映射而成，可以以SQL的方式使用它。'
                    >
                        <Input
                            placeholder="请输入映射表名"
                            className="right-input"
                            onChange={(e: any) => handleInputChange('table', e.target.value)}
                        />
                    </FormItem>
                    <Row>
                        <div className="ant-form-item-label ant-col-xs-24 ant-col-sm-6">
                            <label className="required-tip">字段</label>
                        </div>
                        <Col span={18} style={{ marginBottom: 20, height: 202 }}>
                            {isShow && (
                                <Editor
                                    style={{ minHeight: 202, height: '100%', borderRadius: 4 }}
                                    className="bd"
                                    sync={sync}
                                    placeholder={`字段 类型, 比如 id int 一行一个字段${panelColumn?.type !== DATA_SOURCE_ENUM.KAFKA_CONFLUENT
                                        ? '\n\n仅支持JSON格式数据源，若为嵌套格式，\n字段名称由JSON的各层级key组合隔，例如：\n\nkey1.keya INT AS columnName \nkey1.keyb VARCHAR AS columnName'
                                        : ''}`}
                                    value={panelColumn.columnsText}
                                    onChange={debounceEditorChange.bind(undefined, 'columnsText')}
                                // editorRef={(ref: any) => {
                                //     this._editorRef = ref;
                                // }}
                                />
                            )}
                        </Col>
                    </Row>
                </React.Fragment>
            }
            <FormItem
                label='Offset'
                tooltip={<div>
                    <p>latest：从Kafka Topic内最新的数据开始消费</p>
                    <p>earliest：从Kafka Topic内最老的数据开始消费</p>
                </div>}
                name='offsetReset'
            >
                <Radio.Group className="right-select" onChange={(v: any) => { handleInputChange('offsetReset', v.target.value) }}>
                    <Radio value="latest">latest</Radio>
                    <Radio value="earliest">earliest</Radio>
                    {showTimeForOffsetReset(panelColumn.type) && <Radio value="timestamp">time</Radio>}
                    <Radio value="custom">自定义参数</Radio>
                </Radio.Group>
            </FormItem>
            {getFieldValue('offsetReset') === 'timestamp' && (
                <FormItem
                    label="选择时间"
                    style={{ textAlign: 'left' }}
                    name='timestampOffset'
                    rules={[{ required: true, message: '请选择时间' }]}
                >
                    <DatePicker
                        onChange={(v: any) => {
                            handleInputChange('timestampOffset', v.valueOf())
                        }}
                        showTime
                        placeholder="请选择起始时间"
                        format={'YYYY-MM-DD HH:mm:ss'}
                        style={{ width: '100%' }}
                    />
                </FormItem>
            )}
            {getFieldValue('offsetReset') == 'custom' && (<Row>
                <div className="ant-form-item-label ant-col-xs-24 ant-col-sm-6">
                    <label>偏移量</label>
                </div>
                <Col span={18} style={{ marginBottom: 20, height: 202 }}>
                    {isShow && (
                        <Editor
                            style={{ minHeight: 202, height: '100%' }}
                            className="bd"
                            sync={sync}
                            placeholder="分区 偏移量，比如pt 2 一行一对值"
                            value={panelColumn.offsetValue}
                            onChange={debounceEditorChange.bind(undefined, 'offsetValue')}
                        />
                    )}
                </Col>
            </Row>)}
            <FormItem
                label='时间特征'
                tooltip={<div>
                    <p>ProcTime：按照Flink的处理时间处理</p>
                    <p>EventTime：按照流式数据本身包含的业务时间戳处理</p>
                    <p>
                        Flink1.12后，基于事件时间的时态表 Join 开发需勾选ProcTime详情可参考
                        <a
                            href="https://ci.apache.org/projects/flink/flink-docs-release-1.12/zh/dev/table/streaming/joins.html"
                            target="_blank"
                            rel="noopener noreferrer"
                        >帮助文档</a>
                    </p>
                </div>}
                name={componentVersion !== '1.12' ? 'timeType' : 'timeTypeArr'}
            >{componentVersion !== '1.12'
                ? <Radio.Group className="right-select" onChange={(v: any) => { handleInputChange('timeType', v.target.value) }}>
                    <Radio value={1}>ProcTime</Radio>
                    <Radio value={2}>EventTime</Radio>
                </Radio.Group>
                : <Checkbox.Group
                    options={[{ label: 'ProcTime', value: 1 }, { label: 'EventTime', value: 2 }]}
                    onChange={value => { changeTimeTypeArr(value) }}
                />
                }
            </FormItem>
            {componentVersion === '1.12' && panelColumn?.timeTypeArr?.includes?.(SOURCE_TIME_TYPE.PROC_TIME) &&
                <FormItem
                    label="ProcTime 名称"
                    name='procTime'
                    rules={[{ pattern: /^\w*$/, message: '仅支持输入英文、数字、下划线' }]}
                >
                    <Input
                        className="right-input"
                        maxLength={64}
                        placeholder="自定义ProcTime名称，为空时默认为 proc_time"
                        onChange={e => { handleInputChange('procTime', e.target.value) }}
                    />
                </FormItem>
            }
            {((componentVersion !== '1.12' && panelColumn.timeType === SOURCE_TIME_TYPE.EVENT_TIME) ||
                (componentVersion === '1.12' && panelColumn?.timeTypeArr?.includes?.(SOURCE_TIME_TYPE.EVENT_TIME))) &&
                <React.Fragment>
                    <FormItem
                        label="时间列"
                        name='timeColumn'
                        rules={validDes.timeColumn}
                    >
                        <Select
                            placeholder="请选择" className="right-select" onChange={(v: any) => { handleInputChange('timeColumn', v) }}
                            showSearch
                            filterOption={(input: any, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                        >{eventTimeOptionType}</Select>
                    </FormItem>
                    <FormItem
                        label='最大延迟时间'
                        tooltip='当event time超过最大延迟时间时，系统自动丢弃此条数据'
                        name='offset'
                        rules={validDes.offset}
                    >
                        <InputNumber
                            min={0}
                            className="number-input"
                            style={{ width: componentVersion === '1.12' ? '70%' : '90%', height: '32px' }}
                            onChange={(value: any) => handleInputChange('offset', value)}
                            addonAfter={componentVersion === '1.12' ? <Form.Item name="offsetUnit" noStyle initialValue='SECOND'>
                                <Select
                                    className="right-select"
                                    style={{ width: 80 }}
                                    onChange={value => { handleInputChange('offsetUnit', value) }}
                                >
                                    <Option value="SECOND">sec</Option>
                                    <Option value="MINUTE">min</Option>
                                    <Option value="HOUR">hour</Option>
                                    <Option value="DAY">day</Option>
                                    <Option value="MONTH">mon</Option>
                                    <Option value="YEAR">year</Option>
                                </Select>
                            </Form.Item> : 'ms'}
                        />
                    </FormItem>
                </React.Fragment>
            }
            {/* 高级参数按钮 */}
            <div style={{ margin: '12px 0', textAlign: 'center' }}>
                <span
                    style={{ cursor: 'pointer', color: '#666666' }}
                    onClick={() => { setShowAdvancedParams(!showAdvancedParams) }}
                >
                    高级参数&nbsp;
                    {showAdvancedParams ? <UpOutlined /> : <DownOutlined />}
                </span>
            </div>
            {/* 高级参数抽屉 */}
            <div style={{ display: showAdvancedParams ? 'block' : 'none' }}>
                <FormItem name='parallelism' label="并行度">
                    <InputNumber className="number-input" min={1} onChange={(value: any) => handleInputChange('parallelism', value)} />
                </FormItem>
                <FormItem
                    label='时区'
                    tooltip='注意：时区设置功能目前只支持时间特征为EventTime的任务'
                    name='timeZone'
                >
                    <Cascader
                        allowClear={false}
                        onChange={(value: any) => handleInputChange('timeZone', value.join('/'))}
                        placeholder="请选择时区"
                        showSearch
                        options={timeZoneData}
                    />
                </FormItem>
                <CustomParams
                    formItemLayout={formItemLayout}
                    customParams={panelColumn.customParams || []}
                    onChange={(type: any, id?: any, value?: any) => { handleInputChange('customParams', value, { id, type }) }}
                />
            </div>
        </Form>
        <DataPreviewModal
            visible={visible}
            type={panelColumn?.type}
            isAssetCreate={panelColumn?.createType === TABLE_SOURCE.DATA_ASSET}
            onCancel={() => { setVisible(false) }}
            params={params}
        />
    </Row>
}