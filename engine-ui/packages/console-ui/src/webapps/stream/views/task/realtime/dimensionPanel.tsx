import * as React from 'react';
import {
    Row,
    Col,
    Icon,
    Table,
    Input,
    Select,
    Collapse,
    Button,
    Popover,
    Form,
    Switch,
    Tooltip,
    InputNumber,
    Popconfirm
} from 'antd';
import { debounce, isEmpty } from 'lodash';
import utils from 'utils';
import Api from '../../../api';
import * as BrowserAction from '../../../store/modules/realtimeTask/browser';
import { DATA_SOURCE } from '../../../comm/const';
import { haveTableList, haveCustomParams, haveTableColumn } from './sidePanel/panelCommonUtil';

import Editor from 'widgets/code-editor';
import { CustomParams, generateMapValues, changeCustomParams, initCustomParam } from './sidePanel/customParams';
import { switchPartition, primaryKeyFilter, queryFault } from '../../../views/helpDoc/docs';
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
        return result;
    };
    componentDidUpdate () {
        this.refreshEditor();
    }
    originOption = (type: any, arrData: any) => {
        switch (type) {
            case 'originType':
                return arrData.map((v: any) => {
                    return (
                        <Option key={v} value={`${v.id}`}>
                            {v.name}
                        </Option>
                    );
                });
            case 'currencyType':
                return arrData.map((v: any) => {
                    return (
                        <Option key={v} value={`${v}`}>
                            {v}
                        </Option>
                    );
                });
            case 'columnType':
                return arrData.map((v: any, index: any) => {
                    return (
                        <Option key={index} value={`${v.key}`}>
                            {v.key}
                        </Option>
                    );
                });
            case 'primaryType':
                return arrData.map((v: any, index: any) => {
                    return (
                        <Option key={index} value={`${v.column}`}>
                            {v.column}
                        </Option>
                    );
                });
            default:
                return null;
        }
    }

    editorParamsChange (a: any, b: any, c: any) {
        const { handleInputChange, index, textChange } = this.props;
        textChange();
        handleInputChange('columnsText', index, b);
        // this.props.editorParamsChange(...arguments);
    }
    debounceEditorChange = debounce(this.editorParamsChange, 300, { 'maxWait': 2000 })
    render () {
        const {
            handleInputChange,
            index,
            sync,
            originOptionType,
            tableOptionType,
            panelColumn,
            tableColumnOptionType,
            isShow
        } = this.props;
        const { getFieldDecorator } = this.props.form;
        const originOptionTypes = this.originOption(
            'originType',
            originOptionType[index] || []
        );
        const tableOptionTypes = this.originOption(
            'currencyType',
            tableOptionType[index] || []
        );
        const tableColumnOptionTypes = this.originOption(
            'columnType',
            tableColumnOptionType[index] || []
        );
        const primaryKeyOptionTypes = this.originOption(
            'primaryType',
            panelColumn[index].columns || []
        );
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
        const targetColText = '别名指字段的别名，如select  order_sales as order_amont from  shop_order，order_sales字段的别名即为order_amont';
        return (
            <Row className="title-content">
                <FormItem {...formItemLayout} label="存储类型">
                    {getFieldDecorator('type', {
                        rules: [{ required: true, message: '请选择存储类型' }]
                    })(
                        <Select
                            className="right-select"
                            onChange={(v: any) => {
                                handleInputChange('type', index, v);
                            }}
                            showSearch
                            filterOption={(input: any, option: any) =>
                                option.props.children
                                    .toLowerCase()
                                    .indexOf(input.toLowerCase()) >= 0
                            }
                        >
                            <Option value={DATA_SOURCE.MYSQL}>MySQL</Option>
                            <Option value={DATA_SOURCE.ORACLE}>Oracle</Option>
                            {/* <Option value={DATA_SOURCE.POSTGRESQL}>PostgreSQL</Option> */}
                            <Option value={DATA_SOURCE.KUDU}>Kudu</Option>
                            <Option value={DATA_SOURCE.HBASE}>HBase</Option>
                            <Option value={DATA_SOURCE.REDIS}>Redis</Option>
                            <Option value={DATA_SOURCE.MONGODB}>MongoDB</Option>
                            {/* <Option value="11">ElasticSearch</Option> */}
                        </Select>
                    )}
                </FormItem>
                <FormItem {...formItemLayout} label="数据源">
                    {getFieldDecorator('sourceId', {
                        initialValue: 'disabled',
                        rules: [{ required: true, message: '请选择数据源' }]
                    })(
                        <Select
                            className="right-select"
                            onChange={(v: any) => {
                                handleInputChange('sourceId', index, v);
                            }}
                            showSearch
                            filterOption={(input: any, option: any) =>
                                option.props.children
                                    .toLowerCase()
                                    .indexOf(input.toLowerCase()) >= 0
                            }
                        >
                            {originOptionTypes}
                        </Select>
                    )}
                </FormItem>
                {(() => {
                    switch (panelColumn[index].type) {
                        case DATA_SOURCE.REDIS: {
                            return (
                                <FormItem
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
                                </FormItem>
                            )
                        }
                        default: {
                            return (
                                <FormItem {...formItemLayout} label="表">
                                    {getFieldDecorator('table', {
                                        rules: [{ required: true, message: '请选择表' }]
                                    })(
                                        <Select
                                            className="right-select"
                                            onChange={(v: any) => {
                                                handleInputChange('table', index, v);
                                            }}
                                            showSearch
                                            filterOption={(input: any, option: any) =>
                                                option.props.children
                                                    .toLowerCase()
                                                    .indexOf(input.toLowerCase()) >= 0
                                            }
                                        >
                                            {tableOptionTypes}
                                        </Select>
                                    )}
                                </FormItem>
                            )
                        }
                    }
                })()}
                <FormItem {...formItemLayout} label="映射表">
                    {getFieldDecorator('tableName', {
                        rules: [
                            { required: true, message: '请输入映射表名' }
                        ]
                    })(
                        <Input
                            placeholder="请输入映射表名"
                            onChange={(e: any) =>
                                handleInputChange(
                                    'tableName',
                                    index,
                                    e.target.value
                                )
                            }
                        />
                    )}
                </FormItem>
                <Row>
                    <div className="ant-form-item-label ant-col-xs-24 ant-col-sm-6 required-tip">
                        <label className='required-tip'>字段</label>
                    </div>
                    {haveTableColumn(panelColumn[index].type)
                        ? <Col span={18} style={{ marginBottom: 20 }}>
                            <div style={{ textAlign: 'right', padding: '8px 5px 5px 0px' }}>
                                <a onClick={() => { handleInputChange('addAllColumn', index) }} style={{ marginRight: 5 }}>导入全部字段</a>
                                <Popconfirm title="确认清空所有字段？" onConfirm={() => { handleInputChange('deleteAllColumn', index) }} okText="确认" cancelText="取消">
                                    <a>清空</a>
                                </Popconfirm>
                            </div>
                            <div className="bd">
                                <Table
                                    dataSource={panelColumn[index].columns}
                                    className="table-small"
                                    pagination={false}
                                    size="small"
                                >
                                    <Column
                                        title="字段"
                                        dataIndex="column"
                                        key="字段"
                                        width="40%"
                                        render={(text: any, record: any, subIndex: any) => {
                                            return (
                                                <Select
                                                    className="sub-right-select"
                                                    value={text}
                                                    onChange={(v: any) => {
                                                        handleInputChange(
                                                            'subColumn',
                                                            index,
                                                            subIndex,
                                                            v
                                                        );
                                                    }}
                                                    showSearch
                                                    filterOption={(input: any, option: any) =>
                                                        option.props.children
                                                            .toLowerCase()
                                                            .indexOf(
                                                                input.toLowerCase()
                                                            ) >= 0
                                                    }
                                                >
                                                    {tableColumnOptionTypes}
                                                </Select>
                                            );
                                        }}
                                    />
                                    <Column
                                        title="类型"
                                        dataIndex="type"
                                        key="类型"
                                        width="30%"
                                        render={(text: any, record: any, subIndex: any) => {
                                            return <Input value={text} disabled />;
                                        }}
                                    />
                                    <Column
                                        title={
                                            <div>
                                                <Tooltip placement="top" title={targetColText} arrowPointAtCenter>
                                                    <span>别名 &nbsp;
                                                        <Icon type="question-circle-o" />
                                                    </span>
                                                </Tooltip>
                                            </div>
                                        }
                                        dataIndex="targetCol"
                                        key="别名"
                                        width="30%"
                                        render={(text: any, record: any, subIndex: any) => {
                                            return <Input
                                                value={text}
                                                onChange={(e: any) =>
                                                    handleInputChange(
                                                        'targetCol',
                                                        index,
                                                        subIndex,
                                                        e.target.value
                                                    )
                                                }
                                            />;
                                        }}
                                    />
                                    <Column
                                        key="delete"
                                        render={(text: any, record: any, subIndex: any) => {
                                            return (
                                                <Icon
                                                    type="close"
                                                    style={{
                                                        fontSize: 16,
                                                        color: '#888'
                                                    }}
                                                    onClick={() => {
                                                        handleInputChange(
                                                            'deleteColumn',
                                                            index,
                                                            subIndex
                                                        );
                                                    }}
                                                />
                                            );
                                        }}
                                    />
                                </Table>
                                <div style={{ padding: '0 20 20' }}>
                                    <Button
                                        className="stream-btn"
                                        type="dashed"
                                        style={{ borderRadius: 5 }}
                                        onClick={() => {
                                            handleInputChange('columns', index, {});
                                        }}
                                    >
                                        <Icon type="plus" />
                                        <span> 添加输入</span>
                                    </Button>
                                </div>
                            </div>
                        </Col> : (
                            <Col span={18} style={{ marginBottom: 20, height: 200 }}>
                                {isShow && (
                                    <Editor
                                        style={{
                                            minHeight: 202,
                                            border: '1px solid #ddd',
                                            height: '100%'
                                        }}
                                        key="params-editor"
                                        sync={sync}
                                        placeholder={'字段 类型, 比如 id int 一行一个字段'}
                                        // options={jsonEditorOptions}
                                        value={panelColumn[index].columnsText}
                                        onChange={this.debounceEditorChange.bind(this)}
                                        editorRef={(ref: any) => {
                                            this._editorRef = ref;
                                        }}
                                    />
                                )}
                            </Col>)}
                </Row>
                {(() => {
                    switch (panelColumn[index].type) {
                        case DATA_SOURCE.KUDU:
                        case DATA_SOURCE.POSTGRESQL:
                        case DATA_SOURCE.ORACLE:
                        case DATA_SOURCE.MYSQL: {
                            return (
                                <FormItem {...formItemLayout} label="主键">
                                    {getFieldDecorator('primaryKey', {
                                        rules: [{ required: true, message: '请选择主键' }]
                                    })(
                                        <Select
                                            className="right-select"
                                            onChange={(v: any) => {
                                                handleInputChange('primaryKey', index, v);
                                            }}
                                            mode="multiple"
                                            showSearch
                                            filterOption={(input: any, option: any) =>
                                                option.props.children
                                                    .toLowerCase()
                                                    .indexOf(input.toLowerCase()) >= 0
                                            }
                                        >
                                            {primaryKeyOptionTypes}
                                        </Select>
                                    )}
                                </FormItem>
                            )
                        }
                        case DATA_SOURCE.MONGODB: {
                            return (
                                <FormItem {...formItemLayout} label="主键">
                                    {getFieldDecorator('primaryKey-input', {
                                        rules: [{ required: true, message: '请选择主键' }]
                                    })(
                                        <Input
                                            placeholder="请输入主键"
                                            onChange={(e: any) =>
                                                handleInputChange(
                                                    'primaryKey',
                                                    index,
                                                    e.target.value
                                                )
                                            }
                                        />
                                    )}
                                </FormItem>
                            )
                        }
                        case DATA_SOURCE.REDIS: {
                            return (
                                <FormItem {...formItemLayout} label="主键">
                                    {getFieldDecorator('primaryKey-input', {
                                        rules: [{ required: true, message: '请选择主键' }]
                                    })(
                                        <Input
                                            placeholder="结果表主键，多个字段用英文逗号隔开"
                                            onChange={(e: any) =>
                                                handleInputChange(
                                                    'primaryKey',
                                                    index,
                                                    e.target.value
                                                )
                                            }
                                        />
                                    )}
                                </FormItem>
                            )
                        }
                        case DATA_SOURCE.HBASE: {
                            return (
                                <FormItem {...formItemLayout} label="主键">
                                    {getFieldDecorator('hbasePrimaryKey', {
                                        rules: [{ required: true, message: '请输入主键' }]
                                    })(
                                        <Input
                                            placeholder="请输入主键"
                                            onChange={(e: any) =>
                                                handleInputChange(
                                                    'hbasePrimaryKey',
                                                    index,
                                                    e.target.value
                                                )
                                            }
                                        />
                                    )}
                                </FormItem>
                            )
                        }
                        default: {
                            return null;
                        }
                    }
                })()}
                <FormItem {...formItemLayout} label="并行度">
                    {getFieldDecorator('parallelism')(
                        <InputNumber
                            className="number-input"
                            min={1}
                            onChange={(value: any) =>
                                handleInputChange('parallelism', index, value)
                            }
                        />
                    )}
                </FormItem>
                <FormItem {...formItemLayout} label="缓存策略">
                    {getFieldDecorator('cache', {
                        rules: [{ required: true, message: '请选择缓存策略' }]
                    })(
                        <Select
                            placeholder="请选择"
                            className="right-select"
                            onChange={(v: any) => {
                                handleInputChange('cache', index, v);
                            }}
                            showSearch
                            filterOption={(input: any, option: any) =>
                                option.props.children
                                    .toLowerCase()
                                    .indexOf(input.toLowerCase()) >= 0
                            }
                        >
                            <Option key="None" value="None">
                                None
                            </Option>
                            <Option key="LRU" value="LRU">
                                LRU
                            </Option>
                            <Option key="ALL" value="ALL">
                                ALL
                            </Option>
                        </Select>
                    )}
                </FormItem>
                {/* eslint-disable */}
                {panelColumn[index].cache === 'LRU' ? ([
                    <FormItem {...formItemLayout} label="缓存大小(行)">
                        {getFieldDecorator('cacheSize', {
                            rules: [
                                { required: true, message: '请输入缓存大小' }
                                // { validator: this.checkConfirm }
                            ]
                        })(
                            <InputNumber
                                className="number-input"
                                min={0}
                                onChange={(value: any) =>
                                    handleInputChange('cacheSize', index, value)
                                }
                            />
                        )}
                    </FormItem>,
                    <FormItem {...formItemLayout} label="缓存超时时间(ms)">
                        {getFieldDecorator('cacheTTLMs', {
                            rules: [
                                {
                                    required: true,
                                    message: '请输入缓存超时时间'
                                }
                                // { validator: this.checkConfirm }
                            ]
                        })(
                            <InputNumber
                                className="number-input"
                                min={0}
                                onChange={(value: any) =>
                                    handleInputChange(
                                        'cacheTTLMs',
                                        index,
                                        value
                                    )
                                }
                            />
                        )}
                    </FormItem>,
                    <FormItem {...formItemLayout}
                        label={(
                            <span >
                                开启分区&nbsp;
                                <Tooltip title={switchPartition}>
                                    <Icon type="question-circle-o" />
                                </Tooltip>
                            </span>)}
                    >
                        {getFieldDecorator('partitionedJoin', {})(
                            <Switch
                                defaultChecked={panelColumn[index].partitionedJoin}
                                onChange={(checked: any) =>
                                    handleInputChange('partitionedJoin', index, checked)
                                }
                            />
                        )}
                    </FormItem>
                ]) : (
                        undefined
                    )}

                {/* eslint-enable */}
                {panelColumn[index].cache === 'ALL'
                    ? (
                        <>
                            <FormItem {...formItemLayout}
                                label={(
                                    <span >
                                        按主键过滤&nbsp;
                                        <Tooltip title={primaryKeyFilter}>
                                            <Icon type="question-circle-o" />
                                        </Tooltip>
                                    </span>)}
                            >
                                {getFieldDecorator('keyFilter', {})(
                                    <Switch
                                        defaultChecked={panelColumn[index].keyFilter}
                                        onChange={(checked: any) =>
                                            handleInputChange('keyFilter', index, checked)
                                        }
                                    />
                                )}
                            </FormItem>
                            {
                                panelColumn[index].keyFilter ? (
                                    <React.Fragment>
                                        <FormItem {...formItemLayout} label="主键最小值">
                                            {getFieldDecorator('lowerBoundPrimaryKey', {
                                                rules: [
                                                    {
                                                        required: true,
                                                        message: '请输入主键最小值'
                                                    }
                                                ]
                                            })(
                                                <InputNumber
                                                    className="number-input"
                                                    min={0}
                                                    onChange={(value: any) =>
                                                        handleInputChange(
                                                            'lowerBoundPrimaryKey',
                                                            index,
                                                            value
                                                        )
                                                    }
                                                />
                                            )}
                                        </FormItem>
                                        <FormItem {...formItemLayout} label="主键最大值">
                                            {getFieldDecorator('upperBoundPrimaryKey', {
                                                rules: [
                                                    {
                                                        required: true,
                                                        message: '请输入主键最大值'
                                                    }
                                                ]
                                            })(
                                                <InputNumber
                                                    className="number-input"
                                                    min={0}
                                                    onChange={(value: any) =>
                                                        handleInputChange(
                                                            'upperBoundPrimaryKey',
                                                            index,
                                                            value
                                                        )
                                                    }
                                                />
                                            )}
                                        </FormItem>
                                    </React.Fragment>
                                ) : null
                            }
                            <FormItem {...formItemLayout} label="缓存超时时间(ms)">
                                {getFieldDecorator('cacheTTLMs', {
                                    rules: [
                                        {
                                            required: true,
                                            message: '请输入缓存超时时间'
                                        }
                                    ]
                                })(
                                    <InputNumber
                                        className="number-input"
                                        min={0}
                                        onChange={(value: any) =>
                                            handleInputChange(
                                                'cacheTTLMs',
                                                index,
                                                value
                                            )
                                        }
                                    />
                                )}
                            </FormItem>
                        </>
                    ) : undefined}
                {
                    panelColumn[index].type == DATA_SOURCE.KUDU ? (
                        <>
                            <FormItem {...formItemLayout}
                                label={(
                                    <span >
                                        查询容错&nbsp;
                                        <Tooltip title={queryFault}>
                                            <Icon type="question-circle-o" />
                                        </Tooltip>
                                    </span>)}
                            >
                                {getFieldDecorator('isFaultTolerant', {})(
                                    <Switch
                                        defaultChecked={panelColumn[index].isFaultTolerant}
                                        onChange={(checked: any) =>
                                            handleInputChange('isFaultTolerant', index, checked)
                                        }
                                    />
                                )}
                            </FormItem>
                            <FormItem
                                {...formItemLayout}
                                label="高级配置"
                            >
                                {getFieldDecorator('advanConf')(
                                    <TextArea placeholder="以JSON格式添加高级参数"
                                        style={{ minHeight: '100px' }}
                                        onChange={(e: any) => { handleInputChange('advanConf', index, e.target.value) }}
                                    />
                                )}
                            </FormItem>
                        </>
                    ) : null
                }
                {haveCustomParams(panelColumn[index].type) && <CustomParams
                    getFieldDecorator={getFieldDecorator}
                    formItemLayout={formItemLayout}
                    customParams={customParams}
                    onChange={(type: any, id: any, value: any) => { handleInputChange('customParams', index, value, { id, type }) }}
                />}
            </Row>
        );
    }
}

