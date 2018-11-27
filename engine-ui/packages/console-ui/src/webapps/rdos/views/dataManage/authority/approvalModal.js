import React, { Component } from 'react'
import { Form, Input, Modal } from 'antd'

import { formItemLayout } from '../../../comm/const'

const FormItem = Form.Item

class ApprovelModal extends Component {

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
        const title = agreeApply ? '批量通过' : '批量驳回';
        
        return (
            <Modal
                title={title}
                visible={visible}
                onOk={this.submit}
                onCancel={this.cancle}
            >
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label="名称"
                        hasFeedback
                    >
                        {getFieldDecorator('tableName', {
                            rules: [],
                        })(
                            <span>{ this.handleResource('resourceName') }</span>
                           
                        )}
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
                    <FormItem
                        {...formItemLayout}
                        label={agreeApply ? '请输入审批意见' : '请输入驳回原因'}
                        hasFeedback
                    >
                        {getFieldDecorator('reply', {
                            rules: [{
                                required: agreeApply ? false : true,
                                max: 200,
                                message: '请控制在200个字符以内！',
                            }],
                        })(
                            <Input type="textarea" rows={4} placeholder="回复内容" />,
                        )}
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
const FormWrapper = Form.create()(ApprovelModal);
export default FormWrapper
