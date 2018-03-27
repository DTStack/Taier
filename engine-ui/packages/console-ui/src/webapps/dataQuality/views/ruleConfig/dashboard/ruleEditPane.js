import React, { Component } from 'react';
import { connect } from 'react-redux';
import { isEmpty } from 'lodash';
import { Button, Form, Select, Input, Row, Col, Table, message, Popconfirm, InputNumber, Modal } from 'antd';
import { ruleConfigActions } from '../../../actions/ruleConfig';
import { dataSourceActions } from '../../../actions/dataSource';

import ExecuteForm from './executeForm';
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
    getMonitorDetail(params) {
        dispatch(ruleConfigActions.getMonitorDetail(params));
    },
    changeMonitorStatus(params) {
        dispatch(ruleConfigActions.changeMonitorStatus(params));
    },
    executeMonitor(params) {
        dispatch(ruleConfigActions.executeMonitor(params));
    }
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
            monitorId: undefined,
            showExecuteModal: false
        };
    }

    componentWillReceiveProps(nextProps) {
        let oldData = this.props.data,
            newData = nextProps.data;

        if (isEmpty(oldData) && !isEmpty(newData)) {
            let monitorId = newData.monitorPartVOS[0].monitorId;

            if (monitorId) {
                this.initData(monitorId, newData);
                this.setState({ monitorId });
            }

        }
    }

    initData = (monitorId, data) => {
        this.props.getMonitorDetail({ monitorId });
        RCApi.getMonitorRule({ monitorId }).then((res) => {
            if (res.code === 1) {
                this.setState({
                    rules: res.data
                });
            }
        });
        this.props.getRuleFunction();
        this.props.getDataSourcesColumn({
            sourceId: data.sourceId,
            tableName: data.tableName
        });
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
        const { form, dataSource, ruleConfig, common } = this.props;
        const { getFieldDecorator } = form;
        const { sourceColumn } = dataSource;
        const { monitorFunction } = ruleConfig;
        const { verifyType } = common.allDict;
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
                                <Input 
                                    onChange={this.changeRuleParams.bind(this, 'customizeSql')} 
                                    disabled={record.editStatus === 'edit'} />
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
                                    disabled={record.isTable || record.editStatus === 'edit'}>
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
                            initialValue: record.functionId ? record.functionId.toString() : undefined
                        })(
                            <Select 
                                style={{ width: '100%' }} 
                                onChange={this.changeRuleParams.bind(this, 'functionId')}
                                disabled={record.editStatus === 'edit'}>
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
                            <Input 
                                onChange={this.changeRuleParams.bind(this, 'filter')} 
                                disabled={record.editStatus === 'edit'} />
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
                            initialValue: record.verifyType ? record.verifyType.toString() : undefined
                        })(
                            <Select 
                                style={{ width: '100%' }} 
                                onChange={this.changeRuleParams.bind(this, 'verifyType')}
                                disabled={record.editStatus === 'edit'}>
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
                            initialValue: record.operator
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
                            initialValue: record.threshold
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
                return text ? verifyType.filter(item => parseInt(text) === item.value)[0].name : undefined;
            }

            case 'threshold': {
                let value = `${record.operator}  ${text}`;
                return record.verifyType == 1 ? `${value} %` : value;
            }

            default:
                return text;
        }
    }

    // 编辑
    edit(id) {
        const { currentRule, rules } = this.state;

        let newData = [...rules],
            target  = newData.filter(item => id === item.id)[0];

        if (!isEmpty(currentRule)) {
            if (currentRule.editStatus === 'edit') {
                delete currentRule.editable
                delete currentRule.editStatus
            } else {
                newData.shift();
            }
        }

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
            currentRule: {},
            rules: newData
        });
    }

    // 删除
    delete(record) {
        const { monitorId } = this.state;
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
                    RCApi.getMonitorRule({ monitorId }).then((res) => {
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
        const { currentRule, SQLFields, columnFields, monitorId } = this.state;
        let fields  = currentRule.isCustomizeSql ? SQLFields : columnFields;

        this.props.form.validateFields(fields, { force: true }, (err, values) => {
            console.log(err,values)
            if(!err) {
                if (currentRule.editStatus === 'new') {
                    currentRule.monitorId = monitorId;
                    delete currentRule.id;
                }

                delete currentRule.editable;
                delete currentRule.editStatus;

                RCApi.saveMonitorRule({...currentRule}).then((res) => {
                    if (res.code === 1) {
                        message.success('保存成功');
                        RCApi.getMonitorRule({
                            monitorId: monitorId
                        }).then((res) => {
                            if (res.code === 1) {
                                this.setState({
                                    rules: res.data
                                });
                            }
                        });
                    }
                });

                this.setState({ currentRule: {} });
            }
        });

    }

    addNewRule = (type) => {
        const { form, data } = this.props;
        const { currentRule, rules } = this.state;

        let newData = [...rules];

        if (!isEmpty(currentRule)) {
            if (currentRule.editStatus === "edit") {
                delete currentRule.editable
                delete currentRule.editStatus
            } else {
                newData.shift();
                form.resetFields();
                this.setState({ currentRule: {} });
            }
        }

        let target = {
            id: 0,
            editStatus: 'new',
            editable: true,
            filter: '',
            verifyType: undefined,
            operator: undefined,
            threshold: undefined,
        };

        switch (type) {
            case 'column':
                target.isCustomizeSql = false;
                target.columnName = undefined;
                target.functionId = undefined;
                break;
            case 'SQL':
                target.isCustomizeSql = true;
                target.customizeSql = undefined;
                break;
            case 'table':
                target.isCustomizeSql = false;
                target.columnName = data.tableName;
                target.functionId = undefined;
                break;
            default:
                break;
        }

        newData.unshift(target);
        this.setState({ 
            currentRule: target,
            rules: newData 
        });
    }

    onMonitorIdChange = (value) => {
        const { data } = this.props;
        let monitorId = value;

        this.props.getMonitorDetail({ monitorId });
        RCApi.getMonitorRule({ monitorId }).then((res) => {
            if (res.code === 1) {
                this.setState({
                    rules: res.data
                });
            }
        });
        this.setState({ monitorId });
    }

    executeMonitor = (monitorId) => {
        this.props.executeMonitor({ monitorId });
        this.props.closeSlidePane();
    }

    changeMonitorStatus = (monitorId) => {
        RCApi.changeMonitorStatus({ monitorId }).then((res) => {
            if (res.code === 1) {
                message.success('操作成功');
                this.props.getMonitorDetail({ monitorId });
            }
        });
        // this.props.changeMonitorStatus({ monitorId });
    }

    openExecuteModal = () => {
        this.setState({
            showExecuteModal: true
        });
    }

    closeExecuteModal = (updated) => {
        console.log(updated)
        const { monitorId } = this.state;

        if (updated) {
            this.props.getMonitorDetail({ monitorId });
        }

        this.setState({
            showExecuteModal: false
        });
    }

    render() {
        const { data, ruleConfig, form, common } = this.props;
        const { getFieldDecorator } = form;
        const { rules, monitorId, showExecuteModal } = this.state;
        // const { executeTime, notifyUser, periodType, scheduleConf, sendTypes } = ruleConfig.monitorDetail;
        const { periodType, notifyType } = common.allDict;
        const { monitorDetail } = ruleConfig;

        let monitorPartVOS = data.monitorPartVOS ? data.monitorPartVOS : [];

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
                                    value={monitorId ? monitorId.toString() : undefined}
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
                        <Button 
                            type="primary" 
                            onClick={this.executeMonitor.bind(this, monitorId)}>
                            立即执行
                        </Button>
                        <Button 
                            className="m-l-8" 
                            type="primary" 
                            onClick={this.openExecuteModal}>
                            编辑执行信息
                        </Button>
                        <Button 
                            className="m-l-8" 
                            type="primary" 
                            onClick={this.changeMonitorStatus.bind(this, monitorId)}>
                            {monitorDetail.isClosed ? '开启检测' : '关闭检测'}
                        </Button>
                    </Col>
                </Row>

                <Row className="monitor-info-table">
                    <table width="100%" cellPadding="0" cellSpacing="0">
                        <tbody>
                            <tr>
                                <th>执行周期</th>
                                <td className="width-3">{monitorDetail.periodTypeName}</td>
                                <th>告警方式</th>
                                <td className="width-3">{monitorDetail.sendTypeNames}</td>
                            </tr>
                            <tr>
                                <th>执行时间</th>
                                <td className="width-3">{monitorDetail.executeTime ? monitorDetail.executeTime : ''}</td>
                                <th>接收人</th>
                                <td className="width-3">{monitorDetail.notifyUser ? monitorDetail.notifyUser.map(item => item.name).join('，') : ''}</td>
                            </tr>
                        </tbody>
                    </table>
                </Row>

                <div className="rule-action">
                    <Button 
                        type="primary" 
                        onClick={this.addNewRule.bind(this, 'table')}>
                        添加表级规则
                    </Button>
                    <Button 
                        className="m-l-8" 
                        type="primary" 
                        onClick={this.addNewRule.bind(this, 'column')}>
                        添加字段级规则
                    </Button>
                    <Button 
                        className="m-l-8" 
                        type="primary" 
                        onClick={this.addNewRule.bind(this, 'SQL')}>
                        添加自定义SQL
                    </Button>
                </div>

                <Table 
                    rowKey="id"
                    className="m-table rule-edit-table"
                    columns={this.initColumns()}
                    pagination={false}
                    dataSource={rules}
                />
                
                <ExecuteForm 
                    visible={showExecuteModal} 
                    closeModal={this.closeExecuteModal}
                    data={monitorDetail}>
                </ExecuteForm>
            </div>
        );
    }
}
RuleEditPane = Form.create()(RuleEditPane);

