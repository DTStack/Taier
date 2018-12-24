import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Modal, Form, Input, Select, Alert } from 'antd';
import { formItemLayout } from '../../../comm/const';

const FormItem = Form.Item;
const Option = Select.Option;
// mock
@connect(state => {
    return {
        projects: state.projects,
        user: state.user
    }
}, null)
class AddDesensitization extends Component {
    cancel = () => {
        const { onCancel } = this.props;
        onCancel();
    }
    submit = () => {
        const { onOk } = this.props;
        const desensitizationData = this.props.form.getFieldsValue();
        this.props.form.validateFields((err) => {
            if (!err) {
                this.props.form.resetFields()
                onOk(desensitizationData)
            }
        });
    }
    /* eslint-disable */
    render () {
        const { getFieldDecorator } = this.props.form;
        const { projects } = this.props;
        const projectsOptions = projects.map(item => {
            return <Option
                title={item.projectAlias}
                key={item.id}
                name={item.projectAlias}
                value={`${item.id}`}
            >
                {item.projectAlias}
            </Option>
        })
        return (
            <Modal
                visible={this.props.visible}
                title='添加脱敏'
                onCancel={this.cancel}
                onOk={this.submit}
            >
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label="脱敏名称"
                    >
                        {getFieldDecorator('name', {
                            rules: [{
                                required: true,
                                message: '脱敏名称不可为空！'
                            }]
                        })(
                            <Input />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="项目"
                    >
                        {getFieldDecorator('projectId', {
                            rules: [{
                                required: true,
                                message: '项目不可为空！'
                            }]
                        })(
                            <Select
                                allowClear
                            >
                                {/* {projectsOptions} */}
                                <Option value={1}>1</Option>
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="表"
                    >
                        {getFieldDecorator('tableId', {
                            rules: [{
                                required: true,
                                message: '表不可为空！'
                            }]
                        })(
                            <Select
                                allowClear
                            >
                                {/* {projectsOptions} */}
                                <Option value={1}>1</Option>
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="字段"
                    >
                        {getFieldDecorator('columnName', {
                            rules: [{
                                required: true,
                                message: '字段不可为空！'
                            }]
                        })(
                            <Select
                                allowClear
                            >
                                {/* {projectsOptions} */}
                                <Option value='columns'>columns</Option>
                            </Select>
                        )}
                    </FormItem>
                    <div style={{ marginLeft: '104px', marginTop: '-10px' }} className='desenAlert'>
                        <Alert message='上游表、下游表的相关字段会自动脱敏' type="info" showIcon />
                    </div>
                    <FormItem
                        {...formItemLayout}
                        label="样例数据"
                    >
                        {getFieldDecorator('example', {
                            rules: [{
                                max: 200,
                                message: '样例数据请控制在200个字符以内！'
                            }]
                        })(
                            <Input type="textarea" rows={4} />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="脱敏规则"
                    >
                        {getFieldDecorator('ruleId', {
                            rules: [{
                                required: true,
                                message: '脱敏规则不可为空！'
                            }]
                        })(
                            <Select
                                allowClear
                            >
                                {/* {projectsOptions} */}
                                <Option value={2}>2</Option>
                            </Select>
                        )}
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
export default Form.create()(AddDesensitization);
