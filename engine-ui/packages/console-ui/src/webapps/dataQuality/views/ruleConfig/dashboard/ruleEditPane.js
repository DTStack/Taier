import React, { Component } from 'react';
import { connect } from 'react-redux';
import { isEmpty } from 'lodash';
import { Button, Form, Select, Input, Row, Col, Table, message, Popconfirm, InputNumber } from 'antd';
import { ruleConfigActions } from '../../../actions/ruleConfig';
import { dataSourceActions } from '../../../actions/dataSource';

import { formItemLayout, rowFormItemLayout } from '../../../consts';
import DSApi from '../../../api/dataSource';
import RCApi from '../../../api/ruleConfig';

const FormItem = Form.Item;
const Option = Select.Option;

const mapStateToProps = state => {
    const { ruleConfig, dataSource, common } = state;
    return { ruleConfig, dataSource, common }
}

const mapDispatchToProps = dispatch => ({
    getRuleFunction(params) {
        dispatch(ruleConfigActions.getRuleFunction(params));
    },
    getDataSourcesColumn(params) {
        dispatch(dataSourceActions.getDataSourcesColumn(params));
    },
    getMonitorRule(params) {
        dispatch(ruleConfigActions.getMonitorRule(params));
    },
    getMonitorDetail(params) {
        dispatch(ruleConfigActions.getMonitorDetail(params));
    },
    changeMonitorStatus(params) {
        dispatch(ruleConfigActions.changeMonitorStatus(params));
    },
    executeMonitor(params) {
        dispatch(ruleConfigActions.executeMonitor(params));
    },
    
})

@connect(mapStateToProps, mapDispatchToProps)
export default class RuleEditPane extends Component {
    constructor(props) {
        super(props);
        this.state = {
            SQLFields: ['customizeSql', 'verifyType', 'operator', 'threshold'],
            columnFields: ['columnName', 'functionId', 'verifyType', 'operator', 'threshold'],
            rules: [],
            currentRule: {},
            monitorPart: {},
            detail: {},
        };
    }

    componentDidMount() {
        console.log(this.props.data, 'mount')
    }

    componentWillReceiveProps(nextProps) {
        let oldData = this.props.data,
            newData = nextProps.data;

        if (isEmpty(oldData) && !isEmpty(newData)) {
            console.log(this.props,nextProps, 'havedata')
            this.props.getRuleFunction();
            this.props.getDataSourcesColumn({
                sourceId: newData.dataSourceType,
                tableName: newData.tableName
            });
            this.props.getMonitorDetail({
                monitorId: newData.monitorPartVOS[0].monitorId
            });

            RCApi.getMonitorRule({
                monitorId: newData.monitorPartVOS[0].monitorId
            }).then((res) => {
                if (res.code === 1) {
                    this.setState({
                        rules: res.data
                    });
                }
            });

            this.setState({
                monitorPart: newData.monitorPartVOS[0]
            });
        }
    }

    initColumns = () => {
        return [{
            title: '字段',
            dataIndex: 'columnName',
            key: 'columnName',
            render: (text, record) => this.renderColumns(text, record, 'columnName'),
            width: '15%',
        }, {
            title: '统计函数',
            dataIndex: 'functionId',
            key: 'functionId',
            render: (text, record) => this.renderColumns(text, record, 'functionId'),
            width: '15%',
        }, 
        {
            title: '过滤条件',
            dataIndex: 'filter',
            key: 'filter',
            render: (text, record) => this.renderColumns(text, record, 'filter'),
            width: '30%'
        }, {
            title: '校验方法',
            dataIndex: 'verifyType',
            key: 'verifyType',
            render: (text, record) => this.renderColumns(text, record, 'verifyType'),
            width: '15%',
        }, {
            title: '阈值配置',
            dataIndex: 'threshold',
            key: 'threshold',
            render: (text, record) => this.renderColumns(text, record, 'threshold'),
            width: '15%'
        }, {
            title: '操作',
            width: '10%',
            render: (text, record) => {
                const { editable } = record;
                return (
                    <div className="editable-row-operations">
                    {
                        editable ?
                        <span>
                            <a onClick={() => this.save(record.id)}>保存</a>
                            <a onClick={() => this.cancel(record.id)}>取消</a>
                        </span>
                        : 
                        <span>
                            <a onClick={() => this.edit(record.id)}>编辑</a>
                            <Popconfirm title="确定要删除吗？" onConfirm={() => this.delete(record)}>
                                <a>删除</a>
                            </Popconfirm>
                        </span>
                    }
                    </div>
                );
            },
        }]  

    }

