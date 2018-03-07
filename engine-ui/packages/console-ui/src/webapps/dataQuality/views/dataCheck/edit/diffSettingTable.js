import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Table, Checkbox, TimePicker, Form, InputNumber, Button, Switch, Select, message } from 'antd';

const FormItem = Form.Item;

export default class DiffSettingTable extends Component {
    constructor(props) {
        super(props);
        this.state = {
            selectedIds: [],
            selectedRows: [],
            diffRule: []
        }
    }

    componentDidMount() {
        this.initTableData()
    }


    initColumns = () => {
        return [
            {
                title: '差异特征',
                dataIndex: 'setting',
                key: 'setting',
                render: (text, record) => {
                    return this.getSettingItem(text);
                },
                width: '95%',
            }, 
        ]
    }

    initTableData = () => {
        let diffRule = [
            {
                id: 1,
                setting: 'diffNum'
            },
            {
                id: 2,
                setting: 'diffPer'
            },
            {
                id: 3,
                setting: 'diffAbs'
            },
            {
                id: 4,
                setting: 'diffDecimal'
            },
            {
                id: 5,
                setting: 'diffCase'
            },
            {
                id: 6,
                setting: 'null'
            }
        ];
        this.setState({ diffRule });
    }

    getSettingItem = (key) => {
        const { getFieldDecorator } = this.props.form;
        switch (key) {
            case 'diffNum':
                return (
                    <div>
                        记录数差异，对比左右表的总记录数，差距小于
                        {
                            getFieldDecorator('diffNum', {
                                rules: [{ required: true, message: '不能为空' }],
                            })(
                                <InputNumber min={0} max={10} step={0.1} />
                            )
                        }
                        %时候，计为成功匹配
                    </div>
                )
                break;
            case 'diffPer':
                return (
                    <div>
                        数值差异百分比，对比左右表的数值型数据时，差距百分比小于
                        {
                            getFieldDecorator('diffPer', {
                                rules: [{ required: true, message: '不能为空' }],
                            })(
                                <InputNumber min={0} max={10} step={0.1} />
                            )
                        }
                        %时候，计为成功匹配
                    </div>
                )
                break;
            case 'diffAbs':
                return (
                    <div>
                        数值差异绝对值，对比左右表的数值型数据时，差距绝对值小于
                        {
                            getFieldDecorator('diffAbs', {
                                rules: [{ required: true, message: '不能为空' }],
                            })(
                                <InputNumber min={0} max={10} step={0.1} />
                            )
                        }
                        时候，计为成功匹配
                    </div>
                )
                break;
            case 'diffDecimal':
                return (
                    <div>
                        数值对比忽略小数点，忽略小数点后
                        {
                            getFieldDecorator('diffDecimal', {
                                rules: [{ required: true, message: '不能为空' }],
                            })(
                                <InputNumber min={0} max={10} step={0.1} />
                            )
                        }
                        位
                    </div>
                )
                break;
            case 'diffCase':
                return (
                    <p>字符不区分大小写，对比左右表的字符串型数据时，不区分大小写</p>
                )
                break;
            case 'null':
                return (
                    <p>空值与NULL等价，对比左右表的数据时，认为空值与NULL值是相等的</p>
                )
                break;
            default:
                break;
        }
    }

    render() {
        const { selectedIds, diffRule } = this.state;
        const rowSelection = {
            selectedRowKeys: selectedIds,
            onChange: (selectedIds, selectedRows) => {
                this.setState({
                    selectedIds: selectedIds,
                    selectedRows: selectedRows
                });
            }
        };
        return (
            <Table
                className="m-table diffrule-table"
                showHeader={false}
                columns={this.initColumns()}
                rowSelection={rowSelection}
                rowKey={record => record.id}
                dataSource={diffRule}
                pagination={false}
            />
        )
    }
}
DiffSettingTable = Form.create()(DiffSettingTable);