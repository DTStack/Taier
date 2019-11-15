import * as React from 'react'
import {
    Row, Col, Icon, Tooltip, Table, Input,
    Select, Collapse, Button, Popover, Popconfirm,
    Form, InputNumber, Checkbox
} from 'antd'
import { debounce, isEmpty } from 'lodash';

import Api from '../../../api'
import * as BrowserAction from '../../../store/modules/realtimeTask/browser'
import { DATA_SOURCE, HELP_TEXT } from '../../../comm/const';
import { haveTableList, haveCustomParams, haveTableColumn, havePrimaryKey, haveTopic } from './sidePanel/panelCommonUtil';

import Editor from 'widgets/code-editor'
import { CustomParams, generateMapValues, changeCustomParams, initCustomParam } from './sidePanel/customParams';
import LockPanel from '../../../components/lockPanel';

const { TextArea } = Input;
const Option = Select.Option;
const Panel = Collapse.Panel;
const { Column } = Table;
const FormItem = Form.Item;

class OutputOrigin extends React.Component<any, any> {
    _editorRef: any;
    componentDidMount () {
        this.props.onRef(this);
    }
    refreshEditor () {
        if (this._editorRef) {
            console.log('refresh')
            this._editorRef.refresh();
        }
    }
    checkParams = (v: any) => {
        // 手动检测table参数

        let result: any = {};
        this.props.form.validateFields((err: any, values: any) => {
            if (!err) {
                const { panelColumn, index } = this.props;
                const data = panelColumn[index];
                if ((!data.columnsText || !data.columnsText.trim()) && !data.columns.filter((item: any) => { return !isEmpty(item) }).length) {
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
            case 'columnType':
                return arrData.map((v: any, index: any) => {
                    return <Option key={index} value={`${v.key}`}>{v.key}</Option>
                })
            case 'primaryType':
                return arrData.map((v: any, index: any) => {
                    return <Option key={index} value={`${v.column}`}>{v.column}</Option>
                })
            default:
                return null;
        }
    }

    editorParamsChange (a: any, b: any, c: any) {
        const { handleInputChange, index, textChange } = this.props;
        textChange();
        handleInputChange('columnsText', index, b);
    }

    debounceEditorChange = debounce(this.editorParamsChange, 300, { 'maxWait': 2000 })

    render () {
        const {
            handleInputChange, index, sync, originOptionType,
            tableOptionType, panelColumn, tableColumnOptionType, isShow,
            topicOptionType
        } = this.props;
        const { getFieldDecorator } = this.props.form;
        const originOptionTypes = this.originOption('originType', originOptionType[index] || []);
        const tableOptionTypes = this.originOption('currencyType', tableOptionType[index] || []);
        const topicOptionTypes = this.originOption('currencyType', topicOptionType[index] || []);
        const tableColumnOptionTypes = this.originOption('columnType', tableColumnOptionType[index] || []);
        const primaryKeyOptionTypes = this.originOption('primaryType', panelColumn[index].columns || []);
        const customParams = panelColumn[index].customParams || [];
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
                <FormItem
                    {...formItemLayout}
                    label="存储类型"
                >
                    {getFieldDecorator('type', {
                        rules: [
                            { required: true, message: '请选择存储类型' }
                        ]
                    })(
                        <Select className="right-select" onChange={(v: any) => { handleInputChange('type', index, v) }}
                            showSearch filterOption={(input: any, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                        >
                            <Option value={DATA_SOURCE.MYSQL}>MySQL</Option>
                            <Option value={DATA_SOURCE.ORACLE}>Oracle</Option>
                            {/* <Option value={DATA_SOURCE.POSTGRESQL}>PostgreSQL</Option> */}
                            <Option value={DATA_SOURCE.KUDU}>Kudu</Option>
                            <Option value={DATA_SOURCE.HBASE}>HBase</Option>
                            <Option value={DATA_SOURCE.ES}>ElasticSearch</Option>
                            <Option value={DATA_SOURCE.REDIS}>Redis</Option>
                            <Option value={DATA_SOURCE.MONGODB}>MongoDB</Option>
                            <Option value={DATA_SOURCE.KAFKA}>Kafka</Option>
                            <Option value={DATA_SOURCE.KAFKA_09}>Kafka09</Option>
                            <Option value={DATA_SOURCE.KAFKA_10}>Kafka10</Option>
                            <Option value={DATA_SOURCE.KAFKA_11}>Kafka11</Option>
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="数据源"
                >
                    {getFieldDecorator('sourceId', {
                        initialValue: 'disabled',
                        rules: [
                            { required: true, message: '请选择数据源' }
                        ]
                    })(
                        <Select
                            showSearch
                            placeholder="请选择数据源"
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
                {
                    haveTopic(panelColumn[index].type)
                        ? <FormItem
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
                        </FormItem> : ''
                }
                {haveTableList(panelColumn[index].type)
                    ? <FormItem
                        {...formItemLayout}
                        label="表"
                    >
                        {getFieldDecorator('table', {
                            initialValue: 'disabled',
                            rules: [
                                { required: true, message: '请选择表' }
                            ]
                        })(
                            <Select className="right-select" onChange={(v: any) => { handleInputChange('table', index, v) }}
                                showSearch filterOption={(input: any, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                            >
                                {
                                    tableOptionTypes
                                }
                            </Select>
                        )}
                    </FormItem> : ''
                }
                {
                    panelColumn[index].type == DATA_SOURCE.REDIS
                        ? <FormItem
                            {...formItemLayout}
                            label="表"
                        >
                            {getFieldDecorator('table-input', {
                                initialValue: 'disabled',
                                rules: [
                                    { required: true, message: '请输入表名' }
                                ]
                            })(
                                <Input onChange={(v: any) => { handleInputChange('table', index, v.target.value) }} />
                            )}
                        </FormItem> : ''
                }
                {panelColumn[index].type == DATA_SOURCE.REDIS
                    ? <FormItem
                        {...formItemLayout}
                        label="主键"
                    >
                        {getFieldDecorator('primaryKey-input', {
                            rules: [
                                { required: true, message: '请输入主键' }
                            ]
                        })(
                            <Input placeholder="结果表主键，多个字段用英文逗号隔开" onChange={(e: any) => handleInputChange('primaryKey', index, e.target.value)} />
                        )}
                    </FormItem> : ''
                }
                {
                    panelColumn[index].type == DATA_SOURCE.ES
                        ? <FormItem
                            {...formItemLayout}
                            label="索引"
                        >
                            {getFieldDecorator('index', {
                                rules: [
                                    { required: true, message: '请输入索引' }
                                ]
                            })(
                                <Input placeholder="请输入索引" onChange={(e: any) => handleInputChange('index', index, e.target.value)} />
                            )}
                        </FormItem> : ''
                }
                {
                    panelColumn[index].type == DATA_SOURCE.ES
                        ? <FormItem
                            {...formItemLayout}
                            label={(
                                <span >
                                    id&nbsp;
                                    <Tooltip title="id生成规则：填写字段的索引位置（从0开始)">
                                        <Icon type="question-circle-o" />
                                    </Tooltip>
                                    &nbsp;
                                </span>
                            )}
                        >
                            {getFieldDecorator('esId')(
                                <Input placeholder="请输入id" onChange={(e: any) => handleInputChange('esId', index, e.target.value)} />
                            )}
                        </FormItem> : ''
                }
                {
                    panelColumn[index].type == DATA_SOURCE.ES
                        ? <FormItem
                            {...formItemLayout}
                            label="索引类型"
                        >
                            {getFieldDecorator('esType', {
                                rules: [
                                    { required: true, message: '请输入索引类型' }
                                ]
                            })(
                                <Input placeholder="请输入索引类型" onChange={(e: any) => handleInputChange('esType', index, e.target.value)} />
                            )}
                        </FormItem> : ''
                }
                {
                    panelColumn[index].type == DATA_SOURCE.HBASE
                        ? <FormItem
                            {...formItemLayout}
                            label="rowKey"
                        >
                            {getFieldDecorator('rowKey', {
                                rules: [
                                    { required: true, message: '请输入rowKey' }
                                ]
                            })(
                                <Input placeholder=" rowKey 格式：填写字段1 , 填写字段2 " onChange={(e: any) => handleInputChange('rowKey', index, e.target.value)} />
                            )}
                        </FormItem> : ''
                }
                <FormItem
                    {...formItemLayout}
                    label="映射表"
                >
                    {getFieldDecorator('tableName', {
                        rules: [
                            { required: true, message: '请输入映射表名' }
                        ]
                    })(
                        <Input placeholder="请输入映射表名" onChange={(e: any) => handleInputChange('tableName', index, e.target.value)} />
                    )}
                </FormItem>
                <Row>
                    <div className="ant-form-item-label ant-col-xs-24 ant-col-sm-6 required-tip">
                        <label className='required-tip'>字段</label>
                    </div>
                    {
                        haveTableColumn(panelColumn[index].type)
                            ? <Col span={18} style={{ marginBottom: 20 }}>
                                <div style={{ textAlign: 'right', padding: '8px 5px 5px 0px' }}>
                                    <a onClick={() => { handleInputChange('addAllColumn', index) }} style={{ marginRight: 5 }}>导入全部字段</a>
                                    <Popconfirm title="确认清空所有字段？" onConfirm={() => { handleInputChange('deleteAllColumn', index) }} okText="确认" cancelText="取消">
                                        <a>清空</a>
                                    </Popconfirm>
                                </div>
                                <div className="bd">
                                    <Table scroll={{ y: 310 }} dataSource={panelColumn[index].columns} className="table-small" pagination={false} size="small" >
                                        <Column
                                            title="字段"
                                            dataIndex="column"
                                            key="字段"
                                            width='50%'
                                            render={(text: any, record: any, subIndex: any) => {
                                                return <Select className="sub-right-select" value={text} onChange={(v: any) => { handleInputChange('subColumn', index, subIndex, v) }}
                                                    showSearch filterOption={(input: any, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                                                >
                                                    {
                                                        tableColumnOptionTypes
                                                    }
                                                </Select>
                                            }}
                                        />
                                        <Column
                                            title="类型"
                                            dataIndex="type"
                                            key="类型"
                                            width='40%'
                                            render={(text: any, record: any, subIndex: any) => {
                                                return <Input value={text} disabled />
                                            }}
                                        />
                                        <Column
                                            key="delete"
                                            render={(text: any, record: any, subIndex: any) => { return <Icon type="close" style={{ fontSize: 16, color: '#888' }} onClick={() => { handleInputChange('deleteColumn', index, subIndex) }} /> }}
                                        />
                                    </Table>
                                    <div style={{ padding: '0 20 20' }}>
                                        <Button className="stream-btn" type="dashed" style={{ borderRadius: 5 }} onClick={() => { handleInputChange('columns', index, {}) }}>
                                            <Icon type="plus" /><span> 添加输入</span>
                                        </Button>
                                    </div>
                                </div>
                            </Col>
                            : <Col span={18} style={{ marginBottom: 20, height: 200 }}>
                                {isShow && (
                                    <Editor
                                        style={{ minHeight: 202, border: '1px solid #ddd', height: '100%' }}
                                        sync={sync}
                                        placeholder={'字段 类型, 比如 id int 一行一个字段'}
                                        value={panelColumn[index].columnsText}
                                        onChange={this.debounceEditorChange.bind(this)}
                                        editorRef={(ref: any) => {
                                            this._editorRef = ref;
                                        }}
                                    />
                                )}
                            </Col>
                    }
                </Row>
                {
                    havePrimaryKey(panelColumn[index].type)
                        ? <FormItem
                            {...formItemLayout}
                            label="主键"
                        >
                            {getFieldDecorator('primaryKey', {
                                rules: [{ required: panelColumn[index].isUpsert, message: '请输入主键' }]
                            })(
                                <Select className="right-select" onChange={(v: any) => { handleInputChange('primaryKey', index, v) }} mode="multiple"
                                    showSearch filterOption={(input: any, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                                >
                                    {
                                        primaryKeyOptionTypes
                                    }
                                </Select>
                            )}
                        </FormItem> : ''
                }
                {
                    panelColumn[index].type == DATA_SOURCE.POSTGRESQL
                        ? <FormItem
                            {...formItemLayout}
                            label="写入模式"
                        >
                            {getFieldDecorator('isUpsert')(
                                <Checkbox defaultChecked={false} onChange={(e: any) => handleInputChange('isUpsert', index, e.target.checked)}>
                                    开启upsert&nbsp;&nbsp;
                                    <Tooltip placement="top" title={HELP_TEXT.WRITE_MODE} arrowPointAtCenter>
                                        <Icon type="question-circle-o" />
                                    </Tooltip>
                                </Checkbox>
                            )}
                        </FormItem> : ''
                }
                {
                    panelColumn[index].type == DATA_SOURCE.KUDU
                        ? <FormItem
                            {...formItemLayout}
                            label={(
                                <span >
                                    写入模式&nbsp;
                                    <Tooltip title="insert: 插入数据，当主键重复时报错。update: 更新数据，当主键不存在时报错。upsert: 插入或更新数据，主键已存在时执行更新，不存在时执行插入">
                                        <Icon type="question-circle-o" />
                                    </Tooltip>
                                    &nbsp;
                                </span>
                            )}
                        >
                            {getFieldDecorator('writeMode', {
                                rules: [
                                    { required: true, message: '请选择写入模式' }
                                ]
                            })(
                                <Select className="right-select" onChange={(v: any) => { handleInputChange('writeMode', index, v) }}
                                    showSearch filterOption={(input: any, option: any) => option.props.children.toLowerCase().indexOf(input.toLowerCase()) >= 0}
                                >
                                    <Option value='insert'>insert</Option>
                                    <Option value='update'>update</Option>
                                    <Option value='upset'>upset</Option>
                                </Select>
                            )}
                        </FormItem> : ''
                }
                <FormItem
                    {...formItemLayout}
                    label="并行度"
                >
                    {getFieldDecorator('parallelism')(
                        <InputNumber className="number-input" min={1} onChange={(value: any) => handleInputChange('parallelism', index, value)} />
                    )}
                </FormItem>
                {
                    panelColumn[index].type == DATA_SOURCE.KUDU
                        ? <FormItem
                            {...formItemLayout}
                            label="高级配置"
                        >
                            {getFieldDecorator('advanConf')(
                                <TextArea placeholder="以JSON格式添加高级参数"
                                    style={{ minHeight: '100px' }}
                                    onChange={(e: any) => { handleInputChange('advanConf', index, e.target.value) }}
                                />
                            )}
                        </FormItem> : ''
                }
                {haveCustomParams(panelColumn[index].type) && <CustomParams
                    getFieldDecorator={getFieldDecorator}
                    formItemLayout={formItemLayout}
                    customParams={customParams}
                    onChange={(type: any, id: any, value: any) => { handleInputChange('customParams', index, value, { id, type }) }}
                />}
            </Row>
        )
    }
}

const OutputForm = Form.create({
    mapPropsToFields (props: any) {
        const {
            type, sourceId,
            table, columns,
            columnsText, id,
            index, writePolicy,
            esId, esType,
            parallelism, isUpsert, tableName,
            writeMode, advanConf,
            primaryKey, rowKey, topic,
            customParams
        } = props.panelColumn[props.index];
        return {
            type: { value: parseInt(type) },
            sourceId: { value: sourceId },
            table: { value: table },
            'table-input': { value: table },
            columns: { value: columns },
            columnsText: { value: columnsText },
            id: { value: id },
            index: { value: index },
            writePolicy: { value: writePolicy },
            esId: { value: esId },
            esType: { value: esType },
            topic: { value: topic },
            isUpsert: { value: isUpsert },
            writeMode: { value: writeMode },
            advanConf: { value: advanConf },
            parallelism: { value: parallelism },
            tableName: { value: tableName },
            primaryKey: { value: primaryKey },
            'primaryKey-input': { value: primaryKey },
            rowKey: { value: rowKey },
            ...generateMapValues(customParams)
        }
    }
})(OutputOrigin);

const initialData: any = {
    tabTemplate: [], // 模版存储,所有输出源(记录个数)
    panelActiveKey: [], // 输出源是打开或关闭状态
    popoverVisible: [], // 删除显示按钮状态
    panelColumn: [], // 存储数据
    checkFormParams: [], // 存储要检查的参数from
    originOptionType: [], // 数据源选择数据
    topicOptionType: [], // topic 数据
    tableOptionType: [], // 表选择数据
    tableColumnOptionType: []// 表字段选择的类型
}

export default class OutputPanel extends React.Component<any, any> {
    constructor (props: any) {
        super(props)
        this.state = {
            tabTemplate: [], // 模版存储,所有输出源(记录个数)
            panelActiveKey: [], // 输出源是打开或关闭状态
            popoverVisible: [], // 删除显示按钮状态
            panelColumn: [], // 存储数据
            checkFormParams: [], // 存储要检查的参数from
            originOptionType: [], // 数据源选择数据
            tableOptionType: [], // 表选择数据
            topicOptionType: [], // topic 列表
            tableColumnOptionType: []// 表字段选择的类型
        }
    }

    componentDidMount () {
        const { sink } = this.props.currentPage;
        if (sink && sink.length > 0) {
            /**
             * 初始化
             */
            this.currentInitData(sink)
        }
    }

    currentInitData = (sink: any) => {
        const { tabTemplate, panelColumn } = this.state;
        sink.map((v: any, index: any) => {
            tabTemplate.push('OutputForm');
            initCustomParam(v);
            panelColumn.push(v);
            this.getTypeOriginData(index, v.type);
            if (haveTableList(v.type)) {
                this.getTableType(index, v.sourceId)
                if (haveTableColumn(v.type)) {
                    this.getTableColumns(index, v.sourceId, v.table)
                }
            }
            if (haveTopic(v.type)) {
                this.getTopicType(index, v.sourceId)
            }
        })
        this.setOutputData({ tabTemplate, panelColumn })
        this.setState({
            tabTemplate,
            panelColumn
        })
    }

    getCurrentData = (taskId: any, nextProps: any) => {
        const { currentPage, outputData, dispatch } = nextProps;
        const { sink } = currentPage;
        if (!outputData[taskId] && sink.length > 0) {
            this.receiveState(taskId, sink, dispatch)
        } else {
            const copyInitialData = JSON.parse(JSON.stringify(initialData));
            const data = outputData[taskId] || copyInitialData;
            this.setState({ ...data })
        }
    }

    receiveState = (taskId: any, sink: any, dispatch: any) => {
        const tabTemplate: any = [];
        const panelColumn: any = [];
        const panelActiveKey: any = [];
        const popoverVisible: any = [];
        const checkFormParams: any = [];
        const originOptionType: any = [];
        const tableOptionType: any = [];
        const tableColumnOptionType: any = [];
        const topicOptionType: any = [];

        sink.map((v: any) => {
            tabTemplate.push('OutputForm');
            panelColumn.push(v);
        })
        dispatch(BrowserAction.setOutputData({
            taskId,
            sink: {
                tabTemplate,
                panelColumn,
                panelActiveKey,
                popoverVisible,
                checkFormParams,
                originOptionType,
                tableOptionType,
                tableColumnOptionType,
                topicOptionType
            }
        }));
        this.setState({
            tabTemplate,
            panelColumn,
            panelActiveKey,
            popoverVisible,
            checkFormParams,
            originOptionType,
            tableOptionType,
            tableColumnOptionType,
            topicOptionType
        }, () => {
            sink.map((v: any, index: any) => {
                this.getTypeOriginData(index, v.type)
                if (haveTableList(v.type)) {
                    this.getTableType(index, v.sourceId)
                    if (haveTableColumn(v.type)) {
                        this.getTableColumns(index, v.sourceId, v.table)
                    }
                }
                if (haveTopic(v.type)) {
                    this.getTopicType(index, v.sourceId)
                }
            })
        })
    }

    /**
     * 获取数据源列表
     */
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
            this.setOutputData({ originOptionType });
            this.setState({
                originOptionType
            })
        })
    }

    /**
     * 获取表列表
     */
    getTableType = (index: any, sourceId: any) => {
        const { tableOptionType } = this.state;
        if (sourceId) {
            Api.getStremTableType({ sourceId, 'isSys': false }).then((v: any) => {
                if (index === 'add') {
                    if (v.code === 1) {
                        tableOptionType.push(v.data)
                    } else {
                        tableOptionType.push([])
                    }
                } else {
                    if (v.code === 1) {
                        tableOptionType[index] = v.data;
                    } else {
                        tableOptionType[index] = [];
                    }
                }
                this.setOutputData({ tableOptionType });
                this.setState({
                    tableOptionType
                })
            })
        } else {
            if (index === 'add') {
                tableOptionType.push([]);
            } else {
                tableOptionType[index] = [];
            }
            this.setOutputData({ tableOptionType });
            this.setState({
                tableOptionType
            })
        }
    }

    /**
     * 获取表字段列表
     */
    getTableColumns = (index: any, sourceId: any, tableName: any) => {
        const { tableColumnOptionType } = this.state;
        if (!sourceId || !tableName) {
            return;
        }
        Api.getStreamTableColumn({ sourceId, tableName }).then((v: any) => {
            if (v.code === 1) {
                tableColumnOptionType[index] = v.data;
            } else {
                tableColumnOptionType[index] = []
            }
            this.setOutputData({ tableColumnOptionType })
            this.setState({
                tableColumnOptionType
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
                // this.setOutputData({ topicOptionType });
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
            // this.setOutputData({ topicOptionType });
            this.setState({
                topicOptionType
            })
        }
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps: any) {
        const currentPage = nextProps.currentPage
        const oldPage = this.props.currentPage
        if (currentPage.id !== oldPage.id) {
            this.getCurrentData(currentPage.id, nextProps)
            this.setState({
                sync: true
            })
        }
    }

    changeInputTabs = (type: any, index?: any) => {
        const inputData: any = {
            type: DATA_SOURCE.MYSQL,
            columns: [],
            sourceId: undefined,
            table: undefined,
            columnsText: undefined,
            esId: undefined,
            esType: undefined,
            writePolicy: undefined,
            index: undefined,
            id: undefined,
            parallelism: 1,
            tableName: undefined,
            primaryKey: undefined,
            rowKey: undefined
        }
        let {
            tabTemplate, panelActiveKey, popoverVisible,
            panelColumn, checkFormParams, originOptionType,
            tableOptionType, tableColumnOptionType, topicOptionType
        } = this.state;
        if (type === 'add') {
            tabTemplate.push('OutputForm');
            panelColumn.push(inputData);
            this.getTypeOriginData('add', inputData.type);
            this.getTableType('add', inputData.table);
            this.getTopicType('add', inputData.sourceId);
            tableColumnOptionType.push([]);
            let pushIndex = `${tabTemplate.length}`;
            panelActiveKey.push(pushIndex)
        } else {
            tabTemplate.splice(index, 1);
            panelColumn.splice(index, 1);
            originOptionType.splice(index, 1);
            topicOptionType.splice(index, 1);
            tableOptionType.splice(index, 1);
            tableColumnOptionType.splice(index, 1);
            checkFormParams.pop();
            panelActiveKey = this.changeActiveKey(index);
            popoverVisible[index] = false;
        }
        this.props.tableParamsChange()// 添加数据改变标记
        this.setOutputData({
            tabTemplate,
            panelActiveKey,
            popoverVisible,
            panelColumn,
            tableColumnOptionType,
            topicOptionType
        });
        this.setState({
            tabTemplate,
            panelActiveKey,
            popoverVisible,
            panelColumn,
            checkFormParams,
            originOptionType,
            tableOptionType,
            topicOptionType,
            tableColumnOptionType
        })
    }

    setOutputData = (data: any) => {
        const { dispatch, currentPage } = this.props;
        const dispatchSource: any = { ...this.state, ...data };
        dispatch(BrowserAction.setOutputData({ taskId: currentPage.id, sink: dispatchSource }));
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
        this.setOutputData({ panelActiveKey })
        this.setState({
            panelActiveKey
        })
    }

    tableColumnType = (index: any, column: any) => {
        const { tableColumnOptionType } = this.state;
        const filterColumn = tableColumnOptionType[index].filter((v: any) => {
            return v.key === column
        })
        return filterColumn[0].type
    }

    filterPrimaryKey = (columns: any, primaryKeys: any) => { // 删除导致原始的primaryKey不存在
        return primaryKeys.filter((v: any) => {
            let flag = false;
            columns.map((value: any) => {
                if (value.column === v) {
                    flag = true
                }
            })
            return flag;
        })
    }
    getAllColumn (index: number) {
        const { tableColumnOptionType } = this.state;
        const columns = tableColumnOptionType[index] || [];
        return columns.map((column: { key: string; type: string }) => {
            return {
                column: column.key,
                type: column.type
            }
        })
    }
    /**
     * 监听数据改变
     * @param {String} type 改变的属性
     * @param {String} index 改变的panel序号
     */
    handleInputChange = (type: any, index: any, value: any, subValue: any) => {
        const {
            panelColumn, originOptionType, tableOptionType,
            tableColumnOptionType, topicOptionType
        } = this.state;
        let shouldUpdateEditor = true;
        if (type === 'columns') {
            panelColumn[index][type].push(value);
        } else if (type === 'deleteColumn') {
            panelColumn[index]['columns'].splice(value, 1);
            const filterPrimaryKeys = this.filterPrimaryKey(panelColumn[index]['columns'], panelColumn[index].primaryKey || []);
            panelColumn[index].primaryKey = filterPrimaryKeys;
        } else if (type === 'subColumn') {
            panelColumn[index]['columns'][value].column = subValue;
            const subType = this.tableColumnType(index, subValue);
            panelColumn[index]['columns'][value].type = subType;
        } else if (type == 'addAllColumn') {
            panelColumn[index]['columns'] = this.getAllColumn(index);
        } else if (type == 'deleteAllColumn') {
            panelColumn[index]['columns'] = [];
        } else if (type == 'customParams') {
            changeCustomParams(panelColumn[index], value, subValue);
        } else {
            panelColumn[index][type] = value;
        }
        if (type === 'columnsText') {
            // this.parseColumnsText(index,value)
        }
        const allParamsType: any = [
            'type', 'sourceId',
            'table', 'columns',
            'columnsText', 'id',
            'index', 'writePolicy',
            'esId', 'esType', 'topic',
            'parallelism', 'tableName', 'writeMode', 'advanConf',
            'primaryKey', 'rowKey', 'customParams'
        ];
        const sourceType = panelColumn[index].type;
        /**
         * 这里开始处理改变操作，比如数据源改变要改变重置表名等
         */
        if (type === 'type') {
            originOptionType[index] = [];
            tableOptionType[index] = [];
            tableColumnOptionType[index] = [];
            topicOptionType[index] = [];
            allParamsType.map((v: any) => {
                if (v === 'type') {
                    panelColumn[index][v] = value;
                    // if (value == DATA_SOURCE.POSTGRESQL) {
                    //     panelColumn[index]['isUpsert'] = false;
                    // } else {
                    //     panelColumn[index]['isUpsert'] = undefined;
                    // }
                } else if (v == 'parallelism') {
                    panelColumn[index][v] = 1
                } else if (v == 'columns') {
                    panelColumn[index][v] = [];
                } else {
                    panelColumn[index][v] = undefined
                }
            })
            // this.clearCurrentInfo(type,index,value)
            this.getTypeOriginData(index, value);
        } else if (type === 'sourceId') {
            tableOptionType[index] = [];
            tableColumnOptionType[index] = [];
            topicOptionType[index] = []; // 清空topic列表
            allParamsType.map((v: any) => {
                if (v !== 'type' && v != 'sourceId' && v != 'customParams') {
                    if (v == 'columns' || v == 'topic') {
                        panelColumn[index][v] = [];
                    } else if (v == 'parallelism') {
                        panelColumn[index][v] = 1
                    } else {
                        panelColumn[index][v] = undefined
                    }
                }
            })
            if (haveTableList(sourceType)) {
                this.getTableType(index, value)
            }
            if (haveTopic(sourceType)) {
                this.getTopicType(index, value)
            }
        } else if (type === 'table') {
            tableColumnOptionType[index] = [];
            const { sourceId } = panelColumn[index];
            panelColumn[index].columns = [];
            allParamsType.map((v: any) => {
                if (v != 'type' && v != 'sourceId' && v != 'table' && v != 'customParams') {
                    if (v == 'columns') {
                        panelColumn[index][v] = [];
                    } else if (v == 'parallelism') {
                        panelColumn[index][v] = 1
                    } else {
                        panelColumn[index][v] = undefined
                    }
                }
            })
            if (haveTableColumn(panelColumn[index].type)) {
                this.getTableColumns(index, sourceId, value)
            }
        } else {
            shouldUpdateEditor = false;
        }
        this.props.tableParamsChange()// 添加数据改变标记
        this.setOutputData({ panelColumn })
        this.setState({
            panelColumn,
            sync: shouldUpdateEditor
        })
    }

    clearCurrentInfo = (type: any, index: any, value: any) => {
        const { panelColumn, tableOptionType, originOptionType, topicOptionType } = this.state;
        const inputData: any = {
            type: undefined,
            columns: [],
            sourceId: undefined,
            table: undefined,
            columnsText: undefined,
            esId: undefined,
            esType: undefined,
            writePolicy: undefined,
            index: undefined,
            id: undefined,
            parallelism: 1,
            topic: undefined,
            tableName: undefined,
            rowKey: undefined,
            primaryKey: undefined
        }
        if (type === 'type') {
            inputData.type = value;
            originOptionType[index] = [];
            tableOptionType[index] = [];
            topicOptionType[index] = [];
            panelColumn[index] = inputData;
        } else if (type === 'sourceId') {
            inputData.type = panelColumn[index]['type']
            inputData.sourceId = value;
            tableOptionType[index] = [];
            topicOptionType[index] = [];
            panelColumn[index] = inputData;
        }
        this.setOutputData({ panelColumn, topicOptionType, tableOptionType, originOptionType })
        this.setState({ panelColumn, topicOptionType, tableOptionType, originOptionType });
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
        this.setOutputData({ popoverVisible })
        this.setState({ popoverVisible });
    }

    panelHeader = (index: any) => {
        const { popoverVisible } = this.state;
        const popoverContent = <div className="input-panel-title">
            <div style={{ padding: '8 0 12' }}> <Icon type="exclamation-circle" style={{ color: '#faad14' }} />  你确定要删除此结果表吗？</div>
            <div style={{ textAlign: 'right', padding: '0 0 8' }}>
                <Button style={{ marginRight: 8 }} size="small" onClick={() => { this.handlePopoverVisibleChange(null, index, false) }}>取消</Button>
                <Button type="primary" size="small" onClick={() => { this.changeInputTabs('delete', index) }}>确定</Button>
            </div>
        </div>
        const onClickFix = {
            onClick: (e: any) => { this.handlePopoverVisibleChange(e, index, !popoverVisible[index]) }
        }
        return <div className="input-panel-title">
            <span>{` 结果表 ${index + 1} `}</span>
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
        this.setOutputData({ checkFormParams })
        this.setState({
            checkFormParams
        })
    }

    render () {
        const {
            tabTemplate, panelActiveKey, panelColumn, originOptionType,
            tableOptionType, tableColumnOptionType, topicOptionType, sync
        } = this.state;
        const { isShow, currentPage, isLocked } = this.props;
        return (
            <div className="m-taksdetail panel-content">
                <Collapse activeKey={panelActiveKey} bordered={false} onChange={this.handleActiveKey} >
                    {
                        tabTemplate.map((OutputPutOrigin: any, index: any) => {
                            return (
                                <Panel header={this.panelHeader(index)} key={index + 1} style={{ borderRadius: 5, position: 'relative' }} className="input-panel">
                                    <OutputForm
                                        isShow={panelActiveKey.indexOf(index + 1 + '') > -1 && isShow}
                                        sync={sync}
                                        index={index}
                                        handleInputChange={this.handleInputChange}
                                        panelColumn={panelColumn} originOptionType={originOptionType}
                                        tableOptionType={tableOptionType}
                                        tableColumnOptionType={tableColumnOptionType}
                                        topicOptionType={topicOptionType}
                                        onRef={this.recordForm}
                                        editorParamsChange={this.props.editorParamsChange}
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
                <Button disabled={isLocked} className="stream-btn" onClick={() => { this.changeInputTabs('add') }} style={{ borderRadius: 5 }}><Icon type="plus" /><span> 添加结果表</span></Button>
            </div>
        )
    }
}
