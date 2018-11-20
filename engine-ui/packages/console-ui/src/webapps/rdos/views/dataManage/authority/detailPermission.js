import React, { Component } from 'react'
import { Form, Input, Modal, Checkbox, Pagination } from 'antd'

import { formItemLayout } from '../../../comm/const'
import '../../../styles/pages/dataManage.scss';
const FormItem = Form.Item;

const CheckboxGroup = Checkbox.Group;
const pageSize = 20;
export const formItemLayout1 = { // 表单正常布局
    labelCol: {
        xs: { span: 24 },
        sm: { span: 5 },
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 19 },
    },
}

class DetailPermission extends Component {

    state = {
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
        }
    }

    // componentWillReceiveProps(nextProps) {
    //     this.setState({
    //         columnNames: nextProps.columnNames,
    //         arr: nextProps.columnNames.slice(0,pageSize)
    //     })
    // }

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

    render() {
        const { getFieldDecorator } = this.props.form;
        const { visible, agreeApply } = this.props;
        // const title = agreeApply ? '通过申请' : '通过申请';
        
        const title = (this.props.listType && agreeApply) ? '通过申请' : ((this.props.listType == 0 && !agreeApply) ? '通过申请' : '查看详情')

        const {permissionParams} = this.state;
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
        console.log(this.props)
        return (
            <Modal
                title={title}
                visible={visible}
                onOk={this.submit}
                onCancel={this.cancle}
                width="600"
            >
                <Form>
                    <FormItem
                        {...formItemLayout1}
                        label="表名"
                        hasFeedback
                    >
                        {getFieldDecorator('tableName', {
                            rules: [],
                        })(
                            <span style={{marginRight: "10px"}}>{ this.handleResource('resourceName') }</span>
                           
                        )}
                        <Checkbox disabled>All</Checkbox>
                    </FormItem>

                    {/* 表段权限 */}
                    <div className="content">
                        <p className="title">DDL</p>
                        <CheckboxGroup options={permissionParams.fullDdls} value={ddlCheckArray} disabled></CheckboxGroup>
                    </div>

                    <div className="content">
                        <p className="title">DML</p>
                        <CheckboxGroup options={permissionParams.fullDmls} value={dmlCheckArray} disabled ></CheckboxGroup>
                    </div>


                    {/* 字段权限 */}
                    <div className="tablepermission" style={{marginBottom:"40px",marginLeft:"55px"}}>
                        字段权限：
                        <Checkbox disabled>All(包括新增字段)</Checkbox>
                    </div>
                    <div className="content">
                        <div>
                            <CheckboxGroup options={fullColumnsCheck} value={ids} disabled></CheckboxGroup>
                        </div>
                    </div>

                    {/* <div style={{marginBottom:"20px"}}>
                        <Pagination
                            size="small"
                            total={total}
                            pageSize={pageSize}
                            current={currentPage}
                            onChange={this.onChangePage}
                        />
                    </div> */}


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

                    {
                        this.props.listType == 1  ? <FormItem
                        {...formItemLayout1}
                        label={'申请理由'}
                        hasFeedback
                        >
                        <Input type="textarea" rows={4} placeholder="" disabled={true} value="申请理由"/>,
                        </FormItem> : ""
                    }

                    {
                        this.props.listType == 2  ? <FormItem
                        {...formItemLayout1}
                        label={'审批意见'}
                        hasFeedback
                        >
                        <Input type="textarea" rows={4} placeholder="" disabled={true} value="审批意见"/>,
                        </FormItem> : ""
                    }

                    {  this.props.listType == 0 ? <FormItem
                            {...formItemLayout1}
                            label={agreeApply ? '请输入审批意见' : '请输入驳回原因'}
                            hasFeedback
                        >
                            {getFieldDecorator('reply', {
                                rules: [{
                                    max: 200,
                                    message: '请控制在200个字符以内！',
                                }],
                                // initialValue: this.props.listType == 1 ? 123 : null
                            })(
                                <Input type="textarea" rows={4} placeholder="回复内容" />,
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
