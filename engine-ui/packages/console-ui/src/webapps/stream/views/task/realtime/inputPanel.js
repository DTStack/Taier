import React, { Component } from 'react'
import {
    Row, Col, Icon, Tooltip, Input, Select,
    Collapse, Button, Radio, Popover, Form, InputNumber
} from 'antd';
import { debounce, cloneDeep } from 'lodash';

import Api from '../../../api';
import * as BrowserAction from '../../../store/modules/realtimeTask/browser'
import { DATA_SOURCE_TEXT, DATA_SOURCE } from '../../../comm/const'

import Editor from 'widgets/code-editor'

const Option = Select.Option;
const Panel = Collapse.Panel;
const RadioGroup = Radio.Group;

const FormItem = Form.Item;

class InputOrigin extends Component {
    componentDidMount () {
        this.props.onRef(this);
    }
    refreshEditor () {
        if (this._editorRef) {
            console.log('refresh')
            this._editorRef.refresh();
        }
    }
    // componentDidUpdate() {
    //     if (this.props.isShow) {
    //         this.refreshEditor();
    //     }
    // }
    checkParams = () => {
        // 手动检测table参数
        let result = {};
        this.props.form.validateFields((err, values) => {
            if (!err) {
                result.status = true;
            } else {
                result.status = false;
            }
        });
        return result
    }

    originOption = (type, arrData) => {
        switch (type) {
            case 'originType':
                return arrData.map(v => {
                    return <Option key={v} value={`${v.id}`}>{v.name}</Option>
                })
            case 'currencyType':
                return arrData.map(v => {
                    return <Option key={v} value={`${v}`}>{v}</Option>
                })
            case 'eventTime':
                return arrData.map((v, index) => {
                    return <Option key={index} value={`${v.column}`}>{v.column}</Option>
                })
            default:
                return null;
        }
    }

    editorParamsChange (a, b, c) {
        const { handleInputChange, textChange, index } = this.props;
        textChange();
        handleInputChange('columnsText', index, b);
    }

    debounceEditorChange = debounce(this.editorParamsChange, 300, { 'maxWait': 2000 })

