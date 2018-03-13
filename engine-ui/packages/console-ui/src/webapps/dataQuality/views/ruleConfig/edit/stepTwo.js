import React, { Component } from 'react';
import { isEmpty } from 'lodash';
import { Button, Form, Select, Input, Row, Col, Table, TreeSelect, Icon, message } from 'antd';
import { formItemLayout } from '../../../consts';
import DSApi from '../../../api/dataSource';

const FormItem = Form.Item;
const Option = Select.Option;
const TreeNode = TreeSelect.TreeNode;

export default class StepTwo extends Component {
    constructor(props) {
        super(props);
        this.state = {
        };
    }

    componentDidMount() {}


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
            dataIndex: 'tableName',
            key: 'tableName',
            width: '15%'
        }, {
            title: '统计函数',
            dataIndex: 'dataSourceType',
            key: 'dataSourceType',
            width: '15%',
            render: (text, record) => {
                return text ? `${dataSourceTypes[text]} / ${record.dataName}` : '--';
            }
        }, 
        {
            title: '过滤条件',
            dataIndex: 'periodType',
            key: 'periodType',
            width: '8%'
        }, {
            title: '校验方法',
            dataIndex: 'recentNotifyNum',
            key: 'recentNotifyNum',
            width: '10%',
        }, {
            title: '阈值配置',
            dataIndex: 'isRemoteTrigger',
            key: 'isRemoteTrigger',
            render: (text, record) => {
                if (text === 0) {
                    return <Icon type="check-circle status-success" />
                } else {
                    return <Icon type="close-circle status-error" />
                }
            },
            width: '8%'
        }, {
            title: '操作',
            width: '8%',
            render: (text, record) => {
                return (
                    <a>订阅</a>
                )
            }
        }]
    }

    render() {
        const { getFieldDecorator } = this.props.form;

        return (
            <div>
                <div className="steps-content">
                    <Table 
                        rowKey="id"
                        className="m-table"
                        columns={this.initColumns()} 
                        // loading={loading}
                        pagination={false}
                        dataSource={[]}
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