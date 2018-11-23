import React, { Component } from 'react'
import { Form, Input, Modal, InputNumber, Checkbox, Pagination } from 'antd'

import { formItemLayout } from '../../../comm/const'
import ajax from '../../../api/dataManage';

import '../../../styles/pages/dataManage.scss';

const FormItem = Form.Item
const CheckboxGroup = Checkbox.Group;

const warning = Modal.warning

const pageSize = 20;
const formItemLayout1 = { // ddl,dml表单布局
    labelCol: {
        xs: { span: 24 },
        sm: { span: 4 },
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 20 },
    },
}
class TableApply extends Component {

    state = {
        checkDdlAll: false,
        checkedList: [],   // DDL选中
        checkedDmlList: [], // // DML选中

        arr: [],
        checkIdsAll: false,
        checkedIdsList: [],
        // 字段名
        columnNames: [],
        currentPage: 1,
    }

    componentWillReceiveProps(nextProps) {
        if(this.props.columnNames != nextProps.columnNames) {
            this.setState({
                columnNames: nextProps.columnNames,
                arr: nextProps.columnNames.slice(0,pageSize)
            })
        }
    }


    // 复选框
    changeDdlGroup = (checkedList) => {
        const {ddlList, dmlList} = this.props;
        const {checkedDmlList} = this.state;
        this.setState({
            checkedList,
            checkDdlAll: (checkedList.length+checkedDmlList.length) === (ddlList.length+dmlList.length)
        })
    }

    changeDmlGroup = (checkedDmlList) => {
        const {ddlList, dmlList } = this.props;
        const {checkedList} = this.state;
        this.setState({
            checkedDmlList,
            checkDdlAll: (checkedList.length+checkedDmlList.length) === (ddlList.length+dmlList.length)
        })
    }

    changeIdsGroup = (checkedIdsList) => {
        const {columnNames} = this.state;
        this.setState({
            checkedIdsList,
            checkIdsAll: checkedIdsList.length === columnNames.length
        })
    }


    // 全选
    onCheckDdlAll = (e) => {
        const {ddlList, dmlList} = this.props;
        const ddlListData = ddlList.map(item => {
            return item.value
        })
        const dmlListData = dmlList.map(item => {
            return item.value
        })
        this.setState({
            checkedList: e.target.checked ? ddlListData : [],
            checkedDmlList: e.target.checked ? dmlListData: [],
            checkDdlAll: e.target.checked
        })
    }

    onCheckIdsAll = (e) => {
        const {columnNames} = this.state;
        this.setState({
            checkedIdsList: e.target.checked ? columnNames : [],
            checkIdsAll: e.target.checked
        })
    }

    // 改变页码
    onChangePage = (currentPage, pageSize) => {
        const {columnNames} = this.state;
        const arr = columnNames.slice((currentPage-1)*pageSize,(currentPage-1)*pageSize+pageSize)
        this.setState({
            currentPage,
            arr
        })
        console.log(currentPage)
    }

    submit = (e) => {
        e.preventDefault()
        const { onOk, form } = this.props
        const formData = form.getFieldsValue()
        const {checkedList, checkedDmlList, checkedIdsList, checkIdsAll} = this.state;
        console.log(formData)
        const paramsApply = {
            ddl: checkedList,
            dml: checkedDmlList,
            columnNames: checkedIdsList,
            fullColumn: checkIdsAll
        }
        // if(!(checkedList&&checkedDmlList)) {
        //     warning({
        //         title: '提示',
        //         content: '您没有选择任何DDL或者DML权限！',
        //     })
        // }
        const params = {...paramsApply, ...formData}
            form.validateFields((err) => {
                const {checkedList, checkedDmlList, checkedIdsList} = this.state;
                if (!err && ((checkedList.length + checkedDmlList.length > 0)|| checkedIdsList.length > 0)) {
                        setTimeout(() => { 
                            this.setState({
                                checkDdlAll: false,
                                checkedList: [],   // DDL选中
                                checkedDmlList: [], // // DML选中
                                checkIdsAll: false,
                                checkedIdsList: [],
                            })
                            form.resetFields() 
                        }, 200)
                        onOk(params)
                }
                else if(err) {
                    
                }
                else {
                    warning({
                        title: '提示',
                        content: '操作权限或者字段权限未选择！',
                    })
                }
            });
        
    }

