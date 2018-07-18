import React, { Component } from "react"
import { Form, Input, Button, Select, Card, Cascader, message } from "antd";

import DataSourceTable from "./dataSourceTable"
import { formItemLayout, API_METHOD, API_METHOD_key } from "../../../consts"
import NewGroupModal from "../../../components/newGroupModal";

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
    cancelAndSave() {
        const {getFieldsValue} = this.props.form;
        this.props.cancelAndSave({ ...getFieldsValue() });
    }
    //数据源改变，获取表
    dataSourceChange(key) {
        this.setState({
            showTable: false
        }, )
        this.props.form.setFieldsValue({
            table: ""
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
            (item, index) => {
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

            if (!data || data.length < 1) {
                return null;
            }
            for (let i = 0; i < data.length; i++) {
                let item = data[i];

                if (item.api) {
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

        return exchangeTree(tree) || [];
    }
    showNewGroup() {
        this.setState({
            newGroupModalShow: true
        })
    }
    hideNewGroup() {
        this.setState({
            newGroupModalShow: false
        })
    }
    groupChange(value) {
        const tree=this.props.apiMarket.apiCatalogue;
        let arr = [];

        function exchangeTree(data) {
            if (!data || data.length < 1) {
                return null;
            }

            for (let i = 0; i < data.length; i++) {
                let item = data[i];

                if (item.id == value&&!item.api) {
                    arr.push(item.id);
                    return item.id;
                }
                if (exchangeTree(item.childCatalogue)) {
                    arr.push(item.id);
                    return item.id
                }
            }
            return null;
        }

        if (exchangeTree(tree)) {
            this.props.form.setFieldsValue({
                APIGroup: arr.reverse()
            })
        }

    }
    renderMethod(){
        let arr=[];
        for(let key in API_METHOD){
            let value=API_METHOD[key];
            arr.push(<Option value={value}>{key}</Option>)
        }
        return arr;
    }
    render() {
        const { getFieldDecorator } = this.props.form
        const options = this.getCatagoryOption();
        const { newGroupModalShow } = this.state;

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
                                initialValue: this.props.APIGroup
                            })(
                                <Cascader style={{ width: '85%' }} showSearch popupClassName="noheight" options={options} placeholder="请选择分组" />
                            )
                            }
                            <a style={{ paddingLeft: "8px" }} onClick={this.showNewGroup.bind(this)} >新建分组</a>
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="API名称"
                        >
                            {getFieldDecorator('APIName', {
                                rules: [{ required: true, message: '请输入API名称' },
                                { min: 2, message: "最小字数不能少于2" },
                                { max: 16, message: "最大字数不能超过16" },
                                { pattern: new RegExp(/^([\w|\u4e00-\u9fa5]*)$/), message: 'API名字只能以字母，数字，下划线组成' }],
                                initialValue: this.props.APIName
                            })(
                                <Input style={{ width: '85%' }} />
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="API描述"
                        >
                            {getFieldDecorator('APIdescription', {
                                rules: [{ required: false, message: '请输入API描述' },
                                { max: 200, message: "最大字符不能超过200" }],
                                initialValue: this.props.APIdescription
                            })(
                                <TextArea style={{ width: '85%' }} />
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="API path"
                        >
                            {getFieldDecorator('APIPath', {
                                rules: [
                                { max: 200, message: "最大字符不能超过200" },
                                { min: 2, message: "最小字符不能小于2" },
                                { pattern: new RegExp(/^(\/[-|\w]+)+$/), message: '支持英文，数字，下划线，连字符(-)，限制2—200个字符，只能 / 开头，如/user' },
                                { pattern: new RegExp(/^(([^\/]*\/[^\/]*){1,2}|[^\/]*)$/), message: '最多支持两层路径' }],
                                initialValue: this.props.APIPath
                            })(
                                <Input style={{ width: '85%' }} />
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="调用限制"
                        >
                            {getFieldDecorator('callLimit', {
                                rules: [
                                    { required: true, message: '请输入调用次数限制' },
                                    {
                                        validator: function (rule, value, callback) {
                                            if (value && (value > 2000 || value < 1)) {
                                                callback("请输入不大于2000的正整数")
                                                return;
                                            }
                                            callback();
                                        }
                                    }
                                ]
                                ,
                                initialValue: this.props.callLimit
                            })(
                                <Input style={{ width: '85%' }} type="number" placeholder="单用户每秒最大调用次数不超过2000次" />
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="协议"
                        >
                            {getFieldDecorator('protocol', {
                                initialValue: "HTTP/HTTPS"
                            })(
                                <Select style={{ width: '85%' }}>
                                    <Option value="HTTP/HTTPS">HTTP/HTTPS</Option>
                                </Select>
                                // <Input disabled  style={{ width: '85%' }}/>
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="请求方式"
                        >
                            {getFieldDecorator('method', {
                                rules: [{ required: true, message: "请选择请求方式" }],
                                initialValue: (this.props.reqType||this.props.reqType==0)?this.props.reqType:API_METHOD.POST
                            })(
                                <Select style={{ width: '85%' }}>
                                    {this.renderMethod()}
                                </Select>
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="返回类型"
                        >
                            {getFieldDecorator('responseType', {
                                rules: [{ required: true, message: "请选择返回类型" }],
                                initialValue: "JSON"
                            })(
                                <Select style={{ width: '85%' }}>
                                    <Option value="JSON">JSON</Option>
                                </Select>
                                // <Input disabled  style={{ width: '85%' }} />
                            )}
                        </FormItem>
                    </Form>
                </div>
                <div
                    className="steps-action"
                >
                    <Button onClick={() => this.cancelAndSave()}>
                        保存并退出
                    </Button>
                    <Button type="primary" style={{ marginLeft: 8 }} onClick={() => this.pass()}>下一步</Button>

                </div>
                <NewGroupModal groupChange={this.groupChange.bind(this)} visible={newGroupModalShow} cancel={this.hideNewGroup.bind(this)} />
            </div>
        )
    }
}
const wrappedComponent = Form.create()(ManageBasicProperties);
export default wrappedComponent