    renderColumns(text, record, type) {
        const { currentRule } = this.state;
        let obj = {
            children: <Form layout="inline">
            {
                record.editable ?
                this.renderEditTD(text, type)
                :
                this.renderTD(text, record, type)
            }
            </Form>,
            props: {},
        };

        if (record.isCustomizeSql) {
            switch(type) {
                case 'columnName':
                    obj.props.colSpan = 3;
                    break;
                case 'functionId':
                    obj.props.colSpan = 0;
                    break;
                case 'filter':
                    obj.props.colSpan = 0;
                    break;
                default:
                    break;
            }
        }

        return obj;
    }

    changeRuleParams = (type, value) => {
        let obj = {};
        obj[type] = value.target ? value.target.value : value;

        this.setState({ currentRule: { ...this.state.currentRule, ...obj } });
        console.log(this.state,obj,'currentRule')
        // this.changeCurrentRule(obj);
    }

    renderEditTD = (text, type) => {
        const { form, dataSource, ruleConfig, common } = this.props;
        const { getFieldDecorator } = form;
        const { sourceColumn } = dataSource;
        const { monitorFunction } = ruleConfig;
        const { verifyType } = common.allDict;
        const { currentRule } = this.state;

        switch(type) {
            case 'columnName': {
                if (currentRule.isCustomizeSql) {
                    return <FormItem {...rowFormItemLayout} className="rule-edit-td">
                        {
                            getFieldDecorator('customizeSql', {
                                rules: [{
                                    required: true, message: '自定义SQL不可为空！',
                                }],
                                initialValue: currentRule.customizeSql
                            })(
                                <Input 
                                    onChange={this.changeRuleParams.bind(this, 'customizeSql')} 
                                    disabled={currentRule.editStatus === 'edit'} />
                            )
                        }
                    </FormItem>
                } else {
                    return (
                        <FormItem {...rowFormItemLayout} className="rule-edit-td">
                        {
                            getFieldDecorator('columnName', {
                                rules: [{
                                    required: true, message: '字段不可为空！',
                                }],
                                initialValue: currentRule.columnName
                            })(
                                <Select 
                                    style={{ width: '100%' }} 
                                    onChange={this.changeRuleParams.bind(this, 'columnName')} 
                                    disabled={currentRule.isTable || currentRule.editStatus === 'edit'}>
                                    {
                                        sourceColumn.map((item) => {
                                            return <Option key={item.key} value={item.key}>
                                                {item.key}
                                            </Option>
                                        })
                                    }
                                </Select>
                            )
                        }
                        </FormItem>
                    )
                }
            }

            case 'functionId': {
                return (
                    <FormItem {...rowFormItemLayout} className="rule-edit-td">
                    {
                        getFieldDecorator('functionId', {
                            rules: [{
                                required: true, message: '统计函数不可为空！',
                            }],
                            initialValue: currentRule.functionId ? currentRule.functionId.toString() : undefined
                        })(
                            <Select 
                                style={{ width: '100%' }} 
                                onChange={this.changeRuleParams.bind(this, 'functionId')}
                                disabled={currentRule.editStatus === 'edit'}>
                                {
                                    monitorFunction.map((item) => {
                                        return <Option key={item.id} value={item.id.toString()}>
                                            {item.nameZc}
                                        </Option>
                                    })
                                }
                            </Select>
                        )
                    }
                    </FormItem>
                )
            }

            case 'filter': {
                return <FormItem {...rowFormItemLayout} className="rule-edit-td">
                    {
                        getFieldDecorator('filter', {
                            rules: [],
                            initialValue: currentRule.filter
                        })(
                            <Input 
                                onChange={this.changeRuleParams.bind(this, 'filter')} 
                                disabled={currentRule.editStatus === 'edit'} />
                        )
                    }
                </FormItem>
            }

            case 'verifyType': {
                return <FormItem {...rowFormItemLayout} className="rule-edit-td">
                    {
                        getFieldDecorator('verifyType', {
                            rules: [{
                                required: true, message: '校验方法不可为空！',
                            }],
                            initialValue: currentRule.verifyType ? currentRule.verifyType.toString() : undefined
                        })(
                            <Select 
                                style={{ width: '100%' }} 
                                onChange={this.changeRuleParams.bind(this, 'verifyType')}
                                disabled={currentRule.editStatus === 'edit'}>
                                {
                                    verifyType.map((item) => {
                                        return <Option key={item.value} value={item.value.toString()}>
                                            {item.name}
                                        </Option>
                                    })
                                }
                            </Select>
                        )
                    }
                </FormItem>
            }

            case 'threshold': {
                return <div>
                    <FormItem>
                    {
                        getFieldDecorator('operator', {
                            rules: [{
                                required: true, message: '阈值配置不可为空！',
                            }],
                            initialValue: currentRule.operator
                        })(
                            <Select 
                                style={{ width: 50, marginRight: 5 }} 
                                onChange={this.changeRuleParams.bind(this, 'operator')}>
                                <Option value=">"> {`>`} </Option>
                                <Option value=">="> {`>=`} </Option>
                                <Option value="="> {`=`} </Option>
                                <Option value="<"> {`<`} </Option>
                                <Option value="<="> {`<=`} </Option>
                                <Option value="!="> {`!=`} </Option>
                            </Select>
                        )
                    }
                    </FormItem>
                    <FormItem>
                    {
                        getFieldDecorator('threshold', {
                            rules: [{
                                required: true, message: '阈值不可为空！',
                            }],
                            initialValue: currentRule.threshold
                        })(
                            <InputNumber
                              min={0}
                              max={100}
                              style={{ width: 50, marginRight: 10 }}
                              onChange={this.changeRuleParams.bind(this, 'threshold')}
                            /> 
                        )
                    }
                    </FormItem>
                    {
                        currentRule.verifyType != 1
                        &&
                        <span style={{ height: 32, lineHeight: '32px' }}>%</span>
                    }
                </div>
            }
        }
    }

