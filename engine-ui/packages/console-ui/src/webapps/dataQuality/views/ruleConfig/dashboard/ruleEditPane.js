import React, { Component } from 'react';
import { hashHistory } from 'react-router';
import { connect } from 'react-redux';
import { isEmpty, isNull } from 'lodash';
import { Button, Form, Select, Input, Row, Col, Table, message, Popconfirm, InputNumber, Modal } from 'antd';

import ExecuteForm from './executeForm';
import { ruleConfigActions } from '../../../actions/ruleConfig';
import { rowFormItemLayout, operatorSelect, operatorSelect1 } from '../../../consts';
import RCApi from '../../../api/ruleConfig';

const FormItem = Form.Item;
const Option = Select.Option;
const TextArea=Input.TextArea;

const mapStateToProps = state => {
    const { ruleConfig, common } = state;
    return { ruleConfig, common }
}

const mapDispatchToProps = dispatch => ({
    getRuleFunction(params) {
        dispatch(ruleConfigActions.getRuleFunction(params));
    },
    getTableColumn(params) {
        dispatch(ruleConfigActions.getTableColumn(params));
    },
    getMonitorDetail(params) {
        dispatch(ruleConfigActions.getMonitorDetail(params));
    },
    changeMonitorStatus(params) {
        dispatch(ruleConfigActions.changeMonitorStatus(params));
    },
})

@connect(mapStateToProps, mapDispatchToProps)
export default class RuleEditPane extends Component {
    constructor(props) {
        super(props);
        this.state = {
            enumFields: ['columnName', 'functionId', 'thresholdEnum'],
            SQLFields: ['customizeSql', 'verifyType', 'operator', 'threshold'],
            columnFields: ['columnName', 'functionId', 'verifyType', 'operator', 'threshold'],
            rules: [],
            currentRule: {},
            functionList: [],
            monitorId: undefined,
            havePart: false,
            showExecuteModal: false,
        };
    }

    componentDidMount() {
        this.props.getRuleFunction();
    }

    componentWillReceiveProps(nextProps) {
        let oldData = this.props.data,
            newData = nextProps.data;

        if (!isEmpty(newData) && oldData.tableId !== newData.tableId) {
            let monitorId = newData.monitorPartVOS[0].monitorId;
           
            this.initData(monitorId, newData);
            this.setState({ 
                monitorId,
                havePart: false,
                currentRule: {},
                functionList: []
            });

            if (newData.dataSourceType === 7 || newData.dataSourceType === 10) {
                this.setState({ havePart: true });
            }
        }
    }

    // ajax获取数据
    initData = (monitorId, data) => {
        this.props.getMonitorDetail({ monitorId });
        RCApi.getMonitorRule({ monitorId }).then((res) => {
            if (res.code === 1) {
                this.setState({
                    rules: res.data
                });
            }
        });
        this.props.getTableColumn({
            sourceId: data.sourceId,
            tableName: data.tableName
        });
    }

