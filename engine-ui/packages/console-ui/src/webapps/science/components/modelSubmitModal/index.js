import React from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';

import { Modal, Form, Input, Radio, Select, Icon } from 'antd';

import CopyIcon from 'main/components/copy-icon';
import './modelSubmitModal.scss';

import * as baseActions from '../../actions/base'
import { formItemLayout, siderBarType } from '../../consts';

const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const Option = Select.Option;

const PUBLISH_TYPE = {
    NEW: '0',
    UPDATE: '1'
};
@connect(null, dispatch => {
    return {
        ...bindActionCreators(baseActions, dispatch)
    }
})
class ModelSubmitModalForm extends React.Component {
    state = {
        isSuccess: false,
        successData: {}
    }
    onOk = () => {
        this.props.form.validateFields((err, values) => {
            if (!err) {
                Modal.confirm({
                    title: '确认部署',
                    content: '确认部署此模型吗？',
                    okText: '确定',
                    cancelText: '取消',
                    onOk: () => {
                        this.props.onOk(values).then((res) => {
                            if (res) {
                                this.setState({
                                    isSuccess: true,
                                    successData: res.data
                                })
                            }
                        })
                    }
                });
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
    renderSuccess () {
        const { successData } = this.state;
        return (
            <div className='c-modelSubmitModal'>
                <div className='c-modelSubmitModal__sign'>
                    <Icon type='check-circle' className='c-modelSubmitModal__sign__logo' />
                    <span className='c-modelSubmitModal__sign__txt'>部署成功</span>
                </div>
                <div className='c-modelSubmitModal__content'>
                    <Form>
                        <FormItem
                            label='调用API地址'
                        >
                            <Input
                                addonBefore="API URL"
                                disabled
                                value={successData.url}
                                addonAfter={<CopyIcon copyText={successData.url} />}
                            />
                        </FormItem>
                        <p>前往 <a onClick={() => {
                            this.props.onClose();
                            this.props.changeSiderBar(siderBarType.model)
                        }}>模型</a> 页面查看部署结果</p>
                    </Form>
                </div>
            </div>
        )
    }
    render () {
        const { isSuccess } = this.state;
        const { visible, form, isNotebook, data, onClose } = this.props;
        const { getFieldDecorator } = form;
        return (
            <Modal
                visible={visible}
                onCancel={onClose}
                onOk={this.onOk}
                footer={isSuccess ? null : undefined}
                title='模型在线部署'
            >
                {isSuccess ? (
                    this.renderSuccess()
                ) : (<Form>
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
                </Form>)}
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
        },
        key: null
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
        this.props.onClose();
        this.setState({
            _formData: {
                publishType: {
                    value: PUBLISH_TYPE.NEW
                }
            },
            key: Math.random()
        })
    }
    render () {
        return (
            <WrapModelSubmitModalForm
                key={this.state.key}
                _formData={this.state._formData}
                onFieldsChange={this.onFieldsChange}
                {...this.props}
                onClose={this.onClose}
            />
        )
    }
}
export default ModelSubmitModal;