    // 固定的值
    renderTD = (text, record, type) => {
        const { ruleConfig, common } = this.props;
        const { monitorFunction } = ruleConfig;
        const { verifyType } = common.allDict;

        switch (type) {
            case 'columnName': {
                if (record.isCustomizeSql) {
                    return record.customizeSql;
                } else {
                    return text;
                }
            }

            case 'functionId': {
                return  record.functionName;
            }

            case 'verifyType': {
                return verifyType[text - 1].name || undefined;
            }

            case 'threshold': {
                return text ? `${record.operator}  ${text}` : 0;
            }

            default:
                return text;
        }
    }

    // 编辑
    edit(id) {
        let newData = [...this.state.rules],
            target = newData.filter(item => id === item.id)[0];

        if (target) {
            target.editable = true;
            target.editStatus = "edit";

            this.setState({ 
                currentRule: target,
                rules: newData
            });
        }
    }

    // 取消编辑
    cancel(id) {
        let newData = [...this.state.rules],
            target  = newData.filter(item => id === item.id)[0],
            index   = newData.indexOf(target);

        if (target.editStatus === 'edit') {
            delete target.editable;
            delete target.editStatus;
        } else {
            newData.splice(index, 1);
        }

        this.setState({ 
            currentRule: [],
            rules: newData
        });
    }

    // 删除
    delete(record) {
        const { monitorPart } = this.state;
        let newData = [...this.state.rules],
            target  = newData.filter(item => record.id === item.id)[0],
            index   = newData.indexOf(target);
        
        if (target) {
            RCApi.deleteMonitorRule({
                ruleIds: [record.id],
                monitorId: record.monitorId
            }).then((res) => {
                if (res.code === 1) {
                    message.success('删除成功');
                    RCApi.getMonitorRule({
                        monitorId: monitorPart.monitorId
                    }).then((res) => {
                        if (res.code === 1) {
                            this.setState({
                                rules: res.data
                            });
                        }
                    });
                }
            });
        }
    }

    // 保存
    save(id) {
        const { currentRule, SQLFields, columnFields, monitorPart } = this.state;
        let fields  = currentRule.isCustomizeSql ? SQLFields : columnFields;

        this.props.form.validateFields(fields, { force: true }, (err, values) => {
            console.log(err,values)
            if(!err) {
                if (!currentRule.editStatus) {
                    currentRule.monitorId = monitorPart.monitorId;
                }

                delete currentRule.editable;
                delete currentRule.editStatus;

                RCApi.saveMonitorRule({...currentRule}).then((res) => {
                    if (res.code === 1) {
                        message.success('保存成功');
                        RCApi.getMonitorRule({
                            monitorId: monitorPart.monitorId
                        }).then((res) => {
                            if (res.code === 1) {
                                this.setState({
                                    rules: res.data
                                });
                            }
                        });
                    }
                });

                this.setState({ 
                    currentRule: []
                });
            }
        });

    }

    addColumnRule = () => {
        let newData = [...this.state.rules],
            firstData = newData[0],
            firstId = firstData ? firstData.id : undefined;

        if (firstData && firstData.editable) {
            newData.shift();
            firstId = undefined;
        }

        let target = {
            id: undefined,
            // id: firstId ? firstId + 1 : 1,
            editable: true,
            isCustomizeSql: false,
            columnName: undefined,
            functionId: undefined,
            filter: '',
            verifyType: undefined,
            operator: undefined,
            threshold: undefined,
        };

        newData.unshift(target);
        this.setState({ 
            currentRule: target,
            rules: newData
        });
    }

    addSQLRule = () => {
        let newData = [...this.state.rules],
            firstData = newData[0],
            firstId = firstData ? firstData.id : undefined;

        if (firstData && firstData.editable) {
            newData.shift();
            firstId = undefined;
        }

        let target = {
            id: undefined,
            // id: firstId ? firstId + 1 : 1,
            editable: true,
            isCustomizeSql: true,
            customizeSql: undefined,
            filter: '',
            verifyType: undefined,
            operator: undefined,
            threshold: undefined,
        };
        newData.unshift(target);
        this.setState({ 
            currentRule: target,
            rules: newData
        });
    }

