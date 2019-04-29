import React from 'react';
import moment from 'moment';

import { Form, Select, Radio, Checkbox, DatePicker, Input, Button, Icon } from 'antd';

import { formItemLayout, DATA_SOURCE, CAT_TYPE, collectType } from '../../../../../comm/const'
import HelpDoc from '../../../../helpDoc';
import { isKafka } from '../../../../../comm';

import ajax from '../../../../../api/index';

import DataPreviewModal from '../../dataPreviewModal';

const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;
const CheckboxGroup = Checkbox.Group;

class CollectionSource extends React.Component {
    constructor (props) {
        super(props);
        this.state = {
            tableList: [],
            binLogList: [],
            dataSourceTypes: []
        }
    }
    componentDidMount () {
        const { collectionData } = this.props;
        const { sourceMap = {} } = collectionData;
        this.getSupportDaTypes();
        if (sourceMap.sourceId) {
            if (sourceMap.type === DATA_SOURCE.MYSQL) {
                this.getTableList(sourceMap.sourceId);
            }
            if (sourceMap.collectType == collectType.FILE) {
                this.getBinLogList(sourceMap.sourceId);
            }
        }
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps) {
        const { collectionData } = nextProps;
        const { sourceMap } = collectionData;
        const { collectionData: oldCol } = this.props;
        const { sourceMap: oldSource } = oldCol;
        if (sourceMap.sourceId && oldSource.sourceId != sourceMap.sourceId) {
            if (sourceMap.type === DATA_SOURCE.MYSQL) {
                this.getTableList(sourceMap.sourceId);
            }
        }
        /**
         * 当collectType是File，并且此时collectType发生过改变或者tab的id发生了改变，则去请求binlog列表
         */
        if (
            (sourceMap.collectType != oldSource.collectType || collectionData.id != oldCol.id) &&
            sourceMap.collectType == collectType.FILE) {
            this.getBinLogList(sourceMap.sourceId);
        }
    }

    getSupportDaTypes () {
        ajax.getSupportDaTypes().then(
            (res) => {
                if (res.code == 1) {
                    this.setState({
                        dataSourceTypes: res.data
                    })
                }
            }
        )
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
    getBinLogList (sourceId) {
        ajax.getBinlogListBySource({ sourceId })
            .then((res) => {
                if (res.code == 1) {
                    this.setState({
                        binLogList: res.data
                    })
                }
            })
    }
    clearBinLog () {
        this.setState({
            binLogList: []
        })
    }
    next () {
        this._form.validateFields(null, {}, (err, values) => {
            if (!err) {
                this.props.navtoStep(1)
            }
        })
    }
    render () {
        const { tableList, binLogList, dataSourceTypes } = this.state;
        return (
            <div>
                <WrapCollectionSourceForm
                    ref={(f) => { this._form = f }}
                    dataSourceTypes={dataSourceTypes}
                    binLogList={binLogList}
                    tableList={tableList}
                    {...this.props}
                />
                {!this.props.readonly && (
                    <div className="steps-action">
                        <Button type="primary" onClick={() => this.next()}>下一步</Button>
                    </div>
                )}
            </div>
        )
    }
}

class CollectionSourceForm extends React.Component {
    state = {
        sourceList: [], // TODO 此处 sourceList 跟 MySQL 的并未共用
        topicList: [],
        previewParams: {},
        previewVisible: false
    }

    componentDidMount () {
        const { collectionData } = this.props;
        this.loadKafkaSourceList(collectionData.sourceMap.type);
    }

    loadPreview = () => {
        const { collectionData } = this.props;
        const { sourceMap } = collectionData;
        this.setState({
            previewParams: {
                sourceId: sourceMap.sourceId,
                topic: sourceMap.topic
            },
            previewVisible: true
        })
    }

    onSourceChange = (sourceId) => {
        this.getTopicType(sourceId);
    }

    onSourceTypeChange = (sourceType) => {
        this.loadKafkaSourceList(sourceType);
    }

