import React, { Component } from "react";
import { connect } from 'react-redux';
import { Link } from 'react-router';
import { Form, Input, Icon, Button, Select, Card, Cascader, message, InputNumber } from "antd";

import { dataSourceActions } from '../../../actions/dataSource';
import DataSourceTable from "./dataSourceTable";
import { formItemLayout } from "../../../consts";

const FormItem = Form.Item;
const Option = Select.Option;
const TextArea = Input.TextArea;

const mapStateToProps = state => {
    const { dataSource, tagConfig, apiMarket } = state;
    return { dataSource, tagConfig, apiMarket }
}

const mapDispatchToProps = dispatch => ({
    getDataSourcesList(params) {
        dispatch(dataSourceActions.getDataSourcesList(params));
    },
    getDataSourcesTable(params) {
        dispatch(dataSourceActions.getDataSourcesTable(params));
    },
    resetDataSourcesTable() {
        dispatch(dataSourceActions.resetDataSourcesTable());
    },
    getDataSourcesColumn(params) {
        dispatch(dataSourceActions.getDataSourcesColumn(params));
    },
})

@connect(mapStateToProps, mapDispatchToProps)
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
    tableChange(name) {
        const { form } = this.props;
        this.setState({
            showTable: false
        });
        this.props.getDataSourcesColumn({ 
            sourceId: form.getFieldValue("dataSourceId"), 
            tableName: name
        });
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

    // 类目下拉框数据初始化
    initCatagoryOption = (data) => {
        if (data.some(item => item.api === true)) {
            return [];
        } else {
            return data.map((item) => {
                return {
                    value: item.id,
                    label: item.catalogueName,
                    children: this.initCatagoryOption(item.childCatalogue)
                }
            });
        }
    }

    getCatagoryOption() {
        const tree = this.props.apiMarket.apiCatalogue;

        function exchangeTree(data) {
            let arr = []
            if (!data||data.length<1) {
                return [];
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
        const { form, tagConfig, apiMarket, dataSource } = this.props;
        const { getFieldDecorator } = form;
        const { apiCatalogue } = apiMarket;
        const { identifyColumn } = tagConfig;
        const { sourceColumn } = dataSource;
       
        return (
            <div>
                <div className="steps-content">
                    <Form>
                        <FormItem {...formItemLayout} label="所属分组">
                            {
                                getFieldDecorator('APIGroup', {
                                    rules: [{ 
                                        required: true, 
                                        message: '请选择分组' 
                                    }],
                                    initialValue: this.props.APIGroup
                                })(
                                    <Cascader 
                                        showSearch 
                                        popupClassName="noheight" 
                                        options={this.initCatagoryOption(apiCatalogue)}
                                        placeholder="请选择分组" 
                                    />
                                )
                            }
                        </FormItem>
                        <FormItem {...formItemLayout} label="标签名称">
                            {
                                getFieldDecorator('name', {
                                    rules: [{ 
                                        required: true, 
                                        message: '请输入标签名称' 
                                    }, { 
                                        max: 16,
                                        message: "最大字数不能超过16" 
                                    }, { 
                                        pattern: new RegExp(/^([\w|\u4e00-\u9fa5]*)$/), 
                                        message: 'API名字只能以字母，数字，下划线组成' 
                                    }],
                                    initialValue: this.props.APIName
                                })(
                                    <Input />
                                )
                            }
                        </FormItem>
                        <FormItem {...formItemLayout} label="标签描述">
                            {
                                getFieldDecorator('APIdescription', {
                                    rules: [{
                                        max: 200,
                                        message: "标签描述字符不能超过200"
                                    }],
                                    initialValue: this.props.APIdescription
                                })(
                                    <TextArea 
                                        placeholder="标签描述" 
                                        autosize={{ minRows: 2, maxRows: 6 }} 
                                    />
                                )
                            }
                        </FormItem>
                        <FormItem {...formItemLayout} label="值域">
                            {
                                getFieldDecorator('tagRange', {
                                    rules: [{ 
                                        required: true, 
                                        message: '标签名称不可为空' 
                                    }], 
                                    initialValue: this.props.tagRange
                                })(
                                    <TextArea 
                                        placeholder="值域" 
                                        autosize={{ minRows: 2, maxRows: 6 }} 
                                    />
                                )
                            }
                        </FormItem>
                        <FormItem {...formItemLayout} label="调用限制">
                            {
                                getFieldDecorator('callLimit', {
                                rules: [{ 
                                    required: true, 
                                    message: '请输入调用次数限制' 
                                }, {
                                        validator:function(rule, value, callback){
                                        if(value&&(value>1000||value<1)){
                                            callback("请输入不大于1000的正整数")
                                            return;
                                        }
                                        callback();
                                    }
                                }],
                                initialValue: this.props.callLimit
                            })(
                                <InputNumber
                                    min={1}
                                    step={1}
                                    max={1000}
                                    style={{ width: '85%' }}
                                    placeholder="单用户每秒最高调用次数"
                                />
                            )}
                        </FormItem>
                        <FormItem {...formItemLayout} label="返回条数限制">
                            {
                                getFieldDecorator('backLimit', {
                                    rules: [{ 
                                        required: true, 
                                        message: '请输入最大返回条数' 
                                    }, { 
                                        pattern: new RegExp(/^1[0-9]{0,3}$|^2000$|^[0-9]$|^[1-9][0-9]{1,2}$/), 
                                        message: '请输入不大于2000的正整数' 
                                    }],
                                    initialValue: this.props.backLimit
                                })(
                                    <InputNumber 
                                        min={1}
                                        step={1}
                                        max={2000}
                                        style={{ width: '85%' }}
                                        placeholder="单次最大返回数据条数 (最高支持2000条)" 
                                    />
                                )
                            }
                        </FormItem>
                        <FormItem {...formItemLayout} label="目标数据库">
                            {
                                getFieldDecorator('dataSourceId', {
                                    rules: [{ 
                                        required: true, 
                                        message: '请选择数据源' 
                                    }],
                                    initialValue: this.props.dataSourceId
                                })(
                                    <Select 
                                        showSearch
                                        placeholder="请选择数据源"
                                        style={{ width: '85%', marginRight: 15 }}
                                        onChange={this.dataSourceChange.bind(this)}>
                                        {this.getDataSourceOptionView()}
                                    </Select>
                                )
                            }
                            <Link to="/api/dataSource">添加数据源</Link>
                        </FormItem>
                        <FormItem {...formItemLayout} label="请选择表">
                            {
                                getFieldDecorator('table', {
                                    rules: [{ 
                                        required: true, 
                                        message: '请选择表' 
                                    }],
                                    initialValue: this.props.table
                                })(
                                    <Select 
                                        showSearch
                                        placeholder="请选择表"
                                        style={{ width: '85%' }}
                                        onChange={this.tableChange.bind(this)}>
                                        {this.getTableListView()}
                                    </Select>
                                )
                            }
                        </FormItem>
                        <FormItem {...formItemLayout} label="来源列">
                            {
                                getFieldDecorator('originColumn', {
                                    rules: [{ 
                                        required: true, 
                                        message: '不可为空' 
                                    }], 
                                    initialValue: this.props.originColumn
                                })(
                                    <Select
                                        showSearch
                                        // style={{ width: 150 }}
                                        // optionFilterProp="title"
                                        style={{ width: '85%' }}
                                        placeholder="选择来源列">
                                        {
                                            sourceColumn.map(item => {
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
                        <FormItem {...formItemLayout} label="识别列ID">
                            {
                                getFieldDecorator('identityColumn', {
                                    rules: [{ 
                                        required: true, 
                                        message: '标签名称不可为空' 
                                    }], 
                                    initialValue: this.props.identityColumn
                                })(
                                    <Input />
                                )
                            }
                        </FormItem>
                        <FormItem {...formItemLayout} label="识别列类型">
                            {
                                getFieldDecorator('identityId', {
                                    rules: [{ 
                                        required: true, 
                                        message: '标签名称不可为空' 
                                    }], 
                                    initialValue: this.props.identityId ? this.props.identityId.toString() : undefined
                                })(
                                    <Select
                                        showSearch
                                        optionFilterProp="title"
                                        placeholder="选择识别列类型">
                                        {
                                            identifyColumn.map((item) => {
                                                return <Option 
                                                    key={item.id} 
                                                    value={item.id.toString()}
                                                    title={item.name}>
                                                    {item.name}
                                                </Option>
                                            })
                                        }
                                    </Select>
                                )
                            }
                        </FormItem>

                        <div className="txt-center font-14">
                            <a onClick={this.onSourcePreview.bind(this)}>
                                数据预览
                                <Icon type={this.state.showTable ? 'up' : 'down'} style={{ marginLeft: 5 }} />
                            </a>
                        </div>

                        {
                            this.getTable()
                        }
                    </Form>
                </div>
                <div className="steps-action">
                    <Button 
                        className="m-r-8"
                        onClick={() => this.props.cancel()}>
                        取消
                    </Button>
                    <Button 
                        type="primary" 
                        onClick={() => this.pass()}>
                        下一步
                    </Button>
                </div>
            </div>
        )
    }
}

const wrappedComponent = Form.create()(ManageBasicProperties);
export default wrappedComponent