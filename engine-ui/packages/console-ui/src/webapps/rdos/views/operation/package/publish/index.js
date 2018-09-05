import React from "react";
import { Card, Table, Form, Select, DatePicker, Input } from "antd";
import moment from "moment";

import PublishModal from "./publishModal";

const { RangePicker } = DatePicker;
const FormItem = Form.Item;
const Option = Select.Option;
const Search = Input.Search;

class PackagePublish extends React.Component {
    state = {
        publishVisible:false,
        packageList: [],
        tableParams:{
            filter:{},
            sorter:{},
            pagination:{
                current:1,
                pageSize:20,
                total:0
            }
        }
    }
    componentDidMount(){
        this.getPackageList();
    }
    getPackageList(){

    }
    initColumns() {
        return [{
            title: "发布包",
            dataIndex: "packageName"
        }, {
            title: "申请人",
            dataIndex: "a"
        }, {
            title: "申请时间",
            dataIndex: "s",
            sorter:true
        }, {
            title: "发布人",
            dataIndex: "d"
        }, {
            title: "发布时间",
            dataIndex: "f",
            sorter:true
        }, {
            title: "发布描述",
            dataIndex: "q"
        }, {
            title: "发布状态",
            dataIndex: "w"
        }, {
            title: "操作",
            dataIndex: "deal"
        }]
    }
    selectChange(key,value) {
        this.setState({
            [key]:value
        },this.getPackageList)
    }
    onTableChange(pagination, filters, sorter){
        this.setState({
            tableParams:{
                pagination,
                filters,
                sorter
            }
        },this.getPackageList)
    }
    disabledDate(currentDate){
        const now = new moment;
        if(currentDate>now){
            return true
        }
        return false;
    }
    dateChange(key,dates){
        this.setState({
            [key]:dates
        },this.getPackageList)
    }
    getTableTitle = () => {
        return (
                <Form
                    style={{marginTop:"10px"}}
                    layout="inline"
                >
                    <FormItem
                        label=""
                    >
                        <Search
                            size="default"
                            placeholder="输入发布包名称"
                            style={{ width: 120 }}
                            onSearch={value => console.log(value)}
                        />
                    </FormItem>
                    <FormItem
                        label="发布人"
                    >
                        <Select size="default" onChange={this.selectChange.bind(this,'publishName')} style={{ width: 110 }}>
                            <Option value="o">option</Option>
                        </Select>
                    </FormItem>
                    <FormItem
                        label="申请人"
                    >
                        <Select size="default" onChange={this.selectChange.bind(this,'applyName')} style={{ width: 110 }}>
                            <Option value="o">option</Option>
                        </Select>
                    </FormItem>
                    <FormItem
                        label="发布状态"
                    >
                        <Select size="default" onChange={this.selectChange.bind(this,'publishStatus')} style={{ width: 110 }}>
                            <Option value="o">option</Option>
                        </Select>
                    </FormItem>
                    <FormItem
                        label="发布日期"
                    >
                        <RangePicker onChange={this.dateChange.bind(this,'publishDate')} disabledDate={this.disabledDate} size="default" style={{ width: 170 }} />
                    </FormItem>
                    <FormItem
                        label="申请日期"
                    >
                        <RangePicker onChange={this.dateChange.bind(this,'applyDate')} disabledDate={this.disabledDate} size="default" style={{ width: 170 }} />
                    </FormItem>
                </Form>
        )
    }
    render() {
        const { packageList, tableParams, publishVisible } = this.state;
        return (
            <div className="m-card">
                <PublishModal onCancel={()=>{this.setState({publishVisible:false})}}  visible={publishVisible} />
                <Card
                    noHovering
                    bordered={false}
                    title={this.getTableTitle()}
                >
                    <Table
                        className="m-table"
                        columns={this.initColumns()}
                        pagination={tableParams.pagination}
                        dataSource={packageList}
                        onChange={this.onTableChange.bind(this)}
                    />
                </Card>
            </div>
        )
    }
}

export default PackagePublish;