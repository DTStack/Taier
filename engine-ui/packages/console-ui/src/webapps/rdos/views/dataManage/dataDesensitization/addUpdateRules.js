import React, { Component } from 'react';
import { Modal, Form, Input, Radio, InputNumber, Button } from 'antd';
import { formItemLayout } from '../../../comm/const';

const FormItem = Form.Item;
const RadioGroup = Radio.Group
class AddUpdateRules extends Component {
    state = {
        // 部分脱敏参数
        partData: {
            startInputValue: '',
            finishInputValue: '',
            repalceValue: ''
        },
        newReplaceData: ''
    }
    // 改变开始值
    changeStartValue = (value) => {
        const { partData } = this.state;
        this.setState({
            partData: Object.assign(partData, { startInputValue: value })
        })
    }
    changeFinishValue = (value) => {
        const { partData } = this.state;
        this.setState({
            partData: Object.assign(partData, { finishInputValue: value })
        })
    }
    changeReplaceValue = (e) => {
        const { partData } = this.state;
        this.setState({
            partData: Object.assign(partData, { repalceValue: e.target.value })
        })
    }
    // 效果预览
    preview = () => {
        const { getFieldValue } = this.props.form;
        const sampleData = getFieldValue('sampleData');
        const desensitizationType = getFieldValue('desensitizationType');
        // const { startInputValue, finishInputValue, repalceValue } = this.state;
        console.log(sampleData);
        if (desensitizationType === 1) {
            if (sampleData) {
                const newReplaceData = sampleData.replace(sampleData, '*');
                console.log(newReplaceData)
                this.setState({
                    newReplaceData
                })
            }
        } else {
        }
    }
    cancel = () => {
        const { onCancel } = this.props;
        onCancel();
    }
    submit = () => {
        const { onOk } = this.props;
        const { partData } = this.state;
        const ruleData = this.props.form.getFieldsValue();
        this.props.form.validateFields((err) => {
            if (!err) {
                this.props.form.resetFields()
                onOk({ ...ruleData, partData })
            }
        });
    }
    render () {
        const { getFieldDecorator, getFieldValue } = this.props.form;
        const { status, dataSource } = this.props;
        const isEdit = status === 'edit';
        const desensitizationType = getFieldValue('desensitizationType');
        // const sampleData = getFieldValue('sampleData'); // 样例数据
        const { newReplaceData } = this.state;
        return (
            <Modal
                visible={this.props.visible}
                width='650'
                title={isEdit ? '编辑规则' : '创建规则'}
                onCancel={this.cancel}
                onOk={this.submit}
            >
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label="规则名称"
                    >
                        {getFieldDecorator('ruleName', {
                            rules: [{
                                required: true,
                                message: '规则名称不可为空！'
                            }],
                            initialValue: dataSource.ruleName ? dataSource.ruleName : ''
                        })(
                            <Input />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="样例数据"
                    >
                        {getFieldDecorator('sampleData', {
                            rules: [{
                                max: 200,
                                message: '样例数据请控制在200个字符以内！'
                            }]
                            // initialValue: dataSource.ruleName ? dataSource.ruleName : ''
                        })(
                            <Input type="textarea" rows={4} />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="脱敏效果"
                    >
                        {getFieldDecorator('desensitizationType', {
                            rules: [{
                                required: true
                            }]
                            // initialValue: dataSource.ruleName ? dataSource.ruleName : ''
                        })(
                            <RadioGroup
                            >
                                <Radio value={1}>全部脱敏</Radio>
                                <Radio value={0}>部分脱敏</Radio>
                            </RadioGroup>
                        )}
                    </FormItem>
                    { desensitizationType === 0 ? <FormItem>
                        <div style={{ margin: '-10 0 0 150' }}>
                            若脱敏规则匹配，则将第
                            <InputNumber
                                style={{ width: '50px' }}
                                min={1}
                                value={this.state.partData.startInputValue}
                                onChange={this.changeStartValue}
                            />位至第<InputNumber
                                style={{ width: '50px' }}
                                min={1}
                                max={100}
                                value={this.state.partData.finishInputValue}
                                onChange={this.changeFinishValue}
                            />位替换为:<Input
                                style={{ width: '30px' }}
                                value={this.state.partData.repalceValue}
                                onChange={this.changeReplaceValue}
                            />
                        </div>
                    </FormItem> : ''}
                    <FormItem
                        {...formItemLayout}
                    >
                        <div style={{ margin: '-10 0 0 150' }}>
                            <Button
                                type="primary"
                                onClick={this.preview}
                            >效果预览</Button>
                            <div>{newReplaceData}</div>
                        </div>
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
export default Form.create()(AddUpdateRules);
