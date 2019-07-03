
import React from 'react';

import {
    Form, Select,
    Button, Input, Radio
} from 'antd';

import ajax from '../../../../../api/index'
import { formItemLayout, DATA_SOURCE, DATA_SOURCE_TEXT } from '../../../../../comm/const'
import { isSupportedTargetSource, isKafka, isHive } from '../../../../../comm'
import HelpDoc from '../../../../helpDoc';

const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;

function getSourceInitialField (sourceType) {
    const initialFields = { type: sourceType };
    switch (sourceType) {
        case DATA_SOURCE.HDFS: {
            initialFields.fileType = 'orc';
            initialFields.fieldDelimiter = ',';
            initialFields.encoding = 'utf-8';
            initialFields.writeMode = 'APPEND';
            return initialFields;
        }
        case DATA_SOURCE.HIVE: {
            // eslint-disable-next-line
            initialFields.sourceColumn = '${table}';
            initialFields.writeTableType = 'custom';
            initialFields.writeMethod = 'time';
            initialFields.interval = 10;
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
            tableList: []
        }
    }

    componentDidMount () {
        const { collectionData } = this.props;
        const { targetMap = {} } = collectionData;
        if (targetMap.sourceId) {
            this.onSourceIdChange(targetMap.type, targetMap.sourceId)
        }
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps) {
        const { collectionData } = nextProps;
        const { targetMap = {} } = collectionData;
        const { collectionData: oldCol } = this.props;
        const { targetMap: oldTarget = {} } = oldCol;
        if (targetMap.sourceId && oldTarget.sourceId != targetMap.sourceId) {
            this.onSourceIdChange(targetMap.type, targetMap.sourceId);
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
            tableList: []
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
        const { topicList, tableList } = this.state;
        return (
            <div>
                <WrapCollectionTargetForm
                    ref={(f) => { this._form = f }}
                    onFormValuesChange={this.onFormValuesChange}
                    topicList={topicList}
                    tableList={tableList}
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
        const initialFields = getSourceInitialField(sourceType);
        /**
         * sourceId 改变,则清空表
         */
        this.props.updateTargetMap(initialFields, true);
    }
    dynamicRender () {
        const { collectionData, topicList, tableList } = this.props;
        const { isEdit, targetMap = {}, sourceMap = {} } = collectionData;
        const { getFieldDecorator } = this.props.form;
        if (!targetMap || !sourceMap) return [];
        const isOrc = targetMap.fileType == 'orc';
        const isMysqlSource = sourceMap.type == DATA_SOURCE.MYSQL;
        const { writeTableType } = targetMap;

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
                                    <Radio disabled value="auto" style={{ float: 'left' }}>
                                        自动建表
                                    </Radio>
                                ) : null}
                                <Radio value="custom" style={{ float: 'left' }}>
                                    手动选择
                                </Radio>
                            </RadioGroup>
                        )}
                    </FormItem>,
                    writeTableType == 'auto' && (
                        <FormItem
                            {...formItemLayout}
                            label="k-v解析"
                            key="sourceColumn"
                        >
                            {getFieldDecorator('sourceColumn', {
                                rules: [{
                                    required: true, message: '该字段不能为空'
                                }]
                            })(
                                <Input />
                            )}
                        </FormItem>
                    ),
                    writeTableType == 'custom' && (
                        <FormItem
                            {...formItemLayout}
                            label="表"
                            key="targetTable"
                        >
                            {getFieldDecorator('targetTable', {
                                rules: [{
                                    required: true, message: '请选择表'
                                }]
                            })(
                                <Select>
                                    {tableList.map((tableName) => {
                                        return <Option key={tableName} value={tableName}>{tableName}</Option>
                                    })}
                                </Select>
                            )}
                        </FormItem>
                    ),
                    writeTableType == 'custom' && (
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

                                </Select>
                            )}
                        </FormItem>
                    ),
                    <FormItem
                        {...formItemLayout}
                        label="写入策略"
                        key="writeMethod"
                    >
                        {getFieldDecorator('writeMethod', {
                            rules: [{
                                required: true, message: '请选择写入策略'
                            }]
                        })(
                            <Select>
                                <Option value='time'>按时间</Option>
                                <Option value='size'>按文件大小</Option>
                            </Select>
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="间隔时间"
                        key="interval"
                    >
                        {getFieldDecorator('interval', {
                            rules: [{
                                required: true, message: '请输入间隔时间'
                            }]
                        })(
                            <Select>
                                {[10, 20, 30, 40, 50, 60].map((time) => {
                                    return <Option key={time} value={time}>{time}</Option>
                                })}
                            </Select>
                        )}
                    </FormItem>,
                    <FormItem
                        {...formItemLayout}
                        label="文件大小"
                        key="fileSize"
                    >
                        {getFieldDecorator('fileSize', {
                            rules: [{
                                required: true, message: '请输入文件大小'
                            }]
                        })(
                            <Select>
                                {[5, 10, 20, 30, 40, 50].map((size) => {
                                    return <Option key={size} value={size}>{size}</Option>
                                })}
                            </Select>
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
            // 源类型为Kafka时，目标仅能选择HDFS类型
            if (sourceType == DATA_SOURCE.KAFKA_09 || sourceType == DATA_SOURCE.KAFKA_10) {
                return targetSourceType !== DATA_SOURCE.HDFS;
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
        // 建表模式
        if (fields.hasOwnProperty('writeTableType')) {
            fields['targetTable'] = undefined;
            fields['partition'] = undefined;
            // eslint-disable-next-line
            fields['sourceColumn'] = '${table}';
        }
        // 写入表
        if (fields.hasOwnProperty('targetTable')) {
            fields['partition'] = undefined;
        }
        // 写入策略
        if (fields.hasOwnProperty('writeMethod')) {
            let writeMethod = fields.writeMethod;
            if (writeMethod == 'time') {
                fields['fileSize'] = undefined;
                fields['interval'] = 10;
            } else {
                fields['fileSize'] = 10;
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
            sourceColumn: {
                value: targetMap.sourceColumn
            },
            writeTableType: {
                value: targetMap.writeTableType
            },
            targetTable: {
                value: targetMap.targetTable
            },
            partition: {
                value: targetMap.partition
            },
            writeMethod: {
                value: targetMap.writeMethod
            },
            interval: {
                value: targetMap.interval
            },
            fileSize: {
                value: targetMap.fileSize
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
