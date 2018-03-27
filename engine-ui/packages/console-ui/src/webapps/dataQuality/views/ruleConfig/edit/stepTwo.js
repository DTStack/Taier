import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { isEmpty } from 'lodash';
import { Button, Form, Select, Input, Row, Col, Table, message, Popconfirm, InputNumber } from 'antd';

import { commonActions } from '../../../actions/common';
import { ruleConfigActions } from '../../../actions/ruleConfig';
import { dataSourceActions } from '../../../actions/dataSource';
import { formItemLayout, rowFormItemLayout } from '../../../consts';
import DSApi from '../../../api/dataSource';

const FormItem = Form.Item;
const Option = Select.Option;

const mapStateToProps = state => {
    const { ruleConfig, dataSource, common } = state;
    return { ruleConfig, dataSource, common }
}

const mapDispatchToProps = dispatch => ({
    getAllDict(params) {
        dispatch(commonActions.getAllDict(params));
    },
    getRuleFunction(params) {
        dispatch(ruleConfigActions.getRuleFunction(params));
    },
    getDataSourcesColumn(params) {
        dispatch(dataSourceActions.getDataSourcesColumn(params));
    },
})

@connect(mapStateToProps, mapDispatchToProps)
export default class StepTwo extends Component {
    constructor(props) {
        super(props);
        this.state = {
            currentRule: {},
            SQLFields: ['customizeSql', 'verifyType', 'operator', 'threshold'],
            columnFields: ['columnName', 'functionId', 'verifyType', 'operator', 'threshold']
        };
    }

    componentDidMount() {
        const { editParams } = this.props;
        this.props.getAllDict();
        this.props.getRuleFunction();
        this.props.getDataSourcesColumn({
            sourceId: editParams.dataSourceId,
            tableName: editParams.tableName
        });
    }

    changeCurrentRule = (obj) => {
        let currentRule = { ...this.state.currentRule, ...obj };
        this.setState({ currentRule });
        console.log(this,obj,'currentRule')
    }

    prev = () => {
        const { currentStep, navToStep } = this.props;
        navToStep(currentStep - 1);
    }

    next = () => {
        const { currentStep, navToStep, editParams } = this.props;
        const { currentRule } = this.state;

        if (editParams.rules.length) {
            if (!isEmpty(currentRule)) {
                // this.cancel(currentRule.id);
                message.error('监控规则未保存');
            } else {
                navToStep(currentStep + 1);
            }
        } else {
            message.error('请添加监控规则');
        }

        // if (editParams.rules.length && isEmpty(currentRule)) {
        //     navToStep(currentStep + 1);
        // } else {
        //     message.error('请添加监控规则');
        // }
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
        this.changeCurrentRule(obj);
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
        const { verifyType } = this.props.common.allDict;

        switch (type) {
            case 'columnName': {
                if (record.isCustomizeSql) {
                    return record.customizeSql
                } else {
                    return text
                }
            }
            case 'functionId': {
                return  text ? monitorFunction.filter(item => parseInt(text) === item.id)[0].nameZc : undefined
            }

            case 'verifyType': {
                return text ? verifyType.filter(item => parseInt(text) === item.value)[0].name : undefined
            }

            case 'threshold': {
                let value = `${record.operator}  ${text}`;
                return record.verifyType == 1 ? `${value} %` : value
            }

            default:
                return text
        }
    }

    // 编辑
    edit(id) {
        const { rules } = this.props.editParams;
        const { currentRule } = this.state;

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

            this.setState({ currentRule: target });
            this.props.changeParams({ rules: newData });
        }
    }

    // 取消编辑
    cancel(id) {
        let newData = [...this.props.editParams.rules],
            target  = newData.filter(item => id === item.id)[0],
            index   = newData.indexOf(target);

        if (target.editStatus === 'edit') {
            delete target.editable;
            delete target.editStatus;
        } else {
            newData.splice(index, 1);
        }

        this.setState({ currentRule: [] });
        this.props.changeParams({ rules: newData });
    }

    // 删除
    delete(id) {
        let newData = [...this.props.editParams.rules],
            target  = newData.filter(item => id === item.id)[0],
            index   = newData.indexOf(target);
        
        if (target) {
            newData.splice(index, 1);
            this.props.changeParams({ rules: newData });
        }
    }

    // 保存
    save(id) {
        const { currentRule, SQLFields, columnFields } = this.state;
        let newData = [...this.props.editParams.rules],
            target  = newData.filter(item => id === item.id)[0],
            index   = newData.indexOf(target),
            fields  = currentRule.isCustomizeSql ? SQLFields : columnFields;

        this.props.form.validateFields(fields, { force: true }, (err, values) => {
            console.log(err,values)
            if(!err) {
                
                delete currentRule.editStatus;
                delete currentRule.editable;
                newData[index] = currentRule;

                this.setState({ currentRule: [] });
                this.props.changeParams({ rules: newData });
            }
        });
    }

    addNewRule = (type) => {
        const { editParams, form } = this.props;
        const { currentRule } = this.state;

        let newData = [...editParams.rules];

        if (!isEmpty(currentRule)) {
            if (currentRule.editStatus === "edit") {
                delete currentRule.editable
                delete currentRule.editStatus
            } else {
                newData.shift();
                form.resetFields();
                this.setState({ currentRule: [] });
            }
        }

        let target = {
            id: newData[0] ? newData[0].id + 1 : 1,
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
                target.columnName = editParams.tableName;
                target.functionId = undefined;
                break;
            default:
                break;
        }

        newData.unshift(target);
        this.setState({ currentRule: target });
        this.props.changeParams({
            rules: newData
        });

    }

    render() {
        const { rules } = this.props.editParams;

        return (
            <div>
                <div className="steps-content">
                    <div className="rule-action">
                        <Button 
                            type="primary" 
                            onClick={this.addNewRule.bind(this, 'table')}>
                            添加表级规则
                        </Button>
                        <Button 
                            type="primary" 
                            className="m-l-8" 
                            onClick={this.addNewRule.bind(this, 'column')}>
                            添加字段级规则
                        </Button>
                        <Button 
                            type="primary" 
                            className="m-l-8" 
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
                        onChange={this.onTableChange}
                    />
                </div>

                <div className="steps-action">
                    <Button onClick={this.prev}>上一步</Button>
                    <Button className="m-l-8" type="primary" onClick={this.next}>下一步</Button>
                </div>
            </div>
        );
    }
}
StepTwo = Form.create()(StepTwo);