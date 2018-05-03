import React, { Component } from "react"
import { Form, Input, Icon, Button, Checkbox, Select, Row, Card, Col, Cascader, message,InputNumber  } from "antd";
import { Link } from 'react-router';

import DataSourceTable from "./dataSourceTable"
import { formItemLayout } from "../../../consts"
const FormItem = Form.Item;
const TextArea = Input.TextArea;
const Option = Select.Option;
class ManageBasicProperties extends Component {
    state = {
        dataSource: [],
        tableList: [],
        tableDetailList: {}
    }
    componentDidMount() {
        this.props.getCatalogue(0);
        this.getDataSource();
    }
    pass() {
        this.props.form.validateFields((err, values) => {
            if (!err) {
                console.log('Received values of form: ', values);
                this.props.dataChange({
                    ...values 
                })
            } 

        });

    }
    //数据源改变，获取表
    dataSourceChange(key) {
        this.setState({
            showTable: false
        },)
        this.props.form.setFieldsValue({
            table:""
        })
        
        this.props.tablelist(key)
            .then(
                (res) => {
                    if (res) {
                        this.setState({
                            tableList: res.data
                        })
                    }
                }
            )
    }
    //表改变
    tableChange() {
        this.setState({
            showTable: false
        })
    }
    getTableListView() {
        const data = this.state.tableList;
        return data.map(
            (item,index) => {
                return <Option key={item}>{item}</Option>
            }
        )
    }
    onSourcePreview() {
        const dataSource = this.props.form.getFieldValue("dataSource");
        const tableValue = this.props.form.getFieldValue("table");
        if (!dataSource || !tableValue) {
            message.error("请选择数据表！")
            return;
        }
        this.setState({
            showTable: !this.state.showTable
        }
            , () => {
                if (this.state.showTable) {
                    this.getTableDetail();
                }
            })


    }
    getDataSourceOptionView() {
        const data = this.state.dataSource;
        return data.map(
            (item) => {
                return <Option key={item.id}>{item.name}</Option>
            }
        )

    }
    getDataSource() {
        this.props.getDataSourceList(null)
            .then(
                (res) => {
                    if (res) {
                        this.setState({
                            dataSource: res.data
                        })
                    }
                }
            )

    }
    getTableDetail() {
        const dataSource = this.props.form.getFieldValue("dataSource");
        const tableValue = this.props.form.getFieldValue("table");
        this.setState({
            tableDetailList: [],
            tableLoading: true
        })
        this.props.previewData(dataSource, tableValue)
            .then(
                (res) => {
                    if (res) {
                        this.setState({
                            tableDetailList: res.data,
                            tableLoading: false
                        })
                    }
                }
            )
    }
    getTable() {

        if (this.state.showTable) {

            return (
                <Card
                    className="box-2"
                    style={{ marginTop: "10px" }}
                    noHovering>
                    <DataSourceTable loading={this.state.tableLoading} data={this.state.tableDetailList}></DataSourceTable>
                </Card>)
        }
        return null;
    }
    getCatagoryOption() {
        const tree = this.props.apiMarket.apiCatalogue;

        function exchangeTree(data) {
            let arr = []
            if (!data||data.length<1) {
                return null;
            }
            
            
            for (let i = 0; i < data.length; i++) {
            
                let item = data[i];
                
                if(item.api){
                    return null;
                }
                arr.push({
                    value: item.id,
                    label: item.catalogueName,
                    children: exchangeTree(item.childCatalogue)
                })
            }
            return arr;
        }
        return exchangeTree(tree);


    }
    render() {
        const { getFieldDecorator } = this.props.form
        const options = this.getCatagoryOption();
       
        return (
            <div>
                <div className="steps-content">
                    <Form onSubmit={this.handleSubmit}>
                        <FormItem
                            {...formItemLayout}
                            label="所属分组"

                        >
                            {getFieldDecorator('APIGroup', {
                                rules: [
                                    { required: true, message: '请选择分组' },
                                ],
                                initialValue:this.props.APIGroup
                            })(
                                <Cascader showSearch popupClassName="noheight" options={options} placeholder="请选择分组" />
                            )
                            }
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="API名称"
                            
                            hasFeedback >
                            {getFieldDecorator('APIName', {
                                rules: [{ required: true, message: '请输入API名称' },
                                {max:16,message:"最大字数不能超过16"},
                                { pattern: new RegExp(/^([\w|\u4e00-\u9fa5]*)$/), message: 'API名字只能以字母，数字，下划线组成' }],
                                initialValue: this.props.APIName
                            })(
                                <Input />
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="API描述"
                            hasFeedback
                            
                        >
                            {getFieldDecorator('APIdescription', {
                                rules: [{ required: false, message: '请输入API描述' },
                            {max:200,message:"最大字符不能超过200"}],
                                initialValue: this.props.APIdescription
                            })(
                                <TextArea />
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="调用限制"
                            hasFeedback >
                            {getFieldDecorator('callLimit', {
                                rules: [
                                    { required: true, message: '请输入调用次数限制' },
                                    {validator:function(rule, value, callback){
                                        if(value&&(value>1000||value<1)){
                                            callback("请输入不大于1000的正整数")
                                            return;
                                        }
                                        callback();
                                    }}
                                    
                                ]
                                ,
                                initialValue: this.props.callLimit
                            })(
                                <Input type="number"   placeholder="单用户每秒最高调用次数" />
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="返回条数限制"
                            hasFeedback >
                            {getFieldDecorator('backLimit', {
                                rules: [
                                    { required: true, message: '请输入最大返回条数' },
                                    { pattern: new RegExp(/^1[0-9]{0,3}$|^2000$|^[0-9]$|^[1-9][0-9]{1,2}$/), message: '请输入不大于2000的正整数' },
                            ],
                                initialValue: this.props.backLimit
                            })(
                                <Input type="number"  placeholder="单次最大返回数据条数 (最高支持2000条)" />
                            )}
                        </FormItem>

                        <FormItem
                            {...formItemLayout}
                            label="请选择数据源"
                        >
                            {getFieldDecorator('dataSource', {
                                rules: [{ required: true, message: '请选择数据源' }],
                                initialValue: this.props.dataSource
                            })(
                                <Select placeholder="请选择数据源"
                                    showSearch
                                    style={{ width: '85%', marginRight: 15 }}
                                    onChange={this.dataSourceChange.bind(this)} >
                                    {this.getDataSourceOptionView()}
                                </Select>

                            )}
                            <Link to="/api/dataSource">添加数据源</Link>
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="请选择表"  
                        >
                            {getFieldDecorator('table', {
                                rules: [{ required: true, message: '请选择表' }],
                                initialValue: this.props.table
                            })(
                                <Select placeholder="请选择表"
                                    showSearch
                                    onChange={this.tableChange.bind(this)}
                                >
                                    {this.getTableListView()}
                                </Select>
                            )}
                        </FormItem>
                        <Row type="flex" justify="center" className="font-14">
                            <a onClick={this.onSourcePreview.bind(this)}>数据预览<Icon type={this.state.showTable ? 'up' : 'down'} style={{ marginLeft: 5 }} /></a>
                        </Row>

                        {this.getTable()}


                    </Form>
                </div>
                <div
                    className="steps-action"
                >
                    {


                        <Button onClick={() => this.props.cancel()}>
                            取消
                        </Button>
                    }
                    {

                        <Button type="primary" style={{ marginLeft: 8 }} onClick={() => this.pass()}>下一步</Button>
                    }


                </div>
            </div>
        )
    }
}
const wrappedComponent = Form.create()(ManageBasicProperties);
export default wrappedComponent