    cancle = () => {
        const { onCancel, form } = this.props;
        const {checkedList,checkedDmlList} = this.state;
        onCancel()
        form.resetFields()
        this.setState({
            checkedList: [],
            checkedDmlList: []
        })
    }

    render() {
        const { getFieldDecorator } = this.props.form;
        const { visible, table, ddlList, dmlList } = this.props;
        const {checkDdlAll, checkedList, checkedDmlList,
            checkIdsAll, checkedIdsList
        } = this.state;
        const {currentPage, arr, columnNames} = this.state;
        const total = columnNames.length;
        console.log(total)
        return (
            <Modal
                title="申请授权"
                visible={visible}
                onOk={this.submit}
                onCancel={this.cancle}
                width="750px"
            >
                <Form>
                    <FormItem
                        {...formItemLayout1}
                        label="申请表名"
                        hasFeedback
                    >
                        <span style={{marginRight: "10px"}}>{table.tableName}</span>
                        <Checkbox checked={checkDdlAll} onChange={this.onCheckDdlAll}>All</Checkbox>
                    </FormItem>

                    {/* 表段权限 */}
                    <FormItem
                        {...formItemLayout1}
                        label="DDL"
                        style={{marginTop:"-20px"}}
                    >
                        <div className="content">
                            <CheckboxGroup options={ddlList} value={checkedList} onChange={this.changeDdlGroup}></CheckboxGroup>
                        </div>
                    </FormItem>

                    <FormItem
                        {...formItemLayout1}
                        label="DDL"
                        style={{marginTop:"-20px"}}
                    >
                        <div className="content">
                            <CheckboxGroup options={dmlList} value={checkedDmlList} onChange={this.changeDmlGroup}></CheckboxGroup>
                        </div>
                    </FormItem>

                    {/* 字段权限 */}
                    <FormItem
                        {...formItemLayout1}
                        label="字段权限"
                        style={{background: "#FAFAFA"}}
                    >
                        <Checkbox checked={checkIdsAll} onChange={this.onCheckIdsAll}>All(包括新增字段)</Checkbox>
                        <div className="content">
                            <CheckboxGroup options={arr} value={checkedIdsList} onChange={this.changeIdsGroup}></CheckboxGroup>
                        </div>
                        {/* 控制字段名分页 */}
                        {total > 0 ? <Pagination
                            size="small"
                            total={total}
                            pageSize={pageSize}
                            current={currentPage}
                            onChange={this.onChangePage}
                            style={{marginLeft:"70%",marginTop:"10px",marginBottom: "20px"}}
                        /> : ""}
                    </FormItem>

                    {/* 控制字段名分页 */}
                    {/* <FormItem
                        {...formItemLayout1}
                        style={{marginLeft:"70%"}}
                    >
                        <Pagination
                            // size="small"
                            total={total}
                            pageSize={pageSize}
                            current={currentPage}
                            onChange={this.onChangePage}
                        />
                    </FormItem> */}

                    <FormItem
                        {...formItemLayout1}
                        label="权限有效期"
                        hasFeedback
                    >
                        {getFieldDecorator('day', {
                            rules: [
                                {
                                    required: true, message: '请选择有效期时间',
                                }
                            ],
                        })(
                            <InputNumber min={1} placeholder="请输入申请时长（天）" style={{width: "80%"}}/>,
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout1}
                        label="申请理由"
                        hasFeedback
                    >
                        {getFieldDecorator('applyReason', {
                            rules: [{
                                max: 200,
                                message: '申请理由请控制在200个字符以内！',
                            },{
                                required: true, message: '必须填写申请理由',
                              }],
                        })(
                            <Input type="textarea" rows={4} placeholder="请输入申请理由" style={{width: "80%"}}/>,
                        )}
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
const FormWrapper = Form.create()(TableApply);
export default FormWrapper
