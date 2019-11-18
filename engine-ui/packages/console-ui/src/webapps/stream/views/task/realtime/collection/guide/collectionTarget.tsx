
import * as React from 'react';

import {
    Form, Select,
    Button, Input, Radio, notification
} from 'antd';

import ajax from '../../../../../api/index'
import { formItemLayout, DATA_SOURCE, DATA_SOURCE_TEXT, writeTableTypes, writeStrategys, partitionTypes } from '../../../../../comm/const'
import { isSupportedTargetSource, isKafka, isHive } from '../../../../../comm'
import HelpDoc from '../../../../helpDoc';

const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;

// eslint-disable-next-line
const prefixRule = '${schema}_${table}';

function getSourceInitialField (sourceType: any, data: any) {
    const initialFields: any = { type: sourceType };
    const { sourceMap = {} } = data;
    const isMysqlSource = sourceMap.type == DATA_SOURCE.MYSQL;
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
            initialFields.partitionType = partitionTypes.DAY;
            initialFields.analyticalRules = isMysqlSource ? prefixRule : undefined;
            initialFields.partition = isMysqlSource ? 'pt' : undefined; // 后端（nanqi）要求自动建表默认加一个partition = pt。
            initialFields.writeTableType = isMysqlSource ? writeTableTypes.AUTO : writeTableTypes.HAND;
            initialFields.writeStrategy = writeStrategys.FILESIZE;
            initialFields.bufferSize = `${10 * 1024 * 1024}`;
            initialFields.writeMode = 'insert';
            return initialFields;
        }
    }
    return initialFields;
}

