import React, { Component } from "react"
import { Form, Input, Icon, Button, Checkbox, Select, Row, Card, Col, Cascader, message } from "antd";
import { Link } from 'react-router';

import DataSourceTable from "./dataSourceTable"
import { formItemLayout, TAG_TYPE } from "../../../consts"
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
        this.setDefault(this.props.initValues);
    }
    componentWillReceiveProps(nextProps) {
        if (this.props.initValues != nextProps.initValues || this.props.apiMarket.apiCatalogue != nextProps.apiMarket.apiCatalogue) {
            this.setDefault(nextProps.initValues, nextProps.apiMarket.apiCatalogue);
        }
    }
    getInitCatagoryList(value, catagorys) {

        const tree = catagorys || this.props.apiMarket.apiCatalogue;
        let arr = [];
        function exchangeTree(data) {

            if (!data || data.length < 1) {
                return null;
            }
            for (let i = 0; i < data.length; i++) {
                let item = data[i];

                if (item.id == value) {
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
            return arr.reverse();
        }
        return null;

    }
    setDefault(initValues, catalogue) {
        if (!initValues || !initValues.name) {
            return;
        }
        const name = initValues.name,
            dataSrcId = initValues.dataSourceId,
            tableName = initValues.tableName,
            reqLimit = initValues.reqLimit,
            respLimit = initValues.respLimit,
            apiDesc = initValues.apiDesc,
            catalogueId = initValues.catalogueId,
            originColumn = initValues.originColumn,
            identityId = initValues.identityId,
            tagRange = initValues.tagRange,
            identifyName = initValues.identifyName;

        this.props.form.setFieldsValue({
            APIGroup: this.getInitCatagoryList(catalogueId, catalogue),
            APIName: name,
            APIdescription: apiDesc,
            callLimit: reqLimit,
            backLimit: respLimit,
            table: tableName + '',
            dataSource: dataSrcId + '',
            originColumn,
            identityId,
            tagRange,
            identifyName
        })
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
            (item) => {
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
        return exchangeTree(tree);
    }

    render() {
        const { getFieldDecorator } = this.props.form
        const tagInfo = this.props.initValues; // tag对象
        const isRegisterTag = tagInfo && tagInfo.type === TAG_TYPE.REGISTER;
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
                                initialValue: this.props.APIGroup
                            })(
                                <Cascader showSearch options={options} placeholder="请选择分组" />
                            )
                            }
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="标签名称"
                            hasFeedback >
                            {getFieldDecorator('APIName', {
                                rules: [{ required: true, message: '请输入标签名称' },
                                { max: 16, message: "最大字数不能超过16" },
                                { pattern: new RegExp(/^([\w|\u4e00-\u9fa5]*)$/), message: 'API名字只能以字母，数字，下划线组成' }],
                                initialValue: this.props.APIName
                            })(
                                <Input />
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="标签描述"
                            hasFeedback
                        >
                            {getFieldDecorator('APIdescription', {
                                rules: [{ required: false, message: '请输入标签描述' },
                                { max: 200, message: "最大字数不能超过200" }],
                                initialValue: this.props.APIdescription
                            })(
                                <TextArea />
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="标签值域"
                            hasFeedback >
                            {getFieldDecorator('tagRange', {
                                rules: [
                                    { required: true },
                                ],
                                initialValue: this.props.tagRange
                            })(
                                <Input placeholder="请输入标签值域" />
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="调用限制"
                            hasFeedback >
                            <span>
                                单用户调用次数不能超过&nbsp; 
                            </span> 
                            {getFieldDecorator('callLimit', {
                                rules: [
                                    { required: true, message: '请输入调用次数限制' },
                                    {
                                        validator: function (rule, value, callback) {
                                            if (value && (value > 1000 || value < 1)) {
                                                callback("请输入不大于1000的正整数")
                                                return;
                                            }
                                            callback();
                                        }
                                    }
                                ],
                                initialValue: this.props.callLimit
                            })(
                                <Input placeholder="单用户每秒最高调用次数" style={{width: 100}}/>
                            )}
                            <span> 次/秒</span>
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="返回条数限制"
                            hasFeedback >
                            <span>
                                单次返回不超过&nbsp; 
                            </span> 
                            {getFieldDecorator('backLimit', {
                                rules: [{ required: true, message: '请输入最大返回条数' },
                                { pattern: new RegExp(/^1[0-9]{0,3}$|^2000$|^[0-9]$|^[1-9][0-9]{1,2}$/), message: '请输入不大于2000的正整数' }],
                                initialValue: this.props.backLimit
                            })(
                                <Input style={{width: 100}} placeholder="单次最大返回数据条数 (最高支持2000条)" />
                            )}
                             <span> 条数据（最高支持2000条）</span>
                        </FormItem>

                        <FormItem
                            {...formItemLayout}
                            label="请选择数据源"
                        >
                            {getFieldDecorator('dataSource', {
                                rules: [{ required: true, message: '请选择数据源' }],
                                initialValue: this.props.dataSource
                            })(
                                <Select disabled placeholder="请选择数据源"
                                    style={{ width: '85%', marginRight: 15 }}
                                    onChange={this.dataSourceChange.bind(this)} >
                                    {this.getDataSourceOptionView()}
                                </Select>

                            )}
                            <Link to="/dl/dataSource">添加数据源</Link>
                        </FormItem>
                        {
                            isRegisterTag && <span>
                                <FormItem
                                    {...formItemLayout}
                                    label="请选择表"
                                >
                                    {getFieldDecorator('table', {
                                        rules: [{ required: true, message: '请选择表' }],
                                        initialValue: this.props.table
                                    })(
                                        <Select disabled placeholder="请选择表"
                                            onChange={this.tableChange.bind(this)}
                                        >
                                            {this.getTableListView()}
                                        </Select>
                                    )}
                                </FormItem>
                                <FormItem
                                    {...formItemLayout}
                                    label="选择列"
                                    hasFeedback >
                                    {getFieldDecorator('originColumn', {
                                        rules: [{ required: true, message: '选择列' } ],
                                        initialValue: this.props.originColumn,
                                    })(
                                        <Select disabled placeholder="请选择列">
                                            {this.getTableListView()}
                                        </Select>
                                    )}
                                </FormItem>
                            </span>
                        }
                        <FormItem
                            {...formItemLayout}
                            label="识别列ID"
                            hasFeedback >
                            {getFieldDecorator('identityId', {
                                rules: [{ required: true, message: '选择列' } ],
                                initialValue: this.props.identityId,
                            })(
                                <Select disabled placeholder="请选择列">
                                    {this.getTableListView()}
                                </Select>
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="识别列类型"
                            hasFeedback >
                            {getFieldDecorator('identityName', {
                                rules: [{ required: true, message: '选择列' } ],
                                initialValue: this.props.identityName,
                            })(
                                <Select disabled placeholder="请选择列">
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