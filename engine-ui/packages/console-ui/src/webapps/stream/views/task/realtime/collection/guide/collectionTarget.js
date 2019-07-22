
import React from 'react';

import {
    Form, Select,
    Button, Input, Radio
} from 'antd';

import ajax from '../../../../../api/index'
import { formItemLayout, DATA_SOURCE, DATA_SOURCE_TEXT, writeTableTypes, writeStrategys } from '../../../../../comm/const'
import { isSupportedTargetSource, isKafka, isHive } from '../../../../../comm'
import HelpDoc from '../../../../helpDoc';

const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;

function getSourceInitialField (sourceType, data) {
    const initialFields = { type: sourceType };
    const { sourceMap = {} } = data;
    const isMysqlSource = sourceMap.type == DATA_SOURCE.MYSQL;
    switch (sourceType) {
        case DATA_SOURCE.HDFS: {
            initialFields.fileType = 'orc';
            initialFields.fieldDelimiter = ',';
            initialFields.encoding = 'utf-8';
            initialFields.writeMode = 'insert';
            return initialFields;
        }
        case DATA_SOURCE.HIVE: {
            // eslint-disable-next-line
            initialFields.analyticalRules = '${schema}_${table}';
            initialFields.writeTableType = isMysqlSource ? writeTableTypes.AUTO : writeTableTypes.HAND;
            initialFields.writeStrategy = writeStrategys.TIME;
            initialFields.interval = `${10 * 60 * 1000}`;
            initialFields.writeMode = 'insert';
            return initialFields;
        }
    }
    return initialFields;
}

class CollectionTarget extends React.Component {
    constructor (props) {
        super(props);
        this.state = {
            topicList: [],
            tableList: [],
            partitions: []
        }
    }

    componentDidMount () {
        const { collectionData } = this.props;
        const { targetMap = {} } = collectionData;
        const { sourceId, type, table } = targetMap;
        if (sourceId) {
            this.onSourceIdChange(type, sourceId);
            if (isHive(type) && table) {
                this.onHiveTableChange(sourceId, table);
            }
        }
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps) {
        const { collectionData } = nextProps;
        const { targetMap = {} } = collectionData;
        const { sourceId, type, table } = targetMap;
        const { collectionData: oldCol } = this.props;
        const { targetMap: oldTarget = {} } = oldCol;
        if (sourceId && oldTarget.sourceId != sourceId) {
            this.onSourceIdChange(type, sourceId);
        }
        if (table != oldTarget.table) {
            this.onHiveTableChange(sourceId, table);
        }
    }
    async onHiveTableChange (sourceId, tableName) {
        this.setState({
            partition: []
        })
        if (!sourceId || !tableName) {
            return;
        }
        let res = await ajax.getHivePartitions({
            sourceId,
            tableName
        });
        if (res && res.code == 1) {
            this.setState({
                partitions: res.data
            })
        }
    }
    getTableList (sourceId) {
        ajax.getStreamTablelist({
            sourceId,
            isSys: false
        }).then(res => {
            if (res.code === 1) {
                this.setState({
                    tableList: res.data || []
                });
            }
        });
    }

    onSourceIdChange (type, sourceId) {
        this.setState({
            topicList: [],
            tableList: [],
            partitions: []
        })
        if (isKafka(type)) {
            this.getTopicType(sourceId)
        } else if (isHive(type)) {
            this.getTableList(sourceId);
        }
    }

    onFormValuesChange = () => {
        const { updateCurrentPage } = this.props;
        setTimeout(() => {
            this._form.validateFields(null, {}, (err, values) => {
                let invalidSubmit = false;
                if (err) {
                    invalidSubmit = true;
                }
                updateCurrentPage({
                    invalidSubmit
                });
            });
        }, 200)
    }

    prev () {
        this.props.navtoStep(0)
    }

    next () {
        this._form.validateFields(null, {}, (err, values) => {
            if (!err) {
                this.props.navtoStep(2)
            }
        })
    }

    getTopicType (sourceId) {
        ajax.getTopicType({
            sourceId
        }).then((res) => {
            if (res.data) {
                this.setState({
                    topicList: res.data
                })
            }
        })
    }

