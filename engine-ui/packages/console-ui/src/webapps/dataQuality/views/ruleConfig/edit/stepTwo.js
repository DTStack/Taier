import React, { Component } from 'react';
import { connect } from 'react-redux';
import { isEmpty } from 'lodash';
import { Button, Form, Select, Input, Row, Col, Table, TreeSelect, Icon, message, Popconfirm } from 'antd';

import RuleEditTD from './ruleEditTD';
import { ruleConfigActions } from '../../../actions/ruleConfig';
import { dataSourceActions } from '../../../actions/dataSource';
import { commonActions } from '../../../actions/common';
import { formItemLayout } from '../../../consts';
import DSApi from '../../../api/dataSource';

const FormItem = Form.Item;
const Option = Select.Option;
const TreeNode = TreeSelect.TreeNode;

const mapStateToProps = state => {
    const { ruleConfig, common } = state;
    return { ruleConfig, common }
}

const mapDispatchToProps = dispatch => ({
    getMonitorFunction(params) {
        dispatch(ruleConfigActions.getMonitorFunction(params));
    },
    getDataSourcesColumn(params) {
        dispatch(dataSourceActions.getDataSourcesColumn(params));
    },
    getAllDict(params) {
        dispatch(commonActions.getAllDict(params));
    },
})

@connect(mapStateToProps, mapDispatchToProps)
export default class StepTwo extends Component {
    constructor(props) {
        super(props);
        this.state = {
            currentRule: {},
        };
    }

    componentDidMount() {
        const { editParams } = this.props;
        this.props.getAllDict();
        this.props.getMonitorFunction();
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
        const { currentStep, navToStep, form } = this.props;
        form.validateFields({ force: true }, (err, values) => {
            console.log(err,values)
            if(!err) {
                navToStep(currentStep + 1);
            }
        })
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
            children: <RuleEditTD
                editable={record.editable}
                editParams={this.props.editParams}
                type={type}
                value={text}
                record={record}
                data={currentRule}
                changeCurrentRule={this.changeCurrentRule}
            />,
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

    edit(id) {
        let newData = [...this.props.editParams.rules],
            target = newData.filter(item => id === item.id)[0];

        if (target) {
            target.editable = true;
            target.editStatus = "edit";
            this.setState({ currentRule: target });
            this.props.changeParams({
                rules: newData
            });
        }
    }

    cancel(id) {
        const { currentRule } = this.state;
        let newData = [...this.props.editParams.rules],
            target  = newData.filter(item => id === item.id)[0],
            index   = newData.indexOf(target);

        if (target.editStatus) {
            delete target.editable;
            delete target.editStatus;
        } else {
            newData.splice(index, 1);
        }

        this.setState({ currentRule: [] });
        this.props.changeParams({
            rules: newData
        });
    }

    delete(id) {
        let newData = [...this.props.editParams.rules],
            target  = newData.filter(item => id === item.id)[0],
            index   = newData.indexOf(target);
        
        if (index > -1) {
            newData.splice(index, 1);
            this.props.changeParams({
                rules: newData
            });
        }
    }

    save(id) {
        const { currentRule } = this.state;
        let newData = [...this.props.editParams.rules],
            target  = newData.filter(item => id === item.id)[0],
            index   = newData.indexOf(target);

        for (let [k, v] of Object.entries(currentRule)) {
            if (v === undefined) {
                message.error('请填写相应规则')
                return
            }
        }

        delete currentRule.editable;
        newData[index] = currentRule;

        this.setState({ currentRule: [] });
        this.props.changeParams({
            rules: newData
        });
    }

    addColumnRule = () => {
        let newData = [...this.props.editParams.rules],
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
            filter: undefined,
            verifyType: '1',
            operator: '>',
            threshold: undefined,
        };

        newData.unshift(target);
        this.setState({ currentRule: target });
        this.props.changeParams({
            rules: newData
        });
    }

    addSQLRule = () => {
        let newData = [...this.props.editParams.rules],
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
            verifyType: '1',
            operator: '>',
            threshold: undefined,
        };
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
                        <Button type="primary" onClick={this.addTableRule}>添加表级规则</Button>
                        <Button className="m-l-8" type="primary" onClick={this.addColumnRule}>添加字段级规则</Button>
                        <Button className="m-l-8" type="primary" onClick={this.addSQLRule}>添加自定义SQL</Button>
                    </div>

                    <Table 
                        rowKey="id"
                        className="m-table rule-table"
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