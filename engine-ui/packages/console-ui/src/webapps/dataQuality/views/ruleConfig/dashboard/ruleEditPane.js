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
    changeMonitorStatus(params) {
        dispatch(ruleConfigActions.changeMonitorStatus(params));
    },
    getMonitorDetail(params) {
        dispatch(ruleConfigActions.getMonitorDetail(params));
    }
})

@connect(mapStateToProps, mapDispatchToProps)
export default class RuleEditPane extends Component {
    constructor(props) {
        super(props);
        this.state = {
            currentRule: {},
            SQLFields: ['customizeSql', 'verifyType', 'operator', 'threshold'],
            columnFields: ['columnName', 'functionId', 'verifyType', 'operator', 'threshold'],
            rules: [],
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
            })

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
                            <Popconfirm title="确定要删除吗？" onConfirm={() => this.delete(record.id)}>
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
                this.renderEditTD(text, record, type)
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

    renderEditTD = (text, record, type) => {
        const { getFieldDecorator } = this.props.form;
        const { sourceColumn } = this.props.dataSource;
        const { monitorFunction } = this.props.ruleConfig;
        const { allDict } = this.props.common;
        const { currentRule } = this.state;

        switch(type) {
            case 'columnName': {
                if (record.isCustomizeSql) {
                    return <FormItem {...rowFormItemLayout} className="rule-edit-td">
                        {
                            getFieldDecorator('customizeSql', {
                                rules: [{
                                    required: true, message: '自定义SQL不可为空！',
                                }],
                                initialValue: record.customizeSql
                            })(
                                <Input onChange={this.changeRuleParams.bind(this, 'customizeSql')}/>
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
                                initialValue: record.columnName
                            })(
                                <Select 
                                    style={{ width: '100%' }} 
                                    onChange={this.changeRuleParams.bind(this, 'columnName')} 
                                    disabled={record.isTable}>
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
                            initialValue: record.functionId
                        })(
                            <Select 
                                style={{ width: '100%' }} 
                                onChange={this.changeRuleParams.bind(this, 'functionId')}>
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
                            initialValue: record.filter
                        })(
                            <Input onChange={this.changeRuleParams.bind(this, 'filter')}/>
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
                            initialValue: record.verifyType
                        })(
                            <Select 
                                style={{ width: '100%' }} 
                                onChange={this.changeRuleParams.bind(this, 'verifyType')}>
                                {
                                    allDict.verifyType.map((item) => {
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
                            initialValue: record.operator
                        })(
                            <Select 
                                style={{ width: 70, marginRight: 10 }} 
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
                            initialValue: record.threshold
                        })(
                            <InputNumber
                              min={0}
                              max={100}
                              style={{ marginRight: 10 }}
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
        const { monitorFunction } = this.props.ruleConfig;
        const { allDict } = this.props.common;

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
                return allDict.verifyType[text-1].name || undefined;
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
    delete(id) {
        let newData = [...this.state.rules],
            target  = newData.filter(item => id === item.id)[0],
            index   = newData.indexOf(target);
        
        if (target) {
            newData.splice(index, 1);
            this.setState({ 
                rules: newData
            });
        }
    }

    // 保存
    save(id) {
        const { currentRule, SQLFields, columnFields, monitorPart } = this.state;
        let newData = [...this.state.rules],
            target  = newData.filter(item => id === item.id)[0],
            index   = newData.indexOf(target),
            fields  = currentRule.isCustomizeSql ? SQLFields : columnFields;

        this.props.form.validateFields(fields, { force: true }, (err, values) => {
            console.log(err,values)
            if(!err) {
                if (currentRule.editStatus) {
                    currentRule.monitorId = monitorPart.monitorId;
                    console.log(currentRule)
                    // delete currentRule.editStatus;
                }
                // delete currentRule.editable;
                // newData[index] = currentRule;

                this.setState({ 
                    // currentRule: [],
                    // rules: newData
                });

                RCApi.saveMonitorRule({

                })
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
            id: firstId ? firstId + 1 : 1,
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
            id: firstId ? firstId + 1 : 1,
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
            id: firstId ? firstId + 1 : 1,
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
                        <Button type="primary" >立即执行</Button>
                        <Button className="m-l-8" type="primary">编辑执行信息</Button>
                        <Button className="m-l-8" type="primary" onClick={this.props.changeRuleStatus}>关闭检测</Button>
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

