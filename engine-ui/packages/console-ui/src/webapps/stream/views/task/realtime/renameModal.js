import React, { Component } from 'react'
import {
    Form, Input, Modal
} from 'antd'

import { getContainer } from 'funcs';
import { formItemLayout } from '../../../comm/const'

const FormItem = Form.Item

class RenameModal extends Component {
    submit = (e) => {
        e.preventDefault()
        const { handOk } = this.props
        const formData = this.props.form.getFieldsValue()
        this.props.form.validateFields((err) => {
            if (!err) {
                handOk(formData)
            }
        });
    }

    render () {
        const { data, visible, handCancel } = this.props
        const { getFieldDecorator } = this.props.form
        return (
            <div id="JS_rename_modal">
                <Modal
                    title="重命名"
                    visible={visible}
                    onOk={this.submit}
                    onCancel={handCancel}
                    getContainer={() => getContainer('JS_rename_modal')}
                >
                    <Form>
                        <FormItem
                            {...formItemLayout}
                            label="新名称"
                            hasFeedback
                        >
                            {getFieldDecorator('name', {
                                rules: [{
                                    required: true, message: '名称不可为空！'
                                }],
                                initialValue: data.name
                            })(
                                <Input placeholder="请输入资源名称" />
                            )}
                        </FormItem>
                    </Form>
                </Modal>
            </div>
        )
    }
}
const wrappedForm = Form.create()(RenameModal);
export default wrappedForm