class CollectionTarget extends React.Component<any, any> {
    _form: any;
    constructor (props: any) {
        super(props);
        this.state = {
            topicList: [],
            tableList: [],
            partitions: [],
            loading: false
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
    UNSAFE_componentWillReceiveProps (nextProps: any) {
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
    async onHiveTableChange (sourceId: any, tableName: any) {
        this.setState({
            partition: []
        })
        if (!sourceId || !tableName) {
            return;
        }
        this.setState({
            loading: true
        })
        let res = await ajax.getHivePartitions({
            sourceId,
            tableName
        });
        this.setState({
            loading: false
        })
        if (res && res.code == 1) {
            const partitions = res.data;
            if (partitions && partitions.length) {
                let pt = partitions.find((p: any) => {
                    return p == 'pt'
                });
                if (pt) {
                    this.setState({
                        partitions: res.data
                    });
                    this.props.updateTargetMap({
                        partition: 'pt'
                    }, false, true);
                    return;
                }
            }
            notification.error({
                message: '提示',
                description: '目标表必须以pt作为分区字段，且string作为分区字段类型',
                duration: 5
            });
            this.props.updateTargetMap({
                table: null
            }, false, true);
        }
    }
    getTableList (sourceId: any) {
        ajax.getStreamTablelist({
            sourceId,
            isSys: false
        }).then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    tableList: res.data || []
                });
            }
        });
    }

    onSourceIdChange (type: any, sourceId: any) {
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
            this._form.validateFields(null, {}, (err: any, values: any) => {
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
        this._form.validateFields(null, {}, (err: any, values: any) => {
            if (!err) {
                this.props.navtoStep(2)
            }
        })
    }

    getTopicType (sourceId: any) {
        ajax.getTopicType({
            sourceId
        }).then((res: any) => {
            if (res.data) {
                this.setState({
                    topicList: res.data
                })
            }
        })
    }

    render () {
        const { topicList, tableList, partitions, loading } = this.state;
        return (
            <div>
                <WrapCollectionTargetForm
                    ref={(f: any) => { this._form = f }}
                    onFormValuesChange={this.onFormValuesChange}
                    topicList={topicList}
                    tableList={tableList}
                    partitions={partitions}
                    {...this.props}
                />
                {!this.props.readonly && (
                    <div className="steps-action">
                        <Button style={{ marginRight: 8 }} onClick={() => this.prev()}>上一步</Button>
                        <Button loading={loading} type="primary" onClick={() => this.next()}>下一步</Button>
                    </div>
                )}
            </div>
        )
    }
}

class CollectionTargetForm extends React.Component<any, any> {
    getTableList: any;
    onSelectSource = (value: any, option: any) => {
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
        const isText = targetMap.fileType == 'text';
        const isMysqlSource = sourceMap.type == DATA_SOURCE.MYSQL;
        const { writeTableType, writeStrategy, table, writeMode } = targetMap;
        const isWriteStrategyBeTime = writeStrategy == writeStrategys.TIME;

        switch (targetMap.type) {
            case DATA_SOURCE.KAFKA:
            case DATA_SOURCE.KAFKA_09:
            case DATA_SOURCE.KAFKA_10:
            case DATA_SOURCE.KAFKA_11: {
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
                                disabled={isEdit && targetMap.type === DATA_SOURCE.KAFKA}
                                style={{ width: '100%' }}
                                placeholder="请选择topic"

                            >
                                {topicList.map(
                                    (topic: any) => {
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
                            <RadioGroup>
                                <Radio value="orc">
                                    orc
                                </Radio>
                                <Radio value="text">
                                    text
                                </Radio>
                                <Radio value="parquet">
                                    parquet
                                </Radio>
                            </RadioGroup>
                        )}
                    </FormItem>,
                    isText && (<FormItem
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
                    isText && (
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
                                <Radio value="NONCONFLICT" disabled style={{ float: 'left' }}>
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
                            <RadioGroup disabled onChange={this.getTableList}>
                                {isMysqlSource ? (
                                    <Radio key={writeTableTypes.AUTO} value={writeTableTypes.AUTO} style={{ float: 'left' }}>
                                        自动建表
                                    </Radio>
                                ) : null}
                                <Radio key={writeTableTypes.HAND} value={writeTableTypes.HAND} style={{ float: 'left' }}>
                                    手动选择分区表
                                </Radio>
                            </RadioGroup>
                        )}
                        <HelpDoc overlayClassName='big-tooltip' doc='writeTableType' />
                    </FormItem>,
                    writeTableType == writeTableTypes.AUTO && (
                        <React.Fragment>
                            <FormItem
                                {...formItemLayout}
                                label="表名拼装规则"
                                key="analyticalRules"
                            >
                                {getFieldDecorator('analyticalRules', {
                                    rules: [{
                                        required: false, message: '该字段不能为空'
                                    }, {
                                        pattern: /^[^.&%\s]*$/,
                                        message: '不能包含空格、小数点等特殊字符，需符合Hive表建表规范'
                                    }]
                                })(
                                    <Input addonBefore={`stream_${prefixRule}`} />
                                )}
                                <HelpDoc overlayClassName='big-tooltip' doc='analyticalRules' />
                            </FormItem>
                            <FormItem
                                {...formItemLayout}
                                label="存储类型"
                                key="fileType"
                            >
                                {getFieldDecorator('fileType', {
                                    rules: [{
                                        required: true, message: '存储类型不能为空'
                                    }]
                                })(
                                    <RadioGroup>
                                        <Radio value="orc" style={{ float: 'left' }}>
                                            orc
                                        </Radio>
                                        <Radio value="text" style={{ float: 'left' }}>
                                            text
                                        </Radio>
                                        <Radio value="parquet" style={{ float: 'left' }}>
                                            parquet
                                        </Radio>
                                    </RadioGroup>
                                )}
                            </FormItem>
                        </React.Fragment>
                    ),
                    <FormItem
                        {...formItemLayout}
                        label="分区粒度"
                        key="partitionType"
                    >
                        {getFieldDecorator('partitionType', {
                            rules: [{
                                required: false, message: '该字段不能为空'
                            }]
                        })(
                            <RadioGroup>
                                <Radio value={partitionTypes.DAY} style={{ float: 'left' }}>
                                    天
                                </Radio>
                                <Radio value={partitionTypes.HOUR} style={{ float: 'left' }}>
                                    小时
                                </Radio>
                            </RadioGroup>
                        )}
                        <HelpDoc overlayClassName='big-tooltip' doc='partitionType' />
                    </FormItem>,
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
                                <Select showSearch placeholder='请选择表'>
                                    {tableList.map((tableName: any) => {
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
                                <Select disabled>
                                    {partitions.map((partition: any) => {
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
                                {/* <Option value={writeStrategys.TIME}>按时间</Option> */}
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
                                validator: (rule: any, value: any, callback: any) => {
                                    let errorMsg: any;
                                    try {
                                        value = parseFloat(value);
                                        if (value <= 0) {
                                            errorMsg = '数字必须大于0'
                                        } else if (value != parseInt(value, 10)) {
                                            errorMsg = '必须为整数'
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
                            <RadioGroup >
                                <Radio value="insert" style={{ float: 'left' }}>
                                    追加（Insert Into）
                                </Radio>
                            </RadioGroup>
                        )}
                        {writeMode == 'replace' && <p style={{ color: 'red' }}>注意：切换为覆盖模式，任务启动时，将删除目标表和历史数据</p>}
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
        const disableOption = (targetSourceType: any) => {
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
                                {dataSourceList.map((item: any) => {
                                    if (!isSupportedTargetSource(item.type)) {
                                        return null
                                    }
                                    const dataFix = {
                                        data: item
                                    }
                                    return <Option
                                        key={item.id}
                                        // data={item}
                                        value={item.id}
                                        disabled={disableOption(item.type)}
                                        {...dataFix}
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
    onValuesChange (props: any, fields: any) {
        if (fields.hasOwnProperty('analyticalRules')) {
            if (fields['analyticalRules']) {
                if (fields['analyticalRules'][0] == '_') {
                    fields['analyticalRules'] = prefixRule + fields['analyticalRules'];
                } else {
                    fields['analyticalRules'] = prefixRule + '_' + fields['analyticalRules'];
                }
            } else {
                fields['analyticalRules'] = prefixRule
            }
        }
        // 建表模式
        if (fields.hasOwnProperty('writeTableType')) {
            if (fields['writeTableType'] == writeTableTypes.AUTO) {
                // eslint-disable-next-line
                fields['fileType'] = 'orc';
                fields['analyticalRules'] = prefixRule;
            } else {
                fields['analyticalRules'] = undefined;
                fields['fileType'] = undefined;
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
    mapPropsToFields (props: any) {
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
                value: targetMap.analyticalRules ? targetMap.analyticalRules.replace(prefixRule, '') : ''
            },
            writeTableType: {
                value: targetMap.writeTableType
            },
            table: {
                value: targetMap.table
            },
            fileType: {
                value: targetMap.fileType || 'orc'
            },
            partition: {
                value: targetMap.partition
            },
            writeStrategy: {
                value: targetMap.writeStrategy
            },
            bufferSize: {
                value: Number.isNaN(parseInt(targetMap.bufferSize)) ? undefined : targetMap.bufferSize / (1024 * 1024)
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
            fileName: {
                value: targetMap.fileName
            },
            path: {
                value: targetMap.path
            },
            partitionType: {
                value: targetMap.partitionType
            }
        }
    }
})(CollectionTargetForm);

export default CollectionTarget;
