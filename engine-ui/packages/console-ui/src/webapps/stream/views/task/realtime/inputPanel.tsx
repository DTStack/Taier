import * as React from 'react'
import {
    Row, Col, Icon, Tooltip, Input, Select, message,
    Collapse, Button, Radio, Popover, Form, InputNumber, Cascader
} from 'antd';

import { debounce, cloneDeep } from 'lodash';

import Api from '../../../api';
import * as BrowserAction from '../../../store/modules/realtimeTask/browser'
import { DATA_SOURCE_TEXT, DATA_SOURCE } from '../../../comm/const'
import { CustomParams, generateMapValues, changeCustomParams, initCustomParam } from './sidePanel/customParams';

import Editor from 'widgets/code-editor'
import DataPreviewModal from './dataPreviewModal';
import LockPanel from '../../../components/lockPanel';

const Option = Select.Option;
const Panel = Collapse.Panel;
const RadioGroup = Radio.Group;

const FormItem = Form.Item;

class InputOrigin extends React.Component<any, any> {
    _editorRef: any;
    constructor (props: any) {
        super(props)
        this.state = {
            visible: false,
            params: {} // 数据预览请求参数
        };
    }
    componentDidMount () {
        this.props.onRef(this);
    }
    refreshEditor () {
        if (this._editorRef) {
            this._editorRef.refresh();
        }
    }

    checkParams = () => {
        // 手动检测table参数
        let result: any = {};
        this.props.form.validateFields((err: any, values: any) => {
            if (!err) {
                const { panelColumn, index } = this.props;
                const data = panelColumn[index];
                if (!data.columnsText || !data.columnsText.trim()) {
                    result.status = false;
                    result.message = '字段信息不能为空！'
                    return;
                }
                result.status = true;
            } else {
                result.status = false;
            }
        });
        return result
    }

    showPreviewModal = () => {
        const { index, panelColumn } = this.props;
        const sourceId = panelColumn[index].sourceId;
        const topic = panelColumn[index].topic;
        if (!sourceId || !topic) {
            message.error('数据预览需要选择数据源和Topic！')
            return;
        }
        this.setState({
            visible: true,
            params: {
                sourceId,
                topic
            }
        })
    }

    originOption = (type: any, arrData: any) => {
        switch (type) {
            case 'originType':
                return arrData.map((v: any) => {
                    return <Option key={v} value={`${v.id}`}>{v.name}</Option>
                })
            case 'currencyType':
                return arrData.map((v: any) => {
                    return <Option key={v} value={`${v}`}>{v}</Option>
                })
            case 'eventTime':
                return arrData.map((v: any, index: any) => {
                    return <Option key={index} value={`${v.column}`}>{v.column}</Option>
                })
            default:
                return null;
        }
    }

    editorParamsChange (type: any, a: any, b: any, c: any) {
        const { handleInputChange, textChange, index } = this.props;
        textChange();
        handleInputChange(type, index, b);
    }

    debounceEditorChange = debounce(this.editorParamsChange, 300, { 'maxWait': 2000 })

