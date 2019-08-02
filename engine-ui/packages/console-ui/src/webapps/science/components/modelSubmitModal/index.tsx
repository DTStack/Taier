import * as React from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';

import { Modal, Form, Input, Radio, Select, Icon } from 'antd';

import CopyIcon from 'main/components/copy-icon';
import './modelSubmitModal.scss';

import * as baseActions from '../../actions/base'
import { formItemLayout, siderBarType } from '../../consts';

import Api from '../../api';

const FormItem = Form.Item;
const RadioGroup = Radio.Group;
const Option = Select.Option;

const PUBLISH_TYPE: any = {
    NEW: '0',
    UPDATE: '1'
};
@(connect(null, (dispatch: any) => {
    return {
        ...bindActionCreators(baseActions, dispatch)
    }
}) as any)
class ModelSubmitModalForm extends React.Component<any, any> {
    state: any = {
        isSuccess: false,
        successData: null,
        componentModelList: [],
        modelList: []
    }
    componentDidMount () {
        const { visible } = this.props;
        if (visible) {
            this.initData();
        }
    }
    componentDidUpdate (prevProps: any) {
        if (!prevProps.visible && this.props.visible) {
            this.initData();
        }
    }
    initData () {
        const { isNotebook } = this.props;
        if (!isNotebook) {
            this.getLabModels();
        }
        this.getModelList();
    }
    async getLabModels () {
        const { data } = this.props;
        let res = await Api.model.getModelListFromLab({
            taskId: data.id
        });
        if (res && res.code == 1) {
            this.setState({
                componentModelList: res.data
            })
        }
    }
    async getModelList () {
        let res = await Api.model.getTaskModels();
        if (res && res.code == 1) {
            this.setState({
                modelList: res.data
            })
        }
    }
    onOk = () => {
        this.props.form.validateFields((err: any, values: any) => {
            if (!err) {
                Modal.confirm({
                    title: '确认部署',
                    content: '确认部署此模型吗？',
                    okText: '确定',
                    cancelText: '取消',
                    onOk: async () => {
                        this.props.onOk(values).then((res: any) => {
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
        const { modelList } = this.state;
        const { _formData, form } = this.props;
        const { getFieldDecorator } = form;
        const { publishType, modelId } = _formData;
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
                            {getFieldDecorator('modelId', {
                                rules: [{
                                    required: true,
                                    message: '请选择模型名称'
                                }]
                            })(
                                <Select>
                                    {modelList.map((m: any) => {
                                        return <Option value={m.id} key={m.id} >{m.modelName}</Option>
                                    })}
                                </Select>
                            )}
                        </FormItem>
                        {modelId && modelId.value ? (
                            <FormItem
                                {...formItemLayout}
                                label='模型版本号'
                            >
                                <Input value={`v${modelList.find((m: any) => {
                                    return m.id == modelId.value
                                }).version + 1}`} disabled />
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
                                value={successData}
                                addonAfter={<CopyIcon copyText={successData} />}
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
        const { isSuccess, componentModelList } = this.state;
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
                            {getFieldDecorator('componentId', {
                                rules: [{
                                    required: true,
                                    message: '请选择部署模型'
                                }]
                            })(
                                <Select>
                                    {componentModelList.map((c: any) => {
                                        return <Option key={c.id} value={c.id} >{c.name}</Option>
                                    })}
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
    mapPropsToFields (props: any) {
        return props._formData;
    },
    onFieldsChange (props: any, fields: any) {
        if (fields.hasOwnProperty('publishType')) {
            fields.modelId = {
                value: undefined
            }
            fields.modelName = {
                value: undefined
            }
        }
        if (fields.hasOwnProperty('componentId')) {
            fields.modelName = {
                value: undefined
            }
            fields.modelId = {
                value: undefined
            }
            fields.publishType = {
                value: PUBLISH_TYPE.NEW
            }
        }
        props.onFieldsChange(fields);
    }
})(ModelSubmitModalForm);

class ModelSubmitModal extends React.Component<any, any> {
    state: any = {
        _formData: {
            publishType: {
                value: PUBLISH_TYPE.NEW
            }
        },
        key: null
    }
    onFieldsChange = (fields: any) => {
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
