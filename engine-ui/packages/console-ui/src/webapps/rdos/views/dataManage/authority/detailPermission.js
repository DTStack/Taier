import React, { Component } from 'react'
import { Form, Input, Modal, Checkbox, Pagination } from 'antd'

import { formItemLayout } from '../../../comm/const'
import '../../../styles/pages/dataManage.scss';
const FormItem = Form.Item;

const CheckboxGroup = Checkbox.Group;
const pageSize = 1;
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

class DetailPermission extends Component {

    state = {
        currentPage: 1,
        arr: [],
        permissionParams: {
            "fullDdls":[
                {
                    "label":"insert into",
                    "value":"1",
                    "status":true
                },
                {
                    "label":"inser",
                    "value":"2",
                    "status":false
                }
            ],
            "fullDmls":[
                {
                    "label":"insert into",
                    "value":"1",
                    "status":true
                },
                {
                    "label":"inser",
                    "value":"2",
                    "status":false
                }
            ],
            "fullColumns":[
                {
                    "name":"aaa",
                    "status":true
                },
                {
                    "name":"bbb",
                    "status":false
                }
            ]
        },
    }

    componentWillReceiveProps(nextProps) {
        // this.setState({
        //     columnNames: nextProps.columnNames,
        //     arr: nextProps.columnNames.slice(0,pageSize)
        // })
        // this.setState({
        //     tableResource: nextProps.table[0]
        // })
        // console.log("===============")
        // console.log(nextProps.table)
    }

    submit = (e) => {
        e.preventDefault()
        const { onOk, form } = this.props
        const formData = form.getFieldsValue()
        form.validateFields((err) => {
            if (!err) {
                setTimeout(() => { form.resetFields() }, 200)
                onOk(formData)
            }
        });
    }

    cancle = () => {
        const { onCancel, form } = this.props
        onCancel()
        form.resetFields()
    }
    

    handleResource(type){
        const { table } = this.props;
        let data;
        if(type === "resourceName"){
            const resourceName =  table.map(v=>{
                return v.resourceName
            })
            data = resourceName.join(" 、")
        }else{
            data =  table.map(v=>{
                return v.applyId
            })
        }
        return data
    }

     // 改变页码
     onChangePage = (currentPage, pageSize) => {
        const {permissionParams} = this.state;
        const fullColumnsCheck = permissionParams.fullColumns.map(item => {
            return item.name
        });
        const arr = fullColumnsCheck.slice((currentPage-1)*pageSize,(currentPage-1)*pageSize+pageSize)
        this.setState({
            currentPage,
            arr
        })
        console.log(currentPage)
    }