    addTableRule = () => {
        const { data } = this.props;
        let newData = [...this.state.rules],
            firstData = newData[0],
            firstId = firstData ? firstData.id : undefined;

        if (firstData && firstData.editable) {
            newData.shift();
            firstId = undefined;
        }

        let target = {
            id: undefined,
            // id: firstId ? firstId + 1 : 1,
            editable: true,
            isCustomizeSql: false,
            isTable: true,
            columnName: data.tableName,
            functionId: undefined,
            filter: '',
            verifyType: undefined,
            operator: undefined,
            threshold: undefined,
        };
        newData.unshift(target);
        this.setState({ 
            currentRule: target, 
            rules: newData 
        });
    }

    onMonitorIdChange = (value) => {

    }

    initMonitorInfoColumns = () => {
        return [{
            dataIndex: 'column1',
            className: 'column-key',
            key: 'column1',
            width: '12%',
        }, {
            dataIndex: 'value1',
            key: 'value1',  
            width: '34%',
        }, {
            dataIndex: 'column2',
            className: 'column-key',
            key: 'column2',
            width: '12%',
        }, {
            dataIndex: 'value2',
            key: 'value2',
            width: '34%',
        }]
    }

    executeMonitor = (monitorId) => {
        this.props.executeMonitor({ monitorId });
    }

    changeMonitorStatus = (monitorId) => {
        this.props.changeMonitorStatus({ monitorId });
    }

    render() {
        const { data, ruleConfig, form, common } = this.props;
        const { getFieldDecorator } = form;
        const { rules, monitorPart } = this.state;
        // const { executeTime, notifyUser, periodType, scheduleConf, sendTypes } = ruleConfig.monitorDetail;
        const { periodType, notifyType } = common.allDict;
        const { monitorDetail } = ruleConfig;

        let monitorPartVOS = data.monitorPartVOS ? data.monitorPartVOS : [];
        let monitorId = monitorPart.monitorId ? monitorPart.monitorId.toString() : '';

        let monitorInfoData = [{
            column1: '执行周期',
            value1: monitorDetail.periodType ? periodType[monitorDetail.periodType - 1].name : '',
            column2: '告警方式',
            value2: monitorDetail.sendTypes ? monitorDetail.sendTypes.map((item) => {

                        return notifyType[item - 1] ? notifyType[item - 1].name : ''
                        }).join('，') : '' 
        }, {
            column1: '执行时间',
            value1: monitorDetail.executeTime ? monitorDetail.executeTime : '',
            column2: '接收人',
            value2: monitorDetail.notifyUser ? monitorDetail.notifyUser.join('，') : ''
        }]

        return (
            <div className="rule-manage">
                <Row className="rule-action">
                    <Col span={12} className="txt-left">
                        {
                            monitorPartVOS.length > 1
                            &&
                            <div>
                                分区：
                                <Select 
                                    value={monitorId}
                                    style={{ width: 150 }}
                                    onChange={this.onMonitorIdChange}>
                                    {
                                        monitorPartVOS.map((item) => {
                                            return <Option key={item.monitorId} value={item.monitorId.toString()}>{item.partValue}</Option>
                                        })
                                    }
                                </Select>
                            </div>
                        }
                    </Col>

                    <Col span={12}>
                        <Button type="primary" onClick={this.executeMonitor.bind(this, monitorId)}>立即执行</Button>
                        <Button className="m-l-8" type="primary" onClick={this.editMonitorInfo}>编辑执行信息</Button>
                        <Button className="m-l-8" type="primary" onClick={this.changeMonitorStatus.bind(this, monitorId)}>关闭检测</Button>
                    </Col>
                </Row>

                <Table 
                    rowKey="column1"
                    bordered
                    className="m-table monitor-info-table"
                    columns={this.initMonitorInfoColumns()}
                    showHeader={false}
                    pagination={false}
                    dataSource={monitorInfoData}
                />

                <div className="rule-action">
                    <Button type="primary" onClick={this.addTableRule}>添加表级规则</Button>
                    <Button className="m-l-8" type="primary" onClick={this.addColumnRule}>添加字段级规则</Button>
                    <Button className="m-l-8" type="primary" onClick={this.addSQLRule}>添加自定义SQL</Button>
                </div>

                <Table 
                    rowKey="id"
                    className="m-table rule-edit-table"
                    columns={this.initColumns()}
                    pagination={false}
                    dataSource={rules}
                    onChange={this.onTableChange}
                />
            </div>
        );
    }
}
RuleEditPane = Form.create()(RuleEditPane);