    render () {
        const { topicList, tableList, partitions } = this.state;
        return (
            <div>
                <WrapCollectionTargetForm
                    ref={(f) => { this._form = f }}
                    onFormValuesChange={this.onFormValuesChange}
                    topicList={topicList}
                    tableList={tableList}
                    partitions={partitions}
                    {...this.props}
                />
                {!this.props.readonly && (
                    <div className="steps-action">
                        <Button style={{ marginRight: 8 }} onClick={() => this.prev()}>上一步</Button>
                        <Button type="primary" onClick={() => this.next()}>下一步</Button>
                    </div>
                )}
            </div>
        )
    }
}

class CollectionTargetForm extends React.Component {
    onSelectSource = (value, option) => {
        const sourceType = option.props.data.type;
        const initialFields = getSourceInitialField(sourceType, this.props.collectionData);
        /**
         * sourceId 改变,则清空表
         */
        this.props.updateTargetMap(initialFields, true);
    }
    dynamicRender () {
        const { collectionData, topicList, tableList, partitions } = this.props;
        const { isEdit, targetMap = {}, sourceMap = {} } = collectionData;
        const { getFieldDecorator } = this.props.form;
        if (!targetMap || !sourceMap) return [];
        const isOrc = targetMap.fileType == 'orc';
        const isMysqlSource = sourceMap.type == DATA_SOURCE.MYSQL;
        const { writeTableType, writeStrategy, table, writeMode } = targetMap;
        const isWriteStrategyBeTime = writeStrategy == writeStrategys.TIME;

        switch (targetMap.type) {
            case DATA_SOURCE.KAFKA_09:
            case DATA_SOURCE.KAFKA_10:
            case DATA_SOURCE.KAFKA: {
                return (
                    <FormItem
                        {...formItemLayout}
                        label="Topic"
                    >
                        {getFieldDecorator('topic', {
                            rules: [{
                                required: true, message: '请选择topic'
                            }]
                        })(
                            <Select
                                disabled={isEdit}
                                style={{ width: '100%' }}
                                placeholder="请选择topic"

                            >
                                {topicList.map(
                                    (topic) => {
                                        return <Option key={`${topic}`} value={topic}>
                                            {topic}
                                        </Option>
                                    }
                                )}
                            </Select>
                        )}
                    </FormItem>
                )
            }
            case DATA_SOURCE.HDFS: {
                return [
                    <FormItem
                        {...formItemLayout}
                        label="路径"
                        key="path"
                    >
                        {getFieldDecorator('path', {
                            rules: [{
                                required: true
                            }]
                        })(
                            <Input placeholder="例如: /app/batch" />
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="文件名"
                        key="fileName"
                    >
                        {getFieldDecorator('fileName', {
                            rules: [{
                                required: true
                            }]
                        })(
                            <Input placeholder="文件名" />
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="文件类型"
                        key="fileType"
                    >
                        {getFieldDecorator('fileType', {
                            rules: [{
                                required: true
                            }]
                        })(
                            <Select>
                                <Option value="orc">orc</Option>
                                <Option value="text">text</Option>
                            </Select>
                        )}
                    </FormItem>,
                    !isOrc && (<FormItem
                        {...formItemLayout}
                        label="列分隔符"
                        key="fieldDelimiter"
                    >
                        {getFieldDecorator('fieldDelimiter', {
                            rules: []
                        })(
                            <Input
                                /* eslint-disable-next-line */
                                placeholder="例如: 目标为hive则 分隔符为\001" />
                        )}
                        <HelpDoc doc="splitCharacter" />
                    </FormItem>),
                    !isOrc && (
                        <FormItem
                            {...formItemLayout}
                            label="编码"
                            key="encoding"
                        >
                            {getFieldDecorator('encoding', {
                                rules: [{
                                    required: true
                                }]
                            })(
                                <Select>
                                    <Option value="utf-8">utf-8</Option>
                                    <Option value="gbk">gbk</Option>
                                </Select>
                            )}
                        </FormItem>
                    ),
                    <FormItem
                        {...formItemLayout}
                        label="写入模式"
                        className="txt-left"
                        key="writeMode-hdfs"
                    >
                        {getFieldDecorator('writeMode', {
                            rules: [{
                                required: true
                            }]
                        })(
                            <RadioGroup>
                                <Radio disabled value="NONCONFLICT" style={{ float: 'left' }}>
                                    覆盖（Insert Overwrite）
                                </Radio>
                                <Radio value="APPEND" style={{ float: 'left' }}>
                                    追加（Insert Into）
                                </Radio>
                            </RadioGroup>
                        )}
                    </FormItem>
                ].filter(Boolean);
            }
            case DATA_SOURCE.HIVE: {
                return [
                    <FormItem
                        {...formItemLayout}
                        label="写入表"
                        key="writeTableType"
                    >
                        {getFieldDecorator('writeTableType', {
                            rules: [{
                                required: true
                            }]
                        })(
                            <RadioGroup onChange={this.getTableList}>
                                {isMysqlSource ? (
                                    <Radio key={writeTableTypes.AUTO} value={writeTableTypes.AUTO} style={{ float: 'left' }}>
                                        自动建表
                                    </Radio>
                                ) : null}
                                <Radio key={writeTableTypes.HAND} value={writeTableTypes.HAND} style={{ float: 'left' }}>
                                    手动选择
                                </Radio>
                            </RadioGroup>
                        )}
                    </FormItem>,
                    writeTableType == writeTableTypes.AUTO && (
                        <FormItem
                            {...formItemLayout}
                            label="表名拼装规则"
                            key="analyticalRules"
                        >
                            {getFieldDecorator('analyticalRules', {
                                rules: [{
                                    required: false, message: '该字段不能为空'
                                }]
                            })(
                                // eslint-disable-next-line
                                <Input addonBefore='stream_${schema}_${table}' />
                            )}
                            <HelpDoc overlayClassName='big-tooltip' doc='analyticalRules' />
                        </FormItem>
                    ),
                    writeTableType == writeTableTypes.HAND && (
                        <FormItem
                            {...formItemLayout}
                            label="表"
                            key="table"
                        >
                            {getFieldDecorator('table', {
                                rules: [{
                                    required: true, message: '请选择表'
                                }]
                            })(
                                <Select placeholder='请选择表'>
                                    {tableList.map((tableName) => {
                                        return <Option key={tableName} value={tableName}>{tableName}</Option>
                                    })}
                                </Select>
                            )}
                        </FormItem>
                    ),
                    (writeTableType == writeTableTypes.HAND && table && partitions && partitions.length) && (
                        <FormItem
                            {...formItemLayout}
                            label="分区"
                            key="partition"
                        >
                            {getFieldDecorator('partition', {
                                rules: [{
                                    required: true, message: '请选择分区'
                                }]
                            })(
                                <Select>
                                    {partitions.map((partition) => {
                                        return <Option key={partition} value={partition}>{partition}</Option>
                                    })}
                                </Select>
                            )}
                        </FormItem>
                    ),
                    <FormItem
                        {...formItemLayout}
                        label="写入策略"
                        key="writeStrategy"
                    >
                        {getFieldDecorator('writeStrategy', {
                            rules: [{
                                required: true, message: '请选择写入策略'
                            }]
                        })(
                            <Select>
                                <Option value={writeStrategys.TIME}>按时间</Option>
                                <Option value={writeStrategys.FILESIZE}>按文件大小</Option>
                            </Select>
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label={isWriteStrategyBeTime ? '间隔时间' : '文件大小'}
                        key={isWriteStrategyBeTime ? 'interval' : 'bufferSize'}
                    >
                        {getFieldDecorator(isWriteStrategyBeTime ? 'interval' : 'bufferSize', {
                            rules: [{
                                required: true, message: isWriteStrategyBeTime ? '请输入间隔时间' : '请输入文件大小'
                            }, {
                                validator: (rule, value, callback) => {
                                    let errorMsg;
                                    try {
                                        value = parseFloat(value);
                                        if (value <= 0) {
                                            errorMsg = '数字必须大于0'
                                        }
                                    } catch (e) {
                                        errorMsg = '请填写大于0的有效数字'
                                    } finally {
                                        callback(errorMsg);
                                    }
                                }
                            }]
                        })(
                            <Input type='number' addonBefore='每隔' addonAfter={`${isWriteStrategyBeTime ? '分钟' : 'MB'}，写入一次`} />
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="写入模式"
                        className="txt-left"
                        key="writeMode"
                    >
                        {getFieldDecorator('writeMode', {
                            rules: [{
                                required: true
                            }]
                        })(
                            <RadioGroup>
                                <Radio value="replace" style={{ float: 'left' }}>
                                    覆盖（Insert Overwrite）
                                </Radio>
                                <Radio value="insert" style={{ float: 'left' }}>
                                    追加（Insert Into）
                                </Radio>
                            </RadioGroup>
                        )}
                        {writeMode == 'replace' && <p style={{ color: 'red' }}>注意：Overwrite 模式将会删除表和数据！</p>}
                    </FormItem>
                ].filter(Boolean);
            }
            default: {
                return null;
            }
        }
    }

    render () {
        const { collectionData } = this.props;
        const { dataSourceList = [], isEdit, sourceMap = {} } = collectionData;
        const { getFieldDecorator } = this.props.form;
        const disableOption = (targetSourceType) => {
            const sourceType = sourceMap.type;
            // 源类型为Kafka时，目标仅能选择HDFS, Hive类型
            if (isKafka(sourceType)) {
                return targetSourceType !== DATA_SOURCE.HDFS && targetSourceType !== DATA_SOURCE.HIVE;
            }
            return false;
        }
        return (
            <div>
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label="数据源"
                    >
                        {getFieldDecorator('sourceId', {
                            rules: [{ required: true, message: '请选择数据源' }]
                        })(
                            <Select
                                disabled={isEdit}
                                placeholder="请选择数据源"
                                onSelect={this.onSelectSource}
                                style={{ width: '100%' }}
                                allowClear
                            >
                                {dataSourceList.map((item) => {
                                    if (!isSupportedTargetSource(item.type)) {
                                        return null
                                    }
                                    return <Option
                                        key={item.id}
                                        data={item}
                                        value={item.id}
                                        disabled={disableOption(item.type)}
                                    >
                                        {item.dataName}({DATA_SOURCE_TEXT[item.type]})
                                    </Option>
                                }).filter(Boolean)}
                            </Select>
                        )}
                    </FormItem>
                    {this.dynamicRender()}
                </Form>
            </div>
        )
    }
}

const WrapCollectionTargetForm = Form.create({
    onValuesChange (props, fields) {
        if (fields.hasOwnProperty('analyticalRules')) {
            // eslint-disable-next-line
            fields['analyticalRules'] = '${schema}_${table}' + fields['analyticalRules'];
        }
        // 建表模式
        if (fields.hasOwnProperty('writeTableType')) {
            if (fields['writeTableType'] == writeTableTypes.AUTO) {
                // eslint-disable-next-line
                fields['analyticalRules'] = '${schema}_${table}';
            } else {
                fields['analyticalRules'] = undefined;
            }
            fields['table'] = undefined;
            fields['partition'] = undefined;
        }
        // 写入表
        if (fields.hasOwnProperty('table')) {
            fields['partition'] = undefined;
        }
        if (fields['interval']) {
            fields['interval'] = fields['interval'] * 60 * 1000;
        }
        if (fields['bufferSize']) {
            fields['bufferSize'] = fields['bufferSize'] * 1024 * 1024;
        }
        // 写入策略
        if (fields.hasOwnProperty('writeStrategy')) {
            if (fields.writeStrategy == writeStrategys.TIME) {
                fields['interval'] = `${10 * 60 * 1000}`;
                fields['bufferSize'] = undefined;
            } else {
                fields['bufferSize'] = `${10 * 1024 * 1024}`;
                fields['interval'] = undefined;
            }
        }
        props.updateTargetMap(fields, false);
        if (props.onFormValuesChange) {
            props.onFormValuesChange(props, fields);
        }
    },
    mapPropsToFields (props) {
        const { collectionData } = props;
        const targetMap = collectionData.targetMap;
        if (!targetMap) return {};

        return {
            sourceId: {
                value: targetMap.sourceId
            },
            topic: {
                value: targetMap.topic
            },
            analyticalRules: {
                // eslint-disable-next-line
                value: targetMap.analyticalRules ? targetMap.analyticalRules.replace('${schema}_${table}', '') : ''
            },
            writeTableType: {
                value: targetMap.writeTableType
            },
            table: {
                value: targetMap.table
            },
            partition: {
                value: targetMap.partition
            },
            writeStrategy: {
                value: targetMap.writeStrategy
            },
            bufferSize: {
                value: targetMap.bufferSize / (1024 * 1024)
            },
            interval: {
                value: targetMap.interval / 60000
            },
            writeMode: {
                value: targetMap.writeMode
            },
            encoding: {
                value: targetMap.encoding
            },
            fieldDelimiter: {
                value: targetMap.fieldDelimiter
            },
            fileType: {
                value: targetMap.fileType
            },
            fileName: {
                value: targetMap.fileName
            },
            path: {
                value: targetMap.path
            }
        }
    }
})(CollectionTargetForm);

export default CollectionTarget;