    render() {
        const { getFieldDecorator } = this.props.form;
        const { visible, agreeApply, table } = this.props;
        // const title = agreeApply ? '通过申请' : '驳回申请';
        
        const title = (this.props.listType == 0 && agreeApply) ? '通过申请' : ((this.props.listType == 0 && !agreeApply) ? '驳回申请' : '查看详情')
        const {permissionParams, arr, currentPage} = this.state;
        const ddlCheck = permissionParams.fullDdls.filter(item => {
            return item.status === true
        })
        const ddlCheckArray = ddlCheck.map(item => {
            return item.value
        })

        const dmlCheck = permissionParams.fullDmls.filter(item => {
            return item.status === true
        })
        const dmlCheckArray = dmlCheck.map(item => {
            return item.value
        })

        const fullColumnsCheck = permissionParams.fullColumns.map(item => {
            return item.name
        });
        const fullColumnsCheckArray = permissionParams.fullColumns.filter(item => {
            return item.status === true
        })
        const ids = fullColumnsCheckArray.map(item => {
            return item.name
        })
        const total = fullColumnsCheck.length;
        // 判断是否全选
        const ischeckAll = (permissionParams.fullDdls.length + permissionParams.fullDmls.length) == (ddlCheck.length + dmlCheck.length);
        const idCheckIds = (permissionParams.fullColumns.length == fullColumnsCheckArray.length);
        return (
            <Modal
                title={title}
                visible={visible}
                // onOk={this.submit}
                onOk={this.props.listType == 0 ? this.submit : this.cancle}
                onCancel={this.cancle}
                width="750px"
            >
                <Form>
                    <FormItem
                        {...formItemLayout1}
                        label="名称"
                        hasFeedback
                    >
                        {getFieldDecorator('tableName', {
                            rules: [],
                        })(
                            <span style={{marginRight: "10px"}}>{ this.handleResource('resourceName') }</span>
                           
                        )}
                        <Checkbox disabled checked={ischeckAll}>All</Checkbox>
                    </FormItem>

                     {/* 表段权限 */}
                    <FormItem
                        {...formItemLayout1}
                        label="DDL"
                        style={{marginLeft:"20px",marginTop:"-20px"}}
                    >
                        <div className="content">
                            <CheckboxGroup options={permissionParams.fullDdls} value={ddlCheckArray} disabled></CheckboxGroup>
                        </div>
                    </FormItem>
                    <FormItem
                        {...formItemLayout1}
                        label="DML"
                        style={{marginLeft:"20px",marginTop:"-20px"}}
                    >
                        <div className="content">
                            <CheckboxGroup options={permissionParams.fullDmls} value={dmlCheckArray} disabled ></CheckboxGroup>
                        </div>
                    </FormItem>

                    {/* 字段权限 */}
                    <FormItem
                        {...formItemLayout1}
                        label="字段权限"
                        style={{background: "#FAFAFA"}}
                    >
                        <Checkbox disabled checked={idCheckIds}>All(包括新增字段)</Checkbox>
                        <div className="content">
                            <div>
                                <CheckboxGroup options={arr} value={ids} disabled></CheckboxGroup>
                            </div>
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


                    <FormItem
                        style={{display: 'none'}}
                    >
                        {getFieldDecorator('ids', {
                            rules: [],
                            initialValue: this.handleResource('ids')
                        })(
                            <Input type="hidden" />,
                        )}
                    </FormItem>

                    <FormItem
                        style={{display: 'none'}}
                    >
                        {getFieldDecorator('status', {
                            rules: [],
                            initialValue: agreeApply ? 1 : 2
                        })(
                            <Input type="hidden" />,
                        )}
                    </FormItem>

                    {/* 申请记录 */}
                    {
                        this.props.listType == 1  ? <FormItem
                        {...formItemLayout1}
                        label={'申请理由'}
                        hasFeedback
                        >
                        <Input type="textarea" rows={4} placeholder="" disabled={true} value={this.props.applyReason} style={{width: "80%"}}/>,
                        </FormItem> : ""
                    }
                    {/* 已处理  权限回收*/}
                    {
                        this.props.listType == 2 || this.props.listType == 3  ? <FormItem
                        {...formItemLayout1}
                        label={'审批意见'}
                        hasFeedback
                        >
                        <Input type="textarea" rows={4} placeholder="" disabled={true} value={this.props.reply} style={{width: "80%"}}/>,
                        </FormItem> : ""
                    }

                    {/* 待我审批 */}
                    {  this.props.listType == 0 ? <FormItem
                            {...formItemLayout1}
                            label={agreeApply ? '请输入审批意见' : '请输入驳回原因'}
                            hasFeedback
                        >
                            {getFieldDecorator('reply', {
                                rules: [{
                                    required: agreeApply ? false : true,
                                    max: 200,
                                    message: '请控制在200个字符以内！',
                                }],
                                // initialValue: this.props.listType == 1 ? 123 : null
                            })(
                                <Input type="textarea" rows={4} placeholder="回复内容" style={{width: "80%"}} />,
                            )}
                        </FormItem> : ""
                    }
                </Form>
            </Modal>
        )
    }
}
const FormWrapper = Form.create()(DetailPermission);
export default FormWrapper
