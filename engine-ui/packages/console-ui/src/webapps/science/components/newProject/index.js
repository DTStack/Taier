import React, { Component } from 'react';
import { Modal, Form, Input, Icon } from 'antd';
import PropTypes from 'prop-types'
import './index.scss';
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
class Index extends Component {
    handleOk = () => {
        this.props.form.validateFieldsAndScroll((err, values) => {
            if (!err) {
                console.log('Received values of form: ', values);
                this.handleCancel();
            }
        });
    }
    handleCancel = () => {
        this.props.form.resetFields();
        this.props.onCancel();
    }
    alert () {
        const values = this.props.form.getFieldsValue(['projectName', 'projectAliaName']);
        return values.projectName && values.projectAliaName && <div className="alert"><Icon type="question-circle-o" />项目创建后，项目显示名支持修改，项目名称将不能再修改</div>;
    }
    render () {
        const { visible } = this.props;
        const { getFieldDecorator } = this.props.form;
        return (
            <div>
                <Modal
                    maskClosable={false}
                    title="创建项目"
                    visible={visible}
                    onOk={this.handleOk}
                    wrapClassName='projectsModal'
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
                                    pattern: /^[A-Za-z0-9_]+$/, message: '不超过32个字符，只支持字母、数字、下划线'
                                }]
                            })(
                                <Input placeholder="不超过32个字符，只支持字母、数字、下划线" />
                            )}
                        </FormItem>
                        <FormItem
                            {...formItemLayout}
                            label="项目显示名"
                        >
                            {getFieldDecorator('projectAliaName', {
                                rules: [{
                                    required: true, message: '请填写项目显示名'
                                }, {
                                    max: 32, message: '不超过32个字符'
                                }]
                            })(
                                <Input placeholder="不超过32个字符" />
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
                                <Input type="textarea" rows={4} placeholder="不超过64个字符" />
                            )}
                        </FormItem>
                    </Form>
                </Modal>
            </div>
        );
    }
}
Index.propTypes = {
    visible: PropTypes.Boolean,
    onCancel: PropTypes.func
};
export default Form.create()(Index);