    loadKafkaSourceList = (sourceType) => {
        if (isKafka(sourceType)) {
            ajax.getTypeOriginData({ type: sourceType }).then(res => {
                if (res.code === 1) {
                    this.setState({
                        sourceList: res.data
                    })
                }
            });
        }
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

    renderByCatType () {
        const { collectionData, form, binLogList } = this.props;
        const { getFieldDecorator } = form;
        const { sourceMap, isEdit } = collectionData;
        const collectTypeValue = sourceMap.collectType
        switch (collectTypeValue) {
            case collectType.ALL: {
                return null
            }
            case collectType.TIME: {
                return <FormItem
                    {...formItemLayout}
                    label="起始时间"
                    style={{ textAlign: 'left' }}
                >
                    {getFieldDecorator('timestamp', {
                        rules: [{
                            required: true, message: '请选择起始时间'
                        }]
                    })(
                        <DatePicker
                            disabled={isEdit}
                            showTime
                            placeholder="请选择起始时间"
                            format="YYYY-MM-DD HH:mm:ss"
                        />
                    )}
                </FormItem>
            }
            case collectType.FILE: {
                return <FormItem
                    {...formItemLayout}
                    label="起始文件"
                >
                    {getFieldDecorator('journalName', {
                        rules: [{
                            required: true, message: '请填写起始文件'
                        }]
                    })(
                        <Select
                            placeholder="请填写起始文件"
                            disabled={isEdit}
                        >
                            {binLogList.map((binlog) => {
                                return <Option key={binlog}>{binlog}</Option>
                            })}
                        </Select>
                    )}
                </FormItem>
            }
        }
    }

    renderForm () {
        let { collectionData, tableList } = this.props;
        let { dataSourceList = [], sourceMap, isEdit } = collectionData;
        if (!sourceMap) return [];
        const { getFieldDecorator } = this.props.form;
        const allTable = sourceMap.allTable;
        const { type, sourceId } = sourceMap;
        const isCollectTypeEdit = !!sourceId;

        switch (type) {
            case DATA_SOURCE.MYSQL: {
                return [
                    <FormItem
                        key="sourceId"
                        {...formItemLayout}
                        label="数据源"
                    >
                        {getFieldDecorator('sourceId', {
                            rules: [{ required: true, message: '请选择数据源' }]
                        })(
                            <Select
                                disabled={isEdit}
                                placeholder="请选择数据源"
                                style={{ width: '100%' }}
                            >
                                {dataSourceList.map((item) => {
                                    if (item.type != type) {
                                        return null
                                    }
                                    return <Option key={item.id} value={item.id}>{item.dataName}</Option>
                                }).filter(Boolean)}
                            </Select>
                        )}
                    </FormItem>,
                    <FormItem
                        key="table"
                        {...formItemLayout}
                        label="表"
                    >
                        {getFieldDecorator('table', {
                            rules: [{
                                required: true, message: '请选择表'
                            }]
                        })(
                            <Select
                                mode="multiple"
                                style={{ width: '100%' }}
                                placeholder="请选择表"

                            >
                                {tableList.length ? [<Option key={-1} value={-1}>全部</Option>].concat(tableList.map(
                                    (table) => {
                                        return <Option disabled={allTable} key={`${table}`} value={table}>
                                            {table}
                                        </Option>
                                    }
                                )) : [<Option key={-1} value={-1}>全部</Option>]}
                            </Select>
                        )}
                    </FormItem>,
                    <FormItem
                        key="collectType"
                        {...formItemLayout}
                        label="采集起点"
                        style={{ textAlign: 'left' }}
                    >
                        {getFieldDecorator('collectType', {
                            rules: [{
                                required: true, message: '请选择采集起点'
                            }]
                        })(
                            <RadioGroup disabled={isEdit || !isCollectTypeEdit}>
                                <Radio value={collectType.ALL}>从任务运行时开始</Radio>
                                <Radio value={collectType.TIME}>按时间选择</Radio>
                                <Radio value={collectType.FILE}>按文件选择</Radio>
                            </RadioGroup>
                        )}
                    </FormItem>,
                    this.renderByCatType(),
                    <FormItem
                        key="cat"
                        {...formItemLayout}
                        label="数据操作"
                    >
                        {getFieldDecorator('cat', {
                            rules: [{
                                required: true, message: '请选择数据操作'
                            }]
                        })(
                            <CheckboxGroup options={
                                [
                                    { label: 'Insert', value: CAT_TYPE.INSERT },
                                    { label: 'Update', value: CAT_TYPE.UPDATE },
                                    { label: 'Delete', value: CAT_TYPE.DELETE }
                                ]
                            }
                            />
                        )}
                    </FormItem>
                ]
            }
            case DATA_SOURCE.BEATS: {
                return [
                    <FormItem
                        key="macAndIp"
                        {...formItemLayout}
                        label="主机名/IP"
                    >
                        {getFieldDecorator('macAndIp', {})(
                            <Input disabled />
                        )}
                    </FormItem>,
                    <FormItem
                        key="port"
                        {...formItemLayout}
                        label="端口"
                    >
                        {getFieldDecorator('port', {
                            rules: [{
                                validator: (rule, value, callback) => {
                                    if (value) {
                                        if (parseInt(value)) {
                                            callback()
                                        } else {
                                            const error = '请输入正确的端口'
                                            callback(error)
                                        }
                                    } else {
                                        callback()
                                    }
                                }
                            }]
                        })(
                            <Input
                                // disabled={isEdit}
                                placeholder="请输入端口"
                                style={{ width: '100%' }}
                            />
                        )}
                        <HelpDoc doc="binlogPortHelp" />
                    </FormItem>
                ]
            }
            case DATA_SOURCE.KAFKA_09:
            case DATA_SOURCE.KAFKA_10: {
                const { topicList, sourceList } = this.state;
                const sourceOptions = sourceList.map(o => {
                    return <Option key={o.id} value={o.id}>{o.name}</Option>
                })
                return [
                    <FormItem
                        {...formItemLayout}
                        key="sourceId"
                        label="数据源"
                    >
                        {getFieldDecorator('sourceId', {
                            rules: [
                                { required: true, message: '请选择数据源' }
                            ]
                        })(
                            <Select
                                showSearch
                                placeholder="请选择数据源"
                                className="right-select"
                                onChange={this.onSourceChange}
                                filterOption={(input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                            >
                                {
                                    sourceOptions
                                }
                            </Select>
                        )}
                    </FormItem>,
                    <FormItem
                        key="topic"
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
                    </FormItem>,
                    <FormItem key="preview">
                        <p className="txt-center">
                            <a
                                style={{ cursor: 'pointer' }}
                                href="javascript:void(0)"
                                onClick={this.loadPreview.bind(this)}
                            >
                                数据预览 <Icon type="down" />
                            </a>
                        </p>
                    </FormItem>
                ];
            }
            default: {
                return null;
            }
        }
    }

    render () {
        let { collectionData, dataSourceTypes = [] } = this.props;
        let { isEdit } = collectionData;
        const { getFieldDecorator } = this.props.form;
        return (
            <div>
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label="数据源类型"
                    >
                        {getFieldDecorator('type', {
                            rules: [{ required: true, message: '请选择数据源类型' }]
                        })(
                            <Select
                                allowClear
                                disabled={isEdit}
                                onChange={this.onSourceTypeChange}
                                placeholder="请选择数据源类型"
                                style={{ width: '100%' }}
                            >
                                {dataSourceTypes.map((item) => {
                                    return <Option key={item.value} value={item.value} >{item.key}</Option>
                                }).filter(Boolean)}
                            </Select>
                        )}
                    </FormItem>
                    {this.renderForm()}
                </Form>
                <DataPreviewModal
                    visible={this.state.previewVisible}
                    onCancel={() => { this.setState({ previewVisible: false, previewParams: {} }) }}
                    params={this.state.previewParams}
                />
            </div>
        )
    }
}

const WrapCollectionSourceForm = Form.create({
    onValuesChange (props, fields) {
        let clear = false;
        /**
         * 数据源类型改变，清空数据源
         */
        if (fields.type != undefined) {
            fields.sourceId = undefined;
            clear = true;
        }
        /**
         * sourceId改变,则清空表
         */
        if (fields.sourceId != undefined) {
            clear = true
        }
        /**
         * moment=>时间戳,并且清除其他的选项
         */
        if (fields.timestamp) {
            fields.timestamp = fields.timestamp.valueOf()
            fields.journalName = null;
        }
        if (fields.journalName) {
            fields.timestamp = null;
        }
        if (fields.collectType != undefined && fields.collectType == collectType.ALL) {
            fields.journalName = null;
            fields.timestamp = null;
        }
        /**
         * 改变table的情况
         * 1.包含全部，则剔除所有其他选项，设置alltable=true
         * 2.不包含全部，设置alltable=false
         */
        if (fields.table) {
            if (fields.table.includes(-1)) {
                fields.table = [];
                fields.allTable = true;
            } else {
                fields.allTable = false;
            }
        }
        props.updateSourceMap(fields, clear);
    },
    mapPropsToFields (props) {
        const { collectionData } = props;
        const sourceMap = collectionData.sourceMap;
        if (!sourceMap) return {};
        return {
            type: {
                value: sourceMap.type
            },
            port: {
                value: sourceMap.port
            },
            sourceId: {
                value: sourceMap.sourceId
            },
            topic: {
                value: sourceMap.topic
            },
            table: {
                value: sourceMap.allTable ? -1 : sourceMap.table
            },
            collectType: {
                value: sourceMap.collectType
            },
            cat: {
                value: sourceMap.cat
            },
            timestamp: {
                value: sourceMap.timestamp ? moment(sourceMap.timestamp) : undefined
            },
            journalName: {
                value: sourceMap.journalName
            },
            macAndIp: {
                value: '任务运行时自动分配，无需手动指定'
            }
        }
    }
})(CollectionSourceForm);

export default CollectionSource;