const OutputForm = Form.create({
    mapPropsToFields (props: any) {
        const {
            type,
            sourceId,
            table,
            columns,
            parallelism,
            columnsText,
            partitionedJoin,
            cache,
            cacheSize,
            hbasePrimaryKey,
            lowerBoundPrimaryKey,
            upperBoundPrimaryKey,
            keyFilter,
            isFaultTolerant,
            cacheTTLMs,
            tableName,
            primaryKey,
            customParams
        } = props.panelColumn[props.index];
        return {
            type: { value: parseInt(type) },
            sourceId: { value: sourceId },
            table: { value: table },
            'table-input': { value: table },
            tableName: { value: tableName },
            columns: { value: columns },
            parallelism: { value: parallelism },
            columnsText: { value: columnsText },
            partitionedJoin: { value: partitionedJoin },
            lowerBoundPrimaryKey: { value: lowerBoundPrimaryKey },
            upperBoundPrimaryKey: { value: upperBoundPrimaryKey },
            keyFilter: { value: keyFilter },
            isFaultTolerant: { value: isFaultTolerant },
            cache: { value: cache },
            cacheSize: { value: cacheSize },
            cacheTTLMs: { value: cacheTTLMs },
            primaryKey: { value: primaryKey },
            'primaryKey-input': { value: primaryKey },
            hbasePrimaryKey: { value: hbasePrimaryKey },
            ...generateMapValues(customParams)
        };
    }
})(OutputOrigin);