    render () {
        const { handleInputChange, index, panelColumn, sync, timeColumoption = [], originOptionType = [], topicOptionType = [], isShow } = this.props;
        const originOptionTypes = this.originOption('originType', originOptionType[index] || []);
        const topicOptionTypes = this.originOption('currencyType', topicOptionType[index] || []);

        const eventTimeOptionType = this.originOption('eventTime', timeColumoption[index] || []);
        const { getFieldDecorator } = this.props.form;

        const formItemLayout = {
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
                            <Select placeholder="请选择" className="right-select" onChange={(v) => { handleInputChange('type', index, v) }}
                                showSearch filterOption={(input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                            >
                                <Option value={DATA_SOURCE.KAFKA}>{DATA_SOURCE_TEXT[DATA_SOURCE.KAFKA]}</Option>
                                <Option value={DATA_SOURCE.KAFKA_10}>{DATA_SOURCE_TEXT[DATA_SOURCE.KAFKA_10]}</Option>
                                <Option value={DATA_SOURCE.KAFKA_09}>{DATA_SOURCE_TEXT[DATA_SOURCE.KAFKA_09]}</Option>
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
                            <Select placeholder="请选择" className="right-select" onChange={(v) => { handleInputChange('sourceId', index, v) }}
                                showSearch filterOption={(input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
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
                    >
                        {getFieldDecorator('topic', {
                            rules: [
                                { required: true, message: '请选择Topic' }
                            ]
                        })(
                            <Select placeholder="请选择" className="right-select" onChange={(v) => { handleInputChange('topic', index, v) }}
                                showSearch filterOption={(input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                            >
                                {
                                    topicOptionTypes
                                }
                            </Select>
                        )}
                    </FormItem>
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
                            <Input placeholder="请输入映射表名" className="right-input" onChange={e => handleInputChange('table', index, e.target.value)} />
                        )}
                    </FormItem>
                    <Row>
                        <div className="ant-form-item-label ant-col-xs-24 ant-col-sm-6">
                            <label className='required-tip'>字段</label>
                        </div>
                        <Col span="18" style={{ marginBottom: 20, height: 202 }}>
                            {isShow && (
                                <Editor
                                    style={{ minHeight: 202 }}
                                    className="bd"
                                    sync={sync}
                                    placeholder="字段 类型, 比如 id int 一行一个字段"
                                    value={panelColumn[index].columnsText}
                                    onChange={this.debounceEditorChange.bind(this)}
                                    editorRef={(ref) => {
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
                            <RadioGroup className="right-select" onChange={(v) => { handleInputChange('offsetReset', index, v.target.value) }}>
                                <Radio value='latest'>latest</Radio>
                                <Radio value='earliest'>earliest</Radio>
                            </RadioGroup>
                        )}
                    </FormItem>
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
                            <RadioGroup className="right-select" onChange={(v) => { handleInputChange('timeType', index, v.target.value) }}>
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
                                    <Select placeholder="请选择" className="right-select" onChange={(v) => { handleInputChange('timeColumn', index, v) }}
                                        showSearch filterOption={(input, option) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
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
                                    <InputNumber className="number-input" min={0} onChange={value => handleInputChange('offset', index, value)} />
                                )}
                            </FormItem> : undefined
                    }
                    <FormItem
                        {...formItemLayout}
                        label="并行度"
                    >
                        {getFieldDecorator('parallelism')(
                            <InputNumber className="number-input" min={1} onChange={value => handleInputChange('parallelism', index, value)} />
                        )}
                    </FormItem>
                </Form>
            </Row>
        )
    }
}
/**
 * 一组表单
 */
const InputForm = Form.create({
    mapPropsToFields (props) {
        const { type, sourceId, topic, table, columns, timeType, timeColumn, offset, columnsText, parallelism, offsetReset } = props.panelColumn[props.index];
        return {
            type: { value: parseInt(type) },
            sourceId: { value: sourceId },
            topic: { value: topic },
            table: { value: table },
            columns: { value: columns },
            timeType: { value: timeType },
            timeColumn: { value: timeColumn },
            offset: { value: offset },
            offsetReset: { value: offsetReset },
            columnsText: { value: columnsText },
            parallelism: { value: parallelism }
            // alias: { value: alias },
        }
    }
})(InputOrigin);

const initialData = {
    tabTemplate: [], // 模版存储,所有输入源
    panelActiveKey: [], // 输入源是打开或关闭状态
    popoverVisible: [], // 删除显示按钮状态
    panelColumn: [], // 存储数据
    checkFormParams: [], // 存储要检查的参数from
    timeColumoption: [], // 时间列选择数据
    topicOptionType: [], // topic选择数据
    originOptionType: []// 数据源选择数据
}

export default class InputPanel extends Component {
    constructor (props) {
        super(props)
        this.state = {
            tabTemplate: [], // 模版存储,所有输入源
            panelActiveKey: [], // 输入源是打开或关闭状态
            popoverVisible: [], // 删除显示按钮状态
            panelColumn: [], // 存储数据
            checkFormParams: [], // 存储要检查的参数from
            timeColumoption: [], // 时间列选择数据
            topicOptionType: [], // topic选择数据
            originOptionType: []// 数据源选择数据
        };
    }

    componentDidMount () {
        const { source } = this.props.currentPage;
        if (source && source.length > 0) {
            this.currentInitData(source)
        }
    }

    currentInitData = (source) => {
        const { tabTemplate, panelColumn } = this.state;
        source.map((v, index) => {
            tabTemplate.push('InputForm');
            panelColumn.push(v);
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

    parseColumnsText = (index, text = '') => {
        const { timeColumoption, panelColumn } = this.state;
        const columns = text.split('\n').filter(Boolean).map(v => {
            let column;
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

        const filterColumns = columns.filter(v => {
            return v.column
        })
        timeColumoption[index] = filterColumns;
        console.log(' filterColumns.filter(v=> v.column === panelColumn[index].timeColumn)[0]', filterColumns.filter(v => v.column === panelColumn[index].timeColumn)[0]);
        const timeColumn = filterColumns.filter(v => v.column === panelColumn[index].timeColumn)[0]
        panelColumn[index].timeColumn = (timeColumn && timeColumn.column) || undefined;
        this.setCurrentSource({ timeColumoption })
        this.setState({
            timeColumoption
        })
    }

    getTypeOriginData = (index, type) => {
        const { originOptionType } = this.state;
        Api.getTypeOriginData({ type }).then(v => {
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

    getTopicType = (index, sourceId) => {
        const { topicOptionType } = this.state;
        if (sourceId) {
            Api.getTopicType({ sourceId }).then(v => {
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

    getCurrentData = (taskId, nextProps) => {
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

    receiveState = (taskId, source, dispatch) => {
        const tabTemplate = [];
        const panelColumn = [];
        const panelActiveKey = [];
        const popoverVisible = [];
        const checkFormParams = [];
        const timeColumoption = [];
        const originOptionType = [];
        const topicOptionType = [];
        source.map(v => {
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
            source.map((v, index) => {
                this.getTypeOriginData(index, v.type);
                this.getTopicType(index, v.sourceId)
                this.parseColumnsText(index, v.columnsText)
            })
        })
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps) {
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
    changeInputTabs = (type, index) => {
        const inputData = {
            type: DATA_SOURCE.KAFKA,
            sourceId: undefined,
            topic: undefined,
            table: undefined,
            // model: 1,
            // columns: [],
            timeType: 1,
            timeColumn: undefined,
            offset: 0,
            columnsText: undefined,
            parallelism: 1,
            offsetReset: 'latest'
            // alias: undefined,
        }

        let { tabTemplate, panelActiveKey, popoverVisible, panelColumn, checkFormParams, originOptionType, topicOptionType } = this.state;
        if (type === 'add') {
            tabTemplate.push('InputForm');
            panelColumn.push(inputData);
            this.getTypeOriginData('add', inputData.type);
            this.getTopicType('add', inputData.sourceId)
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

    setCurrentSource = (data) => {
        const { dispatch, currentPage } = this.props;
        const dispatchSource = { ...this.state, ...data };
        dispatch(BrowserAction.setInputData({ taskId: currentPage.id, source: dispatchSource }));
    }

    changeActiveKey = (index) => { // 删除导致key改变,处理被改变key的值
        const { panelActiveKey } = this.state;
        const deleteActiveKey = `${index + 1}`;
        const deleteActiveKeyIndex = panelActiveKey.indexOf(deleteActiveKey);
        if (deleteActiveKeyIndex > -1) {
            panelActiveKey.splice(deleteActiveKeyIndex, 1)
        }
        return panelActiveKey.map(v => {
            return Number(v) > Number(index) ? `${Number(v) - 1}` : v
        });
    }

    handleActiveKey = (key) => {
        let { panelActiveKey } = this.state;
        panelActiveKey = key
        this.setCurrentSource({ panelActiveKey })
        this.setState({
            panelActiveKey
        })
    }

    handleInputChange = (type, index, value) => { // 监听数据改变
        let { panelColumn, timeColumoption, originOptionType, topicOptionType } = this.state;
        let shouldUpdateEditor = true;
        const allParamsType = ['type', 'sourceId', 'topic', 'table', 'columns', 'timeType', 'timeColumn', 'offset', 'offsetReset', 'columnsText', 'parallelism']
        if (type === 'columnsText') {
            this.parseColumnsText(index, value, 'changeText')
        }
        panelColumn = cloneDeep(panelColumn);
        panelColumn[index][type] = value;
        if (type === 'type') {
            // this.clearCurrentInfo(type,index)
            timeColumoption[index] = [];
            originOptionType[index] = [];
            topicOptionType[index] = [];
            allParamsType.map(v => {
                if (v != 'type') {
                    if (v == 'columns') {
                        panelColumn[index][v] = [];
                    } else if (v == 'timeType') {
                        panelColumn[index][v] = 1
                    } else if (v == 'parallelism') {
                        panelColumn[index][v] = 1
                    } else if (v == 'offsetReset') {
                        panelColumn[index][v] = 'latest'
                    } else {
                        panelColumn[index][v] = undefined
                    }
                }
            })
            this.getTypeOriginData(index, value);
        } else if (type === 'sourceId') {
            // this.clearCurrentInfo(type,index)
            timeColumoption[index] = [];
            topicOptionType[index] = [];
            allParamsType.map(v => {
                if (v != 'type' && v != 'sourceId') {
                    if (v == 'columns') {
                        panelColumn[index][v] = [];
                    } else if (v == 'timeType') {
                        panelColumn[index][v] = 1
                    } else if (v == 'parallelism') {
                        panelColumn[index][v] = 1
                    } else if (v == 'offsetReset') {
                        panelColumn[index][v] = 'latest'
                    } else {
                        panelColumn[index][v] = undefined
                    }
                }
            })
            this.getTopicType(index, value);
        } else if (type === 'topic') {
            timeColumoption[index] = [];
            allParamsType.map(v => {
                if (v != 'type' && v != 'sourceId' && v != 'topic') {
                    if (v == 'columns') {
                        panelColumn[index][v] = [];
                    } else if (v == 'timeType') {
                        panelColumn[index][v] = 1
                    } else if (v == 'parallelism') {
                        panelColumn[index][v] = 1
                    } else if (v == 'offsetReset') {
                        panelColumn[index][v] = 'latest'
                    } else {
                        panelColumn[index][v] = undefined
                    }
                }
            })
        } else {
            shouldUpdateEditor = false;
        }
        this.props.tableParamsChange()// 添加数据改变标记
        this.setCurrentSource({ panelColumn })
        this.setState({
            panelColumn,
            sync: shouldUpdateEditor
        })
    }

    clearCurrentInfo = (type, index, value) => {
        const { panelColumn, topicOptionType, originOptionType } = this.state;
        const inputData = {
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

    handlePopoverVisibleChange = (e, index, visible) => {
        let { popoverVisible } = this.state;
        popoverVisible[index] = visible;
        if (e) {
            e.stopPropagation();// 阻止删除按钮点击后冒泡到panel
            if (visible) { // 只打开一个Popover提示
                popoverVisible = popoverVisible.map((v, i) => {
                    return index == i
                })
            }
        }
        this.setCurrentSource({ popoverVisible })
        this.setState({ popoverVisible });
    }

    panelHeader = (index) => {
        const { popoverVisible } = this.state;
        const popoverContent = <div className="input-panel-title">
            <div style={{ padding: '8 0 12' }}> <Icon type="exclamation-circle" style={{ color: '#faad14' }} />  你确定要删除此输入源吗？</div>
            <div style={{ textAlign: 'right', padding: '0 0 8' }}>
                <Button style={{ marginRight: 8 }} size="small" onClick={() => { this.handlePopoverVisibleChange(null, index, false) }}>取消</Button>
                <Button type="primary" size="small" onClick={() => { this.changeInputTabs('delete', index) }}>确定</Button>
            </div>
        </div>
        return <div className="input-panel-title">
            <span>{` 输入源 ${index + 1} `}</span>
            <Popover
                trigger="click"
                placement="topLeft"
                content={popoverContent}
                visible={popoverVisible[index]}
                onClick={(e) => { this.handlePopoverVisibleChange(e, index, !popoverVisible[index]) }}
            >
                <span className="title-icon input-panel-title" ><Icon type="delete" /></span>
            </Popover>
        </div>
    }

    recordForm = (ref) => { // 存储子组建的所有要检查的form表单
        const { checkFormParams } = this.state;
        checkFormParams.push(ref);
        this.setCurrentSource({ checkFormParams })
        this.setState({
            checkFormParams
        })
    }

    render () {
        const { tabTemplate, panelActiveKey, panelColumn, timeColumoption, topicOptionType, originOptionType, sync } = this.state;
        const { isShow } = this.props;
        return (
            <div className="m-taksdetail panel-content">
                <Collapse activeKey={panelActiveKey} bordered={false} onChange={this.handleActiveKey} >
                    {
                        tabTemplate.map((InputPutOrigin, index) => {
                            return (
                                <Panel header={this.panelHeader(index)} key={index + 1} style={{ borderRadius: 5 }} className="input-panel">
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
                                        textChange={() => {
                                            this.setState({
                                                sync: false
                                            })
                                        }}
                                    />
                                </Panel>
                            )
                        })
                    }
                </Collapse>
                <Button className="stream-btn" onClick={() => { this.changeInputTabs('add') }} style={{ borderRadius: 5 }}><Icon type="plus" /><span> 添加输入</span></Button>
            </div>
        )
    }
}