    // 规则列表配置
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
        }, {
            title: '过滤条件',
            dataIndex: 'filter',
            key: 'filter',
            render: (text, record) => this.renderColumns(text, record, 'filter'),
            width: '25%'
        }, {
            title: '校验方法',
            dataIndex: 'verifyType',
            key: 'verifyType',
            render: (text, record) => this.renderColumns(text, record, 'verifyType'),
            width: '15%',
        }, {
            title: '阈值配置（不符合阈值条件时触发告警）',
            dataIndex: 'threshold',
            key: 'threshold',
            render: (text, record) => this.renderColumns(text, record, 'threshold'),
            width: '20%'
        }, {
            title: '操作',
            width: '10%',
            render: (text, record) => {
                const { editable } = record;
                return (
                    <div>
                        {
                            editable ?
                            <span>
                                <a className="m-r-8" onClick={() => this.save(record.id)}>保存</a>
                                <a onClick={() => this.cancel(record.id)}>取消</a>
                            </span>
                            : 
                            <span>
                                <a className="m-r-8" onClick={() => this.edit(record.id)}>编辑</a>
                                <Popconfirm title="确定要删除吗？" onConfirm={() => this.delete(record.id)}>
                                    <a>删除</a>
                                </Popconfirm>
                            </span>
                        }
                    </div>
                );
            },
        }];
    }

    renderColumns(text, record, type) {
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

    // 参数改变回调
    changeRuleParams = (type, value) => {
        let obj = {};
        obj[type] = value.target ? value.target.value : value;

        this.setState({ currentRule: {...this.state.currentRule, ...obj} });
    }

    // 校验字段回调
    onColumnNameChange = (name) => {
        const { form, ruleConfig } = this.props;
        const { tableColumn, monitorFunction } = ruleConfig;

        let columnType   = tableColumn.filter(item => item.key === name)[0].type,
            functionList = monitorFunction[columnType];

        form.setFieldsValue({ functionId: undefined });
        this.setState({ 
            functionList,
            currentRule: {
                ...this.state.currentRule, 
                columnName: name,
                functionId: undefined
            }
        });
    }

    // 统计函数变化回调
    onFunctionChange = (id) => {
        const { form } = this.props;
        const { functionList } = this.state;

        let isPercentage = functionList.filter(item => item.id == id)[0].isPercent,
            nameZc = functionList.filter(item => item.id == id)[0].nameZc,
            isEnum = nameZc === '枚举值' ? true : false;

        let currentRule  = {
            ...this.state.currentRule, 
            functionId: id,
            functionName: nameZc, 
            verifyType: isEnum ? '1' : undefined,
            operator: isEnum ? 'in' : undefined,
            isPercentage: isPercentage, 
            percentType: isPercentage === 1 ? 'limit' : 'free'
        };

        form.setFieldsValue({ 
            verifyType: isEnum ? '1' : undefined,
            operator: undefined 
        });

        this.setState({ currentRule });
    }

    // 校验方法变化回调
    onVerifyTypeChange = (value) => {
        let currentRule = {
            ...this.state.currentRule,
            verifyType: value
        };

        if (currentRule.percentType !== 'limit') {
            currentRule.isPercentage = value == 1 ? 0 : 1;
        }

        this.setState({ currentRule });
    }

    // 编辑状态的TD
    renderEditTD = (text, record, type) => {
        const { form, common, ruleConfig } = this.props;
        const { getFieldDecorator } = form;
        const { tableColumn } = ruleConfig;
        const { verifyType } = common.allDict;
        const { currentRule, functionList } = this.state;

        const isStringLength = (name) => {
            return name === '字符串最大长度' || name === '字符串最小长度';
        }

        let operatorMap = isStringLength(currentRule.functionName) ? operatorSelect1 : operatorSelect;

        switch(type) {
            case 'columnName': {
                if (record.isCustomizeSql) {
                    return <FormItem {...rowFormItemLayout} className="rule-edit-td">
                        {
                            getFieldDecorator('customizeSql', {
                                rules: [{
                                    required: true, 
                                    message: '自定义SQL不可为空'
                                }],
                                initialValue: record.customizeSql
                            })( record.isCustomizeSql?
                                (<TextArea 
                                    style={{resize: "none"}}
                                    autosize={{ minRows: 3,maxRows:8}}
                                    placeholder="查询结果为一个数值类型"
                                    onChange={this.changeRuleParams.bind(this, 'customizeSql')} 
                                    
                                />)
                                :
                                (<Input 
                                    placeholder="查询结果为一个数值类型"
                                    onChange={this.changeRuleParams.bind(this, 'customizeSql')} 
                                    disabled={record.editStatus === 'edit'} 
                                />)
                            )
                        }
                    </FormItem>
                } else {
                    return <FormItem {...rowFormItemLayout} className="rule-edit-td">
                        {
                            getFieldDecorator('columnName', {
                                rules: [{
                                    required: true, 
                                    message: '字段不可为空'
                                }],
                                initialValue: record.columnName
                            })(
                                <Select 
                                    showSearch
                                    onChange={this.onColumnNameChange} 
                                    disabled={record.isTable || record.editStatus === 'edit'}>
                                    {
                                        tableColumn.map((item) => {
                                            return <Option 
                                                key={item.key} 
                                                value={item.key}>
                                                {item.key}
                                            </Option>
                                        })
                                    }
                                </Select>
                            )
                        }
                    </FormItem>
                }
            }

            case 'functionId': {
                return <FormItem {...rowFormItemLayout} className="rule-edit-td">
                    {
                        getFieldDecorator('functionId', {
                            rules: [{
                                required: true, 
                                message: '统计函数不可为空'
                            }],
                            initialValue: record.editStatus === 'edit' ? record.functionName : record.functionId
                        })(
                            <Select
                                onChange={this.onFunctionChange}
                                disabled={record.editStatus === 'edit'}>
                                {
                                    functionList.map((item) => {
                                        return <Option 
                                            key={item.id} 
                                            value={item.id.toString()}>
                                            {item.nameZc}
                                        </Option>
                                    })
                                }
                            </Select>
                        )
                    }
                </FormItem>
            }

            case 'filter': {
                return <FormItem {...rowFormItemLayout} className="rule-edit-td">
                    {
                        getFieldDecorator('filter', {
                            rules: [],
                            initialValue: record.filter
                        })(
                            <Input 
                                placeholder={`"and"开头的条件语句，如and col = "val"`}
                                onChange={this.changeRuleParams.bind(this, 'filter')} 
                                disabled={record.editStatus === 'edit'} 
                            />
                        )
                    }
                </FormItem>
            }

            case 'verifyType': {
                return <FormItem {...rowFormItemLayout} className="rule-edit-td">
                    {
                        getFieldDecorator('verifyType', {
                            rules: [{
                                required: true, 
                                message: '校验方法不可为空'
                            }],
                            initialValue: record.verifyType ? record.verifyType.toString() : undefined
                        })(
                            <Select 
                                onChange={this.onVerifyTypeChange}
                                disabled={record.editStatus === 'edit' || currentRule.operator === 'in'}>
                                {
                                    verifyType.map((item) => {
                                        return <Option 
                                            key={item.value} 
                                            value={item.value.toString()}>
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
                if (currentRule.operator === 'in') {
                    return <FormItem {...rowFormItemLayout} className="rule-edit-td">
                        {
                            getFieldDecorator('thresholdEnum', {
                                rules: [{
                                    required: true, 
                                    message: '不可为空'
                                }],
                                initialValue: record.threshold
                            })(
                                <Input
                                    placeholder="枚举格式为(value1,value2,.....)"
                                    onChange={this.changeRuleParams.bind(this, 'threshold')}
                                /> 
                            )
                        }
                    </FormItem>
                } else {
                    return (
                        <div>
                            <FormItem>
                                {
                                    getFieldDecorator('operator', {
                                        rules: [{
                                            required: true, 
                                            message: '不可为空',
                                        }],
                                        initialValue: record.operator
                                    })(
                                        <Select 
                                            style={{ width: 65 }} 
                                            onChange={this.changeRuleParams.bind(this, 'operator')}>
                                            {
                                                operatorMap.map((item) => {
                                                    return <Option 
                                                        key={item.value} 
                                                        value={item.value}> 
                                                        {item.text} 
                                                    </Option>
                                                })
                                            }
                                        </Select>
                                    )
                                }
                            </FormItem>
                            <FormItem>
                                {
                                    getFieldDecorator('threshold', {
                                        rules: [{
                                            required: true, 
                                            message: '不可为空',
                                        }],
                                        initialValue: record.threshold
                                    })(
                                        <InputNumber
                                            style={{ width: 65 }}
                                            onChange={this.changeRuleParams.bind(this, 'threshold')}
                                        /> 
                                    )
                                }
                            </FormItem>
                            {
                                currentRule.isPercentage === 1
                                &&
                                <span style={{ height: 32, lineHeight: '32px' }}>%</span>
                            }
                        </div>
                    )
                }
            }
        }
    }

    // 已有数据
    renderTD = (text, record, type) => {
        switch (type) {
            case 'columnName': {
                return record.isCustomizeSql ? record.customizeSql : text;
            }
            case 'functionId': {
                return  record.functionName;
            }
            case 'verifyType': {
                return record.verifyTypeValue;
            }
            case 'threshold': {
                let value = `${record.operator}  ${text}`;
                return record.isPercentage ? `${value} %` : value;
            }
            default:
                return text;
        }
    }

    // 编辑规则
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

    // 删除规则
    delete(id) {
        const { monitorId } = this.state;
        let newData = [...this.state.rules],
            target  = newData.filter(item => id === item.id)[0];
        
        if (target) {
            RCApi.deleteMonitorRule({
                ruleIds: [id],
                monitorId: monitorId
            }).then((res) => {
                if (res.code === 1) {
                    message.success('删除成功');
                    RCApi.getMonitorRule({ monitorId }).then((res) => {
                        if (res.code === 1) {
                            this.setState({
                                rules: res.data
                            });

                            if (isNull(res.data)) {
                                this.props.refresh();
                            }
                        }
                    });
                }
            });
        }
    }

    // 保存规则
    save(id) {
        const { currentRule, enumFields, SQLFields, columnFields, monitorId } = this.state;
        let fields = currentRule.isCustomizeSql ? SQLFields : columnFields;

        if (currentRule.operator === 'in') {
            fields = enumFields;
        }

        this.props.form.validateFields(fields, { force: true }, (err, values) => {
            console.log(err,values)
            if(!err) {
                if (currentRule.editStatus === 'new') {
                    currentRule.monitorId = monitorId;
                    delete currentRule.id;
                }

                // delete currentRule.isEnum;
                delete currentRule.isTable;
                delete currentRule.editable;
                delete currentRule.editStatus;
                delete currentRule.percentType;

                RCApi.saveMonitorRule({...currentRule}).then((res) => {
                    if (res.code === 1) {
                        message.success('保存成功');
                        RCApi.getMonitorRule({
                            monitorId: monitorId
                        }).then((res) => {
                            if (res.code === 1) {
                                this.setState({
                                    rules: res.data,
                                    currentRule: {}
                                });
                            }
                        });
                    }
                });
            }
        });

    }

    // 新增规则
    addNewRule = (type) => {
        const { form, data, ruleConfig } = this.props;
        const { currentRule, rules } = this.state;

        let newData = [...rules];

        if (!isEmpty(currentRule)) {
            if (currentRule.editStatus === "edit") {
                delete currentRule.editable
                delete currentRule.editStatus
            } else {
                newData.shift();
                form.resetFields();
                this.setState({ 
                    currentRule: {}, 
                    functionList: [] 
                });
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
                target.isTable = true;
                target.isCustomizeSql = false;
                target.columnName = data.tableName;
                target.functionId = undefined;

                // 表规则的统计函数
                this.setState({ 
                    functionList: ruleConfig.monitorFunction.all.filter(item => item.level === 1) 
                });
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

    // 切换分区
    onMonitorIdChange = (value) => {
        let monitorId = value;

        this.props.getMonitorDetail({ monitorId });
        RCApi.getMonitorRule({ monitorId }).then((res) => {
            if (res.code === 1) {
                this.setState({
                    rules: res.data
                });
            }
        });

        this.setState({ 
            monitorId,
            currentRule: {}
        });
    }

    // 立即执行监控
    executeMonitor = (monitorId) => {
        const { data } = this.props;

        RCApi.executeMonitor({ monitorId }).then((res) => {
            if (res.code === 1) {
                message.success('操作成功，稍后可在任务查询中查看详情');
                this.props.closeSlidePane();
                // hashHistory.push(`/dq/taskQuery?tb=${data.tableName}&source=${data.dataSourceType}`);
            } else {
                message.error('执行失败');
            }
        });
    }

    // 开启或关闭监控
    changeMonitorStatus = (monitorId) => {
        RCApi.changeMonitorStatus({ monitorId }).then((res) => {
            if (res.code === 1) {
                message.success('操作成功');
                this.props.getMonitorDetail({ monitorId });
            }
        });
    }

    // 打开编辑弹窗
    openExecuteModal = () => {
        this.setState({
            showExecuteModal: true
        });
    }

    /**
     * 关闭编辑弹窗（是否更新数据）
     * @param {boolean} updated
     */
    closeExecuteModal = (updated) => {
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
        const { monitorDetail } = ruleConfig;
        const { periodType, notifyType } = common.allDict;
        const { rules, monitorId, havePart, showExecuteModal } = this.state;

        let monitorPart = data.monitorPartVOS ? data.monitorPartVOS : [];

        return (
            <div className="rule-manage">
                <Row>
                    <Col span={12} className="txt-left">
                        {
                            havePart
                            &&
                            <div>
                                分区：
                                <Select 
                                    style={{ width: 150 }}
                                    value={monitorId ? monitorId.toString() : undefined}
                                    onChange={this.onMonitorIdChange}>
                                    {
                                        monitorPart.map((item) => {
                                            return <Option 
                                                key={item.monitorId} 
                                                value={item.monitorId.toString()}>
                                                {item.partValue ? item.partValue : '全表'}
                                            </Option>
                                        })
                                    }
                                </Select>
                            </div>
                        }
                    </Col>

                    <Col span={12} className="txt-right">
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

                <div className="monitor-info-table">
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
                                <td className="width-3">{monitorDetail.executeTime}</td>
                                <th>接收人</th>
                                <td className="width-3">
                                    {
                                        monitorDetail.notifyUser ? 
                                        monitorDetail.notifyUser.map(item => item.name).join('，') : ''
                                    }
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>

                <div className="txt-right">
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
                    style={{ padding: '20px 0' }}
                    pagination={false}
                    dataSource={rules}
                    columns={this.initColumns()}
                    scroll={{y:361}}
                />
                
                <ExecuteForm 
                    data={monitorDetail}
                    visible={showExecuteModal} 
                    closeModal={this.closeExecuteModal}>
                </ExecuteForm>
            </div>
        );
    }
}
RuleEditPane = Form.create()(RuleEditPane);