const initialData: any = {
    tabTemplate: [], // 模版存储,所有输出源(记录个数)
    panelActiveKey: [], // 输出源是打开或关闭状态
    popoverVisible: [], // 删除显示按钮状态
    panelColumn: [], // 存储数据
    checkFormParams: [], // 存储要检查的参数from
    originOptionType: [], // 数据源选择数据
    tableOptionType: [], // 表选择数据
    tableColumnOptionType: [] // 表字段选择的类型
};

export default class OutputPanel extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            tabTemplate: [], // 模版存储,所有输出源(记录个数)
            panelActiveKey: [], // 输出源是打开或关闭状态
            popoverVisible: [], // 删除显示按钮状态
            panelColumn: [], // 存储数据
            checkFormParams: [], // 存储要检查的参数from
            originOptionType: [], // 数据源选择数据
            tableOptionType: [], // 表选择数据
            tableColumnOptionType: [] // 表字段选择的类型
        };
    }

    componentDidMount () {
        const { side } = this.props.currentPage;
        if (side && side.length > 0) {
            this.currentInitData(side);
        }
    }

    currentInitData = (side: any) => {
        const { tabTemplate, panelColumn } = this.state;
        side.map((v: any, index: any) => {
            tabTemplate.push('OutputForm');
            initCustomParam(v)
            panelColumn.push(v);
            this.getTypeOriginData(index, v.type);
            if (haveTableList(v.type)) {
                this.getTableType(index, v.sourceId);
                if (v.type == DATA_SOURCE.MYSQL) {
                    this.getTableColumns(index, v.sourceId, v.table);
                }
            }
        });
        this.setOutputData({ tabTemplate, panelColumn });
        this.setState({
            tabTemplate,
            panelColumn
        });
    };

    getCurrentData = (taskId: any, nextProps: any) => {
        const { currentPage, dimensionData, dispatch } = nextProps;
        const { side } = currentPage;
        if (!dimensionData[taskId] && side.length > 0) {
            this.receiveState(taskId, side, dispatch);
        } else {
            const copyInitialData = JSON.parse(JSON.stringify(initialData));
            const data = dimensionData[taskId] || copyInitialData;
            this.setState({ ...data });
        }
    };

    receiveState = (taskId: any, side: any, dispatch: any) => {
        const tabTemplate: any = [];
        const panelColumn: any = [];
        const panelActiveKey: any = [];
        const popoverVisible: any = [];
        const checkFormParams: any = [];
        const originOptionType: any = [];
        const tableOptionType: any = [];
        const tableColumnOptionType: any = [];
        side.map((v: any) => {
            tabTemplate.push('OutputForm');
            panelColumn.push(v);
        });
        dispatch(
            BrowserAction.setDimensionData({
                taskId,
                side: {
                    tabTemplate,
                    panelColumn,
                    panelActiveKey,
                    popoverVisible,
                    checkFormParams,
                    originOptionType,
                    tableOptionType,
                    tableColumnOptionType
                }
            })
        );
        this.setState(
            {
                tabTemplate,
                panelColumn,
                panelActiveKey,
                popoverVisible,
                checkFormParams,
                originOptionType,
                tableOptionType,
                tableColumnOptionType
            },
            () => {
                side.map((v: any, index: any) => {
                    this.getTypeOriginData(index, v.type);
                    if (haveTableList(v.type)) {
                        this.getTableType(index, v.sourceId);
                        if (v.type == DATA_SOURCE.MYSQL) {
                            this.getTableColumns(index, v.sourceId, v.table);
                        }
                    }
                });
            }
        );
    };

    getTypeOriginData = (index: any, type: any) => {
        const { originOptionType } = this.state;
        Api.getTypeOriginData({ type }).then((v: any) => {
            if (index === 'add') {
                if (v.code === 1) {
                    originOptionType.push(v.data);
                } else {
                    originOptionType.push([]);
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
            });
        });
    };

    getTableType = (index: any, sourceId: any, type?: any) => {
        const { tableOptionType } = this.state;
        if (sourceId) {
            Api.getStremTableType({ sourceId, isSys: false }).then((v: any) => {
                if (index === 'add') {
                    if (v.code === 1) {
                        tableOptionType.push(v.data);
                    } else {
                        tableOptionType.push([]);
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
                });
            });
        } else {
            if ((index == 'add')) {
                tableOptionType.push([]);
            } else {
                tableOptionType[index] = [];
            }
            this.setOutputData({ tableOptionType });
            this.setState({
                tableOptionType
            });
        }
    };

    getTableColumns = (index: any, sourceId: any, tableName: any) => {
        const { tableColumnOptionType } = this.state;
        if (!sourceId || !tableName) {
            return;
        }
        Api.getStreamTableColumn({ sourceId, tableName }).then((v: any) => {
            if (v.code === 1) {
                tableColumnOptionType[index] = v.data;
            } else {
                tableColumnOptionType[index] = [];
            }
            this.setOutputData({ tableColumnOptionType });
            this.setState({
                tableColumnOptionType
            });
        });
    };

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        const currentPage = nextProps.currentPage;
        const oldPage = this.props.currentPage;
        if (currentPage.id !== oldPage.id) {
            this.getCurrentData(currentPage.id, nextProps);
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
            tableName: undefined,
            primaryKey: undefined,
            hbasePrimaryKey: undefined,
            parallelism: 1,
            cache: 'LRU',
            cacheSize: 10000,
            cacheTTLMs: 60000
        };
        let {
            tabTemplate,
            panelActiveKey,
            popoverVisible,
            panelColumn,
            checkFormParams,
            originOptionType,
            tableOptionType,
            tableColumnOptionType
        } = this.state;
        if (type === 'add') {
            tabTemplate.push('OutputForm');
            panelColumn.push(inputData);
            this.getTypeOriginData('add', inputData.type);
            this.getTableType('add', inputData.table);
            tableColumnOptionType.push([]);
            let pushIndex = `${tabTemplate.length}`;
            panelActiveKey.push(pushIndex);
        } else {
            tabTemplate.splice(index, 1);
            panelColumn.splice(index, 1);
            originOptionType.splice(index, 1);
            tableOptionType.splice(index, 1);
            tableColumnOptionType.splice(index, 1);
            checkFormParams.pop();
            panelActiveKey = this.changeActiveKey(index);
            popoverVisible[index] = false;
        }
        this.props.tableParamsChange(); // 添加数据改变标记
        this.setOutputData({
            tabTemplate,
            panelActiveKey,
            popoverVisible,
            panelColumn,
            checkFormParams,
            originOptionType,
            tableOptionType,
            tableColumnOptionType
        });
        this.setState({
            tabTemplate,
            panelActiveKey,
            popoverVisible,
            panelColumn,
            checkFormParams,
            originOptionType,
            tableOptionType,
            tableColumnOptionType
        });
    };

    setOutputData = (data: any) => {
        const { dispatch, currentPage } = this.props;
        const dispatchSource: any = { ...this.state, ...data };
        dispatch(
            BrowserAction.setDimensionData({
                taskId: currentPage.id,
                side: dispatchSource
            })
        );
    };

    changeActiveKey = (index: any) => {
        // 删除导致key改变,处理被改变key的值
        const { panelActiveKey } = this.state;
        const deleteActiveKey = `${index + 1}`;
        const deleteActiveKeyIndex = panelActiveKey.indexOf(deleteActiveKey);
        if (deleteActiveKeyIndex > -1) {
            panelActiveKey.splice(deleteActiveKeyIndex, 1);
        }
        return panelActiveKey.map((v: any) => {
            return Number(v) > Number(index) ? `${Number(v) - 1}` : v;
        });
    };

    handleActiveKey = (key: any) => {
        let { panelActiveKey } = this.state;
        panelActiveKey = key;
        this.setOutputData({ panelActiveKey });
        this.setState({
            panelActiveKey
        });
    };

    tableColumnType = (index: any, column: any) => {
        const { tableColumnOptionType } = this.state;
        console.log('tableColumnOptionType', tableColumnOptionType);

        const filterColumn = tableColumnOptionType[index].filter((v: any) => {
            return v.key === column;
        });
        return filterColumn[0].type;
    };

    filterPrimaryKey = (columns: any, primaryKeys: any) => {
        // 删除导致原始的primaryKey不存在
        console.log('primaryKeys', primaryKeys);
        return primaryKeys.filter((v: any) => {
            let flag = false;
            columns.map((value: any) => {
                if (value.column === v) {
                    flag = true;
                }
            });
            return flag;
        });
    };

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

    handleInputChange = (type: any, index: any, value: any, subValue: any) => {
        // 监听数据改变
        let shouldUpdateEditor = true;
        const {
            panelColumn,
            originOptionType,
            tableOptionType,
            tableColumnOptionType
        } = this.state;
        if (type === 'columns') {
            panelColumn[index][type].push(value);
        } else if (type === 'deleteColumn') {
            panelColumn[index]['columns'].splice(value, 1);
            const filterPrimaryKeys = this.filterPrimaryKey(
                panelColumn[index]['columns'],
                panelColumn[index].primaryKey || []
            );
            panelColumn[index].primaryKey = filterPrimaryKeys;
        } else if (type === 'subColumn') {
            panelColumn[index]['columns'][value].column = subValue;
            const subType = this.tableColumnType(index, subValue);
            panelColumn[index]['columns'][value].type = subType;
        } else if (type === 'targetCol') {
            // 去除空格汉字
            const reg = /[\u4E00-\u9FA5]|[\uFE30-\uFFA0]/gi;
            let val = subValue;
            if (subValue) {
                val = utils.removeAllSpaces(subValue);
                if (reg.test(val)) {
                    val = subValue.replace(reg, '');
                }
            } else {
                val = undefined
            }
            panelColumn[index]['columns'][value].targetCol = val;
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
            'type',
            'sourceId',
            'table',
            'columns',
            'columnsText',
            'parallelism',
            'cache',
            'cacheSize',
            'hbasePrimaryKey',
            'cacheTTLMs',
            'tableName',
            'primaryKey',
            'customParams'
        ];
        if (type === 'type') {
            originOptionType[index] = [];
            tableOptionType[index] = [];
            tableColumnOptionType[index] = [];
            allParamsType.map((v: any) => {
                if (v != 'type') {
                    if (v == 'parallelism') {
                        panelColumn[index][v] = 1;
                    } else if (v == 'columns') {
                        panelColumn[index][v] = [];
                    } else if (v == 'cache') {
                        panelColumn[index][v] = 'LRU';
                    } else if (v == 'cacheSize') {
                        panelColumn[index][v] = 10000;
                    } else if (v == 'cacheTTLMs') {
                        panelColumn[index][v] = 60000;
                    } else {
                        panelColumn[index][v] = undefined;
                    }
                }
            });
            // this.clearCurrentInfo(type,index,value)
            this.getTypeOriginData(index, value);
        } else if (type === 'sourceId') {
            tableOptionType[index] = [];
            tableColumnOptionType[index] = [];
            panelColumn[index].columns = [];

            allParamsType.map((v: any) => {
                if (v != 'type' && v != 'sourceId' && v != 'customParams') {
                    if (v == 'parallelism') {
                        panelColumn[index][v] = 1;
                    } else if (v == 'columns') {
                        panelColumn[index][v] = [];
                    } else if (v == 'cache') {
                        panelColumn[index][v] = 'LRU';
                    } else if (v == 'cacheSize') {
                        panelColumn[index][v] = 10000;
                    } else if (v == 'cacheTTLMs') {
                        panelColumn[index][v] = 60000;
                    } else {
                        panelColumn[index][v] = undefined;
                    }
                }
            });
            // this.clearCurrentInfo(type,index,value)
            if (haveTableList(panelColumn[index].type)) {
                this.getTableType(index, value, type);
            }
        } else if (type === 'table') {
            tableColumnOptionType[index] = [];
            allParamsType.map((v: any) => {
                if (v != 'type' && v != 'sourceId' && v != 'table' && v != 'customParams') {
                    if (v == 'parallelism') {
                        panelColumn[index][v] = 1;
                    } else if (v == 'columns') {
                        panelColumn[index][v] = [];
                    } else if (v == 'cache') {
                        panelColumn[index][v] = 'LRU';
                    } else if (v == 'cacheSize') {
                        panelColumn[index][v] = 10000;
                    } else if (v == 'cacheTTLMs') {
                        panelColumn[index][v] = 60000;
                    } else {
                        panelColumn[index][v] = undefined;
                    }
                }
            });
            const { sourceId } = panelColumn[index];
            if (haveTableColumn(panelColumn[index].type)) {
                this.getTableColumns(index, sourceId, value);
            }
        } else {
            shouldUpdateEditor = false;
        }
        this.props.tableParamsChange(); // 添加数据改变标记
        this.setOutputData({ panelColumn });
        this.setState({
            panelColumn,
            sync: shouldUpdateEditor
        });
    };

    clearCurrentInfo = (type: any, index: any, value: any) => {
        const { panelColumn, originOptionType, tableOptionType } = this.state;
        const inputData: any = {
            type: undefined,
            columns: [],
            sourceId: undefined,
            table: undefined,
            tableName: undefined,
            parallelism: 1,
            cache: 'LRU',
            cacheSize: 10000,
            cacheTTLMs: 60000
        };
        if (type === 'type') {
            inputData.type = value;
            panelColumn[index] = inputData;
            originOptionType[index] = [];
            tableOptionType[index] = [];
        } else if (type === 'sourceId') {
            inputData.type = panelColumn[index]['type'];
            inputData.sourceId = value;
            panelColumn[index] = inputData;
            tableOptionType[index] = [];
        }
        this.setOutputData({ panelColumn, originOptionType, tableOptionType });
        this.setState({ panelColumn, originOptionType, tableOptionType });
    };

    handlePopoverVisibleChange = (e: any, index: any, visible: any) => {
        let { popoverVisible } = this.state;
        popoverVisible[index] = visible;
        if (e) {
            e.stopPropagation(); // 阻止删除按钮点击后冒泡到panel
            if (visible) {
                // 只打开一个Popover提示
                popoverVisible = popoverVisible.map((v: any, i: any) => {
                    return index == i;
                });
            }
        }
        this.setOutputData({ popoverVisible });
        this.setState({ popoverVisible });
    };

    panelHeader = (index: any) => {
        const { popoverVisible } = this.state;
        const popoverContent = (
            <div className="input-panel-title">
                <div style={{ padding: '8 0 12' }}>
                    {' '}
                    <Icon
                        type="exclamation-circle"
                        style={{ color: '#faad14' }}
                    />{' '}
                    你确定要删除此维表吗？
                </div>
                <div style={{ textAlign: 'right', padding: '0 0 8' }}>
                    <Button
                        style={{ marginRight: 8 }}
                        size="small"
                        onClick={() => {
                            this.handlePopoverVisibleChange(null, index, false);
                        }}
                    >
                        取消
                    </Button>
                    <Button
                        type="primary"
                        size="small"
                        onClick={() => {
                            this.changeInputTabs('delete', index);
                        }}
                    >
                        确定
                    </Button>
                </div>
            </div>
        );
        const onClickFix = {
            onClick: (e: any) => {
                this.handlePopoverVisibleChange(
                    e,
                    index,
                    !popoverVisible[index]
                );
            }
        }
        return (
            <div className="input-panel-title">
                <span>{` 维表 ${index + 1}`}</span>
                <Popover
                    trigger="click"
                    placement="topLeft"
                    content={popoverContent}
                    visible={popoverVisible[index]}
                    // onClick={(e: any) => {
                    //     this.handlePopoverVisibleChange(
                    //         e,
                    //         index,
                    //         !popoverVisible[index]
                    //     );
                    // }}
                    {...onClickFix}
                >
                    <span className="title-icon input-panel-title">
                        <Icon type="delete" />
                    </span>
                </Popover>
            </div>
        );
    };

    recordForm = (ref: any) => {
        // 存储子组建的所有要检查的form表单
        const { checkFormParams } = this.state;
        checkFormParams.push(ref);
        this.setOutputData({ checkFormParams });
        this.setState({
            checkFormParams
        });
    };

    render () {
        const {
            tabTemplate,
            panelActiveKey,
            panelColumn,
            originOptionType,
            tableOptionType,
            tableColumnOptionType,
            sync
        } = this.state;
        const { isShow, currentPage, isLocked } = this.props;
        return (
            <div className="m-taksdetail panel-content">
                <Collapse
                    activeKey={panelActiveKey}
                    bordered={false}
                    onChange={this.handleActiveKey}
                >
                    {tabTemplate.map((OutputPutOrigin: any, index: any) => {
                        return (
                            <Panel
                                header={this.panelHeader(index)}
                                key={index + 1}
                                style={{ borderRadius: 5, position: 'relative' }}
                                className="input-panel"
                            >
                                <OutputForm
                                    isShow={panelActiveKey.indexOf(index + 1 + '') > -1 && isShow}
                                    index={index}
                                    sync={sync}
                                    handleInputChange={this.handleInputChange}
                                    panelColumn={panelColumn}
                                    originOptionType={originOptionType}
                                    tableOptionType={tableOptionType}
                                    tableColumnOptionType={
                                        tableColumnOptionType
                                    }
                                    onRef={this.recordForm}
                                    editorParamsChange={
                                        this.props.editorParamsChange
                                    }
                                    textChange={() => {
                                        this.setState({
                                            sync: false
                                        })
                                    }}
                                />
                                <LockPanel lockTarget={currentPage} />
                            </Panel>
                        );
                    })}
                </Collapse>
                <Button
                    disabled={isLocked}
                    className="stream-btn"
                    onClick={() => {
                        this.changeInputTabs('add');
                    }}
                    style={{ borderRadius: 5 }}
                >
                    <Icon type="plus" />
                    <span>添加维表</span>
                </Button>
            </div>
        );
    }
}
