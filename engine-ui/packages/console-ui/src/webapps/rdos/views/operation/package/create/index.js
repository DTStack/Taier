import React from "react";
import {
    Card, Table, Form, Select,
    DatePicker, Input, Radio, Pagination,
    Button, Icon, Checkbox
} from "antd";
import moment from "moment";

import AddLinkModal from "./addLinkModal"

const { RangePicker } = DatePicker;
const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const Option = Select.Option;
const Search = Input.Search;

class PackageCreate extends React.Component {

    state = {
        addLinkVisible:false,
        tableParams: {
            filter: {},
            sorter: {},
            pagination: {
                current: 1,
                pageSize: 20,
                total: 0
            }
        },
        selectedRowKeys:[],
        selectedRows:[],
    }

    componentDidMount() {
        this.getTaskList();
    }

    getTaskList() {

    }

    onTableChange(pagination, filters, sorter) {
        this.setState({
            tableParams: {
                pagination,
                filters,
                sorter
            }
        }, this.getTaskList)
    }

    initColumns() {
        return [{
            title: "名称",
            dataIndex: "name"
        }, {
            title: "负责人",
            dataIndex: "a"
        }, {
            title: "提交人",
            dataIndex: "s"
        }, {
            title: "提交时间",
            dataIndex: "d",
            sorter: true
        }, {
            title: "备注",
            dataIndex: "f"
        }, {
            title: "操作",
            dataIndex: "deal",
            width: "200px"
        }]
    }

    disabledDate(currentDate) {
        const now = new moment;
        if (currentDate > now) {
            return true
        }
        return false;
    }

    dateChange(key, dates) {
        this.setState({
            [key]: dates
        }, this.getTaskList)
    }

    selectChange(key, value) {
        if (key == "taskType") {
            value = value.target.value
        }
        this.setState({
            [key]: value
        }, this.getPackageList)
    }

    rowSelection() {
        return {
            onChange: (selectedRowKeys,selectedRows) => {
                this.setState({ selectedRowKeys,selectedRows })
            }
        }
    }

    render() {
        const { packageList, tableParams, addLinkVisible } = this.state;
        const { mode } = this.props;
        return (
            <div className="package-create-box">
                <div className="table-box">
                    <div className="table-header">
                        <div className="header-item">
                            <span className="title">发布人：</span>
                            <Select className="item" size="default" onChange={this.selectChange.bind(this, 'publishName')} >
                                <Option value="o">option</Option>
                            </Select>
                        </div>
                        <div className="header-item">
                            <span className="title">对象类型：</span>
                            <RadioGroup defaultValue={1} onChange={this.selectChange.bind(this, 'taskType')}>
                                <Radio value={1}>任务</Radio>
                                <Radio value={2}>资源</Radio>
                                <Radio value={3}>函数</Radio>
                                {mode == "offline" && <Radio value={4}>表</Radio>}
                            </RadioGroup>
                        </div>
                        <div className="header-item">
                            <span className="title">发布日期：</span>
                            <RangePicker className="item" onChange={this.dateChange.bind(this, 'publishDate')} disabledDate={this.disabledDate} size="default" />
                        </div>
                        <div className="header-item">
                            <span className="title">发布对象：</span>
                            <Search
                                className="item"
                                size="default"
                                placeholder="搜索发布对象名"
                                onSearch={this.selectChange.bind(this, "publishName")}
                            />
                        </div>
                    </div>
                    <Table
                        className="m-table select-all-table"
                        columns={this.initColumns()}
                        pagination={tableParams.pagination}
                        dataSource={packageList}
                        onChange={this.onTableChange.bind(this)}
                        rowSelection={this.rowSelection()}
                    />
                </div>
                <div className="tool-box">
                    <div className="box-border">
                        <div className="title">
                            发布到目标项目：111
                        </div>
                        <div className="tool-top">
                            待发布对象 <span className="publish-num">12</span>
                            <Button type="primary" className="pack">打包</Button>
                        </div>
                        <div className="main">
                            <div className="item">
                                <Icon className="close" type="close" />
                                <p><span className="item-title">任务：</span>dt-sql-parser</p>
                                <p><span className="item-title">负责人/修改人：</span>admin@dtstack.com</p>
                                <Checkbox >更新环境参数</Checkbox>
                            </div>
                            <div className="item"></div>
                            <div className="item"></div>
                            <div className="item"></div>
                            <div className="item"></div>
                        </div>
                        <div className="tool-bottom">
                            <Button className="clear" size="small">清空</Button>
                            <div className="pagn">
                                <Pagination size="small" pageSize={8} defaultCurrent={1} total={50} />
                            </div>
                        </div>
                    </div>
                </div>
                <AddLinkModal onCancel={()=>{this.setState({addLinkVisible:false})}} mode={mode} visible={addLinkVisible} />
            </div>
        )
    }
}

export default PackageCreate;