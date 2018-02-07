import React, { Component } from 'react'
import {
    Form, Input, Modal, Select,
 } from 'antd'
 import { isEmpty }  from 'lodash'

import { formItemLayout } from '../../../comm/const'
import FolderPicker from './folderTree'

const FormItem = Form.Item
const Option = Select.Option

class ResFormModal extends Component {

    state = {
        file: '',
        accept: '.jar',
        fileType: '2',
    }

    submit = (e) => {
        e.preventDefault()
        const { handOk, form } = this.props
        const { file } = this.state
        const resource = this.props.form.getFieldsValue()
        this.props.form.validateFields((err) => {
            if (!err) {
                resource.file = file.files[0]
                handOk(resource)
                setTimeout(() => {
                    form.resetFields()
                    this.setState({ file: '' })
                }, 100)
            }
        });
    }

    onSelect = (value) => {
        this.props.form.setFieldsValue({ nodePid: value })
    }

    fileChange = (e) => {
        const file = e.target
        this.setState({ file })
    }

    validateFileType = (rule, value, callback) => {
        const reg = /\.(jar)$/
        if (value && !reg.test(value)) {
            callback('资源文件只能是Jar文件!');
        }
        callback();
    }

    changeFileType = (value) => {
        this.setState({
            accept: value === '2' ? '' : '.jar',
            fileType: value,
        })
    }

    render() {
        const {
            form, resRoot, title, ayncTree,
            handCancel, visible, activeNode,
        } = this.props
        const { file } = this.state
        const { getFieldDecorator } = form
        return (
            <Modal
              title={title}
              wrapClassName="vertical-center-modal"
              visible={visible}
              onOk={this.submit}
              onCancel={handCancel}
            >
                <Form>
                    <FormItem
                      {...formItemLayout}
                      label="资源名称"
                      hasFeedback
                    >
                        {getFieldDecorator('resourceName', {
                            rules: [{
                                required: true, message: '资源名称不可为空！',
                            }, {
                                pattern: /^[A-Za-z0-9_-]+$/,
                                message: '资源名称只能由字母、数字、下划线组成!',
                            }, {
                                max: 20,
                                message: '资源名称不得超过20个字符！',
                            }],
                        })(
                            <Input placeholder="请输入资源名称" />,
                        )}
                    </FormItem>
                    <FormItem
                      {...formItemLayout}
                      label="资源类型"
                      hasFeedback
                    >
                        {getFieldDecorator('resourceType', {
                            rules: [{
                                required: true, message: '资源类型必选！',
                            }],
                            initialValue: '1',
                        })(
                            <Select onChange={this.changeFileType}>
                                <Option key="jar" value="1">jar</Option>
                            </Select>,
                        )}
                    </FormItem>
                    <FormItem
                      {...formItemLayout}
                      label="上传"
                      hasFeedback
                    >
                        {getFieldDecorator('file', {
                            rules: [{
                                required: true, message: '请选择上传文件',
                            }, {
                                validator: this.validateFileType,
                            }],
                        })(
                            <div>
                                <label
                                  style={{ lineHeight: '28px' }}
                                  className="ant-btn"
                                  htmlFor="myFile">选择文件</label>
                                <span> {file.files && file.files[0].name}</span>
                                <input
                                  name="file"
                                  type="file"
                                  id="myFile"
                                  accept=".jar"
                                  onChange={this.fileChange}
                                  style={{ display: 'none' }}
                                />
                            </div>,
                        )}
                    </FormItem>
                    <FormItem
                      {...formItemLayout}
                      label="选择存储位置"
                      hasFeedback
                    >
                        {getFieldDecorator('nodePid', {
                            rules: [{
                                required: true, message: '存储位置必选！',
                            }],
                            initialValue: !isEmpty(activeNode) ? activeNode.id : '',
                        })(
                            <FolderPicker
                                isPicker
                                isFolderPicker
                                treeData={ resRoot }
                                loadData={ ayncTree }
                                onSelect={this.onSelect}
                            />,
                        )}
                    </FormItem>
                    <FormItem
                      {...formItemLayout}
                      label="描述"
                      hasFeedback
                    >
                        {getFieldDecorator('resourceDesc', {
                            rules: [{
                                max: 200,
                                message: '描述请控制在200个字符以内！',
                            }],
                            initialValue: '',
                        })(
                            <Input type="textarea" rows={4} />,
                        )}
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
const wrappedForm = Form.create()(ResFormModal);
export default wrappedForm
