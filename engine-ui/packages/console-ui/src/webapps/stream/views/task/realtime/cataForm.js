import React, { Component } from 'react'
import {
    Form, Input, Modal
} from 'antd'
import { isEmpty } from 'lodash'

import { getContainer } from 'funcs';

import { formItemLayout } from '../../../comm/const'
import FolderPicker from './folderTree'

const FormItem = Form.Item;

class CataFormModal extends Component {
    onSelect = (value) => {
        this.props.form.setFieldsValue({ nodePid: value })
    }

    submit = (e) => {
        e.preventDefault()
        const { handOk, form } = this.props
        form.validateFields((err, formData) => {
            if (!err) {
                handOk({
                    ...formData,
                    nodeName: formData.name,
                    name: undefined
                })
                setTimeout(() => {
                    form.resetFields()
                }, 100)
            }
        })
    }

    render () {
        const {
            handCancel, visible, treeData, form,
            loadTreeData, operation, defaultData
        } = this.props
        const { getFieldDecorator } = form

        const isEdit = operation && operation.indexOf('EDIT') > -1;
        const title = isEdit ? '编辑目录' : '创建目录'

        let savePath = treeData[0] && treeData[0].id;
        if (!isEmpty(defaultData)) {
            savePath = isEdit ? defaultData.parentId : defaultData.id;
        }

        return (
            <div id="JS_cata_modal">
                <Modal
                    title={title}
                    key={operation}
                    visible={visible}
                    onOk={this.submit}
                    onCancel={handCancel}
                    getContainer={() => getContainer('JS_cata_modal')}
                >
                    <Form>
                        <FormItem
                            {...formItemLayout}
                            label="目录名称"
                            hasFeedback
                        >
                            {getFieldDecorator('name', {
                                rules: [{
                                    required: true, message: '目录名称不可为空！'
                                }, {
                                    max: 20,
                                    message: '项目名称不得超过20个字符！'
                                }],
                                initialValue: isEdit ? defaultData.name : ''
                            })(
                                <Input />
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="选择存储位置"
                            hasFeedback
                        >
                            {getFieldDecorator('nodePid', {
                                rules: [{
                                    required: true, message: '请您选择存储位置！'
                                }],
                                initialValue: savePath
                            })(
                                <FolderPicker
                                    isPicker
                                    id="cataForm"
                                    isFolderPicker
                                    treeData={ treeData }
                                    loadData={ loadTreeData }
                                />
                            )}
                        </FormItem>
                    </Form>
                </Modal>
            </div>
        )
    }
}

const WrappedCataForm = Form.create()(CataFormModal);
export default WrappedCataForm
