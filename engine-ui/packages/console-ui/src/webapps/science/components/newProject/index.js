import React, { Component } from 'react';
import { connect } from 'react-redux';

import { Modal, Form, Input, Icon, message } from 'antd';
import './index.scss';

import * as baseActions from '../../actions/base'
import { bindActionCreators } from 'redux';

const FormItem = Form.Item;
const formItemLayout = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 }
    }
};

class NewProject extends Component {
    state = {
        loading: false
    }
    handleOk = () => {
        this.props.form.validateFieldsAndScroll(async (err, values) => {
            if (!err) {
                this.setState({
                    loading: true
                })
                let res = await this.props.createProject(values);
                if (res && res.code == 1) {
                    message.success('创建成功');
                    if (typeof this.props.onOk === 'function') {
                        this.props.onOk(res.data);
                    }
                    this.handleCancel();
                } else {
                    this.setState({
                        loading: false
                    })
                }
            }
        });
    }
    handleCancel = () => {
        this.props.form.resetFields();
        this.setState({
            loading: false
        })
        this.props.onCancel();
    }
    alert () {
        const values = this.props.form.getFieldsValue(['projectName', 'projectAlias']);
        return values.projectName && values.projectAlias && <div className="alert"><Icon type="question-circle-o" />项目创建后，项目显示名支持修改，项目名称将不能再修改</div>;
    }
    render () {
        const { loading } = this.state;
        const { visible } = this.props;
        const { getFieldDecorator } = this.props.form;
        return (
            <div>
                <Modal
                    maskClosable={false}
                    title="创建项目"
                    visible={visible}
                    onOk={this.handleOk}
                    confirmLoading={loading}
                    wrapClassName='projects-modal'
                    okText="创建"
                    onCancel={this.handleCancel}
                >
                    <div className="tips">注：数据科学平台创建的项目，同时会同步至离线计算中，可在离线计算中进行数据同步、任务运维。</div>
                    {this.alert()}
                    <Form>
                        <FormItem
                            {...formItemLayout}
                            label="项目名称"
                        >
                            {getFieldDecorator('projectName', {
                                rules: [{
                                    required: true, message: '请填写项目名称'
                                }, {
                                    max: 32, message: '不超过32个字符，只支持字母、数字、下划线'
                                }, {
                                    pattern: /^\w+$/, message: '不超过32个字符，只支持字母、数字、下划线'
                                }]
                            })(
                                <Input placeholder='请输入项目名称' />
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="项目显示名"
                        >
                            {getFieldDecorator('projectAlias', {
                                rules: [{
                                    required: true, message: '请填写项目显示名'
                                }, {
                                    max: 32, message: '不超过32个字符'
                                }]
                            })(
                                <Input placeholder='请输入项目显示名' />
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="项目描述"
                        >
                            {getFieldDecorator('projectDesc', {
                                rules: [{
                                    max: 64,
                                    message: '不超过64个字符'
                                }]
                            })(
                                <Input placeholder='请输入项目描述' type="textarea" rows={4} />
                            )}
                        </FormItem>
                    </Form>
                </Modal>
            </div>
        );
    }
}
const WrapForm = Form.create()(NewProject);
export default connect(null, dispatch => {
    return {
        ...bindActionCreators(baseActions, dispatch)
    }
})(WrapForm);
