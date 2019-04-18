import React from 'react';

import { Modal, Form, Input, Radio, Select } from 'antd';

import { formItemLayout } from '../../consts';

const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const Option = Select.Option;

const PUBLISH_TYPE = {
    NEW: '0',
    UPDATE: '1'
};
class ModelSubmitModalForm extends React.Component {
    state = {
        key: null
    }
    onCancel = () => {
        this.props.onClose();
        this.setState({
            key: Math.random()
        })
    }
    onOk = () => {
        this.props.form.validateFields((err, values) => {
            if (!err) {
                this.props.onOk(values).then((success) => {
                    if (success) {
                        this.onCancel();
                    }
                })
            }
        });
    }
    renderDetail () {
        const { _formData, form } = this.props;
        const { getFieldDecorator } = form;
        const { publishType, modelVersions } = _formData;
        switch (publishType.value) {
            case PUBLISH_TYPE.NEW: {
                return (
                    <FormItem
                        {...formItemLayout}
                        label='自定义模型名称'
                    >
                        {getFieldDecorator('modelName', {
                            rules: [{
                                required: true,
                                message: '请输入模型名称'
                            }, {
                                pattern: /^\w{0,32}$/,
                                message: '模型名称不超过32个字符，支持字母、数字与下划线'
                            }]
                        })(
                            <Input />
                        )}
                    </FormItem>
                )
            }
            case PUBLISH_TYPE.UPDATE: {
                return (
                    <React.Fragment>
                        <FormItem
                            {...formItemLayout}
                            label='选择模型名称'
                        >
                            {getFieldDecorator('modelVersions', {
                                rules: [{
                                    required: true,
                                    message: '请选择模型名称'
                                }]
                            })(
                                <Select>
                                    <Option key='1.0'>1.0</Option>
                                </Select>
                            )}
                        </FormItem>
                        {modelVersions && modelVersions.value ? (
                            <FormItem
                                {...formItemLayout}
                                label='模型版本号'
                            >
                                <Input disabled />
                            </FormItem>
                        ) : null}
                    </React.Fragment>
                )
            }
        }
    }
    render () {
        const { key } = this.state;
        const { visible, form, isNotebook, data } = this.props;
        const { getFieldDecorator } = form;
        return (
            <Modal
                key={key}
                visible={visible}
                onCancel={this.onCancel}
                onOk={this.onOk}
                title='模型在线部署'
            >
                <Form>
                    {isNotebook && (
                        <FormItem
                            {...formItemLayout}
                            label='Notebook作业名称'
                        >
                            <Input disabled value={data.name} />
                        </FormItem>
                    )}
                    {!isNotebook && (
                        <FormItem
                            {...formItemLayout}
                            label='选择部署模型'
                        >
                            {getFieldDecorator('modelId')(
                                <Select>
                                    <Option key='1'>模型一</Option>
                                </Select>
                            )}
                        </FormItem>
                    )}
                    <FormItem
                        {...formItemLayout}
                        label='部署方式'
                    >
                        {getFieldDecorator('publishType')(
                            <RadioGroup>
                                <Radio key={PUBLISH_TYPE.NEW} value={PUBLISH_TYPE.NEW}>新建模型服务</Radio>
                                <Radio key={PUBLISH_TYPE.UPDATE} value={PUBLISH_TYPE.UPDATE}>增加已有模型的服务版本</Radio>
                            </RadioGroup>
                        )}
                    </FormItem>
                    {this.renderDetail()}
                </Form>
            </Modal>
        )
    }
}
const WrapModelSubmitModalForm = Form.create({
    mapPropsToFields (props) {
        return props._formData;
    },
    onFieldsChange (props, fields) {
        if (fields.hasOwnProperty('publishType')) {
            fields.modelVersions = {
                value: undefined
            }
            fields.modelName = {
                value: undefined
            }
        }
        if (fields.hasOwnProperty('modelId')) {
            fields.modelName = {
                value: undefined
            }
            fields.modelVersions = {
                value: undefined
            }
            fields.publishType = {
                value: PUBLISH_TYPE.NEW
            }
        }
        props.onFieldsChange(fields);
    }
})(ModelSubmitModalForm);

class ModelSubmitModal extends React.Component {
    state = {
        _formData: {
            publishType: {
                value: PUBLISH_TYPE.NEW
            }
        }
    }
    onFieldsChange = (fields) => {
        this.setState({
            _formData: {
                ...this.state._formData,
                ...fields
            }
        })
    }
    onClose = () => {
        this.setState({
            _formData: {
                publishType: {
                    value: PUBLISH_TYPE.NEW
                }
            }
        })
        return this.props.onClose();
    }
    render () {
        return (
            <WrapModelSubmitModalForm
                _formData={this.state._formData}
                onFieldsChange={this.onFieldsChange}
                {...this.props}
                onClose={this.onClose}
            />
        )
    }
}
export default ModelSubmitModal;