    render () {
        const { handleInputChange, index, panelColumn, sync, timeColumoption = [], originOptionType = [],
            topicOptionType = [], isShow, timeZoneData } = this.props;
        const originOptionTypes = this.originOption('originType', originOptionType[index] || []);
        const topicOptionTypes = this.originOption('currencyType', topicOptionType[index] || []);
        const eventTimeOptionType = this.originOption('eventTime', timeColumoption[index] || []);

        // TODO topic 支持数组时启用
        // const topicIsPattern = panelColumn[index].topicIsPattern;
        // const topic = panelColumn[index].topic || [];
        const offsetReset = panelColumn[index].offsetReset;
        const customParams = panelColumn[index].customParams || [];

        const { getFieldDecorator } = this.props.form;

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
        return (
            <Row className="title-content">
                <Form >
                    <FormItem
                        {...formItemLayout}
                        label="类型"
                    >
                        {getFieldDecorator('type', {
                            rules: [
                                { required: true, message: '请选择类型' }
                            ]
                        })(
                            <Select placeholder="请选择" className="right-select" onChange={(v: any) => { handleInputChange('type', index, v) }}
                                showSearch filterOption={(input: any, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                            >
                                <Option value={DATA_SOURCE.KAFKA_11}>{DATA_SOURCE_TEXT[DATA_SOURCE.KAFKA_11]}</Option>
                                <Option value={DATA_SOURCE.KAFKA_10}>{DATA_SOURCE_TEXT[DATA_SOURCE.KAFKA_10]}</Option>
                                <Option value={DATA_SOURCE.KAFKA_09}>{DATA_SOURCE_TEXT[DATA_SOURCE.KAFKA_09]}</Option>
                                <Option value={DATA_SOURCE.KAFKA}>{DATA_SOURCE_TEXT[DATA_SOURCE.KAFKA]}</Option>
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="数据源"
                    >
                        {getFieldDecorator('sourceId', {
                            rules: [
                                { required: true, message: '请选择数据源' }
                            ]
                        })(
                            <Select
                                showSearch
                                placeholder="请选择"
                                className="right-select"
                                onChange={(v: any) => { handleInputChange('sourceId', index, v) }}
                                filterOption={(input: any, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                            >
                                {
                                    originOptionTypes
                                }
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="Topic"
                        style={{ marginBottom: '10px' }}
                    >
                        {getFieldDecorator('topic', {
                            rules: [
                                { required: true, message: '请选择Topic' }
                            ]
                        })(
                            <Select
                                placeholder="请选择Topic"
                                className="right-select"
                                onChange={(v: any) => { handleInputChange('topic', index, v) }}
                                showSearch
                                filterOption={(input: any, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}

                            >
                                {
                                    topicOptionTypes
                                }
                            </Select>
                        )}
                    </FormItem>
                    <Row>
                        <div className="ant-form-item-label ant-col-xs-24 ant-col-sm-6">
                        </div>
                        <Col span={18} style={{ marginBottom: 12 }}>
                            <a onClick={this.showPreviewModal}>数据预览</a>
                        </Col>
                    </Row>
                    <FormItem
                        {...formItemLayout}
                        label={(
                            <span >
                                映射表&nbsp;
                                <Tooltip title="该表是kafka中的topic映射而成，可以以SQL的方式使用它。">
                                    <Icon type="question-circle-o" />
                                </Tooltip>
                            </span>
                        )}
                    >
                        {getFieldDecorator('table', {
                            rules: [
                                { required: true, message: '请输入映射表名' }
                            ]
                        })(
                            <Input placeholder="请输入映射表名" className="right-input" onChange={(e: any) => handleInputChange('table', index, e.target.value)} />
                        )}
                    </FormItem>
                    <Row>
                        <div className="ant-form-item-label ant-col-xs-24 ant-col-sm-6">
                            <label className='required-tip'>字段</label>
                        </div>
                        <Col span={18} style={{ marginBottom: 20, height: 202 }}>
                            {isShow && (
                                <Editor
                                    style={{ minHeight: 202, height: '100%' }}
                                    className="bd"
                                    sync={sync}
                                    placeholder={'字段 类型, 比如 id int 一行一个字段\n\n仅支持JSON格式数据源，若为嵌套格式，\n字段名称由JSON的各层级key组合隔，例如：\n\nkey1.keya INT AS columnName \nkey1.keyb VARCHAR AS columnName'}
                                    value={panelColumn[index].columnsText}
                                    onChange={this.debounceEditorChange.bind(this, 'columnsText')}
                                    editorRef={(ref: any) => {
                                        this._editorRef = ref;
                                    }}
                                />
                            )}
                        </Col>
                    </Row>
                    <FormItem
                        {...formItemLayout}
                        label={<span>
                            <span style={{ paddingRight: '5px' }}>Offset</span>
                            <Tooltip overlayClassName="big-tooltip" title={<div>
                                <p>latest：从Kafka Topic内最新的数据开始消费</p>
                                <p>earliest：从Kafka Topic内最老的数据开始消费</p>
                            </div>}>
                                <Icon type="question-circle-o" />
                            </Tooltip>
                        </span>}
                    >
                        {getFieldDecorator('offsetReset')(
                            <RadioGroup className="right-select" onChange={(v: any) => { handleInputChange('offsetReset', index, v.target.value) }}>
                                <Radio value='latest'>latest</Radio>
                                <Radio value='earliest'>earliest</Radio>
                                <Radio value='custom'>自定义参数</Radio>
                            </RadioGroup>
                        )}
                    </FormItem>
                    {offsetReset == 'custom' && (
                        <Row>
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
                                        value={panelColumn[index].offsetValue}
                                        onChange={this.debounceEditorChange.bind(this, 'offsetValue')}
                                    />
                                )}
                            </Col>
                        </Row>
                    )}
                    <FormItem
                        {...formItemLayout}
                        label={<span>
                            <span style={{ paddingRight: '5px' }}>时间特征</span>
                            <Tooltip overlayClassName="big-tooltip" title={<div>
                                <p>ProcTime：按照Flink的处理时间处理</p>
                                <p>EventTime：按照流式数据本身包含的业务时间戳处理</p>
                            </div>}>
                                <Icon type="question-circle-o" />
                            </Tooltip>
                        </span>}
                    >
                        {getFieldDecorator('timeType')(
                            <RadioGroup className="right-select" onChange={(v: any) => { handleInputChange('timeType', index, v.target.value) }}>
                                <Radio value={1}>ProcTime</Radio>
                                <Radio value={2}>EventTime</Radio>
                            </RadioGroup>
                        )}
                    </FormItem>
                    {
                        panelColumn[index].timeType === 2
                            ? <FormItem
                                {...formItemLayout}
                                label="时间列"
                            >
                                {getFieldDecorator('timeColumn', {
                                    rules: [
                                        { required: true, message: '请选择时间列' }
                                    ]
                                })(
                                    <Select placeholder="请选择" className="right-select" onChange={(v: any) => { handleInputChange('timeColumn', index, v) }}
                                        showSearch filterOption={(input: any, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                                    >
                                        {
                                            eventTimeOptionType
                                        }
                                    </Select>
                                )}
                            </FormItem> : undefined
                    }
                    {
                        panelColumn[index].timeType === 2
                            ? <FormItem
                                {...formItemLayout}
                                label={(
                                    <span style={{ lineHeight: 1 }} >
                                        <span style={{ paddingRight: '5px' }}>最大延迟时间</span><br />(ms)
                                        <Tooltip title="当event time超过最大延迟时间时，系统自动丢弃此条数据">
                                            <Icon type="question-circle-o" />
                                        </Tooltip>
                                    </span>
                                )}
                            >
                                {getFieldDecorator('offset', {
                                    rules: [
                                        { required: true, message: '请输入最大延迟时间' }
                                    ]
                                })(
                                    <InputNumber className="number-input" min={0} onChange={(value: any) => handleInputChange('offset', index, value)} />
                                )}
                            </FormItem> : undefined
                    }
                    <FormItem
                        {...formItemLayout}
                        label="并行度"
                    >
                        {getFieldDecorator('parallelism')(
                            <InputNumber className="number-input" min={1} onChange={(value: any) => handleInputChange('parallelism', index, value)} />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label={
                            <span>
                                <span style={{ paddingRight: '5px' }}>时区</span>
                                <Tooltip overlayClassName="big-tooltip" title={<div>
                                    <p>注意：时区设置功能目前只支持时间特征为EventTime的任务</p>
                                </div>}>
                                    <Icon type="question-circle-o" />
                                </Tooltip>
                            </span>
                        }
                    >
                        {getFieldDecorator('timeZone')(
                            <Cascader
                                allowClear={false}
                                onChange={(value: any) => handleInputChange('timeZone', index, value.join('/'))}
                                placeholder='请选择时区'
                                showSearch
                                options={timeZoneData}
                            />
                        )}
                    </FormItem>
                    <CustomParams
                        getFieldDecorator={getFieldDecorator}
                        formItemLayout={formItemLayout}
                        customParams={customParams}
                        onChange={(type: any, id: any, value: any) => { handleInputChange('customParams', index, value, { id, type }) }}
                    />
                </Form>
                <DataPreviewModal
                    visible={this.state.visible}
                    onCancel={() => { this.setState({ visible: false }) }}
                    params={this.state.params}
                />
            </Row>
        )
    }
}
/**
 * 一组表单
 */
const InputForm = Form.create({
    mapPropsToFields (props: any) {
        const {
            type,
            sourceId,
            topic,
            table,
            columns,
            timeType,
            timeColumn,
            offset,
            columnsText,
            parallelism,
            offsetReset,
            timeZone,
            customParams
        } = props.panelColumn[props.index];

        const initialTimeZoneValue = timeZone ? timeZone.split('/') : ['Asia', 'Shanghai'];
        return {
            type: { value: parseInt(type) },
            sourceId: { value: sourceId },
            topic: { value: topic },
            // eslint-disable-next-line @typescript-eslint/camelcase
            topic_input: { value: topic },
            table: { value: table },
            columns: { value: columns },
            timeType: { value: timeType },
            timeColumn: { value: timeColumn },
            timeZone: { value: initialTimeZoneValue },
            offset: { value: offset },
            offsetReset: { value: offsetReset },
            columnsText: { value: columnsText },
            parallelism: { value: parallelism },
            ...generateMapValues(customParams)
        }
    }
})(InputOrigin);

const initialData: any = {
    tabTemplate: [], // 模版存储,所有输入源
    panelActiveKey: [], // 输入源是打开或关闭状态
    popoverVisible: [], // 删除显示按钮状态
    panelColumn: [], // 存储数据
    checkFormParams: [], // 存储要检查的参数from
    timeColumoption: [], // 时间列选择数据
    topicOptionType: [], // topic选择数据
    originOptionType: []// 数据源选择数据
}

export default class InputPanel extends React.Component<any, any> {
    constructor (props: any) {
        super(props)
        this.state = {
            tabTemplate: [], // 模版存储,所有输入源
            panelActiveKey: [], // 输入源是打开或关闭状态
            popoverVisible: [], // 删除显示按钮状态
            panelColumn: [], // 存储数据
            checkFormParams: [], // 存储要检查的参数from
            timeColumoption: [], // 时间列选择数据
            topicOptionType: [], // topic选择数据
            originOptionType: [] // 数据源选择数据
        };
    }

    componentDidMount () {
        const { source } = this.props.currentPage;
        if (source && source.length > 0) {
            this.currentInitData(source)
        }
    }

    currentInitData = (source: any) => {
        const { tabTemplate, panelColumn } = this.state;
        source.map((v: any, index: any) => {
            tabTemplate.push('InputForm');
            panelColumn.push(v);
            initCustomParam(v);
            this.getTypeOriginData(index, v.type);
            this.parseColumnsText(index, v.columnsText);
            this.getTopicType(index, v.sourceId);
        })
        this.setCurrentSource({ tabTemplate, panelColumn })
        this.setState({
            tabTemplate,
            panelColumn
        })
    }

    parseColumnsText = (index: React.ReactText, text = '') => {
        const { timeColumoption, panelColumn } = this.state;
        const columns = text.split('\n').filter(Boolean).map((v: any) => {
            let column: any;
            const asCase = /^.*\w.*\s+as\s+(\w+)$/i.exec(v);
            if (asCase) {
                return {
                    column: asCase[1]
                }
            } else {
                column = v.trim().split(' ');
            }
            return { column: column[0], type: column[1] }
        })
        console.log('columns', columns);

        const filterColumns = columns.filter((v: any) => {
            return v.column
        })
        timeColumoption[index] = filterColumns;
        console.log(' filterColumns.filter(v=> v.column === panelColumn[index].timeColumn)[0]', filterColumns.filter((v: any) => v.column === panelColumn[index].timeColumn)[0]);
        const timeColumn = filterColumns.filter((v: any) => v.column === panelColumn[index].timeColumn)[0]
        panelColumn[index].timeColumn = (timeColumn && timeColumn.column) || undefined;
        this.setCurrentSource({ timeColumoption })
        this.setState({
            timeColumoption
        })
    }

    getTypeOriginData = (index: any, type: any) => {
        const { originOptionType } = this.state;
        Api.getTypeOriginData({ type }).then((v: any) => {
            if (index === 'add') {
                if (v.code === 1) {
                    originOptionType.push(v.data)
                } else {
                    originOptionType.push([])
                }
            } else {
                if (v.code === 1) {
                    originOptionType[index] = v.data;
                } else {
                    originOptionType[index] = [];
                }
            }
            this.setCurrentSource({ originOptionType });
            this.setState({
                originOptionType
            })
        })
    }

    getTopicType = (index: any, sourceId: any) => {
        const { topicOptionType } = this.state;
        if (sourceId) {
            Api.getTopicType({ sourceId }).then((v: any) => {
                if (index === 'add') {
                    if (v.code === 1) {
                        topicOptionType.push(v.data)
                    } else {
                        topicOptionType.push([])
                    }
                } else {
                    if (v.code === 1) {
                        topicOptionType[index] = v.data;
                    } else {
                        topicOptionType[index] = [];
                    }
                }
                this.setCurrentSource({ topicOptionType });
                this.setState({
                    topicOptionType
                })
            })
        } else {
            if (index === 'add') {
                topicOptionType.push([]);
            } else {
                topicOptionType[index] = [];
            }
            this.setCurrentSource({ topicOptionType });
            this.setState({
                topicOptionType
            })
        }
    }

    getCurrentData = (taskId: any, nextProps: any) => {
        const { dispatch, inputData, currentPage } = nextProps;
        const { source } = currentPage;
        if (!inputData[taskId] && source.length > 0) {
            this.receiveState(taskId, source, dispatch)
        } else {
            const copyInitialData = JSON.parse(JSON.stringify(initialData));
            const data = inputData[taskId] || copyInitialData;
            this.setState({ ...data })
        }
    }

    receiveState = (taskId: any, source: any, dispatch: any) => {
        const tabTemplate: any = [];
        const panelColumn: any = [];
        const panelActiveKey: any = [];
        const popoverVisible: any = [];
        const checkFormParams: any = [];
        const timeColumoption: any = [];
        const originOptionType: any = [];
        const topicOptionType: any = [];
        source.map((v: any) => {
            tabTemplate.push('InputForm');
            panelColumn.push(v);
        })
        dispatch(BrowserAction.setInputData({
            taskId,
            source: {
                tabTemplate,
                panelColumn,
                panelActiveKey,
                popoverVisible,
                checkFormParams,
                timeColumoption,
                originOptionType,
                topicOptionType
            }
        }));
        this.setState({
            tabTemplate, panelColumn, panelActiveKey, popoverVisible, checkFormParams, originOptionType, topicOptionType, timeColumoption
        }, () => {
            source.map((v: any, index: any) => {
                this.getTypeOriginData(index, v.type);
                this.getTopicType(index, v.sourceId)
                this.parseColumnsText(index, v.columnsText)
            })
        })
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        const currentPage = nextProps.currentPage
        const oldPage = this.props.currentPage
        console.log('oldPage.id----currentPage.id', currentPage.id, oldPage.id);
        if (currentPage.id !== oldPage.id) {
            this.getCurrentData(currentPage.id, nextProps)
            this.setState({
                sync: true
            })
        }
    }

    changeInputTabs = (type: any, index?: any) => {
        const inputData: any = {
            type: DATA_SOURCE.KAFKA_11,
            sourceId: undefined,
            topic: [],
            table: undefined,
            // model: 1,
            // columns: [],
            timeType: 1,
            timeZone: 'Asia/Shanghai', // 默认时区值
            timeColumn: undefined,
            offset: 0,
            columnsText: undefined,
            parallelism: 1,
            offsetReset: 'latest'
            // topicIsPattern: TOPIC_TYPE.NORMAL
            // alias: undefined,
        }

        let {
            tabTemplate,
            panelActiveKey,
            popoverVisible,
            panelColumn,
            checkFormParams,
            originOptionType,
            topicOptionType
        } = this.state;
        if (type === 'add') {
            tabTemplate.push('InputForm');
            panelColumn.push(inputData);
            this.getTypeOriginData('add', inputData.type);
            this.getTopicType('add', inputData.sourceId);
            let pushIndex = `${tabTemplate.length}`;
            panelActiveKey.push(pushIndex)
        } else {
            tabTemplate.splice(index, 1);
            panelColumn.splice(index, 1);
            originOptionType.splice(index, 1);
            topicOptionType.splice(index, 1);
            checkFormParams.pop();
            panelActiveKey = this.changeActiveKey(index);
            popoverVisible[index] = false;
        }
        this.props.tableParamsChange()// 添加数据改变标记
        this.setCurrentSource({ tabTemplate, panelActiveKey, popoverVisible, panelColumn, originOptionType, topicOptionType });
        this.setState({
            tabTemplate,
            panelActiveKey,
            popoverVisible,
            panelColumn,
            checkFormParams,
            originOptionType,
            topicOptionType
        })
    }

    setCurrentSource = (data: any) => {
        const { dispatch, currentPage } = this.props;
        const dispatchSource: any = { ...this.state, ...data };
        dispatch(BrowserAction.setInputData({ taskId: currentPage.id, source: dispatchSource }));
    }

    changeActiveKey = (index: any) => { // 删除导致key改变,处理被改变key的值
        const { panelActiveKey } = this.state;
        const deleteActiveKey = `${index + 1}`;
        const deleteActiveKeyIndex = panelActiveKey.indexOf(deleteActiveKey);
        if (deleteActiveKeyIndex > -1) {
            panelActiveKey.splice(deleteActiveKeyIndex, 1)
        }
        return panelActiveKey.map((v: any) => {
            return Number(v) > Number(index) ? `${Number(v) - 1}` : v
        });
    }

    handleActiveKey = (key: any) => {
        let { panelActiveKey } = this.state;
        panelActiveKey = key
        this.setCurrentSource({ panelActiveKey })
        this.setState({
            panelActiveKey
        })
    }
    // 时区不做处理
    handleInputChange = (type: any, index: any, value: any, subValue: any) => { // 监听数据改变
        let { panelColumn, timeColumoption, originOptionType, topicOptionType } = this.state;
        let shouldUpdateEditor = true;
        const allParamsType: any = [
            'type',
            'sourceId',
            'topic',
            'table',
            'columns',
            'timeType',
            'timeZone',
            'timeColumn',
            'offset',
            'offsetReset',
            'columnsText',
            'parallelism',
            'offsetValue',
            'customParams'
        ]
        if (type === 'columnsText') {
            // this.parseColumnsText(index, value, 'changeText')
            this.parseColumnsText(index, value)
        }
        panelColumn = cloneDeep(panelColumn);
        if (type == 'customParams') { // customParams暂时不会执行
            changeCustomParams(panelColumn[index], value, subValue);
        } else {
            panelColumn[index][type] = value;
        }
        switch (type) {
            case 'type': {
                timeColumoption[index] = [];
                originOptionType[index] = [];
                topicOptionType[index] = [];
                allParamsType.map((v: any) => {
                    if (v != 'type') {
                        if (v == 'columns') {
                            panelColumn[index][v] = [];
                        } else if (v == 'timeType') {
                            panelColumn[index][v] = 1
                        } else if (v == 'parallelism') {
                            panelColumn[index][v] = 1
                        } else if (v == 'offsetReset') {
                            panelColumn[index][v] = 'latest'
                        } else if (v == 'timeZone') {
                            panelColumn[index][v] = 'Asia/Shanghai'
                        } else {
                            panelColumn[index][v] = undefined
                        }
                    }
                })
                this.getTypeOriginData(index, value);
                break;
            }
            case 'sourceId': {
                timeColumoption[index] = [];
                topicOptionType[index] = []; // 清空topic列表
                allParamsType.map((v: any) => {
                    if (v != 'type' && v != 'sourceId') {
                        if (v == 'columns' || v == 'topic') {
                            panelColumn[index][v] = [];
                        } else if (v == 'timeType') {
                            panelColumn[index][v] = 1
                        } else if (v == 'parallelism') {
                            panelColumn[index][v] = 1
                        } else if (v == 'offsetReset') {
                            panelColumn[index][v] = 'latest'
                        } else if (v == 'timeZone') {
                            panelColumn[index][v] = 'Asia/Shanghai'
                        } else {
                            panelColumn[index][v] = undefined
                        }
                    }
                })
                this.getTopicType(index, value);
                break;
            }
            default: {
                shouldUpdateEditor = false;
            }
        }
        this.props.tableParamsChange()// 添加数据改变标记
        this.setCurrentSource({ panelColumn })
        this.setState({
            panelColumn,
            sync: shouldUpdateEditor
        })
    }

    clearCurrentInfo = (type: any, index: any, value: any) => {
        const { panelColumn, topicOptionType, originOptionType } = this.state;
        const inputData: any = {
            type: undefined,
            sourceId: undefined,
            topic: undefined,
            table: undefined,
            timeType: 1,
            timeColumn: undefined,
            offset: 0,
            columnsText: undefined,
            parallelism: 1,
            offsetReset: 'latest'
        }
        if (type === 'type') {
            inputData.type = value;
            panelColumn[index] = inputData;
            topicOptionType[index] = [];
            originOptionType[index] = []
        } else if (type === 'sourceId') {
            inputData.type = panelColumn[index]['type']
            inputData.sourceId = value;
            panelColumn[index] = inputData;
            topicOptionType[index] = [];
        }
        this.setCurrentSource({ panelColumn, topicOptionType, originOptionType })
        this.setState({ panelColumn, topicOptionType, originOptionType });
    }

    handlePopoverVisibleChange = (e: any, index: any, visible: any) => {
        let { popoverVisible } = this.state;
        popoverVisible[index] = visible;
        if (e) {
            e.stopPropagation();// 阻止删除按钮点击后冒泡到panel
            if (visible) { // 只打开一个Popover提示
                popoverVisible = popoverVisible.map((v: any, i: any) => {
                    return index == i
                })
            }
        }
        this.setCurrentSource({ popoverVisible })
        this.setState({ popoverVisible });
    }

    panelHeader = (index: any) => {
        const { popoverVisible } = this.state;
        const popoverContent = <div className="input-panel-title">
            <div style={{ padding: '8 0 12' }}> <Icon type="exclamation-circle" style={{ color: '#faad14' }} />  你确定要删除此源表吗？</div>
            <div style={{ textAlign: 'right', padding: '0 0 8' }}>
                <Button style={{ marginRight: 8 }} size="small" onClick={() => { this.handlePopoverVisibleChange(null, index, false) }}>取消</Button>
                <Button type="primary" size="small" onClick={() => { this.changeInputTabs('delete', index) }}>确定</Button>
            </div>
        </div>
        const onClickFix = {
            onClick: (e: any) => { this.handlePopoverVisibleChange(e, index, !popoverVisible[index]) }
        }
        return <div className="input-panel-title">
            <span>{` 源表 ${index + 1} `}</span>
            <Popover
                trigger="click"
                placement="topLeft"
                content={popoverContent}
                visible={popoverVisible[index]}
                // onClick={(e: any) => { this.handlePopoverVisibleChange(e, index, !popoverVisible[index]) }}
                {...onClickFix}
            >
                <span className="title-icon input-panel-title" ><Icon type="delete" /></span>
            </Popover>
        </div>
    }

    recordForm = (ref: any) => { // 存储子组建的所有要检查的form表单
        const { checkFormParams } = this.state;
        checkFormParams.push(ref);
        this.setCurrentSource({ checkFormParams })
        this.setState({
            checkFormParams
        })
    }

    render () {
        const { tabTemplate, panelActiveKey, panelColumn, timeColumoption, topicOptionType, originOptionType, sync } = this.state;
        const { isShow, timeZoneData, currentPage, isLocked } = this.props;
        return (
            <div className="m-taksdetail panel-content">
                <Collapse activeKey={panelActiveKey} bordered={false} onChange={this.handleActiveKey} >
                    {
                        tabTemplate.map((InputPutOrigin: any, index: any) => {
                            return (
                                <Panel header={this.panelHeader(index)} key={index + 1} style={{ borderRadius: 5, position: 'relative' }} className="input-panel">
                                    <InputForm
                                        isShow={panelActiveKey.indexOf(index + 1 + '') > -1 && isShow}
                                        sync={sync}
                                        index={index}
                                        handleInputChange={this.handleInputChange}
                                        panelColumn={panelColumn}
                                        onRef={this.recordForm}
                                        editorParamsChange={this.props.editorParamsChange}
                                        timeColumoption={timeColumoption}
                                        topicOptionType={topicOptionType}
                                        originOptionType={originOptionType}
                                        timeZoneData={timeZoneData}
                                        textChange={() => {
                                            this.setState({
                                                sync: false
                                            })
                                        }}
                                    />
                                    <LockPanel lockTarget={currentPage} />
                                </Panel>
                            )
                        })
                    }
                </Collapse>
                <Button disabled={isLocked} className="stream-btn" onClick={() => { this.changeInputTabs('add') }} style={{ borderRadius: 5 }}><Icon type="plus" /><span> 添加源表</span></Button>
            </div>
        )
    }
}
